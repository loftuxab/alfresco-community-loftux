/*
 * Copyright (C) 2005 Alfresco, Inc.
 *
 * Licensed under the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version
 * 2.1 of the License, or (at your option) any later version.
 * You may obtain a copy of the License at
 *
 *     http://www.gnu.org/licenses/lgpl.txt
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 */
package org.alfresco.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

/**
 * Base test class providing Hibernate sessions
 * 
 * @author Derek Hulley
 */
public abstract class BaseHibernateTest extends BaseSpringTest
{
    private static final Log logger = LogFactory.getLog(BaseHibernateTest.class);
    
    private SessionFactory sessionFactory;

    private Session session;

    public BaseHibernateTest()
    {
    }

    public void setSessionFactory(SessionFactory sessionFactory)
    {
        this.sessionFactory = sessionFactory;
        // construct a session at the same time
        this.session = sessionFactory.openSession();
    }

    /**
     * @return Returns a <code>Session</code> that is <b>separate</b> from that provided
     *      automatically to the test and related beans
     */
    protected Session getSession()
    {
        return session;
    }

    public void afterPropertiesSet() throws Exception
    {
        throw new UnsupportedOperationException();
    }
}
