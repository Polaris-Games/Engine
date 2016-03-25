package com.polaris.engine.network;

public class SidedNetwork<T extends NetworkManager>
{

	private T sidedNetworkManager;
	
	public SidedNetwork(T networkManager)
	{
		sidedNetworkManager = networkManager;
	}
	
	public T getSidedNetwork()
	{
		return sidedNetworkManager;
	}
	
}
