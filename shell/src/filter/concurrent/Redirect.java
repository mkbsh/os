package cs131.pa1.filter.concurrent;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import cs131.pa1.filter.Message;

public class Redirect extends ConcurrentFilter {
	private final String command;
	public Redirect(String command) {
		this.command = command.trim();
	}
	
	@Override
	public void run() {
		String[] tokenized = command.split(" ");
		if (tokenized.length != 2) {
			System.out.print(Message.REQUIRES_PARAMETER.with_parameter(command));
			throw new IllegalArgumentException();
		}
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter (ConcurrentREPL.currentWorkingDirectory + ConcurrentFilter.FILE_SEPARATOR + tokenized[1]));			
			String line = null;
			while (!isDone()){
				try {
		            line = input.take();
		            if (line.equals(POISON_PILL)) {
		            	break;
		            }
		            writer.write(line + "\n");
		        } catch (Exception e) {
		            e.printStackTrace();
		        }			
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
