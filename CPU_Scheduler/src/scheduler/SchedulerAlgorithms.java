package scheduler;

import java.util.*;
import model.Process;

public class SchedulerAlgorithms {

    public static ArrayList<String[]> sjfTimeline = new ArrayList<>();
    public static ArrayList<String[]> priorityTimeline = new ArrayList<>();
    
public static ArrayList<Process> sjfscheduling(ArrayList<Process> list) {

    int time = 0, completed = 0, n = list.size();

    while (completed < n) {

        Process best = null;

        for (Process p : list) {

            if (!p.finished && p.arrival <= time) {

                if (best == null
                        || p.burst < best.burst
                        || (p.burst == best.burst && p.arrival < best.arrival)) {

                    best = p;
                }
            }
        }

        if (best == null) {
            time++;
            continue;
        }

        if (!best.started) {

            best.response = time - best.arrival;
            best.startTime = time;
            best.started = true;
        }

        time += best.burst;

        best.completion = time;
        best.turnaround = best.completion - best.arrival;
        best.waiting = best.turnaround - best.burst;

        best.finished = true;
        completed++;
    }

    return list;
}
public static ArrayList<Process> sjfschedulingPreemptive(ArrayList<Process> list) {

    for (Process p : list)
        p.remaining = p.burst;

    sjfTimeline.clear();

    int time = 0, completed = 0, n = list.size();

    String lastRunning = null;
    int segStart = 0;

    while (completed < n) {

        Process best = null;

        for (Process p : list) {

            if (!p.finished && p.arrival <= time) {

                if (best == null
                        || p.remaining < best.remaining
                        || (p.remaining == best.remaining && p.arrival < best.arrival)) {

                    best = p;
                }
            }
        }

        if (best == null) {

            if (lastRunning != null) {

                sjfTimeline.add(new String[]{
                        lastRunning,
                        String.valueOf(segStart),
                        String.valueOf(time)
                });

                lastRunning = null;
            }

            time++;
            continue;
        }

        if (!best.name.equals(lastRunning)) {

            if (lastRunning != null) {

                sjfTimeline.add(new String[]{
                        lastRunning,
                        String.valueOf(segStart),
                        String.valueOf(time)
                });
            }

            lastRunning = best.name;
            segStart = time;
        }

        if (!best.started) {

            best.response = time - best.arrival;
            best.started = true;
        }

        best.remaining--;
        time++;

        if (best.remaining == 0) {

            best.completion = time;
            best.turnaround = best.completion - best.arrival;
            best.waiting = best.turnaround - best.burst;

            best.finished = true;
            completed++;
        }
    }

    if (lastRunning != null) {

        sjfTimeline.add(new String[]{
                lastRunning,
                String.valueOf(segStart),
                String.valueOf(time)
        });
    }

    return list;
}
public static ArrayList<Process> priorityschedulingNonPreemptive(ArrayList<Process> list) {

    int time = 0, completed = 0, n = list.size();

    while (completed < n) {

        Process best = null;

        for (Process p : list) {

            if (!p.finished && p.arrival <= time) {

                if (best == null
                        || p.priority < best.priority
                        || (p.priority == best.priority && p.arrival < best.arrival)) {

                    best = p;
                }
            }
        }

        if (best == null) {
            time++;
            continue;
        }

        if (!best.started) {

            best.response = time - best.arrival;
            best.startTime = time;

            best.started = true;
        }

        time += best.burst;

        best.completion = time;
        best.turnaround = best.completion - best.arrival;
        best.waiting = best.turnaround - best.burst;

        best.finished = true;
        completed++;
    }

    return list;
}
public static ArrayList<Process> priorityscheduling(ArrayList<Process> list) {

    for (Process p : list)
        p.remaining = p.burst;

    priorityTimeline.clear();

    int time = 0, completed = 0, n = list.size();

    String lastRunning = null;
    int segStart = 0;

    while (completed < n) {

        Process best = null;

        for (Process p : list) {

            if (!p.finished && p.arrival <= time) {

                if (best == null
                        || p.priority < best.priority
                        || (p.priority == best.priority && p.arrival < best.arrival)) {

                    best = p;
                }
            }
        }

        if (best == null) {

            if (lastRunning != null) {

                priorityTimeline.add(new String[]{
                        lastRunning,
                        String.valueOf(segStart),
                        String.valueOf(time)
                });

                lastRunning = null;
            }

            time++;
            continue;
        }

        if (!best.name.equals(lastRunning)) {

            if (lastRunning != null) {

                priorityTimeline.add(new String[]{
                        lastRunning,
                        String.valueOf(segStart),
                        String.valueOf(time)
                });
            }

            lastRunning = best.name;
            segStart = time;
        }

        if (!best.started) {

            best.response = time - best.arrival;
            best.started = true;
        }

        best.remaining--;
        time++;

        if (best.remaining == 0) {

            best.completion = time;
            best.turnaround = best.completion - best.arrival;
            best.waiting = best.turnaround - best.burst;

            best.finished = true;
            completed++;
        }
    }

    if (lastRunning != null) {

        priorityTimeline.add(new String[]{
                lastRunning,
                String.valueOf(segStart),
                String.valueOf(time)
        });
    }

    return list;
}
}
    
