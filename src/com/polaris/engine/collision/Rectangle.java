package com.polaris.engine.collision;

import static com.polaris.engine.Helper.*;
import static java.lang.Math.*;
import com.polaris.engine.Pos;


public class Rectangle<T extends Pos> extends Shape<T>
{

	protected double width;
	protected double height;
	
	public Rectangle(T pos, double r, double w, double h)
	{
		super(pos, r);
		width = w;
		height = h;
	}

	public double getPosX()
	{
		return position.getX();
	}
	
	public double getWidth()
	{
		return width;
	}

	public double getPosWidth() 
	{
		return position.getX() + width;
	}

	public double getPosY() 
	{
		return position.getY();
	}
	
	public double getHeight()
	{
		return height;
	}

	public double getPosHeight()
	{
		return position.getY() + height;
	}

	@Override
	public Double[] getAxes() 
	{
		return new Double[] {Double.valueOf(rotation), Double.valueOf(rotation + HALFPI)};
	}

	@Override
	public Projection project(double axis)
	{
		int flag = cos(axis) >= 0 ? 0 : 1;
		int flag1 = sin(axis) <= 0 ? 0 : 1;
		double min = rotate(getPosX() + width * flag, getPosY() + height * flag1, axis);
		double max = rotate(getPosX() + width * ((flag + 1) % 2), getPosY() + height * ((flag1 + 1) % 2), axis);
		return new Projection(min, max);
	}

}
