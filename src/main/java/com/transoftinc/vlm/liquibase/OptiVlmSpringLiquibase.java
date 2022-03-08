package com.transoftinc.vlm.liquibase;

import java.io.StringWriter;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.StringTokenizer;

import liquibase.Contexts;
import liquibase.LabelExpression;
import liquibase.configuration.ConfigurationProperty;
import liquibase.configuration.GlobalConfiguration;
import liquibase.configuration.LiquibaseConfiguration;
import liquibase.database.Database;
import liquibase.exception.DatabaseException;
import liquibase.exception.LiquibaseException;
import liquibase.integration.spring.SpringLiquibase;
import liquibase.logging.LogFactory;
import liquibase.logging.Logger;

import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * Spring-Liquibase integration for OptiVLM applications. This is an improvement
 * of the standard integration provided by Liquibase. <br />
 * <br />
 * Allows storing upgrade queries in memory, ignoring checksum validation
 * failures, applying the database upgrade.
 * 
 * @author ar250203
 */
public class OptiVlmSpringLiquibase extends SpringLiquibase {

	private Logger logger = LogFactory.getInstance().getLog();

	/**
	 * Default count of lines of text than an empty upgrade file may have.
	 * (I.e., if we got this number of lines or less, we consider the file to be
	 * empty and no upgrade will be required).
	 */
	private final static int DEFAULT_EMPTY_FILE_LINE_COUNT = 16;

	/**
	 * Whether a database upgrade is required, or it's up to date
	 */
	protected boolean upgradeRequired = false;

	/**
	 * Upgrade queries that need to be run, if the upgrade is required
	 */
	protected String upgradeSql = "";

	/**
	 * Whether the database structure (checksums) is currently valid
	 */
	protected boolean valid = false;

	/**
	 * Custom schema name. If empty, we will assume it is the &lt;username&gt;
	 * of the connecting user.
	 */
	protected String customSchema = "";

	/**
	 * Custom data tablespace name. If empty, we will assume it is the
	 * &lt;username&gt;_DAT
	 */
	protected String customTablespaceData = "";

	/**
	 * Custom index tablespace name. If empty, we will assume it is the
	 * &lt;username&gt;_IDX
	 */
	protected String customTablespaceIndex = "";

	protected String schema = "";
	protected String tablespaceData = "";
	protected String tablespaceIndex = "";
	
	/**
	 * Check if the database requires an upgrade. The boolean result will be
	 * stored in 'upgradeRequired' property. If an upgrade is required, the
	 * upgrade SQL will be stored in 'upgradeSql' property.
	 * 
	 * @throws LiquibaseException
	 */
	public void checkUpgrade() throws LiquibaseException {
		// call overloaded method
		checkUpgrade(DEFAULT_EMPTY_FILE_LINE_COUNT);
	}

	/**
	 * Check if the database requires an upgrade. (See overloaded method for
	 * details.) Provide maximum number of lines that an SQL file may have, if
	 * there is no upgrade required. If the 'upgradeSql' contains more lines
	 * than this number, we will know that an upgrade is required.
	 * 
	 * @param maxLineCount
	 * @throws LiquibaseException
	 */
	public void checkUpgrade(int maxLineCount) throws LiquibaseException {
		// check for upgrade and generate SQL queries
		doCheckUpgrade();

		/*
		 * Even an empty upgrade file will have some lines of text in it. This
		 * method will check if the number of lines is over a certain number. If
		 * it is, we will know the upgrade file is not empty.
		 */
		determineUpgradeRequired(maxLineCount);
	}

	/**
	 * Run the SQL to perform database upgrade. First, this method will check if
	 * upgrade is required. Then it will run the upgrade queries and return an
	 * error message (if there was an error). If the execution is successful, a
	 * null will be returned.
	 * 
	 * @return
	 * @throws LiquibaseException
	 */
	public String applyUpgrade() throws LiquibaseException {
		// make sure an upgrade is required
		checkUpgrade();

		String result = null;

		if (isUpgradeRequired()) {
			Connection c = null;
			Statement stmt = null;

			try {
				c = getDataSource().getConnection();
				stmt = c.createStatement();

				String sql = getUpgradeSql();

				// remove all comments
				sql = sql.replaceAll("(?m)^--.*", "");

				for (String item : sql.split("\r\nGO\r\n\r\n|;")) {
					item = item.trim();
					if (item.length() > 0) {
						logger.debug("Executing SQL: " + item);

						try {
							stmt.execute(item);
						} catch (SQLException e) {
							// consume the exception, but stop execution
							result = e.getMessage().trim();
							logger.severe("SQL Error! " + result);
							logger.severe("Failing query: " + item);
							break;
						}
					}
				}
			} catch (SQLException e) {
				throw new DatabaseException(e);
			} finally {
				DbUtils.closeQuietly(stmt);
				DbUtils.closeQuietly(c);
			}
		}

		return result;
	}

	/**
	 * Executed automatically when this Spring bean is initialized.
	 */
	@Override
	public void afterPropertiesSet() throws LiquibaseException {
		checkUpgrade();
	}

