package org.alfresco.repo.index.shard;


/**
 * @author Andy
 *
 */
public enum ShardMethodEnum
{
    MOD_ACL_ID, UNKOWN;
    
    public static ShardMethodEnum getShardMethod(String shardMethod)
    {
        ShardMethodEnum shardMethodEnum;

        if(null == shardMethod)
        {
            shardMethodEnum = ShardMethodEnum.UNKOWN;
        }
        else
        {
            if (shardMethod.equalsIgnoreCase("MOD_ACL_ID"))
            {
                shardMethodEnum = ShardMethodEnum.MOD_ACL_ID;
            }
            else
            {
                shardMethodEnum = ShardMethodEnum.UNKOWN;
            }
        }
        return shardMethodEnum;
    }
}
