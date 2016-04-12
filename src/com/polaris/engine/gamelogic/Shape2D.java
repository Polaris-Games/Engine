package com.polaris.engine.gamelogic;

public abstract class Shape2D extends Shape
{

	protected double rotation;

	@Override
	public double getRotationX()
	{
		return rotation;
	}

	@Override
	public double getRotationY() 
	{
		return 0;
	}

	@Override
	public double getRotationZ() 
	{
		return 0;
	}
	
}
