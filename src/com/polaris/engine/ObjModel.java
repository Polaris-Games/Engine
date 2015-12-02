package com.polaris.engine;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.lwjgl.opengl.GL11;

import static com.polaris.engine.Renderer.*;
import static org.lwjgl.opengl.GL11.*;

public class ObjModel 
{

	protected short[][] faceArray;
	protected float[][] vertexArray;
	protected float[][] textureCoordArray;
	protected boolean supportsQuads;
	protected int textureId;

	public ObjModel(String name)
	{
		try
		{
			BufferedReader reader = new BufferedReader(new InputStreamReader(Helper.getResourceStream("models/" + name + ".obj")));
			loadPolygons(reader);
			reader.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		textureId = createTextureId("model_" + name, false);
	}

	public ObjModel(String url, String textureUrl) throws IOException
	{
		HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
		BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		loadPolygons(reader);
		reader.close();
		connection.disconnect();
		connection = (HttpURLConnection) new URL(textureUrl).openConnection();
		textureId = createTextureId(textureUrl, ImageIO.read(connection.getInputStream()), false);
		connection.disconnect();
	}

	public void render(double x, double y, double z, double rotationX, double rotationY, double rotationZ)
	{
		glPushMatrix();
		glBind(textureId);
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

	public void destroy()
	{
		glClearTexture(textureId);
	}

	private void loadPolygons(BufferedReader reader) throws IOException
	{
		List<Short[]> faceList = new ArrayList<Short[]>();
		List<Float[]> vertexList = new ArrayList<Float[]>();
		List<Float[]> textureCoordList = new ArrayList<Float[]>();
		String line = null;
		supportsQuads = false;
		while((line = reader.readLine()) != null)
		{
			if(line.startsWith("v "))
			{
				String[] desc = line.substring(2).split(" ");
				vertexList.add(new Float[]{parse(desc[0]), parse(desc[1]), parse(desc[2])});
			}
			else if(line.startsWith("vt "))
			{
				String[] desc = line.substring(3).split(" ");
				textureCoordList.add(new Float[]{parse(desc[0]), parse(desc[1])});
			}
			else if(line.startsWith("f "))
			{
				String[] desc = line.substring(2).split(" ");
				Short[] coordArray = new Short[desc.length * 2];
				if(desc.length == 4)
				{
					supportsQuads = true;
				}
				for(int i = 0; i < desc.length; i++)
				{
					String[] coord = desc[i].split("/");
					coordArray[i * 2] = parse1(coord[0]);
					coordArray[i * 2 + 1] = parse1(coord[1]);
				}
				faceList.add(coordArray);
			}
		}
		faceArray = new short[faceList.size()][(supportsQuads ? 4 : 3) * 2];
		vertexArray = new float[vertexList.size()][3];
		textureCoordArray = new float[textureCoordList.size()][2];
		int i;
		int j;
		for(i = 0; i < vertexList.size(); i++)
		{
			Float[] vertex = vertexList.get(i);
			for(j = 0; j < vertex.length; j++)
			{
				vertexArray[i][j] = vertex[j];
			}
		}
		for(i = 0; i < textureCoordList.size(); i++)
		{
			Float[] textureCoord = textureCoordList.get(i);
			for(j = 0; j < textureCoord.length; j++)
			{
				textureCoordArray[i][j] = textureCoord[j];
			}
		}
		for(i = 0; i < faceList.size(); i++)
		{
			Short[] coords = faceList.get(i);
			for(j = 0; j < coords.length; j++)
			{
				faceArray[i][j] = (short) (coords[j] - 1);
			}
		}
	}

	private static Float parse(String s)
	{
		return Float.parseFloat(s);
	}

	private static Short parse1(String s)
	{
		return Short.parseShort(s);
	}
}
