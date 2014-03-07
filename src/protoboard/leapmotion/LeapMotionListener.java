package protoboard.leapmotion;

import protoboard.Constants;
import protoboard.blackboard.Blackboard;

import com.leapmotion.leap.CircleGesture;
import com.leapmotion.leap.Controller;
import com.leapmotion.leap.Frame;
import com.leapmotion.leap.Gesture;
import com.leapmotion.leap.Gesture.State;
import com.leapmotion.leap.GestureList;
import com.leapmotion.leap.Listener;
import com.leapmotion.leap.SwipeGesture;

/**
 * Processes the info received from the Leap Motion controller
 * 
 */
public class LeapMotionListener extends Listener {
	private Blackboard board;

	public LeapMotionListener(Blackboard board) {
		this.board = board;
	}

	@Override
	public void onConnect(Controller controller) {
		System.out.println(Constants.LeapMotion.onConnect);
		
		if (controller.config().setFloat("Gesture.Swipe.MinLength", Constants.LeapMotion.swipe_minlength))
			controller.config().save();
		
		controller.enableGesture(Gesture.Type.TYPE_SWIPE);
		controller.enableGesture(Gesture.Type.TYPE_CIRCLE);
//		controller.enableGesture(Gesture.Type.TYPE_SCREEN_TAP);
//		controller.enableGesture(Gesture.Type.TYPE_KEY_TAP);
	}

	@Override
	public void onDisconnect(Controller controller) {
		// Note: not dispatched when running in a debugger.
		System.out.println(Constants.LeapMotion.onDisconnect);
	}

	@Override
	public void onExit(Controller controller) {
		System.out.println(Constants.LeapMotion.onExit);
	}

