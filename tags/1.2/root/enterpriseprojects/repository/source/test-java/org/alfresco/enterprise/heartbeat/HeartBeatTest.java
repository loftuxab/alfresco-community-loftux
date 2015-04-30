/*
 * Copyright 2005-2010 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.heartbeat;

import java.beans.XMLDecoder;
import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import junit.framework.TestCase;

import org.alfresco.encryption.DecryptingInputStream;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.service.transaction.TransactionService;
import org.alfresco.test_category.OwnJVMTestsCategory;
import org.alfresco.util.ResourceFinder;
import org.junit.experimental.categories.Category;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;

/**
 * An integration test for the heartbeat service. Fakes an HTTP endpoint with a server socket in order to sure the
 * service is functioning correctly.
 * 
 * @author dward
 */
public class HeartBeatTest extends TestCase
{

    private static final String[] CONFIG_LOCATIONS = new String[]
    {
        "classpath:alfresco/application-context.xml", "classpath:license-application-context-test.xml",
    };

    private static final ApplicationContext CONTEXT;

    // Set up the ApplicationContext
    static
    {
        ResourcePatternResolver resolver = new ResourceFinder();
        // Put the test directory at the front of the classpath
        try
        {
            Resource[] resources = resolver.getResources("classpath*:/heartbeattest");
            File sourceDir = resources[0].getFile();

            // Let's give our classloader 'child-first' resource loading qualities!
			// This lets us use a different keystore for unit test purposes
            ClassLoader classLoader = new URLClassLoader(new URL[]
            {
                sourceDir.toURL()
            }, Thread.currentThread().getContextClassLoader())
            {
                @Override
                public URL getResource(String name)
                {
                    URL ret = findResource(name);
                    return ret == null ? super.getResource(name) : ret;
                }
            };
            ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(HeartBeatTest.CONFIG_LOCATIONS,
                    false);
            context.setClassLoader(classLoader);
            context.refresh();
            CONTEXT = context;
        }
        catch (IOException e)
        {
            throw new ExceptionInInitializerError(e);
        }

    }

    /**
     * Test heart beat.
     * 
     * @throws Exception
     *             the exception
     */
    @SuppressWarnings("unchecked")
    public void testHeartBeat() throws Exception
    {
        // Load the private key from the trial key store
        PrivateKey privateKey;
        {
            final KeyStore keyStore = KeyStore.getInstance("JKS");
            final InputStream in = HeartBeatTest.CONTEXT.getResource(HeartBeat.PUBLIC_STORE).getInputStream();
            keyStore.load(in, HeartBeat.PUBLIC_STORE_PWD);
            in.close();
            privateKey = (PrivateKey) keyStore.getKey("heartbeat", HeartBeat.PUBLIC_STORE_PWD);
        }

        TransactionService transactionService = (TransactionService) HeartBeatTest.CONTEXT
                .getBean("transactionService");
        @SuppressWarnings("unused")
        HeartBeat heartBeat = transactionService.getRetryingTransactionHelper().doInTransaction(
                new RetryingTransactionHelper.RetryingTransactionCallback<HeartBeat>()
                {

                    public HeartBeat execute() throws Throwable
                    {
                        // Construct a heartbeat instance in test mode (beats every second using test public
                        // key)
                        return new HeartBeat(HeartBeatTest.CONTEXT, true);
                    }
                }, transactionService.isReadOnly());

        ServerSocket serverSocket = new ServerSocket(9999);
        serverSocket.setSoTimeout(10*60*1000);

        // Now attempt to parse 4 of the 'beats'
        for (int i = 0; i < 4; i++)
        {
            Socket clientSocket = serverSocket.accept();
            XMLDecoder decoder = null;
            InputStream in = null;
            OutputStream out = null;
            try
            {
                in = new GZIPInputStream(new DecryptingInputStream(new HttpInputStream(clientSocket
                        .getInputStream()), privateKey), 1024);
                out = clientSocket.getOutputStream();
                decoder = new XMLDecoder(in);
                Map<String, String> params = (Map<String, String>) decoder.readObject();
                out.write("HTTP/1.1 200 OK\r\n\r\n".getBytes("ASCII"));
                System.out.println(params);
            }
            finally
            {
                if (decoder != null)
                {
                    try
                    {
                        decoder.close();
                        in = null;
                    }
                    catch (final Exception e)
                    {
                    }
                }
                if (in != null)
                {
                    try
                    {
                        in.close();
                    }
                    catch (final Exception e)
                    {
                    }
                }
                if (out != null)
                {
                    try
                    {
                        out.close();
                    }
                    catch (final Exception e)
                    {
                    }
                }
                try
                {
                    clientSocket.close();
                }
                catch (Exception e)
                {
                }
            }

        }
        serverSocket.close();

    }

