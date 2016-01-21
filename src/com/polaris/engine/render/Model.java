package com.polaris.engine.render;

import static com.polaris.engine.render.Renderer.*;
import static com.polaris.engine.render.Renderer.glBegin;
import static com.polaris.engine.render.Renderer.vertexUV;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glRotated;
import static org.lwjgl.opengl.GL11.glTranslated;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.imageio.ImageIO;

import org.lwjgl.opengl.GL11;

import com.polaris.engine.util.Helper;

public abstract class Model implements ITexture
{
	
	protected short[][] faceArray;
	protected float[][] vertexArray;
	protected float[][] textureCoordArray;
	protected boolean supportsQuads;
	private int textureId;

	public Model(File modelLocation)
	{
		try
		{
			BufferedReader reader = new BufferedReader(new FileReader(modelLocation));
			loadPolygons(reader);
			reader.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	protected abstract void loadPolygons(BufferedReader reader) throws IOException;
	
	public void render(double x, double y, double z, double rotationX, double rotationY, double rotationZ)
	{
		glPushMatrix();
		glBindTexture(textureId);
		glTranslated(x, y, z);
		glRotated(rotationX, 1, 0, 0);
		glRotated(rotationY, 0, 1, 0);
		glRotated(rotationZ, 0, 0, 1);
		if(supportsQuads)
			glBegin();
		else
			glBegin(GL11.GL_TRIANGLES);
		int i;
		int j;
		short[] face;
		for(i = 0; i < faceArray.length; i++)
		{
			face = faceArray[i];
			for(j = 0; j < face.length; j+=2)
			{
				vertexUV(vertexArray[face[j]][0], vertexArray[face[j]][1], vertexArray[face[j]][2], textureCoordArray[face[j + 1]][0], textureCoordArray[face[j + 1]][1]);
			}
		}
		glEnd();
		glPopMatrix();
	}
	
	public int getTextureID()
	{
		return textureId;
	}
	
	public void setTextureID(int id)
	{
		textureId = id;
	}
	
	public Texture getTexture() {return null;}
	
	public Texture getTexture(String textureName) {return null;}
	
	protected static Float parse(String s)
	{
		return Float.parseFloat(s);
	}

	protected static Short parse1(String s)
	{
		return Short.parseShort(s);
	}
	
}
