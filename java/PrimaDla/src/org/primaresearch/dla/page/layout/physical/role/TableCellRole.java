package org.primaresearch.dla.page.layout.physical.role;

import org.primaresearch.dla.page.layout.physical.AttributeFactory;
import org.primaresearch.dla.page.layout.physical.shared.RoleType;
import org.primaresearch.shared.variable.VariableMap;

/**
 * A role representing a table cell. To be used for regions that are nested in a table region.
 * 
 * @author Christian Clausner
 *
 */
public class TableCellRole implements RegionRole {

	private VariableMap attributes;

	/**
	 * Constructor
	 * @param attrFactory
	 */
	public TableCellRole(AttributeFactory attrFactory) {
		attributes = attrFactory.createAttributes(RoleType.TableCellRole);
	}
	
	@Override
	public VariableMap getAttributes() {
		return attributes;
	}

	@Override
	public RoleType getType() {
		return RoleType.TableCellRole;
	}

}
