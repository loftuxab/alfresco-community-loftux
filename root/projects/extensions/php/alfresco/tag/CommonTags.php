<?php

   /*
     Copyright (C) 2005 Alfresco, Inc.

     Licensed under the Mozilla Public License version 1.1
     with a permitted attribution clause. You may obtain a
     copy of the License at

       http://www.alfresco.org/legal/license.txt

     Unless required by applicable law or agreed to in writing,
     software distributed under the License is distributed on an
     "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
     either express or implied. See the License for the specific
     language governing permissions and limitations under the
     License.
   */

   require_once("alfresco/tag/BaseTag.php");
   
   /**
    * Form tag
    */
   class FormTag extends BaseTag
   {
      public static function get_name()
      {
         return "form";
      }

      public function do_tag()
      {
         $result = "<form name='main_form' id='main_form' method='post' action='".$this->attributes['action']."'>";
         $result .= $tag->body."</form>";
         return $result;
      }
   }
   
   class InputTag extends BaseTag
   {
      public static function get_name()
      {
         return "input";
      }

      public function do_tag()
      {
         var_dump($this->attributes);

         $value = "";
         if (isset($_REQUEST[$this->attributes["name"]]) == true)
         {
            $value = $_REQUEST[$this->attributes["name"]];
         }

         return "
            <input name='".$this->attributes["name"]."'
                   type='".$this->attributes["type"]."'
                   value='".$value."'
                   style='".$this->attributes["style"]."'/>
         ";
      }
   }

   class ErrorTag extends BaseTag
   {
      public static function get_name()
      {
         return "error";
      }
      
      public function do_tag()
      {
         $result = "";

         if (isset($this->attributes["error_message"]) == true)
         {
            $error_message = $this->attributes["error_message"];
            if ($error_message != "")
            {
               $result = "<span style='font-weight:bold;color:red'>".$error_message."</span>";
            }
         }

         return $result;
      }
   }
?>
