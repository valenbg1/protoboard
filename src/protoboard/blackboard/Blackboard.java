package protoboard.blackboard;

import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.io.File;
import java.util.Date;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.JFrame;

import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PVector;
import protoboard.Constants.BlackboardC;
import protoboard.Constants.LeapMotionListenerC;
import protoboard.Main;
import protoboard.leapmotion.LeapMotionObserver;

/**
 * Implements the blackboard mode of the application.
 *
 */
public class Blackboard extends MyPApplet implements LeapMotionObserver {
	private static final long serialVersionUID = -2341687072941440919L;
	
	private CopyOnWriteArrayList<PGraphics> screens;
	private PGraphics screen_curr;
	private int screen_pos;
	private AtomicBoolean[] screen_change;
	private PVector screen_draw_p;
	
	private Sizes sizes;

	private Colors draw_color;

	private ArrowsSquare color_square;
	
	private ArrowsSquare number_square;
	private AtomicBoolean save_text;

	private AtomicInteger save_text_time;
	private AtomicBoolean multiScreenMode;
	
	private ScreensIterator screens_iter;
	private AtomicBoolean frame_setup;
	
	private float draw_line_weight;
	
	private ArrowsCircleSquare draw_line_square;
	private String save_path;
	
	public Blackboard() {
		super();
		
		this.screens = new CopyOnWriteArrayList<PGraphics>();
		this.screen_curr = null;
		this.screen_pos = -1;
		this.screen_change = new AtomicBoolean[2];
		this.screen_change[0] = new AtomicBoolean(false);
		this.screen_change[1] = new AtomicBoolean(false);
		this.screen_draw_p = new PVector(0, 0);
		
		this.sizes = null;

		this.draw_color = new Colors();
		
		this.color_square = null;
		this.number_square = null;

		this.save_text = new AtomicBoolean(false);
		this.save_text_time = new AtomicInteger(0);
		
		this.multiScreenMode = new AtomicBoolean(false);
		this.screens_iter = null;
		
		this.frame_setup = new AtomicBoolean(false);
		
		this.draw_line_weight = 0;
		
		this.save_path = "";
	}
	
	private synchronized void _changeScreenBack() {
		if ((screen_pos - 1) > -1)
			screen_curr = screens.get(--screen_pos);

		screen_change[0].set(false);
		resetCurrScreenToCenter();
	}
	
	private synchronized void _changeScreenForth() {
		if (screens.size() == BlackboardC.max_screens)
			return;
		
		if ((screen_pos + 1) == screens.size())
			addAndSetNewScreen();
		else
			screen_curr = screens.get(++screen_pos);

		screen_change[1].set(false);
		resetCurrScreenToCenter();
	}
	
	private synchronized void addAndSetNewScreen() {
		float f = BlackboardC.screen_factor;
		
		PGraphics s_curr_aux = newScreen((int) (displayWidth*f), (int) (displayHeight*f));
		screens.add(s_curr_aux);
		screen_pos = screens.size()-1;
		screen_curr = s_curr_aux;
		resetCurrScreenToCenter();
	}

	private PVector boundScreenfrom(PVector pos) {
		if (screen_curr != null) {
			PVector pos_aux = new PVector(pos.x, pos.y);
			
			pos_aux.x = Math.max(pos_aux.x, -(screen_curr.width - width));
			pos_aux.x = Math.min(pos_aux.x, 0);
			
			pos_aux.y = Math.max(pos_aux.y, -(screen_curr.height - height));
			pos_aux.y = Math.min(pos_aux.y, 0);
			
			return pos_aux;
		} else
			return new PVector(0, 0);
	}
	
	public void changeDrawColorBack() {
		draw_color = draw_color.prev();
		color_square = ArrowsSquare.colorSquare(this, color(draw_color.getActual()));
		updateDrawLineSquare();
	}
	
	public void changeDrawColorForth() {
		draw_color = draw_color.next();
		color_square = ArrowsSquare.colorSquare(this, color(draw_color.getActual()));
		updateDrawLineSquare();
	}

	public synchronized void changeDrawLineWeightBack() {
		draw_line_weight = Math.max(draw_line_weight
				- sizes.draw_line_weight_sum,
				sizes.draw_line_weight_limits[0]);
		updateDrawLineSquare();
	}

