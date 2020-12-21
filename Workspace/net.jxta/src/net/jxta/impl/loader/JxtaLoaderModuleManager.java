package net.jxta.impl.loader;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import net.jxta.document.Advertisement;
import net.jxta.document.AdvertisementFactory;
import net.jxta.document.Element;
import net.jxta.document.StructuredDocument;
import net.jxta.document.XMLElement;
import net.jxta.exception.PeerGroupException;
import net.jxta.id.ID;
import net.jxta.impl.modulemanager.ImplAdvModuleDescriptor;
import net.jxta.impl.modulemanager.ImplAdvertisementComparable;
import net.jxta.impl.modulemanager.JxtaModuleBuilder;
import net.jxta.impl.modulemanager.ModuleException;
import net.jxta.impl.modulemanager.ModuleVerifier;
import net.jxta.impl.peergroup.CompatibilityEquater;
import net.jxta.impl.peergroup.CompatibilityUtils;
import net.jxta.impl.protocol.PeerGroupConfigAdv;
import net.jxta.impl.protocol.PeerGroupConfigFlag;
import net.jxta.logging.Logger;
import net.jxta.logging.Logging;
import net.jxta.module.IJxtaModuleBuilder;
import net.jxta.module.IJxtaModuleDescriptor;
import net.jxta.module.IJxtaModuleManager;
import net.jxta.module.IModuleBuilder;
import net.jxta.module.IModuleDescriptor;
import net.jxta.module.IModuleManager;
import net.jxta.peergroup.IModuleDefinitions;
import net.jxta.peergroup.PeerGroup;
import net.jxta.peergroup.core.IJxtaLoader;
import net.jxta.peergroup.core.Module;
import net.jxta.peergroup.core.ModuleSpecID;
import net.jxta.protocol.ModuleImplAdvertisement;
import net.jxta.protocol.PeerGroupAdvertisement;
import net.jxta.util.cardinality.Cardinality;
import net.jxta.util.cardinality.CardinalityException;
import net.jxta.util.cardinality.ICardinality;

public class JxtaLoaderModuleManager<T extends Module> implements IJxtaModuleManager<T>{

	public static final String S_ERR_INVALID_BUILDER = "The builder is not of the required interface: ";
	
    /**
     * Default compatibility equater instance.
     */
    private static final CompatibilityEquater COMP_EQ =
    	new CompatibilityEquater() {
        public boolean compatible(Element<?> test) {
            return CompatibilityUtils.isCompatible(test);
        }
    };

    /**
     * Statically scoped JxtaLoader which is used as the root of the
     * JXTA class loader hierarchy.  The world peer group defers to the
     * static loader as it's parent class loader.
     * <p/>
     * This class loader is a concession.  Use the group-scoped loader
     * instead.
     * <p/>
     */
    private static JxtaLoaderModuleManager<Module> root;

    private final static transient Logger LOG = Logging.getLogger( JxtaLoaderModuleManager.class.getName());

    private JxtaLoaderModuleManager<? extends Module> parent = null;
    
    //apart from the root manager, every module manager is 
    private PeerGroup peergroup;
    private IJxtaLoader loader;
    
    private static Map<PeerGroup, IModuleManager<? extends Module>> managers;
    
    private Collection<IModuleBuilder<T>> builders;
    
    private boolean started;
    private Lock lock;
    private ModuleVerifier<T> verifier;
    
	protected JxtaLoaderModuleManager( ClassLoader loader) {
		this.loader = new RefJxtaLoader(new URL[0], loader, COMP_EQ );
		managers = new HashMap<PeerGroup, IModuleManager<? extends Module>>();
		builders = new ArrayList<IModuleBuilder<T>>();
		this.verifier = new ModuleVerifier<T>( builders );
		this.started = false;
		lock = new ReentrantLock();
	}

	/**
	 * Onle allowed for the root loader
	 * @param loader
	 */
	private JxtaLoaderModuleManager( IJxtaLoader loader) {
		this.loader = loader;
		builders = new ArrayList<IModuleBuilder<T>>();
		this.verifier = new ModuleVerifier<T>( this.builders );
		managers = new HashMap<PeerGroup, IModuleManager<? extends Module>>();
		this.started = false;
		lock = new ReentrantLock();
	}

