package com.polaris.engine.gui;

import java.util.ArrayList;
import static org.lwjgl.glfw.GLFW.*;
import java.util.Collections;
import java.util.List;

import com.polaris.engine.Application;

public abstract class Gui
{

	private volatile List<Element> elementList = Collections.synchronizedList(new ArrayList<Element>());
	protected Element currentElement;
	protected int ticksExisted = 0;
	protected boolean shiftDown = false;
	protected Application application;
	protected Gui parent;

	public Gui(Application app)
	{
		application = app;
		parent = null;
	}
	public Gui(Gui gui)
	{
		this(gui.application);
		parent = gui;
	}

	public void init() {}

	public void update(double delta)
	{
		ticksExisted++;
		try
		{
			for(int i = 0; i < elementList.size(); i++)
			{
				Element e = elementList.get(i);
				e.update(delta);
			}
		}
		catch(Exception e) {}
	}

	public void render(double delta)
	{
		try
		{
			for(int i = 0; i < elementList.size(); i++)
			{
				Element e = elementList.get(i);
				e.render(delta);
			}
		}
		catch(Exception e) {}
	}

	public boolean mouseClick(int mouseId)
	{
		try
		{
			for(int i = 0; i < elementList.size(); i++)
			{
				Element e = elementList.get(i);
				if(e.isInRegion())
				{
					boolean flag = e.mouseClick(mouseId);
					if(flag && e != currentElement)
					{
						unbindCurrentElement(e);
					}
					elementUpdate(e);
					return flag;
				}
				else
				{
					e.mouseOutOfRegion(mouseId);
				}
			}
		}
		catch(Exception e) {}
		unbindCurrentElement();
		return false;
	}

	public void mouseHeld(int mouseId)
	{
		if(currentElement != null)
		{
			currentElement.mouseHeld(mouseId);
		}
	}

	public void mouseRelease(int mouseId)
	{
		if(currentElement != null)
		{
			currentElement.mouseRelease(mouseId);
			unbindCurrentElement();
		}
	}

	public void mouseScroll(double xOffset, double yOffset) 
	{
		if(currentElement != null)
		{
			//currentElement.mouseScroll(mouseMove);
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

	protected Gui getParent()
	{
		return parent;
	}

}