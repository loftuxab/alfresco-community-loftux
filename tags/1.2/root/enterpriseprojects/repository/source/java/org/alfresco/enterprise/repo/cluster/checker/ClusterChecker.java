/*
 * Copyright 2005-2013 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.cluster.checker;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

import org.alfresco.enterprise.repo.cluster.core.ClusterService;
import org.alfresco.enterprise.repo.cluster.messenger.MessageReceiver;
import org.alfresco.enterprise.repo.cluster.messenger.Messenger;
import org.alfresco.enterprise.repo.cluster.messenger.MessengerFactory;
import org.alfresco.repo.lock.JobLockService;
import org.alfresco.repo.lock.LockAcquisitionException;
import org.alfresco.repo.security.authentication.AuthenticationException;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.transaction.RetryingTransactionHelper.RetryingTransactionCallback;
import org.alfresco.service.cmr.security.AuthenticationService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.transaction.TransactionService;
import org.alfresco.util.EqualsHelper;
import org.alfresco.util.GUID;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.extensions.surf.util.I18NUtil;

/**
 * Checks that the cluster is working.
 * 
 * @since 4.2
 *
 */
public class ClusterChecker implements MessageReceiver<ClusterMessageEvent>, ApplicationContextAware
{
    private static final Log logger = LogFactory.getLog(ClusterChecker.class);
    private static final QName LOCK = QName.createQName(NamespaceService.SYSTEM_MODEL_1_0_URI, "ClusterChecker");

    /*
     * WORKING: is synced with other nodes in the cluster
     * NOTWORKING: is alive but not synced with other nodes in the cluster 
     * UNKNOWN: status is unknown (could be in the middle of checking)
     * CHECKING: still waiting for cluster check response
     */
    public static enum NodeStatus
    {
        WORKING, NOTWORKING, TIMEOUT, UNKNOWN;
    };

    // time to wait for a cluster node to respond
    private int timeout = 4000; // ms

    private ApplicationContext applicationContext;
    private AuthenticationService authenticationService;
    private TransactionService transactionService;
    private MessengerFactory messengerFactory;
    private JobLockService jobLockService;
    private ClusterService clusterService;
    private Messenger<ClusterMessageEvent> messenger;

    private Timer timer = new Timer();

    // cluster nodes that this node knows about
    private Map<String, NodeInfo> nodeInfo = new ConcurrentHashMap<String, NodeInfo>();

    // unique id for this cluster node
    private final String id = GUID.generate();

    // JSON Object Mapper
    private final ObjectMapper jsonMapper = new ObjectMapper();

    /**
     * Attempts to get the lock. If the lock couldn't be taken, then <tt>null</tt> is returned.
     * 
     * @return Returns the lock token or <tt>null</tt>
     */
    private String getLock(long time)
    {
        try
        {
            return jobLockService.getLock(LOCK, time);
        }
        catch (LockAcquisitionException e)
        {
            return null;
        }
    }
    
    public void init()
    {
        this.messenger = messengerFactory.createMessenger(getClass().getName(), true);
        messenger.setReceiver(this);
    }
    
    public void shutdown()
    {
    	cancelTimer();
    }

    public void setTimeout(int timeout)
    {
        this.timeout = timeout;
    }

    public void setJobLockService(JobLockService jobLockService)
    {
        this.jobLockService = jobLockService;
    }
    
    public void setClusterService(ClusterService clusterService)
    {
        this.clusterService = clusterService;
    }
    
    public void setTransactionService(TransactionService transactionService)
    {
        this.transactionService = transactionService;
    }

    public void setAuthenticationService(AuthenticationService authenticationService)
    {
        this.authenticationService = authenticationService;
    }

    public void setMessengerFactory(MessengerFactory messengerFactory)
    {
        this.messengerFactory = messengerFactory;
    }

    private void cancelTimer()
    {
        timer.cancel();
    }

    private NodeInfo registerNode(String id)
    {
        NodeInfo info = new NodeInfo(id);
        nodeInfo.put(id, info);
        return info;
    }

