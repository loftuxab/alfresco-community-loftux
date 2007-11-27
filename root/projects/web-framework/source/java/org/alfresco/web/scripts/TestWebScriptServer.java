/*
 * Copyright (C) 2005-2007 Alfresco Software Limited.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.

 * As a special exception to the terms and conditions of version 2.0 of 
 * the GPL, you may redistribute this Program in connection with Free/Libre 
 * and Open Source Software ("FLOSS") applications as described in Alfresco's 
 * FLOSS exception.  You should have recieved a copy of the text describing 
 * the FLOSS exception, and it is also available here: 
 * http://www.alfresco.com/legal/licensing"
 */
package org.alfresco.web.scripts;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import org.alfresco.web.config.ServerProperties;
import org.alfresco.web.scripts.servlet.ServletAuthenticatorFactory;
import org.alfresco.web.scripts.servlet.WebScriptServletRuntime;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;


/**
 * Stand-alone Web Script Test Server
 * 
 * @author davidc
 */
public class TestWebScriptServer
{
    // dependencies
    protected RuntimeContainer container;
    protected ServletAuthenticatorFactory authenticatorFactory;
    
    /** Server Configuration */
    protected ServerProperties serverProperties;
    
    /** The reader for interaction. */
    protected BufferedReader fIn;
    
    /** Last command issued */
    protected String lastCommand = null;

    /** Current user */
    protected String username = "admin";
    
    /** Current headers */
    protected Map<String, String> headers = new HashMap<String, String>();
    
    /** I18N Messages */
    protected MessageSource m_messages;    
    
    
    /**
     * Sets the Web Script Runtime Context
     * 
     * @param container
     */
    public void setContainer(RuntimeContainer container)
    {
        this.container = container;
    }

    /**
     * @param authenticatoFactory
     */
    public void setServletAuthenticatorFactory(ServletAuthenticatorFactory authenticatorFactory)
    {
        this.authenticatorFactory = authenticatorFactory;
    }

    /**
     * Sets the Messages resource bundle
     * 
     * @param messages
     * @throws IOException
     */
    public void setMessages(MessageSource messages)
        throws IOException
    {
        this.m_messages = messages;
    }

    
    /**
     * Main entry point.
     */
    public static void main(String[] args)
    {
        try
        {
            TestWebScriptServer testServer = getTestServer();
            testServer.rep();
        }
        catch(Throwable e)
        {
            StringWriter strWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(strWriter);
            e.printStackTrace(printWriter);
            System.out.println(strWriter.toString());
        }
        finally
        {
            System.exit(0);
        }
    }

    
    /**
     * Retrieve an instance of the TestWebScriptServer
     *  
     * @return  Test Server
     */
    public static TestWebScriptServer getTestServer()
    {
        String[] CONFIG_LOCATIONS = new String[]
        {
            "classpath:alfresco/web-framework-application-context.xml", 
            "classpath:alfresco/web-framework-application-context-test.xml"
        };
        ApplicationContext context = new ClassPathXmlApplicationContext(CONFIG_LOCATIONS);
        TestWebScriptServer testServer = (TestWebScriptServer)context.getBean("webscripts.test");
        return testServer;
    }
    
    /**
     * Submit a Web Script Request
     * 
     * @param method  http method
     * @param uri  web script uri (relative to /alfresco/service)
     * @return  response
     * @throws IOException
     */ 
    public MockHttpServletResponse submitRequest(String method, String uri)
        throws IOException
    {
        MockHttpServletRequest req = createRequest(method, uri);
        MockHttpServletResponse res = new MockHttpServletResponse();
        AbstractRuntime runtime = new WebScriptServletRuntime(container, null, req, res, serverProperties);
        runtime.executeScript();
        return res;
    }
    
    /**
     * Submit a Web Script Request
     * 
     * @param method  http method
     * @param uri  web script uri (relative to /alfresco/service)
     * @param headers  headers
     * @return  response
     * @throws IOException
     */
    public MockHttpServletResponse submitRequest(String method, String uri, Map<String, String> headers)
        throws IOException
    {
        MockHttpServletRequest req = createRequest(method, uri);
        for (Map.Entry<String, String> header: headers.entrySet())
        {
            req.addHeader(header.getKey(), header.getValue());
        }
        MockHttpServletResponse res = new MockHttpServletResponse();
        AbstractRuntime runtime = new WebScriptServletRuntime(container, authenticatorFactory, req, res, serverProperties);
        runtime.executeScript();
        return res;
    }    
    
    /**
     * A Read-Eval-Print loop.
     */
    public void rep()
    {
        // accept commands
        fIn = new BufferedReader(new InputStreamReader(System.in));
        while (true)
        {
            System.out.print("ok> ");
            try
            {
                // get command
                final String line = fIn.readLine();
                if (line == null || line.equals("exit") || line.equals("quit"))
                {
                    return;
                }
                                
                // execute command in context of currently selected user
                long startms = System.currentTimeMillis();
                System.out.print(interpretCommand(line));
                System.out.println("" + (System.currentTimeMillis() - startms) + "ms");
            }
            catch (Exception e)
            {
                e.printStackTrace(System.err);
                System.out.println("");
            }
        }
    }
    
    /**
     * Interpret a single command using the BufferedReader passed in for any data needed.
     * 
     * @param line The unparsed command
     * @return The textual output of the command.
     */
    protected String interpretCommand(final String line)
        throws IOException
    {
        return executeCommand(line);
    }
    
