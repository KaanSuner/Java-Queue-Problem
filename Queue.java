
import java.io.*;
import java.util.*;
import java.util.logging.*;

/**
 * job status per quantum time for execution type
 */
enum STATUS {
    /**
     * if this is, current job enters the queue
     */
    ENTER("enter"),
    /**
     * if this is, it prints remaining time of running job
     */
    EXPIRED("expired"),
    /**
     * if this is, current job leaves from queue
     */
    TERMINATED("terminated");

    private String name;

    private STATUS(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}

/**
 *
 * @author Kaan Suner 
 * @version
 */
public class Queue {

    /**
     * Main method
     *
     * @param args
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static void main(String[] args) throws FileNotFoundException, IOException {
        ArrayList<String> name = new ArrayList<String>();
        ArrayList<Double> arvtime = new ArrayList<Double>();
        ArrayList<Double> runtime = new ArrayList<Double>();

        try {

            String filelocation = args[0];
            //find the file with jobs data
            File jobsdata = new File(filelocation);
            Scanner jobsdataScanner = new Scanner(jobsdata);

            while (jobsdataScanner.hasNext()) {
                String nextLine = jobsdataScanner.nextLine();
                String[] jobtimes = nextLine.split(",");
                String jobsname = jobtimes[0];
                String jobsarvtime = jobtimes[1];
                String jobsruntime = jobtimes[2];

                double doublearvtime = Double.parseDouble(jobsarvtime);
                double doubleruntime = Double.parseDouble(jobsruntime);
                name.add(jobsname);
                arvtime.add(doublearvtime);
                runtime.add(doubleruntime);
            }
            Queue start = new Queue(100);
            for (int i = 0; i < name.size(); i++) {
                start.addJob(new Jobs(name.get(i), arvtime.get(i), runtime.get(i)));
            }

            start.scheduleProgram();

        } catch (FileNotFoundException ex) {
            Logger.getLogger(Queue.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * First jobs are added then program will try to execute and job queuing can
     * be done in simultaneously so that's the real time dynamic execution
     *
     * @throws IOException
     */
    public void scheduleProgram() throws IOException {

        // reading input file
        FileWriter fw = new FileWriter("output.txt");
        PrintWriter pw = new PrintWriter(fw);

        if (jobs.isEmpty()) {
            pw.println("eror");
            return;
        }

        double time = 0; // starting time
        int jobsize = jobs.size(), jobidx = 0;
        // either jobqueue exists or job list available
        while (jobidx < jobsize || !jobqueue.isEmpty()) {

            double limit = time + quantumtime;

            Jobs current = null; // selected job
            boolean executed = false;
            boolean terminate = false;

            while (jobidx < jobsize) {
                Jobs job = jobs.get(jobidx);
                double at = job.getArrivalTime();

                if (at > limit) {
                    // arrival time reached at limit
                    break;
                }

                if (current != null) {
                    double rem = current.getRemainingTime();
                    if (rem <= at - time) {
                        // execute before next job(s) come
                        // execute the job reset next time slot
                        time += rem;
                        terminate = true;
                        break;
                    }
                }

                // push into queue
                jobqueue.addJob(job);
                // job enters
                show(at, job.getName(), STATUS.ENTER, -1, pw);
                jobidx++;

                if (current == null) {
                    // selected job for execution
                    current = jobqueue.peek();
                }
            }

            // if not job selected
            if (current == null) {
                // selected job for execution
                current = jobqueue.peek();
            }

            if (!terminate && current != null) {
                double rem = current.getRemainingTime();

                if (rem <= limit - time) {
                    // when all jobs added to job queue
                    time += rem;
                    terminate = true;
                }
            }

            if (terminate) {
                // terminate the job based on enum
                // terminate job and show the status
                current.execute(current.getRemainingTime());
                jobqueue.remove(); // remove job from job long
                show(time, current.getName(), STATUS.TERMINATED, -1, pw);

                current = null; // reset
                executed = true; // mark execution enum
            }

            if (!executed) {
                current.execute(quantumtime); // use full time slice
                // move time slot
                time = limit;

                // remove and push back to queue again
                Jobs save = jobqueue.remove();
                jobqueue.addJob(save);

                // show status
                show(limit, current.getName(), STATUS.EXPIRED, current.getRemainingTime(), pw);
            }
        }
        pw.close();
    }

    /**
     * After quantum time execution for a job, job has to leave and make other
     * jobs to execute*
     */
    double quantumtime;

