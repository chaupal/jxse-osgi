/*
 * Copyright (c) 2001-2007 Sun Microsystems, Inc.  All rights reserved.
 *
 *  The Sun Project JXTA(TM) Software License
 *
 *  Redistribution and use in source and binary forms, with or without 
 *  modification, are permitted provided that the following conditions are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice,
 *     this list of conditions and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice, 
 *     this list of conditions and the following disclaimer in the documentation 
 *     and/or other materials provided with the distribution.
 *
 *  3. The end-user documentation included with the redistribution, if any, must 
 *     include the following acknowledgment: "This product includes software 
 *     developed by Sun Microsystems, Inc. for JXTA(TM) technology." 
 *     Alternately, this acknowledgment may appear in the software itself, if 
 *     and wherever such third-party acknowledgments normally appear.
 *
 *  4. The names "Sun", "Sun Microsystems, Inc.", "JXTA" and "Project JXTA" must 
 *     not be used to endorse or promote products derived from this software 
 *     without prior written permission. For written permission, please contact 
 *     Project JXTA at http://www.jxta.org.
 *
 *  5. Products derived from this software may not be called "JXTA", nor may 
 *     "JXTA" appear in their name, without prior written permission of Sun.
 *
 *  THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 *  INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND 
 *  FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL SUN 
 *  MICROSYSTEMS OR ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, 
 *  INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT 
 *  LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, 
 *  OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF 
 *  LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING 
 *  NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, 
 *  EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 *  JXTA is a registered trademark of Sun Microsystems, Inc. in the United 
 *  States and other countries.
 *
 *  Please see the license information page at :
 *  <http://www.jxta.org/project/www/license.html> for instructions on use of 
 *  the license in source files.
 *
 *  ====================================================================
 *
 *  This software consists of voluntary contributions made by many individuals 
 *  on behalf of Project JXTA. For more information on Project JXTA, please see 
 *  http://www.jxta.org.
 *
 *  This license is based on the BSD license adopted by the Apache Foundation. 
 */

package net.jxta.rendezvous;

//import net.jxta.endpoint.EndpointAddress;
//import net.jxta.endpoint.EndpointListener;
import net.jxta.endpoint.Message;
import net.jxta.id.ID;
//import net.jxta.protocol.PeerAdvertisement;
//import net.jxta.protocol.RdvAdvertisement;
import net.jxta.service.Service;
import java.io.IOException;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;
import net.jxta.peer.PeerID;

/**
 * The RendezVous Service provides propagation of messages within a JXTA
 * PeerGroup.
 *
 * <p/>The JXTA RendezVous Service defines a subscription mechanism
 * allowing JXTA peers to receive propagated messages (clients of the service)
 * or become a repeater of the service (rendezvous peers).
 *
 * <p/>The Standard Reference Implementation requires that at least one peer in
 * a PeerGroup to act as a Rendezvous. Rendezvous peers may dynamically join or
 * leave the PeerGroup over time.
 *
 * @see <a href="https://jxta-spec.dev.java.net/nonav/JXTAProtocols.html#proto-rvp" target='_blank'>JXTA Protocols Specification : Rendezvous</a>
 */
public interface RendezVousService extends Service {

    /**
     *  Perform <code>propagate()</code> or <code>walk()</code> using the most
     *  appropriate TTL value for the implementation and configuration. The
     *  message will almost certainly be sent with a TTL value much less than
     *  this value.
     */
    public final static int DEFAULT_TTL = Integer.MAX_VALUE;


    /**
     * Start the local peer as a RendezVous peer.
     */
    public void startRendezVous();

    /**
     * Stop the RendezVous function on the local Peer. All connected Peers are
     * disconnected.
     */
    public void stopRendezVous();
    
    /**
     * Add a listener for RendezVousEvents.
     *
     * @param  listener  An RendezvousListener to process the event.
     */
    public void addListener(RendezvousListener listener);

    /**
     * Removes a Rendezvous event listener previously added with addListener.
     *
     * @param  listener  the RendezvousListener listener remove
     * @return           true if successful
     */
    public boolean removeListener(RendezvousListener listener);

