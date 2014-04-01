package protoboard.blackboard;

import processing.core.PVector;
import protoboard.Constants.BlackboardC;

/**
 * Externalized relative sizes within blackboard mode scope. It's an immutable object.
 *
 */
class Sizes {
	public final int width;
	public final int height;
	
	public final int dispWidthxHeight;
	
	public final float draw_line_weight;
	public final float draw_line_weight_sum;
	public final float[] draw_line_weight_limits;

	public final float erase_square_border_weight;
	
	public final float square_weight;
	public final PVector common_square_diag;
	public final PVector color_square_pos;
	public final PVector number_square_pos;
	public final float square_triangle_length;
	public final float square_triangle_det;
	public final PVector draw_line_weight_square_pos;
	
	public final PVector number_pos;
	public final float number_gt_10_xpos;
	
	public final float save_text_size;
	public final float save_text_ypos;
	
	public final PVector little_screen_pos;
	
	public Sizes(int width, int height) {
		this.width = width;
		this.height = height;
		this.dispWidthxHeight = width*height;
		
		this.draw_line_weight = dispWidthxHeight*0.000009765625f;
		this.draw_line_weight_sum = draw_line_weight*0.5f;
		this.draw_line_weight_limits = new float[]{ draw_line_weight*0.15f, draw_line_weight*5f };
		
		this.erase_square_border_weight = dispWidthxHeight*0.00000176562f;
		
		this.square_weight = dispWidthxHeight*0.0000029296875f;
		this.common_square_diag = new PVector(dispWidthxHeight*0.000048828125f, dispWidthxHeight*0.000048828125f);
		this.color_square_pos = new PVector(width*0.0078125f, height*0.925f);
		this.number_square_pos = new PVector(width - color_square_pos.x - common_square_diag.x, color_square_pos.y);
		this.square_triangle_length = dispWidthxHeight*0.000009765625f;
		this.square_triangle_det = dispWidthxHeight*0.00001953125f;
		this.draw_line_weight_square_pos = new PVector(color_square_pos.x, color_square_pos.y*0.98f - common_square_diag.y);
		
		this.number_pos = new PVector(number_square_pos.x + common_square_diag.x*0.18f, number_square_pos.y + common_square_diag.y*0.88f );
		this.number_gt_10_xpos = number_square_pos.x - common_square_diag.x + common_square_diag.x*0.40f;
		
		this.save_text_size = dispWidthxHeight*0.000107421875f;
		this.save_text_ypos = height*0.9125f;
		
		this.little_screen_pos = new PVector(width / 2.0f - width
				* BlackboardC.little_screen_prop / 2.0f, height / 2.0f - height
				* BlackboardC.little_screen_prop / 2.0f);
	}
}