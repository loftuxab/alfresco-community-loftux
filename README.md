# Loftux AB - Alfresco Community edition
This is a Github fork of the official Alfresco Community edition. This fork will not contain any major feature additions, those will be released as separate addons.  
What you will find here is  
 * Bugfixes
 * Enhancement of existing features.  

The intention is to keep the code line as close as possible to the Alfresco official code line.  

## Branches and build
The master branch is always a mirror to the Alfresco Gtihub master branch.  
The loftux branch is the one we commit our changes to, and this is the branch you should check out and build against.  

     git checkout loftux  
     ant -f loftux.xml distribute

## Issues
We use the issue tracker to record the changes we have made to code. The changes we make are on behalf of Loftux AB customers. If you are not a Loftux AB customer, please file issues only if you are sure that the issue is due to a change that was made by us. All other issues should be filed in the official Alfresco issue tracker https://issues.alfresco.com. Before you do that, make sure that the issue can be reproduced by an Alfresco official build. 
We will be somewhat restrictive with accepting issues from non-customers, since we do not have the resources to research the issues and fix them. But if you already have a patch or a proposed fix, then we will try to incorporate them.

## Should I use this source?
A build from this source is not guaranteed to be more or less stable. That may vary from time to time. So if you want to use a build from this source, make sure that you understand the consequences and that you may have to upgrade more frequently. We do not have resources for extensive testing.  
That said, at Loftux AB we use builds that are from source that are not from an official release revision and with good results. Have an understanding though that our clients are mostly smaller companies that can allow for some downtime in order to upgrade should that be needed.  

## Can I switch back to Alfresco official?
Yes, we will not make modifications that will change compatiblity. Just make sure you use the same schema version. If you use the latest source here, use the same from Alfresco.

# About Loftux AB
Loftux AB, http://loftux.se http://loftux.com is a company based in Sweden that delivers implementations and addons to Alfresco Community Edition. Please visit our website should you want seek out the opportunity to use our services.