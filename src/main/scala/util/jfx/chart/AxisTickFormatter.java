package util.jfx.chart;


public interface AxisTickFormatter {
	public void setRange(double low, double high, double tickSpacing);
	public String format(Number value);
}
