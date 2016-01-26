package com.polaris.engine.sound;

import static org.lwjgl.openal.AL10.AL_ORIENTATION;
import static org.lwjgl.openal.AL10.AL_POSITION;
import static org.lwjgl.openal.AL10.AL_VELOCITY;
import static org.lwjgl.openal.AL10.alListenerfv;
import static org.lwjgl.openal.ALC10.alcCreateContext;
import static org.lwjgl.openal.ALC10.alcMakeContextCurrent;
import static org.lwjgl.openal.ALC10.alcOpenDevice;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.BufferUtils;
import org.lwjgl.openal.ALContext;
import org.lwjgl.openal.ALDevice;

import com.polaris.engine.Application;
import com.polaris.engine.Camera;


public class SoundManager extends Thread
{

	private static ALDevice device;
	private static ALContext context;
	private static List<StaticSound> staticSounds = new ArrayList<StaticSound>();
	private static List<Sound> updateSounds = new ArrayList<Sound>();
	private static BackgroundSound backgroundSound = null;

	public boolean isRunning = true;
	private Application app;

	public SoundManager(Application application)
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

		while(isRunning)
		{
			update();
		}
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
		Camera camera = app.getCurrentScreen().getCamera();
		alListenerfv(AL_POSITION, camera.getPosition());
		alListenerfv(AL_VELOCITY, camera.getVelocity());
		alListenerfv(AL_ORIENTATION, camera.getOrientation());

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
