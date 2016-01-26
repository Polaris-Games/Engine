package com.polaris.engine;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;

public class Camera 
{
	
	private FloatBuffer listenerPosition = BufferUtils.createFloatBuffer(3);
	private FloatBuffer listenerOrientation = BufferUtils.createFloatBuffer(6);
	private FloatBuffer listenerVelocity = BufferUtils.createFloatBuffer(3);	
	
	public Camera(float[] fs, float[] fs2, float[] fs3) 
	{
		listenerPosition.put(fs, 0, fs.length);
		listenerOrientation.put(fs2, 0, fs2.length);
		listenerVelocity.put(fs3, 0, fs3.length);
	}
	
	public FloatBuffer getPosition()
	{
		return listenerPosition;
	}
	
	public FloatBuffer getOrientation()
	{
		return listenerOrientation;
	}
	
	public FloatBuffer getVelocity()
	{
		return listenerVelocity;
	}

}
