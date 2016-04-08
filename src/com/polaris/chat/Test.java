package com.polaris.chat;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPublicKey;

import javax.crypto.Cipher;

public class Test
{
	
	public static void main(String[] args) throws Exception
	{
		KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
		keyGen.initialize(1024);
		KeyPair key = keyGen.generateKeyPair();
		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.DECRYPT_MODE, key.getPrivate());
		RSAPublicKey publicKey = (RSAPublicKey) key.getPublic();
		System.out.println(publicKey.getModulus());
		System.out.println(publicKey.getPublicExponent());
		System.out.println(publicKey.getEncoded().length);
	}

}
