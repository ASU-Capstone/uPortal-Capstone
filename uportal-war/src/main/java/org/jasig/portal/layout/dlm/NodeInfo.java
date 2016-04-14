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
package org.jasig.portal.layout.dlm;

import org.w3c.dom.Element;

/**
 * @since uPortal 2.5
 */
public class NodeInfo
{
    String id = null;
    Element node = null;
    boolean differentParent = false;
    int indexInCVP = -1; // CVP = Composite View Parent
    Precedence precedence = null;
    Element positionDirective = null;
    
    NodeInfo( Element node )
    {
        this.node = node;
        precedence = Precedence
        .newInstance( node.getAttribute( Constants.ATT_FRAGMENT ) );
        id = node.getAttribute( Constants.ATT_ID );
    }
    
    NodeInfo( Element node, int indexInCVP )
    {
        this( node );
        this.indexInCVP = indexInCVP;
    }
    
    public boolean equals( Object o )
    {
        if ( o != null &&
             o instanceof NodeInfo &&
             ((NodeInfo) o).id.equals( id ) )
            return true;
        if ( o == this )
            return true;
        return false;
    }

    public String toString()
    {
        return "ni[ id:" + id +
        ", diffPrnt:" + differentParent +
        ", idxInCVP:" + indexInCVP +
        ", prec:" + precedence +
        " ]";
    }
}
