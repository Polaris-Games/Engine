package com.polaris.engine.render;

import java.util.HashMap;
import java.util.Map;

public class TextureMap extends StitchedMap
{

	private Map<String, Texture> textureMap = new HashMap<String, Texture>();
	private Texture currentTexture = null;
	
	public Map<String, Texture> getMap()
	{
		return textureMap;
	}
	
	public void setMap(Map<String, Texture> textureMap)
	{
		this.textureMap = textureMap;
	}

	public boolean bindTexture(String texture) 
	{
		Texture bindTexture = textureMap.containsKey(texture) ? textureMap.get(texture) : null;
		if(bindTexture != null && currentTexture != bindTexture)
		{
			currentTexture = bindTexture;
			return true;
		}
		return false;
	}
	
	@Override
	public Texture getTexture()
	{
		return currentTexture;
	}
	
	@Override
	public Texture getTexture(String textureName)
	{
		return textureMap.get(textureName);
	}

}
