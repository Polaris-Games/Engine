package com.polaris.engine;

import static com.polaris.engine.options.Settings.getNextWindow;
import static com.polaris.engine.render.Texture.loadContent;
import static com.polaris.engine.render.Texture.getContent;
import static com.polaris.engine.render.Texture.reinitContent;
import static com.polaris.engine.render.Window.close;
import static com.polaris.engine.render.Window.create;
import static com.polaris.engine.render.Window.destroy;
import static com.polaris.engine.render.Window.getCurrentWindow;
import static com.polaris.engine.render.Window.getKey;
import static com.polaris.engine.render.Window.getTimeAndReset;
import static com.polaris.engine.render.Window.pollEvents;
import static com.polaris.engine.render.Window.setTime;
import static com.polaris.engine.render.Window.setupWindow;
import static com.polaris.engine.render.Window.shouldClose;
import static com.polaris.engine.render.Window.swapBuffers;
import static com.polaris.engine.render.Window.updateSize;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GLCapabilities;

import com.polaris.engine.gui.Gui;
import com.polaris.engine.options.Settings;
import com.polaris.engine.sound.SoundManager;

public abstract class Application
{

	/**
	 * Instance version of the application's mouse position
	 */
	protected static double mouseX;
	/**
	 * Instance version of the application's mouse position
	 */
	protected static double mouseY;

	protected static double mouseDeltaX;
	protected static double mouseDeltaY;

	private Map<Integer, Integer> keyboardPress = new HashMap<Integer, Integer>();
	protected Gui currentGui;

	public SoundManager soundManager;
	private GLCapabilities glCapabilities;

	/**
	 * Initializes a window application
	 */
	public void run()
	{
		Settings.load(getResourceLocation());
		if(create() || setupWindow(this) == -1)
			return;

		glCapabilities = GL.createCapabilities();
		if(glCapabilities == null || !checkOpenGL())
			throw new RuntimeException("OpenGL Creation Failed, an updated OpenGL version is required.");

		try
		{
			loadContent();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return;
		}

		init();
		setTime(0);
		//soundManager = new SoundManager(this); 

		while(shouldClose())
		{
			double delta = getTimeAndReset();

			if(getNextWindow() != getCurrentWindow())
			{
				Map<String, ByteBuffer> data = getContent();
				if(setupWindow(this) == -1)
					return;
				reinitContent(data);
			}
			pollEvents();

			for(Integer key : keyboardPress.keySet())
			{
				int j = keyboardPress.get(key);
				int k = j & 0x0000FFFF;
				j >>= 16;
				if(getKey(key) == 1)
				{
					if(k-- <= 0)
					{
						k = currentGui.keyHeld(key, j);
						j++;
						if(k <= 0)
						{
							keyboardPress.remove(key);
							break;
						}
					}
					keyboardPress.put(key, (j << 16) | k);
				}
				else
				{
					currentGui.keyRelease(key, 0);
					keyboardPress.remove(key);
				}
			}

			update(delta);
			render(delta);
			mouseDeltaX = mouseDeltaY = 0;
			swapBuffers();
		}

		destroy();
	}

	/**
	 * @param newGui : the new gui the screen will adopt, if set to null then the application will close.
	 */
	public void setGui(Gui newGui)
	{
		if(newGui == null)
		{
			close();
			return;
		}
		if(currentGui != null)
		{
			currentGui.close();
		}
		newGui.init();
		currentGui = newGui;
	}


	
	protected boolean checkOpenGL()
	{
		return glCapabilities.OpenGL33;
	}

	/**
	 * when the mouse leaves the window, or enters
	 * @param entered : true if mouse enters window
	 */
	public void cursorMoveBounds(boolean entered) {}

	/**
	 * when the mouse moves in the window
	 * @param mouseX : new mouse x
	 * @param mouseY : new mouse y
	 */
	public void cursorMove(double mX, double mY) 
	{
		mouseDeltaX = mX - mouseX;
		mouseDeltaY = mY - mouseY;
		mouseX = mX;
		mouseY = mY;
	}

