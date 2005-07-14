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
package org.alfresco.repo.integrity.db.hibernate;

import java.util.List;

import org.alfresco.repo.domain.IntegrityEvent;
import org.alfresco.repo.domain.hibernate.IntegrityEventImpl;
import org.alfresco.repo.integrity.db.IntegrityDaoService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.util.Assert;

/**
 * Hibernate-specific implementation of the integrity persistence layer.
 * 
 * @author Derek Hulley
 */
public class HibernateIntegrityDaoServiceImpl extends HibernateDaoSupport implements IntegrityDaoService
{
    private static Log logger = LogFactory.getLog(HibernateIntegrityDaoServiceImpl.class);
    
    private static final String QUERY_GET_ALL_EVENTS = "integrity.GetAllEvents";
    private static final String QUERY_GET_TXN_EVENTS = "integrity.GetTxnEvents";
    private static final String QUERY_GET_TXN_EVENTS_EX_DEL = "integrity.GetTxnEventsExDeleted";
    
    /**
     * Just flush and clear the hibernate session.
     */
    public void flushAndClear()
    {
        getHibernateTemplate().flush();
        getHibernateTemplate().clear();
    }

    public IntegrityEvent newEvent(String txnId, String eventType, NodeRef primaryNodeRef)
    {
        Assert.notNull(txnId, "txnId");
        Assert.notNull(eventType, "eventType");
        Assert.notNull(primaryNodeRef, "primaryNodeRef");
        
        IntegrityEvent event = new IntegrityEventImpl();
        event.setTransactionId(txnId);
        event.setEventType(eventType);
        event.setPrimaryNodeRef(primaryNodeRef.toString());
        // save it
        getHibernateTemplate().save(event);
        // done
        return event;
    }

    public void deleteEvent(Long id)
    {
        IntegrityEvent event = (IntegrityEvent) getHibernateTemplate().get(IntegrityEventImpl.class, id);
        if (event != null)
        {
            getHibernateTemplate().delete(event);
        }
        // done
    }
    
    public List<IntegrityEvent> getEvents(final int firstResult, final int maxResults)
    {
        HibernateCallback callback = new HibernateCallback()
        {
            public Object doInHibernate(Session session)
            {
                Query query = session.getNamedQuery(HibernateIntegrityDaoServiceImpl.QUERY_GET_ALL_EVENTS);
                query.setFirstResult(firstResult);
                query.setMaxResults(maxResults);
                return query.list();
            }
        };
        List<IntegrityEvent> queryResults = (List) getHibernateTemplate().execute(callback);
        // done
        return queryResults;
    }

    public List<IntegrityEvent> getEvents(final String txnId, final int firstResult, final int maxResults)
    {
        HibernateCallback callback = new HibernateCallback()
        {
            public Object doInHibernate(Session session)
            {
                Query query = session.getNamedQuery(HibernateIntegrityDaoServiceImpl.QUERY_GET_TXN_EVENTS_EX_DEL);
                query.setString("transactionId", txnId);
                query.setFirstResult(firstResult);
                query.setMaxResults(maxResults);
                return query.list();
            }
        };
        List<IntegrityEvent> queryResults = (List) getHibernateTemplate().execute(callback);
        // done
        return queryResults;
    }

    /**
     * Helper method to step through all events for deletion purposes
     */
    private List<IntegrityEvent> getEventsForDeletion(
            Session session,
            String txnId,
            int firstResult,
            int maxResults)
    {
        Query query = session.getNamedQuery(HibernateIntegrityDaoServiceImpl.QUERY_GET_TXN_EVENTS);
        query.setString("transactionId", txnId);
        query.setFirstResult(firstResult);
        query.setMaxResults(maxResults);
        return query.list();
    }

    public void deleteEvents(final String txnId, final int flushSize)
    {
        HibernateCallback callback = new HibernateCallback()
        {
            public Object doInHibernate(Session session)
            {
                int currentRow = 0;
                while (true)
                {
                    List<IntegrityEvent> events = getEventsForDeletion(session, txnId, currentRow, flushSize);
                    for (IntegrityEvent event : events)
                    {
                        if (logger.isDebugEnabled())
                        {
                            logger.debug("Deleting event: " + event);
                        }
                        session.delete(event);
                    }
                    // flush and clear the caches after each batch
                    session.flush();
                    session.clear();

                    // break or get next batch of results
                    if (events.size() < flushSize)
                    {
                        // retrieved fewer events than the maximum
                        break;
                    }
                    else
                    {
                        // may be more rows to fetch
                        currentRow += flushSize;
                    }
                }
                return null;
            }
        };
        getHibernateTemplate().execute(callback);
        // done
    }
}
