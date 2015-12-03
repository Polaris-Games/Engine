package com.polaris.engine.render;

public interface ITexture 
{

	public String getTitle();
	public void load();
	public void unload();
	public void setCurrent();
	public boolean stitch();
	
}
