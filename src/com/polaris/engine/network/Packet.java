package com.polaris.engine.network;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.LinkedHashMap;

public abstract class Packet
{
	
	private static LinkedHashMap<Class<? extends Packet>, Short> packetMap = new LinkedHashMap<Class<? extends Packet>, Short>();
	
	static
	{
		addPacket(PacketReceiveLock.class);
		addPacket(PacketRequestLock.class);
	}
	
	private short packetHeader = 0;
	
	public static void addPacket(Class<? extends Packet> packet)
	{
		packetMap.put(packet, (short) packetMap.size());
	}
	
	public static short getPacketHeader(Packet packet)
	{
		return packetMap.get(packet.getClass());
	}
	
	public static Packet wrap(int packet, byte[] data) throws ReflectiveOperationException, IOException 
	{
		Class<? extends Packet> packetClass = null;
		for(Class<? extends Packet> packetCl : packetMap.keySet())
		{
			if(packetMap.get(packetCl) == packet)
			{
				packetClass = packetCl;
				break;
			}
		}
		Constructor<? extends Packet> packetInstance = packetClass.getConstructor(ByteArrayOutputStream.class);
		ByteArrayOutputStream dataStream = new ByteArrayOutputStream();
		dataStream.write(data);
		return packetInstance.newInstance(dataStream);
	}
	
	public Packet() 
	{
		packetHeader = getPacketHeader(this);
	}
	
	public Packet(ByteArrayOutputStream data)
	{
		copy(data);
	}

	public abstract void writeData(ByteArrayOutputStream output);
	
	public abstract void copy(ByteArrayOutputStream data);
	
	public abstract void handle(Network network);

	public final int getHeader()
	{
		return packetHeader;
	}

}
