package com.polaris.engine.network;

import java.io.ByteArrayOutputStream;

public class PacketRequestLock extends Packet
{

	@Override
	public Packet copy(byte[] data) 
	{
		return this;
	}

	@Override
	public void writeData(ByteArrayOutputStream output) 
	{
		
	}

}