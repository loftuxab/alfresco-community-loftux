/*
 * Created on Mar 24, 2005
 * 
 */
package org.alfresco.repo.search.impl.lucene;

import java.io.IOException;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.transaction.Status;
import javax.transaction.xa.XAResource;

import org.alfresco.repo.dictionary.ClassRef;
import org.alfresco.repo.dictionary.DictionaryRef;
import org.alfresco.repo.dictionary.PropertyDefinition;
import org.alfresco.repo.dictionary.PropertyTypeDefinition;
import org.alfresco.repo.dictionary.TypeDefinition;
import org.alfresco.repo.node.NodeService;
import org.alfresco.repo.ref.ChildAssocRef;
import org.alfresco.repo.ref.NodeRef;
import org.alfresco.repo.ref.Path;
import org.alfresco.repo.ref.QName;
import org.alfresco.repo.ref.StoreRef;
import org.alfresco.repo.search.IndexerException;
import org.alfresco.repo.search.ResultSetRow;
import org.alfresco.repo.search.impl.lucene.fts.FTSIndexerAware;
import org.alfresco.repo.search.impl.lucene.fts.FullTextSearchIndexer;
import org.alfresco.repo.value.ValueConverter;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermDocs;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Searcher;
import org.apache.lucene.search.TermQuery;

import com.vladium.utils.timing.ITimer;
import com.vladium.utils.timing.TimerFactory;

/**
 * The implementation of the lucene based indexer. Supports basic transactional
 * behaviour if used on its own.
 * 
 * @author andyh
 * 
 */
public class LuceneIndexerImpl extends LuceneBase implements LuceneIndexer
{
    private enum Action {
        INDEX, REINDEX, DELETE
    };

    /**
     * The node service we use to get information about nodes
     */
    private NodeService nodeService;

    /**
     * A list of all deletoins we have made - at merge these deletions need to
     * be made against the main index.
     * 
     * TODO: Consider if this informantion needs to be persisted for recovery
     */

    private Set<NodeRef> deletions = new LinkedHashSet<NodeRef>();

    /**
     * A list of all nodes we have altered This list is used to drive the
     * background full text seach index which is to time consuming to do as part
     * of the transaction. The commit of the list of nodes to reindex is done as
     * part of the transaction.
     * 
     * TODO: Condsider persistence and recovery
     */

    private Set<NodeRef> fts = new LinkedHashSet<NodeRef>();

    /**
     * The status of this index - follows javax.transaction.Status
     */

    private int status = Status.STATUS_UNKNOWN;

    /**
     * Has this index been modified?
     */

    private boolean isModified = false;

    private Boolean isFTSUpdate = null;

    DecimalFormat format;

    ITimer timer;

    /**
     * Setter for getting the node service via IOC Used in the Spring container
     * 
     * @param nodeService
     */

    public void setNodeService(NodeService nodeService)
    {
        this.nodeService = nodeService;

        timer = TimerFactory.newTimer();

        format = new DecimalFormat();
        format.setMinimumFractionDigits(3);
        format.setMaximumFractionDigits(3);

    }
    
    /*
     * Indexer Implementation
     */

    /**
     * Utility method to check we are in the correct state to do work Also keeps
     * track of the dirty flag.
     * 
     */

    private void checkAbleToDoWork(boolean isFTS)
    {
        if (isFTSUpdate == null)
        {
            isFTSUpdate = Boolean.valueOf(isFTS);
        }
        else
        {
            if (isFTS != isFTSUpdate.booleanValue())
            {
                throw new IndexerException("Can not mix FTS and transactional updates");
            }
        }

        switch (status)
        {
        case Status.STATUS_UNKNOWN:
            status = Status.STATUS_ACTIVE;
            break;
        case Status.STATUS_ACTIVE:
            // OK
            break;
        default:
            // All other states are a problem
            throw new IndexerException(buildErrorString());
        }
        isModified = true;
    }

