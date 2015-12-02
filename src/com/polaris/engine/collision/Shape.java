package com.polaris.engine.collision;

import com.polaris.engine.Pos;


public abstract class Shape<T extends Pos> 
{
	
	protected T position;
	protected double rotation = 0;
	
	public Shape(T pos, double rot)
	{
		position = pos;
		rotation = rot;
	}

	public void update()
	{
		
	}
	
	public abstract Double[] getAxes();

	public T getPosition() 
	{
		return position;
	}

	public abstract Projection project(double axis);
	
}
