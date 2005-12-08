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

abstract class BaseTag
{
   protected $parent;
   protected $children = array();

   public $body;
   public $length;
   public $start_pos;
   public $attributes;

   public function __construct($parent)
   {
      // Set the member variables
      $this->parent = $parent;

      // Add child callback
      if ($parent != null)
      {
         $parent->add_child($this);
      }
   }

   public function before_do_tag()
   {
   }

   abstract public static function get_name();

   abstract public function do_tag();
   
   public function is_tag_complete()
   {
      return true;
   }

   public function add_child($child)
   {
      $this->children[] = $child;
   }
}

?>
