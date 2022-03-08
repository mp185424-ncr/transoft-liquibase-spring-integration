package com.transoftinc.vlm.liquibase;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.util.Map;

import javax.sql.DataSource;

import liquibase.Liquibase;
import liquibase.exception.LiquibaseException;
import liquibase.integration.spring.SpringLiquibase;

/**
 * This is a modified SpringLiquibase bean, which allows us to write DB upgrade queries to a file. 
 * 
 */
@Deprecated
public class EnhancedSpringLiquibase extends SpringLiquibase {
	
	private boolean writeSqlFileEnabled = false;
	
	private String sqlOutputDir = "";
	
	private Map<String, String> parameters;
	
	private boolean databaseUpgradeRequired = false;

    public void setChangeLogParameters(Map<String, String> parameters) {
        this.parameters = parameters;
    }
	
    protected Liquibase createLiquibase(Connection c) throws LiquibaseException {
    	Liquibase liquibase = new EnhancedLiquibase(getChangeLog(), createResourceOpener(), createDatabase(c));
        
        if (parameters != null) {
            for(Map.Entry<String, String> entry: parameters.entrySet()) {
                liquibase.setChangeLogParameter(entry.getKey(), entry.getValue());
            }
        }

        if (isDropFirst()) {
            liquibase.dropAll();
        }

        return liquibase;
    }
    
	protected void performUpdate(Liquibase liquibase) throws LiquibaseException {
		if (writeSqlFileEnabled) {
			liquibase.update(getContexts(), getWriter());
		} else {
			liquibase.update(getContexts());
		}
		
		// check if database upgrade is required
		if (liquibase instanceof EnhancedLiquibase) {
			EnhancedLiquibase enhancedLiquibase = (EnhancedLiquibase)liquibase;
			if (enhancedLiquibase.getAppliedStatementsCounter() > 0) {
				setDatabaseUpgradeRequired(true);
			}
		}
	}

	public PrintWriter getWriter() {
		File file = new File(createSqlOutputFile());
		PrintWriter pw;
		try {
			pw = new PrintWriter(file);
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
		return pw;
	}
	
	private String createSqlOutputFile() {
		String outputDir = getSqlOutputDir();
		if (outputDir.endsWith("\\")) {
			outputDir = outputDir.substring(0, outputDir.length() - 1);
		}
		File outputDirectory = new File(outputDir);
		
		if (!outputDirectory.isDirectory()) {
			throw new IllegalArgumentException("The sqlOutputDir "+sqlOutputDir+" is not a valid directory");
		}
		
		if (!outputDirectory.exists()) {
			outputDirectory.mkdir();
		}
		
		String outputFilePath = outputDirectory.toString() + File.separator + "db-upgrade.sql";
		return outputFilePath;
	}

	// Getters & Setters
	
	public boolean getWriteSqlFileEnabled() {
		return writeSqlFileEnabled;
	}

	public void setWriteSqlFileEnabled(boolean writeSqlFileEnabled) {
		this.writeSqlFileEnabled = writeSqlFileEnabled;
	}

	public String getSqlOutputDir() {
		return sqlOutputDir;
	}

	public void setSqlOutputDir(String sqlOutputDir) {
		this.sqlOutputDir = sqlOutputDir;
	}

	public boolean isDatabaseUpgradeRequired() {
		return databaseUpgradeRequired;
	}

	public void setDatabaseUpgradeRequired(boolean databaseUpgradeRequired) {
		this.databaseUpgradeRequired = databaseUpgradeRequired;
	}
	
	public void setDataSource(DataSource dataSource){
		super.setDataSource(dataSource);
	}
	
	public void setChangeLog(String changeLog){
		super.setChangeLog(changeLog);
	}
	
	public void setContexts(String contexts){
		super.setContexts(contexts);
	}
}
