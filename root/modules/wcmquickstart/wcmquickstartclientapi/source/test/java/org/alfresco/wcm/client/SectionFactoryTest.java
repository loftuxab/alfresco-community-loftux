package org.alfresco.wcm.client;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SectionFactoryTest extends BaseTest 
{
    private final static Log log = LogFactory.getLog(SectionFactoryTest.class);

	public void testGetSections()
	{		
		WebSite site = getWebSite();
        Section root = site.getRootSection();
        String rootId = root.getId();		
		
		Section section = sectionFactory.getSectionFromPathSegments(rootId, new String[] {"news"});
		assertNotNull(section);
		//assertNotNull(section.getCollectionFolderId());
		
		Section bad = sectionFactory.getSectionFromPathSegments(rootId, new String[] {"news", "wooble"});
		assertNull(bad);

		Section exists2 = sectionFactory.getSection(section.getId());
		assertNotNull(exists2);		
		//assertNotNull(exists2.getCollectionFolderId());
		
		log.debug(section.getProperties());
	}

}
