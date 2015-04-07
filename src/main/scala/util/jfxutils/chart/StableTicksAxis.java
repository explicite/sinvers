/*
 * Copyright 2013 Jason Winnebeck
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package util.jfxutils.chart;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.WritableValue;
import javafx.collections.ObservableList;
import javafx.geometry.Dimension2D;
import javafx.scene.chart.ValueAxis;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

public class StableTicksAxis extends ValueAxis<Number> {

	private static final double[] dividers = new double[] { 1.0, 2.5, 5.0 };

	private static final int numMinorTicks = 3;

	private final Timeline animationTimeline = new Timeline();
	private final WritableValue<Double> scaleValue = new WritableValue<Double>() {
		@Override
		public Double getValue() {
			return getScale();
		}

		@Override
		public void setValue( Double value ) {
			setScale( value );
		}
	};

	private AxisTickFormatter axisTickFormatter = new DefaultAxisTickFormatter();

	private List<Number> minorTicks;

	private DoubleProperty autoRangePadding = new SimpleDoubleProperty( 0.1 );

	private BooleanProperty forceZeroInRange = new SimpleBooleanProperty( true );

	public StableTicksAxis() {
	}

	public StableTicksAxis( double lowerBound, double upperBound ) {
		super( lowerBound, upperBound );
	}

	public AxisTickFormatter getAxisTickFormatter() {
		return axisTickFormatter;
	}

	public void setAxisTickFormatter( AxisTickFormatter axisTickFormatter ) {
		this.axisTickFormatter = axisTickFormatter;
	}

	public double getAutoRangePadding() {
		return autoRangePadding.get();
	}

	public DoubleProperty autoRangePaddingProperty() {
		return autoRangePadding;
	}

	public void setAutoRangePadding( double autoRangePadding ) {
		this.autoRangePadding.set( autoRangePadding );
	}

	public boolean isForceZeroInRange() {
		return forceZeroInRange.get();
	}

	public BooleanProperty forceZeroInRangeProperty() {
		return forceZeroInRange;
	}

	public void setForceZeroInRange( boolean forceZeroInRange ) {
		this.forceZeroInRange.set( forceZeroInRange );
	}

	@Override
	protected Range autoRange( double minValue, double maxValue, double length, double labelSize ) {

		if ( Math.abs(minValue - maxValue) < 1e-300) {
			minValue = minValue - 1;
			maxValue = maxValue + 1;

		} else {
			double delta = maxValue - minValue;
			double paddedMin = minValue - delta * autoRangePadding.get();
			if ( Math.signum( paddedMin ) != Math.signum( minValue ) )
				paddedMin = 0.0;

			double paddedMax = maxValue + delta * autoRangePadding.get();
			if ( Math.signum( paddedMax ) != Math.signum( maxValue ) )
				paddedMax = 0.0;

			minValue = paddedMin;
			maxValue = paddedMax;
		}

		if ( forceZeroInRange.get() ) {
			if ( minValue < 0 && maxValue < 0 ) {
				maxValue = 0;
				minValue -= -minValue * autoRangePadding.get();
			} else if ( minValue > 0 && maxValue > 0 ) {
				minValue = 0;
				maxValue += maxValue * autoRangePadding.get();
			}
		}

		Range ret = getRange( minValue, maxValue );
		return ret;
	}

	private Range getRange( double minValue, double maxValue ) {
		double length = getLength();
		double delta = maxValue - minValue;
		double scale = calculateNewScale( length, minValue, maxValue );

		int maxTicks = Math.max( 1, (int) ( length / getLabelSize() ) );

		Range ret;
		ret = new Range( minValue, maxValue, calculateTickSpacing( delta, maxTicks ), scale );
		return ret;
	}

	public static double calculateTickSpacing( double delta, int maxTicks ) {
		if ( delta == 0.0 )
			return 0.0;
		if ( delta <= 0.0 )
			throw new IllegalArgumentException( "delta must be positive" );
		if ( maxTicks < 1 )
			throw new IllegalArgumentException( "must be at least one tick" );

		int factor = (int) Math.log10( delta );
		int divider = 0;
		double numTicks = delta / ( dividers[divider] * Math.pow( 10, factor ) );

		if ( numTicks < maxTicks ) {
			while ( numTicks < maxTicks ) {
				--divider;
				if ( divider < 0 ) {
					--factor;
					divider = dividers.length - 1;
				}

				numTicks = delta / ( dividers[divider] * Math.pow( 10, factor ) );
			}

			if ( numTicks != maxTicks ) {
				++divider;
				if ( divider >= dividers.length ) {
					++factor;
					divider = 0;
				}
			}
		} else {
			while ( numTicks > maxTicks ) {
				++divider;
				if ( divider >= dividers.length ) {
					++factor;
					divider = 0;
				}

				numTicks = delta / ( dividers[divider] * Math.pow( 10, factor ) );
			}
		}

		return dividers[divider] * Math.pow( 10, factor );
	}

	@Override
	protected List<Number> calculateMinorTickMarks() {
		return minorTicks;
	}

	@Override
	protected void setRange( Object range, boolean animate ) {
		Range rangeVal = (Range) range;
		if ( animate ) {
			animationTimeline.stop();
			ObservableList<KeyFrame> keyFrames = animationTimeline.getKeyFrames();
			keyFrames.setAll(
					new KeyFrame( Duration.ZERO,
					              new KeyValue( currentLowerBound, getLowerBound() ),
					              new KeyValue( scaleValue, getScale() ) ),
					new KeyFrame( Duration.millis( 750 ),
					              new KeyValue( currentLowerBound, rangeVal.low ),
					              new KeyValue( scaleValue, rangeVal.scale ) ) );
			animationTimeline.play();

		} else {
			currentLowerBound.set( rangeVal.low );
			setScale( rangeVal.scale );
		}
		setLowerBound( rangeVal.low );
		setUpperBound( rangeVal.high );

		axisTickFormatter.setRange( rangeVal.low, rangeVal.high, rangeVal.tickSpacing );
	}

	@Override
	protected Range getRange() {
		return getRange( getLowerBound(), getUpperBound() );
	}

	@Override
	protected List<Number> calculateTickValues( double length, Object range ) {
		Range rangeVal = (Range) range;
		double firstTick = Math.floor( rangeVal.low / rangeVal.tickSpacing ) * rangeVal.tickSpacing;
		int numTicks = (int) (rangeVal.getDelta() / rangeVal.tickSpacing) + 1;
		List<Number> ret = new ArrayList<Number>( numTicks + 1 );
		minorTicks = new ArrayList<Number>( ( numTicks + 2 ) * numMinorTicks );
		double minorTickSpacing = rangeVal.tickSpacing / ( numMinorTicks + 1 );
		for ( int i = 0; i <= numTicks; ++i ) {
			double majorTick = firstTick + rangeVal.tickSpacing * i;
			ret.add( majorTick );
			for ( int j = 1; j <= numMinorTicks; ++j ) {
				minorTicks.add( majorTick + minorTickSpacing * j );
			}
		}
		return ret;
	}

	@Override
	protected String getTickMarkLabel( Number number ) {
		return axisTickFormatter.format( number );
	}

	private double getLength() {
		if ( getSide().isHorizontal() )
			return getWidth();
		else
			return getHeight();
	}

	private double getLabelSize() {
		Dimension2D dim = measureTickMarkLabelSize( "-888.88E-88", getTickLabelRotation() );
		if ( getSide().isHorizontal() ) {
			return dim.getWidth();
		} else {
			return dim.getHeight();
		}
	}

	private static class Range {
		public final double low;
		public final double high;
		public final double tickSpacing;
		public final double scale;

		private Range( double low, double high, double tickSpacing, double scale ) {
			this.low = low;
			this.high = high;
			this.tickSpacing = tickSpacing;
			this.scale = scale;
		}

		public double getDelta() {
			return high - low;
		}

		@Override
		public String toString() {
			return "Range{" +
			       "low=" + low +
			       ", high=" + high +
			       ", tickSpacing=" + tickSpacing +
			       ", scale=" + scale +
			       '}';
		}
	}
}
