package org.alfresco.web.scripts;

import java.util.Map;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.WrapFactory;

public class PresentationWrapFactory extends WrapFactory
{

    public Scriptable wrapAsJavaObject(Context cx, Scriptable scope, Object javaObject, Class staticType)
    {
        if (javaObject instanceof Map)
        {
            return new NativeMap(scope, (Map)javaObject);
        }
        return super.wrapAsJavaObject(cx, scope, javaObject, staticType);
    }

}
