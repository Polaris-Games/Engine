package com.polaris.engine;

import java.io.IOException;

import com.polaris.engine.network.ClientNetwork;

public abstract class Application extends App
{
	private ClientNetwork network = null;
	
	public Application() {}
	
	public Application(ClientNetwork sidedNetwork)
	{
		network = sidedNetwork;
		network.setApplication(this);
	}
	
	@Override
	public void run()
	{
		super.run();
		try 
		{
			getNetwork().invalidate();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Update method called every n times / second 
	 * <br><b>DON'T CALL super.update(delta) UNLESS YOU IMPLEMENT GUI CLASS STRUCTURE</b>
	 * @param mouseX : current Mouse Position, updates before method call
	 * @param mouseY : current Mouse Position, updates before method call
	 * @param delta : change in time, measured in actual seconds
	 */
	public void update(double delta) 
	{
		if(network != null)
		{
			network.update(delta);
		}
		currentGui.update(delta);
	}
	
	public ClientNetwork getNetwork()
	{
		return network;
	}

}
