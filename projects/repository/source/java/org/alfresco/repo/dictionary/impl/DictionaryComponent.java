package org.alfresco.repo.dictionary.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.alfresco.repo.dictionary.AspectDefinition;
import org.alfresco.repo.dictionary.ClassDefinition;
import org.alfresco.repo.dictionary.DictionaryService;
import org.alfresco.repo.dictionary.ModelDefinition;
import org.alfresco.repo.dictionary.PropertyDefinition;
import org.alfresco.repo.dictionary.PropertyTypeDefinition;
import org.alfresco.repo.dictionary.TypeDefinition;
import org.alfresco.repo.ref.QName;


/**
 * Data Dictionary Service Implementation
 * 
 * @author David Caruana
 */
public class DictionaryComponent implements DictionaryService
{

    /**
     * Dictionary DAO
     */
    private DictionaryDAO dictionaryDAO;


    /**
     * Sets the Meta Model DAO
     * 
     * @param metaModelDAO  meta model DAO
     */
    public void setDictionaryDAO(DictionaryDAO dictionaryDAO)
    {
        this.dictionaryDAO = dictionaryDAO;
    }

    public Collection<QName> getAllModels()
    {
        return dictionaryDAO.getModels();
    }

    public ModelDefinition getModel(QName model)
    {
        return dictionaryDAO.getModel(model);
    }

    public Collection<QName> getAllPropertyTypes()
    {
        Collection<QName> propertyTypes = new ArrayList<QName>();
        for (QName model : getAllModels())
        {
            propertyTypes.addAll(getAspects(model));
        }
        return propertyTypes;
    }

    public Collection<QName> getPropertyTypes(QName model)
    {
        Collection<PropertyTypeDefinition> propertyTypes = dictionaryDAO.getPropertyTypes(model);
        Collection<QName> qnames = new ArrayList<QName>(propertyTypes.size());
        for (PropertyTypeDefinition def : propertyTypes)
        {
            qnames.add(def.getName());
        }
        return qnames;
    }

    public Collection<QName> getAllTypes()
    {
        Collection<QName> types = new ArrayList<QName>();
        for (QName model : getAllModels())
        {
            types.addAll(getTypes(model));
        }
        return types;
    }

    public Collection<QName> getTypes(QName model)
    {
        Collection<TypeDefinition> types = dictionaryDAO.getTypes(model);
        Collection<QName> qnames = new ArrayList<QName>(types.size());
        for (TypeDefinition def : types)
        {
            qnames.add(def.getName());
        }
        return qnames;
    }

    public Collection<QName> getAllAspects()
    {
        Collection<QName> aspects = new ArrayList<QName>();
        for (QName model : getAllModels())
        {
            aspects.addAll(getAspects(model));
        }
        return aspects;
    }

    public Collection<QName> getAspects(QName model)
    {
        Collection<AspectDefinition> aspects = dictionaryDAO.getAspects(model);
        Collection<QName> qnames = new ArrayList<QName>(aspects.size());
        for (AspectDefinition def : aspects)
        {
            qnames.add(def.getName());
        }
        return qnames;
    }

    public boolean isSubClass(QName className, QName ofClassName)
    {
        // TODO Auto-generated method stub
        return true;
    }

    public PropertyTypeDefinition getPropertyType(QName name)
    {
        return dictionaryDAO.getPropertyType(name);
    }

    public TypeDefinition getType(QName name)
    {
        return dictionaryDAO.getType(name);
    }

    public AspectDefinition getAspect(QName name)
    {
        return dictionaryDAO.getAspect(name);
    }

    public ClassDefinition getClass(QName name)
    {
        return dictionaryDAO.getClass(name);
    }
    
    
    public TypeDefinition getAnonymousType(QName type, Collection<QName> aspects)
    {
        return dictionaryDAO.getAnonymousType(type, aspects);
    }

    public PropertyDefinition getProperty(QName className, QName propertyName)
    {
        PropertyDefinition propDef = null;
        ClassDefinition classDef = dictionaryDAO.getClass(className);
        if (classDef != null)
        {
            Map<QName,PropertyDefinition> propDefs = classDef.getProperties();
            propDef = propDefs.get(propertyName);
        }
        return propDef;
    }

    public PropertyDefinition getProperty(QName propertyName)
    {
        return dictionaryDAO.getProperty(propertyName);
    }
    
}
