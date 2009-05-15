/**
 * File Record Action
 *
 * @actionedUponNode 		the record we are filing
 *
 * @param recordsFolder  	the record folder we are filing the record into
 * @param recordProperties  the record properties we are going to set
 */

function main(document)
{
    var recordsFolder = action.parameters["recordFolder"];    
	var recordCategory = rmService.getRecordCategory(recordsFolder);

	// TODO .. do we need to check for the filable aspect??
	
	if (rmService.isRecord(document) == true)
	{			
		// TODO .. for now throw an exception
		throw "This is aleady a record";
	}
						
	// Make the document a record
	record = rmService.makeRecord(document);
	
	// Calculated properties
	var filedDate = new Date();
	var recordId = filedDate.getFullYear() + "-" + utils.pad(document.properties["sys:node-dbid"].toString(), 10);
	record.properties[rmService.PROP_IDENTIFIER] = recordId;
	record.properties[rmService.PROP_DATE_FILED] = filedDate;
	record.save();
	
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
	
	// Calculate a cut off schedule for the record
	var asOfDate = recordCategory.getCutOffDate(filedDate);
	if (asOfDate != null)
	{
		record.addAspect(rmService.ASPECT_PENDING_CUT_OFF);
		record.properties[rmService.PROP_CUT_OFF_AS_OF] = asOfDate;
		record.save();
	}
	
	// Calculate the review schedule
	asOfDate = recordCategory.getNextReviewDate(filedDate);
	if (asOfDate != null)
	{
		record.addAspect(rmService.ASPECT_PENDING_REVIEW);
		record.properties[rmService.PROP_REVIEW_AS_OF] = asOfDate;
		record.save();
	}

	// Remove the undeclared record aspect
	record.removeAspect(rmService.ASPECT_UNDECLARED_RECORD);
}

main(document);
