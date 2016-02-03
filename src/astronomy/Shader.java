package astronomy;

import static org.lwjgl.opengl.ARBShaderObjects.*;
import static org.lwjgl.opengl.ARBVertexShader.*;
import static org.lwjgl.opengl.ARBFragmentShader.*;
import static org.lwjgl.opengl.ARBSeamlessCubeMap.*;
import static org.lwjgl.opengl.ARBTextureCubeMap.*;
import static org.lwjgl.opengl.EXTTextureFilterAnisotropic.*;
import static com.polaris.engine.render.Renderer.windowHeight;
import static com.polaris.engine.render.Renderer.windowWidth;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.stb.STBImage.*;
import static org.lwjgl.system.MemoryUtil.*;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.PointerBuffer;
import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.GL13;

import com.polaris.engine.Application;
import com.polaris.engine.util.Helper;

public class Shader
{

	private int shaderID = 0;
	private int time = 0;
	private int deltaTime = 0;
	private int mouse = 0;
	private int resolution = 0;

	public Shader(String resourceLocation)
	{
		try
		{
			int vshader = createShader(resourceLocation + ".vs", GL_VERTEX_SHADER_ARB);
			int fshader = createShader(resourceLocation + ".fs", GL_FRAGMENT_SHADER_ARB);

			shaderID = glCreateProgramObjectARB();
	        glAttachObjectARB(shaderID, vshader);
	        glAttachObjectARB(shaderID, fshader);
	        glLinkProgramARB(shaderID);
	        
	        int linked = glGetObjectParameteriARB(shaderID, GL_OBJECT_LINK_STATUS_ARB);
	        String programLog = glGetInfoLogARB(shaderID);
	        if (programLog.trim().length() > 0) 
	        {
	            System.err.println(programLog);
	        }
	        
	        if (linked == 0)
	        {
	        	shaderID = 0;
	            throw new AssertionError("Could not link program");
	        }
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}

	private int createShader(String resource, int type) throws IOException
	{
		int shader = glCreateShaderObjectARB(type);
		ByteBuffer source = Helper.ioResourceToByteBuffer(resource, 1024);
		PointerBuffer strings = BufferUtils.createPointerBuffer(1);
		IntBuffer lengths = BufferUtils.createIntBuffer(1);
		strings.put(0, source);
		lengths.put(0, source.remaining());
		glShaderSourceARB(shader, strings, lengths);
		glCompileShaderARB(shader);
		int compiled = glGetObjectParameteriARB(shader, GL_OBJECT_COMPILE_STATUS_ARB);
		String shaderLog = glGetInfoLogARB(shader);
		if (shaderLog.trim().length() > 0) 
		{
			System.err.println(shaderLog);
		}
		if (compiled == 0) 
		{
			throw new AssertionError("Could not compile shader");
		}
		return shader;
	}
	
	public void begin(float ticks, float delta)
	{
		glUseProgramObjectARB(shaderID);
		
		glUniform3fARB(resolution, windowWidth, windowHeight, 0);
		glUniform1fARB(time,  ticks);
		glUniform1fARB(deltaTime, delta);
		glUniform4fARB(mouse, (float) Application.getMouseX(), (float) Application.getMouseY(), 0, 0);
	}
	
	public void end()
	{
		glUseProgramObjectARB(0);
	}
	
	public int getVariable(String varName)
	{
		return glGetUniformLocationARB(shaderID, varName);
	}
	
	public void load()
	{
		glUseProgramObjectARB(shaderID);

		int texLocation = getVariable("iChannel0");

		glUniform1iARB(texLocation, 1);

		texLocation = getVariable("iChannel1");
		time = getVariable("iGlobalTime");
		deltaTime = getVariable("iTimeDelta");
		mouse = getVariable("iMouse");
		resolution = getVariable("iResolution");
		
		glUniform1iARB(texLocation, 2);
		
		end();
	}
	
}
