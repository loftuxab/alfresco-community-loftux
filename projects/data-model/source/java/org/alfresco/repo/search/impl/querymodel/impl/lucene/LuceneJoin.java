package org.alfresco.repo.search.impl.querymodel.impl.lucene;

import org.alfresco.repo.search.impl.querymodel.Constraint;
import org.alfresco.repo.search.impl.querymodel.JoinType;
import org.alfresco.repo.search.impl.querymodel.Source;
import org.alfresco.repo.search.impl.querymodel.impl.BaseJoin;

/**
 * @author andyh
 *
 */
public class LuceneJoin extends BaseJoin
{

    /**
     * @param left Source
     * @param right Source
     * @param joinType JoinType
     * @param joinConstraint Constraint
     */
    public LuceneJoin(Source left, Source right, JoinType joinType, Constraint joinConstraint)
    {
        super(left, right, joinType, joinConstraint);
        // TODO Auto-generated constructor stub
    }

}
