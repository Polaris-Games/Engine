package com.polaris.engine.network;

import java.io.ByteArrayOutputStream;
import java.util.List;

public abstract class Packet
{
	
	private static List<Packet> packetList;

	protected final SidedNetwork<? extends NetworkManager> network;
	
	private byte packetHeader = 0;
	
	public static void addPacket(Packet packetToAdd)
	{
		packetToAdd.setHeader(packetList.size() - 128);
	}

	public static int getPacketSize(int packetIndicator)
	{
		return packetList.get(packetIndicator + 128).getPacketLength();
	}
	
	public static Packet wrap(SidedNetwork<? extends NetworkManager> sidedNetwork, int packetIndicator, byte[] data) 
	{
		return packetList.get(packetIndicator + 128).copy(sidedNetwork, data);
	}
	
	public Packet()
	{
		network = null;
	}
	
	public Packet(SidedNetwork<? extends NetworkManager> sidedNetwork)
	{
		network = sidedNetwork;
	}
	
	public abstract int getPacketId();

	public abstract void writeData(ByteArrayOutputStream output);
	
	public abstract Packet copy(SidedNetwork<? extends NetworkManager> sidedNetwork, byte[] data);

	private void setHeader(int i)
	{
		packetHeader = (byte)i;
	}
	
	public final void wrapHeader(ByteArrayOutputStream output)
	{
		output.write(packetHeader);
	}
	
	public abstract short getPacketLength();

}
