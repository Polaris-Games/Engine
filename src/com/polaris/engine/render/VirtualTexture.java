package com.polaris.engine.render;

public class VirtualTexture implements ITexture
{

	private int textureId = 0;
	
	@Override
	public int getTextureID()
	{
		return textureId;
	}
	
	@Override
	public void setTextureID(int id)
	{
		textureId = id;
	}
	
	@Override
	public Texture getTexture()
	{
		return null;
	}
	
	@Override
	public Texture getTexture(String textureName)
	{
		return null;
	}

}
