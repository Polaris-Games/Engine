package com.polaris.engine.gamelogic;

import org.joml.Vector2d;
import org.joml.Vector3d;

import com.polaris.engine.util.GeometryHelper;

public class Rectangle extends Shape
{
	
	private double xMidLength;
	private double yMidLength;
	
	public Rectangle(Vector2d center, double xmid, double ymid)
	{
		centerPosition = new Vector3d(center, 0);
		xMidLength = xmid;
		yMidLength = ymid;
	}
	
	public Rectangle(Vector2d topLeft, Vector2d bottomRight)
	{
		centerPosition = new Vector3d(GeometryHelper.center(topLeft, bottomRight), 0);
		xMidLength = GeometryHelper.distanceFromToX(centerPosition, topLeft);
		yMidLength = GeometryHelper.distanceFromToY(centerPosition, topLeft);
	}

	@Override
	public Axis[] getAxes(double rotationX, double rotationY, double rotationZ) 
	{
		return null;
	}

	@Override
	public Projection project(Axis axis) 
	{
		return null;
	}

}
