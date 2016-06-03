
package org.alfresco.repo.search.adaptor.lucene;

/**
 * Functions that can be applied to lucene fields
 * 
 * Currently upper and lower that perform a case insensitive match for untokenised fields.
 * (If the field is tokenised the match should already be case insensitive.)
 * 
 * @author andyh
 *
 */
public enum LuceneFunction
{
    /**
     * Match as if the field was converted to upper case.
     */
    UPPER, 
    /**
     * Match as if the field was converted to lower case.
     */
    LOWER, 
    /**
     * A normal lucene field match.
     */
    FIELD;
}   
