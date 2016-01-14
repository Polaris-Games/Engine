package com.polaris.engine.render;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBindTexture;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import org.lwjgl.opengl.GL11;

import com.polaris.engine.util.Helper;

public class TextureManager 
{

	private Map<String, VirtualTexture> virtualMap = new HashMap<String, VirtualTexture>();
	private Map<String, StitchedMap> stitchedMap = new HashMap<String, StitchedMap>();
	private ITexture currentTexture = null;
	private File textureDirectory = null;

	public TextureManager(String location, FontManager fontManager, ModelManager modelManager) throws IOException
	{
		textureDirectory = new File(Helper.getResource(location).getFile() + "/textures");
		for(File file : textureDirectory.listFiles())
		{
			if(file.isDirectory() && !Helper.fileStartsWith(file, "stitched", "models"))
			{
				loadStitchMaps(file, !file.getName().contains("$NOLOAD$"), fontManager, modelManager);
			}
		}
	}

	private void loadStitchMaps(File stitchDirectory, boolean load, FontManager fontManager, ModelManager modelManager) throws IOException
	{
		for(File subDirectory : stitchDirectory.listFiles())
		{
			if(subDirectory.isDirectory())
			{
				loadStitchMaps(subDirectory, !subDirectory.getName().contains("$NOLOAD$"), fontManager, modelManager);
			}
		}
		List<File> stitchTextures = new ArrayList<File>();
		for(File stitchTexture : stitchDirectory.listFiles())
		{
			if(stitchTexture.isFile() && stitchTexture.getName().endsWith(".png"))
			{
				stitchTextures.add(stitchTexture);
			}
		}
		
		StitchedMap texture = null;
		String title = stitchDirectory.getPath().substring(textureDirectory.getPath().length() + 1).replace("/", ":");
		if(title.startsWith("fonts:"))
		{
			texture = new FontMap();
			fontManager.addFont(title.substring(title.indexOf(":")), (FontMap) texture);
		}
		else
			texture = new TextureMap();
		BufferedImage savedImage = texture.genTextureMap(stitchTextures, new File(stitchDirectory, "animation.ani"));
		
		if(load)
		{
			texture.setTextureID(Renderer.createTextureId(savedImage, false));
			stitchedMap.put(title, texture);
		}
		
		ImageIO.write(savedImage, "PNG", new File(textureDirectory, "stitched/" + title + ".png"));
		texture.genInfo(new File(textureDirectory, "stitched/" + title + ".info"));
	}

	public void clearTextureMaps()
	{
		for(String textureTitle : stitchedMap.keySet())
		{
			clearTexture(textureTitle);
		}
	}

	public void loadTextureMap(String textureMap) throws IOException
	{
		File textureMapFile = new File(textureDirectory, "stitched/" + textureMap + ".png");
		File textureMapInfo = new File(textureDirectory, "stitched/" + textureMap + ".info");
		if(textureMapFile.exists() && textureMapFile.isFile() && textureMapInfo.exists() && textureMapInfo.isFile())
		{
			String title = textureMapFile.getName().split("/.")[0];
			if(!stitchedMap.containsKey(title))
			{
				TextureMap texture = new TextureMap();
				texture.loadInfo(textureMapInfo);
				texture.setTextureID(Renderer.createTextureId(ImageIO.read(textureMapFile), false));
				stitchedMap.put(textureMap, texture);
			}
		}
	}

	public boolean bindTexture(String texture) 
	{
		if(texture.startsWith("$"))
		{
			ITexture textureId = getTextureId(texture.substring(1));
			if(textureId != null && textureId != currentTexture)
			{
				glBindTexture(GL_TEXTURE_2D, textureId.getTextureID());
				currentTexture = textureId;
				return true;
			}
		}
		else if(currentTexture instanceof TextureMap)
		{
			TextureMap map = (TextureMap) currentTexture;
			return map.bindTexture(texture);
		}
		return false;
	}
	
	public boolean clearTexture(String texture)
	{
		if(stitchedMap.containsKey(texture))
		{
			GL11.glDeleteTextures(stitchedMap.get(texture).getTextureID());
			stitchedMap.remove(texture);
			return true;
		}
		else if(virtualMap.containsKey(texture))
		{
			GL11.glDeleteTextures(virtualMap.get(texture).getTextureID());
			virtualMap.remove(texture);
			return true;
		}
		return false;
	}

	public ITexture getTextureId(String texture)
	{
		return stitchedMap.containsKey(texture) ? stitchedMap.get(texture) : virtualMap.containsKey(texture) ? virtualMap.get(texture) : null;
	}
	
	public Texture getTexture()
	{
		return currentTexture.getTexture();
	}
	
	public Texture getTexture(String textureName)
	{
		return currentTexture.getTexture(textureName);
	}

}
