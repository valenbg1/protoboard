package protoboard.input;

import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import protoboard.Constants.InputC;
import protoboard.leapmotion.LeapMotionListener;
import protoboard.leapmotion.LeapMotionObserver;

public class Input implements LeapMotionObserver, Runnable {
	private Robot robot;
	
	private LeapMotionListener lmlistener;
	
	private boolean running;
	
	public Input(LeapMotionListener lmlistener) {
		try {
			this.robot = new Robot();
		} catch (Exception e) {
			System.err.println(InputC.err_no_robot);
			return;
		}
		
		this.lmlistener = lmlistener;
		this.lmlistener.register(this);
		
		this.running = false;
	}

	@Override
	public void onDownSwipe() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onKeyTap() {
		onScreenTap();
	}

	@Override
	public void onLeftCircle() {
		onLeftSwipe();
	}

	@Override
	public void onLeftSwipe() {
		robot.keyPress(KeyEvent.VK_LEFT);
		robot.keyRelease(KeyEvent.VK_LEFT);
	}

	@Override
	public void onRighCircle() {
		onRighSwipe();
	}

	@Override
	public void onRighSwipe() {
		robot.keyPress(KeyEvent.VK_RIGHT);
		robot.keyRelease(KeyEvent.VK_RIGHT);
	}

	@Override
	public void onScreenTap() {
		robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
	}

	@Override
	public void onUpSwipe() {
		// TODO Auto-generated method stub
		
	}
	
	public synchronized void stop() {
		if (running)
			lmlistener.unregister(this);
	}

	@Override
	public synchronized void run() {
		if (!running)
			lmlistener.register(this);
	}
}