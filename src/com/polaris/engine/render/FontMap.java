package com.polaris.engine.render;

import java.util.HashMap;
import java.util.Map;

public class FontMap extends StitchedMap
{

	private IntObject[] textureArray = null;

	@Override
	public Map<String, Texture> getMap() 
	{
		Map<String, Texture> textureMap = new HashMap<String, Texture>();
		for(int i = 0; i < textureArray.length; i++)
		{
			IntObject intObject = textureArray[i];
			if(intObject != null)
			{
				textureMap.put("" + (char) i, intObject.texture);
			}
		}
		return textureMap;
	}

	@Override
	public void setMap(Map<String, Texture> textureMap)
	{
		int largestIntCode = 0;
		float largestWidth = 0;
		float largestHeight = 0;
		for(String s : textureMap.keySet())
		{
			Texture texture = textureMap.get(s);
			largestIntCode = Math.max(largestIntCode, (int) s.charAt(0));
			largestWidth = Math.max(largestWidth, texture.getMaxU(0) - texture.getMinU(0));
			largestHeight = Math.max(largestHeight, texture.getMaxV(0) - texture.getMinV(0));
		}
		textureArray = new IntObject[largestIntCode + 1];
		for(String s : textureMap.keySet())
		{
			Texture texture = textureMap.get(s);
			int code = (int) s.charAt(0);
			textureArray[code] = new IntObject();
			textureArray[code].texture = textureMap.get(s);
			textureArray[code].width = (texture.getMaxU(0) - texture.getMinU(0)) / largestWidth;
			textureArray[code].height = (texture.getMaxV(0) - texture.getMinV(0)) / largestHeight;
		}
	}

	public IntObject getCharData(char letter)
	{
		if(textureArray.length > (int) letter)
			return textureArray[(int) letter];
		return null;
	}

	public IntObject getCharData(int letter)
	{
		if(textureArray.length > letter)
			return textureArray[letter];
		return null;
	}

	public static class IntObject
	{
		private Texture texture;
		private float width;
		private float height;

		public Texture getTexture()
		{
			return texture;
		}

		public float getWidth()
		{
			return width;
		}

		public float getHeight()
		{
			return height;
		}
	}

}
