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
package org.alfresco.repo.integrity.db;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.alfresco.repo.domain.IntegrityEvent;
import org.alfresco.repo.integrity.IntegrityException;
import org.alfresco.repo.integrity.IntegrityRecord;
import org.alfresco.repo.integrity.IntegrityService;
import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.repo.transaction.AlfrescoTransactionManager;
import org.alfresco.service.cmr.dictionary.AspectDefinition;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.dictionary.PropertyDefinition;
import org.alfresco.service.cmr.dictionary.TypeDefinition;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.EqualsHelper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.Assert;

/**
 * Implementation of the {@link org.alfresco.repo.integrity.IntegrityService integrity service}
 * that uses the domain persistence mechanism to store and recall integrity events.
 * <p>
 * In order to fulfill the contract of the interface, this class registers to receive notifications
 * pertinent to changes in the node structure.  These are then store away in the persistent
 * store until the request to
 * {@link org.alfresco.repo.integrity.IntegrityService#checkIntegrity(String) check integrity} is
 * made.
 * <p>
 * In order to ensure registration of these events, the {@link #init()} method must be called.
 * <p>
 * By default, this service is enabled, but can be disabled using {@link #setEnabled(boolean)}.<br>
 * Tracing of the event stacks is, for performance reasons, disabled by default but can be enabled
 * using {@link #setTraceOn(boolean)}.<br>
 * When enabled, the integrity check can either fail with a <tt>RuntimeException</tt> or not.  In either
 * case, the integrity violations are logged as warnings or errors.  This behaviour is controleed using
 * {@link #setFailOnViolation(boolean)} and is off by default.  In other words, if not set, this service
 * will only log warnings about integrity violations.
 * <p>
 * Some integrity checks are not performed here as they are dealt with directly during the modification
 * operation in the {@link org.alfresco.service.cmr.repository.NodeService node service}.
 * 
 * @see #setPolicyComponent(PolicyComponent)
 * @see #setDictionaryService(DictionaryService)
 * @see #setIntegrityDaoService(IntegrityDaoService)
 * @see #setMaxErrorsPerTransaction(int)
 * @see #setFlushSize(int)
 * 
 * @author Derek Hulley
 */
