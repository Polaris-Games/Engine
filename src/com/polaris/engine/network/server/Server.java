package com.polaris.engine.network.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;

public class Server
{

	private final ServerSocket listener;
	private final Map<String, ClientLink> clientMap;
	protected boolean canAcceptClients = true;
	private ServerNetworkManager network;
	
	private Cipher cipher;
	private RSAPublicKey publicKey;

	public Server(ServerNetworkManager sidedNetwork) throws IOException
	{
		listener = new ServerSocket(getServerPort());
		clientMap = new HashMap<String, ClientLink>();
		network = sidedNetwork;
		try
		{
			KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
			keyGen.initialize(8192);
			KeyPair key = keyGen.generateKeyPair();
			cipher = Cipher.getInstance("RSA");
			cipher.init(Cipher.DECRYPT_MODE, key.getPrivate());
			publicKey = (RSAPublicKey) key.getPublic();
		}
		catch(NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException e)
		{
			e.printStackTrace();
		}

		new Thread()
		{
			@Override
			public void run()
			{
				Socket clientSocket;
				ClientLink newClient;
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

	protected ClientLink genConnection(Socket clientSocket) throws IOException
	{
		return new ClientLink(network, clientSocket);
	}

}
