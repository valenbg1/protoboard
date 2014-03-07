package protoboard;

import java.awt.Toolkit;

/**
 * Class externalizing all the constant within the application
 * 
 */
public final class Constants {
	public static final int displayWidth = Toolkit.getDefaultToolkit().getScreenSize().width;
	public static final int displayHeight = Toolkit.getDefaultToolkit().getScreenSize().height;
	
	/**
	 * Externalized constant within blackboard mode scope
	 *
	 */
	public static final class Blackboard {
		public static final int save_text_time = 100;
		public static final int background_rgb[] = { 0, 0, 0 };
		public static final String save_name = "board";
		public static final float draw_line_weight = 10;
		public static final int max_screens = 99;
		
		public static final int square_ext_color[] = { 255, 255, 255 };
		public static final float square_weight = 3;
		public static final float[] common_square_args = { 50, 50, 10 };
		public static final float[] color_square_pos = { 10, displayHeight - 60 };
		public static final float[] number_square_pos = { displayWidth - 60, displayHeight - 60 };
		public static final int number_square_fill_color[] = { 0, 0, 0 };
		
		public static final int number_size = 50;
		public static final int number_color[] = { 255, 255, 255 };
		public static final float[] number_pos = { displayWidth - 51, displayHeight - 17 };
		public static final float number_gt_10_xpos = Constants.Blackboard.number_pos[0]*0.9674f;
		
		public static final int save_text_color[] = { 255, 255, 255 };
		public static final int save_text_size = 30;
		public static final String save_text = "SAVED!";
		public static final float[] save_text_pos = { displayWidth - 110, displayHeight - 70 };
		
		public static final int[][] draw_colors = { 
			{255, 255, 255}, // White
			{255, 255, 102}, // Yellow
			{102, 255, 102}, // Green
			{102, 255, 255}, // Blue
			{255, 102, 102}, // Red
			{178, 102, 255}, // Purple
		};
	}
	
	/**
	 * Externalized constant within Leap Motion controller scope
	 *
	 */
	public static final class LeapMotion {
		public static final String onConnect = "Leap connected";
		public static final String onDisconnect = "Leap disconnected";
		public static final String onExit = "Leap exited";
		public static final String onInit = "Leap initialized";
		
		public static final String onCircle = "Leap circle";
		public static final String onLeftSwipe = "Leap back swipe";
		public static final String onRightSwipe = "Leap forth swipe";
		public static final String onDownSwipe = "Leap down swipe";
		
		public static final float swipe_minlength = 225;
		
		public static final float max_ang_swipe = (float) Math.toRadians(15);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}