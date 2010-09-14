/*
 * Copyright (C) 2005-2010 Alfresco Software Limited.
 *
 * This file is part of Alfresco
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
 */
package org.alfresco.wcm.client.service;

import org.alfresco.wcm.client.AssetCollection;
import org.alfresco.wcm.client.CollectionFactory;
import org.springframework.extensions.webscripts.processor.BaseProcessorExtension;

public class CollectionService extends BaseProcessorExtension implements CollectionFactory
{
    private CollectionFactory collectionFactory;

	public void setCollectionFactory(CollectionFactory collectionFactory) {
		this.collectionFactory = collectionFactory;
	}

	@Override
	public AssetCollection getCollection(String sectionId, String collectionName) {
		return collectionFactory.getCollection(sectionId, collectionName);
	}

	@Override
	public AssetCollection getCollection(String sectionId, String collectionName, int resultsToSkip, int maxResults) {
		return collectionFactory.getCollection(sectionId, collectionName, resultsToSkip, maxResults);
	}

}
