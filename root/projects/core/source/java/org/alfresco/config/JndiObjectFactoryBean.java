/*
 * Copyright (C) 2005-2009 Alfresco Software Limited.
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
 * FLOSS exception.  You should have received a copy of the text describing 
 * the FLOSS exception, and it is also available here: 
 * http://www.alfresco.com/legal/licensing"
 */
package org.alfresco.config;

import java.sql.Connection;

import javax.naming.NamingException;
import javax.sql.DataSource;

/**
 * An extended version of JndiObjectFactoryBean that actually tests a JNDI data source before falling back to its
 * default object. Allows continued backward compatibility with old-style datasource configuration.
 * 
 * @author dward
 */
public class JndiObjectFactoryBean extends org.springframework.jndi.JndiObjectFactoryBean
{

    @Override
    protected Object lookup() throws NamingException
    {
        Object candidate = super.lookup();
        if (candidate instanceof DataSource)
        {
            Connection con = null;
            try
            {
                con = ((DataSource) candidate).getConnection();
            }
            catch (Exception e)
            {
                NamingException e1 = new NamingException("Unable to get connection from " + getJndiName());
                e1.setRootCause(e);
                throw e1;
            }
            finally
            {
                try
                {
                    if (con != null)
                    {
                        con.close();
                    }
                }
                catch (Exception e)
                {
                }
            }
        }
        return candidate;
    }
}
