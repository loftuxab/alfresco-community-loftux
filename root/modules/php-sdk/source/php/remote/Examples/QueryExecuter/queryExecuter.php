<?php
/*
 * Copyright (C) 2005 Alfresco, Inc.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.

 * As a special exception to the terms and conditions of version 2.0 of 
 * the GPL, you may redistribute this Program in connection with Free/Libre 
 * and Open Source Software ("FLOSS") applications as described in Alfresco's 
 * FLOSS exception.  You should have recieved a copy of the text describing 
 * the FLOSS exception, and it is also available here: 
 * http://www.alfresco.com/legal/licensing"
 */
 
require_once('Alfresco/Service/Repository.php');
require_once('Alfresco/Service/Session.php');
require_once('Alfresco/Service/SpacesStore.php');
	
session_start();

// Start the Alfresco session
$repository = new Repository();
$ticket = $repository->authenticate("admin", "admin");
$session = $repository->createSession($ticket);

// Get the current query details
$currentStore = new SpacesStore($session);
if (isset($_REQUEST['store']) == true)
{
	$currentStore = $session->getStoreFromString($_REQUEST['store']);	
}
$statement = null;
if (isset($_REQUEST['statement']) == true)
{	
	$statement = $_REQUEST['statement'];	
}

// Figure out whether we need to execute the query
$nodes = null;
if ($currentStore != null && $statement != null)
{
	$nodes = $session->query($currentStore, $statement);
}

?>

<html>
<head>
	<title>Query Executer</title>
</head>
<body>

<form action="queryExecuter.php" method="post" name="mainForm">

<table cellpadding=2 cellspacing=3 border=0 width=100%>
    <tr>
	   <td>

			<table cellpadding=2 cellspacing=3 border=0>
				<tr>
					<td>Space:</td>
					<td>
						<select name="store">
<?php
						foreach ($session->stores as $store)
						{	
?>
							<option 
							    <?php if ($store->__toString() == $currentStore->__toString()) { echo "selected"; } ?>
								value="<?php echo $store->__toString() ?>">
									<?php echo $store->__toString() ?>
							</option>
<?php
						}
?>					
						</select>
					</td>
				</tr>
				<tr>
					<td>Query:</td>
					<td><input 
							name="statement" 
							type="textbox" 
							style="width: 300" 
							value='<?php if ($statement != null) { echo $statement; } ?>'/></td>
				<tr>
				<tr>
					<td></td>
					<td align=right>
						<input type='submit' vlaue='Execute'/>
					</td>
				</tr>
			</table>
		</td>
	</tr>
<?php
	if ($nodes != null)
	{
?>	
	<tr>
		<td><hr></td>
	</tr>
	<tr>
		<td>
		
			<table cellspacing=2 cellpadding=3 border=1>
				<tr>
					<td><b>Id</b></td>
					<td><b>Type</b></td>
					<td><b>Name</b></td>
				</tr>
<?php
				foreach ($nodes as $node)
				{
?>
				<tr>
					<td><?php echo $node->id; ?></td>
					<td><?php echo $node->type; ?></td>
					<td><?php echo $node->cm_name; ?></td>
				</tr>	
<?php
				}
?>				
				
				
			</table>
		
		</td>
	</tr>
<?php
	}
	else if ($statement != null)
	{
?>
	<tr>
		<td><hr></td>
	</tr>
	<tr>
		<td>No results found</td>
	</tr>
<?php		
	}
?>
</table>

</form>

</body>
</html>