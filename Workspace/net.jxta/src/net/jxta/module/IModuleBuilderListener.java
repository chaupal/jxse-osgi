package net.jxta.module;

public interface IModuleBuilderListener<T extends Object> {

	public enum BuildEvents{
		INITIALSED,
		BUILT;
	}
	
	/**
	 * Notify listeners top a built module
	 * @param event
	 */
	public void notifyModuleBuilt( ModuleEvent<T> event );
}
