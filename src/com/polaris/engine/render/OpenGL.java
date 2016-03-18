package com.polaris.engine.render;

import static com.polaris.engine.options.Settings.getModelDirectory;
import static com.polaris.engine.options.Settings.getTextureDirectory;
import static org.lwjgl.opengl.GL11.GL_ALPHA_TEST;
import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_DITHER;
import static org.lwjgl.opengl.GL11.GL_GREATER;
import static org.lwjgl.opengl.GL11.GL_LEQUAL;
import static org.lwjgl.opengl.GL11.GL_LINEAR;
import static org.lwjgl.opengl.GL11.GL_LINEAR_MIPMAP_LINEAR;
import static org.lwjgl.opengl.GL11.GL_NICEST;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_PERSPECTIVE_CORRECTION_HINT;
import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.GL_REPEAT;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_RGBA8;
import static org.lwjgl.opengl.GL11.GL_SMOOTH;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_STENCIL_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_HEIGHT;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WIDTH;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_S;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_T;
import static org.lwjgl.opengl.GL11.GL_UNPACK_ALIGNMENT;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.glAlphaFunc;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glClearDepth;
import static org.lwjgl.opengl.GL11.glClearStencil;
import static org.lwjgl.opengl.GL11.glColor4d;
import static org.lwjgl.opengl.GL11.glDeleteTextures;
import static org.lwjgl.opengl.GL11.glDepthFunc;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.opengl.GL11.glGetTexImage;
import static org.lwjgl.opengl.GL11.glGetTexLevelParameteriv;
import static org.lwjgl.opengl.GL11.glHint;
import static org.lwjgl.opengl.GL11.glPixelStorei;
import static org.lwjgl.opengl.GL11.glShadeModel;
import static org.lwjgl.opengl.GL11.glTexCoord2d;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import static org.lwjgl.opengl.GL11.glTexSubImage2D;
import static org.lwjgl.opengl.GL11.glVertex3d;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.ByteBuffer;
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


public class OpenGL 
{

	private static Color4d currentColor = new Color4d(1, 1, 1, 1);

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
		glColor(1, 1, 1, 1);
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
		glAlphaFunc(GL_GREATER, .05f);
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
	public static void glVertex(double x, double y, double z, Color4d color)
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
	public static void glVertex(double x, double y, double z, double r, double g, double b, double a)
	{
		glColor(r, g, b, a);
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
	public static void glVertex(double x, double y, double z, double u, double v)
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
	public static void glVertex(double x, double y, double z, double u, double v, Color4d color)
	{
		glColor(color);
		glVertex(x, y, z, u, v);
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
	public static void glVertex(double x, double y, double z, double u, double v, double r, double g, double b, double a)
	{
		glColor(r, g, b, a);
		glVertex(x, y, z, u, v);
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
	public static void glVertex(double x, double y, double z, double u, double v, Texture texture)
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
	public static void glVertex(double x, double y, double z, double u, double v, Texture texture, Color4d color)
	{
		glColor(color);
		glVertex(x, y, z, u, v, texture);
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
	public static void glVertex(double x, double y, double z, double u, double v, Texture texture, double r, double g, double b, double a)
	{
		glColor(r, g, b, a);
		glVertex(x, y, z, u, v, texture);
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
	public static void glVertex(double x, double y, double z, double u, double v, Texture texture, int animationID)
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
	public static void glVertex(double x, double y, double z, double u, double v, Texture texture, int animationID, Color4d color)
	{
		glColor(color);
		glVertex(x, y, z, u, v, texture, animationID);
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
	public static void glVertex(double x, double y, double z, double u, double v, Texture texture, int animationID, double r, double g, double b, double a)
	{
		glColor(r, g, b, a);
		glVertex(x, y, z, u, v, texture, animationID);
	}

}
