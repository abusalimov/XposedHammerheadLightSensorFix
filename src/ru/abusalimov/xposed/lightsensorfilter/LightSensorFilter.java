package ru.abusalimov.xposed.lightsensorfilter;

import android.util.Log;

/**
 * Filters inadequately high and low lux values using Moving Average filter.
 * @author Eldar Abusalimov
 */
public class LightSensorFilter {

	public static final long DEFAULT_SMOOTH_WINDOW = 3000l * 1000 * 1000;
	public static final float DEFAULT_SPIKE_FIXUP = 1.1f;

	protected static final float LOW_SPIKE_LUX = 0.0f;
	protected static final float HIGH_SPIKE_10K_LUX = 10000.0f;
	protected static final float HIGH_SPIKE_30K_LUX = 30000.0f;

	private static final String LOG_TAG = LightSensorFilter.class.getSimpleName();

	private final long mSmoothWindow;
	private final float mSpikeFixup;

	private final MovingAverage mFilter;
	private long mLastSpikeTimestamp;

	public LightSensorFilter() {
		this(DEFAULT_SMOOTH_WINDOW, DEFAULT_SPIKE_FIXUP);
	}

	/**
	 * @param smoothWindow window size for smoothing (in nanoseconds)
	 * @param spikeFixup coefficient to use to adjust spikes
	 */
	public LightSensorFilter(long smoothWindow, float spikeFixup) {
		mSmoothWindow = smoothWindow;
		mSpikeFixup = spikeFixup;

		mFilter = new MovingAverage(smoothWindow);
	}

	public float fixupLuxValue(float lux, long timestamp) {
		double averagedLux = Double.NaN;

		// In this case the use of exact float comparison is intended.
		if (lux == HIGH_SPIKE_30K_LUX || lux == HIGH_SPIKE_10K_LUX) {
			// it goes crazy and shows 30k lux (10k on some devices)
			averagedLux = mFilter.getAverage() * mSpikeFixup;
			Log.d(LOG_TAG, "fixup high spike: " + lux + " lux");

			// sometimes it drops from 30k to zero occasionally,
			// be ready for fluctuations in the nearest future
			mLastSpikeTimestamp = timestamp;

		} else if (lux == LOW_SPIKE_LUX &&
				timestamp - mLastSpikeTimestamp < mSmoothWindow * 2) {
			// reports zero within few ticks after 30k: outlier? out liar!
			averagedLux = mFilter.getAverage() / mSpikeFixup;
			Log.d(LOG_TAG, "fixup low spike:  " + lux + " lux");

		}

		if (!Double.isNaN(averagedLux)) {
			lux = (float) averagedLux;
		} else {
			mFilter.addValue(lux, timestamp);
		}

		return lux;
	}

	public long getSmoothWindow() {
		return mSmoothWindow;
	}

	public double getSpikeFixup() {
		return mSpikeFixup;
	}

}
