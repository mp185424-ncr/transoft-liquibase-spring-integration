package liquibase.change.ext;

import liquibase.change.ChangeMetaData;
import liquibase.change.CheckSum;
import liquibase.change.DatabaseChange;
import liquibase.change.core.DropViewChange;
import liquibase.serializer.core.string.StringChangeLogSerializer;

import com.transoftinc.vlm.liquibase.SchemaExcludingChecksumGenerator;
import com.transoftinc.vlm.liquibase.SchemaExcludingFieldFilter;

/**
 * Drops an existing view.
 */
@DatabaseChange(name = "dropView", description = "Drops an existing view", priority = ChangeMetaData.PRIORITY_DEFAULT + 10, appliesTo = "view")
public class NoSchemaChecksumDropViewChange extends DropViewChange {

	@Override
	public CheckSum generateCheckSum() {
		return SchemaExcludingChecksumGenerator.generate(this);
	}

}