	@SuppressWarnings("unchecked")
	private JxtaLoaderModuleManager( IJxtaLoader loader, JxtaLoaderModuleManager<? extends Module> parent, PeerGroup peergroup ) {
		this.loader = loader;
		this.parent = parent;
		this.verifier = getVerifier( (IJxtaModuleManager<T>) parent, peergroup );
		managers = new HashMap<PeerGroup, IModuleManager<? extends Module>>();
		builders = new ArrayList<IModuleBuilder<T>>();
		this.started = false;
		lock = new ReentrantLock();
	}

	/**
	 * initialise the module manager
	 */
	public void init(PeerGroup group, ID assignedID, Advertisement implAdv)
			throws PeerGroupException {
		this.peergroup = group;
	}

	/**
	 * Get the peer group  that owns this module manager, or null if it is the root
	 * @return
	 */
	public PeerGroup getPeergroup() {
		return peergroup;
	}
	
	public ModuleVerifier<T> getVerifier() {
		return verifier;
	}

	public IJxtaLoader getLoader() {
		return loader;
	}

	
	/**
	 * Get a list of all the cardinalities which are equal to the given reference
	 * @return
	 */
	protected Collection<ICardinality> getCardinalityCollection( IModuleDescriptor reference ){
		Collection<ICardinality> collection = new ArrayList<ICardinality>();
		try{
			lock.lock();

			for( IModuleBuilder<T> builder: builders ){
				IModuleDescriptor[] descriptors = builder.getSupportedDescriptors();
				for( IModuleDescriptor mdesc: descriptors ){
					if( mdesc.compareTo( reference ) == 0 )
						collection.add( mdesc );  
				}
			}
		}
		finally{
			lock.unlock();
		}
		return collection;
	}
	
	/**
	 * Registers a builder if the cardinality allows it.
	 */
	public synchronized void registerBuilder(IModuleBuilder<T> builder) {
		IModuleDescriptor[] descriptors = builder.getSupportedDescriptors();
		Collection<ICardinality> collection = null;
		int result = 0;
		for( IModuleDescriptor descriptor: descriptors ){
			collection = getCardinalityCollection(descriptor);
			result = Cardinality.accept(collection, descriptor );
			if( result != 0 )	
				throw new CardinalityException( this, descriptor, result);
		}
		try{
			lock.lock();
			builders.add( builder );
		}
		finally{
			lock.unlock();
		}
	}

	public synchronized void unregisterBuilder(IModuleBuilder<T> builder) {
		try{
			lock.lock();
			builders.remove( builder );
		}
		finally{
			lock.unlock();
		}
	}

	/**
	 * Loads the given descriptor in the module manager. This basically means that:
	 * 1: The descriptor is initialised
	 * 2: The corresponding module impl advertisement is created 
	 * @param builder
	 * @param descriptor
	 * @return
	 */
	protected boolean loadDescriptor( IModuleBuilder<T> builder, IModuleDescriptor descriptor){
		if(!( descriptor instanceof IJxtaModuleDescriptor ))
			return false;
		builder.initialise(descriptor);
		return verifier.acceptDescriptor(builder, descriptor);
	}
	
    /**
     *  Finds the ModuleImplAdvertisement for the associated class in the 
     *  context of this ClassLoader.
     *
     *  @param msid The module spec id who's ModuleImplAdvertisement is desired.
     *  @return The matching {@code ModuleImplAdvertisement} otherwise
     *  {@code null} if there is no known association.
     */
    public ModuleImplAdvertisement findModuleImplAdvertisement(ModuleSpecID msid){
    	for( IModuleBuilder<T> builder: builders ){
    		IModuleDescriptor[] descriptors = builder.getSupportedDescriptors();
    		for( IModuleDescriptor descriptor: descriptors ){
        		if(!loadDescriptor(builder, descriptor))
        			continue;
         		IJxtaModuleDescriptor jdescriptor = (IJxtaModuleDescriptor) descriptor;
        		if( jdescriptor.getModuleSpecID().equals( msid ))
        			return jdescriptor.getModuleImplAdvertisement();
    		}
    	}
    	ModuleImplAdvertisement implAdv = null;
    	try{
    		implAdv = loader.findModuleImplAdvertisement(msid);
    	}
    	catch( Exception ex ){
    		ex.printStackTrace();
    	}
    	return implAdv;
    }

