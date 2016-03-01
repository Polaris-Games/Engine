package com.polaris.engine.gui.element;

import static com.polaris.engine.util.ListHelper.*;

public abstract class MultiButton extends Button
{

	protected String[] modeNames;
	protected int mode = 0;

	public MultiButton(double x, double y, double width, double height, String ... list)
	{
		super(list[0], x, y, width, height);
		modeNames = list;
	}

	@Override
	public int mouseClick(int mouseId)
	{
		buttonText = modeNames[(mode = getListLoc(mode + 1, modeNames.length))];
		return 2;
	}

	public int getMode()
	{
		return mode;
	}

	public void setMode(int m)
	{
		buttonText = modeNames[(mode = getListLoc(m, modeNames.length))];
	}
	
}
