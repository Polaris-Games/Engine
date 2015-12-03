package com.polaris.engine.render;

import java.util.HashMap;
import java.util.Map;

public class TextureManager 
{
	
	private Map<String, ITexture> textureMap = new HashMap<String, ITexture>();
	private Map<String, TextureCoordinates> stitchedMap = new HashMap<String, TextureCoordinates>();
	private ITexture currentTexture = null;
	
	public TextureManager()
	{
		
	}
	
	private static class TextureCoordinates
	{
		
	}

}
