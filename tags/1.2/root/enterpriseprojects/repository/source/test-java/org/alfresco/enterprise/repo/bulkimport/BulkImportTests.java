/*
 * Copyright (C) 2005-2015 Alfresco Software Limited.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.

 * As a special exception to the terms and conditions of version 2.0 of 
 * the GPL, you may redistribute this Program in connection with Free/Libre 
 * and Open Source Software ("FLOSS") applications as described in Alfresco's 
 * FLOSS exception.  You should have received a copy of the text describing 
 * the FLOSS exception, and it is also available here: 
 * http://www.alfresco.com/legal/licensing"
 */
package org.alfresco.enterprise.repo.bulkimport;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.List;
import java.util.Map;

import javax.transaction.NotSupportedException;
import javax.transaction.SystemException;

import org.alfresco.enterprise.repo.bulkimport.impl.InPlaceNodeImporterFactory;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.bulkimport.BulkImportParameters;
import org.alfresco.repo.bulkimport.NodeImporter;
import org.alfresco.repo.bulkimport.impl.AbstractBulkImportTests;
import org.alfresco.repo.content.ContentStore;
import org.alfresco.repo.content.MimetypeMap;
import org.alfresco.service.cmr.model.FileInfo;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.version.Version;
import org.alfresco.service.cmr.version.VersionHistory;
import org.alfresco.util.ApplicationContextHelper;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.util.ResourceUtils;

/**
 * 
 * @since 4.0
 *
 */
public class BulkImportTests extends AbstractBulkImportTests
{
	private InPlaceNodeImporterFactory inPlaceNodeImporterFactory;

	@BeforeClass
	public static void beforeTests()
	{
		startContext(new String[] {"classpath:bulkimporttest/alfresco/overrides-context.xml", ApplicationContextHelper.CONFIG_LOCATIONS[0]});
	}

    @Before
	public void setup() throws SystemException, NotSupportedException
	{
    	super.setup();
    	inPlaceNodeImporterFactory = (InPlaceNodeImporterFactory)ctx.getBean("inPlaceNodeImporterFactory");
	}
		
    /**
     * InPlaceNodeImporter should skip folders if they are described only using metadata file and has no content folder. There should'n be any errors.
     * 
     * @throws Throwable
     */
    @Test
    public void testMNT9851() throws Throwable
    {
        txn = transactionService.getUserTransaction();
        txn.begin();

        NodeRef folderNode = topLevelFolder.getNodeRef();
        File bulkimport4 = ResourceUtils.getFile("classpath:bulkimport4");
        ContentStore fileContentStore = (ContentStore)ctx.getBean("fileContentStore");
        File contentStoreRoot = new File(fileContentStore.getRootLocation());
        FileUtils.copyDirectory(bulkimport4, contentStoreRoot);
        
        try
        {
            NodeImporter nodeImporter = inPlaceNodeImporterFactory.getNodeImporter("default","bulkimport4");
            BulkImportParameters bulkImportParameters = new BulkImportParameters();
            bulkImportParameters.setTarget(folderNode);
            bulkImportParameters.setReplaceExisting(true);
            bulkImportParameters.setDisableRulesService(true);
            bulkImportParameters.setBatchSize(40);
            bulkImporter.bulkImport(bulkImportParameters, nodeImporter);
        }
        catch(Throwable e)
        {
            fail(e.getMessage());
        }

        System.out.println(bulkImporter.getStatus());
        assertEquals(false, bulkImporter.getStatus().inProgress());
        
        List<FileInfo> folders = getFolders(folderNode, null);
        assertEquals(0, folders.size());
    }  

