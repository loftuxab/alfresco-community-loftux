/*
Script: Core.js
	Mootools - My Object Oriented Javascript.

License:
	MIT-style license.

MooTools Copyright:
	copyright (c) 2007 Valerio Proietti, <http://mad4milk.net>
	
MooTools Code & Documentation:
	The MooTools team <http://mootools.net/developers>.

MooTools Credits:
	- Class is slightly based on Base.js <http://dean.edwards.name/weblog/2006/03/base/> (c) 2006 Dean Edwards, License <http://creativecommons.org/licenses/LGPL/2.1/>
	- Some functions are inspired by those found in prototype.js <http://prototypejs.org/> (c) 2005 Sam Stephenson (sam [at] conio [dot] net), MIT-style license
*/

var MooTools = {
	'version': '1.2dev',
	'build': '%build%'
};

/* Section: Core Functions */

/*
Function: $extend
	Copies all the properties from the second object passed in to the first object passed in.
	In myWhatever.extend = $extend, the first parameter will become myWhatever, and the extend function will only need one parameter.

Syntax:
	>$extend(obj1, obj2);

Arguments:
	obj1 - The object to be extended.
	obj2 - The object whose properties will be copied to obj1.

Returns:
	The first object, extended.

Example:
	(start code)
	var firstOb = {
		'name': 'John',
		'lastName': 'Doe'
	};
	var secondOb = {
		'age': '20',
		'sex': 'male',
		'lastName': 'Dorian'
	};
	$extend(firstOb, secondOb);
	//firstOb is { 'name': 'John', 'lastName': 'Dorian', 'age': '20', 'sex': 'male' };
	(end)
*/

function $extend(src, add){
	if (!add){
		add = src;
		src = this;
	}
	for (var prop in add) src[prop] = add[prop];
	return src;
};

/*
Function: Native
	This will add a .extend method to the objects passed as a parameter,
	but the property passed in will be copied to the object's prototype only if not previously existent.
	The purpose of Native is also to create generics methods (Class Methods) from the prototypes passed in.
	Used in MooTools to automatically implement Array/Function/Number/String/RegExp methods to browsers that don't natively support them.

Arguments:
	Any number of Classes/native JavaScript objects.
*/

var Native = function(){
	for (var i = arguments.length; i--;){
		arguments[i].extend = function(props){
			for (var prop in props){
				if (!this.prototype[prop]) this.prototype[prop] = props[prop];
				if (!this[prop]) this[prop] = Native.generic(prop);
			}
		};
	}
};

Native.generic = function(prop){
	return function(bind){
		return this.prototype[prop].apply(bind, Array.prototype.slice.call(arguments, 1));
	};
};

Native.setFamily = function(natives){
	for (var type in natives) natives[type].prototype.$family = type;
};

Native(Array, Function, String, RegExp, Number);
Native.setFamily({'array': Array, 'function': Function, 'string': String, 'regexp': RegExp});

/* Section: Utility Functions */

/*
Function: $A()
	Useful for applying the Array prototypes to iterable objects such as a DOM Element collection or the arguments object.

Syntax:
	>var copiedArray = $A(array);

Arguments:
	array - (array) The array to copy.

Returns:
	(array) The new copied array.

Example:
	(start code)
	function myFunction(){
		$A(arguments).each(function(argument, index){
			alert(argument);
		});
	}; //will alert all the arguments passed to the function myFunction.
	(end)
*/

function $A(iterable, start, length){
	start = start || 0;
	if (start < 0) start = iterable.length + start;
	length = length || (iterable.length - start);
	var array = [];
	for (var i = 0; i < length; i++) array[i] = iterable[start++];
	return array;
};

/*
Function: $chk
	Checks to see if a value exists or is 0. Useful for allowing 0.

Syntax:
	>$chk(obj);

Arguments:
	obj - (mixed) The object to inspect.

Returns:
	(boolean) If the object passed in exists or is 0, returns true. Otherwise, returns false.

Example:
	(start code)
	function myFunction(arg){
		if($chk(arg)) alert('The object exists or is 0.');
		else alert('The object is either null, undefined, false, or ""');
	}
	(end)
*/

function $chk(obj){
	return !!(obj || obj === 0);
};

/*
Function: $clear
	Clears a timeout or an Interval.

Syntax:
	>$clear(timer)

Arguments:
	timer - (integer) The identifier of the setInterval (periodical) or setTimeout (delay) to clear.

Returns:
	null

Example:
	(start code)
	var myTimer = myFunction.delay(5000); //Wait 5 seconds and execute myFunction.
	myTimer = $clear(myTimer); //Nevermind.
	(end)

See also:
	<Function.delay>, <Function.periodical>
*/

function $clear(timer){
	clearTimeout(timer);
	clearInterval(timer);
	return null;
};

/*
Function: $defined
	Checks to see if a value is defined.

Syntax:
	>$defined(obj);

Arguments:
	obj - (mixed) The object to inspect.

Returns:
	(boolean) If the object passed is not null or undefined, returns true. Otherwise, returns false.

Example:
	(start code)
	function myFunction(arg){
		if($defined(arg)) alert('The object is defined.');
		else alert('The object is null or undefined.');
	}
	(end)
*/

function $defined(obj){
	return (obj != undefined);
};

/*
Function: $empty
	An empty function, that's it.
*/

function $empty(){};

/*
Function: $merge
	Merges any number of objects recursively without referencing them or their sub-objects.

Syntax:
	>var merged = $merge(obj1, obj2[, obj3[, ...]]);

Arguments:
	(objects) Any number of objects.

Returns:
	(object) The object that is created as a result of merging all the objects passed in.

Example:
	(start code)
	$merge(obj1, obj2, obj3); //returns the merged object (obj1, obj2, and obj3 are unaltered)
	(end)
*/

function $merge(){
	var mix = {};
	for (var i = 0; i < arguments.length; i++){
		for (var property in arguments[i]){
			var ap = arguments[i][property];
			var mp = mix[property];
			if (mp && $type(ap) == 'object' && $type(mp) == 'object') mix[property] = $merge(mp, ap);
			else mix[property] = ap;
		}
	}
	return mix;
};

/*
Function: $pick
	Returns the first defined argument passed in, or null.

Syntax:
	>var picked = $pick(var1, var2[, var3[, ...]]);

Arguments:
	(mixed) Any number of variables.

Returns:
	(mixed) The first variable that is defined. If all variables passed in are null or undefined, returns null.

Example:
	(start code)
	function say(infoMessage, errorMessage){
		alert($pick(errorMessage, infoMessage, 'There was no message supplied.'));
	}
	(end)
*/

function $pick(){
	for (var i = 0, l = arguments.length; i < l; i++){
		if ($defined(arguments[i])) return arguments[i];
	}
	return null;
};

/*
Function: $random
	Returns a random integer number between the two passed in values.

Syntax:
	>var random = $random(min, max);

Arguments:
	min - (integer) The minimum value (inclusive).
	max - (integer) The maximum value (inclusive).

Returns:
	(integer) A random integer between min and max.

Example:
	(start code)
	alert($random(5, 20)); //alerts a random number between 5 and 20
	(end)
*/

function $random(min, max){
	return Math.floor(Math.random() * (max - min + 1) + min);
};

/*
Function: $splat
	Array-ifies the argument passed in if it is defined and not already an array.

Syntax:
	>var splatted = $splat(obj);

Arguments:
	obj - (mixed) Any type of variable.

Returns:
	(array) If the variable passed in is an array, returns the array. Otherwise, returns an array with the only element being the variable passed in.

Examples:
	(start code)
	var obj = 'hello';
	$splat(obj); //returns ['hello']
	var obj2 = ['a', 'b', 'c'];
	$splat(obj2); //returns ['a', 'b', 'c']
	(end)
*/

function $splat(obj){
	var type = $type(obj);
	if (type && type != 'array') obj = [obj];
	return obj;
};

/*
Function: $time
	Returns the current time as a timestamp.

Syntax:
	>var time = $time();

Returns:
	(integer) - Timestamp.
*/

function $time(){
	return new Date().getTime();
};

/*
Function: $try
	Tries to execute a function. Returns false if it fails.

Syntax:
	>$try(fn, bind, args);

Arguments:
	fn   - (function) The function to execute.
	bind - (object) The object to use as 'this' in the function. For more information see <Function.bind>.
	args - (mixed) Single item or array of items as arguments to be passed to the function.

Returns:
	(mixed) Standard return of the called function, or false on failure.

Example:
	(start code)
	$try(eval, window, 'some invalid javascript'); //false
	(end)

Warning:
	If the function passed can return false, there will be no way to know if it has been successfully executed or not.
*/

function $try(fn, bind, args){
	try {
		return fn.apply(bind || fn, $splat(args) || []);
	} catch(e){
		return false;
	}
};

/*
Function: $type
	Returns the type of object that matches the element passed in.

Syntax:
	>$type(obj);

Arguments:
	obj - (object) The object to inspect.

Returns:
	'element'    - (string) If passed object is a DOM element node.
	'textnode'   - (string) If passed object is a DOM text node.
	'whitespace' - (string) If passed object is a DOM whitespace node.
	'arguments'  - (string) If passed object is an arguments object.
	'array'      - (string) If passed object is an array.
	'object'     - (string) If passed object is an object.
	'string'     - (string) If passed object is a string.
	'number'     - (string) If passed object is a number.
	'boolean'    - (string) If passed object is a boolean.
	'function'   - (string) If passed object is a function.
	'regexp'     - (string) If passed object is a regular expression.
	'class'      - (string) If passed object is a Class (created with new Class, or the extend of another class).
	'collection' - (string) If object is a native htmlelements collection, such as childNodes, getElementsByTagName, etc.
	'window'     - (string) If object passed is the window object.
	'document'   - (string) If passed object is the document object.
	false        - (boolean) If passed object is undefined, null, NaN or none of the above.

Example:
	(start code)
	var myString = 'hello';
	$type(myString); //returns "string"
	(end)
*/

function $type(obj){
	if (obj == undefined) return false;
	if (obj.$family) return obj.$family;
	if (obj.htmlElement) return 'element';
	var type = typeof obj;
	if (obj.nodeName){
		switch (obj.nodeType){
			case 1: return 'element';
			case 3: return (/\S/).test(obj.nodeValue) ? 'textnode' : 'whitespace';
		}
	} else if (typeof obj.length == 'number'){
		if (obj.item) return 'collection';
		if (obj.callee) return 'arguments';
	}
	if (type == 'number' && !isFinite(obj)) return false;
	return type;
};

//document, window
window.extend = document.extend = $extend;
window.$family = 'window';
document.$family = 'document';
document.head = document.getElementsByTagName('head')[0];

/*
Class: Client
	Some browser properties are attached to the Client object for browser and platform detection.

Features:
	Client.Features.xpath - (boolean) Browser supports dom queries using xpath.
	Client.Features.xhr   - (boolean) Browser supports native XMLHTTP object.

Engine:
	Client.Engine.ie        - (boolean) True if the current browser is internet explorer (any).
	Client.Engine.ie6       - (boolean) True if the current browser is internet explorer 6.
	Client.Engine.ie7       - (boolean) True if the current browser is internet explorer 7.
	Client.Engine.gecko     - (boolean) True if the current browser is Mozilla/Gecko.
	Client.Engine.webkit    - (boolean) True if the current browser is Safari/Konqueror.
	Client.Engine.webkit419 - (boolean) True if the current browser is Safari2 / webkit till version 419.
	Client.Engine.webkit420 - (boolean) True if the current browser is Safari3 (Webkit SVN Build) / webkit over version 419.
	Client.Engine.opera     - (boolean) True if the current browser is opera.
	Client.Engine.name      - (string) The name of the engine.

Platform:
	Client.Platform.mac     - (boolean) True if the platform is mac.
	Client.Platform.windows - (boolean) True if the platform is windows.
	Client.Platform.linux   - (boolean) True if the platform is linux.
	Client.Platform.other   - (boolean) True if the platform is neither mac, windows or linux.
	Client.Platform.name    - (string) The name of the platform.

Note:
	Engine detection is entirely object-based.
*/

var Client = {
	Engine: {'name': 'unknown', 'version': ''},
	Platform: {},
	Features: {}
};

//Client.Features
Client.Features.xhr = !!(window.XMLHttpRequest);
Client.Features.xpath = !!(document.evaluate);

//Client.Engine
if (window.opera) Client.Engine.name = 'opera';
else if (window.ActiveXObject) Client.Engine = {'name': 'ie', 'version': (Client.Features.xhr) ? 7 : 6};
else if (!navigator.taintEnabled) Client.Engine = {'name': 'webkit', 'version': (Client.Features.xpath) ? 420 : 419};
else if (document.getBoxObjectFor != null) Client.Engine.name = 'gecko';
Client.Engine[Client.Engine.name] = Client.Engine[Client.Engine.name + Client.Engine.version] = true;

//Client.Platform
Client.Platform.name = navigator.platform.match(/(mac)|(win)|(linux)|(nix)/i) || ['Other'];
Client.Platform.name = Client.Platform.name[0].toLowerCase();
Client.Platform[Client.Platform.name] = true;

//htmlelement
if (typeof HTMLElement == 'undefined'){
	var HTMLElement = $empty;
	if (Client.Engine.webkit) document.createElement("iframe"); //fixes safari 2
	HTMLElement.prototype = (Client.Engine.webkit) ? window["[[DOMElement.prototype]]"] : {};
}
HTMLElement.prototype.htmlElement = $empty;
HTMLElement.prototype.$family = 'element';

//enable background image cache for internet explorer 6
if (Client.Engine.ie6) $try(function(){
	document.execCommand("BackgroundImageCache", false, true);
});/*
Script: Class.js
	Contains the Class and Abstract implementations.

License:
	MIT-style license.
*/

/*
Class: Class
	The base Class object of the <http://mootools.net> framework.
	Creates a new Class.  Its initialize method will fire upon class instantiation unless *null* is passed to the Class constructor.

Syntax:
	>var MyClass = new Class(properties);

Arguments:
	properties - (object) The collection of properties that apply to the Class. Also accepts Extends and Implements. See below.
	
Example:
	(start code)
	var Cat = new Class({
		initialize: function(name){
			this.name = name;
		}
	});
	var myCat = new Cat('Micia');
	alert(myCat.name); //alerts 'Micia'
	(end)
	
Implements:
	Implements the passed in Class properties into the base Class prototypes. Similar to Extends, but it simply overrides the properties.
	Useful when implementing a Class properties in multiple classes.

	Implements Syntax:
		>var MyClass = new Class({Implements: SomeOtherClass});

	Implements Example:
		(start code)
		var Animal = new Class({
			initialize: function(age){
				this.age = age;
			}
		});
		var Cat = new Class({Implements: Animal,
			setName: function(name){
				this.name = name
			}
		});
		var myCat = new Cat(20);
		myAnimal.setName('Micia');
		alert(myAnimal.name); //alerts 'Micia'
		(end)
	
Extends:
	this class will be extended from the other class you pass in.

	Extends Syntax:
		>var MyExtendedClass = new Class({Extends: SomeOtherClass});

	Extends Example:
		(start code)
		var Animal = new Class({
			initialize: function(age){
				this.age = age;
			}
		});
		var Cat = new Class({Extends: Animal
			initialize: function(name, age){
				this.parent(age); //will call initalize of Animal
				this.name = name;
			}
		});
		var myCat = new Cat('Micia', 20);
		alert(myCat.name); //alerts 'Micia'
		alert(myCat.age); //alerts 20
		(end)
*/

var Class = function(properties){
	properties = properties || {};
	var klass = function(){
		var self = (arguments[0] !== $empty && this.initialize && $type(this.initialize) == 'function') ? this.initialize.apply(this, arguments) : this;
		if (this.options && this.options.initialize) this.options.initialize.call(this);
		return self;
	};
	
	if (properties.Implements){
		$extend(properties, Class.implement($splat(properties.Implements)));
		delete properties.Implements;
	}
	
	if (properties.Extends){
		properties = Class.extend(properties.Extends, properties);
		delete properties.Extends;
	}
	
	$extend(klass, this);
	klass.prototype = properties;
	klass.prototype.constructor = klass;
	klass.$family = 'class';
	return klass;
};

Class.empty = $empty;

Class.prototype = {

	constructor: Class,
	
	/*
	Property: extend
		Returns a copy of the Class extended with the properties passed in. The original Class will be unaltered.

	Syntax:
		>var MyExtendedClass = MyClass.extend(properties);

	Arguments:
		properties - (object) The properties to add to the base Class in this new Class.

	Example:
		(start code)
		var Animal = new Class({
			initialize: function(age){
				this.age = age;
			}
		});
		var Cat = Animal.extend({
			initialize: function(name, age){
				this.parent(age); //will call initalize of Animal
				this.name = name;
			}
		});
		var myCat = new Cat('Micia', 20);
		alert(myCat.name); //alerts 'Micia'
		alert(myCat.age); //alerts 20
		(end)
	*/

	extend: function(properties){
		return new Class(Class.extend(this, properties));
	},
	
	/*
	Property: implement
		Implements the passed in properties into the base Class prototypes, altering the base Class, unlike <Class.extend>.

	Syntax:
		>MyClass.implement(properties);

	Arguments:
		properties - (object) The properties to add to the base Class.

	Example:
		(start code)
		var Animal = new Class({
			initialize: function(age){
				this.age = age;
			}
		});
		Animal.implement({
			setName: function(name){
				this.name = name
			}
		});
		var myAnimal = new Animal(20);
		myAnimal.setName('Micia');
		alert(myAnimal.name); //alerts 'Micia'
		(end)
	*/

	implement: function(){
		$extend(this.prototype, Class.implement($A(arguments)));
		return this;
	}

};

Class.implement = function(sets){
	var all = {};
	for (var i = 0, l = sets.length; i < l; i++) $extend(all, ($type(sets[i]) == 'class') ? new sets[i]($empty) : sets[i]);
	return all;
};

Class.extend = function(klass, properties){
	var proto = new klass($empty);
	for (var property in properties){
		var pp = proto[property];
		proto[property] = Class.merge(pp, properties[property]);
	}
	return proto;
};

Class.merge = function(previous, current){
	if ($defined(previous) && previous != current){
		var type = $type(current);
		if (type != $type(previous)) return current;
		switch (type){
			case 'function':
				var merged = function(){
					this.parent = arguments.callee.parent;
					return current.apply(this, arguments);
				};
				merged.parent = previous;
				return merged;
			case 'object': return $merge(previous, current);
		}
	}
	return current;
};

/*
Class: Abstract
	-doc missing-
*/

var Abstract = function(obj){
	return $extend(this, obj || {});
};

Native(Abstract);

Abstract.extend({
	
	each: function(fn, bind){
		for (var property in this){
			if (this.hasOwnProperty(property)) fn.call(bind || this, this[property], property);
		}
	},
	
	remove: function(property){
		delete this[property];
		return this;
	},

	extend: $extend

});

Client = new Abstract(Client);/*
Script: Class.Extras.js
	Contains common implementations for custom classes.
	In Mootools these Utilities are implemented in <Ajax>, <XHR>, <Fx> and many other Classes to provide rich functionality.

License:
	MIT-style license.
*/

/*
Class: Chain
	A "Utility" Class which executes functions one after another, with each function firing after completion of the previous.
	Its methods can be implemented with <Class.implement> into any <Class>, and it is currently implemented in <Fx>, <XHR> and <Ajax>.
	In <Fx>, for example, it is used to create custom, complex animations.

Syntax:
	for new classes:
	> var MyClass = new Class({Implements: Chain});
	for existing classes:
	> MyClass.implement(new Chain);

Example:
	(start code)
	var myFx = new Fx.Style('element', 'opacity');
	myFx.start(1,0).chain(function(){
		myFx.start(0,1);
	}).chain(function(){
		myFx.start(1,0);
	}).chain(function(){
		myFx.start(0,1);
	});	//this will fade the element in and out three times
	(end)
*/

var Chain = new Class({

	/*
	Property: chain
		Adds a function to the Chain instance stack.

	Arguments:
		fn - (function) The function to append to the call stack.
	*/

	chain: function(fn){
		this.$chain = this.$chain || [];
		this.$chain.push(fn);
		return this;
	},

	/*
	Property: callChain
		Removes the first function of the Chain instance stack and executes it.  The next function will then become first in the array.
	*/

	callChain: function(){
		if (this.$chain && this.$chain.length) this.$chain.shift().delay(10, this);
	},

	/*
	Property: clearChain
		Clears the stack of a Chain instance.
	*/

	clearChain: function(){
		if (this.$chain) this.$chain.empty();
	}

});

/*
Class: Events
	A "Utility" Class. Its methods can be implemented with <Class.implement> into any <Class>.
	In <Fx>, for example, this Class is used to allow any number of functions to be added to the Fx events, like onComplete, onStart, and onCancel.
	Events in a Class that implements <Events> must be either added as an option or with addEvent, not directly through .options.onEventName.

Syntax:
	for new classes:
	> var MyClass = new Class({Implements: Events});
	for existing classes:
	> MyClass.implement(new Events);

Example:
	(start code)
	var myFx = new Fx.Style('element', 'opacity');
	myFx.addEvent('onStart', function(){
		alert('The effect has started.');
	}).addEvent('onComplete', function(){
		alert('The effect is complete.');
	});

	//will display an alert on start, and another on complete.
	myFx.start(0,1);
	(end)

Implementing:
	This class can be implemented into other classes to add its functionality to them.
	It has been designed to work well with the <Options> class.

Example:
	(start code)
	var Widget = new Class({
		initialize: function(element){
			...
		},
		complete: function(){
			this.fireEvent('onComplete');
		}
	});
	Widget.implement(new Events);
	//later...
	var myWidget = new Widget();
	myWidget.addEvent('onComplete', myFunction);
	(end)
*/

var Events = new Class({

	/*
	Property: addEvent
		Adds an event to the Class instance's event stack.

	Syntax:
		>myClass.addEvent(type, fn);

	Arguments:
		type - (string)   The type of event (e.g. 'onComplete').
		fn   - (function) The function to execute.

	Example:
		(start code)
		var myFx = new Fx.Style('element', 'opacity');
		myFx.addEvent('onStart', myStartFunction);
		(end)
	*/

	addEvent: function(type, fn, internal){
		if (fn != $empty){
			this.$events = this.$events || {};
			this.$events[type] = this.$events[type] || [];
			this.$events[type].include(fn);
			if (internal) fn.internal = true;
		}
		return this;
	},

	/*
	Property: addEvents
		Works as <addEvent>, but accepts an object to add multiple events at once.

	Syntax:
		>myClass.addEvents(events);

	Arguments:
		events - (object) An object containing the event type / function pairs.

	Example:
		(start code)
		var myFx = new Fx.Style('element', 'opacity');
		myFx.addEvents({
			'onStart': myStartFunction,
			'onComplete': myCompleteFunction
		});
		(end)
	*/

	addEvents: function(events){
		for (var type in events) this.addEvent(type, events[type]);
		return this;
	},

	/*
	Property: fireEvent
		Fires all events of the specified type in the Class instance.

	Syntax:
		>myClass.fireEvent(type[, args[, delay]]);

	Arguments:
		type  - (string) The type of event (e.g. 'onComplete').
		args  - (mixed, optional) The argument(s) to pass to the function. To pass more than one argument, the arguments must be in an array.
		delay - (integer, optional) Delay in miliseconds to wait before executing the event (defaults to 0).

	Example:
		(start code)
		var Widget = new Class({
			initialize: function(arg1, arg2){
				...
				this.fireEvent("onInitialize", [arg1, arg2], 50);
			}
		});
		Widget.implement(Events);
		(end)
	*/

	fireEvent: function(type, args, delay){
		if (this.$events && this.$events[type]){
			this.$events[type].each(function(fn){
				fn.create({'bind': this, 'delay': delay, 'arguments': args})();
			}, this);
		}
		return this;
	},

	/*
	Property: removeEvent
		Removes an event from the stack of events of the Class instance.

	Syntax:
		>myClass.removeEvent(type, fn);

	Arguments:
		type - (string) The type of event (e.g. 'onComplete').
		fn   - (function) The function to remove.
	*/

	removeEvent: function(type, fn){
		if (this.$events && this.$events[type]){
			if (!fn.internal) this.$events[type].remove(fn);
		}
		return this;
	},

	/*
	Property: removeEvents
		Removes all events of the given type from the stack of events of a Class instance. If no type is specified, removes all events of all types.

	Syntax:
		>myClass.removeEvents([type]);

	Arguments:
		type - (string, optional) The type of event to remove (e.g. 'onComplete'). If no type is specified, removes all events of all types.

	Example:
		(start code)
		var myFx = new Fx.Style('element', 'opacity');
		myFx.removeEvents('onComplete');
		(end)
	*/

	removeEvents: function(type){
		for (var e in this.$events){
			if (!type || type == e){
				var fns = this.$events[e];
				for (var i = fns.length; i--;) this.removeEvent(e, fns[i]);
			}
		}
		return this;
	}

});

/*
Class: Options
	A "Utility" Class. Its methods can be implemented with <Class.implement> into any <Class>.
	Used to automate the setting of a Class instance's options.
	Will also add Class <Events> when the option begins with on, followed by a capital letter (e.g. 'onComplete').

Syntax:
	for new classes:
	> var MyClass = new Class({Implements: Options});
	for existing classes:
	> MyClass.implement(Options);

Example:
	(start code)
	var Widget = new Class({
		options: {
			color: '#fff',
			size: {
				width: 100
				height: 100
			}
		},
		initialize: function(options){
			this.setOptions(options);
		}
	});
	Widget.implement(new Options);
	//later...
	var myWidget = new Widget({
		color: '#f00',
		size: {
			width: 200
		}
	});
	//myWidget.options is now {color: #f00, size: {width: 200, height: 100}}
	(end)
*/

var Options = new Class({

	/*
	Property: setOptions
		Merges the default options of the Class with the options passed in.

	Syntax:
		>myClass.setOptions([options]);

	Arguments:
		options - (object, optional) The user defined options to merge with the defaults.

	Note:
		Relies on the default options of a Class defined in its options object.
		If a Class has <Events> implemented, every option beginning with 'on' and followed by a capital letter (e.g. 'onComplete') becomes a Class instance event,
		assuming the value of the option is a function.

	Example:
		See above.
	*/

	setOptions: function(options){
		this.options = $merge(this.options, options);
		if (this.addEvent){
			for (var option in this.options){
				if ((/^on[A-Z]/).test(option) && $type(this.options[option] == 'function')) this.addEvent(option, this.options[option]);
			}
		}
		return this;
	}

});
/*
Script: Array.js
	Contains Array prototypes, <$A>, <$each>.

License:
	MIT-style license.
*/

/*
Class: Array
	A collection of the Array Object prototype methods.
	For more information on the JavaScript Array Object see <http://developer.mozilla.org/en/docs/Core_JavaScript_1.5_Reference:Global_Objects:Array>.
*/

