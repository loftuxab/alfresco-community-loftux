function doCutOff(recordCategory, record, cutOffDate)
{
	// Apply cut off aspect
	record.addAspect(rmService.ASPECT_CUT_OFF);
			
	// Set the cut off date
	record.properties[rmService.PROP_CUT_OFF_DATE] = cutOffDate;
	
	// TODO Calculate any disposition date's required ...	
}

function main(record)
{
	if (record.hasAspect(rmService.ASPECT_CUT_OFF) == false)
	{
		// Confirm we have a record (is it a record folder?)
		if ((rmService.isRecord(record) == true) ||
		    (rmService.isRecordFolder(record) == true))
		{
			// Get the record category
			var recordCategory = rmService.getRecordCategory(record);
		
			// Cut off
			var cutOffDate = new Date();
			doCutOff(recordCategory, record, cutOffDate);
			
			// Save record					
			record.save();
			
			// Cut off children of a record folder
			if (rmService.isRecordFolder(record) == true)
			{
				var recordFolder = rmService.getRecordFolder(record);
				for (childRecord in recordFolder.records)
				{
					doCutOff(recordCategory, childRecord, cutOffDate);
				}
			}
				
			// Check category for retention
			if (recordCategory.hasRetention == true)
			{
			  	// Retain 
			  	rmService.executeRecordAction(record, "retain");
			}			  			
		}
		else
		{
			throw "Can only cut off a record or a record folder";
		}
	}
}
  	
main(document);