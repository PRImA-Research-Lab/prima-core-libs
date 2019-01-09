package org.primaresearch.dla.page;

/**
 * Alternative document page image (e.g. black-and-white or grey level)
 * 
 * @author Christian Clausner
 *
 */
public final class AlternativeImage {
	private String filename;
	private String comments;
	private Double confidence;
	
	public AlternativeImage(String filename) {
		this.filename = filename;
	}
	
	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	public String getComments() {
		return comments;
	}
	public void setComments(String comments) {
		this.comments = comments;
	}

	/**
	 * Image confidence (e.g. for binarised image)
	 * @return 0.0..1.0
	 */
	public Double getConfidence() {
		return confidence;
	}

	/**
	 * Image confidence (e.g. for binarised image)
	 * @param confidence 0.0..1.0
	 */
	public void setConfidence(Double confidence) {
		this.confidence = confidence;
	}
	
}