package astronomy;

import static astronomy.InfoApplication.galaxyArray;
import static com.polaris.engine.render.Renderer.*;
import static com.polaris.engine.render.Renderer.glColor;
import static org.lwjgl.opengl.ARBFragmentShader.GL_FRAGMENT_SHADER_ARB;
import static org.lwjgl.opengl.ARBShaderObjects.GL_OBJECT_COMPILE_STATUS_ARB;
import static org.lwjgl.opengl.ARBShaderObjects.GL_OBJECT_LINK_STATUS_ARB;
import static org.lwjgl.opengl.ARBShaderObjects.glAttachObjectARB;
import static org.lwjgl.opengl.ARBShaderObjects.glCompileShaderARB;
import static org.lwjgl.opengl.ARBShaderObjects.glCreateProgramObjectARB;
import static org.lwjgl.opengl.ARBShaderObjects.glCreateShaderObjectARB;
import static org.lwjgl.opengl.ARBShaderObjects.glGetInfoLogARB;
import static org.lwjgl.opengl.ARBShaderObjects.glGetObjectParameteriARB;
import static org.lwjgl.opengl.ARBShaderObjects.glGetUniformLocationARB;
import static org.lwjgl.opengl.ARBShaderObjects.glLinkProgramARB;
import static org.lwjgl.opengl.ARBShaderObjects.glShaderSourceARB;
import static org.lwjgl.opengl.ARBShaderObjects.*;
import static org.lwjgl.opengl.ARBShaderObjects.glUseProgramObjectARB;
import static org.lwjgl.opengl.ARBVertexShader.GL_VERTEX_SHADER_ARB;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL30.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;

import javax.imageio.ImageIO;

import org.lwjgl.BufferUtils;
import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;

import com.polaris.engine.Application;
import com.polaris.engine.gui.Gui;
import com.polaris.engine.render.FontMap;
import com.polaris.engine.render.Renderer;
import com.polaris.engine.util.Helper;

public class GuiAstronomy extends Gui
{

	protected double ticks = 0;

	private FrameBuffer frame;
	private Shader starPhase0;
	private Shader starPhase1;
	private Shader starPhase2;

	public GuiAstronomy(Application application)
	{
		super(application);
	}

	public void init()
	{
		frame = new FrameBuffer();
		starPhase0 = new Shader("astronomy/shader/starPhase0");
		starPhase1 = new Shader("astronomy/shader/starPhase1");
		starPhase2 = new Shader("astronomy/shader/starPhase2");

		starPhase0.load();
		starPhase1.load();
		starPhase2.load();

		try
		{
			GL13.glActiveTexture(GL13.GL_TEXTURE1);
			glBindTexture(GL_TEXTURE_2D, Renderer.createTextureId(ImageIO.read(Helper.getResourceStream("astronomy/shader/star0.png")), false));
			GL13.glActiveTexture(GL13.GL_TEXTURE2);
			glBindTexture(GL_TEXTURE_2D, Renderer.createTextureId(ImageIO.read(Helper.getResourceStream("astronomy/shader/star1.png")), false));
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}

		GL13.glActiveTexture(GL13.GL_TEXTURE0);
	}

	public void render(double delta)
	{
		glColor(0, 0, 0, 1);
		glClearBuffers();
		glDefaults();
		ticks += delta;
		glEnable(GL_BLEND);

		gl2d();

		if(ticks > 100)
		{
			starPhase2.begin((float) ticks, (float) delta);
			glBegin();
			drawRect(0, 0, 1920, 1080, -1);
			glEnd();
			starPhase0.end();
		}


		if(ticks < 101)
		{
			if(ticks < 36)
				starPhase0.begin((float)ticks, (float)delta);
			else
				starPhase1.begin((float)ticks, (float)delta);

			glBegin();
			drawRect(0, 0, 1920, 1080, 0);
			glEnd();
			starPhase0.end();
		}

		glBindTexture(0);
		super.render(delta);
	}

}
