/**
 * Licensed to Apereo under one or more contributor license
 * agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership.
 * Apereo licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License.  You may obtain a
 * copy of the License at the following location:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package  org.jasig.portal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.portal.i18n.LocaleManager;
import org.jasig.portal.security.IPerson;
import org.jasig.portal.user.IUserInstance;


/**
 * A class handling holding all user state information. The class is also reponsible for
 * request processing and orchestrating the entire rendering procedure.
 * (this is a replacement for the good old LayoutBean class)
 * @author Peter Kharchenko  {@link <a href="mailto:pkharchenko@interactivebusiness.com"">pkharchenko@interactivebusiness.com"</a>}
 * @version $Revision$
 */
public class UserInstance implements IUserInstance {
    protected final Log log = LogFactory.getLog(this.getClass());

    // manages layout and preferences
    private final IUserPreferencesManager preferencesManager;
    // manages locale
    private final LocaleManager localeManager;

    private final IPerson person;
    
    public UserInstance(IPerson person, IUserPreferencesManager preferencesManager, LocaleManager localeManager) {
        this.person = person;
        this.preferencesManager = preferencesManager;
        this.localeManager = localeManager;
    }

    /* (non-Javadoc)
     * @see org.jasig.portal.IUserInstance#getPerson()
     */
    @Override
    public IPerson getPerson() {
        return this.person;
    }

    /* (non-Javadoc)
     * @see org.jasig.portal.IUserInstance#getPreferencesManager()
     */
    @Override
    public IUserPreferencesManager getPreferencesManager() {
        return this.preferencesManager;
    }

    /* (non-Javadoc)
     * @see org.jasig.portal.IUserInstance#getLocaleManager()
     */
    @Override
    public LocaleManager getLocaleManager() {
        return this.localeManager;
    }
}
