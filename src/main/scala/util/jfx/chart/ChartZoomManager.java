package util.jfx.chart;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.chart.ValueAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import util.jfx.EventHandlerManager;


public class ChartZoomManager {

	public static final EventHandler<MouseEvent> DEFAULT_FILTER = new EventHandler<MouseEvent>() {
		@Override
		public void handle( MouseEvent mouseEvent ) {
			if ( mouseEvent.getButton() != MouseButton.PRIMARY )
				mouseEvent.consume();
		}
	};

	private final SimpleDoubleProperty rectX = new SimpleDoubleProperty();
	private final SimpleDoubleProperty rectY = new SimpleDoubleProperty();
	private final SimpleBooleanProperty selecting = new SimpleBooleanProperty( false );

	private final DoubleProperty zoomDurationMillis = new SimpleDoubleProperty( 750.0 );
	private final BooleanProperty zoomAnimated = new SimpleBooleanProperty( true );
	private final BooleanProperty mouseWheelZoomAllowed = new SimpleBooleanProperty( true );

	private static enum ZoomMode { Horizontal, Vertical, Both }

	private ZoomMode zoomMode;

	private EventHandler<? super MouseEvent> mouseFilter = DEFAULT_FILTER;

	private final EventHandlerManager handlerManager;

	private final Rectangle selectRect;
	private final ValueAxis<?> xAxis;
	private final ValueAxis<?> yAxis;
	private final XYChartInfo chartInfo;

	private final Timeline zoomAnimation = new Timeline();

	public ChartZoomManager( Pane chartPane, Rectangle selectRect, XYChart<?,?> chart ) {
		this.selectRect = selectRect;
		this.xAxis = (ValueAxis<?>) chart.getXAxis();
		this.yAxis = (ValueAxis<?>) chart.getYAxis();
		chartInfo = new XYChartInfo( chart, chartPane );

		handlerManager = new EventHandlerManager( chartPane );

		handlerManager.addEventHandler( false, MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
			@Override
			public void handle( MouseEvent mouseEvent ) {
				if ( passesFilter( mouseEvent ) )
					onMousePressed( mouseEvent );
			}
		} );

		handlerManager.addEventHandler( false, MouseEvent.DRAG_DETECTED, new EventHandler<MouseEvent>() {
			@Override
			public void handle( MouseEvent mouseEvent ) {
				if ( passesFilter( mouseEvent ) )
					onDragStart();
			}
		} );

		handlerManager.addEventHandler( false, MouseEvent.MOUSE_DRAGGED, new EventHandler<MouseEvent>() {
			@Override
			public void handle( MouseEvent mouseEvent ) {
				onMouseDragged( mouseEvent );
			}
		} );

		handlerManager.addEventHandler( false, MouseEvent.MOUSE_RELEASED, new EventHandler<MouseEvent>() {
			@Override
			public void handle( MouseEvent mouseEvent ) {
				onMouseReleased();
			}
		} );

