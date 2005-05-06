/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.alfresco.repo.dictionary.metamodel.emf.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.emf.ecore.util.EObjectContainmentWithInverseEList;
import org.eclipse.emf.ecore.util.InternalEList;

import org.alfresco.repo.dictionary.ClassDefinition;
import org.alfresco.repo.dictionary.metamodel.M2Association;
import org.alfresco.repo.dictionary.metamodel.M2ChildAssociation;
import org.alfresco.repo.dictionary.metamodel.M2Class;
import org.alfresco.repo.dictionary.metamodel.M2ClassDefinition;
import org.alfresco.repo.dictionary.metamodel.M2Property;
import org.alfresco.repo.dictionary.metamodel.emf.EMFAssociation;
import org.alfresco.repo.dictionary.metamodel.emf.EMFClass;
import org.alfresco.repo.dictionary.metamodel.emf.EMFProperty;
import org.alfresco.repo.dictionary.metamodel.emf.EmfFactory;
import org.alfresco.repo.dictionary.metamodel.emf.EmfPackage;
import org.alfresco.repo.ref.QName;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>EMF Class</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.alfresco.repo.dictionary.metamodel.emf.impl.EMFClassImpl#getEmfName <em>Emf Name</em>}</li>
 *   <li>{@link org.alfresco.repo.dictionary.metamodel.emf.impl.EMFClassImpl#getEmfProperties <em>Emf Properties</em>}</li>
 *   <li>{@link org.alfresco.repo.dictionary.metamodel.emf.impl.EMFClassImpl#getEmfSuperClass <em>Emf Super Class</em>}</li>
 *   <li>{@link org.alfresco.repo.dictionary.metamodel.emf.impl.EMFClassImpl#getEmfAssociations <em>Emf Associations</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public abstract class EMFClassImpl extends EObjectImpl implements EMFClass, M2Class
{
    /**
     * The default value of the '{@link #getEmfName() <em>Emf Name</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getEmfName()
     * @generated
     * @ordered
     */
    protected static final QName EMF_NAME_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getEmfName() <em>Emf Name</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getEmfName()
     * @generated
     * @ordered
     */
    protected QName emfName = EMF_NAME_EDEFAULT;

    /**
     * The cached value of the '{@link #getEmfProperties() <em>Emf Properties</em>}' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getEmfProperties()
     * @generated
     * @ordered
     */
    protected EList emfProperties = null;

    /**
     * The cached value of the '{@link #getEmfSuperClass() <em>Emf Super Class</em>}' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getEmfSuperClass()
     * @generated
     * @ordered
     */
    protected EMFClass emfSuperClass = null;

    
    /**
     * The cached value of the '{@link #getEmfAssociations() <em>Emf Associations</em>}' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getEmfAssociations()
     * @generated
     * @ordered
     */
    protected EList emfAssociations = null;

    /**
     * Read-only Class Definition
     */
    protected ClassDefinition classDefinition = null;
    

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected EMFClassImpl()
    {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected EClass eStaticClass()
    {
        return EmfPackage.eINSTANCE.getEMFClass();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public QName getEmfName()
    {
        return emfName;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setEmfName(QName newEmfName)
    {
        QName oldEmfName = emfName;
        emfName = newEmfName;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, EmfPackage.EMF_CLASS__EMF_NAME, oldEmfName, emfName));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EList getEmfProperties()
    {
        if (emfProperties == null)
        {
            emfProperties = new EObjectContainmentWithInverseEList(EMFProperty.class, this, EmfPackage.EMF_CLASS__EMF_PROPERTIES, EmfPackage.EMF_PROPERTY__EMF_CONTAINER_CLASS);
        }
        return emfProperties;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EMFClass getEmfSuperClass()
    {
        if (emfSuperClass != null && emfSuperClass.eIsProxy())
        {
            EMFClass oldEmfSuperClass = emfSuperClass;
            emfSuperClass = (EMFClass)eResolveProxy((InternalEObject)emfSuperClass);
            if (emfSuperClass != oldEmfSuperClass)
            {
                if (eNotificationRequired())
                    eNotify(new ENotificationImpl(this, Notification.RESOLVE, EmfPackage.EMF_CLASS__EMF_SUPER_CLASS, oldEmfSuperClass, emfSuperClass));
            }
        }
        return emfSuperClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EMFClass basicGetEmfSuperClass()
    {
        return emfSuperClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setEmfSuperClass(EMFClass newEmfSuperClass)
    {
        EMFClass oldEmfSuperClass = emfSuperClass;
        emfSuperClass = newEmfSuperClass;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, EmfPackage.EMF_CLASS__EMF_SUPER_CLASS, oldEmfSuperClass, emfSuperClass));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EList getEmfAssociations()
    {
        if (emfAssociations == null)
        {
            emfAssociations = new EObjectContainmentWithInverseEList(EMFAssociation.class, this, EmfPackage.EMF_CLASS__EMF_ASSOCIATIONS, EmfPackage.EMF_ASSOCIATION__EMF_CONTAINER_CLASS);
        }
        return emfAssociations;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, Class baseClass, NotificationChain msgs)
    {
        if (featureID >= 0)
        {
            switch (eDerivedStructuralFeatureID(featureID, baseClass))
            {
                case EmfPackage.EMF_CLASS__EMF_PROPERTIES:
                    return ((InternalEList)getEmfProperties()).basicAdd(otherEnd, msgs);
                case EmfPackage.EMF_CLASS__EMF_ASSOCIATIONS:
                    return ((InternalEList)getEmfAssociations()).basicAdd(otherEnd, msgs);
                default:
                    return eDynamicInverseAdd(otherEnd, featureID, baseClass, msgs);
            }
        }
        if (eContainer != null)
            msgs = eBasicRemoveFromContainer(msgs);
        return eBasicSetContainer(otherEnd, featureID, msgs);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, Class baseClass, NotificationChain msgs)
    {
        if (featureID >= 0)
        {
            switch (eDerivedStructuralFeatureID(featureID, baseClass))
            {
                case EmfPackage.EMF_CLASS__EMF_PROPERTIES:
                    return ((InternalEList)getEmfProperties()).basicRemove(otherEnd, msgs);
                case EmfPackage.EMF_CLASS__EMF_ASSOCIATIONS:
                    return ((InternalEList)getEmfAssociations()).basicRemove(otherEnd, msgs);
                default:
                    return eDynamicInverseRemove(otherEnd, featureID, baseClass, msgs);
            }
        }
        return eBasicSetContainer(null, featureID, msgs);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public Object eGet(EStructuralFeature eFeature, boolean resolve)
    {
        switch (eDerivedStructuralFeatureID(eFeature))
        {
            case EmfPackage.EMF_CLASS__EMF_NAME:
                return getEmfName();
            case EmfPackage.EMF_CLASS__EMF_PROPERTIES:
                return getEmfProperties();
            case EmfPackage.EMF_CLASS__EMF_SUPER_CLASS:
                if (resolve) return getEmfSuperClass();
                return basicGetEmfSuperClass();
            case EmfPackage.EMF_CLASS__EMF_ASSOCIATIONS:
                return getEmfAssociations();
        }
        return eDynamicGet(eFeature, resolve);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void eSet(EStructuralFeature eFeature, Object newValue)
    {
        switch (eDerivedStructuralFeatureID(eFeature))
        {
            case EmfPackage.EMF_CLASS__EMF_NAME:
                setEmfName((QName)newValue);
                return;
            case EmfPackage.EMF_CLASS__EMF_PROPERTIES:
                getEmfProperties().clear();
                getEmfProperties().addAll((Collection)newValue);
                return;
            case EmfPackage.EMF_CLASS__EMF_SUPER_CLASS:
                setEmfSuperClass((EMFClass)newValue);
                return;
            case EmfPackage.EMF_CLASS__EMF_ASSOCIATIONS:
                getEmfAssociations().clear();
                getEmfAssociations().addAll((Collection)newValue);
                return;
        }
        eDynamicSet(eFeature, newValue);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void eUnset(EStructuralFeature eFeature)
    {
        switch (eDerivedStructuralFeatureID(eFeature))
        {
            case EmfPackage.EMF_CLASS__EMF_NAME:
                setEmfName(EMF_NAME_EDEFAULT);
                return;
            case EmfPackage.EMF_CLASS__EMF_PROPERTIES:
                getEmfProperties().clear();
                return;
            case EmfPackage.EMF_CLASS__EMF_SUPER_CLASS:
                setEmfSuperClass((EMFClass)null);
                return;
            case EmfPackage.EMF_CLASS__EMF_ASSOCIATIONS:
                getEmfAssociations().clear();
                return;
        }
        eDynamicUnset(eFeature);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public boolean eIsSet(EStructuralFeature eFeature)
    {
        switch (eDerivedStructuralFeatureID(eFeature))
        {
            case EmfPackage.EMF_CLASS__EMF_NAME:
                return EMF_NAME_EDEFAULT == null ? emfName != null : !EMF_NAME_EDEFAULT.equals(emfName);
            case EmfPackage.EMF_CLASS__EMF_PROPERTIES:
                return emfProperties != null && !emfProperties.isEmpty();
            case EmfPackage.EMF_CLASS__EMF_SUPER_CLASS:
                return emfSuperClass != null;
            case EmfPackage.EMF_CLASS__EMF_ASSOCIATIONS:
                return emfAssociations != null && !emfAssociations.isEmpty();
        }
        return eDynamicIsSet(eFeature);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String toString()
    {
        if (eIsProxy()) return super.toString();

        StringBuffer result = new StringBuffer(super.toString());
        result.append(" (emfName: ");
        result.append(emfName);
        result.append(')');
        return result.toString();
    }

    /* (non-Javadoc)
     * @see org.alfresco.repo.dictionary.metamodel.M2Class#getClassDefinition()
     */
    public ClassDefinition getClassDefinition()
    {
        if (classDefinition == null)
        {
            classDefinition = M2ClassDefinition.create(this);
        }
        return classDefinition;
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.repo.dictionary.metamodel.M2Class#getName()
     */
    public QName getQName()
    {
        return getEmfName();
    }

    /* (non-Javadoc)
     * @see org.alfresco.repo.dictionary.metamodel.M2Class#setName(org.alfresco.repo.ref.QName)
     */
    public void setQName(QName qname)
    {
        setEmfName(qname);
    }


    /* (non-Javadoc)
     * @see org.alfresco.repo.dictionary.metamodel.M2Class#getSuperClass()
     */
    public M2Class getSuperClass()
    {
        return (M2Class)getEmfSuperClass();
    }

    /* (non-Javadoc)
     * @see org.alfresco.repo.dictionary.metamodel.M2Class#setSuperClass(org.alfresco.repo.dictionary.metamodel.M2Class)
     */
    public void setSuperClass(M2Class superClass)
    {
        setEmfSuperClass((EMFClass)superClass);
    }

    /* (non-Javadoc)
     * @see org.alfresco.repo.dictionary.metamodel.M2Class#createProperty(java.lang.String)
     */
    public M2Property createProperty(String propertyName)
    {
        EmfFactory factory = EmfFactory.eINSTANCE;
        M2Property property = (M2Property)factory.createEMFProperty();
        property.setName(propertyName);
        getProperties().add(property);
        return property;
    }
        
    /* (non-Javadoc)
     * @see org.alfresco.repo.dictionary.metamodel.M2Class#getProperties()
     */
    public List<M2Property> getProperties()
    {
        return getEmfProperties();
    }

    /* (non-Javadoc)
     * @see org.alfresco.repo.dictionary.metamodel.M2Class#getInheritedProperties()
     */
    public List<M2Property> getInheritedProperties()
    {
        List<M2Property> inheritedProperties = new ArrayList<M2Property>();
        deriveInheritedProperties(this, inheritedProperties);
        return inheritedProperties;
    }

    private void deriveInheritedProperties(M2Class thisClass, List<M2Property> properties)
    {
        M2Class superClass = thisClass.getSuperClass();
        if (superClass != null)
        {
            deriveInheritedProperties(superClass, properties);
        }
        properties.addAll(thisClass.getProperties());
    }

    /* (non-Javadoc)
     * @see org.alfresco.repo.dictionary.metamodel.M2Class#createChildAssociation(java.lang.String)
     */
    public M2ChildAssociation createChildAssociation(String associationName)
    {
        EmfFactory factory = EmfFactory.eINSTANCE;
        M2ChildAssociation association = (M2ChildAssociation)factory.createEMFChildAssociation();
        association.setName(associationName);
        getAssociations().add(association);
        return association;
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.repo.dictionary.metamodel.M2Class#createAssociation(java.lang.String)
     */
    public M2Association createAssociation(String associationName)
    {
        EmfFactory factory = EmfFactory.eINSTANCE;
        M2Association association = (M2Association)factory.createEMFAssociation();
        association.setName(associationName);
        getAssociations().add(association);
        return association;
    }

    /* (non-Javadoc)
     * @see org.alfresco.repo.dictionary.metamodel.M2Class#getAssociations()
     */
    public List<M2Association> getAssociations()
    {
        return getEmfAssociations();
    }

    /* (non-Javadoc)
     * @see org.alfresco.repo.dictionary.metamodel.M2Class#getInheritedAssociations()
     */
    public List<M2Association> getInheritedAssociations()
    {
        List<M2Association> inheritedAssociations = new ArrayList<M2Association>();
        deriveInheritedAssociations(this, inheritedAssociations);
        return inheritedAssociations;
    }

    private void deriveInheritedAssociations(M2Class thisClass, List<M2Association> associations)
    {
        M2Class superClass = thisClass.getSuperClass();
        if (superClass != null)
        {
            deriveInheritedAssociations(superClass, associations);
        }
        associations.addAll(thisClass.getAssociations());
    }
    
} //EMFClassImpl
