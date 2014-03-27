package protoboard.blackboard;

import java.awt.Frame;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.io.File;
import java.util.Date;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;
import protoboard.Constants;
import protoboard.Constants.BlackboardC;
import protoboard.Main;
import protoboard.leapmotion.LeapMotionObserver;

/**
 * Implements the blackboard mode of the application.
 *
 */
public class Blackboard extends PApplet implements LeapMotionObserver {
	private static final long serialVersionUID = -2341687072941440919L;
	
	private CopyOnWriteArrayList<PGraphics> screens;
	private PGraphics screen_curr;
	private int screen_pos;
	private AtomicBoolean[] screen_change;

	private Colors draw_color;
	
	private ArrowsSquare color_square;
	private ArrowsSquare number_square;

	private AtomicBoolean save_text;
	private AtomicInteger save_text_time;
	
	private AtomicBoolean multiScreenMode;
	private ScreensIterator screens_iter;
	
	private AtomicBoolean windows_f_l_created;
	
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

		this.draw_color = new Colors();
		
		this.color_square = ArrowsSquare.colorSquare(this, draw_color.getActual());
		this.number_square = ArrowsSquare.numberSquare(this);

		this.save_text = new AtomicBoolean(false);
		this.save_text_time = new AtomicInteger(0);
		
		this.multiScreenMode = new AtomicBoolean(false);
		this.screens_iter = null;
		
		this.windows_f_l_created = new AtomicBoolean(false);
		
		this.draw_line_weight = BlackboardC.draw_line_weight;
		updateDrawLineSquare();
		
