package com.polaris.engine.gui.element;

import static com.polaris.engine.Application.getMouseX;
import static com.polaris.engine.Application.getMouseY;

import java.util.ArrayList;
import java.util.List;

public abstract class ListElement<T> extends Element
{
	protected List<ListItem<?>> elementList = new ArrayList<ListItem<?>>();
	protected double shiftWidth = 0;
	protected double extraWidth = 0;
	protected double shiftHeight = 0;
	protected double extraHeight = 0;
	protected boolean clicked = false;

	public ListElement(double x, double y, double width, double height, List<ListItem<?>> list)
	{
		super(x, y, width, height);
		elementList = list;
		setListDimensions();
	}

	public ListElement(double x, double y, double width, double sWidth, double eWidth, double height, double sHeight, double eHeight, List<ListItem<?>> list)
	{
		this(x, y, width, height, list);
		shiftWidth = sWidth;
		extraWidth = eWidth;
		shiftHeight = sHeight;
		extraHeight = eHeight;
		setListDimensions();
	}

	protected abstract void setListDimensions();

	@Override
	public void update(double delta)
	{
		ticksExisted++;
		if(clicked)
		{
			for(int i = 0; i < elementList.size(); i++)
			{
				elementList.get(i).update(delta);
			}
		}
	}

	@Override
	public void render(double delta)
	{
		if(clicked)
		{
			for(int i = 0; i < elementList.size(); i++)
			{
				elementList.get(i).render(delta);
			}
		}
	}

	@Override
	public int mouseClick(int mouseId) 
	{
		if(!clicked)
		{
			clicked = true;
		}
		else if(super.isInRegion())
		{
			clicked = false;
		}
		return 2;
	}

	@SuppressWarnings("unchecked")
	public T getValue(int i)
	{
		return (T) elementList.get(i).getValue();
	}

	public int getListSize()
	{
		return elementList.size();
	}

	public boolean isInRegion()
	{
		return super.isInRegion() || (clicked && getMouseX() >= pos.getX() - shiftWidth && getMouseY() >= pos.getY() - shiftHeight && getMouseX() <= (pos.getX() - shiftWidth + extraWidth) && getMouseY() <= (pos.getY() + elementHeight - shiftHeight + extraHeight));
	}
	
	public void mouseOutOfRegion(int mouseId)
	{
		clicked = false;
	}

}
