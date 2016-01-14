package com.polaris.engine.render;

import java.util.HashMap;
import java.util.Map;

public class FontManager
{

	private Map<String, Font> fontMap = new HashMap<String, Font>();
	private Font currentFont = null;

	public void addFont(String title, FontMap map)
	{
		fontMap.put(title, new Font(map));
	}

	public Font bindFont(String fontName)
	{
		if(fontMap.containsKey(fontName))
		{
			Font font = fontMap.get(fontName);
			font.bindFont();
			return font;
		}
		return null;
	}

	public void rebindFont()
	{
		currentFont.bindFont();
	}

	public void loadFont(String fontName)
	{

	}

	public void unloadFont(String fontName)
	{

	}

	public void unloadFonts()
	{

	}

	public ITexture getFontTexture()
	{
		return currentFont.getFontMap();
	}

	public Font getCurrentFont()
	{
		return currentFont;
	}

}
