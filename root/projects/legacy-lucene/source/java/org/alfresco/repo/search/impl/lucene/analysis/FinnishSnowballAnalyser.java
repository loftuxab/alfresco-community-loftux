package org.alfresco.repo.search.impl.lucene.analysis;

import org.apache.lucene.analysis.snowball.SnowballAnalyzer;

public class FinnishSnowballAnalyser extends SnowballAnalyzer
{

    public FinnishSnowballAnalyser()
    {
        super("Finnish");
    }
}
