/**
 * <copyright> </copyright>
 * 
 * $Id$
 */
package com.activiti.repo.dictionary.metamodel.emf.impl;

import com.activiti.repo.dictionary.PropertyDefinition;
import com.activiti.repo.dictionary.PropertyTypeDefinition;
import com.activiti.repo.dictionary.metamodel.M2PropertyDefinition;
import com.activiti.repo.dictionary.metamodel.M2PropertyType;
import com.activiti.repo.dictionary.metamodel.M2PropertyTypeDefinition;
import com.activiti.repo.dictionary.metamodel.emf.EMFPropertyType;
import com.activiti.repo.dictionary.metamodel.emf.EmfPackage;

import com.activiti.repo.ref.QName;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>EMF Property Type</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link com.activiti.repo.dictionary.metamodel.emf.impl.EMFPropertyTypeImpl#getQName <em>QName</em>}</li>
 *   <li>{@link com.activiti.repo.dictionary.metamodel.emf.impl.EMFPropertyTypeImpl#getEmfAnalyserClassName <em>Emf Analyser Class Name</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class EMFPropertyTypeImpl extends EObjectImpl implements EMFPropertyType, M2PropertyType
{
    /**
     * The default value of the '{@link #getQName() <em>QName</em>}' attribute.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @see #getQName()
     * @generated
     * @ordered
     */
    protected static final QName QNAME_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getQName() <em>QName</em>}' attribute.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @see #getQName()
     * @generated
     * @ordered
     */
    protected QName qName = QNAME_EDEFAULT;

    /**
     * The default value of the '{@link #getEmfAnalyserClassName() <em>Emf Analyser Class Name</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getEmfAnalyserClassName()
     * @generated
     * @ordered
     */
    protected static final String EMF_ANALYSER_CLASS_NAME_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getEmfAnalyserClassName() <em>Emf Analyser Class Name</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getEmfAnalyserClassName()
     * @generated
     * @ordered
     */
    protected String emfAnalyserClassName = EMF_ANALYSER_CLASS_NAME_EDEFAULT;

    private PropertyTypeDefinition propertyTypeDefinition;

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    protected EMFPropertyTypeImpl()
    {
        super();
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    protected EClass eStaticClass()
    {
        return EmfPackage.eINSTANCE.getEMFPropertyType();
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public QName getQName()
    {
        return qName;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public void setQName(QName newQName)
    {
        QName oldQName = qName;
        qName = newQName;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, EmfPackage.EMF_PROPERTY_TYPE__QNAME, oldQName, qName));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String getEmfAnalyserClassName()
    {
        return emfAnalyserClassName;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setEmfAnalyserClassName(String newEmfAnalyserClassName)
    {
        String oldEmfAnalyserClassName = emfAnalyserClassName;
        emfAnalyserClassName = newEmfAnalyserClassName;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, EmfPackage.EMF_PROPERTY_TYPE__EMF_ANALYSER_CLASS_NAME, oldEmfAnalyserClassName, emfAnalyserClassName));
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public Object eGet(EStructuralFeature eFeature, boolean resolve)
    {
        switch (eDerivedStructuralFeatureID(eFeature))
        {
            case EmfPackage.EMF_PROPERTY_TYPE__QNAME:
                return getQName();
            case EmfPackage.EMF_PROPERTY_TYPE__EMF_ANALYSER_CLASS_NAME:
                return getEmfAnalyserClassName();
        }
        return eDynamicGet(eFeature, resolve);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public void eSet(EStructuralFeature eFeature, Object newValue)
    {
        switch (eDerivedStructuralFeatureID(eFeature))
        {
            case EmfPackage.EMF_PROPERTY_TYPE__QNAME:
                setQName((QName)newValue);
                return;
            case EmfPackage.EMF_PROPERTY_TYPE__EMF_ANALYSER_CLASS_NAME:
                setEmfAnalyserClassName((String)newValue);
                return;
        }
        eDynamicSet(eFeature, newValue);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public void eUnset(EStructuralFeature eFeature)
    {
        switch (eDerivedStructuralFeatureID(eFeature))
        {
            case EmfPackage.EMF_PROPERTY_TYPE__QNAME:
                setQName(QNAME_EDEFAULT);
                return;
            case EmfPackage.EMF_PROPERTY_TYPE__EMF_ANALYSER_CLASS_NAME:
                setEmfAnalyserClassName(EMF_ANALYSER_CLASS_NAME_EDEFAULT);
                return;
        }
        eDynamicUnset(eFeature);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public boolean eIsSet(EStructuralFeature eFeature)
    {
        switch (eDerivedStructuralFeatureID(eFeature))
        {
            case EmfPackage.EMF_PROPERTY_TYPE__QNAME:
                return QNAME_EDEFAULT == null ? qName != null : !QNAME_EDEFAULT.equals(qName);
            case EmfPackage.EMF_PROPERTY_TYPE__EMF_ANALYSER_CLASS_NAME:
                return EMF_ANALYSER_CLASS_NAME_EDEFAULT == null ? emfAnalyserClassName != null : !EMF_ANALYSER_CLASS_NAME_EDEFAULT.equals(emfAnalyserClassName);
        }
        return eDynamicIsSet(eFeature);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public String toString()
    {
        if (eIsProxy()) return super.toString();

        StringBuffer result = new StringBuffer(super.toString());
        result.append(" (qName: ");
        result.append(qName);
        result.append(", emfAnalyserClassName: ");
        result.append(emfAnalyserClassName);
        result.append(')');
        return result.toString();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.activiti.repo.dictionary.metamodel.M2PropertyType#getPropertyTypeDefinition()
     */
    public PropertyTypeDefinition getPropertyTypeDefinition()
    {
        if (propertyTypeDefinition == null)
        {
            propertyTypeDefinition = M2PropertyTypeDefinition.create(this);
        }
        return propertyTypeDefinition;

    }

    public String getAnalyserClassName()
    {
        return getEmfAnalyserClassName();
    }

    public void setAnalyserClassName(String analyserClassName)
    {
        setEmfAnalyserClassName(analyserClassName);
    }

    

} // EMFPropertyTypeImpl
