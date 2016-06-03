package org.alfresco.solr.tracker;


abstract class AbstractWorkerRunnable implements Runnable
{
    QueueHandler queueHandler;
    
    public AbstractWorkerRunnable(QueueHandler qh)
    {
        this.queueHandler = qh;
    }
    
    /*
     * (non-Javadoc)
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run()
    {
        try
        {
            doWork();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            // Triple check that we get the queue state right
            queueHandler.removeFromQueueAndProdHead(this);
        }
    }
    
    abstract protected void doWork() throws Exception;
}
