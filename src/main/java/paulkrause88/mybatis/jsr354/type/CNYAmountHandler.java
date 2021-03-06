package paulkrause88.mybatis.jsr354.type;

import javax.money.CurrencyUnit;
import javax.money.MonetaryCurrencies;

import org.apache.ibatis.type.Alias;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;

@Alias("CNYAmountHandler")
@MappedJdbcTypes(JdbcType.DECIMAL)
public class CNYAmountHandler extends AbstractMonetaryAmountHandler {

	private static final CurrencyUnit CNY = MonetaryCurrencies.getCurrency("CNY");

	@Override
	public CurrencyUnit getCurrency() {
		return CNY;
	}
}
