package com.polaris.engine.util;
import org.joml.Vector2d;
import org.joml.Vector3d;
public class GeometryHelper 
{
	
	public static boolean withinBounds(double x, double y, double x1, double y1, double w, double h)
	{
		return x1 <= x && y1 <= y && x1 + w >= x && y1 + h >= y;
	}
	
	public static double distanceFromTo(Vector2d startPoint, Vector2d endPoint)
	{
		return MathHelper.pythagoreon(startPoint.x - endPoint.x, startPoint.y - endPoint.y);
	}
	
	public static double distanceFromTo(Vector3d startPoint, Vector3d endPoint)
	{
		return MathHelper.pythagoreon(startPoint.x - endPoint.x, startPoint.y - endPoint.y, startPoint.z - endPoint.z);
	}
	
	public static Vector2d distanceFromToVec(Vector2d startPoint, Vector2d endPoint)
	{
		return new Vector2d(distanceFromToX(startPoint, endPoint), distanceFromToY(startPoint, endPoint));
	}
	
	public static Vector3d distanceFromToVec(Vector3d startPoint, Vector3d endPoint)
	{
		return new Vector3d(distanceFromToX(startPoint, endPoint), distanceFromToY(startPoint, endPoint), distanceFromToZ(startPoint, endPoint));
	}
	
	public static double distanceFromToX(Vector2d startPoint, Vector2d endPoint)
	{
		return Math.abs(startPoint.x - endPoint.x);
	}
	
	public static double distanceFromToX(Vector3d startPoint, Vector2d endPoint)
	{
		return Math.abs(startPoint.x - endPoint.x);
	}
	
	public static double distanceFromToX(Vector3d startPoint, Vector3d endPoint)
	{
		return Math.abs(startPoint.x - endPoint.x);
	}
	
	public static double distanceFromToY(Vector2d startPoint, Vector2d endPoint)
	{
		return Math.abs(startPoint.y - endPoint.y);
	}
	
	public static double distanceFromToY(Vector3d startPoint, Vector2d endPoint)
	{
		return Math.abs(startPoint.y - endPoint.y);
	}
	
	public static double distanceFromToY(Vector3d startPoint, Vector3d endPoint)
	{
		return Math.abs(startPoint.y - endPoint.y);
	}
	
	public static double distanceFromToZ(Vector3d startPoint, Vector3d endPoint)
	{
		return Math.abs(startPoint.z - endPoint.z);
	}
	
	public static double distanceFromToSquared(Vector3d startPoint, Vector3d endPoint)
	{
		double x = startPoint.x - endPoint.x;
		double y = startPoint.y - endPoint.y;
		double z = startPoint.z - endPoint.z;
		return x * x + y * y + z * z;
	}
	
	public static double distanceFromToSquared(Vector2d startPoint, Vector2d endPoint)
	{
		double x = startPoint.x - endPoint.x;
		double y = startPoint.y - endPoint.y;
		return x * x + y * y;
	}
	
	public static double distanceToSquared(Vector2d point)
	{
		return point.x * point.x + point.y * point.y;
	}
	
	public static double distanceToSquared(Vector3d point)
	{
		return point.x * point.x + point.y * point.y + point.z * point.z;
	}
	
	public static Vector2d center(Vector2d ... vectors)
	{
		assert vectors.length > 1;
		double xmin = vectors[0].x;
		double xmax = vectors[0].x;
		double ymin = vectors[0].y;
		double ymax = vectors[0].y;
		
		for(int i = 1; i < vectors.length; i++)
		{
			xmin = Math.min(xmin, vectors[i].x);
			xmax = Math.max(xmax, vectors[i].x);
			ymin = Math.min(ymin, vectors[i].y);
			ymax = Math.max(ymax, vectors[i].y);
		}
		
		return new Vector2d((xmin + xmax) / 2, (ymin + ymax) / 2);
	}

	public static Vector3d center(Vector3d ... vectors)
	{
		assert vectors.length > 1;
		double xmin = vectors[0].x;
		double xmax = vectors[0].x;
		double ymin = vectors[0].y;
		double ymax = vectors[0].y;
		double zmin = vectors[0].z;
		double zmax = vectors[0].z;
		
		for(int i = 1; i < vectors.length; i++)
		{
			xmin = Math.min(xmin, vectors[i].x);
			xmax = Math.max(xmax, vectors[i].x);
			ymin = Math.min(ymin, vectors[i].y);
			ymax = Math.max(ymax, vectors[i].y);
			zmin = Math.min(zmin, vectors[i].z);
			zmax = Math.max(zmax, vectors[i].z);
		}
		
		return new Vector3d((xmin + xmax) / 2, (ymin + ymax) / 2, (zmin + zmax) / 2);
	}
	
}
