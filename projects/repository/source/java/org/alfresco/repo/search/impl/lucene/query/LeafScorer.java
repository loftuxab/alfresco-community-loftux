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

    private Counter currentCounter;

    private int countInCounter;

    int min = 0;

    int max = 0;

    boolean more = true;

    Scorer containerScorer;

    StructuredFieldPosition[] sfps;

    float freq = 0.0f;

    HashMap<String, Counter> parentIds = new HashMap<String, Counter>();

    IndexReader reader;

    private TermPositions allNodes;

    TermPositions level0;

    HashSet<String> selfLinks = new HashSet<String>();

    public LeafScorer(Weight weight, TermPositions level0, ContainerScorer containerScorer, StructuredFieldPosition[] sfps, TermPositions allNodes, IndexReader reader,
            Similarity similarity, byte[] norms)
    {
        super(similarity);
        this.containerScorer = containerScorer;
        this.sfps = sfps;
        this.allNodes = allNodes;
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
        if (currentCounter != null)
        {
            if (countInCounter < currentCounter.count)
            {
                countInCounter++;
                return true;
            }
        }

        if (allNodes())
        {
            while (more)
            {
                if (allNodes.next())
                {
                    if (containersIncludeCurrent())
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

        // We have a single entry - no max constraint in match term
        if (check(0, -1))
        {
            return true;
        }

        // We had checks to do and they all failed.
        return false;
    }

    private boolean check(int start, int end) throws IOException
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

        return containersIncludeCurrent();

    }

    private boolean containersIncludeCurrent() throws IOException
    {
        if ((containerScorer != null) || (level0 != null))
        {
            Document document = reader.document(doc());
            StructuredFieldPosition last = sfps[sfps.length - 1];
            Field field;
            field = document.getField("ID");
            String id = field.stringValue();
            if ((last.linkSelf() && parentIds.containsKey(id)))
            {
                Counter counter = parentIds.get(id);
                if (counter != null)
                {
                    if(!selfLinks.contains(id))
                    {
                       currentCounter = counter;
                       countInCounter = 1;
                       selfLinks.add(id);
                       return true;
                    }
                }
            }
            else if (last.linkParent())
            {
                field = document.getField("PARENT");
                if (field != null)
                {
                    Counter counter = parentIds.get(field.stringValue());
                    if (counter != null)
                    {
                        currentCounter = counter;
                        countInCounter = 1;
                        return true;
                    }
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
        if (allNodes())
        {
            return allNodes.skipTo(target);
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
