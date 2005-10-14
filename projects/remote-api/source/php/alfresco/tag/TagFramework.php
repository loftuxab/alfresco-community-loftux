<?php

   /**
    * =====================================================================
    * Global Constants
    */
   
   /**
    * Global constant values
    */
   $PHP_TAG = "<alftag:";
   $PHP_TAG_END = "</alftag:";

   /**
    * Global tag register
    */
   $TAG_REGISTER = null;

   $ACTION_REGISTER = array();
   
   
   /**
    * =====================================================================
    * Public Functions
    */
 
   function start_tags()
   {
      ob_start("callback");
   }
   
   function end_tags()
   {
      // TODO: put the ob_flush call in here
   }
      
   function register_tag($tag_name, $fn_name, $attribute_defaults=null)
   {
      global $TAG_REGISTER;   
      $TAG_REGISTER[$tag_name] = new SPTagDefinition($tag_name, $fn_name, $attribute_defaults);
   }         
   
   function register_action($action_id, $action_fn)
   {
      global $ACTION_REGISTER;
      $ACTION_REGISTER[$action_id] = $action_fn;
   }

   function execute_action($action_id, $attributes)
   {
      global $ACTION_REGISTER;
      $fn_name = $ACTION_REGISTER[$action_id];
      return $fn_name($action_id, $attributes);
   } 

 
   /**
    * =====================================================================
    * Class Defintions
    */
    
   class SPTagDefinition
   {
      var $tag_name;
      var $fn_name;
      var $attribute_defaults;
      
      function SPTagDefinition($tag_name, $fn_name, $attribute_defaults=null)
      {
         $this->tag_name = $tag_name;
         $this->fn_name = $fn_name;
         
         // Sort out the attribute defaults array
         if ($attribute_defaults == null)
         {
            $this->attribute_defaults = array();
         }
         else
         {
            $this->attribute_defaults = $attribute_defaults;
         }
      }
   }


   /**
    * SPTag class
    * 
    * Encapsulates the parsed information about a SPTag.
    */
   class SPTag
   {
      var $passed_tag_name;
      var $tag_definition;
      var $start_pos;
      var $length;
      var $body;
      var $attributes;
      var $parent_tag;   
   
      function SPTag()
      {
      }
      
      function set_tag_name($tag_name)
      {
         // Get the associated tag defintion
         global $TAG_REGISTER;      
         $this->passed_tag_name = $tag_name;
         $this->tag_definition = $TAG_REGISTER[$tag_name];
      }
      
      function init_attributes($attributes)
      {
         $this->attribtues = $attribtues; // TODO sort out the default values
      }
      
      /**
       * Evaluate the tag
       */
      function do_tag()
      {
         $fn_name = $this->tag_definition->fn_name;   
         error_log("Trying to execute tag: ".$this->passed_tag_name);          
         return $fn_name($this);
      }
   }


   /**
    * =====================================================================
    * Private Functions
    */
   
   /**
    * The callback method
    *
    * @param   $buffer  string containing the contents of the page
    * @return  string   the contents of the page with the tag resolved
    */
   function callback($buffer)
   {    
      return parse_buffer($buffer);     
   }
   
   /**
    * Parse a string for the presence of an spTags.  All tags found are evaluated and their results replaced
    * in the string returned.
    *
    * @param   $buffer  the string to parse for spTags
    * @return  string   the parsed string with the spTags resolved
    */
   function parse_buffer($buffer, $parent_tag = null)
   {
      global $PHP_TAG;
       
      $result = '';
      $offset = 0;
   
      $start_pos = strpos($buffer, $PHP_TAG);
      while ($start_pos !== false)
      {
         // Output the content's up to the tag
         $result .= substr($buffer, $offset, $start_pos-$offset);
         
         // Parse the tag
         $tag = parse_tag($buffer, $start_pos, $parent_tag);    
         
         // Execute the tag      
         $result .= $tag->do_tag();
         
         // Move the offset along to the end of the tag
         $offset = $start_pos + $tag->length; //$tag["length"];      
         
         // Look for the next tag
         $start_pos = strpos($buffer, $PHP_TAG, $offset);     
      }
      
      // Output the remainder of the buffer before returning
      return $result.substr($buffer, $offset);
   }
   
   /**
    * Parses the string representing a spTag
    *
    * @param   $buffer     the string to parse
    * @param   $start_pos  the start position in the passed buffer
    */
   function parse_tag($buffer, $start_pos, $parent_tag)
   { 
      // Include the required global values  
      global $PHP_TAG;
      global $PHP_TAG_END;
      
      // Local variable declarations
      $tag_name = '';
      $char = '';
      $body = '';
      $attributes = '';
      
      $tag = new SPTag();
      
      // Calculate the offset value
      $offset = $start_pos + strlen($PHP_TAG);
      
      while (true)
      {
         // Read the next character in the buffer
         $char = $buffer{$offset};
         $offset += 1;
         
         if ($char == '/')
         {         
            if ($buffer{$offset} != '>')
            {
               // This is a parse error !!! should be a > here !!!            
            }
            $offset += 1;
            break;
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
            }
            else
            {          
               // Get the body
               $body = substr($buffer, $offset, $end_tag_pos - $offset);
               
               // Parse the body for inner tags
               $tag->body = parse_buffer($body);
               
               // Adjust the offset value
               $offset = $end_tag_pos + strlen($end_tag);      
            }
            
            break;
         }
         else if ($char == ' ')
         {         
            // Parse attributes
            $tag->attributes = parse_attributes($buffer, $offset);
         }
         else
         {
            // Build up the tag name
            $tag_name .= $char;
         }
      }      
      
      $tag->set_tag_name($tag_name);
      $tag->start_pos = $start_pos;
      $tag->length = $offset - $start_pos;   
      
      // Create and return the tag object 
      return $tag;
   }
   
   /**
    * Parse an attribute string
    * 
    * @param   $buffer   the current string being parsed
    * @param   &$offset  the offset position in the buffer
    * @return  array     array containing the name and value pairs of the attribtues in the buffer
    */
   function parse_attributes($buffer, &$offset)
   {
      // Create the attribute array
      $attributes = array();
      
      // Find the end of the attribtues
      $pos = strpos($buffer, '>', $offset);
      if ($pos !== false)
      {
         // Get the string containing the attribtue values
         $attrib_buffer = substr($buffer, $offset, $pos - $offset);
              
         error_log("Attribute buffer - ".$attrib_buffer);      
         $attrib_offset = 0;
       
         while ($attrib_offset < strlen($attrib_buffer))
         {  
            // TODO strip any blank spaces from the name
            $name = readUntil($attrib_buffer, '=', $attrib_offset);
            $value = '';
            
            $next = $attrib_buffer{$attrib_offset};
            if ($next == "\"" || $next == "'")
            {
               $attrib_offset += 1;
               $value = readUntil($attrib_buffer, $next, $attrib_offset);
               
               // TODO step over one space, need to cope with more!
               $attrib_offset += 1;
            }
            else
            {
               // TODO need to cope with an unkown number of spaces
               $value = readUntil($attrib_buffer, " ", $attrib_offset);
            }   
            
            error_log("Attribute ->".$name.": ".$value);
            
            $attributes[$name] = $value;
         }         
         
         // Update the buffer offset
         $offset = $pos;
      }
      else
      {
         // Error since the tag isn't closed
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
?>
