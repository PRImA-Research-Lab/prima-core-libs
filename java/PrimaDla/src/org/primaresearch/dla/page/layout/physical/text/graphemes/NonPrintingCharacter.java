package org.primaresearch.dla.page.layout.physical.text.graphemes;

import org.primaresearch.dla.page.layout.physical.AttributeFactory;
import org.primaresearch.dla.page.layout.physical.shared.ContentType;
import org.primaresearch.dla.page.layout.physical.shared.LowLevelTextType;
import org.primaresearch.dla.page.layout.physical.text.impl.Glyph;
import org.primaresearch.ident.Id;
import org.primaresearch.ident.IdRegister;
import org.primaresearch.shared.variable.VariableMap;

/**
 * A grapheme element representing a non-visual / non-printing character (as component of a glyph). 
 * 
 * @author Christian Clausner
 *
 */
public class NonPrintingCharacter extends GraphemeElementImpl implements GraphemeElement {

	protected NonPrintingCharacter(IdRegister idRegister, Id id, VariableMap attributes, Glyph parent,
			AttributeFactory attrFactory) {
		super(idRegister, id, attributes, parent, attrFactory);
	}

	@Override
	public ContentType getType() {
		return LowLevelTextType.NonPrintingCharacter;
	}

}
