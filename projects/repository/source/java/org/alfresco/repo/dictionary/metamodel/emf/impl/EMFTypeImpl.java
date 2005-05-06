/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.alfresco.repo.dictionary.metamodel.emf.impl;

import org.alfresco.repo.dictionary.metamodel.emf.EMFAspect;
import org.alfresco.repo.dictionary.metamodel.M2Aspect;
import org.alfresco.repo.dictionary.metamodel.M2Type;
import org.alfresco.repo.dictionary.metamodel.emf.EMFClass;
import org.alfresco.repo.dictionary.metamodel.emf.EMFType;
import org.alfresco.repo.dictionary.metamodel.emf.EmfPackage;

import org.alfresco.repo.ref.QName;

import java.util.Collection;
import java.util.List;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

import org.eclipse.emf.ecore.util.EObjectResolvingEList;
import org.eclipse.emf.ecore.util.InternalEList;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>EMF Type</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.alfresco.repo.dictionary.metamodel.emf.impl.EMFTypeImpl#getEmfOrderedChildren <em>Emf Ordered Children</em>}</li>
 *   <li>{@link org.alfresco.repo.dictionary.metamodel.emf.impl.EMFTypeImpl#getEmfDefaultAspects <em>Emf Default Aspects</em>}</li>
 *   <li>{@link org.alfresco.repo.dictionary.metamodel.emf.impl.EMFTypeImpl#getEmfStrict <em>Emf Strict</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class EMFTypeImpl extends EMFClassImpl implements EMFType, M2Type
{
    /**
     * The default value of the '{@link #getEmfOrderedChildren() <em>Emf Ordered Children</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getEmfOrderedChildren()
     * @generated
     * @ordered
     */
    protected static final Boolean EMF_ORDERED_CHILDREN_EDEFAULT = new Boolean(false);

    /**
     * The cached value of the '{@link #getEmfOrderedChildren() <em>Emf Ordered Children</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getEmfOrderedChildren()
     * @generated
     * @ordered
     */
    protected Boolean emfOrderedChildren = EMF_ORDERED_CHILDREN_EDEFAULT;

    /**
     * The cached value of the '{@link #getEmfDefaultAspects() <em>Emf Default Aspects</em>}' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getEmfDefaultAspects()
     * @generated
     * @ordered
     */
    protected EList emfDefaultAspects = null;

    /**
     * The default value of the '{@link #getEmfStrict() <em>Emf Strict</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getEmfStrict()
     * @generated
     * @ordered
     */
    protected static final Boolean EMF_STRICT_EDEFAULT = new Boolean(true);

    /**
     * The cached value of the '{@link #getEmfStrict() <em>Emf Strict</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getEmfStrict()
     * @generated
     * @ordered
     */
    protected Boolean emfStrict = EMF_STRICT_EDEFAULT;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected EMFTypeImpl()
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
        return EmfPackage.eINSTANCE.getEMFType();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public Boolean getEmfOrderedChildren()
    {
        return emfOrderedChildren;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setEmfOrderedChildren(Boolean newEmfOrderedChildren)
    {
        Boolean oldEmfOrderedChildren = emfOrderedChildren;
        emfOrderedChildren = newEmfOrderedChildren;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, EmfPackage.EMF_TYPE__EMF_ORDERED_CHILDREN, oldEmfOrderedChildren, emfOrderedChildren));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EList getEmfDefaultAspects()
    {
        if (emfDefaultAspects == null)
        {
            emfDefaultAspects = new EObjectResolvingEList(EMFAspect.class, this, EmfPackage.EMF_TYPE__EMF_DEFAULT_ASPECTS);
        }
        return emfDefaultAspects;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public Boolean getEmfStrict()
    {
        return emfStrict;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setEmfStrict(Boolean newEmfStrict)
    {
        Boolean oldEmfStrict = emfStrict;
        emfStrict = newEmfStrict;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, EmfPackage.EMF_TYPE__EMF_STRICT, oldEmfStrict, emfStrict));
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
                case EmfPackage.EMF_TYPE__EMF_PROPERTIES:
                    return ((InternalEList)getEmfProperties()).basicAdd(otherEnd, msgs);
                case EmfPackage.EMF_TYPE__EMF_ASSOCIATIONS:
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
                case EmfPackage.EMF_TYPE__EMF_PROPERTIES:
                    return ((InternalEList)getEmfProperties()).basicRemove(otherEnd, msgs);
                case EmfPackage.EMF_TYPE__EMF_ASSOCIATIONS:
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
            case EmfPackage.EMF_TYPE__EMF_NAME:
                return getEmfName();
            case EmfPackage.EMF_TYPE__EMF_PROPERTIES:
                return getEmfProperties();
            case EmfPackage.EMF_TYPE__EMF_SUPER_CLASS:
                if (resolve) return getEmfSuperClass();
                return basicGetEmfSuperClass();
            case EmfPackage.EMF_TYPE__EMF_ASSOCIATIONS:
                return getEmfAssociations();
            case EmfPackage.EMF_TYPE__EMF_ORDERED_CHILDREN:
                return getEmfOrderedChildren();
            case EmfPackage.EMF_TYPE__EMF_DEFAULT_ASPECTS:
                return getEmfDefaultAspects();
            case EmfPackage.EMF_TYPE__EMF_STRICT:
                return getEmfStrict();
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
            case EmfPackage.EMF_TYPE__EMF_NAME:
                setEmfName((QName)newValue);
                return;
            case EmfPackage.EMF_TYPE__EMF_PROPERTIES:
                getEmfProperties().clear();
                getEmfProperties().addAll((Collection)newValue);
                return;
            case EmfPackage.EMF_TYPE__EMF_SUPER_CLASS:
                setEmfSuperClass((EMFClass)newValue);
                return;
            case EmfPackage.EMF_TYPE__EMF_ASSOCIATIONS:
                getEmfAssociations().clear();
                getEmfAssociations().addAll((Collection)newValue);
                return;
            case EmfPackage.EMF_TYPE__EMF_ORDERED_CHILDREN:
                setEmfOrderedChildren((Boolean)newValue);
                return;
            case EmfPackage.EMF_TYPE__EMF_DEFAULT_ASPECTS:
                getEmfDefaultAspects().clear();
                getEmfDefaultAspects().addAll((Collection)newValue);
                return;
            case EmfPackage.EMF_TYPE__EMF_STRICT:
                setEmfStrict((Boolean)newValue);
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
            case EmfPackage.EMF_TYPE__EMF_NAME:
                setEmfName(EMF_NAME_EDEFAULT);
                return;
            case EmfPackage.EMF_TYPE__EMF_PROPERTIES:
                getEmfProperties().clear();
                return;
            case EmfPackage.EMF_TYPE__EMF_SUPER_CLASS:
                setEmfSuperClass((EMFClass)null);
                return;
            case EmfPackage.EMF_TYPE__EMF_ASSOCIATIONS:
                getEmfAssociations().clear();
                return;
            case EmfPackage.EMF_TYPE__EMF_ORDERED_CHILDREN:
                setEmfOrderedChildren(EMF_ORDERED_CHILDREN_EDEFAULT);
                return;
            case EmfPackage.EMF_TYPE__EMF_DEFAULT_ASPECTS:
                getEmfDefaultAspects().clear();
                return;
            case EmfPackage.EMF_TYPE__EMF_STRICT:
                setEmfStrict(EMF_STRICT_EDEFAULT);
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
            case EmfPackage.EMF_TYPE__EMF_NAME:
                return EMF_NAME_EDEFAULT == null ? emfName != null : !EMF_NAME_EDEFAULT.equals(emfName);
            case EmfPackage.EMF_TYPE__EMF_PROPERTIES:
                return emfProperties != null && !emfProperties.isEmpty();
            case EmfPackage.EMF_TYPE__EMF_SUPER_CLASS:
                return emfSuperClass != null;
            case EmfPackage.EMF_TYPE__EMF_ASSOCIATIONS:
                return emfAssociations != null && !emfAssociations.isEmpty();
            case EmfPackage.EMF_TYPE__EMF_ORDERED_CHILDREN:
                return EMF_ORDERED_CHILDREN_EDEFAULT == null ? emfOrderedChildren != null : !EMF_ORDERED_CHILDREN_EDEFAULT.equals(emfOrderedChildren);
            case EmfPackage.EMF_TYPE__EMF_DEFAULT_ASPECTS:
                return emfDefaultAspects != null && !emfDefaultAspects.isEmpty();
            case EmfPackage.EMF_TYPE__EMF_STRICT:
                return EMF_STRICT_EDEFAULT == null ? emfStrict != null : !EMF_STRICT_EDEFAULT.equals(emfStrict);
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
        result.append(" (emfOrderedChildren: ");
        result.append(emfOrderedChildren);
        result.append(", emfStrict: ");
        result.append(emfStrict);
        result.append(')');
        return result.toString();
    }

    /* (non-Javadoc)
     * @see org.alfresco.repo.dictionary.metamodel.M2Type#getOrderedChildren()
     */
    public boolean getOrderedChildren()
    {
        return getEmfOrderedChildren();
    }

    /* (non-Javadoc)
     * @see org.alfresco.repo.dictionary.metamodel.M2Type#setOrderedChildren(boolean)
     */
    public void setOrderedChildren(boolean areChildrenOrdered)
    {
        setEmfOrderedChildren(areChildrenOrdered);
    }

    /* (non-Javadoc)
     * @see org.alfresco.repo.dictionary.metamodel.M2Type#getDefaultAspects()
     */
    public List<M2Aspect> getDefaultAspects()
    {
        return (List)getEmfDefaultAspects();
    }
    
} //EMFTypeImpl
