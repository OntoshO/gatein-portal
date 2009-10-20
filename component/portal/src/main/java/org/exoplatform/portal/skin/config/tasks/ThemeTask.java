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

package org.exoplatform.portal.skin.config.tasks;

import org.exoplatform.portal.skin.SkinService;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;

/**
 * 
 * Created by eXoPlatform SAS
 *
 * Author: Minh Hoang TO - hoang281283@gmail.com
 *
 *      Sep 16, 2009
 */
public class ThemeTask extends AbstractSkinTask
{

   private String styleName;

   private List<String> themeNames;

   public ThemeTask()
   {
      this.themeNames = new ArrayList<String>();
   }

   public void addThemeName(String _themeName)
   {
      //TODO: Check duplicated theme name
      this.themeNames.add(_themeName);
   }

   public void setStyleName(String _styleName)
   {
      this.styleName = _styleName;
   }

   @Override
   public void execute(SkinService skinService, ServletContext scontext)
   {
      if (styleName == null || themeNames.size() < 1)
      {
         return;
      }
      skinService.addTheme(styleName, themeNames);
   }

}
