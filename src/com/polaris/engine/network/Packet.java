package com.polaris.engine.network;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.LinkedHashMap;

public abstract class Packet
{
	
	private static LinkedHashMap<Class<? extends Packet>, Short> packetMap = new LinkedHashMap<Class<? extends Packet>, Short>();
	
	static
	{
		addPacket(PacketRSA.class);
		addPacket(PacketAES.class);
		addPacket(PacketSecure.class);
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
		Constructor<? extends Packet> packetInstance = packetClass.getConstructor();
		DataInputStream dataStream = new DataInputStream(new ByteArrayInputStream(data));
		Packet p = packetInstance.newInstance();
		p.copy(dataStream);
		return p;
	}
	
	public Packet() 
	{
		packetHeader = getPacketHeader(this);
	}
	
	public abstract void writeData(DataOutputStream output) throws IOException;
	
	public abstract void copy(DataInputStream data) throws IOException;
	
	public abstract void handle(Network network);

	public final int getHeader()
	{
		return packetHeader;
	}

}
