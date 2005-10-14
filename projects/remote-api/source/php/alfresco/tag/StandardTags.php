<?php

   require_once($_SERVER["DOCUMENT_ROOT"]."/alfresco/tag/TagFramework.php");
   
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
         <input name='".$tag->attributes["name"]."' type='".$tag->attributes["type"]."' value='".$value."'/>
      ";
   }
?>