Array.extend({

	/*
	Property: every
		Returns true if every element in the array satisfies the provided testing function.
		For more information see <http://developer.mozilla.org/en/docs/Core_JavaScript_1.5_Reference:Global_Objects:Array:every>.

		This method is provided only for browsers without native *every* support.

	Syntax:
		>var allPassed = myArray.every(fn[, bind]);

	Arguments:
		fn   - (function) The function to test for each element. This function is passed the item and its index in the array.
		bind - (object, optional) The object to use as 'this' in the function. For more information see <Function.bind>.

	Returns:
		(boolean) If every element in the array satisfies the provided testing function, returns true. Otherwise, returns false.

	Example:
		(start code)
		var areAllBigEnough = [10, 4, 25, 100].every(function(item, index){
			return item > 20;
		}); //areAllBigEnough = false
		(end)
	*/

	every: function(fn, bind){
		for (var i = 0, l = this.length; i < l; i++){
			if (!fn.call(bind, this[i], i, this)) return false;
		}
		return true;
	},

	/*
	Property: filter
		Creates a new array with all of the elements of the array for which the provided filtering function returns true.
		For more information see <http://developer.mozilla.org/en/docs/Core_JavaScript_1.5_Reference:Global_Objects:Array:filter>.

		This method is provided only for browsers without native *filter* support.

	Syntax:
		>var filteredArray = myArray.filter(fn[, bind]);

	Arguments:
		fn   - (function) The function to test each element of the array. This function is passed the item and its index in the array.
		bind - (object, optional) The object to use as 'this' in the function. For more information see <Function.bind>.

	Returns:
		(array) The new filtered array.

	Example:
		(start code)
		var biggerThanTwenty = [10, 3, 25, 100].filter(function(item, index){
			return item > 20;
		}); //biggerThanTwenty = [25, 100]
		(end)
	*/

	filter: function(fn, bind){
		var results = [];
		for (var i = 0, l = this.length; i < l; i++){
			if (fn.call(bind, this[i], i, this)) results.push(this[i]);
		}
		return results;
	},

	/*
	Property: forEach
		Calls a function for each element in the array.
		For more information see <http://developer.mozilla.org/en/docs/Core_JavaScript_1.5_Reference:Global_Objects:Array:forEach>.

		This method is only available for browsers without native *forEach* support.

	Syntax:
		>myArray.forEach(fn[, bind]);

	Arguments:
		fn   - (function) The function which should be executed on each item in the array. This function is passed the item and its index in the array.
		bind - (object, optional) The object to use as 'this' in the function. For more information see <Function.bind>.

	Example:
		(start code)
		['apple', 'banana', 'lemon'].forEach(function(item, index){
			alert(index + " = " + item); //alerts "0 = apple" etc.
		}, bind); //optional second argument for binding, not used here
		(end)
	*/

	forEach: function(fn, bind){
		for (var i = 0, l = this.length; i < l; i++) fn.call(bind, this[i], i, this);
	},

	/*
	Property: indexOf
		Returns the index of the first element within the array equal to the specified value, or -1 if the value is not found.
		For more information see <http://developer.mozilla.org/en/docs/Core_JavaScript_1.5_Reference:Global_Objects:Array:indexOf>.

		This method is provided only for browsers without native *indexOf* support.

	Syntax:
		>var index = myArray.indexOf(item[, from]);

	Returns:
		(integer) The index of the first element within the array equal to the specified value. If not found, returns -1.

	Arguments:
		item - (object) The item to search for in the array.
		from - (integer, optional) The index of the array at which to begin the search (defaults to 0).

	Example:
		(start code)
		['apple', 'lemon', 'banana'].indexOf('lemon'); //returns 1
		['apple', 'lemon'].indexOf('banana'); //returns -1
		(end)
	*/

	indexOf: function(item, from){
		var len = this.length;
		for (var i = (from < 0) ? Math.max(0, len + from) : from || 0; i < len; i++){
			if (this[i] === item) return i;
		}
		return -1;
	},

	/*
	Property: map
		Creates a new array with the results of calling a provided function on every element in the array.
		For more information see <http://developer.mozilla.org/en/docs/Core_JavaScript_1.5_Reference:Global_Objects:Array:map>.

		This method is provided only for browsers without native *map* support.

	Syntax:
		>var mappedArray = myArray.map(fn[, bind]);

	Arguments:
		fn   - (function) The function to produce an element of the new Array from an element of the current one.
			This function is passed the item and its index in the array.
		bind - (object, optional) The object to use as 'this' in the function. For more information see <Function.bind>.

	Returns:
		(array) The new mapped array.

	Example:
		(start code)
		var timesTwo = [1, 2, 3].map(function(item, index){
			return item * 2;
		}); //timesTwo = [2, 4, 6];
		(end)
	*/

	map: function(fn, bind){
		var results = [];
		for (var i = 0, l = this.length; i < l; i++) results[i] = fn.call(bind, this[i], i, this);
		return results;
	},

	/*
	Property: some
		Returns true if at least one element in the array satisfies the provided testing function.
		For more information see <http://developer.mozilla.org/en/docs/Core_JavaScript_1.5_Reference:Global_Objects:Array:some>.

		This method is provided only for browsers without native *some* support.

	Syntax:
		>var somePassed = myArray.some(fn[, bind]);

	Returns:
		(boolean) If at least one element in the array satisfies the provided testing function returns true. Otherwise, returns false.

	Arguments:
		fn   - (function) The function to test for each element. This function is passed the item and its index in the array.
		bind - (object, optional) The object to use as 'this' in the function. For more information see <Function.bind>.

	Example:
		(start code)
		var isAnyBigEnough = [10, 4, 25, 100].some(function(item, index){
			return item > 20;
		}); //isAnyBigEnough = true
		(end)
	*/

	some: function(fn, bind){
		for (var i = 0, l = this.length; i < l; i++){
			if (fn.call(bind, this[i], i, this)) return true;
		}
		return false;
	},

	/*
	Property: reduce
		Apply a function simultaneously against two values of the array (from left-to-right) as to reduce it to a single value.
		For more information see <http://developer.mozilla.org/en/docs/Core_JavaScript_1.5_Reference:Objects:Array:reduce>.

		This method is provided only for browsers without native *reduce* support.

	Syntax:
		>var reduced = myArray.reduce(fn[, value]);

	Arguments:
		fn    - (function) Function to execute on each value in the array.
			This function is passed the previous item, the current item, the current index and the array itself.
		value - (object, optional) Object to use as the initial argument to the first call of the callback.

	Returns:
		(mixed) The result of reducing this array according to fn.

	Examples:
		Sum up numbers
		(start code)
		var sum = [1, 2, 3, 4, 6].reduce(function(previousItem, currentItem){
			return previousItem + currentItem;
		}, 10); // sum is 26
		(end)

		Collect elements of many arrays into an array
		(start code)
		var collected = [['a', 'b'], ['c', 'd'], ['e', 'f', 'g']].reduce(function(previousItem, currentItem) {
			return previousItem.concat(currentItem);
		}, []); // collected is ['a', 'b', 'c', 'd', 'e', 'f', 'g']
		(end)
	*/

	reduce: function(fn, value){
		var i = 0;
		if (arguments.length < 2 && this.length) value = this[i++];
		for (var l = this.length; i < l; i++) value = fn.call(null, value, this[i], i, this);
		return value;
	},

	/*
	Property: associate
		Creates an object with key-value pairs based on the array of keywords passed in and the current content of the array.
		Can also accept an object of key / type pairs to assign values.

	Syntax:
		>var associated = myArray.associate(obj);

	Arguments:
		obj - (mixed) If an array is passed, its items will be used as the keys of the object that will be created.
			Alternatively, an object containing key / type pairs may be passed and used as a template for associating values with the different keys.

	Returns:
		(object) The new associated object.

	Examples:
		array example
		(start code)
		var animals = ['Cow', 'Pig', 'Dog', 'Cat'];
		var sounds = ['Moo', 'Oink', 'Woof', 'Miao'];
		animals.associate(sounds);
		//returns {'Cow': 'Moo', 'Pig': 'Oink', 'Dog': 'Woof', 'Cat': 'Miao'}
		(end)

		object example
		(start code)
		var values = [100, 'Hello', {foo: 'bar'}, $('myelement')];
		values.associate({myNumber: 'number', myElement: 'element', myObject: 'object', myString: 'string'});
		//returns {myNumber: 100, myElement: <div id="myelement">, myObject: {foo: bar}, myString: Hello}
		(end)
	*/

	associate: function(obj){
		var routed = {};
		var objtype = $type(obj);
		if (objtype == 'array'){
			var temp = {};
			for (var i = 0, j = obj.length; i < j; i++) temp[obj[i]] = true;
			obj = temp;
		}
		for (var oname in obj) routed[oname] = null;
		for (var k = 0, l = this.length; k < l; k++){
			var res = (objtype == 'array') ? $defined(this[k]) : $type(this[k]);
			for (var name in obj){
				if (!$defined(routed[name]) && ((res && obj[name] === true) || obj[name].contains(res))){
					routed[name] = this[k];
					break;
				}
			}
		}
		return routed;
	},

	/*
	Property: contains
		Tests an array for the presence of an item.

	Syntax:
		>var inArray = myArray.contains(item[, from])

	Arguments:
		item - (object) The item to search for in the array.
		from - (integer, optional) The index of the array at which to begin the search (defaults to 0).

	Returns:
		(boolean) If the array contains the item specified, returns true. Otherwise, returns false.

	Example:
		(start code)
		["a","b","c"].contains("a"); //returns true
		["a","b","c"].contains("d"); //returns false
		(end)
	*/

	contains: function(item, from){
		return this.indexOf(item, from) != -1;
	},

	/*
	Property: copy
		Returns a copy of the array.

	Syntax:
		>var copiedArray = myArray.copy([start, [length]]);

	Arguments:
		start  - (integer, optional) The index from which the copy should be started.
			If a negative number is provided, the offset is taken from the end of the array (defaults to 0).
		length - (integer, optional) The number of elements to copy (defaults to array.length - start).

	Returns:
		(array) The new copied array.

	Example:
		(start code)
		var letters = ["a","b","c"];
		var copy = letters.copy(); //copy = ["a", "b", "c"]
		(end)
	*/

	copy: function(start, length){
		return $A(this, start, length);
	},

	/*
	Property: each
		Same as <Array.forEach>.

	Syntax:
		>myArray.each(fn[, bind]);

	Arguments:
		fn   - (function) The function which should be executed on each item in the array. This function is passed the item and its index in the array.
		bind - (object, optional) The object to use as 'this' in the function. For more information see <Function.bind>.

	Example:
		(start code)
		['apple','banana','lemon'].each(function(item, index){
			alert(index + " = " + item); //alerts "0 = apple" etc.
		}, bind); //optional second argument for binding, not used here
		(end)
	*/

	/*
	Property: extend
		Extends an array with all the items of another.

	Syntax:
		>myArray.extend(array);

	Arguments:
		array - (array) The array whose items should be extended into this array.

	Returns:
		(array) This array, extended.

	Example:
		(start code)
		var animals = ['Cow', 'Pig', 'Dog'];
		animals.extend(['Cat', 'Dog']); //animals = ['Cow', 'Pig', 'Dog', 'Cat', 'Dog'];
		(end)
	*/

	extend: function(array){
		for (var i = 0, j = array.length; i < j; i++) this.push(array[i]);
		return this;
	},

	/*
	Property: getLast
		Returns the last item from the array.

	Syntax:
		>myArray.getLast();

	Returns:
		(mixed) The last item in this array. If this array is empty, returns null.

	Example:
		(start code)
		['Cow', 'Pig', 'Dog', 'Cat'].getLast(); //returns 'Cat'
		(end)
	*/

	getLast: function(){
		return (this.length) ? this[this.length - 1] : null;
	},

	/*
	Property: getRandom
		Returns a random item from the array.

	Syntax:
		>myArray.getRandom();

	Returns:
		(mixed) A random item from this array. If this array is empty, returns null.

	Example:
		(start code)
		['Cow', 'Pig', 'Dog', 'Cat'].getRandom(); //returns one of the items
		(end)
	*/

	getRandom: function(){
		return (this.length) ? this[$random(0, this.length - 1)] : null;
	},

	/*
	Property: include
		Pushes the passed element into the array if it's not already present (case and type sensitive).

	Syntax:
		>myArray.include(item);

	Arguments:
		item - (object) The item that should be added to this array.

	Returns:
		(array) This array with the new item included.

	Example:
		(start code)
		['Cow', 'Pig', 'Dog'].include('Cat'); //returns ['Cow', 'Pig', 'Dog', 'Cat']
		['Cow', 'Pig', 'Dog'].include('Dog'); //returns ['Cow', 'Pig', 'Dog']
		(end)
	*/

	include: function(item){
		if (!this.contains(item)) this.push(item);
		return this;
	},

	/*
	Property: merge
		Merges an array with all the items of another. Does not allow duplicates (case and type sensitive).

	Syntax:
		>myArray.merge(array);

	Arguments:
		array - (array) The array whose items should be merged into this array.

	Returns:
		(array) This array merged with the new items.

	Example:
		(start code)
		var animals = ['Cow', 'Pig', 'Dog'];
		animals.merge(['Cat', 'Dog']); //animals = ['Cow', 'Pig', 'Dog', 'Cat'];
		(end)
	*/

	merge: function(array){
		for (var i = 0, l = array.length; i < l; i++) this.include(array[i]);
		return this;
	},

	/*
	Property: remove
		Removes all occurrences of an item from the array.

	Syntax:
		>myArray.remove(item);

	Arguments:
		item - (object) The item to search for in the array.

	Returns:
		(array) This array with all occurrences of the item removed.

	Example:
		(start code)
		['Cow', 'Pig', 'Dog', 'Cat', 'Dog'].remove('Dog') //returns ['Cow', 'Pig', 'Cat']
		['Cow', 'Pig', 'Dog'].remove('Cat') //returns ['Cow', 'Pig', 'Dog']
		(end)
	*/

	remove: function(item){
		for (var i = this.length; i--;) if (this[i] === item) this.splice(i, 1);
		return this;
	},

	/*
	Property: empty
		Empties an array.

	Syntax:
		>myArray.empty();

	Returns:
		(array) This array, emptied.

	Example:
		(start code)
		var myArray = ['old', 'data'];
		myArray.empty(); // now myArray.length is 0
		(end code)
	*/

	empty: function(){
		this.length = 0;
		return this;
	}

});

//copied functions
Array.prototype.each = Array.prototype.forEach;
Array.each = Array.forEach;

/* Section: Utility Functions */

/*
Function: $each
	Use to iterate through iterables that are not regular arrays, such as builtin getElementsByTagName calls, arguments of a function, or an object.

Syntax:
	>$each(iterable, fn[, bind]);

Arguments:
	iterable - (object or array) The object or array to iterate through.
	fn       - (function) The function to test for each element. This function is passed the item and its index in the array.
		In the case of an object, it is passed the key of that item rather than the index.
	bind     - (object, optional) The object to use as 'this' in the function. For more information see <Function.bind>.

Examples:
	array example
	(start code)
	$each(['Sun','Mon','Tue'], function(day, index){
		alert('name:' + day + ', index: ' + index);
	}); //alerts "name: Sun, index: 0", "name: Mon, index: 1", etc.
	(end)

	object example
	(start code)
	$each({first: "Sunday", second: "Monday", third: "Tuesday"}, function(value, key){
		alert("the " + key + " day of the week is " + value);
	}); //alerts "the first day of the week is Sunday", "the second day of the week is Monday", etc.
	(end)
*/

function $each(iterable, fn, bind){
	((iterable && typeof iterable.length == 'number' && $type(iterable) != 'object') ? Array : Abstract).each(iterable, fn, bind);
};/*
Script: String.js
	Contains String prototypes.

License:
	MIT-style license.
*/

/*
Class: String
	A collection of the String Object prototype methods.
	For more information on the JavaScript String Object see <http://developer.mozilla.org/en/docs/Core_JavaScript_1.5_Reference:Global_Objects:String>
*/

