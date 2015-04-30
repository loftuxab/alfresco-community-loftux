package org.alfresco.enterprise.repo.content.cryptodoc;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import org.alfresco.enterprise.repo.content.cryptodoc.io.ByteBufferChannel;
import org.alfresco.enterprise.repo.content.cryptodoc.jce.DecryptingByteChannel;
import org.alfresco.enterprise.repo.content.cryptodoc.jce.EncryptingByteChannel;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class EncryptDecryptUnitTest {
	
	private Charset charset = Charset.defaultCharset();
	private SecretKey aesKey;
	private SecretKey desKey;
	private SecretKey desedeKey;
	
	@Before
	public void generateAesKey() throws NoSuchAlgorithmException {
		KeyGenerator keygen = KeyGenerator.getInstance("AES");
		keygen.init(128);
		this.aesKey = keygen.generateKey();
	}
	
	@Before
	public void generateDesKey() throws NoSuchAlgorithmException {
		KeyGenerator keygen = KeyGenerator.getInstance("DES");
		keygen.init(56);
		this.desKey = keygen.generateKey();
	}
	
	@Before
	public void generateDesedeKey() throws NoSuchAlgorithmException {
		KeyGenerator keygen = KeyGenerator.getInstance("DESede");
		keygen.init(168);
		this.desedeKey = keygen.generateKey();
	}
	
	@Test
	public void testBlockSize() throws Exception {
		String dataAsString = "Hello World!!!!!";
		Assert.assertEquals(dataAsString, this.test(this.aesKey, dataAsString, 64));

		dataAsString = "Hello!!!";
		Assert.assertEquals(dataAsString, this.test(this.desKey, dataAsString, 64));
		Assert.assertEquals(dataAsString, this.test(this.desedeKey, dataAsString, 64));
	}
	
	@Test
	public void testDoubleBlockSize() throws Exception {
		String dataAsString = "Hello World! Brian Long is here!";
		Assert.assertEquals(dataAsString, this.test(this.aesKey, dataAsString, 64));

		dataAsString = "Hello World!!!!!";
		Assert.assertEquals(dataAsString, this.test(this.desKey, dataAsString, 64));
		Assert.assertEquals(dataAsString, this.test(this.desedeKey, dataAsString, 64));
	}
	
	@Test
	public void testShorterThanBlockSize() throws Exception {
		String dataAsString = "Hello!";
		Assert.assertEquals(dataAsString, this.test(this.aesKey, dataAsString, 64));
		Assert.assertEquals(dataAsString, this.test(this.desKey, dataAsString, 64));
		Assert.assertEquals(dataAsString, this.test(this.desedeKey, dataAsString, 64));
	}
	
	@Test
	public void testLongerThanBlockSize() throws Exception {
		String dataAsString = "Hello World! Brian is here!";
		Assert.assertEquals(dataAsString, this.test(this.aesKey, dataAsString, 64));
		Assert.assertEquals(dataAsString, this.test(this.desKey, dataAsString, 64));
		Assert.assertEquals(dataAsString, this.test(this.desedeKey, dataAsString, 64));
	}
	
	@Test
	public void testEmpty() throws Exception {
		String dataAsString = "";
		Assert.assertEquals(dataAsString, this.test(this.aesKey, dataAsString, 64));
		Assert.assertEquals(dataAsString, this.test(this.desKey, dataAsString, 64));
		Assert.assertEquals(dataAsString, this.test(this.desedeKey, dataAsString, 64));
	}
	
	@Test
	public void testMuchLongerThanBlockSize() throws Exception {
		String dataAsString = "Hello World! Brian is here! This is meant to be a long message that is at least 64 bytes long. This will require the function to chunk the decryption.";
		Assert.assertEquals(dataAsString, this.test(this.aesKey, dataAsString, 64));
		Assert.assertEquals(dataAsString, this.test(this.desKey, dataAsString, 64));
		Assert.assertEquals(dataAsString, this.test(this.desedeKey, dataAsString, 64));
	}
	
	@Test
	public void testDoubleBufferLengthBlockSize() throws Exception {
		String dataAsString = "Hello World! This is exactly twice the buffer length of 64......";
		Assert.assertEquals(dataAsString, this.test(this.aesKey, dataAsString, 32));
		Assert.assertEquals(dataAsString, this.test(this.desKey, dataAsString, 32));
		Assert.assertEquals(dataAsString, this.test(this.desedeKey, dataAsString, 32));
	}
	
	@Test
	public void testBufferSameBlockSize() throws Exception {
		String dataAsString = "Hello World! Brian is here!";
		Assert.assertEquals(dataAsString, this.test(this.aesKey, dataAsString, 16));
	}
	
	private String test(Key key, String text, int bufferSize) throws Exception {
		ByteBuffer dataAsByteBuffer = ByteBuffer.wrap(text.getBytes(this.charset));
		
		ByteBufferChannel bbchannel = new ByteBufferChannel(500);
		EncryptingByteChannel ebchannel = new EncryptingByteChannel(bbchannel, key);
		ebchannel.write(dataAsByteBuffer);
		ebchannel.close();
		
		dataAsByteBuffer = ByteBuffer.allocate(bufferSize);
		
		StringBuilder sbuilder = new StringBuilder();

		DecryptingByteChannel dbchannel = new DecryptingByteChannel(bbchannel, key);
		while (dbchannel.read(dataAsByteBuffer) != -1) {
			dataAsByteBuffer.flip();
			sbuilder.append(this.charset.decode(dataAsByteBuffer));
			dataAsByteBuffer.compact();
		}
		
		Assert.assertEquals(-1, dbchannel.read(dataAsByteBuffer));
		dbchannel.close();
		
		return sbuilder.toString();
	}

}
