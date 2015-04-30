package org.alfresco.enterprise.repo.sync;

import java.util.Date;

/**
 * 
 * Implementation of ConflictResponse
 *
 */
public class ConflictResponseImpl implements ConflictResponse
{
    
    private Resolution resolution = Resolution.NOT_RESOLVED;
    private Date timeOfConflict;
    private Date timeOfLastSuccessfulSync;
    private String versionLabelOfConflict;
    private String versionLabelOfLastSuccessfulSync;
    
    public ConflictResponseImpl()
    {
    }

    @Override
    public Date getTimeOfConflict()
    {
        return timeOfConflict;
    }
    
    public void setTimeOfConflict(Date datetime)
    {
        this.timeOfConflict = datetime;
    }

    @Override
    public String getVersionLabelOfConflict()
    {
        return versionLabelOfConflict;
    }

    @Override
    public Date getTimeOfLastSuccessfulSync()
    {
        return timeOfLastSuccessfulSync;
    }

    @Override
    public String getVersionLabelOfLastSuccessfulSync()
    {
        return versionLabelOfLastSuccessfulSync;
    }

    @Override
    public Resolution getResolution()
    {
        return resolution;
    }
    
    public void setResolution(Resolution resolution)
    {
        this.resolution = resolution;
    }
    
    public void setTimeOfLastSuccessfulSync(Date timeOfLastSuccessfulSync)
    {
        this.timeOfLastSuccessfulSync = timeOfLastSuccessfulSync;
    }
    
    public void setVersionLabelOfConflict(String versionLabelOfConflict)
    {
        this.versionLabelOfConflict = versionLabelOfConflict;
    }

    public void setVersionLabelOfLastSuccessfulSync(
            String versionLabelOfLastSuccessfulSync)
    {
        this.versionLabelOfLastSuccessfulSync = versionLabelOfLastSuccessfulSync;
    }
}
