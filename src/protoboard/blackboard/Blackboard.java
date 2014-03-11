package protoboard.blackboard;

import java.util.Date;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import processing.core.PApplet;
import processing.core.PGraphics;
import protoboard.Constants;
import protoboard.Constants.BlackboardC;
import protoboard.leapmotion.LeapMotionListener;
import protoboard.leapmotion.LeapMotionObserver;

import com.leapmotion.leap.Controller;

/**
 * Implements the blackboard mode of the application.
 *
 */
public class Blackboard extends PApplet implements LeapMotionObserver {
	private static final long serialVersionUID = -2341687072941440919L;

	private Controller controller;
	private LeapMotionListener listener;
	
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
	
	public Blackboard() {
		super();
		
		this.controller = new Controller();
		this.listener = new LeapMotionListener(this);
		this.controller.addListener(listener);
		
		this.screens = new CopyOnWriteArrayList<PGraphics>();
		this.screen_curr = null;
		this.screen_pos = new AtomicInteger(-1);
		this.screen_change = new AtomicBoolean[2];
		this.screen_change[0] = new AtomicBoolean(false);
		this.screen_change[1] = new AtomicBoolean(false);

		this.draw_color = new Colors();
		
		this.color_square = ArrowsSquare.colorSquare(draw_color.get());
		this.number_square = ArrowsSquare.numberSquare();

		this.save_text = new AtomicBoolean(false);
		this.save_text_time = new AtomicInteger(0);
		
		this.mouseClicked = new AtomicBoolean(false);
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
		
		screen_curr = createGraphics(Constants.displayWidth, Constants.displayHeight);
		screen_curr.beginDraw();
		screen_curr.background(background[0], background[1], background[2]);
		screen_curr.endDraw();
		screens.add(screen_curr);
		screen_pos.incrementAndGet();
	}

	public void changeDrawColorBack() {
		draw_color.prev();
		color_square = ArrowsSquare.colorSquare(draw_color.get());
	}

	public void changeDrawColorForth() {
		draw_color.next();
		color_square = ArrowsSquare.colorSquare(draw_color.get());
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
	
	@Override
	public void draw() {
		// Clean background
		int[] background = BlackboardC.background_rgb;
		
		background(background[0], background[1], background[2]);
		
		changeScreen();
		
		image(screen_curr, 0, 0);
		
		processCursor();
		
		color_square.draw(this);

		drawScreenNumber();
		
		drawSaveText();
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
				number_square = ArrowsSquare.numberSquare();
			
			number_square.draw(this);
			fill(numb_color[0], numb_color[1], numb_color[2]);
			text(screen_p, numb_pos[0], numb_pos[1]);
		} else {
			if (sq_pos[0] == BlackboardC.number_square_pos[0]) {
				number_square = new ArrowsSquare(new float[] { 2 * sq_args[0],
						sq_args[1], sq_args[2] }, new float[] {
						sq_pos[0] - sq_args[0], sq_pos[1] },
						BlackboardC.square_ext_color,
						BlackboardC.number_square_fill_color);
			}
			
			number_square.draw(this);
			fill(numb_color[0], numb_color[1], numb_color[2]);
			text(screen_p, BlackboardC.number_gt_10_xpos, numb_pos[1]);
		}
	}
	
	@Override
	public void exit() {
		listener.unregister(this);
		controller.removeListener(listener);
		super.exit();
	}

	@Override
	public void mouseClicked() {
		mouseClicked.compareAndSet(false, true);
	}

	@Override
	public void onDownSwipe() {
		saveCurrentScreen();
	}

	@Override
	public void onLeftCircle() {
		changeDrawColorBack();
	}

	@Override
	public void onLeftSwipe() {
		changeScreenBack();
	}

	@Override
	public void onRighCircle() {
		changeDrawColorForth();
	}

	@Override
	public void onRighSwipe() {
		changeScreenForth();
	}

	@Override
	public void onUpSwipe() {}

	private void processCursor() {
		if (mouseClicked.get()) {
			if (color_square.isOnLeftTriangle(mouseX, mouseY))
				changeDrawColorBack();
			else if (color_square.isOnRightTriangle(mouseX, mouseY))
				changeDrawColorForth();
			else if (number_square.isOnLeftTriangle(mouseX, mouseY))
				changeScreenBack();
			else if (number_square.isOnRightTriangle(mouseX, mouseY))
				changeScreenForth();

			mouseClicked.set(false);
		}

		if (color_square.isOnAnyTriangle(mouseX, mouseY) || number_square.isOnAnyTriangle(mouseX, mouseY)) {
			cursor(HAND);
		} else {
			noCursor();

			int[] draw_col = draw_color.get();

			stroke(draw_col[0], draw_col[1], draw_col[2]);
			strokeWeight(BlackboardC.draw_line_weight);
			line(mouseX, mouseY, pmouseX, pmouseY);

			if (mousePressed) {
				screen_curr.beginDraw();
				screen_curr.stroke(draw_col[0], draw_col[1], draw_col[2]);
				screen_curr.strokeWeight(BlackboardC.draw_line_weight);
				screen_curr.line(mouseX, mouseY, pmouseX, pmouseY);
				screen_curr.endDraw();
			}
		}
	}

	public synchronized void saveCurrentScreen() {
		screen_curr.save(BlackboardC.save_name + "[" + screen_pos.get() + "]_"+ new Date().getTime() +".png");
		save_text.compareAndSet(false, true);
		save_text_time.set((int) Math.ceil(frameRate*BlackboardC.save_text_time));
	}

	@Override
	public void setup() {
		size(Constants.displayWidth, Constants.displayHeight);
		addAndSetNewScreen();
		noCursor();
		listener.register(this);
	}
}