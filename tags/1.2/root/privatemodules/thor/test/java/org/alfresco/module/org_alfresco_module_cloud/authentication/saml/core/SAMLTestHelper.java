/*
 * Copyright 2005-2013 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.module.org_alfresco_module_cloud.authentication.saml.core;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.KeyStore;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.codec.binary.Base64;
import org.joda.time.DateTime;
import org.opensaml.DefaultBootstrap;
import org.opensaml.saml2.core.Assertion;
import org.opensaml.saml2.core.Attribute;
import org.opensaml.saml2.core.AttributeStatement;
import org.opensaml.saml2.core.AttributeValue;
import org.opensaml.saml2.core.Audience;
import org.opensaml.saml2.core.AudienceRestriction;
import org.opensaml.saml2.core.AuthnContext;
import org.opensaml.saml2.core.AuthnContextClassRef;
import org.opensaml.saml2.core.AuthnStatement;
import org.opensaml.saml2.core.Conditions;
import org.opensaml.saml2.core.Issuer;
import org.opensaml.saml2.core.NameID;
import org.opensaml.saml2.core.NameIDType;
import org.opensaml.saml2.core.Response;
import org.opensaml.saml2.core.Status;
import org.opensaml.saml2.core.StatusCode;
import org.opensaml.saml2.core.Subject;
import org.opensaml.saml2.core.SubjectConfirmation;
import org.opensaml.saml2.core.SubjectConfirmationData;
import org.opensaml.xml.Configuration;
import org.opensaml.xml.ConfigurationException;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.io.Unmarshaller;
import org.opensaml.xml.io.UnmarshallerFactory;
import org.opensaml.xml.parse.ParserPool;
import org.opensaml.xml.parse.XMLParserException;
import org.opensaml.xml.schema.XSAny;
import org.opensaml.xml.schema.XSString;
import org.opensaml.xml.schema.impl.XSAnyBuilder;
import org.opensaml.xml.schema.impl.XSStringBuilder;
import org.opensaml.xml.security.CriteriaSet;
import org.opensaml.xml.security.credential.CollectionCredentialResolver;
import org.opensaml.xml.security.credential.Credential;
import org.opensaml.xml.security.credential.KeyStoreCredentialResolver;
import org.opensaml.xml.security.credential.UsageType;
import org.opensaml.xml.security.criteria.EntityIDCriteria;
import org.opensaml.xml.security.criteria.UsageCriteria;
import org.opensaml.xml.security.trust.TrustEngine;
import org.opensaml.xml.security.x509.BasicX509Credential;
import org.opensaml.xml.signature.Signature;
import org.opensaml.xml.signature.impl.ExplicitKeySignatureTrustEngine;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.ResourceUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Helper class for SAML testing.
 * 
 * @author jkaabimofrad
 * @since Cloud SAML
 */
