package com.polaris.engine.collision;

public class MTV 
{

	private double axis = 0;
	private double overlap = Double.MAX_VALUE;
	
	public void checkAndSet(double a, double o)
	{
		if (o < overlap) 
		{
			overlap = o;
			axis = a;
		}
	}

	public double getOverlap() 
	{
		return overlap;
	}

	public double getAxis() {
		// TODO Auto-generated method stub
		return axis;
	}

}
