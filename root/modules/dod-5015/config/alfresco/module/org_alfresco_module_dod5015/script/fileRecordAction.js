/**
 * File Record Action
 *
 * @actionedUponNode 		the record we are filing
 *
 * @param fileableNode  	the node we are filing the record into
 * @param recordProperties  the record properties we are going to set
 */

function main(document)
{
    var fileableNode = action.parameters["fileableNode"];
	if (fileableNode.hasAspect(rmService.ASPECT_FILABLE) == true)
	{		
	    var recordCategory = rmService.getRecordCategory(fileableNode);
	
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
			record.properties[rmService.PROP_IDENTIFIER] = recordCategory.nextRecordId;
			record.properties[rmService.PROP_DATE_FILED] = new Date();
			
			// Get hte record properties
			var recordProperties = action.parameters["recordProperties"];
			
			// Add any custom aspects that are implied by the provided properties
			var customAspects = rmService.getCustomRMAspects(recordProperties);
			for (customAspect in customAspects)
			{
				record.addAspect(customAspect);
			}
			
			// Set the property values
			for (propName in recordProperties)
			{
				record.properties[propName] = recordProperties[propName];
			}		
			record.save();	
		}
		
		var filed = record.properties[rmService.PROP_DATE_FILED];
		// Only calculate a cut off schedule for a record filed directly in a category
		if (record.isFiledInFolder() == false)
		{
			// Re-calculate the cut off schedule
			var asOfDate = recordCategory.getCutOffDate(filed);
			if (asOfDate != null)
			{
				record.addAspect(rmService.ASPECT_PENDING_CUT_OFF);
				record.properties[rmService.PROP_CUT_OFF_AS_OF] = asOfDate;
				record.save();
			}
		}
		
		// Re-calculate the review schedule
		var asOfDate2 = recordCategory.getNextReviewDate(filed);
		if (asOfDate2 != null)
		{
			record.addAspect(rmService.ASPECT_PENDING_REVIEW);
			record.properties[rmService.PROP_REVIEW_AS_OF] = asOfDate2;
			record.save();
		}

		// Remove the incomplete record aspect if it's present
		if (record.hasAspect(rmService.ASPECT_INCOMPLETE_RECORD) == true)
		{
		   record.removeAspect(rmService.ASPECT_INCOMPLETE_RECORD);
		}
	}
	else
	{
	   throw "Can not file a record in a node that is not fileable.";
	}
}

main(document);
