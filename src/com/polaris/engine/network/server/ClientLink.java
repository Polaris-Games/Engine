package com.polaris.engine.network.server;

import java.io.IOException;
import java.net.Socket;

import com.polaris.engine.network.Connection;

public class ClientLink extends Connection
{
	
	private String connectionId;

	public ClientLink(ServerNetworkManager sidedNetwork, Socket clientSocket) throws IOException
	{
		super(sidedNetwork, clientSocket);
		connectionId = clientSocket.getRemoteSocketAddress().toString();
	}

	public String getConnectionId()
	{
		return connectionId;
	}

}