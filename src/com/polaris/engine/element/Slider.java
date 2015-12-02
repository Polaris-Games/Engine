package com.polaris.engine.element;

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
	public void update(double mouseX, double mouseY, double delta) 
	{
		
	}

	@Override
	public void render(double mouseX, double mouseY, double delta)
	{
		
	}

	@Override
	public boolean mouseClick(double mouseX, double mouseY, int mouseId) 
	{
		return false;
	}

}
