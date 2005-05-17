/*
 * Created on 06-Apr-2005
 * 
 * TODO Comment this class
 * 
 * 
 */
package org.alfresco.repo.search.impl.lucene.query;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;

import org.alfresco.repo.search.SearcherException;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.TermPositions;
import org.apache.lucene.search.Explanation;
import org.apache.lucene.search.Scorer;
import org.apache.lucene.search.Similarity;
import org.apache.lucene.search.Weight;

public class LeafScorer extends Scorer
{
    static class Counter
    {
        int count = 0;

        public String toString()
        {
            return "count = " + count;
        }
    }

    private int counter;

    private int countInCounter;

    int min = 0;

    int max = 0;

    boolean more = true;

    Scorer containerScorer;

    StructuredFieldPosition[] sfps;

    float freq = 0.0f;

    HashMap<String, Counter> parentIds = new HashMap<String, Counter>();

    HashMap<String, Counter> selfIds = null;

    boolean hasSelfScorer;

    IndexReader reader;

    private TermPositions allNodes;

    TermPositions level0;

    HashSet<String> selfLinks = new HashSet<String>();

    private TermPositions root;

    private int rootDoc;

    public LeafScorer(Weight weight, TermPositions root, TermPositions level0, ContainerScorer containerScorer, StructuredFieldPosition[] sfps, TermPositions allNodes,
            HashMap<String, Counter> selfIds, IndexReader reader, Similarity similarity, byte[] norms)
    {
        super(similarity);
        this.root = root;
        this.containerScorer = containerScorer;
        this.sfps = sfps;
        this.allNodes = allNodes;
        if (selfIds == null)
        {
            this.selfIds = new HashMap<String, Counter>();
            hasSelfScorer = false;
        }
        else
        {
            this.selfIds = selfIds;
            hasSelfScorer = true;
        }
        this.reader = reader;
        this.level0 = level0;
        try
        {
            initialise();
        }
        catch (IOException e)
        {
            throw new SearcherException(e);
        }
    }

    private void initialise() throws IOException
    {
        if (containerScorer != null)
        {
            parentIds.clear();
            while (containerScorer.next())
            {
                int doc = containerScorer.doc();
                Document document = reader.document(doc);
                Field id = document.getField("ID");
                Counter counter = parentIds.get(id.stringValue());
                if (counter == null)
                {
                    counter = new Counter();
                    parentIds.put(id.stringValue(), counter);
                }
                counter.count++;

                if (!hasSelfScorer)
                {
                    counter = selfIds.get(id.stringValue());
                    if (counter == null)
                    {
                        counter = new Counter();
                        selfIds.put(id.stringValue(), counter);
                    }
                    counter.count++;
                }
            }
        }
        else if (level0 != null)
        {
            parentIds.clear();
            while (level0.next())
            {
                int doc = level0.doc();
                Document document = reader.document(doc);
                Field id = document.getField("ID");
                if (id != null)
                {
                    Counter counter = parentIds.get(id.stringValue());
                    if (counter == null)
                    {
                        counter = new Counter();
                        parentIds.put(id.stringValue(), counter);
                    }
                    counter.count++;

                    counter = selfIds.get(id.stringValue());
                    if (counter == null)
                    {
                        counter = new Counter();
                        selfIds.put(id.stringValue(), counter);
                    }
                    counter.count++;
                }
            }
            if (parentIds.size() != 1)
            {
                throw new SearcherException("More than one root node? " + parentIds.size());
            }
        }
    }

    public boolean next() throws IOException
    {

        if (countInCounter < counter)
        {
            countInCounter++;
            return true;
        }
        else
        {
            countInCounter = 1;
            counter = 0;
        }

        if (allNodes())
        {
            while (more)
            {
                if (allNodes.next() && root.next())
                {
                    if (check())
                    {
                        return true;
                    }
                }
                else
                {
                    more = false;
                    return false;
                }
            }
        }

        if (!more)
        {
            // One of the search terms has no more docuements
            return false;
        }

        if (max == 0)
        {
            // We need to initialise
            // Just do a next on all terms and check if the first doc matches
            doNextOnAll();
            if (found())
            {
                return true;
            }
        }

        return findNext();
    }

    private boolean allNodes()
    {
        if (sfps.length == 0)
        {
            return true;
        }
        for (StructuredFieldPosition sfp : sfps)
        {
            if (sfp.getCachingTermPositions() != null)
            {
                return false;
            }
        }
        return true;
    }

    private boolean findNext() throws IOException
    {
        // Move to the next document

        while (more)
        {
            move(); // may set more to false
            if (found())
            {
                return true;
            }
        }

        // If we get here we must have no more documents
        return false;
    }

    private void skipToMax() throws IOException
    {
        // Do the terms
        int current;
        for (int i = 0, l = sfps.length; i < l; i++)
        {
            if (i == 0)
            {
                min = max;
            }
            if (sfps[i].getCachingTermPositions() != null)
            {
                if (sfps[i].getCachingTermPositions().doc() < max)
                {
                    if (sfps[i].getCachingTermPositions().skipTo(max))
                    {
                        current = sfps[i].getCachingTermPositions().doc();
                        adjustMinMax(current, false);
                    }
                    else
                    {
                        more = false;
                        return;
                    }
                }
            }
        }

        // Do the root
        if (root.doc() < max)
        {
            if (root.skipTo(max))
            {
                rootDoc = root.doc();
            }
            else
            {
                more = false;
                return;
            }
        }
    }

