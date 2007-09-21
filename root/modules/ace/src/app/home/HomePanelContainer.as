package app.home
{
	import mx.containers.Canvas;
	import mx.containers.VBox;
	import flash.display.DisplayObject;
	import mx.controls.Alert;
	import mx.core.IChildList;
	import mx.utils.DisplayUtil;
	import util.error.ErrorService;
	import component.util.SimpleGradientBorder;
	import component.util.GlossGradientBorder;
	import mx.core.EdgeMetrics;

	public class HomePanelContainer extends Canvas
	{
		public function HomePanelContainer()
		{
			super();		
		}
		
		override protected function createChildren():void
		{
			try
			{
				var vBox:VBox = new VBox();
				vBox.percentHeight = 100;
				vBox.percentWidth = 100;		
				
				var topCanvas:HomePanelTop = new HomePanelTop();
				topCanvas.percentWidth = 100;
				topCanvas.height = 30;	
				topCanvas.title = this.label;			
				
				var mainCanvas:Canvas = new Canvas();
				mainCanvas.percentWidth = 100;
				mainCanvas.height = 100;
	
				var bottomCanvas:HomePanelBottom = new HomePanelBottom();
				bottomCanvas.percentWidth = 100;
				bottomCanvas.height = 30;
				
				vBox.addChild(topCanvas);
				vBox.addChild(mainCanvas);
				vBox.addChild(bottomCanvas);	
				
				super.createChildren();
				
				var i:int;
				for (i = 0; i < numChildren; i++) 
				{
		    		var control:DisplayObject = getChildAt(i);
					
					this.removeChild(control);
					mainCanvas.addChild(control);
				}
	
				this.addChild(vBox);
			}
			catch (error:Error)
			{
				ErrorService.instance.raiseError(ErrorService.APPLICATION_ERROR, error);
			}
		}
		
	}
}