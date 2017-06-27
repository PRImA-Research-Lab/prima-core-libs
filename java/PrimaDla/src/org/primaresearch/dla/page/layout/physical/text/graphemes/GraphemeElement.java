package org.primaresearch.dla.page.layout.physical.text.graphemes;

import org.primaresearch.dla.page.layout.physical.shared.ContentType;
import org.primaresearch.dla.page.layout.physical.text.TextContentVariants;
import org.primaresearch.dla.page.layout.physical.text.impl.Glyph;
import org.primaresearch.ident.Identifiable;

/**
 * Interface for Graphemes, GraphemeGroups and NonPrintingCharacters
 * 
 * @author Christian Clausner
 *
 */
public interface GraphemeElement extends Identifiable, TextContentVariants {
	
	/**
	 * Returns the parent glyph this grapheme element belongs to
	 * @return A glyph object (should not be null)
	 */
	public Glyph getParent();

	/**
	 * Sets the parent glyph this grapheme element belongs to
	 * @param parent A glyph object (should not be null)
	 */
	public void setParent(Glyph parent);
	
	/**
	 * Type of this grapheme element (as a glyph component)
	 * @return 'base' or 'combining'
	 */
	public String getCharacterType();
	
	/**
	 * Type of this grapheme element (as a glyph component)
	 * @param type 'base' or 'combining'
	 */
	public void setCharacterType(String type);
	
	/**
	 * The position of this grapheme element within the glyph grapheme list or the grapheme group (if member of a group)
	 * @return Sort index >= 0
	 */
	public int getSortIndex();
	
	/**
	 * The position of this grapheme element within the glyph grapheme list or the grapheme group (if member of a group)
	 * @param index Sort index >= 0
	 */	
	public void setSortIndex(int index);
	
	/**
	 * The type of the grapheme element (grapheme, grapheme group, or non-printing character)
	 * @return
	 */
	public ContentType getType();
}
