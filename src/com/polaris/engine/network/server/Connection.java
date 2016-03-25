package com.polaris.engine.network.server;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;

import com.polaris.engine.network.Packet;
import com.polaris.engine.network.SidedNetwork;

public class Connection
{

	private SidedNetwork<? extends ServerNetworkManager> network;
	private Socket socket;
	private InputStream inputStream;
	private OutputStream outputStream;
	private String connectionId;
	private LinkedBlockingQueue<Packet> packetsToSend;

	public Connection(SidedNetwork<? extends ServerNetworkManager> sidedNetwork, Socket socket) throws IOException
	{
		this.network = sidedNetwork;
		this.socket = socket;
		if(socket != null)
		{
			socket.setTcpNoDelay(true);
			this.inputStream = socket.getInputStream();
			this.outputStream = socket.getOutputStream();
			this.connectionId = socket.getRemoteSocketAddress().toString();
			this.packetsToSend = new LinkedBlockingQueue<Packet>(256);
			if(this.connectionId == null || this.inputStream == null || this.outputStream == null)
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
						int packetIndicator = inputStream.read();
						System.out.println(packetIndicator);
						byte[] data = new byte[Packet.getPacketSize(packetIndicator)];
						inputStream.read(data, 0, data.length);
						network.getSidedNetwork().queueForProcess(Packet.wrap(network, packetIndicator, data));
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
						packetToSend.wrapHeader(output);
						packetToSend.writeData(output);
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

	public String getConnectionId()
	{
		return connectionId;
	}

	public void invalidate() throws IOException 
	{
		socket.close();
	}

}