package org.alfresco.enterprise.repo.content.cryptodoc.io;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

// TODO can we replace the synchronization with r/w locks?
public class ByteBufferChannel implements ReadableByteChannel, WritableByteChannel
{
	
	private final ByteBuffer bb;
	private final ByteBuffer robb;
	private final Object limitLock = new Object();
	
	public ByteBufferChannel(int capacity) 
	{
		this.bb = ByteBuffer.allocate(capacity);
		this.robb = this.bb.asReadOnlyBuffer();
		this.robb.limit(0);
	}
	
	public ByteBufferChannel(ByteBuffer bb)
	{
		this.bb = bb;
		this.robb = this.bb.asReadOnlyBuffer();
		this.bb.position(this.bb.limit());
		this.bb.limit(this.bb.capacity());
	}
	
	public ByteBuffer getByteBuffer()
	{
		return this.robb.asReadOnlyBuffer();
	}
	
	@Override
	public boolean isOpen()
	{
		return true;
	}
	
	@Override
	public void close()
	{
	}
	
	public void compact()
	{
		synchronized (this.limitLock)
		{
			this.robb.compact();
			this.bb.position(this.robb.limit());
			this.bb.limit(this.bb.capacity());
		}
	}
	
	public int bytesToRead()
	{
		return this.robb.remaining();
	}
	
	@Override
	public int read(ByteBuffer bb) throws IOException
	{
		if (this.robb.remaining() <= 0)
			return -1;  // End-of-stream
		
		int oldLimit = this.robb.limit();
		if (this.robb.remaining() > bb.remaining())
			this.robb.limit(this.robb.position() + bb.remaining());
		
		int bytes = this.robb.remaining();
		bb.put(this.robb);

		this.robb.limit(oldLimit);
		
		return bytes;
	}
	
	public int bytesToWrite()
	{
		return this.bb.remaining();
	}
	
	@Override
	public int write(ByteBuffer bb) throws IOException
	{
		int oldLimit = -1;
		if (bb.remaining() > this.bb.remaining())
		{
			oldLimit = bb.limit();
			bb.limit(bb.position() + this.bb.remaining());
		}
		
		int bytes = bb.remaining();
		synchronized (this.limitLock)
		{
			this.bb.put(bb);
			this.robb.limit(this.bb.position());
		}
		
		if (oldLimit > -1)
		{
			bb.limit(oldLimit);
		}
		
		return bytes;
	}

}
