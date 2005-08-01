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
package org.alfresco.repo.node.integrity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.repo.transaction.AlfrescoTransactionSupport;
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
public class IntegrityChecker
        implements  NodeServicePolicies.OnCreateNodePolicy,
                    NodeServicePolicies.OnUpdatePropertiesPolicy,
                    NodeServicePolicies.OnDeleteNodePolicy,
                    NodeServicePolicies.OnAddAspectPolicy,
                    NodeServicePolicies.OnRemoveAspectPolicy,
                    NodeServicePolicies.OnCreateChildAssociationPolicy,
                    NodeServicePolicies.OnDeleteChildAssociationPolicy,
                    NodeServicePolicies.OnCreateAssociationPolicy,
                    NodeServicePolicies.OnDeleteAssociationPolicy
{
    private static Log logger = LogFactory.getLog(IntegrityChecker.class);
    
    /** key against which the event list is stored in the current transaction */
    private static final String KEY_EVENT_LIST = "IntegrityChecker.EventList";
    
    // build sets of event names particular to a type of integrity check
    public static final List<IntegrityEvent.EventType> CHECK_ALL_PROPERTIES = new ArrayList<IntegrityEvent.EventType>(4);
    static
    {
        // check that all required properties are present
        CHECK_ALL_PROPERTIES.add(IntegrityEvent.EventType.PROPERTIES_CHANGED);
        CHECK_ALL_PROPERTIES.add(IntegrityEvent.EventType.NODE_CREATED);
        CHECK_ALL_PROPERTIES.add(IntegrityEvent.EventType.ASPECT_ADDED);
        // TODO: Further checks for associations required
    }

    private PolicyComponent policyComponent;
    private DictionaryService dictionaryService;
    private NodeService nodeService;
    private boolean enabled;
    private boolean failOnViolation;
    private int maxErrorsPerTransaction;
    private boolean traceOn;
    
    /**
     */
    public IntegrityChecker()
    {
        this.enabled = true;
        this.failOnViolation = false;
        this.maxErrorsPerTransaction = 10;
        this.traceOn = false;
    }

    /**
     * @param policyComponent the component to register behaviour with
     */
    public void setPolicyComponent(PolicyComponent policyComponent)
    {
        this.policyComponent = policyComponent;
    }

    /**
     * @param dictionaryService the dictionary against which to confirm model details
     */
    public void setDictionaryService(DictionaryService dictionaryService)
    {
        this.dictionaryService = dictionaryService;
    }

    /**
     * @param nodeService the node service to use for browsing node structures
     */
    public void setNodeService(NodeService nodeService)
    {
        this.nodeService = nodeService;
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
     * Registers the system-level policy behaviours
     */
    public void init()
    {
        if (enabled)  // only register behaviour if integrity checking is on
        {
            // register behaviour
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
     * Ensures that this service is registered with the transaction and saves the event
     * 
     * @param event
     */
    @SuppressWarnings("unchecked")
    private void save(IntegrityEvent event)
    {
        // register this service
        AlfrescoTransactionSupport.bindIntegrityChecker(this);
        
        // get the event list
        List<IntegrityEvent> events = (List<IntegrityEvent>) AlfrescoTransactionSupport.getResource(KEY_EVENT_LIST);
        if (events == null)
        {
            events = new ArrayList<IntegrityEvent>(100);
            AlfrescoTransactionSupport.bindResource(KEY_EVENT_LIST, events);
        }
        // add event
        events.add(event);
    }

    /**
     * @see IntegrityEvent#EventType.NODE_CREATED
     */
    public void onCreateNode(ChildAssociationRef childAssocRef)
    {
        IntegrityEvent event = new IntegrityEvent(
                IntegrityEvent.EventType.NODE_CREATED,
                childAssocRef.getChildRef());
        event.setSecondaryNodeRef(childAssocRef.getParentRef());
        event.setAssocTypeQName(childAssocRef.getTypeQName());
        event.setAssocQName(childAssocRef.getQName());
        
        // set optional tracing
        setTrace(event);
        // save event
        save(event);
    }

    /**
     * @see IntegrityEvent#EventType.PROPERTIES_CHANGED
     */
    public void onUpdateProperties(
            NodeRef nodeRef,
            Map<QName, Serializable> before,
            Map<QName, Serializable> after)
    {
        IntegrityEvent event = new IntegrityEvent(
                IntegrityEvent.EventType.PROPERTIES_CHANGED,
                nodeRef);

        // set optional tracing
        setTrace(event);
        // save event
        save(event);
    }

    public void onDeleteNode(ChildAssociationRef childAssocRef)
    {
        IntegrityEvent event = new IntegrityEvent(
                IntegrityEvent.EventType.NODE_DELETED,
                childAssocRef.getChildRef());
        event.setAssocTypeQName(childAssocRef.getTypeQName());
        event.setAssocQName(childAssocRef.getQName());
        
        // set optional tracing
        setTrace(event);
        // save event
        save(event);
    }

    /**
     * @see IntegrityEvent#EventType.ASPECT_ADDED
     */
    public void onAddAspect(NodeRef nodeRef, QName aspectTypeQName)
    {
        IntegrityEvent event = new IntegrityEvent(
                IntegrityEvent.EventType.ASPECT_ADDED,
                nodeRef);
        event.setAspectTypeQName(aspectTypeQName);
        
        // set optional tracing
        setTrace(event);
        // save event
        save(event);
    }

    /**
     * @see IntegrityEvent#EventType.ASPECT_REMOVED
     */
    public void onRemoveAspect(NodeRef nodeRef, QName aspectTypeQName)
    {
        IntegrityEvent event = new IntegrityEvent(
                IntegrityEvent.EventType.ASPECT_REMOVED,
                nodeRef);
        event.setAspectTypeQName(aspectTypeQName);
        
        // set optional tracing
        setTrace(event);
        // save event
        save(event);
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
     * <p>
     * The interface contracts also requires that all events for the transaction
     * get cleaned up.
     */
    public void checkIntegrity() throws IntegrityException
    {
        if (!enabled)
        {
            return;
        }
        
        // process events and check for failures
        List<IntegrityRecord> failures = processAllEvents();
        // clear out all events
        AlfrescoTransactionSupport.unbindResource(KEY_EVENT_LIST);
        
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
     * @return Returns a list of integrity violations, up to the
     *      {@link #maxErrorsPerTransaction the maximum defined
     */
    @SuppressWarnings("unchecked")
    private List<IntegrityRecord> processAllEvents()
    {
        // the results
        ArrayList<IntegrityRecord> results = new ArrayList<IntegrityRecord>(0); // generally unused

        List<IntegrityEvent> events = (List<IntegrityEvent>) AlfrescoTransactionSupport.getResource(KEY_EVENT_LIST);
        if (events == null)
        {
            // no events were registered - nothing of significance happened
            return results;
        }

        // the current node reference
        NodeRef currentNodeRef = null;
        String currentNodeRefStr = null;
        // the current event type
        IntegrityEvent.EventType currentEventType = null;
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
                currentNodeRef = event.getPrimaryNodeRef();
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
            if (traceOn && event.getTrace() != null)
            {
                // record the current event trace if present
                for (IntegrityRecord record : eventResults)
                {
                    record.addTrace(event.getTrace());
                }
            }
            
            // copy all the event results to the final results
            results.addAll(eventResults);
            // clear the event results
            eventResults.clear();
            
            if (results.size() >= maxErrorsPerTransaction)
            {
                // only so many errors wanted at a time
                break;
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
