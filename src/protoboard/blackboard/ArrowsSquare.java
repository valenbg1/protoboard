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
	public static ArrowsSquare colorSquare(Blackboard context, int draw_color) {
		Sizes sizes = context.getSizes();
		
		return new ArrowsSquare(context, sizes.common_square_diag,
				sizes.color_square_pos, context.color(BlackboardC.square_ext_color),
				draw_color);
	}

	public static ArrowsSquare numberSquare(Blackboard context) {
		Sizes sizes = context.getSizes();
		
		return new ArrowsSquare(context, sizes.common_square_diag,
				sizes.number_square_pos, context.color(BlackboardC.square_ext_color),
				context.color(BlackboardC.number_square_fill_color), true, true);
	}

	public static ArrowsSquare screenSquare(Blackboard context, PVector diag, PVector pos) {
		return new ArrowsSquare(context, diag, pos,
				context.color(BlackboardC.square_ext_color),
				context.color(BlackboardC.number_square_fill_color), false, false);
	}

	public final int draw_color;
	public final float square_weight;

	public ArrowsSquare(Blackboard context, PVector diag, PVector pos,
			int sq_tria_color, int draw_color, boolean show_lr_triangles,
			boolean show_ud_triangles) {
		super(context, diag, pos, sq_tria_color, show_lr_triangles,
				show_ud_triangles);

		this.draw_color = draw_color;
		this.square_weight = context.getSizes().square_weight;
	}

	public ArrowsSquare(Blackboard context, PVector commonSquareArgs, PVector colorSquarePos,
			int sq_tria_color, int draw_color) {
		this(context, commonSquareArgs, colorSquarePos, sq_tria_color, draw_color, true, false);
	}

	@Override
	public void draw() {
		context.stroke(sq_tria_color);
		context.fill(draw_color);
		context.strokeWeight(square_weight);
		context.rect(pos, diag, BlackboardC.common_square_rad);
		
		drawTriangles();
	}
}