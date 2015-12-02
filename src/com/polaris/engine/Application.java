package com.polaris.engine;

import static com.polaris.engine.Renderer.glClearBuffers;
import static com.polaris.engine.Renderer.glDefaults;
import static com.polaris.engine.Renderer.updateSize;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;
import static org.lwjgl.glfw.GLFW.GLFW_REPEAT;
import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.glfw.GLFW.glfwDestroyWindow;
import static org.lwjgl.glfw.GLFW.glfwGetCursorPos;
import static org.lwjgl.glfw.GLFW.glfwGetPrimaryMonitor;
import static org.lwjgl.glfw.GLFW.glfwGetTime;
import static org.lwjgl.glfw.GLFW.glfwGetVideoMode;
import static org.lwjgl.glfw.GLFW.glfwGetWindowMonitor;
import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSetCursorEnterCallback;
import static org.lwjgl.glfw.GLFW.glfwSetCursorPosCallback;
import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;
import static org.lwjgl.glfw.GLFW.glfwSetMouseButtonCallback;
import static org.lwjgl.glfw.GLFW.glfwSetScrollCallback;
import static org.lwjgl.glfw.GLFW.glfwSetTime;
import static org.lwjgl.glfw.GLFW.glfwSetWindowCloseCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowFocusCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowIconifyCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowPos;
import static org.lwjgl.glfw.GLFW.glfwSetWindowPosCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowRefreshCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowSizeCallback;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwSwapInterval;
import static org.lwjgl.glfw.GLFW.glfwTerminate;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;
import static org.lwjgl.opengl.GL11.GL_TRUE;

import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.util.HashMap;
import java.util.Map;

import org.lwjgl.glfw.GLFWCursorEnterCallback;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import org.lwjgl.glfw.GLFWScrollCallback;
import org.lwjgl.glfw.GLFWWindowCloseCallback;
import org.lwjgl.glfw.GLFWWindowFocusCallback;
import org.lwjgl.glfw.GLFWWindowIconifyCallback;
import org.lwjgl.glfw.GLFWWindowPosCallback;
import org.lwjgl.glfw.GLFWWindowRefreshCallback;
import org.lwjgl.glfw.GLFWWindowSizeCallback;
import org.lwjgl.glfw.GLFWvidmode;
import org.lwjgl.opengl.GL;

public abstract class Application extends Thread
{

	private static ThreadLocal<Long> instance = new ThreadLocal<Long>() {
		public Long initialValue()
		{
			return 0L;
		}
	};
	/**
	 * Instance version of the application's window instance
	 */
	protected long windowInstance;

	private static ThreadLocal<Double> mousePositionX = new ThreadLocal<Double>() {
		public Double initialValue()
		{
			return 0D;
		}
	};

	private static ThreadLocal<Double> mousePositionY = new ThreadLocal<Double>() {
		public Double initialValue()
		{
			return 0D;
		}
	};
	/**
	 * Instance version of the application's mouse position
	 */
	protected double mouseX;
	/**
	 * Instance version of the application's mouse position
	 */
	protected double mouseY;

	private GLFWCursorEnterCallback cursorBounds = new GLFWCursorEnterCallback () {

		public void invoke(long window, int entered) 
		{
			cursorMoveBounds(entered == GL_TRUE);
		}
	};
	private GLFWCursorPosCallback cursorPos = new GLFWCursorPosCallback () {

		public void invoke(long window, double xpos, double ypos) 
		{
			cursorMove(xpos, ypos);
		}
	};
	private GLFWMouseButtonCallback cursorButton = new GLFWMouseButtonCallback () {

		public void invoke(long window, int button, int action, int mods)
		{
			cursorClick(button, action);
		}
	};
	private GLFWScrollCallback cursorScroll = new GLFWScrollCallback () {

		public void invoke(long window, double xoffset, double yoffset)
		{
			cursorScroll(xoffset, yoffset);
		}
	};
	private GLFWKeyCallback keyboard = new GLFWKeyCallback () {

		public void invoke(long window, int key, int scancode, int action, int mods)
		{
			keyboardClick(key, action);
		}
	};
	private GLFWWindowCloseCallback windowClose = new GLFWWindowCloseCallback () {

		public void invoke(long window) 
		{
			windowClose();
		}
	};
	private GLFWWindowFocusCallback windowFocus = new GLFWWindowFocusCallback () {

		public void invoke(long window, int focused)
		{
			windowFocus(focused == GL_TRUE);
		}
	};
	private GLFWWindowIconifyCallback windowIconify = new GLFWWindowIconifyCallback () {

		public void invoke(long window, int iconified) 
		{
			windowIconify(iconified == GL_TRUE);
		}
	};
	private GLFWWindowPosCallback windowPos = new GLFWWindowPosCallback () {

		public void invoke(long window, int xpos, int ypos)
		{
			windowPos(xpos, ypos);
		}
	};
	private GLFWWindowRefreshCallback windowRefresh = new GLFWWindowRefreshCallback () {

		public void invoke(long window) 
		{
			windowRefresh();
		}
	};
	private GLFWWindowSizeCallback windowSize = new GLFWWindowSizeCallback () {

		public void invoke(long window, int width, int height)
		{
			windowSize(width, height);
		}
	};

