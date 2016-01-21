package com.polaris.engine.render;

import static com.polaris.engine.render.Renderer.drawColorHRectUV;
import static com.polaris.engine.render.Renderer.drawColorVRectUV;
import static com.polaris.engine.render.Renderer.drawRectUV;
import static com.polaris.engine.render.Renderer.getAlpha;
import static com.polaris.engine.render.Renderer.getBlue;
import static com.polaris.engine.render.Renderer.getGreen;
import static com.polaris.engine.render.Renderer.getRed;
import static com.polaris.engine.render.Renderer.glBegin;
import static org.lwjgl.opengl.GL11.glEnd;

import java.util.HashMap;
import java.util.Map;

import com.polaris.engine.util.Color4d;

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
	
	/**
	 * Simple form of drawing a string, will draw left->right
	 * @param whatchars : contains the content to be written
	 * @param x : x-position on screen to draw string on
	 * @param y : y-position on screen to draw string on
	 * @param z : z-level
	 * @param pointFont : size of characters
	 */
	public void drawString(CharSequence whatchars, double x, double y, double z, double pointFont)
	{
		drawChars(whatchars, x, y, z, pointFont);
	}

	/**
	 * Draw an aligned string
	 * @param whatchars : contains the content to be written
	 * @param x : x-position on screen to draw string on
	 * @param y : y-position on screen to draw string on
	 * @param z : z-level
	 * @param pointFont : size of characters
	 * @param alignment : 0->left, 1->center, 2->right alignment of string relative to x
	 */
	public void drawAlignedString(CharSequence whatchars, double x, double y, double z, double pointFont, int alignment)
	{
		switch(alignment)
		{
		case 1:
			x = x - getTextWidth(whatchars, pointFont) / 2;
			break;
		case 2:
			x = x - getTextWidth(whatchars, pointFont);
			break;
		}
		drawChars(whatchars, x, y, z, pointFont);
	}
	
	/**
	 * Draw string with maximum bounds, stretching or compressing if necessary
	 * @param whatchars : contains the content to be written
	 * @param x : x-position on screen to draw string on
	 * @param y : y-position on screen to draw string on
	 * @param z : z-level
	 * @param width : maximum width the string can be
	 * @param height : maximum height the string can be
	 */
	public void drawFittedString(CharSequence whatchars, double x, double y, double z, double width, double height)
	{
		drawChars(whatchars, x, y, z, Math.min(height / getTextHeight(whatchars), width / getTextWidth(whatchars)));
	}
	
	/**
	 * Draw string with maximum bounds, stretching or compressing if necessary
	 * @param whatchars : contains the content to be written
	 * @param x : x-position on screen to draw string on
	 * @param y : y-position on screen to draw string on
	 * @param z : z-level
	 * @param alignment : 0->left, 1->center, 2->right alignment of string relative to x
	 * @param width : maximum width the string can be
	 * @param height : maximum height the string can be
	 */
	public void drawAlignedFittedString(CharSequence whatchars, double x, double y, double z, int alignment, double width, double height)
	{
		double pointFont = Math.min(height / getTextHeight(whatchars), width / getTextWidth(whatchars));
		switch(alignment)
		{
		case 1:
			x = x - getTextWidth(whatchars, pointFont) / 2;
			break;
		case 2:
			x = x - getTextWidth(whatchars, pointFont);
			break;
		}
		drawChars(whatchars, x, y, z, pointFont);
	}
	
	/**
	 * Draw a color shifted string from left->right color shifting
	 * @param whatchars : contains the content to be written
	 * @param x : x-position on screen to draw string on
	 * @param y : y-position on screen to draw string on
	 * @param z : z-level
	 * @param pointFont : size of characters
	 * @param shiftToColor : ending color on the right
	 */
	public void drawColorString(CharSequence whatchars, double x, double y, double z, double pointFont, Color4d shiftToColor)
	{
		drawChars(whatchars, x, y, z, pointFont, shiftToColor);
	}
	
	/**
	 * Draw a color shifted, aligned string with left->right color shifting
	 * @param whatchars : contains the content to be written
	 * @param x : x-position on screen to draw string on
	 * @param y : y-position on screen to draw string on
	 * @param z : z-level
	 * @param pointFont : size of characters
	 * @param alignment : 0->left, 1->center, 2->right alignment of string relative to x
	 * @param shiftToColor : ending color on the right
	 */
	public void drawAlignedCString(CharSequence whatchars, double x, double y, double z, double pointFont, int alignment, Color4d shiftToColor)
	{
		switch(alignment)
		{
		case 1:
			x = x - getTextWidth(whatchars, pointFont) / 2;
			break;
		case 2:
			x = x - getTextWidth(whatchars, pointFont);
			break;
		}
		drawChars(whatchars, x, y, z, pointFont, shiftToColor);
	}
	
	/**
	 * Draw a color shifted, aligned, and fitted string with left->right color shifting
	 * @param whatchars : contains the content to be written
	 * @param x : x-position on screen to draw string on
	 * @param y : y-position on screen to draw string on
	 * @param z : z-level
	 * @param alignment : 0->left, 1->center, 2->right alignment of string relative to x
	 * @param shiftToColor : ending color on the right
	 * @param width : maximum width the string can be
	 * @param height : maximum height the string can be
	 */
	public void drawAlignedFCString(CharSequence whatchars, double x, double y, double z, int alignment, double width, double height, Color4d shiftToColor)
	{
		double pointFont = Math.min(height / getTextHeight(whatchars), width / getTextWidth(whatchars));
		switch(alignment)
		{
		case 1:
			x = x - getTextWidth(whatchars, pointFont) / 2;
			break;
		case 2:
			x = x - getTextWidth(whatchars, pointFont);
			break;
		}
		drawChars(whatchars, x, y, z, pointFont, shiftToColor);
	}
	
	/**
	 * Simple form of drawing a string, will draw top->bottom
	 * @param whatchars : contains the content to be written
	 * @param x : x-position on screen to draw string on
	 * @param y : y-position on screen to draw string on
	 * @param z : z-level
	 * @param pointFont : size of characters
	 */
	public void drawVerticalString(CharSequence whatchars, double x, double y, double z, double pointFont)
	{
		drawVerticalChars(whatchars, x, y, z, pointFont);
	}
	
	/**
	 * Draw an aligned string
	 * @param whatchars : contains the content to be written
	 * @param x : x-position on screen to draw string on
	 * @param y : y-position on screen to draw string on
	 * @param z : z-level
	 * @param pointFont : size of characters
	 * @param alignment : 0->top, 1->center, 2->bottom alignment of string relative to y
	 */
	public void drawVAlignedString(CharSequence whatchars, double x, double y, double z, double pointFont, int alignment)
	{
		switch(alignment)
		{
		case 1:
			y = y - getVTextHeight(whatchars, pointFont) / 2;
			break;
		case 2:
			y = y - getVTextHeight(whatchars, pointFont);
			break;
		}
		drawVerticalChars(whatchars, x, y, z, pointFont);
	}
	
	/**
	 * Draw string with maximum bounds, stretching or compressing if necessary
	 * @param whatchars : contains the content to be written
	 * @param x : x-position on screen to draw string on
	 * @param y : y-position on screen to draw string on
	 * @param z : z-level
	 * @param width : maximum width the string can be
	 * @param height : maximum height the string can be
	 */
	public void drawVFittedString(CharSequence whatchars, double x, double y, double z, double width, double height)
	{
		drawVerticalChars(whatchars, x, y, z, Math.min(height / getVTextHeight(whatchars), width / getVTextWidth(whatchars)));
	}
	
	/**
	 * Draw string with maximum bounds, stretching or compressing if necessary
	 * @param whatchars : contains the content to be written
	 * @param x : x-position on screen to draw string on
	 * @param y : y-position on screen to draw string on
	 * @param z : z-level
	 * @param alignment : 0->top, 1->center, 2->bottom alignment of string relative to y
	 * @param width : maximum width the string can be
	 * @param height : maximum height the string can be
	 */
	public void drawVAlignedFittedString(CharSequence whatchars, double x, double y, double z, int alignment, double width, double height)
	{
		double pointFont = Math.min(height / getVTextHeight(whatchars), width / getVTextWidth(whatchars));
		switch(alignment)
		{
		case 1:
			y = y - getVTextHeight(whatchars, pointFont) / 2;
			break;
		case 2:
			y = y - getVTextHeight(whatchars, pointFont);
			break;
		}
		drawVerticalChars(whatchars, x, y, z, pointFont);
	}
	
	/**
	 * Draw a color shifted string from top->bottom color shifting
	 * @param whatchars : contains the content to be written
	 * @param x : x-position on screen to draw string on
	 * @param y : y-position on screen to draw string on
	 * @param z : z-level
	 * @param pointFont : size of characters
	 * @param shiftToColor : ending color on the right
	 */
	public void drawVColorString(CharSequence whatchars, double x, double y, double z, double pointFont, Color4d shiftToColor)
	{
		drawVerticalChars(whatchars, x, y, z, pointFont, shiftToColor);
	}
	
	/**
	 * Draw a color shifted, aligned string with top->bottom color shifting
	 * @param whatchars : contains the content to be written
	 * @param x : x-position on screen to draw string on
	 * @param y : y-position on screen to draw string on
	 * @param z : z-level
	 * @param pointFont : size of characters
	 * @param alignment : 0->top, 1->center, 2->bottom alignment of string relative to y
	 * @param shiftToColor : ending color on the right
	 */
	public void drawVAlignedCString(CharSequence whatchars, double x, double y, double z, double pointFont, int alignment, Color4d shiftToColor)
	{
		switch(alignment)
		{
		case 1:
			y = y - getVTextHeight(whatchars, pointFont) / 2;
			break;
		case 2:
			y = y - getVTextHeight(whatchars, pointFont);
			break;
		}
		drawVerticalChars(whatchars, x, y, z, pointFont, shiftToColor);
	}
	
	/**
	 * Draw a color shifted, aligned, and fitted string with left->right color shifting
	 * @param whatchars : contains the content to be written
	 * @param x : x-position on screen to draw string on
	 * @param y : y-position on screen to draw string on
	 * @param z : z-level
	 * @param alignment : 0->left, 1->center, 2->right alignment of string relative to x
	 * @param shiftToColor : ending color on the right
	 * @param width : maximum width the string can be
	 * @param height : maximum height the string can be
	 */
	public void drawVAlignedFCString(CharSequence whatchars, double x, double y, double z, int alignment, double width, double height, Color4d shiftToColor)
	{
		double pointFont = Math.min(height / getVTextHeight(whatchars), width / getVTextWidth(whatchars));
		switch(alignment)
		{
		case 1:
			y = y - getVTextHeight(whatchars, pointFont) / 2;
			break;
		case 2:
			y = y - getVTextHeight(whatchars, pointFont);
			break;
		}
		drawVerticalChars(whatchars, x, y, z, pointFont, shiftToColor);
	}
	
	
	private void drawChars(CharSequence whatchars, double x, double y, double z, double pointFont)
	{
		IntObject letter;
		int i;
		double width;
		glBegin();
		for(i = 0; i < whatchars.length(); i++)
		{
			letter = getCharData(whatchars.charAt(i));
			if(letter != null)
			{
				width = letter.getWidth() * pointFont;
				drawRectUV(x, y, x + width, y + letter.getHeight() * pointFont, z, letter.getTexture());
				x += width;
			}
		}
		glEnd();
	}
	
	private void drawChars(CharSequence whatchars, double x, double y, double z, double pointFont, Color4d shiftToColor)
	{
		double textWidth = getTextWidth(whatchars, pointFont);
		double redShift = (shiftToColor.getRed() - getRed()) / textWidth;
		double greenShift = (shiftToColor.getGreen() - getGreen()) / textWidth;
		double blueShift = (shiftToColor.getBlue() - getBlue()) / textWidth;
		double alphaShift = (shiftToColor.getAlpha() - getAlpha()) / textWidth;
		
		IntObject letter;
		int i;
		double width;
		glBegin();
		for(i = 0; i < whatchars.length(); i++)
		{
			letter = getCharData(whatchars.charAt(i));
			if(letter != null)
			{
				width = letter.getWidth() * pointFont;
				drawColorHRectUV(x, y, x + width, y + letter.getHeight() * pointFont, z, letter.getTexture(), getRed() + (redShift * width), getGreen() + (greenShift * width), getBlue() + (blueShift * width), getAlpha() + (alphaShift * width));
				x += width;
			}
		}
		glEnd();
	}
	
	private void drawVerticalChars(CharSequence whatchars, double x, double y, double z, double pointFont)
	{
		IntObject letter;
		int i;
		double height;
		glBegin();
		for(i = 0; i < whatchars.length(); i++)
		{
			letter = getCharData(whatchars.charAt(i));
			if(letter != null)
			{
				height = letter.getHeight() * pointFont;
				drawRectUV(x, y, x + letter.getWidth() * pointFont, y + height, z, letter.getTexture());
				y += height;
			}
		}
		glEnd();
	}
	
	private void drawVerticalChars(CharSequence whatchars, double x, double y, double z, double pointFont, Color4d shiftToColor)
	{
		double textWidth = getTextWidth(whatchars, pointFont);
		double redShift = (shiftToColor.getRed() - getRed()) / textWidth;
		double greenShift = (shiftToColor.getGreen() - getGreen()) / textWidth;
		double blueShift = (shiftToColor.getBlue() - getBlue()) / textWidth;
		double alphaShift = (shiftToColor.getAlpha() - getAlpha()) / textWidth;
		
		IntObject letter;
		int i;
		double height;
		glBegin();
		for(i = 0; i < whatchars.length(); i++)
		{
			letter = getCharData(whatchars.charAt(i));
			if(letter != null)
			{
				height = letter.getHeight() * pointFont;
				drawColorVRectUV(x, y, x + letter.getWidth() * pointFont, y + height, z, letter.getTexture(), getRed() + (redShift * height), getGreen() + (greenShift * height), getBlue() + (blueShift * height), getAlpha() + (alphaShift * height));
				y += height;
			}
		}
		glEnd();
	}
	
	/**
	 * Gets the width of the text (without any point font multiplication)
	 * @param whatchars : contains the content to be written
	 * @return total width of whatchars with a <b>double return type</b>
	 */
	public double getTextWidth(CharSequence whatchars)
	{
		double totalWidth = 0;
		IntObject intObject = null;

		for (int i = 0; i < whatchars.length(); i++)
		{
			intObject = getCharData(whatchars.charAt(i));
			if(intObject != null)
			{
				totalWidth += intObject.getWidth();
			}
			else
				return 0;
		}
		return totalWidth;
	}

	/**
	 * Gets the width of the text
	 * @param whatchars : contains the content to be written
	 * @param pointFont : size of characters
	 * @return total width of whatchars * pointFont, <b>double return type</b>
	 */
	public double getTextWidth(CharSequence whatchars, double pointFont)
	{
		return getTextWidth(whatchars) * pointFont;
	}
	
	/**
	 * Gets the width of a vertical text (without any point font multiplication)
	 * @param whatchars : contains the content to be written
	 * @return largest character width, <b>double return type</b>
	 */
	public double getVTextWidth(CharSequence whatchars)
	{
		double largestWidth = 0;
		IntObject intObject = null;
		
		for	(int i = 0; i < whatchars.length(); i++)
		{
			intObject= getCharData(whatchars.charAt(i));
			if(intObject != null)
			{
				largestWidth = Math.max(intObject.getWidth(), largestWidth);
			}
		}
		return largestWidth;
	}
	
	/**
	 * Gets the width of a vertical text
	 * @param whatchars : contains the content to be written
	 * @param pointFont : size of characters
	 * @return largest character width * pointFont, <b>double return type</b>
	 */
	public double getVTextWidth(CharSequence whatchars, double pointFont)
	{
		return getVTextWidth(whatchars) * pointFont;
	}

	public double getTextHeight(CharSequence whatchars)
	{
		double largestHeight = 0;
		IntObject intObject = null;

		for (int i = 0; i < whatchars.length(); i++)
		{
			intObject = getCharData(whatchars.charAt(i));
			if(intObject != null)
			{
				largestHeight = Math.max(intObject.getHeight(), largestHeight);
			}
		}
		return largestHeight;
	}
	
	public double getTextHeight(CharSequence whatchars, double pointFont)
	{
		return getTextHeight(whatchars) * pointFont;
	}
	
	public double getVTextHeight(CharSequence whatchars)
	{
		double totalHeight = 0;
		IntObject intObject = null;

		for (int i = 0; i < whatchars.length(); i++)
		{
			intObject = getCharData(whatchars.charAt(i));
			if(intObject != null)
			{
				totalHeight += intObject.getHeight();
			}
			else
				return 0;
		}
		return totalHeight;
	}
	
	public double getVTextHeight(CharSequence whatchars, double pointFont)
	{
		return getVTextHeight(whatchars) * pointFont;
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
