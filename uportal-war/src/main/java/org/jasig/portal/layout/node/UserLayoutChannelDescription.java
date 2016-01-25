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
package org.jasig.portal.layout.node;

import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Map;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.portal.PortalException;
import org.jasig.portal.portlet.om.IPortletDefinition;
import org.jasig.portal.portlet.om.IPortletDefinitionParameter;
import org.jasig.portal.portlet.registry.IPortletDefinitionRegistry;
import org.jasig.portal.spring.locator.PortletDefinitionRegistryLocator;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;


/**
 * A class managing information contained in a user layout channel node.
 *
 * @author Peter Kharchenko  {@link <a href="mailto:pkharchenko@interactivebusiness.com"">pkharchenko@interactivebusiness.com"</a>}
 * @version 1.0
 */
public class UserLayoutChannelDescription extends UserLayoutNodeDescription implements IUserLayoutChannelDescription  {

	private static final Log log = LogFactory.getLog(UserLayoutChannelDescription.class);
	
    Hashtable parameters=new Hashtable();

    String title=null;
    String description=null;
    String className=null;
    String channelPublishId=null;
    String channelTypeId=null;
    String functionalName=null;
    long timeout=-1;
    boolean editable=false;
    boolean hasHelp=false;
    boolean hasAbout=false;
    boolean isSecure=false;
    
    public UserLayoutChannelDescription() {
        super();
    }

    public UserLayoutChannelDescription(IUserLayoutChannelDescription d) {

        super(d);

        this.title=d.getTitle();
        this.name = d.getName();
        this.description=d.getDescription();
        this.setClassName(d.getClassName());
        this.channelPublishId=d.getChannelPublishId();
        this.channelTypeId=d.getChannelTypeId();
        this.functionalName=d.getFunctionalName();
        this.timeout=d.getTimeout();
        this.editable=d.isEditable();
        this.hasHelp=d.hasHelp();
        this.hasAbout=d.hasAbout();

        for(Enumeration enum1 = d.getParameterNames(); enum1.hasMoreElements();) {
            String pName=(String)enum1.nextElement();
            this.setParameterValue(pName,d.getParameterValue(pName));
        }
    }

    /**
     * Construct channel information from a Channel Definition object.
     * 
     * @param definition
     */
    public UserLayoutChannelDescription(IPortletDefinition definition) {
        this();
        this.title = definition.getTitle();
        this.name = definition.getName();
        this.name = definition.getName();
        this.description = definition.getDescription();
        this.channelPublishId = String.valueOf(definition.getPortletDefinitionId().getStringId());
        this.channelTypeId = String.valueOf(definition.getType().getId());
        this.functionalName = definition.getFName();
        this.timeout = definition.getTimeout();
//        this.editable = definition.isEditable();
//        this.hasHelp = definition.hasHelp();
//        this.hasAbout = definition.hasAbout();
        
        for (IPortletDefinitionParameter param : definition.getParameters()) {
            this.setParameterValue(param.getName(), param.getValue());
        }
    }

    /**
     * Reconstruct channel information from an xml <code>Element</code>
     *
     * @param xmlNode a user layout channel <code>Element</code> value
     * @exception PortalException if xml is malformed
     */
    public UserLayoutChannelDescription(Element xmlNode) throws PortalException {
        super( xmlNode );
        
        if(!xmlNode.getNodeName().equals("channel")) {
            throw new PortalException("Given XML Element is not a channel!");
        }

        // channel-specific attributes
        this.setTitle(xmlNode.getAttribute("title"));
        this.setDescription(xmlNode.getAttribute("description"));
        this.setClassName(xmlNode.getAttribute("class"));
        this.setChannelPublishId(xmlNode.getAttribute("chanID"));
        this.setChannelTypeId(xmlNode.getAttribute("typeID"));
        this.setFunctionalName(xmlNode.getAttribute("fname"));
        this.setTimeout(Long.parseLong(xmlNode.getAttribute("timeout")));
        this.setEditable(Boolean.valueOf(xmlNode.getAttribute("editable")).booleanValue());
        this.setHasHelp(Boolean.valueOf(xmlNode.getAttribute("hasHelp")).booleanValue());
        this.setHasAbout(Boolean.valueOf(xmlNode.getAttribute("hasAbout")).booleanValue());
        this.setIsSecure(Boolean.valueOf(xmlNode.getAttribute("secure")).booleanValue());
        
        // process parameter elements
        for(Node n=xmlNode.getFirstChild(); n!=null;n=n.getNextSibling()) {
            if(n.getNodeType()==Node.ELEMENT_NODE) {
                Element e=(Element) n;
                if(e.getNodeName().equals("parameter")) {
                    // get parameter name and value
                    String pName=e.getAttribute("name");
                    String pValue=e.getAttribute("value");

                    Boolean canOverride=new Boolean(false);
                    String str_override=e.getAttribute("override");
                    if(str_override!=null && str_override.equals("yes")) {
                        canOverride=new Boolean(true);
                    }

                    if(pName!=null && pValue!=null) {
                        this.setParameterValue(pName,pValue);
                    }
                }
            }
        }
    }

