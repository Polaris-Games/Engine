package com.polaris.engine.sound;

import org.lwjgl.openal.ALContext;
import org.lwjgl.openal.ALDevice;

public class SoundManager
{

	/*private static ThreadLocal<ALContext> context = new ThreadLocal<ALContext>() {
		public ALContext initialValue()
		{
			return ALContext.create(null, 48000, 60, false);
		}
	};*/

	public static void update() 
	{

	}
	
	public static void release()
	{
		//ALDevice device = context.get().getDevice();
		//context.get().destroy();
		//device.destroy();
	}

}
