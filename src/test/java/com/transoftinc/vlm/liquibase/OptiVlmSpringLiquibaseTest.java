package com.transoftinc.vlm.liquibase;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import liquibase.exception.LiquibaseException;

import org.apache.commons.dbutils.DbUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Creates a minimized InvoiceValidation application context.
 * 
 * @author ar250203
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/META-INF/spring/applicationContext.xml" })
public class OptiVlmSpringLiquibaseTest {

	@Autowired
	private OptiVlmSpringLiquibase liquibase;

	/*
	@Test
	public void runProcess1() throws LiquibaseException {

		System.out.println(liquibase.getUpgradeSql());
	}
	*/
	
	@Test
	public void testUpgradeRequired() throws LiquibaseException {
		liquibase.setChangeLog("classpath:/META-INF/db/master_log_1.xml");

		liquibase.checkUpgrade();

		assertTrue(liquibase.isUpgradeRequired());

		// System.out.println(liquibase.getUpgradeSql());
	}

	@Test
	public void testNoUpgradeRequired() throws LiquibaseException, SQLException {
		liquibase.setChangeLog("classpath:/META-INF/db/master_log_1.xml");

		runSql("INSERT INTO AVV_IV3.DATABASECHANGELOG (ID, AUTHOR, FILENAME, DATEEXECUTED, ORDEREXECUTED, MD5SUM, DESCRIPTION, COMMENTS, EXECTYPE, LIQUIBASE) VALUES ('test-100', 'ar250203', 'classpath:/META-INF/db/master_log_1.xml', SYSTIMESTAMP, 142, '7:01d96d0886df507cd37513a2dfb198a8', 'sql', '', 'EXECUTED', '3.3.5')");

		liquibase.checkUpgrade();

		assertFalse(liquibase.isUpgradeRequired());

		runSql("DELETE FROM AVV_IV3.DATABASECHANGELOG WHERE ID = 'test-100'");
	}

	@Test
	public void testApplyUpgrade() throws LiquibaseException, SQLException {
		liquibase.setChangeLog("classpath:/META-INF/db/master_log_1.xml");

		liquibase.checkUpgrade();

		assertTrue(liquibase.isUpgradeRequired());

		String error = liquibase.applyUpgrade();

		//System.out.println(error);
		
		assertNull(error);

		liquibase.checkUpgrade();

		assertFalse(liquibase.isUpgradeRequired());

		runSql("DELETE FROM AVV_IV3.DATABASECHANGELOG WHERE ID = 'test-100'");
	}

	private void runSql(String sql) throws SQLException {
		Connection c = null;
		Statement stmt = null;

		try {
			c = liquibase.getDataSource().getConnection();
			stmt = c.createStatement();
			stmt.execute(sql);
		} catch (SQLException e) {
			throw e;
		} finally {
			DbUtils.closeQuietly(stmt);
			DbUtils.closeQuietly(c);
		}
	}

}