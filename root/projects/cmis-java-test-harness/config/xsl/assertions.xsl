<?xml version="1.0" encoding="UTF-8"?>

<!--
  Formatting for WS-I Profile Test Assertion Document used by the Testing Tools
 
	Copyright (c) 2002-2004 by The Web Services-Interoperability Organization (WS-I) and 
	Certain of its Members. All Rights Reserved.
	
	Notice
	The material contained herein is not a license, either expressly or impliedly, to any 
	intellectual property owned or controlled by any of the authors or developers of this 
	material or WS-I. The material contained herein is provided on an "AS IS" basis and to 
	the maximum extent permitted by applicable law, this material is provided AS IS AND WITH 
	ALL FAULTS, and the authors and developers of this material and WS-I hereby disclaim all 
	other warranties and conditions, either express, implied or statutory, including, but not 
	limited to, any (if any) implied warranties, duties or conditions of  merchantability, 
	of fitness for a particular purpose, of accuracy or completeness of responses, of results, 
	of workmanlike effort, of lack of viruses, and of lack of negligence. ALSO, THERE IS NO 
	WARRANTY OR CONDITION OF TITLE, QUIET ENJOYMENT, QUIET POSSESSION, CORRESPONDENCE TO 
	DESCRIPTION OR NON-INFRINGEMENT WITH REGARD TO THIS MATERIAL.
	
	IN NO EVENT WILL ANY AUTHOR OR DEVELOPER OF THIS MATERIAL OR WS-I BE LIABLE TO ANY OTHER 
	PARTY FOR THE COST OF PROCURING SUBSTITUTE GOODS OR SERVICES, LOST PROFITS, LOSS OF USE, 
	LOSS OF DATA, OR ANY INCIDENTAL, CONSEQUENTIAL, DIRECT, INDIRECT, OR SPECIAL DAMAGES 
	WHETHER UNDER CONTRACT, TORT, WARRANTY, OR OTHERWISE, ARISING IN ANY WAY OUT OF THIS OR 
	ANY OTHER AGREEMENT RELATING TO THIS MATERIAL, WHETHER OR NOT SUCH PARTY HAD ADVANCE 
	NOTICE OF THE POSSIBILITY OF SUCH DAMAGES.
	
	WS-I License Information
	Use of this WS-I Material is governed by the WS-I Test License and other licenses.  Information on these 
	licenses are contained in the README.txt and ReleaseNotes.txt files.  By downloading this file, you agree 
	to the terms of these licenses.
		
	How To Provide Feedback
	The Web Services-Interoperability Organization (WS-I) would like to receive input, 
	suggestions and other feedback ("Feedback") on this work from a wide variety of 
	industry participants to improve its quality over time. 
	
	By sending email, or otherwise communicating with WS-I, you (on behalf of yourself if 
	you are an individual, and your company if you are providing Feedback on behalf of the 
	company) will be deemed to have granted to WS-I, the members of WS-I, and other parties 
	that have access to your Feedback, a non-exclusive, non-transferable, worldwide, perpetual, 
	irrevocable, royalty-free license to use, disclose, copy, license, modify, sublicense or 
	otherwise distribute and exploit in any manner whatsoever the Feedback you provide regarding 
	the work. You acknowledge that you have no expectation of confidentiality with respect to 
	any Feedback you provide. You represent and warrant that you have rights to provide this 
	Feedback, and if you are providing Feedback on behalf of a company, you represent and warrant 
	that you have the rights to provide Feedback on behalf of your company. You also acknowledge 
	that WS-I is not required to review, discuss, use, consider or in any way incorporate your 
	Feedback into future versions of its work. If WS-I does incorporate some or all of your 
	Feedback in a future version of the work, it may, but is not obligated to include your name 
	(or, if you are identified as acting on behalf of your company, the name of your company) on 
	a list of contributors to the work. If the foregoing is not acceptable to you and any company 
	on whose behalf you are acting, please do not provide any Feedback.
	
	WS-I members should direct feedback on this document to wsi_testing@lists.ws-i.org; 
    non-members should direct feedback to wsi-tools@ws-i.org. 

 
  Copyright (c) 2002 - 2004 IBM Corporation.  All rights reserved.
 
  @author Peter Brittenham, peterbr@us.ibm.com
  @author Simeon Greene, simeon.m.greene@oracle.com
  @version 0.91 
-->

<xsl:stylesheet
	version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:wsi-common="http://www.ws-i.org/testing/2003/03/common/"
	xmlns:wsi-assertions="http://www.ws-i.org/testing/2004/07/assertions/">
<xsl:import href="common.xsl"/>

<xsl:output method="html" indent="yes"/>

<xsl:variable name="uddiReferences" select="boolean(/wsi-assertions:profileAssertions/wsi-assertions:artifact[@type='discovery']/wsi-assertions:specificationReferenceList)"/>
<xsl:variable name="descriptionReferences" select="boolean(/wsi-assertions:profileAssertions/wsi-assertions:artifact[@type='description']/wsi-assertions:specificationReferenceList)"/>
<xsl:variable name="messageReferences" select="boolean(/wsi-assertions:profileAssertions/wsi-assertions:artifact[@type='message']/wsi-assertions:specificationReferenceList)"/>
<xsl:variable name="envelopeReferences" select="boolean(/wsi-assertions:profileAssertions/wsi-assertions:artifact[@type='envelope']/wsi-assertions:specificationReferenceList)"/>
<xsl:variable name="secureEnvelopeReferences" select="boolean(/wsi-assertions:profileAssertions/wsi-assertions:artifact[@type='secureEnvelope']/wsi-assertions:specificationReferenceList)"/>
<xsl:variable name="has_references" select="boolean($uddiReferences or $descriptionReferences or $messageReferences or $envelopeReferences or $secureEnvelopeReferences)"/>

<xsl:variable name="highlightColor1" select="'#009999'"/>
<xsl:variable name="highlightColor2" select="'#7ca8da'"/>
<xsl:variable name="highlightColor3" select="'orange'"/>
<xsl:variable name="highlightColor4" select="'red'"/>
<xsl:variable name="noteColor" select="'#0000cc'"/>

<xsl:key name="enabled" match="@enabled" use="."/>
<xsl:key name="status" match="@status" use="."/>
<xsl:key name="targetRelease" match="@targetRelease" use="."/>
<xsl:key name="type" match="@type" use="."/>