    /**
     * Execute a single command using the BufferedReader passed in for any data needed.
     * 
     * TODO: Use decent parser!
     * 
     * @param line The unparsed command
     * @return The textual output of the command.
     */
    protected String executeCommand(String line)
        throws IOException
    {
        String[] command = line.split(" ");
        if (command.length == 0)
        {
            command = new String[1];
            command[0] = line;
        }
        
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(bout);

        // repeat last command?
        if (command[0].equals("r"))
        {
            if (lastCommand == null)
            {
                return "No command entered yet.";
            }
            return "repeating command " + lastCommand + "\n\n" + interpretCommand(lastCommand);
        }
        
        // remember last command
        lastCommand = line;

        // execute command
        if (command[0].equals("help"))
        {
            String helpFile = m_messages.getMessage("testserver.help", null, null);
            ClassPathResource helpResource = new ClassPathResource(helpFile);
            byte[] helpBytes = new byte[500];
            InputStream helpStream = helpResource.getInputStream();
            try
            {
                int read = helpStream.read(helpBytes);
                while (read != -1)
                {
                    bout.write(helpBytes, 0, read);
                    read = helpStream.read(helpBytes);
                }
            }
            finally
            {
                helpStream.close();
            }
        }
        
        else if (command[0].equals("user"))
        {
            if (command.length == 2)
            {
                username = command[1];
            }
            out.println("using user " + username);
        }
        
        else if (command[0].equals("get") ||
                 command[0].equals("put") ||
                 command[0].equals("post") ||
                 command[0].equals("delete"))
        {
            String uri = (command.length > 1) ? command[1] : null;
            MockHttpServletResponse res = submitRequest(command[0], uri, headers);
            bout.write(("Response status: " + res.getStatus()).getBytes());
            out.println();
            bout.write(res.getContentAsByteArray());
            out.println();
        }
        
        else if (command[0].equals("tunnel"))
        {
            if (command.length < 4)
            {
                return "Syntax Error.\n";
            }
            
            if (command[1].equals("param"))
            {
                String uri = command[3];
                if (uri.indexOf('?') == -1)
                {
                    uri += "?alf:method=" + command[2];
                }
                else
                {
                    uri += "&alf:method=" + command[2];
                }
                MockHttpServletResponse res = submitRequest("post", uri, headers);
                bout.write(res.getContentAsByteArray());
                out.println();
            }
            
            else if (command[1].equals("header"))
            {
                Map<String, String> tunnelheaders = new HashMap<String, String>();
                tunnelheaders.putAll(headers);
                tunnelheaders.put("X-HTTP-Method-Override", command[2]);
                MockHttpServletResponse res = submitRequest("post", command[3], tunnelheaders);
                bout.write(res.getContentAsByteArray());
                out.println();
            }
                
            else
            {
                return "Syntax Error.\n";
            }
        }

        else if (command[0].equals("header"))
        {
            if (command.length == 1)
            {
                for (Map.Entry<String, String> entry : headers.entrySet())
                {
                    out.println(entry.getKey() + " = " + entry.getValue());
                }
            }
            else if (command.length == 2)
            {
                String[] param = command[1].split("=");
                if (param.length == 0)
                {
                    return "Syntax Error.\n";
                }
                if (param.length == 1)
                {
                    headers.remove(param[0]);
                    out.println("deleted header " + param[0]);
                }
                else if (param.length == 2)
                {
                    headers.put(param[0], param[1]);
                    out.println("set header " + param[0] + " = " + headers.get(param[0]));
                }
                else
                {
                    return "Syntax Error.\n";
                }
            }
            else
            {
                return "Syntax Error.\n";
            }
        }            

        else if (command[0].equals("reset"))
        {
            container.reset();
            out.println("Runtime context '" + container.getName() + "' reset.");
        }
        
        else
        {
            return "Syntax Error.\n";
        }
 
        out.flush();
        String retVal = new String(bout.toByteArray());
        out.close();
        return retVal;
    }

    /**
     * Create a Mock HTTP Servlet Request
     * 
     * @param method
     * @param uri
     * @return  mock http servlet request
     * @throws UnsupportedEncodingException 
     */
    private MockHttpServletRequest createRequest(String method, String uri)
        throws UnsupportedEncodingException
    {
        MockHttpServletRequest req = new MockHttpServletRequest(method, uri);

        req.setContextPath("/alfresco");
        req.setServletPath("/service");

        if (uri != null)
        {
            int iArgIndex = uri.indexOf('?');
            if (iArgIndex != -1 && iArgIndex != uri.length() -1)
            {
                String uriArgs = uri.substring(iArgIndex +1);
                String[] args = uriArgs.split("&");
                for (String arg : args)
                {
                    String[] parts = arg.split("=");
                    req.addParameter(parts[0], (parts.length == 2) ? URLDecoder.decode(parts[1], "UTF-8") : null);
                }
                req.setQueryString(URLDecoder.decode(uriArgs, "UTF-8"));
            }
            String requestURI = "/alfresco/service" + (iArgIndex == -1 ? uri : uri.substring(0, iArgIndex));
            req.setRequestURI(URLDecoder.decode(requestURI, "UTF-8"));
        }
        
        return req;
    }
}
