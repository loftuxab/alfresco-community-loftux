/*
 * Created on Mar 24, 2005
 * 
 */
package org.alfresco.repo.search.impl.lucene;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import org.alfresco.repo.search.IndexerException;
import org.alfresco.repo.search.transaction.LuceneIndexLock;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

/**
 * Common support for abstracting the lucene indexer from its configuration and
 * management requirements.
 * 
 * <p>
 * This class defines where the indexes are stored. This should be via a
 * configurable Bean property in Spring.
 * 
 * <p>
 * The default file structure is
 * <ol>
 * <li><b>"base"/"protocol"/"name"/</b> for the main index
 * <li><b>"base"/"protocol"/"name"/deltas/"id"</b> for transactional
 * updates
 * <li><b>"base"/"protocol"/"name"/undo/"id"</b> undo information
 * </ol>
 * 
 * <p>
 * The IndexWriter and IndexReader for a given index are toggled (one should be
 * used for delete and the other for write). These are reused/closed/initialised
 * as required.
 * 
 * <p>
 * The index deltas are buffered to memory and persisted in the file system as
 * required.
 * 
 * @author andyh
 * 
 */

public abstract class LuceneBase implements Lockable
{
    /**
     * The base directory for the index (on file)
     */

    private Directory baseDir;

    /**
     * The directory for deltas (on file)
     */

    private Directory deltaDir;

    /**
     * The directory for undo information (on file)
     */

    private Directory undoDir;

    /**
     * The index reader for the on file delta. (This should no coexist with the
     * writer)
     */

    private IndexReader deltaReader;

    /**
     * The writer for the delta to file. (This should no coexist with the
     * reader)
     */

    private IndexWriter deltaWriter;

    /**
     * The writer for the main index. (This should no coexist with the reader)
     */

    private IndexWriter mainWriter;

    /*
     * TODO: The main indexer operations need to be serialised to the main index
     */

    /**
     * The reader for the main index. (This should no coexist with the writer)
     */

    private IndexReader mainReader;

    /**
     * The idetifier for the store
     */

    protected StoreRef store;

    /**
     * The identifier for the delta
     */

    protected String deltaId;

    private LuceneIndexLock luceneIndexLock;

    private String indexRootLocation = null; //File.separator + "lucene-indexes";

    /**
     * Initiase the configuration elements of the lucene store indexers and
     * searchers.
     * 
     * @param store
     * @param deltaId
     * @throws IOException
     */
    protected void initialise(StoreRef store, String deltaId, boolean createMain) throws IOException
    {
        this.store = store;
        this.deltaId = deltaId;

        String basePath = getMainPath();
        baseDir = initialiseFSDirectory(basePath, false, createMain);
        if (deltaId != null)
        {
            String deltaPath = getDeltaPath();
            deltaDir = initialiseFSDirectory(deltaPath, true, true);
            //undoDir = initialiseFSDirectory(basePath + File.separator + "undo" + File.separator + deltaId + File.separator, true, true);
        }
    }

    /**
     * Utility method to find the path to the transactional store for this index
     * delta
     * 
     * @return
     */
    private String getDeltaPath()
    {
        String deltaPath = getBasePath() + File.separator + "delta" + File.separator + this.deltaId + File.separator;
        return deltaPath;
    }

    private String getMainPath()
    {
        String mainPath = getBasePath() + File.separator + "index" + File.separator;
        return mainPath;
    }

    
    /**
     * Utility method to find the path to the base index
     * 
     * @return
     */
    private String getBasePath()
    {
        if(indexRootLocation == null)
        {
            throw new IndexerException("No configuration for index location");
        }
        String basePath = indexRootLocation + File.separator + store.getProtocol() + File.separator + store.getIdentifier() + File.separator;
        return basePath;
    }

    /**
     * Utility method to initiliase a lucene FSDirectorya at a given location.
     * We may try and delete the directory when the JVM exits.
     * 
     * @param path
     * @param temp
     * @return
     * @throws IOException
     */
    private Directory initialiseFSDirectory(String path, boolean deleteOnExit, boolean overwrite) throws IOException
    {
        File file = new File(path);
        if (overwrite)
        {
            //deleteDirectory(file);
        }
        if (!file.exists())
        {
            file.mkdirs();
            if (deleteOnExit)
            {
                file.deleteOnExit();
            }

            return FSDirectory.getDirectory(file, true);
        }
        else
        {
            return FSDirectory.getDirectory(file, overwrite);
        }
    }

    /**
     * Get a searcher for the main index TODO: Split out support for the main
     * index. We really only need this if we want to search over the changing
     * index before it is commited
     * 
     * @return
     * @throws IOException
     */