public class SAMLTestHelper
{
    public static final String RESPONSE_OPENAM = "PHNhbWxwOlJlc3BvbnNlIHhtbG5zOnNhbWxwPSJ1cm46b2FzaXM6bmFtZXM6dGM6U0FNTDoyLjA6"
        + "cHJvdG9jb2wiIElEPSJzMjBkMmExOGY1NGU4NDE3NjQyMDEwMDMyODI2MWEwYzc1ODY2MWExOWUi"
        + "IFZlcnNpb249IjIuMCIgSXNzdWVJbnN0YW50PSIyMDEzLTAxLTI3VDE5OjUwOjIwWiIgRGVzdGlu"
        + "YXRpb249Imh0dHA6Ly9sb2NhbGhvc3Q6ODA4MS9zaGFyZS9vcGVuYW0udGVzdC9zYW1sL2F1dGhu"
        + "cmVzcG9uc2UiPjxzYW1sOklzc3VlciB4bWxuczpzYW1sPSJ1cm46b2FzaXM6bmFtZXM6dGM6U0FN"
        + "TDoyLjA6YXNzZXJ0aW9uIj5odHRwOi8vd3d3Lm15b3BlbmFtLmNvbTo5MDgwL29wZW5zc288L3Nh"
        + "bWw6SXNzdWVyPjxzYW1scDpTdGF0dXMgeG1sbnM6c2FtbHA9InVybjpvYXNpczpuYW1lczp0YzpT"
        + "QU1MOjIuMDpwcm90b2NvbCI+CjxzYW1scDpTdGF0dXNDb2RlICB4bWxuczpzYW1scD0idXJuOm9h"
        + "c2lzOm5hbWVzOnRjOlNBTUw6Mi4wOnByb3RvY29sIgpWYWx1ZT0idXJuOm9hc2lzOm5hbWVzOnRj"
        + "OlNBTUw6Mi4wOnN0YXR1czpTdWNjZXNzIj4KPC9zYW1scDpTdGF0dXNDb2RlPgo8L3NhbWxwOlN0"
        + "YXR1cz48c2FtbDpBc3NlcnRpb24geG1sbnM6c2FtbD0idXJuOm9hc2lzOm5hbWVzOnRjOlNBTUw6"
        + "Mi4wOmFzc2VydGlvbiIgSUQ9InMyYmZmN2M2MmQwNTYwYTdhZjgxMWM1YzUyOGI4YzcxY2M0NDdm"
        + "ZDNlMyIgSXNzdWVJbnN0YW50PSIyMDEzLTAxLTI3VDE5OjUwOjIwWiIgVmVyc2lvbj0iMi4wIj4N"
        + "CjxzYW1sOklzc3Vlcj5odHRwOi8vd3d3Lm15b3BlbmFtLmNvbTo5MDgwL29wZW5zc288L3NhbWw6"
        + "SXNzdWVyPjxkczpTaWduYXR1cmUgeG1sbnM6ZHM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvMDkv"
        + "eG1sZHNpZyMiPg0KPGRzOlNpZ25lZEluZm8+DQo8ZHM6Q2Fub25pY2FsaXphdGlvbk1ldGhvZCBB"
        + "bGdvcml0aG09Imh0dHA6Ly93d3cudzMub3JnLzIwMDEvMTAveG1sLWV4Yy1jMTRuIyIvPg0KPGRz"
        + "OlNpZ25hdHVyZU1ldGhvZCBBbGdvcml0aG09Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvMDkveG1s"
        + "ZHNpZyNyc2Etc2hhMSIvPg0KPGRzOlJlZmVyZW5jZSBVUkk9IiNzMmJmZjdjNjJkMDU2MGE3YWY4"
        + "MTFjNWM1MjhiOGM3MWNjNDQ3ZmQzZTMiPg0KPGRzOlRyYW5zZm9ybXM+DQo8ZHM6VHJhbnNmb3Jt"
        + "IEFsZ29yaXRobT0iaHR0cDovL3d3dy53My5vcmcvMjAwMC8wOS94bWxkc2lnI2VudmVsb3BlZC1z"
        + "aWduYXR1cmUiLz4NCjxkczpUcmFuc2Zvcm0gQWxnb3JpdGhtPSJodHRwOi8vd3d3LnczLm9yZy8y"
        + "MDAxLzEwL3htbC1leGMtYzE0biMiLz4NCjwvZHM6VHJhbnNmb3Jtcz4NCjxkczpEaWdlc3RNZXRo"
        + "b2QgQWxnb3JpdGhtPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwLzA5L3htbGRzaWcjc2hhMSIvPg0K"
        + "PGRzOkRpZ2VzdFZhbHVlPmZsdGpXdUxQNnhmK0tBQmFkampZd3U5Rk0zMD08L2RzOkRpZ2VzdFZh"
        + "bHVlPg0KPC9kczpSZWZlcmVuY2U+DQo8L2RzOlNpZ25lZEluZm8+DQo8ZHM6U2lnbmF0dXJlVmFs"
        + "dWU+DQpxanhqdmxJNVkvZnVyN3BqSmI1Uk1FRTgyOVcvWlVidDRtTDlmYnNTTWFwNDhRaUxrdjNN"
        + "RjlvNmV4eFJ4WmJxRGQ4aHBFS1c3a0RsDQpQWmNUeWhsYU1mNlN6ZmJvaUhMbzZqYzI1cUhQUGY4"
        + "UWRzNUpxOWlKQ0lBeVlKWHk5VnZtT1RCYnZUNnJFeFVGakVOVzNoTGRSQVpaDQpIcUJ0cy9KU3Zx"
        + "WmZNT2JTY1RNPQ0KPC9kczpTaWduYXR1cmVWYWx1ZT4NCjxkczpLZXlJbmZvPg0KPGRzOlg1MDlE"
        + "YXRhPg0KPGRzOlg1MDlDZXJ0aWZpY2F0ZT4NCk1JSURMakNDQXBlZ0F3SUJBZ0lFTzZFUFdUQU5C"
        + "Z2txaGtpRzl3MEJBUXNGQURCdk1Rc3dDUVlEVlFRR0V3SlZTekVUTUJFR0ExVUUNCkNCTUtVMjl0"
        + "WlMxVGRHRjBaVEVQTUEwR0ExVUVCeE1HVEc5dVpHOXVNUm93R0FZRFZRUUtFeEZCYkdaeVpYTmpi"
        + "eUJUYjJaMGQyRnkNClpURVJNQThHQTFVRUN4TUlRMnh2ZFdRdFUxQXhDekFKQmdOVkJBTVRBbk53"
        + "TUNBWERURXlNVEl4T1RFeU1EazFOVm9ZRHpJeE1USXgNCk1USTFNVEl3T1RVMVdqQnZNUXN3Q1FZ"
        + "RFZRUUdFd0pWU3pFVE1CRUdBMVVFQ0JNS1UyOXRaUzFUZEdGMFpURVBNQTBHQTFVRUJ4TUcNClRH"
        + "OXVaRzl1TVJvd0dBWURWUVFLRXhGQmJHWnlaWE5qYnlCVGIyWjBkMkZ5WlRFUk1BOEdBMVVFQ3hN"
        + "SVEyeHZkV1F0VTFBeEN6QUoNCkJnTlZCQU1UQW5Od01JR2ZNQTBHQ1NxR1NJYjNEUUVCQVFVQUE0"
        + "R05BRENCaVFLQmdRREZVbE1KSHFxR1Yva3c0VDl4ME1YQ21yUlQNCk1sZ2xHZG1KeFlZMVJpNmJE"
        + "eDlwQ3dLNVg3NnB3VGZtREtIbWJadkg5ay9PU08wV2MwNHl3a21CeUVOOTRvZGhyUjNBb2lzaS9m"
        + "djcNCnk0UVhGbWVrQzM3eDVHUnIvTnRtNExoZGM2dklUY3pMWThiTitTY0V2MU4ycTE4Q2wzL2VY"
        + "Tm53QmlDTVcrWlVpQ09LSHdJREFRQUINCm80SFVNSUhSTUIwR0ExVWREZ1FXQkJRQW1iK1I0RCtJ"
        + "U29tczVTMGVqdzhYc3ZYQzNEQ0JvUVlEVlIwakJJR1pNSUdXZ0JRQW1iK1INCjREK0lTb21zNVMw"
        + "ZWp3OFhzdlhDM0tGenBIRXdiekVMTUFrR0ExVUVCaE1DVlVzeEV6QVJCZ05WQkFnVENsTnZiV1V0"
        + "VTNSaGRHVXgNCkR6QU5CZ05WQkFjVEJreHZibVJ2YmpFYU1CZ0dBMVVFQ2hNUlFXeG1jbVZ6WTI4"
        + "Z1UyOW1kSGRoY21VeEVUQVBCZ05WQkFzVENFTnMNCmIzVmtMVk5RTVFzd0NRWURWUVFERXdKemNJ"
        + "SUpBS0JIeW5PcWJ0MGtNQXdHQTFVZEV3UUZNQU1CQWY4d0RRWUpLb1pJaHZjTkFRRUwNCkJRQURn"
        + "WUVBUEM5bmtzbGt6WW1nVkdJODdIalVDUkdDU0dpUnpUWk12U3hvRndqOG5nb3h5SEY3Vkg5NVpK"
        + "Y2pjR25QdW9VY3NXVEcNCkdpSFlMVElrWWdJK2pWOWVtczBmdVRZMjJsV1BQQkJ3cGNFbktYOVJH"
        + "OGZONDJwemYxU3ZxRFpJWnNPUm5iWW43bmVxODB2YkpNdFgNCjZTdGV5RkFWMHpnMG5yS2s1L0Vh"
        + "Z0NVNnVPMD0NCjwvZHM6WDUwOUNlcnRpZmljYXRlPg0KPC9kczpYNTA5RGF0YT4NCjwvZHM6S2V5"
        + "SW5mbz4NCjwvZHM6U2lnbmF0dXJlPjxzYW1sOlN1YmplY3Q+DQo8c2FtbDpOYW1lSUQgRm9ybWF0"
        + "PSJ1cm46b2FzaXM6bmFtZXM6dGM6U0FNTDoyLjA6bmFtZWlkLWZvcm1hdDpwZXJzaXN0ZW50IiBO"
        + "YW1lUXVhbGlmaWVyPSJodHRwOi8vd3d3Lm15b3BlbmFtLmNvbTo5MDgwL29wZW5zc28iPnhvVnhw"
        + "Q0lLclBaZnlRdlFSd01Ka0JTSjhDUHU8L3NhbWw6TmFtZUlEPjxzYW1sOlN1YmplY3RDb25maXJt"
        + "YXRpb24gTWV0aG9kPSJ1cm46b2FzaXM6bmFtZXM6dGM6U0FNTDoyLjA6Y206YmVhcmVyIj4NCjxz"
        + "YW1sOlN1YmplY3RDb25maXJtYXRpb25EYXRhIE5vdE9uT3JBZnRlcj0iMjAxMy0wMS0yN1QyMDow"
        + "MDoyMFoiIFJlY2lwaWVudD0iaHR0cDovL2xvY2FsaG9zdDo4MDgxL3NoYXJlL29wZW5hbS50ZXN0"
        + "L3NhbWwvYXV0aG5yZXNwb25zZSIvPjwvc2FtbDpTdWJqZWN0Q29uZmlybWF0aW9uPg0KPC9zYW1s"
        + "OlN1YmplY3Q+PHNhbWw6Q29uZGl0aW9ucyBOb3RCZWZvcmU9IjIwMTMtMDEtMjdUMTk6NDA6MjBa"
        + "IiBOb3RPbk9yQWZ0ZXI9IjIwMTMtMDEtMjdUMjA6MDA6MjBaIj4NCjxzYW1sOkF1ZGllbmNlUmVz"
        + "dHJpY3Rpb24+DQo8c2FtbDpBdWRpZW5jZT5teS5hbGZyZXNjby5jb20tb3BlbmFtLnRlc3Q8L3Nh"
        + "bWw6QXVkaWVuY2U+DQo8L3NhbWw6QXVkaWVuY2VSZXN0cmljdGlvbj4NCjwvc2FtbDpDb25kaXRp"
        + "b25zPg0KPHNhbWw6QXV0aG5TdGF0ZW1lbnQgQXV0aG5JbnN0YW50PSIyMDEzLTAxLTI3VDE5OjUw"
        + "OjIwWiIgU2Vzc2lvbkluZGV4PSJzMjU4NGM5MzUyNDAxNGI4NWJmNTM1YmQyMDJiMzNhOGQ2YTM0"
        + "ZDlmMDEiPjxzYW1sOkF1dGhuQ29udGV4dD48c2FtbDpBdXRobkNvbnRleHRDbGFzc1JlZj51cm46"
        + "b2FzaXM6bmFtZXM6dGM6U0FNTDoyLjA6YWM6Y2xhc3NlczpQYXNzd29yZDwvc2FtbDpBdXRobkNv"
        + "bnRleHRDbGFzc1JlZj48L3NhbWw6QXV0aG5Db250ZXh0Pjwvc2FtbDpBdXRoblN0YXRlbWVudD48"
        + "c2FtbDpBdHRyaWJ1dGVTdGF0ZW1lbnQ+PHNhbWw6QXR0cmlidXRlIE5hbWU9IkVtYWlsIj48c2Ft"
        + "bDpBdHRyaWJ1dGVWYWx1ZSB4bWxuczp4cz0iaHR0cDovL3d3dy53My5vcmcvMjAwMS9YTUxTY2hl"
        + "bWEiIHhtbG5zOnhzaT0iaHR0cDovL3d3dy53My5vcmcvMjAwMS9YTUxTY2hlbWEtaW5zdGFuY2Ui"
        + "IHhzaTp0eXBlPSJ4czpzdHJpbmciPnVzZXIyQG9wZW5hbS50ZXN0PC9zYW1sOkF0dHJpYnV0ZVZh"
        + "bHVlPjwvc2FtbDpBdHRyaWJ1dGU+PC9zYW1sOkF0dHJpYnV0ZVN0YXRlbWVudD48L3NhbWw6QXNz"
        + "ZXJ0aW9uPjwvc2FtbHA6UmVzcG9uc2U+";
    /*
     * Decode version of the above RESPONSE
     * 
     * <?xml version="1.0" encoding="UTF-8"?>
     * <samlp:Response
     * Destination="http://localhost:8081/share/openam.test/saml/authnresponse"
     * ID="s20d2a18f54e84176420100328261a0c758661a19e" IssueInstant="2013-01-27T19:50:20Z"
     * Version="2.0" xmlns:samlp="urn:oasis:names:tc:SAML:2.0:protocol">
     * <saml:Issuer xmlns:saml="urn:oasis:names:tc:SAML:2.0:assertion">http://www.myopenam.com:9080/opensso
     * </saml:Issuer>
     * <samlp:Status xmlns:samlp="urn:oasis:names:tc:SAML:2.0:protocol">
     * <samlp:StatusCode Value="urn:oasis:names:tc:SAML:2.0:status:Success"
     * xmlns:samlp="urn:oasis:names:tc:SAML:2.0:protocol">
     * </samlp:StatusCode>
     * </samlp:Status>
     * <saml:Assertion ID="s2bff7c62d0560a7af811c5c528b8c71cc447fd3e3"
     * IssueInstant="2013-01-27T19:50:20Z" Version="2.0"
     * xmlns:saml="urn:oasis:names:tc:SAML:2.0:assertion">
     * <saml:Issuer>http://www.myopenam.com:9080/opensso</saml:Issuer>
     * <ds:Signature xmlns:ds="http://www.w3.org/2000/09/xmldsig#">
     * <ds:SignedInfo>
     * <ds:CanonicalizationMethod Algorithm="http://www.w3.org/2001/10/xml-exc-c14n#" />
     * <ds:SignatureMethod Algorithm="http://www.w3.org/2000/09/xmldsig#rsa-sha1" />
     * <ds:Reference URI="#s2bff7c62d0560a7af811c5c528b8c71cc447fd3e3">
     * <ds:Transforms>
     * <ds:Transform
     * Algorithm="http://www.w3.org/2000/09/xmldsig#enveloped-signature" />
     * <ds:Transform Algorithm="http://www.w3.org/2001/10/xml-exc-c14n#" />
     * </ds:Transforms>
     * <ds:DigestMethod Algorithm="http://www.w3.org/2000/09/xmldsig#sha1" />
     * <ds:DigestValue>fltjWuLP6xf+KABadjjYwu9FM30=</ds:DigestValue>
     * </ds:Reference>
     * </ds:SignedInfo>
     * <ds:SignatureValue>
     * qjxjvlI5Y/fur7pjJb5RMEE829W/ZUbt4mL9fbsSMap48QiLkv3MF9o6exxRxZbqDd8hpEKW7kDl
     * PZcTyhlaMf6SzfboiHLo6jc25qHPPf8Qds5Jq9iJCIAyYJXy9VvmOTBbvT6rExUFjENW3hLdRAZZ
     * HqBts/JSvqZfMObScTM=
     * </ds:SignatureValue>
     * <ds:KeyInfo>
     * <ds:X509Data>
     * <ds:X509Certificate>
     * MIIDLjCCApegAwIBAgIEO6EPWTANBgkqhkiG9w0BAQsFADBvMQswCQYDVQQGEwJVSzETMBEGA1UE
     * CBMKU29tZS1TdGF0ZTEPMA0GA1UEBxMGTG9uZG9uMRowGAYDVQQKExFBbGZyZXNjbyBTb2Z0d2Fy
     * ZTERMA8GA1UECxMIQ2xvdWQtU1AxCzAJBgNVBAMTAnNwMCAXDTEyMTIxOTEyMDk1NVoYDzIxMTIx
     * MTI1MTIwOTU1WjBvMQswCQYDVQQGEwJVSzETMBEGA1UECBMKU29tZS1TdGF0ZTEPMA0GA1UEBxMG
     * TG9uZG9uMRowGAYDVQQKExFBbGZyZXNjbyBTb2Z0d2FyZTERMA8GA1UECxMIQ2xvdWQtU1AxCzAJ
     * BgNVBAMTAnNwMIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDFUlMJHqqGV/kw4T9x0MXCmrRT
     * MlglGdmJxYY1Ri6bDx9pCwK5X76pwTfmDKHmbZvH9k/OSO0Wc04ywkmByEN94odhrR3Aoisi/fv7
     * y4QXFmekC37x5GRr/Ntm4Lhdc6vITczLY8bN+ScEv1N2q18Cl3/eXNnwBiCMW+ZUiCOKHwIDAQAB
     * o4HUMIHRMB0GA1UdDgQWBBQAmb+R4D+ISoms5S0ejw8XsvXC3DCBoQYDVR0jBIGZMIGWgBQAmb+R
     * 4D+ISoms5S0ejw8XsvXC3KFzpHEwbzELMAkGA1UEBhMCVUsxEzARBgNVBAgTClNvbWUtU3RhdGUx
     * DzANBgNVBAcTBkxvbmRvbjEaMBgGA1UEChMRQWxmcmVzY28gU29mdHdhcmUxETAPBgNVBAsTCENs
     * b3VkLVNQMQswCQYDVQQDEwJzcIIJAKBHynOqbt0kMAwGA1UdEwQFMAMBAf8wDQYJKoZIhvcNAQEL
     * BQADgYEAPC9nkslkzYmgVGI87HjUCRGCSGiRzTZMvSxoFwj8ngoxyHF7VH95ZJcjcGnPuoUcsWTG
     * GiHYLTIkYgI+jV9ems0fuTY22lWPPBBwpcEnKX9RG8fN42pzf1SvqDZIZsORnbYn7neq80vbJMtX
     * 6SteyFAV0zg0nrKk5/EagCU6uO0=
     * </ds:X509Certificate>
     * </ds:X509Data>
     * </ds:KeyInfo>
     * </ds:Signature>
     * <saml:Subject>
     * <saml:NameID Format="urn:oasis:names:tc:SAML:2.0:nameid-format:persistent"
     * NameQualifier="http://www.myopenam.com:9080/opensso">xoVxpCIKrPZfyQvQRwMJkBSJ8CPu</saml:NameID>
     * <saml:SubjectConfirmation Method="urn:oasis:names:tc:SAML:2.0:cm:bearer">
     * <saml:SubjectConfirmationData
     * NotOnOrAfter="2013-01-27T20:00:20Z"
     * Recipient="http://localhost:8081/share/openam.test/saml/authnresponse" />
     * </saml:SubjectConfirmation>
     * </saml:Subject>
     * <saml:Conditions NotBefore="2013-01-27T19:40:20Z"
     * NotOnOrAfter="2013-01-27T20:00:20Z">
     * <saml:AudienceRestriction>
     * <saml:Audience>my.alfresco.com-openam.test</saml:Audience>
     * </saml:AudienceRestriction>
     * </saml:Conditions>
     * <saml:AuthnStatement AuthnInstant="2013-01-27T19:50:20Z"
     * SessionIndex="s2584c93524014b85bf535bd202b33a8d6a34d9f01">
     * <saml:AuthnContext>
     * <saml:AuthnContextClassRef>urn:oasis:names:tc:SAML:2.0:ac:classes:Password
     * </saml:AuthnContextClassRef>
     * </saml:AuthnContext>
     * </saml:AuthnStatement>
     * <saml:AttributeStatement>
     * <saml:Attribute Name="Email">
     * <saml:AttributeValue xmlns:xs="http://www.w3.org/2001/XMLSchema"
     * xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:type="xs:string">user2@openam.test
     * </saml:AttributeValue>
     * </saml:Attribute>
     * </saml:AttributeStatement>
     * </saml:Assertion>
     * </samlp:Response>
     */

