package protoboard.blackboard;

import processing.core.PVector;
import protoboard.Constants.BlackboardC;

/**
 * Represents a 2D square with 4 optional selection arrow: left, right, up, down.
 * It's an immutable object.
 * 
 */
class ArrowsSquare extends ArrowsDrawable {
	// Static factory methods
	public static ArrowsSquare colorSquare(MyPApplet context, int draw_color) {
		return new ArrowsSquare(context, BlackboardC.common_square_diag,
				BlackboardC.color_square_pos, context.color(BlackboardC.square_ext_color),
				draw_color);
	}

	public static ArrowsSquare numberSquare(MyPApplet context) {
		return new ArrowsSquare(context, BlackboardC.common_square_diag,
				BlackboardC.number_square_pos, context.color(BlackboardC.square_ext_color),
				context.color(BlackboardC.number_square_fill_color), true, true);
	}

	public static ArrowsSquare screenSquare(MyPApplet context, PVector diag, PVector pos) {
		return new ArrowsSquare(context, diag, pos,
				context.color(BlackboardC.square_ext_color),
				context.color(BlackboardC.number_square_fill_color), false, false);
	}

	public final int draw_color;

	public ArrowsSquare(MyPApplet context, PVector diag, PVector pos,
			int sq_tria_color, int draw_color, boolean show_lr_triangles,
			boolean show_ud_triangles) {
		super(context, diag, pos, sq_tria_color, show_lr_triangles,
				show_ud_triangles);

		this.draw_color = draw_color;
	}

	public ArrowsSquare(MyPApplet context, PVector commonSquareArgs, PVector colorSquarePos,
			int sq_tria_color, int draw_color) {
		this(context, commonSquareArgs, colorSquarePos, sq_tria_color, draw_color, true, false);
	}

	@Override
	public void draw() {
		context.stroke(sq_tria_color);
		context.fill(draw_color);
		context.strokeWeight(BlackboardC.square_weight);
		context.rect(pos, diag, BlackboardC.common_square_rad);
		
		drawTriangles();
	}
}