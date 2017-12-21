package cs131.pa1.filter.concurrent;

import cs131.pa1.filter.Message;

import java.util.*;

public class ConcurrentREPL {
	final static String rootDirectory = System.getProperty("user.dir");
	
	public static String currentWorkingDirectory;
	// List of commands for background threads.
	private static List<String> threads = new ArrayList<String>();
	public static void main(String[] args) {
		currentWorkingDirectory = rootDirectory;
		Scanner scanner = new Scanner(System.in);
		System.out.print(Message.WELCOME.toString());
		while (true) {
			System.out.print(Message.NEWCOMMAND);
			if (!scanner.hasNextLine())
				continue;
			String command = scanner.nextLine();
			if (command.equals("exit"))
				break;

			if (command.equals("repl_jobs")) {
				printThreads();
				continue;
			}
			if (command.trim().endsWith("&")) {
				// Add it to the current list of threads.
				threads.add(command);
				command = command.trim().substring(0, command.length() - 1);
			} else {
				// Exit out of background mode.
				threads.clear();
			}
			ConcurrentFilter filter = ConcurrentCommandBuilder.createFiltersFromCommand(command);

			Thread t = null;
			while (filter != null) {
				t = new Thread(filter);
				try {
					t.start(); // Start thread.
					filter = (ConcurrentFilter) filter.getNext();
				} catch (Exception e) {
					break;
				}
			}

			if (!command.endsWith("&")) {
				try {
					// Thread t is set as the last filter. Do not proceed
					// with execution until t is done.
					if (t != null)
						t.join();
				} catch (InterruptedException e) {
					System.out.println("Child thread failed to complete.");
				}
			}

		}
		scanner.close();
		System.out.print(Message.GOODBYE);

	}

	private static void printThreads() {
		for (int i = 0; i < threads.size(); ++i) {
			System.out.println("\t" + (i + 1) + ". " + threads.get(i));
		}
	}

}
