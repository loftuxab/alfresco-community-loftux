package org.alfresco.repo.content.transform;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.repo.content.MimetypeMap;
import org.alfresco.util.debug.CodeMonkey;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.Assert;

/**
 * Holds and provides the most appropriate content transformer for
 * a particular source and target mimetype transformation request.
 * <p>
 * The transformers themselves are used to determine the applicability
 * of a particular transformation.
 *
 * @see org.alfresco.repo.content.transform.ContentTransformer
 * 
 * @author Derek Hulley
 */
public class ContentTransformerRegistry
{
    private static final Log logger = LogFactory.getLog(ContentTransformerRegistry.class);
    
    private List<ContentTransformer> transformers;
    private MimetypeMap mimetypeMap;
    /** Cache of previously used transactions */
    private Map<TransformationKey, ContentTransformer> transformationCache;
    /** Controls read access to the transformation cache */
    private Lock transformationCacheReadLock;
    private Lock transformationCacheWriteLock;
    
    /**
     * @param transformers all the available transformers that the registry can
     *      work with
     * @param mimetypeMap all the mimetypes available to the system
     */
    public ContentTransformerRegistry(
            List<ContentTransformer> transformers,
            MimetypeMap mimetypeMap)
    {
        Assert.notEmpty(transformers, "At least one content transformer must be supplied");
        Assert.notNull(mimetypeMap, "The MimetypeMap is mandatory");
        this.transformers = transformers;
        this.mimetypeMap = mimetypeMap;
        transformationCache = new HashMap<TransformationKey, ContentTransformer>(17);
        // create lock objects for access to the cache
        ReadWriteLock transformationCacheLock = new ReentrantReadWriteLock();
        transformationCacheReadLock = transformationCacheLock.readLock();
        transformationCacheWriteLock = transformationCacheLock.writeLock();
    }
    
    /**
     * Resets the transformation cache.  This allows a fresh analysis of the best
     * conversions based on actual average performance of the transformers.
     */
    public void resetCache()
    {
        // get a write lock on the cache
        transformationCacheWriteLock.lock();
        try
        {
            transformationCache.clear();
            // done
        }
        finally
        {
            transformationCacheWriteLock.unlock();
        }
    }
    
    /**
     * Gets the best transformer possible.
     * <p>
     * The result is cached for quicker access next time.
     * 
     * @param sourceMimetype the source mimetype of the transformation
     * @param targetMimetype the target mimetype of the transformation
     * @return Returns a content transformer that can perform the desired
     *      transformation or null if no transformer could be found that would do it.
     */
    public ContentTransformer getTransformer(String sourceMimetype, String targetMimetype)
    {
        // check that the mimetypes are valid
        if (!mimetypeMap.getMimetypes().contains(sourceMimetype))
        {
            throw new AlfrescoRuntimeException("Unknown source mimetype: " + sourceMimetype);
        }
        if (!mimetypeMap.getMimetypes().contains(targetMimetype))
        {
            throw new AlfrescoRuntimeException("Unknown target mimetype: " + targetMimetype);
        }
        
        TransformationKey key = new TransformationKey(sourceMimetype, targetMimetype);
        transformationCacheReadLock.lock();
        try
        {
            if (transformationCache.containsKey(key))
            {
                // the translation has been requested before
                // it might have been null
                return transformationCache.get(key);
            }
        }
        finally
        {
            transformationCacheReadLock.unlock();
        }
        // the translation has not been requested before
        // get a write lock on the cache
        // no double check done as it is not an expensive task
        transformationCacheWriteLock.lock();
        try
        {
            // find the most suitable transformer - may be null
            ContentTransformer transformer = findTransformer(sourceMimetype, targetMimetype);
            // store the result even if it is null
            transformationCache.put(key, transformer);
            // done
            return transformer;
        }
        finally
        {
            transformationCacheWriteLock.unlock();
        }
    }
    
