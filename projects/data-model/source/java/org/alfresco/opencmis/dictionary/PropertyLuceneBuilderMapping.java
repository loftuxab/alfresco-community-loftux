package org.alfresco.opencmis.dictionary;

import org.alfresco.service.namespace.QName;

/**
 * Encapsulate the mapping of property to lucene builder
 * 
 * @author davidc
 */
public interface PropertyLuceneBuilderMapping
{
    /**
     * Gets a property lucene builder
     * 
     * @param propertyId property id
     * @return property builder
     */
    public CMISPropertyLuceneBuilder getPropertyLuceneBuilder(String propertyId);

    /**
     * Create a direct node property lucene builder
     * 
     * @param propertyName  node property name
     * @return  property lucene builder
     */
    public CMISPropertyLuceneBuilder createDirectPropertyLuceneBuilder(QName propertyName);
}
