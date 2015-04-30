package org.alfresco.enterprise.repo.cluster.core;

import static org.junit.Assert.*;

import java.net.Inet6Address;
import java.net.UnknownHostException;

import org.junit.Before;
import org.junit.Test;


/**
 * Tests for the {@link NonMemberIPAddressPickerImpl} class.
 * 
 * @author Matt Ward
 */
public class NonMemberIPAddressPickerImplTest
{
    private NonMemberIPAddressPickerImpl addressPicker;
    
    @Before
    public void setUp() throws Exception
    {
        addressPicker = new NonMemberIPAddressPickerImpl();
    }

    @Test
    public void testPick() throws UnknownHostException
    {
        String result = addressPicker.pick();

        if (result.contains("."))
        {
            // IPv4
            String[] parts = result.split("\\.");
            assertEquals(4, parts.length);
            for (String part : parts)
            {
                int partInt = Integer.parseInt(part); 
                assertTrue(partInt < 256);
                assertTrue(partInt >= 0);
            }
        }
        else if (result.contains(":"))
        {
            // IPv6
            Inet6Address inet6 = (Inet6Address) Inet6Address.getByName(result);
            assertNotNull(inet6);
        }
    }
}
