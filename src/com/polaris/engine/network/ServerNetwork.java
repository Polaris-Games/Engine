package com.polaris.engine.network;

import java.io.IOException;
import java.net.Socket;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.polaris.engine.ServerApplication;

public class ServerNetwork extends Network
{
	
	private ServerApplication server;
	private UUID uuid;
	
	public ServerNetwork(ServerApplication serverApplication, Socket clientSocket) throws IOException
	{
		server = serverApplication;
		connect(clientSocket);
	}
	
	public UUID getUUID()
	{
		return uuid;
	}
	
	public ServerApplication getServer()
	{
		return server;
	}
	
}
