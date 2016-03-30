package com.polaris.engine.network;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;

public class Connection 
{
	
	protected SidedNetwork<? extends NetworkManager> network;
	protected Socket socket;
	private DataInputStream inputStream;
	private DataOutputStream outputStream;
	protected LinkedBlockingQueue<Packet> packetsToSend;

	public Connection(SidedNetwork<? extends NetworkManager> sidedNetwork, Socket clientSocket) throws IOException
	{
		network = sidedNetwork;
		socket = clientSocket;
		if(socket != null)
		{
			socket.setTcpNoDelay(true);
			inputStream = new DataInputStream(socket.getInputStream());
			outputStream = new DataOutputStream(socket.getOutputStream());
			outputStream.flush();

			packetsToSend = new LinkedBlockingQueue<Packet>();
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
						network.getSidedNetwork().queueForProcess(Packet.wrap(network, packet, data));
					}
				} 
				catch (IOException e) 
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
						System.out.println(packetToSend.getHeader());
						outputStream.writeShort(packetToSend.getHeader());
						packetToSend.writeData(output);
						outputStream.writeInt(output.size());
						output.writeTo(outputStream);
						outputStream.flush();
						System.out.println(packetToSend);
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
	
	public void sendPacket(Packet packetToSend)
	{
		try 
		{
			packetToSend.setHeader(Packet.getPacketHeader(packetToSend));
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
