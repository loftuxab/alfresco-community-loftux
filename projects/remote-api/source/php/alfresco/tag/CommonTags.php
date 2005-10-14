<?php

   require_once("alfresco/tag/TagFramework.php");
   
   register_tag("form", "form_tag");
   function form_tag($tag)
   {
      $result = "<form name='main_form' id='main_form' method='post' action='".$tag->attributes['action']."'>";
      $result .= $tag->body."</form>";
      
      return $result;
   }
   
   register_tag("input", "input_tag");
   function input_tag($tag)
   {
      $value = $_REQUEST[$tag->attributes["name"]];
      return "
         <input name='".$tag->attributes["name"]."'
                type='".$tag->attributes["type"]."'
                value='".$value."'
                style='".$tag->attributes["style"]."'/>
      ";
   }

   register_tag("error", "error_tag");
   function error_tag($tag)
   {
      $result = "";

      if (isset($tag->attributes["error_message"]) == true)
      {
         $error_message = $tag->attributes["error_message"];
         if ($error_message != "")
         {
            $result = "<div>".$error_message."</div>";
         }
      }
      
      return $result;
   }
?>
