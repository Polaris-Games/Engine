package com.polaris.engine.logic;

public class OpenLogic 
{
	
	private static LogicSettings currentLogicSettings;
	
	public static void setCurrentSettings(LogicSettings settings)
	{
		currentLogicSettings = settings;
	}
	
	public static Vector olGenVector()
	{
		return currentLogicSettings.genVector();
	}

}