    public static final String LOGOUT_REQUEST_OPENAM = "PHNhbWxwOkxvZ291dFJlcXVlc3QgeG1sbnM6c2FtbHA9InVybjpvYXNpczpuYW1lczp0YzpTQU1M"
        + "OjIuMDpwcm90b2NvbCIgRGVzdGluYXRpb249Imh0dHA6Ly9sb2NhbGhvc3Q6ODA4MS9zaGFyZS9v"
        + "cGVuYW0udGVzdC9zYW1sL2xvZ291dHJlcXVlc3QiIElEPSJzMmEwZWY0YjI4ZDVhMjg2NjFmYWUy"
        + "YzE2MTQ4MDc5ZGViMjg0MWIyMjkiIElzc3VlSW5zdGFudD0iMjAxMy0wMS0yN1QxOTo1MDozN1oi"
        + "IE5vdE9uT3JBZnRlcj0iMjAxMy0wMS0yN1QyMDowMDozN1oiIFZlcnNpb249IjIuMCI+PHNhbWw6"
        + "SXNzdWVyIHhtbG5zOnNhbWw9InVybjpvYXNpczpuYW1lczp0YzpTQU1MOjIuMDphc3NlcnRpb24i"
        + "Pmh0dHA6Ly93d3cubXlvcGVuYW0uY29tOjkwODAvb3BlbnNzbzwvc2FtbDpJc3N1ZXI+PGRzOlNp"
        + "Z25hdHVyZSB4bWxuczpkcz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC8wOS94bWxkc2lnIyI+DQo8"
        + "ZHM6U2lnbmVkSW5mbz4NCjxkczpDYW5vbmljYWxpemF0aW9uTWV0aG9kIEFsZ29yaXRobT0iaHR0"
        + "cDovL3d3dy53My5vcmcvMjAwMS8xMC94bWwtZXhjLWMxNG4jIi8+DQo8ZHM6U2lnbmF0dXJlTWV0"
        + "aG9kIEFsZ29yaXRobT0iaHR0cDovL3d3dy53My5vcmcvMjAwMC8wOS94bWxkc2lnI3JzYS1zaGEx"
        + "Ii8+DQo8ZHM6UmVmZXJlbmNlIFVSST0iI3MyYTBlZjRiMjhkNWEyODY2MWZhZTJjMTYxNDgwNzlk"
        + "ZWIyODQxYjIyOSI+DQo8ZHM6VHJhbnNmb3Jtcz4NCjxkczpUcmFuc2Zvcm0gQWxnb3JpdGhtPSJo"
        + "dHRwOi8vd3d3LnczLm9yZy8yMDAwLzA5L3htbGRzaWcjZW52ZWxvcGVkLXNpZ25hdHVyZSIvPg0K"
        + "PGRzOlRyYW5zZm9ybSBBbGdvcml0aG09Imh0dHA6Ly93d3cudzMub3JnLzIwMDEvMTAveG1sLWV4"
        + "Yy1jMTRuIyIvPg0KPC9kczpUcmFuc2Zvcm1zPg0KPGRzOkRpZ2VzdE1ldGhvZCBBbGdvcml0aG09"
        + "Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvMDkveG1sZHNpZyNzaGExIi8+DQo8ZHM6RGlnZXN0VmFs"
        + "dWU+NzFnQ0dEZTlLZjZjRGpUaGt2M3VIRVVqdVhvPTwvZHM6RGlnZXN0VmFsdWU+DQo8L2RzOlJl"
        + "ZmVyZW5jZT4NCjwvZHM6U2lnbmVkSW5mbz4NCjxkczpTaWduYXR1cmVWYWx1ZT4NCmtVeHpzOGNE"
        + "R0VvVk9YT0RJL290UEpBQTJiWm5nVnJYWGt5OTFRU3h3OXVuQVdlM2IyWDFsNUwwc1RRbFkySjNz"
        + "OE1wVTFjTzBKV1ENCnpVcktHanNXbGEwdmlkK0hGNGo3VTA1dTM4OGU5NG9SRExNM0dHelJtWjdo"
        + "N3Z2YU9kMEplbVFnUnF2QThwZktTLzVxeU5Pa3crbHcNCituTnFaY0UrQnpmWEVnQlU3QWM9DQo8"
        + "L2RzOlNpZ25hdHVyZVZhbHVlPg0KPC9kczpTaWduYXR1cmU+PHNhbWw6TmFtZUlEIHhtbG5zOnNh"
        + "bWw9InVybjpvYXNpczpuYW1lczp0YzpTQU1MOjIuMDphc3NlcnRpb24iIEZvcm1hdD0idXJuOm9h"
        + "c2lzOm5hbWVzOnRjOlNBTUw6Mi4wOm5hbWVpZC1mb3JtYXQ6cGVyc2lzdGVudCIgTmFtZVF1YWxp"
        + "Zmllcj0iaHR0cDovL3d3dy5teW9wZW5hbS5jb206OTA4MC9vcGVuc3NvIj54b1Z4cENJS3JQWmZ5"
        + "UXZRUndNSmtCU0o4Q1B1PC9zYW1sOk5hbWVJRD48c2FtbHA6U2Vzc2lvbkluZGV4PnMyNTg0Yzkz"
        + "NTI0MDE0Yjg1YmY1MzViZDIwMmIzM2E4ZDZhMzRkOWYwMTwvc2FtbHA6U2Vzc2lvbkluZGV4Pjwv"
        + "c2FtbHA6TG9nb3V0UmVxdWVzdD4=";
    /*
     * Decode version of the above LOGOUT_REQUEST
     * 
     * <?xml version="1.0" encoding="UTF-8"?>
     * <samlp:LogoutRequest
     * Destination="http://localhost:8081/share/openam.test/saml/logoutrequest"
     * ID="s2a0ef4b28d5a28661fae2c16148079deb2841b229" IssueInstant="2013-01-27T19:50:37Z"
     * NotOnOrAfter="2013-01-27T20:00:37Z" Version="2.0"
     * xmlns:samlp="urn:oasis:names:tc:SAML:2.0:protocol">
     * <saml:Issuer xmlns:saml="urn:oasis:names:tc:SAML:2.0:assertion">http://www.myopenam.com:9080/opensso
     * </saml:Issuer>
     * <ds:Signature xmlns:ds="http://www.w3.org/2000/09/xmldsig#">
     * <ds:SignedInfo>
     * <ds:CanonicalizationMethod Algorithm="http://www.w3.org/2001/10/xml-exc-c14n#" />
     * <ds:SignatureMethod Algorithm="http://www.w3.org/2000/09/xmldsig#rsa-sha1" />
     * <ds:Reference URI="#s2a0ef4b28d5a28661fae2c16148079deb2841b229">
     * <ds:Transforms>
     * <ds:Transform
     * Algorithm="http://www.w3.org/2000/09/xmldsig#enveloped-signature" />
     * <ds:Transform Algorithm="http://www.w3.org/2001/10/xml-exc-c14n#" />
     * </ds:Transforms>
     * <ds:DigestMethod Algorithm="http://www.w3.org/2000/09/xmldsig#sha1" />
     * <ds:DigestValue>71gCGDe9Kf6cDjThkv3uHEUjuXo=</ds:DigestValue>
     * </ds:Reference>
     * </ds:SignedInfo>
     * <ds:SignatureValue>
     * kUxzs8cDGEoVOXODI/otPJAA2bZngVrXXky91QSxw9unAWe3b2X1l5L0sTQlY2J3s8MpU1cO0JWQ
     * zUrKGjsWla0vid+HF4j7U05u388e94oRDLM3GGzRmZ7h7vvaOd0JemQgRqvA8pfKS/5qyNOkw+lw
     * +nNqZcE+BzfXEgBU7Ac=
     * </ds:SignatureValue>
     * </ds:Signature>
     * <saml:NameID Format="urn:oasis:names:tc:SAML:2.0:nameid-format:persistent"
     * NameQualifier="http://www.myopenam.com:9080/opensso"
     * xmlns:saml="urn:oasis:names:tc:SAML:2.0:assertion">xoVxpCIKrPZfyQvQRwMJkBSJ8CPu
     * </saml:NameID>
     * <samlp:SessionIndex>s2584c93524014b85bf535bd202b33a8d6a34d9f01
     * </samlp:SessionIndex>
     * </samlp:LogoutRequest>
     */