    private void checkCluster()
    {
        // set the status of any currently tracked to 'checking'
        for(NodeInfo info : nodeInfo.values())
        {
            info.setChecking(true);
        }

        // Authenticate and get a ticket. This will be used to validate that the other nodes in the cluster are
        // 'working' i.e. their caches are updating in the cluster.
        try
        {
            AuthenticationUtil.pushAuthentication();
        	AuthenticationUtil.setFullyAuthenticatedUser(AuthenticationUtil.getAdminUserName());
            String ticket = authenticationService.getCurrentTicket();
            messenger.send(new ClusterValidateEvent(this, ticket, id, null));
        }
        catch(AuthenticationException e)
        {
        	logger.warn("Unable to check cluster, authentication failed", e);
        	return;
        }
        finally
        {
            AuthenticationUtil.popAuthentication();
        }

        // A timer to mark nodes still in the checking state as not alive after a timeout.
        timer.schedule(new TimerTask()
        {
            @Override
            public void run()
            {
                for(NodeInfo info : nodeInfo.values())
                {
                    List<String> timedOut = info.timeoutNodes();
                    for(String nodeId : timedOut)
                    {
                        nodePairStatusChange(info.getId(), nodeId, NodeStatus.TIMEOUT);
                    }
                }
            }
        }, timeout);
    }
    
    private void nodePairStatusChange(String sourceNodeId, String targetNodeId, NodeStatus status)
    {
        if (logger.isDebugEnabled())
        {
            logger.debug("Publishing ClusterNodePairStatusEvent: source=" + sourceNodeId +
                        ", target=" + targetNodeId + ", status=" + status);
        }
        publishEvent(new ClusterNodePairStatusEvent(this, sourceNodeId, targetNodeId, status));
    }
    
    private void nodeFound(String nodeId)
    {
        publishEvent(new ClusterNodeExistsEvent(this, nodeId));
    }

    private void publishEvent(ApplicationEvent event)
    {
        applicationContext.publishEvent(event);
    }

    private void handleValidationEvent(ClusterValidateEvent validateEvent)
    {
        String sourceId = validateEvent.getSourceId();
        String ticket = validateEvent.getTicket();

        // try to validate the ticket generated by the source node
        boolean ticketValid = true;
        try
        {
            AuthenticationUtil.pushAuthentication();
            authenticationService.validate(ticket);
            if(!authenticationService.getCurrentUserName().equals(AuthenticationUtil.getAdminUserName()))
            {
                ticketValid = false;
            }
        }
        catch(AuthenticationException e)
        {
            ticketValid = false;
        }
        finally
        {
            AuthenticationUtil.popAuthentication();
        }

        String ipAddress = clusterService.getMemberIP();
        String hostName = clusterService.getMemberHostName();
        int port = clusterService.getMemberPort();
        messenger.send(new ClusterValidateResponseEvent(this, ipAddress, hostName, port, sourceId, id, ticketValid)); 
    }
    
