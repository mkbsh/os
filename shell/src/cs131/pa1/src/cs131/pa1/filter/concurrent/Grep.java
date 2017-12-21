package cs131.pa1.filter.concurrent;

import cs131.pa1.filter.Message;

public class Grep extends ConcurrentFilter {
	final private String command;

	public Grep(String command) {
		this.command = command.trim();
	}

	@Override
	public void run() {
		String[] tokenized = command.split(" ");
		if (tokenized.length != 2) {
			System.out.print(Message.REQUIRES_PARAMETER.with_parameter(command));
			terminate();
			throw new IllegalArgumentException();
		}
		
		String line = null;
		while (!isDone()){
			try {
	            line = input.take();
	            if (line.equals(POISON_PILL)) { break;}
	            if (line.indexOf(tokenized[1]) != -1) output.add(line);
	        } catch (Exception e) {
	            e.printStackTrace();
	        }			
		}
		
		if (prev == null || prev.getStatus() == ThreadStatus.FINISHED) {
			finish();
		} else {
			terminate();
		}
		
	}
			
}
