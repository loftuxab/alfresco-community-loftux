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
package org.alfresco.repo.dictionary;

import java.io.ByteArrayInputStream;
import java.util.List;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.service.namespace.QName;

public class DiffModelTest extends AbstractModelTest
{

    public void testDeleteModel()
    {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(AbstractModelTest.MODEL1_XML.getBytes());

        M2Model model = M2Model.createModel(byteArrayInputStream);
        QName modelName = dictionaryDAO.putModel(model);
        
        CompiledModel previousVersion = dictionaryDAO.getCompiledModel(modelName);
        
        List<M2ModelDiff> modelDiffs = dictionaryDAO.diffModel(previousVersion, null);
        
        for (M2ModelDiff modelDiff : modelDiffs)
        {
            System.out.println(modelDiff.toString());
        }   
        
        assertEquals(6, modelDiffs.size());
        
        assertEquals(3, countDiffs(modelDiffs, M2ModelDiff.TYPE_TYPE, M2ModelDiff.DIFF_DELETED));
        assertEquals(3, countDiffs(modelDiffs, M2ModelDiff.TYPE_ASPECT, M2ModelDiff.DIFF_DELETED));
    }
    
    @SuppressWarnings("unused")
    public void testNoExistingModelToDelete()
    {
        try
        {
            List<M2ModelDiff> modelDiffs = dictionaryDAO.diffModel(null, null);
            assertTrue("Should throw exeception that there is no previous version of the model to delete", true);
        }
        catch (AlfrescoRuntimeException e)
        {
            assertTrue("Wrong error message", e.getMessage().equals("Invalid arguments - no previous version of model to delete"));
        }
        
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(AbstractModelTest.MODEL1_XML.getBytes());

        M2Model model = M2Model.createModel(byteArrayInputStream);
        QName modelName = dictionaryDAO.putModel(model);
        
        CompiledModel compiledModel = dictionaryDAO.getCompiledModel(modelName);
        
        try
        {
            List<M2ModelDiff> modelDiffs = dictionaryDAO.diffModel(null, compiledModel);
            assertTrue("Should throw exeception that there is no previous version of the model to delete", true);
        }
        catch (AlfrescoRuntimeException e)
        {
            assertTrue("Wrong error message", e.getMessage().equals("Invalid arguments - no previous version of model to delete"));
        }
    }
    
    public void testNewModel()
    {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(AbstractModelTest.MODEL1_XML.getBytes());

        M2Model model = M2Model.createModel(byteArrayInputStream);
        QName modelName = dictionaryDAO.putModel(model);
        
        CompiledModel newVersion = dictionaryDAO.getCompiledModel(modelName);
        
        List<M2ModelDiff> modelDiffs = dictionaryDAO.diffModel(null, newVersion);
        
        for (M2ModelDiff modelDiff : modelDiffs)
        {
            System.out.println(modelDiff.toString());
        }   
        
        assertEquals(6, modelDiffs.size());
        
        assertEquals(3, countDiffs(modelDiffs, M2ModelDiff.TYPE_TYPE, M2ModelDiff.DIFF_CREATED));
        assertEquals(3, countDiffs(modelDiffs, M2ModelDiff.TYPE_ASPECT, M2ModelDiff.DIFF_CREATED));
    }
    
