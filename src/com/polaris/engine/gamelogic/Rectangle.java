package com.polaris.engine.gamelogic;

import org.joml.AxisAngle4d;
import org.joml.Vector2d;
import org.joml.Vector3d;

import com.polaris.engine.util.GeometryHelper;
import com.polaris.engine.util.Helper;
import com.polaris.engine.util.MathHelper;
import static com.polaris.engine.util.GeometryHelper.*;
public class Rectangle extends Shape
{
	
	private double xMidLength;
	private double yMidLength;
	
	public Rectangle(Vector2d center, double xmid, double ymid)
	{
		this(center, xmid, ymid, new Vector3d(0));
	}
	
	public Rectangle(Vector2d center, double xmid, double ymid, Vector3d rot)
	{
		position = new Vector3d(center, 0);
		xMidLength = xmid;
		yMidLength = ymid;
		circleRadius = MathHelper.pythagoreon(xmid, ymid);
		rotation = rot;
	}
	
	public Rectangle(Vector2d topLeft, Vector2d bottomRight)
	{
		this(topLeft, bottomRight, new Vector3d(0));
	}

	public Rectangle(Vector2d topLeft, Vector2d bottomRight, Vector3d rot)
	{
		position = new Vector3d(GeometryHelper.center(topLeft, bottomRight), 0);
		xMidLength = distanceFromToX(position, topLeft);
		yMidLength = distanceFromToY(position, topLeft);
		circleRadius = MathHelper.pythagoreon(xMidLength, yMidLength);
		rotation = rot;
	}
	
	@Override
	public Axis[] getAxes() 
	{
		return new Axis[] {new Axis(rotation.x), new Axis(rotation.x + Helper.HALFPI)};
	}

	@Override
	public Projection project(Axis axis) 
	{
        double cos = Math.abs(Math.cos(axis.getRotationX() - rotation.x));
        double sin = Math.abs(Math.sin(axis.getRotationX() - rotation.x));
        Vector2d min = new Vector2d(position.x, position.y).sub(xMidLength, yMidLength);
        Vector2d max = new Vector2d(position.x, position.y).add(xMidLength, yMidLength);
        double minPoint = GeometryHelper.distanceToSquared(new Vector2d(min.x * cos, min.y * sin));
        double maxPoint = GeometryHelper.distanceToSquared(new Vector2d(max.x * cos, max.y * sin));
		return new Projection(new Vector2d(min.x * cos, min.y * sin).length(), new Vector2d(max.x * cos, max.y * sin).length());
	}

}
