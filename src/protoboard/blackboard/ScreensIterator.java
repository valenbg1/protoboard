package protoboard.blackboard;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

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
	private CopyOnWriteArrayList<PGraphics> screens;
	private PGraphics screen_curr;
	private PGraphics[] screen_prevs, screen_nexts;
	private ArrowsSquare sq_curr;
	private ArrowsSquare[] sq_prevs, sq_nexts;
	private int n_around;
	private AtomicInteger screen_pos;
	
	public ScreensIterator(CopyOnWriteArrayList<PGraphics> screens,
			int screen_pos, int n_around) {
		this.screens = screens;
		this.screen_pos = new AtomicInteger(screen_pos);
		this.screen_curr = screens.get(screen_pos);
		this.n_around = n_around;
		this.screen_prevs = new PGraphics[n_around];
		this.screen_nexts = new PGraphics[n_around];
		this.sq_prevs = new ArrowsSquare[n_around];
		this.sq_nexts = new ArrowsSquare[n_around];
		
		getPrevs();
		getNexts();
		
		float width_mini = Constants.displayWidth*BlackboardC.mini_screen_prop,
				height_mini = Constants.displayHeight*BlackboardC.mini_screen_prop,
				width_little = Constants.displayWidth*BlackboardC.little_screen_prop,
				height_little = Constants.displayHeight*BlackboardC.little_screen_prop,
				x = BlackboardC.little_screen_pos[0] + width_little,
				y = BlackboardC.little_screen_pos[1] + height_little/2.0f - height_mini/2.0f;
		
		for (int i = 0; i < n_around; ++i)
			this.sq_nexts[i] = ArrowsSquare.screenSquare(width_mini, height_mini, x + i*width_mini, y);
		
		x = BlackboardC.little_screen_pos[0] - width_mini;
		y = BlackboardC.little_screen_pos[1] + height_little/2.0f - height_mini/2.0f;
		
		for (int i = 0; i < n_around; ++i)
			this.sq_prevs[i] = ArrowsSquare.screenSquare(width_mini, height_mini, x - i*width_mini, y);

		this.sq_curr = ArrowsSquare.screenSquare(width_little, height_little, BlackboardC.little_screen_pos[0], BlackboardC.little_screen_pos[1]);
	}
	
	public synchronized void advance() {
		int sc_pos_aux = screen_pos.get();
		
		if (sc_pos_aux < (screens.size()-1))
			screen_pos.incrementAndGet();
		
		this.screen_curr = screens.get(screen_pos.get());
		getPrevs();
		getNexts();
	}
	
	
	public PGraphics current() {
		return screen_curr;
	}
	
	public int currentPos() {
		return screen_pos.get();
	}
	
	public void draw(PApplet board) {
		for (int i = 0; i < n_around; ++i) {
			if (screen_prevs[i] != null)
				draw(board, screen_prevs[i], sq_prevs[i]);	
		}
		
		drawCurrent(board);
		
		for (int i = 0; i < n_around; ++i) {
			if (screen_nexts[i] != null)
				draw(board, screen_nexts[i], sq_nexts[i]);	
		}
	}
	
	private void draw(PApplet board, PGraphics graph, ArrowsSquare sq) {
		sq.draw(board);
		board.image(graph, sq.sq_pos[0], sq.sq_pos[1], sq.sq_args[0], sq.sq_args[1]);
	}
	
	public void drawCurrent(PApplet board) {
		draw(board, screen_curr, sq_curr);
	}
	
	private synchronized void getNexts() {
		int sc_pos_aux = screen_pos.get();
		
		for (int i = 0; i < n_around; ++i) {
			if (sc_pos_aux < (screens.size()-1))
				screen_nexts[i] = screens.get(++sc_pos_aux);
			else
				screen_nexts[i] = null;
		}
	}
	
	private synchronized void getPrevs() {
		int sc_pos_aux = screen_pos.get();
		
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

	public boolean isOnCurr(int mouseX, int mouseY) {
		return isOnSquare(mouseX, mouseY, screen_curr, sq_curr);
	}
	
	public boolean isOnNexts(int mouseX, int mouseY, int i) {
		return isOnSquare(mouseX, mouseY, screen_nexts[i], sq_nexts[i]);
	}
	
	public boolean isOnPrevs(int mouseX, int mouseY, int i) {
		return isOnSquare(mouseX, mouseY, screen_prevs[i], sq_prevs[i]);
	}
	
	private boolean isOnSquare(int mouseX, int mouseY, PGraphics graph, ArrowsSquare sq) {
		return (graph != null) && (sq != null) && sq.isOnAnySide(mouseX, mouseY);
	}
	
	public synchronized void regress() {
		int sc_pos_aux = screen_pos.get();
		
		if (sc_pos_aux > 0)
			screen_pos.decrementAndGet();
		
		this.screen_curr = screens.get(screen_pos.get());
		getPrevs();
		getNexts();
	}
}