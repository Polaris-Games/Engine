package com.polaris.engine.gui;


public abstract class Button extends Element
{
	
	protected String buttonText;

	
	public Button(String name, double x, double y, double width, double height)
	{
		super(x, y, width, height);
		buttonText = name;
	}
	
}
