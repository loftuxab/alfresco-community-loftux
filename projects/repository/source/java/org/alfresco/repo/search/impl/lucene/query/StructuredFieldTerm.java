/*
 * Created on Mar 14, 2005
 * 
 * TODO Comment this class
 * 
 * 
 */
package org.alfresco.repo.search.impl.lucene.query;

import org.apache.lucene.index.Term;

/**
 * @author andyh
 */
public class StructuredFieldTerm
{

    private Term term;

    private StructuredFieldPosition sfp;

    /**
     * 
     */
    public StructuredFieldTerm(Term term, StructuredFieldPosition sfp)
    {
        this.term = term;
        this.sfp = sfp;
    }

    /**
     * @return Returns the sfp.
     */
    public StructuredFieldPosition getSfp()
    {
        return sfp;
    }

    /**
     * @return Returns the term.
     */
    public Term getTerm()
    {
        return term;
    }
}
