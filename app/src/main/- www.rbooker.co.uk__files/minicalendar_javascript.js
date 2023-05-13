function ResizeWindow()
{
	var aContainer = document.getElementById("container");  
	var aHtmlHeight = document.body.parentNode.scrollHeight;  
	var aWindowHeight = window.innerHeight; 

	aWindowHeight = aWindowHeight - 67;
	
	if (aHtmlHeight < aWindowHeight)
	{
		document.body.style.height = aWindowHeight + "px"; aContainer.style.height = aWindowHeight + "px";
	}
	else
	{
		document.body.style.height = aWindowHeight + "px"; aContainer.style.height = aWindowHeight + "px";
	}
}