/*
 * Copyright (C) 2005-2008 Alfresco Software Limited.
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
package org.alfresco.web.site.parser.tags;

import java.io.IOException;
import java.lang.reflect.Method;

/**
 * @author muzquiano
 */
public class Token implements java.io.Serializable
{
    public static final int PRINT = 1;
    public static final int INIT_TAG = 2;
    public static final int TAG_ATTRIBUTE = 3;
    public static final int START_TAG = 4;
    public static final int START_BODY = 5;
    public static final int END_BODY = 6;
    public static final int END_TAG = 7;

    public Token(int command)
    {
        this.command = command;
    }

    public Token(int command, String tagname)
    {
        this.command = command;
        this.tagname = tagname;
    }

    public Token(int command, Object data)
    {
        this.command = command;
        this.data = data;
    }

    public Token(int command, String tagname, Object data)
    {
        this.command = command;
        this.data = data;
        this.tagname = tagname;
    }

    public Token(int command, Method method, Object data)
    {
        this.command = command;
        this.method = method;
        this.data = data;
    }

    public int getCommand()
    {
        return command;
    }

    public String getTagName()
    {
        return tagname;
    }

    public Method getMethod()
    {
        return method;
    }

    public Object getData()
    {
        return data;
    }

    /**
     * A custom writeObject method for serialization since java.reflect.Method
     * is not Serializable.
     */
    private void writeObject(java.io.ObjectOutputStream out) throws IOException
    {
        out.writeInt(command);
        // handle the case where method is null
        if (method == null)
        {
            out.writeBoolean(false);
        }
        else
        {
            out.writeBoolean(true);
            // write out the information necessary to get the method again
            out.writeObject(method.getDeclaringClass());
            out.writeObject(method.getName());
            out.writeObject(method.getParameterTypes());
        }
        out.writeObject(tagname);
        out.writeObject(data);
    }

    /**
     * A custom readObject method for serialization to match writeObject
     */
    private void readObject(java.io.ObjectInputStream in) throws IOException,
            ClassNotFoundException
    {
        command = in.readInt();
        boolean hasMethod = in.readBoolean();
        // check whether method was null
        if (hasMethod)
        {
            Class declaringClass = (Class) in.readObject();
            String name = (String) in.readObject();
            Class[] params = (Class[]) in.readObject();
            try
            {
                method = declaringClass.getMethod(name, params);
            }
            catch (NoSuchMethodException nsme)
            {
                throw new IOException(nsme.getMessage());
            }
        }
        tagname = (String) in.readObject();
        data = in.readObject();
    }

    private int command;
    private Method method;
    private String tagname;
    private Object data;
}
