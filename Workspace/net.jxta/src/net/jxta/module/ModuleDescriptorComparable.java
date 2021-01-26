package net.jxta.module;

public class ModuleDescriptorComparable implements Comparable<IModuleDescriptor> {

	private IJxtaModuleDescriptor thisOne;
	
	public ModuleDescriptorComparable( IJxtaModuleDescriptor thisOne) {
		this.thisOne = thisOne;
	}

	public int compareTo(IModuleDescriptor o) {
		if( thisOne.equals( o ))
			return 0;
		if(!( o instanceof IJxtaModuleDescriptor ))
			return 1;
		IJxtaModuleDescriptor thatOne = (IJxtaModuleDescriptor) o;
		int comp = thisOne.getModuleSpecID().toString().compareTo( thatOne.getModuleSpecID().toString() );
		if( comp != 0 )
			return comp;
		comp = thisOne.getRepresentedClassName().compareTo( thatOne.getRepresentedClassName() );
		if( comp != 0 )
			return comp;
		return thisOne.getVersion().compareTo( thatOne.getVersion() );
	}
	
}