		this.save_path = "";
	}
	
	private synchronized void _changeScreenBack() {
		if ((screen_pos - 1) > -1)
			screen_curr = screens.get(--screen_pos);

		screen_change[0].set(false);
	}

	private synchronized void _changeScreenForth() {
		if (screens.size() == BlackboardC.max_screens)
			return;
		
		if ((screen_pos + 1) == screens.size())
			addAndSetNewScreen();
		else
			screen_curr = screens.get(++screen_pos);

		screen_change[1].set(false);
	}

	private synchronized void addAndSetNewScreen() {
		int[] background = BlackboardC.background_rgb;
		
		PGraphics s_curr_aux = createGraphics(Constants.displayWidth, Constants.displayHeight);
		s_curr_aux.beginDraw();
		s_curr_aux.background(background[0], background[1], background[2]);
		s_curr_aux.endDraw();
		screens.add(s_curr_aux);
		screen_pos = screens.size()-1;
		screen_curr = s_curr_aux;
	}

	public void changeDrawColorBack() {
		draw_color = draw_color.prev();
		color_square = ArrowsSquare.colorSquare(this, draw_color.getActual());
		updateDrawLineSquare();
	}

	public void changeDrawColorForth() {
		draw_color = draw_color.next();
		color_square = ArrowsSquare.colorSquare(this, draw_color.getActual());
		updateDrawLineSquare();
	}
	
	public synchronized void changeDrawLineWeightBack() {
		draw_line_weight = Math.max(draw_line_weight
				- BlackboardC.draw_line_weight_sum,
				BlackboardC.draw_line_weight_limits[0]);
		updateDrawLineSquare();
	}

	public synchronized void changeDrawLineWeightForth() {
		draw_line_weight = Math.min(draw_line_weight
				+ BlackboardC.draw_line_weight_sum,
				BlackboardC.draw_line_weight_limits[1]);
		updateDrawLineSquare();
	}
	
	public synchronized void changeMultiScreenBack() {
		if (multiScreenMode.get()) {
			screens_iter.regress();
			screen_pos = screens_iter.currentPos();
		}
	}
	
	public synchronized void changeMultiScreenForth() {
		if (multiScreenMode.get()) {
			screens_iter.advance();
			screen_pos = screens_iter.currentPos();
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
		if ((frame != null) && !windows_f_l_created.get())
			frame.addWindowFocusListener(window_f_l());
		
		setLocation(0, 0);

		// Clean background
		int[] background = BlackboardC.background_rgb;
		
		background(background[0], background[1], background[2]);
		
		changeScreen();
		
		image(screen_curr, 0, 0);
		
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
			textSize(BlackboardC.save_text_size);
			
			String sav_text = BlackboardC.save_text;
			final float save_t_xpos = (Constants.displayWidth / 2.0f) - (textWidth(sav_text) / 2.0f);
			int[] save_t_color = BlackboardC.save_text_color;
			
			fill(save_t_color[0], save_t_color[1], save_t_color[2]);
			text(sav_text, save_t_xpos, BlackboardC.save_text_ypos);
			
			if (save_text_time.decrementAndGet() <= 0)
				save_text.set(false);
		}
	}
	
	private void drawScreenNumber() {
		int[] numb_color = BlackboardC.number_color;
		float[] sq_pos = number_square.sq_pos, sq_args = number_square.sq_args,
				numb_pos = BlackboardC.number_pos;
		int screen_p = screen_pos;
		
		textSize(sq_args[1]);
		
		if (screen_p < 10) {
			if (sq_pos[0] != BlackboardC.number_square_pos[0])
				number_square = ArrowsSquare.numberSquare(this);
			
			number_square.draw();
			fill(numb_color[0], numb_color[1], numb_color[2]);
			text(screen_p, numb_pos[0], numb_pos[1]);
		} else {
			if (sq_pos[0] == BlackboardC.number_square_pos[0]) {
				number_square = new ArrowsSquare(this, new float[] {
						2 * sq_args[0], sq_args[1], sq_args[2] }, new float[] {
						sq_pos[0] - sq_args[0], sq_pos[1] },
						BlackboardC.square_ext_color,
						BlackboardC.number_square_fill_color, true, true);
			}
			
			number_square.draw();
			fill(numb_color[0], numb_color[1], numb_color[2]);
			text(screen_p, BlackboardC.number_gt_10_xpos, numb_pos[1]);
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

	public boolean isMaximized() {
		return frame.getExtendedState() == Frame.MAXIMIZED_BOTH;
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
				float width_dif = (float) scr.width / screen_curr.width,
						height_dif = (float) scr.height / screen_curr.height;
				
				// Resize the image conveniently
				if ((width_dif > 1) && (scr.width >= scr.height))
					scr.resize((int) (scr.width/width_dif), (int) (scr.height/width_dif));
				else if (height_dif > 1)
					scr.resize((int) (scr.width/height_dif), (int) (scr.height/height_dif));
				
				screen_curr.beginDraw();
				screen_curr.image(scr, 0, 0, scr.width, scr.height);
				screen_curr.endDraw();
			}
		}
	}
	
	public void maximize() {
		registerAsObserver();
		frame.setExtendedState(Frame.MAXIMIZED_BOTH);  // Maximize window
	}

	public void maximizeAndLoad(File[] files) {
		if (files != null)
			loadAndAddScreens(files);
		
		maximize();
	}
	
	public void minimize() {
		unregisterAsObserver();
		frame.setExtendedState(Frame.ICONIFIED);  // Minimize window
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
					strokeWeight(BlackboardC.erase_square_border_weight);
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
					
					screen_curr.line(mouseX, mouseY, pmouseX, pmouseY);
					
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
		screen_curr = screens_iter.current();
		screen_pos = screens_iter.currentPos();
		multiScreenMode.set(false);
		screens_iter = null;
	}
	
	public void registerAsObserver() {
		Main.lm_listener.register(this);
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
		screen.save(path + BlackboardC.save_name + "[" + pos + "]_"
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
		size(Constants.displayWidth, Constants.displayHeight);
		registerAsObserver();
		Main.setBlackBoardMode(this);
		
		File[] load_files = Main.getAndNullLoadFiles_blckbrdMode();
		
		if (load_files != null)
			loadAndAddScreens(load_files);
		
		if (screens.size() == 0)
			addAndSetNewScreen();
	}
	
	@Override
	public boolean sketchFullScreen() {
		return true;
	}
	
	public void unregisterAsObserver() {
		Main.lm_listener.unregister(this);
	}
	
	private void updateDrawLineSquare() {
		int[] color = draw_color.isEraseColor() ? BlackboardC.background_rgb_1
				: draw_color.getActual();
		draw_line_square = ArrowsCircleSquare.drawLineWeightCircleSquare(this,
				draw_line_weight, color);
	}

	/**
	 * @return the WindowFocusListener for this Blackboard only once.
	 * 
	 */
	private WindowFocusListener window_f_l() {	
		if (windows_f_l_created.compareAndSet(false, true)) {
			return new WindowFocusListener() {
				@Override
				public void windowGainedFocus(WindowEvent e) {
					Main.runBlackboardMode();
				}
				
				@Override
				public void windowLostFocus(WindowEvent e) {
					Main.stopBlackboardMode();
				}
			};
		} else
			return null;
	}
}