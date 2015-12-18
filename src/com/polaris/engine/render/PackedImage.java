package com.polaris.engine.render;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.polaris.engine.util.Helper;

public class PackedImage 
{

	private int defaultWidth = 2;
	private int defaultHeight = 2;

	private List<Texture> textureList = new ArrayList<Texture>();
	private List<Row> rows = new ArrayList<Row>();

	public PackedImage(List<BufferedImage> images)
	{
		int totalArea = 0;
		for(BufferedImage image : images)
		{
			//checkBounds(image.getWidth(), image.getHeight());
			totalArea += image.getWidth() * image.getHeight();
		}
		defaultWidth = defaultHeight = (int) Math.pow(2, (int) Helper.log(2, Math.sqrt(totalArea) + 1));
		rows.add(new Row(defaultHeight, new Cell(defaultWidth)));
		pack(images);
	}

	/*private void checkBounds(int width, int height)
	{
		if(defaultWidth < width)
		{
			defaultWidth *= 2;
			checkBounds(width, height);
		}
		if(defaultHeight < height)
		{
			defaultHeight *= 2;
			checkBounds(width, height);
		}
	}*/

	private void pack(List<BufferedImage> bufferedImages)
	{
		for(BufferedImage image : bufferedImages)
		{
			int rowSize = rows.size();
			int cellSize = rows.get(0).cells.size();
			int currentRow = 0;
			int currentCell = 0;
			boolean fitInto = false;
			while(!fitInto)
			{
				int tempCell = currentCell;
				while(!fitInto && tempCell < cellSize)
				{
					fitInto = checkIfFits(currentRow, tempCell, rowSize, cellSize, image.getWidth(), image.getHeight(), image);
					tempCell++;
				}

				if(!fitInto)
				{
					int tempRow = currentRow;
					while(!fitInto && tempRow < rows.size())
					{
						fitInto = checkIfFits(tempRow, currentCell, rowSize, cellSize, image.getWidth(), image.getHeight());
						tempRow++;
					}

					if(!fitInto)
					{
						if(rowSize == currentRow || cellSize == currentCell)
						{
							expandImage();
							currentRow = currentCell = 0;
						}
						else
						{
							currentCell = ++currentRow;
						}
					}
				}
			}
		}
	}

	private boolean checkIfFits(int rowPosition, int cellPosition, int rowSize, int cellSize, int width, int height, BufferedImage image)
	{
		if(rowPosition == rowSize || cellPosition == cellSize)
			return false;
		Row row = rows.get(rowPosition);
		Cell cell = row.cells.get(cellPosition);
		if(cell.filled)
			return false;
		boolean flag = true;
		if(cell.width < width && cellPosition + 1 < cellSize)
		{
			flag = checkIfFits(rowPosition, cellPosition + 1, rowSize, cellSize, width - cell.width, height, image);
		}
		if(row.height < height && rowPosition + 1 < rowSize)
		{
			flag = checkIfFits(rowPosition + 1, cellPosition, rowSize, cellSize, width, height - row.height, image);
		}
		if(flag)
			cell.filled = true;
		return flag;
	}

	private void expandImage()
	{
		if(defaultHeight == defaultWidth)
			defaultWidth *= 2;
		else
			defaultHeight *= 2;
	}

	public int getWidth()
	{
		return defaultWidth;
	}

	public int getHeight()
	{
		return defaultHeight;
	}

	public byte[] getData()
	{
		return null;
	}

	private static class Cell
	{
		private int width;
		private boolean filled = false;

		public Cell(int width) 
		{
			this.width = width;
		}
	}

	private static class Row
	{
		private int height;
		private List<Cell> cells = new ArrayList<Cell>();

		public Row(int height, Cell ... cells)
		{
			this.height = height;
			this.cells = Arrays.asList(cells);
		}
	}

}
