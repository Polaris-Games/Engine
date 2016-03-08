package com.polaris.engine.gamelogic;

import org.joml.Vector3d;

public abstract class Shape
{
	protected Vector3d position;
	protected Vector3d rotation;
	protected double circleRadius;
	
	public Vector3d getPosition()
	{
		return position;
	}
	
	public abstract Axis[] getAxes();
	public abstract Projection project(Axis axis);
	
	public boolean isSimpleColliding(Shape collision)
	{
		double length = collision.circleRadius + circleRadius;
		return getPosition().distanceSquared(collision.getPosition()) < length*length;
	}

}