    /**
     * Utility method to report errors about invalid state.
     * 
     * @return
     */
    private String buildErrorString()
    {
        StringBuilder buffer = new StringBuilder(128);
        buffer.append("The indexer is unable to accept more work: ");
        switch (status)
        {
        case Status.STATUS_COMMITTED:
            buffer.append("The indexer has been committed");
            break;
        case Status.STATUS_COMMITTING:
            buffer.append("The indexer is committing");
            break;
        case Status.STATUS_MARKED_ROLLBACK:
            buffer.append("The indexer is marked for rollback");
            break;
        case Status.STATUS_PREPARED:
            buffer.append("The indexer is prepared to commit");
            break;
        case Status.STATUS_PREPARING:
            buffer.append("The indexer is preparing to commit");
            break;
        case Status.STATUS_ROLLEDBACK:
            buffer.append("The indexer has been rolled back");
            break;
        case Status.STATUS_ROLLING_BACK:
            buffer.append("The indexer is rolling back");
            break;
        case Status.STATUS_UNKNOWN:
            buffer.append("The indexer is in an unknown state");
            break;
        default:
            break;
        }
        return buffer.toString();
    }

    /*
     * Indexer Implementation
     */

    public void createNode(ChildAssocRef relationshipRef) throws IndexerException
    {
        checkAbleToDoWork(false);
        try
        {
            if (relationshipRef.getParentRef() != null)
            {
                // reindex(relationshipRef.getParentRef());
            }
            reindex(relationshipRef.getChildRef());
        }
        catch (IOException e)
        {
            setRollbackOnly();
            throw new IndexerException(e);
        }
    }

    public void updateNode(NodeRef nodeRef) throws IndexerException
    {
        checkAbleToDoWork(false);
        try
        {
            reindex(nodeRef);
        }
        catch (IOException e)
        {
            setRollbackOnly();
            throw new IndexerException(e);
        }
    }

    public void deleteNode(ChildAssocRef relationshipRef) throws IndexerException
    {
        checkAbleToDoWork(false);
        try
        {
            delete(relationshipRef.getChildRef(), false);
        }
        catch (IOException e)
        {
            setRollbackOnly();
            throw new IndexerException(e);
        }
    }

    public void createChildRelationship(ChildAssocRef relationshipRef) throws IndexerException
    {
        checkAbleToDoWork(false);
        try
        {
            // TODO: Optimise
            // reindex(relationshipRef.getParentRef());
            reindex(relationshipRef.getChildRef());
        }
        catch (IOException e)
        {
            setRollbackOnly();
            throw new IndexerException(e);
        }
    }

    public void updateChildRelationship(ChildAssocRef relationshipBeforeRef, ChildAssocRef relationshipAfterRef) throws IndexerException
    {
        checkAbleToDoWork(false);
        try
        {
            // TODO: Optimise
            if (relationshipBeforeRef.getParentRef() != null)
            {
                // reindex(relationshipBeforeRef.getParentRef());
            }
            reindex(relationshipBeforeRef.getChildRef());
        }
        catch (IOException e)
        {
            setRollbackOnly();
            throw new IndexerException(e);
        }
    }

    public void deleteChildRelationship(ChildAssocRef relationshipRef) throws IndexerException
    {
        checkAbleToDoWork(false);
        try
        {
            // TODO: Optimise
            if (relationshipRef.getParentRef() != null)
            {
                // reindex(relationshipRef.getParentRef());
            }
            reindex(relationshipRef.getChildRef());
        }
        catch (IOException e)
        {
            setRollbackOnly();
            throw new IndexerException(e);
        }
    }

    /**
     * Generate an indexer
     * 
     * @param storeRef
     * @param deltaId
     * @return
     */
    public static LuceneIndexerImpl getUpdateIndexer(StoreRef storeRef, String deltaId)
    {
        LuceneIndexerImpl indexer = new LuceneIndexerImpl();
        try
        {
            indexer.initialise(storeRef, deltaId, false);
        }
        catch (IOException e)
        {
            throw new IndexerException(e);
        }
        return indexer;
    }

    /*
     * Transactional support Used by the resource mananger for indexers.
     */

