package com.polaris.chat;


import static com.polaris.engine.render.Draw.rect;
import static com.polaris.engine.render.Draw.rectUV;
import static com.polaris.engine.render.Draw.rectUV90;
import static com.polaris.engine.render.OpenGL.glBegin;
import static com.polaris.engine.render.OpenGL.glBlend;
import static com.polaris.engine.render.OpenGL.glColor;
import static com.polaris.engine.render.Texture.glBindSubTexture;
import static com.polaris.engine.render.Texture.glBindTexture;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glRotated;
import static org.lwjgl.opengl.GL11.glTranslated;

import java.io.IOException;
import java.net.Socket;

import org.lwjgl.glfw.GLFW;

import com.polaris.engine.App;
import com.polaris.engine.Application;
import com.polaris.engine.gui.Gui;
import com.polaris.engine.gui.element.Element;
import com.polaris.engine.network.PacketSecure;
import com.polaris.engine.render.FontMap;
import com.polaris.engine.render.Texture;
import com.polaris.engine.util.Color4d;
import com.polaris.engine.util.MathHelper;

public class GuiLogin extends Gui
{

	private FontMap map;
	private UsernameField username;
	private PasswordField password;
	private double errorTicks = 0;
	private String errorText = "";
	public double arrowYCoord = -280;
	public int loadingPhase = 0;
	public double loadingTicks = 0;
	private Socket serverSocket;

	public GuiLogin(App app) 
	{
		super(app);
		username = new UsernameField();
		password = new PasswordField();
		map = Texture.getFontMap("basic");
		this.addElement(username);
		this.addElement(password);
	}

	public void render(double delta)
	{
		glBlend();
		glColor(new Color4d(240, 188, 17, 255));
		glBegin();
		if(loadingPhase < 2)
			rect(0, 0, 1920, 1080, -100);
		else
			rect(0, 0, 1920, arrowYCoord * 2, -100);
		glColor(new Color4d(137, 105, 0, 255));
		if(loadingPhase < 2)
			rect(0, 950, 1920, 1080, -100);
		else
			rect(0, arrowYCoord * 1.75926, 1920, 1080, -100);
		glColor(new Color4d(240, 240, 240, 176));
		rect(810, arrowYCoord - 73, 1110, arrowYCoord - 70, -1);
		rect(810, 73 + arrowYCoord, 1110, 70 + arrowYCoord, -1);
		glEnd();

		glEnable(GL_TEXTURE_2D);
		glBindTexture("login", "tripleArrow");
		glBegin();
		rectUV90(920, arrowYCoord - 140, 1000, arrowYCoord - 90, -1);
		rectUV90(920, 140 + arrowYCoord, 1000, 90 + arrowYCoord, -1);
		glEnd();
		if(loadingPhase == 1)
		{
			glPushMatrix();
			glTranslated(960, 1080 + 200 - arrowYCoord, 2);
			glBindSubTexture("throbber3");
			glRotated(loadingTicks * 360, 0, 0, 1);
			glBegin();
			rectUV(-40, -40, 40, 40, 0);
			glEnd();
			glRotated(-loadingTicks * 720, 0, 0, 1);
			glBindSubTexture("throbber2");
			glBegin();
			rectUV(-40, -40, 40, 40, 0);
			glEnd();
			glRotated(-loadingTicks * 360, 0, 0, 1);
			glBindSubTexture("throbber1");
			glBegin();
			rectUV(-40, -40, 40, 40, 0);
			glEnd();
			glPopMatrix();
		}
		if(errorTicks > 0)
		{
			glBindTexture("font:basic");
			glColor(1, 1, 1, Math.min(1, errorTicks));
			map.drawAlignedString(errorText, 960, arrowYCoord - 200, -1, 32, 1);
		}
		glDisable(GL_TEXTURE_2D);
		super.render(delta);
	}

	public void update(double delta)
	{
		super.update(delta);
		arrowYCoord = MathHelper.getExpValue(arrowYCoord, 540 - (loadingPhase == 2 ? 820 : 0), .25, delta);
		if(loadingPhase == 2 && arrowYCoord < -140)
		{
			this.application.setGui(new GuiChat(this.application, serverSocket));
		}
		loadingTicks += delta;
		errorTicks = errorTicks <= 1 ? MathHelper.getExpValue(errorTicks, 0, .15, delta) : MathHelper.getLinearValue(errorTicks, 0, 1, delta);
	}

	public void elementUpdate(Element element, int actionId)
	{
		if(actionId == 4)
		{
			loadingPhase = 1;
			new Thread()
			{
				public void run()
				{
					try
					{
						((Application) application).getNetwork().connect("localhost", 8888);
						((Application) application).getNetwork().validate(true);
						((Application) application).getNetwork().sendSecurePacket(new PacketContent("Hello this is a test"));
						loadingPhase = 2;
					} 
					catch (IOException e) 
					{
						loadingPhase = 0;
						errorTicks = 5;
						errorText = "Sorry, could not connect to server...";
					}
				}
			}.start();
		}
	}
	
	public int keyPressed(int keyId, int mods)
	{
		if(currentElement == null)
		{
			if(keyId == GLFW.GLFW_KEY_TAB)
			{
				this.setCurrentElement(0);
				return 0;
			}
		}
		return super.keyPressed(keyId, mods);
	}
}
