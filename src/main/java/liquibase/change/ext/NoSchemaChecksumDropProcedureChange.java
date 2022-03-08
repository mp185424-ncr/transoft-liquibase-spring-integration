package liquibase.change.ext;

import liquibase.change.ChangeMetaData;
import liquibase.change.CheckSum;
import liquibase.change.DatabaseChange;
import liquibase.change.core.DropProcedureChange;
import liquibase.serializer.core.string.StringChangeLogSerializer;

import com.transoftinc.vlm.liquibase.SchemaExcludingChecksumGenerator;
import com.transoftinc.vlm.liquibase.SchemaExcludingFieldFilter;

@DatabaseChange(name = "dropProcedure", description = "Drops an existing procedure", priority = ChangeMetaData.PRIORITY_DEFAULT + 110, appliesTo = "storedProcedure")
public class NoSchemaChecksumDropProcedureChange extends DropProcedureChange {

	@Override
	public CheckSum generateCheckSum() {
		return SchemaExcludingChecksumGenerator.generate(this);
	}

}
