<?php

require_once "alfresco/type/ResultSet.php";

class DataListTag extends BaseTag
{
   public $result_set;
   public $row_index = -1;

   public static function get_name()
   {
      return "datalist";
   }

   public function has_next_row()
   {
      $result = false;
      if ($this->row_index+1 < $this->result_set->rowCount())
      {
        $result = true;
      }
      return $result;
   }

   public function get_next_row()
   {
      $row = null;
      if ($this->row_index+1 < $this->result_set->rowCount())
      {
         $rows = $this->result_set->rows();
         $row = $rows[$this->row_index+1];
         $this->row_index += 1;
      }
      return $row;
   }

   public function before_do_tag()
   {
      global $result_sets;
      $result_set_id = $this->attributes["result_set"];

      // TODO error if the result set id is not set

      // Try and get the result set
      $this->result_set = $result_sets[$result_set_id];

      // TODO error if the result set is not found
   }

   public function do_tag()
   {
      // Output the table
      $result = "<table cellspacing='0' cellpadding='0' border='0'>";
      if ($this->result_set->rowCount() == 0)
      {
         $result .= "<tr><td>The list is empty</td></tr>";
      }
      else
      {
         $result .= $this->body;
      }
      $result .= "</table>";

      return $result;
   }
}

class DataRowTag extends BaseTag
{
   public $row;

   public static function get_name()
   {
      return "datarow";
   }

   public function is_tag_complete()
   {
      return !$this->parent->has_next_row();
   }

   public function before_do_tag()
   {
      $this->row = $this->parent->get_next_row();
   }

   public function do_tag()
   {
      $result = "<tr><td>";
      $result .= $this->body;
      $result .= "</td></tr>";

      return $result;
   }
}

class TextTag extends BaseTag
{
   public static function get_name()
   {
      return "text";
   }

   public function do_tag()
   {
      $text = "";
      if (isset($this->attributes["text"]) == true)
      {
         $text = $this->attributes["text"];
      }

      if (isset($this->attributes["data_source"]) == true)
      {
         if (get_class($this->parent) == "DataRowTag")
         {
            // Overwrite the text value with the data value
            $text = $this->parent->row->getValue($this->attributes["data_source"]);
         }
      }

      return "<span>".$text."</span>";
   }
}

?>
