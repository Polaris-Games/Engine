package com.polaris.engine.gui;

import java.util.ArrayList;
import static org.lwjgl.glfw.GLFW.*;
import java.util.Collections;
import java.util.List;

import com.polaris.engine.Application;

public abstract class GUI
{

	private volatile List<Element> elementList = Collections.synchronizedList(new ArrayList<Element>());
	protected Element currentElement;
	protected int ticksExisted = 0;
	protected boolean shiftDown = false;
	protected Application application;
	protected GUI parent;

	public GUI(Application app)
	{
		application = app;
		parent = null;
	}
	public GUI(GUI gui)
	{
		this(gui.application);
		parent = gui;
	}

	public void init() {}

	public void update(double mouseX, double mouseY, double delta)
	{
		ticksExisted++;
		try
		{
			for(int i = 0; i < elementList.size(); i++)
			{
				Element e = elementList.get(i);
				e.update(mouseX, mouseY, delta);
			}
		}
		catch(Exception e) {}
	}

	public void render(double mouseX, double mouseY, double delta)
	{
		try
		{
			for(int i = 0; i < elementList.size(); i++)
			{
				Element e = elementList.get(i);
				e.render(mouseX, mouseY, delta);
			}
		}
		catch(Exception e) {}
	}

	public boolean mouseClick(double x, double y, int mouseId)
	{
		try
		{
			for(int i = 0; i < elementList.size(); i++)
			{
				Element e = elementList.get(i);
				if(e.isInRegion(x, y))
				{
					boolean flag = e.mouseClick(x, y, mouseId);
					if(flag && e != currentElement)
					{
						unbindCurrentElement(e);
					}
					elementUpdate(e);
					return flag;
				}
				else
				{
					e.mouseOutOfRegion(x, y, mouseId);
				}
			}
		}
		catch(Exception e) {}
		unbindCurrentElement();
		return false;
	}

	public void mouseHeld(double x, double y, int mouseId)
	{
		if(currentElement != null)
		{
			currentElement.mouseHeld(x, y, mouseId);
		}
	}

	public void mouseRelease(double x, double y, int mouseId)
	{
		if(currentElement != null)
		{
			currentElement.mouseRelease(x, y, mouseId);
			unbindCurrentElement();
		}
	}

	public void mouseScroll(double x, double y, double xOffset, double yOffset) 
	{
		if(currentElement != null)
		{
			//currentElement.mouseScroll(x, y, mouseMove);
		}
	}

	public int keyPressed(int keyID) 
	{
		if(currentElement != null)
		{
			return currentElement.keyPressed(keyID);
		}
		if(keyID == GLFW_KEY_LEFT_SHIFT|| keyID == GLFW_KEY_RIGHT_SHIFT)
		{
			shiftDown = true;
			return -1;
		}
		if(keyID == GLFW_KEY_ESCAPE)
		{
			if(shiftDown)
			{
				application.setFullscreen(application.getFullscreenMode() < 2 ? 2 : 0);
				shiftDown = false;
			}
			else
			{
				if(getParent() != null)
				{
					getParent().reinit();
					application.setGui(getParent());
					return 0;
				}
				else
				{
					application.close();
				}
			}
		}
		return -1;
	}

	public int keyHeld(int keyID, int called)
	{
		if(currentElement != null)
		{
			return currentElement.keyHeld(keyID);
		}
		return -1;
	}

	public void keyRelease(int keyID)
	{
		if(currentElement != null)
		{
			currentElement.keyRelease(keyID);
		}
		if(keyID == GLFW_KEY_LEFT_SHIFT || keyID == GLFW_KEY_RIGHT_SHIFT)
		{
			shiftDown = false;
		}
	}

	public void unbindCurrentElement(Element e)
	{
		unbindCurrentElement();
		currentElement = e;
	}

	public void unbindCurrentElement()
	{
		if(currentElement != null)
		{
			currentElement.unbind();
			currentElement = null;
		}
	}

	public void addElement(Element e)
	{
		e.setId(elementList.size());
		e.setGui(this);
		elementList.add(e);
	}

	public void removeElement(int i)
	{
		elementList.remove(i).close();
	}

	public void removeElements(int i, int i1)
	{
		for(int j = i1 - 1; j >= i; j--)
		{
			elementList.remove(j).close();
		}
	}

	public Element getElement(int i)
	{
		return elementList.get(i);
	}

	public int getSize()
	{
		return elementList.size();
	}

	public void elementUpdate(Element e) {}

	public void clear()
	{
		elementList.clear();
	}

	protected void reinit()
	{

	}

	public void close() 
	{
		this.currentElement = null;
	}

	public Element getCurrentElement()
	{
		return currentElement;
	}

	protected GUI getParent()
	{
		return parent;
	}

}
