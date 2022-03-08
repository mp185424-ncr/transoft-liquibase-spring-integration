package liquibase.change.ext;

import liquibase.change.ChangeMetaData;
import liquibase.change.CheckSum;
import liquibase.change.DatabaseChange;
import liquibase.change.DatabaseChangeNote;
import liquibase.change.core.AddAutoIncrementChange;

import com.transoftinc.vlm.liquibase.SchemaExcludingChecksumGenerator;

@DatabaseChange(name = "addAutoIncrement", description = "Converts an existing column to be an auto-increment (a.k.a 'identity') column", priority = ChangeMetaData.PRIORITY_DEFAULT + 10, appliesTo = "column",
	databaseNotes = { @DatabaseChangeNote(database = "sqlite", notes = "If the column type is not INTEGER it is converted to INTEGER") })
public class NoSchemaChecksumAddAutoIncrementChange extends AddAutoIncrementChange {

	@Override
	public CheckSum generateCheckSum() {
		return SchemaExcludingChecksumGenerator.generate(this);
	}

}
