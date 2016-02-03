package astronomy;

import org.lwjgl.glfw.GLFW;

import static com.polaris.engine.render.Renderer.*;
import static org.lwjgl.opengl.EXTFramebufferObject.*;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT0;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER_COMPLETE;
import static org.lwjgl.opengl.GL30.glDeleteFramebuffers;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL14.*;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import com.polaris.engine.Application;
import com.polaris.engine.render.Renderer;


public class InfoApplication extends Application
{

	@Override
	protected void init() 
	{
		glDefaults();
		setGui(new GuiMainMenu(this));
	}

	@Override
	protected long createWindow() 
	{
		setHint(GLFW.GLFW_RESIZABLE, true);
		return createAndCenter(1280, 720, "Hello", 0);
	}

	public static double[][] galaxyArray = new double[24000][7];

	public static void main(String[] args)
	{
		for (int i = 0; i < galaxyArray.length; i++)
		{
			double rotX = 0;
			double rotY = 0;
			double rotZ = 0;
			rotX = (Math.random() * 2 * Math.PI);
			rotY = (Math.random() * 2 * Math.PI);
			rotZ = (Math.random() * 2 * Math.PI);
			double[] point = new double[]{ 128, 128, 128 };
			point = rotateX(rotX, point);
			point = rotateY(rotY, point);
			point = rotateZ(rotZ, point);

			// star points x, y, z
			galaxyArray[i][0] = point[0];
			galaxyArray[i][1] = point[1];
			galaxyArray[i][2] = point[2];

			double d = Math.random();
			if(d < .1d)
			{
				galaxyArray[i][3] = .6d + Math.random() * .4d;
				galaxyArray[i][4] = .4d;
				galaxyArray[i][5] = .4d;
			}
			else if(d < .15d)
			{
				galaxyArray[i][3] = 1d;
				galaxyArray[i][4] = 1d;
				galaxyArray[i][5] = 1d;
			}
			else if(d < .35d)
			{
				galaxyArray[i][3] = .7d;
				galaxyArray[i][4] = .7d;
				galaxyArray[i][5] = Math.random() * .05d + .95d;
			}
			else if(d < .55d)
			{
				galaxyArray[i][3] = 1d;
				galaxyArray[i][4] = 1d;
				galaxyArray[i][5] = 1d;
			}
			else
			{
				double d1 = Math.random() * .2d;
				galaxyArray[i][3] = .8d + d1;
				galaxyArray[i][4] = .8d + d1;
				galaxyArray[i][5] = .4d;
			}
			galaxyArray[i][6] = Math.random() * .75 + .25;
		}
		new InfoApplication().run();
	}

	protected static double[] rotateZ(double rotation, double... points)
	{
		double sin = Math.sin(rotation);
		double cos = Math.cos(rotation);

		for (int i = 0; i < points.length; i += 3)
		{
			double x = points[i];
			double y = points[i + 1];

			points[i] = (x * cos - y * sin) * (rotation < 180 ? 1 : -1);
			points[i + 1] = (y * cos + x * sin) * (rotation < 180 ? 1 : -1);
		}

		return points;
	}

	protected static double[] rotateY(double rotation, double... points)
	{
		double sin = Math.sin(rotation);
		double cos = Math.cos(rotation);

		for (int i = 0; i < points.length; i += 3)
		{
			double x = points[i];
			double z = points[i + 2];

			points[i] = (x * cos + z * sin) * (rotation < 180 ? 1 : -1);
			points[i + 2] = (z * cos - x * sin) * (rotation < 180 ? 1 : -1);
		}

		return points;
	}

	protected static double[] rotateX(double rotation, double... points)
	{
		double sin = Math.sin(rotation);
		double cos = Math.cos(rotation);

		for (int i = 0; i < points.length; i += 3)
		{
			double y = points[i + 1];  
			double z = points[i + 2];

			points[i + 1] = (y * cos - z * sin) * (rotation < 180 ? 1 : -1);
			points[i + 2] = (z * cos + y * sin) * (rotation < 180 ? 1 : -1);
		}

		return points;
	}

}
