package org.alfresco.enterprise.repo.cluster.core;

import java.net.InetSocketAddress;
import java.util.Set;

import org.alfresco.util.LogUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.surf.util.I18NUtil;

import com.hazelcast.core.Member;
import com.hazelcast.core.MembershipEvent;
import com.hazelcast.core.MembershipListener;

/**
 * Logs changes in membership to the Hazelcast cluster.
 * 
 * @author Matt Ward
 */
public class MembershipChangeLogger implements MembershipListener
{
    private static final String MSG_CLUSTER_MEMBER_JOINED = "system.cluster.member_joined";
    private static final String MSG_CLUSTER_MEMBER_LEFT = "system.cluster.member_left";
    private static final String MSG_CURRENT_MEMBERS = "system.cluster.curr_members";
    private static final String MSG_MEMBER = "system.cluster.member";
    private Log log;
    
    /**
     * Constructor. Allows injection of an Apache Commons {@link Log} instance.
     * 
     * @param log
     */
    public MembershipChangeLogger(Log log)
    {
        this.log = log;
    }
    
    /**
     * Default constructor. 
     */
    public MembershipChangeLogger()
    {
        this.log = LogFactory.getLog(getClass());
    }

    @Override
    public void memberAdded(MembershipEvent event)
    {
        logMembership(event);
    }

    @Override
    public void memberRemoved(MembershipEvent event)
    {
        logMembership(event);
    }
    
    private void logMembership(MembershipEvent event)
    {
        String member = memberToString(event.getMember());
        
        // Event overview, e.g. "Member joined: 10.244.51.102:5702 (hostname: repo-node1)"
        if (event.getEventType() == MembershipEvent.MEMBER_ADDED)
        {
            LogUtil.info(log, MSG_CLUSTER_MEMBER_JOINED, member);
        }
        else if (event.getEventType() == MembershipEvent.MEMBER_REMOVED)
        {
            LogUtil.info(log, MSG_CLUSTER_MEMBER_LEFT, member);            
        }
        
        // Relist all cluster members
        Set<Member> members = event.getCluster().getMembers();
        StringBuilder sb = new StringBuilder(I18NUtil.getMessage(MSG_CURRENT_MEMBERS));
        sb.append("\n");
        for (Member m : members)
        {
            sb.append("  ").
               append(memberToString(m)).
               append("\n");
        }
        log.info(sb.toString());
    }
    
    private String memberToString(Member member)
    {
        InetSocketAddress sockAddr = member.getInetSocketAddress();
        String memberIP = sockAddr.getAddress().getHostAddress();
        int memberPort = sockAddr.getPort();
        String memberHostName = sockAddr.getAddress().getHostName();
        String memberStr = I18NUtil.getMessage(MSG_MEMBER, memberIP + ":" + memberPort, memberHostName);
        return memberStr;
    }
}
