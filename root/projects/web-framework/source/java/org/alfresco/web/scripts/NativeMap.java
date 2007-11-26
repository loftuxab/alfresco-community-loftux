package org.alfresco.web.scripts;

import java.util.Iterator;
import java.util.Map;

import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.Wrapper;

public class NativeMap implements Scriptable, Wrapper
{
    private static final long serialVersionUID = 3664761893203964569L;
    
    private Map<Object, Object> map;
    private Scriptable parentScope;
    private Scriptable prototype;

    
    public static NativeMap wrap(Scriptable scope, Map<Object, Object> map)
    {
        return new NativeMap(scope, map);
    }

    public Object unwrap() {
        return map;
    }

    public NativeMap(Scriptable scope, Map<Object, Object> map)
    {
        this.parentScope = scope;
        this.map = map;
    }

    
    /**
     * @see org.mozilla.javascript.Scriptable#getClassName()
     */
    public String getClassName()
    {
        return "NativeMap";
    }

    
    /**
     * @see org.mozilla.javascript.Scriptable#get(java.lang.String, org.mozilla.javascript.Scriptable)
     */
    public Object get(String name, Scriptable start)
    {
        // get the property from the underlying QName map
        if ("length".equals(name))
        {
            return map.size();
        }
        else
        {
            return map.get(name);
        }
    }

    /**
     * @see org.mozilla.javascript.Scriptable#get(int, org.mozilla.javascript.Scriptable)
     */
    public Object get(int index, Scriptable start)
    {
        Object value =  null;
        int i=0;
        Iterator itrValues = map.values().iterator();
        while (i++ <= index && itrValues.hasNext())
        {
            value = itrValues.next();
        }
        return value;
    }

    /**
     * @see org.mozilla.javascript.Scriptable#has(java.lang.String, org.mozilla.javascript.Scriptable)
     */
    public boolean has(String name, Scriptable start)
    {
        // locate the property in the underlying map
        return map.containsKey(name);
    }

    /**
     * @see org.mozilla.javascript.Scriptable#has(int, org.mozilla.javascript.Scriptable)
     */
    public boolean has(int index, Scriptable start)
    {
        return (index >= 0 && map.values().size() > index);
    }

    /**
     * @see org.mozilla.javascript.Scriptable#put(java.lang.String, org.mozilla.javascript.Scriptable, java.lang.Object)
     */
    @SuppressWarnings("unchecked")
    public void put(String name, Scriptable start, Object value)
    {
        map.put(name, value);
    }

    /**
     * @see org.mozilla.javascript.Scriptable#put(int, org.mozilla.javascript.Scriptable, java.lang.Object)
     */
    public void put(int index, Scriptable start, Object value)
    {
        // TODO: implement?
    }

    /**
     * @see org.mozilla.javascript.Scriptable#delete(java.lang.String)
     */
    public void delete(String name)
    {
        map.remove(name);
    }

    /**
     * @see org.mozilla.javascript.Scriptable#delete(int)
     */
    public void delete(int index)
    {
        int i=0;
        Iterator itrKeys = map.keySet().iterator();
        while (i <= index && itrKeys.hasNext())
        {
            Object key = itrKeys.next();
            if (i == index)
            {
                map.remove(key);
                break;
            }
        }
    }

    /**
     * @see org.mozilla.javascript.Scriptable#getPrototype()
     */
    public Scriptable getPrototype()
    {
        return this.prototype;
    }

    /**
     * @see org.mozilla.javascript.Scriptable#setPrototype(org.mozilla.javascript.Scriptable)
     */
    public void setPrototype(Scriptable prototype)
    {
        this.prototype = prototype;
    }

    /**
     * @see org.mozilla.javascript.Scriptable#getParentScope()
     */
    public Scriptable getParentScope()
    {
        return this.parentScope;
    }

    /**
     * @see org.mozilla.javascript.Scriptable#setParentScope(org.mozilla.javascript.Scriptable)
     */
    public void setParentScope(Scriptable parent)
    {
        this.parentScope = parent;
    }

    /**
     * @see org.mozilla.javascript.Scriptable#getIds()
     */
    public Object[] getIds()
    {
        return map.keySet().toArray();
    }

    /**
     * @see org.mozilla.javascript.Scriptable#getDefaultValue(java.lang.Class)
     */
    public Object getDefaultValue(Class hint)
    {
        return null;
    }

    /**
     * @see org.mozilla.javascript.Scriptable#hasInstance(org.mozilla.javascript.Scriptable)
     */
    public boolean hasInstance(Scriptable value)
    {
        if (!(value instanceof Wrapper))
            return false;
        Object instance = ((Wrapper)value).unwrap();
        return Map.class.isInstance(instance);
    }

}
