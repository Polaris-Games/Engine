package com.polaris.chat;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import com.polaris.engine.network.Network;
import com.polaris.engine.network.Packet;

public class PacketContent extends Packet
{
	
	private byte[] data;

	public PacketContent(String string)
	{
		data = string.getBytes();
	}

	@Override
	public void writeData(DataOutputStream output) throws IOException
	{
		output.write(data);
	}

	@Override
	public void copy(DataInputStream data) throws IOException
	{
		 this.data = new byte[data.available()];
		 data.readFully(this.data);
	}

	@Override
	public void handle(Network network)
	{
		System.out.println(new String(data));
	}

}
