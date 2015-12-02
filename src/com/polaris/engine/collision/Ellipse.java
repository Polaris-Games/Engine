package com.polaris.engine.collision;

import com.polaris.engine.Pos;

public class Ellipse<T extends Pos> extends Shape<T>
{
	
	private double f1Distance;
	private double f2Distance;

	public Ellipse(T pos, double r, double f, double f1)
	{
		super(pos, r);
		f1Distance = f;
		f2Distance = f1;
	}

	@Override
	public Double[] getAxes() 
	{
		return new Double[]{};
	}

	@Override
	public Projection project(double axis)
	{
		return null;
	}

}
