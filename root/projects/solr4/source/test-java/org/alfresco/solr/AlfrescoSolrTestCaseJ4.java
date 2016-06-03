package org.alfresco.solr;


import java.io.File;

import org.apache.solr.SolrTestCaseJ4;


/**
 * @author Joel
 *
 */
public class AlfrescoSolrTestCaseJ4 extends SolrTestCaseJ4
{


    public static File HOME() {
        return getFile("../source/test-files");
    }


}