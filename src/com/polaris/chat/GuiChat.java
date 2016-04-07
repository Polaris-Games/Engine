package com.polaris.chat;
import static com.polaris.engine.render.Draw.rect;
import static com.polaris.engine.render.OpenGL.glBegin;
import static com.polaris.engine.render.OpenGL.glBlend;
import static com.polaris.engine.render.OpenGL.glColor;
import static org.lwjgl.opengl.GL11.glEnd;

import java.net.Socket;

import com.polaris.engine.App;
import com.polaris.engine.gui.Gui;
import com.polaris.engine.gui.element.Element;
import com.polaris.engine.util.Color4d;

public class GuiChat extends Gui
{
	
	private InputField input = new InputField();

	public GuiChat(App app, Socket serverSocket)
	{
		super(app);
		this.addElement(input);
	}
	
	public void render(double delta)
	{
		glBlend();
		glColor(new Color4d(137, 105, 0, 255));
		glBegin();
		rect(0, 0, 1920, 1080, -100);
		glEnd();
		super.render(delta);
	}
	
	public void elementUpdate(Element element, int actionId)
	{
		if(actionId == 4)
		{
			
		}
	}
	
	public int keyPressed(int keyId, int mods)
	{
		return input.nKeyPressed(keyId, mods);
	}

}
