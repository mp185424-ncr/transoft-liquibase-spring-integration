package liquibase.change.ext;

import liquibase.change.ChangeMetaData;
import liquibase.change.CheckSum;
import liquibase.change.DatabaseChange;
import liquibase.change.core.RenameSequenceChange;
import liquibase.serializer.core.string.StringChangeLogSerializer;

import com.transoftinc.vlm.liquibase.SchemaExcludingChecksumGenerator;
import com.transoftinc.vlm.liquibase.SchemaExcludingFieldFilter;

/**
 * Renames an existing table.
 */
@DatabaseChange(name = "renameSequence", description = "Renames an existing sequence", priority = ChangeMetaData.PRIORITY_DEFAULT + 10, appliesTo = "sequence")
public class NoSchemaChecksumRenameSequenceChange extends RenameSequenceChange {

	@Override
	public CheckSum generateCheckSum() {
		return SchemaExcludingChecksumGenerator.generate(this);
	}

}
