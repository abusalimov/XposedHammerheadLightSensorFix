package ru.abusalimov.xposed.lightsensorfilter;

import android.util.Log;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

public class LightSensorFilter implements IXposedHookLoadPackage {

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
						if (handle == 1) {
							float[] values = (float[]) param.args[1];
							float lux = values[0];
							Log.v("LightSensorFilter", "lux value: " + lux);
						}

						// invoke the original method
					}

				});
	}

}
