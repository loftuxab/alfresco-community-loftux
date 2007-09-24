package component.dashedline
{
	import mx.containers.Canvas
	import mx.controls.HRule;
	import mx.core.UIComponent;
	import flash.display.Sprite;
	
	
	public class DrawLine extends  Canvas
		{
			
		public function DrawLine()
		{
			
			var line:Canvas = new Canvas();
			line.percentWidth = 100;
			var dashedLine:DashedLine = new DashedLine();
			
			dashedLine.drawHorizontal(line, 0, 1250, 0, 3, 1,0xafa0a0);
			this.addChild(line);
		}
		
		
		
	}
}