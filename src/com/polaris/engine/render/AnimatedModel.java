package com.polaris.engine.render;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;

public class AnimatedModel extends Model
{

	public AnimatedModel(File modelLocation)
	{
		super(modelLocation);
	}

	@Override
	protected void loadPolygons(BufferedReader reader) throws IOException
	{
		
	}

}
