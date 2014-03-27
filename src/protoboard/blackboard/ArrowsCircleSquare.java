package protoboard.blackboard;

import processing.core.PApplet;
import protoboard.Constants.BlackboardC;

/**
 * Represents a 2D square with 4 optional selection arrow and a circle inside. It's an
 * immutable object.
 * 
 */
class ArrowsCircleSquare extends ArrowsSquare {
	// Static factory methods
	public static ArrowsCircleSquare drawLineWeightCircleSquare(
			PApplet context, float radius, int[] ball_color) {
		return new ArrowsCircleSquare(context, BlackboardC.common_square_args,
				BlackboardC.draw_line_weight_square_pos,
				BlackboardC.square_ext_color,
				BlackboardC.number_square_fill_color, true, false, radius,
				ball_color);
	}

	public final float[] center;
	public final float diam;

	public final int[] ball_color;

	public ArrowsCircleSquare(PApplet context, float[] sq_args, float[] sq_pos,
			int[] sq_tria_color, int[] draw_color, boolean show_lr_triangles,
			boolean show_ud_triangles, float diam, int[] ball_color) {
		super(context, sq_args, sq_pos, sq_tria_color, draw_color,
				show_lr_triangles, show_ud_triangles);

		this.center = new float[2];
		this.center[0] = sq_pos[0] + sq_args[0] / 2.0f;
		this.center[1] = sq_pos[1] + sq_args[1] / 2.0f;
		this.diam = diam <= sq_args[0] ? diam : sq_args[0];
		this.ball_color = ball_color;
	}

	public ArrowsCircleSquare(PApplet context, float[] sq_args, float[] sq_pos,
			int[] sq_tria_color, int[] draw_color, float diam, int[] ball_color) {
		this(context, sq_args, sq_pos, sq_tria_color, draw_color, true, false,
				diam, ball_color);
	}

	@Override
	public void draw() {
		super.draw();

		context.stroke(ball_color[0], ball_color[1], ball_color[2]);
		context.strokeWeight(diam);
		context.point(center[0], center[1]);
	}
}