    public static final String LOGOUT_RESPONSE_OPENAM = "PHNhbWxwOkxvZ291dFJlc3BvbnNlIHhtbG5zOnNhbWxwPSJ1cm46b2FzaXM6bmFtZXM6dGM6U0FN"
        + "TDoyLjA6cHJvdG9jb2wiIERlc3RpbmF0aW9uPSJodHRwOi8vbG9jYWxob3N0OjgwODEvc2hhcmUv"
        + "b3BlbmFtLnRlc3Qvc2FtbC9sb2dvdXRyZXNwb25zZSIgSUQ9InM5NDI0OTg5M2FhMzFmZTgxMTI5"
        + "OTZjYWZiODhlNWJiMDkzNjc1NWM4IiBJblJlc3BvbnNlVG89Il8xOWQ5ODlkLWM3ZjgtNGVhYy1i"
        + "MjdlLWU5YmMyZWM0YWIyOCIgSXNzdWVJbnN0YW50PSIyMDEzLTAxLTI3VDE5OjUyOjIwWiIgVmVy"
        + "c2lvbj0iMi4wIj4NCjxzYW1sOklzc3VlciB4bWxuczpzYW1sPSJ1cm46b2FzaXM6bmFtZXM6dGM6"
        + "U0FNTDoyLjA6YXNzZXJ0aW9uIj5odHRwOi8vd3d3Lm15b3BlbmFtLmNvbTo5MDgwL29wZW5zc288"
        + "L3NhbWw6SXNzdWVyPjxkczpTaWduYXR1cmUgeG1sbnM6ZHM9Imh0dHA6Ly93d3cudzMub3JnLzIw"
        + "MDAvMDkveG1sZHNpZyMiPg0KPGRzOlNpZ25lZEluZm8+DQo8ZHM6Q2Fub25pY2FsaXphdGlvbk1l"
        + "dGhvZCBBbGdvcml0aG09Imh0dHA6Ly93d3cudzMub3JnLzIwMDEvMTAveG1sLWV4Yy1jMTRuIyIv"
        + "Pg0KPGRzOlNpZ25hdHVyZU1ldGhvZCBBbGdvcml0aG09Imh0dHA6Ly93d3cudzMub3JnLzIwMDAv"
        + "MDkveG1sZHNpZyNyc2Etc2hhMSIvPg0KPGRzOlJlZmVyZW5jZSBVUkk9IiNzOTQyNDk4OTNhYTMx"
        + "ZmU4MTEyOTk2Y2FmYjg4ZTViYjA5MzY3NTVjOCI+DQo8ZHM6VHJhbnNmb3Jtcz4NCjxkczpUcmFu"
        + "c2Zvcm0gQWxnb3JpdGhtPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwLzA5L3htbGRzaWcjZW52ZWxv"
        + "cGVkLXNpZ25hdHVyZSIvPg0KPGRzOlRyYW5zZm9ybSBBbGdvcml0aG09Imh0dHA6Ly93d3cudzMu"
        + "b3JnLzIwMDEvMTAveG1sLWV4Yy1jMTRuIyIvPg0KPC9kczpUcmFuc2Zvcm1zPg0KPGRzOkRpZ2Vz"
        + "dE1ldGhvZCBBbGdvcml0aG09Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvMDkveG1sZHNpZyNzaGEx"
        + "Ii8+DQo8ZHM6RGlnZXN0VmFsdWU+eklNektNZld3Nm54dCtrRjBubktDS3RDZnY4PTwvZHM6RGln"
        + "ZXN0VmFsdWU+DQo8L2RzOlJlZmVyZW5jZT4NCjwvZHM6U2lnbmVkSW5mbz4NCjxkczpTaWduYXR1"
        + "cmVWYWx1ZT4NClVjUE16a0FNKzh5Q21XVmdYZnRZejZXTWxyeVNPVGpaV1h6ZFVXdllNcjVkWjV3"
        + "b0FJaVNuWG44RGJQT05hendLcjF6SktlQzVEbXkNCkFjTStnOVkvOTdMT3JKUDlZTXpxeU12V2FJ"
        + "Zll5dFV1aGxpMlRLKzJhV1lyK0d3NmhobkpQd0VYdjZkUXU5czU5clZxdFdxN25qYUENCkk1TXRC"
        + "OUlyZWZnM1JPa2Z3bm89DQo8L2RzOlNpZ25hdHVyZVZhbHVlPg0KPC9kczpTaWduYXR1cmU+DQo8"
        + "c2FtbHA6U3RhdHVzPg0KPHNhbWxwOlN0YXR1c0NvZGUgVmFsdWU9InVybjpvYXNpczpuYW1lczp0"
        + "YzpTQU1MOjIuMDpzdGF0dXM6U3VjY2VzcyI+DQo8L3NhbWxwOlN0YXR1c0NvZGU+DQo8L3NhbWxw"
        + "OlN0YXR1cz4NCjwvc2FtbHA6TG9nb3V0UmVzcG9uc2U+";
    /*
     * Decode version of the above LOGOUT_RESPONSE
     * 
     * <?xml version="1.0" encoding="UTF-8"?>
     * <samlp:LogoutResponse
     * Destination="http://localhost:8081/share/openam.test/saml/logoutresponse"
     * ID="s94249893aa31fe8112996cafb88e5bb0936755c8" InResponseTo="_19d989d-c7f8-4eac-b27e-e9bc2ec4ab28"
     * IssueInstant="2013-01-27T19:52:20Z" Version="2.0"
     * xmlns:samlp="urn:oasis:names:tc:SAML:2.0:protocol">
     * <saml:Issuer xmlns:saml="urn:oasis:names:tc:SAML:2.0:assertion">http://www.myopenam.com:9080/opensso
     * </saml:Issuer>
     * <ds:Signature xmlns:ds="http://www.w3.org/2000/09/xmldsig#">
     * <ds:SignedInfo>
     * <ds:CanonicalizationMethod Algorithm="http://www.w3.org/2001/10/xml-exc-c14n#" />
     * <ds:SignatureMethod Algorithm="http://www.w3.org/2000/09/xmldsig#rsa-sha1" />
     * <ds:Reference URI="#s94249893aa31fe8112996cafb88e5bb0936755c8">
     * <ds:Transforms>
     * <ds:Transform
     * Algorithm="http://www.w3.org/2000/09/xmldsig#enveloped-signature" />
     * <ds:Transform Algorithm="http://www.w3.org/2001/10/xml-exc-c14n#" />
     * </ds:Transforms>
     * <ds:DigestMethod Algorithm="http://www.w3.org/2000/09/xmldsig#sha1" />
     * <ds:DigestValue>zIMzKMfWw6nxt+kF0nnKCKtCfv8=</ds:DigestValue>
     * </ds:Reference>
     * </ds:SignedInfo>
     * <ds:SignatureValue>
     * UcPMzkAM+8yCmWVgXftYz6WMlrySOTjZWXzdUWvYMr5dZ5woAIiSnXn8DbPONazwKr1zJKeC5Dmy
     * AcM+g9Y/97LOrJP9YMzqyMvWaIfYytUuhli2TK+2aWYr+Gw6hhnJPwEXv6dQu9s59rVqtWq7njaA
     * I5MtB9Irefg3ROkfwno=
     * </ds:SignatureValue>
     * </ds:Signature>
     * <samlp:Status>
     * <samlp:StatusCode Value="urn:oasis:names:tc:SAML:2.0:status:Success">
     * </samlp:StatusCode>
     * </samlp:Status>
     * </samlp:LogoutResponse>
     */

