package com.polaris.engine.network.client;

import java.io.IOException;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;

import com.polaris.engine.network.Connection;
import com.polaris.engine.network.PacketRequestLock;

public class Client extends Connection
{

	private Cipher cipher = null;

	public Client(ClientNetworkManager sidedNetwork, String serverAddress, int port) throws IOException
	{
		super(sidedNetwork, new Socket(serverAddress, port));
	}

	public void validate(boolean establishSecureTunnel)
	{
		super.validate();
		if(establishSecureTunnel)
		{
			sendPacket(new PacketRequestLock());
			while(cipher == null);
		}
	}

	public void setEncryptionKey(RSAPublicKey key)
	{
		try
		{
			cipher = Cipher.getInstance("RSA");
			cipher.init(Cipher.ENCRYPT_MODE, key);
		}
		catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException e) 
		{
			e.printStackTrace();
		}
	}

}
