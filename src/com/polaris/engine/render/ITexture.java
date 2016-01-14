package com.polaris.engine.render;

public interface ITexture
{
	
	public int getTextureID();
	public void setTextureID(int id);
	public Texture getTexture();
	public Texture getTexture(String textureName);

}
