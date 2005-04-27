package com.activiti.repo.dictionary.metamodel.emf;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;

import com.activiti.repo.dictionary.ClassRef;
import com.activiti.repo.dictionary.DictionaryException;
import com.activiti.repo.dictionary.PropertyRef;
import com.activiti.repo.dictionary.metamodel.M2Aspect;
import com.activiti.repo.dictionary.metamodel.M2Class;
import com.activiti.repo.dictionary.metamodel.M2Property;
import com.activiti.repo.dictionary.metamodel.M2PropertyType;
import com.activiti.repo.dictionary.metamodel.M2Type;
import com.activiti.repo.dictionary.metamodel.MetaModelDAO;
import com.activiti.repo.ref.QName;

/**
 * EMF implementation of Meta Model DAO
 * 
 * @author David Caruana
 */
public class EMFMetaModelDAO implements MetaModelDAO
{

    /**
     * Resource that holds Model Definitions
     */
    private Resource resource = null;

    /**
     * Index of QName to EMF Class
     */
    private Map<EClass, Map<QName, EObject>> classIndex;

    /**
     * Sets the Resource
     * 
     * @param resource
     *            the emf resource
     */
    public void setResource(EMFResource resource)
    {
        this.resource = resource.getResource();
    }

    /**
     * Initialise EMF DAO
     */
    public void init()
    {
        if (resource == null)
        {
            throw new DictionaryException("EMF Resource has not been provided");
        }

        // Initialise Object Index (for lookup)
        initIndex(resource);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.activiti.repo.dictionary.metamodel.MetaModelDAO#getTypes()
     */
    public Collection<QName> getTypes()
    {
        Map<QName, EObject> objectIndex = classIndex.get(EmfPackage.eINSTANCE.getEMFType());
        return objectIndex == null ? null : objectIndex.keySet();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.activiti.repo.dictionary.metamodel.MetaModelDAO#getClass(com.activiti.repo.ref.QName)
     */
    public M2Class getClass(QName className)
    {
        Map<QName, EObject> objectIndex = classIndex.get(EmfPackage.eINSTANCE.getEMFClass());
        return objectIndex == null ? null : (M2Class) objectIndex.get(className);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.activiti.repo.dictionary.metamodel.MetaModelDAO#getType(com.activiti.repo.ref.QName)
     */
    public M2Type getType(QName typeName)
    {
        Map<QName, EObject> objectIndex = classIndex.get(EmfPackage.eINSTANCE.getEMFType());
        return objectIndex == null ? null : (M2Type) objectIndex.get(typeName);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.activiti.repo.dictionary.metamodel.MetaModelDAO#getAspect(com.activiti.repo.ref.QName)
     */
    public M2Aspect getAspect(QName aspectName)
    {
        Map<QName, EObject> objectIndex = classIndex.get(EmfPackage.eINSTANCE.getEMFAspect());
        return objectIndex == null ? null : (M2Aspect) objectIndex.get(aspectName);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.activiti.repo.dictionary.metamodel.MetaModelDAO#getProperty(com.activiti.repo.ref.QName,
     *      java.lang.String)
     */
    public M2Property getProperty(QName className, String propertyName)
    {
        M2Class m2Class = getClass(className);
        if (m2Class != null)
        {
            List<M2Property> properties = m2Class.getProperties();
            for (M2Property property : properties)
            {
                if (property.getName().equals(propertyName))
                {
                    return property;
                }
            }
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.activiti.repo.dictionary.metamodel.MetaModelDAO#getPropertyType(com.activiti.repo.ref.QName)
     */
    public M2PropertyType getPropertyType(QName propertyType)
    {
        Map<QName, EObject> idIndex = classIndex.get(EmfPackage.eINSTANCE.getEMFPropertyType());
        return idIndex == null ? null : (M2PropertyType) idIndex.get(propertyType);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.activiti.repo.dictionary.metamodel.MetaModelDAO#createPropertyType(com.activiti.repo.ref.QName)
     */
    public M2PropertyType createPropertyType(QName typeName)
    {
        EmfFactory factory = EmfFactory.eINSTANCE;
        M2PropertyType type = (M2PropertyType) factory.createEMFPropertyType();
        type.setQName(typeName);
        resource.getContents().add(type);
        indexObject((EObject) type);
        return type;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.activiti.repo.dictionary.metamodel.MetaModelDAO#createType(com.activiti.repo.ref.QName)
     */
    public M2Type createType(QName typeName)
    {
        EmfFactory factory = EmfFactory.eINSTANCE;
        M2Type type = (M2Type) factory.createEMFType();
        type.setQName(typeName);
        resource.getContents().add(type);
        indexObject((EObject) type);
        return type;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.activiti.repo.dictionary.metamodel.MetaModelDAO#createAspect(com.activiti.repo.ref.QName)
     */
    public M2Aspect createAspect(QName aspectName)
    {
        EmfFactory factory = EmfFactory.eINSTANCE;
        M2Aspect aspect = (M2Aspect) factory.createEMFAspect();
        aspect.setQName(aspectName);
        resource.getContents().add(aspect);
        indexObject((EObject) aspect);
        return aspect;
    }

    //
    // EMF Class and Object Indexes (by QName)
    //

    private void initIndex(Resource resource)
    {
        classIndex = new HashMap<EClass, Map<QName, EObject>>();
        List<EObject> objects = resource.getContents();
        for (EObject eObject : objects)
        {
            indexObject(eObject);
        }
    }

    private void indexObject(EObject object)
    {
        // Index Object
        if (object instanceof M2Type)
        {
            M2Type m2Type = (M2Type) object;
            getObjectIndex(EmfPackage.eINSTANCE.getEMFClass()).put(m2Type.getQName(), object);
            getObjectIndex(EmfPackage.eINSTANCE.getEMFType()).put(m2Type.getQName(), object);
        }
        else if (object instanceof M2Aspect)
        {
            M2Aspect m2Aspect = (M2Aspect) object;
            getObjectIndex(EmfPackage.eINSTANCE.getEMFClass()).put(m2Aspect.getQName(), object);
            getObjectIndex(EmfPackage.eINSTANCE.getEMFAspect()).put(m2Aspect.getQName(), object);
        }
        else if (object instanceof M2PropertyType)
        {
            M2PropertyType m2PropertyType = (M2PropertyType) object;
            getObjectIndex(EmfPackage.eINSTANCE.getEMFPropertyType()).put(m2PropertyType.getQName(), object);
        }

        // Index all properties

        if (object instanceof M2Class)
        {
            M2Class m2Class = (M2Class) object;
            List<M2Property> properties = m2Class.getProperties();
            for (M2Property property : properties)
            {
                PropertyRef ref = new PropertyRef(new ClassRef(m2Class.getQName()), property.getName());
                if (property instanceof EMFProperty)
                {
                    EMFProperty emfProperty = (EMFProperty) property;
                    getObjectIndex(EmfPackage.eINSTANCE.getEMFProperty()).put(ref.getQName(), emfProperty);
                }
            }
        }
    }

    private Map<QName, EObject> getObjectIndex(EClass eClass)
    {
        Map<QName, EObject> objectIndex = classIndex.get(eClass);
        if (objectIndex == null)
        {
            objectIndex = new HashMap<QName, EObject>();
            classIndex.put(eClass, objectIndex);
        }
        return objectIndex;
    }

    public M2Property getProperty(QName propertyName)
    {

        Map<QName, EObject> idIndex = classIndex.get(EmfPackage.eINSTANCE.getEMFProperty());
        return idIndex == null ? null : (M2Property) idIndex.get(propertyName);

    }

}
