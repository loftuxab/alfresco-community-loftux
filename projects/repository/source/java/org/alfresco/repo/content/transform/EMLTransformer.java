/*
 * #%L
 * Alfresco Repository
 * %%
 * Copyright (C) 2005 - 2016 Alfresco Software Limited
 * %%
 * This file is part of the Alfresco software. 
 * If the software was purchased under a paid Alfresco license, the terms of 
 * the paid license agreement will prevail.  Otherwise, the software is 
 * provided under the following open source license terms:
 * 
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

package org.alfresco.repo.content.transform;

import java.io.IOException;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;

import com.sun.mail.util.BASE64DecoderStream;
import org.alfresco.repo.content.MimetypeMap;
import org.alfresco.repo.content.transform.AbstractContentTransformer2;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.TransformationOptions;
import org.apache.tika.io.TikaInputStream;

/**
 * Uses javax.mail.MimeMessage to generate plain text versions of RFC822 email
 * messages. Searches for all text content parts, and returns them. Any
 * attachments are ignored. TIKA Note - could be replaced with the Tika email
 * parser. Would require a recursing parser to be specified, but not the full
 * Auto one (we don't want attachments), just one containing text and html
 * related parsers.
 */
public class EMLTransformer extends AbstractContentTransformer2
{

    private String transformTo = "text";

    public void setTransformTo(String transformTo) {
        this.transformTo = transformTo;
    }

    @Override
    public boolean isTransformableMimetype(String sourceMimetype, String targetMimetype, TransformationOptions options)
    {
        if (!MimetypeMap.MIMETYPE_RFC822.equals(sourceMimetype) ){
            return false;
        }
        if(transformTo.equalsIgnoreCase("text")) {
            if(!(MimetypeMap.MIMETYPE_TEXT_PLAIN.equals(targetMimetype))){
                return false;
            }
        }
        if(transformTo.equalsIgnoreCase("html")) {
            if(!(MimetypeMap.MIMETYPE_HTML.equals(targetMimetype))){
                return false;
            }
        }

        return true;
    }

    @Override
    public String getComments(boolean available)
    {
        return onlySupports(MimetypeMap.MIMETYPE_RFC822, MimetypeMap.MIMETYPE_TEXT_PLAIN, available);
    }

    @Override
    protected void transformInternal(ContentReader reader, ContentWriter writer, TransformationOptions options)
            throws Exception
    {
        TikaInputStream tikaInputStream = null;
        try
        {
            // wrap the given stream to a TikaInputStream instance
            tikaInputStream = TikaInputStream.get(reader.getContentInputStream());

            MimeMessage mimeMessage = new MimeMessage(Session.getDefaultInstance(new Properties()), tikaInputStream);

            final StringBuilder sb = new StringBuilder();
            Object content = mimeMessage.getContent();
            if (content instanceof Multipart) {
                sb.append(processMultiPart((Multipart) content));
            }
            else
            {
                // Check if the mail is actually multipart, if so throw an error
                if(content.toString().contains("multipart/")) {
                    throw new Exception("RFC822 Multipart detection error: Email is multipart but ");
                }

                String emailContent;
                if(content instanceof BASE64DecoderStream) {
                    // Probably an MS Exchange email with winmail.dat only. Ignore for now
                    emailContent = "";
                } else {
                    emailContent = (String)content;
                }

                if (transformTo.equalsIgnoreCase("html")) {
                    StringBuilder sbhtml = new StringBuilder();
                    // If not wrapped with html tag, add that.
                    if(!emailContent.toLowerCase().contains("<html>")){
                        sbhtml.append("<html><body>");
                        sbhtml.append(emailContent.replace("\n", "<br>"));
                        sbhtml.append("</body></html>");
                    } else {
                        sbhtml.append(emailContent);
                    }

                    sb.append(sbhtml.toString());
                } else {
                    // Clean out any html. Note: Side effect, any legitimate html that sender intended to be may ba removed.

                    if(emailContent.toLowerCase().contains("<html>")) {
                        // We have the start tag, so lets assume we have html email without multipart and strip all html tags
                        // Keep the breaks
                        emailContent = emailContent.replaceAll("<[bB][rR].?\\/?>","\n");
                        Pattern p = Pattern.compile("<[^>]*>");
                        Matcher m = p.matcher(emailContent);
                        emailContent = m.replaceAll("");

                    }
                    sb.append(emailContent);
                }
            }

            writer.putContent(sb.toString());
        }
        finally
        {
            if (tikaInputStream != null)
            {
                try
                {
                    // it closes any other resources associated with it
                    tikaInputStream.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Find "text" parts of message recursively
     *
     * @param multipart Multipart to process
     * @return "text" parts of message
     * @throws MessagingException
     * @throws IOException
     */
    private StringBuilder processMultiPart(Multipart multipart) throws MessagingException, IOException
    {
        StringBuilder sb = new StringBuilder();
        StringBuilder sbhtml = new StringBuilder();

        for (int i = 0, n = multipart.getCount(); i < n; i++) {
            Part part = multipart.getBodyPart(i);
            if (part.getContent() instanceof Multipart) {
                sb.append(processMultiPart((Multipart) part.getContent()));

            } else if (part.getContentType().contains("text")) {
                //Ignore the html part
                if (!part.getContentType().contains("html")) {
                    sb.append(part.getContent().toString()).append("\n");
                } else {
                    sbhtml.append(part.getContent().toString()).append("\n");
                }

            }

        }

        if (transformTo.equalsIgnoreCase("text")) {
            if (sb.length() > 0) {
                return sb;
            } else if (sbhtml.length() > 0) {
                Pattern p = Pattern.compile("<[^>]*>");
                Matcher m = p.matcher(sbhtml);
                return new StringBuilder().append(m.replaceAll(""));
            }
        } else if (transformTo.equalsIgnoreCase("html")) {
            {
                if (sbhtml.length() > 0) {
                    return sbhtml;
                } else if (sb.length() > 0) {
                    StringBuilder sbhtml2 = new StringBuilder();
                    sbhtml2.append("<html><body>");
                    sbhtml2.append(sb.toString().replace("\n","<br>"));
                    sbhtml2.append("</body></html>");
                    return sbhtml2;
                }
            }


        }
        return sb;
    }

}