    private static final String KEYSTORE = "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";

    private static final String CERTIFICATE = "MIIDLjCCApegAwIBAgIEO6EPWTANBgkqhkiG9w0BAQsFADBvMQswCQYDVQQGEwJVSzETMBEGA1UE"
        + "CBMKU29tZS1TdGF0ZTEPMA0GA1UEBxMGTG9uZG9uMRowGAYDVQQKExFBbGZyZXNjbyBTb2Z0d2Fy"
        + "ZTERMA8GA1UECxMIQ2xvdWQtU1AxCzAJBgNVBAMTAnNwMCAXDTEyMTIxOTEyMDk1NVoYDzIxMTIx"
        + "MTI1MTIwOTU1WjBvMQswCQYDVQQGEwJVSzETMBEGA1UECBMKU29tZS1TdGF0ZTEPMA0GA1UEBxMG"
        + "TG9uZG9uMRowGAYDVQQKExFBbGZyZXNjbyBTb2Z0d2FyZTERMA8GA1UECxMIQ2xvdWQtU1AxCzAJ"
        + "BgNVBAMTAnNwMIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDFUlMJHqqGV/kw4T9x0MXCmrRT"
        + "MlglGdmJxYY1Ri6bDx9pCwK5X76pwTfmDKHmbZvH9k/OSO0Wc04ywkmByEN94odhrR3Aoisi/fv7"
        + "y4QXFmekC37x5GRr/Ntm4Lhdc6vITczLY8bN+ScEv1N2q18Cl3/eXNnwBiCMW+ZUiCOKHwIDAQAB"
        + "o4HUMIHRMB0GA1UdDgQWBBQAmb+R4D+ISoms5S0ejw8XsvXC3DCBoQYDVR0jBIGZMIGWgBQAmb+R"
        + "4D+ISoms5S0ejw8XsvXC3KFzpHEwbzELMAkGA1UEBhMCVUsxEzARBgNVBAgTClNvbWUtU3RhdGUx"
        + "DzANBgNVBAcTBkxvbmRvbjEaMBgGA1UEChMRQWxmcmVzY28gU29mdHdhcmUxETAPBgNVBAsTCENs"
        + "b3VkLVNQMQswCQYDVQQDEwJzcIIJAKBHynOqbt0kMAwGA1UdEwQFMAMBAf8wDQYJKoZIhvcNAQEL"
        + "BQADgYEAPC9nkslkzYmgVGI87HjUCRGCSGiRzTZMvSxoFwj8ngoxyHF7VH95ZJcjcGnPuoUcsWTG"
        + "GiHYLTIkYgI+jV9ems0fuTY22lWPPBBwpcEnKX9RG8fN42pzf1SvqDZIZsORnbYn7neq80vbJMtX"
        + "6SteyFAV0zg0nrKk5/EagCU6uO0=";

