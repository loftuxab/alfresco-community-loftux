

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html lang="en">
<head>
<script src="/assets/js/vm_common2005.js" type="text/javascript"></script>

    <script type="text/JavaScript">

    function SendForm()
    {
    // sends the form and sets a hidden field to prevent it being sent again
     this.document.RenewalDatesForm.formsubmitted.value = 'Y';
     this.document.RenewalDatesForm.submit();
    }

    function checkformSubmitButton()
    {
     //checks the hidden field , if it has been sent it returns false and prevent re-submission
       if (this.document.RenewalDatesForm.formsubmitted.value != 'Y')
       {
           return true;
       }
       else
       {
           alert('You have already submitted this page. Please click on OK to \nclose this message, and wait for the page to change');
           return false;
       };
    };

    </script>
    
<link type="text/css" href="/assets/css/vm_generic.css" rel="stylesheet">

<style>




.form-title-small {
clear:left;
width:150px;
float:left;
}

.form-title-medium {
width:270px;
float:left;
}

.form-title-large {
width:350px;
float:left;
}

.form-fields-small {
width:150px;
float:left;
}

.form-fields-medium {
width:210px;
float:left;
}

.form-fields-large {
width:370px;
float:left;
}

.form-content-left {
width:320px;
float:left
}

.form-content-right {
width:310px;
float:left
}

.form-content {
width:640px;
}

.clear {
clear:both;
}

</style>


<link type="text/css" href="/assets/css/vm_home.css" rel="stylesheet">
<title>Virgin Money Insurance </title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">



<script language="javascript" type="text/javascript">
<!--
// Copyright Clickstream Technologies (C) 2004
// Version:12 Date: 09/12/04
var CSv14=12;var CSv19="";var CSv20="1v3vE8l";
var CSv21=true;var CSv22="SF093cr01w";
function CSunload(){};function CSload(){};
//-->
</script>
<script language="javascript" type="text/javascript" src="/clickstream/clickstream.js"></script>
</head>
<body id="popup-background" onload="CSload();" onunload="CSunload();" >
	<div class="pop-content">
<div class="pop-header">
<h2>Please get in touch to tell me about your insurance products.</h2>
</div>

<div class="pop-text">