    /**
     * Propagates a message to the local network and to as many members of
     * the peer group as possible.
     *
     * <p/>This method sends the message to all peers, rendezvous peers and
     * edge peer. This method of propagation is very expensive and should
     * be used very cautiously. When rendezvous peers are used in order to
     * cache index of data, it is more efficient to use the walk() method.
     *
     * <p/>Only a single HOP at a time is performed. Messages are always
     * delivered to the destination handler on arrival. This handler
     * is responsible for repropagating further, if deemed appropriate.
     *
     * <p/>Loop and TTL control are performed automatically.
     *
     * <p/>Messages can be propagated via this method for the first time or
     * can be re-propagated by re-using a message that came in via propagation.
     * In the later case, the TTL and loop detection parameters CANNOT be
     * re-initialized. If one wants to "re-propagate" a message with a new TTL
     * and blank gateways list one must generate a completely new message.
     * This limits the risk of accidental propagation storms, although they
     * can always be engineered deliberately.
     *
     * @param  msg              is the message to propagate.
     * @param  serviceName      is the name of the service.
     * @param  serviceParam     is the parameter of the service.
     * @param  ttl              The requested TTL for the message.
     * @exception  IOException  if an io error occurs
     */
    public void propagate(Message msg, String serviceName, String serviceParam, int ttl) throws IOException;

    /**
     * Propagates a message to the specified peers.
     *
     * @param  destPeerIds      An enumeration of PeerIDs of the peers that are the
     * intended recipients of the propagated message.
     * @param  msg              The message to propagate.
     * @param  serviceName      The name of the service.
     * @param  serviceParam     The parameter of the service.
     * @param  ttl              The requested TTL for the message.
     * @exception  IOException  if an io error occurs
     */
    public void propagate(Enumeration<? extends ID> destPeerIds, Message msg, String serviceName, String serviceParam, int ttl) throws IOException;

    /**
     * Propagates a message to members of the peer group reachable via the
     * local network. Typically this is accomplished by broadcasting or
     * multicasting.
     *
     * <p/>Only a single HOP at a time is performed. Messages are always
     * delivered to the destination handler on arrival. This handler
     * is responsible for repropagating further, if deemed appropriate.
     *
     * <p/>Loop and TTL control are performed automatically.
     *
     * <p/>Messages can be propagated via this method for the first time or
     * can be re-propagated by re-using a message that came in via propagation.
     * In the later case, the TTL and loop detection parameters CANNOT be
     * re-initialized. If one wants to "re-propagate" a message with a new TTL
     * and blank gateways list one must generate a completely new message.
     * This limits the risk of accidental propagation storms, although they
     * can always be engineered deliberately.
     *
     * @param  msg              is the message to propagate.
     * @param  serviceName      is the name of the service.
     * @param  serviceParam     is the parameter of the service.
     * @param  ttl              The requested TTL for the message.
     * @exception  IOException  if an io error occurs
     */
    public void propagateToNeighbors(Message msg, String serviceName, String serviceParam, int ttl) throws IOException;

    /**
     * Propagates a message to as many members of the peer group as possible.
     *
     * <p/>Only a single HOP at a time is performed. Messages are always
     * delivered to the destination handler on arrival. This handler
     * is responsible for repropagating further, if deemed appropriate.
     *
     * <p/>Loop and TTL control are performed automatically.
     *
     * <p/>Messages can be propagated via this method for the first time or
     * can be re-propagated by re-using a message that came in via propagation.
     * In the later case, the TTL and loop detection parameters CANNOT be
     * re-initialized. If one wants to "re-propagate" a message with a new TTL
     * and blank gateways list one must generate a completely new message.
     * This limits the risk of accidental propagation storms, although they
     * can always be engineered deliberately.
     *
     * @param  msg              is the message to propagate.
     * @param  serviceName      is the name of the service
     * @param  serviceParam     is the parameter of the service
     * @param  ttl              The requested TTL for the message.
     * @exception  IOException  if an io error occurs
     */
    public void propagateInGroup(Message msg, String serviceName, String serviceParam, int ttl) throws IOException;

    /**
     * Return {@code true} if connected to a rendezvous.
     *
     * {@since 2.6 The behavior of this method changes to reflect the true connectivity
     * status to RDVs. If this peer is an ADHOC, it always returns {@code false}. If this
     * peer is a RDV, it will return {@code true} if it knows about other RDVs through its
     * peerview. If this peer is an EDGE, the returned value depends on the fact that
     * the EDGE is connected to a RDV or not (not change in behavior).}
     *
     * </p>REM: Before release 2.6, the information returned by this method was
     * ambiguous. For ADHOC peers, it would always return {@code true} although
     * the peer was never connected to a RDV. For RDV peers, it would always return
     * {@code false}. For EDGE peers, the returned value depended on the fact that
     * the EDGE was connected to a RDV or not.
     *
     * @return {@code true} if connected to a rendezvous otherwise {@code false}.
     */
    public boolean isConnectedToRendezVous();

