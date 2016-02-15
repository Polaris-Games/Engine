package com.polaris.engine.render;

import static com.polaris.engine.render.Renderer.glClearBuffers;
import static com.polaris.engine.render.Draw.*;
import static com.polaris.engine.render.Renderer.glColor;
import static com.polaris.engine.render.Renderer.glDefaults;
import static com.polaris.engine.render.Window.*;
import static com.polaris.engine.render.Window.getWindowWidth;
import static com.polaris.engine.render.Window.gl2d;
import static org.lwjgl.opengl.EXTFramebufferObject.GL_FRAMEBUFFER_INCOMPLETE_DIMENSIONS_EXT;
import static org.lwjgl.opengl.EXTFramebufferObject.GL_FRAMEBUFFER_INCOMPLETE_FORMATS_EXT;
import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_LINEAR;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.GL_REPEAT;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_S;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_T;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import static org.lwjgl.opengl.GL14.GL_DEPTH_COMPONENT24;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT0;
import static org.lwjgl.opengl.GL30.GL_DEPTH_ATTACHMENT;
import static org.lwjgl.opengl.GL30.GL_DRAW_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER_COMPLETE;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER_INCOMPLETE_MULTISAMPLE;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER_UNSUPPORTED;
import static org.lwjgl.opengl.GL30.GL_RENDERBUFFER;
import static org.lwjgl.opengl.GL30.glBindFramebuffer;
import static org.lwjgl.opengl.GL30.glBindRenderbuffer;
import static org.lwjgl.opengl.GL30.glCheckFramebufferStatus;
import static org.lwjgl.opengl.GL30.glFramebufferRenderbuffer;
import static org.lwjgl.opengl.GL30.glFramebufferTexture2D;
import static org.lwjgl.opengl.GL30.glGenFramebuffers;
import static org.lwjgl.opengl.GL30.glGenRenderbuffers;
import static org.lwjgl.opengl.GL30.glRenderbufferStorage;

public class FrameBuffer 
{
	
	private int frameBufferID = 0;
	private int frameBufferColorID = 0;
	private int frameBufferRenderID = 0;
	
	public static FrameBuffer glGenFramebuffer()
	{
		FrameBuffer buffer = new FrameBuffer();
		return buffer;
	}
	
	private FrameBuffer()
	{
		this(scaleWidth, scaleHeight, GL_COLOR_ATTACHMENT0);
	}
	
	private FrameBuffer(int width, int height, int colorAttachment)
	{
		frameBufferID = glGenFramebuffers();
		frameBufferColorID = glGenTextures();
		frameBufferRenderID = glGenRenderbuffers();
		
		glBindFramebuffer(GL_FRAMEBUFFER, frameBufferID);
		glBindTexture(GL_TEXTURE_2D, frameBufferColorID);
		
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, 0);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		glFramebufferTexture2D(GL_DRAW_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, frameBufferColorID, 0);

		glBindRenderbuffer(GL_RENDERBUFFER, frameBufferRenderID);
		glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH_COMPONENT24, width, height);
		glFramebufferRenderbuffer(GL_DRAW_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_RENDERBUFFER, frameBufferRenderID);
		
		checkFramebufferStatus();
		
		glBindFramebuffer(GL_FRAMEBUFFER, 0);
		glBindTexture(GL_TEXTURE_2D, 0);
		glBindRenderbuffer(GL_RENDERBUFFER, 0);
	}
	
	private void checkFramebufferStatus()
	{
		int status = glCheckFramebufferStatus(GL_FRAMEBUFFER);
		switch(status) 
		{
		case GL_FRAMEBUFFER_COMPLETE:
			break;

		case GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT:
			throw new RuntimeException("An attachment could not be bound to frame buffer object!");

		case GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT:
			throw new RuntimeException("Attachments are missing! At least one image (texture) must be bound to the frame buffer object!");

		case GL_FRAMEBUFFER_INCOMPLETE_DIMENSIONS_EXT:
			throw new RuntimeException("The dimensions of the buffers attached to the currently used frame buffer object do not match!");

		case GL_FRAMEBUFFER_INCOMPLETE_FORMATS_EXT:
			throw new RuntimeException("The formats of the currently used frame buffer object are not supported or do not fit together!");

		case GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER:
			throw new RuntimeException("A Draw buffer is incomplete or undefinied. All draw buffers must specify attachment points that have images attached.");

		case GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER:
			throw new RuntimeException("A Read buffer is incomplete or undefinied. All read buffers must specify attachment points that have images attached.");

		case GL_FRAMEBUFFER_INCOMPLETE_MULTISAMPLE:
			throw new RuntimeException("All images must have the same number of multisample samples.");

		case GL_FRAMEBUFFER_UNSUPPORTED:
			throw new RuntimeException("Attempt to use an unsupported format combinaton!");
		}
	}
	
	public void begin()
	{
		glEnable(GL_TEXTURE_2D);
		glBindTexture(GL_TEXTURE_2D, 0);
		glBindFramebuffer(GL_FRAMEBUFFER, frameBufferID);
		glClearBuffers();
		glDefaults();
		glPushMatrix();
	}
	
	public void end()
	{
		glPopMatrix();
		glBindFramebuffer(GL_FRAMEBUFFER, 0);
		glColor(1, 1, 1, 1);
	}
	
	public void draw(double x, double y, double x1, double y1, double z)
	{
		glClearBuffers();
		glDefaults();
		gl2d();
		glEnable(GL_TEXTURE_2D);
		glBindTexture(GL_TEXTURE_2D, frameBufferColorID);
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		glColor(1, 1, 1, 1);
		glBegin(GL_QUADS);
		rectUV(x, y, x1, y1, z, 0, 1, 1, 0);
		glEnd();
	}

	public int getTextureID()
	{
		return frameBufferColorID;
	}

}
