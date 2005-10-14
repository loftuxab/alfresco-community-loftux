/*
 * Copyright (C) 2005 Alfresco, Inc.
 *
 * Licensed under the Alfresco Network License. You may obtain a
 * copy of the License at
 *
 *   http://www.alfrescosoftware.com/legal/
 *
 * Please view the license relevant to your network subscription.
 *
 * BY CLICKING THE "I UNDERSTAND AND ACCEPT" BOX, OR INSTALLING,  
 * READING OR USING ALFRESCO'S Network SOFTWARE (THE "SOFTWARE"),  
 * YOU ARE AGREEING ON BEHALF OF THE ENTITY LICENSING THE SOFTWARE    
 * ("COMPANY") THAT COMPANY WILL BE BOUND BY AND IS BECOMING A PARTY TO 
 * THIS ALFRESCO NETWORK AGREEMENT ("AGREEMENT") AND THAT YOU HAVE THE   
 * AUTHORITY TO BIND COMPANY. IF COMPANY DOES NOT AGREE TO ALL OF THE   
 * TERMS OF THIS AGREEMENT, DO NOT SELECT THE "I UNDERSTAND AND AGREE"   
 * BOX AND DO NOT INSTALL THE SOFTWARE OR VIEW THE SOURCE CODE. COMPANY   
 * HAS NOT BECOME A LICENSEE OF, AND IS NOT AUTHORIZED TO USE THE    
 * SOFTWARE UNLESS AND UNTIL IT HAS AGREED TO BE BOUND BY THESE LICENSE  
 * TERMS. THE "EFFECTIVE DATE" FOR THIS AGREEMENT SHALL BE THE DAY YOU  
 * CHECK THE "I UNDERSTAND AND ACCEPT" BOX.
 */
package org.alfresco.repo.security.permissions.impl.acegi;

import java.util.ArrayList;
import java.util.ListIterator;

import junit.framework.TestCase;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.search.results.ChildAssocRefResultSet;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.ResultSetRow;
import org.alfresco.service.namespace.QName;

public class FilteringResultSetTest extends TestCase
{

    
    
    public FilteringResultSetTest()
    {
        super();
    }

    public FilteringResultSetTest(String arg0)
    {
        super(arg0);
    }

    public void test()
    {
        StoreRef storeRef = new StoreRef("protocol", "test");
        NodeRef root = new NodeRef(storeRef, "n0");
        NodeRef n1 = new NodeRef(storeRef, "n1");
        NodeRef n2 = new NodeRef(storeRef, "n2");
        NodeRef n3 = new NodeRef(storeRef, "n3");
        NodeRef n4 = new NodeRef(storeRef, "n4");
        NodeRef n5 = new NodeRef(storeRef, "n5");
        
        ArrayList<ChildAssociationRef> cars = new ArrayList<ChildAssociationRef>();
        ChildAssociationRef car0 = new ChildAssociationRef(null, null, null, root);
        ChildAssociationRef car1 = new ChildAssociationRef(ContentModel.ASSOC_CHILDREN, root, QName.createQName("{test}n2"), n1);
        ChildAssociationRef car2 = new ChildAssociationRef(ContentModel.ASSOC_CHILDREN, n1, QName.createQName("{test}n3"), n2);
        ChildAssociationRef car3 = new ChildAssociationRef(ContentModel.ASSOC_CHILDREN, n2, QName.createQName("{test}n4"), n3);
        ChildAssociationRef car4 = new ChildAssociationRef(ContentModel.ASSOC_CHILDREN, n3, QName.createQName("{test}n5"), n4);
        ChildAssociationRef car5 = new ChildAssociationRef(ContentModel.ASSOC_CHILDREN, n4, QName.createQName("{test}n6"), n5);
        cars.add(car0);
        cars.add(car1);
        cars.add(car2);
        cars.add(car3);
        cars.add(car4);
        cars.add(car5);
        
        ResultSet in = new ChildAssocRefResultSet(null, cars, null);
        
        FilteringResultSet filtering = new FilteringResultSet(in);
        
        assertEquals(0, filtering.length());
        for(int i = 0; i < 6; i++)
        {
           filtering.setIncluded(i, true);
           assertEquals(1, filtering.length());
           assertEquals("n"+i, filtering.getNodeRef(0).getId());
           filtering.setIncluded(i, false);
           assertEquals(0, filtering.length());
        }       
        
        for(int i = 0; i < 6; i++)
        {
            filtering.setIncluded(i, true);
            assertEquals(i+1, filtering.length());
            assertEquals("n"+i, filtering.getNodeRef(i).getId());
        }
        
        int count = 0;
        for(ResultSetRow row : filtering)
        {
            assertNotNull(row);
            assertTrue(count < 6);
            count++;
        }
        
        ResultSetRow last = null;
        for(ListIterator<ResultSetRow> it = filtering.iterator(); it.hasNext(); /**/)
        {
            ResultSetRow row = it.next();
            if(last != null)
            {
                assertTrue(it.hasPrevious()); 
                ResultSetRow previous = it.previous();
                assertEquals(last.getIndex(), previous.getIndex());
                row = it.next();
                
            }
            else
            {
                assertFalse(it.hasPrevious());
            }
            last = row;
         
        }
    }
    
}
