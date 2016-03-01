package com.polaris.engine.render;

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
import static org.lwjgl.opengl.ARBShaderObjects.glUseProgramObjectARB;
import static org.lwjgl.opengl.ARBVertexShader.GL_VERTEX_SHADER_ARB;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.PointerBuffer;

import com.polaris.engine.util.Helper;
import com.polaris.engine.util.ResourceHelper;

public class Shader
{

	private int shaderID = 0;

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
		ByteBuffer source = ResourceHelper.ioResourceToByteBuffer(resource, 1024);
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
		
		end();
	}
	
}
