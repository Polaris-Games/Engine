package com.polaris.engine.network;

import java.io.IOException;
import java.net.Socket;
import java.security.GeneralSecurityException;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import com.polaris.engine.ServerApplication;

public class ServerNetwork extends Network
{

	private ServerApplication server;
	private String uuid;

	public ServerNetwork(ServerApplication serverApplication, Socket clientSocket) throws IOException
	{
		server = serverApplication;
		connect(clientSocket);
		uuid = clientSocket.getRemoteSocketAddress().toString();
	}

	public String getUUID()
	{
		return uuid;
	}

	public ServerApplication getServer()
	{
		return server;
	}

	public void setAESKey(byte[] encoded)
	{	
		try
		{
			SecretKeySpec secretKeySpec = new SecretKeySpec(getServer().decryptRSA(encoded), "AES");

			encrypt = Cipher.getInstance("AES/CBC/PKCS5Padding");
			IvParameterSpec ivParamsSpec = new IvParameterSpec(new byte[encrypt.getBlockSize()]);
			encrypt.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParamsSpec);
			decrypt = Cipher.getInstance("AES/CBC/PKCS5Padding");
			ivParamsSpec = new IvParameterSpec(new byte[decrypt.getBlockSize()]);
			decrypt.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParamsSpec);
		}
		catch(GeneralSecurityException e)
		{
			e.printStackTrace();
		}
	}

}
