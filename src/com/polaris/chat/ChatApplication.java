package com.polaris.chat;
import java.io.IOException;

import org.lwjgl.glfw.GLFW;

import com.polaris.engine.Application;
import com.polaris.engine.network.client.Client;
import com.polaris.engine.render.Window;

public class ChatApplication extends Application
{

	public static Client connectionToServer;

	public static void main(String[] args) throws IOException
	{
		ChatApplication chat = new ChatApplication(ClientNetwork.clientNetwork);
		chat.run();
		if(connectionToServer != null)
		{
			connectionToServer.invalidate();
		}
		System.exit(0);
	}
	
	public ChatApplication(ClientNetwork clientNetwork) 
	{
		super(clientNetwork);
	}

	@Override
	protected void init() 
	{
		this.setGui(new GuiLogin(this));
	}

	@Override
	public long createWindow()
	{
		Window.setHint(GLFW.GLFW_RESIZABLE, true);
		Window.setHint(GLFW.GLFW_DECORATED, false);
		return Window.createAndCenter(1280, 720, "Chat System", 0);
	}

}
