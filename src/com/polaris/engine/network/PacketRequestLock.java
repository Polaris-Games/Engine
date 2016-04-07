package com.polaris.engine.network;

import java.io.ByteArrayOutputStream;

public class PacketRequestLock extends Packet
{

	@Override
	public void copy(ByteArrayOutputStream data) 
	{
		
	}

	@Override
	public void writeData(ByteArrayOutputStream output) 
	{
		
	}

	@Override
	public void handle(Network network) 
	{
		network.sendPacket(new PacketReceiveLock(((ServerNetwork) network).getServer().getPublicKey()));
	}

}