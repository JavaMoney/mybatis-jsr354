package paulkrause88.mybatis.jsr354;

import static org.apache.ibatis.io.Resources.getResourceAsReader;
import static org.junit.Assert.*;

import java.io.IOException;
import java.io.Reader;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.Year;

import javax.money.CurrencyUnit;
import javax.money.MonetaryCurrencies;

import org.apache.ibatis.jdbc.ScriptRunner;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.BeforeClass;
import org.junit.Test;

import domain.money.dao.NetIntlInvstPosMapper;
import domain.money.dao.PublicDebtMapper;
import domain.money.model.NetIntlInvstPos;
import domain.money.model.PublicDebt;

@SuppressWarnings("static-method")
public class MoneyDomainTest {

	private static SqlSessionFactory sqlSessionFactory;
	private static CurrencyUnit USD = MonetaryCurrencies.getCurrency("USD");

	@BeforeClass
	public static void setUp() throws Exception {
		// create a SqlSessionFactory
		try (Reader reader = getResourceAsReader("databases/money/mybatis-config.xml")) {
			sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
		}

		// populate in-memory database
		runScripts("databases/money/money-hsqldb-schema.sql",
				   "databases/money/money-hsqldb-dataload.sql");
	}

	private static void runScripts(String ... scripts) throws IOException, SQLException {
		try (SqlSession session = sqlSessionFactory.openSession();
				Connection conn = session.getConnection()) {
			for (String script : scripts) {
				try (Reader reader = getResourceAsReader(script)) {
					ScriptRunner runner = new ScriptRunner(conn);
					runner.setLogWriter(null);
					runner.runScript(reader);
				}
			}
		}
	}
		
	@Test
	public void testGetPublicDebtUS() {
		try (SqlSession session = sqlSessionFactory.openSession()) {
			PublicDebtMapper mapper = session.getMapper(PublicDebtMapper.class);
			PublicDebt us = mapper.selectMostRecent("US");
			assertEquals("US", us.getCountryCode());
			assertEquals(17344649899999L, us.getExternalDebtUSD().getNumber().longValue());
			assertEquals(USD, us.getExternalDebtUSD().getCurrency());
			assertEquals(52170, us.getPerCapitaUSD().getNumber().intValue());
			assertEquals(USD, us.getPerCapitaUSD().getCurrency());
		}
	}

	@Test
	public void testGetPublicDebtJP() {
		try (SqlSession session = sqlSessionFactory.openSession()) {
			PublicDebtMapper mapper = session.getMapper(PublicDebtMapper.class);
			PublicDebt jp = mapper.selectMostRecent("JP");
			assertEquals("JP", jp.getCountryCode());
			assertEquals(3024000000000L, jp.getExternalDebtUSD().getNumber().longValue());
			assertEquals(USD, jp.getExternalDebtUSD().getCurrency());
			assertEquals(24000, jp.getPerCapitaUSD().getNumber().intValue());
			assertEquals(USD, jp.getPerCapitaUSD().getCurrency());
		}
	}
	
	@Test
	public void testGetNetIntlInvstPosJP() {
		try (SqlSession session = sqlSessionFactory.openSession()) {
			NetIntlInvstPosMapper mapper = session.getMapper(NetIntlInvstPosMapper.class);
			NetIntlInvstPos jp = mapper.selectMostRecent("JP");
			assertEquals("JP", jp.getCountryCode());
			assertEquals(Year.of(2009), jp.getForYear());
			//'470936600000000 JPY', 
			//'266233000000000 JPY'
		}
	}

}
