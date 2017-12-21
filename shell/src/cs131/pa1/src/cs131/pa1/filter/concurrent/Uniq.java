package cs131.pa1.filter.concurrent;

import java.util.HashSet;
import java.util.Set;

import cs131.pa1.filter.Message;

public class Uniq extends ConcurrentFilter {
	private final String command;
	public Uniq(String command) {
		this.command = command.trim();
	}

	@Override
	public void run() {
		if (!command.equals("uniq")) {
			System.out.println(Message.INVALID_PARAMETER.with_parameter(command));
			terminate();
			throw new IllegalArgumentException();
			
		}
		Set<String> seen = new HashSet<String>();
		
		String line = null;
		while (!isDone()){
			try {
	            line = input.take();
	            if (line.equals(POISON_PILL)) {
	            	if (next != null) break;
	            }
	            if (seen.contains(line)) continue;
	            output.add(line);
	            seen.add(line);
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
