package com.polaris.chat;

import com.polaris.engine.network.SidedNetwork;
import com.polaris.engine.network.client.ClientNetworkManager;

public class ClientNetwork extends ClientNetworkManager
{

	public static SidedNetwork<ClientNetwork> sidedNetwork = new SidedNetwork<ClientNetwork>(new ClientNetwork());

}
