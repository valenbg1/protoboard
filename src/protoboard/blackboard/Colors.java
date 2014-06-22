package protoboard.blackboard;

import protoboard.Constants.BlackboardC;

/**
 * Simple class that maintain the position in a color array. It's an immutable object
 * 
 * @see Constants.BlackboardC.draw_colors
 * 
 */
class Colors {
	public final int pos;
	public static final int max_pos = BlackboardC.draw_colors.length;
	
	public Colors() {
		this.pos = 0;
	}
	
	private Colors(int pos) {
		this.pos = pos;
	}

	public int[] getActual() {
		return BlackboardC.draw_colors[pos];
	}

	public boolean isEraseColor() {
		return (pos+1) == max_pos;
	}

	public Colors next() {
		return new Colors((pos+1) % max_pos);
	}

	public Colors prev() {
		return new Colors((pos-1) < 0 ? max_pos-1 : pos-1);
	}
}