/*
 * jQuery FlyOut
 * author: Jolyon Terwilliger - Nixbox Web Designs
 * website: http://nixboxdesigns.com/jquery.flyout.php
 *
 * Dual licensed under the MIT and GPL licenses:
 *   http://www.opensource.org/licenses/mit-license.php
 *   http://www.gnu.org/licenses/gpl.html
 *
 * version 0.21 (July 21, 2008)
 * version 0.22 (July 22, 2008) 
 	notes: minor reordering to loadingSrc logic.
 * version 0.23 (August 15, 2008) 
 	added: config options for loadingText and closeTip to facilitate locale.
			Thanks Tony for the nudge.
 * version 0.24 (Oct 2, 2008) 
 	added: customize start location and size of flyout, if different 
			from thumb link. Thanks to Jake Kronika for this patch.
 * version 1.0 (Oct 11, 2008) 
 	added: support for final flyout location via destElement and destPadding: 
			define a fixed container anywhere in the document and the pic will 
			fly to that location, regardless of viewport position. 
 	fixed: clicking on open source link no longer reopens same image. 
	added: 4 callbacks for start and finish of flyOut and putAway animations. 
	fixed: putAway function to put back to correct location, in case 
			thumb has moved (page or div scroll, etc)
 * version 1.1 (Nov 16, 2008)
 	fixed: Opera 9.5+ doesn't report window.height() correctly - patched with code
			from jquery Bug 3117:  http://dev.jquery.com/ticket/3117
			note: once this is patched in jQuery core, or fixed in Opera
			this may eventually be removed.
	added: when flyOut image is completed, a customizable class 
			(default to 'shown') is appended to the thumb image container
			so an external event can trigger the click to close any open
			elements. See demo page for example.
 */

/**
 * The flyout() method provides an alternate means of loading and display sub-content
 * with a nifty flyout animation technique.
 * Currently, flyout only supports img sub-content.
 *
 * flyout() takes a single object argument:  $().flyout({param:setting, etc..})
 *
 * Settings:
 *
 *			outSpeed:	speed in milliseconds for the flyout animation - default: 1000
 *
 *			inSpeed:	speed in milliseconds for the flyback animation - default: 500
 *
 *			outEase:	the easing method to use for the flyout animation - default: swing
 *
 *			inEase:		the easing method to use for the flyback animation - default: swing
 *			
 *			loadingSrc: the image file to use while an image is being loaded prior to flyout
 *						default: none
 *						
 *			loader: 	the ID for the created flyout div that contains the sub-content
 *						this is currently only useful for multiple skinnings via CSS
 *						default: 'loader'
 *
 *			loaderZIndex: the CSS z-index for the flyout
 *						default: 500
 *
 *			widthMargin: the left and right margin space for the final flyout
 *						this value is effectively divided between the left and right margin
 *						default: 40
 *			
 *			heightMargin: the top and bottom margin space for the final flyout
 *						this value is effectively divided between the top and bottom margin
 *						default: 40
 *
 *			loadingText: text shown when image is loading
 *
 *			closeTip: tip text for image alt/title tags
 *
 *			destElement: the destination container - overrides height and widthMargins
 *						specified in CSS notation - e.g. "#picContainer"
 *						default: none
 *
 *			destPadding: number of pixels to pad when flying out to destElement
 *						default: 10
 *
 *			startOffsetX: horizontal offset added to thumb left value for start of flyout animation
 *						Hint: can be negative.
 *						default: 0
 *
 *			startOffsetY: vertical offset added to thumb top value for start of flyout animation.
 *						default: 0
 *
 *			startHeight: overrides starting height of flyout animation
 *						default: 0  (uses thumb image height by default)
 *
 *			startWidth: overrides starting width of flyout animation
 *						default: 0  (uses thumb image width by default)
 *
 *			flyOutStart: function to run at start of flyout animation
 *						default: none
 *
 *			flyOutFinish: function to run at finish of flyout animation
 *						default: none
 *
 *			putAwayStart: function to run at start of putaway animation
 *						default: none
 *
 *			putAwayFinish: function to run at finish of putaway animation
 *						default: none
 *
 * For more details see: http://nixbox.com/demos/jquery.flyout.php
 *
 * @example $('.thumb').flyout();
 * @desc standard flyouts applied to all elements with the 'thumbs' class. 
 * 
 * @example $('.thumb').flyout({loadingSrc:'images/thumb-loading.gif',
 *								outEase:'easeOutCirc',
 *								inEase:'easeOutBounce'});
 * @desc flyouts created with different ease in and ease out and a loading animation image is specified
 *
 * @name flyout
 * @type jQuery
 * @param Object options Options which control the flyout animation and content
 * @cat Plugins/Flyout
 * @return jQuery
 * @author Jolyon Terwilliger (jolyon@nixbox.com)
 */