	public synchronized void changeDrawLineWeightForth() {
		draw_line_weight = Math.min(draw_line_weight
				+ sizes.draw_line_weight_sum,
				sizes.draw_line_weight_limits[1]);
		updateDrawLineSquare();
	}

	public synchronized void changeMultiScreenBack() {
		if (multiScreenMode.get()) {
			if (screen_pos > 0)
				_changeScreenBack();
			
			screens_iter = new ScreensIterator(this, screens, screen_pos, BlackboardC.multi_screen_around);
		}
	}
	
	public synchronized void changeMultiScreenForth() {
		if (multiScreenMode.get()) {
			if ((screen_pos+1) < screens.size())
				_changeScreenForth();
			
			screens_iter = new ScreensIterator(this, screens, screen_pos, BlackboardC.multi_screen_around);
		}
	}

	private void changeScreen() {
		if (screen_change[0].get())
			_changeScreenBack();
		else if (screen_change[1].get()) {
			_changeScreenForth();
		}
	}
	
	public void changeScreenBack() {
		screen_change[0].compareAndSet(false, true);
	}
	
	public void changeScreenForth() {
		screen_change[1].compareAndSet(false, true);
	}
	
	public synchronized void cleanScreens() {
		this.screens = new CopyOnWriteArrayList<PGraphics>();
		this.screen_curr = null;
		this.screen_pos = -1;
		addAndSetNewScreen();
	}
	
	@Override
	public void draw() {
		updateDrawablesSizes();
		
		if ((frame != null) && !frame_setup.compareAndSet(false, true)) {
			frame.addWindowFocusListener(window_f_l());
			frame.setResizable(true);
		}
		
		setLocation(0, 0);

		// Clean screen
		int[] background = BlackboardC.background_rgb;
		
		background(background[0], background[1], background[2]);
		
		changeScreen();
	
		screen_curr.smooth();
		image(screen_curr, screen_draw_p.x, screen_draw_p.y);
		
		processCursor();
		
		drawMultiScreenMode();
		
		color_square.draw();
		
		draw_line_square.draw();

		drawScreenNumber();
		
		drawSaveText();
	}
	
	private void drawMultiScreenMode() {
		if (multiScreenMode.get())
			screens_iter.draw();
	}
	
	private void drawSaveText() {
		if (save_text.get()) {
			textSize(sizes.save_text_size);
			
			String sav_text = BlackboardC.save_text;
			final float save_t_xpos = (sizes.width / 2.0f) - (textWidth(sav_text) / 2.0f);
			int[] save_t_color = BlackboardC.save_text_color;
			
			fill(save_t_color[0], save_t_color[1], save_t_color[2]);
			text(sav_text, save_t_xpos, sizes.save_text_ypos);
			
			if (save_text_time.decrementAndGet() <= 0)
				save_text.set(false);
		}
	}

	private void drawScreenNumber() {
		int[] numb_color = BlackboardC.number_color;
		PVector pos = number_square.pos, diag = number_square.diag,
				numb_pos = sizes.number_pos;
		int screen_p = screen_pos;
		
		// Weird AWT exception if not checking this
		if (diag.y >= 1)
			textSize(diag.y);
		
		if (screen_p < 10) {
			if (pos.x != sizes.number_square_pos.x)
				number_square = ArrowsSquare.numberSquare(this);
			
			number_square.draw();
			fill(numb_color[0], numb_color[1], numb_color[2]);
			text(screen_p, numb_pos);
		} else {
			if (pos.x == sizes.number_square_pos.x) {
				number_square = new ArrowsSquare(this,
						new PVector(diag.x*2f, diag.y),
						new PVector(pos.x - diag.x, pos.y),
						color(BlackboardC.square_ext_color),
						color(BlackboardC.number_square_fill_color), true, true);
			}
			
			number_square.draw();
			fill(numb_color[0], numb_color[1], numb_color[2]);
			text(screen_p, sizes.number_gt_10_xpos, numb_pos.y);
		}
	}

	public void enterMultiScreenMode() {
		screens_iter = new ScreensIterator(this, screens, screen_pos, BlackboardC.multi_screen_around);
		multiScreenMode.set(true);
	}

