package com.polaris.engine.logic;

public abstract class LogicSettings 
{
	
	private static LogicSettings ULTRA_PRECISION;
	private static LogicSettings HIGH_PRECISION;
	private static LogicSettings NORMAL_PRECISION;
	
	public static void loadUltraSettings()
	{
		ULTRA_PRECISION = new UltraPrecision();
	}
	
	public static void loadHighSettings()
	{
		HIGH_PRECISION = new HighPrecision();
	}
	
	public static void loadNormalSettings()
	{	
		NORMAL_PRECISION = new NormalPrecision();
	}
	
	public abstract String getIdentifier();
	public abstract Vector genVector();

}
