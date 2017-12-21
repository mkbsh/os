package cs131.pa1.filter.concurrent;

import java.io.File;

import cs131.pa1.filter.Message;

public class Cd extends ConcurrentFilter {
	private final String command;	
	
	public Cd(String command) {
		this.command = command.trim();
	}
	
	@Override
	public void run() {
		String[] tokenized = command.split(" ");		
		if (tokenized.length == 1) {
			System.out.print(Message.REQUIRES_PARAMETER.with_parameter(command));
			throw new IllegalArgumentException();
		} else if (tokenized.length > 2) {
			System.out.println(Message.DIRECTORY_NOT_FOUND.with_parameter(command));
			throw new IllegalArgumentException();
		}
		String new_dir = ConcurrentREPL.currentWorkingDirectory;

		if (tokenized[1].equals("..")) {
			int last_slash = ConcurrentREPL.currentWorkingDirectory.lastIndexOf(ConcurrentFilter.FILE_SEPARATOR);
			new_dir = ConcurrentREPL.currentWorkingDirectory.substring(0, last_slash);
		} else if (!tokenized[1].equals(".")) {
			new_dir = ConcurrentREPL.currentWorkingDirectory + ConcurrentFilter.FILE_SEPARATOR + tokenized[1];
			File file = new File(new_dir);
			if (!file.isDirectory()) {
				System.out.print(Message.DIRECTORY_NOT_FOUND.with_parameter(command));
				throw new IllegalArgumentException();
			}
		}
					
		ConcurrentREPL.currentWorkingDirectory = new_dir;
		
	}
	
}
