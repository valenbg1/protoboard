package protoboard.blackboard;

import java.util.Date;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import processing.core.PApplet;
import processing.core.PGraphics;
import protoboard.Constants;
import protoboard.leapmotion.LeapMotionListener;

import com.leapmotion.leap.Controller;

/**
 * Implements the blackboard mode of the application
 *
 */
public class Blackboard extends PApplet {
	private static final long serialVersionUID = -2341687072941440919L;

	private Controller controller;
	private LeapMotionListener listener;
	
	private CopyOnWriteArrayList<PGraphics> screens;
	private PGraphics screen_curr;
	private AtomicInteger screen_pos;
	private AtomicBoolean[] screen_change;

	private Colors draw_color;

	private AtomicBoolean save_text;
	private AtomicInteger save_text_time;
	
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

		this.save_text = new AtomicBoolean(false);
		this.save_text_time = new AtomicInteger(Constants.Blackboard.save_text_time);
	}
	
	private synchronized void _changeScreenBack() {
		if ((screen_pos.get() - 1) > -1)
			screen_curr = screens.get(screen_pos.decrementAndGet());

		screen_change[0].set(false);
	}

	private synchronized void _changeScreenForth() {
		if (screens.size() == Constants.Blackboard.max_screens)
			return;
		
		if ((screen_pos.get() + 1) == screens.size())
			addAndSetNewScreen();
		else
			screen_curr = screens.get(screen_pos.incrementAndGet());

		screen_change[1].set(false);
	}

	private synchronized void addAndSetNewScreen() {
		int[] background = Constants.Blackboard.background_rgb;
		
		screen_curr = createGraphics(displayWidth, displayHeight);
		screen_curr.beginDraw();
		screen_curr.background(background[0], background[1], background[2]);
		screen_curr.endDraw();
		screens.add(screen_curr);
		screen_pos.incrementAndGet();
	}

	public void changeDrawColorBack() {
		draw_color.prev();
	}

	public void changeDrawColorForth() {
		draw_color.next();
	}
	
	public void changeScreenBack() {
		screen_change[0].compareAndSet(false, true);
	}

	public void changeScreenForth() {
		screen_change[1].compareAndSet(false, true);
	}

	@Override
	public void draw() {
		// Change screen
		if (screen_change[0].get())
			_changeScreenBack();
		else if (screen_change[1].get()) {
			_changeScreenForth();
		}

		// Current screen
		int[] draw_col = draw_color.get();
		
		if (mousePressed) {
			screen_curr.beginDraw();
			screen_curr.stroke(draw_col[0], draw_col[1], draw_col[2]);
			screen_curr.strokeWeight(Constants.Blackboard.draw_line_weight);
			screen_curr.line(mouseX, mouseY, pmouseX, pmouseY);
			screen_curr.endDraw();
		}

		image(screen_curr, 0, 0);

		// Color square
		float[] sq_args = Constants.Blackboard.common_square_args, sq_pos = Constants.Blackboard.color_square_pos;
		int[] sq_e_col = Constants.Blackboard.square_ext_color;
		
		stroke(sq_e_col[0], sq_e_col[1], sq_e_col[2]);
		strokeWeight(Constants.Blackboard.square_weight);
		fill(draw_col[0], draw_col[1], draw_col[2]);
		rect(sq_pos[0], sq_pos[1], sq_args[0], sq_args[1], sq_args[2]);

		// Board number
		int[] numb_sq_fill_color = Constants.Blackboard.number_square_fill_color, numb_color = Constants.Blackboard.number_color;
		float[] numb_sq_pos = Constants.Blackboard.number_square_pos, numb_pos = Constants.Blackboard.number_pos;
		int screen_p = screen_pos.get();
		
		stroke(sq_e_col[0], sq_e_col[1], sq_e_col[2]);
		strokeWeight(Constants.Blackboard.square_weight);
		fill(numb_sq_fill_color[0], numb_sq_fill_color[1], numb_sq_fill_color[2]);
		textSize(Constants.Blackboard.number_size);
		
		if (screen_p < 10) {
			rect(numb_sq_pos[0], numb_sq_pos[1], sq_args[0], sq_args[1], sq_args[2]);
			fill(numb_color[0], numb_color[1], numb_color[2]);
			text(screen_p, numb_pos[0], numb_pos[1]);
		} else {
			rect(numb_sq_pos[0] - sq_args[0], numb_sq_pos[1], 2*sq_args[0], sq_args[1], sq_args[2]);
			fill(numb_color[0], numb_color[1], numb_color[2]);
			text(screen_p, Constants.Blackboard.number_gt_10_xpos, numb_pos[1]);
		}
		
		// Save text
		if (save_text.get()) {
			int[] save_t_color = Constants.Blackboard.save_text_color;
			float[] save_t_pos = Constants.Blackboard.save_text_pos;
			
			fill(save_t_color[0], save_t_color[1], save_t_color[2]);
			textSize(Constants.Blackboard.save_text_size);
			text(Constants.Blackboard.save_text, save_t_pos[0], save_t_pos[1]);
			
			if (save_text_time.decrementAndGet() == 0)
				save_text.set(false);
		}
	}

	@Override
	public void exit() {
		controller.removeListener(listener);
		super.exit();
	}

	public synchronized void saveCurrentScreen() {
		screen_curr.save(Constants.Blackboard.save_name + "[" + screen_pos.get() + "]_"+ new Date().getTime() +".png");
		save_text.compareAndSet(false, true);
		save_text_time.set(Constants.Blackboard.save_text_time);
	}

	@Override
	public void setup() {
		size(Constants.displayWidth, Constants.displayHeight);
		addAndSetNewScreen();
	}
}