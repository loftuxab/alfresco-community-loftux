/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package com.activiti.repo.dictionary.metamodel.emf.impl;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;

import com.activiti.repo.dictionary.PropertyDefinition;
import com.activiti.repo.dictionary.PropertyRef;
import com.activiti.repo.dictionary.metamodel.M2Class;
import com.activiti.repo.dictionary.metamodel.M2Property;
import com.activiti.repo.dictionary.metamodel.M2PropertyDefinition;
import com.activiti.repo.dictionary.metamodel.M2PropertyType;
import com.activiti.repo.dictionary.metamodel.emf.EMFClass;
import com.activiti.repo.dictionary.metamodel.emf.EMFProperty;
import com.activiti.repo.dictionary.metamodel.emf.EMFPropertyType;
import com.activiti.repo.dictionary.metamodel.emf.EmfPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>EMF Property</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link com.activiti.repo.dictionary.metamodel.emf.impl.EMFPropertyImpl#getEmfContainerClass <em>Emf Container Class</em>}</li>
 *   <li>{@link com.activiti.repo.dictionary.metamodel.emf.impl.EMFPropertyImpl#getEmfName <em>Emf Name</em>}</li>
 *   <li>{@link com.activiti.repo.dictionary.metamodel.emf.impl.EMFPropertyImpl#getEmfType <em>Emf Type</em>}</li>
 *   <li>{@link com.activiti.repo.dictionary.metamodel.emf.impl.EMFPropertyImpl#getEmfProtected <em>Emf Protected</em>}</li>
 *   <li>{@link com.activiti.repo.dictionary.metamodel.emf.impl.EMFPropertyImpl#getEmfMandatory <em>Emf Mandatory</em>}</li>
 *   <li>{@link com.activiti.repo.dictionary.metamodel.emf.impl.EMFPropertyImpl#getEmfMultiple <em>Emf Multiple</em>}</li>
 *   <li>{@link com.activiti.repo.dictionary.metamodel.emf.impl.EMFPropertyImpl#getEmfIndexed <em>Emf Indexed</em>}</li>
 *   <li>{@link com.activiti.repo.dictionary.metamodel.emf.impl.EMFPropertyImpl#getEmfStoredInIndex <em>Emf Stored In Index</em>}</li>
 *   <li>{@link com.activiti.repo.dictionary.metamodel.emf.impl.EMFPropertyImpl#getEmfIndexTokeniserClassName <em>Emf Index Tokeniser Class Name</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
/**
 * @author David Caruana
 *
 */
public class EMFPropertyImpl extends EObjectImpl implements EMFProperty, M2Property
{
    /**
     * The default value of the '{@link #getEmfName() <em>Emf Name</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getEmfName()
     * @generated
     * @ordered
     */
    protected static final String EMF_NAME_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getEmfName() <em>Emf Name</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getEmfName()
     * @generated
     * @ordered
     */
    protected String emfName = EMF_NAME_EDEFAULT;

    
    /**
     * The cached value of the '{@link #getEmfType() <em>Emf Type</em>}' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getEmfType()
     * @generated
     * @ordered
     */
    protected EMFPropertyType emfType = null;

    /**
     * The default value of the '{@link #getEmfProtected() <em>Emf Protected</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getEmfProtected()
     * @generated
     * @ordered
     */
    protected static final Boolean EMF_PROTECTED_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getEmfProtected() <em>Emf Protected</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getEmfProtected()
     * @generated
     * @ordered
     */
    protected Boolean emfProtected = EMF_PROTECTED_EDEFAULT;

    /**
     * The default value of the '{@link #getEmfMandatory() <em>Emf Mandatory</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getEmfMandatory()
     * @generated
     * @ordered
     */
    protected static final Boolean EMF_MANDATORY_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getEmfMandatory() <em>Emf Mandatory</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getEmfMandatory()
     * @generated
     * @ordered
     */
    protected Boolean emfMandatory = EMF_MANDATORY_EDEFAULT;

    /**
     * The default value of the '{@link #getEmfMultiple() <em>Emf Multiple</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getEmfMultiple()
     * @generated
     * @ordered
     */
    protected static final Boolean EMF_MULTIPLE_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getEmfMultiple() <em>Emf Multiple</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getEmfMultiple()
     * @generated
     * @ordered
     */
    protected Boolean emfMultiple = EMF_MULTIPLE_EDEFAULT;

    /**
     * The default value of the '{@link #getEmfIndexed() <em>Emf Indexed</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getEmfIndexed()
     * @generated
     * @ordered
     */
    protected static final Boolean EMF_INDEXED_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getEmfIndexed() <em>Emf Indexed</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getEmfIndexed()
     * @generated
     * @ordered
     */
    protected Boolean emfIndexed = EMF_INDEXED_EDEFAULT;

    /**
     * The default value of the '{@link #getEmfStoredInIndex() <em>Emf Stored In Index</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getEmfStoredInIndex()
     * @generated
     * @ordered
     */
    protected static final Boolean EMF_STORED_IN_INDEX_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getEmfStoredInIndex() <em>Emf Stored In Index</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getEmfStoredInIndex()
     * @generated
     * @ordered
     */
    protected Boolean emfStoredInIndex = EMF_STORED_IN_INDEX_EDEFAULT;

    /**
     * The default value of the '{@link #getEmfIndexTokeniserClassName() <em>Emf Index Tokeniser Class Name</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getEmfIndexTokeniserClassName()
     * @generated
     * @ordered
     */
    protected static final String EMF_INDEX_TOKENISER_CLASS_NAME_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getEmfIndexTokeniserClassName() <em>Emf Index Tokeniser Class Name</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getEmfIndexTokeniserClassName()
     * @generated
     * @ordered
     */
    protected String emfIndexTokeniserClassName = EMF_INDEX_TOKENISER_CLASS_NAME_EDEFAULT;

    /**
     * Property Reference
     */
    protected PropertyRef propertyRef = null;
    
    /**
     * Read-only Property Definition
     */
    protected PropertyDefinition propertyDefinition = null;
    
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected EMFPropertyImpl()
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
        return EmfPackage.eINSTANCE.getEMFProperty();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EMFClass getEmfContainerClass()
    {
        if (eContainerFeatureID != EmfPackage.EMF_PROPERTY__EMF_CONTAINER_CLASS) return null;
        return (EMFClass)eContainer;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String getEmfName()
    {
        return emfName;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setEmfName(String newEmfName)
    {
        String oldEmfName = emfName;
        emfName = newEmfName;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, EmfPackage.EMF_PROPERTY__EMF_NAME, oldEmfName, emfName));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EMFPropertyType getEmfType()
    {
        if (emfType != null && emfType.eIsProxy())
        {
            EMFPropertyType oldEmfType = emfType;
            emfType = (EMFPropertyType)eResolveProxy((InternalEObject)emfType);
            if (emfType != oldEmfType)
            {
                if (eNotificationRequired())
                    eNotify(new ENotificationImpl(this, Notification.RESOLVE, EmfPackage.EMF_PROPERTY__EMF_TYPE, oldEmfType, emfType));
            }
        }
        return emfType;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EMFPropertyType basicGetEmfType()
    {
        return emfType;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setEmfType(EMFPropertyType newEmfType)
    {
        EMFPropertyType oldEmfType = emfType;
        emfType = newEmfType;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, EmfPackage.EMF_PROPERTY__EMF_TYPE, oldEmfType, emfType));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public Boolean getEmfProtected()
    {
        return emfProtected;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setEmfProtected(Boolean newEmfProtected)
    {
        Boolean oldEmfProtected = emfProtected;
        emfProtected = newEmfProtected;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, EmfPackage.EMF_PROPERTY__EMF_PROTECTED, oldEmfProtected, emfProtected));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public Boolean getEmfMandatory()
    {
        return emfMandatory;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setEmfMandatory(Boolean newEmfMandatory)
    {
        Boolean oldEmfMandatory = emfMandatory;
        emfMandatory = newEmfMandatory;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, EmfPackage.EMF_PROPERTY__EMF_MANDATORY, oldEmfMandatory, emfMandatory));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public Boolean getEmfMultiple()
    {
        return emfMultiple;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setEmfMultiple(Boolean newEmfMultiple)
    {
        Boolean oldEmfMultiple = emfMultiple;
        emfMultiple = newEmfMultiple;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, EmfPackage.EMF_PROPERTY__EMF_MULTIPLE, oldEmfMultiple, emfMultiple));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public Boolean getEmfIndexed()
    {
        return emfIndexed;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setEmfIndexed(Boolean newEmfIndexed)
    {
        Boolean oldEmfIndexed = emfIndexed;
        emfIndexed = newEmfIndexed;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, EmfPackage.EMF_PROPERTY__EMF_INDEXED, oldEmfIndexed, emfIndexed));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public Boolean getEmfStoredInIndex()
    {
        return emfStoredInIndex;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setEmfStoredInIndex(Boolean newEmfStoredInIndex)
    {
        Boolean oldEmfStoredInIndex = emfStoredInIndex;
        emfStoredInIndex = newEmfStoredInIndex;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, EmfPackage.EMF_PROPERTY__EMF_STORED_IN_INDEX, oldEmfStoredInIndex, emfStoredInIndex));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String getEmfIndexTokeniserClassName()
    {
        return emfIndexTokeniserClassName;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setEmfIndexTokeniserClassName(String newEmfIndexTokeniserClassName)
    {
        String oldEmfIndexTokeniserClassName = emfIndexTokeniserClassName;
        emfIndexTokeniserClassName = newEmfIndexTokeniserClassName;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, EmfPackage.EMF_PROPERTY__EMF_INDEX_TOKENISER_CLASS_NAME, oldEmfIndexTokeniserClassName, emfIndexTokeniserClassName));
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
                case EmfPackage.EMF_PROPERTY__EMF_CONTAINER_CLASS:
                    if (eContainer != null)
                        msgs = eBasicRemoveFromContainer(msgs);
                    return eBasicSetContainer(otherEnd, EmfPackage.EMF_PROPERTY__EMF_CONTAINER_CLASS, msgs);
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
                case EmfPackage.EMF_PROPERTY__EMF_CONTAINER_CLASS:
                    return eBasicSetContainer(null, EmfPackage.EMF_PROPERTY__EMF_CONTAINER_CLASS, msgs);
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
                case EmfPackage.EMF_PROPERTY__EMF_CONTAINER_CLASS:
                    return ((InternalEObject)eContainer).eInverseRemove(this, EmfPackage.EMF_CLASS__EMF_PROPERTIES, EMFClass.class, msgs);
                default:
                    return eDynamicBasicRemoveFromContainer(msgs);
            }
        }
        return ((InternalEObject)eContainer).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - eContainerFeatureID, null, msgs);
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
            case EmfPackage.EMF_PROPERTY__EMF_CONTAINER_CLASS:
                return getEmfContainerClass();
            case EmfPackage.EMF_PROPERTY__EMF_NAME:
                return getEmfName();
            case EmfPackage.EMF_PROPERTY__EMF_TYPE:
                if (resolve) return getEmfType();
                return basicGetEmfType();
            case EmfPackage.EMF_PROPERTY__EMF_PROTECTED:
                return getEmfProtected();
            case EmfPackage.EMF_PROPERTY__EMF_MANDATORY:
                return getEmfMandatory();
            case EmfPackage.EMF_PROPERTY__EMF_MULTIPLE:
                return getEmfMultiple();
            case EmfPackage.EMF_PROPERTY__EMF_INDEXED:
                return getEmfIndexed();
            case EmfPackage.EMF_PROPERTY__EMF_STORED_IN_INDEX:
                return getEmfStoredInIndex();
            case EmfPackage.EMF_PROPERTY__EMF_INDEX_TOKENISER_CLASS_NAME:
                return getEmfIndexTokeniserClassName();
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
            case EmfPackage.EMF_PROPERTY__EMF_NAME:
                setEmfName((String)newValue);
                return;
            case EmfPackage.EMF_PROPERTY__EMF_TYPE:
                setEmfType((EMFPropertyType)newValue);
                return;
            case EmfPackage.EMF_PROPERTY__EMF_PROTECTED:
                setEmfProtected((Boolean)newValue);
                return;
            case EmfPackage.EMF_PROPERTY__EMF_MANDATORY:
                setEmfMandatory((Boolean)newValue);
                return;
            case EmfPackage.EMF_PROPERTY__EMF_MULTIPLE:
                setEmfMultiple((Boolean)newValue);
                return;
            case EmfPackage.EMF_PROPERTY__EMF_INDEXED:
                setEmfIndexed((Boolean)newValue);
                return;
            case EmfPackage.EMF_PROPERTY__EMF_STORED_IN_INDEX:
                setEmfStoredInIndex((Boolean)newValue);
                return;
            case EmfPackage.EMF_PROPERTY__EMF_INDEX_TOKENISER_CLASS_NAME:
                setEmfIndexTokeniserClassName((String)newValue);
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
            case EmfPackage.EMF_PROPERTY__EMF_NAME:
                setEmfName(EMF_NAME_EDEFAULT);
                return;
            case EmfPackage.EMF_PROPERTY__EMF_TYPE:
                setEmfType((EMFPropertyType)null);
                return;
            case EmfPackage.EMF_PROPERTY__EMF_PROTECTED:
                setEmfProtected(EMF_PROTECTED_EDEFAULT);
                return;
            case EmfPackage.EMF_PROPERTY__EMF_MANDATORY:
                setEmfMandatory(EMF_MANDATORY_EDEFAULT);
                return;
            case EmfPackage.EMF_PROPERTY__EMF_MULTIPLE:
                setEmfMultiple(EMF_MULTIPLE_EDEFAULT);
                return;
            case EmfPackage.EMF_PROPERTY__EMF_INDEXED:
                setEmfIndexed(EMF_INDEXED_EDEFAULT);
                return;
            case EmfPackage.EMF_PROPERTY__EMF_STORED_IN_INDEX:
                setEmfStoredInIndex(EMF_STORED_IN_INDEX_EDEFAULT);
                return;
            case EmfPackage.EMF_PROPERTY__EMF_INDEX_TOKENISER_CLASS_NAME:
                setEmfIndexTokeniserClassName(EMF_INDEX_TOKENISER_CLASS_NAME_EDEFAULT);
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
            case EmfPackage.EMF_PROPERTY__EMF_CONTAINER_CLASS:
                return getEmfContainerClass() != null;
            case EmfPackage.EMF_PROPERTY__EMF_NAME:
                return EMF_NAME_EDEFAULT == null ? emfName != null : !EMF_NAME_EDEFAULT.equals(emfName);
            case EmfPackage.EMF_PROPERTY__EMF_TYPE:
                return emfType != null;
            case EmfPackage.EMF_PROPERTY__EMF_PROTECTED:
                return EMF_PROTECTED_EDEFAULT == null ? emfProtected != null : !EMF_PROTECTED_EDEFAULT.equals(emfProtected);
            case EmfPackage.EMF_PROPERTY__EMF_MANDATORY:
                return EMF_MANDATORY_EDEFAULT == null ? emfMandatory != null : !EMF_MANDATORY_EDEFAULT.equals(emfMandatory);
            case EmfPackage.EMF_PROPERTY__EMF_MULTIPLE:
                return EMF_MULTIPLE_EDEFAULT == null ? emfMultiple != null : !EMF_MULTIPLE_EDEFAULT.equals(emfMultiple);
            case EmfPackage.EMF_PROPERTY__EMF_INDEXED:
                return EMF_INDEXED_EDEFAULT == null ? emfIndexed != null : !EMF_INDEXED_EDEFAULT.equals(emfIndexed);
            case EmfPackage.EMF_PROPERTY__EMF_STORED_IN_INDEX:
                return EMF_STORED_IN_INDEX_EDEFAULT == null ? emfStoredInIndex != null : !EMF_STORED_IN_INDEX_EDEFAULT.equals(emfStoredInIndex);
            case EmfPackage.EMF_PROPERTY__EMF_INDEX_TOKENISER_CLASS_NAME:
                return EMF_INDEX_TOKENISER_CLASS_NAME_EDEFAULT == null ? emfIndexTokeniserClassName != null : !EMF_INDEX_TOKENISER_CLASS_NAME_EDEFAULT.equals(emfIndexTokeniserClassName);
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
        result.append(", emfProtected: ");
        result.append(emfProtected);
        result.append(", emfMandatory: ");
        result.append(emfMandatory);
        result.append(", emfMultiple: ");
        result.append(emfMultiple);
        result.append(", emfIndexed: ");
        result.append(emfIndexed);
        result.append(", emfStoredInIndex: ");
        result.append(emfStoredInIndex);
        result.append(", emfIndexTokeniserClassName: ");
        result.append(emfIndexTokeniserClassName);
        result.append(')');
        return result.toString();
    }

    /* (non-Javadoc)
     * @see com.activiti.repo.dictionary.metamodel.M2Property#getReference()
     */
    public PropertyRef getReference()
    {
        if (propertyRef == null)
        {
            propertyRef = new PropertyRef(getContainerClass().getReference(), getName());
        }
        return propertyRef;
    }
    
    /* (non-Javadoc)
     * @see com.activiti.repo.dictionary.metamodel.M2Property#getName()
     */
    public String getName()
    {
        return getEmfName();
    }

    /* (non-Javadoc)
     * @see com.activiti.repo.dictionary.metamodel.M2Property#setName(java.lang.String)
     */
    public void setName(String name)
    {
        setEmfName(name);
    }

    /* (non-Javadoc)
     * @see com.activiti.repo.dictionary.metamodel.M2Property#getContainerClass()
     */
    public M2Class getContainerClass()
    {
        return (M2Class)getEmfContainerClass();
    }

    /* (non-Javadoc)
     * @see com.activiti.repo.dictionary.metamodel.M2Property#getType()
     */
    public M2PropertyType getType()
    {
        return (M2PropertyType)getEmfType();
    }

    /* (non-Javadoc)
     * @see com.activiti.repo.dictionary.metamodel.M2Property#setType(com.activiti.repo.dictionary.metamodel.M2PropertyType)
     */
    public void setType(M2PropertyType type)
    {
        setEmfType((EMFPropertyType)type);
    }
    
    /* (non-Javadoc)
     * @see com.activiti.repo.dictionary.metamodel.M2Property#isProtected()
     */
    public boolean isProtected()
    {
        return getEmfProtected();
    }

    /* (non-Javadoc)
     * @see com.activiti.repo.dictionary.metamodel.M2Property#setProtected(boolean)
     */
    public void setProtected(boolean isProtected)
    {
        setEmfProtected(isProtected);
    }

    /* (non-Javadoc)
     * @see com.activiti.repo.dictionary.metamodel.M2Property#isMandatory()
     */
    public boolean isMandatory()
    {
        return getEmfMandatory();
    }

    /* (non-Javadoc)
     * @see com.activiti.repo.dictionary.metamodel.M2Property#setMandatory(boolean)
     */
    public void setMandatory(boolean isMandatory)
    {
        setEmfMandatory(isMandatory);
    }

    /* (non-Javadoc)
     * @see com.activiti.repo.dictionary.metamodel.M2Property#isMultiValued()
     */
    public boolean isMultiValued()
    {
        return getEmfMultiple();
    }

    /* (non-Javadoc)
     * @see com.activiti.repo.dictionary.metamodel.M2Property#setMultiValued(boolean)
     */
    public void setMultiValued(boolean isMultiValued)
    {
        setEmfMultiple(isMultiValued);
    }

    /* (non-Javadoc)
     * @see com.activiti.repo.dictionary.metamodel.M2Property#isIndexed()
     */
    public boolean isIndexed()
    {
        return getEmfIndexed();
    }

    /* (non-Javadoc)
     * @see com.activiti.repo.dictionary.metamodel.M2Property#setIndexed(boolean)
     */
    public void setIndexed(boolean isIndexed)
    {
        setEmfIndexed(isIndexed);
    }

    /* (non-Javadoc)
     * @see com.activiti.repo.dictionary.metamodel.M2Property#isStoredInIndex()
     */
    public boolean isStoredInIndex()
    {
        return getEmfStoredInIndex();
    }

    /* (non-Javadoc)
     * @see com.activiti.repo.dictionary.metamodel.M2Property#setStoredInIndex(boolean)
     */
    public void setStoredInIndex(boolean isStoredInIndex)
    {
        setEmfStoredInIndex(isStoredInIndex);
    }

    /* (non-Javadoc)
     * @see com.activiti.repo.dictionary.metamodel.M2Property#getIndexTokeniserClassName()
     */
    public String getIndexTokeniserClassName()
    {
        return getEmfIndexTokeniserClassName();
    }

    /* (non-Javadoc)
     * @see com.activiti.repo.dictionary.metamodel.M2Property#setIndexTokeniserClassName(java.lang.String)
     */
    public void setIndexTokeniserClassName(String indexTokeniserName)
    {
        setEmfIndexTokeniserClassName(indexTokeniserName);
    }

    /* (non-Javadoc)
     * @see com.activiti.repo.dictionary.metamodel.M2Property#getPropertyDefinition()
     */
    public PropertyDefinition getPropertyDefinition()
    {
        if (propertyDefinition == null)
        {
            propertyDefinition = M2PropertyDefinition.create(this);
        }
        return propertyDefinition;
    }

} //EMFPropertyImpl
