<?php

class ResultSetTag extends BaseTag
{
   public static function get_name()
   {
      return "resultset";
   }

   public function is_container_tag()
   {
      return true;
   }

   public function do_tag()
   {
      return "";
   }
}

?>
