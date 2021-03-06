package paulkrause88.mybatis.jsr354.type;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.money.CurrencySupplier;
import javax.money.CurrencyUnit;
import javax.money.MonetaryAmount;
import javax.money.MonetaryAmountFactory;
import javax.money.MonetaryAmounts;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

public abstract class AbstractMonetaryAmountHandler extends BaseTypeHandler<MonetaryAmount> implements CurrencySupplier {
	
	private static ThreadLocal<Map<CurrencyUnit, MonetaryAmountFactory<?>>> FACTORY_MAPS = new ThreadLocal<>();

	private static MonetaryAmountFactory<?> factoryFor(CurrencyUnit currency) {
		Map<CurrencyUnit, MonetaryAmountFactory<?>> map = FACTORY_MAPS.get();
		if (map == null) {
			map = new HashMap<>();
			FACTORY_MAPS.set(map);
		}
		MonetaryAmountFactory<? extends MonetaryAmount> factory = map.get(currency);
		if (factory == null) {
			factory = MonetaryAmounts.getAmountFactory();
			factory.setCurrency(currency);
			map.put(currency, factory);
		}
		return factory;
	}
	
	private MonetaryAmount toMonetaryAmount(BigDecimal big) {
		return big == null ? null : factoryFor(getCurrency()).setNumber(big).create();
	}
	
	@Override
	public MonetaryAmount getNullableResult(ResultSet rs, String col) throws SQLException {
		return toMonetaryAmount(rs.getBigDecimal(col));
	}

	@Override
	public MonetaryAmount getNullableResult(ResultSet rs, int col) throws SQLException {
		return toMonetaryAmount(rs.getBigDecimal(col));
	}

	@Override
	public MonetaryAmount getNullableResult(CallableStatement cs, int col) throws SQLException {
		return toMonetaryAmount(cs.getBigDecimal(col));
	}

	@Override
	public void setNonNullParameter(PreparedStatement ps, int i, MonetaryAmount x, JdbcType type) throws SQLException {
		ps.setBigDecimal(i, x.getNumber().numberValueExact(BigDecimal.class));
	}
}
