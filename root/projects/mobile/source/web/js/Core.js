Core = (window.Core || {});
Core.util = (window.Core.util || {});

Core.util.merge = function merge() {
  var o={}, a=arguments, l=a.length, i;
  for (i=0; i<l; i=i+1) {
      L.augmentObject(o, a[i], true);
  }
  return o;
};
Core.util.augment = function augment(r, s) {
  if (!s||!r) {
      throw new Error("Absorb failed, verify dependencies.");
  }
  var a=arguments, i, p, overrideList=a[2];
  if (overrideList && overrideList!==true) { 
      for (i=2; i<a.length; i=i+1) {
          r[a[i]] = s[a[i]];
      }
  } else {
      for (p in s) { 
          if (overrideList || !(p in r)) {
              r[p] = s[p];
          }
      }
  }
};
Core.util.trim = function trim(s){
    try {
        return s.replace(/^\s+|\s+$/g, "");
    } catch(e) {
        return s;
    }
};
Core.util.extend = function extend(subc, superc, overrides) {
    if (!superc||!subc) {
        throw new Error("extend failed, please check that " +
                        "all dependencies are included.");
    }
    var F = function() {};
    F.prototype=superc.prototype;
    subc.prototype=new F();
    subc.prototype.constructor=subc;
    subc.superclass=superc.prototype;
    if (superc.prototype.constructor == Object.prototype.constructor) {
      superc.prototype.constructor=superc;
    }

    if (overrides) 
    {
      for (i in overrides) 
      {
        subc.prototype[i]=overrides[i];  
      }
    }
    return subc;
};
/**
 * Provides AOP style functionality (before,after,around)
 *  
 */
(Do = function() {
    var aAspects = [];
    aAspects["before"]=function(oTarget,sMethodName,fn) {
        var fOrigMethod = oTarget[sMethodName];
        
        oTarget[sMethodName] = function() {
          fn.apply(oTarget, arguments);
          return fOrigMethod.apply(oTarget, arguments);
        };
    }; 
    //after
    aAspects["after"]=function(oTarget,sMethodName,fn){
        var fOrigMethod = oTarget[sMethodName];
        oTarget[sMethodName] = function() {
            var rv = fOrigMethod.apply(oTarget, arguments);
            return fn.apply(oTarget, [rv]);
        };
    }; 
    //around
    aAspects["around"]=function(oTarget,sMethodName,aFn){
        var fOrigMethod = oTarget[sMethodName];
        oTarget[sMethodName] = function() {
              if (aFn && aFn.length==2) {
                aFn[0].apply(oTarget, arguments);
                var rv = fOrigMethod.apply(oTarget, arguments);
                return aFn[1].apply(oTarget, [rv]);
              }
              else {
                return fOrigMethod.apply(oTarget, arguments);
              }
              
            };
    };
    var advise = function(oTarget,sAspect,sMethod,fAdvice) {
      if (oTarget && sAspect && sMethod && fAdvice && aAspects[sAspect]) {
          //decorate specified method
          aAspects[sAspect](oTarget,sMethod,fAdvice);
      }
      return oTarget;
    };
    return {
      before : function after(oTarget,sMethod,fAdvice)
      {
        return advise(oTarget,Do.BEFORE,sMethod,fAdvice);
      },
      after : function after(oTarget,sMethod,fAdvice)
      {
        return advise(oTarget,Do.AFTER,sMethod,fAdvice);
      },
      around : function after(oTarget,sMethod,aAdvices)
      {
        return advise(oTarget,Do.AROUND,sMethod,aAdvices);
      }
    };
}());

Do.BEFORE = 'before';
Do.AFTER = 'after';
Do.AROUND = 'around';