	@Override
	public void exit() {
		Main.stopBlackboardMode();
	}
	
	public String getSavePath() {
		return save_path;
	}
	
	public PVector getScreen_draw_p() {
		return screen_draw_p;
	}
	
	public Sizes getSizes() {
		return sizes;
	}
	
	public boolean isFocused() {
		return (frame != null) && (frame.isFocused());
	}

	@Override
	public void keyPressed() {
		if (key == CODED) {
			switch (keyCode) {
				case KeyEvent.VK_LEFT:
					onTranslation(-sizes.translation_arrows, 0);
					break;
				case KeyEvent.VK_RIGHT:
					onTranslation(sizes.translation_arrows, 0);
					break;
				case KeyEvent.VK_UP:
					onTranslation(0, sizes.translation_arrows);
					break;
				case KeyEvent.VK_DOWN:
					onTranslation(0, -sizes.translation_arrows);
					break;
			}
		}
	}
	
	/**
	 * @param newScreens an array of PNG's
	 * 
	 */
	public synchronized void loadAndAddScreens(File[] newScreens) {
		for (File screen : newScreens) {
			addAndSetNewScreen();

			if (screen != null) {
				PImage scr = loadImage(screen.getPath());
				resizeIfGTBlckbrdSize(scr);
				
				screen_curr.beginDraw();
				screen_curr.image(scr, 0, 0, scr.width, scr.height);
				screen_curr.endDraw();
			}
		}
	}

	public void minimize() {
		unregisterAsObserver();
		
		if (frame != null)
			frame.setExtendedState(JFrame.ICONIFIED);  // Minimize window
	}
	
	@Override
	public void mouseClicked() {
		if (multiScreenMode.get())
			// Process multiscreen mode clicks
			mouseClickedMultiscreenMode();
		else {
			// Process color square
			if (color_square.isOnAnySide(mouseX, mouseY)) {
				if (color_square.isOnLeft(mouseX, mouseY))
					changeDrawColorBack();
				else if (color_square.isOnRight(mouseX, mouseY))
					changeDrawColorForth();
			} 
			
			// Process number square
			else if (number_square.isOnAnySide(mouseX, mouseY)) {
				if (number_square.isOnLeft(mouseX, mouseY))
					changeScreenBack();
				else if (number_square.isOnRight(mouseX, mouseY))
					changeScreenForth();
				else if (number_square.isOnUp(mouseX, mouseY))
					enterMultiScreenMode();
				else if (number_square.isOnDown(mouseX, mouseY))
					saveCurrentScreen();
			}
			
			// Process draw line square
			else if (draw_line_square.isOnAnySide(mouseX, mouseY)) {
				if (draw_line_square.isOnLeft(mouseX, mouseY))
					changeDrawLineWeightBack();
				else if (draw_line_square.isOnRight(mouseX, mouseY))
					changeDrawLineWeightForth();
			}
		}
	}

	private void mouseClickedMultiscreenMode() {
		int pos;
		
		if (((pos = screens_iter.isOnAny(mouseX, mouseY)) != Integer.MIN_VALUE)
				|| number_square.isOnAnySide(mouseX, mouseY)) {
			if (pos == 0)
				quitMultiScreenMode();
			else if (pos > 0) {
				for (int i = 0; i < pos; ++i)
					changeMultiScreenForth();
			} else if (pos != Integer.MIN_VALUE){
				for (int i = 0; i < -pos; ++i)
					changeMultiScreenBack();
			} else if (number_square.isOnLeft(mouseX, mouseY))
				changeMultiScreenBack();
			else if (number_square.isOnRight(mouseX, mouseY))
				changeMultiScreenForth();
			else if (number_square.isOnDown(mouseX, mouseY))
				quitMultiScreenMode();
		}
	}
	
	private PGraphics newScreen(int width, int height) {
		int[] background = BlackboardC.background_rgb;
		PGraphics scr = createGraphics(width, height);
		
		scr.beginDraw();
		scr.background(background[0], background[1], background[2]);	
		scr.endDraw();
		
		return scr;
	}
	
