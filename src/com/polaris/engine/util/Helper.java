package com.polaris.engine.util;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

import javax.imageio.ImageIO;

public class Helper 
{

	/**
	 * Current OS that this application is running on. Though, this will only give generic os names.
	 * @value windows, linux, osx
	 */
	public static final String osName;
	/**
	 * Pi * 2
	 */
	public static final double TWOPI = Math.PI * 2;
	/**
	 * PI / 2
	 */
	public static final double HALFPI = Math.PI / 2;
	
	public static final ClassLoader classLoader = Helper.class.getClassLoader();

	static
	{
		String s = System.getProperty("os.name", "generic").toLowerCase(Locale.ENGLISH);
		if(s.contains("windows"))
			osName = "windows";
		else if(s.contains("linux"))
			osName = "linux";
		else
			osName = "osx";
	}

	/**
	 * Retrieves a linear number from one number to another
	 * @param value : The current value
	 * @param toValue : The value for which value wants to become
	 * @param modifier : How fast for the value to get there
	 * @return either (value - modifier) or (value + modifier) with a cap at toValue
	 */
	public static double getLinearValue(double value, double toValue, double modifier)
	{
		if(value > toValue)
			return Math.max(toValue, value - modifier);
		return Math.min(toValue, value + modifier);
	}

	/**
	 * Retrieves a linear number from one number to another
	 * @param value : The current value
	 * @param toValue : The value for which value wants to become
	 * @param modifier : How fast for the value to get there
	 * @return either (value - modifier) or (value + modifier) with a cap at toValue
	 */
	public static float getLinearValue(float value, float toValue, float modifier)
	{
		if(value > toValue)
			return Math.max(toValue, value - modifier);
		return Math.min(toValue, value + modifier);
	}

	/**
	 * Retrieves a linear number from one number to another
	 * @param value : The current value
	 * @param toValue : The value for which value wants to become
	 * @param modifier : How fast for the value to get there
	 * @param delta : For use with rendering speeds, makes sure the value is coherent with the logic loop ticking
	 * @return either (value - modifier * delta) or (value + modifier * delta) with a cap at toValue
	 */
	public static double getLinearValue(double value, double toValue, double modifier, double delta)
	{
		if(value > toValue)
			return Math.max(toValue, value - modifier * delta);
		return Math.min(toValue, value + modifier * delta);
	}

	/**
	 * Retrieves a linear number from one number to another
	 * @param value : The current value
	 * @param toValue : The value for which value wants to become
	 * @param modifier : How fast for the value to get there
	 * @param delta : For use with rendering speeds, makes sure the value is coherent with the logic loop ticking
	 * @return either (value - modifier * delta) or (value + modifier * delta) with a cap at toValue
	 */
	public static double getLinearValue(double value, double toValue, double modifier, float delta)
	{
		if(value > toValue)
			return Math.max(toValue, value - modifier * delta);
		return Math.min(toValue, value + modifier * delta);
	}

	/**
	 * Retrieves a linear number from one number to another
	 * @param value : The current value
	 * @param toValue : The value for which value wants to become
	 * @param modifier : How fast for the value to get there
	 * @param delta : For use with rendering speeds, makes sure the value is coherent with the logic loop ticking
	 * @return either (value - modifier) or (value * delta + modifier * delta) with a cap at toValue
	 */
	public static float getLinearValue(float value, float toValue, float modifier, double delta)
	{
		if(value > toValue)
			return Math.max(toValue, value - modifier * (float) delta);
		return Math.min(toValue, value + modifier * (float) delta);
	}

	/**
	 * Retrieves a linear number from one number to another
	 * @param value : The current value
	 * @param toValue : The value for which value wants to become
	 * @param modifier : How fast for the value to get there
	 * @param delta : For use with rendering speeds, makes sure the value is coherent with the logic loop ticking
	 * @return either (value - modifier * delta) or (value + modifier * delta) with a cap at toValue
	 */
	public static float getLinearValue(float value, float toValue, float modifier, float delta)
	{
		if(value > toValue)
			return Math.max(toValue, value - modifier * delta);
		return Math.min(toValue, value + modifier * delta);
	}

	public static double getExpValue(double value, double toValue, double modifier)
	{
		return value + (toValue - value) / modifier;
	}

	public static float getExpValue(float value, float toValue, float modifier)
	{
		return value + (toValue - value) / modifier;
	}

