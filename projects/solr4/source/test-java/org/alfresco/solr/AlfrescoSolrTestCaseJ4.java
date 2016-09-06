/*
 * #%L
 * Alfresco Solr 4
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
package org.alfresco.solr;


import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

import org.apache.solr.SolrTestCaseJ4;
import org.apache.solr.common.params.CommonParams;
import org.apache.solr.common.params.ModifiableSolrParams;
import org.apache.solr.common.params.MultiMapSolrParams;
import org.apache.solr.common.params.SolrParams;
import org.apache.solr.common.util.ContentStream;
import org.apache.solr.common.util.ContentStreamBase;
import org.apache.solr.common.util.NamedList;
import org.apache.solr.core.ConfigSolr;
import org.apache.solr.core.ConfigSolrXmlOld;
import org.apache.solr.core.SolrCore;
import org.apache.solr.core.SolrResourceLoader;
import org.apache.solr.request.LocalSolrQueryRequest;
import org.apache.solr.request.SolrQueryRequestBase;
import org.apache.solr.util.TestHarness;

import javax.servlet.http.HttpServletRequest;


/**
 * @author Joel
 *
 */
public class AlfrescoSolrTestCaseJ4 extends SolrTestCaseJ4
{


    public static File HOME() {
        return getFile("../source/test-files");
    }

    public static class SolrServletRequest extends SolrQueryRequestBase {
        public SolrServletRequest(SolrCore core, HttpServletRequest req)
        {
            super(core, new MultiMapSolrParams(Collections.<String, String[]> emptyMap()));
        }
    }

    public SolrServletRequest areq(ModifiableSolrParams params, String json) {
        if(params.get("wt" ) == null) params.add("wt","xml");
        SolrServletRequest req =  new SolrServletRequest(h.getCore(), null);
        req.setParams(params);
        if(json != null) {
            ContentStream stream = new ContentStreamBase.StringStream(json);
            ArrayList<ContentStream> streams = new ArrayList();
            streams.add(stream);
            req.setContentStreams(streams);
        }
        return req;
    }


    public static void initAlfrescoCore(String config, String schema) throws Exception {
        log.info("####initCore");

        configString = config;
        schemaString = schema;
        testSolrHome = HOME().getAbsolutePath();
        ignoreException("ignore_exception");

        System.setProperty("solr.directoryFactory","solr.RAMDirectoryFactory");


        // other  methods like starting a jetty instance need these too
        System.setProperty("solr.test.sys.prop1", "propone");
        System.setProperty("solr.test.sys.prop2", "proptwo");
        System.setProperty("alfresco.test", "true");

        String configFile = getSolrConfigFile();
        if (configFile != null) {
            createAlfrescoCore(config, schema);
        }
        log.info("####initCore end");
    }

    public static void createAlfrescoCore(String config, String schema) {
        assertNotNull(testSolrHome);
        SolrResourceLoader resourceLoader = new SolrResourceLoader(HOME().getAbsolutePath());
        ConfigSolr configSolr = getTestHarnessConfig(resourceLoader,
                                                     ConfigSolrXmlOld.DEFAULT_DEFAULT_CORE_NAME,
                                                     initCoreDataDir.getAbsolutePath(),
                                                     config,
                                                     schema);

        h = new TestHarness(resourceLoader, configSolr);
        lrf = h.getRequestFactory
                ("standard",0,20, CommonParams.VERSION,"2.2");
    }

    private static ConfigSolr getTestHarnessConfig(SolrResourceLoader loader, String coreName, String dataDir,
                                                   String solrConfig, String schema) {
        String solrxml = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n"
                + "<solr persistent=\"false\">\n"
                + "  <cores adminHandler=\"org.alfresco.solr.AlfrescoCoreAdminHandler\" adminPath=\"/admin/cores\" defaultCoreName=\""
                + ConfigSolrXmlOld.DEFAULT_DEFAULT_CORE_NAME
                + "\""
                + " host=\"${host:}\" hostPort=\"${hostPort:}\" hostContext=\"${hostContext:}\""
                + " distribUpdateSoTimeout=\"30000\""
                + " zkClientTimeout=\"${zkClientTimeout:30000}\" distribUpdateConnTimeout=\"30000\""
                + ">\n"
                + "    <core name=\"" + coreName + "\" config=\"" + solrConfig
                + "\" schema=\"" + schema + "\" dataDir=\"" + dataDir
                + "\" transient=\"false\" loadOnStartup=\"true\""
                + " shard=\"${shard:shard1}\" collection=\"${collection:collection1}\" instanceDir=\"" + coreName + "/\" />\n"
                + "  </cores>\n" + "</solr>";
        return ConfigSolr.fromString(loader, solrxml);
    }








}