    private void move() throws IOException
    {
        if (min == max)
        {
            // If we were at a match just do next on all terms
            doNextOnAll();
        }
        else
        {
            // We are in a range - try and skip to the max position on all terms
            skipToMax();
        }
    }

    private void doNextOnAll() throws IOException
    {
        // Do the terms
        int current;
        boolean first = true;
        for (int i = 0, l = sfps.length; i < l; i++)
        {
            if (sfps[i].getCachingTermPositions() != null)
            {
                if (sfps[i].getCachingTermPositions().next())
                {
                    current = sfps[i].getCachingTermPositions().doc();
                    adjustMinMax(current, first);
                    first = false;
                }
                else
                {
                    more = false;
                    return;
                }
            }
        }

        // Do the root term
        if (root.next())
        {
            rootDoc = root.doc();
        }
        else
        {
            more = false;
            return;
        }
        if (root.doc() < max)
        {
            if (root.skipTo(max))
            {
                rootDoc = root.doc();
            }
            else
            {
                more = false;
                return;
            }
        }
    }

    private void adjustMinMax(int doc, boolean setMin)
    {

        if (max < doc)
        {
            max = doc;
        }

        if (setMin)
        {
            min = doc;
        }
        else if (min > doc)
        {
            min = doc;
        }
    }

    private boolean found() throws IOException
    {
        if (sfps.length == 0)
        {
            return true;
        }

        // no more documents - no match
        if (!more)
        {
            return false;
        }

        // min and max must point to the same document
        if (min != max)
        {
            return false;
        }

        if (rootDoc != max)
        {
            return false;
        }

        return check();
    }

    private boolean check() throws IOException
    {
        // We have duplicate entries
        // The match must be in a known term range
        int count = root.freq();
        int start = 0;
        int end = -1;
        for (int i = 0; i < count; i++)
        {
            if (i == 0)
            {
                // First starts at zero
                start = 0;
                end = root.nextPosition();
            }
            else
            {
                start = end + 1;
                end = root.nextPosition();
            }

            check(start, end, i);

        }
        // We had checks to do and they all failed.
        return this.counter > 0;
    }

    private boolean check(int start, int end, int position) throws IOException
    {
        int offset = 0;
        for (int i = 0, l = sfps.length; i < l; i++)
        {
            offset = sfps[i].matches(start, end, offset);
            if (offset == -1)
            {
                return false;
            }
        }
        // Last match may fail
        if (offset == -1)
        {
            return false;
        }
        else
        {
            if ((sfps[sfps.length - 1].isTerminal()) && (offset != 2))
            {
                return false;
            }
        }

        Field[] fields = reader.document(doc()).getFields("PARENT");
        String parentID = null;
        if ((fields != null) && (fields.length > position) && (fields[position] != null))
        {
            parentID = fields[position].stringValue();
        }

        return containersIncludeCurrent(parentID);

    }

    private boolean containersIncludeCurrent(String parentID) throws IOException
    {
        if ((containerScorer != null) || (level0 != null))
        {
            if (sfps.length == 0)
            {
                return false;
            }
            Document document = reader.document(doc());
            StructuredFieldPosition last = sfps[sfps.length - 1];
            Field field;
            field = document.getField("ID");
            String id = field.stringValue();
            if ((last.linkSelf() && selfIds.containsKey(id)))
            {
                Counter counter = selfIds.get(id);
                if (counter != null)
                {
                    if (!selfLinks.contains(id))
                    {
                        this.counter += counter.count;
                        selfLinks.add(id);
                        return true;
                    }
                }
            }
            if ((parentID != null) && (parentID.length() > 0) && last.linkParent())
            {
                if (!selfLinks.contains(id))
                {
                    // field = document.getField("PARENT");
                    // if (field != null)
                    // {
                    // Counter counter = parentIds.get(field.stringValue());
                    Counter counter = parentIds.get(parentID);
                    if (counter != null)
                    {
                        this.counter += counter.count;
                        return true;
                    }
                    // }
                }
            }

            return false;
        }
        else
        {
            return true;
        }
    }

    public int doc()
    {
        if (allNodes())
        {
            return allNodes.doc();
        }
        return max;
    }

    public float score() throws IOException
    {
        return 1.0f;
    }

    public boolean skipTo(int target) throws IOException
    {

        countInCounter = 1;
        counter = 0;

        if (allNodes())
        {
            allNodes.skipTo(target);
            root.skipTo(allNodes.doc()); // must match
            if (check())
            {
                return true;
            }
            while (more)
            {
                if (allNodes.next() && root.next())
                {
                    if (check())
                    {
                        return true;
                    }
                }
                else
                {
                    more = false;
                    return false;
                }
            }
        }

        max = target;
        return findNext();
    }

    public Explanation explain(int doc) throws IOException
    {
        Explanation tfExplanation = new Explanation();

        while (next() && doc() < doc)
        {
        }

        float phraseFreq = (doc() == doc) ? freq : 0.0f;
        tfExplanation.setValue(getSimilarity().tf(phraseFreq));
        tfExplanation.setDescription("tf(phraseFreq=" + phraseFreq + ")");

        return tfExplanation;
    }

}
