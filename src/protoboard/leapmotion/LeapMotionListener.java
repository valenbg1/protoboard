package protoboard.leapmotion;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicInteger;

import protoboard.AtomicFloat;
import protoboard.Constants.LeapMotionListenerC;

import com.leapmotion.leap.CircleGesture;
import com.leapmotion.leap.Controller;
import com.leapmotion.leap.Frame;
import com.leapmotion.leap.Gesture;
import com.leapmotion.leap.Gesture.State;
import com.leapmotion.leap.GestureList;
import com.leapmotion.leap.KeyTapGesture;
import com.leapmotion.leap.Listener;
import com.leapmotion.leap.ScreenTapGesture;
import com.leapmotion.leap.SwipeGesture;

/**
 * Processes the info received from the Leap Motion controller.
 * 
 */
public class LeapMotionListener extends Listener {
	public enum Gestures {
		DOWN_SWIPE, KEY_TAP, LEFT_CIRCLE, LEFT_SWIPE,
		RIGHT_CIRCLE, RIGHT_SWIPE, SCREEN_TAP, UP_SWIPE
	}

	private int wait_frames;
	private int wait_swipe_frames;
	private int wait_between_changing_circle_id;

	private Set<LeapMotionObserver> observers;
	
	private AtomicInteger current_circle_id;
	private AtomicFloat current_circle_turns;

	public LeapMotionListener() {
		this.wait_frames = 0;
		this.wait_swipe_frames = 0;
		this.wait_between_changing_circle_id = 0;
		this.observers = new CopyOnWriteArraySet<LeapMotionObserver>();
		this.current_circle_id = new AtomicInteger(-1);
		this.current_circle_turns = new AtomicFloat(0);
	}