	public static double getExpValue(double value, double toValue, double modifier, double delta)
	{
		return value + ((toValue - value) / modifier) * delta;
	}

	public static double getExpValue(double value, double toValue, double modifier, float delta)
	{
		return value + ((toValue - value) / modifier) * delta;
	}

	public static float getExpValue(float value, float toValue, float modifier, double delta)
	{
		return value + ((toValue - value) / modifier) * (float) delta;
	}

	public static float getExpValue(float value, float toValue, float modifier, float delta)
	{
		return value + ((toValue - value) / modifier) * delta;
	}

	public static boolean isEqual(double value, double value1)
	{
		return Math.abs(value - value1) < .0001;
	}

	public static boolean isEqual(float value, float value1)
	{
		return Math.abs(value - value1) < .0001f;
	}
	
	public static boolean isEqual(double value, double value1, double tolerance)
	{
		return Math.abs(value - value1) < tolerance;
	}

	public static boolean isEqual(float value, float value1, float tolerance)
	{
		return Math.abs(value - value1) < tolerance;
	}

	public static int random(int maxValue)
	{
		return (int) (Math.random() * (maxValue + 1));
	}

	public static float random(float maxValue)
	{
		return (float) (Math.random() * maxValue);
	}

	public static double random(double maxValue)
	{
		return Math.random() * maxValue;
	}

	public static int random(int minValue, int maxValue)
	{
		return (int) (Math.random() * (maxValue - minValue + 1)) + minValue;
	}

	public static float random(float minValue, float maxValue)
	{
		return (float) (Math.random() * (maxValue - minValue)) + minValue;
	}

	public static double random(double minValue, double maxValue)
	{
		return Math.random() * (maxValue - minValue) + maxValue;
	}

	public static double clamp(double min, double max, double value)
	{
		if(min > value)
			return min;
		if(max < value)
			return max;
		return value;
	}

	public static byte[] convertToBytes(List<Byte> list)
	{
		byte[] array = new byte[list.size()];
		for(int i = 0; i < array.length; i++)
		{
			array[i] = list.get(i);
		}
		return array;
	}

	public static String[] readCompressedFile(File file) throws IOException
	{
		List<String> stringList = new ArrayList<String>();
		InflaterInputStream stream = new InflaterInputStream(new FileInputStream(file), new Inflater(), 1024);
		BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
		String line = null;
		try
		{
			while((line = reader.readLine()) != null)
			{
				stringList.add(line);
			}
		}
		catch(Exception e) {}
		reader.close();
		return stringList.toArray(new String[stringList.size()]);
	}

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

	/*public static void restart() throws IOException
	{
		StringBuilder cmd = new StringBuilder();
		cmd.append(System.getProperty("java.home") + File.separator + "bin" + File.separator + "java ");
		for (String jvmArg : ManagementFactory.getRuntimeMXBean().getInputArguments()) 
		{
			cmd.append(jvmArg + " ");
		}
		cmd.append("-cp ").append(ManagementFactory.getRuntimeMXBean().getClassPath()).append(" ");
		cmd.append(Main.class.getName()).append(" ");
		Runtime.getRuntime().exec(cmd.toString());
		System.exit(0);
	}*/

	public static boolean withinBounds(double x, double y, double x1, double y1, double w, double h)
	{
		return x1 <= x && y1 <= y && x1 + w >= x && y1 + h >= y;
	}

	public static URL getResource(String s)
	{
		return classLoader.getResource(s);
	}

	public static InputStream getResourceStream(String s)
	{
		return classLoader.getResourceAsStream(s);
	}

	public static void createFileSafely(File file) throws IOException
	{
		File parentFile = new File(file.getParent());
		if(!parentFile.exists())
		{
			if(!parentFile.mkdirs())
			{
				throw new IOException("Unable to create parent file: "+file.getParent());
			}
		}
		if(file.exists())
		{
			if(!file.delete())
			{
				throw new IOException("Couldn't delete '".concat(file.getAbsolutePath()).concat("'"));
			}
		}
		if(!file.createNewFile())
		{
			throw new IOException("Couldn't create '".concat(file.getAbsolutePath()).concat("'"));
		}
	}

	public static void downloadFile(String url, File dest) throws IOException
	{
		String md5 = null;
		if (dest.exists())
		{
			md5 = getMD5(dest);
		}

		HttpURLConnection connection = (HttpURLConnection)new URL(url).openConnection();
		if (md5 != null)
		{
			connection.setRequestProperty("If-None-Match", md5);
		}
		connection.connect();

		if(connection.getResponseCode() == 304)
		{
			return;
		}

		createFileSafely(dest);

		BufferedInputStream in = new BufferedInputStream(connection.getInputStream(), 16384);
		BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(dest), 16384);

