/*
 * Created on Mar 24, 2005
 * 
 */
package com.activiti.repo.search.impl.lucene;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.RAMDirectory;

import com.activiti.repo.ref.StoreRef;
import com.activiti.repo.search.IndexerException;

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
 * <li><b>/lucene-indexes/"protocol"/"name"/</b> for the main index
 * <li><b>/lucene-indexes/"protocol"/"name"/deltas/"id"</b> for transactional
 * updates
 * <li><b>/lucene-indexes/"protocol"/"name"/undo/"id"</b> undo information
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

public abstract class LuceneBase
{
    /*
     * TODO: Should make the delta directories etc on the fly so we do not build
     * them just to do a search
     */

    /**
     * The in memory index store
     */

    private Directory deltaRamDir;

    /**
     * The in memory undo store
     */

    private Directory undoRamDir;

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
     * The index reader for the in memory index delta . (This should no coexist
     * with the writer)
     */

    private IndexReader deltaRamReader;

    /**
     * The writer for the delta to file. (This should no coexist with the
     * reader)
     */

    private IndexWriter deltaWriter;

    /**
     * The writer for the delta to disk. (This should no coexist with the
     * reader)
     */

    private IndexWriter deltaRamWriter;

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

    /**
     * Initiase the configuration elements of the lucene store indexers and
     * searchers.
     * 
     * @param store
     * @param deltaId
     * @throws IOException
     */
    protected void initialise(StoreRef store, String deltaId) throws IOException
    {
        this.store = store;
        this.deltaId = deltaId;

        String basePath = getBasePath();
        baseDir = initialiseFSDirectory(basePath, false);
        if (deltaId != null)
        {
            String deltaPath = getDeltaPath();
            deltaDir = initialiseFSDirectory(deltaPath, true);
            undoDir = initialiseFSDirectory(basePath + File.separator + "undo" + File.separator + deltaId
                    + File.separator, true);
            deltaRamDir = new RAMDirectory();
            undoRamDir = new RAMDirectory();
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

    /**
     * Utility method to find the path to the base index
     * 
     * @return
     */
    private String getBasePath()
    {
        String basePath = File.separator + "lucene-indexes" + File.separator + store.getProtocol() + File.separator
                + store.getIdentifier() + File.separator;
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
    private Directory initialiseFSDirectory(String path, boolean deleteOnExit) throws IOException
    {
        File file = new File(path);
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
            return FSDirectory.getDirectory(file, false);
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
        return new IndexSearcher(baseDir);
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
                IndexWriter writer = new IndexWriter(deltaDir, new LuceneAnalyser(), true);
                writer.close();
            }
            deltaReader = IndexReader.open(deltaDir);

        }
        return deltaReader;
    }

    /**
     * Get a reader for the in memory portion of the delta
     * 
     * @return
     * @throws IOException
     */
    protected IndexReader getDeltaRamReader() throws IOException
    {
        if (deltaRamReader == null)
        {
            // Readers and writes can not exists at the same time so we swap
            // between them.
            closeDeltaRamWriter();

            if (!IndexReader.indexExists(deltaRamDir))
            {
                // Make sure there is something we can read.
                IndexWriter writer = new IndexWriter(deltaRamDir, new LuceneAnalyser(), true);
                writer.close();
            }
            deltaRamReader = IndexReader.open(deltaRamDir);

        }
        return deltaRamReader;
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
     * Close the in memory reader for the delta if it is open
     * 
     * @throws IOException
     */
    protected void closeDeltaRamReader() throws IOException
    {
        if (deltaRamReader != null)
        {
            try
            {
                deltaRamReader.close();
            }
            finally
            {
                deltaRamReader = null;
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
                deltaWriter = new IndexWriter(deltaDir, new LuceneAnalyser(), false);
            }
            catch (IOException e)
            {
                throw new IndexerException(e);
            }
        }
        deltaWriter.setUseCompoundFile(true);
        return deltaWriter;
    }

    /**
     * Get the in memory writer
     * 
     * @return
     * @throws IOException
     */

    protected IndexWriter getDeltaRamWriter() throws IOException
    {
        return getDeltaRamWriter(false);
    }

    /**
     * Get the in memory writer. Provides support to clear the in memory writer.
     * 
     * @param overwriteOrCreate
     * @return
     * @throws IOException
     */
    private IndexWriter getDeltaRamWriter(boolean overwriteOrCreate) throws IOException
    {
        if (deltaRamWriter == null)
        {
            // Readers and writes can not exists at the same time so we swap
            // between them.
            closeDeltaRamReader();

            try
            {
                deltaRamWriter = new IndexWriter(deltaRamDir, new LuceneAnalyser(), overwriteOrCreate);
            }
            catch (IOException e)
            {
                throw new IndexerException(e);
            }
        }
        deltaRamWriter.setUseCompoundFile(true);
        return deltaRamWriter;
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
                deltaWriter.close();
            }
            finally
            {
                deltaWriter = null;
            }
        }

    }

