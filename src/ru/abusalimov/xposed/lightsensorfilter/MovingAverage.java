package ru.abusalimov.xposed.lightsensorfilter;

/**
 * Exponential Moving Average filter.
 * @author Eldar Abusalimov
 */
public class MovingAverage {

	private final double mAlphaTimescale;

	private double mSum;
	private long mLastTimestamp;

	public MovingAverage(long halfLifeTime) {
		// not a real "half": in exp we use a power of 'e', not 2.
		this(1.0 / halfLifeTime);
	}

	public MovingAverage(double alphaTimescale) {
		mAlphaTimescale = alphaTimescale;
	}

	public void addValue(double value, long timestamp) {
		long elapsed = timestamp - mLastTimestamp;

		if (elapsed > 0) {
			double alpha = -Math.expm1(-mAlphaTimescale * elapsed);
			mSum += alpha * (value - mSum);
		}

		mLastTimestamp = timestamp;
	}

	public double getAverage() {
		return mSum;
	}

}
