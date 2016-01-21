package com.polaris.engine.render;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.ARBFramebufferObject;
import org.lwjgl.opengl.ARBTextureStorage;
import org.lwjgl.opengl.GL11;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.glfw.GLFW.*;
import static java.lang.Math.*;
import static com.polaris.engine.util.Helper.*;

import com.polaris.engine.util.Color4d;
import com.polaris.engine.util.Helper;


public class Renderer 
{

	private static int windowWidth = 0;
	private static int windowHeight = 0;
	private static Color4d currentColor = new Color4d(1, 1, 1, 1);

	private static Map<String, VirtualTexture> virtualMap = new HashMap<String, VirtualTexture>();
	private static Map<String, StitchedMap> textureMap = new HashMap<String, StitchedMap>();
	private static Map<String, Model> modelMap = new HashMap<String, Model>();
	private static ITexture currentTexture = null;
	private static File modelDirectory = null;
	private static File textureDirectory = null;

	public static void updateSize(long windowInstance) 
	{
		IntBuffer width = IntBuffer.allocate(1);
		IntBuffer height = IntBuffer.allocate(1);
		glfwGetFramebufferSize(windowInstance, width, height);
		windowWidth = width.get();
		windowHeight = height.get();
	}

	public static void initializeContent(String location) throws IOException
	{
		textureDirectory = new File(Helper.getResource(location).getFile() + "/textures");
		modelDirectory = new File(Helper.getResource(location).getFile() + "/models");
		for(File file : textureDirectory.listFiles())
		{
			if(file.isDirectory() && !Helper.fileStartsWith(file, "stitched", "models"))
			{
				loadStitchMaps(file, !file.getName().contains("$NOLOAD$"));
			}
		}
	}

