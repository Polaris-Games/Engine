package com.polaris.engine.gui;

import static com.polaris.engine.render.Window.gl2d;

import com.polaris.engine.Camera;

public abstract class Gui
{
	protected double ticksExisted = 0;
	protected Gui parent;

	protected Camera camera;
	
	public Gui()
	{
		parent = null;
	}

	public Gui(Gui gui)
	{
		parent = gui;
	}

	public void init() {}

	public void update(double delta)
	{
		ticksExisted += delta;
	}

	public void render(double delta)
	{
		gl2d();
	}

	public boolean mouseClick(int mouseId) {return false;}

	public void mouseHeld(int mouseId) {}

	public void mouseRelease(int mouseId) {}

	public void mouseScroll(double xOffset, double yOffset) {}

	public int keyPressed(int keyId, int mods) {return -1;}

	public int keyHeld(int keyId, int called, int mods) {return -1;}

	public void keyRelease(int keyId, int mods) {}

	protected void reinit() {}
	public void reload() {}

	public void close() {}

	protected Gui getParent()
	{
		return parent;
	}

	public Camera getCamera()
	{
		return camera;
	}

}
