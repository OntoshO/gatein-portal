/**
 * Copyright (C) 2009 eXo Platform SAS.
 * 
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 * 
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.exoplatform.resolver;

import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.portlet.PortletContext;

/**
 * Created by The eXo Platform SAS
 * Mar 15, 2006
 */
public class PortletResourceResolver extends ResourceResolver
{

   protected static Log log = ExoLogger.getLogger("portlet:PortletResourceResolver");

   private PortletContext pcontext_;

   private String scheme_;

   public PortletResourceResolver(PortletContext context, String scheme)
   {
      pcontext_ = context;
      scheme_ = scheme;
   }

   public URL getResource(String url) throws Exception
   {
      String path = removeScheme(url);
      return pcontext_.getResource(path);
   }

   public InputStream getInputStream(String url) throws Exception
   {
      String path = removeScheme(url);
      return pcontext_.getResourceAsStream(path);
   }

   public List<URL> getResources(String url) throws Exception
   {
      ArrayList<URL> urlList = new ArrayList<URL>();
      urlList.add(getResource(url));
      return urlList;
   }

   public List<InputStream> getInputStreams(String url) throws Exception
   {
      ArrayList<InputStream> inputStreams = new ArrayList<InputStream>();
      inputStreams.add(getInputStream(url));
      return inputStreams;
   }

   public String getRealPath(String url)
   {
      String path = removeScheme(url);
      return pcontext_.getRealPath(path);
   }

   public boolean isModified(String url, long lastAccess)
   {
      File file = new File(getRealPath(url));
      if (log.isDebugEnabled())
         log.debug(url + ": " + file.lastModified() + " " + lastAccess);
      if (file.exists() && file.lastModified() > lastAccess)
      {
         return true;
      }
      return false;
   }

   public String getWebAccessPath(String url)
   {
      return "/" + pcontext_.getPortletContextName() + removeScheme(url);
   }

   public String getResourceScheme()
   {
      return scheme_;
   }

}