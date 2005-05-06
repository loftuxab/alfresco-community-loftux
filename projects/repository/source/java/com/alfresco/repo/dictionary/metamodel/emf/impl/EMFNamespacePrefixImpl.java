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

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>EMF Namespace Prefix</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.alfresco.repo.dictionary.metamodel.emf.impl.EMFNamespacePrefixImpl#getEmfPrefix <em>Emf Prefix</em>}</li>
 *   <li>{@link org.alfresco.repo.dictionary.metamodel.emf.impl.EMFNamespacePrefixImpl#getEmfURI <em>Emf URI</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class EMFNamespacePrefixImpl extends EObjectImpl implements EMFNamespacePrefix, M2NamespacePrefix
{
    /**
     * The default value of the '{@link #getEmfPrefix() <em>Emf Prefix</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getEmfPrefix()
     * @generated
     * @ordered
     */
    protected static final String EMF_PREFIX_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getEmfPrefix() <em>Emf Prefix</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getEmfPrefix()
     * @generated
     * @ordered
     */
    protected String emfPrefix = EMF_PREFIX_EDEFAULT;

    /**
     * The cached value of the '{@link #getEmfURI() <em>Emf URI</em>}' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getEmfURI()
     * @generated
     * @ordered
     */
    protected EMFNamespaceURI emfURI = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected EMFNamespacePrefixImpl()
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
        return EmfPackage.eINSTANCE.getEMFNamespacePrefix();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String getEmfPrefix()
    {
        return emfPrefix;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setEmfPrefix(String newEmfPrefix)
    {
        String oldEmfPrefix = emfPrefix;
        emfPrefix = newEmfPrefix;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, EmfPackage.EMF_NAMESPACE_PREFIX__EMF_PREFIX, oldEmfPrefix, emfPrefix));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EMFNamespaceURI getEmfURI()
    {
        if (emfURI != null && emfURI.eIsProxy())
        {
            EMFNamespaceURI oldEmfURI = emfURI;
            emfURI = (EMFNamespaceURI)eResolveProxy((InternalEObject)emfURI);
            if (emfURI != oldEmfURI)
            {
                if (eNotificationRequired())
                    eNotify(new ENotificationImpl(this, Notification.RESOLVE, EmfPackage.EMF_NAMESPACE_PREFIX__EMF_URI, oldEmfURI, emfURI));
            }
        }
        return emfURI;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EMFNamespaceURI basicGetEmfURI()
    {
        return emfURI;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public NotificationChain basicSetEmfURI(EMFNamespaceURI newEmfURI, NotificationChain msgs)
    {
        EMFNamespaceURI oldEmfURI = emfURI;
        emfURI = newEmfURI;
        if (eNotificationRequired())
        {
            ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, EmfPackage.EMF_NAMESPACE_PREFIX__EMF_URI, oldEmfURI, newEmfURI);
            if (msgs == null) msgs = notification; else msgs.add(notification);
        }
        return msgs;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setEmfURI(EMFNamespaceURI newEmfURI)
    {
        if (newEmfURI != emfURI)
        {
            NotificationChain msgs = null;
            if (emfURI != null)
                msgs = ((InternalEObject)emfURI).eInverseRemove(this, EmfPackage.EMF_NAMESPACE_URI__EMF_PREFIXES, EMFNamespaceURI.class, msgs);
            if (newEmfURI != null)
                msgs = ((InternalEObject)newEmfURI).eInverseAdd(this, EmfPackage.EMF_NAMESPACE_URI__EMF_PREFIXES, EMFNamespaceURI.class, msgs);
            msgs = basicSetEmfURI(newEmfURI, msgs);
            if (msgs != null) msgs.dispatch();
        }
        else if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, EmfPackage.EMF_NAMESPACE_PREFIX__EMF_URI, newEmfURI, newEmfURI));
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
                case EmfPackage.EMF_NAMESPACE_PREFIX__EMF_URI:
                    if (emfURI != null)
                        msgs = ((InternalEObject)emfURI).eInverseRemove(this, EmfPackage.EMF_NAMESPACE_URI__EMF_PREFIXES, EMFNamespaceURI.class, msgs);
                    return basicSetEmfURI((EMFNamespaceURI)otherEnd, msgs);
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
                case EmfPackage.EMF_NAMESPACE_PREFIX__EMF_URI:
                    return basicSetEmfURI(null, msgs);
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
            case EmfPackage.EMF_NAMESPACE_PREFIX__EMF_PREFIX:
                return getEmfPrefix();
            case EmfPackage.EMF_NAMESPACE_PREFIX__EMF_URI:
                if (resolve) return getEmfURI();
                return basicGetEmfURI();
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
            case EmfPackage.EMF_NAMESPACE_PREFIX__EMF_PREFIX:
                setEmfPrefix((String)newValue);
                return;
            case EmfPackage.EMF_NAMESPACE_PREFIX__EMF_URI:
                setEmfURI((EMFNamespaceURI)newValue);
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
            case EmfPackage.EMF_NAMESPACE_PREFIX__EMF_PREFIX:
                setEmfPrefix(EMF_PREFIX_EDEFAULT);
                return;
            case EmfPackage.EMF_NAMESPACE_PREFIX__EMF_URI:
                setEmfURI((EMFNamespaceURI)null);
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
            case EmfPackage.EMF_NAMESPACE_PREFIX__EMF_PREFIX:
                return EMF_PREFIX_EDEFAULT == null ? emfPrefix != null : !EMF_PREFIX_EDEFAULT.equals(emfPrefix);
            case EmfPackage.EMF_NAMESPACE_PREFIX__EMF_URI:
                return emfURI != null;
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
        result.append(" (emfPrefix: ");
        result.append(emfPrefix);
        result.append(')');
        return result.toString();
    }

    /* (non-Javadoc)
     * @see org.alfresco.repo.dictionary.metamodel.M2NamespacePrefix#getPrefix()
     */
    public String getPrefix()
    {
        return getEmfPrefix();
    }

    /* (non-Javadoc)
     * @see org.alfresco.repo.dictionary.metamodel.M2NamespacePrefix#setPrefix(java.lang.String)
     */
    public void setPrefix(String prefix)
    {
        setEmfPrefix(prefix);
    }

    /* (non-Javadoc)
     * @see org.alfresco.repo.dictionary.metamodel.M2NamespacePrefix#getURI()
     */
    public M2NamespaceURI getURI()
    {
        return (M2NamespaceURI)getEmfURI();
    }

    /* (non-Javadoc)
     * @see org.alfresco.repo.dictionary.metamodel.M2NamespacePrefix#setURI(org.alfresco.repo.dictionary.metamodel.M2NamespaceURI)
     */
    public void setURI(M2NamespaceURI uri)
    {
        setEmfURI((EMFNamespaceURI)uri);
    }

} //EMFNamespacePrefixImpl
