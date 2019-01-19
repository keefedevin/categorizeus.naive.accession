package us.categorize.naive.accession;

public class Configuration {
	private boolean addDuplicateAttachments = true;
	private boolean addTags = true;

	public boolean isAddDuplicateAttachments() {
		return addDuplicateAttachments;
	}

	public void setAddDuplicateAttachments(boolean addDuplicateAttachments) {
		this.addDuplicateAttachments = addDuplicateAttachments;
	}

	public boolean isAddTags() {
		return addTags;
	}

	public void setAddTags(boolean addTags) {
		this.addTags = addTags;
	}
	
}