    /**
     * Returns {@code true} if this peer is acting as a rendezvous peer (per the
     * implementation definition) within the peer group. {@code false} is
     * returned for all other peer roles.
     *
     * @return {@code true} if this peer is acting as a rendezvous peer (per the
     * implementation definition) within the peer group. {@code false} is
     * returned for all other peer roles.
     */
    public boolean isRendezVous();

    /**
     * Returns the current role of this peer within the peergroup.
     *
     * @return The current role of this peer within the peergroup.
     */
    public RendezVousStatus getRendezVousStatus();

    /**
     *  Enable or disable the automatic switching between an Edge Peer
     *  and a Rendezvous Peer.
     *
     * @param auto {@code true} will activate automatic role switching.
     * @return The previous auto start value.
     */
    public boolean setAutoStart(boolean auto);

    /**
     *  Enable or disable the automatic switching between an Edge Peer
     *  and a Rendezvous Peer.
     *
     * @param auto {@code true} will activate automatic role switching.
     * @param period The interval in milliseconds at which the peer should
     * reconsider it's role.
     * @return The previous auto start value.
     */
    public boolean setAutoStart(boolean auto, long period);

    /**
     * Walk a message through the rendezvous peers of the network: only
     * rendezvous peers will receive the message.
     *
     * <p/>Only a single HOP at a time is performed. Messages are always
     * delivered to the destination handler on arrival. This handler
     * is responsible for repropagating further, if deemed appropriate.
     *
     * <p/>Loop and TTL control are performed automatically.
     *
     * <p/>Messages can be propagated via this method for the first time or
     * can be re-propagated by re-using a message that came in via propagation.
     * In the later case, the TTL and loop detection parameters CANNOT be
     * re-initialized. If one wants to "re-propagate" a message with a new TTL
     * and blank gateways list one must generate a completely new message.
     * This limits the risk of accidental propagation storms, although they
     * can always be engineered deliberately.
     *
     * <p/><b>Note</b>: The original msg is not modified and may be reused upon return.
     *
     * @param  msg           is the message to walk.
     * @param  serviceName   is the name of the service
     * @param  serviceParam  is the parameter of the service
     * @param  ttl           is the maximum TTL of the message.
     * @throws  IOException  when walking the message is impossible (network failure)
     */
    public void walk(Message msg, String serviceName, String serviceParam, int ttl) throws IOException;

    /**
     * <p/>Walk a message through the rendezvous peers of the network: only
     * rendezvous peers will receive the message.
     *
     * <p/>Only a single HOP at a time is performed. Messages are always
     * delivered to the destination handler on arrival. This handler
     * is responsible for repropagating further, if deemed appropriate.
     *
     * <p/>Loop and TTL control are performed automatically.
     *
     * <p/>Messages can be propagated via this method for the first time or
     * can be re-propagated by re-using a message that came in via propagation.
     * In the later case, the TTL and loop detection parameters CANNOT be
     * re-initialized. If one wants to "re-propagate" a message with a new TTL
     * and blank gateways list one must generate a completely new message.
     * This limits the risk of accidental propagation storms, although they
     * can always be engineered deliberately.
     *
     * <p/><b>Note</b>: The original msg is not modified and may be reused upon return.
     *
     * @param  destPeerIDs   is a Vector of PeerIDs of the peers which are receiving
     * first the walker. Note that each entry in the Vector will create its own
     * walker.
     * @param  msg           is the message to walk.
     * @param  serviceName   is the name of the service
     * @param  serviceParam  is the parameter of the service
     * @param  ttl           is the maximum TTL of the message.
     * @throws  IOException  when walking the message is impossible (network failure)
     */
    public void walk(Vector<? extends ID> destPeerIDs, Message msg, String serviceName, String serviceParam, int ttl) throws IOException;

    /**
     * Provides a list of locally visible RendezVous peer IDs.
     * </p>If this peer is a RENDEZVOUS, the method will return all know RDVs from its peer view.
     * If this peer is an EDGE it will return the rendezvous it is connected to. If this peer is
     * ADHOC, it will return an empty list.
     *
     * @return a local view of RDV peer IDs.
     */
    public List<PeerID> getLocalRendezVousView();

    /**
     * Provides a list of locally connected EDGE peer IDs.
     * </p>If this peer is a RENDEZVOUS, the method will return the list of EDGE peer
     * IDs connected to this peer. If this peer is an EDGE or ADHOC, it will return an
     * empty list.
     *
     * @return a local view of connected EDGE peer IDs.
     */
    public List<PeerID> getLocalEdgeView();
}
