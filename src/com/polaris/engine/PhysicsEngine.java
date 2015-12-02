package com.polaris.engine;

import static com.polaris.engine.Helper.isEqual;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import com.polaris.engine.Pos.PosPhysics;
import com.polaris.engine.collision.MTV;
import com.polaris.engine.collision.Projection;
import com.polaris.engine.collision.Shape;
import com.polaris.steve.Steve;

public class PhysicsEngine 
{

	private Application baseApp;
	private double squareUnitToMeter = 0;
	private double generalGravity = 0;
	private double standardAirDensity = 0;

	private List<MoveableObject> moveables = new ArrayList<MoveableObject>();

	public PhysicsEngine(Application app, String physicsResource) throws IOException
	{
		baseApp = app;
		Properties propFile = new Properties();
		propFile.load(Helper.getResourceStream("physics/" + physicsResource + ".prop"));
		squareUnitToMeter = Double.valueOf(propFile.getProperty("squareUnitToMeter"));
		generalGravity = Double.valueOf(propFile.getProperty("gravity")) / squareUnitToMeter;
		if(Boolean.valueOf(propFile.getProperty("useAirFriction")))
		{
			standardAirDensity = Double.valueOf(propFile.getProperty("standardAirDensity"));
		}
	}

	private List<Double> getAxes(Shape<?> shape, Shape<?> shape1)
	{
		List<Double> axes = Arrays.asList(shape.getAxes());

		for(Double axis : shape1.getAxes())
		{
			boolean flag = true;
			for(Double axis1 : axes)
			{
				if(Helper.isEqual(axis, axis1))
				{
					flag = false;
					break;
				}
			}
			if(flag)
			{
				axes.add(axis);
			}
		}
		return axes;
	}

	public void addObject(MoveableObject object, boolean addGravity)
	{
		if(!moveables.contains(object))
		{
			moveables.add(object);
		}
		if(addGravity)
			((PosPhysics)object.getShape().getPosition()).addForce(new Force("gravity", 0, generalGravity * ((PosPhysics)object.getShape().getPosition()).getMass()));
	}

}
