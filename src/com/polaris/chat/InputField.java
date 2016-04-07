package com.polaris.chat;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glEnd;

import org.lwjgl.glfw.GLFW;

import com.polaris.engine.gui.element.TextField;
import com.polaris.engine.options.Settings;
import com.polaris.engine.render.FontMap;
import com.polaris.engine.render.OpenGL;
import com.polaris.engine.render.Texture;
import com.polaris.engine.render.Window;

public class InputField extends TextField
{
	private FontMap fontMap;

	public InputField()
	{
		super(20, 1000, 1880, 80);
		fontMap = Texture.getFontMap("basic");
		nullText = "Text Here";
	}

	public void render(double delta)
	{

		glEnd();
		glEnable(GL_TEXTURE_2D);
		Texture.glBindTexture("font:basic");
		if(nullText != null && text.length() == 0)
		{
			OpenGL.glColor(0, 0, 0, .2);
			fontMap.drawAlignedFittedString(nullText, position.x + elementWidth / 2, position.y, 1, 1, elementWidth, elementHeight);
		}
		else
		{
			OpenGL.glColor(0, 0, 0, .5);
			fontMap.drawAlignedFittedString(text, position.x + elementWidth / 2, position.y, 1, 1, elementWidth, elementHeight);
		}
		glDisable(GL_TEXTURE_2D);
	}

	public void update(double delta)
	{
		super.update(delta);
		highlighted = true;
	}

	public int keyPressed(int keyId, int mods)
	{
		char key = (char) keyId;
		if(keyId == GLFW_KEY_ESCAPE)
		{
			if((mods & 1) == 1)
				Settings.setNextWindow(Settings.getNextWindow() < 2 ? 2 : 0);
			else
				Window.close();
		}
		else if(keyId == GLFW.GLFW_KEY_ENTER)
		{
			gui.unbindCurrentElement();
			return 1;
		}
		else if((keyId == GLFW.GLFW_KEY_BACKSPACE || keyId == GLFW.GLFW_KEY_DELETE))
		{
			if(text.length() > 0)
			{
				text = text.substring(0, text.length() - 1);
			}
			return (10 << 16);
		}
		else if(text.length() < 200 && key >= 32 && key <= 176)
		{
			text += key;
		}
		return 0;
	}

	public int keyHeld(int keyId, int called, int mods)
	{
		if((keyId == GLFW.GLFW_KEY_BACKSPACE || keyId == GLFW.GLFW_KEY_DELETE))
		{
			if(text.length() > 0)
			{
				text = text.substring(0, text.length() - 1);
			}
			return (10 - Math.min(called, 9) << 16);
		}
		return 0;
	}
}