    protected IndexSearcher getSearcher() throws IOException
    {
        return new IndexSearcher(getMainPath());
    }

    /**
     * Get a reader for the on file portion of the delta
     * 
     * @return
     * @throws IOException
     */

    protected IndexReader getDeltaReader() throws IOException
    {
        if (deltaReader == null)
        {
            // Readers and writes can not exists at the same time so we swap
            // between them.
            closeDeltaWriter();

            if (!IndexReader.indexExists(deltaDir))
            {
                // Make sure there is something we can read
                IndexWriter writer = new IndexWriter(deltaDir, new LuceneAnalyser(dictionaryService), true);
                writer.setUseCompoundFile(true);
                writer.close();
            }
            deltaReader = IndexReader.open(deltaDir);

        }
        return deltaReader;
    }

    /**
     * Close the on file reader for the delta if it is open
     * 
     * @throws IOException
     */

    protected void closeDeltaReader() throws IOException
    {
        if (deltaReader != null)
        {
            try
            {
                deltaReader.close();
            }
            finally
            {
                deltaReader = null;
            }
        }

    }

    /**
     * Get the on file writer for the delta
     * 
     * @return
     * @throws IOException
     */
    protected IndexWriter getDeltaWriter() throws IOException
    {
        if (deltaWriter == null)
        {
            // Readers and writes can not exists at the same time so we swap
            // between them.
            closeDeltaReader();

            try
            {
                boolean create = !IndexReader.indexExists(deltaDir);
                deltaWriter = new IndexWriter(deltaDir, new LuceneAnalyser(dictionaryService), create);
            }
            catch (IOException e)
            {
                throw new IndexerException(e);
            }
        }
        deltaWriter.setUseCompoundFile(true);
        deltaWriter.minMergeDocs = 1000;
        deltaWriter.mergeFactor = 100;
        deltaWriter.maxMergeDocs = 100000;
        return deltaWriter;
    }

    /**
     * Close the on disk delta writer
     * 
     * @throws IOException
     */

    protected void closeDeltaWriter() throws IOException
    {
        if (deltaWriter != null)
        {
            try
            {
                //deltaWriter.optimize();
                deltaWriter.close();
            }
            finally
            {
                deltaWriter = null;
            }
        }

    }

    /**
     * Save the in memory delta to the disk, make sure there is nothing held in
     * memory
     * 
     * @throws IOException
     */
    protected void saveDelta() throws IOException
    {
        closeDeltaReader();
        closeDeltaWriter();
    }

    /**
     * Get all the locks so we can expect a merge to succeed
     * 
     * The delta should be thread local so we do not have to worry about
     * contentention TODO: Worry about main index contentention of readers and
     * writers @
     * @throws IOException
     */
    protected void prepareToMergeIntoMain() throws IOException
    {
        if (mainWriter != null)
        {
            throw new IndexerException("Can not merge as main writer is active");
        }
        if (mainReader != null)
        {
            throw new IndexerException("Can not merge as main reader is active");
        }

        getWriteLock();
        try
        {
            getDeltaReader(); // Flush any deletes
            closeDeltaReader();
        }
        catch (IOException e)
        {
            releaseWriteLock();
            throw e;
        }

    }

    /**
     * Merge the delta in the main index. The delta still exists on disk.
     * 
     * @param terms
     *            A list of terms that identifiy documents to be deleted from
     *            the main index before the delta os merged in.
     * 
     * @throws IOException
     */
    protected void mergeDeltaIntoMain(Set<Term> terms) throws IOException
    {

        if (!IndexReader.indexExists(baseDir))
        {
            mainWriter = new IndexWriter(baseDir, new LuceneAnalyser(dictionaryService), true);
            mainWriter.setUseCompoundFile(true);
            mainWriter.close();
        }
        mainReader = IndexReader.open(baseDir);
        try
        {
            // Do the deletions
            try
            {
                if ((mainReader.numDocs() > 0) && (terms.size() > 0))
                {
                    for (Term term : terms)
                    {
                        mainReader.delete(term);
                    }
                }
            }
            finally
            {
                try
                {
                    mainReader.close();
                }
                finally
                {
                    mainReader = null;
                }
            }

            // Do the append

            mainWriter = new IndexWriter(baseDir, new LuceneAnalyser(dictionaryService), false);
            mainWriter.setUseCompoundFile(true);

            mainWriter.minMergeDocs = 1000;
            mainWriter.mergeFactor = 1000;
            mainWriter.maxMergeDocs = 1000000;

            try
            {
                IndexReader reader = getDeltaReader();
                if (reader.numDocs() > 0)
                {
                    IndexReader[] readers = new IndexReader[] { reader };
                    Directory[] dirs = new Directory[] {deltaDir};
                    mainWriter.addIndexes(dirs);
                    //mainWriter.optimize();
                    closeDeltaReader();
                }
                else
                {
                    closeDeltaReader();
                }
            }
            finally
            {
                try
                {
                    mainWriter.close();
                }
                finally
                {
                    mainWriter = null;
                }
            }
        }
        finally
        {
            releaseWriteLock();
        }
    }

