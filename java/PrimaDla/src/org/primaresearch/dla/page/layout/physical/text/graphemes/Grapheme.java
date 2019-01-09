package org.primaresearch.dla.page.layout.physical.text.graphemes;

import org.primaresearch.dla.page.layout.physical.AttributeFactory;
import org.primaresearch.dla.page.layout.physical.ContentObject;
import org.primaresearch.dla.page.layout.physical.shared.ContentType;
import org.primaresearch.dla.page.layout.physical.shared.LowLevelTextType;
import org.primaresearch.dla.page.layout.physical.text.impl.Glyph;
import org.primaresearch.ident.Id;
import org.primaresearch.ident.IdRegister;
import org.primaresearch.labels.Labels;
import org.primaresearch.maths.geometry.Polygon;
import org.primaresearch.shared.variable.VariableMap;

public class Grapheme extends GraphemeElementImpl implements GraphemeElement, ContentObject {

	private Polygon coords;
	private Labels labels;

	/**
	 * Constructor
	 * @param idRegister
	 * @param id
	 * @param coords
	 * @param attributes
	 * @param parent
	 * @param attrFactory
	 */
	protected Grapheme(IdRegister idRegister, Id id, Polygon coords, VariableMap attributes,
			Glyph parent, AttributeFactory attrFactory) {
		super(idRegister, id, attributes, parent, attrFactory);
	}

	@Override
	public Polygon getCoords() {
		return coords;
	}

	@Override
	public void setCoords(Polygon coords) {
		this.coords = coords;
	}

	@Override
	public ContentType getType() {
		return LowLevelTextType.Grapheme;
	}

	@Override
	public boolean isTemporary() {
		return false;
	}
	
	@Override
	public Labels getLabels() {
		return labels;
	}

	@Override
	public void setLabels(Labels labels) {
		this.labels = labels;		
	}

}
