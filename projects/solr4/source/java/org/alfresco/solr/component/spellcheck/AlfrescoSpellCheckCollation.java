
package org.alfresco.solr.component.spellcheck;

import org.apache.solr.spelling.SpellCheckCollation;

/**
 * @author Jamal Kaabi-Mofrad
 * @since 5.0
 */
public class AlfrescoSpellCheckCollation extends SpellCheckCollation
{
    private String collationQueryString;

    /**
     * @return the collationQueryString
     */
    public String getCollationQueryString()
    {
        return this.collationQueryString;
    }

    /**
     * @param collationQueryString the collationQueryString to set
     */
    public void setCollationQueryString(String collationQueryString)
    {
        this.collationQueryString = collationQueryString;
    }

}
