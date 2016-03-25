package com.polaris.chat;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWVidMode;

import com.polaris.engine.Application;
import com.polaris.engine.network.Packet;
import com.polaris.engine.network.PacketRequestLock;
import com.polaris.engine.network.client.Client;
import com.polaris.engine.render.Window;

public class ChatApplication extends Application
{
	
	public static Client connectionToServer;
	
	public static void main(String[] args)
	{
		ChatApplication chat = new ChatApplication();
		chat.run();
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
