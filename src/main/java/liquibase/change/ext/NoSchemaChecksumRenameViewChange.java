package liquibase.change.ext;

import liquibase.change.ChangeMetaData;
import liquibase.change.CheckSum;
import liquibase.change.DatabaseChange;
import liquibase.change.core.RenameViewChange;
import liquibase.serializer.core.string.StringChangeLogSerializer;

import com.transoftinc.vlm.liquibase.SchemaExcludingChecksumGenerator;
import com.transoftinc.vlm.liquibase.SchemaExcludingFieldFilter;

/**
 * Renames an existing view.
 */
@DatabaseChange(name = "renameView", description = "Renames an existing view", priority = ChangeMetaData.PRIORITY_DEFAULT + 10, appliesTo = "view")
public class NoSchemaChecksumRenameViewChange extends RenameViewChange {

	@Override
	public CheckSum generateCheckSum() {
		return SchemaExcludingChecksumGenerator.generate(this);
	}

}
