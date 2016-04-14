/**
 * Licensed to Apereo under one or more contributor license
 * agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership.
 * Apereo licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License.  You may obtain a
 * copy of the License at the following location:
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jasig.portal.layout.dlm.remoting;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.jasig.portal.groups.IEntityGroup;
import org.jasig.portal.groups.IGroupMember;
import org.jasig.portal.portlet.om.PortletCategory;
import org.jasig.portal.portlets.groupselector.EntityEnum;
import org.jasig.portal.security.IPermission;

/**
 * <p>Entity bean for JSON output.  Used for categories, groups, and people.</p>
 *
 * @author Drew Mazurek
 */
public class JsonEntityBean implements Serializable, Comparable<JsonEntityBean> {

    private static final long serialVersionUID = 1L;

    public static final String ENTITY_CATEGORY = "category";
    public static final String ENTITY_CHANNEL = "channel";
    public static final String ENTITY_GROUP = "group";
    public static final String ENTITY_PERSON = "person";

    private EntityEnum entityType;
    private String id;
    private String name;
    private String creatorId;
    private String description;
    private String principalString;
    private String targetString;
    private List<JsonEntityBean> children = new ArrayList<JsonEntityBean>();
    private boolean childrenInitialized = false;

    public JsonEntityBean() {
    }

    public JsonEntityBean(PortletCategory category) {
        this.entityType = EntityEnum.CATEGORY;
        this.id = category.getId();
        this.name = category.getName();
        this.creatorId = category.getCreatorId();
        this.description = category.getDescription();
        this.targetString = category.getId();  // e.g. 'local.25'
    }

    public JsonEntityBean(IGroupMember groupMember, EntityEnum entityType) {
        this.entityType = entityType;
        this.id = groupMember.getKey();
        String prefix = "";  // default
        switch (entityType) {
            case PORTLET:
                prefix = IPermission.PORTLET_PREFIX;  // E.g. groupMember.getKey()=56
                break;
            case PERSON:
                // No prefix -- e.g. groupMember.getKey()=admin
                break;
            default:
                throw new IllegalArgumentException("Unsupported entityType:  " + entityType);
        }
        this.targetString = prefix + groupMember.getKey();
    }

    public JsonEntityBean(IEntityGroup entityGroup, EntityEnum entityType) {
        this.entityType = entityType;
        this.id = entityGroup.getKey();
        this.name = entityGroup.getName();
        this.creatorId = entityGroup.getCreatorID();
        this.description = entityGroup.getDescription();
        this.targetString = entityGroup.getKey();  // e.g. 'local.19' and 'pags.Authenticated Users'
    }

    public EntityEnum getEntityType() {
        return entityType;
    }

    public String getEntityTypeAsString() {
        return entityType.toString().toUpperCase();
    }

    public void setEntityType(String entityType) {
        this.entityType = EntityEnum.getEntityEnum(entityType);
    }

    public void setEntityType(EntityEnum entityType) {
        this.entityType = entityType;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPrincipalString() {
        return principalString;
    }

    public void setPrincipalString(String principalString) {
        this.principalString = principalString;
    }

    /**
     * Identifies this bean uniquely as a permissions target.  NOTE:  This id is
     * not the fname (for portlets) or name field (for groups), but rater a
     * unique String like 'PORTLET_ID.19' or 'local.36' or 'pags.Authenticated Users'
     *
     * @since uPortal 4.0.14
     */
    public String getTargetString() {
        return targetString;
    }

    /**
     *
     * @since uPortal 4.0.14
     */
    public void setTargetString(String targetString) {
        this.targetString = targetString;
    }

    /**
     * Compute a hash based on type and ID to uniquely identify
     * this bean. This method helps avoid the unlikely case where a
     * group and person in the same principal list have the
     * same ID.
     *
     * Periods are replaced to avoid issues in JSP EL and form names can't contain spaces.  Also SpEL parsing
     * of form field names fails with characters such as dash or parenthesis (which PAGS groups can have) and
     * likely other characters so they are also replaced with underscores.
     *
     * @return  EntityType + "_" + ID
     */
    public String getTypeAndIdHash() {
        assert(entityType != null);
        assert(id != null);
        String idStr = id.replaceAll("\\W", "__");
        return entityType.toString().toLowerCase() + "_" + idStr;
    }

    public List<JsonEntityBean> getChildren() {
        return children;
    }

    public void setChildren(List<JsonEntityBean> children) {
        this.children = children;
    }

    /**
     * <p>Convenience method to add a child to this object's list of
     * children.</p>
     * @param child Object to add
     */
    public void addChild(JsonEntityBean child) {
        children.add(child);
    }

    public boolean isChildrenInitialized() {
        return childrenInitialized;
    }

    public void setChildrenInitialized(boolean childrenInitialized) {
        this.childrenInitialized = childrenInitialized;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (childrenInitialized ? 1231 : 1237);
        result = prime * result
                + ((creatorId == null) ? 0 : creatorId.hashCode());
        result = prime * result
                + ((description == null) ? 0 : description.hashCode());
        result = prime * result
                + ((entityType == null) ? 0 : entityType.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        JsonEntityBean other = (JsonEntityBean) obj;
        if (childrenInitialized != other.childrenInitialized)
            return false;
        if (creatorId == null) {
            if (other.creatorId != null)
                return false;
        } else if (!creatorId.equals(other.creatorId))
            return false;
        if (description == null) {
            if (other.description != null)
                return false;
        } else if (!description.equals(other.description))
            return false;
        if (entityType == null) {
            if (other.entityType != null)
                return false;
        } else if (!entityType.equals(other.entityType))
            return false;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        return true;
    }

    @Override
    public int compareTo(JsonEntityBean entity) {
        return (new CompareToBuilder())
                .append(this.name, entity.getName())
                .append(this.entityType, entity.getEntityType())
                .append(this.creatorId, entity.getCreatorId())
                .append(this.description, entity.getDescription())
                .append(this.entityType, entity.getEntityType())
                .append(this.id, entity.getId())
                .append(this.principalString, this.getPrincipalString())
                .toComparison();
    }

    @Override
    public String toString() {
        return "JsonEntityBean [entityType=" + entityType + ", id=" + id
                + ", name=" + name + ", creatorId=" + creatorId
                + ", description=" + description + ", principalString="
                + principalString + "]";
    }

}
