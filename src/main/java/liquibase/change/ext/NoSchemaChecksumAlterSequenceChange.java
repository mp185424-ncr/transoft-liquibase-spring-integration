package liquibase.change.ext;

import liquibase.change.ChangeMetaData;
import liquibase.change.CheckSum;
import liquibase.change.DatabaseChange;
import liquibase.change.core.AlterSequenceChange;
import liquibase.serializer.core.string.StringChangeLogSerializer;

import com.transoftinc.vlm.liquibase.SchemaExcludingChecksumGenerator;
import com.transoftinc.vlm.liquibase.SchemaExcludingFieldFilter;

/**
 * Modifies properties of an existing sequence. StartValue is not allowed since
 * we cannot alter the starting sequence number
 */
@DatabaseChange(name = "alterSequence", description = "Alter properties of an existing sequence", priority = ChangeMetaData.PRIORITY_DEFAULT + 10, appliesTo = "sequence")
public class NoSchemaChecksumAlterSequenceChange extends AlterSequenceChange {

	@Override
	public CheckSum generateCheckSum() {
		return SchemaExcludingChecksumGenerator.generate(this);
	}

}
