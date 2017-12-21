package cs131.pa1.filter;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import cs131.pa1.filter.concurrent.ThreadStatus;

public abstract class Filter {
    
	public static final String FILE_SEPARATOR = System.getProperty("file.separator");
	protected Filter next;
	protected Filter prev;

	public LinkedBlockingQueue<String> input;
	public LinkedBlockingQueue<String> output;
	
	// Motivation for POISON_PILL:
	// The loop where the consumer listens for inputs can have a straightforward condition such as:
	// 
	// 		if (previous thread is still running || input queue is not empty)
	//
	// However, this introduces a race condition when the previous thread does not produce any output.
	// We enter the loop when the previous thread is still running, and keep waiting but nothing will ever come.
	// Therefore, the previous thread should produce a dummy "poison pill" which should be the last thing that the consumer takes,
	// prompting the consumer to stop listening.
	protected final String POISON_PILL = new String(); 

	// Motivation for ThreadStatus:
	// Consider these two scenarios that would present the same "no output" to the next thread.
	// In each case,  only the poison pill will be inserted in the queue, but it should be handled differently, as specified below.
	//
	// (1) No output but valid: Word count an empty file => Display 0s.
	// (2) No output due to exception: Word count a nonexistent file => Display an error message.
	//
	// Therefore, once the previous thread has stopped running, we need to signal whether the previous thread stopped running
	// due to an interrupt or successful run. We introduce an enum ThreadStatus. A thread undergoes 3 possible states in its lifetime.
	//
	// (1) All threads are initialized to RUNNING upon creation.
	// (2) If a thread encounters an exception, set status to TERMINATED, and insert poison pill.
	// (3) If a thread goes to completion, insert output and set status to FINISHED.
	//
	// In the case of an error, only the poison pill will be inserted, ensuring that no output will be produced in the subsequent
	// commands either. The error status will be propagated i.e. if a previous thread was TERMINATED, the current thread will also
	// signal TERMINATED.
	protected ThreadStatus status = ThreadStatus.RUNNING;
			
	public abstract void setNextFilter(Filter next);
	
	public abstract void setPrevFilter(Filter next);
	
	public Filter getNext() { return next;}
	
	public Filter getPrev() { return prev;}
	
	public ThreadStatus getStatus() {return status;};
	
	public abstract boolean isDone();
	
}
