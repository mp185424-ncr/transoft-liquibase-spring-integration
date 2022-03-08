package liquibase.change.ext;

import liquibase.change.ChangeMetaData;
import liquibase.change.CheckSum;
import liquibase.change.DatabaseChange;
import liquibase.change.core.AddColumnChange;
import liquibase.serializer.core.string.StringChangeLogSerializer;

import com.transoftinc.vlm.liquibase.SchemaExcludingChecksumGenerator;
import com.transoftinc.vlm.liquibase.SchemaExcludingFieldFilter;

/**
 * Adds a column to an existing table.
 */
@DatabaseChange(name = "addColumn", description = "Adds a new column to an existing table", priority = ChangeMetaData.PRIORITY_DEFAULT + 10, appliesTo = "table")
public class NoSchemaChecksumAddColumnChange extends AddColumnChange {

	@Override
	public CheckSum generateCheckSum() {
		return SchemaExcludingChecksumGenerator.generate(this);
	}

}
