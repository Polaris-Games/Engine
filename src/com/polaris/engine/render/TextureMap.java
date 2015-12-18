package com.polaris.engine.render;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageOutputStream;

import com.polaris.engine.util.DimensionalKey;

public class TextureMap implements ITexture
{
	
	private Map<String, Texture> textureMap = new HashMap<String, Texture>();
	private Texture currentTexture = null;
	private int textureId = 0;

	public BufferedImage genTextureMap(List<File> stitchTextures, File outputFile) throws IOException
	{
		List<BufferedImage> imageList = new ArrayList<BufferedImage>();
		for(File file : stitchTextures)
		{
			BufferedImage image = ImageIO.read(file);
			if(image != null && image.getWidth() > 0 && image.getHeight() > 0)
			{
				imageList.add(image);
			}
		}
		
		return null;
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
	
	public void genInfo(File file)
	{
		
	}
	
	public void loadInfo(File file)
	{
		
	}
	
	@Override
	public int getTextureID() 
	{
		return textureId;
	}
	
	@Override
	public void setTextureID(int id)
	{
		textureId = id;
	}
	
}