	private Map<Integer, Integer> keyboardPress = new HashMap<Integer, Integer>();
	private GUI currentGui;
	private boolean isRunning = true;
	private int fullscreenMode = 0;

	/**
	 * Initializes a window application
	 */
	public void run()
	{
		DoubleBuffer mouseBufferX = DoubleBuffer.allocate(1);
		DoubleBuffer mouseBufferY = DoubleBuffer.allocate(1);
		
		if(glfwInit() == 0 || !setupWindow())
			return;
		init();

		GL.createCapabilities();
		glDefaults();
		glfwSetTime(0);
		while(glfwWindowShouldClose(windowInstance) == 0 && isRunning)
		{
			double delta = glfwGetTime();
			glfwSetTime(0);

			if((glfwGetWindowMonitor(windowInstance) == 0) != (fullscreenMode == 0))
			{
				glfwDestroyWindow(windowInstance);
				if(!setupWindow())
					return;
			}
			
			glfwGetCursorPos(Application.getInstance(), mouseBufferX, mouseBufferY);
			mouseX = mouseBufferX.get();
			mouseY = mouseBufferY.get();
			mousePositionX.set(mouseX);
			mousePositionY.set(mouseY);
			glfwPollEvents();

			SoundManager.update();
			update(getMouseX(), getMouseY(), delta);

			glClearBuffers();
			render(getMouseX(), getMouseY(), delta);
			glfwSwapBuffers(windowInstance);
		}
		glfwDestroyWindow(windowInstance);
		GL.destroy();
		glfwTerminate();
	}

	/**
	 * sets up the environment for a window to be created.
	 * @return true for success, false otherwise
	 */
	private boolean setupWindow()
	{
		windowInstance = createWindow();
		if(windowInstance == 0)
		{
			glfwTerminate();
			return false;
		}
		instance.set(windowInstance);
		setWindowEvents();
		glfwMakeContextCurrent(windowInstance);
		glfwSwapInterval(1);
		updateSize(windowInstance);
		glfwShowWindow(windowInstance);
		return true;
	}

	/**
	 * creates the window events that handle input and window changes
	 */
	private void setWindowEvents()
	{
		glfwSetCursorEnterCallback(windowInstance, cursorBounds);
		glfwSetCursorPosCallback(windowInstance, cursorPos);
		glfwSetMouseButtonCallback(windowInstance, cursorButton);
		glfwSetScrollCallback(windowInstance, cursorScroll);
		glfwSetKeyCallback(windowInstance, keyboard);
		glfwSetWindowCloseCallback(windowInstance, windowClose);
		glfwSetWindowFocusCallback(windowInstance, windowFocus);
		glfwSetWindowIconifyCallback(windowInstance, windowIconify);
		glfwSetWindowPosCallback(windowInstance, windowPos);
		glfwSetWindowRefreshCallback(windowInstance, windowRefresh);
		glfwSetWindowSizeCallback(windowInstance, windowSize);
	}

	/**
	 * @param newGui : the new gui the screen will adopt, if set to null then the application will close.
	 */
	public void setGui(GUI newGui)
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

