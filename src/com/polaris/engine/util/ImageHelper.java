package com.polaris.engine.util;

import java.awt.image.BufferedImage;

public class ImageHelper 
{
	
	public static BufferedImage resize(BufferedImage image, int newWidth, int newHeight)
	{
		BufferedImage dimg = new BufferedImage(newWidth, newHeight, image.getType());
		for(int i = 0; i < image.getWidth(); i++)
		{
			for(int j = 0; j < image.getHeight(); j++)
			{
				dimg.setRGB(i, j, image.getRGB(i, j));
			}
		}
		return dimg;
	}
	
	public static void injectBufferedImage(BufferedImage image, BufferedImage injectionImage, int x, int y)
	{
		for(int i = 0; i < injectionImage.getWidth(); i++)
		{
			for(int j = 0; j < injectionImage.getHeight(); j++)
			{
				image.setRGB(i + x, j + y, injectionImage.getRGB(i, j));
			}
		}
	}

}
