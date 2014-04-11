package ru.abusalimov.xposed.lightsensorfilter;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;
import de.robv.android.xposed.callbacks.XCallback;

public class XposedMod implements IXposedHookLoadPackage {

	protected static class MethodHook extends XC_MethodHook {

		protected static final int ALS_HANDLE = 1;
		private LightSensorFilter mFilter = new LightSensorFilter();

		// dispatchSensorEvent(int handle, float[] values,
		//         int inAccuracy, long timestamp)
		//   (see /core/java/android/hardware/SystemSensorManager.java)
		// is called from native Receiver::handleEvent(int, int, void *)
		//   (see /core/jni/android_hardware_SensorManager.cpp)
		// each time a new value is read from HAL.

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

			long timestamp = (long) param.args[3];
			float[] values = (float[]) param.args[1];

			float lux = values[0];
			lux = mFilter.fixupLuxValue(lux);
			values[0] = lux;

			// invoke the original method
		}

		// Singleton-like: all instances are the same to avoid
		// unnecessary repeated filtering (don't know where it comes from).

		@Override
		public int compareTo(XCallback other) {
			if (this.equals(other)) {
				return 0;
			}
			return super.compareTo(other);
		}

		@Override
		public boolean equals(Object o) {
			if (o instanceof MethodHook) {
				return true;
			}
			return super.equals(o);
		}

		@Override
		public int hashCode() {
			return ~MethodHook.class.hashCode() ^ 0xdeadbeef;
		}

	}

	@Override
	public void handleLoadPackage(final LoadPackageParam lpparam) throws Throwable {
		XposedHelpers.findAndHookMethod(
				"android.hardware.SystemSensorManager$SensorEventQueue",
				lpparam.classLoader,
				"dispatchSensorEvent",
				int.class, float[].class, int.class, long.class,
				new MethodHook());
	}

}
