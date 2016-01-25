package com.polaris.engine.sound;

import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;

import com.polaris.engine.util.Vertex3d;

public class StaticSound
{

	/** Buffers hold sound data. */
	protected IntBuffer buffer = BufferUtils.createIntBuffer(1);

	/** Sources are points emitting sound. */
	protected IntBuffer source = BufferUtils.createIntBuffer(1);

	
	protected StaticSound(String location)
	{
		
	}
	
	public StaticSound(String location, Vertex3d sourceVertex)
	{
	}

	public boolean isFinished()
	{
		return false;
	}

	public void close() 
	{

	}

}
