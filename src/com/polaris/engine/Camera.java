package com.polaris.engine;

import static com.polaris.engine.render.Renderer.gl3d;
import static org.lwjgl.opengl.GL11.glRotatef;
import static org.lwjgl.opengl.GL11.glTranslatef;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;

import com.polaris.engine.render.Renderer;

public class Camera 
{
	
	private float prevPitch = 0;
	private float prevYaw = 0;
	private float pitch = 0;
	private float yaw = 0;
	
	public void gl3d(float f, float g, float h)
	{
		Renderer.gl3d(f, g, h);
		
		glTranslatef(0.0F, 0.0F, -0.1F);
		glRotatef(prevPitch + (pitch - prevPitch), 1.0F, 0.0F, 0.0F);
		glRotatef(prevYaw + (yaw - prevYaw) + 180.0F, 0.0F, 1.0F, 0.0F);
	}

	public void setAngles(float y, float p)
	{
		prevPitch = pitch;
		prevYaw = yaw;
		yaw = y;
		pitch = p;
		
		if (pitch < -90.0f)
		{
			pitch = -90.0f;
		}
		
		if (pitch > 90.0f)
		{
			pitch = 90.0f;
		}
	}
	
	public float getPitch()
	{
		return pitch;
	}
	
	public float getYaw()
	{
		return yaw;
	}
	
}
