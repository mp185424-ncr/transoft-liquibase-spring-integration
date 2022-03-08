package liquibase.change.ext;

import liquibase.change.ChangeMetaData;
import liquibase.change.CheckSum;
import liquibase.change.DatabaseChange;
import liquibase.change.core.MergeColumnChange;
import liquibase.serializer.core.string.StringChangeLogSerializer;

import com.transoftinc.vlm.liquibase.SchemaExcludingChecksumGenerator;
import com.transoftinc.vlm.liquibase.SchemaExcludingFieldFilter;

/**
 * Combines data from two existing columns into a new column and drops the
 * original columns.
 */
@DatabaseChange(name = "mergeColumns", description = "Concatenates the values in two columns, joins them by with string, and stores the resulting value in a new column.", priority = ChangeMetaData.PRIORITY_DEFAULT + 10)
public class NoSchemaChecksumMergeColumnChange extends MergeColumnChange {

	@Override
	public CheckSum generateCheckSum() {
		return SchemaExcludingChecksumGenerator.generate(this);
	}

}