	private void callObservers(Gestures gesture) {
		for (LeapMotionObserver observer : observers) {
			switch (gesture) {
				case DOWN_SWIPE:
					observer.onDownSwipe();
					break;
					
				case KEY_TAP:
					observer.onKeyTap();
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
					observer.onRightSwipe();
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

	private boolean circleGesture(CircleGesture circle) {
		boolean clockwise;  //  Calculate clock direction using the angle between circle normal and pointable
		
		// Clockwise if angle is less than 90 degrees
		if (clockwise = (circle.pointable().direction().angleTo(circle.normal()) <= Math.PI / 4))
			callObservers(Gestures.RIGHT_CIRCLE);
		else
			callObservers(Gestures.LEFT_CIRCLE);
		
		if (clockwise)
			callObservers(Gestures.RIGHT_CIRCLE);
		else
			callObservers(Gestures.LEFT_CIRCLE);
			
		System.out.println(LeapMotionListenerC.onCircle + " id: " + circle.id() + ", "
				+ circle.state() + ", progress: " + circle.progress()
				+ ", clockwise: " + clockwise);
		
		return true;
	}
	
	private boolean keyTapGesture(KeyTapGesture keyTap) {
		callObservers(Gestures.KEY_TAP);
		
		System.out.println(LeapMotionListenerC.onKeyTap + " id: "
				+ keyTap.id() + ", " + keyTap.state()
				+ ", position: " + keyTap.position()
				+ ", direction: " + keyTap.direction());
		
		return true;
	}

	@Override
	public void onConnect(Controller controller) {
		System.out.println(LeapMotionListenerC.onConnect);
		
		if (controller.config().setFloat("Gesture.Swipe.MinLength", LeapMotionListenerC.swipe_minlength))
			controller.config().save();
		
		controller.enableGesture(Gesture.Type.TYPE_SWIPE);
		controller.enableGesture(Gesture.Type.TYPE_CIRCLE);
		controller.enableGesture(Gesture.Type.TYPE_SCREEN_TAP);
		controller.enableGesture(Gesture.Type.TYPE_KEY_TAP);
		
		controller.setPolicyFlags(Controller.PolicyFlag.POLICY_BACKGROUND_FRAMES);
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
		// "Weak" thread protection
		int wait_swipe_f = wait_swipe_frames,
				wait_between_changing_ci = wait_between_changing_circle_id,
				wait_f = wait_frames;
		
		// Waiting times
		if (wait_swipe_f > 0) {
			--wait_swipe_f;
			wait_swipe_frames = wait_swipe_f;
		}
		
		if (wait_between_changing_ci > 0) {
			--wait_between_changing_ci;
			wait_between_changing_circle_id = wait_between_changing_ci;
		}
		
		if (wait_f > 0) {
			--wait_f;
			wait_frames = wait_f;
			return;
		}
		
		Frame frame = controller.frame();
		GestureList gestures = frame.gestures();
		boolean detected_gesture = false;
		
		for (Gesture gesture : gestures) {
			switch (gesture.type()) {
				case TYPE_CIRCLE:
					CircleGesture circle = new CircleGesture(gesture);
					
					if (current_circle_id.get() == -1) {
						current_circle_id.set(circle.id());
						current_circle_turns.set(circle.progress() - LeapMotionListenerC.circle_resolution);
					}
					
					float current_ct = current_circle_turns.get();
					
					if (current_circle_id.get() == circle.id()) {
						if ((circle.progress() - current_ct) > LeapMotionListenerC.circle_resolution) {
							current_circle_turns.set(current_ct + LeapMotionListenerC.circle_resolution);
							circleGesture(circle);
						}
						
						if (circle.state() == State.STATE_STOP) {
							current_circle_id.set(-1);
							detected_gesture = true;
						}
					}
					
					break;
					
				case TYPE_SWIPE:
					if (wait_swipe_f > 0)
						break;
					
					if ((gesture.state() == State.STATE_STOP) && swipeGesture(new SwipeGesture(gesture))) {
						// Avoid detecting new swipe gestures for
						// LeapMotionListenerC.wait_between_swipe_gestures s
						wait_swipe_f = (int) Math.ceil(frame
								.currentFramesPerSecond()
								* LeapMotionListenerC.wait_between_swipe_gestures);
						wait_swipe_frames = wait_swipe_f;
						detected_gesture = true;
					}
		
					break;
					
				case TYPE_SCREEN_TAP:
					screenTapGesture(new ScreenTapGesture(gesture));
                    detected_gesture = true;
                    break;
                    
				case TYPE_KEY_TAP:
					keyTapGesture(new KeyTapGesture(gesture));
                    detected_gesture = true;
                    break;
                    
				default:
			}
		}
		
		// Avoid detecting new gestures for LeapMotionListenerC.wait_between_gestures s
		if (detected_gesture)
			wait_frames = (int) Math.ceil(frame.currentFramesPerSecond()*LeapMotionListenerC.wait_between_gestures);
		
		if (wait_between_changing_ci <= 0) {
			current_circle_id.set(-1);
			
			wait_between_changing_circle_id = (int) Math.ceil(frame
					.currentFramesPerSecond()
					* LeapMotionListenerC.wait_between_changing_circle_id);
		}
	}

	@Override
	public void onInit(Controller controller) {
		System.out.println(LeapMotionListenerC.onInit);
	}

	/**
	 * This implementation calls itself each method of LeapMotionObserver into the
	 * onFrame thread handler, so every method for the LeapMotionObserver that the observer
	 * implements must do a quick processing, or start a new thread that will do the job.
	 * 
	 * @param observer
	 */
	public void register(LeapMotionObserver observer) {
		observers.add(observer);
	}

	private boolean screenTapGesture(ScreenTapGesture screenTap) {
		callObservers(Gestures.SCREEN_TAP);
		
		System.out.println(LeapMotionListenerC.onScreenTap + " id: "
				+ screenTap.id() + ", " + screenTap.state()
				+ ", position: " + screenTap.position()
				+ ", direction: " + screenTap.direction());
		
		return true;
	}

	private boolean swipeGesture(SwipeGesture swipe) {
		float length = swipe.position().magnitude();
		
		// Horizontal
		if (Math.abs(swipe.direction().getX()) > Math.abs(swipe.direction().getY())) {
			// Right
			if (swipe.direction().getX() > 0) {
				if ((length <= LeapMotionListenerC.rswipe_limits[0])
						|| (length >= LeapMotionListenerC.rswipe_limits[1]))
					return false;
				
				callObservers(Gestures.RIGHT_SWIPE);
				
				System.out.println(LeapMotionListenerC.onRightSwipe
						+ " id: " + swipe.id() + ", "
						+ swipe.state() + ", position: "
						+ swipe.position() + ", direction: "
						+ swipe.direction() + ", speed: "
						+ swipe.speed() + ", length: " + length);
				
			// Left
			} else {
				if ((length <= LeapMotionListenerC.lswipe_limits[0])
						|| (length >= LeapMotionListenerC.lswipe_limits[1]))
					return false;
				
				callObservers(Gestures.LEFT_SWIPE);
				
				System.out.println(LeapMotionListenerC.onLeftSwipe
						+ " id: " + swipe.id() + ", "
						+ swipe.state() + ", position: "
						+ swipe.position() + ", direction: "
						+ swipe.direction() + ", speed: "
						+ swipe.speed() + ", length: " + length);
			}
		// Vertical
		} else {
			// Up
			if (swipe.direction().getY() > 0) {
				if ((length <= LeapMotionListenerC.uswipe_limits[0])
						|| (length >= LeapMotionListenerC.uswipe_limits[1]))
					return false;
				
				callObservers(Gestures.UP_SWIPE);
				
				System.out.println(LeapMotionListenerC.onUpSwipe
						+ " id: " + swipe.id() + ", "
						+ swipe.state() + ", position: "
						+ swipe.position() + ", direction: "
						+ swipe.direction() + ", speed: "
						+ swipe.speed() + ", length: " + length);
			// Down
			} else {
				if ((length <= LeapMotionListenerC.dswipe_limits[0])
						|| (length >= LeapMotionListenerC.dswipe_limits[1]))
					return false;
				
				callObservers(Gestures.DOWN_SWIPE);
				
				System.out.println(LeapMotionListenerC.onDownSwipe
						+ " id: " + swipe.id() + ", "
						+ swipe.state() + ", position: "
						+ swipe.position() + ", direction: "
						+ swipe.direction() + ", speed: "
						+ swipe.speed() + ", length: " + length);
			}
		}
		
		return true;
	}

	public void unregister(LeapMotionObserver observer) {
		observers.remove(observer);
	}
}