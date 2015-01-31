package org.alfresco.share.util.httpCore;

import com.google.gson.Gson;
import org.alfresco.share.util.httpCore.exceptions.EmulatorException;
import org.alfresco.share.util.httpCore.exceptions.ResponseCodeException;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Set;

import static org.apache.http.client.protocol.ClientContext.COOKIE_STORE;

public class CoreHelper
{

    protected final Gson gson = new Gson();

    public URI makeURI(String url, Object... urlParts) throws URISyntaxException {
        if (urlParts != null || urlParts.length != 0) {
            url = String.format(url, urlParts);
        }
        return new URI(url);
    }


    public HttpEntity makeJsonEntity(Object json) throws UnsupportedEncodingException {
        return new StringEntity(makeJson(json));
    }

    private String makeJson(Object jsonBean) {
        return gson.toJson(jsonBean);
    }

    public HttpEntity makeParamsEntity(List<NameValuePair> params) {
        return new UrlEncodedFormEntity(params, Charset.forName("UTF-8"));
    }

    public <T> T makeJsonObjFromResponse(Response response, Type objClass) {
        return gson.fromJson(response.getResponse(),
                objClass);
    }

    public void checkStatus(Response response, int statusCode, String errorMessage) {
        if (response.getStatusCode() != statusCode) {
            throw new ResponseCodeException(errorMessage + " [Status code:" + response.getStatusCode() + "]");
        }
    }

    public void processException(Exception e) throws EmulatorException {
        e.printStackTrace();
        throw new EmulatorException();
    }

    public String mutateNodeRefToUrl(String nodeRef) {
        return nodeRef.replace("://", "/");
    }

    public void checkText(Response response, String text, String errorMessage) {
        if(!response.toString().contains(text)) {
            throw new ResponseCodeException(errorMessage + " [Response: " + response.toString() + "]");
        }
    }

    public HttpContext makeContextFrom(WebDriver driver)
    {
        HttpContext context = new BasicHttpContext();
        BasicCookieStore basicCookieStore = getCookieFrom(driver);
        context.setAttribute(COOKIE_STORE, basicCookieStore);
        return context;
    }

    private BasicCookieStore getCookieFrom(WebDriver driver)
    {
        Set<Cookie> tempCookies = driver.manage().getCookies();
        Cookie[] sCookies = tempCookies.toArray(new Cookie[tempCookies.size()]);
        BasicCookieStore basicCookieStore = new BasicCookieStore();
        for (Cookie sCookie : sCookies)
        {
            BasicClientCookie basicClientCookie = new BasicClientCookie(sCookie.getName(), sCookie.getValue());
            basicClientCookie.setDomain(sCookie.getDomain());
            basicClientCookie.setPath(sCookie.getPath());
            basicClientCookie.setExpiryDate(sCookie.getExpiry());
            basicClientCookie.setSecure(sCookie.isSecure());
            basicCookieStore.addCookie(basicClientCookie);
        }
        return basicCookieStore;
    }
}
