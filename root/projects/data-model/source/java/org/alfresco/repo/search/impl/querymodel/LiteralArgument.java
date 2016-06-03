package org.alfresco.repo.search.impl.querymodel;

import org.alfresco.service.namespace.QName;

/**
 * @author andyh
 */
public interface LiteralArgument extends StaticArgument
{
    public QName getType();
}
