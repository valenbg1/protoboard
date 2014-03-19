package protoboard;

import java.awt.AWTException;
import java.awt.EventQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.UIManager;

import processing.core.PApplet;
import protoboard.blackboard.Blackboard;
import protoboard.input.Input;
import protoboard.leapmotion.LeapMotionListener;
import protoboard.swing.PrincipalIface;

import com.leapmotion.leap.Controller;

/**
 * Starting point of protoboard.
 * 
 */
public final class Main {
	public static final Controller lm_controller = new Controller();
	public static final LeapMotionListener lm_listener = new LeapMotionListener();
	
	private static AtomicBoolean running_input_mode = new AtomicBoolean(false);
	private static Input input_mode;
	
	private static AtomicBoolean running_blackboard_mode = new AtomicBoolean(false);
	
	public static void main(String[] args) {
		lm_controller.addListener(lm_listener);
		
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) { }
		
		// Testing
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				new PrincipalIface().setVisible(true);
			}
		});
	}
	
	public static void runBlackboardMode() {
		if (running_blackboard_mode.compareAndSet(false, true))
			PApplet.main(Blackboard.class.getName());
	}
	
	public static void stoppedBlackboardMode() {
		running_blackboard_mode.compareAndSet(true, false);
	}
	
	public static void runInputMode() {
		if (running_input_mode.compareAndSet(false, true)) {
			try {
				input_mode = new Input(lm_listener);
				
				new Thread(new Runnable() {
					@Override
					public void run() {
						input_mode.run();
					}
				}).start();
			} catch (AWTException e) {
				running_input_mode.compareAndSet(true, false);
				Constants.printExceptionToErr("", e);
			}
		}
	}
	
	public static boolean runningBlackboardMode() {
		return running_blackboard_mode.get();
	}
	
	public static boolean runningInputMode() {
		return running_input_mode.get();
	}
	
	public static void stopInputMode() {
		if (running_input_mode.compareAndSet(true, false)) {
			if (input_mode != null)
				input_mode.stop();
		}
	}
}