package liquibase.change.ext;

import liquibase.change.ChangeMetaData;
import liquibase.change.CheckSum;
import liquibase.change.DatabaseChange;
import liquibase.change.core.DropUniqueConstraintChange;
import liquibase.serializer.core.string.StringChangeLogSerializer;

import com.transoftinc.vlm.liquibase.SchemaExcludingChecksumGenerator;
import com.transoftinc.vlm.liquibase.SchemaExcludingFieldFilter;

/**
 * Removes an existing unique constraint.
 */
@DatabaseChange(name = "dropUniqueConstraint", description = "Drops an existing unique constraint", priority = ChangeMetaData.PRIORITY_DEFAULT + 10, appliesTo = "uniqueConstraint")
public class NoSchemaChecksumDropUniqueConstraintChange extends DropUniqueConstraintChange {

	@Override
	public CheckSum generateCheckSum() {
		return SchemaExcludingChecksumGenerator.generate(this);
	}

}