    private void handleValidationResponse(ClusterValidateResponseEvent validateResponseEvent)
    {
        String sourceId = validateResponseEvent.getSourceId();
        String targetId = validateResponseEvent.getTargetId();
        final String ipAddress = validateResponseEvent.getIPAddress(); // target address
        final String hostName = validateResponseEvent.getHostName();
        final int port = validateResponseEvent.getPort();

        if (logger.isDebugEnabled())
        {
            logger.debug("Handling validation response, source=" + sourceId +
                        ", target=" + targetId + ", address=" + ipAddress);
        }

        NodeInfo source = getNodeInfo(sourceId);
        boolean newSourceNode = false;
        if(source == null)
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("Registering source node " + sourceId);
            }
            source = registerNode(sourceId);
            newSourceNode = true;
        }

        // update the target's address, if it isn't known already
        boolean newTargetNode = false;
        NodeInfo remote = getNodeInfo(targetId);
        if(remote == null)
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("Registering target node " + targetId + ", " + ipAddress);
            }
            remote = registerNode(targetId);
            newTargetNode = true;
        }
        remote.setIPAddress(ipAddress);
        remote.setHostName(hostName);
        remote.setPort(port);

        // update source node's view of the target's status 
        boolean ticketValid = validateResponseEvent.isTicketValid();
        NodeStatus newTargetStatus = ticketValid ? NodeStatus.WORKING : NodeStatus.NOTWORKING;
        if (logger.isDebugEnabled())
        {
            logger.debug("Target status: " + newTargetStatus);
        }
        source.setStatus(targetId, newTargetStatus);

        if(newSourceNode)
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("Publishing source node found event, id: " + sourceId);
            }
            nodeFound(sourceId);
        }

        if(newTargetNode)
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("Publishing target node found event, id: " + targetId);
            }
            nodeFound(targetId);
        }

        if(!sourceId.equals(targetId) && newTargetStatus != NodeStatus.UNKNOWN)
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("Node pair status change detected");
            }
            nodePairStatusChange(sourceId, targetId, newTargetStatus);
        }
    }
    
    public boolean isConnected()
    {
        return messenger.isConnected();     
    }

    public boolean isClusterActive()
    {
        return messengerFactory.isClusterActive();
    }
    
    public Map<String, NodeInfo> getNodeInfo()
    {
        return Collections.unmodifiableMap(nodeInfo);
    }
    
    public NodeInfo getNodeInfo(String nodeId)
    {
        return nodeInfo.get(nodeId);
    }

    public String getId()
    {
        return id;
    }
    
    public void check()
    {
        if (!(clusterService.isInitialised() && clusterService.isClusteringEnabled()))
        {
            // We can't successfully run this tool.
            throw new IllegalStateException(I18NUtil.getMessage("system.cluster.checker.notavailable"));
        }
        // Take out a lock to prevent more than one check at a time
        RetryingTransactionCallback<String> txnWork = new RetryingTransactionCallback<String>()
        {
            public String execute() throws Exception
            {
                String lockToken = getLock(timeout + 1000);
                return lockToken;
            }
        };

        final String lockToken = transactionService.getRetryingTransactionHelper().doInTransaction(txnWork, false, true);
        if(lockToken == null)
        {
            logger.warn("Can't get lock. Assume multiple cluster checkers ...");
            return;
        }

        // Reset the cluster check (one or members may have been restarted and will have new UUIDs,
        // so it is best to start afresh).
        for (NodeInfo ni : getNodeInfo().values())
        {
            String nodeId = ni.getId();
            publishEvent(new ClusterNodeStopTrackingEvent(this, nodeId));
            nodeInfo.remove(nodeId);
        }
        
        // Kick off the check by broadcasting the initiating event to each node in the cluster
        if (messenger.isConnected())
        {
            messenger.send(new ClusterCheckEvent(this, id, null));
        }

        // A timer to release the lock after a timeout
        timer.schedule(new TimerTask()
        {
            @Override
            public void run()
            {
                jobLockService.releaseLock(lockToken, LOCK);
            }
        }, timeout);
    }
    
    public List<PeerNodeInfo> getPeers(String nodeId)
    {
        NodeInfo nodeInfo = getNodeInfo(nodeId);
        Map<String, PeerStatus> peersInfo = nodeInfo.getPeersInfo();

        List<PeerNodeInfo> ret = new ArrayList<PeerNodeInfo>();
        for(String peerId : peersInfo.keySet())
        {
            if(peerId.equals(nodeId))
            {
                continue;
            }
            NodeInfo peerInfo = getNodeInfo(peerId);
            NodeStatus peerStatus = peersInfo.get(peerId).getNodeStatus();
            String peerAddress = peerInfo.getIPAddress();
            ret.add(new PeerNodeInfo(peerId, peerAddress, peerStatus));
        }

        return ret;
    }
    
    public void stopChecking(String nodeId)
    {
        if(nodeInfo.containsKey(nodeId))
        {
            nodeInfo.remove(nodeId);
        }
        for(NodeInfo node : nodeInfo.values())
        {
            node.stopChecking(nodeId);
        }
        publishEvent(new ClusterNodeStopTrackingEvent(this, nodeId)); 
    }

    @Override
    public void onReceive(ClusterMessageEvent event)
    {
        if (event == null)
        {
            return;
        }

        if(event instanceof ClusterCheckEvent)
        {
        	checkCluster();
        }
        else if(event instanceof ClusterValidateEvent)
        {
            // handle validation request from another node
            handleValidationEvent((ClusterValidateEvent)event);
        }
        else if(event instanceof ClusterValidateResponseEvent)
        {
        	// handle response to a validation request
            handleValidationResponse((ClusterValidateResponseEvent)event);
        }
    }
    
    public Set<MemberPair> getPeerDetails()
    {
        Set<MemberPair> pairs = new HashSet<MemberPair>();

        for(NodeInfo node : nodeInfo.values())
        {
            // a cluster node is regarded as working only if every other node agrees
            // notes that for a 2 node cluster with one node down, the other node will still be regarded
            // as not working because there are no other nodes to counter the non-working node.
            for (MemberPair pair : node.getPeerUUIDs())
            {
                // Transform member UUIDs into member details (JSON strings)
                NodeInfo first = nodeInfo.get(pair.getFirst());
                String firstJSON = nodeInfoAsJSON(first);
                NodeInfo second = nodeInfo.get(pair.getSecond());
                String secondJSON = nodeInfoAsJSON(second);
                
                pairs.add(new MemberPair(firstJSON, secondJSON, pair.isWorking()));
            }
        }

        return pairs;
    }

    private String nodeInfoAsJSON(NodeInfo nodeInfo)
    {
        try
        {
            return jsonMapper.writeValueAsString(nodeInfo);
        }
        catch (Exception error)
        {
            throw new RuntimeException("Unable to generate JSON for NodeInfo: " + nodeInfo);
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException
    {
        this.applicationContext = applicationContext;
    }

    // Records information on a peer i.e. whether it is being checked and its status
    private static class PeerStatus
    {
        private boolean checking;
        private NodeStatus nodeStatus;

        public PeerStatus()
        {
            this.checking = false;
            this.nodeStatus = NodeStatus.UNKNOWN;
        }
        
        public boolean isChecking()
        {
            return checking;
        }

        void setChecking(boolean checking)
        {
            this.checking = checking;
        }

        public NodeStatus getNodeStatus()
        {
            return nodeStatus;
        }

        void setNodeStatus(NodeStatus nodeStatus)
        {
            this.nodeStatus = nodeStatus;
        }
    }
    
    public static class PeerNodeInfo
    {
        private String peerId;
        private String peerAddress;
        private NodeStatus peerStatus;

        public PeerNodeInfo(String peerId, String peerAddress, NodeStatus peerStatus) {
            super();
            this.peerId = peerId;
            this.peerAddress = peerAddress;
            this.peerStatus = peerStatus;
        }

        public String getPeerId()
        {
            return peerId;
        }

        public String getPeerAddress()
        {
            return peerAddress;
        }

        public NodeStatus getPeerStatus()
        {
            return peerStatus;
        }
    }

    // Information pertaining to a cluster node and its peers
    @JsonAutoDetect(fieldVisibility = Visibility.ANY,
                    getterVisibility = Visibility.NONE,
                    setterVisibility = Visibility.NONE)
    public static class NodeInfo
    {
        private String id;
        private String ipAddress;
        private String hostName;
        private int port;
        private @JsonIgnore Map<String, PeerStatus> nodeInfos = new ConcurrentHashMap<String, PeerStatus>(5);

        public NodeInfo(String id)
        {
            super();
            this.id = id;
        }

        public String getId()
        {
            return id;
        }

        public String getIPAddress()
        {
            return ipAddress;
        }

        void setIPAddress(String address)
        {
            this.ipAddress = address;
        }

        void setStatus(String targetId, NodeStatus status)
        {
            PeerStatus peerStatus = getStatus(targetId, true);
            peerStatus.setChecking(false);
            peerStatus.setNodeStatus(status);
        }
        
        void stopChecking(String nodeId)
        {
            nodeInfos.remove(nodeId);
        }
        
        public Map<String, PeerStatus> getPeersInfo()
        {
            return Collections.unmodifiableMap(nodeInfos);
        }

        public PeerStatus getStatus(String nodeId)
        {
            return getStatus(nodeId, false);
        }
        
        public PeerStatus getStatus(String nodeId, boolean create)
        {
            PeerStatus peerStatus = nodeInfos.get(nodeId);
            if(peerStatus == null)
            {
                peerStatus = new PeerStatus();
                nodeInfos.put(nodeId, peerStatus);
            }
            return peerStatus;
        }
        
        void setChecking(boolean checking)
        {
            for(String nodeId : nodeInfos.keySet())
            {
                setChecking(nodeId, checking);
            }
        }
        
        void setChecking(String nodeId, boolean checking)
        {
            PeerStatus status = getStatus(nodeId, true);
            status.setChecking(checking);
        }

        void setStatuses(NodeStatus status)
        {
            for(String nodeId : nodeInfos.keySet())
            {
                setStatus(nodeId, status);
            }
        }

        public String getHostName()
        {
            return this.hostName;
        }

        public void setHostName(String hostName)
        {
            this.hostName = hostName;
        }

        public int getPort()
        {
            return this.port;
        }

        public void setPort(int port)
        {
            this.port = port;
        }

        List<String> timeoutNodes()
        {
            List<String> timedOut = new ArrayList<String>();

            for(String nodeId : nodeInfos.keySet())
            {
                if(getStatus(nodeId).isChecking())
                {
                    setStatus(nodeId, NodeStatus.TIMEOUT);
                    timedOut.add(nodeId);
                }
            }

            return timedOut;
        }
        
        public Set<MemberPair> getPeerUUIDs()
        {
            Set<MemberPair> peers = new HashSet<MemberPair>();
            for(String nodeId : nodeInfos.keySet())
            {
                if(!getId().equals(nodeId))
                {
                    boolean working = (getStatus(nodeId).getNodeStatus() == NodeStatus.WORKING);
                    peers.add(new MemberPair(getId(), nodeId, working));
                }
            }
            
            return peers;
        }
        
        public boolean equals(Object other)
        {
            if (this == other)
            {
                return true;
            }

            if(!(other instanceof NodeInfo))
            {
                return false;
            }

            NodeInfo nodeInfo = (NodeInfo)other;
            return EqualsHelper.nullSafeEquals(getId(), nodeInfo.getId());
        }
    }
    
    public static class MemberPair extends UnorderedPair<String>
    {
        private static final long serialVersionUID = 1L;
        private boolean working;
        
        public MemberPair(String first, String second, boolean working)
        {
            super(first, second);
            this.working = working;
        }

        public boolean isWorking()
        {
            return this.working;
        }
    }
    
    public static class UnorderedPair<T> implements Serializable
    {
        private static final long serialVersionUID = -8947346745086237616L;

        @SuppressWarnings({ "unchecked", "rawtypes" })
        public static final UnorderedPair NULL_PAIR = new UnorderedPair(null, null);
        
        @SuppressWarnings("unchecked")
        public static final <X> UnorderedPair<X> nullPair()
        {
            return NULL_PAIR;
        }
        
        /**
         * The first member of the pair.
         */
        private T first;
        
        /**
         * The second member of the pair.
         */
        private T second;
        
        /**
         * Make a new one.
         * 
         * @param first The first member.
         * @param second The second member.
         */
        public UnorderedPair(T first, T second)
        {
            this.first = first;
            this.second = second;
        }
        
        /**
         * Get the first member of the tuple.
         * @return The first member.
         */
        public final T getFirst()
        {
            return first;
        }
        
        /**
         * Get the second member of the tuple.
         * @return The second member.
         */
        public final T getSecond()
        {
            return second;
        }
        
        @Override
        public boolean equals(Object other)
        {
            if (this == other)
            {
                return true;
            }
            if (other == null || !(other instanceof UnorderedPair<?>))
            {
                return false;
            }
            UnorderedPair<?> o = (UnorderedPair<?>)other;
            return EqualsHelper.nullSafeEquals(this.first, o.first) &&
                   EqualsHelper.nullSafeEquals(this.second, o.second) ||
                   EqualsHelper.nullSafeEquals(this.first, o.second) &&
                   EqualsHelper.nullSafeEquals(this.second, o.first);
        }
        
        @Override
        public int hashCode()
        {
            return (first == null ? 0 : first.hashCode()) + (second == null ? 0 : second.hashCode());
        }

        @Override
        public String toString()
        {
            return "(" + first + ", " + second + ")";
        }
    }
}