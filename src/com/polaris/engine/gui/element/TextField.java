package com.polaris.engine.gui.element;

public class TextField extends Element
{

	protected String text;
	protected String nullText = "";
	
	public TextField(double x, double y, double width, double height)
	{
		super(x, y, width, height);
	}
	public TextField(double x, double y, double width, double height, String text)
	{
		super(x, y, width, height);
		nullText = text;
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
	public int mouseClick(int mouseId) 
	{
		return 2;
	}

}
