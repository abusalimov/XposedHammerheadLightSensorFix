package ru.abusalimov.xposed.lightsensorfilter;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

public class XposedMod implements IXposedHookLoadPackage {

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
					private LightSensorFilter mFilter = new LightSensorFilter();

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
						lux = mFilter.fixupLuxValue(lux);
						values[0] = lux;

						// invoke the original method
					}

				});
	}

}
