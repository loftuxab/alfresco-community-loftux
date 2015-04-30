/*
 * Copyright (C) 2014-2014 Alfresco Software Limited.
 *
 * This file is part of Alfresco
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.enterprise.spring.encryption;

import java.net.URL;
import java.net.URLClassLoader;

import org.alfresco.enterprise.heartbeat.HeartBeatTest;
import org.alfresco.util.TempFileProvider;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import junit.framework.TestCase;

public class PublicPrivateKeySpringEncryptorTest extends TestCase
{
    private static final String[] CONFIG_LOCATIONS = new String[]
    {
        "classpath:alfresco/application-context.xml"
    };
    
    private static final ApplicationContext CONTEXT = null;
    
    @Override
    protected void setUp() throws Exception
    {
    	
    }
    
    @Override
    protected void tearDown() throws Exception
    {
    	
    }
    
    public void testOne() throws Exception
    {
    	String sharedDir = "c:/temp";
    	PublicPrivateKeyStringEncryptor encryptor = new PublicPrivateKeyStringEncryptor();

    	encryptor.createKeyFiles(sharedDir);
    	
    	encryptor.init();
        encryptor.initPublic(sharedDir);
        encryptor.initPrivate(sharedDir);
    	
//        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(PublicPrivateKeySpringEncryptorTest.CONFIG_LOCATIONS,
//                false);
        
//        ClassLoader classLoader = new URLClassLoader(new URL[]
//        {
//             sharedDir.toURL()
//        }, Thread.currentThread().getContextClassLoader())
         
//       context.setClassLoader(classLoader);
//       context.refresh();
//        CONTEXT = context;

    	
    	String message = "qwerty123";
    	String encrypted = encryptor.encrypt(message);
    	String decrypted = encryptor.decrypt(encrypted);

    	
    	assertEquals(message, decrypted);
    }
    
    public void testVeryLongStrings() throws Exception
    {
        String sharedDir = "c:/temp";
        PublicPrivateKeyStringEncryptor encryptor = new PublicPrivateKeyStringEncryptor();

        encryptor.createKeyFiles(sharedDir);
        
        encryptor.init();
        encryptor.initPublic(sharedDir);
        encryptor.initPrivate(sharedDir);
                       
        String message = "a horribly long string, considerably longer than can be encrypted by RSA in one chunk";
        
        assertTrue("test not valid, test message too small", message.length() > 60);
        String encrypted = encryptor.encrypt(message);
        String decrypted = encryptor.decrypt(encrypted);
        
        assertEquals(message, decrypted);
    }
    
    /**
     * Messages are chunked to 53 byte boundaries
     * @throws Exception
     */
    public void testMultiplesOf53() throws Exception
    {
        String sharedDir = "c:/temp";
        PublicPrivateKeyStringEncryptor encryptor = new PublicPrivateKeyStringEncryptor();

        encryptor.createKeyFiles(sharedDir);
        
        encryptor.init();
        encryptor.initPublic(sharedDir);
        encryptor.initPrivate(sharedDir);
                       
        String message = "12345678901234567890123456789012345678901234567890123";
        
        assertEquals("test not valid, test message not 53 bytes", message.length(), 53);
        String encrypted = encryptor.encrypt(message);
        String decrypted = encryptor.decrypt(encrypted);
        
        assertEquals(message, decrypted);
        
        String message2 = message + message ;
        
        assertEquals("test not valid, test message not 106 bytes", message2.length(), 106);
        encrypted = encryptor.encrypt(message2);
        decrypted = encryptor.decrypt(encrypted);
        
        assertEquals(message2, decrypted);
    }


}
