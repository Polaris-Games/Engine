package com.polaris.engine.util;

public class DimensionalKey 
{
	
	private int hashcode = 0;
	
	public DimensionalKey(Integer ... integers)
	{
		
	}

    @Override
    public boolean equals(Object o) 
    {
        return o.hashCode() == hashCode();
    }

    @Override
    public int hashCode()
    {
        return hashcode;
    }

}
