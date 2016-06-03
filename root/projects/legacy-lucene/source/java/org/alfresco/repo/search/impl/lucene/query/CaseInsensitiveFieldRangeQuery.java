/*
 * #%L
 * Alfresco Legacy Lucene
 * %%
 * Copyright (C) 2005 - 2016 Alfresco Software Limited
 * %%
 * This file is part of the Alfresco software. 
 * If the software was purchased under a paid Alfresco license, the terms of 
 * the paid license agreement will prevail.  Otherwise, the software is 
 * provided under the following open source license terms:
 * 
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */
package org.alfresco.repo.search.impl.lucene.query;

import java.io.IOException;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.FilteredTermEnum;
import org.apache.lucene.search.MultiTermQuery;

/**
 * Find terms that match a range ignoring case
 * 
 * @author andyh
 */
public class CaseInsensitiveFieldRangeQuery extends MultiTermQuery
{
    /**
     * 
     */
    private static final long serialVersionUID = -5859977841901861122L;

    String expandedFieldName;

    String lowerTermText;

    String upperTermText;

    boolean includeLower;

    boolean includeUpper;

    /**
     * @param expandedFieldName -
     *            field
     * @param lowerTermText -
     *            upper range value
     * @param upperTermText -
     *            lower range value
     * @param includeLower -
     *            include the lower value
     * @param includeUpper -
     *            include the upper value
     */
    public CaseInsensitiveFieldRangeQuery(String expandedFieldName, String lowerTermText, String upperTermText, boolean includeLower, boolean includeUpper)
    {
        super(new Term(expandedFieldName, ""));
        this.expandedFieldName = expandedFieldName;
        this.lowerTermText = lowerTermText;
        this.upperTermText = upperTermText;
        this.includeLower = includeLower;
        this.includeUpper = includeUpper;
    }

    @Override
    protected FilteredTermEnum getEnum(IndexReader reader) throws IOException
    {
        return new CaseInsensitiveTermRangeEnum(reader, expandedFieldName, lowerTermText, upperTermText, includeLower, includeUpper);
    }

}
