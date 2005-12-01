/*
 * Copyright (C) 2005 Alfresco, Inc.
 *
 * Licensed under the Mozilla Public License version 1.1 
 * with a permitted attribution clause. You may obtain a
 * copy of the License at
 *
 *   http://www.alfresco.org/legal/license.txt
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 */
package org.alfresco.jcr.util;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import javax.jcr.AccessDeniedException;
import javax.jcr.RepositoryException;
import javax.jcr.lock.LockException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.nodetype.NoSuchNodeTypeException;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.jcr.session.SessionImpl;
import org.alfresco.repo.node.integrity.IntegrityException;
import org.alfresco.repo.transaction.AlfrescoTransactionSupport;
import org.alfresco.service.cmr.dictionary.InvalidTypeException;
import org.alfresco.service.cmr.lock.NodeLockedException;


/**
 * Factory for creating a JCR Session Context Proxy
 * 
 * The Session Context Proxy is responsible for ensuring that the appropriate Alfresco
 * Repository context is setup when a method is executed within a JCR session.  For
 * example, the appropriate authenticated user is validated.
 * 
 * @author David Caruana
 *
 */
public class JCRProxyFactory
{
    
    /**
     * Create a Session Context Proxy
     * 
     * @param target  target object to wrap
     * @param proxyInterface  the proxy interface to export
     * @param context  the session context
     * @return  the proxied target
     */
    public static Object create(Object target, Class proxyInterface, SessionImpl context)
    {
        InvocationHandler handler = new SessionContextInvocationHandler(target, context);
        return Proxy.newProxyInstance(proxyInterface.getClassLoader(), new Class[]{proxyInterface}, handler);
    }


    /**
     * Session Context Invocation Handler
     * 
     * @author David Caruana
     */
    private static class SessionContextInvocationHandler implements InvocationHandler
    {
        private Object target;
        private SessionImpl context;

        /**
         * Constuct.
         * 
         * @param instance  the object instance holding the method
         * @param delegateMethod  the method to invoke
         */
        private SessionContextInvocationHandler(Object target, SessionImpl context)
        {
            this.target = target;
            this.context = context;
        }
        
        /* (non-Javadoc)
         * @see java.lang.reflect.InvocationHandler#invoke(java.lang.Object, java.lang.reflect.Method, java.lang.Object[])
         */
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
        {
            // Handle Object level methods
            if (method.getName().equals("toString"))
            {
                return toString();
            }
            else if (method.getName().equals("hashCode"))
            {
                return hashCode();
            }
            else if (method.getName().equals("equals"))
            {
                if (Proxy.isProxyClass(args[0].getClass()))
                {
                    return equals(Proxy.getInvocationHandler(args[0]));
                }
                return false;
            }

            // Ensure invocation is under correct context
            try
            {
                // test for existence of transaction
                if (!(method.getName().equals("login") || method.getName().equals("logout")))
                {        
                    String trxId = AlfrescoTransactionSupport.getTransactionId();
                    if (trxId == null)
                    {
                        throw new RepositoryException("Session must be used within the context of a transaction.");
                        
                        // TODO: Check that session is tied to single transaction
                        //if (!trxId.equals(context.getTransactionId()))
                        //{
                        //    throw new RepositoryException("Cannot use session in transaction " + trxId + " as it is tied to transaction " + context.getTransactionId());
                        //}
                    }
                }
                
                // test authentication
                String ticket = context.getTicket();
                if (ticket != null)
                {
                    context.getRepositoryImpl().getServiceRegistry().getAuthenticationService().validate(context.getTicket());
                }

                // invoke underlying service
                return method.invoke(target, args);
            }
            catch (InvocationTargetException e)
            {
                Throwable cause = e.getCause();
                
                // Map Alfresco exceptions to JCR exceptions
                if (cause instanceof IntegrityException)
                {
                    throw new ConstraintViolationException(cause);
                }
                else if (cause instanceof NodeLockedException)
                {
                    throw new LockException(cause);
                }
                else if (cause instanceof InvalidTypeException)
                {
                    throw new NoSuchNodeTypeException(cause);
                }
                else if (cause instanceof org.alfresco.repo.security.permissions.AccessDeniedException)
                {
                    throw new AccessDeniedException(cause);
                }
                else if (cause instanceof AlfrescoRuntimeException)
                {
                    throw new RepositoryException(cause);
                }
                throw cause;
            }
            finally
            {
                // clear authentication context
                context.getRepositoryImpl().getServiceRegistry().getAuthenticationService().clearCurrentSecurityContext();
            }
        }
    
        @Override
        public boolean equals(Object obj)
        {
            if (obj == this)
            {
                return true;
            }
            else if (obj == null || !(obj instanceof SessionContextInvocationHandler))
            {
                return false;
            }
            SessionContextInvocationHandler other = (SessionContextInvocationHandler)obj;
            return target.equals(other.target);
        }
    
        @Override
        public int hashCode()
        {
            return target.hashCode();
        }
    
        @Override
        public String toString()
        {
            return target.toString();
        }
    }

}