    public void testNonIncUpdateModel()
    {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(AbstractModelTest.MODEL1_XML.getBytes());
        M2Model model = M2Model.createModel(byteArrayInputStream);
        QName modelName = dictionaryDAO.putModel(model);
        CompiledModel previousVersion = dictionaryDAO.getCompiledModel(modelName);
        
        byteArrayInputStream = new ByteArrayInputStream(AbstractModelTest.MODEL1_UPDATE1_XML.getBytes());
        model = M2Model.createModel(byteArrayInputStream);
        modelName = dictionaryDAO.putModel(model);
        CompiledModel newVersion = dictionaryDAO.getCompiledModel(modelName);
        
        List<M2ModelDiff> modelDiffs = dictionaryDAO.diffModel(previousVersion, newVersion);
        
        for (M2ModelDiff M2ModelDiff : modelDiffs)
        {
            System.out.println(M2ModelDiff.toString());
        }   
        
        assertEquals(16, modelDiffs.size());
        
        assertEquals(1, countDiffs(modelDiffs, M2ModelDiff.TYPE_TYPE, M2ModelDiff.DIFF_CREATED));
        assertEquals(2, countDiffs(modelDiffs, M2ModelDiff.TYPE_TYPE, M2ModelDiff.DIFF_UNCHANGED));
        assertEquals(0, countDiffs(modelDiffs, M2ModelDiff.TYPE_TYPE, M2ModelDiff.DIFF_UPDATED));
        assertEquals(1, countDiffs(modelDiffs, M2ModelDiff.TYPE_TYPE, M2ModelDiff.DIFF_DELETED));
        
        assertEquals(1, countDiffs(modelDiffs, M2ModelDiff.TYPE_ASPECT, M2ModelDiff.DIFF_CREATED));
        assertEquals(2, countDiffs(modelDiffs, M2ModelDiff.TYPE_ASPECT, M2ModelDiff.DIFF_UNCHANGED));
        assertEquals(0, countDiffs(modelDiffs, M2ModelDiff.TYPE_ASPECT, M2ModelDiff.DIFF_UPDATED));
        assertEquals(1, countDiffs(modelDiffs, M2ModelDiff.TYPE_ASPECT, M2ModelDiff.DIFF_DELETED));
        
        assertEquals(0, countDiffs(modelDiffs, M2ModelDiff.TYPE_PROPERTY, M2ModelDiff.DIFF_CREATED));
        assertEquals(6, countDiffs(modelDiffs, M2ModelDiff.TYPE_PROPERTY, M2ModelDiff.DIFF_UNCHANGED));
        assertEquals(1, countDiffs(modelDiffs, M2ModelDiff.TYPE_PROPERTY, M2ModelDiff.DIFF_UPDATED));
        assertEquals(1, countDiffs(modelDiffs, M2ModelDiff.TYPE_PROPERTY, M2ModelDiff.DIFF_DELETED));
    }
    
    public void testIncUpdatePropertiesAdded()
    {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(AbstractModelTest.MODEL2_XML.getBytes());
        M2Model model = M2Model.createModel(byteArrayInputStream);
        QName modelName = dictionaryDAO.putModel(model);
        CompiledModel previousVersion = dictionaryDAO.getCompiledModel(modelName);
        
        byteArrayInputStream = new ByteArrayInputStream(AbstractModelTest.MODEL2_EXTRA_PROPERTIES_XML.getBytes());
        model = M2Model.createModel(byteArrayInputStream);
        modelName = dictionaryDAO.putModel(model);
        CompiledModel newVersion = dictionaryDAO.getCompiledModel(modelName);
        
        List<M2ModelDiff> modelDiffs = dictionaryDAO.diffModel(previousVersion, newVersion);
        
        for (M2ModelDiff modelDiff : modelDiffs)
        {
            System.out.println(modelDiff.toString());
        }   
        
        assertEquals(8, modelDiffs.size());
        
        assertEquals(1, countDiffs(modelDiffs, M2ModelDiff.TYPE_TYPE, M2ModelDiff.DIFF_UNCHANGED));
        assertEquals(1, countDiffs(modelDiffs, M2ModelDiff.TYPE_ASPECT, M2ModelDiff.DIFF_UNCHANGED));
        assertEquals(4, countDiffs(modelDiffs, M2ModelDiff.TYPE_PROPERTY, M2ModelDiff.DIFF_UNCHANGED));
        assertEquals(2, countDiffs(modelDiffs, M2ModelDiff.TYPE_PROPERTY, M2ModelDiff.DIFF_CREATED));
    }

    public void testIncUpdateTypesAndAspectsAdded()
    {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(AbstractModelTest.MODEL3_XML.getBytes());
        M2Model model = M2Model.createModel(byteArrayInputStream);
        QName modelName = dictionaryDAO.putModel(model);
        CompiledModel previousVersion = dictionaryDAO.getCompiledModel(modelName);
        
        byteArrayInputStream = new ByteArrayInputStream(AbstractModelTest.MODEL3_EXTRA_TYPES_AND_ASPECTS_XML.getBytes());
        model = M2Model.createModel(byteArrayInputStream);
        modelName = dictionaryDAO.putModel(model);
        CompiledModel newVersion = dictionaryDAO.getCompiledModel(modelName);
        
        List<M2ModelDiff> modelDiffs = dictionaryDAO.diffModel(previousVersion, newVersion);
        
        for (M2ModelDiff modelDiff : modelDiffs)
        {
            System.out.println(modelDiff.toString());
        }   
        
        assertEquals(8, modelDiffs.size());
        
        assertEquals(1, countDiffs(modelDiffs, M2ModelDiff.TYPE_TYPE, M2ModelDiff.DIFF_UNCHANGED));
        assertEquals(1, countDiffs(modelDiffs, M2ModelDiff.TYPE_ASPECT, M2ModelDiff.DIFF_UNCHANGED));
        
        assertEquals(1, countDiffs(modelDiffs, M2ModelDiff.TYPE_TYPE, M2ModelDiff.DIFF_CREATED));
        assertEquals(1, countDiffs(modelDiffs, M2ModelDiff.TYPE_ASPECT, M2ModelDiff.DIFF_CREATED));
        
        assertEquals(4, countDiffs(modelDiffs, M2ModelDiff.TYPE_PROPERTY, M2ModelDiff.DIFF_UNCHANGED));
    }
    