	@SuppressWarnings("unchecked")
	public T getModule(ModuleImplAdvertisement implAdv) throws ModuleException {
		IModuleBuilder<T>[] builders = this.findBuilders(implAdv);
		T module = null;
		if(( builders != null ) && ( builders.length > 0 )){
			IJxtaModuleBuilder<T> builder = (IJxtaModuleBuilder<T>) builders[0];
			IJxtaModuleDescriptor descriptor = builder.getDescriptor( implAdv );
			if( this.loadDescriptor(builder, descriptor))
					module = builder.buildModule(descriptor);
		}
		if( module != null )
			return module;
		if( parent != null )
			module = (T) parent.getModule(implAdv);
		if( module != null )
			return module;
		return (T) loadModule( loader, implAdv );
	}

	/**
	 * Returns the builders corresponding to the given impl advertisement. The result is also sorted for
	 * version numbers, if they are included, with the highest version first.
	 * @param impl
	 * @return
	 */
	protected boolean hasBuilder( ModuleImplAdvertisement impl ){
		for( IModuleBuilder<T> builder: builders ){
    		IModuleDescriptor[] descriptors = builder.getSupportedDescriptors();
    		for( IModuleDescriptor descriptor: descriptors ){
        		if(!( descriptor instanceof IJxtaModuleDescriptor ))
        			continue;
    			IJxtaModuleDescriptor jdescriptor = (IJxtaModuleDescriptor) descriptor;
        		ImplAdvertisementComparable comp = new ImplAdvertisementComparable( jdescriptor.getModuleImplAdvertisement() );
    			if( comp.compareTo( impl ) == 0 )
					return true;
    		}
    	}
		return false;
	}

	/**
	 * Retuens the builders corresponding to the given impl advertisement. The result is also sorted for
	 * version numbers, if they are included, with the highest version first.
	 * @param impl
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected IModuleBuilder<T>[] findBuilders( ModuleImplAdvertisement impl ){
    	Map<IModuleDescriptor, IModuleBuilder<T>> found = new HashMap<IModuleDescriptor,IModuleBuilder<T>>( );
		for( IModuleBuilder<T> builder: builders ){
    		IModuleDescriptor[] descriptors = builder.getSupportedDescriptors();
    		for( IModuleDescriptor descriptor: descriptors ){
        		if(!( descriptor instanceof IJxtaModuleDescriptor ))
        			continue;
    			builder.initialise(descriptor);
        		IJxtaModuleDescriptor jdescriptor = (IJxtaModuleDescriptor) descriptor;
        		ImplAdvertisementComparable comp = new ImplAdvertisementComparable( jdescriptor.getModuleImplAdvertisement() );
    			if( comp.compareTo( impl ) == 0 )
					found.put( jdescriptor, builder );
    		}
    	}
		Collection<IModuleBuilder<T>> results = found.values();//Already sorted for version numbers 
		return results.toArray( new IModuleBuilder[ results.size() ]);
	}

	/**
	 * Register a builder, based on the impl Adv.If the builder already exists, a false is returned,
	 * otherwise a true is given 
	 * @param implAdv
	 * @return
	 */
	public boolean register( ModuleImplAdvertisement implAdv ){
    	IModuleBuilder<T>[] jbuilders = this.findBuilders(implAdv);
    	if(( jbuilders != null ) && ( jbuilders.length > 0 )){
        	IJxtaModuleBuilder<T> jbuilder = (IJxtaModuleBuilder<T>) jbuilders[0];
    		jbuilder.getRepresentedClass( new ImplAdvModuleDescriptor( implAdv));
    		return false;
    	}
    	IJxtaModuleBuilder<T> builder = new JxtaModuleBuilder<T>( this.loader ); 
    	this.registerBuilder( builder );
    	return builders.contains( builder );
	}

