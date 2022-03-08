package liquibase.change.ext;

import liquibase.change.ChangeMetaData;
import liquibase.change.CheckSum;
import liquibase.change.DatabaseChange;
import liquibase.change.core.DropSequenceChange;
import liquibase.serializer.core.string.StringChangeLogSerializer;

import com.transoftinc.vlm.liquibase.SchemaExcludingChecksumGenerator;
import com.transoftinc.vlm.liquibase.SchemaExcludingFieldFilter;

/**
 * Drops an existing sequence.
 */
@DatabaseChange(name = "dropSequence", description = "Drop an existing sequence", priority = ChangeMetaData.PRIORITY_DEFAULT + 10, appliesTo = "sequence")
public class NoSchemaChecksumDropSequenceChange extends DropSequenceChange {

	@Override
	public CheckSum generateCheckSum() {
		return SchemaExcludingChecksumGenerator.generate(this);
	}

}
