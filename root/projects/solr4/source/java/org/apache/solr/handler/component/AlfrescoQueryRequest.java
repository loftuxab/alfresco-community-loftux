/*
 * Copyright (C) 2005-2013 Alfresco Software Limited.
 *
 * This file is part of Alfresco
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
 */
package org.apache.solr.handler.component;

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import org.apache.solr.client.solrj.SolrRequest;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrException;
import org.apache.solr.common.params.CommonParams;
import org.apache.solr.common.params.SolrParams;
import org.apache.solr.common.util.ContentStream;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

/**
 * 
 *
 * @since solr 1.3
 */
public class AlfrescoQueryRequest extends SolrRequest
{
  private SolrParams query;
  
  private ContentStream contentStream = null;
  
  public AlfrescoQueryRequest()
  {
    super( METHOD.GET, null );
  }

  public AlfrescoQueryRequest( SolrParams q )
  {
    super( METHOD.GET, null );
    query = q;
  }
  
  public AlfrescoQueryRequest( SolrParams q, METHOD method )
  {
    super( method, null );
    query = q;
  }

  /**
   * Use the params 'QT' parameter if it exists
   */
  @Override
  public String getPath() {
    String qt = query == null ? null : query.get( CommonParams.QT );
    if( qt == null ) {
      qt = super.getPath();
    }
    if( qt != null && qt.startsWith( "/" ) ) {
      return qt;
    }
    return "/select";
  }
  
  //---------------------------------------------------------------------------------
  //---------------------------------------------------------------------------------
  
  @Override
  public Collection<ContentStream> getContentStreams() {
      return contentStream == null ? null : Collections.singletonList(contentStream);
  }
  
  /**
 * @param contentStream the contentStream to set
 */
public void setContentStream(ContentStream contentStream)
{
    this.contentStream = contentStream;
}

@Override
  public SolrParams getParams() {
    return query;
  }

  @Override
  public QueryResponse process( SolrServer server ) throws SolrServerException 
  {
    try {
      long startTime = TimeUnit.MILLISECONDS.convert(System.nanoTime(), TimeUnit.NANOSECONDS);
      QueryResponse res = new QueryResponse( server.request( this ), server );
      long endTime = TimeUnit.MILLISECONDS.convert(System.nanoTime(), TimeUnit.NANOSECONDS);
      res.setElapsedTime(endTime - startTime);
      return res;
    } catch (SolrServerException e){
      throw e;
    } catch (SolrException s){
      throw s;
    } catch (Exception e) {
      throw new SolrServerException("Error executing query", e);
    }
  }
}


