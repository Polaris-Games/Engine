package com.polaris.engine.network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.SecretKey;

public class PacketAES extends Packet
{
	
	private byte[] encoded;
	
	public PacketAES() {}
	
	public PacketAES(Cipher cipher, SecretKey aesKey) throws IllegalBlockSizeException, BadPaddingException
	{
		encoded = cipher.doFinal(aesKey.getEncoded());
	}

	@Override
	public void writeData(DataOutputStream output) throws IOException
	{
		output.write(encoded);
	}

	@Override
	public void copy(DataInputStream data) throws IOException 
	{
		encoded = new byte[data.available()];
		data.readFully(encoded);
	}

	@Override
	public void handle(Network network) 
	{
		((ServerNetwork)network).setAESKey(encoded);
	}
	
	

}
