package protoboard;

import java.awt.Toolkit;


/**
 * Class externalizing all the constant within the application.
 * 
 */
public final class Constants {
	public static final int displayWidth = Toolkit.getDefaultToolkit().getScreenSize().width;
	public static final int displayHeight = Toolkit.getDefaultToolkit().getScreenSize().height;
	
//	For testing
//	public static final int displayWidth = 1024;
//	public static final int displayHeight = 768;
	
	public static final int dispWidthxHeight = displayWidth*displayHeight;
	
	/**
	 * Externalized constant within blackboard mode scope.
	 *
	 */
	public static final class BlackboardC {
		public static final int[] info_blue_rgb = { 102, 178, 255 };
		
		public static final int background_rgb[] = { 0, 0, 0 };
		public static final String save_name = "board";
		public static final float draw_line_weight = dispWidthxHeight*0.000009765625f;
		public static final int max_screens = 51;
		public static final int multi_screen_around = 2;
		
		public static final int erase_color[] = background_rgb;
		public static final int erase_square_border_color[] = { 255, 255, 255 };
		public static final float erase_square_border_weight = dispWidthxHeight*0.00000176562f;
		public static final float erase_square_weight = draw_line_weight*1.75f;
		
		public static final int square_ext_color[] = info_blue_rgb;
		public static final float square_weight = dispWidthxHeight*0.0000029296875f;
		public static final float[] common_square_args = { dispWidthxHeight*0.000048828125f, dispWidthxHeight*0.000048828125f, dispWidthxHeight*0.000009765625f };
		public static final float[] color_square_pos = { displayWidth*0.0078125f, displayHeight*0.925f };
		public static final float[] number_square_pos = { displayWidth - color_square_pos[0] - common_square_args[0], color_square_pos[1] };
		public static final int number_square_fill_color[] = background_rgb;
		public static final float square_triangle_length = dispWidthxHeight*0.000009765625f;
		public static final float square_triangle_det = dispWidthxHeight*0.00001953125f;
		
		public static final int number_color[] = { 255, 255, 255 };
		public static final float[] number_pos = { number_square_pos[0] + common_square_args[0]*0.18f, number_square_pos[1] + common_square_args[1]*0.88f };
		public static final float number_gt_10_xpos = number_square_pos[0] - common_square_args[0] + common_square_args[0]*0.40f;
		
		public static final int save_text_color[] = info_blue_rgb;
		public static final float save_text_size = dispWidthxHeight*0.000107421875f;
		public static final String save_text = "SAVED!";
		public static final float save_text_ypos = displayHeight*0.9125f;
		public static final float save_text_time = 1.85f; // s
		
		public static final float mini_screen_prop = 0.15f;
		public static final float little_screen_prop = 0.35f;
		public static final float[] little_screen_pos = { displayWidth/2.0f - displayWidth*little_screen_prop/2.0f, displayHeight/2.0f - displayHeight*little_screen_prop/2.0f };
		
		public static final int[][] draw_colors = { 
			{255, 255, 255}, // White
			{255, 255, 102}, // Yellow
			{102, 255, 102}, // Green
			{102, 255, 255}, // Blue
			{255, 102, 102}, // Red
			{178, 102, 255}, // Purple
			erase_color		 // Erase color
		};
	}
	
	/**
	 * Externalized constant within Leap Motion controller scope.
	 *
	 */
	public static final class LeapMotionListenerC {
		public static final String onConnect = "Leap connected";
		public static final String onDisconnect = "Leap disconnected";
		public static final String onExit = "Leap exited";
		public static final String onInit = "Leap initialized";
		
		public static final String onCircle = "Leap circle";
		public static final String onLeftSwipe = "Leap back swipe";
		public static final String onRightSwipe = "Leap forth swipe";
		public static final String onDownSwipe = "Leap down swipe";
		public static final String onUpSwipe = "Leap up swipe";
		public static final String onScreenTap = "Screen tap";
		public static final String onKeyTap = "Key tap";
		
		public static final float swipe_minlength = 200; // mm
		
		public static final float circle_resolution = 0.5f; // (0, 1]: % circle to send an event of detected circle gesture
		
		public static final float wait_between_gestures = 0.85f; // s
		public static final float wait_between_swipe_gestures = wait_between_gestures; // s
		public static final float wait_between_changing_circle_id = 5f; // s
	}	
}