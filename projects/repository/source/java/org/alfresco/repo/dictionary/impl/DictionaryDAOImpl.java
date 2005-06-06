package org.alfresco.repo.dictionary.impl;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alfresco.repo.dictionary.AspectDefinition;
import org.alfresco.repo.dictionary.AssociationDefinition;
import org.alfresco.repo.dictionary.ClassDefinition;
import org.alfresco.repo.dictionary.DictionaryException;
import org.alfresco.repo.dictionary.ModelDefinition;
import org.alfresco.repo.dictionary.PropertyDefinition;
import org.alfresco.repo.dictionary.PropertyTypeDefinition;
import org.alfresco.repo.dictionary.TypeDefinition;
import org.alfresco.repo.ref.QName;

public class DictionaryDAOImpl implements DictionaryDAO
{
    // TODO: Allow for the dynamic creation of models.  Supporting
    //       this requires the ability to persistently store the
    //       registration of models, the ability to load models
    //       from a persistent store, the refresh of the cache
    //       and concurrent read/write of the models.
    
    private NamespaceDAO namespaceDAO;

    private List<String> bootstrapModels = new ArrayList<String>();
    
    private Map<String,QName> namespaceToModel = new HashMap<String,QName>();
    private Map<QName,CompiledModel> compiledModels = new HashMap<QName,CompiledModel>();


    public DictionaryDAOImpl(NamespaceDAO namespaceDAO)
    {
        this.namespaceDAO = namespaceDAO;
    }
    
    public void setBootstrapModels(List<String> modelResources)
    {
        this.bootstrapModels = modelResources;
    }
    
    public void bootstrap()
    {
        for (String bootstrapModel : bootstrapModels)
        {
            InputStream modelStream = getClass().getClassLoader().getResourceAsStream(bootstrapModel);
            if (modelStream == null)
            {
                throw new DictionaryException("Could not find bootstrap model " + bootstrapModel);
            }
            try
            {
                M2Model model = M2Model.createModel(modelStream);
                putModel(model);
            }
            catch(DictionaryException e)
            {
                throw new DictionaryException("Could not import bootstrap model " + bootstrapModel, e);
            }
        }
    }

    
    
    
    private CompiledModel getCompiledModelForNamespace(String uri)
    {
        QName modelName = namespaceToModel.get(uri);
        return (modelName == null) ? null : getCompiledModel(modelName); 
    }
    
    
    private CompiledModel getCompiledModel(QName modelName)
    {
        CompiledModel model = compiledModels.get(modelName);
        if (model == null)
        {
            // TODO: Load model from persistent store 
            throw new DictionaryException("Model " + modelName + " does not exist");
        }
        return model;
    }
    
    public PropertyTypeDefinition getPropertyType(QName typeName)
    {
        CompiledModel model = getCompiledModelForNamespace(typeName.getNamespaceURI());
        return (model == null) ? null : model.getPropertyType(typeName);
    }

    public Collection<PropertyTypeDefinition> getPropertyTypes(QName modelName)
    {
        CompiledModel model = getCompiledModel(modelName);
        return model.getPropertyTypes();
    }



    public TypeDefinition getType(QName typeName)
    {
        CompiledModel model = getCompiledModelForNamespace(typeName.getNamespaceURI());
        return (model == null) ? null : model.getType(typeName);
    }


    public AspectDefinition getAspect(QName aspectName)
    {
        CompiledModel model = getCompiledModelForNamespace(aspectName.getNamespaceURI());
        return (model == null) ? null : model.getAspect(aspectName);
    }


    public ClassDefinition getClass(QName className)
    {
        CompiledModel model = getCompiledModelForNamespace(className.getNamespaceURI());
        return (model == null) ? null : model.getClass(className);
    }

    
    public PropertyDefinition getProperty(QName propertyName)
    {
        CompiledModel model = getCompiledModelForNamespace(propertyName.getNamespaceURI());
        return (model == null) ? null : model.getProperty(propertyName);
    }
    
    public AssociationDefinition getAssociation(QName assocName)
    {
        CompiledModel model = getCompiledModelForNamespace(assocName.getNamespaceURI());
        return (model == null) ? null : model.getAssociation(assocName);
    }

    
    public void putModel(M2Model model)
    {
        // Compile model definition
        CompiledModel compiledModel = model.compile(this, namespaceDAO);
        QName modelName = compiledModel.getModelDefinition().getName();

        // Remove namespace definitions for previous model, if it exists
        CompiledModel previousVersion = compiledModels.get(modelName);
        if (previousVersion != null)
        {
            for (M2Namespace namespace : previousVersion.getModel().getNamespaces())
            {
                namespaceDAO.removePrefix(namespace.getPrefix());
                namespaceDAO.removeURI(namespace.getUri());
                namespaceToModel.remove(namespace.getUri());
            }
        }
        
        // Create namespace definitions for new model
        for (M2Namespace namespace : model.getNamespaces())
        {
            namespaceDAO.addURI(namespace.getUri());
            namespaceDAO.addPrefix(namespace.getPrefix(), namespace.getUri());
            namespaceToModel.put(namespace.getUri(), modelName);
        }
        
        // Publish new Model Definition
        compiledModels.put(modelName, compiledModel);
    }


    public Collection<QName> getModels()
    {
        return compiledModels.keySet();
    }


    public ModelDefinition getModel(QName name)
    {
        CompiledModel model = getCompiledModel(name);
        if (model != null)
        {
            return model.getModelDefinition();
        }
        return null;
    }


    public Collection<TypeDefinition> getTypes(QName modelName)
    {
        CompiledModel model = getCompiledModel(modelName);
        return model.getTypes();
    }


    public Collection<AspectDefinition> getAspects(QName modelName)
    {
        CompiledModel model = getCompiledModel(modelName);
        return model.getAspects();
    }


    public TypeDefinition getAnonymousType(QName type, Collection<QName> aspects)
    {
        TypeDefinition typeDef = getType(type);
        if (typeDef == null)
        {
            throw new DictionaryException("Failed to create anonymous type as specified type " + type + " not found");
        }
        Collection<AspectDefinition> aspectDefs = new ArrayList<AspectDefinition>();
        if (aspects != null)
        {
            for (QName aspect : aspects)
            {
                AspectDefinition aspectDef = getAspect(aspect);
                if (typeDef == null)
                {
                    throw new DictionaryException("Failed to create anonymous type as specified aspect " + aspect + " not found");
                }
                aspectDefs.add(aspectDef);
            }
        }
        return new M2AnonymousTypeDefinition(typeDef, aspectDefs);
    }


}
