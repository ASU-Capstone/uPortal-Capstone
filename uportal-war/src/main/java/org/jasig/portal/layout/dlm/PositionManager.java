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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.portal.PortalException;
import org.jasig.portal.layout.IUserLayoutStore;
import org.jasig.portal.security.IPerson;
import org.jasig.portal.spring.locator.UserLayoutStoreLocator;
import org.jasig.portal.xml.XmlUtilitiesImpl;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/** 
 * Applies and updates position specifiers for child nodes in the
 * composite layout.
 * 
 * @since uPortal 2.5
 */
public class PositionManager {

    private static Log LOG = LogFactory.getLog(PositionManager.class);
    private static IUserLayoutStore dls = null;

    /**
     * Hands back the single instance of RDBMDistributedLayoutStore. There is
     * already a method for acquiring a single instance of the configured layout
     * store so we delegate over there so that all references refer to the same
     * instance.  This method is solely for convenience so that we don't have to
     * keep calling UserLayoutStoreFactory and casting the resulting class.
     */
    private static IUserLayoutStore getDLS() {
        if (dls == null) {
            dls = UserLayoutStoreLocator.getUserLayoutStore();
        }
        return dls;
    }

    /**
       This method and ones that it delegates to have the responsibility of
       organizing the child nodes of the passed in composite view parent node
       according to the order specified in the passed in position set and
       return via the passed in result set whether the personal layout
       fragment (one portion of which is the position set) or the incoporated
       layouts fragment (one portion of which is the compViewParent) were
       changed.

       This may also include pulling nodes in from other parents under certain
       circumstances. For example, if allowed a user can move nodes that are
       not part of their personal layout fragment or PLF; the UI elements that
       they own. These node do not exist in their layout in the database but
       instead are merged in with their owned elements at log in and other
       times. So to move them during subsequent merges a position set can
       contain a position directive indicating the id of the node to be moved
       into a specific position in the sibling list and that well may refer to
       a node not in the sibling list to begin with. If the node no longer
       exists in the composite view then that position directive can safely be
       discarded.

       Positioning is meant to preserve as much as possible the user's
       specified ordering of user interface elements but always respecting
       movement restrictions placed on those elements that are incorporated by
       their owners. So the following rules apply from most important to least.

       1) nodes with moveAllowed="false" prevent nodes of lower precedence from
       being to their left or higher with left or higher defined as having a
       lower index in the sibling list. (applyLowerPrecRestriction)

       2) nodes with moveAllowed="false" prevent nodes of equal precedence from
       moving from one side of this node to the other from their position as
       found in the compViewParent initially and prevents nodes with the same
       precedence from moving from other parents into this parent prior to the
       restricted node. Prior to implies a lower sibling index.
       (applyHoppingRestriction)

       3) nodes with moveAllowed="false" prevent nodes of equal precedence
       lower in the sibling list from being reparented. (ie: moving to another
       parent) However, they can be deleted. (applyReparentingCheck)

       4) nodes should be ordered as much as possible in the order specified by
       the user but in view of the above conditions. So if a user has moved
       nodes thus specifying some order and the owner of some node in that set
       then locks one of those nodes some of those nodes will have to move
       back to their orinial positions to conform with the rules above but for
       the remaining nodes they should be found in the same relative order
       specified by the user. (getOrder)

       5) nodes not included in the order specified by the user (ie: nodes
       added since the user last ordered them) should maintain their relative
       order as much as possible and be appended to the end of the sibling
       list after all others rules have been applied. (getOrder)

       Each of these rules is applied by a call to a method 5 being first and
       1 being last so that 1 has the highest precedence and last say. Once
       the final ordering is specified then it is applied to the children of
       the compViewParent and returned.
     */
    static void applyPositions( Element compViewParent,
                                Element positionSet,
                                IntegrationResult result )
        throws PortalException
    {
        if ( positionSet == null ||
             positionSet.getFirstChild() == null )
            return;

        List<NodeInfo> order = new ArrayList<NodeInfo>();

        applyOrdering        ( order, compViewParent, positionSet );
        applyNoReparenting   ( order, compViewParent, positionSet );
        applyNoHopping       ( order, compViewParent, positionSet );
        applyLowerPrecedence ( order, compViewParent, positionSet );
        evaluateAndApply     ( order, compViewParent, positionSet, result );
    }

