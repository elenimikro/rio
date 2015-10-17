package org.coode.rio.cli;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;

import jline.console.ConsoleReader;
import jline.console.completer.ArgumentCompleter;
import jline.console.completer.Completer;
import jline.console.completer.StringsCompleter;

/**
 * Command line tool for rio
 * 
 * @author elenimikroyannidi
 *
 */
public class Shell {
	
	private String[] commandsList;

	public void init() {
		commandsList = new String[] { "help", "load", "runclustering", "exit" };
	}
	
	public void run() throws IOException {
		printWelcomeMessage();
		ConsoleReader reader = new ConsoleReader();
		reader.setBellEnabled(false);
		List<Completer> completors = new LinkedList<Completer>();
		
		// Completes using random words
		completors.add(new StringsCompleter("-o", "-c", "command"));

		completors.add(new StringsCompleter(commandsList));
		reader.addCompleter(new ArgumentCompleter(completors));

		String line;
		PrintWriter out = new PrintWriter(System.out);

		while ((line = readLine(reader, "")) != null) {
			if ("help".equals(line)) {
				printHelp();
			} else if ("load".equals(line)) {
				System.out.println("You have selection action1");
			} else if ("action2".equals(line)) {
				System.out.println("You have selection action2");
			} else if ("exit".equals(line)) {
				System.out.println("Exiting application");
				return;
			} else {
				System.out
						.println("Invalid command, For assistance press TAB or type \"help\" then hit ENTER.");
			}
			out.flush();
		}
	}

	
	private void printHelp() {
		System.out.println("help			- Show help");
		System.out.println("load			- Load an ontology");
		System.out.println("runclustering	- Execute action2");
		System.out.println("exit        	- Exit the app");
	}

	private String readLine(ConsoleReader reader, String promtMessage) throws IOException {
		String line = reader.readLine(promtMessage + "\nrio> ");
		return line.trim();
	}

	private void printWelcomeMessage() {
		System.out.println("Welcome to jLine Sample App. For assistance press TAB or type \"help\" then hit ENTER.");
	}

	public static void main(String[] args) throws IOException {
		Shell shell = new Shell();
		shell.init();
		shell.run();
	}
}
