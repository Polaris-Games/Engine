package com.polaris.engine.collision;

public class Projection 
{
	
	private double max;
	private double length;
	
	public Projection(double m, double m1)
	{
		max = m1;
		length = max - m;
	}

	public double getOverlap(Projection p2)
	{
		return p2.length - Math.abs(p2.max - max);
	}

	public void add(double change) 
	{
		max += change;
	}

}
