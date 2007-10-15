package org.alfresco.ace.control.swipe
{
	import mx.containers.Canvas;
	import mx.events.FlexEvent;
	import flash.events.Event;
	import mx.controls.Alert;
	import mx.controls.Button;
	import mx.core.UIComponent;
	import flash.display.DisplayObject;
	import flash.events.MouseEvent;
	import mx.controls.Label;
	import mx.events.EffectEvent;
	import mx.events.ResizeEvent;
	import mx.controls.Image;

	/**
	 * Internal swipe control class
	 */
	public class SwipeInternalClass extends Canvas
	{
		/** Control states */
		private static const STATE_SECONDARY:String = "secondaryState";
		
		/** Display object to be contained inside the swiped canvas' */
		private var _childOne:DisplayObject;		
		private var _childTwo:DisplayObject;
		
		/** Control labels */
		private var _primaryStateLabel:String;
		private var _secondaryStateLabel:String;
		
		/** UI controls */
		public var swipeLabel:Label;
		public var downArrow:Image;
		public var upArrow:Image;
		
		/** Indicates whether the swipe button is enabled or not */
		private var _swipeButtonEnabled:Boolean = true;
		
		/**
		 * Constructor
		 */
		public function SwipeInternalClass()
		{
			super();	
		}
		
		/**
		 * On click event fired when swipe button is clicked.  Does a wipe of the
		 * canvas'
		 */
		public function doWipe(event:Event):void
		{			
			if (currentState == null)
			{
				showSecondaryState();									
			}
			else
			{
				showPrimaryState();	
			}
		}
		
		/**
		 * Shows the primary state, ie the first child control
		 */
		public function showPrimaryState():void
		{
			if (this._swipeButtonEnabled == true)
			{
				currentState = null;	
			}
		}
		
		/**
		 * Shows the secondard state, ie the second child control
		 */
		public function showSecondaryState():void
		{
			if (this._swipeButtonEnabled == true)
			{
				currentState = STATE_SECONDARY;	
			}
		}
		
		/**
		 * Set the first child control
		 */
		public function setChildOne(childOne:DisplayObject, label:String):void
		{
			this._childOne = childOne;
			this._primaryStateLabel = label;
		}
		
		/**
		 * Set the second child control
		 */
		public function setChildTwo(childTwo:DisplayObject, label:String):void
		{
			this._childTwo = childTwo;
			this._secondaryStateLabel = label;
		}
		
		/**
		 * Override for the createChild function.  Positions the child controls in the correct place in
		 * the swipe control
		 */
		override protected function createChildren():void
		{
			super.createChildren();
			
			// Add the child controls
			var canvasOne:Canvas = getChildByName("canvasOne") as Canvas;
			var canvasTwo:Canvas = getChildByName("canvasTwo") as Canvas;
			canvasOne.addChild(this._childOne);
			canvasTwo.addChild(this._childTwo);
			
			// Register interest in the swipeButton events
			var swipeButton:Canvas = getChildByName("swipeButton") as Canvas;
			swipeButton.addEventListener(MouseEvent.CLICK, doWipe);	
			swipeButton.addEventListener(EffectEvent.EFFECT_START, effectStart);
			swipeButton.addEventListener(EffectEvent.EFFECT_END, effectEnd);
			
			// Register interest in the resize event
			canvasTwo.addEventListener(ResizeEvent.RESIZE, onResize);
			
			// Set the initial swipe label value
			swipeLabel.text = this._secondaryStateLabel;
		}
		
		private function effectStart(event:Event):void
		{
			// Disable the button
			this._swipeButtonEnabled = false;		
		}
		
		private function effectEnd(event:Event):void
		{
			// Change the swipe label accordingly
			if (currentState == null)
			{
				swipeLabel.text = this._secondaryStateLabel;	
				downArrow.visible = true;
				downArrow.includeInLayout = true;
				upArrow.visible = false;
				upArrow.includeInLayout = false;								
			}
			else
			{
				swipeLabel.text = this._primaryStateLabel;	
				downArrow.visible = false;
				downArrow.includeInLayout = false;
				upArrow.visible = true;
				upArrow.includeInLayout = true;
			}	
			
			// Re-enable the button
			this._swipeButtonEnabled = true;
		}
		
		private function onResize(event:Event):void
		{
			if (currentState == STATE_SECONDARY)
			{
				// Reset the position of the swipe button
				getChildByName("swipeButton").y = getChildByName("canvasTwo").height+1;
			}
		}
	}
}