package com.algolia.firebase.connector;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;


public class App 
{
	public static final Logger logger = Logger.getLogger("firebase-connector");
	
	
	public static final String CONF_URL = "url";
	public static final String CONF_TO = "to";
	public static final String CONF_DOCMANAGER_PARAM = "param";
	public static final String CONF_MAPPING = "mapping";
	public static final String CONF_INITIAL_IMPORT = "initialImport";
	public static final String CONF_COMMIT_DELAY = "commitDelay";
	public static final String CONF_HELP = "help";
	
	public static final Options options = new Options();
	public static final HashMap<String, String> configuration = new HashMap<String, String>();
	
	static {
		options.addOption(null, CONF_HELP, true, "the help");
		options.addOption(null, CONF_URL, true, "firebase url");
		options.addOption(null, CONF_TO, true, "url for the doc manager");
		options.addOption(null, CONF_MAPPING, true, "mapping for the doc manager");
		options.addOption(null, CONF_INITIAL_IMPORT, false, "begin with a initial import");
		options.addOption(null, CONF_COMMIT_DELAY, true, "delay between two commit");
		options.addOption(null, CONF_DOCMANAGER_PARAM, true, "extra parametter for the doc manager");
	}
	
	public static String getFirebaseURL() {
		return configuration.get(CONF_URL);
	}
	
	public static String getDocManagerUrl() {
		return configuration.get(CONF_TO);
	}
	
	public static String getDocManagerParam() {
		return configuration.get(CONF_DOCMANAGER_PARAM);
	}
	
	public static String getMapping() {
		return configuration.get(CONF_MAPPING);
	}
	
	private static void usage(int exitCode) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.setWidth(160);
        formatter.printHelp("LogProcessor [option]...", options);
        System.exit(exitCode);
    }
	
	private static void initializeConfVar(CommandLine cli) {
		configuration.put(CONF_URL, cli.getOptionValue(CONF_URL));
		configuration.put(CONF_TO, cli.getOptionValue(CONF_TO));
		configuration.put(CONF_MAPPING, cli.getOptionValue(CONF_MAPPING));
		configuration.put(CONF_INITIAL_IMPORT, cli.hasOption(CONF_INITIAL_IMPORT) ? "true" : null);
		configuration.put(CONF_COMMIT_DELAY, cli.getOptionValue(CONF_COMMIT_DELAY) != null ? cli.getOptionValue(CONF_COMMIT_DELAY) : "5");
		configuration.put(CONF_DOCMANAGER_PARAM, cli.getOptionValue(CONF_DOCMANAGER_PARAM));
	}
	
    public static void main( String[] args )
    {
    	boolean running = true;
    	CommandLine cli = null;
    	logger.info("Start");
    	try {
            java.util.logging.LogManager.getLogManager().readConfiguration(App.class.getClassLoader().getResourceAsStream("logging.properties"));
        } catch (SecurityException | IOException e) {
            throw new IllegalStateException(e);
        }
    	try {
			cli = new BasicParser().parse(options, args, false);
			String[] unparsedTargets = cli.getArgs();
			if (unparsedTargets.length > 0 || cli.hasOption(CONF_HELP)) {
				usage(1);
			}
		} catch (org.apache.commons.cli.ParseException e) {
			usage(1);
		}
    	System.setProperty("file.encoding", "UTF-8");
    	System.setProperty("client.encoding.override", "UTF-8");
        
    	initializeConfVar(cli);
    	
    	logger.info("Initialize");
    	Long delay = Long.parseLong(configuration.get(CONF_COMMIT_DELAY));
    	Connector connector = new Connector();
    	if (configuration.get(CONF_INITIAL_IMPORT) != null) {
    		connector.initialImport();
    	}
    	connector.listen();
    	while (running) {
    		try {
				Thread.sleep(delay * TimeUnit.MILLISECONDS.convert(1, TimeUnit.SECONDS));
			} catch (InterruptedException e) {
				// Not fatal
			}
    		connector.commit();
    	}
    	logger.info("Stop");
    }
}
