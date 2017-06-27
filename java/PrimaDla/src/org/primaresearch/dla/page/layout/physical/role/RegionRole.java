package org.primaresearch.dla.page.layout.physical.role;

import org.primaresearch.dla.page.layout.physical.AttributeContainer;
import org.primaresearch.dla.page.layout.physical.shared.RoleType;

/**
 * Interface for roles that regions can take one (e.g. table cell).
 * 
 * @author Christian Clausner
 *
 */
public interface RegionRole extends AttributeContainer {

	/**
	 * The type of region role (e.g. table cell)
	 */
	public RoleType getType();
}
