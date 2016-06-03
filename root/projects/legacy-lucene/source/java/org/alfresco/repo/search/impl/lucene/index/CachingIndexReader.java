package org.alfresco.repo.search.impl.lucene.index;

import java.io.IOException;

import org.apache.lucene.index.TermDocs;

/**
 * 
 * @author andyh
 *
 */
public interface CachingIndexReader
{
    public String getId(int n) throws IOException;
    
    public String getPathLinkId(int n) throws IOException;
    
    public String[] getIds(int n) throws IOException;
    
    public String getIsCategory(int n) throws IOException;
    
    public String getPath(int n) throws IOException;
    
    public String[] getParents(int n) throws IOException;
    
    public String[] getLinkAspects(int n) throws IOException;
    
    public String getType(int n) throws IOException;
    
    public TermDocs getNodeDocs() throws IOException;
    
}
