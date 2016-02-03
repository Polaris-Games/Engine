package com.polaris.engine.render;

import static com.polaris.engine.util.Helper.TWOPI;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static org.lwjgl.glfw.GLFW.glfwGetWindowSize;
import static org.lwjgl.opengl.GL11.*;

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

import com.polaris.engine.util.Color4d;
import com.polaris.engine.util.Helper;


public class Renderer 
{

	public static int windowWidth = 0;
	public static int windowHeight = 0;
	private static Color4d currentColor = new Color4d(1, 1, 1, 1);

	private static Map<String, ITexture> textures = new HashMap<String, ITexture>();
	private static Map<String, Model> modelMap = new HashMap<String, Model>();
	private static ITexture currentTexture = null;
	private static File modelDirectory = null;
	private static File textureDirectory = null;

	/**
	 * Called to update the windows size
	 * @param windowInstance
	 */
	public static void updateSize(long windowInstance) 
	{
		IntBuffer width = BufferUtils.createIntBuffer(1);
		IntBuffer height = BufferUtils.createIntBuffer(1);
		glfwGetWindowSize(windowInstance, width, height);
		windowWidth = width.get();
		windowHeight = height.get();
	}

	/**
	 * Called once by Application.java to create the texture, model, and font handling. NEVER CALL!
	 * @param location
	 * @throws IOException
	 */
	public static void initializeContent(String location) throws IOException
	{
		textureDirectory = new File("resources/textures");
		modelDirectory = new File("resources/models");
		for(File file : textureDirectory.listFiles())
		{
			if(file.isDirectory() && !Helper.fileStartsWith(file, "stitched", "models"))
			{
				loadTextures(file, !file.getName().startsWith("$"));
			}
		}
	}

	public static Map<String, ByteBuffer> getContent()
	{
		Map<String, ByteBuffer> buffers = new HashMap<String, ByteBuffer>();
		for(String texture : textures.keySet())
		{
			ByteBuffer data;
			ByteBuffer width = BufferUtils.createByteBuffer(4);
			ByteBuffer height = BufferUtils.createByteBuffer(4);
			ITexture tex = textures.get(texture);
			glBindTexture(tex.getTextureID());
			glGetTexLevelParameteriv(GL_TEXTURE_2D, 0, GL_TEXTURE_WIDTH, width);
			glGetTexLevelParameteriv(GL_TEXTURE_2D, 0, GL_TEXTURE_HEIGHT, height);
			data = BufferUtils.createByteBuffer(width.getInt(0) * height.getInt(0) * 4);
			glGetTexImage(GL_TEXTURE_2D, 0, GL_RGBA, GL_UNSIGNED_BYTE, data);
			buffers.put(texture, data);
		}
		return buffers;
	}

