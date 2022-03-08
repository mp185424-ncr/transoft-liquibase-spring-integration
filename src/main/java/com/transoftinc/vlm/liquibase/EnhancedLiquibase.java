package com.transoftinc.vlm.liquibase;

import java.io.IOException;
import java.io.Writer;
import java.text.DateFormat;
import java.util.Date;

import liquibase.Contexts;
import liquibase.LabelExpression;
import liquibase.Liquibase;
import liquibase.changelog.ChangeLogParameters;
import liquibase.database.Database;
import liquibase.exception.DatabaseException;
import liquibase.exception.LiquibaseException;
import liquibase.executor.Executor;
import liquibase.executor.ExecutorService;
import liquibase.lockservice.LockService;
import liquibase.lockservice.LockServiceFactory;
import liquibase.resource.ResourceAccessor;
import liquibase.util.LiquibaseUtil;
import liquibase.util.StreamUtil;
import liquibase.util.StringUtils;

/**
 * We need the Liquibase object to use EnhancedLoggingExecutor, instead of the usual one. The enhanced one counts the number of statements executed.
 */
public class EnhancedLiquibase extends Liquibase {

	private String changeLogFile;
	
	private ChangeLogParameters changeLogParameters;
	
	private int appliedStatementsCounter = 0;
	
	public EnhancedLiquibase(String changeLogFile, ResourceAccessor resourceAccessor, Database database) throws LiquibaseException {
		super(changeLogFile, resourceAccessor, database);
		
        if (changeLogFile != null) {
            this.changeLogFile = changeLogFile.replace('\\', '/');  //convert to standard / if usign absolute path on windows
        }

        changeLogParameters = new ChangeLogParameters(database);
        setDatabase(database);
	}

    private void setDatabase(Database database) throws DatabaseException {
        this.database=database;
        if(database!=null) //Some tests use a null database
            setDatabasePropertiesAsChangelogParameters(database);
    }

    /**
     * Add safe database properties as changelog parameters.<br/>
     * Safe properties are the ones that doesn't have side effects in liquibase state and also don't change in during the liquibase execution
     * @param database Database which propeties are put in the changelog
     * @throws DatabaseException
     */
    private void setDatabasePropertiesAsChangelogParameters(Database database) throws DatabaseException {            
        setChangeLogParameter("database.autoIncrementClause", database.getAutoIncrementClause(null, null));
        setChangeLogParameter("database.currentDateTimeFunction", database.getCurrentDateTimeFunction());
        setChangeLogParameter("database.databaseChangeLogLockTableName", database.getDatabaseChangeLogLockTableName());
        setChangeLogParameter("database.databaseChangeLogTableName", database.getDatabaseChangeLogTableName());
        setChangeLogParameter("database.databaseMajorVersion", database.getDatabaseMajorVersion());
        setChangeLogParameter("database.databaseMinorVersion", database.getDatabaseMinorVersion());
        setChangeLogParameter("database.databaseProductName", database.getDatabaseProductName());
        setChangeLogParameter("database.databaseProductVersion", database.getDatabaseProductVersion());
        setChangeLogParameter("database.defaultCatalogName", database.getDefaultCatalogName());
        setChangeLogParameter("database.defaultSchemaName", database.getDefaultSchemaName());
        setChangeLogParameter("database.defaultSchemaNamePrefix", StringUtils.trimToNull(database.getDefaultSchemaName())==null?"":"."+database.getDefaultSchemaName());
        setChangeLogParameter("database.lineComment", database.getLineComment());
        setChangeLogParameter("database.liquibaseSchemaName", database.getLiquibaseSchemaName());
        setChangeLogParameter("database.liquibaseTablespaceName", database.getLiquibaseTablespaceName());
        setChangeLogParameter("database.typeName", database.getShortName());
        setChangeLogParameter("database.isSafeToRunUpdate", database.isSafeToRunUpdate());
        setChangeLogParameter("database.requiresPassword", database.requiresPassword());
        setChangeLogParameter("database.requiresUsername", database.requiresUsername());
        setChangeLogParameter("database.supportsForeignKeyDisable", database.supportsForeignKeyDisable());
        setChangeLogParameter("database.supportsInitiallyDeferrableColumns", database.supportsInitiallyDeferrableColumns());
        setChangeLogParameter("database.supportsRestrictForeignKeys", database.supportsRestrictForeignKeys());
        setChangeLogParameter("database.supportsSchemas", database.supportsSchemas());
        setChangeLogParameter("database.supportsSequences", database.supportsSequences());
        setChangeLogParameter("database.supportsTablespaces", database.supportsTablespaces());
}
    
    private void outputHeader(String message) throws DatabaseException {
        Executor executor = ExecutorService.getInstance().getExecutor(database);
        executor.comment("*********************************************************************");
        executor.comment(message);
        executor.comment("*********************************************************************");
        executor.comment("Change Log: " + changeLogFile);
        executor.comment("Ran at: " + DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(new Date()));
        executor.comment("Against: " + getDatabase().getConnection().getConnectionUserName() + "@" + getDatabase().getConnection().getURL());
        executor.comment("Liquibase version: " + LiquibaseUtil.getBuildVersion());
        executor.comment("*********************************************************************" + StreamUtil.getLineSeparator());
    }
    
    public void update(String contextString, Writer output) throws LiquibaseException {
        Contexts contexts = new Contexts(contextString);
        changeLogParameters.setContexts(contexts);

        Executor oldTemplate = ExecutorService.getInstance().getExecutor(database);
        EnhancedLoggingExecutor loggingExecutor = new EnhancedLoggingExecutor(ExecutorService.getInstance().getExecutor(database), output, database);
        ExecutorService.getInstance().setExecutor(database, loggingExecutor);

        outputHeader("Update Database Script");

        LockService lockService = LockServiceFactory.getInstance().getLockService(database);
        lockService.waitForLock();

        try {
            update(contexts);
            output.flush();
            appliedStatementsCounter = loggingExecutor.getWrittenStatementsCounter();
        } catch (IOException e) {
            throw new LiquibaseException(e);
        } finally {
            lockService.releaseLock();
        }

        ExecutorService.getInstance().setExecutor(database, oldTemplate);
    }
    
    public void update(int changesToApply, String contextString, Writer output) throws LiquibaseException {
    	 Contexts contexts = new Contexts(contextString);
        changeLogParameters.setContexts(contexts);

        Executor oldTemplate = ExecutorService.getInstance().getExecutor(database);
        EnhancedLoggingExecutor loggingExecutor = new EnhancedLoggingExecutor(ExecutorService.getInstance().getExecutor(database), output, database);
        ExecutorService.getInstance().setExecutor(database, loggingExecutor);

        outputHeader("Update " + changesToApply + " Change Sets Database Script");

        update(changesToApply, contexts, new LabelExpression());

        try {
            output.flush();
            appliedStatementsCounter = loggingExecutor.getWrittenStatementsCounter();
        } catch (IOException e) {
            throw new LiquibaseException(e);
        }

        ExecutorService.getInstance().setExecutor(database, oldTemplate);
    }

	public int getAppliedStatementsCounter() {
		return appliedStatementsCounter;
	}

}
