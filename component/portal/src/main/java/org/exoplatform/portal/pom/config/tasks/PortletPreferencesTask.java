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

package org.exoplatform.portal.pom.config.tasks;

import org.exoplatform.portal.application.PortletPreferences;
import org.exoplatform.portal.application.Preference;
import org.exoplatform.portal.config.model.Mapper;
import org.exoplatform.portal.pom.config.AbstractPOMTask;
import org.exoplatform.portal.pom.config.POMSession;
import org.exoplatform.portal.pom.spi.portlet.Preferences;
import org.exoplatform.portal.pom.spi.portlet.PreferencesBuilder;
import org.gatein.mop.api.content.Customization;
import org.gatein.mop.api.workspace.ObjectType;
import org.gatein.mop.api.workspace.Page;
import org.gatein.mop.api.workspace.Site;
import org.gatein.mop.api.workspace.Workspace;
import org.gatein.mop.api.workspace.ui.UIWindow;

import java.util.ArrayList;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public abstract class PortletPreferencesTask extends AbstractPOMTask
{

   /*

   WindowID:
   - persistenceId : portal#classic:/web/BannerPortlet/testPortletPreferences
   - owner : portal#classic
   - portletApplicationName : web
   - portletName: BannerPortlet
   - uniqueID : testPortletPreferences

   */

   /** . */
   protected final ObjectType<? extends Site> siteType;

   /** . */
   protected final String ownerType;

   /** . */
   protected final String ownerId;

   /** . */
   protected final String applicationName;

   /** . */
   protected final String portletName;

   /** . */
   protected final String instanceName;

   /** . */
   protected final String windowId;

   protected PortletPreferencesTask(String windowId)
   {
      String[] chunks = Mapper.parseWindowId(windowId);
      if (chunks.length < 4 || chunks.length > 5)
      {
         throw new IllegalArgumentException("Invalid window id " + windowId);
      }

      //
      this.ownerType = chunks[0];
      this.siteType = Mapper.parseSiteType(chunks[0]);
      this.ownerId = chunks[1];
      this.applicationName = chunks[2];
      this.portletName = chunks[3];
      this.instanceName = chunks.length > 4 ? chunks[4] : null;
      this.windowId = windowId;
   }

   public static class Save extends PortletPreferencesTask
   {

      /** . */
      private final PortletPreferences prefs;

      public Save(PortletPreferences prefs)
      {
         super(prefs.getWindowId());

         //
         this.prefs = prefs;
      }

      public void run(POMSession session) throws Exception
      {
         Workspace workspace = session.getWorkspace();
         Site site = workspace.getSite(siteType, ownerId);

         //
         Customization customization = null;
         if (site != null)
         {
            if (instanceName.startsWith("@"))
            {
               String id = instanceName.substring(1);
               UIWindow window = session.findObjectById(ObjectType.WINDOW, id);

               // Should check it's pointing to same instance though
               customization = window.getCustomization();
            }
            else
            {
               int pos = instanceName.indexOf("#");
               if (pos != -1)
               {
                  String a = instanceName.substring(0, pos);
                  String b = instanceName.substring(pos + 1);
                  Page page = site.getRootPage().getChild("pages").getChild(b);
                  Customization c = page.getCustomization(a);
                  if (c != null)
                  {
                     c.destroy();
                  }
                  customization =
                     page.customize(a, Preferences.CONTENT_TYPE, applicationName + "/" + portletName,
                        new PreferencesBuilder().build());
               }
               else
               {
                  Customization c = site.getCustomization(instanceName);
                  if (c != null)
                  {
                     c.destroy();
                  }
                  customization =
                     site.customize(instanceName, Preferences.CONTENT_TYPE, applicationName + "/" + portletName,
                        new PreferencesBuilder().build());
               }
            }
         }

         //
         if (customization != null)
         {
            PreferencesBuilder builder = new PreferencesBuilder();
            ArrayList<Preference> list = prefs.getPreferences();
            if (list != null)
            {
               for (Preference pref : list)
               {
                  builder.add(pref.getName(), pref.getValues(), pref.isReadOnly());
               }
            }
            customization.setState(builder.build());
         }
         else
         {
            session.addPortletPreferences(prefs);
         }
      }
   }

   public static class Load extends PortletPreferencesTask
   {

      /** . */
      private PortletPreferences prefs;

      public Load(String windowId)
      {
         super(windowId);
      }

      public PortletPreferences getPreferences()
      {
         return prefs;
      }

      public void run(POMSession session) throws Exception
      {
         Workspace workspace = session.getWorkspace();
         Site site = workspace.getSite(siteType, ownerId);
         if (site == null)
         {
            throw new IllegalArgumentException("Cannot load portlet preferences " + windowId
               + " as the corresponding portal " + ownerId + " with type " + siteType + " does not exist");
         }

         //
         if (instanceName != null)
         {
            Customization<Preferences> customization;
            if (instanceName.startsWith("@"))
            {
               String id = instanceName.substring(1);
               UIWindow window = session.findObjectById(ObjectType.WINDOW, id);
               customization = (Customization<Preferences>)window.getCustomization();
            }
            else
            {
               int pos = instanceName.indexOf('#');
               if (pos == -1)
               {
                  customization = (Customization<Preferences>)site.getCustomization(instanceName);
               }
               else
               {
                  String a = instanceName.substring(0, pos);
                  String b = instanceName.substring(pos + 1);
                  Page page = site.getRootPage().getChild("pages").getChild(b);
                  customization = (Customization<Preferences>)page.getCustomization(a);
               }
            }

            //
            if (customization != null)
            {
               Preferences state = customization.getVirtualState();
               if (state != null)
               {
                  ArrayList<Preference> list = new ArrayList<Preference>();
                  for (org.exoplatform.portal.pom.spi.portlet.Preference preference : state)
                  {
                     Preference pref = new Preference();
                     pref.setName(preference.getName());
                     pref.setValues(new ArrayList<String>(preference.getValues()));
                     pref.setReadOnly(preference.isReadOnly());
                     list.add(pref);
                  }
                  PortletPreferences prefs = new PortletPreferences();
                  prefs.setWindowId(windowId);
                  prefs.setPreferences(list);
                  this.prefs = prefs;
               }
            }
         }
      }
   }
}
