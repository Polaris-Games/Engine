package com.polaris.engine.network;

import java.util.concurrent.ConcurrentLinkedQueue;

public abstract class NetworkManager
{
	
	private ConcurrentLinkedQueue<Packet> packetQueue = new ConcurrentLinkedQueue<Packet>();

	public void queueForProcess(Packet packetToProcess) 
	{
		packetQueue.offer(packetToProcess);
	}

	public void update(double delta)
	{
		
	}

}
