package ru.abusalimov.xposed.lightsensorfilter;

import android.util.Log;

/**
 * Filters inadequately high and low lux values using Moving Average filter.
 * @author Eldar Abusalimov
 */
public class LightSensorFilter {

	public static final int DEFAULT_SMOOTH_WINDOW = 7;
	public static final float DEFAULT_SMOOTH_FIXUP = 1.3f;

	protected static final float HIGH_BOGUS_LUX = 30000.0f;
	protected static final float LOW_BOGUS_LUX = 0.0f;

	private final MovingAverage mMovingAverage;
	private int mSeenBogus;

	private final int mSmoothWindow;
	private final float mSmoothFixup;

	public LightSensorFilter() {
		this(DEFAULT_SMOOTH_WINDOW, DEFAULT_SMOOTH_FIXUP);
	}

	/**
	 * @param smoothWindow window size for smoothing bogus values
	 * @param smoothFixup coefficient to use to adjust smoothed values
	 */
	public LightSensorFilter(int smoothWindow, float smoothFixup) {
		mSmoothWindow = smoothWindow;
		mSmoothFixup = smoothFixup;
		mMovingAverage = new MovingAverage(smoothWindow);
	}

	public float fixupLuxValue(float lux) {
		if (Float.compare(lux, HIGH_BOGUS_LUX) == 0) {
			// it goes crazy and shows 30k lux
			lux = mMovingAverage.getAverage() * mSmoothFixup;
			Log.v("LightSensorFilter", "fixup bogus high: " + lux + "lux");

			// sometimes it drops from 30k to zero occasionally,
			// be ready for fluctuations in the nearest future
			mSeenBogus = mSmoothWindow;
		} else if (mSeenBogus != 0) {

			if (Float.compare(lux, LOW_BOGUS_LUX) == 0) {
				// reports zero within few ticks after 30k: LIAR!11
				lux = mMovingAverage.getAverage() / mSmoothFixup;
				Log.v("LightSensorFilter", "fixup bogus low:  " + lux + "lux");

			} else {
				--mSeenBogus;
			}
		}

		// TODO Does it need to be fed back to MA unconditionally?
		mMovingAverage.handleNewValue(lux);
		return lux;
	}

	public int getSmoothWindow() {
		return mSmoothWindow;
	}

	public float getSmoothFixup() {
		return mSmoothFixup;
	}

}
