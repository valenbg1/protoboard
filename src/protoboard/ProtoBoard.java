package protoboard;

import java.awt.Dimension;
import java.awt.Toolkit;

import processing.core.PApplet;

import com.leapmotion.leap.Controller;

public class ProtoBoard extends PApplet {
	private static final long serialVersionUID = -2341687072941440919L;

	public static void main(String[] args) {
		PApplet.main(new String[] { "--present", ProtoBoard.class.getName() });
	}

	private Controller controller;
	private LeapMotionContrListener listener;
	
	public int drawColor_R = 126;
	public int drawColor_G = 126;
	public int drawColor_B = 126;
	
	public void changeDrawColorForth() {
		drawColor_R = (drawColor_R + 15) % 256;
		drawColor_G = (drawColor_G + 25) % 256;
		drawColor_B = (drawColor_B + 35) % 256;
	}
	
	public void changeDrawColorBack() {
		drawColor_R = (drawColor_R - 15) % 256;
		drawColor_G = (drawColor_G - 25) % 256;
		drawColor_B = (drawColor_B - 35) % 256;
	}

	@Override
	public void draw() {
		stroke(255);
		strokeWeight(3);
		fill(drawColor_R, drawColor_G, drawColor_B);
		rect(10, 10, 50, 50, 10);
		
		if (mousePressed) {
			stroke(drawColor_R, drawColor_G, drawColor_B);
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

		new Thread(new Runnable() {
			@Override
			public void run() {
				controller.addListener(listener);
			}
		}).run();
	}
}