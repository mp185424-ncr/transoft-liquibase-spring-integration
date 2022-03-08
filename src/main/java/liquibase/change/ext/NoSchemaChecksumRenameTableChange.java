package liquibase.change.ext;

import liquibase.change.ChangeMetaData;
import liquibase.change.CheckSum;
import liquibase.change.DatabaseChange;
import liquibase.change.core.RenameTableChange;
import liquibase.serializer.core.string.StringChangeLogSerializer;

import com.transoftinc.vlm.liquibase.SchemaExcludingChecksumGenerator;
import com.transoftinc.vlm.liquibase.SchemaExcludingFieldFilter;

/**
 * Renames an existing table.
 */
@DatabaseChange(name = "renameTable", description = "Renames an existing table", priority = ChangeMetaData.PRIORITY_DEFAULT + 10, appliesTo = "table")
public class NoSchemaChecksumRenameTableChange extends RenameTableChange {

	@Override
	public CheckSum generateCheckSum() {
		return SchemaExcludingChecksumGenerator.generate(this);
	}

}
