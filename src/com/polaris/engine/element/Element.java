package com.polaris.engine.element;

import com.polaris.engine.GUI;
import com.polaris.engine.Pos;

public abstract class Element 
{
	
	protected Pos pos;
	protected double elementWidth = 0;
	protected double elementHeight = 0;
	protected int elementId = 0;
	protected int ticksExisted = 0;
	protected GUI gui;
	protected boolean highlighted = false;
	
	public Element(double x, double y, double width, double height)
	{
		pos = new Pos(x, y);
		elementWidth = width;
		elementHeight = height;
	}

	public void update(double mouseX, double mouseY, double delta)
	{
		ticksExisted++;
		highlighted = isInRegion(mouseX, mouseY);
	}
	
	public abstract void render(double mouseX, double mouseY, double delta);
	
	public abstract boolean mouseClick(double mouseX, double mouseY, int mouseId);
	
	public boolean mouseHeld(double mouseX, double mouseY, int mouseId) {return false;}
	
	public void mouseRelease(double mouseX, double mouseY, int mouseId) {}
	
	public void mouseScroll(double mouseX, double mouseY, int mouseMove) {}
	
	public void mouseOutOfRegion(double mouseX, double mouseY, int mouseId) {}
	
	public int keyPressed(int keyId) {return 0;}
	
	public int keyHeld(int keyId) {return 0;}
	
	public void keyRelease(int keyId) {}
	
	public boolean isInRegion(double mouseX, double mouseY)
	{
		return mouseX >= pos.getX() && mouseY >= pos.getY() && mouseX <= (pos.getX() + elementWidth) && mouseY <= (pos.getY() + elementHeight);
	}

	public void setId(int id) {elementId = id;}
	
	public int getId() {return elementId;}
	
	public boolean equals(Element e) {return getId() == e.getId();}
	
	public void setGui(GUI g) {gui = g;}
	
	public GUI getGui() {return gui;}
	
	public void close() {}

	public void unbind() {}
}
