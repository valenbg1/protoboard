package protoboard.blackboard;

import processing.core.PApplet;
import protoboard.Constants.BlackboardC;

/**
 * Represents a 2D square with 4 selection arrow: left, right, up, down. It's an
 * immutable object.
 * 
 */
class ArrowsSquare extends ArrowsDrawable {
	// Static factory methods
	public static ArrowsSquare colorSquare(int[] draw_color) {
		return new ArrowsSquare(BlackboardC.common_square_args,
				BlackboardC.color_square_pos, BlackboardC.square_ext_color,
				draw_color);
	}
	
	public static ArrowsSquare numberSquare() {
		return new ArrowsSquare(BlackboardC.common_square_args,
				BlackboardC.number_square_pos, BlackboardC.square_ext_color,
				BlackboardC.number_square_fill_color, true, true);
	}

	public static ArrowsSquare screenSquare(float width, float height, float x, float y) {
		return new ArrowsSquare(new float[] { width, height,
				BlackboardC.common_square_args[2] }, new float[] { x, y },
				BlackboardC.square_ext_color,
				BlackboardC.number_square_fill_color, false, false);
	}

	public final int[] draw_color;
	
	public ArrowsSquare(float[] sq_args, float[] sq_pos, int[] sq_tria_color,
			int[] draw_color) {
		this(sq_args, sq_pos, sq_tria_color, draw_color, true, false);
	}

	public ArrowsSquare(float[] sq_args, float[] sq_pos, int[] sq_tria_color,
			int[] draw_color, boolean show_lr_triangles, boolean show_ud_triangles) {
		super(sq_args, sq_pos, sq_tria_color, show_lr_triangles, show_ud_triangles);
		
		this.draw_color = draw_color;
	}

	public void draw(PApplet board) {
		board.stroke(sq_tria_color[0], sq_tria_color[1], sq_tria_color[2]);
		board.fill(draw_color[0], draw_color[1], draw_color[2]);
		board.strokeWeight(BlackboardC.square_weight);
		board.rect(sq_pos[0], sq_pos[1], sq_args[0], sq_args[1], sq_args[2]);

		drawTriangles(board);
	}
}