public class DbIntegrityServiceImpl
        implements  IntegrityService,
                    NodeServicePolicies.OnCreateNodePolicy,
                    NodeServicePolicies.OnUpdatePropertiesPolicy,
                    NodeServicePolicies.OnDeleteNodePolicy,
                    NodeServicePolicies.OnAddAspectPolicy,
                    NodeServicePolicies.OnRemoveAspectPolicy,
                    NodeServicePolicies.OnCreateChildAssociationPolicy,
                    NodeServicePolicies.OnDeleteChildAssociationPolicy,
                    NodeServicePolicies.OnCreateAssociationPolicy,
                    NodeServicePolicies.OnDeleteAssociationPolicy
{
    private static Log logger = LogFactory.getLog(DbIntegrityServiceImpl.class);
    
    // build sets of event names particular to a type of integrity check
    public static final List<String> CHECK_ALL_PROPERTIES = new ArrayList<String>(4);
    static
    {
        // check that all required properties are present
        CHECK_ALL_PROPERTIES.add(IntegrityEvent.EVENT_TYPE_PROPERTIES_CHANGED);
        CHECK_ALL_PROPERTIES.add(IntegrityEvent.EVENT_TYPE_NODE_CREATED);
        CHECK_ALL_PROPERTIES.add(IntegrityEvent.EVENT_TYPE_ASPECT_ADDED);
        // TODO: Further checks for associations required
    }

    private PolicyComponent policyComponent;
    private DictionaryService dictionaryService;
    private NodeService nodeService;
    private IntegrityDaoService integrityDaoService;
    private boolean enabled;
    private boolean traceOn;
    private boolean failOnViolation;
    private int maxErrorsPerTransaction;
    /**
     * parameter controlling flush size during processing of events
     */
    private int flushSize;
    
    /**
     */
    public DbIntegrityServiceImpl()
    {
        this.enabled = true;
        this.traceOn = false;
        this.failOnViolation = false;
        maxErrorsPerTransaction = 10;
        flushSize = 5000;
    }

    /**
     * @param policyComponent the component with which to register behaviour
     */
    public void setPolicyComponent(PolicyComponent policyComponent)
    {
        this.policyComponent = policyComponent;
    }
    
    /**
     * @param dictionaryService used to get the model data
     */
    public void setDictionaryService(DictionaryService dictionaryService)
    {
        this.dictionaryService = dictionaryService;
    }

    /**
     * @param nodeService allows access to the nodes and associations
     */
    public void setNodeService(NodeService nodeService)
    {
        this.nodeService = nodeService;
    }

    /**
     * @param integrityDaoService provides access to the persistent store
     */
    public void setIntegrityDaoService(IntegrityDaoService integrityDaoService)
    {
        this.integrityDaoService = integrityDaoService;
    }

    /**
     * @param enabled set to false to disable integrity checking completely
     */
    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
    }

    /**
     * @param traceOn set to <code>true</code> to enable stack traces recording
     *      of events
     */
    public void setTraceOn(boolean traceOn)
    {
        this.traceOn = traceOn;
    }

    /**
     * @param failOnViolation set to <code>true</code> to force failure by
     *      <tt>RuntimeException</tt> when a violation occurs.
     */
    public void setFailOnViolation(boolean failOnViolation)
    {
        this.failOnViolation = failOnViolation;
    }

    /**
     * @param maxLogNumberPerTransaction upper limit on how many violations are
     *      logged when multiple violations have been found.
     */
    public void setMaxErrorsPerTransaction(int maxLogNumberPerTransaction)
    {
        this.maxErrorsPerTransaction = maxLogNumberPerTransaction;
    }

    /**
     * @param flushSize the number of nodes to process before flushing and clearing
     *      the session cache.  Increasing this number implies the potential use
     *      of more memory during long-running transactions.
     */
    public void setFlushSize(int flushSize)
    {
        this.flushSize = flushSize;
    }

    /**
     * Registers the system-level policy behaviours
     */
    public void init()
    {
        if (enabled)  // only register behaviour if integrity checking is on
        {
            policyComponent.bindClassBehaviour(
                    QName.createQName(NamespaceService.ALFRESCO_URI, "onCreateNode"),
                    this,
                    new JavaBehaviour(this, "onCreateNode"));   
            policyComponent.bindClassBehaviour(
                    QName.createQName(NamespaceService.ALFRESCO_URI, "onUpdateProperties"),
                    this,
                    new JavaBehaviour(this, "onUpdateProperties"));   
            policyComponent.bindClassBehaviour(
                    QName.createQName(NamespaceService.ALFRESCO_URI, "onDeleteNode"),
                    this,
                    new JavaBehaviour(this, "onDeleteNode"));   
            policyComponent.bindClassBehaviour(
                    QName.createQName(NamespaceService.ALFRESCO_URI, "onAddAspect"),
                    this,
                    new JavaBehaviour(this, "onAddAspect"));   
            policyComponent.bindClassBehaviour(
                    QName.createQName(NamespaceService.ALFRESCO_URI, "onRemoveAspect"),
                    this,
                    new JavaBehaviour(this, "onRemoveAspect"));   
            policyComponent.bindAssociationBehaviour(
                    QName.createQName(NamespaceService.ALFRESCO_URI, "onCreateChildAssociation"),
                    this,
                    new JavaBehaviour(this, "onCreateChildAssociation"));   
            policyComponent.bindAssociationBehaviour(
                    QName.createQName(NamespaceService.ALFRESCO_URI, "onDeleteChildAssociation"),
                    this,
                    new JavaBehaviour(this, "onDeleteChildAssociation"));   
            policyComponent.bindAssociationBehaviour(
                    QName.createQName(NamespaceService.ALFRESCO_URI, "onCreateAssociation"),
                    this,
                    new JavaBehaviour(this, "onCreateAssociation"));   
            policyComponent.bindAssociationBehaviour(
                    QName.createQName(NamespaceService.ALFRESCO_URI, "onDeleteAssociation"),
                    this,
                    new JavaBehaviour(this, "onDeleteAssociation"));   
        }
    }
    
    /**
     * Helper method to set the stack trace for the event
     * 
     * @param event the event on which to set, or not set, the stack trace
     */
    private void setTrace(IntegrityEvent event)
    {
        if (traceOn)
        {
            // get a stack trace
            Throwable t = new Throwable();
            t.fillInStackTrace();
            StackTraceElement[] trace = t.getStackTrace();
            
            event.setTrace(trace);
            // done
        }
    }

    /**
     * @see IntegrityEvent#EVENT_TYPE_NODE_CREATED
     */
    public void onCreateNode(ChildAssociationRef childAssocRef)
    {
        String txnId = AlfrescoTransactionManager.getTransactionId();
        IntegrityEvent event = integrityDaoService.newEvent(
                txnId,
                IntegrityEvent.EVENT_TYPE_NODE_CREATED,
                childAssocRef.getChildRef());
        event.setSecondaryNodeRef(childAssocRef.getParentRef().toString());
        event.setAssocTypeQName(childAssocRef.getTypeQName().toString());
        event.setAssocQName(childAssocRef.getQName().toString());
        
        setTrace(event);
    }

    /**
     * @see IntegrityEvent#EVENT_TYPE_PROPERTIES_CHANGED
     */
    public void onUpdateProperties(
            NodeRef nodeRef,
            Map<QName, Serializable> before,
            Map<QName, Serializable> after)
    {
        String txnId = AlfrescoTransactionManager.getTransactionId();
        IntegrityEvent event = integrityDaoService.newEvent(
                txnId,
                IntegrityEvent.EVENT_TYPE_PROPERTIES_CHANGED,
                nodeRef);
        
        setTrace(event);
    }

    public void onDeleteNode(ChildAssociationRef childAssocRef)
    {
        String txnId = AlfrescoTransactionManager.getTransactionId();
        IntegrityEvent event = integrityDaoService.newEvent(
                txnId,
                IntegrityEvent.EVENT_TYPE_NODE_DELETED,
                childAssocRef.getChildRef());
        event.setAssocTypeQName(childAssocRef.getTypeQName().toString());
        event.setAssocQName(childAssocRef.getQName().toString());
        
        setTrace(event);
    }

    /**
     * @see IntegrityEvent#EVENT_TYPE_ASPECT_ADDED
     */
    public void onAddAspect(NodeRef nodeRef, QName aspectTypeQName)
    {
        String txnId = AlfrescoTransactionManager.getTransactionId();
        IntegrityEvent event = integrityDaoService.newEvent(
                txnId,
                IntegrityEvent.EVENT_TYPE_ASPECT_ADDED,
                nodeRef);
        event.setAspectTypeQName(aspectTypeQName.toString());
        
        setTrace(event);
    }

    /**
     * @see IntegrityEvent#EVENT_TYPE_ASPECT_REMOVED
     */
    public void onRemoveAspect(NodeRef nodeRef, QName aspectTypeQName)
    {
        String txnId = AlfrescoTransactionManager.getTransactionId();
        IntegrityEvent event = integrityDaoService.newEvent(
                txnId,
                IntegrityEvent.EVENT_TYPE_ASPECT_REMOVED,
                nodeRef);
        event.setAspectTypeQName(aspectTypeQName.toString());
        
        setTrace(event);
    }

    public void onCreateChildAssociation(ChildAssociationRef childAssocRef)
    {
//        throw new UnsupportedOperationException();
    }

    public void onDeleteChildAssociation(ChildAssociationRef childAssocRef)
    {
//        throw new UnsupportedOperationException();
    }

    public void onCreateAssociation(AssociationRef nodeAssocRef)
    {
//        throw new UnsupportedOperationException();
    }

    public void onDeleteAssociation(AssociationRef nodeAssocRef)
    {
//        throw new UnsupportedOperationException();
    }

    /**
     * Runs several types of checks, querying specifically for events that
     * will necessitate each type of test.
     */
    public void checkIntegrity(String txnId) throws IntegrityException
    {
        if (!enabled)
        {
            return;
        }
        
        Assert.notNull(txnId, "The Transaction ID is mandatory");
        
        // check properties
        List<IntegrityRecord> failures = processAllEvents(txnId);
        
        // drop out quickly if there are no failures
        if (failures.isEmpty())
        {
            return;
        }
        
        // handle errors according to instance flags
        // firstly, log all failures
        int failureCount = failures.size();
        StringBuilder sb = new StringBuilder(300 * failureCount);
        sb.append("Found ").append(failureCount).append(" integrity violations");
        if (maxErrorsPerTransaction < failureCount)
        {
            sb.append(" - first ").append(maxErrorsPerTransaction);
        }
        sb.append(":");
        int count = 0;
        for (IntegrityRecord failure : failures)
        {
            // break if we exceed the maximum number of log entries
            count++;
            if (count > maxErrorsPerTransaction)
            {
                break;
            }
            sb.append("\n").append(failure);
        }
        if (failOnViolation)
        {
            logger.error(sb.toString());
            throw new IntegrityException(failures);
        }
        else
        {
            logger.warn(sb.toString());
            // no exception
        }
    }
    
    /**
     * Pages through resultsets of events that occured within the transaction,
     * processing each node.  Flushing and clearing happens between each page
     * cycle.
     * 
     * @param txnId
     * @return Returns a list of integrity violations, up to the
     *      {@link #maxErrorsPerTransaction the maximum defined
     */
    private List<IntegrityRecord> processAllEvents(String txnId)
    {
        // the results
        ArrayList<IntegrityRecord> results = new ArrayList<IntegrityRecord>(0); // generally unused
        
        // keep a count of the number of events processed
        int currentRow = 0;

        // page through query results
        while(true)  // will break once all results have been read
        {
            List<IntegrityEvent> events = integrityDaoService.getEvents(txnId, currentRow, flushSize);

            // the current node reference
            NodeRef currentNodeRef = null;
            String currentNodeRefStr = null;
            // the current event type
            String currentEventType = null;
            // results requiring trace additions
            List<IntegrityRecord> traceResults = new ArrayList<IntegrityRecord>(0);
            // failure results for the event
            List<IntegrityRecord> eventResults = new ArrayList<IntegrityRecord>(0);

            // keep tabs on what we have done with each node
            boolean checkAllProperties = false;
            
            // cycle through the events, performing various integrity checks
            for (IntegrityEvent event : events)
            {
                // have we moved onto a new node?
                boolean newNode = !EqualsHelper.nullSafeEquals(currentNodeRefStr, event.getPrimaryNodeRef());
                boolean newEventType = !EqualsHelper.nullSafeEquals(currentEventType, event.getEventType());
                
                if (newNode)
                {
                    // primary node reference is mandatory on the event
                    currentNodeRefStr = event.getPrimaryNodeRef();
                    currentNodeRef = new NodeRef(currentNodeRefStr);
                    // reset flags
                    checkAllProperties = true;
                }
                if (newEventType)
                {
                    currentEventType = event.getEventType();
                }
                // perform the various types of integrity checks
                try
                {
                    // check node properties
                    if (checkAllProperties && CHECK_ALL_PROPERTIES.contains(currentEventType))
                    {
                        checkAllProperties = false;
                        checkAllProperties(currentNodeRef, eventResults);
                    }
                }
                catch (Throwable e)
                {
                    // log it as an error and continue
                    IntegrityRecord record = new IntegrityRecord(e.getMessage());
                    record.addTrace(e.getStackTrace());
                    eventResults.add(record);
                }

                // keep track of results needing trace added
                if (traceOn)
                {
                    if (newNode && newEventType)
                    {
                        // we have changed node and event type - clear the trace results
                        traceResults.clear();
                    }
                    // add event results to the trace results
                    traceResults.addAll(eventResults);
                    // record the current event trace if present
                    if (event.getTrace() != null)
                    {
                        for (IntegrityRecord record : traceResults)
                        {
                            record.addTrace(event.getTrace());
                        }
                    }
                }
                
                // copy all the event results to the final results
                results.addAll(eventResults);
                // clear the event results
                eventResults.clear();
            }
            
            if (results.size() >= maxErrorsPerTransaction)
            {
                // only so many errors wanted at a time
                break;
            }
            else if (events.size() < flushSize)
            {
                // retrieved fewer events than the maximum
                break;
            }
            else
            {
                // may be more rows to fetch
                currentRow += flushSize;
                // flush and clear the caches in preparation
                integrityDaoService.flushAndClear();
            }
        }
        // done
        return results;
    }
    
    /**
     * Checks the properties for the type and aspects of the given node.
     * 
     * @param nodeRef
     * @param eventResults
     */
    private void checkAllProperties(NodeRef nodeRef, List<IntegrityRecord> eventResults)
    {
        // get all properties for the node
        Map<QName, Serializable> nodeProperties = nodeService.getProperties(nodeRef);
        
        // get the node type
        QName nodeTypeQName = nodeService.getType(nodeRef);
        // get property definitions for the node type
        TypeDefinition typeDef = dictionaryService.getType(nodeTypeQName);
        Collection<PropertyDefinition> propertyDefs = typeDef.getProperties().values();
        // check them
        checkAllProperties(nodeRef, nodeTypeQName, propertyDefs, nodeProperties, eventResults);
        
        // get the node aspects
        Set<QName> aspectTypeQNames = nodeService.getAspects(nodeRef);
        for (QName aspectTypeQName : aspectTypeQNames)
        {
            // get property definitions for the aspect
            AspectDefinition aspectDef = dictionaryService.getAspect(aspectTypeQName);
            propertyDefs = aspectDef.getProperties().values();
            // check them
            checkAllProperties(nodeRef, aspectTypeQName, propertyDefs, nodeProperties, eventResults);
        }
        // done
    }

    /**
     * Checks the specific map of properties against the required property definitions
     * 
     * @param nodeRef the node to which this applies
     * @param typeQName the qualified name of the aspect or type to which the properties belong
     * @param propertyDefs the definitions to check against - may be null or empty
     * @param nodeProperties the properties to check
     */
    private void checkAllProperties(
            NodeRef nodeRef,
            QName typeQName,
            Collection<PropertyDefinition> propertyDefs,
            Map<QName, Serializable> nodeProperties,
            Collection<IntegrityRecord> eventResults)
    {
        // check for null or empty definitions
        if (propertyDefs == null || propertyDefs.isEmpty())
        {
            return;
        }
        for (PropertyDefinition propertyDef : propertyDefs)
        {
            QName propertyQName = propertyDef.getName();
            Serializable propertyValue = nodeProperties.get(propertyQName);
            // check that mandatory properties are set
            if (propertyDef.isMandatory() && propertyValue == null)
            {
                IntegrityRecord result = new IntegrityRecord(
                        "Mandatory property not set: \n" +
                        "   Node: " + nodeRef + "\n" +
                        "   Type: " + typeQName + "\n" +
                        "   Property: " + propertyQName);
                eventResults.add(result);
                // next one
                continue;
            }
        }
    }
}
