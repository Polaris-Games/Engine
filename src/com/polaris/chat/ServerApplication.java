package com.polaris.chat;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Random;

import com.polaris.engine.network.server.Server;
import com.polaris.engine.util.MathHelper;

public class ServerApplication 
{
	
	public static void main(String[] args) throws IOException
	{
		Server server = new Server(ServerNetwork.sidedNetwork);
	}

}
