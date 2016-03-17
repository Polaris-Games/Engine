package com.polaris.engine.network.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class Server
{

	private final ServerSocket listener;
	private final Map<String, Connection> clientMap;
	protected boolean canAcceptClients = true;
	private ServerNetworkManager serverNetworkManager;

	public Server(ServerNetworkManager manager) throws IOException
	{
		listener = new ServerSocket(getServerPort());
		clientMap = new HashMap<String, Connection>();
		serverNetworkManager = manager;
		new Thread()
		{
			@Override
			public void run()
			{
				Socket clientSocket;
				Connection newClient;
				while(canAcceptClients)
				{
					try 
					{
						clientSocket = listener.accept();
					} 
					catch (IOException e)
					{
						e.printStackTrace();
						continue;
					}
					try 
					{
						newClient = genConnection(clientSocket);
						if(clientMap.containsKey(newClient.getConnectionId()))
						{
							clientMap.get(newClient.getConnectionId()).invalidate();
							clientMap.remove(newClient.getConnectionId());
						}
						newClient.validate();
						clientMap.put(newClient.getConnectionId(), newClient);
					} 
					catch (IOException e)
					{
						e.printStackTrace();
						continue;
					}
				}
				try 
				{
					listener.close();
					for(String connectionId : clientMap.keySet())
					{
						clientMap.get(connectionId).invalidate();
					}
				} 
				catch (IOException e)
				{
					e.printStackTrace();
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

	protected Connection genConnection(Socket clientSocket) throws IOException
	{
		return new Connection(serverNetworkManager, clientSocket);
	}

}
