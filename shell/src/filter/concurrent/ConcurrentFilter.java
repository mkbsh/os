package cs131.pa1.filter.concurrent;

import java.util.LinkedList;
import java.util.concurrent.LinkedBlockingQueue;

import cs131.pa1.filter.Filter;


public abstract class ConcurrentFilter extends Filter implements Runnable {

	@Override
	public void setPrevFilter(Filter prevFilter) {
		prevFilter.setNextFilter(this);
	}
	
	@Override
	public void setNextFilter(Filter nextFilter) {
		if (nextFilter instanceof ConcurrentFilter){
			ConcurrentFilter sequentialNext = (ConcurrentFilter) nextFilter;
			this.next = sequentialNext;
			sequentialNext.prev = this;
			if (this.output == null){
				this.output = new LinkedBlockingQueue<String>();
			}
			sequentialNext.input = this.output;
		} else {
			throw new RuntimeException("Should not attempt to link dissimilar filter types.");
		}
		
	}
	
	public void run(){
		String line = null;
		while (!isDone()){
			try {
	            line = input.take();
	            // We have reached the end of the output queue.
	            if (line.equals(POISON_PILL)) break;
	            processLine(line);
	        } catch (Exception e) {
	            e.printStackTrace();
	        }			
		}
		
		// This encapsulates the general logic that is shared across all filters.
		// If the previous thread has completed, set the status of this thread to FINISHED.
		// If the previous thread has failed, set to TERMINATED.
		if (prev == null || prev.getStatus() == ThreadStatus.FINISHED) {
			finish();
		} else {
			terminate();
		}
		
	}

	private void processLine(String line) {
		if (next == null) {
			System.out.println(line);
		}
		else { output.add(line); }
	}

	protected void finish() {
		status = ThreadStatus.FINISHED;
		output.add(POISON_PILL);
	}
	
	protected void terminate() {
		status = ThreadStatus.TERMINATED;
		output.add(POISON_PILL);
	}

	@Override
	public boolean isDone() {
		// If producer has executed to completion and there exists no other inputs to consume.
		return (prev == null || prev.getStatus() != ThreadStatus.RUNNING) && input.isEmpty();
	}
		
}
