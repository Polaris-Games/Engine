package com.polaris.engine.gamelogic;

public abstract class Shape
{
	
	public abstract Axis[] getAxes(double rotationX, double rotationY, double rotationZ);
	public abstract Projection project(Axis axis);

}
