package liquibase.change.ext;

import liquibase.change.ChangeMetaData;
import liquibase.change.CheckSum;
import liquibase.change.DatabaseChange;
import liquibase.change.core.DropIndexChange;
import liquibase.serializer.core.string.StringChangeLogSerializer;

import com.transoftinc.vlm.liquibase.SchemaExcludingChecksumGenerator;
import com.transoftinc.vlm.liquibase.SchemaExcludingFieldFilter;

/**
 * Drops an existing index.
 */
@DatabaseChange(name = "dropIndex", description = "Drops an existing index", priority = ChangeMetaData.PRIORITY_DEFAULT + 10, appliesTo = "index")
public class NoSchemaChecksumDropIndexChange extends DropIndexChange {

	@Override
	public CheckSum generateCheckSum() {
		return SchemaExcludingChecksumGenerator.generate(this);
	}

}
