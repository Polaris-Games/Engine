package com.polaris.engine.render;

public class Texture
{
	
	private float minU, minV, maxU, maxV;
	
	public Texture(float u, float v, float u1, float v1)
	{
		minU = u;
		minV = v;
		maxU = u1;
		maxV = v1;
	}
	
	public void reduce(int width, int height)
	{
		minU /= (float) width;
		maxU /= (float) width;
		minV /= (float) height;
		maxV /= (float) height;
	}

	public void setMinU(float u)
	{
		minU = u;
	}
	
	public void setMinV(float v)
	{
		minV = v;
	}
	
	public void setMaxU(float u)
	{
		maxU = u;
	}
	
	public void setMaxV(float v)
	{
		maxV = v;
	}
	
	public float getMinU()
	{
		return minU;
	}
	
	public float getMinV()
	{
		return minV;
	}
	
	public float getMaxU()
	{
		return maxU;
	}
	
	public float getMaxV()
	{
		return maxV;
	}
	
	public float getMinU(int animationID)
	{
		return getMinU();
	}
	
	public float getMinV(int animationID)
	{
		return getMinV();
	}
	
	public float getMaxU(int animationID)
	{
		return getMaxU();
	}
	
	public float getMaxV(int animationID)
	{
		return getMaxV();
	}
	
}