    /**
     * Determine if the channel supports "about" action.
     * @return value of hasAbout.
     */
    public boolean hasAbout() {
        return hasAbout;
    }

    /**
     * Specify whether the channel supports "about" action.
     * @param v  Value to assign to hasAbout.
     */
    public void setHasAbout(boolean  v) {
        this.hasAbout = v;
    }

    /**
     * Determine if the channel supports "help" action.
     * @return value of hasHelp.
     */
    public boolean hasHelp() {
        return hasHelp;
    }

    /**
     * Specify whether the channel supports "help" action.
     * @param v  Value to assign to hasHelp.
     */
    public void setHasHelp(boolean  v) {
        this.hasHelp = v;
    }

    /**
     * Determine if the channel is editable.
     * @return value of editable.
     */
    public boolean isEditable() {
        return editable;
    }

    /**
     * Specify whether the channel is editable.
     * @param v  Value to assign to editable.
     */
    public void setEditable(boolean  v) {
        this.editable = v;
    }

    /**
     * Get the value of channel timeout in milliseconds.
     * @return value of timeout.
     */
    public long getTimeout() {
        return timeout;
    }

    /**
     * Set the value of channel timeout in milliseconds.
     * @param v  Value to assign to timeout.
     */
    public void setTimeout(long  v) {
        this.timeout = v;
    }

    /**
     * Get the value of secure setting.
     * @return value of secure.
     */    
    public boolean isSecure(){
        return isSecure;
    }

    /**
     * Set the value of channel secure setting.
     * @param secure  Value to assign to secure
     */    
    public void setIsSecure(boolean secure){
        this.isSecure = secure;
    }
    
    
    /**
     * Get the channel type for portlet / not portlet
     * @return the channel type for portlet / not portlet
     */
    public boolean isPortlet() {
        return true;
    }

    /**
     * Get the value of functionalName.
     * @return value of functionalName.
     */
    public String getFunctionalName() {
        return functionalName;
    }

    /**
     * Set the value of functionalName.
     * @param v  Value to assign to functionalName.
     */
    public void setFunctionalName(String  v) {
        this.functionalName = v;
    }

    /**
     * Get the value of channelSubscribeId.
     * @return value of channelSubscribeId.
     */
    public String getChannelSubscribeId() {
        return super.getId();
    }

    /**
     * Set the value of channelSubscribeId.
     * @param v  Value to assign to channelSubscribeId.
     */
    public void setChannelSubscribeId(String  v) {
        super.setId(v);
    }

    /**
     * Get the value of channelTypeId.
     * @return value of channelTypeId.
     */
    public String getChannelTypeId() {
        return channelTypeId;
    }

    /**
     * Set the value of channelTypeId.
     * @param v  Value to assign to channelTypeId.
     */
    public void setChannelTypeId(String  v) {
        this.channelTypeId = v;
    }

    /**
     * Get the value of channelPublishId for this channel.
     * @return value of channelPublishId.
     */
    public String getChannelPublishId() {
        return channelPublishId;
    }

    /**
     * Set the value of channelPublishId for this channel.
     * @param v  Value to assign to channelPublishId.
     */
    public void setChannelPublishId(String  v) {
        this.channelPublishId = v;
    }

    /**
     * Get the value of className implementing this channel.
     * @return value of className.
     */
    public String getClassName() {
        return className;
    }

    /**
     * Set the value of className implementing this channel.
     * @param v  Value to assign to className.
     */
    public void setClassName(String  v) {
        this.className = v;
    }

    /**
     * Get the value of title.
     * @return value of title.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Set the value of title.
     * @param v  Value to assign to title.
     */
    public void setTitle(String  v) {
        this.title = v;
    }



    /**
     * Get the value of description.
     * @return value of description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Set the value of description.
     * @param v  Value to assign to description.
     */
    public void setDescription(String  v) {
        this.description = v;
    }


    // channel parameter methods

    /**
     * Set a channel parameter value.
     *
     * @param parameterValue a <code>String</code> value
     * @param parameterName a <code>String</code> value
     * @return a <code>String</code> value that was set.
     */
    public String setParameterValue(String parameterName, String parameterValue) {
        // don't try to store a null value
    	if (parameterValue==null)  return null;
        return (String) parameters.put(parameterName,parameterValue);
    }