    /**
     * Job details
     */
    CircularQueue jobqueue = new CircularQueue();
    /**
     * this list is required First all jobs put into list then try to simulate
     */
    List<Jobs> jobs = new ArrayList<>();

    /**
     * jobs can runs at most 100 ms
     *
     * @param quantumtime
     */
    public Queue(double quantumtime) {
        this.quantumtime = quantumtime;
    }

    /**
     *
     * @param job adds job
     */
    public void addJob(Jobs job) {
        jobs.add(job);
    }

    /**
     * Print method for remaining time with customized flags. Flag expired =
     * remaining
     *
     * @param time current time
     * @param job job that has still remainin time for leaving
     * @param status enter/expired/terminated
     * @param remaining remaning time for current job
     * @param pw writing file parameter
     */

    void show(double time, String job, STATUS status, double remaining, PrintWriter pw) {
        pw.print(time + "-" + jobqueue.show() + "-" + job + " ");

        switch (status) {
            case ENTER:
                pw.print(status.getName());
                break;
            case EXPIRED:
                pw.print("is " + status.getName() + ", remaining " + remaining);
                break;
            case TERMINATED:
                pw.print("is " + status.getName());
        }
        pw.println();
    }

}

/**
 * Job class;name,arrival time, remaining time, execution time*
 */
class Jobs {

    String jobname;
    double arrivalTime;
    double executionTime;
    double remainingTime;

    /**
     * Constructor
     *
     * @param jobname name/ID of the job
     * @param arvtime first arrival time of the job
     * @param exetime active run time of the job
     */
    // constructor
    public Jobs(String jobname, double arvtime, double exetime) {
        this.jobname = jobname;
        this.arrivalTime = arvtime;
        this.executionTime = exetime;
        this.remainingTime = exetime;
    }

    /**
     *
     * @return name/ID of current job
     */
    public String getName() {
        return jobname;
    }

    /**
     *
     * @return arrival time of current job
     */
    public double getArrivalTime() {
        return arrivalTime;
    }

    /**
     *
     * @return runtime of current job
     */
    public double getExecutionTime() {
        return executionTime;
    }

    /**
     * if remaning time is greater then parameter, it updates.
     *
     * @param time
     */
    public void execute(double time) {
        if (remainingTime >= time) {
            remainingTime -= time;
        }
    }

    /**
     *
     * @return checks if execution is done
     */
    public boolean isComplete() {
        return remainingTime == 0;
    }

    /**
     *
     * @return remaining time
     */
    public double getRemainingTime() {
        return remainingTime;
    }
}

/**
 * Node class for Circular Queue*
 */

class Node {

    Jobs job;
    Node next = null;

    /**
     * Node class contructor
     *
     * @param job
     */
    public Node(Jobs job) {
        this.job = job;
    }

    /**
     *
     * @return current job
     */
    public Jobs getJob() {
        return job;
    }
}

/**
 * circular queue manages job queue*
 */
class CircularQueue {

    /**
     * only tail is required for circular because tail.next points to head
     */
    Node tail = null;

    /**
     *
     * @return queue is empty
     */

    public boolean isEmpty() {
        return tail == null;
    }

    /**
     *
     * @return prints current queue
     */
    public String show() {
        if (tail != null) {
            Node x = tail.next;
            StringBuilder queue = new StringBuilder();

            while (x != tail) {
                // until circular reference found
                queue.append(x.job.getName());
                queue.append(",");
                x = x.next;
            }
            // last one
            queue.append(x.job.getName());
            return queue.toString();
        } else {
            // queue is empty
            return "";
        }
    }

    /**
     *
     * @return gets "First Ccme First Serve" added job as well as removed
     */
    public Jobs remove() {
        if (tail == null) {
            // empty
            return null;
        } else {
            // peek and remove
            Jobs job = tail.next.job;
            if (tail.next == tail) {
                // single entry
                tail = null;
            } else {
                // multiple entries
                // remove first entry
                tail.next = tail.next.next;
            }
            return job;
        }
    }

    /**
     *
     * @param job adds this job
     */
    public void addJob(Jobs job) {
        if (tail == null) {
            // first entry
            tail = new Node(job);
            tail.next = tail; // make it circular
        } else {
            // subsequent entry
            Node head = tail.next;
            tail.next = new Node(job);
            tail = tail.next;
            tail.next = head; // make it circular
        }
    }

    /**
     *
     * @return gets "First Come First Serve" added job but not removed
     */
    public Jobs peek() {
        if (tail == null) {
            // empty
            return null;
        } else {

            return tail.next.job;
        }
    }

}
