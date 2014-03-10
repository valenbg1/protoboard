package protoboard;

import processing.core.PApplet;
import protoboard.blackboard.Blackboard;

/**
 * Starting point of protoboard.
 * 
 */
public final class Main {
	public static void main(String[] args) {
		PApplet.main(new String[] { "--present", Blackboard.class.getName() });
	}
}