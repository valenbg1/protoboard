package protoboard;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Simple atomic float operations using an AtomicInteger as backbone
 * 
 */
public class AtomicFloat {
	private AtomicInteger int_aux;

	public AtomicFloat() {
		this.int_aux = new AtomicInteger();
	}

	public AtomicFloat(float value) {
		this.int_aux = new AtomicInteger(Float.floatToIntBits(value));
	}

	public float get() {
		return Float.intBitsToFloat(int_aux.get());
	}

	public void set(float value) {
		int_aux.set(Float.floatToIntBits(value));
	}
}