    public void testIncUpdateAssociationsAdded()
    {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(AbstractModelTest.MODEL5_XML.getBytes());
        M2Model model = M2Model.createModel(byteArrayInputStream);
        QName modelName = dictionaryDAO.putModel(model);
        CompiledModel previousVersion = dictionaryDAO.getCompiledModel(modelName);
        
        byteArrayInputStream = new ByteArrayInputStream(AbstractModelTest.MODEL5_EXTRA_ASSOCIATIONS_XML.getBytes());
        model = M2Model.createModel(byteArrayInputStream);
        modelName = dictionaryDAO.putModel(model);
        CompiledModel newVersion = dictionaryDAO.getCompiledModel(modelName);
        
        List<M2ModelDiff> modelDiffs = dictionaryDAO.diffModel(previousVersion, newVersion);
        
        for (M2ModelDiff modelDiff : modelDiffs)
        {
            System.out.println(modelDiff.toString());
        }
        
        assertEquals(12, modelDiffs.size());
        
        assertEquals(1, countDiffs(modelDiffs, M2ModelDiff.TYPE_TYPE, M2ModelDiff.DIFF_UPDATED_INC));
        assertEquals(1, countDiffs(modelDiffs, M2ModelDiff.TYPE_TYPE, M2ModelDiff.DIFF_UNCHANGED));
        
        assertEquals(2, countDiffs(modelDiffs, M2ModelDiff.TYPE_ASPECT, M2ModelDiff.DIFF_UNCHANGED));
        
        assertEquals(6, countDiffs(modelDiffs, M2ModelDiff.TYPE_PROPERTY, M2ModelDiff.DIFF_UNCHANGED));
        
        assertEquals(2, countDiffs(modelDiffs, M2ModelDiff.TYPE_ASSOCIATION, M2ModelDiff.DIFF_CREATED));
    }
    
    public void testIncUpdateTitleDescription()
    {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(AbstractModelTest.MODEL6_XML.getBytes());
        M2Model model = M2Model.createModel(byteArrayInputStream);
        QName modelName = dictionaryDAO.putModel(model);
        CompiledModel previousVersion = dictionaryDAO.getCompiledModel(modelName);
        
        byteArrayInputStream = new ByteArrayInputStream(AbstractModelTest.MODEL6_UPDATE1_XML.getBytes());
        model = M2Model.createModel(byteArrayInputStream);
        modelName = dictionaryDAO.putModel(model);
        CompiledModel newVersion = dictionaryDAO.getCompiledModel(modelName);
        
        List<M2ModelDiff> modelDiffs = dictionaryDAO.diffModel(previousVersion, newVersion);
        
        for (M2ModelDiff modelDiff : modelDiffs)
        {
            System.out.println(modelDiff.toString());
        }
        
        assertEquals(4, modelDiffs.size());
        
        assertEquals(1, countDiffs(modelDiffs, M2ModelDiff.TYPE_TYPE, M2ModelDiff.DIFF_UPDATED_INC));
        assertEquals(1, countDiffs(modelDiffs, M2ModelDiff.TYPE_ASPECT, M2ModelDiff.DIFF_UNCHANGED));
        assertEquals(2, countDiffs(modelDiffs, M2ModelDiff.TYPE_PROPERTY, M2ModelDiff.DIFF_UPDATED_INC));
    }
    
