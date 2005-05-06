/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.alfresco.repo.dictionary.metamodel.emf.impl;

import org.alfresco.repo.dictionary.metamodel.M2Aspect;
import org.alfresco.repo.dictionary.metamodel.emf.EMFAspect;
import org.alfresco.repo.dictionary.metamodel.emf.EMFClass;
import org.alfresco.repo.dictionary.metamodel.emf.EmfPackage;

import org.alfresco.repo.ref.QName;

import java.util.Collection;

import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.util.InternalEList;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>EMF Aspect</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * </p>
 *
 * @generated
 */
public class EMFAspectImpl extends EMFClassImpl implements EMFAspect, M2Aspect
{
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected EMFAspectImpl()
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
        return EmfPackage.eINSTANCE.getEMFAspect();
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
                case EmfPackage.EMF_ASPECT__EMF_PROPERTIES:
                    return ((InternalEList)getEmfProperties()).basicAdd(otherEnd, msgs);
                case EmfPackage.EMF_ASPECT__EMF_ASSOCIATIONS:
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
                case EmfPackage.EMF_ASPECT__EMF_PROPERTIES:
                    return ((InternalEList)getEmfProperties()).basicRemove(otherEnd, msgs);
                case EmfPackage.EMF_ASPECT__EMF_ASSOCIATIONS:
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
            case EmfPackage.EMF_ASPECT__EMF_NAME:
                return getEmfName();
            case EmfPackage.EMF_ASPECT__EMF_PROPERTIES:
                return getEmfProperties();
            case EmfPackage.EMF_ASPECT__EMF_SUPER_CLASS:
                if (resolve) return getEmfSuperClass();
                return basicGetEmfSuperClass();
            case EmfPackage.EMF_ASPECT__EMF_ASSOCIATIONS:
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
            case EmfPackage.EMF_ASPECT__EMF_NAME:
                setEmfName((QName)newValue);
                return;
            case EmfPackage.EMF_ASPECT__EMF_PROPERTIES:
                getEmfProperties().clear();
                getEmfProperties().addAll((Collection)newValue);
                return;
            case EmfPackage.EMF_ASPECT__EMF_SUPER_CLASS:
                setEmfSuperClass((EMFClass)newValue);
                return;
            case EmfPackage.EMF_ASPECT__EMF_ASSOCIATIONS:
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
            case EmfPackage.EMF_ASPECT__EMF_NAME:
                setEmfName(EMF_NAME_EDEFAULT);
                return;
            case EmfPackage.EMF_ASPECT__EMF_PROPERTIES:
                getEmfProperties().clear();
                return;
            case EmfPackage.EMF_ASPECT__EMF_SUPER_CLASS:
                setEmfSuperClass((EMFClass)null);
                return;
            case EmfPackage.EMF_ASPECT__EMF_ASSOCIATIONS:
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
            case EmfPackage.EMF_ASPECT__EMF_NAME:
                return EMF_NAME_EDEFAULT == null ? emfName != null : !EMF_NAME_EDEFAULT.equals(emfName);
            case EmfPackage.EMF_ASPECT__EMF_PROPERTIES:
                return emfProperties != null && !emfProperties.isEmpty();
            case EmfPackage.EMF_ASPECT__EMF_SUPER_CLASS:
                return emfSuperClass != null;
            case EmfPackage.EMF_ASPECT__EMF_ASSOCIATIONS:
                return emfAssociations != null && !emfAssociations.isEmpty();
        }
        return eDynamicIsSet(eFeature);
    }

} //EMFAspectImpl
