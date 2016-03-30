package com.polaris.engine.network.server;

import java.io.IOException;
import java.net.Socket;

import com.polaris.engine.network.Connection;
import com.polaris.engine.network.SidedNetwork;

public class ClientLink extends Connection
{
	
	private String connectionId;

	public ClientLink(SidedNetwork<? extends ServerNetworkManager> sidedNetwork, Socket clientSocket) throws IOException
	{
		super(sidedNetwork, clientSocket);
		connectionId = clientSocket.getRemoteSocketAddress().toString();
		System.out.println(connectionId);
		System.out.println("connection established!");
	}

	public String getConnectionId()
	{
		return connectionId;
	}

}