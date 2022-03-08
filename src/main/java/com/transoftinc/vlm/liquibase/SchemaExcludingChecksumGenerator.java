package com.transoftinc.vlm.liquibase;

import liquibase.change.CheckSum;
import liquibase.serializer.LiquibaseSerializable;
import liquibase.serializer.core.string.StringChangeLogSerializer;
import liquibase.serializer.core.string.StringChangeLogSerializer.FieldFilter;

public class SchemaExcludingChecksumGenerator {

	public static CheckSum generate(LiquibaseSerializable object) {
		FieldFilter fieldFilter = SchemaExcludingFieldFilter.getInstance();
		String valueToChecksum = new StringChangeLogSerializer(fieldFilter).serialize(object, false);
		//System.out.println(valueToChecksum);
		return CheckSum.compute(valueToChecksum);
	}

}
