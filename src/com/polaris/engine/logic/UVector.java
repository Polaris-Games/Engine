package com.polaris.engine.logic;

import static java.math.MathContext.DECIMAL128;

import java.math.BigDecimal;
import java.math.MathContext;;

public final class UVector extends Vector
{
	
	private BigDecimal x;
	private BigDecimal y;
	private BigDecimal z;
	
	public UVector()
	{
		this.x = new BigDecimal(0);
		this.y = new BigDecimal(0);
		this.z = new BigDecimal(0);
	}
	
	public UVector(MathContext context)
	{
		this.x = new BigDecimal(0, context);
		this.y = new BigDecimal(0, context);
		this.z = new BigDecimal(0, context);
	}
	
	public UVector(String value)
	{
		this.x = new BigDecimal(value);
		this.y = new BigDecimal(value);
		this.z = new BigDecimal(value);
	}
	
	public UVector(String value, MathContext context)
	{
		this.x = new BigDecimal(value, context);
		this.y = new BigDecimal(value, context);
		this.z = new BigDecimal(value, context);
	}
	
	public UVector(String x, String y, String z)
	{
		this.x = new BigDecimal(x);
		this.y = new BigDecimal(y);
		this.z = new BigDecimal(z);
	}
	
	public UVector(String x, String y, String z, MathContext context)
	{
		this.x = new BigDecimal(x, context);
		this.y = new BigDecimal(y, context);
		this.z = new BigDecimal(z, context);
	}
	
	public UVector(double val)
	{
		this.x = new BigDecimal(val);
		this.y = new BigDecimal(val);
		this.z = new BigDecimal(val);
	}
	
	public UVector(double val, MathContext context)
	{
		this.x = new BigDecimal(val, context);
		this.y = new BigDecimal(val, context);
		this.z = new BigDecimal(val, context);
	}
	
	public UVector(double x, double y, double z)
	{
		this.x = new BigDecimal(x);
		this.y = new BigDecimal(y);
		this.z = new BigDecimal(z);
	}
	
	public UVector(double x, double y, double z, MathContext context)
	{
		this.x = new BigDecimal(x, context);
		this.y = new BigDecimal(y, context);
		this.z = new BigDecimal(z, context);
	}
	
	public UVector(int val)
	{
		this.x = new BigDecimal(val);
		this.y = new BigDecimal(val);
		this.z = new BigDecimal(val);
	}
	
	public UVector(int val, MathContext context)
	{
		this.x = new BigDecimal(val, context);
		this.y = new BigDecimal(val, context);
		this.z = new BigDecimal(val, context);
	}
	
	public UVector(int x, int y, int z)
	{
		this.x = new BigDecimal(x);
		this.y = new BigDecimal(y);
		this.z = new BigDecimal(z);
	}
	
	public UVector(int x, int y, int z, MathContext context)
	{
		this.x = new BigDecimal(x, context);
		this.y = new BigDecimal(y, context);
		this.z = new BigDecimal(z, context);
	}
	
	public UVector(UVector vector)
	{
		this.x = new BigDecimal(vector.x.toBigInteger());
		this.y = new BigDecimal(vector.y.toBigInteger());
		this.z = new BigDecimal(vector.z.toBigInteger());
	}
	
	public UVector(UVector vector, MathContext context)
	{
		x = new BigDecimal(vector.x.toBigInteger(), context);
		y = new BigDecimal(vector.y.toBigInteger(), context);
		z = new BigDecimal(vector.z.toBigInteger(), context);
	}

	@Override
	public Vector copy() 
	{
		return null;
	}

	@Override
	public void move(Vector moveVector) 
	{
		
	}

	@Override
	public Vector add(Vector vector) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Vector add(Vector vector, Vector dest) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Vector sub(Vector vector) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Vector sub(Vector vector, Vector dest) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Vector mul(Vector vector) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Vector mul(Vector vector, Vector dest) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Vector div(Vector vector) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Vector div(Vector vector, Vector dest) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Vector fma(Vector firstVector, Vector secondVector) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Vector fma(Vector vector) {
		// TODO Auto-generated method stub
		return null;
	}
	
}