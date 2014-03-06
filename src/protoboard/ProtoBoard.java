package protoboard;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.concurrent.atomic.AtomicInteger;

import processing.core.PApplet;

import com.leapmotion.leap.Controller;

public class ProtoBoard extends PApplet {
	private static final long serialVersionUID = -2341687072941440919L;

	public static void main(String[] args) {
		PApplet.main(new String[] { "--present", ProtoBoard.class.getName() });
	}

	private Controller controller;
	private LeapMotionContrListener listener;

	private AtomicInteger[] drawColor = new AtomicInteger[3];

	public synchronized void changeDrawColorBack() {
		drawColor[0].set((drawColor[0].get() - 15) % 256);
		drawColor[1].set((drawColor[1].get() - 25) % 256);
		drawColor[2].set((drawColor[2].get() - 35) % 256);
	}

	public synchronized void changeDrawColorForth() {
		drawColor[0].set((drawColor[0].get() + 15) % 256);
		drawColor[1].set((drawColor[1].get() + 25) % 256);
		drawColor[2].set((drawColor[2].get() + 35) % 256);
	}
	
	public void clearBlackboard() {
		background(0);
	}

	@Override
	public void draw() {
		stroke(255);
		strokeWeight(3);
		fill(drawColor[0].get(), drawColor[1].get(), drawColor[2].get());
		rect(10, displayHeight - 60, 50, 50, 10);

		if (mousePressed) {
			stroke(drawColor[0].get(), drawColor[1].get(), drawColor[2].get());
			strokeWeight(10);
			line(mouseX, mouseY, pmouseX, pmouseY);
		}
	}

	@Override
	public void exit() {
		controller.removeListener(listener);
		super.exit();
	}

	@Override
	public void setup() {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

		size(screenSize.width, screenSize.height);
		background(0);

		controller = new Controller();
		listener = new LeapMotionContrListener(this);
		controller.addListener(listener);
		
		for (int i = 0; i < drawColor.length; ++i)
			drawColor[i] = new AtomicInteger(126);
	}
}