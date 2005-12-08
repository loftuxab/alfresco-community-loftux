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

   require_once('alfresco/tag/TagFramework.php');
   require_once('alfresco/tag/CommonTags.php');
   
   class BoldTag extends BaseTag
   {
      public static function get_name()
      {
         return "bold";
      }

      public function do_tag()
      {
         return "<b>".$this->body."</b>";
      }
   }

   class ItalicTag extends BaseTag
   {
      public static function get_name()
      {
         return "italic";
      }

      public function do_tag()
      {
         return "<i>".$this->body."</i>";
      }
   }
   
   start_tags();
?>

<html>

<head>
   <title>Tag tests</title>
</head>

<body>

   <alftag:bold>This is bold</alftag:bold>
   <hr/>
   <alftag:bold><alftag:italic>This is bold and italic</alftag:italic></alftag:bold>
   <hr/>
   <alftag:error error_message="This is an error message"/>
   <hr/>
   
   <?php
      echo "Tag register <hr>";
      foreach ($TAG_REGISTER as $name=>$class)
      {
         echo $name."=>".$class."<hr>";
      }
   
   ?>
   <hr>
   <?php
   
      echo parse_buffer("<alftag:bold>This is bold</alftag:bold>
   <hr/>
   <alftag:bold><alftag:italic>This is bold and italic</alftag:italic></alftag:bold>
   <hr/>
   <alftag:error  error_message = \"This is an error message\"  another= bobbins three='asdf'  ></alftag:error>
   <hr/>");
   
   ?>

</body>

</html>
