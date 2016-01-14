package com.polaris.engine.render;

import static com.polaris.engine.render.Renderer.drawColorHRectUV;
import static com.polaris.engine.render.Renderer.drawRectUV;
import static com.polaris.engine.render.Renderer.getAlpha;
import static com.polaris.engine.render.Renderer.getBlue;
import static com.polaris.engine.render.Renderer.getGreen;
import static com.polaris.engine.render.Renderer.getRed;
import static com.polaris.engine.render.Renderer.glBegin;
import static org.lwjgl.opengl.GL11.glEnd;

import com.polaris.engine.render.FontMap.IntObject;

public class Font
{
	
	private FontMap fontTexture;
	
	public Font(FontMap fontMap)
	{
		fontTexture = fontMap;
	}
	
	public void bindFont()
	{
		
	}

	/**
	 * Simple form of drawing a string, no manipulators, will draw left->right
	 * @param whatchars : contains the content to be written
	 * @param x : x-position on screen to draw string on
	 * @param y : y-position on screen to draw string on
	 * @param z : z-level
	 * @param pointFont : size of the string to be drawn
	 */
	public void drawString(CharSequence whatchars, double x, double y, double z, double pointFont)
	{
		drawCharacters(whatchars, x, y, z, pointFont);
	}

	/**
	 * Align the drawing of a string, manipulator of alignment
	 * @param whatchars : contains the content to be written
	 * @param x : x-position on screen to draw string on
	 * @param y : y-position on screen to draw string on
	 * @param z : z-level
	 * @param pointFont : size of the string to be drawn
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
		drawCharacters(whatchars, x, y, z, pointFont);
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
		drawCharacters(whatchars, x, y, z, Math.min(height / getTextHeight(whatchars), width / getTextWidth(whatchars)));
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
		drawCharacters(whatchars, x, y, z, pointFont);
	}
	
	/**
	 * Draw a color shifted string from left->right color shifting
	 * @param whatchars : contains the content to be written
	 * @param x : x-position on screen to draw string on
	 * @param y : y-position on screen to draw string on
	 * @param z : z-level
	 * @param pointFont : size of the string to be drawn
	 * @param shiftToColor : ending color on the right
	 */
	public void drawColorHString(CharSequence whatchars, double x, double y, double z, double pointFont, Color4d shiftToColor)
	{
		drawCharacters(whatchars, x, y, z, pointFont, shiftToColor);
	}
	
	public void drawAlignedCHString(CharSequence whatchars, double x, double y, double z, double pointFont, int alignment, Color4d shiftToColor)
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
		drawCharacters(whatchars, x, y, z, pointFont, shiftToColor);
	}
	
	public void drawAlignedFCHString(CharSequence whatchars, double x, double y, double z, int alignment, double width, double height, Color4d shiftToColor)
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
		drawCharacters(whatchars, x, y, z, pointFont, shiftToColor);
	}
	
	public void drawLineString(CharSequence whatchars, double x, double y, double z, double pointFont, Line line)
	{
		IntObject object;
		for(int i = 0; i < whatchars.length(); i++)
		{
			object = fontTexture.getCharData(whatchars.charAt(i));
			if(object != null)
			{
				
			}
		}
	}
	
	private void drawCharacters(CharSequence whatchars, double x, double y, double z, double pointFont)
	{
		IntObject letter;
		int i;
		double width;
		glBegin();
		for(i = 0; i < whatchars.length(); i++)
		{
			letter = fontTexture.getCharData(whatchars.charAt(i));
			if(letter != null)
			{
				width = letter.getWidth() * pointFont;
				drawRectUV(x, y, x + width, y + letter.getHeight() * pointFont, z, letter.getTexture());
				x += width;
			}
		}
		glEnd();
	}
	
	private void drawCharacters(CharSequence whatchars, double x, double y, double z, double pointFont, Color4d shiftToColor)
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
			letter = fontTexture.getCharData(whatchars.charAt(i));
			if(letter != null)
			{
				width = letter.getWidth() * pointFont;
				drawColorHRectUV(x, y, x + width, y + letter.getHeight() * pointFont, z, letter.getTexture(), getRed() + (redShift * width), getGreen() + (greenShift * width), getBlue() + (blueShift * width), getAlpha() + (alphaShift * width));
				x += width;
			}
		}
		glEnd();
	}
	
	public double getTextWidth(CharSequence whatchars)
	{
		double totalWidth = 0;
		IntObject intObject = null;

		for (int i = 0; i < whatchars.length(); i++)
		{
			intObject = fontTexture.getCharData(whatchars.charAt(i));
			if(intObject != null)
			{
				totalWidth += intObject.getWidth();
			}
			else
				return 0;
		}
		return totalWidth;
	}

	public double getTextWidth(CharSequence whatchars, double pointFont)
	{
		return getTextWidth(whatchars) * pointFont;
	}

	public double getTextHeight(CharSequence whatchars)
	{
		double largestHeight = 0;
		IntObject intObject = null;

		for (int i = 0; i < whatchars.length(); i++)
		{
			intObject = fontTexture.getCharData(whatchars.charAt(i));
			if(intObject != null && intObject.getHeight() > largestHeight)
			{
				largestHeight = intObject.getHeight();
			}
		}
		return largestHeight;
	}
	
	public double getTextHeight(CharSequence whatchars, double pointFont)
	{
		return getTextHeight(whatchars) * pointFont;
	}

	public FontMap getFontMap() 
	{
		return fontTexture;
	}

}