
Array.prototype.clear=function()
{
	this.length = 0;
};

function generateID()
{
	var d = new Date();
	return d.getTime();
}