    /**
     * Commit this index
     */

    public void commit()
    {
        switch (status)
        {
        case Status.STATUS_COMMITTING:
            throw new IndexerException("Unable to commit: Transaction is committing");
        case Status.STATUS_COMMITTED:
            throw new IndexerException("Unable to commit: Transaction is commited ");
        case Status.STATUS_ROLLING_BACK:
            throw new IndexerException("Unable to commit: Transaction is rolling back");
        case Status.STATUS_ROLLEDBACK:
            throw new IndexerException("Unable to commit: Transaction is aleady rolled back");
        case Status.STATUS_MARKED_ROLLBACK:
            throw new IndexerException("Unable to commit: Transaction is marked for roll back");
        case Status.STATUS_PREPARING:
            throw new IndexerException("Unable to commit: Transaction is preparing");
        case Status.STATUS_ACTIVE:
            // special case - commit from active
            prepare();
        // drop through to do the commit;
        default:
            status = Status.STATUS_COMMITTING;
            try
            {
                if (isModified())
                {
                    if (isFTSUpdate.booleanValue())
                    {
                        doFTSIndexCommit();
                        // FTS does not trigger indexing request
                    }
                    else
                    {
                        // Build the deletion terms
                        Set<Term> terms = new LinkedHashSet<Term>();
                        for (NodeRef nodeRef : deletions)
                        {
                            terms.add(new Term("ID", nodeRef.getId()));
                        }
                        // Merge
                        mergeDeltaIntoMain(terms);
                        luceneFullTextSearchIndexer.requiresIndex(store);
                    }
                }
                status = Status.STATUS_COMMITTED;
                if (callBack != null)
                {
                    callBack.indexCompleted(store, remainingCount, null);
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
                // If anything goes wrong we try and do a roll back
                rollback();
                throw new IndexerException(e);
            }
            finally
            {
                // Make sure we tidy up
                deleteDelta();
            }
            break;
        }
    }

    private void doFTSIndexCommit() throws IOException
    {
        IndexReader mainReader = getReader();
        IndexReader deltaReader = getDeltaReader();

        IndexSearcher mainSearcher = new IndexSearcher(mainReader);
        IndexSearcher deltaSearcher = new IndexSearcher(deltaReader);

        for (Helper helper : toFTSIndex)
        {
            BooleanQuery query = new BooleanQuery();
            query.add(new TermQuery(new Term("ID", helper.document.getField("ID").stringValue())), true, false);
            query.add(new TermQuery(new Term("TX", helper.document.getField("TX").stringValue())), true, false);

            Hits hits = mainSearcher.search(query);
            if (hits.length() > 0)
            {
                // No change
                for (int i = 0; i < hits.length(); i++)
                {
                    mainReader.delete(hits.id(i));
                }
            }
            else
            {
                hits = deltaSearcher.search(query);
                for (int i = 0; i < hits.length(); i++)
                {
                    deltaReader.delete(hits.id(i));
                }
            }
        }

        deltaSearcher.close();
        mainSearcher.close();
        closeDeltaReader();
        mainReader.close();

        mergeDeltaIntoMain(new LinkedHashSet<Term>());

    }

    /**
     * Prepare to commit
     * 
     * At the moment this makes sure we have all the locks
     * 
     * TODO: This is not doing proper serialisation against the index as would a
     * data base transaction.
     * 
     * @return
     */
    public int prepare()
    {

        switch (status)
        {
        case Status.STATUS_COMMITTING:
            throw new IndexerException("Unable to prepare: Transaction is committing");
        case Status.STATUS_COMMITTED:
            throw new IndexerException("Unable to prepare: Transaction is commited ");
        case Status.STATUS_ROLLING_BACK:
            throw new IndexerException("Unable to prepare: Transaction is rolling back");
        case Status.STATUS_ROLLEDBACK:
            throw new IndexerException("Unable to prepare: Transaction is aleady rolled back");
        case Status.STATUS_MARKED_ROLLBACK:
            throw new IndexerException("Unable to prepare: Transaction is marked for roll back");
        case Status.STATUS_PREPARING:
            throw new IndexerException("Unable to prepare: Transaction is already preparing");
        case Status.STATUS_PREPARED:
            throw new IndexerException("Unable to prepare: Transaction is already prepared");
        default:
            status = Status.STATUS_PREPARING;
            try
            {
                if (isModified())
                {
                    saveDelta();
                    flushPending();
                    prepareToMergeIntoMain();
                }
                status = Status.STATUS_PREPARED;
                return isModified ? XAResource.XA_OK : XAResource.XA_RDONLY;
            }
            catch (IOException e)
            {
                setRollbackOnly();
                throw new IndexerException(e);
            }
        }
    }

    /**
     * Has this index been modified?
     * 
     * @return
     */
    public boolean isModified()
    {
        return isModified;
    }

    /**
     * Return the javax.transaction.Status integer status code
     * 
     * @return
     */
    public int getStatus()
    {
        return status;
    }

    /**
     * Roll back the index changes (this just means they are never added)
     * 
     */

    public void rollback()
    {
        switch (status)
        {

        case Status.STATUS_COMMITTED:
            throw new IndexerException("Unable to roll back: Transaction is commited ");
        case Status.STATUS_ROLLING_BACK:
            throw new IndexerException("Unable to roll back: Transaction is rolling back");
        case Status.STATUS_ROLLEDBACK:
            throw new IndexerException("Unable to roll back: Transaction is aleady rolled back");
        case Status.STATUS_COMMITTING:
        // Can roll back during commit
        default:
            status = Status.STATUS_ROLLING_BACK;
            if (isModified())
            {
                deleteDelta();
            }
            status = Status.STATUS_ROLLEDBACK;
            if (callBack != null)
            {
                callBack.indexCompleted(store, 0, null);
            }
            break;
        }
    }

    /**
     * Mark this index for roll back only. This action can not be reversed. It
     * will reject all other work and only allow roll back.
     * 
     */

    public void setRollbackOnly()
    {
        switch (status)
        {
        case Status.STATUS_COMMITTING:
            throw new IndexerException("Unable to mark for rollback: Transaction is committing");
        case Status.STATUS_COMMITTED:
            throw new IndexerException("Unable to mark for rollback: Transaction is commited");
        default:
            status = Status.STATUS_MARKED_ROLLBACK;
            break;
        }
    }

    /*
     * Implementation
     */

    private void reindex(NodeRef nodeRef) throws IOException
    {

        delete(nodeRef, true);
        // index(nodeRef, false);
    }

    private List<Command> deltaDeletes = new ArrayList<Command>(10000);

    private FTSIndexerAware callBack;
    
    private int remainingCount = 0;

    private ArrayList<Helper> toFTSIndex = new ArrayList<Helper>();

    private void delete(NodeRef nodeRef, boolean forReindex) throws IOException
    {

        deltaDeletes.add(new Command(nodeRef, forReindex ? Action.REINDEX : Action.DELETE));

        if (deltaDeletes.size() > 10000)
        {
            flushPending();
        }
    }

    private void flushPending() throws IOException
    {
        Set<NodeRef> forIndex = new LinkedHashSet<NodeRef>();
        for (Command command : deltaDeletes)
        {
            if (command.action == Action.REINDEX)
            {
                Set<NodeRef> set = deleteImpl(command.nodeRef, true);
                forIndex.removeAll(set);
                forIndex.addAll(set);
            }
            else if (command.action == Action.DELETE)
            {
                deleteImpl(command.nodeRef, false);
            }
        }
        deltaDeletes.clear();
        index(forIndex, false);
    }

    private Set<NodeRef> deleteImpl(NodeRef nodeRef, boolean forReindex) throws IOException
    {
        // startTimer();
        getDeltaReader();
        // outputTime("Delete "+nodeRef+" size = "+getDeltaWriter().docCount());
        Set<NodeRef> refs = new LinkedHashSet<NodeRef>();

        IndexReader mainReader = getReader();
        try
        {
            refs.addAll(deleteContainerAndBelow(nodeRef, getDeltaReader(), true));

            refs.addAll(deleteContainerAndBelow(nodeRef, mainReader, false));

            if (!forReindex)
            {
                Set<NodeRef> leafrefs = new LinkedHashSet<NodeRef>();

                leafrefs.addAll(deletePrimary(refs, getDeltaReader(), true));
                leafrefs.addAll(deletePrimary(refs, mainReader, false));

                leafrefs.addAll(deleteReference(refs, getDeltaReader(), true));
                leafrefs.addAll(deleteReference(refs, mainReader, false));

                refs.addAll(leafrefs);
            }

            deletions.addAll(refs);

            return refs;
        }
        finally
        {
            mainReader.close();
        }
    }

    private Set<NodeRef> deletePrimary(Collection<NodeRef> nodeRefs, IndexReader reader, boolean delete) throws IOException
    {

        Set<NodeRef> refs = new LinkedHashSet<NodeRef>();

        for (NodeRef nodeRef : nodeRefs)
        {

            TermDocs td = reader.termDocs(new Term("PRIMARYPARENT", nodeRef.getId()));
            while (td.next())
            {
                int doc = td.doc();
                Document document = reader.document(doc);
                String id = document.get("ID");
                NodeRef ref = new NodeRef(store, id);
                refs.add(ref);
                if (delete)
                {
                    reader.delete(doc);
                }
            }
        }

        return refs;

    }

    private Set<NodeRef> deleteReference(Collection<NodeRef> nodeRefs, IndexReader reader, boolean delete) throws IOException
    {

        Set<NodeRef> refs = new LinkedHashSet<NodeRef>();

        for (NodeRef nodeRef : nodeRefs)
        {

            TermDocs td = reader.termDocs(new Term("PARENT", nodeRef.getId()));
            while (td.next())
            {
                int doc = td.doc();
                Document document = reader.document(doc);
                String id = document.get("ID");
                NodeRef ref = new NodeRef(store, id);
                refs.add(ref);
                if (delete)
                {
                    reader.delete(doc);
                }
            }
        }

        return refs;

    }

    private Set<NodeRef> deleteContainerAndBelow(NodeRef nodeRef, IndexReader reader, boolean delete) throws IOException
    {
        Set<NodeRef> refs = new LinkedHashSet<NodeRef>();

        if (delete)
        {
            int count = reader.delete(new Term("ID", nodeRef.getId()));
        }
        refs.add(nodeRef);

        TermDocs td = reader.termDocs(new Term("ANCESTOR", nodeRef.getId()));
        while (td.next())
        {
            int doc = td.doc();
            Document document = reader.document(doc);
            String id = document.get("ID");
            NodeRef ref = new NodeRef(store, id);
            refs.add(ref);
            if (delete)
            {
                reader.delete(doc);
            }
        }
        return refs;
    }

    private void index(Set<NodeRef> nodeRefs, boolean isNew) throws IOException
    {
        // Directory temp = new RAMDirectory();
        // IndexWriter localWriter = new IndexWriter(temp, new LuceneAnalyser(),
        // true);
        // IndexWriter localWriter = getDeltaWriter();
        for (NodeRef ref : nodeRefs)
        {
            index(ref, isNew);
        }
        // localWriter.close();
        // IndexWriter writer = getDeltaRamWriter();
        // writer.addIndexes(new Directory[] {temp});
        // chechAndMergeToDisk(100);
    }

    private void index(NodeRef nodeRef, boolean isNew) throws IOException
    {
        IndexWriter writer = getDeltaWriter();

        // avoid attempting to index nodes that don't exist
        if (!nodeService.exists(nodeRef))
        {
            return;
        }
        List<Document> docs = createDocuments(nodeRef, isNew);
        for (Document doc : docs)
        {
            writer.addDocument(doc /* TODO: Select the language based analyser */);
        }

    }

    static class Counter
    {
        int countInParent = 0;

        int count = -1;

        int getCountInParent()
        {
            return countInParent;
        }

        int getRepeat()
        {
            return (count / countInParent) + 1;
        }

        void incrementParentCount()
        {
            countInParent++;
        }

        void increment()
        {
            count++;
        }

    }

    private List<Document> createDocuments(NodeRef nodeRef, boolean isNew)
    {
        // Create mutiple containers
        // For each node and directory create a copy for each parent in which it
        // occurs

        ClassRef nodeTypeRef = nodeService.getType(nodeRef);

        Map<ChildAssocRef, Counter> nodeCounts = new HashMap<ChildAssocRef, Counter>(5);

        List<Document> docs = new ArrayList<Document>();

        ChildAssocRef qNameRef = null;

        Map<QName, Serializable> properties = nodeService.getProperties(nodeRef);

        Collection<Path> paths = nodeService.getPaths(nodeRef, false);

        Set<NodeRef> parentSet = new LinkedHashSet<NodeRef>();
        List<ChildAssocRef> parentAssocs = nodeService.getParentAssocs(nodeRef);
        // count the number of times the association is duplicated
        for (ChildAssocRef assoc : parentAssocs)
        {
            Counter counter = nodeCounts.get(assoc);
            if (counter == null)
            {
                counter = new Counter();
                nodeCounts.put(assoc, counter);
            }
            counter.incrementParentCount();

        }

        StringBuffer aspectBuffer = new StringBuffer();
        for(ClassRef classRef : nodeService.getAspects(nodeRef))
        {
            if(aspectBuffer.length() > 0)
            {
                aspectBuffer.append(";");
            }
            aspectBuffer.append(classRef.getQName().toString());
        }
        
        int containerCount = 0;
        for (Iterator<Path> it = paths.iterator(); it.hasNext(); /**/)
        {

            Path path = it.next();

            // Lucene flags in order are: Stored, indexed, tokenised
            // ID
            Document doc = new Document();
            doc.add(new Field("ID", nodeRef.getId(), true, true, false));

            // Properties

            boolean isAtomic = true;
            for (QName propertyQName : properties.keySet())
            {
                boolean store = true;
                boolean index = true;
                boolean tokenise = true;
                boolean atomic = true;
                // TODO: - should be able to get the property by its QName?

                PropertyDefinition propertyDefinition = getDictionaryService().getProperty(propertyQName);
                PropertyTypeDefinition propertyType = getDictionaryService().getPropertyType(new DictionaryRef(PropertyTypeDefinition.TEXT));
                if (propertyDefinition != null)
                {
                    index = propertyDefinition.isIndexed();
                    store = propertyDefinition.isStoredInIndex();
                    tokenise = propertyDefinition.isTokenisedInIndex();
                    atomic = propertyDefinition.isIndexedAtomically();
                }

                isAtomic &= atomic;

                // PropertyTypeDefinition propDef = null;
                Serializable value = properties.get(propertyQName);
                // convert value to String
                String strValue = ValueConverter.convert(String.class, value);

                // TODO: Need converter here
                // Conversion should be done in the anlyser as we may take
                // advantage of tokenisation

                // Need to add with the correct language based analyser
                if (index && atomic)
                {
                    doc.add(new Field("@" + propertyQName, strValue, store, index, tokenise));
                }

            }

            // Paths

            StringBuilder qNameBuffer = new StringBuilder();
            StringBuilder pathBuffer = new StringBuilder();
            StringBuilder parentBuffer = new StringBuilder();

            ArrayList<NodeRef> parentsInDepthOrderStartingWithSelf = new ArrayList<NodeRef>();

            int pathLength = 0;
            for (Iterator<Path.Element> elit = path.iterator(); elit.hasNext(); /**/)
            {
                Path.Element element = elit.next();
                if (!(element instanceof Path.ChildAssocElement))
                {
                    throw new IndexerException("Confused path: " + path);
                }
                Path.ChildAssocElement cae = (Path.ChildAssocElement) element;
                parentsInDepthOrderStartingWithSelf.add(0, cae.getRef().getChildRef());
                if (!elit.hasNext())
                {
                    if (cae.getRef().getQName() != null)
                    {
                        qNameBuffer.append(cae.getRef().getQName().toString());
                    }
                    if (cae.getRef().getParentRef() != null)
                    {
                        doc.add(new Field("PARENT", cae.getRef().getParentRef().getId(), true, true, false));
                    }
                    qNameRef = cae.getRef();
                }

                if (pathBuffer.length() > 0)
                {
                    pathBuffer.append("/");
                }
                if (cae.getRef().getQName() != null)
                {
                    pathBuffer.append(cae.getRef().getQName().toString());
                }
                pathLength++;
            }

            for (NodeRef ref : parentsInDepthOrderStartingWithSelf)
            {
                if (parentBuffer.length() > 0)
                {
                    parentBuffer.append(" ");
                }
                parentBuffer.append(ref.getId());
            }

            parentsInDepthOrderStartingWithSelf.clear();

            // Root Node
            if (nodeRef.equals(nodeService.getRootNode(nodeRef.getStoreRef())))
            {
                // TODO: Does the root element have a QName?
                doc.add(new Field("ISCONTAINER", "T", true, true, false));
                doc.add(new Field("PATH", ";", true, true, true));
                doc.add(new Field("ISROOT", "T", true, true, false));
                doc.add(new Field("ISNODE", "T", true, true, false));
                docs.add(doc);
            }
            else if (pathLength == 1)
            {
                // Pseudo root node ignore
            }
            else
            // not a root node
            {
                doc.add(new Field("QNAME", qNameBuffer.toString(), true, true, true));

                TypeDefinition nodeTypeDef = getDictionaryService().getType(nodeTypeRef);
                // check for child associations
                if (nodeTypeDef.getChildAssociations().size() > 0)
                {
                    Document directoryEntry = new Document();
                    directoryEntry.add(new Field("ID", nodeRef.getId(), true, true, false));
                    directoryEntry.add(new Field("PATH", pathBuffer.toString(), true, true, true));
                    directoryEntry.add(new Field("ANCESTOR", parentBuffer.toString(), true, true, true));
                    directoryEntry.add(new Field("ISCONTAINER", "T", true, true, false));
                    docs.add(directoryEntry);
                }

                doc.add(new Field("PRIMARYPARENT", nodeService.getPrimaryParent(nodeRef).getParentRef().getId(), true, true, false));
                doc.add(new Field("TYPE", nodeService.getType(nodeRef).getQName().toString(), true, true, true));
                doc.add(new Field("ASPECT", aspectBuffer.toString(), true, true, true));

                Counter counter = nodeCounts.get(qNameRef);
                // If we have something in a container with root aspect we will
                // not find it
                if (counter != null)
                {
                    counter.increment();
                }

                doc.add(new Field("ISROOT", "F", true, true, false));
                doc.add(new Field("ISNODE", "T", true, true, false));
                if (isAtomic)
                {
                    doc.add(new Field("FTSSTATUS", "Clean", true, true, false)); 
                }   
                else
                {
                    if (isNew)
                    {
                        doc.add(new Field("FTSSTATUS", "New", true, true, false));
                    }
                    else
                    {
                        doc.add(new Field("FTSSTATUS", "Dirty", true, true, false));
                    }
                }
                doc.add(new Field("TX", deltaId, true, true, false));

                if ((counter == null) || counter.getRepeat() == 1)
                {
                    docs.add(doc);
                }
            }
        }

        return docs;
    }

    public void startTimer()
    {
        timer.reset();
        timer.start();
    }

    public void outputTime(String message)
    {
        timer.stop();
        System.out.println(message + " in " + format.format(timer.getDuration()));
    }

    public void clearIndex()
    {
        try
        {
            super.clearIndex();
        }
        catch (IOException e)
        {
            setRollbackOnly();
            throw new IndexerException(e);
        }
    }

    public void updateFullTextSearch(int size)
    {
        checkAbleToDoWork(true);
        try
        {
            String lastId = null;

            toFTSIndex = new ArrayList<Helper>(size);
            BooleanQuery booleanQuery = new BooleanQuery();
            booleanQuery.add(new TermQuery(new Term("FTSSTATUS", "Dirty")), false, false);
            booleanQuery.add(new TermQuery(new Term("FTSSTATUS", "New")), false, false);

            Searcher searcher = getSearcher();
            Hits hits = searcher.search(booleanQuery);
            LuceneResultSet results = new LuceneResultSet(store, hits, searcher, nodeService);
            int count = 0;
            for (ResultSetRow row : results)
            {
                LuceneResultSetRow lrow = (LuceneResultSetRow) row;
                Helper helper = new Helper(lrow.getNodeRef(), lrow.getDocument(), lrow.getIndex());
                toFTSIndex.add(helper);
                if (++count >= size)
                {
                    break;
                }
            }
            count = results.length();
            results.close();

            IndexWriter writer = getDeltaWriter();
            for (Helper helper : toFTSIndex)
            {
                Document document = helper.document;
                NodeRef ref = helper.nodeRef;
                ClassRef nodeTypeRef = nodeService.getType(ref);

                Map<QName, Serializable> properties = nodeService.getProperties(ref);

                for (QName propertyQName : properties.keySet())
                {
                    boolean store = true;
                    boolean index = true;
                    boolean tokenise = true;
                    boolean atomic = true;
                    // TODO: - should be able to get the property by its QName?

                    PropertyDefinition propertyDefinition = getDictionaryService().getProperty(propertyQName);
                    PropertyTypeDefinition propertyType = getDictionaryService().getPropertyType(new DictionaryRef(PropertyTypeDefinition.TEXT));
                    if (propertyDefinition != null)
                    {
                        index = propertyDefinition.isIndexed();
                        store = propertyDefinition.isStoredInIndex();
                        tokenise = propertyDefinition.isTokenisedInIndex();
                        atomic = propertyDefinition.isIndexedAtomically();
                    }

                    Serializable value = properties.get(propertyQName);
                    // convert value to String
                    String strValue = value.toString();

                    // TODO: Need converter here
                    // Conversion should be done in the anlyser as we may take
                    // advantage of tokenisation

                    // Need to add with the correct language based analyser
                    if (index && !atomic)
                    {
                        String fieldName = "@" + propertyQName;
                        document.removeFields(fieldName);
                        document.add(new Field(fieldName, strValue, store, index, tokenise));
                    }

                }

                document.removeField("FTSSTATUS");
                document.add(new Field("FTSSTATUS", "Clean", true, true, false));

                writer.addDocument(document /*
                                             * TODO: Select the language based
                                             * analyser
                                             */);

                // Need to do all the current id in the TX - should all be
                // together so skip until id changes
                if (writer.docCount() > size)
                {
                    String id = document.getField("ID").stringValue();
                    if (lastId == null)
                    {
                        lastId = id;
                    }
                    if (!lastId.equals(id))
                    {
                        break;
                    }
                }
            }

            remainingCount = count - writer.docCount();
            
        }
        catch (IOException e)
        {
            setRollbackOnly();
            throw new IndexerException(e);
        }
    }

    public void registerCallBack(FTSIndexerAware callBack)
    {
        this.callBack = callBack;
    }

    private static class Helper
    {
        NodeRef nodeRef;

        Document document;

        int index;

        boolean update = false;

        Helper(NodeRef nodeRef, Document document, int index)
        {
            this.nodeRef = nodeRef;
            this.document = document;
            this.index = index;
        }
    }

    private static class Command
    {
        NodeRef nodeRef;

        Action action;

        Command(NodeRef nodeRef, Action action)
        {
            this.nodeRef = nodeRef;
            this.action = action;
        }
    }
    
    private FullTextSearchIndexer luceneFullTextSearchIndexer;
    
    public void setLuceneFullTextSearchIndexer(FullTextSearchIndexer luceneFullTextSearchIndexer)
    {
        this.luceneFullTextSearchIndexer = luceneFullTextSearchIndexer;
    }
}