	@Override
	public void onDownSwipe() {
		if (multiScreenMode.get())
			quitMultiScreenMode();
		else
			saveCurrentScreen();
	}
		
	@Override
	public void onKeyTap() {
		onScreenTap();
	}
	
	@Override
	public void onLeftCircle() {
		if (multiScreenMode.get())
			changeMultiScreenBack();
		else
			changeDrawColorBack();
	}
	
	@Override
	public void onLeftSwipe() {
		if (multiScreenMode.get())
			changeMultiScreenForth();
		else
			changeScreenForth();
	}
	
	@Override
	public void onRighCircle() {
		if (multiScreenMode.get())
			changeMultiScreenForth();
		else
			changeDrawColorForth();
	}

	@Override
	public void onRightSwipe() {
		if (multiScreenMode.get())
			changeMultiScreenBack();
		else
			changeScreenBack();
	}
	
	@Override
	public void onScreenTap() {
		if (multiScreenMode.get())
			quitMultiScreenMode();
	}

	@Override
	public void onTranslation(float d_x, float d_y) {
		PVector trans = new PVector(0, 0);
		float t_threshold = LeapMotionListenerC.translation_threshold, t_factor = sizes.translation_factor;
		
		if (Math.abs(d_x) > t_threshold)
			trans.x = d_x/t_factor;
		
		if (Math.abs(d_y) > t_threshold)
			trans.y = -d_y/t_factor;
		
		translateScreen(trans.x, trans.y);
	}
	
	@Override
	public void onUpSwipe() {
		enterMultiScreenMode();
	}
	
	private void processCursor() {
		if (multiScreenMode.get())
			processCursorMultiScreenMode();
		else {
			if (color_square.isOnAnySide(mouseX, mouseY)
					|| number_square.isOnAnySide(mouseX, mouseY)
					|| draw_line_square.isOnAnySide(mouseX, mouseY)) {
				cursor(HAND);
			} else {
				noCursor();

				int[] draw_col = draw_color.getActual(), 
						rect_stroke_col = BlackboardC.erase_square_border_color;
				float rect_weight = draw_line_weight*BlackboardC.erase_square_weight;

				if (!draw_color.isEraseColor()) {
					stroke(draw_col[0], draw_col[1], draw_col[2]);
					strokeWeight(draw_line_weight);
					point(mouseX, mouseY);
				} else {
					stroke(rect_stroke_col[0], rect_stroke_col[1], rect_stroke_col[2]);
					strokeWeight(sizes.erase_square_border_weight);
					fill(draw_col[0], draw_col[1], draw_col[2]);
					rect(mouseX - rect_weight/2, mouseY - rect_weight/2, rect_weight, rect_weight);
				}

				if (mousePressed) {
					screen_curr.beginDraw();
					
					screen_curr.stroke(draw_col[0], draw_col[1], draw_col[2]);
					
					if (!draw_color.isEraseColor())
						screen_curr.strokeWeight(draw_line_weight);
					else
						screen_curr.strokeWeight(rect_weight);
					
					screen_curr.line(mouseX - screen_draw_p.x, mouseY
							- screen_draw_p.y, pmouseX - screen_draw_p.x,
							pmouseY - screen_draw_p.y);
					
					screen_curr.endDraw();
				}
			}
		}
	}

	private void processCursorMultiScreenMode() {
		if ((screens_iter.isOnAny(mouseX, mouseY) != Integer.MIN_VALUE)
				|| number_square.isOnAnySide(mouseX, mouseY)) {
			cursor(HAND);
		} else
			cursor(ARROW);
	}

	public synchronized void quitMultiScreenMode() {
		multiScreenMode.set(false);
		screens_iter = null;
	}

	public void registerAsObserver() {
		Main.lm_listener.register(this);
	}

	private void resetCurrScreenToCenter() {
		if (screen_curr != null) {
			PVector pos_aux = new PVector();
			
			pos_aux.x = -((screen_curr.width-width) / 2.0f);
			pos_aux.y = -((screen_curr.height-height) / 2.0f);
			
			screen_draw_p = boundScreenfrom(pos_aux);
		} else
			screen_draw_p = new PVector(0, 0);
	}
	