	protected void doCheckUpgrade() throws LiquibaseException {
		ConfigurationProperty shouldRunProperty = LiquibaseConfiguration.getInstance().getProperty(GlobalConfiguration.class, GlobalConfiguration.SHOULD_RUN);

		if (!shouldRunProperty.getValue(Boolean.class)) {
			logger.info("Liquibase did not run because " + LiquibaseConfiguration.getInstance().describeValueLookupLogic(shouldRunProperty) + " was set to false");
			return;
		}

		if (!shouldRun) {
			logger.info("Liquibase did not run because 'shouldRun' " + "property was set to false on " + getBeanName() + " Liquibase Spring bean.");
			return;
		}

		Connection c = null;
		OptiVlmLiquibase liquibase = null;
		try {
			c = getDataSource().getConnection();
			liquibase = createLiquibase(c);
			performUpdate(liquibase);
		} catch (SQLException e) {
			throw new DatabaseException(e);
		} finally {
			Database database = null;
			if (liquibase != null) {
				database = liquibase.getDatabase();
			}
			if (database != null) {
				database.close();
			}
		}
	}

	/**
	 * Create Liquibase object
	 */
	@Override
	protected OptiVlmLiquibase createLiquibase(Connection c) throws LiquibaseException {
		String schemaName = determineSchema();
		
		// Database object
		Database db = createDatabase(c);
		
		// this sets the schema name to be used
		db.setLiquibaseSchemaName(schemaName);
		db.setDefaultSchemaName(schemaName);

		// create Liquibase object
		OptiVlmLiquibase liquibase = new OptiVlmLiquibase(getChangeLog(), createResourceOpener(), db);
		
		liquibase.setIgnoreClasspathPrefix(isIgnoreClasspathPrefix());
		
		if (parameters != null) {
			for (Map.Entry<String, String> entry : parameters.entrySet()) {
				liquibase.setChangeLogParameter(entry.getKey(), entry.getValue());
			}
		}

		// this pre-sets properties 'schema', 'tablespace.data', and 'tablespace.index' inside changeLog files
		liquibase.setChangeLogParameter("schema", schemaName);
		liquibase.setChangeLogParameter("tablespace.data", determineTablespaceData());
		liquibase.setChangeLogParameter("tablespace.index", determineTablespaceIndex());
		
		if (isDropFirst()) {
			liquibase.dropAll();
		}

		return liquibase;
	}

	/**
	 * Processes changelogs and generates SQL queries to be run. Stores the
	 * queries in the 'upgradeSql' property of this bean. DOES NOT run the SQL
	 * queries, despite what the name implies.
	 */
	protected void performUpdate(OptiVlmLiquibase liquibase) throws LiquibaseException {
		Contexts contextsObj = new Contexts(getContexts());
		LabelExpression labelExpression = new LabelExpression(getLabels());

		StringWriter output = new StringWriter();

		liquibase.updateIgnoreValidation(contextsObj, labelExpression, output);

		// store upgrade queries
		this.upgradeSql = output.toString();

		// retrieve current db status (result of checksums validation)
		this.valid = liquibase.isValid();
	}

	/**
	 * Sets the 'upgradeRequired' property to true, if the upgrade SQL contains
	 * more than specified number of lines.
	 * 
	 * @param maxLineCount
	 */
	protected void determineUpgradeRequired(int maxLineCount) {
		int lineCount = this.upgradeSql.split(System.getProperty("line.separator")).length;

		if (lineCount > maxLineCount) {
			this.upgradeRequired = true;
		} else {
			this.upgradeRequired = false;
		}
	}

	protected String determineSchema() throws DatabaseException {
		// custom schema provided to this bean
		if (StringUtils.isNotBlank(customSchema)) {
			return customSchema;
		}
		
		Connection c = null;
		try {
			c = getDataSource().getConnection();
			DatabaseMetaData meta = c.getMetaData();
			return meta.getUserName();
		} catch (SQLException e) {
			throw new DatabaseException(e);
		} finally {
			DbUtils.closeQuietly(c);
		}
	}
	
	protected String determineTablespaceData() throws DatabaseException {
		// custom tablespace provided to this bean
		if (StringUtils.isNotBlank(customTablespaceData)) {
			return customTablespaceData;
		}
		
		return determineSchema().concat("_DAT");
	}
	
	protected String determineTablespaceIndex() throws DatabaseException {
		// custom tablespace provided to this bean
		if (StringUtils.isNotBlank(customTablespaceIndex)) {
			return customTablespaceIndex;
		}
		
		return determineSchema().concat("_IDX");
	}
	
	// Getters & setters

	public boolean isUpgradeRequired() {
		return upgradeRequired;
	}

	public void setUpgradeRequired(boolean upgradeRequired) {
		this.upgradeRequired = upgradeRequired;
	}

	public String getUpgradeSql() {
		return upgradeSql;
	}

	public void setUpgradeSql(String upgradeSql) {
		this.upgradeSql = upgradeSql;
	}

	public Map<String, String> getParameters() {
		return parameters;
	}

	public void setParameters(Map<String, String> parameters) {
		this.parameters = parameters;
	}

	public String getCustomSchema() {
		return customSchema;
	}

	public void setCustomSchema(String customSchema) {
		this.customSchema = customSchema;
	}

	public String getCustomTablespaceData() {
		return customTablespaceData;
	}

	public void setCustomTablespaceData(String customTablespaceData) {
		this.customTablespaceData = customTablespaceData;
	}

	public String getCustomTablespaceIndex() {
		return customTablespaceIndex;
	}

	public void setCustomTablespaceIndex(String customTablespaceIndex) {
		this.customTablespaceIndex = customTablespaceIndex;
	}

}