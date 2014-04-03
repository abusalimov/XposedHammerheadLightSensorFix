package ru.abusalimov.xposed.lightsensorfilter;

import android.util.Log;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

public class LightSensorFilter implements IXposedHookLoadPackage {

	protected static final int ALS_HANDLE = 1;
	protected static final float BOGUS_LUX_VALUE = 30000.0f;

	@Override
	public void handleLoadPackage(LoadPackageParam lpparam) throws Throwable {
		XposedHelpers.findAndHookMethod(
				"android.hardware.SystemSensorManager$SensorEventQueue",
				lpparam.classLoader,
				"dispatchSensorEvent",
				int.class, float[].class, int.class, long.class,

				new XC_MethodHook() {

					@Override
					protected void beforeHookedMethod(MethodHookParam param)
							throws Throwable {
						int handle = (int) param.args[0];
						if (handle == ALS_HANDLE) {
							float[] values = (float[]) param.args[1];

							float lux = values[0];
							if (Float.compare(lux, BOGUS_LUX_VALUE) == 0) {
								param.setResult(null);
								Log.v("LightSensorFilter",
										"suppresed bogus light sensor value " +
										lux + "lux");
							}
						}

						// invoke the original method
					}

				});
	}

}
