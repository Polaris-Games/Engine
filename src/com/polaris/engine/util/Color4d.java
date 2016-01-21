package com.polaris.engine.util;

public class Color4d 
{
	
	private double red;
	private double green;
	private double blue;
	private double alpha;
	
	/**
	 * Initiate a 4 value color
	 * @param red : 0 - 255
	 * @param green : 0 - 255
	 * @param blue : 0 - 255
	 * @param alpha : 0 - 255
	 */
	public Color4d(int red, int green, int blue, int alpha)
	{
		setColor(red / 255d, green / 255d, blue / 255d, alpha / 255d);
	}
	
	/**
	 * Initiate a 4 value color
	 * @param red : 0 - 1
	 * @param green : 0 - 1
	 * @param blue : 0 - 1
	 * @param alpha : 0 - 1
	 */
	public Color4d(double red, double green, double blue, double alpha)
	{
		setColor(red, green, blue, alpha);
	}
	
	/**
	 * Initiate a 4 value color
	 * @param color : the color
	 */
	public Color4d(Color4d color)
	{
		setColor(color);
	}
	
	/**
	 * @return red value 0 - 1 
	 */
	public double getRed()
	{
		return red;
	}
	
	/**
	 * @param red : value 0 - 1
	 */
	public void setRed(double red) 
	{
		this.red = red;
	}
	
	/**
	 * @return green value 0 - 1 
	 */
	public double getGreen()
	{
		return green;
	}
	
	/**
	 * @param green : value 0 - 1
	 */
	public void setGreen(double green) 
	{
		this.green = green;
	}
	
	/**
	 * @return blue value 0 - 1 
	 */
	public double getBlue()
	{
		return blue;
	}
	
	/**
	 * @param blue : value 0 - 1
	 */
	public void setBlue(double blue)
	{
		this.blue = blue;
	}
	
	/**
	 * @return alpha value 0 - 1 
	 */
	public double getAlpha()
	{
		return alpha;
	}
	
	/**
	 * @param alpha : value 0 - 1
	 */
	public void setAlpha(double alpha)
	{
		this.alpha = alpha;
	}

	/**
	 * Sets all color values from one <b>Color4d</b> object to another
	 * @param color : the color to copy from
	 */
	public void setColor(Color4d color) 
	{
		setRed(color.getRed());
		setGreen(color.getGreen());
		setBlue(color.getBlue());
		setAlpha(color.getAlpha());
	}
	
	/**
	 * Sets all color values
	 * @param red : value 0 - 1
	 * @param green : value 0 - 1
	 * @param blue : value 0 - 1
	 * @param alpha : value 0 - 1
	 */
	public void setColor(double red, double green, double blue, double alpha)
	{
		setRed(red);
		setGreen(green);
		setBlue(blue);
		setAlpha(alpha);
	}

}
