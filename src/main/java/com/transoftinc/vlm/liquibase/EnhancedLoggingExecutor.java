package com.transoftinc.vlm.liquibase;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import liquibase.database.Database;
import liquibase.database.core.MSSQLDatabase;
import liquibase.database.core.SybaseASADatabase;
import liquibase.database.core.SybaseDatabase;
import liquibase.exception.DatabaseException;
import liquibase.executor.Executor;
import liquibase.executor.LoggingExecutor;
import liquibase.sql.visitor.SqlVisitor;
import liquibase.sqlgenerator.SqlGeneratorFactory;
import liquibase.statement.SqlStatement;
import liquibase.statement.core.LockDatabaseChangeLogStatement;
import liquibase.statement.core.RawSqlStatement;
import liquibase.statement.core.UnlockDatabaseChangeLogStatement;
import liquibase.util.StreamUtil;

/**
 * Counts the number of statements executed.
 */
public class EnhancedLoggingExecutor extends LoggingExecutor {

	protected Writer output;
	
	protected int writtenStatementsCounter;
	
	public EnhancedLoggingExecutor(Executor delegatedExecutor, Writer output, Database database) {
		super(delegatedExecutor, output, database);
		
        this.output = output;
        //this.delegatedReadExecutor = delegatedExecutor;
        setDatabase(database);
	}

    public void execute(SqlStatement sql) throws DatabaseException {
        outputStatement(sql);
    }
    
    public int update(SqlStatement sql) throws DatabaseException {
        if (sql instanceof LockDatabaseChangeLogStatement) {
            return 1;
        } else if (sql instanceof UnlockDatabaseChangeLogStatement) {
            return 1;
        }

        outputStatement(sql);

        return 0;
    }
    
    public void execute(SqlStatement sql, List<SqlVisitor> sqlVisitors) throws DatabaseException {
        outputStatement(sql, sqlVisitors);
    }

    public int update(SqlStatement sql, List<SqlVisitor> sqlVisitors) throws DatabaseException {
        outputStatement(sql, sqlVisitors);
        return 0;
    }

    protected void outputStatement(SqlStatement sql) throws DatabaseException {
        outputStatement(sql, new ArrayList<SqlVisitor>());
    }
    
    protected void outputStatement(SqlStatement sql, List<SqlVisitor> sqlVisitors) throws DatabaseException {
        try {
        	if (SqlGeneratorFactory.getInstance().generateStatementsVolatile(sql, database)) {
                throw new DatabaseException(sql.getClass().getSimpleName()+" requires access to up to date database metadata which is not available in SQL output mode");
            }
            for (String statement : applyVisitors(sql, sqlVisitors)) {
                if (statement == null) {
                    continue;
                }
                output.write(statement);
                writtenStatementsCounter++;

                if (database instanceof MSSQLDatabase || database instanceof SybaseDatabase || database instanceof SybaseASADatabase) {
                    output.write(StreamUtil.getLineSeparator());
                    output.write("GO");
                } else {
                    String endDelimiter = ";";
                    if (sql instanceof RawSqlStatement) {
                        endDelimiter = ((RawSqlStatement) sql).getEndDelimiter();
                    }
                    if (!statement.endsWith(endDelimiter)) {
                        output.write(endDelimiter);
                    }
                }
                output.write(StreamUtil.getLineSeparator());
                output.write(StreamUtil.getLineSeparator());
            }
        } catch (IOException e) {
            throw new DatabaseException(e);
        }
    }

	public int getWrittenStatementsCounter() {
		return writtenStatementsCounter;
	}

}