		handlerManager.addEventHandler( false, ScrollEvent.ANY, new MouseWheelZoomHandler() );
	}

	public boolean isZoomAnimated() {
		return zoomAnimated.get();
	}

	public BooleanProperty zoomAnimatedProperty() {
		return zoomAnimated;
	}

	public void setZoomAnimated( boolean zoomAnimated ) {
		this.zoomAnimated.set( zoomAnimated );
	}

	public double getZoomDurationMillis() {
		return zoomDurationMillis.get();
	}

	public DoubleProperty zoomDurationMillisProperty() {
		return zoomDurationMillis;
	}

	public void setZoomDurationMillis( double zoomDurationMillis ) {
		this.zoomDurationMillis.set( zoomDurationMillis );
	}

	public boolean isMouseWheelZoomAllowed() {
		return mouseWheelZoomAllowed.get();
	}

	public BooleanProperty mouseWheelZoomAllowedProperty() {
		return mouseWheelZoomAllowed;
	}

	public void setMouseWheelZoomAllowed( boolean allowed ) {
		mouseWheelZoomAllowed.set( allowed );
	}

	public EventHandler<? super MouseEvent> getMouseFilter() {
		return mouseFilter;
	}

	public void setMouseFilter( EventHandler<? super MouseEvent> mouseFilter ) {
		this.mouseFilter = mouseFilter;
	}

	public void start() {
		handlerManager.addAllHandlers();

		selectRect.widthProperty().bind( rectX.subtract( selectRect.translateXProperty() ) );
		selectRect.heightProperty().bind( rectY.subtract( selectRect.translateYProperty() ) );
		selectRect.visibleProperty().bind( selecting );
	}

	public void stop() {
		handlerManager.removeAllHandlers();
		selecting.set( false );
		selectRect.widthProperty().unbind();
		selectRect.heightProperty().unbind();
		selectRect.visibleProperty().unbind();
	}

	private boolean passesFilter( MouseEvent event ) {
		if ( mouseFilter != null ) {
			MouseEvent cloned = (MouseEvent) event.clone();
			mouseFilter.handle( cloned );
			if ( cloned.isConsumed() )
				return false;
		}

		return true;
	}

	private void onMousePressed( MouseEvent mouseEvent ) {
		double x = mouseEvent.getX();
		double y = mouseEvent.getY();

		Rectangle2D plotArea = chartInfo.getPlotArea();

		if ( plotArea.contains( x, y ) ) {
			selectRect.setTranslateX( x );
			selectRect.setTranslateY( y );
			rectX.set( x );
			rectY.set( y );
			zoomMode = ZoomMode.Both;

		} else if ( chartInfo.getXAxisArea().contains( x, y ) ) {
			selectRect.setTranslateX( x );
			selectRect.setTranslateY( plotArea.getMinY() );
			rectX.set( x );
			rectY.set( plotArea.getMaxY() );
			zoomMode = ZoomMode.Horizontal;

		} else if ( chartInfo.getYAxisArea().contains( x, y ) ) {
			selectRect.setTranslateX( plotArea.getMinX() );
			selectRect.setTranslateY( y );
			rectX.set( plotArea.getMaxX() );
			rectY.set( y );
			zoomMode = ZoomMode.Vertical;
		}
	}

	private void onDragStart() {
		selecting.set( true );
	}

	private void onMouseDragged( MouseEvent mouseEvent ) {
		if ( !selecting.get() )
			return;

		Rectangle2D plotArea = chartInfo.getPlotArea();

		if ( zoomMode == ZoomMode.Both || zoomMode == ZoomMode.Horizontal ) {
			double x = mouseEvent.getX();
			x = Math.max( x, selectRect.getTranslateX() );
			x = Math.min( x, plotArea.getMaxX() );
			rectX.set( x );
		}

		if ( zoomMode == ZoomMode.Both || zoomMode == ZoomMode.Vertical ) {
			double y = mouseEvent.getY();
			y = Math.max( y, selectRect.getTranslateY() );
			y = Math.min( y, plotArea.getMaxY() );
			rectY.set( y );
		}
	}

	private void onMouseReleased() {
		if ( !selecting.get() )
			return;

		if ( selectRect.getWidth() == 0.0 ||
				 selectRect.getHeight() == 0.0 ) {
			selecting.set( false );
			return;
		}

		Rectangle2D zoomWindow = chartInfo.getDataCoordinates(
				selectRect.getTranslateX(), selectRect.getTranslateY(),
				rectX.get(), rectY.get()
		);

		xAxis.setAutoRanging( false );
		yAxis.setAutoRanging( false );
		if ( zoomAnimated.get() ) {
			zoomAnimation.stop();
			zoomAnimation.getKeyFrames().setAll(
					new KeyFrame( Duration.ZERO,
					              new KeyValue( xAxis.lowerBoundProperty(), xAxis.getLowerBound() ),
					              new KeyValue( xAxis.upperBoundProperty(), xAxis.getUpperBound() ),
					              new KeyValue( yAxis.lowerBoundProperty(), yAxis.getLowerBound() ),
					              new KeyValue( yAxis.upperBoundProperty(), yAxis.getUpperBound() )
					),
			    new KeyFrame( Duration.millis( zoomDurationMillis.get() ),
			                  new KeyValue( xAxis.lowerBoundProperty(), zoomWindow.getMinX() ),
			                  new KeyValue( xAxis.upperBoundProperty(), zoomWindow.getMaxX() ),
			                  new KeyValue( yAxis.lowerBoundProperty(), zoomWindow.getMinY() ),
			                  new KeyValue( yAxis.upperBoundProperty(), zoomWindow.getMaxY() )
			    )
			);
			zoomAnimation.play();
		} else {
			zoomAnimation.stop();
			xAxis.setLowerBound( zoomWindow.getMinX() );
			xAxis.setUpperBound( zoomWindow.getMaxX() );
			yAxis.setLowerBound( zoomWindow.getMinY() );
			yAxis.setUpperBound( zoomWindow.getMaxY() );
		}

		selecting.set( false );
	}

	private static double getBalance( double val, double min, double max ) {
		if ( val <= min )
			return 0.0;
		else if ( val >= max )
			return 1.0;

		return (val - min) / (max - min);
	}

	private class MouseWheelZoomHandler implements EventHandler<ScrollEvent> {
		private boolean ignoring = false;

		@Override
		public void handle( ScrollEvent event ) {
			EventType<? extends Event> eventType = event.getEventType();
			if ( eventType == ScrollEvent.SCROLL_STARTED ) {
				ignoring = true;
			} else if ( eventType == ScrollEvent.SCROLL_FINISHED ) {
				ignoring = false;

			} else if ( eventType == ScrollEvent.SCROLL &&
			            mouseWheelZoomAllowed.get() &&
			            !ignoring &&
			            !event.isInertia() &&
			            event.getDeltaY() != 0 &&
			            event.getTouchCount() == 0 ) {

				zoomAnimation.stop();

				ZoomMode zoomMode;
				double eventX = event.getX();
				double eventY = event.getY();
				if ( chartInfo.getXAxisArea().contains( eventX, eventY ) ) {
					zoomMode = ZoomMode.Horizontal;
				} else if ( chartInfo.getYAxisArea().contains( eventX, eventY ) ) {
					zoomMode = ZoomMode.Vertical;
				} else {
					zoomMode = ZoomMode.Both;
				}

				Point2D dataCoords = chartInfo.getDataCoordinates( eventX, eventY );

				double xZoomBalance = getBalance( dataCoords.getX(),
				                                  xAxis.getLowerBound(), xAxis.getUpperBound() );
				double yZoomBalance = getBalance( dataCoords.getY(),
				                                  yAxis.getLowerBound(), yAxis.getUpperBound() );

				double direction = -Math.signum( event.getDeltaY() );

				double zoomAmount = 0.2 * direction;

				if ( zoomMode == ZoomMode.Both || zoomMode == ZoomMode.Horizontal ) {
					double xZoomDelta = ( xAxis.getUpperBound() - xAxis.getLowerBound() ) * zoomAmount;
					xAxis.setAutoRanging( false );
					xAxis.setLowerBound( xAxis.getLowerBound() - xZoomDelta * xZoomBalance );
					xAxis.setUpperBound( xAxis.getUpperBound() + xZoomDelta * ( 1 - xZoomBalance ) );
				}

				if ( zoomMode == ZoomMode.Both || zoomMode == ZoomMode.Vertical ) {
					double yZoomDelta = ( yAxis.getUpperBound() - yAxis.getLowerBound() ) * zoomAmount;
					yAxis.setAutoRanging( false );
					yAxis.setLowerBound( yAxis.getLowerBound() - yZoomDelta * yZoomBalance );
					yAxis.setUpperBound( yAxis.getUpperBound() + yZoomDelta * ( 1 - yZoomBalance ) );
				}
			}
		}
	}
}
