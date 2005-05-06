/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.alfresco.repo.dictionary.metamodel.emf.impl;

import java.util.Collection;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;

import org.alfresco.repo.dictionary.metamodel.M2ChildAssociation;
import org.alfresco.repo.dictionary.metamodel.M2Type;
import org.alfresco.repo.dictionary.metamodel.emf.EMFChildAssociation;
import org.alfresco.repo.dictionary.metamodel.emf.EMFClass;
import org.alfresco.repo.dictionary.metamodel.emf.EMFType;
import org.alfresco.repo.dictionary.metamodel.emf.EmfPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>EMF Child Association</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.alfresco.repo.dictionary.metamodel.emf.impl.EMFChildAssociationImpl#getEmfDefaultType <em>Emf Default Type</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class EMFChildAssociationImpl extends EMFAssociationImpl implements EMFChildAssociation, M2ChildAssociation
{
    /**
     * The cached value of the '{@link #getEmfDefaultType() <em>Emf Default Type</em>}' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getEmfDefaultType()
     * @generated
     * @ordered
     */
    protected EMFType emfDefaultType = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected EMFChildAssociationImpl()
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
        return EmfPackage.eINSTANCE.getEMFChildAssociation();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EMFType getEmfDefaultType()
    {
        if (emfDefaultType != null && emfDefaultType.eIsProxy())
        {
            EMFType oldEmfDefaultType = emfDefaultType;
            emfDefaultType = (EMFType)eResolveProxy((InternalEObject)emfDefaultType);
            if (emfDefaultType != oldEmfDefaultType)
            {
                if (eNotificationRequired())
                    eNotify(new ENotificationImpl(this, Notification.RESOLVE, EmfPackage.EMF_CHILD_ASSOCIATION__EMF_DEFAULT_TYPE, oldEmfDefaultType, emfDefaultType));
            }
        }
        return emfDefaultType;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EMFType basicGetEmfDefaultType()
    {
        return emfDefaultType;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setEmfDefaultType(EMFType newEmfDefaultType)
    {
        EMFType oldEmfDefaultType = emfDefaultType;
        emfDefaultType = newEmfDefaultType;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, EmfPackage.EMF_CHILD_ASSOCIATION__EMF_DEFAULT_TYPE, oldEmfDefaultType, emfDefaultType));
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
                case EmfPackage.EMF_CHILD_ASSOCIATION__EMF_CONTAINER_CLASS:
                    if (eContainer != null)
                        msgs = eBasicRemoveFromContainer(msgs);
                    return eBasicSetContainer(otherEnd, EmfPackage.EMF_CHILD_ASSOCIATION__EMF_CONTAINER_CLASS, msgs);
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
                case EmfPackage.EMF_CHILD_ASSOCIATION__EMF_CONTAINER_CLASS:
                    return eBasicSetContainer(null, EmfPackage.EMF_CHILD_ASSOCIATION__EMF_CONTAINER_CLASS, msgs);
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
    public NotificationChain eBasicRemoveFromContainer(NotificationChain msgs)
    {
        if (eContainerFeatureID >= 0)
        {
            switch (eContainerFeatureID)
            {
                case EmfPackage.EMF_CHILD_ASSOCIATION__EMF_CONTAINER_CLASS:
                    return eContainer.eInverseRemove(this, EmfPackage.EMF_CLASS__EMF_ASSOCIATIONS, EMFClass.class, msgs);
                default:
                    return eDynamicBasicRemoveFromContainer(msgs);
            }
        }
        return eContainer.eInverseRemove(this, EOPPOSITE_FEATURE_BASE - eContainerFeatureID, null, msgs);
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
            case EmfPackage.EMF_CHILD_ASSOCIATION__EMF_NAME:
                return getEmfName();
            case EmfPackage.EMF_CHILD_ASSOCIATION__EMF_CONTAINER_CLASS:
                return getEmfContainerClass();
            case EmfPackage.EMF_CHILD_ASSOCIATION__EMF_PROTECTED:
                return getEmfProtected();
            case EmfPackage.EMF_CHILD_ASSOCIATION__EMF_MANDATORY:
                return getEmfMandatory();
            case EmfPackage.EMF_CHILD_ASSOCIATION__EMF_MULTIPLE:
                return getEmfMultiple();
            case EmfPackage.EMF_CHILD_ASSOCIATION__EMF_REQUIRED_TO_CLASSES:
                return getEmfRequiredToClasses();
            case EmfPackage.EMF_CHILD_ASSOCIATION__EMF_DEFAULT_TYPE:
                if (resolve) return getEmfDefaultType();
                return basicGetEmfDefaultType();
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
            case EmfPackage.EMF_CHILD_ASSOCIATION__EMF_NAME:
                setEmfName((String)newValue);
                return;
            case EmfPackage.EMF_CHILD_ASSOCIATION__EMF_PROTECTED:
                setEmfProtected((Boolean)newValue);
                return;
            case EmfPackage.EMF_CHILD_ASSOCIATION__EMF_MANDATORY:
                setEmfMandatory((Boolean)newValue);
                return;
            case EmfPackage.EMF_CHILD_ASSOCIATION__EMF_MULTIPLE:
                setEmfMultiple((Boolean)newValue);
                return;
            case EmfPackage.EMF_CHILD_ASSOCIATION__EMF_REQUIRED_TO_CLASSES:
                getEmfRequiredToClasses().clear();
                getEmfRequiredToClasses().addAll((Collection)newValue);
                return;
            case EmfPackage.EMF_CHILD_ASSOCIATION__EMF_DEFAULT_TYPE:
                setEmfDefaultType((EMFType)newValue);
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
            case EmfPackage.EMF_CHILD_ASSOCIATION__EMF_NAME:
                setEmfName(EMF_NAME_EDEFAULT);
                return;
            case EmfPackage.EMF_CHILD_ASSOCIATION__EMF_PROTECTED:
                setEmfProtected(EMF_PROTECTED_EDEFAULT);
                return;
            case EmfPackage.EMF_CHILD_ASSOCIATION__EMF_MANDATORY:
                setEmfMandatory(EMF_MANDATORY_EDEFAULT);
                return;
            case EmfPackage.EMF_CHILD_ASSOCIATION__EMF_MULTIPLE:
                setEmfMultiple(EMF_MULTIPLE_EDEFAULT);
                return;
            case EmfPackage.EMF_CHILD_ASSOCIATION__EMF_REQUIRED_TO_CLASSES:
                getEmfRequiredToClasses().clear();
                return;
            case EmfPackage.EMF_CHILD_ASSOCIATION__EMF_DEFAULT_TYPE:
                setEmfDefaultType((EMFType)null);
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
            case EmfPackage.EMF_CHILD_ASSOCIATION__EMF_NAME:
                return EMF_NAME_EDEFAULT == null ? emfName != null : !EMF_NAME_EDEFAULT.equals(emfName);
            case EmfPackage.EMF_CHILD_ASSOCIATION__EMF_CONTAINER_CLASS:
                return getEmfContainerClass() != null;
            case EmfPackage.EMF_CHILD_ASSOCIATION__EMF_PROTECTED:
                return EMF_PROTECTED_EDEFAULT == null ? emfProtected != null : !EMF_PROTECTED_EDEFAULT.equals(emfProtected);
            case EmfPackage.EMF_CHILD_ASSOCIATION__EMF_MANDATORY:
                return EMF_MANDATORY_EDEFAULT == null ? emfMandatory != null : !EMF_MANDATORY_EDEFAULT.equals(emfMandatory);
            case EmfPackage.EMF_CHILD_ASSOCIATION__EMF_MULTIPLE:
                return EMF_MULTIPLE_EDEFAULT == null ? emfMultiple != null : !EMF_MULTIPLE_EDEFAULT.equals(emfMultiple);
            case EmfPackage.EMF_CHILD_ASSOCIATION__EMF_REQUIRED_TO_CLASSES:
                return emfRequiredToClasses != null && !emfRequiredToClasses.isEmpty();
            case EmfPackage.EMF_CHILD_ASSOCIATION__EMF_DEFAULT_TYPE:
                return emfDefaultType != null;
        }
        return eDynamicIsSet(eFeature);
    }

    /* (non-Javadoc)
     * @see org.alfresco.repo.dictionary.metamodel.M2ChildAssociation#getDefaultType()
     */
    public M2Type getDefaultType()
    {
        return (M2Type)getEmfDefaultType();
    }

    /* (non-Javadoc)
     * @see org.alfresco.repo.dictionary.metamodel.M2ChildAssociation#setDefaultType(org.alfresco.repo.dictionary.metamodel.M2Type)
     */
    public void setDefaultType(M2Type defaultType)
    {
        setEmfDefaultType((EMFType)defaultType);
    }

} //EMFChildAssociationImpl
