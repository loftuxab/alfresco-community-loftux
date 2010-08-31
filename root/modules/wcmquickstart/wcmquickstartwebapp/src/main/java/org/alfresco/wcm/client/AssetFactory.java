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
package org.alfresco.wcm.client;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface AssetFactory
{
    Asset getAssetById(String id);
    List<Asset> getAssetsById(Collection<String> ids);
    Asset getSectionAsset(String sectionId, String assetName);
	Asset getSectionAsset(String sectionId, String assetName, boolean wildcardsAllowedInName);
    SearchResults findByQuery(Query query);
    Map<String, List<String>> getSourceRelationships(String assetId);
    public Map<String, Rendition> getRenditions(String assetId);
}
