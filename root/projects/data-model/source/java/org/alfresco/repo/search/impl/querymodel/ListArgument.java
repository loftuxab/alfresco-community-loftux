package org.alfresco.repo.search.impl.querymodel;

import java.util.List;

/**
 * @author andyh
 *
 */
public interface ListArgument extends StaticArgument
{
    public List<Argument> getArguments();
}