<xsl:template match="/">
<html>
	<!-- <link type="text/css" rel="stylesheet" href="http://www.ws-i.org/styles.css" /> 
	        font-size : 12px;
  -->
	<head>
    <title>WS-I Profile Test Assertion Document</title>    
    <!-- Need to put the following in a common CSS file -->
    <style type="text/css">
      body { color: black; background: white; font-family: sans-serif; margin: 16pt 24pt; }
                     dt { font-style: italic; padding-top: 0.5em; }
                     h4 { font-weight: normal; font-style: italic; font-size: 0.8em; margin-bottom: 0; }
                     .refinement { margin: 18pt 0em; }
                    .statement { font-style: italic; margin: 0.5em 5.5em; text-indent: -4em; }
                    .statement-id { background-color: #ffa; font-style: normal; padding: 0em 0.3em; }
                    .statement-type { background-color: #ccf; font-style: normal; padding: 0em 0.3em; font-size: 0.7em; vertical-align: text-top; }
                    .statement-origin { background-color: #fcc; font-style: normal; padding: 0em 0.3em; font-size: 0.7em; vertical-align: text-top; }
                    .rationale { margin: 0.5em 0em;  }
                    .explanation { margin: 0.5em 0em; }
                    .definition { margin: 0.5em 5.5em;}
                    .explanation p, .rationale p { margin: 0.25em 0; }
                    .practice { background-color: green; }
                    .example { background-color: #eee; margin: 0.5em 2em; padding: 0.5em; }
                    .example-banner { margin: 0; }
                    .example pre { margin: 0; }
                    .correct, .incorrect { font-family: sans-serif; font-weight: bold; font-size: 0.9em; margin: 0; }
                    .correct { color: #090; }
                    .incorrect { color: #900; }
                    .specification { padding: 0.5em; background-color: #ffa; margin: 0.5em 2em; }
                    .ednote { margin: 1.2em; padding: 1.2em; background-color: #fcc; }
                    .note { background-color: #fcc; margin-left: 1.5em; }
                    .toc { margin-left: 2em; }
                    .subtoc { margin-left: 3em; }
                    .subsubtoc { margin-left: 4em; }
                    .chg { background-color: orange; text-decoration: underline; }
                    .add { background-color: green; text-decoration: underline; }
                    .del { background-color: red; text-decoration: line-through; }
                    @media print {
                        a.offsite:after { content: " [" attr(href) "] "; font-size: 85%; font-weight: lighter; }
                    } 
                    [id]:hover:after { content: " #" attr(id) " "; font-size: 50%; color: #ccc; text-decoration: none; }
    </style>
	</head>

	<body>    
		<xsl:apply-templates />
	                <xsl:call-template name="trailer"/>
	</body>
</html>
</xsl:template>

<xsl:template match="wsi-assertions:description" >
</xsl:template>

<xsl:template match="wsi-assertions:version" >
</xsl:template>

<xsl:template match="wsi-assertions:profileAssertions" >
    <div class="head">
		<p>
			<a href="http://www.ws-i.org/">
				<img src="http://www.ws-i.org/images/WS-I-logo.gif" height="88" width="107" border="0" alt="WS-I"/>
			</a>
		</p>
                            <h1>
				<xsl:value-of select="@name" /> Version <xsl:value-of select="substring(@version,1,3)"/>
                            </h1>
                            <xsl:if test="count(@status) &gt; 0">
                            <h2>
                                <xsl:choose>
                                    <xsl:when test="@status='ED'">Ediorial Draft</xsl:when>
                                    <xsl:when test="@status='WGD'">Working Group Draft</xsl:when>
                                    <xsl:when test="@status='WGAD'">Working Group Approval Draft</xsl:when>
                                    <xsl:when test="@status='BdAD'">Board Approval Draft</xsl:when>
                                    <xsl:when test="@status='AD'">Approval Draft</xsl:when>
                                    <xsl:when test="@status='Final'">Final Material</xsl:when>
                                    <xsl:otherwise><xsl:value-of select="@status"/></xsl:otherwise>
                                </xsl:choose>                                
                            </h2>                             
                            </xsl:if>
                            <xsl:if test="count(@date) &gt; 0">
                            <h2>
                               <xsl:value-of select="@date"/>
                                </h2>
                            </xsl:if>
                        <dl>
                            <dt>Editors:</dt>
                            <xsl:for-each select="wsi-assertions:editors/wsi-assertions:person">
                                <dd>
                                    <xsl:value-of select="."/>, <xsl:value-of select="@affiliation"/> 
                                    (<a href="mailto:&lt;xsl:value-of select=&quot;@href&quot;/&gt;"><xsl:value-of select="@href"/></a>)     
                                </dd>
                            </xsl:for-each>
                            <xsl:if test="count(wsi-assertions:contributorText) &gt; 0">
                                <dt>Other Contributors</dt>
                                <dd><xsl:value-of select="wsi-assertions:contributorText"/></dd    >
                            </xsl:if>    
                            <dt>Administrative contact:</dt>
            		        <dd>
            			<a href="mailto:secretary@ws-i.org">secretary@ws-i.org</a>
            		    </dd>
                        </dl>
        <xsl:call-template name="copyright"/>
        </div>
  <!--<table>-->
  <!--<tr><td>
  <b>Name:</b>
  </td><td>
  <xsl:value-of select="@name" />
  </td></tr>-->
  <!--<tr><td>
  <b>Version:</b>
  </td><td>
      <xsl:value-of select="substring(@version,1,3)" />
  </td></tr>-->
   <!--<xsl:if test="count(@date) &gt; 0">
      <tr>
          <td>
               <b>Date:</b>
          </td>
          <td>
              <xsl:value-of select="@date"/>
           </td>
      </tr>
  </xsl:if>
  <xsl:if test="count(@status) &gt; 0">
      <tr>
          <td>
               <b>Status:</b>
          </td>
          <td>
              <xsl:value-of select="@status"/>
           </td>
      </tr>
  </xsl:if>
  </table>-->
	<!--<br/>
	<h3>Editors</h3>
<xsl:if test="count(wsi-assertions:editors/wsi-assertions:person) &lt; 1">
    <p>None</p>
</xsl:if>
<table>
<xsl:for-each select="wsi-assertions:editors/wsi-assertions:person">
<tr><td><xsl:value-of select="."/>, (<xsl:value-of select="@affiliation"/>)</td>
        <td><a href="mailto:&lt;xsl:value-of select=&quot;@href&quot;/&gt;"><xsl:value-of select="@href"/></a></td>     
</tr>
</xsl:for-each>
</table>-->
<!--<xsl:if test="count(wsi-assertions:contributorText) &gt; 0">
    <h4>Other Contributors</h4>
    <p><xsl:value-of select="wsi-assertions:contributorText"/></p>
</xsl:if>    -->
  <hr style="color : black;"/>
    <h2 id="abstract">Abstract</h2>
  <p><xsl:value-of select="wsi-assertions:description" /></p>
  
	<p>A "candidate" element is one that is to be verified for conformance.  
The binding of the tModel if &lt;wsi-analyzerConfig:uddiReference&gt; is given or the 
&lt;wsi-analyzerConfig:wsdlElement&gt; in the configuration file of the Analyzer define a 
candidate element for verification.  A verification on an element also implies that the same 
verification is made for all the elements that it uses.  That is, the elements it uses also 
become candidate elements.  Verification it based on the following transitivity rules, applied recursively.
</p>
<p>For WSDL element references:</p>
<ul>
<li>A verification on a wsdl:port is inherited by the referenced wsdl:bindings</li> 
<li>A verification on a wsdl:binding is inherited by the referenced wsdl:portTypes</li> 
<li>A verification on a wsdl:portType is inherited by the referenced wsdl:operations</li> 
<li>A verification on a wsdl:operation is inherited by the referenced wsdl:messages</li> 
</ul>

<p>For UDDI references:</p>
<ul>
<li>A verification on a uddi:bindingTemplate is inherited by the referenced uddi:tModel</li> 
<li>A verification on a uddi:tModel is inherited by the referenced wsdl:binding</li> 
</ul>
 
<p>The <a href="http://www.ws-i.org/Testing/Specs/AnalyzerFunctionalSpecification_1.02.pdf">analyzer 
specification</a> contains a detailed explanation of all of the fields listed in this document.</p>
  <h2 id="notice">Notice</h2>
  <p>The material contained herein is not a license, either expressly or impliedly, to any 
intellectual property owned or controlled by any of the authors or developers of this 
material or WS-I. The material contained herein is provided on an "AS IS" basis and to 
the maximum extent permitted by applicable law, this material is provided AS IS AND WITH 
ALL FAULTS, and the authors and developers of this material and WS-I hereby disclaim all 
other warranties and conditions, either express, implied or statutory, including, but not 
limited to, any (if any) implied warranties, duties or conditions of  merchantability, 
of fitness for a particular purpose, of accuracy or completeness of responses, of results, 
of workmanlike effort, of lack of viruses, and of lack of negligence. ALSO, THERE IS NO 
WARRANTY OR CONDITION OF TITLE, QUIET ENJOYMENT, QUIET POSSESSION, CORRESPONDENCE TO 
DESCRIPTION OR NON-INFRINGEMENT WITH REGARD TO THIS MATERIAL.
 </p>
 <p>
IN NO EVENT WILL ANY AUTHOR OR DEVELOPER OF THIS MATERIAL OR WS-I BE LIABLE TO ANY OTHER 
PARTY FOR THE COST OF PROCURING SUBSTITUTE GOODS OR SERVICES, LOST PROFITS, LOSS OF USE, 
LOSS OF DATA, OR ANY INCIDENTAL, CONSEQUENTIAL, DIRECT, INDIRECT, OR SPECIAL DAMAGES 
WHETHER UNDER CONTRACT, TORT, WARRANTY, OR OTHERWISE, ARISING IN ANY WAY OUT OF THIS OR 
ANY OTHER AGREEMENT RELATING TO THIS MATERIAL, WHETHER OR NOT SUCH PARTY HAD ADVANCE 
 NOTICE OF THE POSSIBILITY OF SUCH DAMAGES.</p>
<h2 id="feedback">Feedback</h2>
    <xsl:choose>        
        <xsl:when test="@status='Final'">
              <p>If there are areas in this specification that could be
    	clearer, or if errors or omissions are identified, WS-I
    	would like to be notified in order to provide the best
              possible interoperability guidance.</p>
        </xsl:when>
        <xsl:otherwise>
             <p>The Web Services-Interoperability Organization (WS-I) would like to receive input, 
suggestions and other feedback ("Feedback") on this work from a wide variety of 
industry participants to improve its quality over time.</p>
    
        </xsl:otherwise>
    </xsl:choose>
    <p>
By sending email, or otherwise communicating with WS-I, you (on behalf of yourself if 
you are an individual, and your company if you are providing Feedback on behalf of the 
company) will be deemed to have granted to WS-I, the members of WS-I, and other parties 
that have access to your Feedback, a non-exclusive, non-transferable, worldwide, perpetual, 
irrevocable, royalty-free license to use, disclose, copy, license, modify, sublicense or 
otherwise distribute and exploit in any manner whatsoever the Feedback you provide regarding 
the work. You acknowledge that you have no expectation of confidentiality with respect to 
any Feedback you provide. You represent and warrant that you have rights to provide this 
Feedback, and if you are providing Feedback on behalf of a company, you represent and warrant 
that you have the rights to provide Feedback on behalf of your company. You also acknowledge 
that WS-I is not required to review, discuss, use, consider or in any way incorporate your 
Feedback into future versions of its work. If WS-I does incorporate some or all of your 
Feedback in a future version of the work, it may, but is not obligated to include your name 
(or, if you are identified as acting on behalf of your company, the name of your company) on 
a list of contributors to the work. If the foregoing is not acceptable to you and any company 
on whose behalf you are acting, please do not provide any Feedback.</p>
<p>
WS-I members should direct feedback on this document to 
<a href="mailto:wsi_testing@lists.ws-i.org">wsi_testing@lists.ws-i.org</a>; 
non-members should direct feedback to 
<a href="mailto:wsi-tools@ws-i.org">wsi-tools@ws-i.org</a>. 
</p>
<h2 id="note">NOTE</h2>    
  <p>Test assertion headings that have 
  this <span style="background-color : {$highlightColor4};border-width : 1px 1px 1px 1px;border-style : solid solid solid solid;border-color : black black black black;">background color</span> are 
  disabled and will not be processed by the analyzer.
  </p>
    
  <!--<xsl:call-template name="copyright"/>-->

  <hr style="color : black;"/>
  <h2 id="toc">Table of Contents</h2>
  <p/>
  <p class="toc"><a href="#profileDefinitions">Profile Definitions</a><br/>
  <a href="#artifacts">Test Assertion Artifacts</a><br/>
  <xsl:for-each select="wsi-assertions:artifact">
    <span class="subtoc">
      <xsl:variable name="linkName" select="@type"/>
      <a href="#artifact{$linkName}"><xsl:value-of select="@type"/></a><br/>
    </span>
  </xsl:for-each>
  <a href="#counts">Test Assertion Counts</a><br/>
  <a href="#requirementsIndex">Profile Requirements Index</a><br/>	
   <xsl:if test="$has_references">
       Appendix A: <a href="#references">Referenced Specifications</a>
   </xsl:if>
  </p>
   
  <hr style="color : black;"/>
  <h2><a name="profileDefinitions">Profile Definitions</a></h2>
  <p/>
  <table cellpadding="4" bgcolor="#000000"  cellspacing="1" valign="top">
  <tr bgcolor="{$titleRowColor}"><td>
  <b>ID</b>
  </td><td>
  <b>Name</b>
  </td><td>
  <b>Version</b>
  </td><td>
  <b>Revision</b>
  </td><td>
  <b>Location</b>
  </td></tr>
  <xsl:for-each select="wsi-assertions:profileList/wsi-assertions:profile">
    <tr bgcolor="#ffffff"><td>
    <xsl:variable name="profileID" select="@id"/>
    <a name="{$profileID}"><xsl:value-of select="$profileID"/></a>
    </td><td>
    <xsl:value-of select="@name"/>
    </td><td>
    <xsl:value-of select="@version"/>
    </td><td>
    <xsl:value-of select="@revision"/>
    </td><td>
    <xsl:variable name="location" select="@location"/>
    <a href="{$location}"><xsl:value-of select="$location"/></a>
    </td></tr>
  </xsl:for-each>
  </table>
  
  <p/>

  <hr style="color : black;"/>
  <h2><a name="artifacts">Test Assertion Artifacts</a></h2>
  <ul>
  <xsl:for-each select="wsi-assertions:artifact">
    <li>
    <xsl:variable name="linkName" select="@type"/>
    <a href="#artifact{$linkName}"><xsl:value-of select="@type"/></a>
    </li>
  </xsl:for-each>
  </ul>

  <hr style="color : black;"/>
  <xsl:apply-templates select="wsi-assertions:artifact"/>

	<br/> 
  <h2><a name="counts">Test Assertion Counts</a></h2>
  <p><b>Total Count: </b>
  <span style="border-width : 1px 1px 1px 1px;border-style : solid solid solid solid;border-color : black black black black;"> <xsl:value-of select="count(//wsi-assertions:testAssertion)"/> </span></p>

  <p><b>Count By Type:</b></p>
  <table cellpadding="4" bgcolor="#000000"  cellspacing="1" valign="top" width="30%">
    <col span="1" width="70%"/>
    <col span="2" width="30%"/>
    <tr bgcolor="{$titleRowColor}"><td>
    <b>Type</b>
    </td><td>
    <b>Count</b>
    </td></tr>
    <xsl:for-each select="//wsi-assertions:testAssertion/@type[generate-id()=generate-id(key('type', .))]">
    <tr bgcolor="#ffffff"><td>
    <xsl:value-of select="."/>
    </td><td>
    <xsl:value-of select="count(//wsi-assertions:testAssertion[@type=current()])"/>
    </td></tr>
  </xsl:for-each>
  </table>

  <p><b>Count By Enabled Indicator:</b></p>
  <table cellpadding="4" bgcolor="#000000"  cellspacing="1" valign="top" width="30%">
    <col span="1" width="70%"/>
    <col span="2" width="30%"/>
    <tr bgcolor="{$titleRowColor}"><td>
    <b>Enabled</b>
    </td><td>
    <b>Count</b>
    </td></tr>
    <xsl:for-each select="//wsi-assertions:testAssertion/@enabled[generate-id()=generate-id(key('enabled', .))]">
    <tr bgcolor="#ffffff"><td>
    <xsl:value-of select="."/>
    </td><td>
    <xsl:value-of select="count(//wsi-assertions:testAssertion[@enabled=current()])"/>
    </td></tr>
  </xsl:for-each>
  </table>

<!--	<xsl:if test="//wsi-assertions:testAssertion/wsi-assertions:additionalInfo">
  <p><b>Count By Status:</b></p>
  <table cellpadding="4" bgcolor="#000000"  cellspacing="1" valign="top" width="30%">
    <col span="1" width="70%"/>
    <col span="2" width="30%"/>
    <tr bgcolor="{$titleRowColor}"><td>
    <b>Status</b>
    </td><td>
    <b>Count</b>
    </td></tr>
    <xsl:for-each select="//wsi-assertions:testAssertion/wsi-assertions:additionalInfo/@status[generate-id()=generate-id(key('status', .))]">
    <tr bgcolor="#ffffff"><td>
    <xsl:value-of select="."/>
    </td><td>
    <xsl:value-of select="count(//wsi-assertions:testAssertion/wsi-assertions:additionalInfo[@status=current()])"/>
    </td></tr>
  </xsl:for-each>
  </table>

  <p><b>Count By Target Release:</b></p>
  <table cellpadding="4" bgcolor="#000000"  cellspacing="1" valign="top" width="30%">
    <col span="1" width="70%"/>
    <col span="2" width="30%"/>
    <tr bgcolor="{$titleRowColor}"><td>
    <b>Target Release</b>
    </td><td>
    <b>Count</b>
    </td></tr>
    <xsl:for-each select="//wsi-assertions:testAssertion/wsi-assertions:additionalInfo/@targetRelease[generate-id()=generate-id(key('targetRelease', .))]">
    <xsl:sort select="." data-type="text"/>
    <tr bgcolor="#ffffff"><td>
    <xsl:value-of select="."/>
    </td><td>
    <xsl:value-of select="count(//wsi-assertions:testAssertion/wsi-assertions:additionalInfo[@targetRelease=current()])"/>
    </td></tr>
  </xsl:for-each>
  </table>
  </xsl:if>-->
  <br/>

  <hr style="color : black;"/>
  <h2><a name="requirementsIndex">Profile Requirement Index</a></h2>
  <p>This index contains a list of all of the requirements listed in the test assertion document.</p>

  <table cellpadding="4" bgcolor="#000000" cellspacing="1" valign="top">
  <tr bgcolor="{$titleRowColor}"><td>
  <b>Profile Requirement</b>
  </td><td>
  <b>Test Assertion</b>
  </td></tr>
  <xsl:for-each select="//wsi-assertions:testAssertion">
    <xsl:sort select="wsi-assertions:referenceList/wsi-assertions:reference" data-type="text"/>
    <xsl:variable name="taID" select="@id"/>
    <xsl:for-each select="wsi-assertions:referenceList/wsi-assertions:reference">
    <xsl:choose>
    <xsl:when test="current()=''"/>
    <xsl:otherwise>
    <tr bgcolor="#ffffff"><td>
    <xsl:variable name="requirement" select="."/>
    <xsl:variable name="profileID" select="./@profileID"/>
    <xsl:variable name="profileLink" select="/wsi-assertions:profileAssertions/wsi-assertions:profileList/wsi-assertions:profile[@id=$profileID]/@location"/> 
    <a href="{$profileLink}#{$requirement}"><xsl:value-of select="."/></a>
    </td><td>
    <a href="#{$taID}"><xsl:value-of select="$taID"/></a>
    </td></tr>
    </xsl:otherwise>
    </xsl:choose>
    </xsl:for-each>
  </xsl:for-each>
  </table>
	
	<p/>
<!--	<xsl:call-template name="notice"/>-->
</xsl:template>

<xsl:template match="wsi-assertions:artifact" >
  <xsl:variable name="linkName" select="@type"/>
	<a name="artifact{$linkName}"><h3>Profile Artifact: <xsl:value-of select="@type"/></h3></a>

  <p><xsl:value-of select="wsi-assertions:description" /></p>

  <p><b>Specification Reference List:</b></p>
  <ul>
  <xsl:for-each select="wsi-assertions:specificationReferenceList/wsi-assertions:specification">
    <li>
    <xsl:variable name="specLink" select="@location"/>
	  <a href="{$specLink}"><xsl:value-of select="@name"/></a>
    </li>
  </xsl:for-each>
  </ul>

  <hr style="color : black;"/>
  <b>Test Assertions [as they appear in the document]:</b>
  <p/>
  <table cellpadding="4" bgcolor="#000000" cellspacing="1" valign="top">
  <tr bgcolor="{$titleRowColor}"><td>
  <b>ID</b>
  </td> 	
  <td>
  <b>Entry Type</b>
  </td><td>
  <b>Test Type</b>
  </td><td>
  <b>Enabled</b>
  </td>
<!--	<xsl:if test="//wsi-assertions:testAssertion/wsi-assertions:additionalInfo">
  <td>
  <b>Priority</b>
  </td><td>
  <b>Status</b>
  </td><td>
  <b>Needed By<br/>Sample Application WG</b>
  </td><td>
  <b>Target Release</b>
  </td>
  </xsl:if>-->
  </tr>
  <xsl:for-each select="wsi-assertions:testAssertion">
    <tr bgcolor="#ffffff"><td>
    <xsl:variable name="taID" select="@id"/>
    <a href="#{$taID}"><xsl:value-of select="@id"/></a>
    </td>
    <td>
    <xsl:value-of select="@entryType"/>
    </td><td>
    <xsl:value-of select="@type"/>
    </td><td>
    <xsl:choose>
    <xsl:when test="@enabled='false'">
      <p style="color : {$highlightColor4};"><b>false</b></p>    
    </xsl:when>
    <xsl:otherwise>
       <p>true</p>    
    </xsl:otherwise>
    </xsl:choose>
    </td>
    </tr>
  </xsl:for-each>
  </table>

  <p/>
  <b>Test Assertions [sorted by ID]:</b>
  <br/>
  <br/>
  <table cellpadding="4" bgcolor="#000000" cellspacing="1" valign="top">
  <tr bgcolor="{$titleRowColor}"><td>
  <b>ID</b>
  </td>
  <!--<xsl:if test="//wsi-assertions:testAssertion/wsi-assertions:additionalInfo">
  <td>
  <b>Old ID</b>
  </td>
  </xsl:if>-->
  <td>
  <b>Entry Type</b>
  </td><td>
  <b>Test Type</b>
  </td><td>
  <b>Enabled</b>
	</td>
	
  </tr>
  <xsl:for-each select="wsi-assertions:testAssertion">
    <xsl:sort select="@id" data-type="text"/>
    <tr bgcolor="#ffffff"><td>
    <xsl:variable name="taID" select="@id"/>
    <a href="#{$taID}"><xsl:value-of select="@id"/></a>
    </td>
 
    <td>
    <xsl:value-of select="@entryType"/>
    </td><td>
    <xsl:value-of select="@type"/>
    </td><td>
    <xsl:choose>
    <xsl:when test="@enabled='false'">
      <p style="color : {$highlightColor4};"><b>false</b></p>    
    </xsl:when>
    <xsl:otherwise>
      <p>true</p>    
    </xsl:otherwise>
    </xsl:choose>
  	</td>
 
    </tr>
  </xsl:for-each>
  </table>

  <hr style="color : black;"/>
  <xsl:apply-templates select="wsi-assertions:testAssertion"/>
  <hr style="color : black;"/>

</xsl:template>

<xsl:template match="wsi-assertions:testAssertion" >
  <xsl:variable name="linkName" select="@id"/>
	<a name="#{$linkName}">
    <xsl:choose>
    <xsl:when test="wsi-assertions:referenceList/wsi-assertions:reference=''"/>
    <xsl:otherwise>
        <xsl:for-each select="wsi-assertions:referenceList/wsi-assertions:reference">
        <a name="reference{text()}"/>
		  </xsl:for-each>
    </xsl:otherwise>
    </xsl:choose>
  <xsl:choose>
  <xsl:when test="@enabled='false'">
    <h3 style="background-color : {$highlightColor4};">Test Assertion: <xsl:value-of select="@id"/>
    
    </h3>    
  </xsl:when>
  <xsl:otherwise>
    <h3>Test Assertion: <xsl:value-of select="@id"/>
    <!-- REMOVE:
   	<xsl:if test="//wsi-assertions:testAssertion/wsi-assertions:additionalInfo">
    [<xsl:value-of select="wsi-assertions:additionalInfo/@oldId"/>]
    </xsl:if>
    -->
    </h3>
  </xsl:otherwise>
  </xsl:choose>
  </a>
  
  <table cellpadding="4" bgcolor="#000000" cellspacing="1" valign="top" width="80%">
  <tbody>
    <tr  bgcolor="{$titleRowColor}">
      <td rowspan="2"><b>Entry Type</b></td>
      <td rowspan="2"><b>Test Type</b></td>
      <td rowspan="2"><b>Enabled</b></td>
      <td colspan="2"><b>Additional Entry Types</b></td>
      <td rowspan="2"><b>Prerequisites</b></td>
      <td colspan="3"><b>Profile Requirements</b></td>
    </tr>
    <tr bgcolor="{$titleRowColor}">
      <td><b>Message Input</b></td>
      <td><b>WSDL Input</b></td>
      <td><b>Target</b></td>
      <td><b>Partial-Target</b></td>
      <td><b>Collateral</b></td>
      <!-- REMOVE:
      <td><b>UDDI Input</b></td>
      -->
    </tr>
    <tr bgcolor="#ffffff">
      <td><xsl:value-of select="@entryType" /></td>
      <td><xsl:value-of select="@type" /></td>
      <td>
        <xsl:choose>
        <xsl:when test="@enabled='false'">
          <p style="color : {$highlightColor4};"><b>false</b></p>    
        </xsl:when>
        <xsl:otherwise>
          <p>true</p>    
        </xsl:otherwise>
        </xsl:choose>
      </td>
      <td>
        <xsl:choose>
        <xsl:when test="wsi-assertions:additionalEntryTypeList/wsi-assertions:messageInput=''">
          <p style="color: {$noteColor};"><b>NOTE: Need to define message input.</b></p>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="wsi-assertions:additionalEntryTypeList/wsi-assertions:messageInput"/>
        </xsl:otherwise>
        </xsl:choose>
      </td>
      <td>
        <xsl:choose>
        <xsl:when test="wsi-assertions:additionalEntryTypeList/wsi-assertions:wsdlInput=''">
          <p style="color: {$noteColor};"><b>NOTE: Need to define WSDL input.</b></p>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="wsi-assertions:additionalEntryTypeList/wsi-assertions:wsdlInput"/>
        </xsl:otherwise>
        </xsl:choose>
      </td>
<!-- REMOVE:
      <td>
        <xsl:choose>
        <xsl:when test="wsi-assertions:additionalEntryTypeList/wsi-assertions:uddiInput=''">
          <p style="color: {$noteColor};"><b>NOTE: Need to define UDDI input.</b></p>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="wsi-assertions:additionalEntryTypeList/wsi-assertions:uddiInput"/>
        </xsl:otherwise>
        </xsl:choose>
      </td>
-->
      <td>
        <xsl:choose>
        <xsl:when test="wsi-assertions:prereqList/wsi-assertions:testAssertionID">
          <xsl:for-each select="wsi-assertions:prereqList/wsi-assertions:testAssertionID">
            <a href="#{text()}"><xsl:value-of select="text()"/></a>
            <xsl:if test="position()!=last()">
              <br/>
            </xsl:if>
          </xsl:for-each>
        </xsl:when>
        <xsl:otherwise>
          <xsl:text>[Not specified]</xsl:text>
        </xsl:otherwise>
        </xsl:choose>
      </td>
      <td>
        <xsl:choose>
        <xsl:when test="wsi-assertions:referenceList/wsi-assertions:reference=''">
          <xsl:text>[Not specified]</xsl:text>
        </xsl:when>
        <xsl:otherwise>
          <xsl:for-each select="wsi-assertions:referenceList/wsi-assertions:reference[not(@role) or (@role = 'target')]">
            <xsl:variable name="profileID" select="./@profileID"/>
            <xsl:variable name="profileLink" select="/wsi-assertions:profileAssertions/wsi-assertions:profileList/wsi-assertions:profile[@id=$profileID]/@location"/> 
            <a name="profile-{text()}"/><a href="{$profileLink}#{text()}"><xsl:value-of select="text()"/></a>
            <xsl:if test="@refid!=''">
              (<a href="#{@refid}"><xsl:value-of select="@refid"/></a>)
            </xsl:if>
            <xsl:if test="position()!=last()">
              <br/>
            </xsl:if>
    		  </xsl:for-each>
        </xsl:otherwise>
        </xsl:choose>
      </td>
      <td>
        <xsl:choose>
        <xsl:when test="wsi-assertions:referenceList/wsi-assertions:reference=''">
          <xsl:text>[Not specified]</xsl:text>
        </xsl:when>
        <xsl:otherwise>
          <xsl:for-each select="wsi-assertions:referenceList/wsi-assertions:reference[@role = 'partial-target']">
            <xsl:variable name="profileID" select="./@profileID"/>
            <xsl:variable name="profileLink" select="/wsi-assertions:profileAssertions/wsi-assertions:profileList/wsi-assertions:profile[@id=$profileID]/@location"/> 
            <a name="profile-{text()}"/><a href="{$profileLink}#{text()}"><xsl:value-of select="text()"/></a>
            <xsl:if test="@refid!=''">
              (<a href="#{@refid}"><xsl:value-of select="@refid"/></a>)
            </xsl:if>
            <xsl:if test="position()!=last()">
              <br/>
            </xsl:if>
   	  </xsl:for-each>
        </xsl:otherwise>
        </xsl:choose>
      </td>
      <td>
        <xsl:choose>
        <xsl:when test="wsi-assertions:referenceList/wsi-assertions:reference=''">
          <xsl:text>[Not specified]</xsl:text>
        </xsl:when>
        <xsl:otherwise>
          <xsl:for-each select="wsi-assertions:referenceList/wsi-assertions:reference[@role = 'collateral']">
            <xsl:variable name="profileID" select="./@profileID"/>
            <xsl:variable name="profileLink" select="/wsi-assertions:profileAssertions/wsi-assertions:profileList/wsi-assertions:profile[@id=$profileID]/@location"/> 
            <a name="profile-{text()}"/><a href="{$profileLink}#{text()}"><xsl:value-of select="text()"/></a>
            <xsl:if test="@refid!=''">
              (<a href="#{@refid}"><xsl:value-of select="@refid"/></a>)
            </xsl:if>
            <xsl:if test="position()!=last()">
              <br/>
            </xsl:if>
    		  </xsl:for-each>
        </xsl:otherwise>
        </xsl:choose>
      </td>
    </tr>
  </tbody>
  </table>

  <br/>

	<p class="data-type"><b>Context:</b><br/>
	<span class="data-content"><xsl:value-of select="wsi-assertions:context" /></span></p>
	<p class="data-type"><b>Assertion Description:</b><br/>
	<span class="data-content">
	  <xsl:choose>
    <xsl:when test="wsi-assertions:assertionDescription=''">
      <p style="color: {$noteColor};"><b>NOTE: Need to define assertion description.</b></p>
    </xsl:when>
    <xsl:otherwise>
      <xsl:value-of select="wsi-assertions:assertionDescription"/>
    </xsl:otherwise>
    </xsl:choose>
  </span></p>
  <xsl:choose>
    <xsl:when test="@type='informational'">
      <!-- informational assertion: do not include failure message -->
      <p class="data-type"><b>Detail Description:</b><br/>
        <span class="data-content">  
          <xsl:choose>
            <xsl:when test="wsi-assertions:detailDescription=''">
              <xsl:text>[Not specified]</xsl:text>
            </xsl:when>
            <xsl:otherwise>
              <xsl:value-of select="wsi-assertions:detailDescription"/>
            </xsl:otherwise>
          </xsl:choose>
        </span>
      </p>
    </xsl:when>
    <xsl:otherwise>
    <!-- assertion with failure message -->
      <p class="data-type"><b>Failure Message:</b><br/>
        <span class="data-content">
          <xsl:choose>
            <xsl:when test="wsi-assertions:failureMessage=''">
              <p style="color: {$noteColor};"><b>NOTE: Need to define failure message.</b></p>
            </xsl:when>
            <xsl:otherwise>
              <xsl:value-of select="wsi-assertions:failureMessage"/>
            </xsl:otherwise>
          </xsl:choose>
        </span>
      </p>
  
      <p class="data-type"><b>Failure Detail Description:</b><br/>
        <span class="data-content">  
          <xsl:choose>
            <xsl:when test="wsi-assertions:failureDetailDescription=''">
              <xsl:text>[Not specified]</xsl:text>
            </xsl:when>
            <xsl:otherwise>
              <xsl:value-of select="wsi-assertions:failureDetailDescription"/>
            </xsl:otherwise>
          </xsl:choose>
        </span>
      </p>
    </xsl:otherwise>
  </xsl:choose> 
  <p class="data-type"><b>Comments:</b><br/>
  <span class="data-content">  
    <xsl:choose>
    <xsl:when test="wsi-assertions:comments=''">
      <xsl:text>[Not specified]</xsl:text>
    </xsl:when>
    <xsl:when test="wsi-assertions:comments">
      <xsl:value-of select="wsi-assertions:comments"/>
    </xsl:when>
    <xsl:otherwise>
      <xsl:text>[Not specified]</xsl:text>
    </xsl:otherwise>
    </xsl:choose>
  </span></p>    
	<br/>
	
	<!--
	<table width="80%">
  <col span="1" width="20%"/>
  <col span="2" width="80%"/>
	<tr valign="top"><td>
	  <b>Context:</b>
  </td><td>
  <xsl:value-of select="wsi-assertions:context" />
  </td></tr>
  <tr></tr>
  <tr valign="top"><td>
  <b>Assertion<br/>Description:</b>
  </td><td>
    <xsl:choose>
    <xsl:when test="wsi-assertions:assertionDescription=''">
      <p style="color: {$noteColor};"><b>NOTE: Need to define assertion description.</b></p>
    </xsl:when>
    <xsl:otherwise>
      <xsl:value-of select="wsi-assertions:assertionDescription"/>
    </xsl:otherwise>
    </xsl:choose>
  </td></tr>
  <tr></tr>
  <tr valign="top"><td>
  <b>Failure<br/>Message:</b>
  </td><td>
    <xsl:choose>
      <xsl:when test="wsi-assertions:failureMessage=''">
        <p style="color: {$noteColor};"><b>NOTE: Need to define failure message.</b></p>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="wsi-assertions:failureMessage"/>
      </xsl:otherwise>
    </xsl:choose>
  </td></tr>
  <tr></tr>
  <tr valign="top"><td>
  <b>Failure Detail<br/>Description:</b>
  </td><td>
    <xsl:choose>
      <xsl:when test="wsi-assertions:failureDetailDescription=''">
        <xsl:text>[Not specified]</xsl:text>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="wsi-assertions:failureDetailDescription"/>
      </xsl:otherwise>
    </xsl:choose>
  </td></tr>
  <tr></tr>
  <tr valign="top"><td>
  <b>Comments:</b>
  </td><td>
    <xsl:choose>
    <xsl:when test="wsi-assertions:comments=''">
      <xsl:text>[Not specified]</xsl:text>
    </xsl:when>
    <xsl:when test="wsi-assertions:comments">
      <xsl:value-of select="wsi-assertions:comments"/>
    </xsl:when>
    <xsl:otherwise>
      <xsl:text>[Not specified]</xsl:text>
    </xsl:otherwise>
    </xsl:choose>
  </td></tr>
  </table>  
  -->
  <!--
  <table cellpadding="4" bgcolor="#000000" cellspacing="1" valign="top">
  <col span="1" width="20%"/>
  <col span="2" width="80%"/>
  <tbody>
  <tr bgcolor="#ffffff"><td>
  <b>Entry Type</b>
  </td><td>
  <xsl:value-of select="@entryType" />
  </td></tr>
  <tr bgcolor="#ffffff"><td>
  <b>Test Type</b>
  </td><td>
  <xsl:value-of select="@type" />
  </td></tr>
  <tr bgcolor="#ffffff"><td>
  <b>Enabled</b>
  </td><td>
  <xsl:choose>
  <xsl:when test="@enabled='false'">
    <p style="color : {$highlightColor4};"><b>false</b></p>    
  </xsl:when>
   <xsl:otherwise>
    <p>true</p>    
  </xsl:otherwise>
  </xsl:choose>
  </td></tr>
  <tr bgcolor="#ffffff"><td>
  <b>Context</b>
  </td><td>
  <xsl:value-of select="wsi-assertions:context" />
  </td></tr>
  <tr bgcolor="#ffffff"><td>
  <b>Assertion Description</b>
  </td><td>
    <xsl:choose>
    <xsl:when test="wsi-assertions:assertionDescription=''">
      <p style="color: {$noteColor};"><b>NOTE: Need to define assertion description.</b></p>
    </xsl:when>
    <xsl:otherwise>
      <xsl:value-of select="wsi-assertions:assertionDescription"/>
    </xsl:otherwise>
    </xsl:choose>
  </td></tr>
  <tr bgcolor="#ffffff"><td>
  <b>Failure Message</b>
  </td><td>
    <xsl:choose>
      <xsl:when test="wsi-assertions:failureMessage=''">
        <p style="color: {$noteColor};"><b>NOTE: Need to define failure message.</b></p>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="wsi-assertions:failureMessage"/>
      </xsl:otherwise>
    </xsl:choose>
  </td></tr>
  <tr bgcolor="#ffffff"><td>
  <b>Failure Detail Description</b>
  </td><td>
    <xsl:choose>
      <xsl:when test="wsi-assertions:failureDetailDescription=''">
        <xsl:text>[Not specified]</xsl:text>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="wsi-assertions:failureDetailDescription"/>
      </xsl:otherwise>
    </xsl:choose>
  </td></tr>
  <xsl:if test="wsi-assertions:additionalEntryTypeList/wsi-assertions:logInput">
  <tr bgcolor="#ffffff"><td>
  <b>Additional Entry Type:<br/>Log Input</b>
  </td><td>
    <xsl:choose>
    <xsl:when test="wsi-assertions:additionalEntryTypeList/wsi-assertions:logInput=''">
      <p style="color: {$noteColor};"><b>NOTE: Need to define log input.</b></p>
    </xsl:when>
    <xsl:otherwise>
      <xsl:value-of select="wsi-assertions:additionalEntryTypeList/wsi-assertions:logInput"/>
    </xsl:otherwise>
    </xsl:choose>
  </td></tr>
  </xsl:if>
  <xsl:if test="wsi-assertions:additionalEntryTypeList/wsi-assertions:wsdlInput">
  <tr bgcolor="#ffffff"><td>
  <b>Additional Entry Type:<br/>WSDL Input</b>
  </td><td>
    <xsl:choose>
    <xsl:when test="wsi-assertions:additionalEntryTypeList/wsi-assertions:wsdlInput=''">
      <p style="color: {$noteColor};"><b>NOTE: Need to define WSDL input.</b></p>
    </xsl:when>
    <xsl:otherwise>
      <xsl:value-of select="wsi-assertions:additionalEntryTypeList/wsi-assertions:wsdlInput"/>
    </xsl:otherwise>
    </xsl:choose>
  </td></tr>
  </xsl:if>
  <xsl:if test="wsi-assertions:additionalEntryTypeList/wsi-assertions:uddiInput">
  <tr bgcolor="#ffffff"><td>
  <b>Additional Entry Type:<br/>UDDI Input</b>
  </td><td>
    <xsl:choose>
      <xsl:when test="wsi-assertions:additionalEntryTypeList/wsi-assertions:uddiInput=''">
        <p style="color: {$noteColor};"><b>NOTE: Need to define UDDI input.</b></p>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="wsi-assertions:additionalEntryTypeList/wsi-assertions:uddiInput"/>
      </xsl:otherwise>
    </xsl:choose>
  </td></tr>
  </xsl:if>
  <tr bgcolor="#ffffff"><td>
  <b>Prereqs</b>
  </td><td>                                                             
    <xsl:choose>
    <xsl:when test="wsi-assertions:prereqList/wsi-assertions:testAssertionID">
      <xsl:for-each select="wsi-assertions:prereqList/wsi-assertions:testAssertionID">
        <a href="#{text()}"><xsl:value-of select="text()"/></a>
        <xsl:if test="position()!=last()">
          <xsl:text>, </xsl:text>
        </xsl:if>
      </xsl:for-each>
    </xsl:when>
    <xsl:otherwise>
      <xsl:text>[Not specified]</xsl:text>
    </xsl:otherwise>
    </xsl:choose>
	</td></tr>
  <tr bgcolor="#ffffff"><td>
  <b>Profile References</b>
  </td><td>                                                             
    <xsl:choose>
    <xsl:when test="wsi-assertions:referenceList/wsi-assertions:reference=''">
      <xsl:text>[Not specified]</xsl:text>
    </xsl:when>
    <xsl:otherwise>
      <xsl:for-each select="wsi-assertions:referenceList/wsi-assertions:reference">
        <xsl:variable name="profileID" select="./@profileID"/>
        <xsl:variable name="profileLink" select="/wsi-assertions:profileAssertions/wsi-assertions:profileList/wsi-assertions:profile[@id=$profileID]/@location"/> 
        <a name="profile-{text()}"/><a href="{$profileLink}#{text()}"><xsl:value-of select="text()"/></a>
        <xsl:if test="@refid!=''">
          (<a href="#{@refid}"><xsl:value-of select="@refid"/></a>)
        </xsl:if>
        <xsl:if test="position()!=last()">
          <xsl:text>, </xsl:text>
        </xsl:if>
		  </xsl:for-each>
    </xsl:otherwise>
    </xsl:choose>
	</td></tr>
  <tr bgcolor="#ffffff"><td>
  <b>Comments</b>
  </td><td>
    <xsl:choose>
    <xsl:when test="wsi-assertions:comments">
      <xsl:value-of select="wsi-assertions:comments"/>
    </xsl:when>
    <xsl:otherwise>
      <xsl:text>[Not specified]</xsl:text>
    </xsl:otherwise>
    </xsl:choose>
  </td></tr>
  </tbody>
  </table>
-->
  <p><a href="#top">Return to top of document.</a></p>
  <p/>
</xsl:template>
<xsl:template name="trailer">
    <h2 id="references">Appendix A: Referenced Specifications</h2>
    <p>The following specifications' requirements are incorporated
into the Test Assertion Document (TAD) by reference, except where superseded by the
    TAD:</p> 
    <xsl:if test="$uddiReferences">
        <h3>Discovery (UDDI):</h3>
        <ul>
            <xsl:apply-templates select="/wsi-assertions:profileAssertions/wsi-assertions:artifact[@type='discovery']/wsi-assertions:specificationReferenceList"/>
        </ul>
    </xsl:if>
   <xsl:if test="$descriptionReferences">
        <h3>Description (WSDL):</h3>
        <ul>
            <xsl:apply-templates select="/wsi-assertions:profileAssertions/wsi-assertions:artifact[@type='description']/wsi-assertions:specificationReferenceList"/>
        </ul>
   </xsl:if>
    <xsl:if test="$messageReferences">
        <h3>Message:</h3>
        <ul>
            <xsl:apply-templates select="/wsi-assertions:profileAssertions/wsi-assertions:artifact[@type='message']/wsi-assertions:specificationReferenceList"/>
        </ul>
    </xsl:if>
    <xsl:if test="$envelopeReferences">
        <h3>SOAP Envelope:</h3>
        <ul>
            <xsl:apply-templates select="/wsi-assertions:profileAssertions/wsi-assertions:artifact[@type='envelope']/wsi-assertions:specificationReferenceList"/>
        </ul>
    </xsl:if>
    <xsl:if test="$secureEnvelopeReferences">
        <h3>Secure SOAP Envelope:</h3>
        <ul>
            <xsl:apply-templates select="/wsi-assertions:profileAssertions/wsi-assertions:artifact[@type='secureEnvelope']/wsi-assertions:specificationReferenceList"/>
        </ul>
    </xsl:if>
</xsl:template>
<xsl:template match="/wsi-assertions:profileAssertions/wsi-assertions:artifact/wsi-assertions:specificationReferenceList">
    <xsl:for-each select="wsi-assertions:specification">
        <li>
            <a class="offsite" href="{@location}">
                <xsl:value-of select="@name"/>
            </a>
         </li>
    </xsl:for-each>        
</xsl:template>
</xsl:stylesheet>