	private void resizeIfGTBlckbrdSize(PImage graph) {
		float width_dif = (float) graph.width / sizes.width,
				height_dif = (float) graph.height / sizes.height,
				width_r = graph.width, height_r = graph.height;
		
		// Resize the image conveniently
		if ((width_dif > 1) && (graph.width >= graph.height)) {
			width_r = graph.width/width_dif;
			height_r = graph.height/width_dif;
		} else if (height_dif > 1) {
			width_r = graph.width/height_dif;
			height_r = graph.height/height_dif;
		}
		
		graph.resize((int) width_r, (int) height_r);
	}
	
	public void saveAllScreens(File path) {
		if (path.isDirectory()) {
			String p = path.getPath() + File.separator;
			
			for (int i = 0; i < screens.size(); ++i)
				saveScreen(screens.get(i), i, p);
				
			setSaveText();
		}
	}
	
	public synchronized void saveCurrentScreen() {
		saveScreen(screen_curr, screen_pos, save_path);
		setSaveText();
	}
	
	private void saveScreen(PGraphics screen, int pos, String path) {
		screen_curr.save(path + BlackboardC.save_name + "[" + pos + "]_"
				+ new Date().getTime() + "." + BlackboardC.save_extension);
	}
	
	public void setSavePath(File path) {
		if (path.isDirectory())
			save_path = path.getPath() + File.separator;
	}
	
	private void setSaveText() {
		save_text_time.set((int) Math.ceil(frameRate
				* BlackboardC.save_text_time));
		save_text.set(true);
	}
	
	@Override
	public void setup() {
		size(displayWidth/2, displayHeight/2);
		this.sizes = new Sizes(0, 0, displayWidth, displayHeight);
		
		updateDrawablesSizes();
		registerAsObserver();
		Main.setBlackBoardMode(this);
		
		File[] load_files = Main.getAndNullLoadFiles_blckbrdMode();
		
		if (load_files != null)
			loadAndAddScreens(load_files);
		
		if (screens.size() == 0)
			addAndSetNewScreen();
	}
	
	public void toFront() {
		registerAsObserver();
		
		if (frame != null) {
			frame.setExtendedState(JFrame.NORMAL);
			frame.toFront();
		}
	}
	
	public void toFrontAndLoad(File[] files) {
		if (files != null)
			loadAndAddScreens(files);
		
		toFront();
	}
	
	public void translateScreen(float d_x, float d_y) {
		PVector draw_p_aux = new PVector(screen_draw_p.x, screen_draw_p.y);
		
		draw_p_aux.x = draw_p_aux.x + d_x;
		draw_p_aux.y = draw_p_aux.y + d_y;
		
		screen_draw_p = boundScreenfrom(draw_p_aux);
	}
	
	public void unregisterAsObserver() {
		Main.lm_listener.unregister(this);
	}
	
	private void updateDrawablesSizes() {
		if ((width != sizes.width) || (height != sizes.height)) {
			sizes = new Sizes(width, height, displayWidth, displayHeight);
			color_square = ArrowsSquare.colorSquare(this, color(draw_color.getActual()));
			number_square = ArrowsSquare.numberSquare(this);
			
			if (draw_line_weight <= 0)
				draw_line_weight = sizes.draw_line_weight;
			
			updateDrawLineSquare();
			
			if (multiScreenMode.get())
				screens_iter = new ScreensIterator(this, screens, screen_pos, BlackboardC.multi_screen_around);
			
			screen_draw_p = boundScreenfrom(screen_draw_p);
		}
	}

	private void updateDrawLineSquare() {
		int[] raw_color = draw_color.isEraseColor() ? BlackboardC.background_rgb_1
				: draw_color.getActual();
		draw_line_square = ArrowsCircleSquare.drawLineWeightCircleSquare(this, draw_line_weight, color(raw_color));
	}

	/**
	 * @return the WindowFocusListener for this Blackboard.
	 * 
	 */
	private WindowFocusListener window_f_l() {	
		return new WindowFocusListener() {
			@Override
			public void windowGainedFocus(WindowEvent e) {
				Main.stopInputMode();
				registerAsObserver();
			}
			
			@Override
			public void windowLostFocus(WindowEvent e) {
				unregisterAsObserver();
			}
		};
	}
}