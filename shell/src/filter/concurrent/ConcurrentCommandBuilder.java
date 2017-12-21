package cs131.pa1.filter.concurrent;

import cs131.pa1.filter.Message;

public class ConcurrentCommandBuilder {

	public static ConcurrentFilter createFiltersFromCommand(String command) {
		String[] subcommands = command.split("(?=>)|\\|");
		ConcurrentFilter previous = null;
		ConcurrentFilter head = null;
		String prev_command = "";
		for (int i = 0; i < subcommands.length; ++i) {
			String trimmed = subcommands[i].trim();
			ConcurrentFilter filter = constructFilterFromSubCommand(trimmed);
			if (filter == null)
				return null; // Faulty command.
			String error_or_null = checkPiping(previous, filter, prev_command, subcommands[i]);
			if (error_or_null != null) {
				System.out.print(error_or_null);
				return null;
			}
			if (previous == null) {
				head = filter;
			} else {
				previous.setNextFilter(filter); // Link.
			}
			previous = filter;
			prev_command = subcommands[i];
		}
		if (! (previous instanceof Redirect || previous instanceof Cd)) {
			previous.setNextFilter(new Print());
		}

		return head;
	}

	// Sanity check on piping.
	private static String checkPiping(ConcurrentFilter previous, ConcurrentFilter current, String prev_command, String current_command) {
		if (previous != null && (current instanceof Pwd || current instanceof Ls || current instanceof Cd
				|| current instanceof Cat)) {
			return Message.CANNOT_HAVE_INPUT.with_parameter(current_command);
		}
		if ((previous instanceof Cd || previous instanceof Redirect) && current != null) {
			return Message.CANNOT_HAVE_OUTPUT.with_parameter(prev_command);
		}
		if (previous == null && (current instanceof Grep || current instanceof Wc || current instanceof Uniq
				|| current instanceof Redirect)) {
			return Message.REQUIRES_INPUT.with_parameter(current_command);
		}
		return null;
	}

	private static ConcurrentFilter constructFilterFromSubCommand(String subCommand) {
		ConcurrentFilter filter = null;
		String[] tokenized = subCommand.split(" ");
		if (tokenized.length < 1) {// Handle empty input.
			System.out.print(Message.COMMAND_NOT_FOUND.with_parameter(subCommand));
		}

		switch (tokenized[0]) {
		case "ls":
			filter = new Ls(subCommand);
			break;
		case "pwd":
			filter = new Pwd(subCommand);
			break;
		case "grep":
			filter = new Grep(subCommand);
			break;
		case "wc":
			filter = new Wc(subCommand);
			break;
		case "cat":
			filter = new Cat(subCommand);
			break;
		case "cd":
			filter = new Cd(subCommand);
			break;
		case ">":
			filter = new Redirect(subCommand);
			break;
		case "uniq":
			filter = new Uniq(subCommand);
			break;
		default:
			System.out.print(Message.COMMAND_NOT_FOUND.with_parameter(subCommand));
			return null;
		}

		return filter;
	}
}
