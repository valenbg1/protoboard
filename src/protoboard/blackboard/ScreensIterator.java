package protoboard.blackboard;

import java.util.ArrayList;
import java.util.List;

import processing.core.PApplet;
import processing.core.PGraphics;
import protoboard.Constants;
import protoboard.Constants.BlackboardC;

/**
 * Implements an iterator over the screens of the blackboard. Used
 * for showing multiple screens at a time (when up swipe is detected).
 *
 */
class ScreensIterator {
	private final ArrayList<PGraphics> screens;
	private PGraphics screen_curr;
	private PGraphics[] screen_prevs, screen_nexts;
	private final ArrowsSquare sq_curr;
	private final ArrowsSquare[] sq_prevs, sq_nexts;
	private final int n_around;
	private int screen_pos;
	private final PApplet context;
	
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
		
		setPrevs();
		setNexts();
		
		float width_mini = Constants.displayWidth*BlackboardC.mini_screen_prop,
				height_mini = Constants.displayHeight*BlackboardC.mini_screen_prop,
				width_little = Constants.displayWidth*BlackboardC.little_screen_prop,
				height_little = Constants.displayHeight*BlackboardC.little_screen_prop,
				x = BlackboardC.little_screen_pos[0] + width_little,
				y = BlackboardC.little_screen_pos[1] + height_little/2.0f - height_mini/2.0f;
		
		for (int i = 0; i < n_around; ++i)
			this.sq_nexts[i] = ArrowsSquare.screenSquare(context, width_mini,
					height_mini, x + i * width_mini, y);

		x = BlackboardC.little_screen_pos[0] - width_mini;
		y = BlackboardC.little_screen_pos[1] + height_little / 2.0f
				- height_mini / 2.0f;

		for (int i = 0; i < n_around; ++i)
			this.sq_prevs[i] = ArrowsSquare.screenSquare(context, width_mini,
					height_mini, x - i * width_mini, y);

		this.sq_curr = ArrowsSquare.screenSquare(context, width_little,
				height_little, BlackboardC.little_screen_pos[0],
				BlackboardC.little_screen_pos[1]);
	}
	
	public synchronized void advance() {
		if (screen_pos < (screens.size()-1))
			++screen_pos;
		
		screen_curr = screens.get(screen_pos);
		setPrevs();
		setNexts();
	}
	
	
	public PGraphics current() {
		return screen_curr;
	}
	
	public int currentPos() {
		return screen_pos;
	}
	
	public void draw(PApplet board) {
		for (int i = 0; i < n_around; ++i) {
			if (screen_prevs[i] != null)
				draw(screen_prevs[i], sq_prevs[i]);	
		}
		
		drawCurrent();
		
		for (int i = 0; i < n_around; ++i) {
			if (screen_nexts[i] != null)
				draw(screen_nexts[i], sq_nexts[i]);	
		}
	}
	
	private void draw(PGraphics graph, ArrowsSquare sq) {
		sq.draw();
		context.image(graph, sq.sq_pos[0], sq.sq_pos[1], sq.sq_args[0], sq.sq_args[1]);
	}
	
	private void drawCurrent() {
		draw(screen_curr, sq_curr);
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
	
	public synchronized void regress() {
		if (screen_pos > 0)
			--screen_pos;
		
		screen_curr = screens.get(screen_pos);
		setPrevs();
		setNexts();
	}
}