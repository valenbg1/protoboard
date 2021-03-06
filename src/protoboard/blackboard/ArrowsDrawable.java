package protoboard.blackboard;

import processing.core.PVector;

/**
 * Represents a 2D abstract square (for selection) with 4 optional selection arrows:
 * left, right, up, down
 * 
 */
abstract class ArrowsDrawable {
	public final Blackboard context;
	public final int sq_tria_color;
	public final PVector pos, diag;
	public final PVector[] ltr_pos, rtr_pos, utr_pos, dtr_pos;
	public final boolean show_lr_triangles, show_ud_triangles;
	public final float square_triangle_det;
	
	protected ArrowsDrawable(Blackboard context, PVector diag, PVector pos,
			int sq_tria_color) {
		this(context, diag, pos, sq_tria_color, true, false);
	}

	protected ArrowsDrawable(Blackboard context, PVector diag,
			PVector pos, int sq_tria_color2, boolean show_lr_triangles,
			boolean show_ud_triangles) {
		Sizes sizes = context.getSizes();
		
		this.context = context;
		this.diag = diag;
		this.pos = pos;
		this.sq_tria_color = sq_tria_color2;
		this.show_lr_triangles = show_lr_triangles;
		this.show_ud_triangles = show_ud_triangles;
		this.square_triangle_det = sizes.square_triangle_det;
		
		float square_triangle_length = sizes.square_triangle_length;
		
		if (this.show_lr_triangles) {
			ltr_pos = new PVector[] {
				new PVector(pos.x, pos.y + diag.y/2.0f - square_triangle_length/2.0f),
				new PVector(pos.x, pos.y + diag.y/2.0f + square_triangle_length/2.0f),
				new PVector(pos.x - square_triangle_length*((float) Math.sqrt(3))/2.0f, pos.y + diag.y/2.0f)
			};
			
			rtr_pos = new PVector[] {
				new PVector(pos.x + diag.x, ltr_pos[0].y),
				new PVector(pos.x + diag.x, ltr_pos[1].y),
				new PVector(pos.x + diag.x + square_triangle_length*((float) Math.sqrt(3))/2.0f, ltr_pos[2].y)
			};
		} else {
			this.ltr_pos = null;
			this.rtr_pos = null;
		}
		
		if (this.show_ud_triangles) {
			utr_pos = new PVector[] {
				new PVector(pos.x + diag.x/2.0f - square_triangle_length/2.0f, pos.y),
				new PVector(pos.x + diag.x/2.0f + square_triangle_length/2.0f, pos.y),
				new PVector(pos.x + diag.x/2.0f, pos.y - square_triangle_length*((float) Math.sqrt(3))/2.0f)
			};
			
			dtr_pos = new PVector[] {
				new PVector(pos.x + diag.x/2.0f - square_triangle_length/2.0f, pos.y + diag.y),
				new PVector(pos.x + diag.x/2.0f + square_triangle_length/2.0f, pos.y + diag.y),
				new PVector(pos.x + diag.x/2.0f, pos.y + diag.y + square_triangle_length*((float) Math.sqrt(3))/2.0f)
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
			context.triangle(ltr_pos[0], ltr_pos[1], ltr_pos[2]);
			// Right triangle
			context.triangle(rtr_pos[0], rtr_pos[1], rtr_pos[2]);
		}

		if (show_ud_triangles) {
			// Up triangle
			context.fill(sq_tria_color);
			context.triangle(utr_pos[0], utr_pos[1], utr_pos[2]);
			// Down triangle
			context.triangle(dtr_pos[0], dtr_pos[1], dtr_pos[2]);
		}
	}

	/**
	 * 
	 * @return if the mouse pointer (mouseX, mouseY) is on any side of the ArrowsDrawable
	 * 
	 */
	public boolean isOnAnySide(int mouseX, int mouseY) {
		if (show_ud_triangles)
			return isOnLeft(mouseX, mouseY) || isOnRight(mouseX, mouseY)
					|| isOnUp(mouseX, mouseY) || isOnDown(mouseX, mouseY);
		else
			return isOnLeft(mouseX, mouseY) || isOnRight(mouseX, mouseY);
	}

	/**
	 * 
	 * @return if the mouse pointer (mouseX, mouseY) is on the down arrow
	 * 
	 */
	public boolean isOnDown(int mouseX, int mouseY) {
		float extension = square_triangle_det;
		
		return (mouseX <= (pos.x + diag.x))
				&& (mouseY <= (pos.y + diag.y + extension))

				&& (mouseX >= pos.x)
				&& (mouseY >= (pos.y + diag.y));
	}

	/**
	 * 
	 * @return if the mouse pointer (mouseX, mouseY) is on the left arrow
	 * 
	 */
	public boolean isOnLeft(int mouseX, int mouseY) {
		float extension = show_lr_triangles ? square_triangle_det : 0;
		
		return (mouseX <= (pos.x + diag.x / 2.0f))
				&& (mouseY >= pos.y)

				&& (mouseX >= (pos.x - extension))
				&& (mouseY <= (pos.y + diag.y));
	}
	
	/**
	 * 
	 * @return if the mouse pointer (mouseX, mouseY) is on the right arrow
	 * 
	 */
	public boolean isOnRight(int mouseX, int mouseY) {
		float extension = show_lr_triangles ? square_triangle_det : 0;
		
		return (mouseX >= (pos.x + diag.x / 2.0f))
				&& (mouseY >= pos.y)

				&& (mouseX <= (pos.x + diag.x + extension))
				&& (mouseY <= (pos.y + diag.y));
	}

	/**
	 * 
	 * @return if the mouse pointer (mouseX, mouseY) is on the up arrow
	 * 
	 */
	public boolean isOnUp(int mouseX, int mouseY) {
		float extension = square_triangle_det;
		
		return (mouseX <= (pos.x + diag.x))
				&& (mouseY >= (pos.y - extension))

				&& (mouseX >= pos.x)
				&& (mouseY <= pos.y);
	}
}