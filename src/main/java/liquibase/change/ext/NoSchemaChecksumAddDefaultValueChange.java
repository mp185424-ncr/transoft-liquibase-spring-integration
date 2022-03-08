package liquibase.change.ext;

import liquibase.change.ChangeMetaData;
import liquibase.change.CheckSum;
import liquibase.change.DatabaseChange;
import liquibase.change.core.AddDefaultValueChange;
import liquibase.serializer.core.string.StringChangeLogSerializer;

import com.transoftinc.vlm.liquibase.SchemaExcludingChecksumGenerator;
import com.transoftinc.vlm.liquibase.SchemaExcludingFieldFilter;

/**
 * Sets a new default value to an existing column.
 */
@DatabaseChange(name = "addDefaultValue", description = "Adds a default value to the database definition for the specified column.\n" + "One of defaultValue, defaultValueNumeric, defaultValueBoolean or defaultValueDate must be set",
	priority = ChangeMetaData.PRIORITY_DEFAULT + 10, appliesTo = "column")
public class NoSchemaChecksumAddDefaultValueChange extends AddDefaultValueChange {

	@Override
	public CheckSum generateCheckSum() {
		return SchemaExcludingChecksumGenerator.generate(this);
	}

}
