package liquibase.change.ext;

import liquibase.change.ChangeMetaData;
import liquibase.change.CheckSum;
import liquibase.change.DatabaseChange;
import liquibase.change.core.AddNotNullConstraintChange;
import liquibase.serializer.core.string.StringChangeLogSerializer;

import com.transoftinc.vlm.liquibase.SchemaExcludingChecksumGenerator;
import com.transoftinc.vlm.liquibase.SchemaExcludingFieldFilter;

/**
 * Adds a not-null constraint to an existing column.
 */
@DatabaseChange(name = "addNotNullConstraint",
	description = "Adds a not-null constraint to an existing table. If a defaultNullValue attribute is passed, all null values for the column will be updated to the passed value before the constraint is applied.",
	priority = ChangeMetaData.PRIORITY_DEFAULT + 10, appliesTo = "column")
public class NoSchemaChecksumAddNotNullConstraintChange extends AddNotNullConstraintChange {

	@Override
	public CheckSum generateCheckSum() {
		return SchemaExcludingChecksumGenerator.generate(this);
	}

}