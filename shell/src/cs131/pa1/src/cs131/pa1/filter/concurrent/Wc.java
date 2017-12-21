package cs131.pa1.filter.concurrent;

import cs131.pa1.filter.Message;

public class Wc extends ConcurrentFilter {
	private final String command;
	
	public Wc(String command) {
		this.command = command.trim();
		// TODO Auto-generated constructor stub
	}

	@Override
	public void run() {
		if (!command.equals("wc")) {
			System.out.println(Message.REQUIRES_PARAMETER.with_parameter(command));
			terminate();
			throw new IllegalArgumentException();
		}
		
		int line_count = 0;
		int char_count = 0;
		int word_count = 0;
		
		String line = null;
		while (!isDone()){
			try {
	            line = input.take();
	            if (line.equals(POISON_PILL)) break;
	            line_count++;
	            char_count += line.length();
				word_count += line.split(" ").length;
	        } catch (Exception e) {
	            e.printStackTrace();
	        }			
		}
		if (prev.getStatus() == ThreadStatus.FINISHED) {
			output.add(line_count + " " + word_count + " " + char_count);
			finish();
		} else {
			terminate();
		}
		
		
	}
}
