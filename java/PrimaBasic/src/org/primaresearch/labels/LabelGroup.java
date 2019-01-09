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
import java.util.LinkedList;
import java.util.List;

public class LabelGroup implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private String externalModel;
	private String externalId;
	private String prefix;
	private String comments;
	
	private List<Label> labels = new LinkedList<Label>();
	
	public LabelGroup() {		
	}
	
	public LabelGroup(String externalModel) {
		this.externalModel = externalModel;
	}

	public String getExternalModel() {
		return externalModel;
	}

	public void setExternalModel(String externalModel) {
		this.externalModel = externalModel;
	}
	
	public void addLabel(Label label) {
		labels.add(label);
	}

	public String getExternalId() {
		return externalId;
	}

	public void setExternalId(String externalId) {
		this.externalId = externalId;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public List<Label> getLabels() {
		return labels;
	}

	
}
