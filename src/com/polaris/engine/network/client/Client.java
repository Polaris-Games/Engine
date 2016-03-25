package com.polaris.engine.network.client;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.interfaces.RSAPublicKey;
import java.util.concurrent.LinkedBlockingQueue;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;

import com.polaris.engine.network.Packet;
import com.polaris.engine.network.PacketRequestLock;
import com.polaris.engine.network.SidedNetwork;

public class Client 
{

	private SidedNetwork<? extends ClientNetworkManager> network;
	private Socket socket;
	private InputStream inputStream;
	private OutputStream outputStream;
	private LinkedBlockingQueue<Packet> packetsToSend;

	private Cipher cipher;

	public Client(SidedNetwork<? extends ClientNetworkManager> sidedNetwork, String serverAddress, int port) throws IOException
	{
		network = sidedNetwork;
		socket = new Socket(serverAddress, port);
		if(socket != null)
		{
			socket.setTcpNoDelay(true);
			inputStream = socket.getInputStream();
			outputStream = socket.getOutputStream();
			packetsToSend = new LinkedBlockingQueue<Packet>(256);
			if(inputStream == null || outputStream == null)
			{
				socket.close();
				throw new IOException("");
			}
		}
		else
		{
			throw new IOException("");
		}
	}

	public void validate(boolean establishSecureTunnel)
	{
		if(establishSecureTunnel)
		{
			sendPacket(new PacketRequestLock());
		}
		new Thread()
		{
			@Override
			public void run()
			{
				try 
				{
					while(true)
					{
						int packetIndicator = inputStream.read();
						byte[] data = new byte[Packet.getPacketSize(packetIndicator)];
						inputStream.read(data, 0, data.length);
						network.getSidedNetwork().queueForProcess(Packet.wrap(network, packetIndicator, data));
					}
				} 
				catch (IOException e) 
				{

				}
			}
		}.start();
		new Thread()
		{
			@Override
			public void run()
			{
				try
				{
					while(true)
					{
						Packet packetToSend = packetsToSend.take();
						ByteArrayOutputStream output = new ByteArrayOutputStream();
						packetToSend.wrapHeader(output);
						packetToSend.writeData(output);
						output.writeTo(outputStream);
						outputStream.flush();
					}
				}
				catch (IOException e)
				{
					e.printStackTrace();
				} 
				catch (InterruptedException e) 
				{
					e.printStackTrace();
				}
			}
		}.start();
	}

	public void sendPacket(Packet packetToSend)
	{
		try 
		{
			packetsToSend.put(packetToSend);
		}
		catch (InterruptedException e) 
		{
			e.printStackTrace();
		}
	}

	public void setEncryptionKey(RSAPublicKey key)
	{
		try
		{
			cipher = Cipher.getInstance("RSA/None/NoPadding", "BC");
			cipher.init(Cipher.ENCRYPT_MODE, key);
		}
		catch (NoSuchAlgorithmException | NoSuchProviderException | NoSuchPaddingException | InvalidKeyException e) 
		{
			e.printStackTrace();
		}
	}

	public void invalidate() throws IOException 
	{
		socket.close();
	}

}
