package protoboard.blackboard;

import processing.core.PApplet;
import protoboard.Constants;
import protoboard.Constants.BlackboardC;

/**
 * Represents a 2D square with 2 selection arrows, left and right. Is an
 * immutable object.
 * 
 */
public class ArrowsSquare {
	public static ArrowsSquare colorSquare(int[] draw_color) {
		return new ArrowsSquare(BlackboardC.common_square_args,
				BlackboardC.color_square_pos, BlackboardC.square_ext_color,
				draw_color);
	}

	public static ArrowsSquare numberSquare() {
		return new ArrowsSquare(BlackboardC.common_square_args,
				BlackboardC.number_square_pos, BlackboardC.square_ext_color,
				BlackboardC.number_square_fill_color);
	}

	public final int[] sq_ext_col, draw_color;
	public final float[] sq_pos, sq_args;
	public final float[][] ltr_pos, rtr_pos;

	public ArrowsSquare(float[] sq_args, float[] sq_pos, int[] sq_ext_col,
			int[] draw_color) {
		this.sq_args = sq_args;
		this.sq_pos = sq_pos;
		this.sq_ext_col = sq_ext_col;
		this.draw_color = draw_color;

		float square_triangle_length = Constants.BlackboardC.square_triangle_length;

		ltr_pos = new float[][] {
			{ sq_pos[0], sq_pos[1] + sq_args[1]/2.0f - square_triangle_length/2.0f },
			{ sq_pos[0], sq_pos[1] + sq_args[1]/2.0f + square_triangle_length/2.0f },
			{ sq_pos[0] - square_triangle_length*((float) Math.sqrt(3))/2.0f, sq_pos[1] + sq_args[1]/2.0f }
		};
		
		rtr_pos = new float[][] {
			{ sq_pos[0] + sq_args[0], ltr_pos[0][1] },
			{ sq_pos[0] + sq_args[0], ltr_pos[1][1] },
			{ sq_pos[0] + sq_args[0] + square_triangle_length*((float) Math.sqrt(3))/2.0f, ltr_pos[2][1] }
		};
	}

	public void draw(PApplet board) {
		board.stroke(sq_ext_col[0], sq_ext_col[1], sq_ext_col[2]);
		board.fill(draw_color[0], draw_color[1], draw_color[2]);
		board.strokeWeight(BlackboardC.square_weight);
		board.rect(sq_pos[0], sq_pos[1], sq_args[0], sq_args[1], sq_args[2]);

		// Left triangle
		board.fill(sq_ext_col[0], sq_ext_col[1], sq_ext_col[2]);
		board.triangle(ltr_pos[0][0], ltr_pos[0][1], ltr_pos[1][0],
				ltr_pos[1][1], ltr_pos[2][0], ltr_pos[2][1]);
		// Right triangle
		board.triangle(rtr_pos[0][0], rtr_pos[0][1], rtr_pos[1][0],
				rtr_pos[1][1], rtr_pos[2][0], rtr_pos[2][1]);
	}
	
	public boolean isOnAnyTriangle(int mouseX, int mouseY) {
		return isOnLeftTriangle(mouseX, mouseY) || isOnRightTriangle(mouseX, mouseY);
	}

	public boolean isOnLeftTriangle(int mouseX, int mouseY) {
		return (mouseX <= (sq_pos[0] + sq_args[0]/2.0f))
				&& (mouseY >= sq_pos[1])
				
				&& (mouseX >= (sq_pos[0] - sq_args[1]*BlackboardC.square_triangle_det))
				&& (mouseY <= (sq_pos[1] + sq_args[1]));
	}

	public boolean isOnRightTriangle(int mouseX, int mouseY) {
		return (mouseX >= (sq_pos[0] + sq_args[0]/2.0f))
				&& (mouseY >= sq_pos[1])
				
				&& (mouseX <= (sq_pos[0] + sq_args[0] + sq_args[1]*BlackboardC.square_triangle_det))
				&& (mouseY <= (sq_pos[1] + sq_args[1]));
	}
}