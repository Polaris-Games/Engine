package com.polaris.engine;

import java.util.ArrayList;
import java.util.List;

public class Pos 
{

	private double posX = 0;
	private double posY = 0;
	private double prevPosX = 0;
	private double prevPosY = 0;
	private double interpPosX = 0;
	private double interpPosY = 0;

	public Pos(double x, double y)
	{
		posX = prevPosX = interpPosX = x;
		posY = prevPosY = interpPosY = y;
	}
	
	public final void update(double delta)
	{
		interpPosX = Helper.getLinearValue(interpPosX, posX, Math.abs(posX - prevPosX), delta);
		interpPosY = Helper.getLinearValue(interpPosY, posY, Math.abs(posY - prevPosY), delta);
	}

	public double getX()
	{
		return interpPosX;
	}

	public double getY()
	{
		return interpPosY;
	}

	public double getRealX()
	{
		return posX;
	}

	public double getRealY()
	{
		return posY;
	}

	public void moveX(double x)
	{
		prevPosX = interpPosX = posX;
		posX += x;
	}

	public void moveY(double y)
	{
		prevPosY = interpPosY = posY;
		posY += y;
	}
	
	public void setPosition(double x, double y)
	{
		setX(x);
		setY(y);
	}

	public void setX(double x)
	{
		posX = prevPosX = interpPosX = x;
	}

	public void setY(double y)
	{
		posY = prevPosY = interpPosY = y;
	}

	public static class PosPhysics extends Pos
	{
		private double velocityX = 0;
		private double velocityY = 0;
		private double accelerationX = 0;
		private double accelerationY = 0;
		private double mass = 0;
		private List<Force> forceList = new ArrayList<Force>();

		public PosPhysics(double x, double y, double m)
		{
			super(x, y);
			mass = m;
		}

		public PosPhysics(double x, double y, double vx, double vy, double m)
		{
			this(x, y, m);
			velocityX = vx;
			velocityY = vy;
		}
		
		public PosPhysics(double x, double y, double vx, double vy, double ax, double ay, double m)
		{
			this(x, y, vx, vy, m);
			accelerationX = ax;
			accelerationY = ay;
		}
		
		public double getVelocityX()
		{
			return velocityX;
		}
		
		public double getVelocityY()
		{
			return velocityY;
		}
		
		public void setVelocity(double vx, double vy)
		{
			setVelocityX(vx);
			setVelocityY(vy);
		}
		
		public void setVelocityX(double vx)
		{
			velocityX = vx;
		}
		
		public void setVelocityY(double vy)
		{
			velocityY = vy;
		}
		
		public double getAccelerationX()
		{
			return accelerationX;
		}
		
		public double getAccelerationY()
		{
			return accelerationY;
		}
		
		public void addForce(Force force)
		{
			int j = -1;
			for(int i = 0; i < forceList.size(); i++)
			{
				if(forceList.get(i).name.equalsIgnoreCase(force.name))
				{
					j = i;
					break;
				}
			}
			if(j == -1)
			{
				forceList.add(force);
			}
			else
			{
				forceList.get(j).setForce(force);
			}
			calculateAcceleration();
		}
		
		public void removeForce(String s)
		{
			for(Force force : forceList)
			{
				if(force.name.equalsIgnoreCase(s))
				{
					forceList.remove(force);
					break;
				}
			}
		}
		
		public double getMass()
		{
			return mass;
		}

		public List<Force> getForces()
		{
			return forceList;
		}
		
		private void calculateAcceleration()
		{
			for(Force f : forceList)
			{
				accelerationX += f.xComponent;
				accelerationY += f.yComponent;
			}
			accelerationX /= getMass();
			accelerationY /= getMass();
		}
	}

}
