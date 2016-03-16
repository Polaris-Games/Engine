package com.polaris.engine.network.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.Map;

public class Server
{
	
	private final ServerSocket listener;
	private final Map<Integer, Connection> clientList;
	protected boolean canAcceptClients = true;
	
	public Server() throws IOException
	{
		listener = new ServerSocket(getServerPort());
		clientList = new HashMap<Integer, Connection>();
		new Thread()
		{
			@Override
			public void run()
			{
				Connection newClient;
				while(canAcceptClients)
				{
					try 
					{
						newClient = new Connection(listener.accept());
						if(newClient.valid())
						{
							clientList.put(newClient.getConnectionId(), newClient);
							newClient.start();
						}
					} 
					catch (IOException e)
					{
						e.printStackTrace();
					}
				}
			}
		}.start();
	}
	
	public void rejectClients()
	{
		canAcceptClients = false;
	}
	
	public int getServerPort()
	{
		return 8888;
	}
	
}
