// Config Script file
package util.webscript{
	
	public class ConfigService{
		/** Static instance of the authentication service */
		private static var _instance:ConfigService;
		
		/** url for config */
		private var configurl:String = "http://localhost:8080";
		
		/**
		 * Singleton method to get the instance of the Search Service
		 */
		public static function get instance():ConfigService
		{
			if (ConfigService._instance == null)
			{
				ConfigService._instance = new ConfigService();
				
			}
			return ConfigService._instance;
		}
		
		/**
		 * Default constructor
		 */
		public function ConfigService()
		{
		}
		
		public function get url():String
		{
			return this.configurl;
		}
		
	}
}