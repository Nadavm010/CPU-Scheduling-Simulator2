package model;

public class Process {

    public String name;
    public int arrival, burst, priority;

    public int completion;
    public int waiting;
    public int turnaround;
    public int response;

    public int remaining;

    public boolean finished;
    public boolean started = false;

    public int startTime = -1;

    public Process(String name, int arrival, int burst, int priority) {

        this.name = name;
        this.arrival = arrival;
        this.burst = burst;
        this.priority = priority;

        this.remaining = burst;
    }
}
