package com.polaris.engine.gui;

import static com.polaris.engine.Application.*;
import com.polaris.engine.Pos;

public abstract class Element 
{
	
	protected Pos pos;
	protected double elementWidth = 0;
	protected double elementHeight = 0;
	protected int elementId = 0;
	protected int ticksExisted = 0;
	protected Gui gui;
	protected boolean highlighted = false;
	
	public Element(double x, double y, double width, double height)
	{
		pos = new Pos(x, y);
		elementWidth = width;
		elementHeight = height;
	}

	public void update(double delta)
	{
		ticksExisted++;
		highlighted = isInRegion();
	}
	
	public abstract void render(double delta);
	
	public abstract boolean mouseClick(int mouseId);
	
	public boolean mouseHeld(int mouseId) {return false;}
	
	public void mouseRelease(int mouseId) {}
	
	public void mouseScroll(int mouseMove) {}
	
	public void mouseOutOfRegion(int mouseId) {}
	
	public int keyPressed(int keyId) {return 0;}
	
	public int keyHeld(int keyId) {return 0;}
	
	public void keyRelease(int keyId) {}
	
	public boolean isInRegion()
	{
		return getMouseX() >= pos.getX() && getMouseY() >= pos.getY() && getMouseX() <= (pos.getX() + elementWidth) && getMouseY() <= (pos.getY() + elementHeight);
	}

	public void setId(int id) {elementId = id;}
	
	public int getId() {return elementId;}
	
	public boolean equals(Element e) {return getId() == e.getId();}
	
	public void setGui(Gui g) {gui = g;}
	
	public Gui getGui() {return gui;}
	
	public void close() {}

	public void unbind() {}
}