    /**
     * Wraps a raw byte stream in a HTTP request to look like a regular input stream. Skips headers and parses body or
     * chunk sizes.
     */
    public static class HttpInputStream extends InputStream
    {
        /**
         * The header that signals a non-chunked encoding will be used.
         */
        private static final String HEADER_CONTENT_LENGTH = "Content-Length:";

        /** The raw input stream. */
        private final InputStream socketIn;

        /** A buffer for parsing headers. */
        private StringBuilder headerBuff = new StringBuilder(100);

        /** Are we using chunked encoding ? */
        private boolean isChunked;
        
        /** The current chunk size. */
        private int chunkSize;

        /** The current position in the chunk. */
        private int chunkPosition;

        /** Have we got to the end of the last chunk? */
        private boolean isAtEnd;

        /**
         * Instantiates a new http input stream.
         * 
         * @param socketIn
         *            raw input stream from an HTTP request
         * @throws IOException
         *             Signals that an I/O exception has occurred.
         */
        public HttpInputStream(InputStream socketIn) throws IOException
        {
            this.socketIn = socketIn;
            this.isChunked = true;
            for (;;)
            {
                String header = getNextHeader(); 
                if (header.length() == 0)
                {
                    break;
                }
                if (header.startsWith(HEADER_CONTENT_LENGTH))
                {
                    this.chunkSize = Integer.parseInt(header.substring(HEADER_CONTENT_LENGTH.length()).trim());
                    this.isChunked = false;
                }
            }
            if (this.isChunked)
            {
                setNextChunkSize();
            }
        }

        /**
         * Gets the next header.
         * 
         * @return the next header
         * @throws IOException
         *             Signals that an I/O exception has occurred.
         */
        private String getNextHeader() throws IOException
        {
            int b;
            while ((b = this.socketIn.read()) != '\n')
            {
                if (b == -1)
                {
                    throw new EOFException();
                }
                this.headerBuff.append((char) b); // cast to char acceptable because this is ASCII
            }
            String header = this.headerBuff.toString().trim();
            this.headerBuff.setLength(0);
            return header;
        }

        /**
         * Sets the next chunk size by parsing a chunk header. May detect an end of file condition and set isAtEnd =
         * true.
         * 
         * @return the next chunk size
         * @throws IOException
         *             Signals that an I/O exception has occurred.
         */
        private int setNextChunkSize() throws IOException
        {
            String chunkHeader = getNextHeader();
            int sepIndex = chunkHeader.indexOf(';');
            if (sepIndex != -1)
            {
                chunkHeader = chunkHeader.substring(0, sepIndex).trim();
            }
            this.chunkSize = Integer.parseInt(chunkHeader, 16);
            this.chunkPosition = 0;
            if (this.chunkSize == 0)
            {
                this.isAtEnd = true;
            }
            return this.chunkSize;
        }

        /*
         * (non-Javadoc)
         * @see java.io.InputStream#close()
         */
        @Override
        public void close() throws IOException
        {
            // We intentionally avoid closing the socket input stream here, as that seems to close the entire socket,
            // and stops us from being able to write a response!
            // this.socketIn.close();
        }

        /*
         * (non-Javadoc)
         * @see java.io.InputStream#read()
         */
        @Override
        public int read() throws IOException
        {
            final byte[] buf = new byte[1];
            int bytesRead;
            while ((bytesRead = read(buf)) == 0)
            {
                ;
            }
            return bytesRead == -1 ? -1 : buf[0] & 0xFF;
        }

        /*
         * (non-Javadoc)
         * @see java.io.InputStream#read(byte[], int, int)
         */
        @Override
        public int read(byte[] b, int off, int len) throws IOException
        {
            if (len == 0)
            {
                return 0;
            }
            if (this.isAtEnd)
            {
                return -1;
            }
            int bytesToRead = len;
            while (bytesToRead > 0)
            {
                if (this.chunkPosition >= this.chunkSize)
                {
                    if (this.isChunked)
                    {
                        // Skip the \r\n after this chunk
                        String eol = getNextHeader();
                        if (eol.length() > 0)
                        {
                            throw new IOException("Bad chunk format");
                        }
                        // Read the new chunk header
                        setNextChunkSize();

                        if (this.isAtEnd)
                        {
                            // Skip past the trailers. We have to do this in case the same connection is recycled for the
                            // next request
                            for (;;)
                            {
                                if (getNextHeader().length() == 0)
                                {
                                    break;
                                }
                            }
                            break;
                        }
                    }
                    else
                    {
                        this.isAtEnd = true;
                        break;
                    }
                }
                int bytesRead = Math.min(bytesToRead, this.chunkSize - this.chunkPosition);
                bytesRead = this.socketIn.read(b, off, bytesRead);
                if (bytesRead == -1)
                {
                    break;
                }
                bytesToRead -= bytesRead;
                off += bytesRead;
                this.chunkPosition += bytesRead;
            }
            return bytesToRead == len ? -1 : len - bytesToRead;
        }
    }
}
