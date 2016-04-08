package com.polaris.engine;

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
import java.util.UUID;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import com.polaris.engine.network.ServerNetwork;

public abstract class ServerApplication extends App
{
	private static ServerApplication server;
	private static boolean stopServerThread = false;

	private Map<String, ServerNetwork> playerNetworks;
	private final ServerSocket listener;

	private Cipher cipher;
	private RSAPublicKey publicKey;

	public ServerApplication(int port, int encryption) throws IOException
	{
		listener = new ServerSocket(port);
		playerNetworks = new HashMap<String, ServerNetwork>();
		if(encryption > 0)
		{
			try
			{
				KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
				keyGen.initialize(encryption);
				KeyPair key = keyGen.generateKeyPair();
				cipher = Cipher.getInstance("RSA");
				cipher.init(Cipher.DECRYPT_MODE, key.getPrivate());
				publicKey = (RSAPublicKey) key.getPublic();
			}
			catch(NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException e)
			{
				e.printStackTrace();
			}
		}
		server = this;
		new Thread()
		{
			@Override
			public void run()
			{
				Socket clientSocket;
				ServerNetwork newClient;
				while(!stopServerThread)
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
						newClient = new ServerNetwork(server, clientSocket);
						if(playerNetworks.containsKey(newClient.getUUID()))
						{
							playerNetworks.get(newClient.getUUID()).invalidate();
							playerNetworks.remove(newClient.getUUID());
						}
						newClient.validate();
						playerNetworks.put(newClient.getUUID(), newClient);
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
					for(String uuid : playerNetworks.keySet())
					{
						playerNetworks.get(uuid).invalidate();
					}
				} 
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}.start();
	}

	@Override
	public void run()
	{
		super.run();
		stopServerThread = true;
	}

	/**
	 * Update method called every n times / second 
	 * <br><b>DON'T CALL super.update(delta) UNLESS YOU IMPLEMENT GUI CLASS STRUCTURE</b>
	 * @param mouseX : current Mouse Position, updates before method call
	 * @param mouseY : current Mouse Position, updates before method call
	 * @param delta : change in time, measured in actual seconds
	 */
	public void update(double delta) 
	{
		for(String uuid : playerNetworks.keySet())
		{
			ServerNetwork playerNetwork = playerNetworks.get(uuid);
			if(playerNetwork.isConnected())
			{
				playerNetworks.get(uuid).update(delta);
			}
			else
			{
				playerNetworks.remove(uuid);
			}
		}
		currentGui.update(delta);
	}

	public ServerNetwork getNetwork(UUID uuid)
	{
		return playerNetworks.get(uuid);
	}

	public int getServerPort()
	{
		return 8888;
	}

	public RSAPublicKey getPublicKey()
	{
		return publicKey;
	}

	public int getAESLength()
	{
		return 128;
	}

	public byte[] decryptRSA(byte[] encoded) 
	{
		try
		{
			return cipher.doFinal(encoded);
		}
		catch (IllegalBlockSizeException | BadPaddingException e)
		{
			e.printStackTrace();
		}
		return null;
	}

}