    /**
     * Delete the delta and make this instance unusable
     * 
     * This tries to tidy up all it can. It is possible some stuff will remain
     * if errors are throws else where
     * 
     * TODO: Support for cleaning up transactions - need to support recovery and
     * knowing of we are prepared
     * 
     */
    protected void deleteDelta()
    {
        try
        {
            // Try and close everything
            try
            {
                closeDeltaReader();
            }
            catch (IOException e)
            {

            }
            try
            {
                closeDeltaWriter();
            }
            catch (IOException e)
            {

            }
            try
            {
                deltaDir.close();
            }
            catch (IOException e1)
            {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            deltaDir = null;

            // Close the main stuff
            if (mainReader != null)
            {
                try
                {
                    mainReader.close();
                }
                catch (IOException e)
                {

                }
            }
            mainReader = null;

            if (mainWriter != null)
            {
                try
                {
                    mainWriter.close();
                }
                catch (IOException e)
                {

                }
            }
            mainWriter = null;
            try
            {
                baseDir.close();
            }
            catch (IOException e)
            {

            }

            // Delete the delta directories
            String deltaPath = getDeltaPath();
            File file = new File(deltaPath);

            deleteDirectory(file);
        }
        finally
        {
            releaseWriteLock();
        }
    }

    /**
     * Suport to help deleting directories
     * 
     * @param file
     */
    private void deleteDirectory(File file)
    {
        File[] children = file.listFiles();
        if (children != null)
        {
            for (int i = 0; i < children.length; i++)
            {
                File child = children[i];
                if (child.isDirectory())
                {
                    deleteDirectory(child);
                }
                else
                {
                    if (child.exists() && !child.delete() && child.exists())
                    {
                        throw new IllegalStateException("Failed to delete " + child);
                    }
                }
            }
        }
        if (file.exists() && !file.delete() && file.exists())
        {
            throw new IllegalStateException("Failed to delete " + file);
        }
    }

    public LuceneIndexLock getLuceneIndexLock()
    {
        return luceneIndexLock;
    }

    public void setLuceneIndexLock(LuceneIndexLock luceneIndexLock)
    {
        this.luceneIndexLock = luceneIndexLock;
    }

    public void getReadLock()
    {
        getLuceneIndexLock().getReadLock(store);
    }

    public void getWriteLock()
    {
        getLuceneIndexLock().getWriteLock(store);
        hasWriteLock = true;
    }

    public void releaseReadLock()
    {
        getLuceneIndexLock().releaseReadLock(store);
    }

    public void releaseWriteLock()
    {
        if (hasWriteLock)
        {
            getLuceneIndexLock().releaseWriteLock(store);
        }
        hasWriteLock = false;
    }

    boolean hasWriteLock = false;

    private DictionaryService dictionaryService;

    public boolean mainIndexExists()
    {
        try
        {
            return IndexReader.indexExists(baseDir);
        }
        catch (IOException e)
        {
            throw new IndexerException("Failed to determine if the index exists", e);
        }
    }

    public void clearIndex() throws IOException
    {
        getWriteLock();
        try
        {
            closeDeltaReader();
            closeDeltaWriter();
            if (mainWriter != null)
            {
                mainWriter.close();
                mainWriter = null;
            }
            if (mainReader != null)
            {
                mainReader.close();
                mainReader = null;
            }
            deltaDir.close();
            baseDir.close();
            initialise(store, deltaId, true);
        }
        finally
        {
            releaseWriteLock();
        }
    }

    protected IndexReader getReader() throws IOException
    {

        if (!IndexReader.indexExists(baseDir))
        {
            mainWriter = new IndexWriter(baseDir, new LuceneAnalyser(dictionaryService), true);
            mainWriter.setUseCompoundFile(true);
            mainWriter.close();
            mainWriter = null;
        }

        return IndexReader.open(baseDir);

    }
    
 

    public void setDictionaryService(DictionaryService dictionaryService)
    {
        this.dictionaryService = dictionaryService;
    }

    public DictionaryService getDictionaryService()
    {
        return dictionaryService;
    }

    public void setIndexRootLocation(String indexRootLocation)
    {
        this.indexRootLocation = indexRootLocation;
    }
    
    

}
