package org.alfresco.enterprise.repo.content.cryptodoc.io;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

import org.apache.log4j.LogManager;

public class IOUtils
{
	// TODO fix up logging in here

	public static void copy(InputStream istream, OutputStream ostream, int chunkSize) throws IOException
	{
		byte[] bytes = new byte[chunkSize];
		
		int bytesRead = 0;
		while (bytesRead >= 0) {
			bytesRead = istream.read(bytes);
			if (bytesRead > 0)
				ostream.write(bytes, 0, bytesRead);
		}
	}
	
	public static void copy(ReadableByteChannel rbchannel, OutputStream ostream, int chunkSize) throws IOException
	{
		WritableByteChannel wbchannel = Channels.newChannel(ostream);
		copy(rbchannel, wbchannel, chunkSize);
	}
	
	public static void copy(InputStream istream, WritableByteChannel wbchannel, int chunkSize) throws IOException 
	{
		ReadableByteChannel rbchannel = Channels.newChannel(istream);
		copy(rbchannel, wbchannel, chunkSize);
	}
	
	public static void copy(ReadableByteChannel rbchannel, WritableByteChannel wbchannel, int chunkSize) throws IOException
	{
		ByteBuffer bb = ByteBuffer.allocate(chunkSize);
		
		int bytesRead = 0;
		while (bytesRead >= 0)
		{
			bytesRead = rbchannel.read(bb);
			if (bytesRead > 0)
			{
				bb.flip();
				wbchannel.write(bb);
				bb.compact();
			}
		}
	}
	
	public static void closeQuietly(InputStream istream)
	{
		try
		{
			istream.close();
		}
		catch (IOException ie)
		{
			LogManager.getLogger(IOUtils.class).warn("An input stream failed to close: " + ie.getMessage(), ie);
		}
	}
	
	public static void closeQuietly(OutputStream ostream) {
		try
		{
			ostream.close();
		}
		catch (IOException ie)
		{
			LogManager.getLogger(IOUtils.class).warn("An output stream failed to close: " + ie.getMessage(), ie);
		}
	}
	
	public static void closeQuietly(Closeable closeable) {
		try
		{
			closeable.close();
		}
		catch (IOException ie)
		{
			LogManager.getLogger(IOUtils.class).warn("A channel failed to close: " + ie.getMessage(), ie);
		}
	}

}
