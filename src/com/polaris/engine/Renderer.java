package com.polaris.engine;

import static com.polaris.engine.Helper.TWOPI;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

import java.awt.Window;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.opengl.ARBFramebufferObject;
import org.lwjgl.opengl.ARBTextureStorage;
import org.lwjgl.opengl.GL11;

public class Renderer 
{
	private static ThreadLocal<Integer> windowWidth = new ThreadLocal<Integer>() {
		public Integer initialValue()
		{
			return 0;
		}
	};
	private static ThreadLocal<Integer> windowHeight = new ThreadLocal<Integer> () {
		public Integer initialValue()
		{
			return 0;
		}
	};
	private static ThreadLocal<Double> windowRatio = new ThreadLocal<Double> () {
		public Double initialValue()
		{
			return 0D;
		}
	};
	private static ThreadLocal<Map<String, Integer>> textureList = new ThreadLocal<Map<String, Integer>> () {
		public Map<String, Integer> initialValue()
		{
			return new HashMap<String, Integer>();
		}
	};
	private static ThreadLocal<Integer> currentTexture = new ThreadLocal<Integer> () {
		public Integer initialValue()
		{
			return -1;
		}
	};
	private static ThreadLocal<Color4d> currentColor = new ThreadLocal<Color4d> () {
		public Color4d initialValue()
		{
			return new Color4d(1, 1, 1, 1);
		}
	};

	public static void updateSize(long windowInstance) 
	{
		IntBuffer width = IntBuffer.allocate(1);
		IntBuffer height = IntBuffer.allocate(1);
		glfwGetFramebufferSize(windowInstance, width, height);
		windowWidth.set(width.get());
		windowHeight.set(height.get());
		windowRatio.set(windowWidth.get() / (double) windowHeight.get());
	}

	public static int createTextureId(String filename, boolean mipmap)
	{
		BufferedImage image = null;
		try
		{
			image = ImageIO.read(Helper.getResourceStream("textures/" + filename + (filename.lastIndexOf('.') == -1 ? ".png" : "")));
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		return createTextureId(filename, image, mipmap);
	}

	public static int createTextureId(String s, BufferedImage image, boolean mipmap)
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
			glBindTexture(GL_TEXTURE_2D, texture);
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
			textureList.get().put(s, texture);
			return texture;
		}
		return -100;
	}
	
	public static void gl2d()
	{
		glViewport(0, 0, windowWidth.get(), windowHeight.get());
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		glOrtho(0, 1280, 720, 0, -1, 1);
		glMatrixMode(GL_MODELVIEW);
		glLoadIdentity();
	}

	public static void gl3d()
	{
		glViewport(0, 0, windowWidth.get(), windowHeight.get());
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		glOrtho(0, windowWidth.get(), windowHeight.get(), 0, .1f, 1000f);
		glMatrixMode(GL_MODELVIEW);
		glLoadIdentity();
	}

	public static boolean glBind(int texId)
	{
		if(texId != currentTexture.get())
		{
			glBindTexture(GL_TEXTURE_2D, texId);
			currentTexture.set(texId);
			return true;
		}
		return false;
	}

	/**
	 * Binds the current OpenGl texture to be the [String:texture] and if it isn't cached already it will be cached
	 * @param texture : The texture to be bound
	 * @return Whether it was successfully bound or not
	 */
	public static boolean glBind(String texture)
	{
		int texId = textureList.get().containsKey(texture) ? textureList.get().get(texture) : createTextureId(texture, true);
		if(texId != -100 && texId != currentTexture.get())
		{
			glBindTexture(GL_TEXTURE_2D, texId);
			currentTexture.set(texId);
			return true;
		}
		return false;
	}

	public static boolean glBind(String texture, boolean mipmap)
	{
		int texId = textureList.get().containsKey(texture) ? textureList.get().get(texture) : createTextureId(texture, mipmap);
		if(texId != -100 && texId != currentTexture.get())
		{
			glBindTexture(GL_TEXTURE_2D, texId);
			currentTexture.set(texId);
			return true;
		}
		return false;
	}

	/**
	 * Binds the current OpenGl texture to be the [String:textureName] from the cached and if non existent then it will generate
	 * the texture under the [BufferedImage:texture]
	 * @param name : The name in the cache to be bound
	 * @param texture : the BufferedImage to add to cache in case not there
	 * @return Whether it was successfully bound or not
	 */
	public static boolean glBind(String name, BufferedImage texture)
	{
		int texId = textureList.get().containsKey(name) ? textureList.get().get(name) : createTextureId(name, texture, true);
		if(texId != -100 && texId != currentTexture.get())
		{
			glBindTexture(GL_TEXTURE_2D, texId);
			currentTexture.set(texId);
			return true;
		}
		return false;
	}

	public static boolean glBind(String name, BufferedImage texture, boolean mipmap)
	{
		glEnable(GL_TEXTURE_2D);
		int texId = textureList.get().containsKey(name) ? textureList.get().get(name) : createTextureId(name, texture, mipmap);
		if(texId != -100 && texId != currentTexture.get())
		{
			glBindTexture(GL_TEXTURE_2D, texId);
			currentTexture.set(texId);
			return true;
		}
		return false;
	}

	public static boolean glClearTexture(String texture)
	{
		if(textureList.get().containsKey(texture))
		{
			GL11.glDeleteTextures(textureList.get().get(texture));
			textureList.get().remove(texture);
			return true;
		}
		return false;
	}

	public static boolean glClearTexture(int textureId)
	{
		boolean found = false;
		for(String textureName : textureList.get().keySet())
		{
			if(textureList.get().get(textureName) == textureId)
			{
				found = true;
				textureList.get().remove(textureName);
				break;
			}
		}
		return found;
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
		return currentColor.get();
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

	public static void colorVertexUV(double x, double y, double z, double u, double v, Color4d color)
	{
		glColor(color);
		vertexUV(x, y, z, u, v);
	}

	public static void colorVertex(double x, double y, double z, Color4d color)
	{
		glColor(color);
		glVertex3d(x, y, z);
	}

	public static void vertexUV(double x, double y, double z, double u, double v)
	{
		glTexCoord2d(u, v);
		glVertex3d(x, y, z);
	}

	public static void colorVertex(double x, double y, double z, double r, double g, double b, double a)
	{
		glColor(r, g, b);
		glVertex3d(x, y, z);
	}

	public static void colorVertexUV(double x, double y, double z, double u, double v, double r, double g, double b, double a)
	{
		glColor(r, g, b, a);
		vertexUV(x, y, z, u, v);
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

	public static void drawRect(double x, double y, double x1, double y1, double z, double u, double v, double u1, double v1)
	{
		vertexUV(x, y1, z, u, v1);
		vertexUV(x1, y1, z, u1, v1);
		vertexUV(x1, y, z, u1, v);
		vertexUV(x, y, z, u, v);
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
		drawCircle(circleX, circleY, z, radius, u, v, u1, v1, lineCount, 0, TWOPI);
	}

	public static void drawCircle(double circleX, double circleY, double z, double radius, double u, double v, double u1, double v1, int lineCount, double angle0, double angle)
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
			vertexUV(circleX + radius * cos((angle -= deltaTheta)), circleY + radius * sin(angle), z, centerU + difU * cos(angle), centerV + difV * sin(angle));
		}
		glEnd();
	}

}