    private static final String UNKNOWN_CERTIFICATE = "MIICGTCCAYKgAwIBAgIGATxy+x1iMA0GCSqGSIb3DQEBBQUAME8xCzAJBgNVBAYTAlVLMQ8wDQYD"
        + "VQQHEwZMb25kb24xCzAJBgNVBAoTAnR0MRMwEQYDVQQLEwp0ZXN0IGNsb3VkMQ0wCwYDVQQDEwRU"
        + "ZXN0MCAXDTEzMDEyNTE4MzIwNVoYDzIxMTMwMTAxMTgzMjA1WjBPMQswCQYDVQQGEwJVSzEPMA0G"
        + "A1UEBxMGTG9uZG9uMQswCQYDVQQKEwJ0dDETMBEGA1UECxMKdGVzdCBjbG91ZDENMAsGA1UEAxME"
        + "VGVzdDCBnzANBgkqhkiG9w0BAQEFAAOBjQAwgYkCgYEAgrU3tSUl0KL7zObURZ4D967e5MrbAyFs"
        + "wt7xHtVbMFaxOmXF7ZxqJhm491GZ7k/5HXJQBWuKyk+8fxcvBYCdAZrcgpv6sMon0BkbsY4tBDh1"
        + "5BsVS87zly3mVkYGe6llLLWNfEwJQNswRcf7/U+6AwOmstUJTp5HEY6htIGswhcCAwEAATANBgkq"
        + "hkiG9w0BAQUFAAOBgQABDbZirchONg35HJ8vkJzfi6pE/MXcflcSnVRdFhpoBVtHdIAPavluhP8j"
        + "e3rNfuFO0Ie7HlQ1L/w9X8lT+pB0OD18ZuEpkGK8L9yF2inlVfM4j4tSyVVicTxwdH50s2VTFkg9"
        + "l7VSuIMbi78yo1PQU+bD4Gji4n+AAKpWQoZOaA==";

