package org.alfresco.repo.dictionary.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.alfresco.repo.dictionary.AspectDefinition;
import org.alfresco.repo.dictionary.AssociationDefinition;
import org.alfresco.repo.dictionary.ClassDefinition;
import org.alfresco.repo.dictionary.DictionaryException;
import org.alfresco.repo.dictionary.ModelDefinition;
import org.alfresco.repo.dictionary.PropertyDefinition;
import org.alfresco.repo.dictionary.PropertyTypeDefinition;
import org.alfresco.repo.dictionary.TypeDefinition;
import org.alfresco.repo.ref.DynamicNamespacePrefixResolver;
import org.alfresco.repo.ref.NamespaceException;
import org.alfresco.repo.ref.NamespacePrefixResolver;
import org.alfresco.repo.ref.QName;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/*package*/ class CompiledModel implements ModelQuery
{
    
    // Logger
    private static final Log logger = LogFactory.getLog(DictionaryDAOImpl.class);
    
    private M2Model model;
    private ModelDefinition modelDefinition;
    private Map<QName, PropertyTypeDefinition> propertyTypes = new HashMap<QName, PropertyTypeDefinition>();
    private Map<QName, ClassDefinition> classes = new HashMap<QName, ClassDefinition>();
    private Map<QName, TypeDefinition> types = new HashMap<QName, TypeDefinition>();
    private Map<QName, AspectDefinition> aspects = new HashMap<QName, AspectDefinition>();
    private Map<QName, PropertyDefinition> properties = new HashMap<QName, PropertyDefinition>();
    private Map<QName, AssociationDefinition> associations = new HashMap<QName, AssociationDefinition>();
    
    
    /*package*/ CompiledModel(M2Model model, DictionaryDAO dictionaryDAO, NamespaceDAO namespaceDAO)
    {
        try
        {
            // Phase 1: Construct model definitions from model entries
            //          resolving qualified names
            this.model = model;
            constructDefinitions(model, dictionaryDAO, namespaceDAO);
    
            // Phase 2: Resolve dependencies between model definitions
            ModelQuery query = new DelegateModelQuery(this, dictionaryDAO);
            resolveDependencies(query);
            
            // Phase 3: Resolve inheritance of values within class hierachy
            resolveInheritance(query);
        }
        catch(Exception e)
        {
            throw new DictionaryException("Failed to compile model " + model.getName(), e);
        }
    }

    
    /*package*/ M2Model getModel()
    {
        return model;
    }
    
    
    private void resolveInheritance(ModelQuery query)
    {
        // Calculate order of class processing (root to leaf)
        Map<Integer,List<ClassDefinition>> order = new TreeMap<Integer,List<ClassDefinition>>();
        for (ClassDefinition def : classes.values())
        {
            // Calculate class depth in hierarchy
            int depth = 0;
            QName parentName = def.getParentName();
            while (parentName != null)
            {
                ClassDefinition parentClass = getClass(parentName);
                if (parentClass == null)
                {
                    break;
                }
                depth = depth +1;
                parentName = parentClass.getParentName();
            }

            // Map class to depth
            List<ClassDefinition> classes = order.get(depth);
            if (classes == null)
            {
                classes = new ArrayList<ClassDefinition>();
                order.put(depth, classes);
            }
            classes.add(def);
            
            if (logger.isDebugEnabled())
                logger.debug("Resolving inheritance: class " + def.getName() + " found at depth " + depth);
        }
        
        // Resolve inheritance of each class
        for (int depth = 0; depth < order.size(); depth++)
        {
            for (ClassDefinition def : order.get(depth))
            {
                ((M2ClassDefinition)def).resolveInheritance(query);
            }
        }
    }
    
    
    private void resolveDependencies(ModelQuery query)
    {
        for (PropertyTypeDefinition def : propertyTypes.values())
        {
            ((M2PropertyTypeDefinition)def).resolveDependencies(query);
        }
        for (ClassDefinition def : classes.values())
        {
            ((M2ClassDefinition)def).resolveDependencies(query);
        }
    }
        

    private void constructDefinitions(M2Model model, DictionaryDAO dictionaryDAO, NamespaceDAO namespaceDAO)
    {
        NamespacePrefixResolver localPrefixes = createLocalPrefixResolver(model, namespaceDAO);
    
        // Construct Model Definition
        modelDefinition = M2ModelDefinition.create(model, localPrefixes);
        
        // Construct Property Types
        for (M2PropertyType propType : model.getPropertyTypes())
        {
            M2PropertyTypeDefinition def = new M2PropertyTypeDefinition(propType, localPrefixes);
            if (propertyTypes.containsKey(def.getName()))
            {
                throw new DictionaryException("Found duplicate property type definition " + propType.getName());
            }
            propertyTypes.put(def.getName(), def);
        }
        
        // Construct Type Definitions
        for (M2Type type : model.getTypes())
        {
            M2TypeDefinition def = new M2TypeDefinition(type, localPrefixes, properties, associations);
            if (classes.containsKey(def.getName()))
            {
                throw new DictionaryException("Found duplicate class definition " + type.getName() + " (a type)");
            }
            classes.put(def.getName(), def);
            types.put(def.getName(), def);
        }
        
        // Construct Aspect Definitions
        for (M2Aspect aspect : model.getAspects())
        {
            M2AspectDefinition def = new M2AspectDefinition(aspect, localPrefixes, properties, associations);
            if (classes.containsKey(def.getName()))
            {
                throw new DictionaryException("Found duplicate class definition " + aspect.getName() + " (an aspect)");
            }
            classes.put(def.getName(), def);
            aspects.put(def.getName(), def);
        }
    }    
    
    
    public ModelDefinition getModelDefinition()
    {
        return modelDefinition;
    }
    
    
    public PropertyTypeDefinition getPropertyType(QName name)
    {
        return propertyTypes.get(name);
    }


    public Collection<PropertyTypeDefinition> getPropertyTypes()
    {
        return propertyTypes.values();
    }

    public Collection<TypeDefinition> getTypes()
    {
        return types.values();
    }
    
    public Collection<AspectDefinition> getAspects()
    {
        return aspects.values();
    }
    
    public TypeDefinition getType(QName name)
    {
        return types.get(name);
    }


    public AspectDefinition getAspect(QName name)
    {
        return aspects.get(name);
    }


    public ClassDefinition getClass(QName name)
    {
        return classes.get(name);
    }
    
    public PropertyDefinition getProperty(QName name)
    {
        return properties.get(name);
    }

    public AssociationDefinition getAssociation(QName name)
    {
        return associations.get(name);
    }

    
    private NamespacePrefixResolver createLocalPrefixResolver(M2Model model, NamespaceDAO namespaceDAO)
    {
        // Retrieve set of existing URIs for validation purposes
        Collection<String> uris = namespaceDAO.getURIs();
        
        // Create a namespace prefix resolver based on imported and defined
        // namespaces within the model
        DynamicNamespacePrefixResolver prefixResolver = new DynamicNamespacePrefixResolver(null);
        for (M2Namespace imported : model.getImports())
        {
            String uri = imported.getUri();
            if (!uris.contains(uri))
            {
                throw new NamespaceException("URI " + uri + " cannot be imported as it is not defined (with prefix " + imported.getPrefix());
            }
            prefixResolver.addDynamicNamespace(imported.getPrefix(), uri);
        }
        for (M2Namespace defined : model.getNamespaces())
        {
            prefixResolver.addDynamicNamespace(defined.getPrefix(), defined.getUri());
        }
        return prefixResolver;
    }


    
}
