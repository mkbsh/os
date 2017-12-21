package cs131.pa1.filter.concurrent;

import cs131.pa1.filter.Message;

public class Pwd extends ConcurrentFilter {
	private final String command;
	
	public Pwd(String command) {
		this.command = command.trim();
	}

	@Override
	public void run() {
		if (!command.equals("pwd")) {
			System.out.println(Message.INVALID_PARAMETER.with_parameter(command));
			terminate();
			throw new IllegalArgumentException();
		}
		output.add(ConcurrentREPL.currentWorkingDirectory);
		finish();
	}

}
