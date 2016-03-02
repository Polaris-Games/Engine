package com.polaris.engine.gamelogic;

import org.joml.Vector2d;
import org.joml.Vector3d;

import com.polaris.engine.util.GeometryHelper;

public abstract class SimpleCollision
{
	
	private double radius = 0;
	private Shape enclosedShape;
	
	public SimpleCollision(double r, Shape shape)
	{
		radius = r;
		enclosedShape = shape;
	}
	
	public boolean isColliding(SimpleCollision collision)
	{
		double length = collision.radius + radius;
		return GeometryHelper.distanceFromToSquared(enclosedShape.getCenter(), collision.enclosedShape.getCenter()) < length*length;
	}

}
