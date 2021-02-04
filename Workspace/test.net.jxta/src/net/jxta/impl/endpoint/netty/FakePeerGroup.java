package net.jxta.impl.endpoint.netty;

import java.io.IOException;
import java.net.URI;
import java.util.Iterator;

import net.jxta.access.AccessService;
import net.jxta.content.ContentService;
import net.jxta.discovery.DiscoveryService;
import net.jxta.document.Element;
import net.jxta.document.Advertisement;
import net.jxta.endpoint.EndpointService;
import net.jxta.exception.PeerGroupException;
import net.jxta.exception.ProtocolNotSupportedException;
import net.jxta.exception.ServiceNotFoundException;
import net.jxta.id.ID;
<<<<<<< HEAD
=======
import net.jxta.id.IDFactory;
import net.jxta.impl.util.threads.TaskManager;
>>>>>>> branch 'main' of https://github.com/chaupal/jxse-osgi.git
import net.jxta.membership.MembershipService;
import net.jxta.protocol.ModuleImplAdvertisement;
import net.jxta.peer.PeerInfoService;
import net.jxta.peer.PeerID;
import net.jxta.peergroup.core.Module;
import net.jxta.peergroup.core.ModuleSpecID;
import net.jxta.peergroup.PeerGroup;
import net.jxta.peergroup.PeerGroupID;
import net.jxta.peergroup.core.IJxtaLoader;
import net.jxta.pipe.PipeService;
import net.jxta.protocol.ConfigParams;
import net.jxta.protocol.PeerAdvertisement;
import net.jxta.protocol.PeerGroupAdvertisement;
import net.jxta.rendezvous.RendezVousService;
import net.jxta.resolver.ResolverService;
import net.jxta.service.Service;
import net.jxta.util.threads.TaskManager;

public class FakePeerGroup implements PeerGroup {

    public static final PeerID DEFAULT_PEER_ID = IDFactory.create(URI.create("urn:jxta:uuid-59616261646162614E50472050325033857CA397BC2A48DB86FED88696A80AA003"));
    
    public PeerGroup parentGroup = null;
    public PeerGroupID peerGroupId = PeerGroupID.defaultNetPeerGroupID;
    public PeerID peerId = DEFAULT_PEER_ID;
    public String peerGroupName = "FakeGroupForTesting";
    public String peerName = "FakePeerForTesting";
    
    public FakeEndpointService endpointService = new FakeEndpointService(this);

    public ConfigParams configAdv;
    
    @Override
	public PeerGroup getParentGroup() {
        return parentGroup;
    }
    
    @Override
	public PeerGroupID getPeerGroupID() {
        return peerGroupId;
    }
    
    @Override
	public String getPeerGroupName() {
        return peerGroupName;
    }

    @Override
	public PeerID getPeerID() {
        return peerId;
    }
    
    @Override
	public String getPeerName() {
        return peerName;
    }
    
    @Override
	public EndpointService getEndpointService() {
        return endpointService;
    }
    
    @Override
	public ConfigParams getConfigAdvertisement() {
        return configAdv;
    }
    
    /* UNIMPLEMENTED, IRRELEVANT METHODS BEYOND THIS POINT */
    
    @Override
	public boolean compatible(Element<?> compat) {
        throw new RuntimeException("not implemented");
    }

    @Override
	public AccessService getAccessService() {
        throw new RuntimeException("not implemented");
    }

    @Override
	public ModuleImplAdvertisement getAllPurposePeerGroupImplAdvertisement() throws Exception {
        throw new RuntimeException("not implemented");
    }

    @Override
	public DiscoveryService getDiscoveryService() {
        throw new RuntimeException("not implemented");
    }

    @Override
	public GlobalRegistry getGlobalRegistry()
    {
        throw new UnsupportedOperationException("getGlobalRegistry not implemented");
    }

    public ThreadGroup getHomeThreadGroup() {
        throw new RuntimeException("not implemented");
    }

    public IJxtaLoader getLoader() {
        throw new RuntimeException("not implemented");
    }

    @Override
	public MembershipService getMembershipService() {
        throw new RuntimeException("not implemented");
    }

    @Override
	public PeerAdvertisement getPeerAdvertisement() {
        throw new RuntimeException("not implemented");
    }

    @Override
	public PeerGroupAdvertisement getPeerGroupAdvertisement() {
        throw new RuntimeException("not implemented");
    }    
    
    @Override
	public PeerInfoService getPeerInfoService() {
        throw new RuntimeException("not implemented");
    }

    @Override
	public PipeService getPipeService() {
        throw new RuntimeException("not implemented");
    }

    @Override
	public RendezVousService getRendezVousService() {
        throw new RuntimeException("not implemented");
    }

    @Override
	public ResolverService getResolverService() {
        throw new RuntimeException("not implemented");
    }

    @Override
	public Iterator<ID> getRoleMap(ID name) {
        throw new RuntimeException("not implemented");
    }

    @Override
	public URI getStoreHome() {
        throw new RuntimeException("not implemented");
    }

    public PeerGroup getWeakInterface() {
        throw new RuntimeException("not implemented");
    }

    @Override
	public boolean isRendezvous() {
        throw new RuntimeException("not implemented");
    }

    @Override
	public Module loadModule(ID assignedID, Advertisement impl) throws ProtocolNotSupportedException, PeerGroupException {
        throw new RuntimeException("not implemented");
    }

    @Override
	public Module loadModule(ID assignedID, ModuleSpecID specID, int where) {
        throw new RuntimeException("not implemented");
    }

    @Override
	public Service lookupService(ID name) throws ServiceNotFoundException {
        throw new RuntimeException("not implemented");
    }

    @Override
	public Service lookupService(ID name, int roleIndex) throws ServiceNotFoundException {
        throw new RuntimeException("not implemented");
    }

    @Override
	public PeerGroup newGroup(Advertisement pgAdv) throws PeerGroupException {
        throw new RuntimeException("not implemented");
    }

    @Override
	public PeerGroup newGroup(PeerGroupID gid, Advertisement impl, String name, String description) throws PeerGroupException {
        throw new RuntimeException("not implemented");
    }

    @Override
	public PeerGroup newGroup(PeerGroupID gid, Advertisement impl, String name, String description, boolean publish) throws PeerGroupException {
        throw new RuntimeException("not implemented");
    }

    @Override
	public PeerGroup newGroup(PeerGroupID gid) throws PeerGroupException {
        throw new RuntimeException("not implemented");
    }

    @Override
	public void publishGroup(String name, String description) throws IOException {
        throw new RuntimeException("not implemented");
    }

//    public boolean unref() {
//        throw new RuntimeException("not implemented");
//    }

    @Override
	public Advertisement getImplAdvertisement() {
        throw new RuntimeException("not implemented");
    }

//    public PeerGroup getInterface() {
//        throw new RuntimeException("not implemented");
//    }

    @Override
	public void init(PeerGroup group, ID assignedID, Advertisement implAdv) throws PeerGroupException {
        throw new RuntimeException("not implemented");
    }

    @Override
	public int startApp(String[] args) {
        throw new RuntimeException("not implemented");
    }

    @Override
	public void stopApp() {
        throw new RuntimeException("not implemented");
    }

    @Override
	public ContentService getContentService() {
        throw new RuntimeException("not implemented");
    }

    @Override
	public TaskManager getTaskManager() {
        throw new RuntimeException("not implemented");
    }

}
