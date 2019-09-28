
/**
 * @author Nick Mirasol
 * Decided to make my own logger formatting to remove pesky INFO:
 * tags that were in front of each logger.info() log.
 */

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
		{
			System.out.println(record.getMessage());
			return record.getMessage() + "\r\n";
		}

		return super.format(record);
	}
}
