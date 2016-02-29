package com.polaris.engine.world;

import com.polaris.engine.collision.Shape;


public abstract class Entity extends MoveableObject
{
	
	public Entity(Shape<?> shape) 
	{
		super(shape);
	}

	public void render(double delta)
	{
		getPosition().update(delta);
	}
	
	public void kill()
	{
		
	}
	
	public double getX()
	{
		return getPosition().getX();
	}
	
	public double getY()
	{
		return getPosition().getY();
	}
	
}