    private static final String EXPIRED_CERTIFICATE = "MIIDQzCCAqygAwIBAgIJAN0xcB0jsYchMA0GCSqGSIb3DQEBBQUAMHUxCzAJBgNVBAYTAlVLMRMw"
        + "EQYDVQQIEwpTb21lLVN0YXRlMQ8wDQYDVQQHEwZMb25kb24xGjAYBgNVBAoTEUFsZnJlc2NvIHNv"
        + "ZnR3YXJlMQ4wDAYDVQQLEwVDTG91ZDEUMBIGA1UEAxMLSmFtYWwgS2FhYmkwHhcNMTIxMDI0MTY0"
        + "OTEyWhcNMTIxMTIzMTY0OTEyWjB1MQswCQYDVQQGEwJVSzETMBEGA1UECBMKU29tZS1TdGF0ZTEP"
        + "MA0GA1UEBxMGTG9uZG9uMRowGAYDVQQKExFBbGZyZXNjbyBzb2Z0d2FyZTEOMAwGA1UECxMFQ0xv"
        + "dWQxFDASBgNVBAMTC0phbWFsIEthYWJpMIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCcCr5m"
        + "FYJnRixtAUOAHTrObkcrVAjvPBROWEkho6RFnbc8ZfEMhPSaCIK9euLBNYIemW1eupFUTzDPsKBY"
        + "y6UtGEpUTjPwtLGFDRjZn30a4Cadra5ZTBhJswQQ+djuF90WOvX/Nh1qI+2KZ6fsKT0wuRyrVR2Q"
        + "Zfe4Qq9NE0z/HwIDAQABo4HaMIHXMB0GA1UdDgQWBBTKs1GXoRFs+ApAToQnHa1gFzuLezCBpwYD"
        + "VR0jBIGfMIGcgBTKs1GXoRFs+ApAToQnHa1gFzuLe6F5pHcwdTELMAkGA1UEBhMCVUsxEzARBgNV"
        + "BAgTClNvbWUtU3RhdGUxDzANBgNVBAcTBkxvbmRvbjEaMBgGA1UEChMRQWxmcmVzY28gc29mdHdh"
        + "cmUxDjAMBgNVBAsTBUNMb3VkMRQwEgYDVQQDEwtKYW1hbCBLYWFiaYIJAN0xcB0jsYchMAwGA1Ud"
        + "EwQFMAMBAf8wDQYJKoZIhvcNAQEFBQADgYEAUFOCGPVw5HiNGUDj23J9JbkmlOM7VTKY0CVXTxCl"
        + "1vCtRtLS5VYCqq4eZw/Dnwv4PdJKERMXgs/53qdfxRDV6O4kNxxHg4cVOotYBrknmiys7qfKJ6cd"
        + "kcjJTxGpCI5UyytSJVf6rdsaQf4WUnXVzcoipV28e3Dqna2byPxTAfk=";

    private static final String KEYSTORE_PASSWORD = "password";
    private static final String ALIAS = "my.alfresco.com";
    private static final String ALIAS_PASSWORD = "password";
    private static final String RESOURCE_PREFIX = "org/alfresco/module/org_alfresco_module_cloud/authentication/saml/";
    private static ParserPool parserPool;
    private static UnmarshallerFactory unmarshallerFactory;

    static
    {
        try
        {
            DefaultBootstrap.bootstrap();
        }
        catch(ConfigurationException e)
        {
            e.printStackTrace();
        }

        parserPool = Configuration.getParserPool();
        unmarshallerFactory = Configuration.getUnmarshallerFactory();
    }

    private SAMLTestHelper()
    {
    }

    public static <T extends XMLObject> T unmarshallElement(String xmlFilename) throws Exception
    {
        Document document = parseXMLDocument(xmlFilename, true);
        T object = unmarshallElementImpl(document);

        assertNotNull(object);
        return object;
    }

    public static <T extends XMLObject> T unmarshallBase64EncodedElement(String base64EncodedElement) throws Exception
    {
        Document document = parseXMLDocument(base64EncodedElement, false);
        T object = unmarshallElementImpl(document);

        assertNotNull(object);
        return object;
    }

    public static <T extends XMLObject> T unmarshallElement(InputStream inputStream) throws Exception
    {
        Document document = parserPool.parse(inputStream);
        T object = unmarshallElementImpl(document);

        assertNotNull(object);
        return object;
    }

    private static <T extends XMLObject> T unmarshallElementImpl(Document document) throws Exception
    {
        Element element = document.getDocumentElement();
        Unmarshaller unmarshaller = getUnmarshaller(element);
        @SuppressWarnings("unchecked")
        T object = (T)unmarshaller.unmarshall(element);
        return object;
    }

    public static Document parseXMLDocument(String str, boolean isFile) throws XMLParserException,
        FileNotFoundException
    {

        Document doc = (isFile) ? parserPool.parse(new FileInputStream(getResourceFile(str))) : parserPool
            .parse(new ByteArrayInputStream(Base64.decodeBase64(str)));
        return doc;
    }

    public static File getResourceFile(String xmlFilename) throws FileNotFoundException
    {
        URL url = SAMLTestHelper.class.getClassLoader().getResource(RESOURCE_PREFIX + xmlFilename);
        if(url == null)
        {
            fail("Cannot get the resource: " + xmlFilename);
        }
        return ResourceUtils.getFile(url);
    }

    public static Unmarshaller getUnmarshaller(Element element)
    {
        Unmarshaller unmarshaller = unmarshallerFactory.getUnmarshaller(element);
        if(unmarshaller == null)
        {
            fail("no unmarshaller registered for " + (element == null ? null : element.getNamespaceURI()));
        }
        return unmarshaller;
    }

    public static SAMLTrustEngineStore buildTestTrustEngine(final X509Certificate cert)
    {
        return new SAMLTrustEngineStore()
        {
            @Override
            public TrustEngine<Signature> getTrustEngine(String tenantDomain)
            {
                BasicX509Credential basicX509Cred = new BasicX509Credential();
                basicX509Cred.setEntityCertificate(cert);
                basicX509Cred.setEntityId(tenantDomain);

                List<Credential> trustedCredentials = new ArrayList<Credential>(1);
                trustedCredentials.add(basicX509Cred);

                TrustEngine<Signature> trustEngine = new ExplicitKeySignatureTrustEngine(
                    new CollectionCredentialResolver(trustedCredentials), Configuration
                        .getGlobalSecurityConfiguration().getDefaultKeyInfoCredentialResolver());

                return trustEngine;
            }
        };
    }

    public static X509Certificate getDefaultCertificate()
    {
        X509Certificate cert = SAMLCertificateUtil.generateCertificate(SAMLCertificateUtil
            .decodeCertificate(CERTIFICATE));
        return cert;
    }

    public static X509Certificate getUnknownCertificate()
    {
        X509Certificate cert = SAMLCertificateUtil.generateCertificate(SAMLCertificateUtil
            .decodeCertificate(UNKNOWN_CERTIFICATE));
        return cert;
    }

    public static X509Certificate getExpiredCertificate()
    {
        X509Certificate cert = SAMLCertificateUtil.generateCertificate(SAMLCertificateUtil
            .decodeCertificate(EXPIRED_CERTIFICATE));
        return cert;
    }

    public static String getResourceAsBase64EncodedString(String xmlFilename) throws FileNotFoundException, IOException
    {
        return Base64.encodeBase64String(FileCopyUtils.copyToByteArray(getResourceFile(xmlFilename)));
    }

    public static Credential getTestSigningCredential() throws Exception
    {
        KeyStore ks = KeyStore.getInstance("JCEKS");

        ks.load(new ByteArrayInputStream(Base64.decodeBase64(KEYSTORE.getBytes())), KEYSTORE_PASSWORD.toCharArray());

        Map<String, String> privateKeyPasswordsByAlias = new HashMap<String, String>();
        privateKeyPasswordsByAlias.put(ALIAS, ALIAS_PASSWORD);
        KeyStoreCredentialResolver keyStoreCredentialResolver = new KeyStoreCredentialResolver(ks,
            privateKeyPasswordsByAlias);

        CriteriaSet criteriaSet = new CriteriaSet();
        criteriaSet.add(new EntityIDCriteria(ALIAS));
        criteriaSet.add(new UsageCriteria(UsageType.SIGNING));

        return keyStoreCredentialResolver.resolveSingle(criteriaSet);
    }

    public static Response buildValidTestResponse()
    {
        return buildValidTestResponse("test1@test.com");
    }

    public static Response buildValidTestResponse(String userId)
    {
        Response response = buildTestResponseWithoutAttribute();

        Attribute attribute = SAMLUtil.buildXMLObject(Attribute.DEFAULT_ELEMENT_NAME);
        attribute.setNameFormat(Attribute.BASIC);
        attribute.setName("Email");

        XSStringBuilder sb = (XSStringBuilder)Configuration.getBuilderFactory().getBuilder(XSString.TYPE_NAME);
        XSString attValue = (XSString)sb.buildObject(AttributeValue.DEFAULT_ELEMENT_NAME, XSString.TYPE_NAME);
        attValue.setValue(userId);
        attribute.getAttributeValues().add(attValue);
        response.getAssertions().get(0).getAttributeStatements().get(0).getAttributes().add(attribute);
        
        return response;
    }
    
