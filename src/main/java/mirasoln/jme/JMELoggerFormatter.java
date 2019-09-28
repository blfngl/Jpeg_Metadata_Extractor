package mirasoln.jme;

import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;

public class JMELoggerFormatter extends SimpleFormatter
{
	@Override
	public String format(LogRecord record)
	{
		if (record.getLevel() == Level.INFO)
			return record.getMessage() + "\r\n";

		return super.format(record);
	}
}
