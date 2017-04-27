package com.jasonwjones.pbcs.cli.command;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import com.beust.jcommander.IStringConverter;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

@Parameters(separators = "=")
public class MainCommand {

	@Parameter(names = {"--conn-properties", "-C"}, converter = PropertiesConverter.class, required = false)
	private Properties connectionProperties;

	@Parameter(names = {"--debug-logging"}, required = false)
	private boolean debugLogging = false;

	@Parameter(names = {"--no-logging"}, required = false)
	private boolean loggingOff = false;
	
	public Properties getConfigFile() {
		return connectionProperties;
	}

	public void setConfigFile(Properties configFile) {
		this.connectionProperties = configFile;
	}

	public boolean isDebugLogging() {
		return debugLogging;
	}

	public void setDebugLogging(boolean debugLogging) {
		this.debugLogging = debugLogging;
	}

	public boolean isLoggingOff() {
		return loggingOff;
	}

	public void setLoggingOff(boolean loggingOff) {
		this.loggingOff = loggingOff;
	}



	public static class PropertiesConverter implements IStringConverter<Properties> {
		@Override
		public Properties convert(String value) {
			try {
				File propFile = new File(value);
				FileInputStream fis = new FileInputStream(propFile);
				Properties properties = new Properties();
				properties.load(fis);
				fis.close();
				return properties;
			} catch (IOException e) {
				throw new RuntimeException("Could not read properties file", e);
			}
		}

	}

}
