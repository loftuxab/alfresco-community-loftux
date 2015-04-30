/**
 * Look up the current tenant/network for this request, either from the URL context or from the username 
 * of the current user if supplied
 *
 * This will change as the user 'switches' networks in the UI, if they have been invited into other networks.
 * 
 * @param username {string} Optional - if supplied the lookup will fall back to the domain part of the username 
 *    if the tenant could not be inferred from the URL context
 * @returns {string} The current network ID, e.g. alfresco.com
 */
function getRequestNetworkId(username)
{
   var currentTenantContext = url.context,
      tenantIndex = currentTenantContext.lastIndexOf('/'),
      currentTenant = tenantIndex != -1 ? currentTenantContext.substring(tenantIndex +1) : '';

   // For some pages where the user is unauthenticated we don't know the tenant
   // So instead we need to get it from the username, if this was supplied
   if ((currentTenant == "" || currentTenant == "-default-" || currentTenant == "-system-") && username)
   {
      currentTenant = getUserNetworkId(username) || currentTenant;
   }
   return currentTenant;
}

/**
 * Get the tenant of the specified username, i.e. the part after the '@' symbol if present.
 *
 * This always returns the 'home' network of the user, or nothing if it could not be inferred.
 *
 * @returns {string} the tenant ID if no '@' symbol is present then an empty string should be returned
 */
function getUserNetworkId(username)
{
   var domainIndex = username.lastIndexOf('@');
   if (domainIndex > 0)
   {
      return username.substring(domainIndex + 1);
   }
   else
   {
      return '';
   }
}

/**
 * Get the effective password policy for the specified network
 * 
 * @param networkId {string} Network ID, e.g. alfresco.com
 * @returns {object} Object containing the properties minLength, minCharsUpper, minCharsLower, minCharsNumeric and minCharsSymbols.
 */
function getPasswordPolicy(networkId)
{
   // Get the current tenant/network
   var userConfig = config.scoped['Users']['users'], 
      networksConfig = config.scoped['Users']['network-users'],
      policy = {
         // cannot use userConfig.getChildValue('password-min-length'), this returns the first definition and ignores overrides in cloud-config.xml
         minLength: 0,
         minCharsUpper: 0,
         minCharsLower: 0,
         minCharsNumeric: 0,
         minCharsSymbols: 0
      };

   // General user config defined by <users> section
   if (userConfig !== null)
   {
      // Default password minimum size
      if (userConfig.getChildren('password-min-length').size() > 0)
      {
         policy.minLength = 
            parseInt(userConfig.getChildren('password-min-length').get(userConfig.getChildren('password-min-length').size()-1).getValue(), 10);
      }
      // Default password content policies
      for (var i = 0; i < userConfig.getChildren('password-contains').size(); i++)
      {
         _setPasswordContentPolicies(userConfig.getChildren('password-contains').get(i), policy);
      }
   }

   // Tenant-specific password config defined by <network-users> section
   if (networksConfig !== null)
   {
      var networkConfigs = networksConfig.getChildren('network'), networkConfig, passwordConfig;
      for (var i = 0; i < networkConfigs.size(); i++)
      {
         networkConfig = networkConfigs.get(i);
         if (networkConfig.getAttribute('id').equals(networkId))
         {
            _setPasswordPolicies(networkConfig, policy);
         }
      }
   }

   return policy;
}

function _setPasswordPolicies(configEl, policy)
{
   policy.minLength = parseInt(configEl.getChildValue('password-min-length') || policy.minLength, 10);
   var passwordConfig = configEl.getChild('password-contains');
   if (passwordConfig !== null)
   {
      _setPasswordContentPolicies(passwordConfig, policy);
   }
   return policy;
}

function _setPasswordContentPolicies(configEl, policy)
{
   policy.minCharsUpper = parseInt(configEl.getChildValue('min-uppercase') || policy.minCharsUpper, 10);
   policy.minCharsLower = parseInt(configEl.getChildValue('min-lowercase') || policy.minCharsLower, 10);
   policy.minCharsNumeric = parseInt(configEl.getChildValue('min-numeric') || policy.minCharsNumeric, 10);
   policy.minCharsSymbols = parseInt(configEl.getChildValue('min-symbols') || policy.minCharsSymbols, 10);
   return policy;
}
