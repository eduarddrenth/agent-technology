package org.logica.ctis.security;

import jade.core.Profile;
import jade.core.ProfileException;

/**
 * This interface extends the basic TokenProvider interface by adding the init() method that
 * is invoked only once just after the TokenProvider instantiation and gives it access to 
 * Profile configurations 
 */
public interface ConfigurableTokenProvider extends TokenProvider {
	void init(Profile p) throws ProfileException;
}
