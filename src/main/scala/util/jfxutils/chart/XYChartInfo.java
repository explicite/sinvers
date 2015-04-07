
package util.jfxutils.chart;

import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.chart.Axis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.Region;

import static util.jfxutils.SFXUtil.*;

public class XYChartInfo {
	private final XYChart<?,?> chart;
	private final Node referenceNode;

	public XYChartInfo( XYChart<?, ?> chart, Node referenceNode ) {
		this.chart = chart;
		this.referenceNode = referenceNode;
	}

	public XYChartInfo( XYChart<?, ?> chart ) {
		this( chart, chart );
	}

	public XYChart<?, ?> getChart() {
		return chart;
	}

	public Node getReferenceNode() {
		return referenceNode;
	}

	@SuppressWarnings( "unchecked" )
	public Point2D getDataCoordinates( double x, double y ) {
		Axis xAxis = chart.getXAxis();
		Axis yAxis = chart.getYAxis();

		double xStart = getXShift( xAxis, referenceNode );
		double yStart = getYShift( yAxis, referenceNode );

		return new Point2D(
				xAxis.toNumericValue( xAxis.getValueForDisplay( x - xStart ) ),
		    yAxis.toNumericValue( yAxis.getValueForDisplay( y - yStart ) )
		);
	}

	@SuppressWarnings( "unchecked" )
	public Rectangle2D getDataCoordinates( double minX, double minY, double maxX, double maxY ) {
		if ( minX > maxX || minY > maxY ) {
			throw new IllegalArgumentException( "min > max for X and/or Y" );
		}

		Axis xAxis = chart.getXAxis();
		Axis yAxis = chart.getYAxis();

		double xStart = getXShift( xAxis, referenceNode );
		double yStart = getYShift( yAxis, referenceNode );

		double minDataX = xAxis.toNumericValue( xAxis.getValueForDisplay( minX - xStart ) );
		double maxDataX = xAxis.toNumericValue( xAxis.getValueForDisplay( maxX - xStart ) );

		double minDataY = yAxis.toNumericValue( yAxis.getValueForDisplay( maxY - yStart ) );
		double maxDataY = yAxis.toNumericValue( yAxis.getValueForDisplay( minY - yStart ) );

		return new Rectangle2D( minDataX,
		                        minDataY,
		                        maxDataX - minDataX,
		                        maxDataY - minDataY );
	}

	public boolean isInPlotArea( double x, double y ) {
		return getPlotArea().contains( x, y );
	}

	public Rectangle2D getPlotArea() {
		Axis<?> xAxis = chart.getXAxis();
		Axis<?> yAxis = chart.getYAxis();

		double xStart = getXShift( xAxis, referenceNode );
		double yStart = getYShift( yAxis, referenceNode );

		double width = xAxis.getWidth();
		double height = yAxis.getHeight();

		return new Rectangle2D( xStart, yStart, width, height );
	}

	public Rectangle2D getXAxisArea() {
		return getComponentArea( chart.getXAxis() );
	}

	public Rectangle2D getYAxisArea() {
		return getComponentArea( chart.getYAxis() );
	}

	private Rectangle2D getComponentArea( Region childRegion ) {
		double xStart = getXShift( childRegion, referenceNode );
		double yStart = getYShift( childRegion, referenceNode );

		return new Rectangle2D( xStart, yStart, childRegion.getWidth(), childRegion.getHeight() );
	}
}
