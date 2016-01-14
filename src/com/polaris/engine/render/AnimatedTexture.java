package com.polaris.engine.render;

public class AnimatedTexture extends Texture
{
	
	private float changeU = 0;
	private float changeV = 0;
	private int height = 0;
	
	public AnimatedTexture(float u, float v, float u1, float v1, int width, int height)
	{
		super(u, v, u1, v1);
		changeU = (getMaxU() - getMinU()) / (float) width;
		changeV = (getMaxV() - getMinV()) / (float) height;
		this.height = height;
	}
	
	public AnimatedTexture(Texture texture, int width, int height)
	{
		super(texture.getMinU(), texture.getMinV(), texture.getMaxU(), texture.getMaxV());
		changeU = (getMaxU() - getMinU()) / (float) width;
		changeV = (getMaxV() - getMinV()) / (float) height;
		this.height = height;
	}

	public void reduce(int width, int height)
	{
		super.reduce(width, height);
		changeU /= (float) width;
		changeV /= (float) height;
	}
	
	public float getMinU(int aniId)
	{
		return getMinU() + (aniId / height) * changeU;
	}
	
	public float getMinV(int aniId)
	{
		return getMinV() + (aniId % height) * changeV;
	}
	
	public float getMaxU(int aniId)
	{
		return getMinU() + (aniId / height + 1) * changeU;
	}
	
	public float getMaxV(int aniId)
	{
		return getMinV() + (aniId % height + 1) * changeV;
	}
	
	public int getWidth()
	{
		return (int) Math.round((getMaxU() - getMinU()) / changeU);
	}
	
	public int getHeight()
	{
		return height;
	}

}
