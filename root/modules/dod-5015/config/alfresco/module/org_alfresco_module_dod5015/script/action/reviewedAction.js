function main(record)
{
    if (record.hasAspect(rmService.ASPECT_PENDING_REVIEW) == true)
    {
        // Get the record category
        var recordCategory = rmService.getRecordCategory(record);
        
        // Calculate the review schedule
        asOfDate = recordCategory.getNextReviewDate(new Date());
        if (asOfDate != null)
        {
            record.properties[rmService.PROP_REVIEW_AS_OF] = asOfDate;
            record.save();
        }
        
        // TODO .. should we mark all the children as reviewed?
    }
}
    
main(document);