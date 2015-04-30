package org.alfresco.enterprise.repo.cluster.cache;

import static org.junit.Assert.assertEquals;

import org.alfresco.enterprise.repo.cluster.messenger.Messenger;
import org.alfresco.repo.cache.DefaultSimpleCache;
import org.alfresco.repo.cache.SimpleCacheTestBase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class InvalidatingCacheTest extends SimpleCacheTestBase<InvalidatingCache<Integer, String>>
{
    /** Messenger used to invalid cache keys - therefore parameterized with key type. */
    private @Mock Messenger<InvalidationMessage> messenger;
    private DefaultSimpleCache<Integer, String> backingCache;
    
    @Override
    protected InvalidatingCache<Integer, String> createCache()
    {
        backingCache = new DefaultSimpleCache<Integer, String>();
        InvalidatingCache<Integer, String> cache = new InvalidatingCache<Integer, String>(backingCache, messenger);
        cache.init();
        return cache;
    }
    
    /**
     * Check that a remove operation that changes the contents of a particular cache entry
     * will result in a suitable invalidation message being sent.
     */
    @Test
    public void willGenerateInvalidationMessagesForSpecificKeys()
    {
        backingCache.put(1, "one");
        backingCache.put(2, "two");
        backingCache.put(3, "three");
        backingCache.put(4, "four");
        backingCache.put(5, "five");
        
        // We want to check verifications as we go along
        InOrder inOrder = Mockito.inOrder(messenger);
        
        // Nothing happens to key 1
        inOrder.verify(messenger, Mockito.never()).send(InvalidationMessage.forKey(1));
        
        // Update a value
        cache.put(2, "II");
        inOrder.verify(messenger).send(InvalidationMessage.forKey(2));
        
        // Nothing happens to key 3
        inOrder.verify(messenger, Mockito.never()).send(InvalidationMessage.forKey(3));
        
        // Remove a value
        cache.remove(4);
        inOrder.verify(messenger).send(InvalidationMessage.forKey(4));
        
        // Nothing happens to key 5
        inOrder.verify(messenger, Mockito.never()).send(InvalidationMessage.forKey(5));
        
        // Put a new, additional value
        cache.put(6, "six");
        inOrder.verify(messenger, Mockito.never()).send(InvalidationMessage.forKey(6));
        
        // Remove all
        cache.clear();
        // The entire cache is invalid
        inOrder.verify(messenger).send(InvalidationMessage.forAllKeys());
        
        // Put still does not result in broadcast of invalidation message
        cache.put(6, "six");
        inOrder.verify(messenger, Mockito.never()).send(InvalidationMessage.forKey(6));
        // But now updating the value, does.
        cache.put(6, "six-updated");
        inOrder.verify(messenger).send(InvalidationMessage.forKey(6));
        
        // Putting an existing key/value pair should not send invalidation message
        cache.put(2, "II");
        inOrder.verify(messenger, Mockito.never()).send(InvalidationMessage.forKey(2));
    }
    
    /**
     * Check that an incoming invalidation message results in the cache item being removed.
     */
    @Test
    public void willRespondToInvalidationMessageForSpecificKeys()
    {
        backingCache.put(1, "one");
        backingCache.put(2, "two");
        backingCache.put(3, "three");
        
        // Check pre-conditions of test.
        assertEquals(3, cache.getKeys().size());
        assertEquals("one", cache.get(1));
        assertEquals("two", cache.get(2));
        assertEquals("three", cache.get(3));
        
        // Simulate cache invalidation message being received.
        cache.onReceive(InvalidationMessage.forKey(2));
        
        // Check cache state after invalidation
        assertEquals(2, cache.getKeys().size());
        assertEquals("one", cache.get(1));
        assertEquals("three", cache.get(3));
    }
    
    /**
     * When all keys have been invalidated then a suitable message is sent.
     */
    @Test
    public void willGenerateInvalidationMessagesForAllKeys()
    {
        backingCache.put(1, "one");
        backingCache.put(2, "two");
        backingCache.put(3, "three");
        backingCache.put(4, "four");
        backingCache.put(5, "five");
        
        cache.clear();
        
        // The entire cache is invalid
        Mockito.verify(messenger).send(InvalidationMessage.forAllKeys());
    }
    
    @Test
    public void willRespondToInvalidationMessageForEntireCache()
    {
        backingCache.put(1, "one");
        backingCache.put(2, "two");
        backingCache.put(3, "three");
        
        // Check pre-conditions of test.
        assertEquals(3, cache.getKeys().size());
        assertEquals("one", cache.get(1));
        assertEquals("two", cache.get(2));
        assertEquals("three", cache.get(3));
        
        // Simulate cache invalidation message being received.
        cache.onReceive(InvalidationMessage.forAllKeys());
        
        // Check cache state after invalidation
        assertEquals(0, cache.getKeys().size());
    }
}
