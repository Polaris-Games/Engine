package com.polaris.engine.sound;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.BufferUtils;
import org.lwjgl.openal.AL10;
import org.lwjgl.openal.ALContext;


public class SoundManager extends Thread
{
	
	private static ALContext context = ALContext.create();
	private static List<StaticSound> staticSounds = new ArrayList<StaticSound>();
	private static List<Sound> updateSounds = new ArrayList<Sound>();
	private static BackgroundSound backgroundSound = null;
	
	public boolean isRunning = true;
	private FloatBuffer listenerPosition = BufferUtils.createFloatBuffer(3).put(new float[] {0, 0, 0});
	private FloatBuffer listenerOrientation = BufferUtils.createFloatBuffer(6).put(new float[] {0, 0, 0, 0, 0, 0});
	private FloatBuffer listenerVelocity = BufferUtils.createFloatBuffer(3).put(new float[] {0, 0, 0});	
	
	public void run()
	{
		if (!context.getCapabilities().OpenAL10)
		    throw new RuntimeException("OpenAL Context Creation failed");
		context.makeCurrentThread();
		
		while(isRunning)
		{
			
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
		AL10.alListenerf(AL10.AL_POSITION,    listenerPosition);
	    AL10.alListenerf(AL10.AL_VELOCITY,    listenerVelocity);
	    AL10.alListenerf(AL10.AL_ORIENTATION, listenerOrientation);
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
