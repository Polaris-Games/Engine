package com.polaris.chat;
import com.polaris.engine.network.SidedNetwork;
import com.polaris.engine.network.server.ServerNetworkManager;

public class ServerNetwork extends ServerNetworkManager
{

	public static final SidedNetwork<ServerNetwork> sidedNetwork = new SidedNetwork<ServerNetwork>(new ServerNetwork());

}
