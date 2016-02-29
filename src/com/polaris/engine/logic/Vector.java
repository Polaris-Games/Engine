package com.polaris.engine.logic;

import java.math.BigDecimal;
import java.math.MathContext;

public abstract class Vector 
{

	public abstract Vector add(Vector vector);
	public abstract Vector add(Vector vector, Vector dest);
	public abstract Vector sub(Vector vector);
	public abstract Vector sub(Vector vector, Vector dest);
	public abstract Vector mul(Vector vector);
	public abstract Vector mul(Vector vector, Vector dest);
	public abstract Vector div(Vector vector);
	public abstract Vector div(Vector vector, Vector dest);
	public abstract Vector fma(Vector firstVector, Vector secondVector);
	public abstract Vector fma(Vector vector);
	
	public abstract Vector copy();
	public abstract void move(Vector moveVector);
	
	public LineVector moveLineVector(Vector moveVector)
	{
		return LineVector.moveVector(this, moveVector);
	}
	
	public LineVector lineVector(Vector toVector)
	{
		return LineVector.moveVector(this, toVector);
	}
	
}
