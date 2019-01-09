/*
 * Copyright 2019 PRImA Research Lab, University of Salford, United Kingdom
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
package org.primaresearch.labels;

import java.io.Serializable;

public class LabelImpl implements Label, Serializable {
	
	private static final long serialVersionUID = 1L;
	private String value;
	private String type;
	private String comments;
	private String externalModel;
	
	public LabelImpl() {
	}
	
	public LabelImpl(String value, String externalModel) {
		this.value = value;
		this.externalModel = externalModel;
	}

	@Override
	public String getValue() {
		return value;
	}

	@Override
	public String getType() {
		return type;
	}

	@Override
	public String getComments() {
		return comments;
	}

	@Override
	public void setValue(String value) {
		this.value = value;		
	}

	@Override
	public void setType(String type) {
		this.type = type;
	}

	@Override
	public void setComments(String comments) {
		this.comments = comments;		
	}

	@Override
	public String getExternalModel() {
		return externalModel;
	}

	@Override
	public void setExternalModel(String externalModel) {
		this.externalModel = externalModel;		
	}

}
