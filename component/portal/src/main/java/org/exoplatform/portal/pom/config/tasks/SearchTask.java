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

import org.exoplatform.commons.utils.LazyPageList;
import org.exoplatform.commons.utils.ListAccess;
import org.exoplatform.portal.application.PortletPreferences;
import org.exoplatform.portal.config.Query;
import org.exoplatform.portal.config.model.Mapper;
import org.exoplatform.portal.config.model.Page;
import org.exoplatform.portal.config.model.PageNavigation;
import org.exoplatform.portal.config.model.PortalConfig;
import org.exoplatform.portal.pom.config.AbstractPOMTask;
import org.exoplatform.portal.pom.config.POMSession;
import org.gatein.mop.api.workspace.Navigation;
import org.gatein.mop.api.workspace.ObjectType;
import org.gatein.mop.api.workspace.Site;
import org.gatein.mop.api.workspace.Workspace;
import org.gatein.mop.api.workspace.WorkspaceObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public abstract class SearchTask<T> extends AbstractPOMTask
{

   /*
   new Query<Page>(PortalConfig.GROUP_TYPE, groupId,  Page.class);
   new Query<Page>(PortalConfig.GROUP_TYPE, groupId,  Page.class);
   new Query<Page>(PortalConfig.USER_TYPE, userName, Page.class);
   new Query<Page>(PortalConfig.USER_TYPE, userName, Page.class);
   new Query<Page>(PortalConfig.PORTAL_TYPE, portalName, null, null, Page.class);
   new Query<Page>(null, null, null, null, Page.class);

   new Query<PortletPreferences>(PortalConfig.GROUP_TYPE, groupId, PortletPreferences.class);
   new Query<PortletPreferences>(PortalConfig.GROUP_TYPE, groupId, PortletPreferences.class);
   new Query<PortletPreferences>(PortalConfig.USER_TYPE, userName, PortletPreferences.class);
   new Query<PortletPreferences>(PortalConfig.PORTAL_TYPE, portalName, null, null, PortletPreferences.class);

   new Query<PageNavigation>(PortalConfig.GROUP_TYPE, null, PageNavigation.class);
   new Query<PageNavigation>(PortalConfig.GROUP_TYPE, null, PageNavigation.class);
   new Query<PageNavigation>(PortalConfig.GROUP_TYPE, null, PageNavigation.class);

   new Query<PortalConfig>(null, null, null, null, PortalConfig.class);
   new Query<PortalConfig>(null, null, null, null, PortalConfig.class);
   new Query<PortalConfig>(null, null, null, null, PortalConfig.class);
   new Query<PortalConfig>(null, null, null, null, PortalConfig.class);
   */

   /** . */
   protected final Query<T> q;

   /** . */
   protected LazyPageList<T> result;

   public SearchTask(Query<T> query)
   {
      this.q = query;
   }

   public LazyPageList<T> getResult()
   {
      return result;
   }

   public abstract static class FindSiteObject<W extends WorkspaceObject, T> extends SearchTask<T>
   {

      public FindSiteObject(Query<T> query)
      {
         super(query);
      }

      public void run(final POMSession session) throws Exception
      {
         Iterator<W> ite;
         try
         {
            String ownerType = q.getOwnerType();
            ObjectType<? extends Site> siteType = null;
            if (ownerType != null)
            {
               siteType = Mapper.parseSiteType(ownerType);
            }
            ite = findW(session, siteType, q.getOwnerId(), q.getTitle());

         }
         catch (IllegalArgumentException e)
         {
            ite = Collections.<W> emptyList().iterator();
         }

         //
         final ArrayList<W> array = new ArrayList<W>();
         while (ite.hasNext())
         {
            array.add(ite.next());
         }

         //
         final Iterator<W> it = array.iterator();
         ListAccess<T> la = new ListAccess<T>()
         {
            public T[] load(int index, int length) throws Exception, IllegalArgumentException
            {
               T[] result = createT(length);
               for (int i = 0; i < length; i++)
               {
                  T t = loadT(session, it.next());
                  result[i] = t;
               }
               return result;
            }

            public int getSize() throws Exception
            {
               return array.size();
            }
         };

         result = new LazyPageList<T>(la, 10);
      }

      protected abstract Iterator<W> findW(POMSession session, ObjectType<? extends Site> siteType, String ownerId,
         String title);

      protected abstract T[] createT(int length);

      protected abstract T loadT(POMSession session, W w);

   }

   public static class FindPage extends FindSiteObject<org.gatein.mop.api.workspace.Page, Page>
   {

      public FindPage(Query<Page> pageQuery)
      {
         super(pageQuery);
      }

      protected Iterator<org.gatein.mop.api.workspace.Page> findW(POMSession session,
         ObjectType<? extends Site> siteType, String ownerId, String title)
      {
         return session.findObjects(ObjectType.PAGE, siteType, q.getOwnerId(), q.getTitle());
      }

      protected Page[] createT(int length)
      {
         return new Page[length];
      }

      protected Page loadT(POMSession session, org.gatein.mop.api.workspace.Page w)
      {
         return new Mapper(session).load(w);
      }
   }

   public static class FindNavigation extends FindSiteObject<Navigation, PageNavigation>
   {

      public FindNavigation(Query<PageNavigation> pageQuery)
      {
         super(pageQuery);
      }

      protected Iterator<Navigation> findW(POMSession session, ObjectType<? extends Site> siteType, String ownerId,
         String title)
      {
         return session.findObjects(ObjectType.NAVIGATION, siteType, q.getOwnerId(), q.getTitle());
      }

      protected PageNavigation[] createT(int length)
      {
         return new PageNavigation[length];
      }

      protected PageNavigation loadT(POMSession session, Navigation w)
      {
         return new Mapper(session).load(w);
      }
   }

   public static class FindPortletPreferences extends SearchTask<PortletPreferences>
   {

      public FindPortletPreferences(Query<PortletPreferences> portletPreferencesQuery)
      {
         super(portletPreferencesQuery);
      }

      public void run(final POMSession session) throws Exception
      {
         // We return empty on purpose at it is used when preferences are deleted by the UserPortalConfigService
         // and the prefs are deleted transitively when an entity is removed
         result = new LazyPageList<PortletPreferences>(new ListAccess<PortletPreferences>()
         {
            public PortletPreferences[] load(int index, int length) throws Exception, IllegalArgumentException
            {
               throw new AssertionError();
            }

            public int getSize() throws Exception
            {
               return 0;
            }
         }, 10);
      }
   }

   public static class FindSite extends SearchTask<PortalConfig>
   {

      public FindSite(Query<PortalConfig> siteQuery)
      {
         super(siteQuery);
      }

      public void run(final POMSession session) throws Exception
      {
         Workspace workspace = session.getWorkspace();
         final Collection<? extends Site> portals = workspace.getSites(ObjectType.PORTAL_SITE);

         ListAccess<PortalConfig> la = new ListAccess<PortalConfig>()
         {
            public PortalConfig[] load(int index, int length) throws Exception, IllegalArgumentException
            {
               Iterator<? extends Site> iterator = portals.iterator();
               Mapper mapper = new Mapper(session);
               PortalConfig[] result = new PortalConfig[length];
               for (int i = 0; i < length; i++)
               {
                  result[i] = mapper.load(iterator.next());
               }
               return result;
            }

            public int getSize() throws Exception
            {
               return portals.size();
            }
         };
         result = new LazyPageList<PortalConfig>(la, 10);
      }
   }
}
