package com.polaris.engine.render;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import com.polaris.engine.util.Helper;

public abstract class StitchedMap implements ITexture
{
	
	private int textureId = 0;

	public BufferedImage genTextureMap(List<File> stitchTextures, File fileLocation) throws IOException
	{
		Map<String, BufferedImage> imageMap = new HashMap<String, BufferedImage>();
		for(File file : stitchTextures)
		{
			BufferedImage image = ImageIO.read(file);
			if(image != null && image.getWidth() > 0 && image.getHeight() > 0)
			{
				imageMap.put(file.getName().split("/.")[0], image);
			}
		}
		
		PackedImage packedImage = new PackedImage(imageMap);
		BufferedImage finalProduct = packedImage.getImage();
		Map<String, Texture> textureMap = packedImage.getTextureMapping();

		if(fileLocation.exists() && fileLocation.isFile())
		{
			BufferedReader reader = Helper.newReader(fileLocation);
			String line = null;
			while((line = reader.readLine()) != null)
			{
				if(line.length() > 0 && line.contains(":"))
				{
					String[] content = line.split(":");
					if(textureMap.containsKey(content[0]))
					{
						textureMap.put(content[0], new AnimatedTexture(textureMap.get(content[0]), Integer.parseInt(content[1]), Integer.parseInt(content[2])));
					}
				}
			}
			reader.close();
		}
		setMap(textureMap);
		return finalProduct;
	}
	
	public void genInfo(File output) throws IOException
	{
		BufferedWriter writer = Helper.newWriter(output);
		Map<String, Texture> textureMap = getMap();
		for(String textureName : textureMap.keySet())
		{
			Texture texture = textureMap.get(textureName);
			writer.append(textureName + ":" + texture.getMinU() + ":" + texture.getMinV() + ":" + texture.getMaxU() + ":" + texture.getMaxV());
			if(texture instanceof AnimatedTexture)
			{
				AnimatedTexture aniTexture = (AnimatedTexture) texture;
				writer.append(":" + aniTexture.getWidth() + ":" + aniTexture.getHeight());
			}
			writer.newLine();
		}
		writer.close();
	}

	public void loadInfo(File input) throws IOException
	{
		BufferedReader reader = Helper.newReader(input);
		String line = null;
		Map<String, Texture> textureMap = new HashMap<String, Texture>();
		while((line = reader.readLine()) != null)
		{
			if(line.length() > 0 && line.contains(":"))
			{
				String[] content = line.split(":");
				Texture texture = new Texture(Float.parseFloat(content[1]), Float.parseFloat(content[2]), Float.parseFloat(content[3]), Float.parseFloat(content[4]));
				if(content.length > 5)
				{
					texture = new AnimatedTexture(texture, Integer.parseInt(content[5]), Integer.parseInt(content[6]));
				}
				textureMap.put(content[0], texture);
			}
		}
		setMap(textureMap);
	}
	
	public boolean bindTexture(String texture) 
	{
		return false;
	}
	
	@Override
	public Texture getTexture() 
	{
		return null;
	}

	@Override
	public Texture getTexture(String textureName)
	{
		return null;
	}

	public abstract Map<String, Texture> getMap();
	public abstract void setMap(Map<String, Texture> textureMap);

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