String.extend({

	/*
	Property: test
		Searches for a match between the string and a regular expression.
		For more information see <http://developer.mozilla.org/en/docs/Core_JavaScript_1.5_Reference:Objects:RegExp:test>.

	Syntax:
		>myString.test(regex[,params]);

	Arguments:
		regex  - (mixed) The string or regular expression you want to match the string with.
		params - (string, optional) If first parameter is a string, any parameters you want to pass to the regular expression ('g' has no effect).

	Returns:
		(boolean) If a match for the regular expression is found in this string returns true. Otherwise, returns false.

	Example:
		(start code)
		"I like cookies".test("cookie"); //returns true
		"I like cookies".test("COOKIE", "i"); //returns true (ignore case)
		"I like cookies".test("cake"); //returns false
		(end)
	*/

	test: function(regex, params){
		return (($type(regex) == 'string') ? new RegExp(regex, params) : regex).test(this);
	},

	/*
	Property: contains
		Checks to see if the string passed in is contained in this string.
		If the separator parameter is passed, will check to see if the string is contained in the list of values separated by that parameter.

	Syntax:
		>myString.contains(string[, separator]);

	Arguments:
		string    - (string) The string to search for.
		separator - (string, optional) The string that separates the values in this string (eg. Element classNames are separated by a ' ').

	Returns:
		(boolean) If the string is contained in this string, returns true. Otherwise, returns false.

	Example:
		(start code)
		'a bc'.contains('bc'); //returns true
		'a b c'.contains('c', ' '); //returns true
		'a bc'.contains('b', ' '); //returns false
		(end)
	*/

	contains: function(string, separator){
		return (separator) ? (separator + this + separator).indexOf(separator + string + separator) > -1 : this.indexOf(string) > -1;
	},

	/*
	Property: trim
		Trims the leading and trailing spaces off a string.

	Syntax:
		>myString.trim();

	Returns:
		(string) The trimmed string.

	Example:
		(start code)
		"    i like cookies     ".trim(); //"i like cookies"
		(end)
	*/

	trim: function(){
		return this.replace(/^\s+|\s+$/g, '');
	},

	/*
	Property: clean
		Removes all extraneous whitespace from a string and trims (<String.trim>) it.

	Syntax:
		>myString.clean();

	Returns:
		(string) The cleaned string.

	Example:
		(start code)
		" i      like     cookies      \n\n".clean(); //returns "i like cookies"
		(end)
	*/

	clean: function(){
		return this.replace(/\s{2,}/g, ' ').trim();
	},

	/*
	Property: camelCase
		Converts a hyphenated string to a camelcased string.

	Syntax:
		>myString.camelCase();

	Returns:
		(string) The camelcased string.

	Example:
		(start code)
		"I-like-cookies".camelCase(); //returns "ILikeCookies"
		(end)
	*/

	camelCase: function(){
		return this.replace(/-\D/g, function(match){
			return match.charAt(1).toUpperCase();
		});
	},

	/*
	Property: hyphenate
		Converts a camelcased string to a hyphenated string.

	Syntax:
		>myString.hyphenate();

	Returns:
		(string) The hyphenated string.

	Example:
		(start code)
		"ILikeCookies".hyphenate(); //returns "I-like-cookies"
		(end)
	*/

	hyphenate: function(){
		return this.replace(/[A-Z]/g, function(match){
			return ('-' + match.charAt(0).toLowerCase());
		});
	},

	/*
	Property: capitalize
		Converts the first letter of each word in a string to uppercase.

	Syntax:
		>myString.capitalize();

	Returns:
		(string) The capitalized string.

	Example:
		(start code)
		"i like cookies".capitalize(); //returns "I Like Cookies"
		(end)
	*/

	capitalize: function(){
		return this.replace(/\b[a-z]/g, function(match){
			return match.toUpperCase();
		});
	},

	/*
	Property: escapeRegExp
		Escapes all regular expression characters from the string.

	Syntax:
		>myString.escapeRegExp();

	Returns:
		(string) The escaped string.

	Example:
		(start code)
		'animals.sheep[1]'.escapeRegExp(); //returns 'animals\.sheep\[1\]'
		(end)
	*/

	escapeRegExp: function(){
		return this.replace(/([.*+?^${}()|[\]\/\\])/g, '\\$1');
	},

	/*
	Property: toInt
		Parses this string and returns an integer of the specified radix or base.
		For more information see <http://developer.mozilla.org/en/docs/Core_JavaScript_1.5_Reference:Global_Functions:parseInt>.

	Syntax:
		>myString.toInt([base]);

	Arguments:
		base - (integer, optional) The base to use (defaults to 10).

	Returns:
		(mixed) The integer. If the string is not numeric, returns NaN.

	Example:
		(start code)
		"4em".toInt(); //returns 4
		"10px".toInt(); //returns 10
		(end)
	*/


	toInt: function(base){
		return parseInt(this, base || 10);
	},

	/*
	Property: toFloat
		Parses this string and returns a floating point number.
		For more information see <http://developer.mozilla.org/en/docs/Core_JavaScript_1.5_Reference:Global_Functions:parseFloat>.

	Syntax:
		>myString.toFloat();

	Returns:
		(mixed) The float. If the string is not numeric, returns NaN.

	Example:
		(start code)
		"95.25%".toFloat(); //returns 95.25
		"10.848".toFloat(); //returns 10.848
		(end)
	*/

	toFloat: function(){
		return parseFloat(this);
	},

	/*
	Property: hexToRgb
		Converts a hexidecimal color value to RGB. Input string must be in one of the following hexidecimal color formats (with or without the hash).
		>'#ffffff', #fff', 'ffffff', or 'fff'

	Syntax:
		myString.hexToRgb([array]);

	Arguments:
		array - (boolean, optional) If true is passed, will output an array (eg. ['ff','33','00']) instead of a string (eg. "#ff3300").

	Returns:
		(mixed) A string representing the color in RGB. If the array flag is set, an array will be returned instead.

	Example:
		(start code)
		"#123".hexToRgb(); //returns "rgb(17,34,51)"
		"112233".hexToRgb(); //returns "rgb(17,34,51)"
		"#112233".hexToRgb(true); //returns [17,34,51]
		(end)

	See Also:
		 <Array.hexToRgb>
	*/

	hexToRgb: function(array){
		var hex = this.match(/^#?(\w{1,2})(\w{1,2})(\w{1,2})$/);
		return (hex) ? hex.slice(1).hexToRgb(array) : false;
	},

	/*
	Property: rgbToHex
		Converts an RGB color value to hexidecimal. Input string must be in one of the following RGB color formats.
		>"rgb(255,255,255)", or "rgba(255,255,255,1)"

	Syntax:
		>myString.rgbToHex([array]);

	Arguments:
		array - (boolean, optional) If true is passed, will output an array (eg. ['ff','33','00']) instead of a string (eg. "#ff3300").

	Returns:
		(mixed) A string representing the color in hexadecimal,
		or transparent if the fourth value of rgba in the input string is 0. If the array flag is set, an array will be returned instead.

	Example:
		(start code)
		"rgb(17,34,51)".rgbToHex(); //returns "#112233"
		"rgb(17,34,51)".rgbToHex(true); //returns ['11','22','33']
		"rgba(17,34,51,0)".rgbToHex(); //returns "transparent"
		(end)

	See Also:
		 <Array.rgbToHex>
	*/

	rgbToHex: function(array){
		var rgb = this.match(/\d{1,3}/g);
		return (rgb) ? rgb.rgbToHex(array) : false;
	}

});

/*
Class: Array
	A collection of the Array Object prototype methods.
	For more information on the JavaScript Array Object see <http://developer.mozilla.org/en/docs/Core_JavaScript_1.5_Reference:Global_Objects:Array>.
*/

Array.extend({

	/*
	Property: hexToRgb
		Converts a hexidecimal color value to RGB. Input array must be in one of the following hexidecimal color formats.
		>['ff', 'ff', 'ff'], or ['f', 'f', 'f']

	Syntax:
		myArray.hexToRgb([array]);

	Arguments:
		array - (boolean, optional) If true is passed, will output an array (eg. ['ff','33','00']) instead of a string (eg. "#ff3300").

	Returns:
		(mixed) A string representing the color in RGB. If the array flag is set, an array will be returned instead.

	Example:
		(start code)
		["1", "2", "3"].hexToRgb(); //returns "rgb(17,34,51)"
		["11", "22", "33"].hexToRgb(); //returns "rgb(17,34,51)"
		["11", "22", "33"].hexToRgb(true); //returns [17,34,51]
		(end)

	See Also:
		 <String.hexToRgb>
	*/

	hexToRgb: function(array){
		if (this.length != 3) return null;
		var rgb = [];
		for (var i = 0; i < 3; i++){
			rgb.push(((this[i].length == 1) ? this[i] + this[i] : this[i]).toInt(16));
		}
		return array ? rgb : 'rgb(' + rgb.join(',') + ')';
	},

	/*
	Property: rgbToHex
		Converts an RGB color value to hexidecimal. Input array must be in one of the following RGB color formats.
		>[255,255,255], or [255,255,255,1]

	Syntax:
		>myArray.rgbToHex([array]);

	Arguments:
		array - (boolean, optional) If true is passed, will output an array (eg. ['ff','33','00']) instead of a string (eg. "#ff3300").

	Returns:
		(mixed) A string representing the color in hexadecimal, or transparent if the fourth value of rgba in the input array is 0.
		If the array flag is set, an array will be returned instead.

	Example:
		(start code)
		[17,34,51].rgbToHex(); //returns "#112233"
		[17,34,51].rgbToHex(true); //returns ['11','22','33']
		[17,34,51,0].rgbToHex(); //returns "transparent"
		(end)

	See Also:
		 <String.rgbToHex>
	*/

	rgbToHex: function(array){
		if (this.length < 3) return null;
		if (this.length == 4 && this[3] == 0 && !array) return 'transparent';
		var hex = [];
		for (var i = 0; i < 3; i++){
			var bit = (this[i] - 0).toString(16);
			hex.push((bit.length == 1) ? '0' + bit : bit);
		}
		return array ? hex : '#' + hex.join('');
	}

});/*
Script: Function.js
	Contains Function prototypes and utility functions .

License:
	MIT-style license.

Credits:
	- Some functions are inspired by those found in prototype.js <http://prototype.conio.net/> (c) 2005 Sam Stephenson sam [at] conio [dot] net, MIT-style license
*/

/*
Class: Function
	A collection of The Function Object prototype methods.
*/

Function.extend({

	extend: $extend,

	/*
	Property: create
		Main function to create closures.

	Returns:
		a function.

	Arguments:
		options - An Options object.

	Options:
		bind - The object that the "this" of the function will refer to. Default is the current function.
		event - If set to true, the function will act as an event listener and receive an event as first argument.
				If set to a class name, the function will receive a new instance of this class (with the event passed as argument's constructor) as first argument.
				Default is false.
		arguments - A single argument or array of arguments that will be passed to the function when called.

					If both the event and arguments options are set, the event is passed as first argument and the arguments array will follow.

					Default is no custom arguments, the function will receive the standard arguments when called.

		delay - Numeric value: if set, the returned function will delay the actual execution by this amount of milliseconds and return a timer handle when called.
			Default is no delay.
		periodical - Numeric value: if set, the returned function will periodically perform the actual execution with this specified interval
			and return a timer handle when called.
			Default is no periodical execution.
		attempt - If set to true, the returned function will try to execute and return either the results or false on error. Default is false.
	*/

	create: function(options){
		var self = this;
		options = options || {};
		return function(event){
			var args = $splat(options.arguments) || arguments;
			if (options.event) args = [event || window.event].extend(args);
			var returns = function(){
				return self.apply($pick(options.bind, self), args);
			};
			if (options.delay) return setTimeout(returns, options.delay);
			if (options.periodical) return setInterval(returns, options.periodical);
			if (options.attempt) return $try(returns);
			return returns();
		};
	},

	/*
	Property: pass
		Shortcut to create closures with arguments and bind.

	Returns:
		a function.

	Arguments:
		args - the arguments passed. must be an array if arguments > 1
		bind - optional, the object that the "this" of the function will refer to.

	Example:
		>myFunction.pass([arg1, arg2], myElement);
	*/

	pass: function(args, bind){
		return this.create({'arguments': args, 'bind': bind});
	},

	/*
	Property: attempt
		Tries to execute the function, returns either the result of the function or false on error.

	Arguments:
		args - the arguments passed. must be an array if arguments > 1
		bind - optional, the object that the "this" of the function will refer to.

	Example:
		>myFunction.attempt([arg1, arg2], myElement);
	*/

	attempt: function(args, bind){
		return this.create({'arguments': args, 'bind': bind, 'attempt': true})();
	},

	/*
	Property: bind
		method to easily create closures with "this" altered.

	Arguments:
		bind - optional, the object that the "this" of the function will refer to.
		args - optional, the arguments passed. must be an array if arguments > 1

	Returns:
		a function.

	Example:
		>function myFunction(){
		>	this.setStyle('color', 'red');
		>	// note that 'this' here refers to myFunction, not an element
		>	// we'll need to bind this function to the element we want to alter
		>};
		>var myBoundFunction = myFunction.bind(myElement);
		>myBoundFunction(); // this will make the element myElement red.
	*/

	bind: function(bind, args, evt){
		return this.create({'bind': bind, 'arguments': args, 'event': evt});
	},

	/*
	Property: delay
		Delays the execution of a function by a specified duration.

	Arguments:
		delay - the duration to wait in milliseconds.
		bind - optional, the object that the "this" of the function will refer to.
		args - optional, the arguments passed. must be an array if arguments > 1

	Example:
		>myFunction.delay(50, myElement) //wait 50 milliseconds, then call myFunction and bind myElement to it
		>(function(){alert('one second later...')}).delay(1000); //wait a second and alert
	*/

	delay: function(delay, bind, args){
		return this.create({'delay': delay, 'bind': bind, 'arguments': args})();
	},

	/*
	Property: periodical
		Executes a function in the specified intervals of time

	Arguments:
		interval - the duration of the intervals between executions.
		bind - optional, the object that the "this" of the function will refer to.
		args - optional, the arguments passed. must be an array if arguments > 1
	*/

	periodical: function(interval, bind, args){
		return this.create({'periodical': interval, 'bind': bind, 'arguments': args})();
	}

});

Function.empty = $empty;/*
Script: Number.js
	Contains the Number prototypes.

License:
	MIT-style license.
*/

/*
Class: Number
	A collection of the Number Object prototype methods.
	For more information on the JavaScript Number Object see <http://developer.mozilla.org/en/docs/Core_JavaScript_1.5_Reference:Global_Objects:Number>.
*/

Number.extend({

	/*
	Property: limit
		Limits this number between two bounds.

	Syntax:
		>myNumber.limit(min, max);

	Arguments:
		min - (number) The minimum possible value.
		max - (number) The maximum possible value.

	Returns:
		(number) The number bounded between the given limits.

	Example:
		>(12).limit(2, 6.5); //returns 6.5
		>(-4).limit(2, 6.5); //returns 2
		>(4.3).limit(2, 6.5); //returns 4.3
	*/

	limit: function(min, max){
		return Math.min(max, Math.max(min, this));
	},

	/*
	Property: round
		Returns this number rounded to the specified precision.

	Syntax:
		>myNumber.round([precision]);

	Arguments:
		precision - (integer, optional) The number of digits after the decimal place (defaults to 0). Argument may also be negative.

	Returns:
		(number) The number, rounded.

	Example:
		>(12.45).round() //returns 12
		>(12.45).round(1) //returns 12.5
		>(12.45).round(-1) //returns 10
	*/

	round: function(precision){
		precision = Math.pow(10, precision || 0);
		return Math.round(this * precision) / precision;
	},

	/*
	Property: times
		Executes the function passed in the specified number of times.

	Syntax:
		>myNumber.times(fn[, bind]);

	Arguments:
		fn   - (function) The function which should be executed on each iteration of the loop. This function is passed the current iteration's index.
		bind - (object, optional) The object to use as 'this' in the function. For more information see <Function.bind>.

	Example:
		(start code)
		(4).times(alert); //alerts 0, 1, 2, 3
		(end)
	*/

	times: function(fn, bind){
		for (var i = 0; i < this; i++) fn.call(bind, i, this);
	},

	/*
	Property: toFloat
		Returns this number as a float. Useful because toFloat must work on both Strings and Numbers.

	Syntax:
		>myNumber.toFloat();

	Returns:
		(number) The number as a float.

	Example:
		(start code)
		(111).toFloat(); //returns 111
		(111.1).toFloat(); //returns 111.1
		(end)
	*/

	toFloat: function(){
		return parseFloat(this);
	},

	/*
	Property: toInt
		Returns this number as an integer in the base passed in. Useful because toInt must work on both Strings and Numbers.

	Syntax:
		>myNumber.toInt([base]);

	Arguments:
		base - (integer, optional) The base to use (defaults to 10).

	Returns:
		(integer) The number as an integer in the base provided.

	Example:
		(start code)
		(111).toInt(); //returns 111
		(111.1).toInt(); //returns 111
		(111).toInt(2); //returns 7
		(end)
	*/

	toInt: function(base){
		return parseInt(this, base || 10);
	}

});/*
Script: Element.js
	Contains useful Element prototypes, to be used with the dollar function <$>.

License:
	MIT-style license.

Credits:
	- Some functions are inspired by those found in prototype.js <http://prototype.conio.net/> (c) 2005 Sam Stephenson sam [at] conio [dot] net, MIT-style license
*/

/*
Class: Element
	Custom class to allow all of its methods to be used with any DOM element via the dollar function <$>.
*/

var Element = function(el, props){

	/*
	Property: initialize
		Creates a new element of the type passed in.

	Arguments:
		el - string; the tag name for the element you wish to create. you can also pass in an element reference, in which case it will be extended.
		props - object; the properties you want to add to your element.
		Accepts the same keys as <Element.setProperties>, but also allows events and styles

	Props:
		the key styles will be used as setStyles, the key events will be used as addEvents. any other key is used as setProperty.

	Example:
		(start code)
		new Element('a', {
			'styles': {
				'display': 'block',
				'border': '1px solid black'
			},
			'events': {
				'click': function(){
					//aaa
				},
				'mousedown': function(){
					//aaa
				}
			},
			'class': 'myClassSuperClass',
			'href': 'http://mad4milk.net'
		});

		(end)
	*/

	if ($type(el) == 'string'){
		if (Client.Engine.ie && props && (props.name || props.type)){
			var name = (props.name) ? ' name="' + props.name + '"' : '';
			var type = (props.type) ? ' type="' + props.type + '"' : '';
			delete props.name;
			delete props.type;
			el = '<' + el + name + type + '>';
		}
		el = document.createElement(el);
	}
	el = $(el);
	return (!props || !el) ? el : el.set(props);
};

Element.prototype = HTMLElement.prototype;

/*
Class: Elements
	- Every dom function such as <$$>, or in general every function that returns a collection of nodes in mootools, returns them as an Elements class.
	- The purpose of the Elements class is to allow <Element> methods to work also on <Elements> array.
	- Elements is an Array in first place, so it accepts all the <Array> methods.
	- Array methods have priority, so overlapping Element methods (remove, getLast) are changed to "method + Elements" (removeElements, getLastElements)
	- Every node of the Elements instance is already extended with <$>.

Example:
	>$$('myselector').each(function(el){
	> //...
	>});

	some iterations here, $$('myselector') is also an array.

	>$$('myselector').setStyle('color', 'red');
	every element returned by $$('myselector') also accepts <Element> methods, in this example every element will be made red.
*/

var Elements = function(elements, nocheck){
	elements = elements || [];
	var l = elements.length;
	if (nocheck || !l) return $extend(elements, this);
	var uniques = {};
	var returned = [];
	for (var i = 0; i < l; i++){
		var el = $(elements[i]);
		if (!el || uniques[el.$attributes.uid]) continue;
		uniques[el.$attributes.uid] = true;
		returned.push(el);
	}
	return $extend(returned, this);
};

/*
Section: Utility Functions

Function: $
	returns the element passed in with all the Element prototypes applied.

Arguments:
	el - a reference to an actual element or a string representing the id of an element

Example:
	>$('myElement') // gets a DOM element by id with all the Element prototypes applied.
	>var div = document.getElementById('myElement');
	>$(div) //returns an Element also with all the mootools extensions applied.

	You'll use this when you aren't sure if a variable is an actual element or an id, as
	well as just shorthand for document.getElementById().

Returns:
	a DOM element or false (if no id was found).

Note:
	you need to call $ on an element only once to get all the prototypes.
	But its no harm to call it multiple times, as it will detect if it has been already extended.
*/

function $(el){
	if (!el) return null;
	if (el.htmlElement) return Garbage.collect(el);
	var type = $type(el);
	if (type == 'string'){
		el = document.getElementById(el);
		type = (el) ? 'element' : false;
	}
	if (type != 'element') return (['window', 'document'].contains(type)) ? el : null;
	if (el.htmlElement) return Garbage.collect(el);
	if (Element.$badTags.contains(el.tagName.toLowerCase())) return el;
	$extend(el, Element.prototype);
	el.htmlElement = $empty;
	return Garbage.collect(el);
};

/*
Function: $$
	Selects, and extends DOM elements. Elements arrays returned with $$ will also accept all the <Element> methods.
	The return type of element methods run through $$ is always an array. If the return array is only made by elements,
	$$ will be applied automatically.

Arguments:
	HTML Collections, arrays of elements, arrays of strings as element ids, elements, strings as selectors.
	Any number of the above as arguments are accepted.

Note:
	if you load <Element.Selectors.js>, $$ will also accept CSS Selectors, otherwise the only selectors supported are tag names.

Example:
	>$$('a') //an array of all anchor tags on the page
	>$$('a', 'b') //an array of all anchor and bold tags on the page
	>$$('#myElement') //array containing only the element with id = myElement. (only with <Element.Selectors.js>)
	>$$('#myElement a.myClass') //an array of all anchor tags with the class "myClass"
	>//within the DOM element with id "myElement" (only with <Element.Selectors.js>)
	>$$(myelement, myelement2, 'a', ['myid', myid2, 'myid3'], document.getElementsByTagName('div')) //an array containing:
	>// the element referenced as myelement if existing,
	>// the element referenced as myelement2 if existing,
	>// all the elements with a as tag in the page,
	>// the element with id = myid if existing
	>// the element with id = myid2 if existing
	>// the element with id = myid3 if existing
	>// all the elements with div as tag in the page

Returns:
	array - array of all the dom elements matched, extended with <$>.  Returns as <Elements>.
*/

document.getElementsBySelector = document.getElementsByTagName;

function $$(){
	var elements = [];
	for (var i = 0, j = arguments.length; i < j; i++){
		var selector = arguments[i];
		switch ($type(selector)){
			case 'element': elements.push(selector); break;
			case false: case null: break;
			case 'string': selector = document.getElementsBySelector(selector, true);
			default: elements.extend(selector);
		}
	}
	return new Elements(elements);
};

Element.extend = function(properties){
	for (var property in properties){
		Element.prototype[property] = properties[property];
		Element[property] = Native.generic(property);
		Elements.prototype[(Array.prototype[property]) ? property + 'Elements' : property] = Elements.$multiply(property);
	}
};

Client.expand = function(properties){
	Element.extend(properties);
	window.extend(properties);
	document.extend(properties);
};

Elements.extend = function(properties){
	for (var property in properties){
		Elements.prototype[property] = properties[property];
		Elements[property] = Native.generic(property);
	}
};

Elements.$multiply = function(property){
	return function(){
		var args = arguments;
		var items = [];
		var elements = true;
		this.each(function(element){
			var returns = element[property].apply(element, args);
			items.push(returns);
			if (elements) elements = ($type(returns) == 'element');
		});
		return (elements) ? new Elements(items) : items;
	};
};

Element.Setters = new Abstract({

	attributes: function(properties){
		this.setProperties(properties);
	}

});

Element.Setters.properties = Element.Setters.attributes;

/*
Class: Element
	Custom class to allow all of its methods to be used with any DOM element via the dollar function <$>.
*/

Element.extend({

	getElement: function(tag){
		return $(this.getElementsByTagName(tag)[0] || null);
	},

	getElements: function(tag){
		return $$(this.getElementsByTagName(tag));
	},

	/*
	Property: set
		you can set events, styles and properties with this shortcut. same as calling new Element.
	*/

	set: function(props){
		for (var prop in props){
			if (Element.Setters[prop]) Element.Setters[prop].call(this, props[prop]);
			else this.setProperty(prop, props[prop]);
		}
		return this;
	},

	inject: function(el, where){
		el = $(el);
		switch (where){
			case 'before': el.parentNode.insertBefore(this, el); break;
			case 'after':
				var next = el.getNext();
				if (!next) el.parentNode.appendChild(this);
				else el.parentNode.insertBefore(this, next);
				break;
			case 'top':
				var first = el.firstChild;
				if (first){
					el.insertBefore(this, first);
					break;
				}
			default: el.appendChild(this);
		}
		return this;
	},

	/*
	Property: injectBefore
		Inserts the Element before the passed element.

	Arguments:
		el - an element reference or the id of the element to be injected in.

	Example:
		>html:
		><div id="myElement"></div>
		><div id="mySecondElement"></div>
		>js:
		>$('mySecondElement').injectBefore('myElement');
		>resulting html:
		><div id="mySecondElement"></div>
		><div id="myElement"></div>
	*/

	injectBefore: function(el){
		return this.inject(el, 'before');
	},

	/*
	Property: injectAfter
		Same as <Element.injectBefore>, but inserts the element after.
	*/

	injectAfter: function(el){
		return this.inject(el, 'after');
	},

	/*
	Property: injectInside
		Same as <Element.injectBefore>, but inserts the element inside.
	*/

	injectInside: function(el){
		return this.inject(el, 'bottom');
	},

	/*
	Property: injectTop
		Same as <Element.injectInside>, but inserts the element inside, at the top.
	*/

	injectTop: function(el){
		return this.inject(el, 'top');
	},

	/*
	Property: adopt
		Inserts the passed elements inside the Element.

	Arguments:
		accepts elements references, element ids as string, selectors ($$('stuff')) / array of elements, array of ids as strings and collections.
	*/

	adopt: function(){
		var elements = [];
		$each(arguments, function(argument){
			elements = elements.concat(argument);
		});
		$$(elements).inject(this);
		return this;
	},

	/*
	Property: remove
		Removes the Element from the DOM.

	Note:
		For <Elements> this method is named removeElements, because <Array.remove> has priority.

	Example:
		>$('myElement').remove() //bye bye
	*/

	remove: function(){
		return this.parentNode.removeChild(this);
	},

	/*
	Property: clone
		Clones the Element and returns the cloned one.

	Arguments:
		contents - boolean, when true the Element is cloned with childNodes, default true

	Returns:
		the cloned element

	Example:
		>var clone = $('myElement').clone().injectAfter('myElement');
		>//clones the Element and append the clone after the Element.
	*/

	clone: function(contents){
		var el = $(this.cloneNode(contents !== false));
		if (!el.$events) return el;
		el.$events = {};
		for (var type in this.$events) el.$events[type] = {
			'keys': $A(this.$events[type].keys),
			'values': $A(this.$events[type].values)
		};
		return el.removeEvents();
	},

	/*
	Property: replaceWith
		Replaces the Element with an element passed.

	Arguments:
		el - a string representing the element to be injected in (myElementId, or div), or an element reference.
		If you pass div or another tag, the element will be created.

	Returns:
		the passed in element

	Example:
		>$('myOldElement').replaceWith($('myNewElement')); //$('myOldElement') is gone, and $('myNewElement') is in its place.
	*/

	replaceWith: function(el){
		el = $(el);
		this.parentNode.replaceChild(el, this);
		return el;
	},

	/*
	Property: appendText
		Appends text node to a DOM element.

	Arguments:
		text - the text to append.

	Example:
		><div id="myElement">hey</div>
		>$('myElement').appendText(' howdy'); //myElement innerHTML is now "hey howdy"
	*/

	appendText: function(text){
		this.appendChild(document.createTextNode(text));
		return this;
	},

	/*
	Property: hasClass
		Tests the Element to see if it has the passed in className.

	Returns:
		true - the Element has the class
		false - it doesn't

	Arguments:
		className - string; the class name to test.

	Example:
		><div id="myElement" class="testClass"></div>
		>$('myElement').hasClass('testClass'); //returns true
	*/

	hasClass: function(className){
		return this.className.contains(className, ' ');
	},

	/*
	Property: addClass
		Adds the passed in class to the Element, if the element doesnt already have it.

	Arguments:
		className - string; the class name to add

	Example:
		><div id="myElement" class="testClass"></div>
		>$('myElement').addClass('newClass'); //<div id="myElement" class="testClass newClass"></div>
	*/

	addClass: function(className){
		if (!this.hasClass(className)) this.className = (this.className + ' ' + className).clean();
		return this;
	},

	/*
	Property: removeClass
		Works like <Element.addClass>, but removes the class from the element.
	*/

	removeClass: function(className){
		this.className = this.className.replace(new RegExp('(^|\\s)' + className + '(?:\\s|$)'), '$1').clean();
		return this;
	},

	/*
	Property: toggleClass
		Adds or removes the passed in class name to the element, depending on if it's present or not.

	Arguments:
		className - the class to add or remove

	Example:
		><div id="myElement" class="myClass"></div>
		>$('myElement').toggleClass('myClass');
		><div id="myElement" class=""></div>
		>$('myElement').toggleClass('myClass');
		><div id="myElement" class="myClass"></div>
	*/

	toggleClass: function(className){
		return this.hasClass(className) ? this.removeClass(className) : this.addClass(className);
	},

	walk: function(brother, start){
		brother += 'Sibling';
		var el = (start) ? this[start] : this[brother];
		while (el && $type(el) != 'element') el = el[brother];
		return $(el);
	},

	/*
	Property: getPrevious
		Returns the previousSibling of the Element, excluding text nodes.

	Example:
		>$('myElement').getPrevious(); //get the previous DOM element from myElement

	Returns:
		the sibling element or undefined if none found.
	*/

	getPrevious: function(){
		return this.walk('previous');
	},

	/*
	Property: getNext
		Works as Element.getPrevious, but tries to find the nextSibling.
	*/

	getNext: function(){
		return this.walk('next');
	},

	/*
	Property: getFirst
		Works as <Element.getPrevious>, but tries to find the firstChild.
	*/

	getFirst: function(){
		return this.walk('next', 'firstChild');
	},

	/*
	Property: getLast
		Works as <Element.getPrevious>, but tries to find the lastChild.

	Note:
		For <Elements> this method is named getLastElements, because <Array.getLast> has priority.
	*/

	getLast: function(){
		return this.walk('previous', 'lastChild');
	},

	/*
	Property: getParent
		returns the $(element.parentNode)
	*/

	getParent: function(){
		return $(this.parentNode);
	},

	/*
	Property: getChildren
		returns all the $(element.childNodes), excluding text nodes. Returns as <Elements>.
	*/

	getChildren: function(){
		return $$(this.childNodes);
	},

	/*
	Property: hasChild
		returns true if the passed in element is a child of the $(element).
	*/

	hasChild: function(el){
		return !!$A(this.getElementsByTagName('*')).contains(el);
	},

	/*
	Property: getProperty
		Gets the an attribute of the Element.

	Arguments:
		property - string; the attribute to retrieve

	Example:
		>$('myImage').getProperty('src') // returns whatever.gif

	Returns:
		the value, or an empty string
	*/

	getProperty: function(property){
		var index = Element.$attributes[property];
		if (index) return this[index];
		var flag = Element.$attributesIFlag[property] || 0;
		if (!Client.Engine.ie || flag) return this.getAttribute(property, flag);
		var node = this.attributes[property];
		return (node) ? node.nodeValue : null;
	},

	/*
	Property: removeProperty
		Removes an attribute from the Element

	Arguments:
		property - string; the attribute to remove
	*/

	removeProperty: function(property){
		var index = Element.$attributes[property];
		if (index) this[index] = '';
		else this.removeAttribute(property);
		return this;
	},

	/*
	Property: getProperties
		same as <Element.getStyles>, but for properties
	*/

	getProperties: function(){
		var result = {};
		$each(arguments, function(key){
			result[key] = this.getProperty(key);
		}, this);
		return result;
	},

	/*
	Property: setProperty
		Sets an attribute for the Element.

	Arguments:
		property - string; the property to assign the value passed in
		value - the value to assign to the property passed in

	Example:
		>$('myImage').setProperty('src', 'whatever.gif'); //myImage now points to whatever.gif for its source
	*/

	setProperty: function(property, value){
		var index = Element.$attributes[property];
		if (index) this[index] = value;
		else this.setAttribute(property, value);
		return this;
	},

	/*
	Property: setProperties
		Sets numerous attributes for the Element.

	Arguments:
		source - an object with key/value pairs.

	Example:
		(start code)
		$('myElement').setProperties({
			src: 'whatever.gif',
			alt: 'whatever dude'
		});
		<img src="whatever.gif" alt="whatever dude">
		(end)
	*/

	setProperties: function(properties){
		for (var property in properties) this.setProperty(property, properties[property]);
		return this;
	},

	/*
	Property: setHTML
		Sets the innerHTML of the Element.

	Arguments:
		html - string; the new innerHTML for the element.

	Example:
		>$('myElement').setHTML(newHTML) //the innerHTML of myElement is now = newHTML
	*/

	setHTML: function(){
		this.innerHTML = $A(arguments).join('');
		return this;
	},

	/*
	Property: setText
		Sets the inner text of the Element.

	Arguments:
		text - string; the new text content for the element.

	Example:
		>$('myElement').setText('some text') //the text of myElement is now = 'some text'
	*/

	setText: function(text){
		var tag = this.getTag();
		if (['style', 'script'].contains(tag)){
			if (Client.Engine.ie){
				if (tag == 'style') this.styleSheet.cssText = text;
				else if (tag ==  'script') this.setProperty('text', text);
				return this;
			} else {
				if (this.firstChild) this.removeChild(this.firstChild);
				return this.appendText(text);
			}
		}
		this[$defined(this.innerText) ? 'innerText' : 'textContent'] = text;
		return this;
	},

	/*
	Property: getText
		Gets the inner text of the Element.
	*/

	getText: function(){
		var tag = this.getTag();
		if (['style', 'script'].contains(tag)){
			if (Client.Engine.ie){
				if (tag == 'style') return this.styleSheet.cssText;
				else if (tag ==  'script') return this.getProperty('text');
			} else {
				return this.innerHTML;
			}
		}
		return ($pick(this.innerText, this.textContent));
	},

	/*
	Property: getTag
		Returns the tagName of the element in lower case.

	Example:
		>$('myImage').getTag() // returns 'img'

	Returns:
		The tag name in lower case
	*/

	getTag: function(){
		return this.tagName.toLowerCase();
	},

	/*
	Property: empty
		Empties an element of all its children.

	Example:
		>$('myDiv').empty() // empties the Div and returns it

	Returns:
		The element.
	*/

	empty: function(){
		Garbage.trash(this.getElementsByTagName('*'));
		return this.setHTML('');
	},

	/*
	Property: destroy
		Empties an element of all its children, removes and garbages the element.

	Example:
		>$('myDiv').destroy() // Div is no more.

	Returns:
		null
	*/

	destroy: function(){
		Garbage.kill(this.empty().remove());
		return null;
	}

});

Element.$badTags = ['object', 'embed'];

Element.$attributes = {
	'class': 'className', 'for': 'htmlFor', 'colspan': 'colSpan', 'rowspan': 'rowSpan',
	'accesskey': 'accessKey', 'tabindex': 'tabIndex', 'maxlength': 'maxLength',
	'readonly': 'readOnly', 'frameborder': 'frameBorder', 'value': 'value',
	'disabled': 'disabled', 'checked': 'checked', 'multiple': 'multiple', 'selected': 'selected'
};

Element.$attributesIFlag = {
	'href': 2, 'src': 2
};

Client.expand({

	addListener: function(type, fn){
		if (this.addEventListener) this.addEventListener(type, fn, false);
		else this.attachEvent('on' + type, fn);
		return this;
	},

	removeListener: function(type, fn){
		if (this.removeEventListener) this.removeEventListener(type, fn, false);
		else this.detachEvent('on' + type, fn);
		return this;
	}

});

Element.UID = 0;

var Garbage = {

	elements: {},

	collect: function(el){
		if (!el.$attributes){
			el.$attributes = {'opacity': 1, 'uid': Element.UID++};
			Garbage.elements[el.$attributes.uid] = el;
		}
		return el;
	},

	trash: function(elements){
		for (var i = elements.length, el; i--;){
			if (!(el = elements[i]) || !el.$attributes) continue;
			if (!el.tagName || Element.$badTags.contains(el.tagName.toLowerCase())) Garbage.kill(el);
		}
	},

	kill: function(el, unload){
		delete Garbage.elements[String(el.$attributes.uid)];
		if (el.$events) el.fireEvent('trash', unload).removeEvents();
		for (var p in el.$attributes) el.$attributes[p] = null;
		if (window.ie){
			for (var d in Element.prototype) el[d] = null;
		}
		el.htmlElement = el.$attributes = el = null;
	},

	empty: function(){
		Garbage.collect(window);
		Garbage.collect(document);
		for (var uid in Garbage.elements) Garbage.kill(Garbage.elements[uid], true);
	}

};

window.addListener('beforeunload', function(){
	window.addListener('unload', Garbage.empty);
	if (Client.Engine.ie) window.addListener('unload', CollectGarbage);
});/*
Script: Element.Style.js
	Contains useful Element prototypes, to set/get styles in a fashionable way.

License:
	MIT-style license.
*/

/*
Class: Element
	Custom class to allow all of its methods to be used with any DOM element via the dollar function <$>.
*/

Element.Setters.styles = function(styles){
	this.setStyles(styles);
};

Element.extend({

	/*
	Property: setStyle
		Sets a css property to the Element.

		Arguments:
			property - the property to set
			value - the value to which to set it; for numeric values that require "px" you can pass an integer

		Example:
			>$('myElement').setStyle('width', '300px'); //the width is now 300px
			>$('myElement').setStyle('width', 300); //the width is now 300px
	*/

	setStyle: function(property, value){
		switch (property){
			case 'opacity': return this.setOpacity(parseFloat(value));
			case 'float': property = (Client.Engine.ie) ? 'styleFloat' : 'cssFloat';
		}
		property = property.camelCase();
		if ($type(value) != 'string'){
			var map = (Element.Styles.All[property] || '@').split(' ');
			value = $splat(value).map(function(val, i){
				if (!map[i]) return '';
				return ($type(val) == 'number') ? map[i].replace('@', Math.round(val)) : val;
			}).join(' ');
		} else if (value == Number(value) + ''){
			value = Math.round(value);
		}
		this.style[property] = value;
		return this;
	},

	/*
	Property: setStyles
		Applies a collection of styles to the Element.

	Arguments:
		source - an object or string containing all the styles to apply. When its a string it overrides old style.

	Examples:
		>$('myElement').setStyles({
		>	border: '1px solid #000',
		>	width: 300,
		>	height: 400
		>});

		OR

		>$('myElement').setStyles('border: 1px solid #000; width: 300px; height: 400px;');
	*/

	setStyles: function(styles){
		switch ($type(styles)){
			case 'object': for (var style in styles) this.setStyle(style, styles[style]); break;
			case 'string': this.style.cssText = styles;
		}
		return this;
	},

	/*
	Property: setOpacity
		Sets the opacity of the Element, and sets also visibility == "hidden" if opacity == 0, and visibility = "visible" if opacity > 0.

	Arguments:
		opacity - float; Accepts values from 0 to 1.

	Example:
		>$('myElement').setOpacity(0.5) //make it 50% transparent
	*/

	setOpacity: function(opacity){
		if (opacity == 0){
			if (this.style.visibility != 'hidden') this.style.visibility = 'hidden';
		} else {
			if (this.style.visibility != 'visible') this.style.visibility = 'visible';
		}
		if (!this.currentStyle || !this.currentStyle.hasLayout) this.style.zoom = 1;
		if (Client.Engine.ie) this.style.filter = (opacity == 1) ? '' : 'alpha(opacity=' + opacity * 100 + ')';
		this.style.opacity = this.$attributes.opacity = opacity;
		return this;
	},

	/*
	Property: getStyle
		Returns the style of the Element given the property passed in.

	Arguments:
		property - the css style property you want to retrieve

	Example:
		>$('myElement').getStyle('width'); //returns "400px"
		>//but you can also use
		>$('myElement').getStyle('width').toInt(); //returns 400

	Returns:
		the style as a string
	*/

	getStyle: function(property){
		property = property.camelCase();
		if (property == 'opacity') return this.$attributes.opacity;
		var result = this.style[property];
		if (!$chk(result)){
			result = [];
			for (var style in Element.Styles.Short){
				if (property != style) continue;
				for (var s in Element.Styles.Short[style]) result.push(this.getStyle(s));
				return (result.every(function(item){
					return item == result[0];
				})) ? result[0] : result.join(' ');
			}
			if (document.defaultView) result = document.defaultView.getComputedStyle(this, null).getPropertyValue(property.hyphenate());
			else if (this.currentStyle) result = this.currentStyle[property];
		}
		if (result){
			result = String(result);
			var color = result.match(/rgba?\([\d\s,]+\)/);
			if (color) result = result.replace(color[0], color[0].rgbToHex());
		}
		return (Client.Engine.ie) ? Element.$fixStyle(property, result, this) : result;
	},

	/*
	Property: getStyles
		Returns an object of styles of the Element for each argument passed in.
		Arguments:
		properties - strings; any number of style properties
	Example:
		>$('myElement').getStyles('width','height','padding');
		>//returns an object like:
		>{width: "10px", height: "10px", padding: "10px 0px 10px 0px"}
	*/

	getStyles: function(){
		var result = {};
		$each(arguments, function(key){
			result[key] = this.getStyle(key);
		}, this);
		return result;
	}

});

Element.$fixStyle = function(property, result, element){
	if ($chk(parseInt(result))) return result;
	if (['height', 'width'].contains(property)){
		var values = (property == 'width') ? ['left', 'right'] : ['top', 'bottom'];
		var size = 0;
		values.each(function(value){
			size += element.getStyle('border-' + value + '-width').toInt() + element.getStyle('padding-' + value).toInt();
		});
		return element['offset' + property.capitalize()] - size + 'px';
	} else if (property.test(/border(.+)Width|margin|padding/)){
		return '0px';
	}
	return result;
};

Element.Styles = {

	All: {
		'width': '@px', 'height': '@px', 'left': '@px', 'top': '@px', 'bottom': '@px', 'right': '@px',
		'backgroundColor': 'rgb(@, @, @)', 'backgroundPosition': '@px @px', 'color': 'rgb(@, @, @)',
		'fontSize': '@px', 'letterSpacing': '@px', 'lineHeight': '@px',
		'margin': '@px @px @px @px', 'padding': '@px @px @px @px', 'border': '@px @ rgb(@, @, @) @px @ rgb(@, @, @) @px @ rgb(@, @, @)',
		'borderWidth': '@px @px @px @px', 'borderStyle': '@ @ @ @', 'borderColor': 'rgb(@, @, @) rgb(@, @, @) rgb(@, @, @) rgb(@, @, @)',
		'zIndex' : '@', 'zoom': '@', 'fontWeight': '@',
		'textIndent': '@px', 'opacity': '@'
	},

	Short: {'margin': {}, 'padding': {}, 'border': {}, 'borderWidth': {}, 'borderStyle': {}, 'borderColor': {}}

};

['Top', 'Right', 'Bottom', 'Left'].each(function(direction){
	var Short = Element.Styles.Short;
	var All = Element.Styles.All;
	['margin', 'padding'].each(function(style){
		var sd = style + direction;
		Short[style][sd] = All[sd] = '@px';
	});
	var bd = 'border' + direction;
	Short.border[bd] = All[bd] = '@px @ rgb(@, @, @)';
	var bdw = bd + 'Width', bds = bd + 'Style', bdc = bd + 'Color';
	Short[bd] = {};
	Short.borderWidth[bdw] = Short[bd][bdw] = '@px';
	Short.borderStyle[bds] = Short[bd][bds] = '@';
	Short.borderColor[bdc] = Short[bd][bdc] = 'rgb(@, @, @)';
});
/*
Script: Element.Event.js
	Contains the Event Class, Element methods to deal with Element events, custom Events.

License:
	MIT-style license.
*/

/*
Class: Event
	Cross browser methods to manage events.

Arguments:
	event - the event

Properties:
	shift - true if the user pressed the shift
	control - true if the user pressed the control
	alt - true if the user pressed the alt
	meta - true if the user pressed the meta key
	wheel - the amount of third button scrolling
	code - the keycode of the key pressed
	page.x - the x position of the mouse, relative to the full window
	page.y - the y position of the mouse, relative to the full window
	client.x - the x position of the mouse, relative to the viewport
	client.y - the y position of the mouse, relative to the viewport
	key - the key pressed as a lowercase string. key also returns 'enter', 'up', 'down', 'left', 'right', 'space', 'backspace', 'delete', 'esc'.
		Handy for these special keys.
	target - the event target
	relatedTarget - the event related target

Note:
	Accessing event.page / event.client requires an XHTML doctype.

Example:
	(start code)
	$('myLink').addEvent('keydown', function(event){
	 	// event is already the Event class, if you use el.onkeydown you have to write e = new Event(e);
		alert(event.key); //returns the lowercase letter pressed
		alert(event.shift); //returns true if the key pressed is shift
		if (event.key == 's' && event.control) alert('document saved');
	});
	(end)
*/

var Event = new Class({

	initialize: function(event){
		if (event && event.$extended) return event;
		this.$extended = true;
		event = event || window.event;
		this.event = event;
		this.type = event.type;
		this.target = event.target || event.srcElement;
		if (this.target.nodeType == 3) this.target = this.target.parentNode;

		this.shift = event.shiftKey;
		this.control = event.ctrlKey;
		this.alt = event.altKey;
		this.meta = event.metaKey;

		if (['DOMMouseScroll', 'mousewheel'].contains(this.type)){
			this.wheel = (event.wheelDelta) ? event.wheelDelta / 120 : -(event.detail || 0) / 3;
		} else if (this.type.contains('key')){
			this.code = event.which || event.keyCode;
			for (var name in Event.Keys){
				if (Event.Keys[name] == this.code){
					this.key = name;
					break;
				}
			}
			if (this.type == 'keydown'){
				var fKey = this.code - 111;
				if (fKey > 0 && fKey < 13) this.key = 'f' + fKey;
			}
			this.key = this.key || String.fromCharCode(this.code).toLowerCase();
		} else if (this.type.test(/(click|mouse|menu)/)){
			this.page = {
				'x': event.pageX || event.clientX + document.documentElement.scrollLeft,
				'y': event.pageY || event.clientY + document.documentElement.scrollTop
			};
			this.client = {
				'x': event.pageX ? event.pageX - window.pageXOffset : event.clientX,
				'y': event.pageY ? event.pageY - window.pageYOffset : event.clientY
			};
			this.rightClick = (event.which == 3) || (event.button == 2);
			switch (this.type){
				case 'mouseover': this.relatedTarget = event.relatedTarget || event.fromElement; break;
				case 'mouseout': this.relatedTarget = event.relatedTarget || event.toElement;
			}
			if (this.fixRelatedTarget.create({'bind': this, 'attempt': Client.Engine.gecko})() === false) this.relatedTarget = this.target;
		}
		return this;
	},

	/*
	Property: stop
		Stop an Event from propagating and also executes preventDefault
	*/

	stop: function(){
		return this.stopPropagation().preventDefault();
	},

	/*
	Property: stopPropagation
		cross browser method to stop the propagation of an event (will not allow the event to bubble up through the DOM)
	*/

	stopPropagation: function(){
		if (this.event.stopPropagation) this.event.stopPropagation();
		else this.event.cancelBubble = true;
		return this;
	},

	/*
	Property: preventDefault
		cross browser method to prevent the default action of the event
	*/

	preventDefault: function(){
		if (this.event.preventDefault) this.event.preventDefault();
		else this.event.returnValue = false;
		return this;
	},

	fixRelatedTarget: function(){
		var rel = this.relatedTarget;
		if (rel && rel.nodeType == 3) this.relatedTarget = rel.parentNode;
	}

});

/*
Property: keys
	you can add additional Event keys codes this way:

Example:
	(start code)
	Event.Keys.whatever = 80;
	$('myInput').addEvent(keydown, function(event){
		if (event.key == 'whatever') alert('whatever key clicked').
	});
	(end)
*/

Event.Keys = new Abstract({
	'enter': 13,
	'up': 38,
	'down': 40,
	'left': 37,
	'right': 39,
	'esc': 27,
	'space': 32,
	'backspace': 8,
	'tab': 9,
	'delete': 46
});

/*
Class: Element
	Custom class to allow all of its methods to be used with any DOM element via the dollar function <$>.
	These methods are also available on window and document.
*/

Element.Setters.events = function(events){
	this.addEvents(events);
};

Client.expand({

	/*
	Property: addEvent
		Attaches an event listener to a DOM element.
		The listener has the instance of the Event class as first argument.
		You can stop the Event by returning false in the listener or calling <Event.stop>.

	Arguments:
		type - the event to monitor ('click', 'load', etc) without the prefix 'on'.
		fn - the function to execute

	Example:
		>$('myElement').addEvent('click', function(){alert('clicked!')});
	*/

	addEvent: function(type, fn){
		this.$events = this.$events || {};
		if (!this.$events[type]) this.$events[type] = {'keys': [], 'values': []};
		if (this.$events[type].keys.contains(fn)) return this;
		this.$events[type].keys.push(fn);
		var realType = type;
		var custom = Element.Events[type];
		var map = fn;
		if (custom){
			if (custom.add) custom.add.call(this, fn);
			if (custom.map){
				map = function(event){
					if (custom.map.call(this, event)) return fn.call(this, event);
					return false;
				};
			}
			if (custom.type) realType = custom.type;
		}
		var defn = fn;
		var nativeEvent = Element.$events[realType] || 0;
		if (nativeEvent){
			if (nativeEvent == 2){
				var self = this;
				defn = function(event){
					event = new Event(event);
					if (map.call(self, event) === false) event.stop();
				};
			}
			this.addListener(realType, defn);
		}
		this.$events[type].values.push(defn);
		return this;
	},

	/*
	Property: removeEvent
		Works as Element.addEvent, but instead removes the previously added event listener.
	*/

	removeEvent: function(type, fn){
		if (!this.$events || !this.$events[type]) return this;
		var pos = this.$events[type].keys.indexOf(fn);
		if (pos == -1) return this;
		var key = this.$events[type].keys.splice(pos, 1)[0];
		var value = this.$events[type].values.splice(pos, 1)[0];
		var custom = Element.Events[type];
		if (custom){
			if (custom.remove) custom.remove.call(this, fn);
			if (custom.type) type = custom.type;
		}
		return (Element.$events[type]) ? this.removeListener(type, value) : this;
	},

	/*
	Property: addEvents
		As <addEvent>, but accepts an object and add multiple events at once.
	*/

	addEvents: function(events){
		for (var event in events) this.addEvent(event, events[event]);
		return this;
	},

	/*
	Property: removeEvents
		removes all events of a certain type from an element. if no argument is passed in, removes all events.

	Arguments:
		type - string; the event name (e.g. 'click')
	*/

	removeEvents: function(type){
		if (!this.$events) return this;
		if (!type){
			for (var evType in this.$events) this.removeEvents(evType);
			this.$events = null;
		} else if (this.$events[type]){
			while (this.$events[type].keys[0]) this.removeEvent(type, this.$events[type].keys[0]);
			this.$events[type] = null;
		}
		return this;
	},

	/*
	Property: fireEvent
		executes all events of the specified type present in the element.

	Arguments:
		type - string; the event name (e.g. 'click')
		args - array or single object; arguments to pass to the function; if more than one argument, must be an array
		delay - (integer) delay (in ms) to wait to execute the event
	*/

	fireEvent: function(type, args, delay){
		if (this.$events && this.$events[type]){
			this.$events[type].keys.each(function(fn){
				fn.create({'bind': this, 'delay': delay, 'arguments': args})();
			}, this);
		}
		return this;
	},

	/*
	Property: cloneEvents
		Clones all events from an element to this element.

	Arguments:
		from - element, copy all events from this element
		type - optional, copies only events of this type
	*/

	cloneEvents: function(from, type){
		if (!from.$events) return this;
		if (!type){
			for (var evType in from.$events) this.cloneEvents(from, evType);
		} else if (from.$events[type]){
			from.$events[type].keys.each(function(fn){
				this.addEvent(type, fn);
			}, this);
		}
		return this;
	}

});

Element.$events = {
	'click': 2, 'dblclick': 2, 'mouseup': 2, 'mousedown': 2, //mouse buttons
	'mousewheel': 2, 'DOMMouseScroll': 2, //mouse wheel
	'mouseover': 2, 'mouseout': 2, 'mousemove': 2, //mouse movement
	'keydown': 2, 'keypress': 2, 'keyup': 2, //keys
	'contextmenu': 2, 'submit': 2, //misc
	'load': 1, 'unload': 1, 'beforeunload': 1, 'resize': 1, 'move': 1, 'DOMContentLoaded': 1, 'readystatechange': 1, //window
	'focus': 1, 'blur': 1, 'change': 1, 'reset': 1, 'select': 1, //forms elements
	'error': 1, 'abort': 1, 'scroll': 1 //misc
};

/* Section: Custom Events */

Element.Events = new Abstract({

	/*
	Event: mouseenter
		In addition to the standard javascript events (load, mouseover, mouseout, click, etc.) <Event.js> contains two custom events
		this event fires when the mouse enters the area of the dom element;
		will not be fired again if the mouse crosses over children of the element (unlike mouseover)


	Example:
		>$(myElement).addEvent('mouseenter', myFunction);
	*/

	'mouseenter': {
		type: 'mouseover',
		map: function(event){
			var related = event.relatedTarget;
			return (related && related != this && !this.hasChild(related));
		}
	},

	/*
	Event: mouseleave
		this event fires when the mouse exits the area of the dom element; will not be fired again if the mouse crosses over children of the element (unlike mouseout)


	Example:
		>$(myElement).addEvent('mouseleave', myFunction);
	*/

	'mouseleave': {
		type: 'mouseout',
		map: function(event){
			var related = event.relatedTarget;
			return (related && related != this && !this.hasChild(related));
		}
	},

	'mousewheel': {
		type: (Client.Engine.gecko) ? 'DOMMouseScroll' : 'mousewheel'
	}

});/*
Script: Element.Filters.js
	Some filtering Elements methods.

License:
	MIT-style license.
*/

/*
Class: Elements
	A collection of methods to be used with <$$> elements collections.
*/

Elements.extend({

	/*
	Property: filterByTag
		Filters the collection by a specified tag name.
		Returns a new Elements collection, while the original remains untouched.
	*/

	filterByTag: function(tag, nocash){
		var elements = this.filter(function(el){
			return (Element.getTag(el) == tag);
		});
		return (nocash) ? elements : new Elements(elements, true);
	},

	/*
	Property: filterByClass
		Filters the collection by a specified class name.
		Returns a new Elements collection, while the original remains untouched.
	*/

	filterByClass: function(className, nocash){
		var elements = this.filter(function(el){
			return (el.className && el.className.contains(className, ' '));
		});
		return (nocash) ? elements : new Elements(elements, true);
	},

	/*
	Property: filterById
		Filters the collection by a specified ID.
		Returns a new Elements collection, while the original remains untouched.
	*/

	filterById: function(id, nocash){
		var elements = this.filter(function(el){
			return (el.id == id);
		});
		return (nocash) ? elements : new Elements(elements, true);
	},

	/*
	Property: filterByAttribute
		Filters the collection by a specified attribute.
		Returns a new Elements collection, while the original remains untouched.

	Arguments:
		name - the attribute name.
		operator - optional, the attribute operator.
		value - optional, the attribute value, only valid if the operator is specified.
	*/

	filterByAttribute: function(name, operator, value, nocash){
		var elements = this.filter(function(el){
			var current = Element.getProperty(el, name);
			if (current){
				switch (operator){
					case '=': return (current == value);
					case '*=': return (current.contains(value));
					case '^=': return (current.substr(0, value.length) == value);
					case '$=': return (current.substr(current.length - value.length) == value);
					case '!=': return (current != value);
					case '~=': return current.contains(value, ' ');
					case '|=': return current.contains(value, '-');
					default: return true;
				}
			};
			return false;
		});
		return (nocash) ? elements : new Elements(elements, true);
	}

});/*
Script: Element.Dimensions.js
	Contains Element prototypes to deal with Element size and position in space.

Note:
	The functions in this script require n XHTML doctype.

License:
	MIT-style license.
*/

/*
Class: Element
	Custom class to allow all of its methods to be used with any DOM element via the dollar function <$>.
*/

Element.extend({

	/*
	Property: scrollTo
		Scrolls the element to the specified coordinated (if the element has an overflow)

	Arguments:
		x - the x coordinate
		y - the y coordinate

	Example:
		>$('myElement').scrollTo(0, 100)
	*/

	scrollTo: function(x, y){
		this.scrollLeft = x;
		this.scrollTop = y;
	},

	/*
	Property: getSize
		Return an Object representing the size/scroll values of the element.

	Example:
		(start code)
		$('myElement').getSize();
		(end)

	Returns:
		(start code)
		{
			'scroll': {'x': 100, 'y': 100},
			'size': {'x': 200, 'y': 400},
			'scrollSize': {'x': 300, 'y': 500}
		}
		(end)
	*/

	getSize: function(){
		return {
			'scroll': {'x': this.scrollLeft, 'y': this.scrollTop},
			'size': {'x': this.offsetWidth, 'y': this.offsetHeight},
			'scrollSize': {'x': this.scrollWidth, 'y': this.scrollHeight}
		};
	},

	/*
	Property: getPosition
		Returns the real offsets of the element.

	Arguments:
		overflown - optional, an array of nested scrolling containers for scroll offset calculation,
		use this if your element is inside any element containing scrollbars

	Example:
		>$('element').getPosition();

	Returns:
		>{x: 100, y:500};
	*/

	getPosition: function(overflown){
		overflown = $splat(overflown) || [];
		var el = this, left = 0, top = 0;
		do {
			left += el.offsetLeft || 0;
			top += el.offsetTop || 0;
			el = el.offsetParent;
		} while (el);
		overflown.each(function(element){
			left -= element.scrollLeft || 0;
			top -= element.scrollTop || 0;
		});
		return {'x': left, 'y': top};
	},

	/*
	Property: getTop
		Returns the distance from the top of the window to the Element.

	Arguments:
		overflown - optional, an array of nested scrolling containers, see Element::getPosition
	*/

	getTop: function(overflown){
		return this.getPosition(overflown).y;
	},

	/*
	Property: getLeft
		Returns the distance from the left of the window to the Element.

	Arguments:
		overflown - optional, an array of nested scrolling containers, see Element::getPosition
	*/

	getLeft: function(overflown){
		return this.getPosition(overflown).x;
	},

	/*
	Property: getCoordinates
		Returns an object with width, height, left, right, top, and bottom, representing the values of the Element

	Arguments:
		overflown - optional, an array of nested scrolling containers, see Element::getPosition

	Example:
		(start code)
		var myValues = $('myElement').getCoordinates();
		(end)

	Returns:
		(start code)
		{
			width: 200,
			height: 300,
			left: 100,
			top: 50,
			right: 300,
			bottom: 350
		}
		(end)
	*/

	getCoordinates: function(overflown){
		var position = this.getPosition(overflown);
		var obj = {
			'width': this.offsetWidth,
			'height': this.offsetHeight,
			'left': position.x,
			'top': position.y
		};
		obj.right = obj.left + obj.width;
		obj.bottom = obj.top + obj.height;
		return obj;
	}

});/*
Script: Element.Form.js
	Contains Element prototypes to deal with Forms and their elements.

License:
	MIT-style license.
*/

/*
Class: Element
	Custom class to allow all of its methods to be used with any DOM element via the dollar function <$>.
*/

Element.extend({

	/*
	Property: getValue
		Returns the value of the Element, if its tag is textarea, select or input. getValue called on a multiple select will return an array.
	*/

	getValue: function(){
		switch (this.getTag()){
			case 'select':
				var values = [];
				$each(this.options, function(option){
					if (option.selected) values.push(option.value);
				});
				return (this.multiple) ? values : values[0];
			case 'input': if (!(this.checked && ['checkbox', 'radio'].contains(this.type)) && !['hidden', 'text', 'password'].contains(this.type)) break;
			case 'textarea': return this.value;
		}
		return false;
	},

	getFormElements: function(){
		return $$(this.getElementsByTagName('input'), this.getElementsByTagName('select'), this.getElementsByTagName('textarea'));
	},

	/*
	Property: toQueryString
		Reads the children inputs of the Element and generates a query string, based on their values. Used internally in <Ajax>

	Example:
		(start code)
		<form id="myForm" action="submit.php">
		<input name="email" value="bob@bob.com">
		<input name="zipCode" value="90210">
		</form>

		<script>
		 $('myForm').toQueryString()
		</script>
		(end)

		Returns:
			email=bob@bob.com&zipCode=90210
	*/

	toQueryString: function(){
		var queryString = [];
		this.getFormElements().each(function(el){
			var name = el.name;
			var value = el.getValue();
			if (value === false || !name || el.disabled) return;
			var qs = function(val){
				queryString.push(name + '=' + encodeURIComponent(val));
			};
			if ($type(value) == 'array') value.each(qs);
			else qs(value);
		});
		return queryString.join('&');
	}

});/*
Script: Selectors.js
	Css Query related <Element> extensions

License:
	MIT-style license.
*/

/*
Class: Element
	Custom class to allow all of its methods to be used with any Selectors element via the dollar function <$>.
*/

Element.$DOMMethods = {

	/*
	Property: getElements
		Gets all the elements within an element that match the given (single) selector.
		Returns as <Elements>.

	Arguments:
		selector - string; the css selector to match

	Examples:
		>$('myElement').getElements('a'); // get all anchors within myElement
		>$('myElement').getElements('input[name=dialog]') //get all input tags with name 'dialog'
		>$('myElement').getElements('input[name$=log]') //get all input tags with names ending with 'log'

	Notes:
		Supports these operators in attribute selectors:

		- = : is equal to
		- ^= : starts-with
		- $= : ends-with
		- != : is not equal to

		Xpath is used automatically for compliant browsers.
	*/

	getElements: function(selector, nocash){
		var items = [];
		var separators = [];
		selector = selector.trim().replace(Selectors.sRegExp, function(match){
			if (match.charAt(2)) match = match.trim();
			separators.push(match.charAt(0));
			return '%' + match.charAt(1);
		}).split('%');
		for (var i = 0, j = selector.length; i < j; i++){
			var params = Selectors.$parse(selector[i]);
			if (!params) break;
			var temp = Selectors.Method.getParam(items, separators[i - 1] || false, this, params.tag, params.id, params.classes, params.attributes, params.pseudos);
			if (!temp) break;
			items = temp;
		}
		return Selectors.Method.getItems(items, this, nocash);
	},

	/*
	Property: getElement
		Same as <Element.getElements>, but returns only the first. Alternate syntax for <$E>, where filter is the Element.
		Returns as <Element>.

	Arguments:
		selector - string; css selector
	*/

	getElement: function(selector){
		return $(this.getElements(selector, true)[0] || null);
	},

	/*
	Property: getElementsBySelector
		Same as <Element.getElements>, but allows for comma separated selectors, as in css. Alternate syntax for <$$>, where filter is the Element.
		Returns as <Elements>.

	Arguments:
		selector - string; css selector
	*/

	getElementsBySelector: function(selector, nocash){
		var elements = [];
		selector = selector.split(',');
		for (var i = 0, j = selector.length; i < j; i++) elements = elements.concat(this.getElements(selector[i], true));
		return (nocash) ? elements : new Elements(elements);
	}

};

Element.extend({

	/*
	Property: getElementById
		Targets an element with the specified id found inside the Element. Does not overwrite document.getElementById.

	Arguments:
		id - string; the id of the element to find.
	*/

	getElementById: function(id){
		var el = document.getElementById(id);
		if (el){
			while ((el = el.parentNode)) if (el == this) return el;
		}
		return null;
	}

});

document.extend(Element.$DOMMethods);
Element.extend(Element.$DOMMethods);

/* Section: Utility Functions */

/*
Function: $E
	Alias for <Element.getElement>, using document as context.
*/

var $E = document.getElement.bind(document);

var Selectors = {

	'regExp': /:([^-:(]+)[^:(]*(?:\((["']?)(.*?)\2\))?|\[(\w+)(?:([!*^$~|]?=)(["']?)(.*?)\6)?\]|\.[\w-]+|#[\w-]+|\w+|\*/g,

	'sRegExp': /\s*([+>~\s])[a-zA-Z#.*\s]/g

};

Selectors.$parse = function(selector){
	var params = {tag: '*', id: null, classes: [], attributes: [], pseudos: []};
	selector = selector.replace(Selectors.regExp, function(bit) {
		switch (bit.charAt(0)){
			case '.': params.classes.push(bit.slice(1)); break;
			case '#': params.id = bit.slice(1); break;
			case '[': params.attributes.push([arguments[4], arguments[5], arguments[7]]); break;
			case ':':
				var name = arguments[1];
				var xparser = Selectors.Pseudo[name];
				var pseudo = {'name': name, 'parser': xparser, 'argument': arguments[3]};
				if (xparser && xparser.parser) pseudo.argument = (xparser.parser.apply) ? xparser.parser(pseudo.argument) : xparser.parser;
				params.pseudos.push(pseudo);
				break;
			default: params.tag = bit;
		}
		return '';
	});
	return params;
};

Selectors.Pseudo = new Abstract();

Selectors.XPath = {

	getParam: function(items, separator, context, tag, id, classNames, attributes, pseudos){
		var temp = context.namespaceURI ? 'xhtml:' : '';
		switch (separator){
			case '~': case '+': temp += '/following-sibling::'; break;
			case '>': temp += '/'; break;
			case ' ': temp += '//';
		}
		temp += tag;
		if (separator == '+') temp += '[1]';
		var i;
		for (i = pseudos.length; i--; i){
			var pseudo = pseudos[i];
			if (pseudo.parser && pseudo.parser.xpath) temp += pseudo.parser.xpath(pseudo.argument);
			else temp += ($chk(pseudo.argument)) ? '[@' + pseudo.name + '="' + pseudo.argument + '"]' : '[@' + pseudo.name + ']';
		}
		if (id) temp += '[@id="' + id + '"]';
		for (i = classNames.length; i--; i) temp += '[contains(concat(" ", @class, " "), " ' + classNames[i] + ' ")]';
		for (i = attributes.length; i--; i){
			var bits = attributes[i];
			switch (bits[1]){
				case '=': temp += '[@' + bits[0] + '="' + bits[2] + '"]'; break;
				case '*=': temp += '[contains(@' + bits[0] + ', "' + bits[2] + '")]'; break;
				case '^=': temp += '[starts-with(@' + bits[0] + ', "' + bits[2] + '")]'; break;
				case '$=': temp += '[substring(@' + bits[0] + ', string-length(@' + bits[0] + ') - ' + bits[2].length + ' + 1) = "' + bits[2] + '"]'; break;
				case '!=': temp += '[@' + bits[0] + '!="' + bits[2] + '"]'; break;
				case '~=': temp += '[contains(concat(" ", @' + bits[0] + ', " "), " ' + bits[2] + ' ")]'; break;
				case '|=': temp += '[contains(concat("-", @' + bits[0] + ', "-"), "-' + bits[2] + '-")]'; break;
				default: temp += '[@' + bits[0] + ']';
			}
		}
		items.push(temp);
		return items;
	},

	getItems: function(items, context, nocash){
		var elements = [];
		var xpath = document.evaluate('.//' + items.join(''), context, Selectors.XPath.resolver, XPathResult.UNORDERED_NODE_SNAPSHOT_TYPE, null);
		for (var i = 0, j = xpath.snapshotLength; i < j; i++) elements[i] = (nocash) ? xpath.snapshotItem(i) : $(xpath.snapshotItem(i));
		return (nocash) ? elements : new Elements(elements, true);
	},

	resolver: function(prefix){
		return (prefix == 'xhtml') ? 'http://www.w3.org/1999/xhtml' : false;
	}

};

Selectors.Filter = {

	getParam: function(items, separator, context, tag, id, classNames, attributes, pseudos){
		var i;
		if (separator){
			var found = [], j = items.length;
			switch (separator){
				case ' ':
					for (i = 0; i < j; i++) found.extend(items[i].getElementsByTagName(tag));
					break;
				case '>':
					for (i = 0; i < j; i++){
						var children = items[i].childNodes;
						for (var k = 0, l = children.length; k < l; k++){
							if (Selectors.Filter.hasTag(children[k], tag)) found.push(children[k]);
						}
					}
					break;
				default:
					var all = !!(separator == '~');
					for (i = 0; i < j; i++){
						var next = items[i].nextSibling;
						while (next){
							if (Selectors.Filter.hasTag(next, tag)){
								found.push(next);
								if (!all) break;
							}
							next = next.nextSibling;
						}
					}
			}
			items = (id) ? Elements.filterById(found, id, true) : found;
		} else {
			if (id){
				var el = context.getElementById(id);
				if (!el || ((tag != '*') && (el.tagName.toLowerCase() != tag))) return false;
				items = [el];
			} else {
				items = $A(context.getElementsByTagName(tag));
			}
		}
		for (i = classNames.length; i--; i) items = Elements.filterByClass(items, classNames[i], true);
		for (i = attributes.length; i--; i){
			var bits = attributes[i];
			items = Elements.filterByAttribute(items, bits[0], bits[1], bits[2], true);
		}
		for (i = pseudos.length; i--; i){
			var pseudo = pseudos[i];
			if (pseudo.parser && pseudo.parser.filter){
				var temp = {}, xparser = pseudo.parser, argument = pseudo.argument;
				items = items.filter(function(el, i, array){
					return xparser.filter(el, argument, i, array, temp);
				});
				temp = null;
			} else {
				items = Elements.filterByAttribute(items, pseudo.name, ($chk(pseudo.argument)) ? '=' : false, pseudo.argument, true);
			}
		}
		return items;
	},

	getItems: function(items, context, nocash){
		return (nocash) ? items : new Elements(items);
	},

	hasTag: function(el, tag){
		return (el.nodeName && el.nodeType == 1 && (tag == '*' || el.tagName.toLowerCase() == tag));
	}

};

Selectors.Method = (Client.Features.xpath) ? Selectors.XPath : Selectors.Filter;
/*
Script: Selectors.Pseudo.js
	Some default Pseudo Selecors for <Selectors.js>

License:
	MIT-style license.
*/

Selectors.Pseudo.enabled = {

	xpath: function(){
		return '[not(@disabled)]';
	},

	filter: function(el){
		return !(el.disabled);
	}
};

Selectors.Pseudo.empty = {

	xpath: function(){
		return '[not(node())]';
	},

	filter: function(el){
		return !(el.innerText || el.textContent || '').length;
	}

};

Selectors.Pseudo.contains = {

	xpath: function(argument){
		return '[contains(text(), "' + argument + '")]';
	},

	filter: function(el, argument){
		for (var i = el.childNodes.length; i--;){
			var child = el.childNodes[i];
			if (child.nodeName && child.nodeType == 3 && child.nodeValue.contains(argument)) return true;
		}
		return false;
	}

};

Selectors.Pseudo.nth = {

	parser: function(argument){
		argument = (argument) ? argument.match(/^([+-]?\d*)?([nodev]+)?([+-]?\d*)?$/) : [null, 1, 'n', 0];
		if (!argument) return false;
		var inta = parseInt(argument[1]);
		var a = ($chk(inta)) ? inta : 1;
		var special = argument[2] || false;
		var b = parseInt(argument[3]) || 0;
		b = b - 1;
		while (b < 1) b += a;
		while (b >= a) b -= a;
		switch (special){
			case 'n': return {'a': a, 'b': b, 'special': 'n'};
			case 'odd': return {'a': 2, 'b': 0, 'special': 'n'};
			case 'even': return {'a': 2, 'b': 1, 'special': 'n'};
			case 'first': return {'a': 0, 'special': 'index'};
			case 'last': return {'special': 'last'};
			case 'only': return {'special': 'only'};
			default: return {'a': (a - 1), 'special': 'index'};
		}
	},

	xpath: function(argument){
		switch (argument.special){
			case 'n': return '[count(preceding-sibling::*) mod ' + argument.a + ' = ' + argument.b + ']';
			case 'last': return '[count(following-sibling::*) = 0]';
			case 'only': return '[not(preceding-sibling::* or following-sibling::*)]';
			default: return '[count(preceding-sibling::*) = ' + argument.a + ']';
		}
	},

	filter: function(el, argument, i, all, temp){
		if (i == 0) temp.parents = [];
		var parent = el.parentNode;
		if (!parent.$children){
			temp.parents.push(parent);
			parent.$children = parent.$children || Array.filter(parent.childNodes, function(child){
				return (child.nodeName && child.nodeType == 1);
			});
		}
		var include = false;
		switch (argument.special){
			case 'n': if (parent.$children.indexOf(el) % argument.a == argument.b) include = true; break;
			case 'last': if (parent.$children.getLast() == el) include = true; break;
			case 'only': if (parent.$children.length == 1) include = true; break;
			case 'index': if (parent.$children[argument.a] == el) include = true;
		}
		if (i == all.length - 1){
			for (var j = temp.parents.length; j--;){
				temp.parents[j].$children = null;
				if (Client.Engine.ie) temp.parents[j].removeAttribute('$children');
			}
		}
		return include;
	}

};

Selectors.Pseudo.extend({

	'even': {
		'parser': {'a': 2, 'b': 1, 'special': 'n'},
		'xpath': Selectors.Pseudo.nth.xpath,
		'filter': Selectors.Pseudo.nth.filter
	},

	'odd': {
		'parser': {'a': 2, 'b': 0, 'special': 'n'},
		'xpath': Selectors.Pseudo.nth.xpath,
		'filter': Selectors.Pseudo.nth.filter
	},

	'first': {
		'parser': {'a': 0, 'special': 'index'},
		'xpath': Selectors.Pseudo.nth.xpath,
		'filter': Selectors.Pseudo.nth.filter
	},

	'last': {
		'parser': {'special': 'last'},
		'xpath': Selectors.Pseudo.nth.xpath,
		'filter': Selectors.Pseudo.nth.filter
	},

	'only': {
		'parser': {'special': 'only'},
		'xpath': Selectors.Pseudo.nth.xpath,
		'filter': Selectors.Pseudo.nth.filter
	}

});/*
Script: Selectors.Pseudo.Children.js
	custom :children() pseudo selecor

License:
	MIT-style license.
*/

Selectors.Pseudo.children = {

	parser: function(argument){
		argument = (argument) ? argument.match(/^([-+]?\d*)?([\-+:])?([-+]?\d*)?$/) : [null, 0, false, 0];
		if (!argument) return false;
		argument[1] = parseInt(argument[1]) || 0;
		var int1 = parseInt(argument[3]);
		argument[3] = ($chk(int1)) ? int1 : 0;
		switch (argument[2]){
			case '-': case '+': case ':': return {'a': argument[1], 'b': argument[3], 'special': argument[2]};
			default: return {'a': argument[1], 'b': 0, 'special': 'index'};
		}
	},

	xpath: function(argument){
		var include = '';
		var len = 'count(../child::*)';
		var a = argument.a + ' + ' + ((argument.a < 0) ? len : 0);
		var b = argument.b + ' + ' + ((argument.b < 0) ? len : 0);
		var pos = 'position()';
		switch (argument.special){
			case '-':
				b = '((' + a + ' - ' + b + ') mod (' + len + '))';
				a += ' + 1';
				b += ' + 1';
				include = '(' + b + ' < 1 and (' + pos + ' <= ' + a + ' or ' + pos + ' >= (' + b + ' + ' + len + ')' + ')) or (' + pos + ' <= ' + a + ' and ' + pos + ' >= ' + b + ')';
			break;
			case '+': b = '((' + a + ' + ' + b + ') mod ( ' + len + '))';
			case ':':
				a += ' + 1';
				b += ' + 1';
				include = '(' + b + ' < ' + a + ' and (' + pos + ' >= ' + a + ' or ' + pos + ' <= ' + b + ')) or (' + pos + ' >= ' + a + ' and ' + pos + ' <= ' + b + ')';
			break;
			default: include = (a + ' + 1');
		}
		return '[' + include + ']';
	},

	filter: function(el, argument, i, all){
		var include = false;
		var len = all.length;
		var a = argument.a + ((argument.a < 0) ? len : 0);
		var b = argument.b + ((argument.b < 0) ? len : 0);
		switch (argument.special){
			case '-':
				b = (a - b) % len;
				include = (b < 0) ? (i <= (a - 1) || i >= (b + len)) : (i <= a && i >= b);
			break;
			case '+': b = (b + a) % len;
			case ':': include = (b < a) ? (i >= a || i <= b) : (i >= a && i <= b); break;
			default: include = (all[a] == el);
		}
		return include;
	}
};/*
Script: Window.DomReady.js
	Contains the custom event domready, for window.

License:
	MIT-style license.
*/

/* Section: Custom Events */

/*
Event: domready
	executes a function when the dom tree is loaded, without waiting for images. Only works when called from window.

Credits:
	(c) Dean Edwards/Matthias Miller/John Resig, remastered for MooTools.

Arguments:
	fn - the function to execute when the DOM is ready

Example:
	> window.addEvent('domready', function(){
	>	alert('the dom is ready');
	> });
*/

Element.Events.domready = {

	add: function(fn){
		if (Client.loaded){
			fn.call(this);
			return this;
		}
		var self = this;
		var domReady = function(){
			if (!arguments.callee.done){
				arguments.callee.done = true;
				fn.call(self);
			};
			return true;
		};
		var check = function(context){
			if ((Client.Engine.webkit ? ['loaded', 'complete'] : 'complete').contains(context.readyState)) return domReady();
			return false;
		};
		if (document.readyState && Client.Engine.webkit){
			(function(){
				if (!check(document)) arguments.callee.delay(50);
			})();
		} else if (document.readyState && Client.Engine.ie){
			var script = $('ie_domready');
			if (!script){
				var src = (window.location.protocol == 'https:') ? '//:' : 'javascript:void(0)';
				document.write('<script id="ie_domready" defer src="' + src + '"><\/script>');
				script = $('ie_domready');
			}
			if (!check(script)) script.addEvent('readystatechange', check.pass(script));
		} else {
			window.addEvent('load', domReady);
			document.addEvent('DOMContentLoaded', domReady);
		}
		return this;
	}

};

window.addEvent('domready', function(){
	Client.loaded = true;
});/*
Script: Window.Size.js
	Window cross-browser dimensions methods.

Note:
	The Functions in this script require an XHTML doctype.

License:
	MIT-style license.
*/

/*
Class: Client
	Cross browser methods to get various window dimensions.
	Warning: All these methods require that the browser operates in strict mode, not quirks mode.
*/

Client.extend({

	/*
	Property: getWidth
		Returns an integer representing the width of the browser window (without the scrollbar).
	*/

	getWidth: function(){
		if (Client.Engine.webkit419) return window.innerWidth;
		if (Client.Engine.opera) return document.body.clientWidth;
		return document.documentElement.clientWidth;
	},

	/*
	Property: getHeight
		Returns an integer representing the height of the browser window (without the scrollbar).
	*/

	getHeight: function(){
		if (Client.Engine.webkit419) return window.innerHeight;
		if (Client.Engine.opera) return document.body.clientHeight;
		return document.documentElement.clientHeight;
	},

	/*
	Property: getScrollWidth
		Returns an integer representing the scrollWidth of the window.
		This value is equal to or bigger than <getWidth>.

	See Also:
		<http://developer.mozilla.org/en/docs/DOM:element.scrollWidth>
	*/

	getScrollWidth: function(){
		if (Client.Engine.ie) return Math.max(document.documentElement.offsetWidth, document.documentElement.scrollWidth);
		if (Client.Engine.webkit) return document.body.scrollWidth;
		return document.documentElement.scrollWidth;
	},

	/*
	Property: getScrollHeight
		Returns an integer representing the scrollHeight of the window.
		This value is equal to or bigger than <getHeight>.

	See Also:
		<http://developer.mozilla.org/en/docs/DOM:element.scrollHeight>
	*/

	getScrollHeight: function(){
		if (Client.Engine.ie) return Math.max(document.documentElement.offsetHeight, document.documentElement.scrollHeight);
		if (Client.Engine.webkit) return document.body.scrollHeight;
		return document.documentElement.scrollHeight;
	},

	/*
	Property: getScrollLeft
		Returns an integer representing the scrollLeft of the window (the number of pixels the window has scrolled from the left).

	See Also:
		<http://developer.mozilla.org/en/docs/DOM:element.scrollLeft>
	*/

	getScrollLeft: function(){
		return window.pageXOffset || document.documentElement.scrollLeft;
	},

	/*
	Property: getScrollTop
		Returns an integer representing the scrollTop of the window (the number of pixels the window has scrolled from the top).

	See Also:
		<http://developer.mozilla.org/en/docs/DOM:element.scrollTop>
	*/

	getScrollTop: function(){
		return window.pageYOffset || document.documentElement.scrollTop;
	},

	/*
	Property: getSize
		Same as <Element.getSize>, but for window
	*/

	getSize: function(){
		return {
			'size': {'x': Client.getWidth(), 'y': Client.getHeight()},
			'scrollSize': {'x': Client.getScrollWidth(), 'y': Client.getScrollHeight()},
			'scroll': {'x': Client.getScrollLeft(), 'y': Client.getScrollTop()}
		};
	}

});

/*
Class: window
	Utility methods attached to the window object to match Element's equivalents
*/

window.extend({

	/*
	Property: getSize
		Same as <Client.getSize>
	*/

	getSize: Client.getSize,

	getPosition: function(){
		return {'x': 0, 'y': 0};
	}

});/*
Script: Fx.js
	Contains <Fx>, the foundamentals of the MooTools Effects.

License:
	MIT-style license.
*/

/*
Class: Fx
	Base class for the Effects.

Options:
	transition - (function) The equation to use for the effect see <Fx.Transitions>; default is <Fx.Transitions.Sine.easeInOut>
	duration - (number) The duration of the effect in ms (defaults to 500).
	unit - (string) The unit, e.g. 'px', 'em' for fonts or '%' (defaults to false).
	wait - (boolean) to wait or not to wait for a current transition to end before running another of the same instance (defaults to true).
	fps - (number) the frames per second for the transition (defaults to 50)

Events:
	onStart - The function to execute as the effect begins.
	onSet - The function to execute when value is setted without transition.
	onComplete - The function to execute after the effect has processed.
	onCancel - The function to execute when you manually stop the effect.
*/

var Fx = new Class({

	Implements: [Chain, Events, Options],

	options: {
		/*onStart: $empty,
		onComplete: $empty,
		onSet: $empty,
		onCancel: $empty,*/
		transition: function(p){
			return -(Math.cos(Math.PI * p) - 1) / 2;
		},
		duration: 500,
		unit: false,
		wait: true,
		fps: 50
	},

	initialize: function(){
		var params = $A(arguments).associate({'options': 'object', 'element': true});
		this.element = this.element || params.element;
		this.setOptions(params.options);
	},

	step: function(){
		var time = $time();
		if (time < this.time + this.options.duration){
			this.delta = this.options.transition((time - this.time) / this.options.duration);
			this.setNow();
			this.increase();
		} else {
			this.stop(true);
			this.now = this.to;
			this.increase();
			this.fireEvent('onComplete', this.element, 10);
			this.callChain();
		}
	},

	/*
	Property: set
		Immediately sets the value with no transition.

	Arguments:
		to - the point to jump to

	Example:
		>var myFx = new Fx.Style('myElement', 'opacity').set(0); //will make it immediately transparent
	*/

	set: function(to){
		this.now = to;
		this.increase();
		this.fireEvent('onSet', this.element);
		return this;
	},

	setNow: function(){
		this.now = this.compute(this.from, this.to);
	},

	compute: function(from, to){
		return (to - from) * this.delta + from;
	},

	/*
	Property: start
		Executes an effect from one position to the other.

	Arguments:
		from - integer: staring value
		to - integer: the ending value

	Examples:
		>var myFx = new Fx.Style('myElement', 'opacity').start(0,1); //display a transition from transparent to opaque.
	*/

	start: function(from, to){
		if (!this.options.wait) this.stop();
		else if (this.timer) return this;
		this.from = from;
		this.to = to;
		this.change = this.to - this.from;
		this.time = $time();
		this.timer = this.step.periodical(Math.round(1000 / this.options.fps), this);
		this.fireEvent('onStart', this.element);
		return this;
	},

	/*
	Property: stop
		Stops the transition.
	*/

	stop: function(end){
		if (!this.timer) return this;
		this.timer = $clear(this.timer);
		if (!end) this.fireEvent('onCancel', this.element);
		return this;
	}

});/*
Script: Fx.CSS.js
	Css parsing class for effects. Required by <Fx.Style>, <Fx.Styles>, <Fx.Elements>.

License:
	MIT-style license.
*/

Fx.CSS = {

	prepare: function(element, property, values){
		values = $splat(values);
		var values1 = values[1];
		if (!$chk(values1)){
			values[1] = values[0];
			values[0] = element.getStyle(property);
		}
		var parsed = values.map(Fx.CSS.set);
		return {'from': parsed[0], 'to': parsed[1]};
	},

	set: function(value){
		value = ($type(value) == 'string') ? value.split(' ') : $splat(value);
		return value.map(function(val){
			val = String(val);
			var found = false;
			Fx.CSS.Parsers.each(function(parser, key){
				if (!found){
					var match = parser.match(val);
					if ($chk(match)) found = {'value': match, 'parser': parser};
				}
			});
			return found || {'value': val, parser: {
				compute: function(from, to){
					return to;
				}
			}};
		});
	},

	compute: function(from, to, fx){
		return from.map(function(obj, i){
			return {'value': obj.parser.compute(obj.value, to[i].value, fx), 'parser': obj.parser};
		});
	},

	serve: function(now, unit){
		return now.reduce(function(prev, cur){
			var serve = cur.parser.serve;
			return prev.concat((serve) ? serve(cur.value, unit) : cur.value);
		}, []);
	}

};

Fx.CSS.Parsers = new Abstract({

	'color': {

		match: function(value){
			if (value.match(/^#[0-9a-f]{3,6}$/i)) return value.hexToRgb(true);
			return ((value = value.match(/(\d+),\s*(\d+),\s*(\d+)/))) ? [value[1], value[2], value[3]] : false;
		},

		compute: function(from, to, fx){
			return from.map(function(value, i){
				return Math.round(fx.compute(value, to[i]));
			});
		},

		serve: function(value){
			return value.map(Number);
		}

	},

	'number': {

		match: function(value){
			return parseFloat(value);
		},

		compute: function(from, to, fx){
			return fx.compute(from, to);
		},

		serve: function(value, unit){
			return (unit) ? value + unit : value;
		}

	}

});/*
Script: Fx.Elements.js
	Contains <Fx.Elements>

License:
	MIT-style license.
*/

/*
Class: Fx.Elements
	Fx.Elements allows you to apply any number of styles transitions to a selection of elements. Includes colors (must be in hex format).
	Inherits methods, properties, options and events from <Fx>.

Arguments:
	elements - a collection of elements the effects will be applied to.
	options - same as <Fx> options.
*/

Fx.Elements = new Class({
	
	Extends: Fx,

	initialize: function(elements, options){
		this.parent(elements, options);
		this.elements = $$(this.element);
	},

	setNow: function(){
		for (var i in this.from){
			var iFrom = this.from[i], iTo = this.to[i], iNow = this.now[i] = {};
			for (var p in iFrom) iNow[p] = Fx.CSS.compute(iFrom[p], iTo[p], this);
		}
	},

	set: function(to){
		var parsed = {};
		this.css = {};
		for (var i in to){
			var iTo = to[i], iParsed = parsed[i] = {};
			for (var p in iTo) iParsed[p] = Fx.CSS.set(iTo[p]);
		}
		return this.parent(parsed);
	},

	/*
	Property: start
		Applies the passed in style transitions to each object named (see example).
		Each item in the collection is refered to as a numerical string ("1" for instance). The first item is "0", the second "1", etc.

	Example:
		(start code)
		var myElementsEffects = new Fx.Elements($$('a'));
		myElementsEffects.start({
			'0': { //let's change the first element's opacity and width
				'opacity': [0,1],
				'width': [100,200]
			},
			'4': { //and the fifth one's opacity
				'opacity': [0.2, 0.5]
			}
		});
		(end)
	*/

	start: function(obj){
		if (this.timer && this.options.wait) return this;
		this.now = {};
		this.css = {};
		var from = {}, to = {};
		for (var i in obj){
			var iProps = obj[i], iFrom = from[i] = {}, iTo = to[i] = {};
			for (var p in iProps){
				var parsed = Fx.CSS.prepare(this.elements[i], p, iProps[p]);
				iFrom[p] = parsed.from;
				iTo[p] = parsed.to;
			}
		}
		return this.parent(from, to);
	},

	increase: function(){
		for (var i in this.now){
			var iNow = this.now[i];
			for (var p in iNow) this.elements[i].setStyle(p, Fx.CSS.serve(iNow[p], this.options.unit));
		}
	}

});/*
Script: Fx.Style.js
	Contains <Fx.Style>

License:
	MIT-style license.
*/

/*
Class: Fx.Style
	The Style effect, used to transition any css property from one value to another. Includes colors.
	Colors must be in hex format.
	Inherits methods, properties, options and events from <Fx>.

Arguments:
	el - the $(element) to apply the style transition to
	property - the property to transition
	options - the Fx options (see: <Fx>)

Example:
	>var marginChange = new Fx.Style('myElement', 'margin-top', {duration:500});
	>marginChange.start(10, 100);
*/

Fx.Style = new Class({
	
	Extends: Fx,

	initialize: function(element, property, options){
		this.parent($(element), options);
		this.property = property;
	},

	/*
	Property: hide
		Same as <Fx.set> (0); hides the element immediately without transition.
	*/

	hide: function(){
		return this.set(0);
	},

	setNow: function(){
		this.now = Fx.CSS.compute(this.from, this.to, this);
	},

	/*
	Property: set
		Sets the element's css property (specified at instantiation) to the specified value immediately.

	Example:
		(start code)
		var marginChange = new Fx.Style('myElement', 'margin-top', {duration:500});
		marginChange.set(10); //margin-top is set to 10px immediately
		(end)
	*/

	set: function(to){
		return this.parent(Fx.CSS.set(to));
	},

	/*
	Property: start
		Displays the transition to the value/values passed in

	Arguments:
		from - (integer; optional) the starting position for the transition
		to - (integer) the ending position for the transition

	Note:
		If you provide only one argument, the transition will use the current css value for its starting value.

	Example:
		(start code)
		var marginChange = new Fx.Style('myElement', 'margin-top', {duration:500});
		marginChange.start(10); //tries to read current margin top value and goes from current to 10
		(end)
	*/

	start: function(from, to){
		if (this.timer && this.options.wait) return this;
		var parsed = Fx.CSS.prepare(this.element, this.property, [from, to]);
		return this.parent(parsed.from, parsed.to);
	},

	increase: function(){
		this.element.setStyle(this.property, Fx.CSS.serve(this.now, this.options.unit));
	}

});

/*
Class: Element
	Custom class to allow all of its methods to be used with any DOM element via the dollar function <$>.
*/

Element.extend({

	/*
	Property: effect
		Applies an <Fx.Style> to the Element; This a shortcut for <Fx.Style>.

	Arguments:
		property - (string) the css property to alter
		options - (object; optional) key/value set of options (see <Fx.Style>)

	Example:
		>var myEffect = $('myElement').effect('height', {duration: 1000, transition: Fx.Transitions.linear});
		>myEffect.start(10, 100);
		>//OR
		>$('myElement').effect('height', {duration: 1000, transition: Fx.Transitions.linear}).start(10,100);
	*/

	effect: function(property, options){
		return new Fx.Style(this, property, options);
	}

});/*
Script: Fx.Styles.js
	Contains <Fx.Styles>

License:
	MIT-style license.
*/

/*
Class: Fx.Styles
	Allows you to animate multiple css properties at once;
	Colors must be in hex format.
	Inherits methods, properties, options and events from <Fx>.

Arguments:
	el - the $(element) to apply the styles transition to
	options - the fx options (see: <Fx>)

Example:
	(start code)
	var myEffects = new Fx.Styles('myElement', {duration: 1000, transition: Fx.Transitions.linear});

	//height from 10 to 100 and width from 900 to 300
	myEffects.start({
		'height': [10, 100],
		'width': [900, 300]
	});

	//or height from current height to 100 and width from current width to 300
	myEffects.start({
		'height': 100,
		'width': 300
	});
	(end)
*/

Fx.Styles = new Class({
	
	Extends: Fx,

	initialize: function(element, options){
		this.parent($(element), options);
	},

	setNow: function(){
		for (var p in this.from) this.now[p] = Fx.CSS.compute(this.from[p], this.to[p], this);
	},

	set: function(to){
		var parsed = {};
		for (var p in to) parsed[p] = Fx.CSS.set(to[p]);
		return this.parent(parsed);
	},

	/*
	Property: start
		Executes a transition for any number of css properties in tandem.

	Arguments:
		obj - an object containing keys that specify css properties to alter and values that specify either the from/to values (as an array)
		or just the end value (an integer).

	Example:
		see <Fx.Styles>
	*/

	start: function(obj){
		if (this.timer && this.options.wait) return this;
		this.now = {};
		var from = {}, to = {};
		for (var p in obj){
			var parsed = Fx.CSS.prepare(this.element, p, obj[p]);
			from[p] = parsed.from;
			to[p] = parsed.to;
		}
		return this.parent(from, to);
	},

	increase: function(){
		for (var p in this.now) this.element.setStyle(p, Fx.CSS.serve(this.now[p], this.options.unit));
	}

});

/*
Class: Element
	Custom class to allow all of its methods to be used with any DOM element via the dollar function <$>.
*/

Element.extend({

	/*
	Property: effects
		Applies an <Fx.Styles> to the Element; This a shortcut for <Fx.Styles>.

	Example:
		>var myEffects = $(myElement).effects({duration: 1000, transition: Fx.Transitions.Sine.easeInOut});
 		>myEffects.start({'height': [10, 100], 'width': [900, 300]});
	*/

	effects: function(options){
		return new Fx.Styles(this, options);
	}

});/*
Script: Fx.Morph.js
	Contains <Fx.Morph>.

License:
	MIT-style license.
*/

/*
Class: Fx.Morph
	Smoothly Morph the element reflecting the properties of a specified class name defined in anywhere in the CSS.
	Inherits methods, properties, options and events from <Fx.Styles>.

Notes:
	- This is still a wip.
	- It only works with 'transitionable' properties.
	- The className will NOT be added onComplete.
	- This Effect is intended to work only with properties found in external styesheet. For custom properties see <Fx.Styles>

Arguments:
	el - the $(element) to apply the style transition to

Example:
	>var myMorph = new Fx.Morph('myElement', {duration:500});
	>myMorph.start('myClassName');
*/

Fx.Morph = new Class({

	Extends: Fx.Styles,

	/*
	Property: start
		Executes a transition to the current properties of the specified className.

	Arguments:
		obj - an object containing keys that specify css properties to alter and values that specify either the from/to values (as an array)
		or just the end value (an integer).

	Example:
		see <Fx.Styles>
	*/

	start: function(className){
		var to = {};
		Array.each(document.styleSheets, function(sheet, j){
			var rules = sheet.rules || sheet.cssRules;
			Array.each(rules, function(rule, i){
				if (!rule.selectorText.test('\.' + className + '$') || !rule.style) return;
				for (var style in Element.Styles.All){
					if (rule.style[style]){
						var ruleStyle = rule.style[style];
						to[style] = (style.test(/color/i) && ruleStyle.test(/^rgb/)) ? ruleStyle.rgbToHex() : ruleStyle;
					}
				};
			});
		});
		return this.parent(to);
	}

});

Element.extend({

	morph: function(className, options){
		var morph = this.$attributes.morph;
		if (!morph) this.$attributes.morph = new Fx.Morph(this, {wait: false});
		if (options) morph.setOptions(options);
		return morph.start(className);
	}

});/*
Script: Fx.Scroll.js
	Contains <Fx.Scroll>

License:
	MIT-style license.
*/

/*
Class: Fx.Scroll
	Scroll any element with an overflow, including the window element.
	Inherits methods, properties, options and events from <Fx>.

Note:
	Fx.Scroll requires an XHTML doctype.

Arguments:
	element - the element to scroll
	options - optional, see Options below.

Options:
	all the Fx options and events, plus:
	offset - the distance for the scrollTo point/element. an Object with x/y properties.
	overflown - an array of nested scrolling containers, see <Element.getPosition>
*/

Fx.Scroll = new Class({

	Extends: Fx,

	options: {
		overflown: [],
		offset: {'x': 0, 'y': 0},
		wheelStops: true
	},

	initialize: function(element, options){
		this.parent($(element), options);
		this.now = [];
		this.bound = {'stop': this.stop.bind(this, false)};
		if (this.options.wheelStops){
			this.addEvent('onStart', function(){
				document.addEvent('mousewheel', this.bound.stop);
			}.bind(this), true);
			this.addEvent('onComplete', function(){
				document.removeEvent('mousewheel', this.bound.stop);
			}.bind(this), true);
		}
	},

	setNow: function(){
		for (var i = 2; i--;) this.now[i] = this.compute(this.from[i], this.to[i]);
	},

	/*
	Property: scrollTo
		Scrolls the chosen element to the x/y coordinates.

	Arguments:
		x - the x coordinate to scroll the element to
		y - the y coordinate to scroll the element to
	*/

	scrollTo: function(x, y){
		if (this.timer && this.options.wait) return this;
		var el = this.element.getSize();
		var values = {'x': x, 'y': y};
		for (var z in el.size){
			var max = el.scrollSize[z] - el.size[z];
			if ($chk(values[z])) values[z] = ($type(values[z]) == 'number') ? values[z].limit(0, max) : max;
			else values[z] = el.scroll[z];
			values[z] += this.options.offset[z];
		}
		return this.start([el.scroll.x, el.scroll.y], [values.x, values.y]);
	},

	/*
	Property: toTop
		Scrolls the chosen element to its maximum top.
	*/

	toTop: function(){
		return this.scrollTo(false, 0);
	},

	/*
	Property: toBottom
		Scrolls the chosen element to its maximum bottom.
	*/

	toBottom: function(){
		return this.scrollTo(false, 'full');
	},

	/*
	Property: toLeft
		Scrolls the chosen element to its maximum left.
	*/

	toLeft: function(){
		return this.scrollTo(0, false);
	},

	/*
	Property: toRight
		Scrolls the chosen element to its maximum right.
	*/

	toRight: function(){
		return this.scrollTo('full', false);
	},

	/*
	Property: toElement
		Scrolls the specified element to the position the passed in element is found.

	Arguments:
		el - the $(element) to scroll the window to
	*/

	toElement: function(el){
		var parent = this.element.getPosition(this.options.overflown);
		var target = $(el).getPosition(this.options.overflown);
		return this.scrollTo(target.x - parent.x, target.y - parent.y);
	},

	increase: function(){
		this.element.scrollTo(this.now[0], this.now[1]);
	}

});/*
Script: Fx.Slide.js
	Contains <Fx.Slide>

License:
	MIT-style license.
*/

/*
Class: Fx.Slide
	The slide effect; slides an element in horizontally or vertically, the contents will fold inside.
	Inherits methods, properties, options and events from <Fx>.

Note:
	Fx.Slide requires an XHTML doctype.

Options:
	mode - set it to vertical or horizontal. Defaults to vertical.
	options - all the <Fx> options

Properties:
	open - (boolean) true: the slide element is visible.

Example:
	(start code)
	var mySlider = new Fx.Slide('myElement', {duration: 500});
	mySlider.toggle() //toggle the slider up and down.
	mySlider.open //true
	(end)
*/

Fx.Slide = new Class({

	Extends: Fx,

	options: {
		mode: 'vertical'
	},

	initialize: function(element, options){
		this.parent($(element), options);
		this.wrapper = new Element('div', {'styles': $extend(this.element.getStyles('margin'), {'overflow': 'hidden'})}).injectAfter(this.element).adopt(this.element);
		this.element.setStyle('margin', 0);
		this.now = [];
		this.open = true;
		this.addEvent('onComplete', function(){
			this.open = (this.now[0] === 0);
			if (this.open){
				this.wrapper.setStyle(this.layout, '');
				if (Client.Engine.webkit419) this.element.remove().inject(this.wrapper);
			}
		}, true);
	},

	setNow: function(){
		for (var i = 2; i--;) this.now[i] = this.compute(this.from[i], this.to[i]);
	},

	vertical: function(){
		this.margin = 'margin-top';
		this.layout = 'height';
		this.offset = this.element.offsetHeight;
	},

	horizontal: function(){
		this.margin = 'margin-left';
		this.layout = 'width';
		this.offset = this.element.offsetWidth;
	},

	/*
	Property: slideIn
		Slides the elements in view horizontally or vertically.

	Arguments:
		mode - (optional, string) 'horizontal' or 'vertical'; defaults to options.mode.
	*/

	slideIn: function(mode){
		this[mode || this.options.mode]();
		return this.start([this.element.getStyle(this.margin).toInt(), this.wrapper.getStyle(this.layout).toInt()], [0, this.offset]);
	},

	/*
	Property: slideOut
		Sides the elements out of view horizontally or vertically.

	Arguments:
		mode - (optional, string) 'horizontal' or 'vertical'; defaults to options.mode.
	*/

	slideOut: function(mode){
		this[mode || this.options.mode]();
		return this.start([this.element.getStyle(this.margin).toInt(), this.wrapper.getStyle(this.layout).toInt()], [-this.offset, 0]);
	},

	/*
	Property: hide
		Hides the element without a transition.

	Arguments:
		mode - (optional, string) 'horizontal' or 'vertical'; defaults to options.mode.
	*/

	hide: function(mode){
		this[mode || this.options.mode]();
		this.open = false;
		return this.set([-this.offset, 0]);
	},

	/*
	Property: show
		Shows the element without a transition.

	Arguments:
		mode - (optional, string) 'horizontal' or 'vertical'; defaults to options.mode.
	*/

	show: function(mode){
		this[mode || this.options.mode]();
		this.open = true;
		return this.set([0, this.offset]);
	},

	/*
	Property: toggle
		Slides in or Out the element, depending on its state

	Arguments:
		mode - (optional, string) 'horizontal' or 'vertical'; defaults to options.mode.

	*/

	toggle: function(mode){
		if (this.wrapper.offsetHeight == 0 || this.wrapper.offsetWidth == 0) return this.slideIn(mode);
		return this.slideOut(mode);
	},

	increase: function(){
		this.element.setStyle(this.margin, this.now[0] + this.options.unit);
		this.wrapper.setStyle(this.layout, this.now[1] + this.options.unit);
	}

});

Fx.Slide.Accessory = {'slideIn': 'slideIn', 'slideOut': 'slideOut', 'slideToggle': 'toggle', 'slideHide': 'hide', 'slideShow': 'show'};

$each(Fx.Slide.Accessory, function(method, accessory){
	Fx.Slide.Accessory[accessory] = function(options){
		var slide = this.$attributes.slide;
		if (!slide){
			slide = new Fx.Slide(this, {wait: false});
			this.$attributes.slide = slide.wrapper.$attributes.slide = slide;
		}
		if (options) slide.setOptions(options);
		return slide[method]();
	};
});
Element.extend(Fx.Slide.Accessory);/*
Script: Fx.Transitions.js
	Effects transitions, to be used with all the effects.

License:
	MIT-style license.

Credits:
	Easing Equations by Robert Penner, <http://www.robertpenner.com/easing/>, modified & optimized to be used with mootools.
*/

/*
Class: Fx.Transitions
	A collection of tweening transitions for use with the <Fx> classes.

Example:
	>//Elastic.easeOut with default values:
	>new Fx.Style('margin', {transition: Fx.Transitions.Elastic.easeOut});
	>//Elastic.easeOut with user-defined value for elasticity.
	> var myTransition = new Fx.Transition(Fx.Transitions.Elastic, 3);
	>new Fx.Style('margin', {transition: myTransition.easeOut});

See also:
	http://www.robertpenner.com/easing/
*/

Fx.Transition = function(transition, params){
	params = $splat(params) || [];
	return $extend(transition, {
		easeIn: function(pos){
			return transition(pos, params);
		},
		easeOut: function(pos){
			return 1 - transition(1 - pos, params);
		},
		easeInOut: function(pos){
			return (pos <= 0.5) ? transition(2 * pos, params) / 2 : (2 - transition(2 * (1 - pos), params)) / 2;
		}
	});
};

Fx.Transitions = new Abstract({

	/*
	Property: linear
		displays a linear transition.

	Graph:
		(see Linear.png)
	*/

	linear: function(p){
		return p;
	}

});

Fx.Transitions.extend = function(transitions){
	for (var transition in transitions) Fx.Transitions[transition] = new Fx.Transition(transitions[transition]);
};

Fx.Transitions.extend({

	/*
	Property: Quad
		displays a quadratic transition. Must be used as Quad.easeIn or Quad.easeOut or Quad.easeInOut

	Graph:
		(see Quad.png)
	*/

	//auto generated

	/*
	Property: Cubic
		displays a cubicular transition. Must be used as Cubic.easeIn or Cubic.easeOut or Cubic.easeInOut

	Graph:
		(see Cubic.png)
	*/

	//auto generated

	/*
	Property: Quart
		displays a quartetic transition. Must be used as Quart.easeIn or Quart.easeOut or Quart.easeInOut

	Graph:
		(see Quart.png)
	*/

	//auto generated

	/*
	Property: Quint
		displays a quintic transition. Must be used as Quint.easeIn or Quint.easeOut or Quint.easeInOut

	Graph:
		(see Quint.png)
	*/

	//auto generated

	/*
	Property: Pow
		Used to generate Quad, Cubic, Quart and Quint.
		By default is p^6.

	Graph:
		(see Pow.png)
	*/

	Pow: function(p, x){
		return Math.pow(p, x[0] || 6);
	},

	/*
	Property: Expo
		displays a exponential transition. Must be used as Expo.easeIn or Expo.easeOut or Expo.easeInOut

	Graph:
		(see Expo.png)
	*/

	Expo: function(p){
		return Math.pow(2, 8 * (p - 1));
	},

	/*
	Property: Circ
		displays a circular transition. Must be used as Circ.easeIn or Circ.easeOut or Circ.easeInOut

	Graph:
		(see Circ.png)
	*/

	Circ: function(p){
		return 1 - Math.sin(Math.acos(p));
	},


	/*
	Property: Sine
		displays a sineousidal transition. Must be used as Sine.easeIn or Sine.easeOut or Sine.easeInOut

	Graph:
		(see Sine.png)
	*/

	Sine: function(p){
		return 1 - Math.sin((1 - p) * Math.PI / 2);
	},

	/*
	Property: Back
		makes the transition go back, then all forth. Must be used as Back.easeIn or Back.easeOut or Back.easeInOut

	Graph:
		(see Back.png)
	*/

	Back: function(p, x){
		x = x[0] || 1.618;
		return Math.pow(p, 2) * ((x + 1) * p - x);
	},

	/*
	Property: Bounce
		makes the transition bouncy. Must be used as Bounce.easeIn or Bounce.easeOut or Bounce.easeInOut

	Graph:
		(see Bounce.png)
	*/

	Bounce: function(p){
		var value;
		for (var a = 0, b = 1; 1; a += b, b /= 2){
			if (p >= (7 - 4 * a) / 11){
				value = - Math.pow((11 - 6 * a - 11 * p) / 4, 2) + b * b;
				break;
			}
		}
		return value;
	},

	/*
	Property: Elastic
		Elastic curve. Must be used as Elastic.easeIn or Elastic.easeOut or Elastic.easeInOut

	Graph:
		(see Elastic.png)
	*/

	Elastic: function(p, x){
		return Math.pow(2, 10 * --p) * Math.cos(20 * p * Math.PI * (x[0] || 1) / 3);
	}

});

['Quad', 'Cubic', 'Quart', 'Quint'].each(function(transition, i){
	Fx.Transitions[transition] = new Fx.Transition(function(p){
		return Math.pow(p, [i + 2]);
	});
});/*
Script: Drag.js
	Contains <Drag>, <Element.makeResizable>

License:
	MIT-style license.
*/

/*
Class: Drag
	Enables the modification of two CSS properties of an Element based on the position of the mouse while the mouse button is held down.

Note:
	Drag requires an XHTML doctype.

Arguments:
	el - the $(element) to apply the transformations to.
	options - optional. The options object.

Options:
	handle    - (Element) [this.element] The Element to act as the handle for the draggable element.  Defaults to the Element itself.
	modifiers - (object)  [object]   See Modifiers Below.
	limit     - (object)  [false]    See Limit below.
	grid      - (integer) [optional] Distance in px for snap-to-grid dragging
	snap      - (integer) [false]    The distance to drag before the Element starts to respond to the drag.

	Modifiers:
		x - (string) [left] The style to modify when the mouse moves in an horizontal direction.
		y - (string) [top]  The style to modify when the mouse moves in a vertical direction.

	Limit:
		x - (array) [false] Start and end limit relative to the 'x' setting of Modifiers.
		y - (array) [false] Start and end limit relative to the 'y' setting of Modifiers.

Events:
	onStart    - (function) Executed when the user starts to drag (on mousedown).
	onComplete - (function) Executed when the user completes the drag.
	onDrag     - (function) Executed at every step of the drag.
*/

var Drag = new Class({

	Implements: [Events, Options],

	options: {
		/*onStart: $empty,
		onBeforeStart: $empty,
		onComplete: $empty,
		onSnap: $empty,
		onDrag: $empty,*/
		handle: false,
		unit: 'px',
		limit: false,
		modifiers: {x: 'left', y: 'top'},
		grid: false,
		snap: 6
	},

	initialize: function(){
		var params = $A(arguments).associate({'options': 'object', 'element': ['element', 'string']});
		this.element = $(params.element);
		this.setOptions(params.options);
		this.handle = $(this.options.handle) || this.element;
		this.mouse = {'now': {}, 'pos': {}};
		this.value = {'start': {}, 'now': {}};
		this.bound = {
			'start': this.start.bind(this),
			'check': this.check.bind(this),
			'drag': this.drag.bind(this),
			'stop': this.stop.bind(this)
		};
		this.attach();
	},

	/*
	Property: attach
		Attaches the mouse listener to the handle. Its automatically called during initialize.

	Returns:
		This Drag instance
	*/

	attach: function(){
		this.handle.addEvent('mousedown', this.bound.start);
		return this;
	},

	/*
	Property: attach
		Detaches the mouse listener from the handle.

	Returns:
		This Drag instance
	*/

	detach: function(){
		this.handle.removeEvent('mousedown', this.bound.start);
		return this;
	},

	start: function(event){
		this.fireEvent('onBeforeStart', this.element);
		this.mouse.start = event.page;
		var limit = this.options.limit;
		this.limit = {'x': [], 'y': []};
		for (var z in this.options.modifiers){
			if (!this.options.modifiers[z]) continue;
			this.value.now[z] = this.element.getStyle(this.options.modifiers[z]).toInt();
			this.mouse.pos[z] = event.page[z] - this.value.now[z];
			if (limit && limit[z]){
				for (var i = 2; i--;){
					if ($chk(limit[z][i])) this.limit[z][i] = ($type(limit[z][i]) == 'function') ? limit[z][i]() : limit[z][i];
				}
			}
		}
		if ($type(this.options.grid) == 'number') this.options.grid = {'x': this.options.grid, 'y': this.options.grid};
		document.addEvents({
			'mousemove': this.bound.check,
			'mouseup': this.bound.stop
		});
		this.fireEvent('onStart', this.element);
		event.stop();
	},

	check: function(event){
		var distance = Math.round(Math.sqrt(Math.pow(event.page.x - this.mouse.start.x, 2) + Math.pow(event.page.y - this.mouse.start.y, 2)));
		if (distance > this.options.snap){
			document.removeEvent('mousemove', this.bound.check);
			document.addEvent('mousemove', this.bound.drag);
			this.drag(event);
			this.fireEvent('onSnap', this.element);
		}
		event.stop();
	},

	drag: function(event){
		this.out = false;
		this.mouse.now = event.page;
		for (var z in this.options.modifiers){
			if (!this.options.modifiers[z]) continue;
			this.value.now[z] = this.mouse.now[z] - this.mouse.pos[z];
			if (this.limit[z]){
				if ($chk(this.limit[z][1]) && (this.value.now[z] > this.limit[z][1])){
					this.value.now[z] = this.limit[z][1];
					this.out = true;
				} else if ($chk(this.limit[z][0]) && (this.value.now[z] < this.limit[z][0])){
					this.value.now[z] = this.limit[z][0];
					this.out = true;
				}
			}
			if (this.options.grid[z]) this.value.now[z] -= (this.value.now[z] % this.options.grid[z]);
			this.element.setStyle(this.options.modifiers[z], this.value.now[z] + this.options.unit);
		}
		this.fireEvent('onDrag', this.element);
		event.stop();
	},

	stop: function(){
		document.removeEvent('mousemove', this.bound.check);
		document.removeEvent('mousemove', this.bound.drag);
		document.removeEvent('mouseup', this.bound.stop);
		this.fireEvent('onComplete', this.element);
	}

});

/*
Class: Element
	Custom class to allow all of its methods to be used with any DOM element via the dollar function <$>.
*/

Element.extend({

	/*
	Property: makeResizable
		Adds drag-to-resize behaviour to an Element using supplied options.

	Arguments:
		options - (object) See <Drag> for acceptable options.

	Returns:
		The created Drag instance
	*/

	makeResizable: function(options){
		return new Drag(this, $merge({modifiers: {x: 'width', y: 'height'}}, options));
	}

});
/*
Script: Drag.Move.js
	Contains <Drag.Move>, <Element.makeDraggable>

License:
	MIT-style license.
*/

/*
Class: Drag.Move
	Extends <Drag>, has additional functionality for dragging an element, support snapping and droppables.
	Drag.move supports either position absolute or relative. If no position is found, absolute will be set.
	Inherits methods, properties, options and events from <Drag>.

Note:
	Drag.Move requires an XHTML doctype.

Arguments:
	el - (element) The $(element) to apply the drag to.
	options - optional. The options object.

Options:
	all the drag options, plus:
	container - (element) Will fill automatically limiting options based on the $(element) size and position. (defaults to false, no limiting)
	droppables - (elements) Elements you can drop your draggable to.
	overflown - (array) Array of nested scrolling containers, see <Element.getPosition>

Droppables:
	Interaction with droppable work with events fired on the doppable element or, for 'emptydrop', on the dragged element.
	The Events 'over', 'leave' and 'drop' get fired on the droppable element with the dragged element as first argument
	when the dragged element hovers, leaves or get dropped on the droppable.

Example:
	(start code)
	var droppables = $$('li.placements');
	droppables.addEvents({
		'over': function() {
			this.addClass('overed');
		},
		'leave': function() {
			this.removeClass('overed');
		},
		'drop': function(el) {
			alert(el.id + ' dropped');
		}
	});
	new Drag.Move($('product-placement'), {
		'droppables': droppables
	});
	(end)

Demos:
	Drag.Cart - <http://demos.mootools.net/Drag.Cart>
	Drag.Absolutely - <http://demos.mootools.net/Drag.Absolutely>
	DragDrop - <http://demos.mootools.net/DragDrop>

*/

Drag.Move = new Class({

	Extends: Drag,

	options: {
		droppables: [],
		container: false,
		overflown: []
	},

	initialize: function(element, options){
		this.parent(element, options);
		this.droppables = $$(this.options.droppables);
		this.container = $(this.options.container);
		this.positions = ['relative', 'absolute', 'fixed'];
		this.position = {'element': this.element.getStyle('position'), 'container': false};
		if (this.container) this.position.container = this.container.getStyle('position');
		if (!this.positions.contains(this.position.element)) this.position.element = 'absolute';
		var top = this.element.getStyle('top').toInt();
		var left = this.element.getStyle('left').toInt();
		if (this.position.element == 'absolute' && !this.positions.contains(this.position.container)){
			top = $chk(top) ? top : this.element.getTop(this.options.overflown);
			left = $chk(left) ? left : this.element.getLeft(this.options.overflown);
		} else {
			top = $chk(top) ? top : 0;
			left = $chk(left) ? left : 0;
		}
		this.element.setStyles({'top': top, 'left': left, 'position': this.position.element});
	},

	start: function(event){
		if (this.overed){
			this.overed.fireEvent('leave', [this.element, this]);
			this.overed = null;
		}
		if (this.container){
			var cont = this.container.getCoordinates();
			var el = this.element.getCoordinates();
			if (this.position.element == 'absolute' && !this.positions.contains(this.position.container)){
				this.options.limit = {'x': [cont.left, cont.right - el.width], 'y': [cont.top, cont.bottom - el.height]};
			} else {
				this.options.limit = {'y': [0, cont.height - el.height], 'x': [0, cont.width - el.width]};
			}
		}
		this.parent(event);
	},

	drag: function(event){
		this.parent(event);
		if (this.droppables.length) this.checkDroppables();
	},

	checkDroppables: function(){
		var overed = this.out ? false : this.droppables.filter(this.checkAgainst, this).getLast();
		if (this.overed != overed){
			if (this.overed) this.overed.fireEvent('leave', [this.element, this]);
			this.overed = overed ? overed.fireEvent('over', [this.element, this]) : null;
		}
	},

	checkAgainst: function(el){
		el = el.getCoordinates(this.options.overflown);
		var now = this.mouse.now;
		return (now.x > el.left && now.x < el.right && now.y < el.bottom && now.y > el.top);
	},

	stop: function(){
		this.checkDroppables();
		if (this.overed && !this.out) this.overed.fireEvent('drop', [this.element, this]);
		else this.element.fireEvent('emptydrop', this);
		this.parent();
		return this;
	}

});

/*
Class: Element
	Custom class to allow all of its methods to be used with any DOM element via the dollar function <$>.
*/

Element.extend({

	/*
	Property: makeDraggable
		Makes an element draggable with the supplied options.

	Arguments:
		options - see <Drag.Move> and <Drag> for acceptable options.
	*/

	makeDraggable: function(options){
		return new Drag.Move(this, options);
	}

});/*
Script: XHR.js
	Contains the basic XMLHttpRequest Class Wrapper.

License:
	MIT-style license.
*/

/*
Class: XHR
	Basic XMLHttpRequest Wrapper.

Arguments:
	options - an object with options names as keys. See options below.

Options:
	method - 'post' or 'get' - the protocol for the request; optional, defaults to 'post'.
	data - you can write parameters here. Can be a querystring, an object or a Form element.
	async - boolean: asynchronous option; true uses asynchronous requests. Defaults to true.
	encoding - the encoding, defaults to utf-8.
	autoCancel - cancels the already running request if another one is sent. defaults to false.
	headers - accepts an object, that will be set to request headers.
	isSuccess - overrides the in-build isSuccess, that checks the response status code

Events:
	onRequest - function to execute when the XHR request is fired.
	onSuccess - function to execute when the XHR request completes.
	onFailure - function to execute when the request failes (error status code).
	onException - function to execute when setting a request header failes.

Properties:
	running - true if the request is running.
	response - object, text and xml as keys. You can access this property in the onSuccess event.

Example:
	>var myXHR = new XHR({method: 'get'}).send('http://site.com/requestHandler.php', 'name=john&lastname=dorian');
*/

var XHR = new Class({
	
	Implements: [Chain, Events, Options],

	options: {
		/*onRequest: $empty,
		onSuccess: $empty,
		onFailure: $empty,
		onException: $empty,*/
		method: 'post',
		async: true,
		data: null,
		urlEncoded: true,
		encoding: 'utf-8',
		autoCancel: false,
		headers: {},
		isSuccess: null
	},

	setTransport: function(){
		this.transport = (window.XMLHttpRequest) ? new XMLHttpRequest() : (Client.Engine.ie ? new ActiveXObject('Microsoft.XMLHTTP') : false);
	},

	initialize: function(){
		var params = $A(arguments).associate({'url': 'string', 'options': 'object'});
		this.url = params.url;
		this.setTransport();
		this.setOptions(params.options);
		this.options.isSuccess = this.options.isSuccess || this.isSuccess;
		this.headers = $merge(this.options.headers);
		if (this.options.urlEncoded && this.options.method != 'get'){
			var encoding = (this.options.encoding) ? '; charset=' + this.options.encoding : '';
			this.setHeader('Content-type', 'application/x-www-form-urlencoded' + encoding);
		}
		this.setHeader('X-Requested-With', 'XMLHttpRequest');
	},

	onStateChange: function(){
		if (this.transport.readyState != 4 || !this.running) return;
		this.running = false;
		this.status = 0;
		$try(function(){
			this.status = this.transport.status;
		}, this);
		if (this.options.isSuccess.call(this, this.status)) this.onSuccess();
		else this.onFailure();
		this.transport.onreadystatechange = $empty;
	},

	isSuccess: function(){
		return ((this.status >= 200) && (this.status < 300));
	},

	onSuccess: function(){
		this.response = {
			text: this.transport.responseText,
			xml: this.transport.responseXML
		};
		this.fireEvent('onSuccess', [this.response.text, this.response.xml]);
		this.callChain();
	},

	onFailure: function(){
		this.fireEvent('onFailure', this.transport);
	},

	/*
	Property: setHeader
		Add/modify an header for the request. It will not override headers from the options.

	Example:
		>var myXhr = new XHR(url, {method: 'get', headers: {'X-Request': 'JSON'}});
		>myXhr.setHeader('Last-Modified','Sat, 1 Jan 2005 05:00:00 GMT');
	*/

	setHeader: function(name, value){
		this.headers[name] = value;
		return this;
	},

	/*
	Property: getHeader
		Returns the given response header or null
	*/

	getHeader: function(name){
		return $try(function(name){
			return this.getResponseHeader(name);
		}, this.transport, name) || null;
	},

	/*
	Property: send
		Opens the XHR connection and sends the data. Data has to be null or a string.

	Example:
		>var myXhr = new XHR({method: 'post'});
		>myXhr.send(url, querystring);
		>
		>var syncXhr = new XHR({async: false, method: 'post'});
		>syncXhr.send(url, null);
		>
	*/

	send: function(url, data){
		if (this.options.autoCancel) this.cancel();
		else if (this.running) return this;
		this.running = true;
		if (data && this.options.method == 'get'){
			url = url + (url.contains('?') ? '&' : '?') + data;
			data = null;
		}
		this.transport.open(this.options.method.toUpperCase(), url, this.options.async);
		this.transport.onreadystatechange = this.onStateChange.bind(this);
		if ((this.options.method == 'post') && this.transport.overrideMimeType) this.setHeader('Connection', 'close');
		for (var type in this.headers){
			try{
				this.transport.setRequestHeader(type, this.headers[type]);
			} catch(e){
				this.fireEvent('onException', [e, type, this.headers[type]]);
			}
		}
		this.fireEvent('onRequest');
		this.transport.send($pick(data, null));
		if (!this.options.async) this.onStateChange();
		return this;
	},

	request: function(data){
		return this.send(this.url, data || this.options.data);
	},

	/*
	Property: cancel
		Cancels the running request. No effect if the request is not running.

	Example:
		>var myXhr = new XHR({method: 'get'}).send(url);
		>myXhr.cancel();
	*/

	cancel: function(){
		if (!this.running) return this;
		this.running = false;
		this.transport.abort();
		this.transport.onreadystatechange = $empty;
		this.setTransport();
		this.fireEvent('onCancel');
		return this;
	}

});/*
Script: Ajax.js
	Contains the <Ajax> class. Also contains methods to generate querystings from forms and Objects.

Credits:
	Loosely based on the version from prototype.js <http://prototype.conio.net>

License:
	MIT-style license.
*/

/*
Class: Ajax
	An Ajax class, For all your asynchronous needs.
	Inherits methods, properties, options and events from <XHR>.

Arguments:
	url - the url pointing to the server-side script.
	options - optional, an object containing options.

Options:
	update - $(element) to insert the response text of the XHR into, upon completion of the request.
	evalScripts - boolean; default is false. Execute scripts in the response text onComplete. When the response is javascript the whole response is evaluated.
	evalResponse - boolean; default is false. Force global evalulation of the whole response, no matter what content-type it is.

Events:
	onComplete - function to execute when the ajax request completes.

Example:
	>var myAjax = new Ajax(url, {method: 'get'}).request();
*/

var Ajax = new Class({

	Extends: XHR,

	options: {
		/*onComplete: $empty,*/
		update: null,
		evalScripts: false,
		evalResponse: false
	},

	initialize: function(url, options){
		this.parent(url, options);
		this.addEvent('onSuccess', this.onComplete, true);
		if (!['post', 'get'].contains(this.options.method)){
			this._method = '_method=' + this.options.method;
			this.options.method = 'post';
		}
		this.setHeader('Accept', 'text/javascript, text/html, application/xml, text/xml, */*');
	},

	onComplete: function(){
		this.evalScripts();
		if (this.options.update) $(this.options.update).empty().setHTML(this.response.text);
		this.fireEvent('onComplete', [this.response.text, this.response.xml], 20);
	},

	/*
	Property: request
		Executes the ajax request.

	Example:
		>var myAjax = new Ajax(url, {method: 'get'});
		>myAjax.request();

		OR

		>new Ajax(url, {method: 'get'}).request();
	*/

	request: function(data){
		data = data || this.options.data;
		switch ($type(data)){
			case 'element': data = $(data).toQueryString(); break;
			case 'object': data = Object.toQueryString(data);
		}
		if (this._method) data = (data) ? [this._method, data].join('&') : this._method;
		return this.parent(data);
	},

	/*
	Property: evalScripts
		Executes scripts in the response text
	*/

	evalScripts: function(){
		var script, scripts = '', regexp = /<script[^>]*>([\s\S]*?)<\/script>/gi;
		if (this.options.evalResponse || (/(ecma|java)script/).test(this.getHeader('Content-type'))){
			scripts = this.response.text;
		} else if (this.options.evalScripts){
			while ((script = regexp.exec(this.response.text))) scripts += script[1] + '\n';
		}
		this.response.text = this.response.text.replace(regexp, '');
		if (scripts) (window.execScript) ? window.execScript(scripts) : window.setTimeout(scripts, 0);
	}

});

/* Section: Object related Functions */

/*
Function: Object.toQueryString
	Generates a querystring from key/pair values in an object

Arguments:
	source - the object to generate the querystring from.

Returns:
	the query string.

Example:
	>Object.toQueryString({apple: "red", lemon: "yellow"}); //returns "apple=red&lemon=yellow"
*/

Object.toQueryString = function(source){
	var queryString = [];
	for (var property in source) queryString.push(encodeURIComponent(property) + '=' + encodeURIComponent(source[property]));
	return queryString.join('&');
};

/*
Class: Element
	Custom class to allow all of its methods to be used with any DOM element via the dollar function <$>.
*/

Element.extend({

	/*
	Property: send
		Sends a form with an ajax post request

	Arguments:
		options - option collection for ajax request. See <Ajax> for the options list.

	Returns:
		The Ajax Class Instance

	Example:
		(start code)
		<form id="myForm" action="submit.php">
		<input name="email" value="bob@bob.com">
		<input name="zipCode" value="90210">
		</form>
		<script>
		$('myForm').send();
		</script>
		(end)
	*/

	send: function(options){
		return new Ajax(this.getProperty('action'), $merge({method: 'post'}, options)).request(this.toQueryString());
	},

	/*
	Property: update
		Updates the content of the element with an ajax get request

	Arguments:
		url - the url pointing to the server-side document.
		options - option collection for ajax request. See <Ajax> for the options list.

	Returns:
		The Ajax Class Instance

	Example:
		(start code)
		<div id="content">Loading content ...</>
		<script>
		$('content').update('page_1.html');
		</script>
		(end)
	*/

	update: function(url, options){
		var update = this.$attributes.update;
		if (!update) update = this.$attributes.update = new Ajax({update: this, method: 'get', autoCancel: true});
		if (options) update.setOptions(options);
		update.url = url;
		return update.request();
	}

});/*
Script: Cookie.js
	A cookie reader/creator

Credits:
	based on the functions by Peter-Paul Koch (http://quirksmode.org)
*/

/*
Class: Cookie
	Class for creating, getting, and removing cookies.
*/

var Cookie = new Abstract({

	options: {
		domain: false,
		path: false,
		duration: false,
		secure: false
	},

	/*
	Property: set
		Sets a cookie in the browser.

	Arguments:
		key - the key (name) for the cookie
		value - the value to set, cannot contain semicolons
		options - an object representing the Cookie options. See Options below. Default values are stored in Cookie.options.

	Options:
		domain - the domain the Cookie belongs to. If you want to share the cookie with pages located on a different domain, you have to set this value.
			Defaults to the current domain.
		path - the path the Cookie belongs to. If you want to share the cookie with pages located in a different path,
			you have to set this value, for example to "/" to share the cookie with all pages on the domain. Defaults to the current path.
		duration - the duration of the Cookie before it expires, in days.
					If set to false or 0, the cookie will be a session cookie that expires when the browser is closed. This is default.
		secure - Stored cookie information can be accessed only from a secure environment.

	Returns:
		An object with the options, the key and the value. You can give it as first parameter to Cookie.remove.

	Example:
		>Cookie.set('username', 'Harald'); // session cookie (duration is false), or ...
		>Cookie.set('username', 'JackBauer', {duration: 1}); // save this for 1 day

	*/

	set: function(key, value, options){
		options = $merge(this.options, options);
		value = encodeURIComponent(value);
		if (options.domain) value += '; domain=' + options.domain;
		if (options.path) value += '; path=' + options.path;
		if (options.duration){
			var date = new Date();
			date.setTime(date.getTime() + options.duration * 24 * 60 * 60 * 1000);
			value += '; expires=' + date.toGMTString();
		}
		if (options.secure) value += '; secure';
		document.cookie = key + '=' + value;
		return $extend(options, {'key': key, 'value': value});
	},

	/*
	Property: get
		Gets the value of a cookie.

	Arguments:
		key - the name of the cookie you wish to retrieve.

	Returns:
		The cookie string value, or false if not found.

	Example:
		>Cookie.get("username") //returns JackBauer
	*/

	get: function(key){
		var value = document.cookie.match('(?:^|;)\\s*' + key.escapeRegExp() + '=([^;]*)');
		return value ? decodeURIComponent(value[1]) : false;
	},

	/*
	Property: remove
		Removes a cookie from the browser.

	Arguments:
		cookie - the name of the cookie to remove or a previous cookie (for domains)
		options - optional. you can also pass the domain and path here. Same as options in <Cookie.set>

	Examples:
		>Cookie.remove('username') //bye-bye JackBauer, cya in 24 hours
		>
		>var myCookie = Cookie.set('username', 'Aaron', {domain: 'mootools.net'}); // Cookie.set returns an object with all values need to remove the cookie
		>Cookie.remove(myCookie);
	*/

	remove: function(cookie, options){
		if ($type(cookie) == 'object') this.set(cookie.key, '', $merge(cookie, {duration: -1}));
		else this.set(cookie, '', $merge(options, {duration: -1}));
	}

});/*
Script: Json.js
	Simple Json parser and Stringyfier, See: <http://www.json.org/>

License:
	MIT-style license.
*/

/*
Class: Json
	Simple Json parser and Stringyfier, See: <http://www.json.org/>
*/

var Json = {

	/*
	Property: encode
		Converts an object or an array to a string, to be passed in server-side scripts as a parameter.

	Arguments:
		obj - the object to convert to string

	Returns:
		A json string

	Example:
		(start code)
		Json.encode({apple: 'red', lemon: 'yellow'}); '{"apple":"red","lemon":"yellow"}'
		(end)
	*/

	encode: function(obj){
		switch ($type(obj)){
			case 'string':
				return '"' + obj.replace(/[\x00-\x1f\\"]/g, Json.$replaceChars) + '"';
			case 'array':
				return '[' + obj.map(Json.encode).filter($defined).join(',') + ']';
			case 'object':
				var string = [];
				for (var prop in obj){
					var val = Json.encode(obj[prop]);
					if ($defined(val)) string.push(Json.encode(prop) + ':' + val);
				}
				return '{' + string.join(',') + '}';
			case 'number':
			case 'boolean': return String(obj);
			case false: return 'null';
		}
		return null;
	},

	$specialChars: {'\b': '\\b', '\t': '\\t', '\n': '\\n', '\f': '\\f', '\r': '\\r', '"' : '\\"', '\\': '\\\\'},

	$replaceChars: function(chr){
		return Json.$specialChars[chr] || '\\u00' + Math.floor(chr.charCodeAt() / 16).toString(16) + (chr.charCodeAt() % 16).toString(16);
	},

	/*
	Property: decode
		converts a json string to an javascript Object.

	Arguments:
		str - the string to evaluate. if its not a string, it returns null.
		secure - optionally, performs syntax check on json string. Defaults to false.

	Credits:
		Json test regexp is by Douglas Crockford <http://crockford.org>.

	Example:
		>var myObject = Json.decode('{"apple":"red","lemon":"yellow"}');
		>//myObject will become {apple: 'red', lemon: 'yellow'}
	*/

	decode: function(string, secure){
		if ($type(string) != 'string' || !string.length) return null;
		if (secure && !(/^[,:{}\[\]0-9.\-+Eaeflnr-u \n\r\t]*$/).test(string.replace(/\\./g, '@').replace(/"[^"\\\n\r]*"/g, ''))) return null;
		return eval('(' + string + ')');
	}

};/*
Script: Json.Remote.js
	Contains <Json.Remote>.

License:
	MIT-style license.
*/

/*
Class: Json.Remote
	Wrapped XHR with automated sending and receiving of Javascript Objects in Json Format.
	Inherits methods, properties, options and events from <XHR>.

Arguments:
	url - the url you want to send your object to.
	options - optional, an object containing options.

Options:
	varName - string, default to 'json'; Name for the variable that holds the Json data. Set it null to send raw data.
	secure - boolean, optional, default true; secure argument for Json.decode.

Example:
	this code will send user information based on name/last name
	(start code)
	var jSonRequest = new Json.Remote("http://site.com/tellMeAge.php", {onComplete: function(person){
		alert(person.age); //is 25 years
		alert(person.height); //is 170 cm
		alert(person.weight); //is 120 kg
	}}).send({'name': 'John', 'lastName': 'Doe'});
	(end)
*/

Json.Remote = new Class({
	
	Extends: XHR,

	options: {
		varName: 'json',
		secure: true
	},

	initialize: function(url, options){
		this.parent(url, options);
		this.addEvent('onSuccess', this.onComplete, true);
		this.setHeader('Accept', 'application/json');
		this.setHeader('X-Request', 'JSON');
	},

	send: function(obj){
		return this.parent(this.url, ((this.options.varName) ? this.options.varName + '=' : '') + Json.encode(obj));
	},

	onComplete: function(text){
		this.response.json = Json.decode(text, this.options.secure);
		this.fireEvent('onComplete', [this.response.json]);
	}

});/*
Script: Assets.js
	provides dynamic loading for images, css and javascript files.

License:
	MIT-style license.
*/

var Asset = new Abstract({

	/*
	Property: javascript
		Injects a javascript file in the page.

	Arguments:
		source - the path of the javascript file
		properties - some additional attributes you might want to add to the script element

	Example:
		> new Asset.javascript('/scripts/myScript.js', {id: 'myScript'});
	*/

	javascript: function(source, properties){
		properties = $merge({
			'onload': $empty
		}, properties);
		var script = new Element('script', {'src': source}).addEvents({
			'load': properties.onload,
			'readystatechange': function(){
				if (this.readyState == 'complete') this.fireEvent('load');
			}
		});
		delete properties.onload;
		return script.setProperties(properties).inject(document.head);
	},

	/*
	Property: css
		Injects a css file in the page.

	Arguments:
		source - the path of the css file
		properties - some additional attributes you might want to add to the link element

	Example:
		> new Asset.css('/css/myStyle.css', {id: 'myStyle', title: 'myStyle'});
	*/

	css: function(source, properties){
		return new Element('link', $merge({
			'rel': 'stylesheet', 'media': 'screen', 'type': 'text/css', 'href': source
		}, properties)).inject(document.head);
	},

	/*
	Property: image
		Preloads an image and returns the img element. Does not inject it to the page.
		DO NOT use addEvent for load/error/abort on the returned element, give them as
		onload/onerror/onabort in the properties argument.

	Arguments:
		source - the path of the image file
		properties - some additional attributes you might want to add to the img element including onload/onerror/onabout events.

	Example:
		> new Asset.image('/images/myImage.png', {id: 'myImage', title: 'myImage', onload: myFunction});

	Returns:
		the img element. you can inject it anywhere you want with <Element.injectInside>/<Element.injectAfter>/<Element.injectBefore>
	*/

	image: function(source, properties){
		properties = $merge({
			'onload': $empty,
			'onabort': $empty,
			'onerror': $empty
		}, properties);
		var image = new Image();
		var element = $(image) || new Element('img');
		['load', 'abort', 'error'].each(function(name){
			var type = 'on' + name;
			var event = properties[type];
			delete properties[type];
			image[type] = function(){
				if (!image) return;
				if (!element.parentNode){
					element.width = image.width;
					element.height = image.height;
				}
				image = image.onload = image.onabort = image.onerror = null;
				event.call(element);
				element.fireEvent(name, element, 1);
			}
		});
		image.src = element.src = source;
		if (image && image.width) image.onload.delay(1);
		return element.setProperties(properties);
	},

	/*
	Property: images
		Preloads an array of images (as strings) and returns an array of img elements. does not inject them to the page.

	Arguments:
		sources - array, the paths of the image files
		options - object, see below

	Options:
		onComplete - a function to execute when all image files are loaded in the browser's cache
		onProgress - a function to execute when one image file is loaded in the browser's cache

	Example:
		(start code)
		new Asset.images(['/images/myImage.png', '/images/myImage2.gif'], {
			onComplete: function(){
				alert('all images loaded!');
			}
		});
		(end)

	Returns:
		the img elements as $$. you can inject them anywhere you want with <Element.injectInside>/<Element.injectAfter>/<Element.injectBefore>
	*/

	images: function(sources, options){
		options = $merge({
			onComplete: $empty,
			onProgress: $empty
		}, options);
		if (!sources.push) sources = [sources];
		var images = [];
		var counter = 0;
		sources.each(function(source){
			var img = new Asset.image(source, {
				'onload': function(){
					options.onProgress.call(this, counter, sources.indexOf(source));
					counter++;
					if (counter == sources.length) options.onComplete();
				}
			});
			images.push(img);
		});
		return new Elements(images);
	}

});/*
Script: Accordion.js
	Contains <Accordion>

License:
	MIT-style license.
*/

/*
Class: Accordion
	The Accordion class creates a group of elements that are toggled when their handles are clicked. When one elements toggles in, the others toggles back.
	Inherits methods, properties, options and events from <Fx.Elements>.
	
Note:
	The Accordion requires an XHTML doctype.

Arguments:
	togglers - required, a collection of elements, the elements handlers that will be clickable.
	elements - required, a collection of elements the transitions will be applied to.
	options - optional, see options below, and <Fx> options and events.

Options:
	show - integer, the Index of the element to show at start.
	display - integer, the Index of the element to show at start (with a transition). defaults to 0.
	fixedHeight - integer, if you want the elements to have a fixed height. defaults to false.
	fixedWidth - integer, if you want the elements to have a fixed width. defaults to false.
	height - boolean, will add a height transition to the accordion if true. defaults to true.
	opacity - boolean, will add an opacity transition to the accordion if true. defaults to true.
	width - boolean, will add a width transition to the accordion if true. defaults to false, css mastery is required to make this work!
	alwaysHide - boolean, will allow to hide all elements if true, instead of always keeping one element shown. defaults to false.
	
Events:
	onActive - function to execute when an element starts to show
	onBackground - function to execute when an element starts to hide
*/

var Accordion = new Class({
	
	Extends: Fx.Elements,

	options: {
		/*onActive: $empty,
		onBackground: $empty,*/
		display: 0,
		show: false,
		height: true,
		width: false,
		opacity: true,
		fixedHeight: false,
		fixedWidth: false,
		wait: false,
		alwaysHide: false
	},

	initialize: function(){
		var params = $A(arguments).associate({'container': 'element', 'options': 'object', 'togglers': true, 'elements': true});
		this.parent(params.elements, params.options);
		this.togglers = $$(params.togglers);
		this.container = $(params.container);
		this.previous = -1;
		if (this.options.alwaysHide) this.options.wait = true;
		if ($chk(this.options.show)){
			this.options.display = false;
			this.previous = this.options.show;
		}
		if (this.options.start){
			this.options.display = false;
			this.options.show = false;
		}
		this.effects = {};
		if (this.options.opacity) this.effects.opacity = 'fullOpacity';
		if (this.options.width) this.effects.width = this.options.fixedWidth ? 'fullWidth' : 'offsetWidth';
		if (this.options.height) this.effects.height = this.options.fixedHeight ? 'fullHeight' : 'scrollHeight';
		for (var i = 0, l = this.togglers.length; i < l; i++) this.addSection(this.togglers[i], this.elements[i]);
		this.elements.each(function(el, i){
			if (this.options.show === i){
				this.fireEvent('onActive', [this.togglers[i], el]);
			} else {
				for (var fx in this.effects) el.setStyle(fx, 0);
			}
		}, this);
		if ($chk(this.options.display)) this.display(this.options.display);
	},

	/*
	Property: addSection
		Dynamically adds a new section into the accordion at the specified position.

	Arguments:
		toggler - (dom element) the element that toggles the accordion section open.
		element - (dom element) the element that stretches open when the toggler is clicked.
		pos - (integer) the index where these objects are to be inserted within the accordion.
	*/

	addSection: function(toggler, element, pos){
		toggler = $(toggler);
		element = $(element);
		var test = this.togglers.contains(toggler);
		var len = this.togglers.length;
		this.togglers.include(toggler);
		this.elements.include(element);
		if (len && (!test || pos)){
			pos = $pick(pos, len - 1);
			toggler.injectBefore(this.togglers[pos]);
			element.injectAfter(toggler);
		} else if (this.container && !test){
			toggler.inject(this.container);
			element.inject(this.container);
		}
		var idx = this.togglers.indexOf(toggler);
		toggler.addEvent('click', this.display.bind(this, idx));
		if (this.options.height) element.setStyles({'padding-top': 0, 'border-top': 'none', 'padding-bottom': 0, 'border-bottom': 'none'});
		if (this.options.width) element.setStyles({'padding-left': 0, 'border-left': 'none', 'padding-right': 0, 'border-right': 'none'});
		element.fullOpacity = 1;
		if (this.options.fixedWidth) element.fullWidth = this.options.fixedWidth;
		if (this.options.fixedHeight) element.fullHeight = this.options.fixedHeight;
		element.setStyle('overflow', 'hidden');
		if (!test){
			for (var fx in this.effects) element.setStyle(fx, 0);
		}
		return this;
	},

	/*
	Property: display
		Shows a specific section and hides all others. Useful when triggering an accordion from outside.

	Arguments:
		index - integer, the index of the item to show, or the actual element to show.
	*/

	display: function(index){
		index = ($type(index) == 'element') ? this.elements.indexOf(index) : index;
		if ((this.timer && this.options.wait) || (index === this.previous && !this.options.alwaysHide)) return this;
		this.previous = index;
		var obj = {};
		this.elements.each(function(el, i){
			obj[i] = {};
			var hide = (i != index) || (this.options.alwaysHide && (el.offsetHeight > 0));
			this.fireEvent(hide ? 'onBackground' : 'onActive', [this.togglers[i], el]);
			for (var fx in this.effects) obj[i][fx] = hide ? 0 : el[this.effects[fx]];
		}, this);
		return this.start(obj);
	},

	showThisHideOpen: function(index){return this.display(index);}

});

Fx.Accordion = Accordion;/*
Script: Color.js
	Contains the Color class.

License:
	MIT-style license.
*/

/*
Class: Color
	Creates a new Color Object, which is an array with some color specific methods.
Arguments:
	color - the hex, the RGB array or the HSB array of the color to create. For HSB colors, you need to specify the second argument.
	type - a string representing the type of the color to create.
		Needs to be specified if you intend to create the color with HSB values, or an array of HEX values. Can be 'rgb', 'hsb' or 'hex'.

Example:
	(start code)
	var black = new Color('#000');
	var purple = new Color([255,0,255]);
	// mix black with white and purple, each time at 10% of the new color
	var darkpurple = black.mix('#fff', purple, 10);
	$('myDiv').setStyle('background-color', darkpurple);
	(end)
*/

var Color = new Class({

	initialize: function(color, type){
		type = type || (color.push ? 'rgb' : 'hex');
		var rgb, hsb;
		switch (type){
			case 'rgb':
				rgb = color;
				hsb = rgb.rgbToHsb();
				break;
			case 'hsb':
				rgb = color.hsbToRgb();
				hsb = color;
				break;
			default:
				rgb = color.hexToRgb(true);
				hsb = rgb.rgbToHsb();
		}
		rgb.hsb = hsb;
		rgb.hex = rgb.rgbToHex();
		return $extend(rgb, Color.prototype);
	},

	/*
	Property: mix
		Mixes two or more colors with the Color.

	Arguments:
		color - a color to mix. you can use as arguments how many colors as you want to mix with the original one.
		alpha - if you use a number as the last argument, it will be threated as the amount of the color to mix.
	*/

	mix: function(){
		var colors = $A(arguments);
		var alpha = ($type(colors[colors.length - 1]) == 'number') ? colors.pop() : 50;
		var rgb = this.copy();
		colors.each(function(color){
			color = new Color(color);
			for (var i = 0; i < 3; i++) rgb[i] = Math.round((rgb[i] / 100 * (100 - alpha)) + (color[i] / 100 * alpha));
		});
		return new Color(rgb, 'rgb');
	},

	/*
	Property: invert
		Inverts the Color.
	*/

	invert: function(){
		return new Color(this.map(function(value){
			return 255 - value;
		}));
	},

	/*
	Property: setHue
		Modifies the hue of the Color, and returns a new one.

	Arguments:
		value - the hue to set
	*/

	setHue: function(value){
		return new Color([value, this.hsb[1], this.hsb[2]], 'hsb');
	},

	/*
	Property: setSaturation
		Changes the saturation of the Color, and returns a new one.

	Arguments:
		percent - the percentage of the saturation to set
	*/

	setSaturation: function(percent){
		return new Color([this.hsb[0], percent, this.hsb[2]], 'hsb');
	},

	/*
	Property: setBrightness
		Changes the brightness of the Color, and returns a new one.

	Arguments:
		percent - the percentage of the brightness to set
	*/

	setBrightness: function(percent){
		return new Color([this.hsb[0], this.hsb[1], percent], 'hsb');
	}

});

/* Section: Utility Functions */

/*
Function: $RGB
	Shortcut to create a new color, based on red, green, blue values.

Arguments:
	r - (integer) red value (0-255)
	g - (integer) green value (0-255)
	b - (integer) blue value (0-255)

*/

function $RGB(r, g, b){
	return new Color([r, g, b], 'rgb');
};

/*
Function: $HSB
	Shortcut to create a new color, based on hue, saturation, brightness values.

Arguments:
	h - (integer) hue value (0-100)
	s - (integer) saturation value (0-100)
	b - (integer) brightness value (0-100)
*/

function $HSB(h, s, b){
	return new Color([h, s, b], 'hsb');
};

/*
Class: Array
	A collection of the Array Object prototype methods.
	For more information on the JavaScript Array Object see <http://developer.mozilla.org/en/docs/Core_JavaScript_1.5_Reference:Global_Objects:Array>.
*/

Array.extend({

	/*
	Property: rgbToHsb
		Converts a RGB array to an HSB array.

	Returns:
		the HSB array.
	*/

	rgbToHsb: function(){
		var red = this[0], green = this[1], blue = this[2];
		var hue, saturation, brightness;
		var max = Math.max(red, green, blue), min = Math.min(red, green, blue);
		var delta = max - min;
		brightness = max / 255;
		saturation = (max != 0) ? delta / max : 0;
		if (saturation == 0){
			hue = 0;
		} else {
			var rr = (max - red) / delta;
			var gr = (max - green) / delta;
			var br = (max - blue) / delta;
			if (red == max) hue = br - gr;
			else if (green == max) hue = 2 + rr - br;
			else hue = 4 + gr - rr;
			hue /= 6;
			if (hue < 0) hue++;
		}
		return [Math.round(hue * 360), Math.round(saturation * 100), Math.round(brightness * 100)];
	},

	/*
	Property: hsbToRgb
		Converts an HSB array to an RGB array.

	Returns:
		the RGB array.
	*/

	hsbToRgb: function(){
		var br = Math.round(this[2] / 100 * 255);
		if (this[1] == 0){
			return [br, br, br];
		} else {
			var hue = this[0] % 360;
			var f = hue % 60;
			var p = Math.round((this[2] * (100 - this[1])) / 10000 * 255);
			var q = Math.round((this[2] * (6000 - this[1] * f)) / 600000 * 255);
			var t = Math.round((this[2] * (6000 - this[1] * (60 - f))) / 600000 * 255);
			switch (Math.floor(hue / 60)){
				case 0: return [br, t, p];
				case 1: return [q, br, p];
				case 2: return [p, br, t];
				case 3: return [p, q, br];
				case 4: return [t, p, br];
				case 5: return [br, p, q];
			}
		}
		return false;
	}

});/*
Script: Group.js
	For Grouping Classes or Elements Events. The Event added to the Group will fire when all of the events of the items of the group are fired.

License:
	MIT-style license.
*/

/*
Class: Group
	An "Utility" Class.

Arguments:
	any number of Class instances, or arrays containing class instances.

Example:
	(start code)
	xhr1 = new Ajax('data.js', {evalScript: true});
	xhr2 = new Ajax('abstraction.js', {evalScript: true});
	xhr3 = new Ajax('template.js', {evalScript: true});

	var group = new Group(xhr1, xhr2, xhr3);
	group.addEvent('onComplete', function(){
		alert('All Scripts loaded');
	});

	xhr1.request();
	xhr2.request();
	xhr3.request();
	(end)
*/

var Group = new Class({

	initialize: function(){
		this.instances = [];
		$each(arguments, function(argument){
			this.instances = this.instances.concat(argument);
		}, this);
		this.events = {};
		this.checker = {};
	},

	/*
	Property: addEvent
		adds an event to the stack of events of the Class instances.

	Arguments:
		type - string; the event name (e.g. 'onComplete')
		fn - function to execute when all instances fired this event
	*/

	addEvent: function(type, fn){
		this.checker[type] = this.checker[type] || {};
		this.events[type] = this.events[type] || [];
		if (this.events[type].contains(fn)) return false;
		else this.events[type].push(fn);
		this.instances.each(function(instance, i){
			instance.addEvent(type, this.check.bind(this, [type, instance, i]));
		}, this);
		return this;
	},

	check: function(type, instance, i){
		this.checker[type][i] = true;
		var every = this.instances.every(function(current, j){
			return this.checker[type][j] || false;
		}, this);
		if (!every) return;
		this.checker[type] = {};
		this.events[type].each(function(event){
			event.call(this, this.instances, instance);
		}, this);
	}

});/*
Script: Hash.js
	Contains the class Hash.

License:
	MIT-style license.
*/

/*
Class: Hash
	It wraps an object that it uses internally as a map.
	The user must use set(), get(), and remove() to add/change, retrieve and remove values, it must not access the internal object directly.
	Null/undefined values are allowed.

Note:
	Each hash instance has the length property.

Arguments:
	obj - an object to convert into a Hash instance.

Example:
	(start code)
	var hash = new Hash({a: 'hi', b: 'world', c: 'howdy'});
	hash.remove('b'); // b is removed.
	hash.set('c', 'hello');
	hash.get('c'); // returns 'hello'
	hash.length // returns 2 (a and c)
	(end)
*/

var Hash = new Class({

	length: 0,

	initialize: function(object){
		this.obj = object || {};
		this.setLength();
	},

	/*
	Property: get
		Retrieves a value from the hash.

	Arguments:
		key - The key

	Returns:
		The value
	*/

	get: function(key){
		return (this.hasKey(key)) ? this.obj[key] : null;
	},

	/*
	Property: hasKey
		Check the presence of a specified key-value pair in the hash.

	Arguments:
		key - The key

	Returns:
		True if the Hash contains a value for the specified key, otherwise false
	*/

	hasKey: function(key){
		return (key in this.obj);
	},

	/*
	Property: set
		Adds a key-value pair to the hash or replaces a previous value associated with the key.

	Arguments:
		key - The key
		value - The value
	*/

	set: function(key, value){
		if (!this.hasKey(key)) this.length++;
		this.obj[key] = value;
		return this;
	},

	setLength: function(){
		this.length = 0;
		for (var p in this.obj) this.length++;
		return this;
	},

	/*
	Property: remove
		Removes a key-value pair from the hash.

	Arguments:
		key - The key
	*/

	remove: function(key){
		if (this.hasKey(key)){
			delete this.obj[key];
			this.length--;
		}
		return this;
	},

	/*
	Property: each
		Calls a function for each key-value pair. The first argument passed to the function will be the value, the second one will be the key, like $each.

	Arguments:
		fn - The function to call for each key-value pair
		bind - Optional, the object that will be referred to as "this" in the function
	*/

	each: function(fn, bind){
		$each(this.obj, fn, bind);
	},

	/*
	Property: extend
		Extends the current hash with an object containing key-value pairs. Values for duplicate keys will be replaced by the new ones.

	Arguments:
		obj - An object containing key-value pairs
	*/

	extend: function(obj){
		$extend(this.obj, obj);
		return this.setLength();
	},

	/*
	Property: merge
		Merges the current hash with multiple objects.
	*/

	merge: function(){
		this.obj = $merge.apply(null, [this.obj].extend(arguments));
		return this.setLength();
	},

	/*
	Property: empty
		Empties all hash values properties and values.
	*/

	empty: function(){
		this.obj = {};
		this.length = 0;
		return this;
	},

	/*
	Property: getKeys
		Returns an array containing all the keys, in the same order as the values returned by <Hash.getValues>.

	Returns:
		An array containing all the keys of the hash
	*/

	getKeys: function(){
		var keys = [];
		for (var property in this.obj) keys.push(property);
		return keys;
	},

	/*
	Property: getValues
		Returns an array containing all the values, in the same order as the keys returned by <Hash.getKeys>.

	Returns:
		An array containing all the values of the hash
	*/

	getValues: function(){
		var values = [];
		for (var property in this.obj) values.push(this.obj[property]);
		return values;
	}

});

/* Section: Utility Functions */

/*
Function: $H
	Shortcut to create a Hash from an Object.
*/

function $H(obj){
	return new Hash(obj);
};/*
Script: Hash.Cookie.js
	Stores and loads an Hash as a cookie using Json format.
*/

/*
Class: Hash.Cookie
	Inherits all the methods from <Hash>, additional methods are save and load.
	Hash json string has a limit of 4kb (4096byte), so be careful with your Hash size.
	Creating a new instance automatically loads the data from the Cookie into the Hash.
	If the Hash is emptied, the cookie is also removed.

Arguments:
	name - the key (name) for the cookie
	options - options are identical to <Cookie> and are simply passed along to it.
		In addition, it has the autoSave option, to save the cookie at every operation. defaults to true.

Example:
	(start code)
	var fruits = new Hash.Cookie('myCookieName', {duration: 3600});
	fruits.extend({
		'lemon': 'yellow',
		'apple': 'red'
	});
	fruits.set('melon', 'green');
	fruits.get('lemon'); // yellow

	// ... on another page ... values load automatically

	var fruits = new Hash.Cookie('myCookieName', {duration: 365});
	fruits.get('melon'); // green

	fruits.erase(); // delete cookie
	(end)
*/

Hash.Cookie = new Class({
	
	Extends: Hash,

	initialize: function(name, options){
		this.name = name;
		this.options = $extend({'autoSave': true}, options || {});
		this.load();
	},

	/*
	Property: save
		Saves the Hash to the cookie. If the hash is empty, removes the cookie.

	Returns:
		Returns false when the JSON string cookie is too long (4kb), otherwise true.

	Example:
		(start code)
		var login = new Hash.Cookie('userstatus', {autoSave: false});

		login.extend({
			'username': 'John',
			'credentials': [4, 7, 9]
		});
		login.set('last_message', 'User logged in!');

		login.save(); // finally save the Hash
		(end)
	*/

	save: function(){
		if (this.length == 0){
			Cookie.remove(this.name, this.options);
			return true;
		}
		var str = Json.encode(this.obj);
		if (str.length > 4096) return false; //cookie would be truncated!
		Cookie.set(this.name, str, this.options);
		return true;
	},

	/*
	Property: load
		Loads the cookie and assigns it to the Hash.
	*/

	load: function(){
		this.obj = Json.decode(Cookie.get(this.name), true) || {};
		this.setLength();
	}

});

Hash.Cookie.Methods = {};
['extend', 'set', 'merge', 'empty', 'remove'].each(function(method){
	Hash.Cookie.Methods[method] = function(){
		Hash.prototype[method].apply(this, arguments);
		if (this.options.autoSave) this.save();
		return this;
	};
});

Hash.Cookie.implement(Hash.Cookie.Methods);/*
Script: Scroller.js
	Contains the <Scroller>.

License:
	MIT-style license.
*/

/*
Class: Scroller
	The Scroller is a class to scroll any element with an overflow (including the window) when the mouse cursor reaches certain buondaries of that element.
	You must call its start method to start listening to mouse movements.

Note:
	The Scroller requires an XHTML doctype.

Arguments:
	element - required, the element to scroll.
	options - optional, see options below, and <Fx> options.

Options:
	area - integer, the necessary boundaries to make the element scroll.
	velocity - integer, velocity ratio, the modifier for the window scrolling speed.

Events:
	onChange - optionally, when the mouse reaches some boundaries, you can choose to alter some other values, instead of the scrolling offsets.
		Automatically passes as parameters x and y values.
*/

var Scroller = new Class({
	
	Implements: [Events, Options],

	options: {
		area: 20,
		velocity: 1,
		onChange: function(x, y){
			this.element.scrollTo(x, y);
		}
	},

	initialize: function(element, options){
		this.setOptions(options);
		this.element = $(element);
		this.mousemover = ([window, document].contains(element)) ? $(document.body) : this.element;
		this.timer = null;
	},

	/*
	Property: start
		The scroller starts listening to mouse movements.
	*/

	start: function(){
		this.coord = this.getCoords.bind(this);
		this.mousemover.addEvent('mousemove', this.coord);
	},

	/*
	Property: stop
		The scroller stops listening to mouse movements.
	*/

	stop: function(){
		this.mousemover.removeEvent('mousemove', this.coord);
		this.timer = $clear(this.timer);
	},

	getCoords: function(event){
		this.page = (this.element == window) ? event.client : event.page;
		if (!this.timer) this.timer = this.scroll.periodical(50, this);
	},

	scroll: function(){
		var el = this.element.getSize();
		var pos = this.element.getPosition();

		var change = {'x': 0, 'y': 0};
		for (var z in this.page){
			if (this.page[z] < (this.options.area + pos[z]) && el.scroll[z] != 0)
				change[z] = (this.page[z] - this.options.area - pos[z]) * this.options.velocity;
			else if (this.page[z] + this.options.area > (el.size[z] + pos[z]) && el.scroll[z] + el.size[z] != el.scrollSize[z])
				change[z] = (this.page[z] - el.size[z] + this.options.area - pos[z]) * this.options.velocity;
		}
		if (change.y || change.x) this.fireEvent('onChange', [el.scroll.x + change.x, el.scroll.y + change.y]);
	}

});/*
Script: Slider.js
	Contains <Slider>

License:
	MIT-style license.
*/

/*
Class: Slider
	Creates a slider with two elements: a knob and a container. Returns the values.
	
Note:
	The Slider requires an XHTML doctype.

Arguments:
	element - the knob container
	knob - the handle
	options - see Options below

Options:
	steps - the number of steps for your slider.
	mode - either 'horizontal' or 'vertical'. defaults to horizontal.
	offset - relative offset for knob position. default to 0.
	
Events:
	onChange - a function to fire when the value changes.
	onComplete - a function to fire when you're done dragging.
	onTick - optionally, you can alter the onTick behavior, for example displaying an effect of the knob moving to the desired position.
		Passes as parameter the new position.
*/

var Slider = new Class({
	
	Implements: [Events, Options],

	options: {
		/*onChange: $empty,
		onComplete: $empty,*/
		onTick: function(pos){
			this.knob.setStyle(this.p, pos);
		},
		mode: 'horizontal',
		steps: 100,
		offset: 0
	},

	initialize: function(el, knob, options){
		this.element = $(el);
		this.knob = $(knob);
		this.setOptions(options);
		this.previousChange = -1;
		this.previousEnd = -1;
		this.step = -1;
		this.element.addEvent('mousedown', this.clickedElement.bind(this));
		var mod, offset;
		switch (this.options.mode){
			case 'horizontal':
				this.z = 'x';
				this.p = 'left';
				mod = {'x': 'left', 'y': false};
				offset = 'offsetWidth';
				break;
			case 'vertical':
				this.z = 'y';
				this.p = 'top';
				mod = {'x': false, 'y': 'top'};
				offset = 'offsetHeight';
		}
		this.max = this.element[offset] - this.knob[offset] + (this.options.offset * 2);
		this.half = this.knob[offset] / 2;
		this.getPos = this.element['get' + this.p.capitalize()].bind(this.element);
		this.knob.setStyle('position', 'relative').setStyle(this.p, - this.options.offset);
		var lim = {};
		lim[this.z] = [- this.options.offset, this.max - this.options.offset];
		this.drag = new Drag(this.knob, {
			limit: lim,
			modifiers: mod,
			snap: 0,
			onStart: function(){
				this.draggedKnob();
			}.bind(this),
			onDrag: function(){
				this.draggedKnob();
			}.bind(this),
			onComplete: function(){
				this.draggedKnob();
				this.end();
			}.bind(this)
		});
	},

	/*
	Property: set
		The slider will get the step you pass.

	Arguments:
		step - one integer
	*/

	set: function(step){
		this.step = step.limit(0, this.options.steps);
		this.checkStep();
		this.end();
		this.fireEvent('onTick', this.toPosition(this.step));
		return this;
	},

	clickedElement: function(event){
		var position = event.page[this.z] - this.getPos() - this.half;
		position = position.limit(-this.options.offset, this.max -this.options.offset);
		this.step = this.toStep(position);
		this.checkStep();
		this.end();
		this.fireEvent('onTick', position);
	},

	draggedKnob: function(){
		this.step = this.toStep(this.drag.value.now[this.z]);
		this.checkStep();
	},

	checkStep: function(){
		if (this.previousChange != this.step){
			this.previousChange = this.step;
			this.fireEvent('onChange', this.step);
		}
	},

	end: function(){
		if (this.previousEnd !== this.step){
			this.previousEnd = this.step;
			this.fireEvent('onComplete', this.step + '');
		}
	},

	toStep: function(position){
		return Math.round((position + this.options.offset) / this.max * this.options.steps);
	},

	toPosition: function(step){
		return this.max * step / this.options.steps;
	}

});/*
Script: SmoothScroll.js
	Contains <SmoothScroll>

License:
	MIT-style license.
*/

/*
Class: SmoothScroll
	Auto targets all the anchors in a page and display a smooth scrolling effect upon clicking them.
	Inherits methods, properties, options and events from <Fx.Scroll>.

Note:
	SmoothScroll requires an XHTML doctype.

Arguments:
	options - the Fx.Scroll options (see: <Fx.Scroll>) plus links, a collection of elements you want your smoothscroll on. Defaults to document.links.

Example:
	>new SmoothScroll();
*/

var SmoothScroll = new Class({
	
	Extends: Fx.Scroll,

	initialize: function(options){
		this.parent(window, options);
		this.links = (this.options.links) ? $$(this.options.links) : $$(document.links);
		var location = window.location.href.match(/^[^#]*/)[0] + '#';
		this.links.each(function(link){
			if (link.href.indexOf(location) != 0) return;
			var anchor = link.href.substr(location.length);
			if (anchor && $(anchor)) this.useLink(link, anchor);
		}, this);
		if (!Client.Engine.webkit419) this.addEvent('onComplete', function(){
			window.location.hash = this.anchor;
		}, true);
	},

	useLink: function(link, anchor){
		link.addEvent('click', function(event){
			this.anchor = anchor;
			this.toElement(anchor);
			event.stop();
		}.bind(this));
	}

});/*
Script: Sortables.js
	Contains <Sortables> Class.

License:
	MIT-style license.
*/

/*
Class: Sortables
	Creates an interface for drag and drop sorting of a list or lists.

Arguments:
	list - required, the list or lists that will become sortable.
		This argument can be an Element, or array of Elements. When a single list (or id) is passed, that list will be sortable only with itself.
		To enable sorting between lists, one or more lists or id's must be passed using an array or an object. See Examples below.
	options - an Object, see options and events below.

Options:
	constrain - whether or not to constrain the element being dragged to its parent element. defaults to false.
	clone - whether or not to display a copy of the actual element while dragging. defaults to true with opacity of 0.7, you can refine styles using an object.
	opacity - opacity of the element being dragged for sorting
	handle - a selector which be used to select the element inside each item to be used as a handle for sorting that item.  if no match is found, the element is used as its own handle.
	revert - whether or not to use an effect to slide the element into its final location after sorting. If you pass an object it will be treated as true and used as aditional options for the revert effect. defaults to false.

Events:
	onStart - function executed when the item starts dragging
	onComplete - function executed when the item ends dragging

Example:
	(start code)
	var mySortables = new Sortables('list-1', {
		revert: { duration: 500, transition: Fx.Transitions.Elastic.easeOut }
	});
	//creates a new Sortable instance over the list with id 'list-1' with some extra options for the revert effect

	var mySortables = new Sortables(['list-1', 'list-2'], {
		constrain: true,
		clone: false,
		revert: true
	});
	//creates a new Sortable instance allowing the sorting of the lists with id's 'list-1' and 'list-2' with extra options
	//since constrain was set to false, the items will not be able to be dragged from one list to the other

	var mySortables = new Sortables(['list-1', 'list-2', 'list-3']);
	//creates a new Sortable instance allowing sorting between the lists with id's 'list-1', 'list-2, and 'list-3'
	(end)
*/

var Sortables = new Class({

	Implements: [Events, Options],

	options: {
		constrain : false,
		clone: true,
		opacity: 0.7,
		handle: false,
		revert: false,
		onStart: $empty,
		onComplete: $empty
	},

	initialize: function(lists, options){
		this.setOptions(options);
		this.idle = true;
		this.hovering = false;
		this.newInsert = false;
		this.bound = {
			start: [],
			end: this.end.bind(this),
			move: this.move.bind(this),
			reset: this.reset.bind(this)
		};
		if (this.options.revert){
			var revertOptions = $merge({duration: 250, wait: false}, this.options.revert);
			this.effect = new Fx.Styles(this.element, revertOptions).addEvent('onComplete', this.bound.reset, true);
		}
		this.cloneContents = !!(this.options.clone);

		this.lists = $$($(lists) || lists);

		this.reinitialize();
		if (this.options.initialize) this.options.initialize.call(this);
	},

	/*
	Property: reinitialize
		Allows the sortables instance to be reinitialized after making modifications to the DOM such as adding or removing elements from any of the lists.
	*/

	reinitialize: function(){
		if (this.handles) this.detach();

		this.handles = [];
		var elements = [];

		this.lists.each(function(list){
			elements.extend(list.getChildren());
		});

		this.handles = !this.options.handle ? elements : elements.map(function(element){
			return element.getElement(this.options.handle) || element;
		}.bind(this));

		this.handles.each(function(handle, i){
			this.bound.start[i] = this.start.bind(this, elements[i], true);
		}, this);

		this.attach();
	},

	/*
	Property: attach
		Attaches the mousedown event to all the handles, enabling sorting.
	*/

	attach: function(){
		this.handles.each(function(handle, i){
			handle.addEvent('mousedown', this.bound.start[i]);
		}, this);
	},

	/*
	Property: detach
		Detaches the mousedown event from the handles, disabling sorting.
	*/

	detach: function(){
		this.handles.each(function(handle, i){
			handle.removeEvent('mousedown', this.bound.start[i]);
		}, this);
	},

	check: function(element, list){
		element = element.getCoordinates();
		var coords = list ? element : {
			left: element.left - this.list.scrollLeft,
			right: element.right - this.list.scrollLeft,
			top: element.top - this.list.scrollTop,
			bottom: element.bottom - this.list.scrollTop
		};
		return (this.curr.x > coords.left && this.curr.x < coords.right && this.curr.y > coords.top && this.curr.y < coords.bottom);
	},

	where: function(element){
		if (this.newInsert){
			this.newInsert = false;
			return 'before';
		}
		var dif = {'x': this.curr.x - this.prev.x, 'y': this.curr.y - this.prev.y};
		return dif[['y', 'x'][(Math.abs(dif.x) >= Math.abs(dif.y)) + 0]] <= 0 ? 'before' : 'after';
	},

	reposition: function(){
		if (this.list.positioned){
			this.position.y -= this.offset.list.y - this.list.scrollTop;
			this.position.x -= this.offset.list.x - this.list.scrollLeft;
		} else if (Client.Engine.opera){
			this.position.y += this.list.scrollTop;
			this.position.x += this.list.scrollLeft;
		}
	},

	start: function(event, element){
		if (!this.idle) return;

		this.idle = false;
		this.prev = {'x': event.page.x, 'y': event.page.y};

		this.styles = element.getStyles('margin-top', 'margin-left', 'padding-top', 'padding-left', 'border-top-width', 'border-left-width', 'opacity');
		this.margin = {
			'top': this.styles['margin-top'].toInt() + this.styles['border-top-width'].toInt(),
			'left': this.styles['margin-left'].toInt() + this.styles['border-left-width'].toInt()
		};

		this.element = element;
		this.list = this.element.getParent();
		this.list.hovering = this.hovering = true;
		this.list.positioned = this.list.getStyle('position').test(/relative|absolute|fixed/);

		var children = this.list.getChildren();
		var bounds = children.shift().getCoordinates();
		children.each(function(element){
			var coords = element.getCoordinates();
			bounds.left = Math.min(coords.left, bounds.left);
			bounds.right = Math.max(coords.right, bounds.right);
			bounds.top = Math.min(coords.top, bounds.top);
			bounds.bottom = Math.max(coords.bottom, bounds.bottom);
		});
		this.bounds = bounds;

		this.position = this.element.getPosition([this.list]);

		this.offset = {
			'list': this.list.getPosition(),
			'element': {'x': event.page.x - this.position.x, 'y': event.page.y - this.position.y}
		};
		this.reposition();

		var clone = this.options.clone;
		switch ($type(clone)){
			case 'function': this.clone = clone.call(this, this.element); break;
			case 'boolean': clone = (clone) ? {'opacity': 0.7} : {'visibility': 'hidden'};
			case 'object': this.clone = this.element.clone(this.cloneContents).setStyles(clone);
		}

		this.clone.injectBefore(this.element.setStyles({
			'position': 'absolute',
			'top': this.position.y - this.margin.top,
			'left': this.position.x - this.margin.left,
			'opacity': this.options.opacity
		}));

		document.addEvent('mousemove', this.bound.move);
		document.addEvent('mouseup', this.bound.end);
		this.fireEvent('onStart', this.element);
		event.stop();
	},

	move: function(event){
		this.curr = {'x': event.page.x, 'y': event.page.y};
		this.position = {'x': this.curr.x - this.offset.element.x, 'y': this.curr.y - this.offset.element.y};

		if (this.options.constrain) {
			this.position.y = this.position.y.limit(this.bounds.top, this.bounds.bottom - this.element.offsetHeight);
			this.position.x = this.position.x.limit(this.bounds.left, this.bounds.right - this.element.offsetWidth);
		}
		this.reposition();
		this.element.setStyles({
			'top' : this.position.y - this.margin.top,
			'left' : this.position.x - this.margin.left
		});

		if (!this.options.constrain){
			var oldSize, newSize;
			this.lists.each(function(list){
				if (!this.check(list, true)){
					list.hovering = false;
				} else if (!list.hovering){
					this.list = list;
					this.list.hovering = this.newInsert = true;
					this.list.positioned = this.list.getStyle('position').test(/relative|absolute|fixed/);
					oldSize = this.clone.getSize().size;
					this.list.adopt(this.clone, this.element);
					newSize = this.clone.getSize().size;
					this.offset = {
						'list': this.list.getPosition(),
						'element': {
							'x': Math.round(newSize.x * (this.offset.element.x / oldSize.x)),
							'y': Math.round(newSize.y * (this.offset.element.y / oldSize.y))
						}
					};
				}
			}, this);
		}

		if (this.list.hovering){
			this.list.getChildren().each(function(element){
				if (!this.check(element)){
					element.hovering = false;
				} else if (!element.hovering && element != this.clone){
					element.hovering = true;
					this.clone.inject(element, this.where(element));
				}
			}, this);
		}

		this.prev = this.curr;
		event.stop();
	},

	end: function(){
		this.prev = null;
		document.removeEvent('mousemove', this.bound.move);
		document.removeEvent('mouseup', this.bound.end);

		this.position = this.clone.getPosition([this.list]);
		this.reposition();

		if (!this.effect){
			this.reset();
		} else {
			this.effect.element = this.element;
			this.effect.start({
				'top' : this.position.y - this.margin.top,
				'left' : this.position.x - this.margin.left,
				'opacity' : this.styles.opacity
			});
		}
	},

	reset: function(){
		this.element.setStyles({
			'position': 'static',
			'opacity': this.styles.opacity
		}).injectBefore(this.clone);
		this.clone.empty().remove();

		this.fireEvent('onComplete', this.element);
		this.idle = true;
	},

	/*
	Property: serialize
		Function to get the order of the elements in the lists of this sortables instance.
		For each list, an array containing the order of the elements will be returned.
		If more than one list is being used, all lists will be serialized and returned in an array.

	Arguments:
		index - int or false; index of the list to serialize. Omit or pass false to serialize all lists.
		modifier - function to override the default output of the sortables.  See Examples below

	Examples:
		(start code)
		mySortables.serialize(1);
		//returns the second list serialized (remember, arrays are 0 based...);
		//['item_1-1', 'item_1-2', 'item_1-3']

		mySortables.serialize();
		//returns a nested array of all lists serialized, or if only one list exists, that lists order
		//[['item_1-1', 'item_1-2', 'item_1-3'], ['item_2-1', 'item_2-2', 'item_2-3'], ['item_3-1', 'item_3-2', 'item_3-3']]

		mySortables.serialize(2, function(element, index){
			return element.getProperty('id').replace('item_','') + '=' + index;
		}).join('&');
		//joins the array with a '&' to return a string of the formatted ids of all the elmements in list 3 with their position
		//'3-0=0&3-1=1&3-2=2'
		(end)
	*/

	serialize: function(index, modifier){
		var map = modifier || function(element, index){
			return element.getProperty('id');
		}.bind(this);

		var serial = this.lists.map(function(list){
			return list.getChildren().map(map, this);
		}, this);

		if (this.lists.length == 1) index = 0;
		return $chk(index) && index >= 0 && index < this.lists.length ? serial[index] : serial;
	}

});/*
Script: Tips.js
	Tooltips, BubbleTips, whatever they are, they will appear on mouseover

License:
	MIT-style license.

Credits:
	The idea behind Tips.js is based on Bubble Tooltips (<http://web-graphics.com/mtarchive/001717.php>) by Alessandro Fulcitiniti <http://web-graphics.com>
*/

/*
Class: Tips
	Display a tip on any element with a title and/or href.

Note:
	Tips requires an XHTML doctype.

Arguments:
	elements - a collection of elements to apply the tooltips to on mouseover.
	options - an object. See options Below.

Options:
	maxTitleChars - the maximum number of characters to display in the title of the tip. defaults to 30.
	showDelay - the delay the onShow method is called. (defaults to 100 ms)
	hideDelay - the delay the onHide method is called. (defaults to 100 ms)

	className - the prefix for your tooltip classNames. defaults to 'tool'.

		the whole tooltip will have as classname: tool-tip

		the title will have as classname: tool-title

		the text will have as classname: tool-text

	offsets - the distance of your tooltip from the mouse. an Object with x/y properties.
	fixed - if set to true, the toolTip will not follow the mouse.
	
Events:
	onShow - optionally you can alter the default onShow behaviour with this option (like displaying a fade in effect);
	onHide - optionally you can alter the default onHide behaviour with this option (like displaying a fade out effect);

Example:
	(start code)
	<img src="/images/i.png" title="The body of the tooltip is stored in the title" class="toolTipImg"/>
	<script>
		var myTips = new Tips($$('.toolTipImg'), {
			maxTitleChars: 50	//I like my captions a little long
		});
	</script>
	(end)

Note:
	The title of the element will always be used as the tooltip body. If you put :: on your title, the text before :: will become the tooltip title.
*/

var Tips = new Class({
	
	Implements: [Events, Options],

	options: {
		onShow: function(tip){
			tip.setStyle('visibility', 'visible');
		},
		onHide: function(tip){
			tip.setStyle('visibility', 'hidden');
		},
		maxTitleChars: 30,
		showDelay: 100,
		hideDelay: 100,
		className: 'tool',
		offsets: {'x': 16, 'y': 16},
		fixed: false
	},

	initialize: function(elements, options){
		this.setOptions(options);
		this.toolTip = new Element('div', {
			'class': this.options.className + '-tip',
			'styles': {
				'position': 'absolute',
				'top': '0',
				'left': '0',
				'visibility': 'hidden'
			}
		}).inject(document.body);
		this.wrapper = new Element('div').inject(this.toolTip);
		$$(elements).each(this.build, this);
	},

	build: function(el){
		el.$attributes.myTitle = (el.href && el.getTag() == 'a') ? el.href.replace('http://', '') : (el.rel || false);
		if (el.title){
			var dual = el.title.split('::');
			if (dual.length > 1){
				el.$attributes.myTitle = dual[0].trim();
				el.$attributes.myText = dual[1].trim();
			} else {
				el.$attributes.myText = el.title;
			}
			el.removeProperty('title');
		} else {
			el.$attributes.myText = false;
		}
		if (el.$attributes.myTitle && el.$attributes.myTitle.length > this.options.maxTitleChars)
			el.$attributes.myTitle = el.$attributes.myTitle.substr(0, this.options.maxTitleChars - 1) + "&hellip;";
		el.addEvent('mouseenter', function(event){
			this.start(el);
			if (!this.options.fixed) this.locate(event);
			else this.position(el);
		}.bind(this));
		if (!this.options.fixed) el.addEvent('mousemove', this.locate.bind(this));
		var end = this.end.bind(this);
		el.addEvent('mouseleave', end);
		el.addEvent('trash', end);
	},

	start: function(el){
		this.wrapper.empty();
		if (el.$attributes.myTitle){
			this.title = new Element('span').inject(
				new Element('div', {'class': this.options.className + '-title'}
			).inject(this.wrapper)).setHTML(el.$attributes.myTitle);
		}
		if (el.$attributes.myText){
			this.text = new Element('span').inject(
				new Element('div', {'class': this.options.className + '-text'}
			).inject(this.wrapper)).setHTML(el.$attributes.myText);
		}
		$clear(this.timer);
		this.timer = this.show.delay(this.options.showDelay, this);
	},

	end: function(event){
		$clear(this.timer);
		this.timer = this.hide.delay(this.options.hideDelay, this);
	},

	position: function(element){
		var pos = element.getPosition();
		this.toolTip.setStyles({
			'left': pos.x + this.options.offsets.x,
			'top': pos.y + this.options.offsets.y
		});
	},

	locate: function(event){
		var win = {'x': Client.getWidth(), 'y': Client.getHeight()};
		var scroll = {'x': Client.getScrollLeft(), 'y': Client.getScrollTop()};
		var tip = {'x': this.toolTip.offsetWidth, 'y': this.toolTip.offsetHeight};
		var prop = {'x': 'left', 'y': 'top'};
		for (var z in prop){
			var pos = event.page[z] + this.options.offsets[z];
			if ((pos + tip[z] - scroll[z]) > win[z]) pos = event.page[z] - this.options.offsets[z] - tip[z];
			this.toolTip.setStyle(prop[z], pos);
		};
	},

	show: function(){
		if (this.options.timeout) this.timer = this.hide.delay(this.options.timeout, this);
		this.fireEvent('onShow', [this.toolTip]);
	},

	hide: function(){
		this.fireEvent('onHide', [this.toolTip]);
	}

});/*
Script: Swiff.js
	Contains <Swiff>, <Swiff.getVersion>, <Swiff.remote>

Credits:
	Flash detection 'borrowed' from SWFObject.

License:
	MIT-style license.
*/

/*
Class: Swiff
	Creates a flash object with supplied parameters.

Arguments:
	movie - (string) The path to the swf movie.
	options - (object) an object with options names as keys. See options below.

Options:
	width - (number) the width of the flash object. defaults to 1.
	height - number) the height of the flash object. defaults to 1.
	id - (string) the id of the flash object. defaults to 'SwiffX' (X is the Swiff UID).
	inject - (element) the target container for the swf object
	params - (object) object params (wmode, bgcolor, allowScriptAccess, loop, etc.), default: allowScriptAccess to sameDomain.
	properties - (object) additional attributes for the object element.
	vars - (object) given to the swf as querystring in flashVars.
	callBacks - (object) Functions you want to pass to your flash movie.

Returns:
	The object element.
	Important: the $ function on the OBJECT element wont extend it, will just target the movie by its id/reference.
	So its not possible to use the <Element> methods on it.

Example:
	(start code)
	var obj = Swiff('myMovie.swf', {
		inject: $('myElement')
		width: 500,
		height: 400,
		id: 'myBeautifulMovie'
		parameters: {
			wmode: 'opaque',
			bgcolor: '#ff3300',
		},
		vars: {
			myVariable: myJsVar,
			myVariableString: 'hello'
		}
		callBacks: {
			onLoad: myOnloadFunc
		}
	});
	(end)
*/

var Swiff = function(movie, options){
	if (!Swiff.fixed) Swiff.fix();
	options = $merge({
		width: 1,
		height: 1,
		id: null,
		inject: null,
		params: {
			allowScriptAccess: 'sameDomain'
		},
		properties: {},
		callBacks: {},
		vars: {}
	}, options);

	var instance = Swiff.nextInstance();
	var properties = $merge(options.properties, {
		id: options.id || instance,
		width: options.width,
		height: options.height
	});
	var params = options.params;
	var vars = options.vars;
	Swiff.callBacks[instance] = {};
	for (var prop in options.callBacks){
		Swiff.callBacks[instance][prop] = options.callBacks[prop];
		vars[prop] = 'Swiff.callBacks.' + instance + '.' + prop;
	}
	if ($type(vars) == 'object') vars = Object.toQueryString(vars);
	if (vars) params.FlashVars = (params.FlashVars) ? (params.FlashVars + '&' + vars) : vars;

	if (Client.Engine.ie){
		properties.classid = 'clsid:D27CDB6E-AE6D-11cf-96B8-444553540000';
		params.movie = movie;
	} else{
		properties.type = 'application/x-shockwave-flash';
		properties.data = movie;
	}

	var build = '<object ';
	for (var attr in properties) build += attr + '="' + properties[attr] + '" ';
	build += '>';
	for (var name in params) build += '<param name="' + name + '" value="' + params[name] + '" />';
	build += '</object>';
	var obj = new Element('div').setHTML(build).firstChild;
	if (options.inject) $(options.inject).appendChild(obj);
	return obj;
};

Swiff.extend({

	UID: 0,

	callBacks: {},

	nextInstance: function(){
		return 'Swiff' + (++Swiff.UID);
	},

	//from swfObject, fixes bugs in ie+fp9

	fix: function(){
		Swiff.fixed = true;
		window.addEvent('beforeunload', function(){
			__flash_unloadHandler = __flash_savedUnloadHandler = $empty;
		});
		if (!Client.Engine.ie) return;
		window.addEvent('unload', function(){
			Array.each(document.getElementsByTagName('object'), function(swf){
				swf.style.display = 'none';
				for (var p in swf){
					if (typeof swf[p] == 'function') swf[p] = $empty;
				}
			});
		});
	},

	/*
	Function: Swiff.getVersion
		Gets the major version of the flash player installed.

	Returns:
		A number representing the (major) flash version installed, or 0 if no player is installed.
	*/

	getVersion: function(){
		if (!$defined(Swiff.pluginVersion)){
			var version;
			if (navigator.plugins && navigator.mimeTypes.length){
				version = navigator.plugins["Shockwave Flash"];
				if (version && version.description) version = version.description;
			} else if (Client.Engine.ie){
				version = $try(function(){
					return new ActiveXObject("ShockwaveFlash.ShockwaveFlash").GetVariable("$version");
				});
			}
			Swiff.pluginVersion = ($type(version) == 'string') ? parseInt(version.match(/\d+/)[0]) : 0;
		}
		return Swiff.pluginVersion;
	},

	/*
	Function: Swiff.remote
		Calls an ActionScript function from javascript. Requires ExternalInterface.

	Returns:
		Whatever the ActionScript Returns
	*/

	remote: function(obj, fn){
		var rs = obj.CallFunction('<invoke name="' + fn + '" returntype="javascript">' + __flash__argumentsToXML(arguments, 2) + '</invoke>');
		return eval(rs);
	}

});var Window = window;
var Document = document;
var $native = Native;
window.extend(Client.Engine);Function.extend({

	bindAsEventListener: function(bind, args){
		return this.create({'bind': bind, 'event': true, 'arguments': args});
	},

	bindWithEvent: function(bind, args){
		return this.create({'bind': bind, 'event': true, 'arguments': args});
	}

});Json.toString = Json.encode;
Json.evaluate = Json.decode;window.extend(Client);Client.expand({
	getElementsByClassName: function(className){
		var self = (this == window) ? document : this;
		return self.getElements('.' + className);
	}
});

function $E(selector, filter){
	return ($(filter) || document).getElement(selector);
};

function $ES(selector, filter){
	return ($(filter) || document).getElementsBySelector(selector);
};Hash.implement({
	'keys': Hash.prototype.getKeys,
	'values': Hash.prototype.getValues
});