package com.polaris.engine.network;

import java.io.IOException;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;

import com.polaris.engine.Application;


public class ClientNetwork extends Network
{

	private Application application = null;
	private Cipher cipher = null;
	
	public void connect(String serverAddress, int port) throws IOException
	{
		connect(new Socket(serverAddress, port));
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
	
	public void setApplication(Application app)
	{
		application = app;
	}
	
	public Application getApplication()
	{
		return application;
	}
	
}
