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

package org.exoplatform.portal.config;

import java.util.HashSet;

/**
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * May 23, 2006
 */
public final class NewPortalConfig
{

   private HashSet<String> predefinedOwner = new HashSet<String>(5);

   private String ownerType;

   private String templateOwner;

   private String templateLocation;

   public NewPortalConfig()
   {
   }

   public NewPortalConfig(NewPortalConfig cfg)
   {
      this.ownerType = cfg.ownerType;
      this.templateLocation = cfg.templateLocation;
      this.templateOwner = cfg.templateOwner;
      this.predefinedOwner = new HashSet<String>(cfg.predefinedOwner);
   }

   public HashSet<String> getPredefinedOwner()
   {
      return predefinedOwner;
   }

   public void setPredefinedOwner(HashSet<String> s)
   {
      this.predefinedOwner = s;
   }

   public String getTemplateLocation()
   {
      return templateLocation;
   }

   public void setTemplateLocation(String s)
   {
      this.templateLocation = s;
   }

   public String getTemplateOwner()
   {
      return templateOwner;
   }

   public void setTemplateOwner(String s)
   {
      this.templateOwner = s;
   }

   public boolean isPredefinedOwner(String user)
   {
      return predefinedOwner.contains(user);
   }

   public String getOwnerType()
   {
      return ownerType;
   }

   public void setOwnerType(String ownerType)
   {
      this.ownerType = ownerType;
   }

}