	/**
	 * when the mouse clicks
	 * <br><b>DON'T CALL super.cursorClick(button, action) UNLESS YOU IMPLEMENT GUI CLASS STRUCTURE</b>
	 * @param button : the mouse button
	 * @param action : type of click, GLFW_PRESS, GLFW_RELEASE, GLFW_REPEAT
	 */
	public void cursorClick(int button, int action) 
	{
		switch(action)
		{
		case 1:
			currentGui.mouseClick(button);
			break;
		case 2:
			currentGui.mouseHeld(button);
			break;
		case 0:
			currentGui.mouseRelease(button);
		}
	}

	/**
	 * when the mouse wheel scrolls
	 * <br><b>DON'T CALL super.cursorScroll(xOffset, yOffset) UNLESS YOU IMPLEMENT GUI CLASS STRUCTURE</b>
	 * @param xOffset : mouse wheel offset x
	 * @param yOffset : mouse wheel offset y
	 */
	public void cursorScroll(double xOffset, double yOffset) 
	{
		currentGui.mouseScroll(xOffset, yOffset);
	}

	/**
	 * when the keyboard clicks
	 * <br><b>DON'T CALL super.keyboardClick(key, action) UNLESS YOU IMPLEMENT GUI CLASS STRUCTURE</b>
	 * @param key : the key id
	 * @param action : type of click, GLFW_PRESS, GLFW_RELEASE, GLFW_REPEAT
	 * @param mods 
	 */
	public void keyboardClick(int key, int action, int mods) 
	{
		switch(action)
		{
		case 1:
			int i = currentGui.keyPressed(key, mods);
			if(i > 0)
			{
				keyboardPress.put(key, i);
			}
			break;
		case 0:
			if(!keyboardPress.containsKey(key))
			{
				currentGui.keyRelease(key, mods);
			}
		}
	}

	/**
	 * when the window closes
	 */
	public void windowClose() {soundManager.isRunning = false;}

	/**
	 * when the window focus changes
	 * @param focused : if the window focuses
	 */
	public void windowFocus(boolean focused) {}

	/**
	 * when the window iconify changes
	 * @param iconified : if the window iconifies
	 */
	public void windowIconify(boolean iconified) {}

	/**
	 * when the windows position changes
	 * @param xPos : new pos x of window
	 * @param yPos : new pos y of window
	 */
	public void windowPos(int xPos, int yPos) {}

	/**
	 * when the window is refreshed
	 */
	public void windowRefresh() {}

	/**
	 * when the windows size changes
	 * @param width : new width
	 * @param height : new height
	 */
	public void windowSize(int width, int height) 
	{
		if(width + height != 0)
			updateSize();
	}

	/**
	 * Update method called every n times / second 
	 * <br><b>DON'T CALL super.update(mouseX, mouseY, delta) UNLESS YOU IMPLEMENT GUI CLASS STRUCTURE</b>
	 * @param mouseX : current Mouse Position, updates before method call
	 * @param mouseY : current Mouse Position, updates before method call
	 * @param delta : change in time, measured in actual seconds
	 */
	public void update(double delta) 
	{
		currentGui.update(delta);
	}

	/**
	 * Render method capped at n times / second
	 * <br><b>DON'T CALL super.render(mouseX, mouseY, delta) UNLESS YOU IMPLEMENT GUI CLASS STRUCTURE</b>
	 * @param mouseX : current Mouse Position, updates before method call
	 * @param mouseY : current Mouse Position, updates before method call
	 * @param delta : change in time, measured in actual seconds
	 */
	public void render(double delta) 
	{
		currentGui.render(delta);
	}

	/**
	 * initialize window
	 */
	protected abstract void init();

	/**
	 * create the window
	 */
	public abstract long createWindow();

	protected String getResourceLocation()
	{
		return "resources";
	}

	public Gui getCurrentScreen()
	{
		return currentGui;
	}

	/**
	 * @return mouse position
	 */
	public static double getMouseX()
	{
		return mouseX;
	}

	/**
	 * @return mouse position
	 */
	public static double getMouseY()
	{
		return mouseY;
	}

	public static double getMouseDeltaX()
	{
		return mouseDeltaX;
	}

	public static double getMouseDeltaY()
	{
		return mouseDeltaY;
	}

}
