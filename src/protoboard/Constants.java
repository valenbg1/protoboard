package protoboard;

import java.awt.Toolkit;


/**
 * Class externalizing all the constant within the application
 * 
 */
public final class Constants {
	public static final int displayWidth = Toolkit.getDefaultToolkit().getScreenSize().width;
	public static final int displayHeight = Toolkit.getDefaultToolkit().getScreenSize().height;
	public static final int dispWidthxHeight = displayWidth*displayHeight;
	
	/**
	 * Externalized constant within blackboard mode scope
	 *
	 */
	public static final class Blackboard {
		public static final int[] info_blue_rgb = { 102, 178, 255 };
		
		public static final int background_rgb[] = { 0, 0, 0 };
		public static final String save_name = "board";
		public static final float draw_line_weight = dispWidthxHeight*0.000009765625f;
		public static final int max_screens = 99;
		
		public static final int square_ext_color[] = { 255, 255, 255 };
		public static final float square_weight = dispWidthxHeight*0.0000029296875f;
		public static final float[] common_square_args = { dispWidthxHeight*0.000048828125f, dispWidthxHeight*0.000048828125f, dispWidthxHeight*0.000009765625f };
		public static final float[] color_square_pos = { displayWidth*0.0078125f, displayHeight*0.925f };
		public static final float[] number_square_pos = { displayWidth-color_square_pos[0]-common_square_args[0], color_square_pos[1] };
		public static final int number_square_fill_color[] = { 0, 0, 0 };
		
		public static final float number_size = common_square_args[0]*common_square_args[1]*0.02f;
		public static final int number_color[] = { 255, 255, 255 };
		public static final float[] number_pos = { number_square_pos[0]*1.007377049180328f, number_square_pos[1] + number_size };
		public static final float number_gt_10_xpos = number_square_pos[0]*0.97459016393443f;
		
		public static final int save_text_color[] = info_blue_rgb;
		public static final float save_text_size = dispWidthxHeight*0.000107421875f;
		public static final String save_text = "SAVED!";
		public static final float[] save_text_pos = { displayWidth*0.36328125f, displayHeight*0.9125f };
		public static final float save_text_time = 1.85f;
		
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
		
		public static final float swipe_minlength = 200;
		
		public static final float wait_between_gestures = 0.85f;
	}	
}