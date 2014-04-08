Nexus 5 Light Sensor Fix
========================

This project addresses some issues with Ambient Light Sensor of Nexus 5 appearing under certain lighting conditions. These issues are discussed on related XDA Developers threads.

XDA topics
----------

[Two major issues with the Nexus 5 ambient light sensor](http://forum.xda-developers.com/google-nexus-5/help/major-issues-nexus-5-ambient-light-t2537978)

> 1. The sensor reading often jumps to 30000lx momentarily, (measured using Lux Dash in Debug mode), and so the phone blinds you for while. This happens in a repeatable fashion when you hold the phone at certain angles. Try it yourself.
> 2. The N5 reads zero lux even in moderate/dim light
([Posted by Palmadores](http://forum.xda-developers.com/showpost.php?p=47725284&postcount=1))

<p>

> Using the Lux app debug mode I rotated the phone while in a room lit with incandescent bulbs and one lit with daylight. When rotating the phone I sometimes see a spike of 30000 lx but more importantly the sensor drops to 0 even though there is plenty of ambient light. During daylight I don't see the 30000 lx spikes but I still see the sensor dropping to 0 when there's plenty of ambient light.
([Posted by exorz](http://forum.xda-developers.com/showpost.php?p=48845073&postcount=13))

[Is the Auto-Brightness Functionality wonky on the Nexus5?](http://forum.xda-developers.com/google-nexus-5/help/auto-brightness-functionality-wonky-t2554153)
> I think it may be bugged with Halogen lighting (correct me if I'm wrong). My home is ~ 18 years old, and we have some bulbs that have not been changed yet (yellow). Sometimes when I use my N5 under those lighting, the sensors go whack and don't register properly picking up 0lx, then spike up to 30000lx. Once I move to areas in the house with newer bulbs, the sensors work normal, picking up the right readings.
([Posted by Aria807](http://forum.xda-developers.com/showpost.php?p=48111193&postcount=4))


The solution
------------
This project is based on [Xposed framework](http://repo.xposed.info/), which is used to intercept and override certain methods of Android framework itself.