    /**
       This method determines if applying all of the positioning rules and
       restrictions ended up making changes to the compViewParent or the
       original position set. If changes are applicable to the CVP then they
       are applied. If the position set changed then the original stored in the
       PLF is updated.
     */
    static void evaluateAndApply( List<NodeInfo> order,
                                  Element compViewParent,
                                  Element positionSet,
                                  IntegrationResult result )
        throws PortalException
    {
        adjustPositionSet( order, positionSet, result );

        if ( hasAffectOnCVP( order, compViewParent ) )
        {
            applyToNodes( order, compViewParent );
            result.setChangedILF(true);;
        }
    }

    /**
       This method trims down the position set to the position directives on
       the node info elements still having a position directive. Any directives
       that violated restrictions were removed from the node info objects so
       the position set should be made to match the order of those still
       having one.
     */
    static void adjustPositionSet( List<NodeInfo> order,
                                   Element positionSet,
                                   IntegrationResult result )
    {
        Node nodeToMatch = positionSet.getFirstChild();
        Element nodeToInsertBefore = positionSet.getOwnerDocument()
        .createElement( "INSERT_POINT" );
        positionSet.insertBefore( nodeToInsertBefore, nodeToMatch );

        for ( Iterator iter = order.iterator();
              iter.hasNext(); )
        {
            NodeInfo ni = (NodeInfo) iter.next();

            if ( ni.positionDirective != null )
            {
                // found one check it against the current one in the position
                // set to see if it is different. If so then indicate that
                // something (the position set) has changed in the plf
                if ( ni.positionDirective != nodeToMatch )
                    result.setChangedPLF(true);;

                // now bump the insertion point forward prior to
                // moving on to the next position node to be evaluated
                if ( nodeToMatch != null )
                    nodeToMatch = nodeToMatch.getNextSibling();

                // now insert it prior to insertion point
                positionSet.insertBefore( ni.positionDirective,
                                          nodeToInsertBefore );
            }
        }

        // now for any left over after the insert point remove them.

        while ( nodeToInsertBefore.getNextSibling() != null )
            positionSet.removeChild( nodeToInsertBefore.getNextSibling() );

        // now remove the insertion point
        positionSet.removeChild( nodeToInsertBefore );
    }

    /**
       This method compares the children by id in the order list with
       the order in the compViewParent's ui visible children and returns true
       if the ordering differs indicating that the positioning if needed.
     */
    static boolean hasAffectOnCVP( List<NodeInfo> order,
                                   Element compViewParent )
    {
        if ( order.size() == 0 )
            return false;
        
        
        int idx = 0;
        Element child = (Element) compViewParent.getFirstChild();
        NodeInfo ni = (NodeInfo) order.get( idx );

        if ( child == null && ni != null ) // most likely nodes to be pulled in
            return true;
        
        while ( child != null )
        {
            if ( child.getAttribute( "hidden" ).equals( "false" ) &&
                 ( ! child.getAttribute( "chanID" ).equals( "" ) ||
                   child.getAttribute( "type" ).equals( "regular" ) ) )
            {
                if ( ni.id.equals( child.getAttribute( Constants.ATT_ID ) ) )
                {
                    if ( idx >= order.size()-1 ) // at end of order list
                        return false;

                    ni = (NodeInfo) order.get( ++idx );
                }
                else // if not equal then return true
                    return true;
            }
            child = (Element) child.getNextSibling();
        }
        if ( idx < order.size() )
            return true; // represents nodes to be pulled in
        return false;
    }

    /**
       This method applies the ordering specified in the passed in order list
       to the child nodes of the compViewParent. Nodes specified in the list
       but located elsewhere are pulled in.
     */
    static void applyToNodes( List<NodeInfo> order,
                              Element compViewParent )
    {
        // first set up a bogus node to assist with inserting
        Node insertPoint = compViewParent.getOwnerDocument()
        .createElement( "bogus" );
        Node first = compViewParent.getFirstChild();

        if ( first != null )
            compViewParent.insertBefore( insertPoint, first );
        else
            compViewParent.appendChild( insertPoint );

        // now pass through the order list inserting the nodes as you go
        for ( int i = 0; i<order.size(); i++ )
            compViewParent.insertBefore( ( (NodeInfo) order.get( i ) ).node,
                                         insertPoint );

        compViewParent.removeChild( insertPoint );
    }                                 

