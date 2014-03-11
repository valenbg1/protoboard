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
public class ScreensIterator {
	private CopyOnWriteArrayList<PGraphics> screens;
	private PGraphics screen_curr, screen_prev, screen_next;
	private AtomicInteger screen_pos;
	
	public ScreensIterator(CopyOnWriteArrayList<PGraphics> screens,
			int screen_pos) {
		this.screens = screens;
		this.screen_pos = new AtomicInteger(screen_pos);
		this.screen_curr = screens.get(screen_pos);
		this.screen_prev = getPrev();
		this.screen_next = getNext();
	}
	
	public synchronized void advance() {
		int sc_pos_aux = screen_pos.get();
		
		if (sc_pos_aux < (screens.size()-1))
			screen_pos.incrementAndGet();
		
		this.screen_curr = screens.get(screen_pos.get());
		this.screen_prev = getPrev();
		this.screen_next = getNext();
	}
	
	public PGraphics current() {
		return screen_curr;
	}
	
	public int currentPos() {
		return screen_pos.get();
	}
	
	public void draw(PApplet board) {
		drawPrev(board);
		drawCurrent(board);
		drawNext(board);
	}
	
	private void draw(PApplet board, float x, float y, PGraphics graph, boolean mini) {
		float width = graph.width, height = graph.height;
		
		if (mini) {
			width *= BlackboardC.mini_screen_prop;
			height *= BlackboardC.mini_screen_prop;
		} else {
			width *= BlackboardC.little_screen_prop;
			height *= BlackboardC.little_screen_prop;
		}
		
		ArrowsSquare sq = ArrowsSquare.screenSquare(width, height, x, y);
		
		board.image(graph, x, y, width, height);
		sq.draw(board);
	}
	
	public void drawCurrent(PApplet board) {
		draw(board, BlackboardC.little_screen_pos[0], BlackboardC.little_screen_pos[1], screen_curr, false);
	}

	public void drawNext(PApplet board) {
		float height = screen_next.height*BlackboardC.mini_screen_prop;
		float x = BlackboardC.little_screen_pos[0] + Constants.displayWidth*BlackboardC.little_screen_prop;
		float y = BlackboardC.little_screen_pos[1] + Constants.displayHeight*BlackboardC.little_screen_prop/2.0f - height/2.0f;
		
		draw(board, x, y, screen_next, true);		
	}
	
	public void drawPrev(PApplet board) {
		float width = screen_prev.width*BlackboardC.mini_screen_prop, height = screen_prev.height*BlackboardC.mini_screen_prop;
		float x = BlackboardC.little_screen_pos[0] - width;
		float y = BlackboardC.little_screen_pos[1] + Constants.displayHeight*BlackboardC.little_screen_prop/2.0f - height/2.0f;
		
		draw(board, x, y, screen_prev, true);
	}
	
	private synchronized PGraphics getNext() {
		int sc_pos_aux = screen_pos.get();
		
		if (sc_pos_aux < (screens.size()-1))
			++sc_pos_aux;
		
		return screens.get(sc_pos_aux);
	}
	
	private synchronized PGraphics getPrev() {
		int sc_pos_aux = screen_pos.get();
		
		if (sc_pos_aux > 0)
			--sc_pos_aux;
		
		return screens.get(sc_pos_aux);
	}
	
	public PGraphics next() {
		return screen_next;
	}

	public PGraphics prev() {
		return screen_prev;
	}
	
	public synchronized void regress() {
		int sc_pos_aux = screen_pos.get();
		
		if (sc_pos_aux > -1)
			screen_pos.decrementAndGet();
		
		this.screen_curr = screens.get(screen_pos.get());
		this.screen_prev = getPrev();
		this.screen_next = getNext();
	}
}