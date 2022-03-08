package liquibase.change.ext;

import liquibase.change.ChangeMetaData;
import liquibase.change.CheckSum;
import liquibase.change.DatabaseChange;
import liquibase.change.core.LoadDataChange;
import liquibase.serializer.core.string.StringChangeLogSerializer;

import com.transoftinc.vlm.liquibase.SchemaExcludingChecksumGenerator;
import com.transoftinc.vlm.liquibase.SchemaExcludingFieldFilter;

@DatabaseChange(
	name = "loadData",
	description = "Loads data from a CSV file into an existing table. A value of NULL in a cell will be converted to a database NULL rather than the string 'NULL'\n"
		+ "\n"
		+ "Date/Time values included in the CSV file should be in ISO formathttp://en.wikipedia.org/wiki/ISO_8601 in order to be parsed correctly by Liquibase. Liquibase will initially set the date format to be 'yyyy-MM-dd'T'HH:mm:ss' and then it checks for two special cases which will override the data format string.\n"
		+ "\n"
		+ "If the string representing the date/time includes a '.', then the date format is changed to 'yyyy-MM-dd'T'HH:mm:ss.SSS'\n"
		+ "If the string representing the date/time includes a space, then the date format is changed to 'yyyy-MM-dd HH:mm:ss'\n"
		+ "Once the date format string is set, Liquibase will then call the SimpleDateFormat.parse() method attempting to parse the input string so that it can return a Date/Time. If problems occur, then a ParseException is thrown and the input string is treated as a String for the INSERT command to be generated.",
	priority = ChangeMetaData.PRIORITY_DEFAULT + 10, appliesTo = "table", since = "1.7")
public class NoSchemaChecksumLoadDataChange extends LoadDataChange {

	@Override
	public CheckSum generateCheckSum() {
		return SchemaExcludingChecksumGenerator.generate(this);
	}

}
