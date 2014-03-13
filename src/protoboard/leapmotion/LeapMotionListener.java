package protoboard.leapmotion;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

import protoboard.Constants.LeapMotionListenerC;
import protoboard.blackboard.Blackboard;

import com.leapmotion.leap.CircleGesture;
import com.leapmotion.leap.Controller;
import com.leapmotion.leap.Frame;
import com.leapmotion.leap.Gesture;
import com.leapmotion.leap.Gesture.State;
import com.leapmotion.leap.GestureList;
import com.leapmotion.leap.Listener;
import com.leapmotion.leap.ScreenTapGesture;
import com.leapmotion.leap.SwipeGesture;

/**
 * Processes the info received from the Leap Motion controller.
 * 
 */
public class LeapMotionListener extends Listener {
	public enum Gestures {
		DOWN_SWIPE, LEFT_CIRCLE, LEFT_SWIPE, RIGHT_CIRCLE, RIGHT_SWIPE, SCREEN_TAP, UP_SWIPE
	}

	private AtomicInteger wait_frames;

	private Queue<LeapMotionObserver> observers;

	public LeapMotionListener(Blackboard board) {
		this.wait_frames = new AtomicInteger(0);
		this.observers = new ConcurrentLinkedQueue<LeapMotionObserver>();
	}

	private void callObservers(Gestures gesture) {
		for (final LeapMotionObserver observer : observers) {
			switch (gesture) {
				case DOWN_SWIPE:
					observer.onDownSwipe();
					break;
					
				case LEFT_CIRCLE:
					observer.onLeftCircle();
					break;
					
				case LEFT_SWIPE:
					observer.onLeftSwipe();
					break;
					
				case RIGHT_CIRCLE:
					observer.onRighCircle();
					break;
					
				case RIGHT_SWIPE:
					observer.onRighSwipe();
					break;
					
				case SCREEN_TAP:
					observer.onScreenTap();
					break;
					
				case UP_SWIPE:
					observer.onUpSwipe();
					break;
			}
		}
	}

	@Override
	public void onConnect(Controller controller) {
		System.out.println(LeapMotionListenerC.onConnect);
		
		if (controller.config().setFloat("Gesture.Swipe.MinLength", LeapMotionListenerC.swipe_minlength))
			controller.config().save();
		
		controller.enableGesture(Gesture.Type.TYPE_SWIPE);
		controller.enableGesture(Gesture.Type.TYPE_CIRCLE);
		controller.enableGesture(Gesture.Type.TYPE_SCREEN_TAP);
	}
	
	@Override
	public void onDisconnect(Controller controller) {
		System.out.println(LeapMotionListenerC.onDisconnect);
	}

	@Override
	public void onExit(Controller controller) {
		System.out.println(LeapMotionListenerC.onExit);
	}

	@Override
	public void onFrame(Controller controller) {
		if (wait_frames.get() > 0) {
			wait_frames.decrementAndGet();
			return;
		}
		
		Frame frame = controller.frame();
		GestureList gestures = frame.gestures();
		boolean detected_gesture = false;
		
		for (int i = 0; !detected_gesture && (i < gestures.count()); ++i) {
			Gesture gesture = gestures.get(i);

			switch (gesture.type()) {
				case TYPE_CIRCLE:
					CircleGesture circle = new CircleGesture(gesture);
					boolean clockwise; //  Calculate clock direction using the angle between circle normal and pointable
					
					// Clockwise if angle is less than 90 degrees
					if (circle.pointable().direction().angleTo(circle.normal()) <= Math.PI / 4)
						clockwise = true;
					else
						clockwise = false;
					
					if (circle.state() == State.STATE_STOP) {
						if (clockwise)
							callObservers(Gestures.RIGHT_CIRCLE);
						else
							callObservers(Gestures.LEFT_CIRCLE);
							
						System.out.println(LeapMotionListenerC.onCircle + " id: " + circle.id() + ", "
								+ circle.state() + ", progress: " + circle.progress()
								+ ", clockwise: " + clockwise);
						
						detected_gesture = true;
					}
					
					break;
					
				case TYPE_SWIPE:
					SwipeGesture swipe = new SwipeGesture(gesture);
					
					if (swipe.state() == State.STATE_STOP) {
						// Horizontal
						if (Math.abs(swipe.direction().getX()) > Math.abs(swipe.direction().getY())) {
							// Right
							if (swipe.direction().getX() > 0) {
								callObservers(Gestures.RIGHT_SWIPE);
								
								System.out.println(LeapMotionListenerC.onRightSwipe +  " id: " + swipe.id()
										+ ", " + swipe.state() + ", position: "
										+ swipe.position() + ", direction: "
										+ swipe.direction() + ", speed: "
										+ swipe.speed());
								
								detected_gesture = true;
							// Left
							} else {
								callObservers(Gestures.LEFT_SWIPE);
								
								System.out.println(LeapMotionListenerC.onLeftSwipe +  " id: " + swipe.id()
										+ ", " + swipe.state() + ", position: "
										+ swipe.position() + ", direction: "
										+ swipe.direction() + ", speed: "
										+ swipe.speed());
								
								detected_gesture = true;
							}
						// Vertical
						} else {
							// Up
							if (swipe.direction().getY() > 0) {
								callObservers(Gestures.UP_SWIPE);
								
								System.out.println(LeapMotionListenerC.onUpSwipe +  " id: " + swipe.id()
										+ ", " + swipe.state() + ", position: "
										+ swipe.position() + ", direction: "
										+ swipe.direction() + ", speed: "
										+ swipe.speed());
								
								detected_gesture = true;
							// Down
							} else {
								callObservers(Gestures.DOWN_SWIPE);
								
								System.out.println(LeapMotionListenerC.onDownSwipe +  " id: " + swipe.id()
										+ ", " + swipe.state() + ", position: "
										+ swipe.position() + ", direction: "
										+ swipe.direction() + ", speed: "
										+ swipe.speed());
								
								detected_gesture = true;
							}
						}
					}
					break;
					
				case TYPE_SCREEN_TAP:
					ScreenTapGesture screenTap = new ScreenTapGesture(gesture);
					
					callObservers(Gestures.SCREEN_TAP);
					
					System.out.println(LeapMotionListenerC.onScreenTap + " id: "
							+ screenTap.id() + ", " + screenTap.state()
							+ ", position: " + screenTap.position()
							+ ", direction: " + screenTap.direction());
                    
                    detected_gesture = true;
                    break;
					
				default:
			}
		}
		
		// Avoid detecting new gestures for LeapMotionListenerC.wait_between_gestures s
		if (detected_gesture)
			wait_frames.set((int) Math.ceil(frame.currentFramesPerSecond()*LeapMotionListenerC.wait_between_gestures));
	}

	@Override
	public void onInit(Controller controller) {
		System.out.println(LeapMotionListenerC.onInit);
	}

	public void register(LeapMotionObserver observer) {
		observers.add(observer);
	}

	public void unregister(LeapMotionObserver observer) {
		observers.remove(observer);
	}
}