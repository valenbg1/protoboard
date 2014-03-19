package protoboard;

import java.awt.AWTException;

import protoboard.input.Input;
import protoboard.leapmotion.LeapMotionListener;

import com.leapmotion.leap.Controller;

/**
 * Starting point of protoboard.
 * 
 */
public final class Main {
	public static final Controller lm_controller = new Controller();
	public static final LeapMotionListener lm_listener = new LeapMotionListener();
	
	public static void main(String[] args) {
		lm_controller.addListener(lm_listener);
		
		// Testing
		/**/
		Input input = null;
		
		try {
			input = new Input(lm_listener);
		} catch (AWTException e) {
			Constants.printExceptionToErr("", e);
		}
		
		input.run();
		/**/
		
		/*/
		PApplet.main(Blackboard.class.getName());
		/**/
		
		//controller.removeListener(lm_listener);
	}
}