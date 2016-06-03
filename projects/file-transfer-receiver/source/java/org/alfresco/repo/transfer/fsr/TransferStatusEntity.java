package org.alfresco.repo.transfer.fsr;

import java.io.Serializable;

public class TransferStatusEntity
{
    private Long id;
    private String transferId;
    private Integer currentPos;
    private Integer endPos;
    private Serializable error;
    private String status;

    public TransferStatusEntity()
    {
    }

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public String getTransferId()
    {
        return transferId;
    }

    public void setTransferId(String transferId)
    {
        this.transferId = transferId;
    }

    public Integer getCurrentPos()
    {
        return currentPos;
    }

    public void setCurrentPos(Integer currentPos)
    {
        this.currentPos = currentPos;
    }

    public Integer getEndPos()
    {
        return endPos;
    }

    public void setEndPos(Integer endPos)
    {
        this.endPos = endPos;
    }

    public Serializable getError()
    {
        return error;
    }

    public void setError(Serializable error)
    {
        this.error = error;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }
}
