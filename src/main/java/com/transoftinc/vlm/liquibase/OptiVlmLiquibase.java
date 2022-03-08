package com.transoftinc.vlm.liquibase;

import java.io.IOException;
import java.io.Writer;
import java.text.DateFormat;
import java.util.Date;

import liquibase.Contexts;
import liquibase.LabelExpression;
import liquibase.Liquibase;
import liquibase.RuntimeEnvironment;
import liquibase.changelog.ChangeLogIterator;
import liquibase.changelog.DatabaseChangeLog;
import liquibase.database.Database;
import liquibase.database.DatabaseConnection;
import liquibase.database.ObjectQuotingStrategy;
import liquibase.exception.DatabaseException;
import liquibase.exception.LiquibaseException;
import liquibase.exception.LockException;
import liquibase.exception.ValidationFailedException;
import liquibase.executor.Executor;
import liquibase.executor.ExecutorService;
import liquibase.executor.LoggingExecutor;
import liquibase.integration.spring.SpringLiquibase.SpringResourceOpener;
import liquibase.lockservice.LockService;
import liquibase.lockservice.LockServiceFactory;
import liquibase.util.LiquibaseUtil;
import liquibase.util.StreamUtil;

/**
 * This implementation allows us to ignore validation results (i.e., if
 * checksums don't match) and run the upgrade anyway. The validation failure is
 * logged as a severe error.
 * 
 * @author ar250203
 */
public class OptiVlmLiquibase extends Liquibase {

	/**
	 * Whether database structure (checksums) is valid
	 */
	private boolean valid = true;
	
	// Constructors
	
	public OptiVlmLiquibase(String changeLog, SpringResourceOpener resourceOpener, Database database) throws LiquibaseException {
		super(changeLog, resourceOpener, database);
	}

	// Methods
	
	public void updateIgnoreValidation(Contexts contexts, LabelExpression labelExpression, Writer output) throws LiquibaseException {
		getChangeLogParameters().setContexts(contexts);
		getChangeLogParameters().setLabels(labelExpression);

		Executor oldTemplate = ExecutorService.getInstance().getExecutor(database);
		LoggingExecutor loggingExecutor = new LoggingExecutor(ExecutorService.getInstance().getExecutor(database), output, database);
		ExecutorService.getInstance().setExecutor(database, loggingExecutor);

		outputHeader("Update Database Script");

		LockService lockService = LockServiceFactory.getInstance().getLockService(database);
		lockService.waitForLock();

		try {
			updateIgnoreValidation(contexts, labelExpression);

			output.flush();
		} catch (IOException e) {
			throw new LiquibaseException(e);
		}

		ExecutorService.getInstance().setExecutor(database, oldTemplate);
		resetServices();
	}

	public void updateIgnoreValidation(Contexts contexts, LabelExpression labelExpression) throws LiquibaseException {
		LockService lockService = LockServiceFactory.getInstance().getLockService(database);
		lockService.waitForLock();

		getChangeLogParameters().setContexts(contexts);
		getChangeLogParameters().setLabels(labelExpression);

		try {
			DatabaseChangeLog changeLog = getDatabaseChangeLog();

			checkLiquibaseTables(true, changeLog, contexts, labelExpression);

			valid = true;
			
			try {
				changeLog.validate(database, contexts, labelExpression);
			} catch (ValidationFailedException e0) {
				valid = false;
				getLog().severe(e0.getMessage());
			}

			ChangeLogIterator changeLogIterator = getStandardChangelogIterator(contexts, labelExpression, changeLog);

			changeLogIterator.run(createUpdateVisitor(), new RuntimeEnvironment(database, contexts, labelExpression));
		} finally {
			database.setObjectQuotingStrategy(ObjectQuotingStrategy.LEGACY);
			try {
				lockService.releaseLock();
			} catch (LockException e) {
				getLog().severe("Could not release lock", e);
			}
			resetServices();
		}
	}

	private void outputHeader(String message) throws DatabaseException {
		Executor executor = ExecutorService.getInstance().getExecutor(database);
		executor.comment("*********************************************************************");
		executor.comment(message);
		executor.comment("*********************************************************************");
		executor.comment("Change Log: " + getChangeLogFile());
		executor.comment("Ran at: " + DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(new Date()));
		DatabaseConnection connection = getDatabase().getConnection();
		if (connection != null) {
			executor.comment("Against: " + connection.getConnectionUserName() + "@" + connection.getURL());
		}
		executor.comment("Liquibase version: " + LiquibaseUtil.getBuildVersion());
		executor.comment("*********************************************************************" + StreamUtil.getLineSeparator());

		// this statement cannot be run through JDBC, so commenting it out
		/*
		if (database instanceof OracleDatabase) {
			executor.execute(new RawSqlStatement("SET DEFINE OFF;"));
		}
		*/
	}

	// Getters & setters
	
	public boolean isValid() {
		return valid;
	}

	public void setValid(boolean valid) {
		this.valid = valid;
	}

}
