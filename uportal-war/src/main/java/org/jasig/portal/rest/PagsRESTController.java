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

package org.jasig.portal.rest;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.jasig.portal.EntityIdentifier;
import org.jasig.portal.groups.IEntityGroup;
import org.jasig.portal.groups.IGroupConstants;
import org.jasig.portal.groups.pags.dao.IPersonAttributesGroupDefinition;
import org.jasig.portal.groups.pags.dao.IPersonAttributesGroupTestGroupDefinition;
import org.jasig.portal.groups.pags.dao.PagsService;
import org.jasig.portal.groups.pags.dao.jpa.PersonAttributesGroupDefinitionImpl;
import org.jasig.portal.security.IPerson;
import org.jasig.portal.security.IPersonManager;
import org.jasig.portal.security.RuntimeAuthorizationException;
import org.jasig.portal.services.GroupService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * REST Controller that leverages PagsAdministrationHelper and related form classes
 * to provide a RESTful API for AJAX management of PAGS.
 * 
 * @author Benito Gonzalez, bgonzalez@unicon.net
 */
@Controller
public class PagsRESTController {
    /*
      Tried to use ResponseEntities but the ran into conflicts when using <mvc:annotation-driven/>
     */

    /**
     * Help other Java classes use this API well.
     */
    public static final String URL_FORMAT_STRING = "/api/v4-3/pags/%s.json";

    @Autowired
    private PagsService pagsService;

    @Autowired
    private IPersonManager personManager;

    @Autowired
    private ObjectMapper objectMapper;

