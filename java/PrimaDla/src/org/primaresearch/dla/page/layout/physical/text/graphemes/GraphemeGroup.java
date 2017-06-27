package org.primaresearch.dla.page.layout.physical.text.graphemes;

import java.util.ArrayList;
import java.util.List;

import org.primaresearch.dla.page.layout.physical.AttributeFactory;
import org.primaresearch.dla.page.layout.physical.shared.ContentType;
import org.primaresearch.dla.page.layout.physical.shared.LowLevelTextType;
import org.primaresearch.dla.page.layout.physical.text.impl.Glyph;
import org.primaresearch.ident.Id;
import org.primaresearch.ident.IdRegister;
import org.primaresearch.shared.variable.VariableMap;

/**
 * A grapheme element containing multiple child graphemes or non-printing characters.
 * 
 * @author Christian Clausner
 *
 */
public class GraphemeGroup extends GraphemeElementImpl implements GraphemeElement {

	private List<GraphemeElement> graphemes = new ArrayList<GraphemeElement>();

	/**
	 * Constructor
	 * @param idRegister
	 * @param id
	 * @param attributes
	 * @param parent
	 * @param attrFactory
	 */
	protected GraphemeGroup(IdRegister idRegister, Id id, VariableMap attributes, Glyph parent,
			AttributeFactory attrFactory) {
		super(idRegister, id, attributes, parent, attrFactory);
	}
	
	/**
	 * Returns a list of all members of this group. 
	 */
	public List<GraphemeElement> getGraphemes() {
		return graphemes;
	}
	
	/**
	 * Returns the member count
	 */
	public int getSize() {
		return graphemes.size();
	}
	
	/**
	 * Adds a new member to this group. Groups cannot be added as members (will throw exception).
	 * @param member Grapheme or NonPrintingCharacter
	 */
	public void addMember(GraphemeElement member) {
		if (member == null || member instanceof GraphemeGroup)
			throw new IllegalArgumentException("Group member cannot be null or of type GraphemeGroup");
		graphemes.add(member);
	}

	@Override
	public ContentType getType() {
		return LowLevelTextType.GraphemeGroup;
	}

}
