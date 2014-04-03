package ru.abusalimov.xposed.lightsensorfilter;

import android.util.Log;

public class LightSensorFilter {

	protected static final int MA_WINDOW_SIZE = 7;
	protected static final int SEEN_BOGUS_WINDOW_SIZE = MA_WINDOW_SIZE;

	protected static final float HIGH_BOGUS_LUX = 30000.0f;
	protected static final float LOW_BOGUS_LUX = 0.0f;

	protected static final float AVG_FIXUP_COEF = 1.3f;

	private MovingAverage mMA = new MovingAverage(MA_WINDOW_SIZE);
	private int mSeenBogus;

	public float fixupLuxValue(float lux) {
		if (Float.compare(lux, HIGH_BOGUS_LUX) == 0) {
			// it goes crazy and shows 30k lux
			lux = mMA.getAverage() * AVG_FIXUP_COEF;
			Log.v("LightSensorFilter", "fixup bogus high: " + lux + "lux");

			// sometimes it drops from 30k to zero occasionally,
			// be ready for fluctuations in the nearest future
			mSeenBogus = SEEN_BOGUS_WINDOW_SIZE;
		} else if (mSeenBogus != 0) {

			if (Float.compare(lux, LOW_BOGUS_LUX) == 0) {
				// reports zero within few ticks after 30k: LIAR!11
				lux = mMA.getAverage() / AVG_FIXUP_COEF;
				Log.v("LightSensorFilter", "fixup bogus low:  " + lux + "lux");

			} else {
				--mSeenBogus;
			}
		}

		// TODO Does it need to be fed back to MA unconditionally?
		mMA.handleNewValue(lux);
		return lux;
	}

}
