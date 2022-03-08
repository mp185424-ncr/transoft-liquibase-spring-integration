package liquibase.change.ext;

import liquibase.change.ChangeMetaData;
import liquibase.change.CheckSum;
import liquibase.change.DatabaseChange;
import liquibase.change.core.AddForeignKeyConstraintChange;
import liquibase.serializer.core.string.StringChangeLogSerializer;

import com.transoftinc.vlm.liquibase.SchemaExcludingChecksumGenerator;
import com.transoftinc.vlm.liquibase.SchemaExcludingFieldFilter;

/**
 * Adds a foreign key constraint to an existing column.
 */
@DatabaseChange(name = "addForeignKeyConstraint", description = "Adds a foreign key constraint to an existing column", priority = ChangeMetaData.PRIORITY_DEFAULT + 10, appliesTo = "column")
public class NoSchemaChecksumAddForeignKeyConstraintChange extends AddForeignKeyConstraintChange {

	@Override
	public CheckSum generateCheckSum() {
		return SchemaExcludingChecksumGenerator.generate(this);
	}

}