    public static Response buildValidTestResponseWithXSAnyType(String userId)
    {
        Response response = buildTestResponseWithoutAttribute();
        Attribute attribute = SAMLUtil.buildXMLObject(Attribute.DEFAULT_ELEMENT_NAME);
        attribute.setNameFormat(Attribute.URI_REFERENCE);
        attribute.setName("Email");

        XSAnyBuilder xsAnyBuilder = (XSAnyBuilder)Configuration.getBuilderFactory().getBuilder(XSAny.TYPE_NAME);
        XSAny anyValue = xsAnyBuilder.buildObject(AttributeValue.DEFAULT_ELEMENT_NAME);
        anyValue.setTextContent(userId);
        attribute.getAttributeValues().add(anyValue);
        response.getAssertions().get(0).getAttributeStatements().get(0).getAttributes().add(attribute);
        return response;
    }

    public static Response buildTestResponseWithoutAttribute()
    {
        Response response = SAMLUtil.buildXMLObject(Response.DEFAULT_ELEMENT_NAME);

        response.setDestination("http://sp-domain.com/acs");

        Issuer issuer = SAMLUtil.buildXMLObject(Issuer.DEFAULT_ELEMENT_NAME);
        issuer.setValue("veryCoolIssuer");
        issuer.setFormat(NameIDType.ENTITY);
        response.setIssuer(issuer);

        Status status = SAMLUtil.buildXMLObject(Status.DEFAULT_ELEMENT_NAME);
        StatusCode statusCode = SAMLUtil.buildXMLObject(StatusCode.DEFAULT_ELEMENT_NAME);
        statusCode.setValue(StatusCode.SUCCESS_URI);
        status.setStatusCode(statusCode);

        response.setStatus(status);
        response.setID(SAMLUtil.generateUUID());

        Assertion assertion = SAMLUtil.buildXMLObject(Assertion.DEFAULT_ELEMENT_NAME);
        Issuer issuer2 = SAMLUtil.buildXMLObject(Issuer.DEFAULT_ELEMENT_NAME);
        issuer2.setValue("veryCoolIssuer");
        issuer2.setFormat(NameIDType.ENTITY);
        assertion.setIssuer(issuer2);
        assertion.setID(SAMLUtil.generateUUID());
        DateTime dateTime = SAMLUtil.getJodaCurrentDateTime();
        assertion.setIssueInstant(dateTime);

        NameID nameid = SAMLUtil.generateNameID("test1@test.com", NameIDType.UNSPECIFIED);

        Subject subject = SAMLUtil.buildXMLObject(Subject.DEFAULT_ELEMENT_NAME);
        subject.setNameID(nameid);
        SubjectConfirmation subjectConfirmation = SAMLUtil.buildXMLObject(SubjectConfirmation.DEFAULT_ELEMENT_NAME);
        subjectConfirmation.setMethod(SubjectConfirmation.METHOD_BEARER);

        SubjectConfirmationData confirmationData = SAMLUtil
            .buildXMLObject(SubjectConfirmationData.DEFAULT_ELEMENT_NAME);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(dateTime.getMillis());
        calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE) + 10);
        DateTime notOnOrAfter = new DateTime(calendar.getTimeInMillis());
        confirmationData.setNotOnOrAfter(notOnOrAfter);
        confirmationData.setRecipient("coolRecipient");

        subjectConfirmation.setSubjectConfirmationData(confirmationData);
        subject.getSubjectConfirmations().add(subjectConfirmation);
        assertion.setSubject(subject);

        Conditions conditions = SAMLUtil.buildXMLObject(Conditions.DEFAULT_ELEMENT_NAME);
        conditions.setNotOnOrAfter(notOnOrAfter);
        conditions.setNotBefore(dateTime);

        AudienceRestriction audienceRestriction = SAMLUtil.buildXMLObject(AudienceRestriction.DEFAULT_ELEMENT_NAME);
        Audience audience = SAMLUtil.buildXMLObject(Audience.DEFAULT_ELEMENT_NAME);
        audience.setAudienceURI("my.alfresco.com");
        audienceRestriction.getAudiences().add(audience);

        conditions.getAudienceRestrictions().add(audienceRestriction);
        assertion.setConditions(conditions);

        AuthnStatement authnStatement = SAMLUtil.buildXMLObject(AuthnStatement.DEFAULT_ELEMENT_NAME);
        authnStatement.setAuthnInstant(SAMLUtil.getJodaCurrentDateTime());
        authnStatement.setSessionIndex(SAMLUtil.generateUUID());

        AuthnContext authnContext = SAMLUtil.buildXMLObject(AuthnContext.DEFAULT_ELEMENT_NAME);
        AuthnContextClassRef authnContextClassRef = SAMLUtil.buildXMLObject(AuthnContextClassRef.DEFAULT_ELEMENT_NAME);
        authnContextClassRef.setAuthnContextClassRef(AuthnContext.PASSWORD_AUTHN_CTX);
        authnContext.setAuthnContextClassRef(authnContextClassRef);
        authnStatement.setAuthnContext(authnContext);
        assertion.getAuthnStatements().add(authnStatement);

        AttributeStatement attributeStatement = SAMLUtil.buildXMLObject(AttributeStatement.DEFAULT_ELEMENT_NAME);
        assertion.getAttributeStatements().add(attributeStatement);

        response.setIssueInstant(dateTime);
        response.getAssertions().add(assertion);

        return response;
    }

    public static Response buildTestResponseWithoutEmailAttribute()
    {
        Response response = buildTestResponseWithoutAttribute();

        Attribute attribute = SAMLUtil.buildXMLObject(Attribute.DEFAULT_ELEMENT_NAME);
        attribute.setNameFormat(Attribute.BASIC);
        attribute.setName("User ID");

        XSStringBuilder sb = (XSStringBuilder)Configuration.getBuilderFactory().getBuilder(XSString.TYPE_NAME);
        XSString attValue = (XSString)sb.buildObject(AttributeValue.DEFAULT_ELEMENT_NAME, XSString.TYPE_NAME);
        attValue.setValue("test1");
        attribute.getAttributeValues().add(attValue);
        response.getAssertions().get(0).getAttributeStatements().get(0).getAttributes().add(attribute);

        return response;
    }

    public static Response buildTestResponseWithInvalidEmailAttribute()
    {
        Response response = buildTestResponseWithoutAttribute();

        Attribute attribute = SAMLUtil.buildXMLObject(Attribute.DEFAULT_ELEMENT_NAME);
        attribute.setNameFormat(Attribute.BASIC);
        attribute.setName("Email");

        XSStringBuilder sb = (XSStringBuilder)Configuration.getBuilderFactory().getBuilder(XSString.TYPE_NAME);
        XSString attValue = (XSString)sb.buildObject(AttributeValue.DEFAULT_ELEMENT_NAME, XSString.TYPE_NAME);
        attValue.setValue("test1");
        attribute.getAttributeValues().add(attValue);
        response.getAssertions().get(0).getAttributeStatements().get(0).getAttributes().add(attribute);

        return response;
    }

    public static Response buildTestResponseWithUnsuccessfulStatus()
    {
        Response response = buildValidTestResponse();

        Status status = SAMLUtil.buildXMLObject(Status.DEFAULT_ELEMENT_NAME);
        StatusCode statusCode = SAMLUtil.buildXMLObject(StatusCode.DEFAULT_ELEMENT_NAME);
        statusCode.setValue(StatusCode.AUTHN_FAILED_URI);
        status.setStatusCode(statusCode);
        response.setStatus(status);

        return response;
    }
}
