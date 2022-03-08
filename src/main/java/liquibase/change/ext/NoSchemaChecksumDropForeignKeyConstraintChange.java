package liquibase.change.ext;

import liquibase.change.ChangeMetaData;
import liquibase.change.CheckSum;
import liquibase.change.DatabaseChange;
import liquibase.change.core.DropForeignKeyConstraintChange;
import liquibase.serializer.core.string.StringChangeLogSerializer;

import com.transoftinc.vlm.liquibase.SchemaExcludingChecksumGenerator;
import com.transoftinc.vlm.liquibase.SchemaExcludingFieldFilter;

/**
 * Drops an existing foreign key constraint.
 */
@DatabaseChange(name = "dropForeignKeyConstraint", description = "Drops an existing foreign key", priority = ChangeMetaData.PRIORITY_DEFAULT + 10, appliesTo = "foreignKey")
public class NoSchemaChecksumDropForeignKeyConstraintChange extends DropForeignKeyConstraintChange {

	@Override
	public CheckSum generateCheckSum() {
		return SchemaExcludingChecksumGenerator.generate(this);
	}

}
