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

include_once "classes.inc";
include_once "alfresco/tag/BaseTag.php";

// TODO need to put this lot in a service class ...

$PHP_TAG = "<alftag:";
$PHP_TAG_END = "</alftag:";

$TAG_REGISTER = array();

function start_tags()
{
   global $TAG_REGISTER;

   // Before we do anything we need to register the tags that are currently
   // available
   foreach (get_declared_classes() as $class)
   {
      if (get_parent_class($class) == 'BaseTag')
      {
         $tag_name = call_user_func(array($class, 'get_name'));
         $TAG_REGISTER[$tag_name] = $class;
      }
   }

   // Now we need to scan the current buffer with a callback
   ob_start("callback");
}

function end_tags()
{
   // TODO: put the ob_flush call in here
}

function callback($buffer)
{
   return parse_buffer($buffer);
}

function parse_buffer($buffer, $process_body=true, $parent_tag=null)
{
   global $PHP_TAG;

   $result = '';
   $offset = 0;

   $start_pos = strpos($buffer, $PHP_TAG);
   while ($start_pos !== false)
   {
      // Output the content's up to the tag
      $result .= substr($buffer, $offset, $start_pos-$offset);

      if ($parent_tag != null && $process_body == true)
      {
         // Check the parent tag for the process body flag
         $process_body = !$parent_tag->is_container_tag();
      }

      // Parse the tag
      $tag = parse_tag($buffer, $start_pos, $process_body, $parent_tag);

      if ($process_body == true)
      {
         $result .= $tag->do_tag();
      }

      // Move the offset along to the end of the tag
      $offset = $start_pos + $tag->length;

      // Look for the next tag
      $start_pos = strpos($buffer, $PHP_TAG, $offset);
   }

   // Output the remainder of the buffer before returning
   return $result.substr($buffer, $offset);
}

function is_white_space($char)
{
   return ($char == ' ' ||
           $char == '\n' ||
           $char == '\t');
}

function ignore_white_space($buffer, &$offset)
{
   $next_char = $buffer{$offset};
   while (is_white_space($next_char) == true)
   {
      $offset += 1;
      $next_char = $buffer{$offset};
   }
}

function parse_name($buffer, &$offset)
{
   $tag_name = "";
   $next_char = $buffer{$offset};
   while (is_white_space($next_char) == false &&
          $next_char != '/' &&
          $next_char != '>')
   {
      $tag_name .= $next_char;
      $offset += 1;
      $next_char = $buffer{$offset};
   }
   return $tag_name;
}

function parse_tag($buffer, $start_pos, $process_body, $parent_tag)
{
   // Include the required global values
   global $PHP_TAG;
   global $PHP_TAG_END;

   // Local variable declarations
   $tag_name = '';
   $char = '';
   $body = '';
   $attributes = '';
   $tag = null;

   // Calculate the offset value
   $offset = $start_pos + strlen($PHP_TAG);
   
   // Create the tag object
   $tag_name = parse_name($buffer, $offset);
   $tag = create_tag($tag_name, $parent_tag);
   
   // Remove white space
   ignore_white_space($buffer, $offset);
   
   $next_char = $buffer{$offset};
   
   if ($next_char != '/' && $next_char != '>')
   {
      // Get the attributes of the tag
      $tag->attributes = get_attributes($buffer, $offset);
      
      // Remove any white space
      ignore_white_space($buffer, $offset);
   }

      // Read the next character in the buffer
      $char = $buffer{$offset};
      $offset += 1;

      if ($char == '/')
      {
         if ($buffer{$offset} != '>')
         {
            // This is a parse error !!! should be a > here !!!
            throw new Exception("There should be a > here");
         }
         $offset += 1;
      }
      else if ($char == '>')
      {
         // Build the end tag (based on the tag name)
         $end_tag = $PHP_TAG_END.$tag_name.">";

         // Look for the position of the end tag
         $end_tag_pos = strpos($buffer, $end_tag, $offset);
         if ($end_tag_pos === false)
         {
            // Error since there is no closing tag
            throw new Exception("There is no closing tag.");
         }
         else
         {
            // Get the body
            $body = substr($buffer, $offset, $end_tag_pos - $offset);

            // Parse the body for inner tags
            $tag->body = parse_buffer($body, $process_body, $tag);

            // Adjust the offset value
            $offset = $end_tag_pos + strlen($end_tag);
         }
   }
   
   $tag->start_pos = $start_pos;
   $tag->length = $offset - $start_pos;

   // Create and return the tag object
   return $tag;
}

function parse_attribute($buffer, &$offset)
{
   $attribute_name = "";
   $attribute_value = "";

   // Get the attribute name
   $next_char = $buffer{$offset};
   while ($next_char != '=' &&
          is_white_space($next_char) == false)
   {
      $attribute_name .= $next_char;
      $offset += 1;
      $next_char = $buffer{$offset};
   }

   // Ignore any white space before the equals
   ignore_white_space($buffer, $offset);

   // Check for the equals
   $next_char = $buffer{$offset};

   if ($next_char != '=')
   {
      throw new Exception("Equals missing for attribute assignment");
   }
   $offset += 1;

   // Ignore any white space the other side of the equals
   ignore_white_space($buffer, $offset);

   $next_char = $buffer{$offset};
   if ($next_char == "\"" || $next_char == "'")
   {
      $offset +=1;
      $end_char = $next_char;

      // Get the value in the quotes
      $next_char = $buffer{$offset};
      while ($next_char != $end_char)
      {
         // Try and detect a missing end quote
         if ($next_char == '>')
         {
            throw new Exception("Attribute value is missing and end quote");
         }

         $attribute_value .= $next_char;
         $offset += 1;
         $next_char = $buffer{$offset};
      }

      // Step over the end quote
      $offset +=1;
   }
   else
   {
      while (is_white_space($next_char) == false &&
             $next_char != '/' &&
             $next_char != '>')
      {
         $attribute_value .= $next_char;
         $offset += 1;
         $next_char = $buffer{$offset};
      }
   }

   ignore_white_space($buffer, $offset);

   print($attribute_name."=".$attribute_value."<br>");

   return array($attribute_name, $attribute_value);
}

function get_attributes($buffer, &$offset)
{
   $attributes = array();

   $next_char = $buffer{$offset};
   while ($next_char != '/' && $next_char != '>')
   {
      $attribute = parse_attribute($buffer, $offset);
      $attributes[$attribute[0]] = $attribute[1];
      $next_char = $buffer{$offset};
   }

   return $attributes;
}

function readUntil($buffer, $untill, &$offset)
{
   $result = '';
   $char = $buffer{$offset};
   $offset += 1;

   while (($char != $untill) && ($offset < strlen($buffer)))
   {
      $result .= $char;
      $char = $buffer{$offset};
      $offset += 1;
   }

   return $result;
}

function create_tag($tag_name, $parent)
{
   global $TAG_REGISTER;
   $class = $TAG_REGISTER[$tag_name];
   return new $class($parent);
}


?>
