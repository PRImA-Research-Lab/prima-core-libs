/*
 * Copyright 2015 PRImA Research Lab, University of Salford, United Kingdom
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.primaresearch.dla.page.layout.physical.text.impl;

/**
 * Collection of text region sub-types in compliance with the PAGE XML schema (version 2013-07-15).
 * 
 * @author Christian Clausner
 *
 */
public interface DefaultTextRegionTypes {
	public static final String PARAGRAPH  	= "paragraph";
	public static final String HEADING 		= "heading";
	public static final String CAPTION  	= "caption";
	public static final String HEADER  		= "header";
	public static final String FOOTER  		= "footer";
	public static final String PAGE_NUMBER  = "page-number";
	public static final String DROP_CAPITAL = "drop-capital";
	public static final String CREDIT  		= "credit";
	public static final String FLOATING  	= "floating";
	public static final String SIGNATURE_MARK  		= "signature-mark";
	public static final String CATCH_WORD  			= "catch-word";
	public static final String MARGINALIA  			= "marginalia";
	public static final String FOOTNOTE  			= "footnote";
	public static final String FOOTNOTE_CONTINUED  	= "footnote-continued";
	public static final String TOC_ENTRY  			= "TOC-entry";
	public static final String ENDNOTE  			= "endnote";
	public static final String OTHER 	 			= "other";
}