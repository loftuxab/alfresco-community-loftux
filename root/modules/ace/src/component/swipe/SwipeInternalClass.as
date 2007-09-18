package component.swipe
{
	import mx.containers.Canvas;
	import mx.events.FlexEvent;
	import flash.events.Event;
	import mx.controls.Alert;
	import mx.controls.Button;
	import mx.core.UIComponent;
	import flash.display.DisplayObject;
	import flash.events.MouseEvent;

	public class SwipeInternalClass extends Canvas
	{
		private var _childOne:DisplayObject;		
		private var _childTwo:DisplayObject;
		
		public function SwipeInternalClass()
		{
			super();	
		}
		
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
		
		public function showPrimaryState():void
		{
			currentState = null;	
		}
		
		public function showSecondaryState():void
		{
			currentState = "secondaryState";	
		}
		
		public function setChildOne(childOne:DisplayObject):void
		{
			this._childOne = childOne;
		}
		
		public function setChildTwo(childTwo:DisplayObject):void
		{
			this._childTwo = childTwo;
		}
		
		override protected function createChildren():void
		{
			super.createChildren();
			
			(this.getChildByName("canvasOne") as Canvas).addChild(this._childOne);
			(this.getChildByName("canvasTwo") as Canvas).addChild(this._childTwo);
			
			var swipeButton:Canvas = getChildByName("swipeButton") as Canvas;
			swipeButton.addEventListener(MouseEvent.CLICK, doWipe);	
		}
	}
}