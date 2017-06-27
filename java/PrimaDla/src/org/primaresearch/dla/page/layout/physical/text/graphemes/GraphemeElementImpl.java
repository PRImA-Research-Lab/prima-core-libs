package org.primaresearch.dla.page.layout.physical.text.graphemes;

import org.primaresearch.dla.page.io.xml.DefaultXmlNames;
import org.primaresearch.dla.page.layout.physical.AttributeFactory;
import org.primaresearch.dla.page.layout.physical.text.TextContent;
import org.primaresearch.dla.page.layout.physical.text.impl.Glyph;
import org.primaresearch.dla.page.layout.physical.text.impl.TextContentVariants;
import org.primaresearch.ident.Id;
import org.primaresearch.ident.IdRegister;
import org.primaresearch.ident.IdRegister.InvalidIdException;
import org.primaresearch.shared.variable.IntegerValue;
import org.primaresearch.shared.variable.StringValue;
import org.primaresearch.shared.variable.VariableMap;
import org.primaresearch.shared.variable.VariableValue;
import org.primaresearch.shared.variable.Variable.WrongVariableTypeException;

public abstract class GraphemeElementImpl implements GraphemeElement {

	private Id id;
	private IdRegister idRegister;

	private VariableMap attributes;
	
	private Glyph parent;
	
	private TextContentVariants textContentVariants;

	/**
	 * Constructor
	 * @param idRegister
	 * @param id
	 * @param coords
	 * @param attributes
	 * @param parent
	 * @param attrFactory
	 */
	protected GraphemeElementImpl(IdRegister idRegister, Id id, 
								VariableMap attributes, Glyph parent,
								AttributeFactory attrFactory) {
		this.idRegister = idRegister;
		this.id = id;
		this.attributes = attributes;
		this.parent = parent;
		textContentVariants = new TextContentVariants(attrFactory);
	}
	
	@Override
	public Id getId() {
		return id;
	}
	
	@Override
	public IdRegister getIdRegister() {
		return idRegister;
	}
	
	@Override
	public void setId(String id) throws InvalidIdException {
		this.id = idRegister.registerId(id, this.id);
	}
	
	@Override
	public void setId(Id id) throws InvalidIdException {
		idRegister.registerId(id, this.id);
		this.id = id;
	}

	/**
	 * Returns the parent glyph this grapheme element belongs to
	 * @return A glyph object (should not be null)
	 */
	public Glyph getParent() {
		return parent;
	}

	/**
	 * Sets the parent glyph this grapheme element belongs to
	 * @param parent A glyph object (should not be null)
	 */
	public void setParent(Glyph parent) {
		this.parent = parent;
	}
	
	@Override
	public String getCharacterType() {
		return ((StringValue)getAttributes().get(DefaultXmlNames.ATTR_charType).getValue()).val;
	}

	@Override
	public void setCharacterType(String type) {
		try {
			getAttributes().get(DefaultXmlNames.ATTR_charType).setValue(VariableValue.createValueObject(type));
		} catch (WrongVariableTypeException e) {
			e.printStackTrace();
		}
	}

	@Override
	public int getSortIndex() {
		return ((IntegerValue)getAttributes().get(DefaultXmlNames.ATTR_index).getValue()).val;
	}

	@Override
	public void setSortIndex(int index) {
		try {
			getAttributes().get(DefaultXmlNames.ATTR_index).setValue(VariableValue.createValueObject(index));
		} catch (WrongVariableTypeException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String getText() {
		return textContentVariants.getText();
	}

	@Override
	public String getPlainText() {
		return textContentVariants.getPlainText();
	}
	
	@Override
	public void setText(String text) {
		textContentVariants.setText(text);
	}
	
	@Override
	public void setPlainText(String text) {
		textContentVariants.setPlainText(text);
	}
	
	@Override
	public String getComments() {
		return textContentVariants.getComments();
	}

	@Override
	public void setComments(String comments) {
		textContentVariants.setComments(comments);
	}

	@Override
	public String getDataType() {
		return textContentVariants.getDataType();
	}

	@Override
	public void setDataType(String datatype) {
		textContentVariants.setDataType(datatype);
	}

	@Override
	public String getDataTypeDetails() {
		return textContentVariants.getDataTypeDetails();
	}

	@Override
	public void setDataTypeDetails(String details) {
		textContentVariants.setDataTypeDetails(details);
	}
	
	/*@Override
	public String getMergeWithNextRule() {
		return textContentVariants.getMergeWithNextRule();
	}

	@Override
	public void setMergeWithNextRule(String rule) {
		textContentVariants.setMergeWithNextRule(rule);		
	}

	@Override
	public String getMergeWithNextRuleData() {
		return textContentVariants.getMergeWithNextRuleData();
	}

	@Override
	public void setMergeWithNextRuleData(String data) {
		textContentVariants.setMergeWithNextRuleData(data);		
	}*/

	@Override
	public int getTextContentVariantCount() {
		return textContentVariants.getTextContentVariantCount();
	}

	@Override
	public TextContent getTextContentVariant(int index) {
		return textContentVariants.getTextContentVariant(index);
	}

	@Override
	public TextContent addTextContentVariant() {
		return textContentVariants.addTextContentVariant();
	}

	@Override
	public void reomveTextContentVariant(int index) {
		textContentVariants.reomveTextContentVariant(index);
	}
	
	@Override
	public Double getConfidence() {
		return textContentVariants.getConfidence();
	}

	@Override
	public void setConfidence(Double confidence) {
		textContentVariants.setConfidence(confidence);
	}
	
	@Override
	public VariableMap getAttributes() {
		return attributes;
	}

}
