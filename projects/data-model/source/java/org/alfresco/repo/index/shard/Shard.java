package org.alfresco.repo.index.shard;

import java.io.Serializable;

/**
 * @author Andy
 *
 */
public class Shard implements Serializable
{
    private static final long serialVersionUID = -7255962796619754211L;

    private Floc floc;
    
    private int instance;

    public Shard()
    {
        
    }
    
    /**
     * @return the floc
     */
    public Floc getFloc()
    {
        return floc;
    }

    /**
     * @param floc the floc to set
     */
    public void setFloc(Floc floc)
    {
        this.floc = floc;
    }

    /**
     * @return the instance
     */
    public int getInstance()
    {
        return instance;
    }

    /**
     * @param instance the instance to set
     */
    public void setInstance(int instance)
    {
        this.instance = instance;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((floc == null) ? 0 : floc.hashCode());
        result = prime * result + instance;
        return result;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Shard other = (Shard) obj;
        if (floc == null)
        {
            if (other.floc != null)
                return false;
        }
        else if (!floc.equals(other.floc))
            return false;
        if (instance != other.instance)
            return false;
        return true;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return "Shard [floc=" + floc + ", instance=" + instance + "]";
    }

   

}
