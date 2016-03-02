package com.polaris.engine.gamelogic;

import org.joml.Vector3d;

public abstract class Shape
{
	protected Vector3d centerPosition;
	
	public Vector3d getCenter()
	{
		return centerPosition;
	}
	
	public abstract Axis[] getAxes(double rotationX, double rotationY, double rotationZ);
	public abstract Projection project(Axis axis);

}
