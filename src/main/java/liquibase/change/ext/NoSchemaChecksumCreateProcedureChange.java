package liquibase.change.ext;

import liquibase.change.ChangeMetaData;
import liquibase.change.CheckSum;
import liquibase.change.DatabaseChange;
import liquibase.change.core.CreateProcedureChange;
import liquibase.serializer.core.string.StringChangeLogSerializer;

import com.transoftinc.vlm.liquibase.SchemaExcludingChecksumGenerator;
import com.transoftinc.vlm.liquibase.SchemaExcludingFieldFilter;

@DatabaseChange(
	name = "createProcedure",
	description = "Defines the definition for a stored procedure. This command is better to use for creating procedures than the raw sql command because it will not attempt to strip comments or break up lines.\n\nOften times it is best to use the CREATE OR REPLACE syntax along with setting runOnChange='true' on the enclosing changeSet tag. That way if you need to make a change to your procedure you can simply change your existing code rather than creating a new REPLACE PROCEDURE call. The advantage to this approach is that it keeps your change log smaller and allows you to more easily see what has changed in your procedure code through your source control system's diff command.",
	priority = ChangeMetaData.PRIORITY_DEFAULT + 10)
public class NoSchemaChecksumCreateProcedureChange extends CreateProcedureChange {

	@Override
	public CheckSum generateCheckSum() {
		return SchemaExcludingChecksumGenerator.generate(this);
	}

}
