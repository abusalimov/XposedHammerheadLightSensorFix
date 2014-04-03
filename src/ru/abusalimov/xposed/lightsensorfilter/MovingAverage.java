package ru.abusalimov.xposed.lightsensorfilter;

/**
 * Simple Moving Average filter.
 * @author Eldar Abusalimov
 */
public class MovingAverage {
	private float mSum;
	private float[] mValues;
	private int mIndex;
	
	public MovingAverage(int windowSize) {
		if (windowSize <= 0) {
			throw new IllegalArgumentException("windowSize must be positive");
		}
		mValues = new float[windowSize];
	}
	
	public MovingAverage(int windowSize, float initial) {
		this(windowSize);
		
		for (int i = 0; i < windowSize; i++) {
			handleNewValue(initial);
		}
	}
	
	public void handleNewValue(float newValue) {
		int i = mIndex;

		float oldValue = mValues[i];
		mSum += newValue - oldValue;
		mValues[i] = newValue;
		
		mIndex = (i + 1) % mValues.length;
	}

	public float getAverage() {
		return mSum / mValues.length;
	}
	
	public int getWindowSize() {
		return mValues.length;
	}
	
}
