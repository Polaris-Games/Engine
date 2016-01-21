package com.polaris.engine.gui;

public abstract class Slider<T> extends Element
{
	
	protected T minValue;
	protected T maxValue;
	protected T currentValue;
	
	public Slider(double x, double y, double width, double height, T min, T max, T current)
	{
		super(x, y, width, height);
		minValue = min;
		maxValue = max;
		currentValue = current;
	}
	
	@Override
	public void update(double delta) 
	{
		
	}

	@Override
	public void render(double delta)
	{
		
	}

	@Override
	public boolean mouseClick(int mouseId) 
	{
		return false;
	}

}
