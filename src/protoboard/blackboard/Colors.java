package protoboard.blackboard;

import protoboard.Constants;

/**
 * Simple class that maintain the position in a color array.
 * @see Constants.Blackboard.draw_colors
 * 
 */
class Colors {
	private int pos = 0;
	private static final int max_pos = Constants.Blackboard.draw_colors.length;

	public synchronized int[] get() {
		return Constants.Blackboard.draw_colors[pos];
	}

	public synchronized void next() {
		pos = (pos+1) % max_pos;
	}

	public synchronized void prev() {
		if ((--pos) == -1)
			pos = max_pos-1;
	}
}