    @Test
    public void testInPlaceImportStriping() throws Throwable
    {
        txn = transactionService.getUserTransaction();
        txn.begin();
        
        NodeRef folderNode = topLevelFolder.getNodeRef();
        File bulkimport5 = ResourceUtils.getFile("classpath:bulkimport5");
        ContentStore fileContentStore = (ContentStore)ctx.getBean("fileContentStore");
        File contentStoreRoot = new File(fileContentStore.getRootLocation());
        FileUtils.copyDirectory(bulkimport5, contentStoreRoot);

        // import with in-place importer
        try
        {
            NodeImporter nodeImporter = inPlaceNodeImporterFactory.getNodeImporter("default", "bulkimport5");
            BulkImportParameters bulkImportParameters = new BulkImportParameters();
            bulkImportParameters.setTarget(folderNode);
            bulkImportParameters.setReplaceExisting(true);
            bulkImportParameters.setBatchSize(40);
            bulkImporter.bulkImport(bulkImportParameters, nodeImporter);
        }
        catch(Throwable e)
        {
            fail(e.getMessage());
        }

        System.out.println(bulkImporter.getStatus());

        checkFiles(folderNode, null, 2, 0,
                new ExpectedFile[]
                {
                },
                new ExpectedFolder[]
                {
                    new ExpectedFolder("1"),
                    new ExpectedFolder("3")
                });

        List<FileInfo> folders = getFolders(folderNode, "1");
        assertEquals("", 1, folders.size());
        NodeRef folder1 = folders.get(0).getNodeRef();
        checkFiles(folder1, null, 1, 0, null,
                new ExpectedFolder[]
                {
                    new ExpectedFolder("18")
                });

        folders = getFolders(folder1, "18");
        assertEquals("", 1, folders.size());
        NodeRef folder18 = folders.get(0).getNodeRef();
        checkFiles(folder18, null, 3, 0,
                new ExpectedFile[]
                {
                },
                new ExpectedFolder[]
                {
                    new ExpectedFolder("1"),
                    new ExpectedFolder("2"),
                    new ExpectedFolder("3")
                });

        folders = getFolders(folder18, "1");
        assertEquals("", 1, folders.size());
        folder1 = folders.get(0).getNodeRef();
        checkFiles(folder1, null, 0, 12,
                new ExpectedFile[]
                {
                    new ExpectedFile("9a61c45b-bb1a-4abd-bdf1-0230bdc647d9.bin", MimetypeMap.MIMETYPE_BINARY),
                    new ExpectedFile("e46c83bc-f396-40d2-80e1-67dbde608344.bin", MimetypeMap.MIMETYPE_BINARY)
                },
                new ExpectedFolder[]
                {
                });
    }

    @Test
    public void testMNT13250() throws Throwable
    {
        txn = transactionService.getUserTransaction();
        txn.begin();
        
        NodeRef folderNode = topLevelFolder.getNodeRef();
        File bulkimport6 = ResourceUtils.getFile("classpath:bulkimport6");
        ContentStore fileContentStore = (ContentStore)ctx.getBean("fileContentStore");
        File contentStoreRoot = new File(fileContentStore.getRootLocation());
        FileUtils.copyDirectory(bulkimport6, contentStoreRoot);

        // import with in-place importer
        try
        {
            NodeImporter nodeImporter = inPlaceNodeImporterFactory.getNodeImporter("default", "bulkimport6");
            BulkImportParameters bulkImportParameters = new BulkImportParameters();
            bulkImportParameters.setTarget(folderNode);
            bulkImportParameters.setReplaceExisting(true);
            bulkImportParameters.setBatchSize(1);
            bulkImporter.bulkImport(bulkImportParameters, nodeImporter);
        }
        catch(Throwable e)
        {
            fail(e.getMessage());
        }

        assertEquals(false, bulkImporter.getStatus().inProgress());

        checkFiles(folderNode, null, 0, 2,
                new ExpectedFile[]
                {
                   new ExpectedFile("a.txt", MimetypeMap.MIMETYPE_TEXT_PLAIN, "a.content.head"),
                   new ExpectedFile("b.txt", MimetypeMap.MIMETYPE_TEXT_PLAIN, "b.content")
                },
                new ExpectedFolder[]
                {
                });

        Map<String, FileInfo> files = toMap(getFiles(folderNode, null));

        NodeRef fileNodeRef = files.get("a.txt").getNodeRef();
        assertTrue("Imported file should be versioned:", versionService.isVersioned(fileNodeRef));

        VersionHistory history = versionService.getVersionHistory(fileNodeRef);
        assertNotNull(history);

        assertEquals("Imported file should have 4 versions:", 4, history.getAllVersions().size());

        Version[] versions = history.getAllVersions().toArray(new Version[4]);

        //compare the content of each version
        ContentReader contentReader;

        contentReader = this.contentService.getReader(versions[0].getFrozenStateNodeRef(), ContentModel.PROP_CONTENT);
        assertNotNull(contentReader);
        assertEquals("a.content.head", contentReader.getContentString());

        contentReader = this.contentService.getReader(versions[1].getFrozenStateNodeRef(), ContentModel.PROP_CONTENT);
        assertNotNull(contentReader);
        assertEquals("a.content.v3", contentReader.getContentString());

        contentReader = this.contentService.getReader(versions[2].getFrozenStateNodeRef(), ContentModel.PROP_CONTENT);
        assertNotNull(contentReader);
        assertEquals("a.content.v2", contentReader.getContentString());

        contentReader = this.contentService.getReader(versions[3].getFrozenStateNodeRef(), ContentModel.PROP_CONTENT);
        assertNotNull(contentReader);
        assertEquals("a.content.v1", contentReader.getContentString());
    }

}
