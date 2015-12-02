package com.polaris.engine.collision;

import com.polaris.engine.Pos;

public class Polygon<T extends Pos> extends Shape<T>
{

	public Polygon(T pos, double r) 
	{
		super(pos, r);
	}

	@Override
	public Double[] getAxes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Projection project(double axis) {
		// TODO Auto-generated method stub
		return null;
	}

}
