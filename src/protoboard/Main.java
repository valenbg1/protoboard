package protoboard;

import processing.core.PApplet;
import protoboard.blackboard.Blackboard;

/**
 * Starting point of protoboard.
 * 
 */
public final class Main {
	public static void main(String[] args) {
//		Testing
//		
//		Controller controller = new Controller();
//		LeapMotionListener listener = new LeapMotionListener();
//		controller.addListener(listener);
//		
//		Input input = new Input(listener);
//		input.run();
//		
//		while (true);
		
		PApplet.main(new String[] { "--present", Blackboard.class.getName() });
	}
}