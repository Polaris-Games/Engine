package com.polaris.engine.gamelogic;

import org.joml.Vector2d;
import org.joml.Vector3d;

import com.polaris.engine.util.GeometryHelper;

public abstract class SimpleCollision
{
	
	private double radius = 0;
	private Vector3d centerPosition;
	
	public SimpleCollision(double r, Vector2d position)
	{
		radius = r;
		centerPosition = new Vector3d(position, 0);
	}
	
	public SimpleCollision(double r, Vector3d position)
	{
		radius = r;
		centerPosition = position;
	}
	
	public boolean isColliding(SimpleCollision collision)
	{
		return GeometryHelper.distanceFromTo(centerPosition, collision.centerPosition) < collision.radius + radius;
	}

}
