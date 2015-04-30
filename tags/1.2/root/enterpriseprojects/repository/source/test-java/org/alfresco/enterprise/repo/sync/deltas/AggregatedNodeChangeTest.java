/*
 * Copyright 2005-2013 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.sync.deltas;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.alfresco.enterprise.repo.sync.deltas.AggregatedNodeChange.SsmnChangeType;
import org.alfresco.error.AlfrescoRuntimeException;
import org.junit.Test;

/**
 * Unit tests for {@link AggregatedNodeChange.SsmnChangeType}. This class tests that {@link SsmnChangeType delta types} are only combined
 * when it makes sense to do so.
 * 
 * @author Neil Mc Erlean
 * @since 4.2
 */
public class AggregatedNodeChangeTest
{
    @Test public void createFollowedByUpdateIsEquivalentToCreate() throws Exception
    {
        assertEquals(SsmnChangeType.CREATE,
                     SsmnChangeType.CREATE.append(SsmnChangeType.UPDATE));
    }
    
    @Test public void updateFollowedByUpdateIsEquivalentToUpdate() throws Exception
    {
        assertEquals(SsmnChangeType.UPDATE,
                SsmnChangeType.UPDATE.append(SsmnChangeType.UPDATE));
    }
    
    @Test public void allOtherCombinationsAreNotCombinable() throws Exception
    {
        for (SsmnChangeType first : SsmnChangeType.values())
        {
            for (SsmnChangeType second : SsmnChangeType.values())
            {
                if ( !isLegalCombination(first, second))
                {
                    boolean expectedExceptionThrown = false;
                    
                    try                                       { first.append(second); }
                    catch (AlfrescoRuntimeException expected) { expectedExceptionThrown = true; }
                    
                    assertTrue("Expected exception not thrown for combination " + first + "->" + second, expectedExceptionThrown);
                }
            }
        }
    }
    
    private boolean isLegalCombination(SsmnChangeType first, SsmnChangeType second)
    {
        // We allow combinations : (CREATE + UPDATE), (UPDATE + UPDATE) and (any + DELETE - MNT-12156)
        return (SsmnChangeType.UPDATE.equals(second) &&
                (SsmnChangeType.CREATE.equals(first) || SsmnChangeType.UPDATE.equals(first)))
                || SsmnChangeType.DELETE.equals(second);
    }
}
