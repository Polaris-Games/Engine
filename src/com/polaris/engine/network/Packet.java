package com.polaris.engine.network;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public abstract class Packet
{
	
	private static List<Packet> packetList = new ArrayList<Packet>();
	
	static
	{
		addPacket(new PacketReceiveLock());
		addPacket(new PacketRequestLock());
	}

	protected final SidedNetwork<? extends NetworkManager> network;
	
	private short packetHeader = 0;
	
	public static void addPacket(Packet packetToAdd)
	{
		packetToAdd.setHeader(packetList.size());
		packetList.add(packetToAdd);
	}
	
	public static int getPacketHeader(Packet packet)
	{
		int header = 0;
		for(int i = 0; i < packetList.size(); i++)
		{
			if(packetList.get(i).getClass() == packet.getClass())
			{
				header = i;
				break;
			}
		}
		return header;
	}
	
	public static Packet wrap(SidedNetwork<? extends NetworkManager> sidedNetwork, int packet, byte[] data) 
	{
		return packetList.get(packet).copy(sidedNetwork, data);
	}
	
	public Packet()
	{
		network = null;
	}
	
	public Packet(SidedNetwork<? extends NetworkManager> sidedNetwork)
	{
		network = sidedNetwork;
	}

	public abstract void writeData(ByteArrayOutputStream output);
	
	public abstract Packet copy(SidedNetwork<? extends NetworkManager> sidedNetwork, byte[] data);

	public final void setHeader(int i)
	{
		packetHeader = (short)i;
	}
	
	public final int getHeader()
	{
		return packetHeader;
	}

}