	/**
	 * Find the module impl advertisement that is represented by the given class
	 * @param clss
	 * @return
	 */
	public ModuleImplAdvertisement findModuleImplAdvertisement( Class<? extends Module> clss ){
    	for( IModuleBuilder<T> builder: builders ){
    		IModuleDescriptor[] descriptors = builder.getSupportedDescriptors();
    		for( IModuleDescriptor descriptor: descriptors ){
        		if(!( descriptor instanceof IJxtaModuleDescriptor ))
        			continue;
    			IJxtaModuleDescriptor jdescriptor = (IJxtaModuleDescriptor) descriptor;
    			if( jdescriptor.getRepresentedClassName().equals( clss.getCanonicalName() ))
    				return jdescriptor.getModuleImplAdvertisement();
    		}
    	}
		return loader.findModuleImplAdvertisement( clss);
	}

	/**
	 * Get the module manager of the given peergroup
	 * @param peergroup
	 * @return
	 */
	public IModuleManager<? extends Module> getModuleManager( PeerGroup peergroup){
		if( managers.isEmpty())
			return root;
		IModuleManager<? extends Module> manager = managers.get( peergroup );
		return ( manager == null )? root: manager;
	}

	/**
	 * Print the registered Builders 
	 * @return
	 */
	public String printRegisteredBuilders(){
		StringBuffer buffer = new StringBuffer();
		buffer.append("The following JXSE modules are registered: \n");
		for( IModuleBuilder<?> builder: this.builders ){
			buffer.append( builder.toString() + "\n" );
		}
		return buffer.toString();
	}
	
	/**
	 * Get the class loader of the given peergroup. This is the corresponding jxta loader
	 * @param peergroup
	 * @return
	 */
	public static ClassLoader getClassLoader( PeerGroup peergroup){
		JxtaLoaderModuleManager<? extends Module> manager = (JxtaLoaderModuleManager<? extends Module>) managers.get( peergroup );
		if( manager == null )
			return (ClassLoader) root.getLoader();
		return (ClassLoader) manager.getLoader().getClassLoader();
	}

    @Override
	public int hashCode() {
		return loader.hashCode();
	}

	/**
     * Load a Module from a ModuleImplAdv.
     * <p/>
     * Compatibility is checked and load is attempted. If compatible and
     * loaded successfully, the resulting Module is initialized and returned.
     * In most cases the other loadModule() method should be preferred, since
     * unlike this one, it will seek many compatible implementation
     * advertisements and try them all until one works. The home group of the new
     * module (it's parent group if the new Module is a group) will be this group.
     *
     * @param assigned   Id to be assigned to that module (usually its ClassID).
     * @param implAdv    An implementation advertisement for that module.
     * @param privileged If {@code true} then the module is provided the true
     *                   group object instead of just an interface to the group object. This is
     *                   normally used only for the group's defined services and applications.
     * @return Module the module loaded and initialized.
     * @throws ModuleException 
     */
	protected static Module loadModule( IJxtaLoader loader, ModuleImplAdvertisement implAdv) throws ModuleException {

		Module loadedModule = null; 
		if ((null != implAdv.getCode()) && (null != implAdv.getUri())) {
			try{
			// Good one. Try it.
			Class<? extends Module> loadedModuleClass;
			try {
				loadedModuleClass = loader.loadClass(implAdv.getModuleSpecID());
			} catch (ClassNotFoundException exception) {
				loadedModuleClass = loader.defineClass(implAdv);
			}
			if (null == loadedModuleClass) {
				throw new ClassNotFoundException("Cannot load class (" + implAdv.getCode() + ") : ");
			}
            loadedModule = loadedModuleClass.newInstance();
			}
			catch( Exception ex ){
                Logging.logCheckedError(LOG,ex);
				
			}
		} else {
			String error;

			if (null == implAdv.getCode()) {
				error = "ModuleImpAdvertisement missing Code element";
			} else if (null == implAdv.getUri()) {
				error = "ModuleImpAdvertisement missing URI element";
			} else {
				error = "ModuleImpAdvertisement missing both Code and URI elements";
			}
			throw new ModuleException("Can not load module : " + error);
		}
		return loadedModule;
	}    

