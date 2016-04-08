package com.polaris.engine.network;

import java.io.IOException;
import java.net.Socket;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

import com.polaris.engine.Application;


public class ClientNetwork extends Network
{

	private Application application = null;


	public void connect(String serverAddress, int port) throws IOException
	{
		connect(new Socket(serverAddress, port));
	}

	public void validate(boolean establishSecureTunnel)
	{
		super.validate();
		if(establishSecureTunnel)
		{
			sendPacket(new PacketRSA());
			while(encrypt == null && decrypt == null)
			{
				try 
				{
					Thread.sleep(10);
				} 
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
			}
		}
	}

	public void createAESKey(byte[] rsaPublicKey, int aesBitLength)
	{
		try
		{
			KeyGenerator keyGen = KeyGenerator.getInstance("AES");
			keyGen.init(aesBitLength);
			SecretKey secretKey = keyGen.generateKey();
			
			encrypt = Cipher.getInstance("AES/CBC/PKCS5Padding");
			IvParameterSpec ivParamsSpec = new IvParameterSpec(new byte[encrypt.getBlockSize()]);
			encrypt.init(Cipher.ENCRYPT_MODE, secretKey, ivParamsSpec);
			decrypt = Cipher.getInstance("AES/CBC/PKCS5Padding");
			ivParamsSpec = new IvParameterSpec(new byte[decrypt.getBlockSize()]);
			decrypt.init(Cipher.DECRYPT_MODE, secretKey, ivParamsSpec);
			
			RSAPublicKey publicKey = (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(rsaPublicKey));
			Cipher rsaCipher = Cipher.getInstance("RSA");
			rsaCipher.init(Cipher.ENCRYPT_MODE, publicKey);
			
			sendPacket(new PacketAES(rsaCipher, secretKey));
		}
		catch (GeneralSecurityException e) 
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
