# CPU Scheduling Simulator  
### SJF vs Priority Scheduling Comparison Tool
 Project Description

This project is a Java-based CPU Scheduling Simulator developed to compare the performance of two important CPU scheduling algorithms:

- Shortest Job First (SJF)
- Priority Scheduling

The simulator supports both:
- Preemptive Scheduling
- Non-Preemptive Scheduling

The system provides an interactive graphical user interface (GUI) that allows users to:
- Add and manage processes
- Run scheduling simulations
- Visualize execution using Gantt Charts
- Compare scheduling performance metrics
- Generate performance analysis and final recommendations

This project demonstrates Operating Systems scheduling concepts and highlights the trade-off between efficiency-focused and urgency-focused scheduling techniques.

---
 Features
- Add multiple processes with:
  - Process ID
  - Arrival Time
  - Burst Time
  - Priority
- Support for:
  - SJF (Non-Preemptive & Preemptive)
  - Priority Scheduling (Non-Preemptive & Preemptive)
- Gantt Chart visualization for both algorithms
- Performance metrics:
  - Waiting Time
  - Turnaround Time
  - Response Time
- Automatic comparison between algorithms
- Final conclusion analysis
- Validation for incorrect inputs
- Predefined test scenarios

---

 Algorithms Implemented

### 1. SJF (Shortest Job First)
- Selects process with shortest burst time
- Two versions:
  - Non-Preemptive
  - Preemptive (SRTF)

### 2. Priority Scheduling
- Selects process with highest priority (lowest number = highest priority)
- Two versions:
  - Non-Preemptive
  - Preemptive

---

 Output Features

- Gantt Chart visualization for each algorithm
- Metrics table showing:
  - Completion Time
  - Waiting Time
  - Turnaround Time
  - Response Time
- Comparison Table between SJF and Priority
- Final recommendation based on performance

---
src/

├── model/

│ └── process.java

├── scheduler/

│ └── algorithm.java

├── gui/

│ └── gui.java

├── util/ (optional)

├── metrics/ (optional)


---

 How to Run

1. Open the project in IntelliJ IDEA or Eclipse
2. Ensure Java 8+ is installed
3. Run the main class:
```

gui.gui

```
4. Add processes and click "Run Simulation"

---

 Screenshots

Add screenshots in the `/screenshots` folder:

- Input screen
- Gantt Chart output
- Metrics table
- Final analysis

---

 Sample Test Cases

Located in `/test-cases`

Example:
| PID | Arrival | Burst | Priority |
|-----|--------|-------|----------|
| 1   | 0      | 5     | 2        |
| 2   | 1      | 3     | 1        |
| 3   | 2      | 4     | 3        |

---
 Technologies Used
- Java
- Swing (GUI)
- OOP Principles
- Data Structures (ArrayList)

---

 Team Information
- Team Number: [106]
- Students:
-Sondos Saber  20240446

-Asmaa Ayish Sayed  20240136

-shahd ayman shahat     20240483

-Nada ahmed   20241046

-Nouran khaled 20241072

-sabrin yasser 20240492

-ى
-ى
-ىشيش
 - ىشي
---

 Notes
- The simulator assumes all inputs are valid integers after validation.
- Lower priority number means higher priority.
- CPU idle time is handled in both algorithms.

---

 Future Improvements
- Add Round Robin algorithm
- Export results to Excel/PDF
- Dark mode UI
- Animated Gantt chart
