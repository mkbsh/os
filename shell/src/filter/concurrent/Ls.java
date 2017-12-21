package cs131.pa1.filter.concurrent;

import java.io.File;

import cs131.pa1.filter.Message;

public class Ls extends ConcurrentFilter {
	private final String command;

	public Ls(String command) {
		this.command = command.trim();
	}

	@Override
	public void run() {
		if (!command.equals("ls")) {
			System.out.print(Message.INVALID_PARAMETER.with_parameter(command));
			terminate();
			throw new IllegalArgumentException();
		}
		File folder = new File(ConcurrentREPL.currentWorkingDirectory);
		File[] listOfFiles = folder.listFiles();
		for (File f : listOfFiles) {
			output.add(f.getName());
		}
		finish();
	}

}