	/**
	 * Create a new module manager from the given parent group
	 * @param parentGroup
	 * @param peerGroupAdvertisement
	 * @return
	 */
	public static IModuleManager<Module> createModuleManager( PeerGroup peergroup, PeerGroupAdvertisement peerGroupAdvertisement ){
        IModuleManager<Module> manager = null;
        PeerGroup parentGroup = peergroup.getParentGroup();
        if (null == parentGroup) {

            Logging.logCheckedDebug(LOG, "Setting up group loader -> static loader");
            manager = root;

        } else {
            //Get the parent manager
        	JxtaLoaderModuleManager<? extends Module> pm = (JxtaLoaderModuleManager<? extends Module>) managers.get( parentGroup ); 
            if( pm == null )
            	pm = root;
            IJxtaLoader upLoader = pm.getLoader();
            StructuredDocument<?> cfgDoc =
                    peerGroupAdvertisement.getServiceParam(
                    		IModuleDefinitions.peerGroupClassID);
            PeerGroupConfigAdv pgca;
            if (cfgDoc != null) {
                pgca = (PeerGroupConfigAdv)
                        AdvertisementFactory.newAdvertisement((XMLElement<?>)
                        peerGroupAdvertisement.getServiceParam(IModuleDefinitions.peerGroupClassID));
                if (pgca.isFlagSet(PeerGroupConfigFlag.SHUNT_PARENT_CLASSLOADER)) {
                    // We'll shunt to the same class loader that loaded this
                    // class, but not the JXTA form (to prevent Module
                    // definitions).
                    upLoader = (IJxtaLoader)JxtaLoaderModuleManager.class.getClassLoader();
                }
            }

            Logging.logCheckedDebug(LOG, "Setting up group loader -> ", upLoader);
            manager = new JxtaLoaderModuleManager<Module>(upLoader, pm, peergroup );
            managers.put( peergroup, manager);
        }
        return manager;
		
	}

	/**
	 * Get the module configuration object that belongs to the given module implementation advertisement
	 * @param implAdv
	 * @return
	 */
	private ModuleVerifier<T> getVerifier( IJxtaModuleManager<T> manager, PeerGroup peergroup ){
		ModuleImplAdvertisement implAdv = (ModuleImplAdvertisement) peergroup.getImplAdvertisement();
		return manager.getVerifier().createVerifier( implAdv );
	}

	/**
	 * (non-Javadoc)
	 * @see net.jxta.module.IModuleClient#canBuild(net.jxta.module.IModuleDescriptor)
	 */
	public boolean canBuild(IModuleDescriptor descriptor) {
		return this.verifier.canBuild(descriptor);
	}

	/**
	 * Get a root manager for the given class
	 * @param clzz
	 * @return
	 */
	public static JxtaLoaderModuleManager<Module> getRoot(){
		return getRoot(RefJxtaLoader.class, false);
	}

	/**
	 * Get a root manager for the given class
	 * @param clzz
	 * @return
	 */
	public static JxtaLoaderModuleManager<Module> getRoot( Class<?> clzz ){
		return getRoot( clzz, false );
	}

	/**
	 * Get a root manager for the given class
	 * @param clzz
	 * @return
	 */
	public static JxtaLoaderModuleManager<Module> getRoot( Class<?> clzz, boolean forceNew ){
		if(( root == null ) || ( forceNew )){
			IJxtaLoader staticLoader = new RefJxtaLoader( new URL[0], clzz.getClassLoader(), COMP_EQ);
			root = new JxtaLoaderModuleManager<Module>( staticLoader );
		}
		return root;
	}

	protected boolean hasStarted(){
		return this.started;
	}
	
	public int startApp(String[] args) {		// TODO Auto-generated method stub
		this.started = true;
		return 0;
	}

	public void stopApp() {
		this.started = false;
		
	}
}
