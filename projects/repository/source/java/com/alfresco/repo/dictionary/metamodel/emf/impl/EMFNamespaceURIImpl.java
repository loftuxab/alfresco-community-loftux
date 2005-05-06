/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.alfresco.repo.dictionary.metamodel.emf.impl;

import org.alfresco.repo.dictionary.metamodel.M2NamespacePrefix;
import org.alfresco.repo.dictionary.metamodel.M2NamespaceURI;
import org.alfresco.repo.dictionary.metamodel.emf.EMFNamespacePrefix;
import org.alfresco.repo.dictionary.metamodel.emf.EMFNamespaceURI;
import org.alfresco.repo.dictionary.metamodel.emf.EmfPackage;

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

import org.eclipse.emf.ecore.util.EObjectWithInverseEList;
import org.eclipse.emf.ecore.util.EObjectResolvingEList;

import org.eclipse.emf.ecore.util.EObjectWithInverseResolvingEList;
import org.eclipse.emf.ecore.util.InternalEList;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>EMF Namespace URI</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.alfresco.repo.dictionary.metamodel.emf.impl.EMFNamespaceURIImpl#getEmfURI <em>Emf URI</em>}</li>
 *   <li>{@link org.alfresco.repo.dictionary.metamodel.emf.impl.EMFNamespaceURIImpl#getEmfPrefixes <em>Emf Prefixes</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class EMFNamespaceURIImpl extends EObjectImpl implements EMFNamespaceURI, M2NamespaceURI
{
    /**
     * The default value of the '{@link #getEmfURI() <em>Emf URI</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getEmfURI()
     * @generated
     * @ordered
     */
    protected static final String EMF_URI_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getEmfURI() <em>Emf URI</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getEmfURI()
     * @generated
     * @ordered
     */
    protected String emfURI = EMF_URI_EDEFAULT;

    /**
     * The cached value of the '{@link #getEmfPrefixes() <em>Emf Prefixes</em>}' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getEmfPrefixes()
     * @generated
     * @ordered
     */
    protected EList emfPrefixes = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected EMFNamespaceURIImpl()
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
        return EmfPackage.eINSTANCE.getEMFNamespaceURI();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String getEmfURI()
    {
        return emfURI;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setEmfURI(String newEmfURI)
    {
        String oldEmfURI = emfURI;
        emfURI = newEmfURI;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, EmfPackage.EMF_NAMESPACE_URI__EMF_URI, oldEmfURI, emfURI));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EList getEmfPrefixes()
    {
        if (emfPrefixes == null)
        {
            emfPrefixes = new EObjectWithInverseResolvingEList(EMFNamespacePrefix.class, this, EmfPackage.EMF_NAMESPACE_URI__EMF_PREFIXES, EmfPackage.EMF_NAMESPACE_PREFIX__EMF_URI);
        }
        return emfPrefixes;
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
                case EmfPackage.EMF_NAMESPACE_URI__EMF_PREFIXES:
                    return ((InternalEList)getEmfPrefixes()).basicAdd(otherEnd, msgs);
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
                case EmfPackage.EMF_NAMESPACE_URI__EMF_PREFIXES:
                    return ((InternalEList)getEmfPrefixes()).basicRemove(otherEnd, msgs);
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
            case EmfPackage.EMF_NAMESPACE_URI__EMF_URI:
                return getEmfURI();
            case EmfPackage.EMF_NAMESPACE_URI__EMF_PREFIXES:
                return getEmfPrefixes();
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
            case EmfPackage.EMF_NAMESPACE_URI__EMF_URI:
                setEmfURI((String)newValue);
                return;
            case EmfPackage.EMF_NAMESPACE_URI__EMF_PREFIXES:
                getEmfPrefixes().clear();
                getEmfPrefixes().addAll((Collection)newValue);
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
            case EmfPackage.EMF_NAMESPACE_URI__EMF_URI:
                setEmfURI(EMF_URI_EDEFAULT);
                return;
            case EmfPackage.EMF_NAMESPACE_URI__EMF_PREFIXES:
                getEmfPrefixes().clear();
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
            case EmfPackage.EMF_NAMESPACE_URI__EMF_URI:
                return EMF_URI_EDEFAULT == null ? emfURI != null : !EMF_URI_EDEFAULT.equals(emfURI);
            case EmfPackage.EMF_NAMESPACE_URI__EMF_PREFIXES:
                return emfPrefixes != null && !emfPrefixes.isEmpty();
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
        result.append(" (emfURI: ");
        result.append(emfURI);
        result.append(')');
        return result.toString();
    }

    /* (non-Javadoc)
     * @see org.alfresco.repo.dictionary.metamodel.M2NamespaceURI#getURI()
     */
    public String getURI()
    {
        return getEmfURI();
    }

    /* (non-Javadoc)
     * @see org.alfresco.repo.dictionary.metamodel.M2NamespaceURI#setURI(java.lang.String)
     */
    public void setURI(String uri)
    {
        setEmfURI(uri);
    }

    /* (non-Javadoc)
     * @see org.alfresco.repo.dictionary.metamodel.M2NamespaceURI#getPrefixes()
     */
    public List<M2NamespacePrefix> getPrefixes()
    {
        return (List)getEmfPrefixes();
    }

} //EMFNamespaceURIImpl
