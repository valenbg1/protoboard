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
	private AtomicInteger wait_swipe_frames;
	private AtomicInteger wait_between_changing_circle_id;

	private Queue<LeapMotionObserver> observers;
	
	private AtomicInteger current_circle_id;
	private AtomicInteger current_circle_turns;

	public LeapMotionListener(Blackboard board) {
		this.wait_frames = new AtomicInteger(0);
		this.wait_swipe_frames = new AtomicInteger(0);
		this.observers = new ConcurrentLinkedQueue<LeapMotionObserver>();
		this.current_circle_id = new AtomicInteger(-1);
		this.current_circle_turns = new AtomicInteger(0);
		this.wait_between_changing_circle_id = new AtomicInteger(0);
	}

	private void callObservers(Gestures gesture) {
		for (LeapMotionObserver observer : observers) {
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
		if (wait_swipe_frames.get() > 0)
			wait_swipe_frames.decrementAndGet();
		
		if (wait_between_changing_circle_id.get() > 0)
			wait_between_changing_circle_id.decrementAndGet();
		
		if (wait_frames.get() > 0) {
			wait_frames.decrementAndGet();
			return;
		}
		
		Frame frame = controller.frame();
		GestureList gestures = frame.gestures();
		boolean detected_gesture = false;
		
		for (int i = 0; i < gestures.count(); ++i) {
			Gesture gesture = gestures.get(i);

			switch (gesture.type()) {
				case TYPE_CIRCLE:
					CircleGesture circle = new CircleGesture(gesture);
					
					if (current_circle_id.get() == -1) {
						current_circle_id.set(circle.id());
						current_circle_turns.set(Float.floatToIntBits(circle.progress() - LeapMotionListenerC.circle_resolution));
					}
					
					if (current_circle_id.get() == circle.id()) {
						if ((circle.progress() - Float.intBitsToFloat(current_circle_turns.get())) > LeapMotionListenerC.circle_resolution) {
							boolean clockwise; //  Calculate clock direction using the angle between circle normal and pointable
							
							current_circle_turns
								.set(Float.floatToIntBits((Float
										.intBitsToFloat(current_circle_turns.get()) + LeapMotionListenerC.circle_resolution)));
							
							// Clockwise if angle is less than 90 degrees
							if (circle.pointable().direction().angleTo(circle.normal()) <= Math.PI / 4)
								clockwise = true;
							else
								clockwise = false;
							
							if (clockwise)
								callObservers(Gestures.RIGHT_CIRCLE);
							else
								callObservers(Gestures.LEFT_CIRCLE);
								
							System.out.println(LeapMotionListenerC.onCircle + " id: " + circle.id() + ", "
									+ circle.state() + ", progress: " + circle.progress()
									+ ", clockwise: " + clockwise);
						}
						
						if (circle.state() == State.STATE_STOP) {
							current_circle_id.set(-1);
							detected_gesture = true;
						}
					}
					
					break;
					
				case TYPE_SWIPE:
					if (wait_swipe_frames.get() > 0)
						break;
					
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
							// Left
							} else {
								callObservers(Gestures.LEFT_SWIPE);
								
								System.out.println(LeapMotionListenerC.onLeftSwipe +  " id: " + swipe.id()
										+ ", " + swipe.state() + ", position: "
										+ swipe.position() + ", direction: "
										+ swipe.direction() + ", speed: "
										+ swipe.speed());
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
							// Down
							} else {
								callObservers(Gestures.DOWN_SWIPE);
								
								System.out.println(LeapMotionListenerC.onDownSwipe +  " id: " + swipe.id()
										+ ", " + swipe.state() + ", position: "
										+ swipe.position() + ", direction: "
										+ swipe.direction() + ", speed: "
										+ swipe.speed());
							}
						}
						
						// Avoid detecting new swipe gestures for LeapMotionListenerC.wait_between_swipe_gestures s
						wait_swipe_frames.set((int) Math.ceil(frame.currentFramesPerSecond()*LeapMotionListenerC.wait_between_swipe_gestures));
						detected_gesture = true;
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
		
		if (wait_between_changing_circle_id.get() == 0) {
			current_circle_id.set(-1);
			
			wait_between_changing_circle_id.set((int) Math.ceil(frame
					.currentFramesPerSecond()
					* LeapMotionListenerC.wait_between_changing_circle_id));
		}
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