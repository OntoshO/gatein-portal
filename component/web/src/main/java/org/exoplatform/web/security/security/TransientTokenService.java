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

package org.exoplatform.web.security.security;

import org.exoplatform.container.xml.InitParams;
import org.exoplatform.web.login.InitiateLoginServlet;
import org.exoplatform.web.security.Credentials;
import org.exoplatform.web.security.Token;

import java.util.concurrent.ConcurrentHashMap;

import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;

/**
 * Created by The eXo Platform SAS Author : liem.nguyen ncliam@gmail.com Jun 5,
 * 2009
 */
public class TransientTokenService extends AbstractTokenService
{

   protected final ConcurrentHashMap<String, Token> tokens = new ConcurrentHashMap<String, Token>();

   public TransientTokenService(InitParams initParams)
   {
      super(initParams);
   }

   public String createToken(Credentials credentials)
   {
      if (validityMillis < 0)
      {
         throw new IllegalArgumentException();
      }
      if (credentials == null)
      {
         throw new NullPointerException();
      }
      String tokenId = InitiateLoginServlet.COOKIE_NAME + random.nextInt();
      long expirationTimeMillis = System.currentTimeMillis() + validityMillis;
      tokens.put(tokenId, new Token(expirationTimeMillis, credentials));
      return tokenId;
   }

   @Override
   public Token getToken(String id) throws PathNotFoundException, RepositoryException
   {
      return tokens.get(id);
   }

   @Override
   public Token deleteToken(String id) throws PathNotFoundException, RepositoryException
   {
      Token token = tokens.get(id);
      tokens.remove(id);
      return token;
   }

   @Override
   public String[] getAllTokens()
   {
      return tokens.keySet().toArray(new String[]{});
   }

   @Override
   public long getNumberTokens() throws Exception
   {
      return tokens.size();
   }
}