	public static void reinitContent(Map<String, ByteBuffer> data)
	{
		for(String texture : textures.keySet())
		{
			ByteBuffer width = BufferUtils.createByteBuffer(4);
			ByteBuffer height = BufferUtils.createByteBuffer(4);
			ITexture tex = textures.get(texture);
			glBindTexture(tex.getTextureID());
			glGetTexLevelParameteriv(GL_TEXTURE_2D, 0, GL_TEXTURE_WIDTH, width);
			glGetTexLevelParameteriv(GL_TEXTURE_2D, 0, GL_TEXTURE_HEIGHT, height);
			glDeleteTextures(tex.getTextureID());

			glBindTexture(tex.getTextureID());
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
			glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width.getInt(0), height.getInt(0), 0, GL_RGBA, GL_UNSIGNED_BYTE, data.get(texture));
			glTexSubImage2D(GL_TEXTURE_2D, 0, 0, 0, width.getInt(0), height.getInt(0), GL_RGBA, GL_UNSIGNED_BYTE, data.get(texture));
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		}
	}

	private static void loadTextures(File stitchDirectory, boolean load) throws IOException
	{
		for(File subDirectory : stitchDirectory.listFiles())
		{
			if(subDirectory.isDirectory())
			{
				loadTextures(subDirectory, !subDirectory.getName().startsWith("$"));
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

		if(stitchTextures.size() == 0)
			return;

		ITexture texture = null;
		String title = stitchDirectory.getPath().replace("$", "").substring(textureDirectory.getPath().length() + 1).replace("\\", ":");
		if(title.startsWith("font:"))
		{
			if(load)
			{
				texture = new FontMap();
				textures.put(title, texture);
				((FontMap) texture).setTextureID(createTextureId(((FontMap) texture).genTextureMap(stitchDirectory), false));
			}
		}
		else
		{
			texture = new StitchedMap();
			BufferedImage savedImage = ((StitchedMap)texture).genTextureMap(stitchTextures, new File(stitchDirectory, "animation.ani"));

			if(load)
			{
				texture.setTextureID(createTextureId(savedImage, false));
				textures.put(title, texture);
			}

			ImageIO.write(savedImage, "PNG", new File(textureDirectory, "stitched/" + title + ".png"));
			((StitchedMap)texture).genInfo(new File(textureDirectory, "stitched/" + title + ".info"));
		}
	}

	/**
	 * Clears all stitched maps from memory
	 */
	public static void clearTextureMap()
	{
		ITexture texture;
		for(String textureTitle : textures.keySet())
		{
			texture = textures.get(textureTitle);
			if(texture instanceof StitchedMap)
			{
				glDeleteTextures(texture.getTextureID());
				textures.remove(textureTitle);
			}
		}
	}

	/**
	 * Clears all fonts from memory
	 */
	public static void clearFontMap()
	{
		ITexture texture;
		for(String fontTitle : textures.keySet())
		{
			texture = textures.get(fontTitle);
			if(texture instanceof FontMap)
			{
				glDeleteTextures(texture.getTextureID());
				textures.remove(fontTitle);
			}
		}
	}

	/**
	 * clears all virtual textures from memory
	 */
	public static void clearVirtualMap()
	{
		ITexture texture;
		for(String virtualTitle : textures.keySet())
		{
			texture = textures.get(virtualTitle);
			if(texture instanceof VirtualTexture)
			{
				glDeleteTextures(texture.getTextureID());
				textures.remove(virtualTitle);
			}
		}
	}

	/**
	 * Loads a stitched texture map into memory
	 * @param texture
	 * @throws IOException
	 */
	public static void loadTextureMap(String texture) throws IOException
	{
		File textureMapFile = new File(textureDirectory, "stitched/" + texture + ".png");
		File textureMapInfo = new File(textureDirectory, "stitched/" + texture + ".info");
		if(textureMapFile.isFile() && textureMapInfo.isFile())
		{
			if(!textures.containsKey(texture))
			{
				StitchedMap stitchedTexture = new StitchedMap();
				stitchedTexture.loadInfo(textureMapInfo);
				stitchedTexture.setTextureID(Renderer.createTextureId(ImageIO.read(textureMapFile), false));
				textures.put(texture, stitchedTexture);
			}
		}
	}

	public static void loadFontMap(String font) throws IOException
	{
		File fontLoc = new File(textureDirectory, font.replace(":", "/"));
		File fontPNG = new File(fontLoc, "font.png");
		if(fontLoc.exists() && fontLoc.isDirectory() && fontPNG.exists() && new File(fontLoc, "font.txt").exists())
		{
			FontMap fontMap = new FontMap();
			fontMap.genTextureMap(fontLoc);
			fontMap.setTextureID(Renderer.createTextureId(ImageIO.read(fontPNG), false));
			textures.put(font, fontMap);
		}
	}

	/**
	 * Loads a model into memory
	 * @param model
	 * @throws IOException
	 */
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

	/**
	 * Binds a texture to the system for rendering, make sure to enable GL_TEXTURE_2D
	 * @param textureId
	 */
	public static void glBindTexture(int textureId)
	{
		currentTexture = null;
		GL11.glBindTexture(GL_TEXTURE_2D, textureId);
	}

	/**
	 * Binds a texture to the system for rendering, make sure to enable GL_TEXTURE_2D
	 * @param texture
	 * @return
	 */
	public static boolean glBindTexture(String texture) 
	{
		if(texture.startsWith("$"))
		{
			ITexture textureId = getTextureId(texture.substring(1));
			if(textureId != null && textureId != currentTexture)
			{
				glBindTexture(textureId.getTextureID());
				currentTexture = textureId;
				return true;
			}
		}
		else if(currentTexture instanceof StitchedMap)
		{
			StitchedMap map = (StitchedMap) currentTexture;
			return map.bindTexture(texture);
		}
		return false;
	}

	/**
	 * Removes a texture from memory
	 * @param texture
	 * @return
	 */
	public static boolean glClearTexture(String texture)
	{
		ITexture tex = textures.get(texture);
		if(tex != null)
		{
			textures.remove(texture);
			if(tex == currentTexture)
				currentTexture = null;
			glDeleteTextures(tex.getTextureID());
			return true;
		}
		return false;
	}

	/**
	 * returns the current texture bounded
	 * @return
	 */
	public static ITexture getTextureId()
	{
		return currentTexture;
	}

	public static FontMap getFontMap(String fontTitle)
	{
		fontTitle = "font:" + fontTitle;
		if(textures.containsKey(fontTitle))
			return (FontMap) textures.get(fontTitle);
		return null;
	}

	/**
	 * returns a texture
	 * @param texture
	 * @return
	 */
	public static ITexture getTextureId(String texture)
	{
		return textures.get(texture);
	}

	/**
	 * only use for texture maps, gets the current texture within the texture map
	 * @return
	 */
	public static Texture getTexture()
	{
		return currentTexture.getTexture();
	}

	/**
	 * only use for texture maps, gets a specfic texture within the texture map
	 * @param textureName
	 * @return
	 */
	public static Texture getTexture(String textureName)
	{
		if(currentTexture instanceof StitchedMap)
			return ((StitchedMap) currentTexture).getTexture(textureName);
		return null;
	}

	/**
	 * Generates a texture id int and loads the buffered image into memory
	 * @param image
	 * @param mipmap
	 * @return
	 */
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
			glBindTexture(texture);
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
				glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, image.getWidth(), image.getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);
				glTexSubImage2D(GL_TEXTURE_2D, 0, 0, 0, image.getWidth(), image.getHeight(), GL_RGBA, GL_UNSIGNED_BYTE, buffer);
				glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
				glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
			}
			return texture;
		}
		return -100;
	}

	/**
	 * Call before performing 2d rendering
	 */
	public static void gl2d()
	{
		glViewport(0, 0, windowWidth, windowHeight);
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		glOrtho(0, 1920, 1080, 0, -100, 100);
		glMatrixMode(GL_MODELVIEW);
		glLoadIdentity();
	}

	/**
	 * Call before performing 3d rendering
	 */
	public static void gl3d(final float fovy, final float zNear, final float zFar)
	{
		glViewport(0, 0, windowWidth, windowHeight);
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		double ymax = zNear * Math.tan( fovy * Math.PI / 360.0 );
		double ymin = -ymax;
		double xmin = ymin * windowWidth / windowHeight;
		double xmax = ymax * windowWidth / windowHeight;

		glFrustum( xmin, xmax, ymin, ymax, zNear, zFar );
		glMatrixMode(GL_MODELVIEW);
		glLoadIdentity();
	}

	/**
	 * Default blending function called for alpha rendering
	 */
	public static void glBlend()
	{
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
	}

	/**
	 * User inputed values for alpha rendering
	 * @param src
	 * @param output
	 */
	public static void glBlend(int src, int output)
	{
		glEnable(GL_BLEND);
		glBlendFunc(src, output);
	}

	/**
	 * begin drawing quads
	 */
	public static void glBegin()
	{
		GL11.glBegin(GL_QUADS);
	}

	/**
	 * Called to clear color, depth, and stencil buffers
	 */
	public static void glClearBuffers()
	{
		glClear(GL_DEPTH_BUFFER_BIT | GL_COLOR_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);
	}

	/**
	 * called to reset application to default content
	 */
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

		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

		glPixelStorei(GL_UNPACK_ALIGNMENT, 1);

		glColor(1, 1, 1, 1);
	}

	/**
	 * color scheme for rendering
	 * @param r
	 * @param g
	 * @param b
	 */
	public static void glColor(double r, double g, double b)
	{
		glColor4d(r, g, b, 1);
		getColor().setColor(r, g, b, 1);
	}

	/**
	 * color scheme for rendering
	 * @param r
	 * @param g
	 * @param b
	 * @param a
	 */
	public static void glColor(double r, double g, double b, double a)
	{
		glColor4d(r, g, b, a);
		getColor().setColor(r, g, b, a);
	}

	/**
	 * color scheme for rendering
	 * @param color
	 */
	public static void glColor(Color4d color)
	{
		glColor4d(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
		getColor().setColor(color);
	}

	/**
	 * get current set color
	 * @return
	 */
	public static Color4d getColor()
	{
		return currentColor;
	}

	/**
	 * get current red
	 * @return
	 */
	public static double getRed()
	{
		return getColor().getRed();
	}

	/**
	 * get current green
	 * @return
	 */
	public static double getGreen()
	{
		return getColor().getGreen();
	}

	/**
	 * get current blue
	 * @return
	 */
	public static double getBlue()
	{
		return getColor().getBlue();
	}

	/**
	 * get current alpha
	 * @return
	 */
	public static double getAlpha()
	{
		return getColor().getAlpha();
	}
	
	/**
	 * called after glBegin(...), sets a vertex with extra data
	 * @param x
	 * @param y
	 * @param z
	 * @param color
	 */
	public static void colorVertex(double x, double y, double z, Color4d color)
	{
		glColor(color);
		glVertex3d(x, y, z);
	}

	/**
	 * called after glBegin(...), sets a vertex with extra data
	 * @param x
	 * @param y
	 * @param z
	 * @param r
	 * @param g
	 * @param b
	 * @param a
	 */
	public static void colorVertex(double x, double y, double z, double r, double g, double b, double a)
	{
		glColor(r, g, b);
		glVertex3d(x, y, z);
	}

	/**
	 * called after glBegin(...), sets a vertex with extra data
	 * @param x
	 * @param y
	 * @param z
	 * @param u
	 * @param v
	 */
	public static void vertexUV(double x, double y, double z, double u, double v)
	{
		glTexCoord2d(u, v);
		glVertex3d(x, y, z);
	}

	/**
	 * called after glBegin(...), sets a vertex with extra data
	 * @param x
	 * @param y
	 * @param z
	 * @param u
	 * @param v
	 * @param color
	 */
	public static void colorVertexUV(double x, double y, double z, double u, double v, Color4d color)
	{
		glColor(color);
		vertexUV(x, y, z, u, v);
	}

	/**
	 * called after glBegin(...), sets a vertex with extra data
	 * @param x
	 * @param y
	 * @param z
	 * @param u
	 * @param v
	 * @param r
	 * @param g
	 * @param b
	 * @param a
	 */
	public static void colorVertexUV(double x, double y, double z, double u, double v, double r, double g, double b, double a)
	{
		glColor(r, g, b, a);
		vertexUV(x, y, z, u, v);
	}

	/**
	 * called after glBegin(...), sets a vertex with extra data
	 * @param x
	 * @param y
	 * @param z
	 * @param u
	 * @param v
	 * @param texture
	 */
	public static void vertexUV(double x, double y, double z, double u, double v, Texture texture)
	{
		glTexCoord2d((texture.getMaxU() - texture.getMinU()) * u + texture.getMinU(), (texture.getMaxV() - texture.getMinV()) * v + texture.getMinV());
		glVertex3d(x, y, z);
	}

	/**
	 * called after glBegin(...), sets a vertex with extra data
	 * @param x
	 * @param y
	 * @param z
	 * @param u
	 * @param v
	 * @param texture
	 */
	public static void colorVertexUV(double x, double y, double z, double u, double v, Texture texture, Color4d color)
	{
		glColor(color);
		vertexUV(x, y, z, u, v, texture);
	}

	/**
	 * called after glBegin(...), sets a vertex with extra data
	 * @param x
	 * @param y
	 * @param z
	 * @param u
	 * @param v
	 * @param texture
	 * @param r
	 * @param g
	 * @param b
	 * @param a
	 */
	public static void colorVertexUV(double x, double y, double z, double u, double v, Texture texture, double r, double g, double b, double a)
	{
		glColor(r, g, b, a);
		vertexUV(x, y, z, u, v, texture);
	}

	/**
	 * called after glBegin(...), sets a vertex with extra data
	 * @param x
	 * @param y
	 * @param z
	 * @param u
	 * @param v
	 * @param texture
	 * @param animationID
	 */
	public static void vertexUV(double x, double y, double z, double u, double v, Texture texture, int animationID)
	{
		glTexCoord2d((texture.getMaxU(animationID) - texture.getMinU(animationID)) * u + texture.getMinU(animationID), (texture.getMaxV(animationID) - texture.getMinV(animationID)) * v + texture.getMinV(animationID));
		glVertex3d(x, y, z);
	}

	/**
	 * called after glBegin(...), sets a vertex with extra data
	 * @param x
	 * @param y
	 * @param z
	 * @param u
	 * @param v
	 * @param texture
	 * @param animationID
	 * @param color
	 */
	public static void colorVertexUV(double x, double y, double z, double u, double v, Texture texture, int animationID, Color4d color)
	{
		glColor(color);
		vertexUV(x, y, z, u, v, texture, animationID);
	}

	/**
	 * called after glBegin(...), sets a vertex with extra data
	 * @param x
	 * @param y
	 * @param z
	 * @param u
	 * @param v
	 * @param texture
	 * @param animationID
	 * @param r
	 * @param g
	 * @param b
	 * @param a
	 */
	public static void colorVertexUV(double x, double y, double z, double u, double v, Texture texture, int animationID, double r, double g, double b, double a)
	{
		glColor(r, g, b, a);
		vertexUV(x, y, z, u, v, texture, animationID);
	}

	/**
	 * draws a rectangle, must have glBegin(GL_QUADS) enabled!
	 * @param x
	 * @param y
	 * @param x1
	 * @param y1
	 * @param z
	 * @param thickness
	 */
	public static void drawRect(double x, double y, double x1, double y1, double z, double thickness)
	{
		drawRect(x, y1 - thickness, x1, y1, z);
		drawRect(x1 - thickness, y, x1, y1 - thickness, z);
		drawRect(x, y, x1 - thickness, y + thickness, z);
		drawRect(x, y + thickness, x + thickness, y1 - thickness, z);
	}

	/**
	 * draws a rectangle, must have glBegin(GL_QUADS) enabled!
	 * @param x
	 * @param y
	 * @param x1
	 * @param y1
	 * @param z
	 * @param thickness
	 * @param innerColor
	 */
	public static void drawRect(double x, double y, double x1, double y1, double z, double thickness, Color4d innerColor)
	{
		Color4d outerColor = new Color4d(getColor());

		glVertex3d(x1, y, z);
		glVertex3d(x, y, z);
		colorVertex(x + thickness, y + thickness, z, innerColor);
		glVertex3d(x1 - thickness, y + thickness, z);

		glVertex3d(x1 - thickness, y + thickness, z);
		glVertex3d(x1 - thickness, y1 - thickness, z);
		colorVertex(x1, y1, z, outerColor);
		glVertex3d(x1, y, z);

		glVertex3d(x, y1, z);
		glVertex3d(x1, y1, z);
		colorVertex(x1 - thickness, y1 - thickness, z, innerColor);
		glVertex3d(x + thickness, y1 - thickness, z);

		glVertex3d(x + thickness, y1 - thickness, z);
		glVertex3d(x + thickness, y + thickness, z);
		colorVertex(x, y, z, outerColor);
		glVertex3d(x, y1, z);
	}

	/**
	 * draws a rectangle, must have glBegin(GL_QUADS) enabled!
	 * @param x
	 * @param y
	 * @param x1
	 * @param y1
	 * @param z
	 */
	public static void drawRect(double x, double y, double x1, double y1, double z)
	{
		glVertex3d(x, y1, z);
		glVertex3d(x1, y1, z);
		glVertex3d(x1, y, z);
		glVertex3d(x, y, z);
	}

	/**
	 * draws a rectangle, must have glBegin(GL_QUADS) enabled!
	 * @param x
	 * @param y
	 * @param x1
	 * @param y1
	 * @param z
	 * @param color0
	 * @param color1
	 * @param color2
	 * @param color3
	 */
	public static void drawRect(double x, double y, double x1, double y1, double z, Color4d color0, Color4d color1, Color4d color2, Color4d color3)
	{
		colorVertex(x, y1, z, color0);
		colorVertex(x1, y1, z, color1);
		colorVertex(x1, y, z, color2);
		colorVertex(x, y, z, color3);
	}

	/**
	 * draws a rectangle, must have glBegin(GL_QUADS) enabled!
	 * @param x
	 * @param y
	 * @param x1
	 * @param y1
	 * @param z
	 * @param u
	 * @param v
	 * @param u1
	 * @param v1
	 */
	public static void drawRectUV(double x, double y, double x1, double y1, double z, double u, double v, double u1, double v1)
	{
		vertexUV(x, y1, z, u, v1);
		vertexUV(x1, y1, z, u1, v1);
		vertexUV(x1, y, z, u1, v);
		vertexUV(x, y, z, u, v);
	}

	/**
	 * draws a rectangle, must have glBegin(GL_QUADS) enabled!
	 * @param x
	 * @param y
	 * @param x1
	 * @param y1
	 * @param z
	 */
	public static void drawRectUV(double x, double y, double x1, double y1, double z)
	{
		Texture texture = getTexture();
		vertexUV(x, y, z, texture.getMinU(), texture.getMinV());
		vertexUV(x, y1, z, texture.getMinU(), texture.getMaxV());
		vertexUV(x1, y1, z, texture.getMaxU(), texture.getMaxV());
		vertexUV(x1, y, z, texture.getMaxU(), texture.getMinV());
	}

	/**
	 * draws a rectangle, must have glBegin(GL_QUADS) enabled!
	 * @param x
	 * @param y
	 * @param x1
	 * @param y1
	 * @param z
	 * @param animationID
	 */
	public static void drawRectUV(double x, double y, double x1, double y1, double z, int animationID)
	{
		Texture texture = getTexture();
		vertexUV(x, y, z, texture.getMinU(animationID), texture.getMinV(animationID));
		vertexUV(x, y1, z, texture.getMinU(animationID), texture.getMaxV(animationID));
		vertexUV(x1, y1, z, texture.getMaxU(animationID), texture.getMaxV(animationID));
		vertexUV(x1, y, z, texture.getMaxU(animationID), texture.getMinV(animationID));
	}

	/**
	 * draws a rectangle, must have glBegin(GL_QUADS) enabled!
	 * @param x
	 * @param y
	 * @param x1
	 * @param y1
	 * @param z
	 * @param texture
	 */
	public static void drawRectUV(double x, double y, double x1, double y1, double z, Texture texture)
	{
		vertexUV(x, y, z, texture.getMinU(), texture.getMinV());
		vertexUV(x, y1, z, texture.getMinU(), texture.getMaxV());
		vertexUV(x1, y1, z, texture.getMaxU(), texture.getMaxV());
		vertexUV(x1, y, z, texture.getMaxU(), texture.getMinV());
	}

	/**
	 * draws a rectangle, must have glBegin(GL_QUADS) enabled!
	 * @param x
	 * @param y
	 * @param x1
	 * @param y1
	 * @param z
	 * @param texture
	 * @param animationID
	 */
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

	/**
	 * draws a rectangle, must have glBegin(GL_QUADS) enabled!
	 * @param x
	 * @param y
	 * @param x1
	 * @param y1
	 * @param z
	 * @param u
	 * @param v
	 * @param u1
	 * @param v1
	 * @param shiftToColor
	 */
	public static void drawColorHRectUV(double x, double y, double x1, double y1, double z, double u, double v, double u1, double v1, Color4d shiftToColor)
	{
		vertexUV(x, y, z, u, v);
		vertexUV(x, y1, z, u, v1);
		colorVertexUV(x1, y1, z, u1, v1, shiftToColor);
		vertexUV(x1, y, z, u1, v);
	}

	/**
	 * draws a rectangle, must have glBegin(GL_QUADS) enabled!
	 * @param x
	 * @param y
	 * @param x1
	 * @param y1
	 * @param z
	 * @param shiftToColor
	 */
	public static void drawColorHRectUV(double x, double y, double x1, double y1, double z, Color4d shiftToColor)
	{
		Texture texture = getTexture();
		vertexUV(x, y, z, texture.getMinU(), texture.getMinV());
		vertexUV(x, y1, z, texture.getMinU(), texture.getMaxV());
		colorVertexUV(x1, y1, z, texture.getMaxU(), texture.getMaxV(), shiftToColor);
		vertexUV(x1, y, z, texture.getMaxU(), texture.getMinV());
	}

	/**
	 * draws a rectangle, must have glBegin(GL_QUADS) enabled!
	 * @param x
	 * @param y
	 * @param x1
	 * @param y1
	 * @param z
	 * @param animationID
	 * @param shiftToColor
	 */
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

	/**
	 * draws a rectangle, must have glBegin(GL_QUADS) enabled!
	 * @param x
	 * @param y
	 * @param x1
	 * @param y1
	 * @param z
	 * @param texture
	 * @param shiftToColor
	 */
	public static void drawColorHRectUV(double x, double y, double x1, double y1, double z, Texture texture, Color4d shiftToColor)
	{
		vertexUV(x, y, z, texture.getMinU(), texture.getMinV());
		vertexUV(x, y1, z, texture.getMinU(), texture.getMaxV());
		colorVertexUV(x1, y1, z, texture.getMaxU(), texture.getMaxV(), shiftToColor);
		vertexUV(x1, y, z, texture.getMaxU(), texture.getMinV());
	}

	/**
	 * draws a rectangle, must have glBegin(GL_QUADS) enabled!
	 * @param x
	 * @param y
	 * @param x1
	 * @param y1
	 * @param z
	 * @param texture
	 * @param animationID
	 * @param shiftToColor
	 */
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

	/**
	 * draws a rectangle, must have glBegin(GL_QUADS) enabled!
	 * @param x
	 * @param y
	 * @param x1
	 * @param y1
	 * @param z
	 * @param u
	 * @param v
	 * @param u1
	 * @param v1
	 * @param r
	 * @param g
	 * @param b
	 * @param a
	 */
	public static void drawColorHRectUV(double x, double y, double x1, double y1, double z, double u, double v, double u1, double v1, double r, double g, double b, double a)
	{
		vertexUV(x, y, z, u, v);
		vertexUV(x, y1, z, u, v1);
		colorVertexUV(x1, y1, z, u1, v1, r, g, b, a);
		vertexUV(x1, y, z, u1, v);
	}

	/**
	 * draws a rectangle, must have glBegin(GL_QUADS) enabled!
	 * @param x
	 * @param y
	 * @param x1
	 * @param y1
	 * @param z
	 * @param r
	 * @param g
	 * @param b
	 * @param a
	 */
	public static void drawColorHRectUV(double x, double y, double x1, double y1, double z, double r, double g, double b, double a)
	{
		Texture texture = getTexture();
		vertexUV(x, y, z, texture.getMinU(), texture.getMinV());
		vertexUV(x, y1, z, texture.getMinU(), texture.getMaxV());
		colorVertexUV(x1, y1, z, texture.getMaxU(), texture.getMaxV(), r, g, b, a);
		vertexUV(x1, y, z, texture.getMaxU(), texture.getMinV());
	}

	/**
	 * draws a rectangle, must have glBegin(GL_QUADS) enabled!
	 * @param x
	 * @param y
	 * @param x1
	 * @param y1
	 * @param z
	 * @param animationID
	 * @param r
	 * @param g
	 * @param b
	 * @param a
	 */
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

	/**
	 * draws a rectangle, must have glBegin(GL_QUADS) enabled!
	 * @param x
	 * @param y
	 * @param x1
	 * @param y1
	 * @param z
	 * @param texture
	 * @param r
	 * @param g
	 * @param b
	 * @param a
	 */
	public static void drawColorHRectUV(double x, double y, double x1, double y1, double z, Texture texture, double r, double g, double b, double a)
	{
		vertexUV(x, y, z, texture.getMinU(), texture.getMinV());
		vertexUV(x, y1, z, texture.getMinU(), texture.getMaxV());
		colorVertexUV(x1, y1, z, texture.getMaxU(), texture.getMaxV(), r, g, b, a);
		vertexUV(x1, y, z, texture.getMaxU(), texture.getMinV());
	}

	/**
	 * draws a rectangle, must have glBegin(GL_QUADS) enabled!
	 * @param x
	 * @param y
	 * @param x1
	 * @param y1
	 * @param z
	 * @param texture
	 * @param animationID
	 * @param r
	 * @param g
	 * @param b
	 * @param a
	 */
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

	/**
	 * draws a rectangle, must have glBegin(GL_QUADS) enabled!
	 * @param x
	 * @param y
	 * @param x1
	 * @param y1
	 * @param z
	 * @param u
	 * @param v
	 * @param u1
	 * @param v1
	 * @param shiftToColor
	 */
	public static void drawColorVRectUV(double x, double y, double x1, double y1, double z, double u, double v, double u1, double v1, Color4d shiftToColor)
	{
		vertexUV(x1, y, z, u1, v);
		vertexUV(x, y, z, u, v);
		colorVertexUV(x, y1, z, u, v1, shiftToColor);
		vertexUV(x1, y1, z, u1, v1);
	}

	/**
	 * draws a rectangle, must have glBegin(GL_QUADS) enabled!
	 * @param x
	 * @param y
	 * @param x1
	 * @param y1
	 * @param z
	 * @param shiftToColor
	 */
	public static void drawColorVRectUV(double x, double y, double x1, double y1, double z, Color4d shiftToColor)
	{
		Texture texture = getTexture();
		vertexUV(x1, y, z, texture.getMaxU(), texture.getMinV());
		vertexUV(x, y, z, texture.getMinU(), texture.getMinV());
		colorVertexUV(x, y1, z, texture.getMinU(), texture.getMaxV(), shiftToColor);
		vertexUV(x1, y1, z, texture.getMaxU(), texture.getMaxV());
	}

	/**
	 * draws a rectangle, must have glBegin(GL_QUADS) enabled!
	 * @param x
	 * @param y
	 * @param x1
	 * @param y1
	 * @param z
	 * @param animationID
	 * @param shiftToColor
	 */
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

	/**
	 * draws a rectangle, must have glBegin(GL_QUADS) enabled!
	 * @param x
	 * @param y
	 * @param x1
	 * @param y1
	 * @param z
	 * @param texture
	 * @param shiftToColor
	 */
	public static void drawColorVRectUV(double x, double y, double x1, double y1, double z, Texture texture, Color4d shiftToColor)
	{
		vertexUV(x1, y, z, texture.getMaxU(), texture.getMinV());
		vertexUV(x, y, z, texture.getMinU(), texture.getMinV());
		colorVertexUV(x, y1, z, texture.getMinU(), texture.getMaxV(), shiftToColor);
		vertexUV(x1, y1, z, texture.getMaxU(), texture.getMaxV());
	}

	/**
	 * draws a rectangle, must have glBegin(GL_QUADS) enabled!
	 * @param x
	 * @param y
	 * @param x1
	 * @param y1
	 * @param z
	 * @param texture
	 * @param animationID
	 * @param shiftToColor
	 */
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

	/**
	 * draws a rectangle, must have glBegin(GL_QUADS) enabled!
	 * @param x
	 * @param y
	 * @param x1
	 * @param y1
	 * @param z
	 * @param u
	 * @param v
	 * @param u1
	 * @param v1
	 * @param r
	 * @param g
	 * @param b
	 * @param a
	 */
	public static void drawColorVRectUV(double x, double y, double x1, double y1, double z, double u, double v, double u1, double v1, double r, double g, double b, double a)
	{
		vertexUV(x1, y, z, u1, v);
		vertexUV(x, y, z, u, v);
		colorVertexUV(x, y1, z, u, v1, r, g, b, a);
		vertexUV(x1, y1, z, u1, v1);
	}

	/**
	 * draws a rectangle, must have glBegin(GL_QUADS) enabled!
	 * @param x
	 * @param y
	 * @param x1
	 * @param y1
	 * @param z
	 * @param r
	 * @param g
	 * @param b
	 * @param a
	 */
	public static void drawColorVRectUV(double x, double y, double x1, double y1, double z, double r, double g, double b, double a)
	{
		Texture texture = getTexture();
		vertexUV(x1, y, z, texture.getMaxU(), texture.getMinV());
		vertexUV(x, y, z, texture.getMinU(), texture.getMinV());
		colorVertexUV(x, y1, z, texture.getMinU(), texture.getMaxV(), r, g, b, a);
		vertexUV(x1, y1, z, texture.getMaxU(), texture.getMaxV());
	}

	/**
	 * draws a rectangle, must have glBegin(GL_QUADS) enabled!
	 * @param x
	 * @param y
	 * @param x1
	 * @param y1
	 * @param z
	 * @param animationID
	 * @param r
	 * @param g
	 * @param b
	 * @param a
	 */
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

	/**
	 * draws a rectangle, must have glBegin(GL_QUADS) enabled!
	 * @param x
	 * @param y
	 * @param x1
	 * @param y1
	 * @param z
	 * @param texture
	 * @param r
	 * @param g
	 * @param b
	 * @param a
	 */
	public static void drawColorVRectUV(double x, double y, double x1, double y1, double z, Texture texture, double r, double g, double b, double a)
	{
		vertexUV(x1, y, z, texture.getMaxU(), texture.getMinV());
		vertexUV(x, y, z, texture.getMinU(), texture.getMinV());
		colorVertexUV(x, y1, z, texture.getMinU(), texture.getMaxV(), r, g, b, a);
		vertexUV(x1, y1, z, texture.getMaxU(), texture.getMaxV());
	}

	/**
	 * draws a rectangle, must have glBegin(GL_QUADS) enabled!
	 * @param x
	 * @param y
	 * @param x1
	 * @param y1
	 * @param z
	 * @param texture
	 * @param animationID
	 * @param r
	 * @param g
	 * @param b
	 * @param a
	 */
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

	/**
	 * draws an arc, must have glBegin(GL_QUADS) enabled!
	 * @param circleX
	 * @param circleY
	 * @param z
	 * @param radius
	 * @param lineCount
	 * @param thickness
	 */
	public static void drawArc(double circleX, double circleY, double z, double radius, int lineCount, double thickness)
	{
		drawArc(circleX, circleY, z, radius, 0, TWOPI, lineCount, thickness);
	}

	/**
	 * draws an arc, must have glBegin(GL_QUADS) enabled!
	 * @param circleX
	 * @param circleY
	 * @param z
	 * @param radius
	 * @param startAngle
	 * @param endAngle
	 * @param lineCount
	 * @param thickness
	 */
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

	/**
	 * draws an arc, must have glBegin(GL_QUADS) enabled!
	 * @param circleX
	 * @param circleY
	 * @param z
	 * @param radius
	 * @param lineCount
	 * @param thickness
	 * @param endColor
	 */
	public static void drawArc(double circleX, double circleY, double z, double radius, int lineCount, double thickness, Color4d endColor)
	{
		drawArc(circleX, circleY, z, radius, 0, TWOPI, lineCount, thickness, endColor);
	}

	/**
	 * draws an arc, must have glBegin(GL_QUADS) enabled!
	 * @param circleX
	 * @param circleY
	 * @param z
	 * @param radius
	 * @param angle0
	 * @param angle
	 * @param lineCount
	 * @param thickness
	 * @param endColor
	 */
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

	/**
	 * draws a circle, do not have any glBegin!
	 * @param circleX
	 * @param circleY
	 * @param z
	 * @param radius
	 * @param lineCount
	 */
	public static void drawCircle(double circleX, double circleY, double z, double radius, int lineCount)
	{
		drawCircle(circleX, circleY, z, radius, lineCount, 0, TWOPI);
	}

	/**
	 * draws a circle, do not have any glBegin!
	 * @param circleX
	 * @param circleY
	 * @param z
	 * @param radius
	 * @param lineCount
	 * @param angle0
	 * @param angle
	 */
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

	/**
	 * draws a circle, do not have any glBegin!
	 * @param circleX
	 * @param circleY
	 * @param z
	 * @param radius
	 * @param lineCount
	 * @param innerColor
	 */
	public static void drawCircle(double circleX, double circleY, double z, double radius, int lineCount, Color4d innerColor)
	{
		drawCircle(circleX, circleY, z, radius, lineCount, 0, TWOPI, innerColor);
	}

	/**
	 * draws a circle, do not have any glBegin!
	 * @param circleX
	 * @param circleY
	 * @param z
	 * @param radius
	 * @param lineCount
	 * @param angle0
	 * @param angle
	 * @param innerColor
	 */
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

	/**
	 * draws a circle, do not have any glBegin!
	 * @param circleX
	 * @param circleY
	 * @param z
	 * @param radius
	 * @param u
	 * @param v
	 * @param u1
	 * @param v1
	 * @param lineCount
	 */
	public static void drawCircle(double circleX, double circleY, double z, double radius, double u, double v, double u1, double v1, int lineCount)
	{
		drawCircle(circleX, circleY, z, radius, u, v, u1, v1, getTexture(), lineCount, 0, TWOPI);
	}

	/**
	 * draws a circle, do not have any glBegin!
	 * @param circleX
	 * @param circleY
	 * @param z
	 * @param radius
	 * @param u
	 * @param v
	 * @param u1
	 * @param v1
	 * @param animationID
	 * @param lineCount
	 */
	public static void drawCircle(double circleX, double circleY, double z, double radius, double u, double v, double u1, double v1, int animationID, int lineCount)
	{
		drawCircle(circleX, circleY, z, radius, u, v, u1, v1, getTexture(), animationID, lineCount, 0, TWOPI);
	}

	/**
	 * draws a circle, do not have any glBegin!
	 * @param circleX
	 * @param circleY
	 * @param z
	 * @param radius
	 * @param u
	 * @param v
	 * @param u1
	 * @param v1
	 * @param texture
	 * @param lineCount
	 */
	public static void drawCircle(double circleX, double circleY, double z, double radius, double u, double v, double u1, double v1, Texture texture, int lineCount)
	{
		drawCircle(circleX, circleY, z, radius, u, v, u1, v1, texture, lineCount, 0, TWOPI);
	}

	/**
	 * draws a circle, do not have any glBegin!
	 * @param circleX
	 * @param circleY
	 * @param z
	 * @param radius
	 * @param u
	 * @param v
	 * @param u1
	 * @param v1
	 * @param texture
	 * @param animationID
	 * @param lineCount
	 */
	public static void drawCircle(double circleX, double circleY, double z, double radius, double u, double v, double u1, double v1, Texture texture, int animationID, int lineCount)
	{
		drawCircle(circleX, circleY, z, radius, u, v, u1, v1, texture, animationID, lineCount, 0, TWOPI);
	}

	/**
	 * draws a circle, do not have any glBegin!
	 * @param circleX
	 * @param circleY
	 * @param z
	 * @param radius
	 * @param u
	 * @param v
	 * @param u1
	 * @param v1
	 * @param texture
	 * @param lineCount
	 * @param angle0
	 * @param angle
	 */
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

	/**
	 * draws a circle, do not have any glBegin!
	 * @param circleX
	 * @param circleY
	 * @param z
	 * @param radius
	 * @param u
	 * @param v
	 * @param u1
	 * @param v1
	 * @param texture
	 * @param animationID
	 * @param lineCount
	 * @param angle0
	 * @param angle
	 */
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
