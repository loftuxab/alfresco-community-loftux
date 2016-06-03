package org.alfresco.repo.dictionary;

/**
 * How tokenisation is supported in the index.
 * 
 * 
 * @author andyh
 * 
 */
public enum IndexTokenisationMode {
	/**
	 * Tokenise the property. If the analyser supported ordering then the field
	 * supports ordering FTS is supported via analysis.
	 */
	TRUE,
	/**
	 * Do not tokenise the property. The field supports ordering and pattern
	 * matching.
	 */
	FALSE,
	/**
	 * There may be two indexes - one to support ordering and one to support
	 * search.
	 */
	BOTH;

	public static String serializer(IndexTokenisationMode indexTokenisationMode) {
		return indexTokenisationMode.toString();
	}

	public static IndexTokenisationMode deserializer(String value) {
		if (value == null) {
			return null;
		} else if (value.equalsIgnoreCase(TRUE.toString())) {
			return TRUE;
		} else if (value.equalsIgnoreCase(FALSE.toString())) {
			return FALSE;
		} else if (value.equalsIgnoreCase(BOTH.toString())) {
			return BOTH;
		} else {
			throw new IllegalArgumentException(
					"Invalid IndexTokenisationMode: " + value);
		}
	}
}