    /**
     * Reset a channel parameter value. Since parameter changes by channels
     * can be persisted if override is allowed this method enables resetting to
     * the original value or, if the parameter is ad-hoc meaning that the
     * channel definition does not provide a value for this parameter, then the
     * parameter value is removed.
     *
     * @param parameterName a <code>String</code> value
     */
    public void resetParameter(String parameterName) throws PortalException
    {
        /*
         * Since original channel definition parameter values that were 
         * overridden are not maintained at this level we must consult the 
         * channel definition to determine the original value and restore it
         * or discover that this is an ad-hoc parameter and can be removed. 
         */
        try
        {
        	IPortletDefinitionRegistry registry = PortletDefinitionRegistryLocator.getPortletDefinitionRegistry();
            int pubId = Integer.parseInt(getChannelPublishId());
            IPortletDefinition def = registry.getPortletDefinition(getChannelPublishId());
            IPortletDefinitionParameter parm = def.getParameter(parameterName);

            if (parm == null) // ad-hoc parm so delete
            {
                parameters.remove(parameterName);
            }
        }
        catch(Exception e)
        {
            throw new PortalException("Unable to reset parameter [" +
                    parameterName + "] for channel [" + getTitle() +
                    "].", e);
       }
    }

    /**
     * Obtain a channel parameter value.
     *
     * @param parameterName a <code>String</code> value
     * @return a <code>String</code> value
     */
    public String getParameterValue(String parameterName) {
        return (String) parameters.get(parameterName);
    }

    /**
     * Obtain values of all existing channel parameters.
     *
     * @return a <code>Collection</code> of <code>String</code> parameter values.
     */
    public Collection getParameterValues()  {
        return parameters.values();
    }


    /**
     * Determines the number of existing channel parameters.
     *
     * @return an <code>int</code> value
     */
    public int numberOfParameters() {
        return parameters.size();
    }


    /**
     * Clears all of the channel parameters.
     *
     */
    public void clearParameters() {
        parameters.clear();
    }


    /**
     * Remove a channel parameter.
     *
     * @param parameterName a <code>String</code> parameter name.
     * @return an old parameter value.
     */
    public String remove(String parameterName) {
        return (String) parameters.remove(parameterName);
    }

    /**
     * Obtain a set of channel parameter names.
     *
     * @return a <code>Set</code> of <code>String</code> parameter names.
     */
    public Enumeration getParameterNames() {
        return parameters.keys();
    }

    /**
     * Returns an entire mapping of parameters.
     *
     * @return a <code>Map</code> of parameter names on parameter values.
     */
    public Map getParameterMap() {
        return Collections.unmodifiableMap(parameters);
    }

    /**
     * Determine if the channel has any parameters.
     *
     * @return a <code>boolean</code> value
     */
    public boolean hasParameters() {
        return !parameters.isEmpty();
    }

    /**
     * Determines if a certain parameter name is present.
     *
     * @param parameterName a <code>String</code> parameter name.
     * @return a <code>boolean</code> value
     */
    public boolean containsParameter(String parameterName) {
        return parameters.containsKey(parameterName);
    }

    /**
     * Creates a <code>org.w3c.dom.Element</code> representation of the current channel.
     *
     * @param root a <code>Document</code> for which the <code>Element</code> should be created.
     * @return a <code>Node</code> value
     */
    public Element getXML(Document root) {
        Element node=root.createElement("channel");
        this.addNodeAttributes(node);
        this.addParameterChildren(node,root);
        return node;
    }

    public void addParameterChildren(Element node, Document root) {
        for(Enumeration enum1 = this.getParameterNames(); enum1.hasMoreElements();) {
            Element pElement=root.createElement("parameter");
            String pName=(String)enum1.nextElement();
            pElement.setAttribute("name",pName);
            pElement.setAttribute("value",getParameterValue(pName));
            node.appendChild(pElement);
        }
    }

    public void addNodeAttributes(Element node) {
        super.addNodeAttributes(node);
        node.setAttribute("title",this.getTitle());
        node.setAttribute("name",this.getName());
        node.setAttribute("description",this.getDescription());
        node.setAttribute("class",this.getClassName());
        node.setAttribute("chanID",this.getChannelPublishId());
        node.setAttribute("typeID",this.getChannelTypeId());
        node.setAttribute("fname",this.getFunctionalName());
        node.setAttribute("timeout",Long.toString(this.getTimeout()));
        node.setAttribute("editable",(new Boolean(this.isEditable())).toString());
        node.setAttribute("hasHelp",(new Boolean(this.hasHelp())).toString());
        node.setAttribute("hasAbout",(new Boolean(this.hasAbout())).toString());
        node.setAttribute("secure",(new Boolean(this.isSecure())).toString());
        node.setAttribute("isPortlet",Boolean.valueOf(this.isPortlet()).toString());
    }

    /**
     * Returns a type of the node, could be FOLDER or CHANNEL integer constant.
     *
     * @return a type
     */
    public LayoutNodeType getType() {
      return LayoutNodeType.PORTLET;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).
                append("ID", this.id).
                append("name", this.name).
                append("channelPublishId", this.channelPublishId).
                append("channelTypeId", this.channelTypeId).
                append("nodeType", this.getType()).
                append("precedence", this.precedence).
                append("moveAllowed", this.moveAllowed).
                append("removable", !this.unremovable).
                append("deleteAllowed", this.deleteAllowed).
                append("immutable", this.immutable).
                append("editAllowed", this.editAllowed).
                append("precedence", this.precedence).
                toString();

    }

}
