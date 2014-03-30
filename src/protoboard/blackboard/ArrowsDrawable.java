package protoboard.blackboard;

import processing.core.PVector;
import protoboard.Constants;
import protoboard.Constants.BlackboardC;

/**
 * Represents a 2D abstract square (for selection) with 4 optional selection arrows:
 * left, right, up, down.
 * 
 */
abstract class ArrowsDrawable {
	public final MyPApplet context;
	public final int sq_tria_color;
	public final PVector pos, diag;
	public final float[][] ltr_pos, rtr_pos, utr_pos, dtr_pos;
	public final boolean show_lr_triangles, show_ud_triangles;
	
	protected ArrowsDrawable(MyPApplet context, PVector diag, PVector pos,
			int sq_tria_color) {
		this(context, diag, pos, sq_tria_color, true, false);
	}

	protected ArrowsDrawable(MyPApplet context, PVector diag,
			PVector pos, int sq_tria_color2, boolean show_lr_triangles,
			boolean show_ud_triangles) {
		this.context = context;
		this.diag = diag;
		this.pos = pos;
		this.sq_tria_color = sq_tria_color2;
		this.show_lr_triangles = show_lr_triangles;
		this.show_ud_triangles = show_ud_triangles;
		
		if (this.show_lr_triangles) {
			float square_triangle_length = Constants.BlackboardC.square_triangle_length;
	
			ltr_pos = new float[][] {
				{ pos.x, pos.y + diag.y/2.0f - square_triangle_length/2.0f },
				{ pos.x, pos.y + diag.y/2.0f + square_triangle_length/2.0f },
				{ pos.x - square_triangle_length*((float) Math.sqrt(3))/2.0f, pos.y + diag.y/2.0f }
			};
			
			rtr_pos = new float[][] {
				{ pos.x + diag.x, ltr_pos[0][1] },
				{ pos.x + diag.x, ltr_pos[1][1] },
				{ pos.x + diag.x + square_triangle_length*((float) Math.sqrt(3))/2.0f, ltr_pos[2][1] }
			};
		} else {
			this.ltr_pos = null;
			this.rtr_pos = null;
		}
		
		if (this.show_ud_triangles) {
			float square_triangle_length = Constants.BlackboardC.square_triangle_length;
	
			utr_pos = new float[][] {
				{ pos.x + diag.x/2.0f - square_triangle_length/2.0f, pos.y },
				{ pos.x + diag.x/2.0f + square_triangle_length/2.0f, pos.y },
				{ pos.x + diag.x/2.0f, pos.y - square_triangle_length*((float) Math.sqrt(3))/2.0f }
			};
			
			dtr_pos = new float[][] {
				{ pos.x + diag.x/2.0f - square_triangle_length/2.0f, pos.y + diag.y },
				{ pos.x + diag.x/2.0f + square_triangle_length/2.0f, pos.y + diag.y },
				{ pos.x + diag.x/2.0f, pos.y + diag.y + square_triangle_length*((float) Math.sqrt(3))/2.0f }
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
			context.fill(sq_tria_color);
			context.triangle(ltr_pos[0][0], ltr_pos[0][1], ltr_pos[1][0],
					ltr_pos[1][1], ltr_pos[2][0], ltr_pos[2][1]);
			// Right triangle
			context.triangle(rtr_pos[0][0], rtr_pos[0][1], rtr_pos[1][0],
					rtr_pos[1][1], rtr_pos[2][0], rtr_pos[2][1]);
		}

		if (show_ud_triangles) {
			// Up triangle
			context.fill(sq_tria_color);
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
		
		return (mouseX <= (pos.x + diag.x))
				&& (mouseY <= (pos.y + diag.y + extension))

				&& (mouseX >= pos.x)
				&& (mouseY >= (pos.y + diag.y));
	}

	public boolean isOnLeft(int mouseX, int mouseY) {
		float extension = show_lr_triangles ? BlackboardC.square_triangle_det : 0;
		
		return (mouseX <= (pos.x + diag.x / 2.0f))
				&& (mouseY >= pos.y)

				&& (mouseX >= (pos.x - extension))
				&& (mouseY <= (pos.y + diag.y));
	}
	
	public boolean isOnRight(int mouseX, int mouseY) {
		float extension = show_lr_triangles ? BlackboardC.square_triangle_det : 0;
		
		return (mouseX >= (pos.x + diag.x / 2.0f))
				&& (mouseY >= pos.y)

				&& (mouseX <= (pos.x + diag.x + extension))
				&& (mouseY <= (pos.y + diag.y));
	}

	public boolean isOnUp(int mouseX, int mouseY) {
		float extension = BlackboardC.square_triangle_det;
		
		return (mouseX <= (pos.x + diag.x))
				&& (mouseY >= (pos.y - extension))

				&& (mouseX >= pos.x)
				&& (mouseY <= pos.y);
	}
}