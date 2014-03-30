package protoboard.blackboard;

import processing.core.PVector;
import protoboard.Constants.BlackboardC;

/**
 * Represents a 2D square with 4 optional selection arrow and a circle inside. It's an
 * immutable object.
 * 
 */
class ArrowsCircleSquare extends ArrowsSquare {
	// Static factory methods
	public static ArrowsCircleSquare drawLineWeightCircleSquare(MyPApplet context, float diam, int ball_color) {
		return new ArrowsCircleSquare(context, BlackboardC.common_square_diag,
				BlackboardC.draw_line_weight_square_pos,
				context.color(BlackboardC.square_ext_color),
				context.color(BlackboardC.number_square_fill_color), diam,
				ball_color, BlackboardC.common_square_rad);
	}

	public final PVector center;
	public final float diam;

	public final int ball_color;

	public ArrowsCircleSquare(MyPApplet context, PVector diag, PVector pos,
			int sq_tria_color, int draw_color, boolean show_lr_triangles,
			boolean show_ud_triangles, float diam, int ball_color, float rad) {
		super(context, diag, pos, sq_tria_color, draw_color,
				show_lr_triangles, show_ud_triangles, rad);

		this.center = new PVector(pos.x + diag.x/2, pos.y + diag.y/2);
		this.diam = diam <= diag.mag() ? diam : diag.mag();
		this.ball_color = ball_color;
	}

	public ArrowsCircleSquare(MyPApplet context, PVector diag, PVector pos,
			int sq_tria_color, int draw_color, float diam, int ball_color, float rad) {
		this(context, diag, pos, sq_tria_color, draw_color, true, false, diam, ball_color, rad);
	}

	@Override
	public void draw() {
		super.draw();
		context.stroke(ball_color);
		context.circle(center, diam);
	}
}