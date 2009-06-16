package ca.wlu.gisql.cytoscape;

import org.apache.log4j.Appender;
import org.apache.log4j.Layout;
import org.apache.log4j.Level;
import org.apache.log4j.helpers.OnlyOnceErrorHandler;
import org.apache.log4j.spi.ErrorHandler;
import org.apache.log4j.spi.Filter;
import org.apache.log4j.spi.LoggingEvent;

import cytoscape.logger.CyLogger;
import cytoscape.logger.LogLevel;

public class LoggingAdapter implements Appender {

	private ErrorHandler errorHandler = new OnlyOnceErrorHandler();

	private Filter headFilter = null;

	private Layout layout;

	private String name = this.toString();

	private Filter tailFilter = null;

	public void addFilter(Filter filter) {
		if (headFilter == null) {
			headFilter = tailFilter = filter;
		} else {
			tailFilter.setNext(filter);
			tailFilter = filter;
		}

	}

	public void clearFilters() {
		headFilter = tailFilter = null;
	}

	public void close() {
	}

	private LogLevel convertLevel(Level level) {
		if (level == Level.FATAL)
			return LogLevel.LOG_FATAL;
		if (level == Level.ERROR)
			return LogLevel.LOG_ERROR;
		if (level == Level.WARN)
			return LogLevel.LOG_WARN;
		if (level == Level.INFO)
			return LogLevel.LOG_INFO;
		if (level == Level.DEBUG)
			return LogLevel.LOG_DEBUG;
		if (level == Level.TRACE)
			return LogLevel.LOG_INFO;
		return LogLevel.LOG_FATAL;
	}

	public void doAppend(LoggingEvent event) {
		Filter filter = this.headFilter;

		FILTER_LOOP: while (filter != null) {
			switch (filter.decide(event)) {
			case Filter.DENY:
				return;
			case Filter.ACCEPT:
				break FILTER_LOOP;
			case Filter.NEUTRAL:
				filter = filter.getNext();
			}
		}

		CyLogger log = CyLogger.getLogger(event.getClass());
		log.log(event.getMessage().toString(), convertLevel(event.getLevel()));
	}

	public ErrorHandler getErrorHandler() {
		return errorHandler;
	}

	public Filter getFilter() {
		return headFilter;
	}

	public Layout getLayout() {
		return layout;
	}

	public String getName() {
		return name;
	}

	public boolean requiresLayout() {
		return false;
	}

	public void setErrorHandler(ErrorHandler errorHandler) {
		if (errorHandler != null)
			this.errorHandler = errorHandler;
	}

	public void setLayout(Layout layout) {
		this.layout = layout;
	}

	public void setName(String name) {
		this.name = name;
	}

}
