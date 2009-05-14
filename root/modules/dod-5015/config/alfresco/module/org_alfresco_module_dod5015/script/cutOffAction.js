
fucntion main(record)
{
	if (record.hasAspect(rmService.ASPECT_CUT_OFF == false)
	{
		// Confirm we have a record (is it a record folder?)
		if ((rmService.isRecord(record) == true) ||
		    (rmService.isRecordFolder(record) == true))
		{
			// Get the record category
			var rc = rmService.getRecordCategory(record);
		
			// Apply cut off aspect
			record.addAspect(rmService.APSECT_CUT_OFF);
			
			// Set the cut off date
			var cutOffDate = new Date();
			record.properties[rmService.PROP_CUT_OFF_DATE] = cutOffDate;
			
			// TODO If record in category or is record folder
				// TODO Calculate destroy date
			
			// Save record					
			record.save();
				
			// Check category for retention
			  	// Retain
			  	
			// Cut off children of a record folder
		}
		else
		{
			throw "Can only cut off a record or a record folder";
		}
	}
}
  	
main(record);