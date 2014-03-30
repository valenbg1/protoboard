package protoboard.blackboard;

import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;

/**
 * Custom PApplet class with some handy methods.
 * 
 */
public class MyPApplet extends PApplet {
	private static final long serialVersionUID = 3653833295051693472L;

	public void circle(PVector center, float diameter) {
		this.strokeWeight(diameter);
		this.point(center.x, center.y);
	}

	public int color(int[] a) {
		if (a.length == 3)
			return this.color(a[0], a[1], a[2]);
		else if (a.length == 4)
			return this.color(a[0], a[1], a[2], a[3]);
		else
			return -1;
	}

	public void image(PImage img, PVector pos) {
		this.image(img, pos.x, pos.y);
	}

	public void image(PImage img, PVector pos, PVector diag) {
		this.image(img, pos.x, pos.y, diag.x, diag.y);
	}

	public void rect(PVector pos, PVector diagonal, float rad) {
		this.rect(pos.x, pos.y, diagonal.x, diagonal.y, rad);
	}

	public void text(int num, PVector pos) {
		this.text(num, pos.x, pos.y);
	}

	public void triangle(PVector a, PVector b, PVector c) {
		this.triangle(a.x, a.y, b.x, b.y, c.x, c.y);
	}
}