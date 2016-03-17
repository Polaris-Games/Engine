package com.polaris.engine.network;

public class Packet 
{

	public static int getPacketSize(int packetIndicator) 
	{
		return 10;
	}

	public static Packet wrap(int packetIndicator, byte[] data) 
	{
		return null;
	}

	public int getPacketId() 
	{
		return 0;
	}

	public byte[] getData()
	{
		return null;
	}

}