    /**
     * Close the in memory deltya writer
     * 
     * @throws IOException
     */

    protected void closeDeltaRamWriter() throws IOException
    {
        if (deltaRamWriter != null)
        {
            try
            {
                deltaRamWriter.close();
            }
            finally
            {
                deltaRamWriter = null;
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
        // Make sure everything is flushed
        closeDeltaReader();
        closeDeltaRamReader();
        closeDeltaWriter();
        closeDeltaRamWriter();
        // Append in memory to the disk
        IndexWriter writer = getDeltaWriter();
        IndexReader[] readers = new IndexReader[] { getDeltaRamReader() };
        writer.addIndexes(readers);
        // Make sure everything is flushed
        closeDeltaRamReader();
        closeDeltaWriter();
        // Clear the in memory index buffer
        getDeltaRamWriter(true);
        closeDeltaRamWriter();
    }

    /**
     * Merge the in memory delat to file if it contains a given number of
     * documents. TODO: This parameter should be tunable
     * 
     * @param size
     * @throws IOException
     */
    protected void chechAndMergeToDisk(int size) throws IOException
    {
        IndexReader reader = getDeltaRamReader();
        if (reader.numDocs() > size)
        {
            saveDelta();
        }
        else
        {
            // Leave open the redaer for potential reuse
        }
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
        chechAndMergeToDisk(0);
        if (mainWriter != null)
        {
            throw new IndexerException("Can not merge as main writer is active");
        }
        if (mainReader != null)
        {
            throw new IndexerException("Can not merge as main reader is active");
        }

        getDeltaReader();
        // Must have the read lock;

        if (IndexReader.indexExists(baseDir))
        {
            mainReader = IndexReader.open(baseDir);
            // If exists lock for deletes
        }
        else
        {
            // Create the main index
            mainWriter = new IndexWriter(baseDir, new LuceneAnalyser(), true);
            try
            {
                mainWriter.close();
            }
            finally
            {
                mainWriter = null;
            }
            // Lock the reader
            mainReader = IndexReader.open(baseDir);
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
        if (mainReader == null)
        {
            throw new IOException("No main index reader lock - not prepared");
        }

        // Do the deletions
        try
        {
            if (terms.size() > 0)
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
        mainWriter = new IndexWriter(baseDir, new LuceneAnalyser(), false);

        try
        {

            IndexReader reader = getDeltaReader();
            IndexReader[] readers = new IndexReader[] { reader };
            mainWriter.addIndexes(readers);
            closeDeltaReader();
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
            closeDeltaRamReader();
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
            closeDeltaRamWriter();
        }
        catch (IOException e)
        {

        }

        // Set all to null so we can not get confused
        // We could just reset here

        deltaRamDir = null;
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

        // Delete the delta directories
        String deltaPath = getDeltaPath();
        File file = new File(deltaPath);

        deleteDirectory(file);
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
                    child.delete();
                }
            }
        }
        file.delete();
    }
}
