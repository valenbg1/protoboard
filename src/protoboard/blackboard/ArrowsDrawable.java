package protoboard.blackboard;

import processing.core.PApplet;
import protoboard.Constants;
import protoboard.Constants.BlackboardC;

/**
 * Represents a 2D abstract square (for selection) with 4 optional selection arrows:
 * left, right, up, down.
 * 
 */
abstract class ArrowsDrawable {
	public final PApplet context;
	public final int[] sq_tria_color;
	public final float[] sq_pos, sq_args;
	public final float[][] ltr_pos, rtr_pos, utr_pos, dtr_pos;
	public final boolean show_lr_triangles, show_ud_triangles;
	
	protected ArrowsDrawable(PApplet context, float[] sq_args, float[] sq_pos,
			int[] sq_tria_color) {
		this(context, sq_args, sq_pos, sq_tria_color, true, false);
	}

	protected ArrowsDrawable(PApplet context, float[] sq_args,
			float[] sq_pos, int[] sq_tria_color, boolean show_lr_triangles,
			boolean show_ud_triangles) {
		this.context = context;
		this.sq_args = sq_args;
		this.sq_pos = sq_pos;
		this.sq_tria_color = sq_tria_color;
		this.show_lr_triangles = show_lr_triangles;
		this.show_ud_triangles = show_ud_triangles;
		
		if (this.show_lr_triangles) {
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
		} else {
			this.ltr_pos = null;
			this.rtr_pos = null;
		}
		
		if (this.show_ud_triangles) {
			float square_triangle_length = Constants.BlackboardC.square_triangle_length;
	
			utr_pos = new float[][] {
				{ sq_pos[0] + sq_args[0]/2.0f - square_triangle_length/2.0f, sq_pos[1] },
				{ sq_pos[0] + sq_args[0]/2.0f + square_triangle_length/2.0f, sq_pos[1] },
				{ sq_pos[0] + sq_args[0]/2.0f, sq_pos[1] - square_triangle_length*((float) Math.sqrt(3))/2.0f }
			};
			
			dtr_pos = new float[][] {
				{ sq_pos[0] + sq_args[0]/2.0f - square_triangle_length/2.0f, sq_pos[1] + sq_args[1] },
				{ sq_pos[0] + sq_args[0]/2.0f + square_triangle_length/2.0f, sq_pos[1] + sq_args[1] },
				{ sq_pos[0] + sq_args[0]/2.0f, sq_pos[1] + sq_args[1] + square_triangle_length*((float) Math.sqrt(3))/2.0f }
			};
		} else {
			this.utr_pos = null;
			this.dtr_pos = null;
		}
	}

	public abstract void draw();
	
	public void drawTriangles() {
		if (show_lr_triangles) {
			// Left triangle
			context.fill(sq_tria_color[0], sq_tria_color[1], sq_tria_color[2]);
			context.triangle(ltr_pos[0][0], ltr_pos[0][1], ltr_pos[1][0],
					ltr_pos[1][1], ltr_pos[2][0], ltr_pos[2][1]);
			// Right triangle
			context.triangle(rtr_pos[0][0], rtr_pos[0][1], rtr_pos[1][0],
					rtr_pos[1][1], rtr_pos[2][0], rtr_pos[2][1]);
		}

		if (show_ud_triangles) {
			// Up triangle
			context.fill(sq_tria_color[0], sq_tria_color[1], sq_tria_color[2]);
			context.triangle(utr_pos[0][0], utr_pos[0][1], utr_pos[1][0],
					utr_pos[1][1], utr_pos[2][0], utr_pos[2][1]);
			// Down triangle
			context.triangle(dtr_pos[0][0], dtr_pos[0][1], dtr_pos[1][0],
					dtr_pos[1][1], dtr_pos[2][0], dtr_pos[2][1]);
		}
	}

	public boolean isOnAnySide(int mouseX, int mouseY) {
		if (show_ud_triangles)
			return isOnLeft(mouseX, mouseY) || isOnRight(mouseX, mouseY)
					|| isOnUp(mouseX, mouseY) || isOnDown(mouseX, mouseY);
		else
			return isOnLeft(mouseX, mouseY) || isOnRight(mouseX, mouseY);
	}

	public boolean isOnDown(int mouseX, int mouseY) {
		float extension = BlackboardC.square_triangle_det;
		
		return (mouseX <= (sq_pos[0] + sq_args[0]))
				&& (mouseY <= (sq_pos[1] + sq_args[1] + extension))

				&& (mouseX >= sq_pos[0])
				&& (mouseY >= (sq_pos[1] + sq_args[1]));
	}

	public boolean isOnLeft(int mouseX, int mouseY) {
		float extension = show_lr_triangles ? BlackboardC.square_triangle_det : 0;
		
		return (mouseX <= (sq_pos[0] + sq_args[0] / 2.0f))
				&& (mouseY >= sq_pos[1])

				&& (mouseX >= (sq_pos[0] - extension))
				&& (mouseY <= (sq_pos[1] + sq_args[1]));
	}
	
	public boolean isOnRight(int mouseX, int mouseY) {
		float extension = show_lr_triangles ? BlackboardC.square_triangle_det : 0;
		
		return (mouseX >= (sq_pos[0] + sq_args[0] / 2.0f))
				&& (mouseY >= sq_pos[1])

				&& (mouseX <= (sq_pos[0] + sq_args[0] + extension))
				&& (mouseY <= (sq_pos[1] + sq_args[1]));
	}

	public boolean isOnUp(int mouseX, int mouseY) {
		float extension = BlackboardC.square_triangle_det;
		
		return (mouseX <= (sq_pos[0] + sq_args[0]))
				&& (mouseY >= (sq_pos[1] - extension))

				&& (mouseX >= sq_pos[0])
				&& (mouseY <= sq_pos[1]);
	}
}