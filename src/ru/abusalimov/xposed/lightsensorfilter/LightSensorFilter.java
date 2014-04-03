package ru.abusalimov.xposed.lightsensorfilter;

import android.util.Log;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

public class LightSensorFilter implements IXposedHookLoadPackage {

	protected static final int MA_WINDOW_SIZE = 7;
	protected static final float BOGUS_LUX_VALUE = 30000.0f;

	private MovingAverage mMA = new MovingAverage(MA_WINDOW_SIZE);

	protected float fixupLuxValue(float lux) {
		if (Float.compare(lux, BOGUS_LUX_VALUE) == 0) {
			lux = mMA.getAverage();
			Log.v("LightSensorFilter", "fixup bogus with: " + lux + "lux");
		}

		// TODO Does it need to be fed back to MA unconditionally?
		mMA.handleNewValue(lux);
		return lux;
	}

	@Override
	public void handleLoadPackage(LoadPackageParam lpparam) throws Throwable {
		XposedHelpers.findAndHookMethod(
				"android.hardware.SystemSensorManager$SensorEventQueue",
				lpparam.classLoader,
				"dispatchSensorEvent",
				int.class, float[].class, int.class, long.class,

				// dispatchSensorEvent(int handle, float[] values,
				//         int inAccuracy, long timestamp)
				//   (see /core/java/android/hardware/SystemSensorManager.java)
				// is called from native Receiver::handleEvent(int, int, void *)
				//   (see /core/jni/android_hardware_SensorManager.cpp)
				// each time a new value is read from HAL.

				new XC_MethodHook() {

					protected static final int ALS_HANDLE = 1;

					@Override
					protected void beforeHookedMethod(MethodHookParam param)
							throws Throwable {
						int handle = (int) param.args[0];

						// We probably could perform more reliable checks
						// for a matching sensor here, but... who cares?
						// On Hammerhead light sensor seems to have handle 1.
						if (handle != ALS_HANDLE) {
							// not an Ambient Light Sensor
							return;
						}

						float[] values = (float[]) param.args[1];

						float lux = values[0];
						lux = fixupLuxValue(lux);
						values[0] = lux;

						// invoke the original method
					}

				});
	}

}