    /**
     * Attempts to get a transformer that does the exact transformation before
     * attempting to find a chain of transformers that can perform the transformation.
     * 
     * @return Returns best transformer for the translation - null if all
     *      score 0.0 on reliability
     */
    private ContentTransformer findTransformer(String sourceMimetype, String targetMimetype)
    {
        // search for a simple transformer that can do the job
        ContentTransformer transformer = findDirectTransformer(sourceMimetype, targetMimetype);
        if (transformer == null)
        {
            // attempt to build a transformer from several others
            transformer = findComplexTransformer(sourceMimetype, targetMimetype);
        }
        // done
        if (logger.isDebugEnabled())
        {
            logger.debug("Searched for transformer: \n" +
                    "   source mimetype: " + sourceMimetype + "\n" +
                    "   target mimetype: " + targetMimetype + "\n" +
                    "   transformer: " + transformer);
        }
        return transformer;
    }
    
    /**
     * Loops through the content transformers and picks the one with the highest reliability.
     * <p>
     * Where there are several transformers that are equally reliable, the fastest will be 
     * returned.  A good example of this would be for a <pre>text/plain --> text/plain</pre>
     * conversion.  A transformer that merely passed the stream across without any actuall
     * interpretation of the data will be much faster than a transformer that had to open
     * an application and then save the file back to disk.
     * 
     * @return Returns best transformer for the translation - null if
     *      all score 0.0 on reliability
     */
    private ContentTransformer findDirectTransformer(String sourceMimetype, String targetMimetype)
    {
        double maxReliability = 0.0;
        long leastTime = 100000L;   // 100 seconds - longer than anyone would think of waiting 
        ContentTransformer bestTransformer = null;
        // loop through transformers
        for (ContentTransformer transformer : this.transformers)
        {
            double reliability = transformer.getReliability(sourceMimetype, targetMimetype);
            long time = transformer.getTransformationTime();
            if (reliability <= 0.0)
            {
                // it is unusable
                continue;
            }
            if (reliability < maxReliability)
            {
                // it is not the best one to use
                continue;
            }
            else if (reliability == maxReliability && time >= leastTime)
            {
                // it is as good as the best so far, but it takes longer
                continue;
            }
            bestTransformer = transformer;
            maxReliability = reliability;
            leastTime = time;
        }
        // done
        return bestTransformer;
    }
    
    /**
     * Uses a list of known mimetypes to build a transformation from several
     * direct transformations. 
     */
    private ContentTransformer findComplexTransformer(String sourceMimetype, String targetMimetype)
    {
        // get a complete list of mimetypes
        CodeMonkey.todo("Build complex transformer by searching for transformations by mimetype"); // TODO
        return null;
    }
    
    /**
     * Recursive method to build up a list of content transformers
     */
    private void buildTransformer(List<ContentTransformer> transformers,
            double reliability,
            List<String> touchedMimetypes,
            String currentMimetype,
            String targetMimetype)
    {
        throw new UnsupportedOperationException();
    }
    
    /**
     * A key for a combination of a source and target mimetype
     */
    public static class TransformationKey
    {
        private final String sourceMimetype;
        private final String targetMimetype;
        private final String key;
        
        public TransformationKey(String sourceMimetype, String targetMimetype)
        {
            this.key = (sourceMimetype + "_" + targetMimetype);
            this.sourceMimetype = sourceMimetype;
            this.targetMimetype = targetMimetype;
        }
        
        public String getSourceMimetype()
        {
            return sourceMimetype;
        }
        public String getTargetMimetype()
        {
            return targetMimetype;
        }

        @Override
        public boolean equals(Object obj)
        {
            if (obj == null)
            {
                return false;
            }
            else if (this == obj)
            {
                return true;
            }
            else if (!(obj instanceof TransformationKey))
            {
                return false;
            }
            TransformationKey that = (TransformationKey) obj;
            return this.key.equals(that.key);
        }
        @Override
        public int hashCode()
        {
            return key.hashCode();
        }
    }
}
