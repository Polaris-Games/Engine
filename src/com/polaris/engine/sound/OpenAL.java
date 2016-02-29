package com.polaris.engine.sound;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.openal.ALContext;
import org.lwjgl.openal.ALDevice;

import com.polaris.engine.Application;


public class OpenAL
{

	private static ALDevice device;
	private static ALContext context;
	private static List<StaticSound> staticSounds = new ArrayList<StaticSound>();
	private static List<Sound> updateSounds = new ArrayList<Sound>();
	private static BackgroundSound backgroundSound = null;
	
	public static void initAL()
	{
		device = ALDevice.create();

		if(device == null || !device.getCapabilities().OpenALC11)
			throw new RuntimeException("OpenAL Device Opening failed");

		context = ALContext.create(device);

		if(context == null || !context.getCapabilities().OpenAL11)
			throw new RuntimeException("OpenAL Context Creation failed");

		context.makeCurrentThread();
	}
	
	public static void closeAL()
	{
		context.destroy();
		device.destroy();
	}

	public boolean isRunning = true;
	private Application app;

	public OpenAL(Application application)
	{
		app = application;
	}

	public void run()
	{
		// Initialize Open AL
		device = ALDevice.create();

		if(device == null || !device.getCapabilities().OpenALC11)
			throw new RuntimeException("OpenAL Device Opening failed");

		context = ALContext.create(device);

		if(context == null || !context.getCapabilities().OpenAL11)
			throw new RuntimeException("OpenAL Context Creation failed");

		context.makeCurrentThread();

		//while(isRunning)
		//{
		//	//System.out.println("thisa");
		//	update();
		//} 
	}

	public static void addSound(StaticSound sound)
	{
		if(sound instanceof Sound)
		{
			updateSounds.add((Sound)sound);
		}
		else
		{
			staticSounds.add(sound);
		}
	}

	public static void setBackgroundSound(final BackgroundSound sound)
	{
		new Thread()
		{
			public void run()
			{
				backgroundSound.quiet();
				while(!backgroundSound.isFinished())
				{
					try
					{
						Thread.sleep(3);
					}
					catch (InterruptedException e) 
					{
						e.printStackTrace();
					}
				}
				backgroundSound.close();
				backgroundSound = sound;
			}
		}.start();
	}

	public void update() 
	{
		//Camera camera = app.getCurrentScreen().getCamera();
		//alListenerfv(AL_POSITION, camera.getPosition());
		//alListenerfv(AL_VELOCITY, camera.getVelocity());
		//alListenerfv(AL_ORIENTATION, camera.getOrientation());

		for(StaticSound sound : staticSounds)
		{
			if(sound.isFinished())
			{
				sound.close();
				staticSounds.remove(sound);
			}
		}
		for(Sound sound : updateSounds)
		{
			if(sound.isFinished())
			{
				sound.close();
				staticSounds.remove(sound);
			}
			else
			{
				sound.update();
			}
		}
		if(backgroundSound != null)
		{
			if(backgroundSound.isFinished())
			{
				backgroundSound.close();
				backgroundSound = null;
			}
			else
			{
				backgroundSound.update();
			}
		}
	}

}
