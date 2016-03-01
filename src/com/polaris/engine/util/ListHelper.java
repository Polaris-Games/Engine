package com.polaris.engine.util;

public class ListHelper 
{
	
	public static int getListLoc(int number, int size)
	{
		if(number < 0)
			return getListLoc(number + size, size);
		if(number >= size)
			return getListLoc(number - size, size);
		return number;
	}

	public static boolean listLocEqual(int number, int number1, int size)
	{
		return getListLoc(number, size) == getListLoc(number1, size);
	}

}