<form name="RenewalDatesForm" method="post" action="/home-insurance/storeRenewalDates.do;jsessionid=F5EE18562DBEC25117823BE256261338.msl_1"><input type="hidden" name="formsubmitted" value="N">
<span class="text-red-bold"></span>


			<div class="form-content">

				<div class="form-content-left">

									<div class="form-title-small"><label for="title" class="text-bold">Title</label></div>
										<div class="form-fields-small">

												<div class="block-form">
													 <select name="title"><option value="Admiral">Admiral</option>
																<option value="Baron">Baron</option>
																<option value="Baroness">Baroness</option>
																<option value="Baronet">Baronet</option>
																<option value="Bishop">Bishop</option>
																<option value="Brigadier">Brigadier</option>
																<option value="Captain">Captain</option>
																<option value="Cardinal">Cardinal</option>
																<option value="Colonel">Colonel</option>
																<option value="Commander">Commander</option>
																<option value="Commodore">Commodore</option>
																<option value="Count">Count</option>
																<option value="Countess">Countess</option>
																<option value="Dame">Dame</option>
																<option value="Dr">Dr</option>
																<option value="Duchess">Duchess</option>
																<option value="Duke">Duke</option>
																<option value="Earl">Earl</option>
																<option value="Frau">Frau</option>
																<option value="General">General</option>
																<option value="Herr">Herr</option>
																<option value="Lady">Lady</option>
																<option value="Lieut Col">Lieut Col</option>
																<option value="Lieut Gen">Lieut Gen</option>
																<option value="Lieutenant">Lieutenant</option>
																<option value="Lord">Lord</option>
																<option value="M">M</option>
																<option value="Maj Gen">Maj Gen</option>
																<option value="Major">Major</option>
																<option value="March">March</option>
																<option value="Marquess">Marquess</option>
																<option value="Master">Master</option>
																<option value="Miss">Miss</option>
																<option value="Mlle">Mlle</option>
																<option value="Mme">Mme</option>
																<option value="Mr" selected="selected">Mr</option>
																<option value="Mrs">Mrs</option>
																<option value="Ms">Ms</option>
																<option value="Prof">Prof</option>
																<option value="Rev">Rev</option>
																<option value="Sir">Sir</option>
																<option value="Viscount">Viscount</option></select>
												</div>
										</div>



									<div class="clear"></div>


									<div class="form-title-small"><label for="forename" class="text-bold">First Name</label></div>
										<div class="form-fields-small">
											<div class="block-form">
												<input type="text" name="forename" maxlength="30" size="20" value="">
											</div>
										</div>


								<div class="clear"></div>


									<div class="form-title-small"><label for="surname" class="text-bold">Surname</label></div>
										<div class="form-fields-small">
											<div class="block-form">
												<input type="text" name="surname" maxlength="30" size="20" value="">
											</div>
										</div>


								<div class="clear"></div>


									<div class="form-title-small"><label for="address1" class="text-bold">Address Line 1</label></div>
										<div class="form-fields-small">
											<div class="block-form">
												<input type="text" name="address1" maxlength="40" size="20" value="">
											</div>
										</div>


								<div class="clear"></div>


									<div class="form-title-small"><label for="address2" class="text-bold">Address Line 2</label></div>
										<div class="form-fields-small">
											<div class="block-form">
												<input type="text" name="address2" maxlength="40" size="20" value="">
											</div>
										</div>


								<div class="clear"></div>


									<div class="form-title-small"><label for="address3">Address Line 3</label></div>
										<div class="form-fields-small">
											<div class="block-form">
												<input type="text" name="address3" maxlength="40" size="20" value="">
											</div>
										</div>


								<div class="clear"></div>


									<div class="form-title-small"><label for="address4">Address Line 4</label></div>
										<div class="form-fields-small">
											<div class="block-form">
												<input type="text" name="address4" maxlength="40" size="20" value="">
											</div>
										</div>


								<div class="clear"></div>


									<div class="form-title-small"><label for="postcode" class="text-bold">Postcode</label></div>
										<div class="form-fields-small">
											<div class="block-form">
												<input type="text" name="postcode" maxlength="10" size="10" value="">
											</div>
										</div>
										
										
								<div class="clear"></div>								

										<div class="form-title-small"><label for="emailAddress" class="text-bold">Email address</label></div>
											<div class="form-fields-small">

													<div class="block-form">
														<input type="text" name="emailAddress" maxlength="200" size="20" value="">
													</div>
											</div>
								
								
								
											



								<div class="clear"></div>
								
								
								


									<div class="form-title-small"><label for="phoneNumber" class="text-bold">Telephone number</label></div>
										<div class="form-fields-small">
											<div class="block-form">
												<input type="text" name="phoneNumber" maxlength="30" size="20" value="">
											</div>
										</div>



				</div>

				<div class="form-content-right">
				
				<p>I'll be thinking about</p>






											<div class="form-title-small"><label for="homeInsRenewalMonth" class="text-bold">Home insurance in:</label></div>
												<div class="form-fields-small">
													<div class="block-form">
														<select name="homeInsRenewalMonth"><option value="0" selected="selected">Not interested</option>
																	<option value="1">January</option>
																	<option value="2">February</option>
																	<option value="3">March</option>
																	<option value="4">April</option>
																	<option value="5">May</option>
																	<option value="6">June</option>
																	<option value="7">July</option>
																	<option value="8">August</option>
																	<option value="9">September</option>
																	<option value="10">October</option>
																	<option value="11">November</option>
																	<option value="12">December</option></select>
													</div>
												</div>


										<div class="clear"></div>


											<div class="form-title-small"><label for="motorInsRenewalMonth" class="text-bold">Car insurance in:</label></div>
												<div class="form-fields-small">
													<div class="block-form">
														 <select name="motorInsRenewalMonth"><option value="0" selected="selected">Not interested</option>
																	<option value="1">January</option>
																	<option value="2">February</option>
																	<option value="3">March</option>
																	<option value="4">April</option>
																	<option value="5">May</option>
																	<option value="6">June</option>
																	<option value="7">July</option>
																	<option value="8">August</option>
																	<option value="9">September</option>
																	<option value="10">October</option>
																	<option value="11">November</option>
																	<option value="12">December</option></select>
													</div>
												</div>


										<div class="clear"></div>


											<div class="form-title-small"><label for="travelInsRenewalMonth" class="text-bold">Travel insurance in:</label></div>
												<div class="form-fields-small">
													<div class="block-form">
														<select name="travelInsRenewalMonth"><option value="0" selected="selected">Not interested</option>
																	<option value="1">January</option>
																	<option value="2">February</option>
																	<option value="3">March</option>
																	<option value="4">April</option>
																	<option value="5">May</option>
																	<option value="6">June</option>
																	<option value="7">July</option>
																	<option value="8">August</option>
																	<option value="9">September</option>
																	<option value="10">October</option>
																	<option value="11">November</option>
																	<option value="12">December</option></select>
													</div>
												</div>
												
										<div class="clear"></div>


											<div class="form-title-small"><label for="petInsRenewalMonth" class="text-bold">Pet insurance in:</label></div>
												<div class="form-fields-small">
													<div class="block-form">
														<select name="petInsRenewalMonth"><option value="0" selected="selected">Not interested</option>
																	<option value="1">January</option>
																	<option value="2">February</option>
																	<option value="3">March</option>
																	<option value="4">April</option>
																	<option value="5">May</option>
																	<option value="6">June</option>
																	<option value="7">July</option>
																	<option value="8">August</option>
																	<option value="9">September</option>
																	<option value="10">October</option>
																	<option value="11">November</option>
																	<option value="12">December</option></select>
													</div>
											</div>


								<div class="clear"></div>

				</div>

			</div>
			
			
			<div class="form-content">
			
			<div class="form-title-large"><br/><span class="text-bold">Are you happy to get the odd e-mail from us about products,
			                                services or offers we think you'll be especially interested in?&nbsp;</span></div>
			                                
			                                <div class="form-fields-small"><br/><input type="radio" name="noEmailInd" value="N">Yes&nbsp;&nbsp;<input type="radio" name="noEmailInd" value="Y" checked="checked">No</div>
			<div class="clear"></div>
			
			</div>
			
			
			<div class="form-content">								
			
			<br/>
			<p><span class="text-bold">Occasionally, we'd like to let you know about other products, services or offers we think you'll be especially interested in.
                        Please tick here if you'd rather we didn't do this by:&nbsp;</span><input type="checkbox" name="noMailingsInd" value="on">Mail&nbsp;&nbsp;<input type="checkbox" name="noPhoneInd" value="on">Phone</p>
			
			
			<div style="margin-right:40px"><a onClick="javascript:checkformSubmitButton();" href="javascript:SendForm()" onMouseOut="MM_swapImgRestore()" onMouseOver="MM_swapImage('submit_btn','','/Images/submit-over_tcm17-10860.gif',1)"><img src="/Images/submit_tcm17-10859.gif" name="submit_btn" align="right"/></a></div>
			
			</div>

		<div class="clear"></div>

</form>

</div>




	</div>


</body>






</html>