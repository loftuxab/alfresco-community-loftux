package org.alfresco.repo.content;

import java.util.Date;

import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.util.BaseSpringTest;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.calendar.BaseCalendar;
import org.quartz.spi.TriggerFiredBundle;
import org.springframework.scheduling.quartz.SimpleTriggerBean;

/**
 * Content store cleanup job unit test
 * 
 * @author Roy Wetherall
 */
public class ContentStoreCleanupJobTest extends BaseSpringTest
{
    private SimpleTriggerBean simpleTriggerBean;
    private JobExecutionContext jobExecutionContext;
    private ContentStoreCleanupJob job;
    
    private ContentStore contentStore;
    private String url;
    
    /**
     * On setup in transaction
     */
    @Override
    protected void onSetUpInTransaction() throws Exception
    {
        this.contentStore = (ContentStore)this.applicationContext.getBean("fileContentStore");
        this.simpleTriggerBean = (SimpleTriggerBean)this.applicationContext.getBean("fileContentStoreCleanerTrigger");
        
        SchedulerFactory factory = new StdSchedulerFactory();
        Scheduler scheduler = factory.getScheduler();      
        
        // Set the protect hours to 0 for the purpose of this test
        JobDataMap jobDataMap = this.simpleTriggerBean.getJobDetail().getJobDataMap();
        jobDataMap.put("protectHours", "0");
        this.simpleTriggerBean.getJobDetail().setJobDataMap(jobDataMap);
        
        this.job = new ContentStoreCleanupJob();
        TriggerFiredBundle triggerFiredBundle = new TriggerFiredBundle(
                this.simpleTriggerBean.getJobDetail(),
                this.simpleTriggerBean,
                new BaseCalendar(),
                false,
                new Date(),
                new Date(),
                new Date(),
                new Date());
        
        this.jobExecutionContext = new JobExecutionContext(scheduler, triggerFiredBundle, job);
        
        ContentWriter contentWriter = this.contentStore.getWriter();
        contentWriter.putContent("This is some content that I am going to delete.");
        this.url = contentWriter.getContentUrl();
    }
    
    /**
     * Test execute method
     */
    public void testExecute()
    {
        try
        {            
            ContentReader before = this.contentStore.getReader(this.url);
            assertTrue(before.exists());
            
            this.job.execute(this.jobExecutionContext);
            
            ContentReader after = this.contentStore.getReader(this.url);
            assertFalse(after.exists());
        }
        catch (JobExecutionException exception)
        {
            fail("Exception raised!");
        }
    }
}
