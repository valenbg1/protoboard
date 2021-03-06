package protoboard.blackboard;

import java.util.ArrayList;
import java.util.List;

import processing.core.PGraphics;
import processing.core.PVector;
import protoboard.Constants.BlackboardC;

/**
 * Implements an iterator over the screens of the blackboard. Used
 * for showing multiple screens at a time (when up swipe is detected).
 * It's an immutable object
 *
 */
class ScreensIterator {
	private final ArrayList<PGraphics> screens;
	private final PGraphics screen_curr;
	private final PGraphics[] screen_prevs, screen_nexts;
	private final ArrowsSquare sq_curr;
	private final ArrowsSquare[] sq_prevs, sq_nexts;
	private final int n_around;
	private final int screen_pos;
	private final Blackboard context;
	private final int pos_sq_color;
	
	public ScreensIterator(Blackboard context, List<PGraphics> screens,
			int screen_pos, int n_around) {
		this.screens = new ArrayList<PGraphics>(screens);
		this.screen_pos = screen_pos;
		this.screen_curr = screens.get(screen_pos);
		this.n_around = n_around;
		this.screen_prevs = new PGraphics[n_around];
		this.screen_nexts = new PGraphics[n_around];
		this.sq_prevs = new ArrowsSquare[n_around];
		this.sq_nexts = new ArrowsSquare[n_around];
		this.context = context;
		this.pos_sq_color = context.color(BlackboardC.alert_red_rgb);
		
		setPrevs();
		setNexts();
		
		Sizes sizes = context.getSizes();
		
		PVector mini = new PVector(sizes.width*BlackboardC.mini_screen_prop, sizes.height*BlackboardC.mini_screen_prop);
		PVector little = new PVector(sizes.width*BlackboardC.little_screen_prop, sizes.height*BlackboardC.little_screen_prop);
		float x = sizes.little_screen_pos.x + little.x;
		float y = sizes.little_screen_pos.y + little.y/2.0f - mini.y/2.0f;
		
		for (int i = 0; i < n_around; ++i)
			this.sq_nexts[i] = ArrowsSquare.screenSquare(context, mini, new PVector(x + i * mini.x, y));

		x = sizes.little_screen_pos.x - mini.x;
		y = sizes.little_screen_pos.y + little.y / 2.0f
				- mini.y / 2.0f;

		for (int i = 0; i < n_around; ++i)
			this.sq_prevs[i] = ArrowsSquare.screenSquare(context, mini, new PVector(x - i * mini.x, y));

		this.sq_curr = ArrowsSquare.screenSquare(context, little, sizes.little_screen_pos);
	}
	
	public void draw() {
		for (int i = 0; i < n_around; ++i) {
			if (screen_prevs[i] != null)
				draw(screen_prevs[i], sq_prevs[i]);	
		}
		
		for (int i = 0; i < n_around; ++i) {
			if (screen_nexts[i] != null)
				draw(screen_nexts[i], sq_nexts[i]);	
		}
		
		drawCurrent();
	}
	
	private void draw(PGraphics graph, ArrowsSquare sq) {
		sq.draw();
		context.image(graph, sq.pos, sq.diag);
	}
	
	private void drawCurrent() {
		draw(screen_curr, sq_curr);

		PVector sq_pos = context.getScreen_draw_p(), factor = new PVector(
				sq_curr.diag.x / screen_curr.width, sq_curr.diag.y
						/ screen_curr.height);
		sq_pos = new PVector(sq_curr.pos.x + Math.abs(sq_pos.x * factor.x),
				sq_curr.pos.y + Math.abs(sq_pos.y * factor.y));

		context.noFill();
		context.stroke(pos_sq_color);
		context.rect(sq_pos, new PVector(context.width * factor.x,
				context.height * factor.y), 0);
	}
	
	/**
	 * 
	 * @return if the mouse pointer (mouseX, mouseY) is on any side of this ScreensIterator
	 * 
	 */
	public int isOnAny(int mouseX, int mouseY) {
		int ret = Integer.MIN_VALUE;
		
		if (isOnCurr(mouseX, mouseY))
			return 0;
		
		for (int i = 0; i < n_around; ++i) {
			if (isOnPrevs(mouseX, mouseY, i))
				return -i-1;
			else if (isOnNexts(mouseX, mouseY, i))
				return i+1;
		}
		
		return ret;
	}
	
	private boolean isOnCurr(int mouseX, int mouseY) {
		return isOnSquare(mouseX, mouseY, screen_curr, sq_curr);
	}
	
	private boolean isOnNexts(int mouseX, int mouseY, int i) {
		return isOnSquare(mouseX, mouseY, screen_nexts[i], sq_nexts[i]);
	}
	
	private boolean isOnPrevs(int mouseX, int mouseY, int i) {
		return isOnSquare(mouseX, mouseY, screen_prevs[i], sq_prevs[i]);
	}

	private boolean isOnSquare(int mouseX, int mouseY, PGraphics graph, ArrowsSquare sq) {
		return (graph != null) && (sq != null) && sq.isOnAnySide(mouseX, mouseY);
	}
	
	private synchronized void setNexts() {
		int sc_pos_aux = screen_pos;

		for (int i = 0; i < n_around; ++i) {
			if (sc_pos_aux < (screens.size()-1))
				screen_nexts[i] = screens.get(++sc_pos_aux);
			else
				screen_nexts[i] = null;
		}
	}
	
	private synchronized void setPrevs() {
		int sc_pos_aux = screen_pos;

		for (int i = 0; i < n_around; ++i) {
			if (sc_pos_aux > 0)
				screen_prevs[i] = screens.get(--sc_pos_aux);
			else
				screen_prevs[i] = null;
		}
	}
}