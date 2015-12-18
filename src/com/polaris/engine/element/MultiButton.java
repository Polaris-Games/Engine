package com.polaris.engine.element;

import com.polaris.engine.util.Helper;

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
	public boolean mouseClick(double mouseX, double mouseY, int mouseId)
	{
		buttonText = modeNames[(mode = Helper.getListLoc(mode + 1, modeNames.length))];
		return false;
	}

	public int getMode()
	{
		return mode;
	}

	public void setMode(int m)
	{
		buttonText = modeNames[(mode = Helper.getListLoc(m, modeNames.length))];
	}
	
}
