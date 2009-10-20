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

package org.exoplatform.portal.webui;

import org.exoplatform.portal.application.PortalRequestContext;
import org.exoplatform.portal.webui.portal.UIPortal;
import org.exoplatform.portal.webui.util.Util;
import org.exoplatform.portal.webui.workspace.UIMaskWorkspace;
import org.exoplatform.services.organization.OrganizationService;
import org.exoplatform.web.application.ApplicationMessage;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.lifecycle.UIFormLifecycle;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;
import org.exoplatform.webui.event.Event.Phase;
import org.exoplatform.webui.exception.MessageException;
import org.exoplatform.webui.form.UIForm;
import org.exoplatform.webui.form.UIFormStringInput;

import java.net.URLEncoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * Created by The eXo Platform SARL
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@exoplatform.com
 * Jul 11, 2006  
 */
@ComponentConfig(lifecycle = UIFormLifecycle.class, template = "system:/groovy/portal/webui/UILoginForm.gtmpl", events = {
   @EventConfig(listeners = UILoginForm.SigninActionListener.class),
   @EventConfig(phase = Phase.DECODE, listeners = UIMaskWorkspace.CloseActionListener.class)})
public class UILoginForm extends UIForm
{

   public UILoginForm() throws Exception
   {
      addUIFormInput(new UIFormStringInput("username", "username", null)).addUIFormInput(
         new UIFormStringInput("password", "password", null).setType(UIFormStringInput.PASSWORD_TYPE));
   }

   static public class SigninActionListener extends EventListener<UILoginForm>
   {

      public void execute(Event<UILoginForm> event) throws Exception
      {
         UILoginForm uiForm = event.getSource();
         String username = uiForm.getUIStringInput("username").getValue();
         String password = uiForm.getUIStringInput("password").getValue();

         OrganizationService orgService = uiForm.getApplicationComponent(OrganizationService.class);
         boolean authentication = orgService.getUserHandler().authenticate(username, password);
         if (!authentication)
         {
            throw new MessageException(new ApplicationMessage("UILoginForm.msg.Invalid-account", null));
         }

         PortalRequestContext prContext = Util.getPortalRequestContext();
         HttpServletRequest request = prContext.getRequest();
         HttpSession session = request.getSession();
         session.setAttribute("authentication.username", username);
         session.setAttribute("authentication.password", password);
         UIPortal uiPortal = Util.getUIPortal();
         prContext.setResponseComplete(true);
         String portalName = uiPortal.getName();
         portalName = URLEncoder.encode(portalName, "UTF-8");
         String redirect = request.getContextPath() + "/private/" + portalName + "/";
         prContext.getResponse().sendRedirect(redirect);
      }

   }

}