	/**
	 * close the application
	 */
	public void close()
	{
		isRunning = false;
	}

	/**
	 * @param fullscreen : 0 for non-fullscreen, 1 for windowed fullscreen, 2 for fullscreen
	 */
	public void setFullscreen(int fullscreen)
	{
		fullscreenMode = fullscreen;
	}

	/**
	 * @return 0 for non-fullscreen, 1 for windowed fullscreen, 2 for fullscreen
	 */
	public int getFullscreenMode()
	{
		return fullscreenMode;
	}

	/**
	 * when the mouse leaves the window, or enters
	 * @param entered : true if mouse enters window
	 */
	protected void cursorMoveBounds(boolean entered) {}

	/**
	 * when the mouse moves in the window
	 * @param mouseX : new mouse x
	 * @param mouseY : new mouse y
	 */
	protected void cursorMove(double mouseX, double mouseY) {}

	/**
	 * when the mouse clicks
	 * <br><b>DON'T CALL super.cursorClick(button, action) UNLESS YOU IMPLEMENT GUI CLASS STRUCTURE</b>
	 * @param button : the mouse button
	 * @param action : type of click, GLFW_PRESS, GLFW_RELEASE, GLFW_REPEAT
	 */
	protected void cursorClick(int button, int action) 
	{
		switch(action)
		{
		case GLFW_PRESS:
			currentGui.mouseClick(mouseX, mouseY, button);
			break;
		case GLFW_REPEAT:
			currentGui.mouseHeld(mouseX, mouseY, button);
			break;
		case GLFW_RELEASE:
			currentGui.mouseRelease(mouseX, mouseY, button);
		}
	}

	/**
	 * when the mouse wheel scrolls
	 * <br><b>DON'T CALL super.cursorScroll(xOffset, yOffset) UNLESS YOU IMPLEMENT GUI CLASS STRUCTURE</b>
	 * @param xOffset : mouse wheel offset x
	 * @param yOffset : mouse wheel offset y
	 */
	protected void cursorScroll(double xOffset, double yOffset) 
	{
		currentGui.mouseScroll(mouseX, mouseY, xOffset, yOffset);
	}

	/**
	 * when the keyboard clicks
	 * <br><b>DON'T CALL super.keyboardClick(key, action) UNLESS YOU IMPLEMENT GUI CLASS STRUCTURE</b>
	 * @param key : the key id
	 * @param action : type of click, GLFW_PRESS, GLFW_RELEASE, GLFW_REPEAT
	 */
	protected void keyboardClick(int key, int action) 
	{
		switch(action)
		{
		case GLFW_PRESS:
			int i = currentGui.keyPressed(key);
			if(i > 0)
			{
				keyboardPress.put(key, i);
			}
			break;
		case GLFW_REPEAT:
			int j = keyboardPress.get(key);
			int k = j & 0x0000FFFF;
			j >>= 16;
			if(k-- <= 0)
			{
				currentGui.keyHeld(key, j);
				j++;
				if(k <= 0)
				{
					keyboardPress.remove(key);
					break;
				}
			}
			keyboardPress.put(key, (j << 16) | k);
			break;
		case GLFW_RELEASE:
			currentGui.keyRelease(key);
			keyboardPress.remove(key);
		}
	}

	/**
	 * when the window closes
	 */
	private void windowClose() 
	{
		this.cursorBounds.release();
		this.cursorButton.release();
		this.cursorPos.release();
		this.cursorScroll.release();
		this.keyboard.release();
		this.windowClose.release();
		this.windowFocus.release();
		this.windowIconify.release();
		this.windowPos.release();
		this.windowRefresh.release();
		this.windowSize.release();
		SoundManager.release();
	}

	/**
	 * when the window focus changes
	 * @param focused : if the window focuses
	 */
	protected void windowFocus(boolean focused) {}

	/**
	 * when the window iconify changes
	 * @param iconified : if the window iconifies
	 */
	protected void windowIconify(boolean iconified) {}

	/**
	 * when the windows position changes
	 * @param xPos : new pos x of window
	 * @param yPos : new pos y of window
	 */
	protected void windowPos(int xPos, int yPos) {}

