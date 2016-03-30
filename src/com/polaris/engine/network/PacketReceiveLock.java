package com.polaris.engine.network;

import java.io.ByteArrayOutputStream;

public class PacketReceiveLock extends Packet
{

	@Override
	public Packet copy(SidedNetwork<? extends NetworkManager> sidedNetwork, byte[] data) 
	{
		return null;
	}

	@Override
	public void writeData(ByteArrayOutputStream output)
	{
		
	}

}
