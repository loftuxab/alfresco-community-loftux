(function() {
  
  UIControl = function (config){
    this.events = [];
    this.elements = null;
    if (config)
    {
      this.config = config;
      if (this.config.setUpCustomEvents)
      {
        //set up around advice for default methods  and any specified methods
        //These fire custom events
        function _setupEvent(mth,evt) {
          if (!this.events[Do.BEFORE+evt]){
            this.events[Do.BEFORE+evt] = new Core.util.CustomEvent(Do.BEFORE+evt,this);
          }
          if (!this.events[Do.AFTER+evt]){
            this.events[Do.AFTER+evt] = new Core.util.CustomEvent(Do.AFTER+evt,this);
          }
          Do.around(this,mth,[
          //before
          function(){
            this.events[Do.BEFORE+evt].fire({args:arguments});
            // console.log(Do.BEFORE + mth + ' ('+evt+')');  
            return this;
          },
          //after
          function(){
            this.events[Do.AFTER+evt].fire({args:arguments});
            // console.log(Do.AFTER + mth);        
            return this;
          }]);
        };
        var evts = (['Init','Render','Destroy','Activate','Deactivate','Hide','Show']).concat(this.config.setUpCustomEvents);
        for (var i=0,len=evts.length;i<len;i++)
        {
          var mthdName = evts[i].toLowerCase();
          _setupEvent.apply(this,[mthdName,evts[i]]);
        };
      }
    };
    return this;
};

  UIControl.prototype = {
    init : function init()
    {
      // console.log('in Init func');
      return this;
    },

    render : function render() 
    {
      return this;
    },

    destroy : function destroy()
    {
      return this;
    },

    on : function on(e,fn,obj,scope)
    {

      if (this.events[e])
      {
        this.events[e].subscribe(fn,obj,scope || window);
      }
      else if (this.elements)//presume native browser events eg click, touchstart
      {
        this.elements.first().addEventListener(e,function()
        {
          fn.apply(obj,arguments);
        });
      }
      return this;      
    },

    activate : function activate()
    {
      this.elements.addClass('active');
      return this;
    },
    deactivate : function deactivate()
    {
      this.elements.removeClass('active');
      return this;
    },
    show : function show()
    {
      return this;
    },

    hide : function hide()
    {
      return this;      
    },
    bind : function bind(fn)
    {
      return function() 
      {
        fn.apply(this,arguments);
      };
    } 
  };
})();

