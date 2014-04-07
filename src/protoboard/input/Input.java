package protoboard.input;

import java.awt.AWTException;
import java.awt.Robot;
import java.util.concurrent.atomic.AtomicBoolean;

import protoboard.Constants.InputC;
import protoboard.leapmotion.LeapMotionListener;
import protoboard.leapmotion.LeapMotionObserver;

/**
 * Implements the input mode of the application.
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
		simKey(InputC.onDownSwipe);
	}

	@Override
	public void onKeyTap() {
		onScreenTap();
	}

	@Override
	public void onLeftCircle() {
		onRightSwipe();
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
		simKey(InputC.onRightSwipe);
	}

	@Override
	public void onScreenTap() {
		simMouse(InputC.onScreenTap);
	}

	@Override
	public void onTranslation(float x, float y) {
		if (Math.abs(y) > InputC.translation_threshold)
			robot.mouseWheel(y < 0 ? -InputC.wheel_notches : InputC.wheel_notches);
	}

	@Override
	public void onUpSwipe() {
		simKey(InputC.onUpSwipe);
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

	public void stop() {
		if (running.compareAndSet(true, false))
			lmlistener.unregister(this);
	}
}