    public void testNonIncUpdatePropertiesRemoved()
    {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(AbstractModelTest.MODEL2_EXTRA_PROPERTIES_XML.getBytes());
        M2Model model = M2Model.createModel(byteArrayInputStream);
        QName modelName = dictionaryDAO.putModel(model);
        CompiledModel previousVersion = dictionaryDAO.getCompiledModel(modelName);
        
        byteArrayInputStream = new ByteArrayInputStream(AbstractModelTest.MODEL2_XML.getBytes());
        model = M2Model.createModel(byteArrayInputStream);
        modelName = dictionaryDAO.putModel(model);
        CompiledModel newVersion = dictionaryDAO.getCompiledModel(modelName);
        
        List<M2ModelDiff> modelDiffs = dictionaryDAO.diffModel(previousVersion, newVersion);
        
        for (M2ModelDiff modelDiff : modelDiffs)
        {
            System.out.println(modelDiff.toString());
        }   
        
        assertEquals(8, modelDiffs.size());
        
        assertEquals(1, countDiffs(modelDiffs, M2ModelDiff.TYPE_TYPE, M2ModelDiff.DIFF_UNCHANGED));
        assertEquals(1, countDiffs(modelDiffs, M2ModelDiff.TYPE_ASPECT, M2ModelDiff.DIFF_UNCHANGED));
        assertEquals(4, countDiffs(modelDiffs, M2ModelDiff.TYPE_PROPERTY, M2ModelDiff.DIFF_UNCHANGED));
        assertEquals(2, countDiffs(modelDiffs, M2ModelDiff.TYPE_PROPERTY, M2ModelDiff.DIFF_DELETED));
    }
    
    public void testNonIncUpdateTypesAndAspectsRemoved()
    {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(AbstractModelTest.MODEL3_EXTRA_TYPES_AND_ASPECTS_XML.getBytes());
        M2Model model = M2Model.createModel(byteArrayInputStream);
        QName modelName = dictionaryDAO.putModel(model);
        CompiledModel previousVersion = dictionaryDAO.getCompiledModel(modelName);
        
        byteArrayInputStream = new ByteArrayInputStream(AbstractModelTest.MODEL3_XML.getBytes());
        model = M2Model.createModel(byteArrayInputStream);
        modelName = dictionaryDAO.putModel(model);
        CompiledModel newVersion = dictionaryDAO.getCompiledModel(modelName);
        
        List<M2ModelDiff> modelDiffs = dictionaryDAO.diffModel(previousVersion, newVersion);
        
        for (M2ModelDiff modelDiff : modelDiffs)
        {
            System.out.println(modelDiff.toString());
        }
        
        assertEquals(8, modelDiffs.size());
        
        assertEquals(1, countDiffs(modelDiffs, M2ModelDiff.TYPE_TYPE, M2ModelDiff.DIFF_UNCHANGED));
        assertEquals(1, countDiffs(modelDiffs, M2ModelDiff.TYPE_ASPECT, M2ModelDiff.DIFF_UNCHANGED));
        
        assertEquals(1, countDiffs(modelDiffs, M2ModelDiff.TYPE_TYPE, M2ModelDiff.DIFF_DELETED));
        assertEquals(1, countDiffs(modelDiffs, M2ModelDiff.TYPE_ASPECT, M2ModelDiff.DIFF_DELETED));
        
        assertEquals(4, countDiffs(modelDiffs, M2ModelDiff.TYPE_PROPERTY, M2ModelDiff.DIFF_UNCHANGED));
    }
    
    public void testNonIncUpdateDefaultAspectAdded()
    {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(AbstractModelTest.MODEL4_XML.getBytes());
        M2Model model = M2Model.createModel(byteArrayInputStream);
        QName modelName = dictionaryDAO.putModel(model);
        CompiledModel previousVersion = dictionaryDAO.getCompiledModel(modelName);
        
        byteArrayInputStream = new ByteArrayInputStream(AbstractModelTest.MODEL4_EXTRA_DEFAULT_ASPECT_XML.getBytes());
        model = M2Model.createModel(byteArrayInputStream);
        modelName = dictionaryDAO.putModel(model);
        CompiledModel newVersion = dictionaryDAO.getCompiledModel(modelName);
        
        List<M2ModelDiff> modelDiffs = dictionaryDAO.diffModel(previousVersion, newVersion);
        
        for (M2ModelDiff modelDiff : modelDiffs)
        {
            System.out.println(modelDiff.toString());
        }
        
        assertEquals(4, modelDiffs.size());
        
        assertEquals(1, countDiffs(modelDiffs, M2ModelDiff.TYPE_TYPE, M2ModelDiff.DIFF_UPDATED));
        assertEquals(1, countDiffs(modelDiffs, M2ModelDiff.TYPE_ASPECT, M2ModelDiff.DIFF_UNCHANGED));
        assertEquals(2, countDiffs(modelDiffs, M2ModelDiff.TYPE_PROPERTY, M2ModelDiff.DIFF_UNCHANGED));
    }
    
