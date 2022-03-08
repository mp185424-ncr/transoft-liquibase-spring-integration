package liquibase.change.ext;

import liquibase.change.ChangeMetaData;
import liquibase.change.CheckSum;
import liquibase.change.DatabaseChange;
import liquibase.change.core.DropNotNullConstraintChange;
import liquibase.serializer.core.string.StringChangeLogSerializer;

import com.transoftinc.vlm.liquibase.SchemaExcludingChecksumGenerator;
import com.transoftinc.vlm.liquibase.SchemaExcludingFieldFilter;

/**
 * Drops a not-null constraint from an existing column.
 */
@DatabaseChange(name = "dropNotNullConstraint", description = "Makes a column nullable", priority = ChangeMetaData.PRIORITY_DEFAULT + 10, appliesTo = "column")
public class NoSchemaChecksumDropNotNullConstraintChange extends DropNotNullConstraintChange {

	@Override
	public CheckSum generateCheckSum() {
		return SchemaExcludingChecksumGenerator.generate(this);
	}

}
