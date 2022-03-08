package liquibase.change.ext;

import liquibase.change.ChangeMetaData;
import liquibase.change.CheckSum;
import liquibase.change.DatabaseChange;
import liquibase.change.core.CreateSequenceChange;
import liquibase.serializer.core.string.StringChangeLogSerializer;

import com.transoftinc.vlm.liquibase.SchemaExcludingChecksumGenerator;
import com.transoftinc.vlm.liquibase.SchemaExcludingFieldFilter;

/**
 * Creates a new sequence.
 */
@DatabaseChange(name = "createSequence", description = "Creates a new database sequence", priority = ChangeMetaData.PRIORITY_DEFAULT + 10)
public class NoSchemaChecksumCreateSequenceChange extends CreateSequenceChange {

	@Override
	public CheckSum generateCheckSum() {
		return SchemaExcludingChecksumGenerator.generate(this);
	}

}