    /**
       This method is responsible for preventing nodes with lower precedence
       from being located to the left (lower sibling order) of nodes having a
       higher precedence and moveAllowed="false".
     */
    static void applyLowerPrecedence( List<NodeInfo> order,
                                      Element compViewParent,
                                      Element positionSet )
    {
        for ( int i = 0; i<order.size(); i++ )
        {
            NodeInfo ni = (NodeInfo) order.get( i );
            if ( ni.node.getAttribute( Constants.ATT_MOVE_ALLOWED )
                 .equals( "false" ) )
            {
                for ( int j=0; j<i; j++ )
                {
                    NodeInfo lefty = (NodeInfo) order.get( j );
                    if ( lefty.precedence == null ||
                         lefty.precedence.isLessThan( ni.precedence ) )
                    {
                        order.remove( j );
                        order.add( i, lefty );
                    }
                }
            }
        }
    }                                 

    /**
       This method is responsible for preventing nodes with identical
       precedence in the same parent from hopping over each other so that a
       layout fragment can lock two tabs that are next to each other and they
       can only be separated by tabs with higher precedence.

       If this situation is detected then the positioning of all nodes
       currently in the compViewParent is left as they are found in the CVP
       with any nodes brought in from
       other parents appended at the end with their relative order preserved.
     */
    static void applyNoHopping( List<NodeInfo> order,
                                Element compViewParent,
                                Element positionSet )
    {
        if ( isIllegalHoppingSpecified( order ) == true )
        {
            ArrayList<NodeInfo> cvpNodeInfos = new ArrayList<>();

            // pull those out of the position list from the CVP
            for ( int i = order.size()-1; i>=0; i-- )
                if ( ((NodeInfo) order.get( i )).indexInCVP != -1 )
                    cvpNodeInfos.add( order.remove( i ) );

            // what is left is coming from other parents. Now push them back in
            // in the order specified in the CVP

            NodeInfo[] nodeInfos = cvpNodeInfos.toArray(new NodeInfo[cvpNodeInfos.size()]);
            Arrays.sort( nodeInfos, new NodeInfoComparator() );
            List<NodeInfo> list = Arrays.asList( nodeInfos );
            order.addAll( 0, list );
        }                            
    }

    /**
       This method determines if any illegal hopping is being specified.
       To determine if the positioning is specifying an ordering that will
       result in hopping I need to determine for each node n in the list if
       any of the nodes to be positioned to its right currently lie to its
       left in the CVP and have moveAllowed="false" and have the same
       precedence or if any of the nodes to be positioned to its left currently
       lie to its right in the CVP and have moveAllowed="false" and have the
       same precedence.

     */
    static boolean isIllegalHoppingSpecified( List<NodeInfo> order )
    {
        for ( int i=0; i< order.size(); i++ )
        {
            NodeInfo ni = ( NodeInfo ) order.get( i );

            // look for move restricted nodes
            if ( ! ni.node.getAttribute( Constants.ATT_MOVE_ALLOWED )
                 .equals( "false" ) )
                continue;

            // now check nodes in lower position to see if they "hopped" here
            // or if they have similar precedence and came from another parent.

            for ( int j=0; j<i; j++ )
            {
                NodeInfo niSib = ( NodeInfo ) order.get( j );

                // skip lower precedence nodes from this parent. These will get
                // bumped during the lower precedence check
                if ( niSib.precedence == Precedence.getUserPrecedence() )
                    continue;

                if ( niSib.precedence.isEqualTo( ni.precedence ) && 
                     ( niSib.indexInCVP == -1 || // from another parent
                     ni.indexInCVP < niSib.indexInCVP ) ) // niSib hopping left
                    return true;
            }

            // now check upper positioned nodes to see if they "hopped"
            
            for ( int j=i+1; j<order.size(); j++ )
            {
                NodeInfo niSib = ( NodeInfo ) order.get( j );

                // ignore nodes from other parents and user precedence nodes
                if ( niSib.indexInCVP == -1 ||
                     niSib.precedence == Precedence.getUserPrecedence() )
                    continue;
                
                if ( ni.indexInCVP > niSib.indexInCVP && // niSib hopped right
                     niSib.precedence.isEqualTo( ni.precedence ) )
                    return true;
            }
        }
        return false;
    }