	@Override
	public void onFrame(Controller controller) {
		// Get the most recent frame and report some basic information
		Frame frame = controller.frame();
//		System.out.println("Frame id: " + frame.id() + ", timestamp: "
//				+ frame.timestamp() + ", hands: " + frame.hands().count()
//				+ ", fingers: " + frame.fingers().count() + ", tools: "
//				+ frame.tools().count() + ", gestures "
//				+ frame.gestures().count());
//
//		if (!frame.hands().isEmpty()) {
//			// Get the first hand
//			Hand hand = frame.hands().get(0);
//
//			// Check if the hand has any fingers
//			FingerList fingers = hand.fingers();
//			if (!fingers.isEmpty()) {
//				// Calculate the hand's average finger tip position
//				Vector avgPos = Vector.zero();
//				for (Finger finger : fingers) {
//					avgPos = avgPos.plus(finger.tipPosition());
//				}
//				avgPos = avgPos.divide(fingers.count());
//				System.out.println("Hand has " + fingers.count()
//						+ " fingers, average finger tip position: " + avgPos);
//			}
//
//			// Get the hand's sphere radius and palm position
//			System.out.println("Hand sphere radius: " + hand.sphereRadius()
//					+ " mm, palm position: " + hand.palmPosition());
//
//			// Get the hand's normal vector and direction
//			Vector normal = hand.palmNormal();
//			Vector direction = hand.direction();
//
//			// Calculate the hand's pitch, roll, and yaw angles
//			System.out.println("Hand pitch: "
//					+ Math.toDegrees(direction.pitch()) + " degrees, "
//					+ "roll: " + Math.toDegrees(normal.roll()) + " degrees, "
//					+ "yaw: " + Math.toDegrees(direction.yaw()) + " degrees");
//		}

		GestureList gestures = frame.gestures();
		for (int i = 0; i < gestures.count(); i++) {
			Gesture gesture = gestures.get(i);

			switch (gesture.type()) {
			case TYPE_CIRCLE:
				CircleGesture circle = new CircleGesture(gesture);

				// Calculate clock direction using the angle between circle
				// normal and pointable
				boolean clockwise;
				if (circle.pointable().direction().angleTo(circle.normal()) <= Math.PI / 4) {
					// Clockwise if angle is less than 90 degrees
					clockwise = true;
				} else {
					clockwise = false;
				}
//
//				// Calculate angle swept since last frame
//				double sweptAngle = 0;
//				if (circle.state() != State.STATE_START) {
//					CircleGesture previousUpdate = new CircleGesture(controller
//							.frame(1).gesture(circle.id()));
//					sweptAngle = (circle.progress() - previousUpdate.progress())
//							* 2 * Math.PI;
//				}
//
//				System.out.println("Circle id: " + circle.id() + ", "
//						+ circle.state() + ", progress: " + circle.progress()
//						+ ", radius: " + circle.radius() + ", angle: "
//						+ Math.toDegrees(sweptAngle) + ", " + clockwiseness);
				
				if (circle.state() == State.STATE_STOP) {
					if (clockwise)
						board.changeDrawColorForth();
					else
						board.changeDrawColorBack();
						
					System.out.println(Constants.LeapMotion.onCircle + " id: " + circle.id() + ", "
							+ circle.state() + ", progress: " + circle.progress()
							+ ", clockwise: " + clockwise);
					
					return;
				}
				
				break;
			case TYPE_SWIPE:
				SwipeGesture swipe = new SwipeGesture(gesture);
				
				if (swipe.state() == State.STATE_STOP) {
					float roll = swipe.direction().roll();
					float pitch = swipe.direction().pitch();

					if ((roll > 0)
							&& (Math.abs(((Math.PI / 2.0f) - roll)) < Constants.LeapMotion.max_ang_swipe)) {
						board.changeScreenForth();

						System.out.println(Constants.LeapMotion.onRightSwipe +  " id: " + swipe.id()
								+ ", " + swipe.state() + ", position: "
								+ swipe.position() + ", direction: "
								+ swipe.direction() + ", speed: "
								+ swipe.speed());
						
						return;
					} else if ((roll < 0)
							&& (Math.abs(((Math.PI / 2.0f) + roll)) < Constants.LeapMotion.max_ang_swipe)) {
						board.changeScreenBack();

						System.out.println(Constants.LeapMotion.onLeftSwipe +  " id: " + swipe.id()
								+ ", " + swipe.state() + ", position: "
								+ swipe.position() + ", direction: "
								+ swipe.direction() + ", speed: "
								+ swipe.speed());
						
						return;
					} else if ((pitch < 0)
							&& (Math.abs(((Math.PI / 2.0f) + pitch)) < Constants.LeapMotion.max_ang_swipe)) {
						board.saveCurrentScreen();

						System.out.println(Constants.LeapMotion.onDownSwipe +  " id: " + swipe.id()
								+ ", " + swipe.state() + ", position: "
								+ swipe.position() + ", direction: "
								+ swipe.direction() + ", speed: "
								+ swipe.speed());
						
						return;
					}
				}
				
				break;
//			case TYPE_SCREEN_TAP:
//				ScreenTapGesture screenTap = new ScreenTapGesture(gesture);
//				System.out.println("Screen Tap id: " + screenTap.id() + ", "
//						+ screenTap.state() + ", position: "
//						+ screenTap.position() + ", direction: "
//						+ screenTap.direction());
//				break;
//			case TYPE_KEY_TAP:
//				KeyTapGesture keyTap = new KeyTapGesture(gesture);
//				System.out.println("Key Tap id: " + keyTap.id() + ", "
//						+ keyTap.state() + ", position: " + keyTap.position()
//						+ ", direction: " + keyTap.direction());
//				break;
			default:
//				System.out.println("Unknown gesture type.");
				break;
			}
		}
//
//		if (!frame.hands().isEmpty() || !gestures.isEmpty()) {
//			System.out.println();
//		}
	}

	@Override
	public void onInit(Controller controller) {
		System.out.println(Constants.LeapMotion.onInit);
	}
}