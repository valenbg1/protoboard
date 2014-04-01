package protoboard;

import java.awt.Toolkit;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;


/**
 * Class externalizing all the constants within the application.
 * 
 */
public final class Constants {
	public static void printExceptionToErr(String message, Exception e) {
		System.err.println("-------------------------------------------\n"
				+ message + ":\n");

		if (e.getMessage() != null)
			System.err.println(e.getMessage());
		else
			e.printStackTrace();

		System.err.println("-------------------------------------------\n");
	}
	
	/**
	 * Externalized constants within blackboard mode scope.
	 *
	 */
	public static final class BlackboardC {
		public static final int displayWidth = Toolkit.getDefaultToolkit().getScreenSize().width;
		public static final int displayHeight = Toolkit.getDefaultToolkit().getScreenSize().height;
		
		public static final int[] info_blue_rgb = { 102, 178, 255 };
		
		public static final int background_rgb[] = { 0, 0, 0 };
		public static final int background_rgb_1[] = { 255 - background_rgb[0], 255 - background_rgb[1], 255 - background_rgb[2] };
		public static final String save_name = "board";
		public static final int max_screens = 51;
		public static final int multi_screen_around = 2;
		public static final String save_extension = "png";
		
		public static final int erase_color[] = background_rgb;
		public static final int erase_square_border_color[] = background_rgb_1;
		public static final float erase_square_weight = 1.75f;
		
		public static final int square_ext_color[] = info_blue_rgb;
		public static final float common_square_rad = 6;
		public static final int number_square_fill_color[] = background_rgb;
		
		public static final int number_color[] = background_rgb_1;
		
		public static final int save_text_color[] = info_blue_rgb;
		public static final String save_text = "SAVED!";
		public static final float save_text_time = 1.85f; // s
		
		public static final float mini_screen_prop = 0.15f;
		public static final float little_screen_prop = 0.35f;
		
		public static final int[][] draw_colors = { 
			background_rgb_1, // White (when background is (0, 0, 0)
			{255, 255, 102}, // Yellow
			{102, 255, 102}, // Green
			{102, 255, 255}, // Blue
			{255, 102, 102}, // Red
			{178, 102, 255}, // Purple
			erase_color		 // Erase color
		};
	}
	
	/**
	 * Externalized constants within Input mode scope.
	 *
	 */
	public static final class InputC {
		public static final String err_no_robot = "The platform configuration does not allow low-level input control";
		public static final String err_interr = "Interrupted while running Input";
		public static final int robot_delay = 25; // ms
		
		public static final int onDownSwipe = KeyEvent.VK_UP;
		public static final int onLeftSwipe = KeyEvent.VK_RIGHT;
		public static final int onRightSwipe = KeyEvent.VK_LEFT;
		public static final int onScreenTap = InputEvent.BUTTON1_DOWN_MASK;
		public static final int onUpSwipe = KeyEvent.VK_DOWN;
	}
	
	/**
	 * Externalized constants within Leap Motion controller scope.
	 *
	 */
	public static final class LeapMotionListenerC {
		public static final String onConnect = "Leap connected";
		public static final String onDisconnect = "Leap disconnected";
		public static final String onExit = "Leap exited";
		public static final String onInit = "Leap initialized";
		
		public static final String onCircle = "Leap circle";
		public static final String onLeftSwipe = "Leap forth swipe";
		public static final String onRightSwipe = "Leap back swipe";
		public static final String onDownSwipe = "Leap down swipe";
		public static final String onUpSwipe = "Leap up swipe";
		public static final String onScreenTap = "Screen tap";
		public static final String onKeyTap = "Key tap";
		
		public static final float swipe_minlength = 80; // mm
		// Testing
		/*/
		public static final float[] rswipe_limits = { 150, 300 }; // mm
		public static final float[] lswipe_limits = { 90, 210 }; // mm
		public static final float[] uswipe_limits = { 250, 450 }; // mm
		public static final float[] dswipe_limits = { 35, 170 }; // mm
		/**/
		/**/
		public static final float[] rswipe_limits = { 0, 800 }; // mm
		public static final float[] lswipe_limits = { 0, 800 }; // mm
		public static final float[] uswipe_limits = { 0, 800 }; // mm
		public static final float[] dswipe_limits = { 0, 800 }; // mm
		/**/
		
		public static final float circle_resolution = 0.5f; // (0, 1]: % circle to send an event of detected circle gesture
		
		public static final float wait_between_gestures = 0.85f; // s
		public static final float wait_between_swipe_gestures = 1f; // s
		public static final float wait_between_changing_circle_id = 5f; // s
	}
	
	/**
	 * Externalized constants within the main Swing interface.
	 *
	 */
	public static final class MainIfaceC {
		public static final String title = "Protoboard";
		
		public static final String input_text_button = "Input mode";
		public static final String blackboard_text_button = "Blackboard mode";
		public static final String config_menu = "Configuration";
		public static final String help_menu = "Help";
		public static final String config_input_menu = input_text_button;
		public static final String config_black_menu = blackboard_text_button;
		public static final String about_opt = "About";
		public static final String file_menu = "File";
		public static final String load_images_opt = "Open saved images...";
		public static final String save_images_opt = "Save all current images...";
		
		public static final String exit_opt = "Exit";
		public static final String load_select_label = "Selected to load: ";
		
		public static final String save_to_label = "Current images saved to: ";	
	
		/**
		 * Externalized constants within de About dialog interface
		 *
		 */
		public static final class AboutDialogC {
			public static final String title = "About";
			public static final String pro_name = MainIfaceC.title;
			public static final String univers_name = "Complutense university of Madrid, 2014.";
			public static final String main_text = "<font face=\"Tahoma\"><br>"
					+ "Simple blackboard application that interacts with a Leap Motion sensor.<br><br>"
					+ "This software is part of an end-of-degree project for computer engineering degree by:<br>"
					+ "* Valentín Blanco Gómez:<br>"
					+ "&emsp;·<a href=\"mailto:valentin_gomez1@hotmail.com\">valentin_gomez1@hotmail.com</a><br>"
					+ "&emsp;·<a href=\"http://github.com/valenbg1\">http://github.com/valenbg1</a><br>"
					+ "<br>* Raúl Sampedro Ruiz:<br>"
					+ "&emsp;·<a href=\"mailto:rsrdesarrollo@gmail.com\">rsrdesarrollo@gmail.com</a><br>"
					+ "&emsp;·<a href=\"http://github.com/rsrdesarrollo\">http://github.com/rsrdesarrollo</a><br><br><br><br>"
					+ "- <a href=\"http://github.com/valenbg1/protoboard\">Protoboard</a>'s GitHub.<br><br>"
					+ "- This software is licensed under the <a href=\"http://www.gnu.org/licenses/gpl-3.0.html\">GPLv3</a> terms.</font>";
		}
	}
}