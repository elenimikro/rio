package org.coode.rio.cli;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class Cli {

	public static void main(String[] args) throws ParseException {
		// create Options object
		Options options = new Options();
		
		// add t option
		options.addOption("t", false, "display current time");
		Option logfile   = Option.builder("cf")
                .argName("configfile")
                .hasArg()
                .desc("Config file for Genome Store")
                .build();
		options.addOption(logfile);
		
		CommandLineParser parser = new DefaultParser();
		CommandLine cmd = parser.parse( options, args);
		
		// automatically generate the help statement
	    HelpFormatter formatter = new HelpFormatter();
	    formatter.printHelp("rio", options );
	}
	
}
