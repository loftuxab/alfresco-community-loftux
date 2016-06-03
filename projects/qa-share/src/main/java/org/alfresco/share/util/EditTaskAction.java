package org.alfresco.share.util;

/**
 * This enums used to describe the Accept and Reject workflow.
 * 
 * @author cbairaajoni
 */
public enum EditTaskAction
{
    APPROVE("Approve"), 
    REJECT("Reject"),
    TASK_DONE("Task Done"),
    SAVE("Save"),
    CANCEL("Cancel");

    private String action;

    private EditTaskAction(String action)
    {
        this.action = action;
    }

    public String getAction()
    {
        return action;
    }

}