package com.polaris.engine.util;
import org.joml.Vector3d;
public class GeometryHelper 
{
	
	public static boolean withinBounds(double x, double y, double x1, double y1, double w, double h)
	{
		return x1 <= x && y1 <= y && x1 + w >= x && y1 + h >= y;
	}

	public static double rotate(double x, double y, double axis) 
	{
		return x * Math.cos(axis) + y * Math.sin(axis);
	}
	
	public static double distanceFromTo(Vector3d startPoint, Vector3d endPoint)
	{
		return MathHelper.pythagoreon(startPoint.x - endPoint.y, startPoint.y - endPoint.y, startPoint.z - endPoint.z);
	}

}