		int readBytes = 0;
		byte[] block = new byte[16384];

		while ((readBytes = in.read(block)) > 0)
		{
			out.write(block, 0, readBytes);
		}
		out.flush();
		out.close();
		in.close();
		connection.disconnect();
	}

	public static BufferedImage downloadImage(String url)
	{
		HttpURLConnection connection = null;
		try
		{
			connection = (HttpURLConnection) new URL(url).openConnection();
			connection.connect();
			BufferedImage image = ImageIO.read(connection.getInputStream());
			connection.disconnect();
			return image;
		}
		catch(IOException e)
		{
			e.printStackTrace();
			if(connection != null)
			{
				connection.disconnect();
			}
			return null;
		}
	}

	public static byte[] readStreamFully(InputStream stream) throws IOException
	{
		byte[] data = new byte[4096];
		ByteArrayOutputStream entryBuffer = new ByteArrayOutputStream();
		int len;
		do
		{
			len = stream.read(data);
			if (len > 0)
			{
				entryBuffer.write(data, 0, len);
			}
		} while (len != -1);

		return entryBuffer.toByteArray();
	}

	public static void copyFile(File from, File to) throws IOException 
	{
		createFileSafely(to);
		BufferedInputStream bis = new BufferedInputStream(new FileInputStream(from));
		BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(to));
		byte[] block;
		while (bis.available() > 0) 
		{
			block = new byte[16384];
			final int readNow = bis.read(block);
			bos.write(block, 0, readNow);
		}
		bos.flush();
		bos.close();
		bis.close();
	}

	public static String getMD5(File file) throws IOException
	{
		DigestInputStream stream = null;
		try
		{
			stream = new DigestInputStream(new FileInputStream(file), MessageDigest.getInstance("MD5"));
			byte[] buffer = new byte[65536];

			int read = stream.read(buffer);
			while (read >= 1)
				read = stream.read(buffer);
		} 
		catch (Exception ignored)
		{
			return null;
		} 
		finally
		{
			stream.close();
		}

		return String.format("%1$032x", new Object[]{new BigInteger(1, stream.getMessageDigest().digest())});
	}

	public static int getSign(double value)
	{
		return value < 0 ? -1 : 1;
	}
	
	public static int getSign(float value)
	{
		return value < 0 ? -1 : 1;
	}
	
	public static int getSign(int value)
	{
		return value < 0 ? -1 : 1;
	}
	
	public static double getAxis(double x, double y)
	{
		if(y == 0)
			return -HALFPI * (getSign(x) - 1);
		if(x == 0)
			return -HALFPI * (getSign(y) - 2);
		return Math.tan(y / x);
	}

	public static double rotate(double x, double y, double axis) 
	{
		return x * Math.cos(axis) + y * Math.sin(axis);
	}
	
	public static double log(double base, double value)
	{
		return Math.log(value) / Math.log(base);
	}
	
	public static BufferedImage resize(BufferedImage image, int newWidth, int newHeight)
	{
		BufferedImage dimg = new BufferedImage(newWidth, newHeight, image.getType());
		for(int i = 0; i < image.getWidth(); i++)
		{
			for(int j = 0; j < image.getHeight(); j++)
			{
				dimg.setRGB(i, j, image.getRGB(i, j));
			}
		}
		System.out.println("test");
		return dimg;
	}
	
	public static void injectBufferedImage(BufferedImage image, BufferedImage injectionImage, int x, int y)
	{
		for(int i = 0; i < injectionImage.getWidth(); i++)
		{
			for(int j = 0; j < injectionImage.getHeight(); j++)
			{
				image.setRGB(i + x, j + y, injectionImage.getRGB(i, j));
			}
		}
	}

	public static BufferedReader newReader(File file) throws FileNotFoundException 
	{
		return new BufferedReader(new FileReader(file));
	}

	public static BufferedWriter newWriter(File file) throws IOException
	{
		return new BufferedWriter(new FileWriter(file));
	}

	public static boolean fileStartsWith(File file, String ... strings)
	{
		boolean flag = false;
		for(int i = 0; i < strings.length && !flag; i++)
		{
			flag = file.getName().startsWith(strings[i]);
		}
		return flag;
	}
	
}
