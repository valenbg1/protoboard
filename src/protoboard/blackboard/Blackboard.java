package protoboard.blackboard;

import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.io.File;
import java.util.Collection;
import java.util.Date;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.JFrame;

import processing.core.PApplet;
import processing.core.PGraphics;
import protoboard.AtomicFloat;
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
	private AtomicInteger screen_pos;
	private AtomicBoolean[] screen_change;

	private Colors draw_color;
	
	private ArrowsSquare color_square;
	private ArrowsSquare number_square;

	private AtomicBoolean save_text;
	private AtomicInteger save_text_time;
	
	private AtomicBoolean mouseClicked;
	
	private AtomicBoolean multiScreenMode;
	private ScreensIterator screens_iter;
	
	private AtomicBoolean windows_f_l_created;
	
	private AtomicFloat draw_line_weight;
	private ArrowsCircleSquare draw_line_square;
	
	public Blackboard() {
		super();
		
		this.screens = new CopyOnWriteArrayList<PGraphics>();
		this.screen_curr = null;
		this.screen_pos = new AtomicInteger(-1);
		this.screen_change = new AtomicBoolean[2];
		this.screen_change[0] = new AtomicBoolean(false);
		this.screen_change[1] = new AtomicBoolean(false);

		this.draw_color = new Colors();
		
		this.color_square = ArrowsSquare.colorSquare(this, draw_color.getActual());
		this.number_square = ArrowsSquare.numberSquare(this);

		this.save_text = new AtomicBoolean(false);
		this.save_text_time = new AtomicInteger(0);
		
		this.mouseClicked = new AtomicBoolean(false);
		
		this.multiScreenMode = new AtomicBoolean(false);
		this.screens_iter = null;
		
		this.windows_f_l_created = new AtomicBoolean(false);
		
		this.draw_line_weight = new AtomicFloat(BlackboardC.draw_line_weight);
		updateDrawLineSquare();
	}
	
	private synchronized void _changeScreenBack() {
		if ((screen_pos.get() - 1) > -1)
			screen_curr = screens.get(screen_pos.decrementAndGet());

		screen_change[0].set(false);
	}

	private synchronized void _changeScreenForth() {
		if (screens.size() == BlackboardC.max_screens)
			return;
		
		if ((screen_pos.get() + 1) == screens.size())
			addAndSetNewScreen();
		else
			screen_curr = screens.get(screen_pos.incrementAndGet());

		screen_change[1].set(false);
	}

	private synchronized void addAndSetNewScreen() {
		int[] background = BlackboardC.background_rgb;
		
		PGraphics s_curr_aux = createGraphics(Constants.displayWidth, Constants.displayHeight);
		s_curr_aux.beginDraw();
		s_curr_aux.background(background[0], background[1], background[2]);
		s_curr_aux.endDraw();
		screens.add(s_curr_aux);
		screen_pos.set(screens.size()-1);
		screen_curr = s_curr_aux;
	}

	public void changeDrawColorBack() {
		draw_color.prev();
		color_square = ArrowsSquare.colorSquare(this, draw_color.getActual());
		updateDrawLineSquare();
	}

	public void changeDrawColorForth() {
		draw_color.next();
		color_square = ArrowsSquare.colorSquare(this, draw_color.getActual());
		updateDrawLineSquare();
	}
	
	private void changeDrawLineWeightBack() {
		draw_line_weight.set(Math.max(draw_line_weight.get()-BlackboardC.draw_line_weight_sum, BlackboardC.draw_line_weight_limits[0]));
		updateDrawLineSquare();
	}

	private void changeDrawLineWeightForth() {
		draw_line_weight.set(Math.min(draw_line_weight.get()+BlackboardC.draw_line_weight_sum, BlackboardC.draw_line_weight_limits[1]));
		updateDrawLineSquare();
	}
	
	public synchronized void changeMultiScreenBack() {
		if (multiScreenMode.get()) {
			screens_iter.regress();
			screen_pos.set(screens_iter.currentPos());
		}
	}
	
	public synchronized void changeMultiScreenForth() {
		if (multiScreenMode.get()) {
			screens_iter.advance();
			screen_pos.set(screens_iter.currentPos());
		}
	}
	
	public synchronized void changeMultiScreenMode() {
		screens_iter = new ScreensIterator(this, screens, screen_pos.get(), BlackboardC.multi_screen_around);
		multiScreenMode.set(true);
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
		this.screen_pos = new AtomicInteger(-1);
		addAndSetNewScreen();
	}

	@Override
	public void draw() {
		if ((frame != null) && windows_f_l_created.compareAndSet(false, true))
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
			screens_iter.draw(this);
	}
	
	private void drawSaveText() {
		if (save_text.get()) {
			textSize(BlackboardC.save_text_size);
			
			String sav_text = BlackboardC.save_text;
			final float save_t_xpos = (Constants.displayWidth / 2.0f) - (textWidth(sav_text) / 2.0f);
			int[] save_t_color = BlackboardC.save_text_color;
			
			fill(save_t_color[0], save_t_color[1], save_t_color[2]);
			text(sav_text, save_t_xpos, BlackboardC.save_text_ypos);
			
			if (save_text_time.decrementAndGet() == 0)
				save_text.set(false);
		}
	}
	
	private void drawScreenNumber() {
		int[] numb_color = BlackboardC.number_color;
		float[] sq_pos = number_square.sq_pos, sq_args = number_square.sq_args,
				numb_pos = BlackboardC.number_pos;
		int screen_p = screen_pos.get();
		
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
	
	@Override
	public void exit() {
		Main.stopBlackboardMode();
	}
	
	public float getActualDrawLineWeight() {
		return draw_line_weight.get();
	}
	
	public boolean isMaximized() {
		return frame.getExtendedState() == JFrame.MAXIMIZED_BOTH;
	}

	public synchronized void loadAndAddScreens(Collection<File> newScreens) {
		for (File screen : newScreens) {
			addAndSetNewScreen();

			if (screen != null) {
				screen_curr.beginDraw();
				screen_curr.image(loadImage(screen.getPath()), 0, 0);
				screen_curr.endDraw();
			}
		}
	}

	public void maximize() {
		registerAsObserver();
		frame.setExtendedState(JFrame.MAXIMIZED_BOTH);  // Maximize window
	}
	
	public void maximizeAndLoad(File[] files) {
		if (files != null)
			new ScreensLoader(this, files).loadScreens();
		
		maximize();
	}

	public void minimize() {
		unregisterAsObserver();
		frame.setExtendedState(JFrame.ICONIFIED);  // Minimize window
	}
	
	@Override
	public void mouseClicked() {
		mouseClicked.compareAndSet(false, true);
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
		changeMultiScreenMode();
	}

	private void processCursor() {
		if (multiScreenMode.get())
			processCursorMultiScreenMode();
		else {
			if (color_square.isOnAnySide(mouseX, mouseY)) {
				cursor(HAND);
				
				if (mouseClicked.get()) {
					if (color_square.isOnLeft(mouseX, mouseY))
						changeDrawColorBack();
					else if (color_square.isOnRight(mouseX, mouseY))
						changeDrawColorForth();
				}
			} else if (number_square.isOnAnySide(mouseX, mouseY)) {
				cursor(HAND);
				
				if (mouseClicked.get()) {
					if (number_square.isOnLeft(mouseX, mouseY))
						changeScreenBack();
					else if (number_square.isOnRight(mouseX, mouseY))
						changeScreenForth();
					else if (number_square.isOnUp(mouseX, mouseY))
						changeMultiScreenMode();
					else if (number_square.isOnDown(mouseX, mouseY))
						saveCurrentScreen();
				}
			} else if (draw_line_square.isOnAnySide(mouseX, mouseY)) {
				cursor(HAND);
				
				if (mouseClicked.get()) {
					if (draw_line_square.isOnLeft(mouseX, mouseY))
						changeDrawLineWeightBack();
					else if (draw_line_square.isOnRight(mouseX, mouseY))
						changeDrawLineWeightForth();
				}
			} else {
				noCursor();

				int[] draw_col = draw_color.getActual(), 
						rect_stroke_col = BlackboardC.erase_square_border_color;
				float rect_weight = draw_line_weight.get()*BlackboardC.erase_square_weight;

				if (!draw_color.isEraseColor()) {
					stroke(draw_col[0], draw_col[1], draw_col[2]);
					strokeWeight(draw_line_weight.get());
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
						screen_curr.strokeWeight(draw_line_weight.get());
					else
						screen_curr.strokeWeight(rect_weight);
					
					screen_curr.line(mouseX, mouseY, pmouseX, pmouseY);
					
					screen_curr.endDraw();
				}
			}
		}
		
		mouseClicked.set(false);
	}

	private void processCursorMultiScreenMode() {
		int pos;
		
		if (((pos = screens_iter.isOnAny(mouseX, mouseY)) != Integer.MIN_VALUE) || number_square.isOnAnySide(mouseX, mouseY)) {
			cursor(HAND);
			
			if (mouseClicked.get()) {
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
		} else
			cursor(ARROW);
	}

	public synchronized void quitMultiScreenMode() {
		screen_curr = screens_iter.current();
		screen_pos.set(screens_iter.currentPos());
		multiScreenMode.set(false);
		screens_iter = null;
	}
	
	private void registerAsObserver() {
		Main.lm_listener.register(this);
	}
	
	public synchronized void saveCurrentScreen() {
		screen_curr.save(BlackboardC.save_name + "[" + screen_pos.get() + "]_"+ new Date().getTime() + BlackboardC.save_extension);
		save_text.compareAndSet(false, true);
		save_text_time.set((int) Math.ceil(frameRate*BlackboardC.save_text_time));
	}
	
	@Override
	public void setup() {
		size(Constants.displayWidth, Constants.displayHeight);
		registerAsObserver();
		Main.setBlackBoardMode(this);
		
		File[] load_files = Main.getAndNullLoadFiles_blckbrdMode();
		
		if (load_files != null)
			new ScreensLoader(this, load_files).loadScreens();
		
		if (screens.size() == 0)
			addAndSetNewScreen();
	}
	
	@Override
	public boolean sketchFullScreen() {
		return true;
	}
	
	private void unregisterAsObserver() {
		Main.lm_listener.unregister(this);
	}
	
	private void updateDrawLineSquare() {
		int[] color = draw_color.isEraseColor() ? BlackboardC.background_rgb_1 : draw_color.getActual();
		draw_line_square = ArrowsCircleSquare.drawLineWeightCircleSquare(this, draw_line_weight.get(), color);
	}

	private WindowFocusListener window_f_l() {	
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
	}
}