    /**
       This method scans through the nodes in the ordered list and identifies
       those that are not in the passed in compViewParent. For those it then
       looks in its current parent and checks to see if there are any down-
       stream (higher sibling index) siblings that have moveAllowed="false".
       If any such sibling is found then the node is not allowed to be
       reparented and is removed from the list.
     */
    static void applyNoReparenting( List<NodeInfo> order,
                                    Element compViewParent,
                                    Element positionSet )
    {

        int i = 0;
        while ( i<order.size() )
        {
            NodeInfo ni = (NodeInfo) order.get( i );
            if ( ! ni.node.getParentNode().equals( compViewParent ) )
            {
                ni.differentParent = true;
                if (isNotReparentable(ni, compViewParent, positionSet)) {
                    LOG.info("Resetting the following NodeInfo because it is not reparentable:  " + ni);

                    // this node should not be reparented. If it was placed
                    // here by way of a position directive then delete that
                    // directive out of the ni and posSet will be updated later
                    ni.positionDirective = null;

                    // now we need to remove it from the ordering list but
                    // skip incrementing i, deleted ni now filled by next ni
                    order.remove( i );
                    continue; 
                }
            }
            i++;
        }
    }

    /**
     * Return true if the passed in node or any downstream (higher index)
     * siblings <strong>relative to its destination location</strong> have
     * moveAllowed="false".
     */
    private static boolean isNotReparentable(NodeInfo ni, Element compViewParent, Element positionSet) {

        // This one is easy -- can't re-parent a node with dlm:moveAllowed=false
        if (ni.node.getAttribute(Constants.ATT_MOVE_ALLOWED).equals("false")) {
            return true;
        }

        try {
            /*
             *  Annoying to do in Java, but we need to find our own placeholder
             *  element in the positionSet
             */
            final XPathFactory xpathFactory = XPathFactory.newInstance();
            final XPath xpath = xpathFactory.newXPath();
            final String findPlaceholderXpath = ".//*[local-name()='position' and @name='" + ni.id + "']";
            final XPathExpression findPlaceholder = xpath.compile(findPlaceholderXpath);
            final NodeList findPlaceholderList = (NodeList) findPlaceholder.evaluate(positionSet, XPathConstants.NODESET);
            switch (findPlaceholderList.getLength()) {
                case 0:
                    LOG.warn("Node not found for XPathExpression=\"" + findPlaceholderXpath + "\" in positionSet=" + XmlUtilitiesImpl.toString(positionSet));
                    return true;
                case 1:
                    // This is healthy
                    break;
                default:
                    LOG.warn("More than one node found for XPathExpression=\"" + findPlaceholderXpath + "\" in positionSet=" + XmlUtilitiesImpl.toString(positionSet));
                    return true;
            }
            final Element placeholder = (Element) findPlaceholderList.item(0);  // At last

            for (Element nextPlaceholder = (Element) placeholder.getNextSibling();  // Start with the next dlm:position element after placeholder
                    nextPlaceholder != null;                                       // As long as we have a placeholder to look at
                    nextPlaceholder = (Element) nextPlaceholder.getNextSibling()) {    // Advance to the next placeholder

                if (LOG.isDebugEnabled()) {
                    LOG.debug("Considering whether node ''" + ni.id
                            + "' is Reparentable;  subsequent sibling is:  "
                            + nextPlaceholder.getAttribute("name"));
                }

                /*
                 * Next task:  we have to find the non-placeholder representation of
                 * the nextSiblingPlaceholder within the compViewParent
                 */
                final String unmaskPlaceholderXpath = ".//*[@ID='" + nextPlaceholder.getAttribute("name") + "']";
                final XPathExpression unmaskPlaceholder = xpath.compile(unmaskPlaceholderXpath);
                final NodeList unmaskPlaceholderList = (NodeList) unmaskPlaceholder.evaluate(compViewParent, XPathConstants.NODESET);
                switch (unmaskPlaceholderList.getLength()) {
                    case 0:
                        // Not a problem;  the nextSiblingPlaceholder also refers
                        // to a node that has been moved to this context (afaik)
                        continue;
                    case 1:
                        final Element nextSibling = (Element) unmaskPlaceholderList.item(0);
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("Considering whether node ''" + ni.id + "' is Reparentable;  subsequent sibling '"
                                    + nextSibling.getAttribute("ID") + "' has dlm:moveAllowed="
                                    + !nextSibling.getAttribute(Constants.ATT_MOVE_ALLOWED).equals("false"));
                        }

                        // Need to perform some checks...
                        if (nextSibling.getAttribute(Constants.ATT_MOVE_ALLOWED).equals("false")) {

                            /*
                             *  The following check is a bit strange;  it seems to verify
                             *  that the current NodeInfo and the nextSibling come from the
                             *  same fragment.  If they don't, the re-parenting is allowable.
                             *  I believe this check could only be unsatisfied in the case
                             *  of tabs.
                             */
                            Precedence p = Precedence.newInstance(nextSibling.getAttribute(Constants.ATT_FRAGMENT));
                            if (ni.precedence.isEqualTo(p)) {
                                return true;
                            }
                        }
                        break;
                    default:
                        LOG.warn("More than one node found for XPathExpression=\"" + unmaskPlaceholderXpath + "\" in compViewParent");
                        return true;
                }
            }
        } catch (XPathExpressionException xpe) {
            throw new RuntimeException("Failed to evaluate XPATH", xpe);
        }

