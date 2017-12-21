package cs131.pa1.filter.concurrent;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import cs131.pa1.filter.Message;

public class Cat extends ConcurrentFilter implements Runnable {
	private String command;

	public Cat(String command) {
		this.command = command.trim();
	}
	
	public String getCommand() {
		return command;
	}
	
	public void run() {
		String[] tokenized = command.split(" ");
		if (tokenized.length < 2) {
			System.out.print(Message.REQUIRES_PARAMETER.with_parameter(command));
			terminate();
			throw new IllegalArgumentException();
		}
		
		Path path = null;
		for (int i = 1; i < tokenized.length; ++i) {
			path = Paths.get(ConcurrentREPL.currentWorkingDirectory + ConcurrentFilter.FILE_SEPARATOR + tokenized[i]);
			if (Files.notExists(path)) {
				System.out.print(Message.FILE_NOT_FOUND.with_parameter(command));
				terminate();
				throw new IllegalArgumentException();
			}
	        try (Stream<String> lines = Files.lines(path)) {
	            lines.forEach(s -> output.add(s));
	        } catch (IOException ex) {}
		}
		
		finish();
	}

}
