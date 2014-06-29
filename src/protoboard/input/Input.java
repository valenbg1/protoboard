package protoboard.input;

import java.awt.AWTException;
import java.awt.Robot;
import java.util.concurrent.atomic.AtomicBoolean;

import protoboard.Constants.InputC;
import protoboard.Constants.LeapMotionListenerC;
import protoboard.leapmotion.LeapMotionListener;
import protoboard.leapmotion.LeapMotionObserver;

/**
 * Implements the input mode of the application
 *
 */
public class Input implements LeapMotionObserver, Runnable {
	private Robot robot;
	
	private LeapMotionListener lmlistener;
	
	private AtomicBoolean running;
	
	private final int delay;
	
	public Input(LeapMotionListener lmlistener) throws AWTException {
		try {
			this.robot = new Robot();
		} catch (Exception e) {
			throw new AWTException(InputC.err_no_robot + "\n" + e.getMessage());
		}
		
		this.lmlistener = lmlistener;
		
		this.running = new AtomicBoolean(false);
		
		this.delay = InputC.robot_delay;
	}

	@Override
	public void onDownSwipe() {
		// NOTHING
	}

	@Override
	public void onKeyTap() {
//		onScreenTap();
	}

	@Override
	public void onLeftCircle() {
		simKey(InputC.onRightSwipe);
	}
	
	@Override
	public void onLeftSwipe() {
		simKey(InputC.onLeftSwipe);
	}
	
	@Override
	public void onRighCircle() {
		onLeftSwipe();
	}

	@Override
	public void onRightSwipe() {
//		simKey(InputC.onRightSwipe);
	}

	@Override
	public void onScreenTap() {
//		simMouse(InputC.onScreenTap);
	}

	@Override
	public void onTranslation(float d_x, float d_y) {
//		float t_threshold = LeapMotionListenerC.translation_threshold, w_factor = InputC.wheel_notches_factor,
//				d_y_abs = Math.abs(d_y);
//		
//		if (d_y_abs > t_threshold) {
//			int wheel_f = (int) Math.floor(d_y_abs/w_factor);
//			
//			robot.mouseWheel(d_y < 0 ? -wheel_f : wheel_f);
//		}
	}

	@Override
	public void onUpSwipe() {
//		simKey(InputC.onUpSwipe);
	}

	/**
	 * Run the input mode. Registers the Input object with the LeapMotionListener.
	 * 
	 */
	@Override
	public void run() {
		if (running.compareAndSet(false, true))
			lmlistener.register(this);
	}
	
	private void simKey(final int key) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				robot.keyPress(key);
				robot.delay(delay);
				robot.keyRelease(key);
			}
		}).start();
	}

	private void simMouse(final int button) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				robot.mousePress(button);
				robot.delay(delay);
				robot.mouseRelease(button);
			}
		}).start();
	}

	/**
	 * Stops this input mode. Unregisters the Input object with the LeapMotionListener.
	 * 
	 */
	public void stop() {
		if (running.compareAndSet(true, false))
			lmlistener.unregister(this);
	}
}