jQuery.fn.extend({flyout : function(options) {
	
		var shown=false;
		var animating=false;
		var $holder;
		var $thumb;
		var tloc;
		var th;
		var tw;
		var bigimg = new Image();
		var subType = 'img';
		var offset;
		
		this.click(function() {
			if (animating == true) { return false; }
	
			if (shown) { putAway(this); }
			else { flyOut(this); }
	
			return false;
		});
		
		var o = jQuery.extend({
			outSpeed : 1000,
			inSpeed : 500,
			outEase : 'swing',
			inEase : 'swing',
			loadingSrc: null,
			loader: 'loader',
			loaderZIndex: 500,
			widthMargin: 40,
			heightMargin: 40,
			loadingText : "Loading...",
			closeTip : " - Click here to close",
			destPadding: 20,
			startOffsetX: 0,
			startOffsetY: 0,
			startHeight: 0,
			startWidth: 0,
			flyOutStart: function() {},
			flyOutFinish: function() {},
			putAwayStart: function() {},
			putAwayFinish: function() {},
			shownClass: 'shown'
		}, options);
	
		function flyOut(it) {
			animating = true;
			
			$holder = jQuery(it);
			$thumb = jQuery('img',it);
			bigimg = new Image(); 
			sL = jQuery(window).scrollLeft();
			sT = jQuery(window).scrollTop();
			tloc = $thumb.offset();
			tloc.left += o.startOffsetX;
			tloc.top += o.startOffsetY;
			th = (o.startHeight > 0 ? o.startHeight : $thumb.height());
			tw = (o.startWidth > 0 ? o.startWidth : $thumb.width());
			
			jQuery('<div></div>').attr('id',o.loader)
							.appendTo('body')
							.css({'position':'absolute',
								'top':tloc.top,
								'left':tloc.left,
								'height':th,
								'width':tw,
								'opacity':.5,
								'display':'block',
								'z-index':o.loaderZIndex});

			if (o.loadingSrc) {
				jQuery('#'+o.loader).append(jQuery('<img/>')
								.load(function() {
										jQuery(this)
											.css({'position':'relative',
												 'top':th/2-(this.height/2),
												 'left':tw/2-(this.width/2)})
											.attr('alt',o.loadingText);
										})
									.attr('src',o.loadingSrc)
								);
			}
			else {
				jQuery('#'+o.loader).css('background-color','#000')
								.append(jQuery('<span></span>')
										  	.text(o.loadingText)
											.css({'position':'relative',
												 'top':'2px',
												 'left':'2px',
												 'color':'#FFF',
												 'font-size':'9px'})
									 	);
			}

			jQuery(bigimg).load(function() {
				imgtag = jQuery('<img/>').attr('src',$holder.attr('href')).attr('title',$thumb.attr('title')+o.closeTip).attr('alt',$thumb.attr('alt')+o.closeTip).height(th).width(tw);

				o.flyOutStart.call(it);

				if (o.destElement) {
					var $dest = jQuery(o.destElement);
					max_x = $dest.innerWidth() - (o.destPadding*2);
					max_y = $dest.innerHeight() - (o.destPadding*2);
				}
				else {
					max_x = jQuery(window).width()-o.widthMargin;
					if (jQuery.browser.opera) 
						wh = document.getElementsByTagName('html')[0].clientHeight;
					else 
						wh = jQuery(window).height();
					max_y = wh-o.heightMargin;
				}

				width = bigimg.width;
				height = bigimg.height;
	
				x_dim = max_x / width;
				y_dim = max_y / height;
	
				if (x_dim <=y_dim) {
					y_dim = x_dim;
				} else {
					x_dim = y_dim;
				}
				
				dw = Math.round(width  * x_dim);
				dh = Math.round(height * y_dim);
				if (dw>width) {dw = width}
				if (dh>height) {dh = height}
				
				if (o.destElement) {
					dPos = $dest.offset();
					dl = Math.round(($dest.outerWidth()/2)-(dw/2)+dPos.left);
					dt = Math.round(($dest.outerHeight()/2)-(dh/2)+dPos.top);
				}
				else {
					dl = Math.round((jQuery(window).width()/2)-(dw/2)+sL);
					if (jQuery.browser.opera) 
						wh = document.getElementsByTagName('html')[0].clientHeight;
					else 
						wh = jQuery(window).height();
					dt = Math.round((wh/2)-(dh/2)+sT);
				}
				
				jQuery('#'+o.loader).empty().css('opacity',1).append(imgtag).width('auto').height('auto').animate({top:dt, left:dl},{duration:o.outSpeed, queue:false, easing:o.outEase});
				jQuery('#'+o.loader+' '+subType).animate({height:dh, width:dw}, o.outSpeed, o.outEase,
				function() {
					o.flyOutFinish.call(it);
					shown = it;
					$holder.addClass(o.shownClass);
					animating=false;
					jQuery('#'+o.loader+' '+subType).click(function(){putAway(null)})
				});
			});
			bigimg.src = $holder.attr('href');
		}
	
	
		function putAway(next) {
			// for future development:
			if (animating == true || shown == false) {return false;}
			o.putAwayStart.call(shown);
			
			animating = true;
			
			// check $thumb loc again, in case it moved...
			tloc = $thumb.offset();
			tloc.left += o.startOffsetX;
			tloc.top += o.startOffsetY;

			jQuery('#'+o.loader).animate({top:tloc.top, left:tloc.left},{duration:o.inSpeed, queue:false, easing:o.inEase});
			jQuery('#'+o.loader+' '+subType).animate({height:th, width:tw}, 
				o.inSpeed, o.inEase, 
				function() {
					jQuery('#'+o.loader).css('display','none').remove(); 
					o.putAwayFinish.call(shown);
					animating=false;
					bigimg=null;			
					if (next && next != shown) {
						shown = false;
						flyOut(next);
					}
					shown = false;
					$holder.removeClass(o.shownClass);
				});
		}
		
		return this;	// never break the chain
		
	}
});
