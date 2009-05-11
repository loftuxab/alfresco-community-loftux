function main(document)
{
	

    var fileableNode = action.parameters["fileableNode"];
	if (fileableNode.hasAspect(rmService.ASPECT_FILABLE) == true)
	{		
		// Inspect the filable node
		var recordCategory;
		var filedInFolder = false;
		// TODO chanage to use new dictionary method added to node
		if (fileableNode.type.endsWith("recordFolder") == true)
		{
			filedInFolder = true;
			recordCategory = fileable.parent;
		}
		else if (fileableNode.type.endsWith("recordCategory") == true)
		{
			recordCategory = fileableNode;
		}
		else
		{
		   throw "The filing type " + filable.type + " is currently unsupported.";
		}	
	
		// TODO Check whether the record is already a child of the filable node or not ..	
			// TODO if it is a child then the incomplete record aspect must be present
		
		var record = null;
		if (rmService.isRecord(document) == true)
		{			
			// Cast the node to a record object
			record = rmService.getRecord(document);
		
			// TODO we are filing the record for the second time ...
		}
		else
		{					
			// Make the document a record
			record = rmService.makeRecord(document);
			
			// Calculated properties
			record.properties[rmService.PROP_INDENTIFIER] = rmService.generateRecordId(recordCategory);
			record.properties[rmService.PROP_DATE_FILED] = new Date();
			
			// TODO add the extended meta-data aspects to the record
			
			// TODO set the property values
			var recordProperties = action.parameters["recordProperties"];
			for (propName in recordProperties)
			{
				record.properties[propName] = recordProperties[propName];
			}			
		}
		
		// Re-calculate the cut off schedule
		var filed = record.properties[rmService.PROP_DATE_FILED];
		var period = recordCategory.properties[rmService.PROP_CUT_OFF_SCHEDULE_PERIOD];
		var asOfDate = rmService.calculateAsOfDate(period, filed);
		if (asOfDate != null)
		{
			record.addAspect(rmService.ASPECT_PENDING_CUT_OFF);
			record.properties[rmService.PROP_CUT_OFF_AS_OF] = asOfDate;
		}
		
		// Re-calculate the review schedule
		period = recordCategory.properties[rmService.PROP_REVIEW_PERIOD];
		asOfDate = rmService.calculateAsOfDate(period, filed);
		if (asOfDate != null)
		{
			record.addAspect(rmService.ASPECT_PENDING_REVIEW);
			record.properties[rmService.PROP_REVIEW_AS_OF] = asOfDate;
		}
		
		// Remove the incomplete record aspect if it is marks as such
		if (record.hasAspect(rmService.ASPECT_INCOMPLETE_RECORD) == true)
		{
		   record.removeAspect(rmService.ASPECT_INCOMPLETE_RECORD);
		}
		
		// Save the records properties
		record.save();
	}
	else
	{
	   throw "Can not file a record in a node that is not fileable.";
	}
}

main(document);
