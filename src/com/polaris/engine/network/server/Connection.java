package com.polaris.engine.network.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;

import com.polaris.engine.network.NetworkManager;
import com.polaris.engine.network.Packet;

public class Connection
{

	private NetworkManager networkManager;
	private Socket socket;
	private InputStream intakeStream;
	private OutputStream outtakeStream;
	private String connectionId;
	private LinkedBlockingQueue<Packet> packetsToSend;

	public Connection(NetworkManager manager, Socket socket) throws IOException
	{
		this(manager, socket, 4096);
	}

	public Connection(NetworkManager manager, Socket socket, int bufferedSize) throws IOException
	{
		this.networkManager = manager;
		this.socket = socket;
		this.intakeStream = socket.getInputStream();
		this.outtakeStream = socket.getOutputStream();
		this.connectionId = socket.getRemoteSocketAddress().toString();
		this.packetsToSend = new LinkedBlockingQueue<Packet>(256);
		if(this.connectionId == null || this.intakeStream == null || this.outtakeStream == null)
		{
			if(socket != null)
			{
				socket.close();
			}
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
						int packetIndicator = intakeStream.read();
						byte[] data = new byte[Packet.getPacketSize(packetIndicator)];
						intakeStream.read(data, 0, data.length);
						networkManager.queueForProcess(Packet.wrap(packetIndicator, data));
					}
				} 
				catch (IOException e) 
				{
					e.printStackTrace();
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
						outtakeStream.write(packetToSend.getPacketId());
						outtakeStream.write(packetToSend.getData());
						outtakeStream.flush();
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