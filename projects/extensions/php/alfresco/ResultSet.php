<?php

class ResultSet
{
   private $row_count = 0;
   private $rows = array();
   private $query_session;
   private $meta_data;

   public static function createResultSet($webservice_query_result)
   {
      // Create the result set object
      $result_set = new ResultSet();

      // Set the query session
      $result_set->query_session = $webservice_query_result->querySession;

      // Get the web service result set
      $webservice_result_set = $webservice_query_result->resultSet;

      if (isset($webservice_result_set->rows) == true)
      {
         $result_set->rows[] = ResultSetRow::createResultSetRow($webservice_result_set->rows);
         $result_set->row_count = 1;
      }
      else
      {
         foreach ($webservice_result_set as $webservice_row)
         {
            if (isset($webservice_row->node) == true)
            {
               $result_set->rows[] = ResultSetRow::createResultSetRow($webservice_row);
            }
            else
            {
               // You have found the record set count
               $result_set->row_count = $webservice_row;
            }
         }
      }

      return $result_set;
   }

   function querySession()
   {
      return $this->query_session;
   }

   function rowCount()
   {
      return $this->row_count;
   }

   function rows()
   {
      return $this->rows;
   }
}

class ResultSetRow
{
   private $row_index;
   private $score;

   private $uuid;
   private $type;
   private $aspects;

   private $values = array();

   public static function createResultSetRow($webservice_row)
   {
      $result_set_row = new ResultSetRow();

      // Get the row index
      $result_set_row->row_index = $webservice_row->rowIndex;

      // NOTE: The result score is not being returned from the W/S
      //$result_set_row->score = $webservice_row->score;

      // Get the node details
      $webservice_node = $webservice_row->node;
      $result_set_row->uuid = $webservice_node->id;
      $result_set_row->type = $webservice_node->type;
      // NOTE: the aspect details are not being returned from the W/S

      // Now we need to get the result details
      foreach ($webservice_row->columns as $column)
      {
         $result_set_row->values[$column->name] = $column->value;
      }

      return $result_set_row;
   }

   public function rowIndex()
   {
      return $this->row_index;
   }

   public function score()
   {
      return $this->score;
   }

   public function uuid()
   {
      return $this->uuid;
   }

   public function type()
   {
      return $this->type;
   }

   public function aspects()
   {
      return $this->aspects;
   }

   public function values()
   {
      return $this->values;
   }

   public function getValue($name)
   {
      return $this->values[$name];
   }
}

?>
