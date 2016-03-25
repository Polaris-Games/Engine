package com.polaris.engine.network;

import java.io.ByteArrayOutputStream;

public class PacketReceiveLock extends Packet
{

	@Override
	public int getPacketId()
	{
		return 0;
	}

	@Override
	public Packet copy(SidedNetwork<? extends NetworkManager> sidedNetwork, byte[] data) 
	{
		return null;
	}

	@Override
	public void writeData(ByteArrayOutputStream output)
	{
		
	}

	@Override
	public short getPacketLength() 
	{
		return 0;
	}

}