        return false;  // Re-parenting is "not disallowed" (double-negative less readable)

    }

    /**
       This method assembles in the passed in order object a list of NodeInfo
       objects ordered first by those specified in the position set and whose
       nodes still exist in the composite view and then by any remaining
       children in the compViewParent.
     */
    static void applyOrdering( List<NodeInfo> order,
                               Element compViewParent,
                               Element positionSet )
    {
        // first pull out all visible channel or visible folder children and
        // put their id's in a list of available children and record their
        // relative order in the CVP.
        
        final Map<String, NodeInfo> available = new LinkedHashMap<String, NodeInfo>();

        Element child = (Element) compViewParent.getFirstChild();
        Element next = null;
        int indexInCVP = 0;
        
        while( child != null )
        {
            next = (Element) child.getNextSibling();
            
            if ( child.getAttribute( "hidden" ).equals( "false" ) &&
                 ( ! child.getAttribute( "chanID" ).equals( "" ) ||
                   child.getAttribute( "type" ).equals( "regular" ) ) ) {
                final NodeInfo nodeInfo = new NodeInfo( child,
                                             indexInCVP++ );
                
                final NodeInfo prevNode = available.put( nodeInfo.id, nodeInfo );
                if (prevNode != null) {
                    throw new IllegalStateException("Infinite loop detected in layout. Triggered by " + nodeInfo.id + " with already visited node ids: " + available.keySet());
                }
            }
            child = next;
        }

        // now fill the order list using id's from the position set if nodes
        // having those ids exist in the composite view. Otherwise discard
        // that position directive. As they are added to the list remove them
        // from the available nodes in the parent.
        
        Document CV = compViewParent.getOwnerDocument();
        Element directive = (Element) positionSet.getFirstChild();
        
        while ( directive != null )
        {
            next = (Element) directive.getNextSibling();

            // id of child to move is in the name attrib on the position nodes
            String id = directive.getAttribute( "name" );
            child = CV.getElementById( id );
            
            if ( child != null )
            {
                // look for the NodeInfo for this node in the available
                // nodes and if found use that one. Otherwise use a new that
                // does not include an index in the CVP parent. In either case
                // indicate the position directive responsible for placing this
                // NodeInfo object in the list.
                
                final String childId = child.getAttribute( Constants.ATT_ID );
                NodeInfo ni = available.remove(childId);
                if (ni == null) {
                    ni = new NodeInfo( child );
                }
                
                ni.positionDirective = directive;
                order.add( ni );
            }
            directive = next;
        }

        // now append any remaining ids from the available list maintaining
        // the order that they have there.

        order.addAll(available.values());
    }

    /**
       This method updates the positions recorded in a position set to reflect
       the ids of the nodes in the composite view of the layout. Any position
       nodes already in existence are reused to reduce database interaction
       needed to generate a new ID attribute. If any are left over after
       updating those position elements are removed. If no position set existed
       a new one is created for the parent. If no ILF nodes are found in the
       parent node then the position set as a whole is reclaimed.
    */
    public static void updatePositionSet( Element compViewParent,
                                          Element plfParent,
                                          IPerson person )
        throws PortalException
    {
        if (LOG.isDebugEnabled())
            LOG.debug("Updating Position Set");

        if ( compViewParent.getChildNodes().getLength() == 0 )
        {
            // no nodes to position. if set exists reclaim the space.
            if (LOG.isDebugEnabled())
                LOG.debug("No Nodes to position");
            Element positions = getPositionSet( plfParent, person, false );
            if ( positions != null )
            plfParent.removeChild( positions );
            return;
        }
        Element posSet = (Element) getPositionSet( plfParent, person, true );
        Element position = (Element) posSet.getFirstChild();
        Element viewNode = (Element) compViewParent.getFirstChild();
        boolean ilfNodesFound = false;
        
        while( viewNode != null )
        {
            String ID = viewNode.getAttribute( Constants.ATT_ID );
            String channelId = viewNode.getAttribute( Constants.ATT_CHANNEL_ID );
            String type = viewNode.getAttribute( Constants.ATT_TYPE );
            String hidden = viewNode.getAttribute( Constants.ATT_HIDDEN );

            if ( ID.startsWith( Constants.FRAGMENT_ID_USER_PREFIX ) )
            ilfNodesFound = true;
            
            if ( ! channelId.equals("") ||    // its a channel node or
             ( type.equals("regular") &&  // a regular, visible folder
               hidden.equals("false") ) )
            {
            if ( position != null )
                position.setAttribute( Constants.ATT_NAME, ID );
            else
                position = createAndAppendPosition( ID, posSet, person );
            position = (Element) position.getNextSibling();
            }
            viewNode = (Element) viewNode.getNextSibling();
        }

        if ( ilfNodesFound == false ) // only plf nodes, no pos set needed
            plfParent.removeChild( posSet );
        else
        {
            // reclaim any leftover positions
            while( position != null )
            {
                Element nextPos = (Element) position.getNextSibling();
                posSet.removeChild( position );
                position = nextPos;
            }
        }
    }

    /**
       This method locates the position set element in the child list of the
       passed in plfParent or if not found it will create one automatically
       and return it if the passed in create flag is true.
    */
    private static Element getPositionSet( Element plfParent,
                                           IPerson person,
                                           boolean create )
        throws PortalException
    {
        Node child = plfParent.getFirstChild();

        while( child != null )
        {
            if ( child.getNodeName().equals( Constants.ELM_POSITION_SET ) )
                return (Element) child;
            child = child.getNextSibling();
        }
        if ( create == false )
            return null;

        
        String ID = null;

        try
        {
            ID = getDLS().getNextStructDirectiveId( person );
        }
        catch (Exception e)
        {
            throw new PortalException( "Exception encountered while " +
                                       "generating new position set node " +
                                       "Id for userId=" + person.getID(),e );
        }
        Document plf = plfParent.getOwnerDocument();
        Element positions = plf.createElement( Constants.ELM_POSITION_SET );
        positions.setAttribute( Constants.ATT_TYPE,
                                Constants.ELM_POSITION_SET );
        positions.setAttribute( Constants.ATT_ID, ID );
        plfParent.appendChild( positions );
        return positions;
    }

    /**
       Create, append to the passed in position set, and return a position
       element that references the passed in elementID.
    */
    private static Element createAndAppendPosition( String elementID,
                                                    Element positions,
                                                    IPerson person )
        throws PortalException
    {
        if (LOG.isDebugEnabled())
            LOG.debug("Adding Position Set entry " + elementID + ".");

        String ID = null;

        try
        {
            ID = getDLS().getNextStructDirectiveId( person );
        }
        catch (Exception e)
        {
            throw new PortalException( "Exception encountered while " +
                                       "generating new position node " +
                                       "Id for userId=" + person.getID(), e );
        }
        Document plf = positions.getOwnerDocument();
        Element position = plf.createElement( Constants.ELM_POSITION );
        position.setAttribute( Constants.ATT_TYPE, Constants.ELM_POSITION );
        position.setAttribute( Constants.ATT_ID, ID );
        position.setAttributeNS( Constants.NS_URI,
                                 Constants.ATT_NAME, elementID );
        positions.appendChild( position );
        return position;
    }

    private static class NodeInfoComparator implements Comparator<NodeInfo> {
        @Override
        public int compare(NodeInfo o1, NodeInfo o2) {
            return ((NodeInfo) o1).indexInCVP - ((NodeInfo) o2).indexInCVP;
        }
    }

}
