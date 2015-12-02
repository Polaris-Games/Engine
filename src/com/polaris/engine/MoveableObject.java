package com.polaris.engine;

import com.polaris.engine.collision.Shape;

public abstract class MoveableObject
{
	
	protected Shape<?> objectShape;
	protected int ticksAlive = 0;
	public int UUID = 0;
	
	public MoveableObject(Shape<?> shape) 
	{
		objectShape = shape;
	}
	
	public abstract void render(double delta);
	
	public void update()
	{
		ticksAlive++;
		objectShape.update();
	}

	public Shape<?> getShape()
	{
		return objectShape;
	}
	
	public void onCollide(MoveableObject otherObject) {}

	public void onMove(double x, double y) {}
	
	public Pos getPosition()
	{
		return objectShape.getPosition();
	}
	
}
