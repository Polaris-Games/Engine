package com.polaris.engine.element;

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
	public void update(double mouseX, double mouseY, double delta)
	{
		ticksExisted++;
		if(clicked)
		{
			for(int i = 0; i < elementList.size(); i++)
			{
				elementList.get(i).update(mouseX, mouseY, delta);
			}
		}
	}

	@Override
	public void render(double mouseX, double mouseY, double delta)
	{
		if(clicked)
		{
			for(int i = 0; i < elementList.size(); i++)
			{
				elementList.get(i).render(mouseX, mouseY, delta);
			}
		}
	}

	@Override
	public boolean mouseClick(double x, double y, int mouseId) 
	{
		if(!clicked)
		{
			clicked = true;
		}
		else if(super.isInRegion(x, y))
		{
			clicked = false;
		}
		return false;
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

	public boolean isInRegion(double x, double y)
	{
		return super.isInRegion(x, y) || (clicked && x >= pos.getX() - shiftWidth && y >= pos.getY() - shiftHeight && x <= (pos.getX() - shiftWidth + extraWidth) && y <= (pos.getY() + elementHeight - shiftHeight + extraHeight));
	}
	
	public void mouseOutOfRegion(double x, double y, int mouseId)
	{
		clicked = false;
	}

}
