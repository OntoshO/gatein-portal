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

package org.exoplatform.webui.core;

import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.UIBreadcumbs.SelectPathActionListener;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;
import org.exoplatform.webui.form.UIForm;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a breadcrumbs component.
 *
 */
@ComponentConfig(events = @EventConfig(listeners = SelectPathActionListener.class))
public class UIBreadcumbs extends UIComponent
{
   /**
    * The list that contains the different local paths, representing the total hierarchy
    */
   private List<LocalPath> path_ = new ArrayList<LocalPath>();

   /**
    * The selected path
    */
   private LocalPath selectedLocalPath_;

   /**
    * The css style
    */
   private String styleBread = "default";

   public List<LocalPath> getPath()
   {
      return path_;
   }

   public void setPath(List<LocalPath> list)
   {
      path_ = list;
   }

   public LocalPath getSelectLocalPath()
   {
      return selectedLocalPath_;
   }

   public void setSelectLocalPath(LocalPath localPath)
   {
      selectedLocalPath_ = localPath;
   }

   public void setSelectPath(String path)
   {
      List<LocalPath> list = getPath();
      for (LocalPath p : list)
      {
         if (!path.equals(p.getId()))
            continue;
         setSelectLocalPath(p);
         break;
      }
   }

   public String event(String name, String beanId) throws Exception
   {
      UIForm uiForm = getAncestorOfType(UIForm.class);
      if (uiForm != null)
         return uiForm.event(name, getId(), beanId);
      return super.event(name, beanId);
   }

   public String getBreadcumbsStyle()
   {
      return styleBread;
   }

   public void setBreadcumbsStyle(String style)
   {
      styleBread = style;
   }

   static public class SelectPathActionListener extends EventListener<UIBreadcumbs>
   {
      public void execute(Event<UIBreadcumbs> event) throws Exception
      {
         UIBreadcumbs uicomp = event.getSource();
         String objectId = event.getRequestContext().getRequestParameter(OBJECTID);
         uicomp.setSelectPath(objectId);
         uicomp.<UIComponent> getParent().broadcast(event, event.getExecutionPhase());
      }
   }

   static public class LocalPath
   {

      private String label_;

      private String id_;

      public LocalPath(String id, String label)
      {
         label_ = label;
         id_ = id;
      }

      public String getLabel()
      {
         return label_;
      }

      public void setLabel(String label)
      {
         label_ = label;
      }

      public String getId()
      {
         return id_;
      }

      public void setId(String id)
      {
         id_ = id;
      }
   }

}