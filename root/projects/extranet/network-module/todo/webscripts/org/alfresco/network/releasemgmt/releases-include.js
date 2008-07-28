<import resource="/Company Home/Data Dictionary/Web Scripts Extensions/ReleaseManagement/category-include.js">

// statics
model.releasesFolder = getReleasesFolder();
model.publishedFolder = getPublishedFolder();
model.rootCategory = getReleasesRootCategory();
model.rootCategories = getRootCategories();

// arrays
model.versions = getReleaseVersionArray();
model.platforms = getReleasePlatformArray();
model.descriptors = getReleaseFileDescriptorArray();
model.families = getReleaseFamilyArray();
model.assetTypes = getReleaseAssetTypeArray();
model.productClasses = getReleaseClassArray();



function getReleasesRootCategory()
{
	return getRootCategory("Releases");
}

function getReleasesFolder()
{
	return companyhome.childByNamePath("Network/Releases");
}

function getPublishedFolder()
{
	return getReleasesFolder().childByNamePath("Published");
}

function getReleaseVersionArray()
{
	return getCategoryArray(model.rootCategory, "ReleaseVersion");
}

function getReleasePlatformArray()
{
	return getCategoryArray(model.rootCategory, "ReleasePlatform");
}

function getReleaseFileDescriptorArray()
{
	return getCategoryArray(model.rootCategory, "ReleaseFileDescriptor");
}

function getReleaseFamilyArray()
{
	return getCategoryArray(model.rootCategory, "ReleaseFamily");
}

function getReleaseAssetTypeArray()
{
	return getCategoryArray(model.rootCategory, "ReleaseAssetType");
}

function getReleaseClassArray()
{
	return getCategoryArray(model.rootCategory, "ReleaseClass");
}


function hasVersion(file, version)
{
	return hasCategory(file, model.rootCategory, getVersion(model.rootCategory, version));
}

function hasPlatform(file, platform)
{
	return hasCategory(file, model.rootCategory, getPlatform(model.rootCategory, platform));
}

function hasFamily(file, family)
{
	return hasCategory(file, model.rootCategory, getFamily(model.rootCategory, family));
}

function hasAssetType(file, assetType)
{
	return hasCategory(file, model.rootCategory, getAssetType(model.rootCategory, assetType));
}

function hasDescriptor(file, descriptor)
{
	return hasCategory(file, model.rootCategory, getDescriptor(model.rootCategory, descriptor));
}

function hasProductClass(file, productClass)
{
	return hasCategory(file, model.rootCategory, getProductClass(model.rootCategory, productClass));
}



function getVersion(rootCategory, version)
{
	return getCategory(rootCategory, "ReleaseVersion/" + version);
}

function getPlatform(rootCategory, platform)
{
	return getCategory(rootCategory, "ReleasePlatform/" + platform);
}

function getFamily(rootCategory, family)
{
	return getCategory(rootCategory, "ReleaseFamily/" + family);
}

function getAssetType(rootCategory, assetType)
{
	return getCategory(rootCategory, "ReleaseAssetType/" + assetType);
}

function getDescriptor(rootCategory, descriptor)
{
	return getCategory(rootCategory, "ReleaseFileDescriptor/" + descriptor);
}

function getProductClass(rootCategory, productClass)
{
	return getCategory(rootCategory, "ReleaseClass/" + productClass);
}