	private static void loadStitchMaps(File stitchDirectory, boolean load) throws IOException
	{
		for(File subDirectory : stitchDirectory.listFiles())
		{
			if(subDirectory.isDirectory())
			{
				loadStitchMaps(subDirectory, !subDirectory.getName().contains("$NOLOAD$"));
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
			textureMap.put(title, (FontMap)texture);
		}
		else
			texture = new TextureMap();
		BufferedImage savedImage = texture.genTextureMap(stitchTextures, new File(stitchDirectory, "animation.ani"));

		if(load)
		{
			texture.setTextureID(Renderer.createTextureId(savedImage, false));
			textureMap.put(title, texture);
		}

		ImageIO.write(savedImage, "PNG", new File(textureDirectory, "stitched/" + title + ".png"));
		texture.genInfo(new File(textureDirectory, "stitched/" + title + ".info"));
	}

	public static void clearTextureMap()
	{
		for(String textureTitle : textureMap.keySet())
		{
			if(textureMap.get(textureTitle) instanceof TextureMap)
			{
				glDeleteTextures(textureMap.get(textureTitle).getTextureID());
				textureMap.remove(textureTitle);
			}
		}
	}

	public static void clearFontMap()
	{
		for(String fontTitle : textureMap.keySet())
		{
			if(textureMap.get(fontTitle) instanceof FontMap)
			{
				glDeleteTextures(textureMap.get(fontTitle).getTextureID());
				textureMap.remove(fontTitle);
			}
		}
	}

	public static void clearVirtualMap()
	{
		for(String virtualTitle : virtualMap.keySet())
		{
			glDeleteTextures(virtualMap.get(virtualTitle).getTextureID());
		}
		virtualMap.clear();
	}

	public static void loadTextureMap(String texture) throws IOException
	{
		File textureMapFile = new File(textureDirectory, "stitched/" + texture + ".png");
		File textureMapInfo = new File(textureDirectory, "stitched/" + texture + ".info");
		if(textureMapFile.isFile() && textureMapInfo.isFile())
		{
			if(!textureMap.containsKey(texture))
			{
				TextureMap stitchedTexture = new TextureMap();
				stitchedTexture.loadInfo(textureMapInfo);
				stitchedTexture.setTextureID(Renderer.createTextureId(ImageIO.read(textureMapFile), false));
				textureMap.put(texture, stitchedTexture);
			}
		}
	}
	
	public static void loadModel(String model) throws IOException
	{
		String convertedModel = model.replace(":", "/");
		File textureFile = new File(textureDirectory, "models/" + convertedModel.substring(0, model.lastIndexOf('.')) + ".png");
		File modelFile = new File(modelDirectory, convertedModel);
		if(textureFile.isFile() && modelFile.isFile())
		{
			if(!modelMap.containsKey(model))
			{
				Model loadedModel = loadModel(model.substring(model.lastIndexOf('.')), modelFile);
				loadedModel.setTextureID(Renderer.createTextureId(ImageIO.read(textureFile), false));
				modelMap.put(model, loadedModel);
			}
		}
	}
	
	private static Model loadModel(String endingTitle, File location)
	{
		Model model = null;
		if(Helper.modelFormats.containsKey(endingTitle))
		{
			try 
			{
				model = Helper.modelFormats.get(endingTitle).newInstance(location);
			} 
			catch (InstantiationException | IllegalAccessException
					| IllegalArgumentException | InvocationTargetException e) 
			{
				e.printStackTrace();
			}
		}
		return model;
	}
	
	public static void glBindTexture(int textureId)
	{
		currentTexture = null;
		GL11.glBindTexture(GL_TEXTURE_2D, textureId);
	}

	public static boolean glBindTexture(String texture) 
	{
		if(texture.startsWith("$"))
		{
			ITexture textureId = getTextureId(texture.substring(1));
			if(textureId != null && textureId != currentTexture)
			{
				GL11.glBindTexture(GL_TEXTURE_2D, textureId.getTextureID());
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

	public static boolean glClearTexture(String texture)
	{
		ITexture tex = textureMap.get(texture);
		if(tex != null)
			textureMap.remove(texture);
		else
		{
			tex = virtualMap.get(texture);
			if(tex != null)
			{
				virtualMap.remove(texture);
			}
		}
		if(tex != null)
		{
			if(tex == currentTexture)
				currentTexture = null;
			glDeleteTextures(tex.getTextureID());
			return true;
		}
		return false;
	}

	public static ITexture getTextureId()
	{
		return currentTexture;
	}

	public static ITexture getTextureId(String texture)
	{
		ITexture tex = textureMap.get(texture);
		if(tex != null)
			return tex;
		tex = virtualMap.get(texture);
		return tex;
	}

	public static Texture getTexture()
	{
		return currentTexture.getTexture();
	}

	public static Texture getTexture(String textureName)
	{
		return currentTexture.getTexture(textureName);
	}

	public static int createTextureId(BufferedImage image, boolean mipmap)
	{
		if(image != null)
		{
			int texture = glGenTextures();
			ByteBuffer buffer = BufferUtils.createByteBuffer(image.getWidth() * image.getHeight() * 4);

			for(int y = 0; y < image.getHeight(); y++)
			{
				for(int x = 0; x < image.getWidth(); x++)
				{
					int pixel = image.getRGB(x, y);
					buffer.put((byte) ((pixel >> 16) & 0xFF));
					buffer.put((byte) ((pixel >> 8) & 0xFF));
					buffer.put((byte) (pixel & 0xFF));
					buffer.put((byte) ((pixel >> 24) & 0xFF));
				}
			}

			buffer.flip();
			GL11.glBindTexture(GL_TEXTURE_2D, texture);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
			if(mipmap)
			{
				ARBTextureStorage.glTexStorage2D(GL_TEXTURE_2D, 5, GL_RGBA8, image.getWidth(), image.getHeight());
				glTexSubImage2D(GL_TEXTURE_2D, 0, 0, 0, image.getWidth(), image.getHeight(), GL_RGBA, GL_UNSIGNED_BYTE, buffer);
				ARBFramebufferObject.glGenerateMipmap(GL_TEXTURE_2D);
				glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
				glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
			}
			else
			{
				ARBTextureStorage.glTexStorage2D(GL_TEXTURE_2D, 1, GL_RGBA8, image.getWidth(), image.getHeight());
				glTexSubImage2D(GL_TEXTURE_2D, 0, 0, 0, image.getWidth(), image.getHeight(), GL_RGBA, GL_UNSIGNED_BYTE, buffer);
				glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
				glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
			}
			return texture;
		}
		return -100;
	}

	public static void gl2d()
	{
		glViewport(0, 0, windowWidth, windowHeight);
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		glOrtho(0, 1280, 720, 0, -1, 1);
		glMatrixMode(GL_MODELVIEW);
		glLoadIdentity();
	}

	public static void gl3d()
	{
		glViewport(0, 0, windowWidth, windowHeight);
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		glOrtho(0, windowWidth, windowHeight, 0, .1f, 1000f);
		glMatrixMode(GL_MODELVIEW);
		glLoadIdentity();
	}

	public static void glEnableText()
	{
		glEnable(GL_TEXTURE_2D);
		glBlend();
	}

	public static void glBlend()
	{
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
	}

	public static void glBlend(int value, int value1)
	{
		glEnable(GL_BLEND);
		glBlendFunc(value, value1);
	}

	public static void glBegin()
	{
		GL11.glBegin(GL_QUADS);
	}

	public static void glClearBuffers()
	{
		GL11.glClear(GL_DEPTH_BUFFER_BIT | GL_COLOR_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);
	}

	public static void glDefaults()
	{
		glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);
		glShadeModel(GL_SMOOTH);
		glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		glClearDepth(1.0f);
		glClearStencil(0);
		glEnable(GL_ALPHA_TEST);
		glAlphaFunc(GL_GREATER, .01f);
		glEnable(GL_DEPTH_TEST);
		glDepthFunc(GL_LEQUAL);
		glDisable(GL_DITHER);

		glCullFace(GL_BACK);
		glEnable(GL_CULL_FACE);

		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

		glPixelStorei(GL_UNPACK_ALIGNMENT, 1);

		glColor3f(1.0f, 1.0f, 1.0f);
	}


	public static void glColor(double r, double g, double b)
	{
		glColor4d(r, g, b, 1);
		getColor().setColor(r, g, b, 1);
	}

	public static void glColor(double r, double g, double b, double a)
	{
		glColor4d(r, g, b, a);
		getColor().setColor(r, g, b, a);
	}

	public static void glColor(Color4d color)
	{
		glColor4d(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
		getColor().setColor(color);
	}

	public static Color4d getColor()
	{
		return currentColor;
	}

	public static double getRed()
	{
		return getColor().getRed();
	}

	public static double getGreen()
	{
		return getColor().getGreen();
	}

	public static double getBlue()
	{
		return getColor().getBlue();
	}

	public static double getAlpha()
	{
		return getColor().getAlpha();
	}

	public static void colorVertex(double x, double y, double z, Color4d color)
	{
		glColor(color);
		glVertex3d(x, y, z);
	}

	public static void colorVertex(double x, double y, double z, double r, double g, double b, double a)
	{
		glColor(r, g, b);
		glVertex3d(x, y, z);
	}

	public static void vertexUV(double x, double y, double z, double u, double v)
	{
		glTexCoord2d(u, v);
		glVertex3d(x, y, z);
	}

	public static void colorVertexUV(double x, double y, double z, double u, double v, Color4d color)
	{
		glColor(color);
		vertexUV(x, y, z, u, v);
	}

	public static void colorVertexUV(double x, double y, double z, double u, double v, double r, double g, double b, double a)
	{
		glColor(r, g, b, a);
		vertexUV(x, y, z, u, v);
	}

	public static void vertexUV(double x, double y, double z, double u, double v, Texture texture)
	{
		glTexCoord2d((texture.getMaxU() - texture.getMinU()) * u + texture.getMinU(), (texture.getMaxV() - texture.getMinV()) * v + texture.getMinV());
		glVertex3d(x, y, z);
	}

	public static void colorVertexUV(double x, double y, double z, double u, double v, Texture texture, Color4d color)
	{
		glColor(color);
		vertexUV(x, y, z, u, v, texture);
	}

	public static void colorVertexUV(double x, double y, double z, double u, double v, Texture texture, double r, double g, double b, double a)
	{
		glColor(r, g, b, a);
		vertexUV(x, y, z, u, v, texture);
	}

	public static void vertexUV(double x, double y, double z, double u, double v, Texture texture, int animationID)
	{
		glTexCoord2d((texture.getMaxU(animationID) - texture.getMinU(animationID)) * u + texture.getMinU(animationID), (texture.getMaxV(animationID) - texture.getMinV(animationID)) * v + texture.getMinV(animationID));
		glVertex3d(x, y, z);
	}

	public static void colorVertexUV(double x, double y, double z, double u, double v, Texture texture, int animationID, Color4d color)
	{
		glColor(color);
		vertexUV(x, y, z, u, v, texture, animationID);
	}

	public static void colorVertexUV(double x, double y, double z, double u, double v, Texture texture, int animationID, double r, double g, double b, double a)
	{
		glColor(r, g, b, a);
		vertexUV(x, y, z, u, v, texture, animationID);
	}

	public static void drawRect(double x, double y, double x1, double y1, double z, double thickness)
	{
		drawRect(x, y1 - thickness, x1, y1, z);
		drawRect(x1 - thickness, y, x1, y1 - thickness, z);
		drawRect(x, y, x1 - thickness, y + thickness, z);
		drawRect(x, y + thickness, x + thickness, y1 - thickness, z);
	}

	public static void drawRect(double x, double y, double x1, double y1, double z, double thickness, Color4d innerColor)
	{
		Color4d outerColor = new Color4d(getColor());

		glVertex2d(x1, y);
		glVertex2d(x, y);
		colorVertex(x + thickness, y + thickness, z, innerColor);
		glVertex2d(x1 - thickness, y + thickness);

		glVertex2d(x1 - thickness, y + thickness);
		glVertex2d(x1 - thickness, y1 - thickness);
		colorVertex(x1, y1, z, outerColor);
		glVertex2d(x1, y);

		glVertex2d(x, y1);
		glVertex2d(x1, y1);
		colorVertex(x1 - thickness, y1 - thickness, z, innerColor);
		glVertex2d(x + thickness, y1 - thickness);

		glVertex2d(x + thickness, y1 - thickness);
		glVertex2d(x + thickness, y + thickness);
		colorVertex(x, y, z, outerColor);
		glVertex2d(x, y1);
	}

	public static void drawRect(double x, double y, double x1, double y1, double z)
	{
		glVertex3d(x, y1, z);
		glVertex3d(x1, y1, z);
		glVertex3d(x1, y, z);
		glVertex3d(x, y, z);
	}

	public static void drawRect(double x, double y, double x1, double y1, double z, Color4d color0, Color4d color1, Color4d color2, Color4d color3)
	{
		colorVertex(x, y1, z, color0);
		colorVertex(x1, y1, z, color1);
		colorVertex(x1, y, z, color2);
		colorVertex(x, y, z, color3);
	}

	public static void drawRectUV(double x, double y, double x1, double y1, double z, double u, double v, double u1, double v1)
	{
		vertexUV(x, y1, z, u, v1);
		vertexUV(x1, y1, z, u1, v1);
		vertexUV(x1, y, z, u1, v);
		vertexUV(x, y, z, u, v);
	}

	public static void drawRectUV(double x, double y, double x1, double y1, double z)
	{
		Texture texture = getTexture();
		vertexUV(x, y, z, texture.getMinU(), texture.getMinV());
		vertexUV(x, y1, z, texture.getMinU(), texture.getMaxV());
		vertexUV(x1, y1, z, texture.getMaxU(), texture.getMaxV());
		vertexUV(x1, y, z, texture.getMaxU(), texture.getMinV());
	}

	public static void drawRectUV(double x, double y, double x1, double y1, double z, int animationID)
	{
		Texture texture = getTexture();
		vertexUV(x, y, z, texture.getMinU(animationID), texture.getMinV(animationID));
		vertexUV(x, y1, z, texture.getMinU(animationID), texture.getMaxV(animationID));
		vertexUV(x1, y1, z, texture.getMaxU(animationID), texture.getMaxV(animationID));
		vertexUV(x1, y, z, texture.getMaxU(animationID), texture.getMinV(animationID));
	}

	public static void drawRectUV(double x, double y, double x1, double y1, double z, Texture texture)
	{
		vertexUV(x, y, z, texture.getMinU(), texture.getMinV());
		vertexUV(x, y1, z, texture.getMinU(), texture.getMaxV());
		vertexUV(x1, y1, z, texture.getMaxU(), texture.getMaxV());
		vertexUV(x1, y, z, texture.getMaxU(), texture.getMinV());
	}

	public static void drawRectUV(double x, double y, double x1, double y1, double z, Texture texture, int animationID)
	{
		float u = texture.getMinU(animationID);
		float u1 = texture.getMaxU(animationID);
		float v = texture.getMinV(animationID);
		float v1 = texture.getMaxV(animationID);
		vertexUV(x, y, z, u, v);
		vertexUV(x, y1, z, u, v1);
		vertexUV(x1, y1, z, u1, v1);
		vertexUV(x1, y, z, u1, v);
	}

	public static void drawColorHRectUV(double x, double y, double x1, double y1, double z, double u, double v, double u1, double v1, Color4d shiftToColor)
	{
		vertexUV(x, y, z, u, v);
		vertexUV(x, y1, z, u, v1);
		colorVertexUV(x1, y1, z, u1, v1, shiftToColor);
		vertexUV(x1, y, z, u1, v);
	}

	public static void drawColorHRectUV(double x, double y, double x1, double y1, double z, Color4d shiftToColor)
	{
		Texture texture = getTexture();
		vertexUV(x, y, z, texture.getMinU(), texture.getMinV());
		vertexUV(x, y1, z, texture.getMinU(), texture.getMaxV());
		colorVertexUV(x1, y1, z, texture.getMaxU(), texture.getMaxV(), shiftToColor);
		vertexUV(x1, y, z, texture.getMaxU(), texture.getMinV());
	}

	public static void drawColorHRectUV(double x, double y, double x1, double y1, double z, int animationID, Color4d shiftToColor)
	{
		Texture texture = getTexture();
		float u = texture.getMinU(animationID);
		float u1 = texture.getMaxU(animationID);
		float v = texture.getMinV(animationID);
		float v1 = texture.getMaxV(animationID);
		vertexUV(x, y, z, u, v);
		vertexUV(x, y1, z, u, v1);
		colorVertexUV(x1, y1, z, u1, v1, shiftToColor);
		vertexUV(x1, y, z, u1, v);
	}

	public static void drawColorHRectUV(double x, double y, double x1, double y1, double z, Texture texture, Color4d shiftToColor)
	{
		vertexUV(x, y, z, texture.getMinU(), texture.getMinV());
		vertexUV(x, y1, z, texture.getMinU(), texture.getMaxV());
		colorVertexUV(x1, y1, z, texture.getMaxU(), texture.getMaxV(), shiftToColor);
		vertexUV(x1, y, z, texture.getMaxU(), texture.getMinV());
	}

	public static void drawColorHRectUV(double x, double y, double x1, double y1, double z, Texture texture, int animationID, Color4d shiftToColor)
	{
		float u = texture.getMinU(animationID);
		float u1 = texture.getMaxU(animationID);
		float v = texture.getMinV(animationID);
		float v1 = texture.getMaxV(animationID);
		vertexUV(x, y, z, u, v);
		vertexUV(x, y1, z, u, v1);
		colorVertexUV(x1, y1, z, u1, v1, shiftToColor);
		vertexUV(x1, y, z, u1, v);
	}

	public static void drawColorHRectUV(double x, double y, double x1, double y1, double z, double u, double v, double u1, double v1, double r, double g, double b, double a)
	{
		vertexUV(x, y, z, u, v);
		vertexUV(x, y1, z, u, v1);
		colorVertexUV(x1, y1, z, u1, v1, r, g, b, a);
		vertexUV(x1, y, z, u1, v);
	}

	public static void drawColorHRectUV(double x, double y, double x1, double y1, double z, double r, double g, double b, double a)
	{
		Texture texture = getTexture();
		vertexUV(x, y, z, texture.getMinU(), texture.getMinV());
		vertexUV(x, y1, z, texture.getMinU(), texture.getMaxV());
		colorVertexUV(x1, y1, z, texture.getMaxU(), texture.getMaxV(), r, g, b, a);
		vertexUV(x1, y, z, texture.getMaxU(), texture.getMinV());
	}

	public static void drawColorHRectUV(double x, double y, double x1, double y1, double z, int animationID, double r, double g, double b, double a)
	{
		Texture texture = getTexture();
		float u = texture.getMinU(animationID);
		float u1 = texture.getMaxU(animationID);
		float v = texture.getMinV(animationID);
		float v1 = texture.getMaxV(animationID);
		vertexUV(x, y, z, u, v);
		vertexUV(x, y1, z, u, v1);
		colorVertexUV(x1, y1, z, u1, v1, r, g, b, a);
		vertexUV(x1, y, z, u1, v);
	}

	public static void drawColorHRectUV(double x, double y, double x1, double y1, double z, Texture texture, double r, double g, double b, double a)
	{
		vertexUV(x, y, z, texture.getMinU(), texture.getMinV());
		vertexUV(x, y1, z, texture.getMinU(), texture.getMaxV());
		colorVertexUV(x1, y1, z, texture.getMaxU(), texture.getMaxV(), r, g, b, a);
		vertexUV(x1, y, z, texture.getMaxU(), texture.getMinV());
	}

	public static void drawColorHRectUV(double x, double y, double x1, double y1, double z, Texture texture, int animationID, double r, double g, double b, double a)
	{
		float u = texture.getMinU(animationID);
		float u1 = texture.getMaxU(animationID);
		float v = texture.getMinV(animationID);
		float v1 = texture.getMaxV(animationID);
		vertexUV(x, y, z, u, v);
		vertexUV(x, y1, z, u, v1);
		colorVertexUV(x1, y1, z, u1, v1, r, g, b, a);
		vertexUV(x1, y, z, u1, v);
	}

	public static void drawColorVRectUV(double x, double y, double x1, double y1, double z, double u, double v, double u1, double v1, Color4d shiftToColor)
	{
		vertexUV(x1, y, z, u1, v);
		vertexUV(x, y, z, u, v);
		colorVertexUV(x, y1, z, u, v1, shiftToColor);
		vertexUV(x1, y1, z, u1, v1);
	}

	public static void drawColorVRectUV(double x, double y, double x1, double y1, double z, Color4d shiftToColor)
	{
		Texture texture = getTexture();
		vertexUV(x1, y, z, texture.getMaxU(), texture.getMinV());
		vertexUV(x, y, z, texture.getMinU(), texture.getMinV());
		colorVertexUV(x, y1, z, texture.getMinU(), texture.getMaxV(), shiftToColor);
		vertexUV(x1, y1, z, texture.getMaxU(), texture.getMaxV());
	}

	public static void drawColorVRectUV(double x, double y, double x1, double y1, double z, int animationID, Color4d shiftToColor)
	{
		Texture texture = getTexture();
		float u = texture.getMinU(animationID);
		float u1 = texture.getMaxU(animationID);
		float v = texture.getMinV(animationID);
		float v1 = texture.getMaxV(animationID);
		vertexUV(x1, y, z, u1, v);
		vertexUV(x, y, z, u, v);
		colorVertexUV(x, y1, z, u, v1, shiftToColor);
		vertexUV(x1, y1, z, u1, v1);
	}

	public static void drawColorVRectUV(double x, double y, double x1, double y1, double z, Texture texture, Color4d shiftToColor)
	{
		vertexUV(x1, y, z, texture.getMaxU(), texture.getMinV());
		vertexUV(x, y, z, texture.getMinU(), texture.getMinV());
		colorVertexUV(x, y1, z, texture.getMinU(), texture.getMaxV(), shiftToColor);
		vertexUV(x1, y1, z, texture.getMaxU(), texture.getMaxV());
	}

	public static void drawColorVRectUV(double x, double y, double x1, double y1, double z, Texture texture, int animationID, Color4d shiftToColor)
	{
		float u = texture.getMinU(animationID);
		float u1 = texture.getMaxU(animationID);
		float v = texture.getMinV(animationID);
		float v1 = texture.getMaxV(animationID);
		vertexUV(x1, y, z, u1, v);
		vertexUV(x, y, z, u, v);
		colorVertexUV(x, y1, z, u, v1, shiftToColor);
		vertexUV(x1, y1, z, u1, v1);
	}

	public static void drawColorVRectUV(double x, double y, double x1, double y1, double z, double u, double v, double u1, double v1, double r, double g, double b, double a)
	{
		vertexUV(x1, y, z, u1, v);
		vertexUV(x, y, z, u, v);
		colorVertexUV(x, y1, z, u, v1, r, g, b, a);
		vertexUV(x1, y1, z, u1, v1);
	}

	public static void drawColorVRectUV(double x, double y, double x1, double y1, double z, double r, double g, double b, double a)
	{
		Texture texture = getTexture();
		vertexUV(x1, y, z, texture.getMaxU(), texture.getMinV());
		vertexUV(x, y, z, texture.getMinU(), texture.getMinV());
		colorVertexUV(x, y1, z, texture.getMinU(), texture.getMaxV(), r, g, b, a);
		vertexUV(x1, y1, z, texture.getMaxU(), texture.getMaxV());
	}

	public static void drawColorVRectUV(double x, double y, double x1, double y1, double z, int animationID, double r, double g, double b, double a)
	{
		Texture texture = getTexture();
		float u = texture.getMinU(animationID);
		float u1 = texture.getMaxU(animationID);
		float v = texture.getMinV(animationID);
		float v1 = texture.getMaxV(animationID);
		vertexUV(x1, y, z, u1, v);
		vertexUV(x, y, z, u, v);
		colorVertexUV(x, y1, z, u, v1, r, g, b, a);
		vertexUV(x1, y1, z, u1, v1);
	}

	public static void drawColorVRectUV(double x, double y, double x1, double y1, double z, Texture texture, double r, double g, double b, double a)
	{
		vertexUV(x1, y, z, texture.getMaxU(), texture.getMinV());
		vertexUV(x, y, z, texture.getMinU(), texture.getMinV());
		colorVertexUV(x, y1, z, texture.getMinU(), texture.getMaxV(), r, g, b, a);
		vertexUV(x1, y1, z, texture.getMaxU(), texture.getMaxV());
	}

	public static void drawColorVRectUV(double x, double y, double x1, double y1, double z, Texture texture, int animationID, double r, double g, double b, double a)
	{
		float u = texture.getMinU(animationID);
		float u1 = texture.getMaxU(animationID);
		float v = texture.getMinV(animationID);
		float v1 = texture.getMaxV(animationID);
		vertexUV(x1, y, z, u1, v);
		vertexUV(x, y, z, u, v);
		colorVertexUV(x, y1, z, u, v1, r, g, b, a);
		vertexUV(x1, y1, z, u1, v1);
	}

	public static void drawArc(double circleX, double circleY, double z, double radius, int lineCount, double thickness)
	{
		drawArc(circleX, circleY, z, radius, 0, TWOPI, lineCount, thickness);
	}

	public static void drawArc(double circleX, double circleY, double z, double radius, double startAngle, double endAngle, int lineCount, double thickness)
	{
		double theta = (endAngle - startAngle) / (lineCount);
		double x = radius * cos(startAngle);
		double y = radius * sin(startAngle);
		for(int i = 0; i < lineCount; i++)
		{
			glVertex3d(circleX + x, circleY + y, z);
			glVertex3d(circleX + (radius - thickness) * cos(startAngle), circleY + (radius - thickness) * sin(startAngle), z);
			startAngle += theta;
			x = radius * cos(startAngle);
			y = radius * sin(startAngle);
			glVertex3d(circleX + (radius - thickness) * cos(startAngle), circleY + (radius - thickness) * sin(startAngle), z);
			glVertex3d(circleX + x, circleY + y, z);
		}
	}

	public static void drawArc(double circleX, double circleY, double z, double radius, int lineCount, double thickness, Color4d endColor)
	{
		drawArc(circleX, circleY, z, radius, 0, TWOPI, lineCount, thickness, endColor);
	}

	public static void drawArc(double circleX, double circleY, double z, double radius, double angle0, double angle, int lineCount, double thickness, Color4d endColor)
	{
		double deltaTheta = (angle - angle0) / lineCount;
		double x = radius * cos(angle0);
		double y = radius * sin(angle0);
		double rShift = (endColor.getRed() - getRed()) / lineCount;
		double gShift = (endColor.getGreen() - getGreen()) / lineCount;
		double bShift = (endColor.getBlue() - getBlue()) / lineCount;
		double aShift = (endColor.getAlpha() - getAlpha()) / lineCount;
		for(int i = 0; i < lineCount; i++)
		{
			glVertex3d(circleX + x, circleY + y, z);
			glVertex3d(circleX + (radius - thickness) * cos(angle0), circleY + (radius - thickness) * sin(angle0), z);
			angle0 += deltaTheta;
			x = radius * cos(angle0);
			y = radius * sin(angle0);
			glColor(getRed() + rShift, getGreen() + gShift, getBlue() + bShift, getAlpha() + aShift);
			glVertex3d(circleX + (radius - thickness) * cos(angle0), circleY + (radius - thickness) * sin(angle0), z);
			glVertex3d(circleX + x, circleY + y, z);
		}
		glColor(getRed() - rShift * lineCount, getGreen() - gShift * lineCount, getBlue() - bShift * lineCount, getAlpha() - aShift * lineCount);
	}

	public static void drawCircle(double circleX, double circleY, double z, double radius, int lineCount)
	{
		drawCircle(circleX, circleY, z, radius, lineCount, 0, TWOPI);
	}

	public static void drawCircle(double circleX, double circleY, double z, double radius, int lineCount, double angle0, double angle)
	{
		double deltaTheta = (angle - angle0) / lineCount;
		GL11.glBegin(GL_TRIANGLE_FAN);
		glVertex3d(circleX, circleY, z); 
		for(int i = 0; i <= lineCount; i++) 
		{ 
			glVertex3d(circleX + (radius * cos((angle -= deltaTheta))), circleY + (radius * sin(angle)), z);
		}
		glEnd();
	}

	public static void drawCircle(double circleX, double circleY, double z, double radius, int lineCount, Color4d innerColor)
	{
		drawCircle(circleX, circleY, z, radius, lineCount, 0, TWOPI, innerColor);
	}

	public static void drawCircle(double circleX, double circleY, double z, double radius, int lineCount, double angle0, double angle, Color4d innerColor)
	{
		double deltaTheta = (angle - angle0) / lineCount;
		Color4d color = new Color4d(getColor());
		GL11.glBegin(GL_TRIANGLE_FAN);
		glColor(innerColor);
		glVertex3d(circleX, circleY, z);
		glColor(color);
		for(int i = 0; i <= lineCount; i++)
		{
			glVertex3d(circleX + radius * cos((angle -= deltaTheta)), circleY + radius * sin(angle), z);
		}
		glEnd();
	}

	public static void drawCircle(double circleX, double circleY, double z, double radius, double u, double v, double u1, double v1, int lineCount)
	{
		drawCircle(circleX, circleY, z, radius, u, v, u1, v1, getTexture(), lineCount, 0, TWOPI);
	}

	public static void drawCircle(double circleX, double circleY, double z, double radius, double u, double v, double u1, double v1, int animationID, int lineCount)
	{
		drawCircle(circleX, circleY, z, radius, u, v, u1, v1, getTexture(), animationID, lineCount, 0, TWOPI);
	}

	public static void drawCircle(double circleX, double circleY, double z, double radius, double u, double v, double u1, double v1, Texture texture, int lineCount)
	{
		drawCircle(circleX, circleY, z, radius, u, v, u1, v1, texture, lineCount, 0, TWOPI);
	}

	public static void drawCircle(double circleX, double circleY, double z, double radius, double u, double v, double u1, double v1, Texture texture, int animationID, int lineCount)
	{
		drawCircle(circleX, circleY, z, radius, u, v, u1, v1, texture, animationID, lineCount, 0, TWOPI);
	}

	public static void drawCircle(double circleX, double circleY, double z, double radius, double u, double v, double u1, double v1, Texture texture, int lineCount, double angle0, double angle)
	{
		double deltaTheta = (angle - angle0) / lineCount;
		double difU = (u1 - u) / 2;
		double difV = (v1 - v) / 2;
		double centerU = u + difU;
		double centerV = v + difV;
		GL11.glBegin(GL_TRIANGLE_FAN);
		vertexUV(circleX, circleY, z, centerU, centerV);
		for(int i = 0; i <= lineCount; i++)
		{
			vertexUV(circleX + radius * cos((angle -= deltaTheta)), circleY + radius * sin(angle), z, centerU + difU * cos(angle), centerV + difV * sin(angle), texture);
		}
		glEnd();
	}

	public static void drawCircle(double circleX, double circleY, double z, double radius, double u, double v, double u1, double v1, Texture texture, int animationID, int lineCount, double angle0, double angle)
	{
		double deltaTheta = (angle - angle0) / lineCount;
		double difU = (u1 - u) / 2;
		double difV = (v1 - v) / 2;
		double centerU = u + difU;
		double centerV = v + difV;
		GL11.glBegin(GL_TRIANGLE_FAN);
		vertexUV(circleX, circleY, z, centerU, centerV);
		for(int i = 0; i <= lineCount; i++)
		{
			vertexUV(circleX + radius * cos((angle -= deltaTheta)), circleY + radius * sin(angle), z, centerU + difU * cos(angle), centerV + difV * sin(angle), texture, animationID);
		}
		glEnd();
	}

}
