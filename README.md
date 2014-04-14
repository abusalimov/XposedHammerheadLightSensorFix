Nexus 5 Light Sensor Fix
========================

This project addresses some issues with Ambient Light Sensor of Nexus 5 appearing under certain lighting conditions, that make stock auto-brightness effectively unusable indoors.

The issue
---------
Sometimes the light sensor goes crazy and reports 30000 lux even in a dim light because of what auto-brightness attempts to blind you in a moment. These random spikes happen when you hold a phone at certain angles and depend on light bulbs used in a room.

The solution
------------
The fix is implemented as an Xposed module.

Basically, it is a filter inserted near a point where native HAL communicates to Android framework. It intercepts all sensor readings and replaces abnormal 30000 lux (and 0 lux following 30000) with an averaged value from a sliding window. This affects any process that use Android sensors API including system_process, so that default Android auto-brightness works fine too (no need to use apps like Lux Dash to workaround the issue).

Installation
------------
 1. Download and install [Xposed framework](http://repo.xposed.info/module/de.robv.android.xposed.installer)
 2. Search for and install **[Nexus 5 Light Sensor fix](http://repo.xposed.info/module/ru.abusalimov.xposed.lightsensorfilter)** module
 3. Activate the module and reboot

Usage
-----
The module provides no user interface, nor it runs any services in a background. It only injects a proxy method to the implementation of Android sensors API. You won't be able to notice it in the main menu or in a task manager. Think of it as a patch that can be turned on and off through Xposed installer.

Links
-----
 - [Module page](http://repo.xposed.info/module/ru.abusalimov.xposed.lightsensorfilter) on Xposed repo
 - [Discussion](http://forum.xda-developers.com/xposed/modules/mod-ambient-light-sensor-fix-nexus-5-t2717309) thread on XDA
 - [Original](http://forum.xda-developers.com/google-nexus-5/help/major-issues-nexus-5-ambient-light-t2537978/post51619083) module announcement and discussion on the issue
