function openUKIapp(product)
{
var NewWin;
url = getUKIappURL(product);


//alert("OpenUKIapp= "+url)

location.replace(url);
}


function getUKIappURL(product,ignorecookie)
{

 if (ignorecookie)
{
//alert("IGNORED");
  // Ignore the cookie and use the product as the tag
 tag = product;               
 }else{

     var cookieString = document.cookie;     
     cookieStrPos = cookieString.indexOf('UKIBanner=');
     newCookieStrPos = cookieString.indexOf('UKINewBanner=');
     productCookieStrPos = cookieString.indexOf('UKIProduct=')
     
  //   alert("THE COOKIE STRING IS"+cookieString)
     
     if (cookieStrPos !=-1 || newCookieStrPos !=-1) {
		//alert("ONE BANNER CODE EXISTS")
	
	if (newCookieStrPos !=-1) {
		    
		    newCookieString = cookieString.slice(newCookieStrPos +13);          
		    			if (newCookieString.indexOf(';') !=-1)
		    			{                
		    			cookieArray = newCookieString.split(";");
		    			newCookieString = cookieArray[0];
				}    
				
		productString = cookieString.slice(productCookieStrPos +11);
					if (productString.indexOf(';') !=-1)
					{                
					cookieArray = productString.split(";");
					productString = cookieArray[0];
				}    
		
		    //alert(newCookieString);
		    //alert(productString)
		    //alert("OF THE NEW FORMAT")
		    
		    if (product == productString) {
		    
		    return(newCookieString)
		    
		    } else {
		    
		    return getUKIappURL(product,true); 
		    
		    }
		    
	    } else
	
	
	if (cookieStrPos !=-1 && !newCookieStrPos !=-1) {
		//alert("OF THE OLD FORMAT")
		cookieString = cookieString.slice(cookieStrPos +10);          
			if (cookieString.indexOf(';') !=-1)
			{                
			cookieArray = cookieString.split(";");
			cookieString = cookieArray[0];
			}     

		tag = cookieString + "_" + product;
	     
	    } 
	     
	     
	}
	
     else
     
	{  
	//alert("NO BANNER EXISTS");
	     tag = product;
	}
}

//alert("HELP"+tag);


var part1 ='http://www.u-k-i.com/HS/servlet/Router/addtrack?Fd_AddTracker_Site=';
switch (tag)
    {    
      case "car":
	return('http://www.u-k-i.com/HS/servlet/Router/addtrack?Fd_AddTracker_Site=4866&Fd_AddTracker_Area=4502&Fd_AddTracker_Adcamp=4542&Fd_RCP=005701&Fd_PresentationId=virgin_motor&Fd_Tracking_Introducer=K');
       break;
       case "home":
	return('http://www.u-k-i.com/HS/servlet/Router/addtrack?Fd_AddTracker_Site=4866&Fd_AddTracker_Area=4507&Fd_AddTracker_Adcamp=4542&Fd_RCP=000293&Fd_PresentationId=virgin_home&Fd_Tracking_Introducer=K');
       break;
       case "pet":
	return('http://www.virginpetins.co.uk/aboutpet.aspx');
       break;
       case "pet-retrieve":
	return('http://www.virginpetins.co.uk/loadquote.aspx');
        break;
        case "travel":
	return('https://www.virgintravelins.co.uk/newquote.asp');
        break;
        case "travel-retrieve":
		return('https://www.virgintravelins.co.uk/retrievequote.asp');
        break;
     
	
case "home1_home":
	 return('http://www.u-k-i.com/HS/servlet/Router/addtrack?Fd_AddTracker_Site=4556&Fd_AddTracker_Area=4507&Fd_AddTracker_Adcamp=4505&Fd_RCP=000293&Fd_PresentationId=virgin_home');
break;


case "home2_home":
	 return('http://www.u-k-i.com/HS/servlet/Router/addtrack?Fd_AddTracker_Site=4557&Fd_AddTracker_Area=4507&Fd_AddTracker_Adcamp=4511&Fd_RCP=000293&Fd_PresentationId=virgin_home');
break;


case "home3_home":
	 return('http://www.u-k-i.com/HS/servlet/Router/addtrack?Fd_AddTracker_Site=4558&Fd_AddTracker_Area=4507&Fd_AddTracker_Adcamp=4505&Fd_RCP=000293&Fd_PresentationId=virgin_home');
break;


case "home4_home":
	 return('http://www.u-k-i.com/HS/servlet/Router/addtrack?Fd_AddTracker_Site=4559&Fd_AddTracker_Area=4507&Fd_AddTracker_Adcamp=4505&Fd_RCP=000293&Fd_PresentationId=virgin_home');
break;


case "home5_home":
	 return('http://www.u-k-i.com/HS/servlet/Router/addtrack?Fd_AddTracker_Site=4500&Fd_AddTracker_Area=4507&Fd_AddTracker_Adcamp=4511&Fd_RCP=000293&Fd_PresentationId=virgin_home');
break;


case "home6_home":
	 return('http://www.u-k-i.com/HS/servlet/Router/addtrack?Fd_AddTracker_Site=4560&Fd_AddTracker_Area=4507&Fd_AddTracker_Adcamp=4511&Fd_RCP=000293&Fd_PresentationId=virgin_home');
break;


case "home7_home":
	 return('http://www.u-k-i.com/HS/servlet/Router/addtrack?Fd_AddTracker_Site=4501&Fd_AddTracker_Area=4507&Fd_AddTracker_Adcamp=4511&Fd_RCP=000293&Fd_PresentationId=virgin_home');
break;


case "home8_home":
	 return('http://www.u-k-i.com/HS/servlet/Router/addtrack?Fd_AddTracker_Site=4556&Fd_AddTracker_Area=4507&Fd_AddTracker_Adcamp=4504&Fd_RCP=000293&Fd_PresentationId=virgin_home');
break;


case "home9_home":
	 return('http://www.u-k-i.com/HS/servlet/Router/addtrack?Fd_AddTracker_Site=4556&Fd_AddTracker_Area=4507&Fd_AddTracker_Adcamp=4507&Fd_RCP=000293&Fd_PresentationId=virgin_home');
break;


case "home10_home":
	 return('http://www.u-k-i.com/HS/servlet/Router/addtrack?Fd_AddTracker_Site=4556&Fd_AddTracker_Area=4507&Fd_AddTracker_Adcamp=4510&Fd_RCP=000293&Fd_PresentationId=virgin_home');
break;


case "home11_home":
	 return('http://www.u-k-i.com/HS/servlet/Router/addtrack?Fd_AddTracker_Site=4556&Fd_AddTracker_Area=4507&Fd_AddTracker_Adcamp=4503&Fd_RCP=000293&Fd_PresentationId=virgin_home');
break;


case "home12_home":
	 return('http://www.u-k-i.com/HS/servlet/Router/addtrack?Fd_AddTracker_Site=4558&Fd_AddTracker_Area=4507&Fd_AddTracker_Adcamp=4504&Fd_RCP=000293&Fd_PresentationId=virgin_home');
break;


case "home13_home":
	 return('http://www.u-k-i.com/HS/servlet/Router/addtrack?Fd_AddTracker_Site=4558&Fd_AddTracker_Area=4507&Fd_AddTracker_Adcamp=4507&Fd_RCP=000293&Fd_PresentationId=virgin_home');
break;


case "home14_home":
	 return('http://www.u-k-i.com/HS/servlet/Router/addtrack?Fd_AddTracker_Site=4558&Fd_AddTracker_Area=4507&Fd_AddTracker_Adcamp=4512&Fd_RCP=000293&Fd_PresentationId=virgin_home');
break;


case "home15_home":
	 return('http://www.u-k-i.com/HS/servlet/Router/addtrack?Fd_AddTracker_Site=4558&Fd_AddTracker_Area=4507&Fd_AddTracker_Adcamp=4503&Fd_RCP=000293&Fd_PresentationId=virgin_home');
break;


case "home16_home":
	 return('http://www.u-k-i.com/HS/servlet/Router/addtrack?Fd_AddTracker_Site=4559&Fd_AddTracker_Area=4507&Fd_AddTracker_Adcamp=4504&Fd_RCP=000293&Fd_PresentationId=virgin_home');
break;


case "home17_home":
	 return('http://www.u-k-i.com/HS/servlet/Router/addtrack?Fd_AddTracker_Site=4559&Fd_AddTracker_Area=4507&Fd_AddTracker_Adcamp=4513&Fd_RCP=000293&Fd_PresentationId=virgin_home');
break;


case "home18_home":
	 return('http://www.u-k-i.com/HS/servlet/Router/addtrack?Fd_AddTracker_Site=4559&Fd_AddTracker_Area=4507&Fd_AddTracker_Adcamp=4514&Fd_RCP=000293&Fd_PresentationId=virgin_home');
break;


case "home19_home":
	 return('http://www.u-k-i.com/HS/servlet/Router/addtrack?Fd_AddTracker_Site=4562&Fd_AddTracker_Area=4507&Fd_AddTracker_Adcamp=4503&Fd_RCP=000293&Fd_PresentationId=virgin_home');
break;


case "home20_home":
	 return('http://www.u-k-i.com/HS/servlet/Router/addtrack?Fd_AddTracker_Site=4565&Fd_AddTracker_Area=4507&Fd_AddTracker_Adcamp=4517&Fd_RCP=000293&Fd_PresentationId=virgin_home');
break;


case "home21_home":
	 return('http://www.u-k-i.com/HS/servlet/Router/addtrack?Fd_AddTracker_Site=4572&Fd_AddTracker_Area=4507&Fd_AddTracker_Adcamp=4520&Fd_RCP=000293&Fd_PresentationId=virgin_home');
break;


case "home22_home":
	 return('http://www.u-k-i.com/HS/servlet/Router/addtrack?Fd_AddTracker_Site=4575&Fd_AddTracker_Area=4507&Fd_AddTracker_Adcamp=4520&Fd_RCP=000293&Fd_PresentationId=virgin_home');
break;


case "home23_home":
	 return('http://www.u-k-i.com/HS/servlet/Router/addtrack?Fd_AddTracker_Site=4576&Fd_AddTracker_Area=4507&Fd_AddTracker_Adcamp=4520&Fd_RCP=000293&Fd_PresentationId=virgin_home');
break;


case "home24_home":
	 return('http://www.u-k-i.com/HS/servlet/Router/addtrack?Fd_AddTracker_Site=4577&Fd_AddTracker_Area=4507&Fd_AddTracker_Adcamp=4520&Fd_RCP=000293&Fd_PresentationId=virgin_home');
break;


case "home25_home":
	 return('http://www.u-k-i.com/HS/servlet/Router/addtrack?Fd_AddTracker_Site=4578&Fd_AddTracker_Area=4507&Fd_AddTracker_Adcamp=4520&Fd_RCP=000293&Fd_PresentationId=virgin_home');
break;case "home26_home":
	 return('http://www.u-k-i.com/HS/servlet/Router/addtrack?Fd_AddTracker_Site=4579&Fd_AddTracker_Area=4507&Fd_AddTracker_Adcamp=4520&Fd_RCP=000293&Fd_PresentationId=virgin_home');
break;


case "home27_home":
	 return('http://www.u-k-i.com/HS/servlet/Router/addtrack?Fd_AddTracker_Site=4580&Fd_AddTracker_Area=4507&Fd_AddTracker_Adcamp=4520&Fd_RCP=000293&Fd_PresentationId=virgin_home');
break;


case "home28_home":
	 return('http://www.u-k-i.com/HS/servlet/Router/addtrack?Fd_AddTracker_Site=4581&Fd_AddTracker_Area=4507&Fd_AddTracker_Adcamp=4520&Fd_RCP=000293&Fd_PresentationId=virgin_home');
break;


case "home29_home":
	 return('http://www.u-k-i.com/HS/servlet/Router/addtrack?Fd_AddTracker_Site=4582&Fd_AddTracker_Area=4507&Fd_AddTracker_Adcamp=4520&Fd_RCP=000293&Fd_PresentationId=virgin_home');
break;


case "home30_home":
	 return('http://www.u-k-i.com/HS/servlet/Router/addtrack?Fd_AddTracker_Site=4583&Fd_AddTracker_Area=4507&Fd_AddTracker_Adcamp=4520&Fd_RCP=000293&Fd_PresentationId=virgin_home');
break;


case "home31_home":
	 return('http://www.u-k-i.com/HS/servlet/Router/addtrack?Fd_AddTracker_Site=4586&Fd_AddTracker_Area=4507&Fd_AddTracker_Adcamp=4520&Fd_RCP=000293&Fd_PresentationId=virgin_home');
break;


case "home32_home":
	 return('http://www.u-k-i.com/HS/servlet/Router/addtrack?Fd_AddTracker_Site=4587&Fd_AddTracker_Area=4507&Fd_AddTracker_Adcamp=4520&Fd_RCP=000293&Fd_PresentationId=virgin_home');
break;


case "home33_home":
	 return('http://www.u-k-i.com/HS/servlet/Router/addtrack?Fd_AddTracker_Site=4588&Fd_AddTracker_Area=4507&Fd_AddTracker_Adcamp=4520&Fd_RCP=000293&Fd_PresentationId=virgin_home');
break;


case "home34_home":
	 return('http://www.u-k-i.com/HS/servlet/Router/addtrack?Fd_AddTracker_Site=4589&Fd_AddTracker_Area=4507&Fd_AddTracker_Adcamp=4520&Fd_RCP=000293&Fd_PresentationId=virgin_home');
break;


case "home35_home":
	 return('http://www.u-k-i.com/HS/servlet/Router/addtrack?Fd_AddTracker_Site=4590&Fd_AddTracker_Area=4507&Fd_AddTracker_Adcamp=4520&Fd_RCP=000293&Fd_PresentationId=virgin_home');
break;


case "home36_home":
	 return('http://www.u-k-i.com/HS/servlet/Router/addtrack?Fd_AddTracker_Site=4591&Fd_AddTracker_Area=4507&Fd_AddTracker_Adcamp=4520&Fd_RCP=000293&Fd_PresentationId=virgin_home');
break;


case "home37_home":
	 return('http://www.u-k-i.com/HS/servlet/Router/addtrack?Fd_AddTracker_Site=4642&Fd_AddTracker_Area=4507&Fd_AddTracker_Adcamp=4520&Fd_RCP=000293&Fd_PresentationId=virgin_home');
break;


case "home38_home":
	 return('http://www.u-k-i.com/HS/servlet/Router/addtrack?Fd_AddTracker_Site=4592&Fd_AddTracker_Area=4507&Fd_AddTracker_Adcamp=4520&Fd_RCP=000293&Fd_PresentationId=virgin_home');
break;


case "home39_home":
	 return('http://www.u-k-i.com/HS/servlet/Router/addtrack?Fd_AddTracker_Site=4593&Fd_AddTracker_Area=4507&Fd_AddTracker_Adcamp=4520&Fd_RCP=000293&Fd_PresentationId=virgin_home');
break;


case "home40_home":
	 return('http://www.u-k-i.com/HS/servlet/Router/addtrack?Fd_AddTracker_Site=4594&Fd_AddTracker_Area=4507&Fd_AddTracker_Adcamp=4520&Fd_RCP=000293&Fd_PresentationId=virgin_home');
break;


case "home41_home":
	 return('http://www.u-k-i.com/HS/servlet/Router/addtrack?Fd_AddTracker_Site=4596&Fd_AddTracker_Area=4507&Fd_AddTracker_Adcamp=4520&Fd_RCP=000293&Fd_PresentationId=virgin_home');
break;


case "home42_home":
	 return('http://www.u-k-i.com/HS/servlet/Router/addtrack?Fd_AddTracker_Site=4597&Fd_AddTracker_Area=4507&Fd_AddTracker_Adcamp=4520&Fd_RCP=000293&Fd_PresentationId=virgin_home');
break;


case "home43_home":
	 return('http://www.u-k-i.com/HS/servlet/Router/addtrack?Fd_AddTracker_Site=4598&Fd_AddTracker_Area=4507&Fd_AddTracker_Adcamp=4520&Fd_RCP=000293&Fd_PresentationId=virgin_home');
break;


case "home44_home":
	 return('http://www.u-k-i.com/HS/servlet/Router/addtrack?Fd_AddTracker_Site=4643&Fd_AddTracker_Area=4507&Fd_AddTracker_Adcamp=4520&Fd_RCP=000293&Fd_PresentationId=virgin_home');
break;


case "home45_home":
	 return('http://www.u-k-i.com/HS/servlet/Router/addtrack?Fd_AddTracker_Site=4599&Fd_AddTracker_Area=4507&Fd_AddTracker_Adcamp=4520&Fd_RCP=000293&Fd_PresentationId=virgin_home');
break;


case "home46_home":
	 return('http://www.u-k-i.com/HS/servlet/Router/addtrack?Fd_AddTracker_Site=4644&Fd_AddTracker_Area=4507&Fd_AddTracker_Adcamp=4520&Fd_RCP=000293&Fd_PresentationId=virgin_home');
break;


case "home47_home":
	 return('http://www.u-k-i.com/HS/servlet/Router/addtrack?Fd_AddTracker_Site=4600&Fd_AddTracker_Area=4507&Fd_AddTracker_Adcamp=4520&Fd_RCP=000293&Fd_PresentationId=virgin_home');
break;


case "home48_home":
	 return('http://www.u-k-i.com/HS/servlet/Router/addtrack?Fd_AddTracker_Site=4601&Fd_AddTracker_Area=4507&Fd_AddTracker_Adcamp=4520&Fd_RCP=000293&Fd_PresentationId=virgin_home');
break;


case "home49_home":
	 return('http://www.u-k-i.com/HS/servlet/Router/addtrack?Fd_AddTracker_Site=4602&Fd_AddTracker_Area=4507&Fd_AddTracker_Adcamp=4520&Fd_RCP=000293&Fd_PresentationId=virgin_home');
break;


case "home50_home":
	 return('http://www.u-k-i.com/HS/servlet/Router/addtrack?Fd_AddTracker_Site=4645&Fd_AddTracker_Area=4507&Fd_AddTracker_Adcamp=4520&Fd_RCP=000293&Fd_PresentationId=virgin_home');
break;case "home51_home":
	 return('http://www.u-k-i.com/HS/servlet/Router/addtrack?Fd_AddTracker_Site=4603&Fd_AddTracker_Area=4507&Fd_AddTracker_Adcamp=4520&Fd_RCP=000293&Fd_PresentationId=virgin_home');
break;


case "home52_home":
	 return('http://www.u-k-i.com/HS/servlet/Router/addtrack?Fd_AddTracker_Site=4646&Fd_AddTracker_Area=4507&Fd_AddTracker_Adcamp=4520&Fd_RCP=000293&Fd_PresentationId=virgin_home');
break;


case "home53_home":
	 return('http://www.u-k-i.com/HS/servlet/Router/addtrack?Fd_AddTracker_Site=4604&Fd_AddTracker_Area=4507&Fd_AddTracker_Adcamp=4520&Fd_RCP=000293&Fd_PresentationId=virgin_home');
break;


case "home54_home":
	 return('http://www.u-k-i.com/HS/servlet/Router/addtrack?Fd_AddTracker_Site=4605&Fd_AddTracker_Area=4507&Fd_AddTracker_Adcamp=4520&Fd_RCP=000293&Fd_PresentationId=virgin_home');
break;


case "home55_home":
	 return('http://www.u-k-i.com/HS/servlet/Router/addtrack?Fd_AddTracker_Site=4647&Fd_AddTracker_Area=4507&Fd_AddTracker_Adcamp=4520&Fd_RCP=000293&Fd_PresentationId=virgin_home');
break;


case "home56_home":
	 return('http://www.u-k-i.com/HS/servlet/Router/addtrack?Fd_AddTracker_Site=4607&Fd_AddTracker_Area=4507&Fd_AddTracker_Adcamp=4520&Fd_RCP=000293&Fd_PresentationId=virgin_home');
break;


case "home57_home":
	 return('http://www.u-k-i.com/HS/servlet/Router/addtrack?Fd_AddTracker_Site=4608&Fd_AddTracker_Area=4507&Fd_AddTracker_Adcamp=4520&Fd_RCP=000293&Fd_PresentationId=virgin_home');
break;


case "home58_home":
	 return('http://www.u-k-i.com/HS/servlet/Router/addtrack?Fd_AddTracker_Site=4609&Fd_AddTracker_Area=4507&Fd_AddTracker_Adcamp=4520&Fd_RCP=000293&Fd_PresentationId=virgin_home');
break;


case "home59_home":
	 return('http://www.u-k-i.com/HS/servlet/Router/addtrack?Fd_AddTracker_Site=4610&Fd_AddTracker_Area=4507&Fd_AddTracker_Adcamp=4520&Fd_RCP=000293&Fd_PresentationId=virgin_home');
break;


case "home60_home":
	 return('http://www.u-k-i.com/HS/servlet/Router/addtrack?Fd_AddTracker_Site=4611&Fd_AddTracker_Area=4507&Fd_AddTracker_Adcamp=4520&Fd_RCP=000293&Fd_PresentationId=virgin_home');
break;


case "home61_home":
	 return('http://www.u-k-i.com/HS/servlet/Router/addtrack?Fd_AddTracker_Site=4612&Fd_AddTracker_Area=4507&Fd_AddTracker_Adcamp=4520&Fd_RCP=000293&Fd_PresentationId=virgin_home');
break;


case "home62_home":
	 return('http://www.u-k-i.com/HS/servlet/Router/addtrack?Fd_AddTracker_Site=4613&Fd_AddTracker_Area=4507&Fd_AddTracker_Adcamp=4520&Fd_RCP=000293&Fd_PresentationId=virgin_home');
break;


case "home63_home":
	 return('http://www.u-k-i.com/HS/servlet/Router/addtrack?Fd_AddTracker_Site=4614&Fd_AddTracker_Area=4507&Fd_AddTracker_Adcamp=4520&Fd_RCP=000293&Fd_PresentationId=virgin_home');
break;


case "home64_home":
	 return('http://www.u-k-i.com/HS/servlet/Router/addtrack?Fd_AddTracker_Site=4615&Fd_AddTracker_Area=4507&Fd_AddTracker_Adcamp=4520&Fd_RCP=000293&Fd_PresentationId=virgin_home');
break;


case "home65_home":
	 return('http://www.u-k-i.com/HS/servlet/Router/addtrack?Fd_AddTracker_Site=4648&Fd_AddTracker_Area=4507&Fd_AddTracker_Adcamp=4520&Fd_RCP=000293&Fd_PresentationId=virgin_home');
break;


case "home66_home":
	 return('http://www.u-k-i.com/HS/servlet/Router/addtrack?Fd_AddTracker_Site=4617&Fd_AddTracker_Area=4507&Fd_AddTracker_Adcamp=4520&Fd_RCP=000293&Fd_PresentationId=virgin_home');
break;


case "home67_home":
	 return('http://www.u-k-i.com/HS/servlet/Router/addtrack?Fd_AddTracker_Site=4649&Fd_AddTracker_Area=4507&Fd_AddTracker_Adcamp=4520&Fd_RCP=000293&Fd_PresentationId=virgin_home');
break;


case "home68_home":
	 return('http://www.u-k-i.com/HS/servlet/Router/addtrack?Fd_AddTracker_Site=4619&Fd_AddTracker_Area=4507&Fd_AddTracker_Adcamp=4520&Fd_RCP=000293&Fd_PresentationId=virgin_home');
break;


case "home69_home":
	 return('http://www.u-k-i.com/HS/servlet/Router/addtrack?Fd_AddTracker_Site=4620&Fd_AddTracker_Area=4507&Fd_AddTracker_Adcamp=4520&Fd_RCP=000293&Fd_PresentationId=virgin_home');
break;


case "home70_home":
	 return('http://www.u-k-i.com/HS/servlet/Router/addtrack?Fd_AddTracker_Site=4621&Fd_AddTracker_Area=4507&Fd_AddTracker_Adcamp=4520&Fd_RCP=000293&Fd_PresentationId=virgin_home');
break;


case "home71_home":
	 return('http://www.u-k-i.com/HS/servlet/Router/addtrack?Fd_AddTracker_Site=4622&Fd_AddTracker_Area=4507&Fd_AddTracker_Adcamp=4520&Fd_RCP=000293&Fd_PresentationId=virgin_home');
break;


case "home72_home":
	 return('http://www.u-k-i.com/HS/servlet/Router/addtrack?Fd_AddTracker_Site=4623&Fd_AddTracker_Area=4507&Fd_AddTracker_Adcamp=4520&Fd_RCP=000293&Fd_PresentationId=virgin_home');
break;


case "home73_home":
	 return('http://www.u-k-i.com/HS/servlet/Router/addtrack?Fd_AddTracker_Site=4624&Fd_AddTracker_Area=4507&Fd_AddTracker_Adcamp=4520&Fd_RCP=000293&Fd_PresentationId=virgin_home');
break;


case "home74_home":
	 return('http://www.u-k-i.com/HS/servlet/Router/addtrack?Fd_AddTracker_Site=4625&Fd_AddTracker_Area=4507&Fd_AddTracker_Adcamp=4520&Fd_RCP=000293&Fd_PresentationId=virgin_home');
break;


case "home75_home":
	 return('http://www.u-k-i.com/HS/servlet/Router/addtrack?Fd_AddTracker_Site=4627&Fd_AddTracker_Area=4507&Fd_AddTracker_Adcamp=4520&Fd_RCP=000293&Fd_PresentationId=virgin_home');
break;case "home76_home":
	 return('http://www.u-k-i.com/HS/servlet/Router/addtrack?Fd_AddTracker_Site=4628&Fd_AddTracker_Area=4507&Fd_AddTracker_Adcamp=4520&Fd_RCP=000293&Fd_PresentationId=virgin_home');
break;


case "home77_home":
	 return('http://www.u-k-i.com/HS/servlet/Router/addtrack?Fd_AddTracker_Site=4629&Fd_AddTracker_Area=4507&Fd_AddTracker_Adcamp=4520&Fd_RCP=000293&Fd_PresentationId=virgin_home');
break;


case "home78_home":
	 return('http://www.u-k-i.com/HS/servlet/Router/addtrack?Fd_AddTracker_Site=4630&Fd_AddTracker_Area=4507&Fd_AddTracker_Adcamp=4520&Fd_RCP=000293&Fd_PresentationId=virgin_home');
break;


case "home79_home":
	 return('http://www.u-k-i.com/HS/servlet/Router/addtrack?Fd_AddTracker_Site=4631&Fd_AddTracker_Area=4507&Fd_AddTracker_Adcamp=4520&Fd_RCP=000293&Fd_PresentationId=virgin_home');
break;


case "home80_home":
	 return('http://www.u-k-i.com/HS/servlet/Router/addtrack?Fd_AddTracker_Site=4632&Fd_AddTracker_Area=4507&Fd_AddTracker_Adcamp=4520&Fd_RCP=000293&Fd_PresentationId=virgin_home');
break;


case "home81_home":
	 return('http://www.u-k-i.com/HS/servlet/Router/addtrack?Fd_AddTracker_Site=4633&Fd_AddTracker_Area=4507&Fd_AddTracker_Adcamp=4520&Fd_RCP=000293&Fd_PresentationId=virgin_home');
break;


case "home82_home":
	 return('http://www.u-k-i.com/HS/servlet/Router/addtrack?Fd_AddTracker_Site=4634&Fd_AddTracker_Area=4507&Fd_AddTracker_Adcamp=4520&Fd_RCP=000293&Fd_PresentationId=virgin_home');
break;


case "home83_home":
	 return('http://www.u-k-i.com/HS/servlet/Router/addtrack?Fd_AddTracker_Site=4635&Fd_AddTracker_Area=4507&Fd_AddTracker_Adcamp=4520&Fd_RCP=000293&Fd_PresentationId=virgin_home');
break;


case "home84_home":
	 return('http://www.u-k-i.com/HS/servlet/Router/addtrack?Fd_AddTracker_Site=4637&Fd_AddTracker_Area=4507&Fd_AddTracker_Adcamp=4520&Fd_RCP=000293&Fd_PresentationId=virgin_home');
break;


case "home85_home":
	 return('http://www.u-k-i.com/HS/servlet/Router/addtrack?Fd_AddTracker_Site=4638&Fd_AddTracker_Area=4507&Fd_AddTracker_Adcamp=4520&Fd_RCP=000293&Fd_PresentationId=virgin_home');
break;


case "home86_home":
	 return('http://www.u-k-i.com/HS/servlet/Router/addtrack?Fd_AddTracker_Site=4639&Fd_AddTracker_Area=4507&Fd_AddTracker_Adcamp=4520&Fd_RCP=000293&Fd_PresentationId=virgin_home');
break;


case "home87_home":
	 return('http://www.u-k-i.com/HS/servlet/Router/addtrack?Fd_AddTracker_Site=4641&Fd_AddTracker_Area=4507&Fd_AddTracker_Adcamp=4520&Fd_RCP=000293&Fd_PresentationId=virgin_home');
break;


case "home88_home":
	 return('http://www.u-k-i.com/HS/servlet/Router/addtrack?Fd_AddTracker_Site=4651&Fd_AddTracker_Area=4505&Fd_AddTracker_Adcamp=4627&Fd_RCP=000293&Fd_PresentationId=virgin_home');
break;


case "home89_home":
	 return('http://www.u-k-i.com/HS/servlet/Router/addtrack?Fd_AddTracker_Site=4570&Fd_AddTracker_Area=4507&Fd_AddTracker_Adcamp=4518&Fd_RCP=000293&Fd_PresentationId=virgin_home');
break;


case "home90_home":
	 return('http://www.u-k-i.com/HS/servlet/Router/addtrack?Fd_AddTracker_Site=4571&Fd_AddTracker_Area=4507&Fd_AddTracker_Adcamp=4519&Fd_RCP=000293&Fd_PresentationId=virgin_home');
break;


case "home92_home":
	 return('http://www.u-k-i.com/HS/servlet/Router/addtrack?Fd_AddTracker_Site=4652&Fd_AddTracker_Area=4507&Fd_AddTracker_Adcamp=4521&Fd_RCP=000293&Fd_PresentationId=virgin_home');
break;


case "home93_home":
	 return('http://www.u-k-i.com/HS/servlet/Router/addtrack?Fd_AddTracker_Site=4696&Fd_AddTracker_Area=4507&Fd_AddTracker_Adcamp=4521&Fd_RCP=000293&Fd_PresentationId=virgin_home');
break;


case "home94_home":
	 return('http://www.u-k-i.com/HS/servlet/Router/addtrack?Fd_AddTracker_Site=4653&Fd_AddTracker_Area=4507&Fd_AddTracker_Adcamp=4521&Fd_RCP=000293&Fd_PresentationId=virgin_home');
break;


case "home95_home":
	 return('http://www.u-k-i.com/HS/servlet/Router/addtrack?Fd_AddTracker_Site=4654&Fd_AddTracker_Area=4507&Fd_AddTracker_Adcamp=4521&Fd_RCP=000293&Fd_PresentationId=virgin_home');
break;


case "home96_home":
	 return('http://www.u-k-i.com/HS/servlet/Router/addtrack?Fd_AddTracker_Site=4657&Fd_AddTracker_Area=4507&Fd_AddTracker_Adcamp=4521&Fd_RCP=000293&Fd_PresentationId=virgin_home');
break;


case "home97_home":
	 return('http://www.u-k-i.com/HS/servlet/Router/addtrack?Fd_AddTracker_Site=4659&Fd_AddTracker_Area=4507&Fd_AddTracker_Adcamp=4521&Fd_RCP=000293&Fd_PresentationId=virgin_home');
break;


case "home98_home":
	 return('http://www.u-k-i.com/HS/servlet/Router/addtrack?Fd_AddTracker_Site=4662&Fd_AddTracker_Area=4507&Fd_AddTracker_Adcamp=4521&Fd_RCP=000293&Fd_PresentationId=virgin_home');
break;


case "home99_home":
	 return('http://www.u-k-i.com/HS/servlet/Router/addtrack?Fd_AddTracker_Site=4663&Fd_AddTracker_Area=4507&Fd_AddTracker_Adcamp=4521&Fd_RCP=000293&Fd_PresentationId=virgin_home');
break;


case "home100_home":
	 return('http://www.u-k-i.com/HS/servlet/Router/addtrack?Fd_AddTracker_Site=4664&Fd_AddTracker_Area=4507&Fd_AddTracker_Adcamp=4521&Fd_RCP=000293&Fd_PresentationId=virgin_home');
break;case "home101_home":
	 return('http://www.u-k-i.com/HS/servlet/Router/addtrack?Fd_AddTracker_Site=4665&Fd_AddTracker_Area=4507&Fd_AddTracker_Adcamp=4521&Fd_RCP=000293&Fd_PresentationId=virgin_home');
break;


case "home102_home":
	 return('http://www.u-k-i.com/HS/servlet/Router/addtrack?Fd_AddTracker_Site=4666&Fd_AddTracker_Area=4507&Fd_AddTracker_Adcamp=4521&Fd_RCP=000293&Fd_PresentationId=virgin_home');
break;


case "home103_home":
	 return('http://www.u-k-i.com/HS/servlet/Router/addtrack?Fd_AddTracker_Site=4697&Fd_AddTracker_Area=4507&Fd_AddTracker_Adcamp=4521&Fd_RCP=000293&Fd_PresentationId=virgin_home');
break;


case "home104_home":
	 return('http://www.u-k-i.com/HS/servlet/Router/addtrack?Fd_AddTracker_Site=4698&Fd_AddTracker_Area=4507&Fd_AddTracker_Adcamp=4521&Fd_RCP=000293&Fd_PresentationId=virgin_home');
break;


case "home105_home":
	 return('http://www.u-k-i.com/HS/servlet/Router/addtrack?Fd_AddTracker_Site=4670&Fd_AddTracker_Area=4507&Fd_AddTracker_Adcamp=4521&Fd_RCP=000293&Fd_PresentationId=virgin_home');
break;


case "home106_home":
	 return('http://www.u-k-i.com/HS/servlet/Router/addtrack?Fd_AddTracker_Site=4672&Fd_AddTracker_Area=4507&Fd_AddTracker_Adcamp=4521&Fd_RCP=000293&Fd_PresentationId=virgin_home');
break;


case "home107_home":
	 return('http://www.u-k-i.com/HS/servlet/Router/addtrack?Fd_AddTracker_Site=4675&Fd_AddTracker_Area=4507&Fd_AddTracker_Adcamp=4521&Fd_RCP=000293&Fd_PresentationId=virgin_home');
break;


case "home108_home":
	 return('http://www.u-k-i.com/HS/servlet/Router/addtrack?Fd_AddTracker_Site=4676&Fd_AddTracker_Area=4507&Fd_AddTracker_Adcamp=4521&Fd_RCP=000293&Fd_PresentationId=virgin_home');
break;


case "home109_home":
	 return('http://www.u-k-i.com/HS/servlet/Router/addtrack?Fd_AddTracker_Site=4678&Fd_AddTracker_Area=4507&Fd_AddTracker_Adcamp=4521&Fd_RCP=000293&Fd_PresentationId=virgin_home');
break;


case "home110_home":
	 return('http://www.u-k-i.com/HS/servlet/Router/addtrack?Fd_AddTracker_Site=4699&Fd_AddTracker_Area=4507&Fd_AddTracker_Adcamp=4521&Fd_RCP=000293&Fd_PresentationId=virgin_home');
break;


case "home111_home":
	 return('http://www.u-k-i.com/HS/servlet/Router/addtrack?Fd_AddTracker_Site=4679&Fd_AddTracker_Area=4507&Fd_AddTracker_Adcamp=4521&Fd_RCP=000293&Fd_PresentationId=virgin_home');
break;


case "home112_home":
	 return('http://www.u-k-i.com/HS/servlet/Router/addtrack?Fd_AddTracker_Site=4680&Fd_AddTracker_Area=4507&Fd_AddTracker_Adcamp=4521&Fd_RCP=000293&Fd_PresentationId=virgin_home');
break;


case "home113_home":
	 return('http://www.u-k-i.com/HS/servlet/Router/addtrack?Fd_AddTracker_Site=4700&Fd_AddTracker_Area=4507&Fd_AddTracker_Adcamp=4521&Fd_RCP=000293&Fd_PresentationId=virgin_home');
break;


case "home114_home":
	 return('http://www.u-k-i.com/HS/servlet/Router/addtrack?Fd_AddTracker_Site=4701&Fd_AddTracker_Area=4507&Fd_AddTracker_Adcamp=4521&Fd_RCP=000293&Fd_PresentationId=virgin_home');
break;


case "home115_home":
	 return('http://www.u-k-i.com/HS/servlet/Router/addtrack?Fd_AddTracker_Site=4702&Fd_AddTracker_Area=4507&Fd_AddTracker_Adcamp=4521&Fd_RCP=000293&Fd_PresentationId=virgin_home');
break;


case "home116_home":
	 return('http://www.u-k-i.com/HS/servlet/Router/addtrack?Fd_AddTracker_Site=4703&Fd_AddTracker_Area=4507&Fd_AddTracker_Adcamp=4521&Fd_RCP=000293&Fd_PresentationId=virgin_home');
break;


case "home117_home":
	 return('http://www.u-k-i.com/HS/servlet/Router/addtrack?Fd_AddTracker_Site=4704&Fd_AddTracker_Area=4507&Fd_AddTracker_Adcamp=4521&Fd_RCP=000293&Fd_PresentationId=virgin_home');
break;


case "home118_home":
	 return('http://www.u-k-i.com/HS/servlet/Router/addtrack?Fd_AddTracker_Site=4705&Fd_AddTracker_Area=4507&Fd_AddTracker_Adcamp=4521&Fd_RCP=000293&Fd_PresentationId=virgin_home');
break;


case "home119_home":
	 return('http://www.u-k-i.com/HS/servlet/Router/addtrack?Fd_AddTracker_Site=4690&Fd_AddTracker_Area=4507&Fd_AddTracker_Adcamp=4521&Fd_RCP=000293&Fd_PresentationId=virgin_home');
break;


case "home120_home":
	 return('http://www.u-k-i.com/HS/servlet/Router/addtrack?Fd_AddTracker_Site=4691&Fd_AddTracker_Area=4507&Fd_AddTracker_Adcamp=4521&Fd_RCP=000293&Fd_PresentationId=virgin_home');
break;


case "home121_home":
	 return('http://www.u-k-i.com/HS/servlet/Router/addtrack?Fd_AddTracker_Site=4692&Fd_AddTracker_Area=4507&Fd_AddTracker_Adcamp=4521&Fd_RCP=000293&Fd_PresentationId=virgin_home');
break;


case "home122_home":
	 return('http://www.u-k-i.com/HS/servlet/Router/addtrack?Fd_AddTracker_Site=4694&Fd_AddTracker_Area=4507&Fd_AddTracker_Adcamp=4521&Fd_RCP=000293&Fd_PresentationId=virgin_home');
break;


case "home123_home":
	 return('http://www.u-k-i.com/HS/servlet/Router/addtrack?Fd_AddTracker_Site=4695&Fd_AddTracker_Area=4507&Fd_AddTracker_Adcamp=4521&Fd_RCP=000293&Fd_PresentationId=virgin_home');
break;


case "home124_home":
	 return('http://www.u-k-i.com/HS/servlet/Router/addtrack?Fd_AddTracker_Site=4719&Fd_AddTracker_Area=4507&Fd_AddTracker_Adcamp=4522&Fd_RCP=000293&Fd_PresentationId=virgin_home');
break;


case "home125_home":
	 return('http://www.u-k-i.com/HS/servlet/Router/addtrack?Fd_AddTracker_Site=4719&Fd_AddTracker_Area=4507&Fd_AddTracker_Adcamp=4523&Fd_RCP=000293&Fd_PresentationId=virgin_home');
break;case "home126_home":
	 return('http://www.u-k-i.com/HS/servlet/Router/addtrack?Fd_AddTracker_Site=4719&Fd_AddTracker_Area=4507&Fd_AddTracker_Adcamp=4524&Fd_RCP=000293&Fd_PresentationId=virgin_home');
break;


case "home127_home":
	 return('http://www.u-k-i.com/HS/servlet/Router/addtrack?Fd_AddTracker_Site=4719&Fd_AddTracker_Area=4507&Fd_AddTracker_Adcamp=4525&Fd_RCP=000293&Fd_PresentationId=virgin_home');
break;


case "home128_home":
	 return('http://www.u-k-i.com/HS/servlet/Router/addtrack?Fd_AddTracker_Site=4719&Fd_AddTracker_Area=4507&Fd_AddTracker_Adcamp=4526&Fd_RCP=000293&Fd_PresentationId=virgin_home');
break;


case "home129_home":
	 return('http://www.u-k-i.com/HS/servlet/Router/addtrack?Fd_AddTracker_Site=4719&Fd_AddTracker_Area=4507&Fd_AddTracker_Adcamp=4527&Fd_RCP=000293&Fd_PresentationId=virgin_home');
break;


case "home130_home":
	 return('http://www.u-k-i.com/HS/servlet/Router/addtrack?Fd_AddTracker_Site=4719&Fd_AddTracker_Area=4507&Fd_AddTracker_Adcamp=4528&Fd_RCP=000293&Fd_PresentationId=virgin_home');
break;


case "home131_home":
	 return('http://www.u-k-i.com/HS/servlet/Router/addtrack?Fd_AddTracker_Site=4719&Fd_AddTracker_Area=4507&Fd_AddTracker_Adcamp=4529&Fd_RCP=000293&Fd_PresentationId=virgin_home');
break;


case "home132_home":
	 return('http://www.u-k-i.com/HS/servlet/Router/addtrack?Fd_AddTracker_Site=4719&Fd_AddTracker_Area=4507&Fd_AddTracker_Adcamp=4530&Fd_RCP=000293&Fd_PresentationId=virgin_home');
break;


case "home133_home":
	 return('http://www.u-k-i.com/HS/servlet/Router/addtrack?Fd_AddTracker_Site=4719&Fd_AddTracker_Area=4507&Fd_AddTracker_Adcamp=4531&Fd_RCP=000293&Fd_PresentationId=virgin_home');
break;


case "home134_home":
	 return('http://www.u-k-i.com/HS/servlet/Router/addtrack?Fd_AddTracker_Site=4719&Fd_AddTracker_Area=4507&Fd_AddTracker_Adcamp=4532&Fd_RCP=000293&Fd_PresentationId=virgin_home');
break;


case "home135_home":
	 return('http://www.u-k-i.com/HS/servlet/Router/addtrack?Fd_AddTracker_Site=4719&Fd_AddTracker_Area=4507&Fd_AddTracker_Adcamp=4533&Fd_RCP=000293&Fd_PresentationId=virgin_home');
break;


case "home136_home":
	 return('http://www.u-k-i.com/HS/servlet/Router/addtrack?Fd_AddTracker_Site=4719&Fd_AddTracker_Area=4507&Fd_AddTracker_Adcamp=4534&Fd_RCP=000293&Fd_PresentationId=virgin_home');
break;


case "home137_home":
	 return('http://www.u-k-i.com/HS/servlet/Router/addtrack?Fd_AddTracker_Site=4719&Fd_AddTracker_Area=4507&Fd_AddTracker_Adcamp=4535&Fd_RCP=000293&Fd_PresentationId=virgin_home');
break;


case "home138_home":
	 return('http://www.u-k-i.com/HS/servlet/Router/addtrack?Fd_AddTracker_Site=4719&Fd_AddTracker_Area=4507&Fd_AddTracker_Adcamp=4536&Fd_RCP=000293&Fd_PresentationId=virgin_home');
break;


case "home139_home":
	 return('http://www.u-k-i.com/HS/servlet/Router/addtrack?Fd_AddTracker_Site=4719&Fd_AddTracker_Area=4507&Fd_AddTracker_Adcamp=4537&Fd_RCP=000293&Fd_PresentationId=virgin_home');
break;


case "home140_home":
	 return('http://www.u-k-i.com/HS/servlet/Router/addtrack?Fd_AddTracker_Site=4719&Fd_AddTracker_Area=4507&Fd_AddTracker_Adcamp=4538&Fd_RCP=000293&Fd_PresentationId=virgin_home');
break;


case "home141_home":
	 return('http://www.u-k-i.com/HS/servlet/Router/addtrack?Fd_AddTracker_Site=4562&Fd_AddTracker_Area=4507&Fd_AddTracker_Adcamp=4512&Fd_RCP=000293&Fd_PresentationId=virgin_home');
break;

		
	default:             
                            // Cookie Value must be duff.... re-set cookie to product and re call this function
                           return getUKIappURL(product,true);     
              break;
	}

}