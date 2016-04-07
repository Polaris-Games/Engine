package com.polaris.engine.network;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.polaris.engine.Application;

public abstract class Network
{	
	private ConcurrentLinkedQueue<Packet> packets;
	
	protected Socket socket;
	private DataInputStream inputStream;
	private DataOutputStream outputStream;
	protected LinkedBlockingQueue<Packet> packetsToSend;

	protected void connect(Socket connectionSocket) throws IOException
	{
		socket = connectionSocket;
		if(socket != null)
		{
			socket.setTcpNoDelay(true);
			inputStream = new DataInputStream(socket.getInputStream());
			outputStream = new DataOutputStream(socket.getOutputStream());
			outputStream.flush();

			packetsToSend = new LinkedBlockingQueue<Packet>();
			packets = new ConcurrentLinkedQueue<Packet>();
			if(inputStream == null || outputStream == null)
			{
				socket.close();
				throw new IOException("");
			}
		}
		else
		{
			throw new IOException("");
		}
	}
	
	public void validate()
	{
		new Thread()
		{
			@Override
			public void run()
			{
				try 
				{
					while(true)
					{
						int packet = inputStream.readShort();
						int length = inputStream.readInt();
						byte[] data = new byte[length];
						inputStream.readFully(data);
						packets.offer(Packet.wrap(packet, data));
					}
				} 
				catch (IOException | ReflectiveOperationException e) 
				{

				}
			}
		}.start();
		new Thread()
		{
			@Override
			public void run()
			{
				try
				{
					while(true)
					{
						Packet packetToSend = packetsToSend.take();
						ByteArrayOutputStream output = new ByteArrayOutputStream();
						outputStream.writeShort(packetToSend.getHeader());
						packetToSend.writeData(output);
						outputStream.writeInt(output.size());
						output.writeTo(outputStream);
						outputStream.flush();
					}
				}
				catch (IOException e)
				{
					e.printStackTrace();
				} 
				catch (InterruptedException e) 
				{
					e.printStackTrace();
				}
			}
		}.start();
	}
	
	public void update(double delta)
	{
		Packet nextPacket = null;
		while((nextPacket = packets.poll()) != null)
		{
			nextPacket.handle(this);
		}
	}
	
	public void sendPacket(Packet packetToSend)
	{
		try 
		{
			packetsToSend.put(packetToSend);
		}
		catch (InterruptedException e) 
		{
			e.printStackTrace();
		}
	}
	
	public void invalidate() throws IOException 
	{
		socket.close();
	}

}
