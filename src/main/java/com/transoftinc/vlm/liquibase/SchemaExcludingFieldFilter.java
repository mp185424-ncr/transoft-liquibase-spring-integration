package com.transoftinc.vlm.liquibase;

import liquibase.serializer.core.string.StringChangeLogSerializer.FieldFilter;

/**
 * Field filter which excludes any field containing words 'tablespace' or
 * 'schema'. It is used by StringChangeLogSerializer when generating checksums
 * for Changes.
 * 
 * @author ar250203
 */
public class SchemaExcludingFieldFilter extends FieldFilter {

	private static SchemaExcludingFieldFilter instance;

	public static SchemaExcludingFieldFilter getInstance() {
		if (instance == null) {
			instance = new SchemaExcludingFieldFilter();
		}

		return instance;
	}

	@Override
	public boolean include(Object obj, String field, Object value) {
		if (field.toLowerCase().contains("tablespace")) {
			return false;
		}

		if (field.toLowerCase().contains("schema")) {
			return false;
		}

		return true;
	}

}