    @SuppressWarnings("unused")
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @RequestMapping(value="/v4-3/pags/{pagsGroupName}.json", produces = {MediaType.APPLICATION_JSON_VALUE}, method = RequestMethod.GET)
    public @ResponseBody String findPagsGroup(HttpServletRequest request, HttpServletResponse res,
                                              @PathVariable("pagsGroupName") String pagsGroupName) {

        res.setContentType(MediaType.APPLICATION_JSON_VALUE);

        /*
         * This step is necessary;  the incoming URLs will sometimes have '+'
         * characters for spaces, and the @PathVariable magic doesn't convert them.
         */
        String name;
        try {
            name = URLDecoder.decode(pagsGroupName, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return "{ 'error': '" + e.toString() + "' }";
        }

        IPerson person = personManager.getPerson(request);
        IPersonAttributesGroupDefinition pagsGroup = this.pagsService.getPagsDefinitionByName(person, name);
        return respondPagsGroupJson(res, pagsGroup, person, HttpServletResponse.SC_FOUND);

    }

    @RequestMapping(value="/v4-3/pags/{parentGroupName}.json", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.POST)
    public @ResponseBody String createPagsGroup(HttpServletRequest request,
                                                HttpServletResponse res,
                                                @PathVariable("parentGroupName") String parentGroupName,
                                                @RequestBody String json) {

        res.setContentType(MediaType.APPLICATION_JSON_VALUE);

        /*
         * This step is necessary;  the incoming URLs will sometimes have '+'
         * characters for spaces, and the @PathVariable magic doesn't convert them.
         */
        String name;
        try {
            name = URLDecoder.decode(parentGroupName, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return "{ 'error': '" + e.getMessage() + "' }";
        }

        IPersonAttributesGroupDefinition inpt;
        try {
            inpt = objectMapper.readValue(json, PersonAttributesGroupDefinitionImpl.class);
        } catch (Exception e) {
            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return "{ 'error': '" + e.getMessage() + "' }"; // should be escaped
        }

        // Obtain a real reference to the parent group
        EntityIdentifier[] eids = GroupService.searchForGroups(name, IGroupConstants.IS, IPerson.class);
        if (eids.length == 0) {
            res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return "{ 'error': 'Parent group does not exist: " + name + "' }";
        }
        IEntityGroup parentGroup = (IEntityGroup) GroupService.getGroupMember(eids[0]);  // Names must be unique

        IPerson person = personManager.getPerson(request);
        IPersonAttributesGroupDefinition rslt;
        try {
            // A little weird that we need to do both;
            // need some PAGS DAO/Service refactoring
            rslt = pagsService.createPagsDefinition(person, parentGroup, inpt.getName(), inpt.getDescription());
            // NOTE:  We are also obligated to establish the backlink
            // testGroupDef --> groupDef;  arguably this backlink serves
            // little purpose and could be removed.
            for (IPersonAttributesGroupTestGroupDefinition testGroupDef : inpt.getTestGroups()) {
                // NOTE:  The deserializer handles testDef --> testGroupDef
                testGroupDef.setGroup(rslt);
            }
            rslt.setTestGroups(inpt.getTestGroups());
            rslt.setMembers(inpt.getMembers());
            pagsService.updatePagsDefinition(person, rslt);
        } catch (RuntimeAuthorizationException rae) {
            res.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return "{ 'error': 'not authorized' }";
        } catch (IllegalArgumentException iae) {
            res.setStatus(HttpServletResponse.SC_CONFLICT);
            return "{ 'error': '" + iae.getMessage() + "' }";
        } catch (Exception e) {
            e.printStackTrace();
            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return "{ 'error': '" + e.getMessage() + "' }";
        }
        return respondPagsGroupJson(res, rslt, person, HttpServletResponse.SC_CREATED);

    }

    @RequestMapping(value="/v4-3/pags/{pagsGroupName}.json", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.PUT)
    public @ResponseBody String updatePagsGroup(HttpServletRequest req,
                                                HttpServletResponse res,
                                                @PathVariable("pagsGroupName") String pagsGroupName,
                                                @RequestBody String json) {

        res.setContentType(MediaType.APPLICATION_JSON_VALUE);

        /*
         * This step is necessary;  the incoming URLs will sometimes have '+'
         * characters for spaces, and the @PathVariable magic doesn't convert them.
         */
        String name;
        try {
            name = URLDecoder.decode(pagsGroupName, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return "{ 'error': '" + e.toString() + "' }";
        }

        IPersonAttributesGroupDefinition inpt;
        try {
            inpt = objectMapper.readValue(json, PersonAttributesGroupDefinitionImpl.class);
        } catch (Exception e) {
            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return "{ 'error': '" + e.toString() + "' }"; // should be escaped
        }
        if (inpt == null) {
            res.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return "{ 'error': 'Not found' }";
        }
        if (!name.equals(inpt.getName())) {
            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return "{ 'error': 'Group name in URL parameter must match name in JSON payload' }";
        }

        IPerson person = personManager.getPerson(req);
        IPersonAttributesGroupDefinition rslt;
        try {
            IPersonAttributesGroupDefinition currentDef = pagsService.getPagsDefinitionByName(person, name);
            if (currentDef == null) {
                res.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return "{ 'error': 'Not found' }";
            }
            /*
             * Copy over the information being passed in to the JPA-managed
             * instance;  the following do not support updates (currently):
             *   - Name
             *   - Members
             */
            currentDef.setDescription(inpt.getDescription());
            // NOTE:  We are also obligated to establish the backlink
            // testGroupDef --> groupDef;  arguably this backlink serves
            // little purpose and could be removed.
            for (IPersonAttributesGroupTestGroupDefinition testGroupDef : inpt.getTestGroups()) {
                // NOTE:  The deserializer handles testDef --> testGroupDef
                testGroupDef.setGroup(currentDef);
            }
            currentDef.setTestGroups(inpt.getTestGroups());
            rslt = pagsService.updatePagsDefinition(person, currentDef);
        } catch (IllegalArgumentException iae) {
            res.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return "{ 'error': '" + iae.getMessage() + "' }"; // should be escaped
        } catch (RuntimeAuthorizationException rae) {
            res.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return "{ 'error': 'not authorized' }";
        } catch (Exception e) {
            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return "{ 'error': '" + e.toString() + "' }";
        }
        return respondPagsGroupJson(res, rslt, person, HttpServletResponse.SC_ACCEPTED);
    }

    /*
     * Implementation
     */

    private String respondPagsGroupJson(HttpServletResponse response, IPersonAttributesGroupDefinition pagsGroup, IPerson person, int status) {
        if (pagsGroup == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return "{ 'error': 'Not Found' }";
        }
        try {
            response.setStatus(status);
            return objectMapper.writeValueAsString(pagsGroup);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return "{ 'error': '" + e.toString() + "' }";
        }
    }

}