	/**
	 * when the window is refreshed
	 */
	protected void windowRefresh() {}

	/**
	 * when the windows size changes
	 * @param width : new width
	 * @param height : new height
	 */
	protected void windowSize(int width, int height) 
	{
		updateSize(windowInstance);
	}

	/**
	 * Update method called every n times / second 
	 * <br><b>DON'T CALL super.update(mouseX, mouseY, delta) UNLESS YOU IMPLEMENT GUI CLASS STRUCTURE</b>
	 * @param mouseX : current Mouse Position, updates before method call
	 * @param mouseY : current Mouse Position, updates before method call
	 * @param delta : change in time, measured in actual seconds
	 */
	protected void update(double mouseX, double mouseY, double delta) 
	{
		currentGui.update(mouseX, mouseY, delta);
	}

	/**
	 * Render method capped at n times / second
	 * <br><b>DON'T CALL super.render(mouseX, mouseY, delta) UNLESS YOU IMPLEMENT GUI CLASS STRUCTURE</b>
	 * @param mouseX : current Mouse Position, updates before method call
	 * @param mouseY : current Mouse Position, updates before method call
	 * @param delta : change in time, measured in actual seconds
	 */
	protected void render(double mouseX, double mouseY, double delta) 
	{
		currentGui.render(mouseX, mouseY, delta);
	}

	/**
	 * initialize window
	 */
	protected abstract void init();

	/**
	 * create the window
	 */
	protected abstract long createWindow();

	/**
	 * Set hints to window and others mostly not used with this method
	 * @param target <br><b>WINDOW</b>
	 * <br> - <b>GLFW_FOCUSED</b> : window is focused (D TRUE)
	 * <br> - <b>GLFW_ICONIFIED</b> : window is minimized (D FALSE)
	 * <br> - <b>GLFW_VISIBLE</b> : window is visible (D TRUE)
	 * <br> - <b>GLFW_RESIZEABLE</b> : window is resizeable (D TRUE)
	 * <br> - <b>GLFW_DECORATED</b> : window has borders (D TRUE)
	 * <br> - <b>GLFW_FLOATING</b> : window is always-on-top (D FALSE)
	 * @param value : true or false
	 */
	protected static void setHint(int target, boolean value)
	{
		glfwWindowHint(target, value ? 1 : 0);
	}

	/**
	 * Centers the window and returns its instance long variable
	 * @param width : window width
	 * @param height : window height
	 * @param title : window title
	 * @param monitor : window monitor
	 * @param share : window share
	 * @return
	 */
	protected static long createAndCenter(int width, int height, String title, int monitor, int share)
	{
		long instance = glfwCreateWindow(width, height, title, monitor, share);
		ByteBuffer buffer = glfwGetVideoMode(glfwGetPrimaryMonitor());
		glfwSetWindowPos(instance, (GLFWvidmode.width(buffer) - width) / 2, (GLFWvidmode.height(buffer) - height) / 2);
		return instance;
	}

	/**
	 * @return windows instance
	 */
	public long getWindowInstance()
	{
		return windowInstance;
	}

	/**
	 * @return mouse position
	 */
	public double getMousePosX()
	{
		return mouseX;
	}

	/**
	 * @return mouse position
	 */
	public double getMousePosY()
	{
		return mouseY;
	}

	/**
	 * Thread specific version of the application's window instance.
	 * <br><b>ONLY WORKS WITH MAIN THREAD, OTHERWISE REROUTE TO INSTANCE VERSION</b>
	 * @return window instance
	 */
	public static long getInstance() 
	{
		return instance.get();
	}

	/**
	 * Thread specific version of the application's mouse position.
	 * <br><b>ONLY WORKS WITH MAIN THREAD, OTHERWISE REROUTE TO INSTANCE VERSION</b>
	 * @return mouse position
	 */
	public static double getMouseX()
	{
		return mousePositionX.get();
	}

	/**
	 * Thread specific version of the application's mouse position.
	 * <br><b>ONLY WORKS WITH MAIN THREAD, OTHERWISE REROUTE TO INSTANCE VERSION</b>
	 * @return mouse position
	 */
	public static double getMouseY()
	{
		return mousePositionY.get();
	}

}
