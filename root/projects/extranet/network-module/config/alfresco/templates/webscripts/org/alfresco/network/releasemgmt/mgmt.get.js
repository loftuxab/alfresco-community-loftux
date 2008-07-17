var platformContent = { };
model.platformContent = platformContent;
var descriptorContent = { };
model.descriptorContent = descriptorContent;
var familyContent = { };
model.familyContent = familyContent;
var typeContent = { };
model.typeContent = typeContent;
var classContent = { };
model.classContent = classContent;

function sortVersions(file, bucket)
{
	if(file.isDocument)
	{
		var sorted = false;
		
		var categories = file.properties["{http://www.alfresco.org/model/content/1.0}categories"];
		if(categories != null)
		{
			for(var i = 0; i < categories.length; i++)
			{
				if(categories[i].parent.name == "ReleaseVersion")
				{
					var files = bucket[categories[i].name];
					if(files == null)
					{
						files = new Array();
						bucket[categories[i].name] = files;
					}
					files[files.length] = file;
					sorted = true;
				}
				if(categories[i].parent.name == "ReleasePlatform")
				{
					platformContent[file.nodeRef] = categories[i].name;
				}
				if(categories[i].parent.name == "ReleaseFileDescriptor")
				{
					descriptorContent[file.nodeRef] = categories[i].name;
				}
				if(categories[i].parent.name == "ReleaseFamily")
				{
					familyContent[file.nodeRef] = categories[i].name;
				}
				if(categories[i].parent.name == "ReleaseAssetType")
				{
					typeContent[file.nodeRef] = categories[i].name;
				}
				if(categories[i].parent.name == "ReleaseClass")
				{
					classContent[file.nodeRef] = categories[i].name;
				}
			}
		}
		
		if(!sorted)
		{
			var files = bucket["unknown"];
			if(files == null)
			{
				files = new Array();
				bucket["unknown"] = files;
			}
			files[files.length] = file;
		}
	}
	else
	{
		for(var i = 0; i < file.children.length; i++)
		{
			sortVersions(file.children[i], bucket);
		}
	}
}


// get the "Releases" folder
var categorizedContent = { };
var releasesFolder = companyhome.childByNamePath("Releases");

// walk all content in this folder and sort it into version categories
sortVersions(releasesFolder, categorizedContent);

// set into model
model.contents = categorizedContent;




// gather root categories
var rootCategories = classification.getRootCategories("cm:generalclassifiable");

// find the releases root category
var rootCategory = null;
for(var i = 0; i < rootCategories.length; i++)
{
	if(rootCategories[i].name == "Releases")
		rootCategory = rootCategories[i];
}
model.rootCategory = rootCategory;


// build a map of the versions
var versions = new Array();
var versionsCategory = rootCategory.childByNamePath("ReleaseVersion");
for(var i = 0; i < versionsCategory.children.length; i++)
{
	versions[versions.length] = versionsCategory.children[i];
}
model.versions = versions;


// build a map of the platforms
var platforms = new Array();
var platformsCategory = rootCategory.childByNamePath("ReleasePlatform");
for(var i = 0; i < platformsCategory.children.length; i++)
{
	platforms[platforms.length] = platformsCategory.children[i];
}
model.platforms = platforms;


// build a map of the descriptors
var descriptors = new Array();
var descriptorsCategory = rootCategory.childByNamePath("ReleaseFileDescriptor");
for(var i = 0; i < descriptorsCategory.children.length; i++)
{
	descriptors[descriptors.length] = descriptorsCategory.children[i];
}
model.descriptors = descriptors;


// build a map of the families
var families = new Array();
var familiesCategory = rootCategory.childByNamePath("ReleaseFamily");
for(var i = 0; i < familiesCategory.children.length; i++)
{
	families[families.length] = familiesCategory.children[i];
}
model.families = families;


// build a map of the asset types
var assetTypes = new Array();
var assetTypesCategory = rootCategory.childByNamePath("ReleaseAssetType");
for(var i = 0; i < assetTypesCategory.children.length; i++)
{
	assetTypes[assetTypes.length] = assetTypesCategory.children[i];
}
model.assetTypes = assetTypes;


// build a map of the product class
var productClasses = new Array();
var productClassCategory = rootCategory.childByNamePath("ReleaseClass");
for(var i = 0; i < productClassCategory.children.length; i++)
{
	productClasses[productClasses.length] = productClassCategory.children[i];
}
model.productClasses = productClasses;
