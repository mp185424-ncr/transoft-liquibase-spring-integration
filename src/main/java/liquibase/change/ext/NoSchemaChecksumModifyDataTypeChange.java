package liquibase.change.ext;

import liquibase.change.ChangeMetaData;
import liquibase.change.CheckSum;
import liquibase.change.DatabaseChange;
import liquibase.change.core.ModifyDataTypeChange;
import liquibase.serializer.core.string.StringChangeLogSerializer;

import com.transoftinc.vlm.liquibase.SchemaExcludingChecksumGenerator;
import com.transoftinc.vlm.liquibase.SchemaExcludingFieldFilter;

@DatabaseChange(name = "modifyDataType", description = "Modify data type", priority = ChangeMetaData.PRIORITY_DEFAULT + 10, appliesTo = "column")
public class NoSchemaChecksumModifyDataTypeChange extends ModifyDataTypeChange {

	@Override
	public CheckSum generateCheckSum() {
		return SchemaExcludingChecksumGenerator.generate(this);
	}

}
