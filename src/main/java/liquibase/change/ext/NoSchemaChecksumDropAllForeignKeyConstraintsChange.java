package liquibase.change.ext;

import liquibase.change.ChangeMetaData;
import liquibase.change.CheckSum;
import liquibase.change.DatabaseChange;
import liquibase.change.core.DropAllForeignKeyConstraintsChange;
import liquibase.serializer.core.string.StringChangeLogSerializer;

import com.transoftinc.vlm.liquibase.SchemaExcludingChecksumGenerator;
import com.transoftinc.vlm.liquibase.SchemaExcludingFieldFilter;

@DatabaseChange(name = "dropAllForeignKeyConstraints", description = "Drops all foreign key constraints for a table", priority = ChangeMetaData.PRIORITY_DEFAULT + 10, appliesTo = "table")
public class NoSchemaChecksumDropAllForeignKeyConstraintsChange extends DropAllForeignKeyConstraintsChange {

	@Override
	public CheckSum generateCheckSum() {
		return SchemaExcludingChecksumGenerator.generate(this);
	}

}
