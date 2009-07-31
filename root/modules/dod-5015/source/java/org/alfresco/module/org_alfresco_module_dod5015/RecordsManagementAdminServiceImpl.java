/*
 * Copyright (C) 2005-2009 Alfresco Software Limited.
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
package org.alfresco.module.org_alfresco_module_dod5015;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alfresco.repo.dictionary.M2Aspect;
import org.alfresco.repo.dictionary.M2ClassAssociation;
import org.alfresco.repo.dictionary.M2Model;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.dictionary.AspectDefinition;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.dictionary.PropertyDefinition;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Records Management AdminService Implementation.
 * 
 * @author Neil McErlean
 */
public class RecordsManagementAdminServiceImpl implements RecordsManagementAdminService
{
    /** Logger */
    private static Log logger = LogFactory.getLog(RecordsManagementAdminServiceImpl.class);

    public static final String CUSTOM_MODEL_PREFIX = "rmc";
    public static final String RMA_RECORD = "rma:record";
    @Deprecated
    public static final String RMC_CUSTOM_PROPS = CUSTOM_MODEL_PREFIX + ":customProperties";
    public static final String RMC_CUSTOM_ASSOCS = CUSTOM_MODEL_PREFIX + ":customAssocs";

    private ServiceRegistry serviceRegistry;

    public void setServiceRegistry(ServiceRegistry serviceRegistry)
    {
        this.serviceRegistry = serviceRegistry;
    }
    
    public Map<QName, CustomAssociation> getAvailableCustomAssociations()
    {
        CustomModelUtil customModelUtil = new CustomModelUtil();
        customModelUtil.setContentService(serviceRegistry.getContentService());

        M2Model customModel = customModelUtil.readCustomContentModel();
        M2Aspect customAssocsAspect = customModel.getAspect(RMC_CUSTOM_ASSOCS);
        
        // M2ClassAssociation is a common supertype for M2Association and M2ChildAssociation.
        List<M2ClassAssociation> allAssocs = customAssocsAspect.getAssociations();

        Map<QName, CustomAssociation> result = new HashMap<QName, CustomAssociation>(allAssocs.size());
        for (M2ClassAssociation a : allAssocs)
        {
            QName n = QName.createQName(a.getName(), serviceRegistry.getNamespaceService());

            CustomAssociation cA = CustomAssociation.createInstance(a, serviceRegistry);

            result.put(n, cA);
        }
        return result;
    }

    public Map<QName, PropertyDefinition> getAvailableCustomProperties(CustomisableRmElement rmElement)
    {
        DictionaryService dictionaryService = serviceRegistry.getDictionaryService();
        NamespaceService namespaceService = serviceRegistry.getNamespaceService();
		QName relevantAspectQName = QName.createQName(rmElement.getCorrespondingAspect(), namespaceService);
        AspectDefinition aspectDefn = dictionaryService.getAspect(relevantAspectQName);
        Map<QName, PropertyDefinition> propDefns = aspectDefn.getProperties();

        return propDefns;
    }
}