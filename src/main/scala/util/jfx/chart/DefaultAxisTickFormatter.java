
package util.jfx.chart;

import java.text.NumberFormat;

public class DefaultAxisTickFormatter implements AxisTickFormatter {

	private NumberFormat currFormat = NumberFormat.getNumberInstance();

	public DefaultAxisTickFormatter() {
	}

	@Override
	public void setRange( double low, double high, double tickSpacing ) {

	}

	@Override
	public String format( Number value ) {
		return currFormat.format( value );
	}
}
