/*
 * Flip! jQuery Plugin (http://lab.smashup.it/flip/)
 * @author Luca Manno (luca@smashup.it) [http://i.smashup.it]
 * 		[Original idea by Nicola Rizzo (thanks!)]
 * 
 * @version 0.4.1
 * 
 * @changelog
 * v 0.4.1 	->	Fixed a regression in Chrome and Safari caused by getTransparent [Oct. 1, 2008]
 * v 0.4 	->	Fixed some bugs with transparent color. Now Flip! works on non-white backgrounds | Update: jquery.color.js plugin or jqueryUI still needed :( [Sept. 29, 2008]
 * v 0.3 	->	Now is possibile to define the content after the animation.
 * 				(jQuery object or text/html is allowed) [Sept. 25, 2008]
 * v 0.2 	->	Fixed chainability and buggy innertext rendering (xNephilimx thanks!)
 * v 0.1 	->	Starting release [Sept. 11, 2008]
 * 
 */
(function($) {
	
  var getTransparent = function(el){
  		for(var n=0;n<el.parents().length;n++){
			var parent = el.parents().get(n);
			var pBg = $.browser.safari ? $(parent).css("background") : $(parent).css("background-color");
			if(pBg!='' && pBg!='transparent'){
				return pBg;
			}
		}
  }
	
  jQuery.fn.flip = function(settings){
	return this.each( function() {
		var $this = $(this);
		if($this.data('flipLock')==true){
			return false;
		} else {
			$this.data('flipLock',true);	
		}
		
		var flipObj = {
			width: $this.width(),
			height: $this.height(),
			bgColor: settings.bgColor || $this.css("background-color"),
			fontSize: $this.css("font-size") || "12px",
			direction: settings.direction || "tb",
			toColor: settings.color || "#f00",
			speed: settings.speed || 500,
			top: $this.offset().top,
			left: $this.offset().left,
			target: settings.content || null,
			transparent: getTransparent($this) || '#fff'
		};
	
		var cloneId = "flipClone_"+(new Date()).getTime();
		
		$this
			.css("visibility","hidden")
			.clone(true)
				.appendTo("body")
				.html("")
				.css({visibility:"visible",position:"absolute",left:flipObj.left,top:flipObj.top,margin:0,zIndex:9999}).attr("id",cloneId);
	
		var dirOptions = {
			"tb": {
				"start": {fontSize:'0px',lineHeight:'0px',borderTopWidth:flipObj.height,borderLeftWidth:'0px',borderRightWidth:'0px',borderBottomWidth:'0px',borderTopColor:flipObj.bgColor,borderBottomColor:flipObj.transparent,borderLeftColor:flipObj.transparent,borderRightColor:flipObj.transparent,borderStyle:'solid',height:'0px',width:flipObj.width},
				"first": {borderTopWidth: '0px',borderLeftWidth: (flipObj.height/100)*15,borderRightWidth: (flipObj.height/100)*15,borderBottomWidth: '0px',borderTopColor: '#999',borderBottomColor: '#999',borderLeftColor: flipObj.transparent,borderRightColor: flipObj.transparent,top: (flipObj.top+(flipObj.height/2)),left: (flipObj.left-(flipObj.height/100)*15)},
				"second": {borderBottomWidth: flipObj.height,borderTopWidth: '0px',borderLeftWidth: '0px',borderRightWidth: '0px',borderTopColor: flipObj.transparent,borderBottomColor: flipObj.toColor,borderLeftColor: flipObj.transparent,borderRightColor: flipObj.transparent,top: flipObj.top,left: flipObj.left}
			},
			"bt": {
				"start": {fontSize:'0px',lineHeight:'0px',borderTopWidth:'0px',borderLeftWidth:'0px',borderRightWidth:'0px',borderBottomWidth:flipObj.height,borderTopColor:flipObj.transparent,borderBottomColor:flipObj.bgColor,borderLeftColor:flipObj.transparent,borderRightColor:flipObj.transparent,borderStyle:'solid',height:'0px',width:flipObj.width},
				"first": {borderTopWidth: '0px',borderLeftWidth: (flipObj.height/100)*15,borderRightWidth: (flipObj.height/100)*15,borderBottomWidth: '0px',borderTopColor: '#999',borderBottomColor: '#999',borderLeftColor: flipObj.transparent,borderRightColor: flipObj.transparent,top: (flipObj.top+(flipObj.height/2)),left: (flipObj.left-(flipObj.height/100)*15)},
				"second": {borderTopWidth: flipObj.height,borderLeftWidth: '0px',borderRightWidth: '0px',borderBottomWidth: '0px',borderTopColor: flipObj.toColor,borderBottomColor: flipObj.transparent,borderLeftColor: flipObj.transparent,borderRightColor: flipObj.transparent,top: flipObj.top,left: flipObj.left							
				}
			},
			"lr": {
				"start": {width:'0px',fontSize:'0px',lineHeight:'0px',borderTopWidth:'0px',borderLeftWidth:flipObj.width,borderRightWidth:'0px',borderBottomWidth:'0px',borderTopColor:flipObj.transparent,borderBottomColor:flipObj.transparent,borderLeftColor:flipObj.bgColor,borderRightColor:flipObj.transparent,borderStyle:'solid',height:flipObj.height},
				"first": {borderTopWidth: (flipObj.height/100)*10,borderLeftWidth: '0px',borderRightWidth: '0px',borderBottomWidth: (flipObj.height/100)*10,borderTopColor: flipObj.transparent,borderBottomColor: flipObj.transparent,borderLeftColor: '#999',borderRightColor: '#999',top: flipObj.top-(flipObj.height/100)*10,left: flipObj.left+(flipObj.width/2)},
				"second": {borderTopWidth: '0px',borderLeftWidth: '0px',borderRightWidth: flipObj.width,borderBottomWidth: '0px',borderTopColor: flipObj.transparent,borderBottomColor: flipObj.transparent,borderLeftColor: flipObj.transparent,borderRightColor: flipObj.toColor,top: flipObj.top,left: flipObj.left}
			},
			"rl": {
				"start": {width:'0px',fontSize:'0px',lineHeight:'0px',borderTopWidth:'0px',borderLeftWidth:'0px',borderRightWidth:flipObj.width,borderBottomWidth:'0px',borderTopColor:flipObj.transparent,borderBottomColor:flipObj.transparent,borderLeftColor:flipObj.transparent,borderRightColor:flipObj.bgColor,borderStyle:'solid',height:flipObj.height},
				"first": {borderTopWidth: (flipObj.height/100)*10,borderLeftWidth: '0px',borderRightWidth: '0px',borderBottomWidth: (flipObj.height/100)*10,borderTopColor: flipObj.transparent,borderBottomColor: flipObj.transparent,borderLeftColor: '#999',borderRightColor: '#999',top: flipObj.top-(flipObj.height/100)*10,left: flipObj.left+(flipObj.width/2)},
				"second": {borderTopWidth: '0px',borderLeftWidth: flipObj.width,borderRightWidth: '0px',borderBottomWidth: '0px',borderTopColor: flipObj.transparent,borderBottomColor: flipObj.transparent,borderLeftColor: flipObj.toColor,borderRightColor: flipObj.transparent,top: flipObj.top,left: flipObj.left}
			}
		};
		
		var _self = $this;
		
		var newContent = function(){
			var target = flipObj.target;
			return target && target.jquery ? target.html() : target;
		}
		
		function queue(_this,_self){			
			_this.queue(function(){
				_this.html('').css(dirOptions[flipObj.direction].start);
				_this.dequeue();
			});
			_this.animate(dirOptions[flipObj.direction].first,flipObj.speed);	
			_this.animate(dirOptions[flipObj.direction].second,flipObj.speed);
			_this.queue(function(){
				_self.css({
					backgroundColor: flipObj.toColor,
					visibility: "visible"
				});
				var nC = newContent();
				if(nC){_self.html(nC);}
				_this.remove();
				_self.removeData('flipLock');
				_this.dequeue();
			});
		}
		queue($("#"+cloneId),_self);
		
	});
  };
})(jQuery);