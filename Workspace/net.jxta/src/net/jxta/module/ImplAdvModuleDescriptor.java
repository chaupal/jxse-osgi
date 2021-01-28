package net.jxta.module;

import net.jxta.document.Advertisement;
import net.jxta.peergroup.core.ModuleClassID;
import net.jxta.peergroup.core.ModuleSpecID;
import net.jxta.platform.PlatformConfig;
import net.jxta.protocol.ModuleImplAdvertisement;
import net.jxta.util.cardinality.Cardinality;
import net.jxta.util.cardinality.Cardinality.Denominator;

public class ImplAdvModuleDescriptor extends AbstractModuleDescriptor
		implements IJxtaModuleDescriptor {

	private static final String S_DEFAULT_VERSION = "2.8.0";
	
	private ModuleImplAdvertisement implAdv;
	private String version;

	protected ImplAdvModuleDescriptor() {
	}

	public ImplAdvModuleDescriptor( ModuleImplAdvertisement implAdv) {
		this.prepare( implAdv );
	}

	/**
	 * Prepare the descriptor
	 */
	protected void prepare(  ModuleImplAdvertisement implAdv ){ 
		this.implAdv = implAdv;
		super.setIdentifier( this.implAdv.getCode() );
		super.setRefClass( this.implAdv.getCode() );
		super.setDescription( this.implAdv.getDescription());
		super.setCardinality( Cardinality.create( Denominator.ONE ));
		super.setVersion( S_DEFAULT_VERSION );
	}

	
	public String getVersion() {
		return version;
	}

	public ModuleClassID getModuleClassID() {
		return this.implAdv.getModuleSpecID().getBaseClass();
	}

	public ModuleSpecID getModuleSpecID() {
		return this.implAdv.getModuleSpecID();
	}

	public ModuleImplAdvertisement getModuleImplAdvertisement() {
		return implAdv;
	}

	protected void setImplAdv(ModuleImplAdvertisement implAdv) {
		this.implAdv = implAdv;
	}

	public String getRepresentedClassName() {
		return super.getIdentifier();
	}

	public Advertisement getAdvertisement(PlatformConfig config) {
		// TODO Auto-generated method stub
		return null;
	}

	public int compareTo(IModuleDescriptor o) {
		ModuleDescriptorComparable dc = new ModuleDescriptorComparable(this);
		return dc.compareTo(o);
	}

	@Override
	protected boolean onInitialised() {
		// TODO Auto-generated method stub
		return false;
	}
}