    public void testNonIncUpdateAssociationsRemoved()
    {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(AbstractModelTest.MODEL5_EXTRA_ASSOCIATIONS_XML.getBytes());
        M2Model model = M2Model.createModel(byteArrayInputStream);
        QName modelName = dictionaryDAO.putModel(model);
        CompiledModel previousVersion = dictionaryDAO.getCompiledModel(modelName);
        
        byteArrayInputStream = new ByteArrayInputStream(AbstractModelTest.MODEL5_XML.getBytes());
        model = M2Model.createModel(byteArrayInputStream);
        modelName = dictionaryDAO.putModel(model);
        CompiledModel newVersion = dictionaryDAO.getCompiledModel(modelName);
        
        List<M2ModelDiff> modelDiffs = dictionaryDAO.diffModel(previousVersion, newVersion);
        
        for (M2ModelDiff modelDiff : modelDiffs)
        {
            System.out.println(modelDiff.toString());
        }
        
        assertEquals(12, modelDiffs.size());
        
        assertEquals(1, countDiffs(modelDiffs, M2ModelDiff.TYPE_TYPE, M2ModelDiff.DIFF_UPDATED));
        assertEquals(1, countDiffs(modelDiffs, M2ModelDiff.TYPE_TYPE, M2ModelDiff.DIFF_UNCHANGED));
        
        assertEquals(2, countDiffs(modelDiffs, M2ModelDiff.TYPE_ASPECT, M2ModelDiff.DIFF_UNCHANGED));
        
        assertEquals(6, countDiffs(modelDiffs, M2ModelDiff.TYPE_PROPERTY, M2ModelDiff.DIFF_UNCHANGED));
        
        assertEquals(2, countDiffs(modelDiffs, M2ModelDiff.TYPE_ASSOCIATION, M2ModelDiff.DIFF_DELETED));
    }
    
    public void testIncUpdatePropertiesAddedToMandatoryAspect()
    {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(AbstractModelTest.MODEL7_XML.getBytes());
        M2Model model = M2Model.createModel(byteArrayInputStream);
        QName modelName = dictionaryDAO.putModel(model);
        CompiledModel previousVersion = dictionaryDAO.getCompiledModel(modelName);
        
        byteArrayInputStream = new ByteArrayInputStream(AbstractModelTest.MODEL7_EXTRA_PROPERTIES_MANDATORY_ASPECTS_XML.getBytes());
        model = M2Model.createModel(byteArrayInputStream);
        modelName = dictionaryDAO.putModel(model);
        CompiledModel newVersion = dictionaryDAO.getCompiledModel(modelName);
        
        List<M2ModelDiff> modelDiffs = dictionaryDAO.diffModel(previousVersion, newVersion);
        
        for (M2ModelDiff modelDiff : modelDiffs)
        {
            System.out.println(modelDiff.toString());
        }   
        
        assertEquals(3, modelDiffs.size());
        
        assertEquals(2, countDiffs(modelDiffs, M2ModelDiff.TYPE_ASPECT, M2ModelDiff.DIFF_UNCHANGED));
        assertEquals(1, countDiffs(modelDiffs, M2ModelDiff.TYPE_PROPERTY, M2ModelDiff.DIFF_CREATED));
    }
    
    public void testNonIncUpdatePropertiesRemovedFromMandatoryAspect()
    {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(AbstractModelTest.MODEL7_EXTRA_PROPERTIES_MANDATORY_ASPECTS_XML.getBytes());
        M2Model model = M2Model.createModel(byteArrayInputStream);
        QName modelName = dictionaryDAO.putModel(model);
        CompiledModel previousVersion = dictionaryDAO.getCompiledModel(modelName);
        
        byteArrayInputStream = new ByteArrayInputStream(AbstractModelTest.MODEL7_XML.getBytes());
        model = M2Model.createModel(byteArrayInputStream);
        modelName = dictionaryDAO.putModel(model);
        CompiledModel newVersion = dictionaryDAO.getCompiledModel(modelName);
        
        List<M2ModelDiff> modelDiffs = dictionaryDAO.diffModel(previousVersion, newVersion);
        
        for (M2ModelDiff modelDiff : modelDiffs)
        {
            System.out.println(modelDiff.toString());
        }   
        
        assertEquals(3, modelDiffs.size());
        
        assertEquals(2, countDiffs(modelDiffs, M2ModelDiff.TYPE_ASPECT, M2ModelDiff.DIFF_UNCHANGED));
        assertEquals(1, countDiffs(modelDiffs, M2ModelDiff.TYPE_PROPERTY, M2ModelDiff.DIFF_DELETED));
    }
    
    private int countDiffs(List<M2ModelDiff> M2ModelDiffs, String elementType, String diffType)
    {
        int count = 0;
        for (M2ModelDiff modelDiff : M2ModelDiffs)
        {
            if (modelDiff.getDiffType().equals(diffType) && modelDiff.getElementType().equals(elementType))
            {
                count++;
            }
        }
        return count;
    }
    
}

