package gui;

import scheduler.SchedulerAlgorithms;
import model.Process;

import java.util.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;


public class MainFrame extends JFrame {
 
    // [UI]
    private JTextField txtPID, txtArrival, txtBurst, txtPriority;
    private DefaultTableModel model, sjfResModel, priorityResModel;
    private JLabel lblStatus, lblSjfAvg, lblPriAvg;
    private JTextArea txtEfficiency, txtUrgency, txtFinalConclusion;
 
    // gantt chart
    private PriorityGanttPanel priorityGanttPanel;
    private SjfGanttPanel sjfGanttPanel;
 
    private JRadioButton sjfPreemptive, sjfNonPreemptive;
    private JRadioButton priPreemptive, priNonPreemptive;
 
    // [ Design]
    private final Color primaryBlue  = new Color(2, 132, 199);
    private final Color successGreen = new Color(34, 197, 94);
    private final Color dangerRed    = new Color(220, 38, 38);
    private final Color softGray     = new Color(248, 250, 252);
    private final Color defaultBorder= new Color(203, 213, 225);
    
    private JTable processTable;
    

    private double sjfAvgWT, sjfAvgTAT, sjfAvgRT;
    private double priAvgWT, priAvgTAT, priAvgRT;
    private DefaultTableModel compModel;
//============================================================= 
    
    public MainFrame(){
        setTitle("CPU Scheduling Simulator - Analysis & Control");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1400, 1000);
        getContentPane().setBackground(Color.WHITE);
        setLayout(new BorderLayout());
 
        setupHeader();
 
        JPanel mainContainer = new JPanel();
        mainContainer.setLayout(new BoxLayout(mainContainer, BoxLayout.Y_AXIS));
        mainContainer.setBackground(Color.WHITE);
        mainContainer.setBorder(BorderFactory.createEmptyBorder(0, 40, 20, 40));
 
        mainContainer.add(createInputSection());
        mainContainer.add(createScenarioButtons());
        mainContainer.add(createValidationBox());
        mainContainer.add(Box.createVerticalStrut(15));
        mainContainer.add(createTableSection());
        mainContainer.add(Box.createVerticalStrut(20));
        mainContainer.add(createControlButtons());
        mainContainer.add(Box.createVerticalStrut(30));
        mainContainer.add(createAnalysisGrid());
        mainContainer.add(Box.createVerticalStrut(25));
        mainContainer.add(createFinalConclusionSection());
 
        JScrollPane mainScroll = new JScrollPane(mainContainer);
        mainScroll.getVerticalScrollBar().setUnitIncrement(50);
        add(mainScroll, BorderLayout.CENTER);
    }
 
    private void setupHeader() {
        JPanel headerPanel = new JPanel(new GridLayout(4, 1));
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
 
        JLabel title = new JLabel("CPU Scheduling Simulator", JLabel.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 36));
 
        JLabel subTitle1 = new JLabel("Preemptive SJF & Priority Scheduling Comparison Tool", JLabel.CENTER);
        subTitle1.setForeground(Color.GRAY);
 
        JPanel subTitle2Panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        subTitle2Panel.setBackground(Color.WHITE);
        JLabel lblSjf = new JLabel("● SJF Algorithm    ");
        lblSjf.setForeground(primaryBlue);
        JLabel lblPriorityHeader = new JLabel("● Preemptive Priority");
        lblPriorityHeader.setForeground(successGreen);
        subTitle2Panel.add(lblSjf);
        subTitle2Panel.add(lblPriorityHeader);
 
        JLabel lblRuleHeader = new JLabel("ⓘ Ties are resolved by Arrival Time (FCFS)", JLabel.CENTER);
        lblRuleHeader.setForeground(primaryBlue);
 
        headerPanel.add(title);
        headerPanel.add(subTitle1);
        headerPanel.add(subTitle2Panel);
        headerPanel.add(lblRuleHeader);
        add(headerPanel, BorderLayout.NORTH);
    }
 
    private JPanel createInputSection() {
        JPanel p = new JPanel(new BorderLayout(10, 10));
        p.setBackground(softGray);
        p.setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(1, 0, 1, 0, new Color(226, 232, 240)),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)));
 
        JLabel lblHeader = new JLabel("+ Process Input Panel");
        lblHeader.setFont(new Font("SansSerif", Font.BOLD, 18));
        lblHeader.setForeground(primaryBlue);
 
        JPanel fieldsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        fieldsPanel.setBackground(softGray);
 
        txtPID      = createSmartTextField(false);
        txtArrival  = createSmartTextField(false);
        txtBurst    = createSmartTextField(true);
        txtPriority = createSmartTextField(false);
 
        fieldsPanel.add(new JLabel("<html>PID <font color='red'>*</font></html>"));     fieldsPanel.add(txtPID);
        fieldsPanel.add(new JLabel("<html>Arrival <font color='red'>*</font></html>")); fieldsPanel.add(txtArrival);
        fieldsPanel.add(new JLabel("<html>Burst <font color='red'>*</font></html>"));   fieldsPanel.add(txtBurst);
        fieldsPanel.add(new JLabel("<html>Priority <font color='red'>*</font></html>"));fieldsPanel.add(txtPriority);
 
        JButton btnAdd = new JButton("+ Add Process");
        btnAdd.setBackground(primaryBlue);
        btnAdd.setForeground(Color.WHITE);
        btnAdd.setOpaque(true); btnAdd.setBorderPainted(false);
        btnAdd.setFont(new Font("SansSerif", Font.BOLD, 13));
        btnAdd.addActionListener(e -> handleAdd());
        fieldsPanel.add(btnAdd);
 
        p.add(lblHeader, BorderLayout.NORTH);
        p.add(fieldsPanel, BorderLayout.CENTER);
        return p;
    }
 
    private JTextField createSmartTextField(boolean isBurstField) {
        JTextField f = new JTextField(8);
        f.setPreferredSize(new Dimension(80, 30));
        f.setBorder(new LineBorder(defaultBorder, 1));
 
        f.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e)  { check(); }
            public void removeUpdate(DocumentEvent e)  { check(); }
            public void changedUpdate(DocumentEvent e) { check(); }
 
            private void check() {
                String val = f.getText().trim();
                if (val.isEmpty()) { f.setBorder(new LineBorder(defaultBorder, 1)); return; }
                try {
                    int n = Integer.parseInt(val);
                    f.setBorder(new LineBorder((n < 0 || (isBurstField && n == 0)) ? dangerRed : successGreen, 2));
                } catch (NumberFormatException e) {
                    f.setBorder(new LineBorder(dangerRed, 2));
                }
            }
        });
        return f;
    }
 
    private JPanel createValidationBox() {
        JPanel container = new JPanel(new BorderLayout());
        container.setBackground(Color.WHITE);
        container.setMaximumSize(new Dimension(2000, 70));
 
        JLabel lblTitle = new JLabel(" VALIDATION STATUS");
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 12));
        lblTitle.setForeground(dangerRed);
 
        JPanel statusBox = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        statusBox.setBackground(new Color(252, 252, 252));
        statusBox.setBorder(new LineBorder(new Color(235, 235, 235)));
 
        lblStatus = new JLabel("ⓘ Ready to add processes...");
        lblStatus.setFont(new Font("SansSerif", Font.PLAIN, 11));
        lblStatus.setForeground(Color.GRAY);
        statusBox.add(lblStatus);
 
        container.add(lblTitle, BorderLayout.NORTH);
        container.add(statusBox, BorderLayout.CENTER);
        return container;
    }
 
    private void handleReset() {
        model.setRowCount(0);
        sjfResModel.setRowCount(0);
        priorityResModel.setRowCount(0);
        lblStatus.setText("ⓘ System Reset. Data cleared.");
        lblStatus.setForeground(Color.GRAY);
        lblSjfAvg.setText("Averages: WT: 0 | TAT: 0 | RT: 0");
        lblPriAvg.setText("Averages: WT: 0 | TAT: 0 | RT: 0");
        txtEfficiency.setText("Analysis will appear here...");
        txtUrgency.setText("Analysis will appear here...");
        txtFinalConclusion.setText("Summary...");
        priorityGanttPanel.update(new ArrayList<>(), 1);
        sjfGanttPanel.update(new ArrayList<>());
        clearInputs();
        
        compModel.setRowCount(0);
        sjfAvgWT = sjfAvgTAT = sjfAvgRT = priAvgWT = priAvgTAT = priAvgRT = 0;
 
        
    }
 
    private void handleAdd() {
        String pStr  = txtPID.getText().trim();
        String aStr  = txtArrival.getText().trim();
        String bStr  = txtBurst.getText().trim();
        String prStr = txtPriority.getText().trim();
 
        if (pStr.isEmpty() || aStr.isEmpty() || bStr.isEmpty() || prStr.isEmpty()) {
            showError("⚠ All fields are required"); return;
        }
        try {
            int pid = Integer.parseInt(pStr);
            int arr = Integer.parseInt(aStr);
            int bur = Integer.parseInt(bStr);
            int pri = Integer.parseInt(prStr);
 
            if (pid < 0 || arr < 0 || bur < 0 || pri < 0) { showError("⚠ Invalid input. Only positive integers allowed"); return; }
            if (bur == 0) { showError("⚠ Invalid Burst: Must be greater than 0"); return; }
            if (pri <= 0) { showError("⚠ Priority must be 1 or higher (1 = highest)"); return; }


            for (int i = 0; i < model.getRowCount(); i++) {
                if (Integer.parseInt(model.getValueAt(i, 0).toString()) == pid) {
                    showError("⚠ Duplicate PID! ID " + pid + " exists."); return;
                }
            }
 
            model.addRow(new Object[]{pid, arr, bur, pri});
            lblStatus.setText("ⓘ Process added successfully.");
            lblStatus.setForeground(successGreen);
            clearInputs();
 
        } catch (NumberFormatException e) {
            showError("⚠ Invalid format: Please enter positive numeric values only");
        }
    }
 
    private void showError(String msg) {
        lblStatus.setText(msg);
        lblStatus.setForeground(dangerRed);
    }
 
    private void clearInputs() {
        txtPID.setText(""); txtArrival.setText(""); txtBurst.setText(""); txtPriority.setText("");
        txtPID.setBorder(new LineBorder(defaultBorder, 1));
        txtArrival.setBorder(new LineBorder(defaultBorder, 1));
        txtBurst.setBorder(new LineBorder(defaultBorder, 1));
        txtPriority.setBorder(new LineBorder(defaultBorder, 1));
    }
 

private JPanel createAnalysisGrid() {
    JPanel grid = new JPanel(new GridLayout(2, 2, 15, 15));
    grid.setBackground(Color.WHITE);
 
    sjfGanttPanel = new SjfGanttPanel();
    grid.add(createStyledBox("SJF Gantt Chart", primaryBlue, sjfGanttPanel));
 
    priorityGanttPanel = new PriorityGanttPanel();
    grid.add(createStyledBox("Priority Gantt Chart", successGreen, new JScrollPane(priorityGanttPanel)));
 
    sjfResModel = new DefaultTableModel(new String[]{"PID","Finish","WT","TAT","RT"}, 0);
    JPanel sjfPanel = new JPanel(new BorderLayout());
    sjfPanel.add(new JScrollPane(new JTable(sjfResModel)), BorderLayout.CENTER);
  
    lblSjfAvg = new JLabel("WT: -- | TAT: -- | RT: --");
    lblSjfAvg.setForeground(Color.WHITE);

    JPanel avgBoxSJF = new JPanel(new BorderLayout());
    avgBoxSJF.setBackground(new Color(99, 102, 241)); // نفس لون Gantt
    avgBoxSJF.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
    avgBoxSJF.setPreferredSize(new Dimension(100, 45)); // ← ارتفاع أكبر

    avgBoxSJF.add(lblSjfAvg, BorderLayout.CENTER);

    sjfPanel.add(avgBoxSJF, BorderLayout.SOUTH);
    
    lblSjfAvg.setFont(new Font("SansSerif", Font.BOLD, 13));
 //==============================================================================================
    
    grid.add(createStyledBox("SJF Metrics", new Color(99, 102, 241), sjfPanel));
 
    priorityResModel = new DefaultTableModel(new String[]{"PID","Finish","WT","TAT","RT"}, 0);
    JPanel priPanel = new JPanel(new BorderLayout());
    priPanel.add(new JScrollPane(new JTable(priorityResModel)), BorderLayout.CENTER);

    grid.add(createStyledBox("Priority Metrics", dangerRed, priPanel));

    lblPriAvg = new JLabel("WT: -- | TAT: -- | RT: --");
    lblPriAvg.setForeground(Color.WHITE);
    

    JPanel avgBoxPri = new JPanel(new BorderLayout());
    avgBoxPri.setBackground(dangerRed); // نفس لون Gantt
    avgBoxPri.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
    avgBoxPri.setPreferredSize(new Dimension(100, 45)); // ارتفاع

    avgBoxPri.add(lblPriAvg, BorderLayout.CENTER);

    priPanel.add(avgBoxPri, BorderLayout.SOUTH);
    lblPriAvg.setFont(new Font("SansSerif", Font.BOLD, 13));
    
    
    //==============================================================================================
 
    // ── Wrap grid + comparison table in a vertical panel ──
    JPanel outer = new JPanel();
    outer.setLayout(new BoxLayout(outer, BoxLayout.Y_AXIS));
    outer.setBackground(Color.WHITE);
    outer.add(grid);
    outer.add(Box.createVerticalStrut(15));
    outer.add(createComparisonTable());   
    return outer;
}
 


    private JPanel createComparisonTable() {
        compModel = new DefaultTableModel(
            new String[]{"Metric", "SJF", "Priority Scheduling", "Winner"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        JTable compTable = new JTable(compModel);
        compTable.setRowHeight(30);
        compTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        compTable.getTableHeader().setBackground(new Color(248, 250, 252));
        compTable.setFont(new Font("SansSerif", Font.PLAIN, 12));

        // ── Custom renderer: color rows + winner cell ──
        compTable.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int col) {
                Component c = super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, col);

                // Alternating row background
                if (!isSelected) {
                    c.setBackground(row % 2 == 0
                        ? new Color(240, 249, 255)   // light blue rows
                        : Color.WHITE);
                }

                // Winner column: bold + colored
                if (col == 3 && value != null) {
                    String val = value.toString();
                    setFont(getFont().deriveFont(Font.BOLD));
                    if (val.contains("SJF")) {
                        setForeground(new Color(2, 132, 199));   // blue
                    } else {
                        setForeground(new Color(34, 197, 94));   // green
                    }
                } else {
                    setForeground(Color.DARK_GRAY);
                }

                // Metric column: bold
                if (col == 0) setFont(getFont().deriveFont(Font.BOLD));

                return c;
            }
        });

        JScrollPane scroll = new JScrollPane(compTable);
        scroll.setPreferredSize(new Dimension(900, 115));

        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(Color.WHITE);
        p.add(scroll, BorderLayout.CENTER);

        return createStyledBox("Comparison Table — Same Workload",
                               new Color(15, 23, 42), p);
    }

 //=======================================================================   


    
 
    private JPanel createFinalConclusionSection() {
        JPanel container = new JPanel(new BorderLayout(0, 15));
        container.setBackground(Color.WHITE);
 
        JPanel analysisGrid = new JPanel(new GridLayout(1, 2, 20, 0));
        analysisGrid.setBackground(Color.WHITE);
        txtEfficiency = createTextArea("Analysis will appear here...", 8);
        txtUrgency    = createTextArea("Analysis will appear here...", 8);
        analysisGrid.add(createStyledBox("Efficiency Focus (SJF)",      primaryBlue,  new JScrollPane(txtEfficiency)));
        analysisGrid.add(createStyledBox("Urgency Focus (Priority)",    successGreen, new JScrollPane(txtUrgency)));
 
        txtFinalConclusion = createTextArea("Summary & Final Recommendation...", 10);
        container.add(analysisGrid, BorderLayout.CENTER);
        container.add(createStyledBox("Final Conclusion & Performance Summary", new Color(15, 23, 42), new JScrollPane(txtFinalConclusion)), BorderLayout.SOUTH);
        return container;
    }
 
    private JTextArea createTextArea(String text, int rows) {
        JTextArea ta = new JTextArea(text);
        ta.setRows(rows); ta.setLineWrap(true); ta.setWrapStyleWord(true);
        ta.setFont(new Font("SansSerif", 0, 13));
        return ta;
    }
 

    private JPanel createStyledBox(String title, Color topColor, Component content) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240)));

        // ── Colored header bar (full width) ──
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(topColor);
        header.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        JLabel t = new JLabel(title);
        t.setFont(new Font("SansSerif", Font.BOLD, 13));
        t.setForeground(Color.WHITE);
        header.add(t, BorderLayout.WEST);

        p.add(header, BorderLayout.NORTH);
        p.add(content, BorderLayout.CENTER);
        return p;
    }


    private JPanel createTableSection() {
    model = new DefaultTableModel(
        new String[]{"PID", "Arrival Time", "Burst Time", "Priority"}, 0);
    processTable = new JTable(model);
    processTable.setAutoCreateRowSorter(true);
    processTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
 
    JScrollPane scroll = new JScrollPane(processTable);
    scroll.setPreferredSize(new Dimension(1100, 180));
 
    // Delete button
    JButton btnDelete = new JButton("✕ Delete Selected");
    btnDelete.setBackground(dangerRed);
    btnDelete.setForeground(Color.WHITE);
    btnDelete.setOpaque(true);
    btnDelete.setBorderPainted(false);
    btnDelete.setFont(new Font("SansSerif", Font.BOLD, 12));
    btnDelete.addActionListener(e -> {
        int row = processTable.getSelectedRow();
        if (row == -1) {
            showError("⚠ Select a process first to delete.");
        } else {
            int modelRow = processTable.convertRowIndexToModel(row);
            model.removeRow(modelRow);
            lblStatus.setText("ⓘ Process removed.");
            lblStatus.setForeground(Color.GRAY);
        }
    });
 
    JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT));
    bottom.setBackground(Color.WHITE);
    bottom.add(btnDelete);
 
    JPanel p = new JPanel(new BorderLayout());
    p.setBackground(Color.WHITE);
    p.add(new JLabel(" Process Queue (Input Data)"), BorderLayout.NORTH);
    p.add(scroll, BorderLayout.CENTER);
    p.add(bottom, BorderLayout.SOUTH);
    return p;
}
 
 
 
    private JPanel createControlButtons() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 10));
        p.setBackground(Color.WHITE);
 
        // SJF options
        JPanel sjfOptions = new JPanel();
        sjfOptions.setBackground(Color.WHITE);
        sjfOptions.add(new JLabel("SJF:"));
        sjfNonPreemptive = new JRadioButton("Non-Preemptive", true);
        sjfPreemptive    = new JRadioButton("Preemptive");
        ButtonGroup sjfGroup = new ButtonGroup();
        sjfGroup.add(sjfNonPreemptive);
        sjfGroup.add(sjfPreemptive);
        sjfOptions.add(sjfNonPreemptive);
        sjfOptions.add(sjfPreemptive);
 
        // Priority options
        JPanel priOptions = new JPanel();
        priOptions.setBackground(Color.WHITE);
        priOptions.add(new JLabel("Priority:"));
        priNonPreemptive = new JRadioButton("Non-Preemptive");
        priPreemptive    = new JRadioButton("Preemptive", true);
        ButtonGroup priGroup = new ButtonGroup();
        priGroup.add(priNonPreemptive);
        priGroup.add(priPreemptive);
        priOptions.add(priNonPreemptive);
        priOptions.add(priPreemptive);
 
        JButton btnRun   = createBigButton("▶ Run Simulation", successGreen);
        JButton btnReset = createBigButton("↺ Reset All", new Color(100, 116, 139));
 
//        btnRun.addActionListener(e -> {
//            runSjfAlgorithm();
//            runPriorityAlgorithm();
//        });

        btnRun.addActionListener(e -> {
            if (model.getRowCount() < 2) {
                showError("⚠ Add at least 2 processes before running the simulation.");
                return;
            }
            runSjfAlgorithm();
            runPriorityAlgorithm();
        });



        btnReset.addActionListener(e -> handleReset());
 
        p.add(sjfOptions);
        p.add(priOptions);
        p.add(btnRun);
        p.add(btnReset);
        return p;
    }
 
    private JButton createBigButton(String text, Color bg) {
        JButton b = new JButton(text);
        b.setBackground(bg); b.setForeground(Color.WHITE);
        b.setOpaque(true); b.setBorderPainted(false);
        b.setPreferredSize(new Dimension(280, 50));
        b.setFont(new Font("SansSerif", Font.BOLD, 16));
        return b;
    }
 
 
private void runSjfAlgorithm() {
    ArrayList<Process> list = new ArrayList<>();
    for (int i = 0; i < model.getRowCount(); i++) {
        String pid   = model.getValueAt(i, 0).toString();
        int arrival  = Integer.parseInt(model.getValueAt(i, 1).toString());
        int burst    = Integer.parseInt(model.getValueAt(i, 2).toString());
        int priority = Integer.parseInt(model.getValueAt(i, 3).toString());
        list.add(new Process(pid, arrival, burst, priority));
    }
 
    ArrayList<Process> result;
    if (sjfPreemptive.isSelected()) {
        result = SchedulerAlgorithms.sjfschedulingPreemptive(list);
    } else {
        result = SchedulerAlgorithms.sjfscheduling(list);
    }
 
    sjfResModel.setRowCount(0);
    result.sort(Comparator.comparingInt(p -> p.completion));
 
    double totalWT = 0, totalTAT = 0, totalRT = 0;
    for (Process p : result) {
        sjfResModel.addRow(new Object[]{p.name, p.completion, p.waiting, p.turnaround, p.response});
        totalWT += p.waiting; totalTAT += p.turnaround; totalRT += p.response;
    }
 
    int n = result.size();
    sjfAvgWT  = totalWT  / n;
    sjfAvgTAT = totalTAT / n;
    sjfAvgRT  = totalRT  / n;
 
    lblSjfAvg.setText(String.format(
        "Averages: WT: %.2f | TAT: %.2f | RT: %.2f", sjfAvgWT, sjfAvgTAT, sjfAvgRT));
 
    int maxTime = result.stream().mapToInt(p -> p.completion).max().orElse(1);
    if (sjfPreemptive.isSelected()) {
        sjfGanttPanel.updateTimeline(SchedulerAlgorithms.sjfTimeline, maxTime);

    } else {
        // sort by actual start time so Gantt order is correct
        result.sort(Comparator.comparingInt(p -> p.startTime));
        ArrayList<String[]> timeline = new ArrayList<>();
        for (Process p : result)
            timeline.add(new String[]{
                p.name,
                String.valueOf(p.startTime),      // ← actual start, not completion-burst
                String.valueOf(p.completion)
            });
        sjfGanttPanel.updateTimeline(timeline, maxTime);
    }

}
 
 
    
   //================================================================================================= 
    

private void runPriorityAlgorithm() {
    ArrayList<Process> list = new ArrayList<>();
    for (int i = 0; i < model.getRowCount(); i++) {
        String pid   = model.getValueAt(i, 0).toString();
        int arrival  = Integer.parseInt(model.getValueAt(i, 1).toString());
        int burst    = Integer.parseInt(model.getValueAt(i, 2).toString());
        int priority = Integer.parseInt(model.getValueAt(i, 3).toString());
        list.add(new Process(pid, arrival, burst, priority));
    }
 
    ArrayList<Process> result;
    if (priPreemptive.isSelected()) {
        result = SchedulerAlgorithms.priorityscheduling(list);
    } else {
        result = SchedulerAlgorithms.priorityschedulingNonPreemptive(list);
    }
 
    priorityResModel.setRowCount(0);
    result.sort(Comparator.comparingInt(p -> p.completion));
 
    double totalWT = 0, totalTAT = 0, totalRT = 0;
    for (Process p : result) {
        priorityResModel.addRow(new Object[]{p.name, p.completion, p.waiting, p.turnaround, p.response});
        totalWT += p.waiting; totalTAT += p.turnaround; totalRT += p.response;
    }
 
    int n = result.size();
    priAvgWT  = totalWT  / n;
    priAvgTAT = totalTAT / n;
    priAvgRT  = totalRT  / n;
 
    lblPriAvg.setText(String.format(
        "Averages: WT: %.2f | TAT: %.2f | RT: %.2f", priAvgWT, priAvgTAT, priAvgRT));
    lblStatus.setText("✔ Done!");
 
    int maxTime = result.stream().mapToInt(p -> p.completion).max().orElse(1);
    if (priPreemptive.isSelected()) {
        priorityGanttPanel.updateTimeline(SchedulerAlgorithms.priorityTimeline, maxTime);
    } else {
        ArrayList<String[]> timeline = new ArrayList<>();
        for (Process p : result)
            timeline.add(new String[]{p.name,
                String.valueOf(p.completion - p.burst), String.valueOf(p.completion)});
        priorityGanttPanel.updateTimeline(timeline, maxTime);
    }
 
    generateAnalysis();   // ← fill all 3 text areas + comparison table
}



private void generateAnalysis() {
 
    // ── Comparison Table ──────────────────────────────────────
    compModel.setRowCount(0);
    compModel.addRow(new Object[]{
        "Avg Waiting Time",
        String.format("%.2f", sjfAvgWT),
        String.format("%.2f", priAvgWT),
        sjfAvgWT <= priAvgWT ? "✔ SJF" : "✔ Priority"
    });
    compModel.addRow(new Object[]{
        "Avg Turnaround Time",
        String.format("%.2f", sjfAvgTAT),
        String.format("%.2f", priAvgTAT),
        sjfAvgTAT <= priAvgTAT ? "✔ SJF" : "✔ Priority"
    });
    compModel.addRow(new Object[]{
        "Avg Response Time",
        String.format("%.2f", sjfAvgRT),
        String.format("%.2f", priAvgRT),
        sjfAvgRT <= priAvgRT ? "✔ SJF" : "✔ Priority"
    });
 
    // ── Efficiency Focus (SJF) ────────────────────────────────
    String sjfMode = sjfPreemptive.isSelected() ? "Preemptive" : "Non-Preemptive";
    StringBuilder eff = new StringBuilder();
    eff.append("SJF Mode: ").append(sjfMode).append("\n\n");
    eff.append(String.format("Avg Waiting Time  : %.2f\n", sjfAvgWT));
    eff.append(String.format("Avg Turnaround    : %.2f\n", sjfAvgTAT));
    eff.append(String.format("Avg Response Time : %.2f\n\n", sjfAvgRT));
    eff.append("Analysis:\n");
    eff.append("• SJF always selects the process with the shortest burst time.\n");
    eff.append("• This minimizes average waiting time across all processes.\n");
    if (sjfAvgWT < priAvgWT) {
        eff.append("• SJF achieved LOWER average waiting time than Priority Scheduling.\n");
        eff.append("• SJF is more EFFICIENT for this workload.\n");
    } else {
        eff.append("• SJF did NOT achieve lower waiting time than Priority on this workload.\n");
        eff.append("• This may happen when short jobs also happen to have high priority.\n");
    }
    eff.append("• Risk: Long processes may starve if short jobs keep arriving.\n");
    txtEfficiency.setText(eff.toString());
 
    // ── Urgency Focus (Priority) ──────────────────────────────
    String priMode = priPreemptive.isSelected() ? "Preemptive" : "Non-Preemptive";
    StringBuilder urg = new StringBuilder();
    urg.append("Priority Mode: ").append(priMode).append("\n");
    urg.append("Rule: Lower priority number = Higher urgency\n\n");
    urg.append(String.format("Avg Waiting Time  : %.2f\n", priAvgWT));
    urg.append(String.format("Avg Turnaround    : %.2f\n", priAvgTAT));
    urg.append(String.format("Avg Response Time : %.2f\n\n", priAvgRT));
    urg.append("Analysis:\n");
    urg.append("• Priority Scheduling serves the most urgent process first.\n");
    urg.append("• Useful in real systems where some tasks must not be delayed.\n");
    if (priAvgWT < sjfAvgWT) {
        urg.append("• Priority achieved LOWER average waiting time than SJF.\n");
        urg.append("• Urgent processes were served quickly on this workload.\n");
    } else {
        urg.append("• Priority had HIGHER average waiting time than SJF.\n");
        urg.append("• A long high-priority process may have blocked shorter ones.\n");
    }
    urg.append("• Risk: Low-priority processes may starve if high-priority jobs keep arriving.\n");
    txtUrgency.setText(urg.toString());
 
    // ── Final Conclusion ──────────────────────────────────────
    String wtWinner  = sjfAvgWT  <= priAvgWT  ? "SJF" : "Priority Scheduling";
    String tatWinner = sjfAvgTAT <= priAvgTAT ? "SJF" : "Priority Scheduling";
    String rtWinner  = sjfAvgRT  <= priAvgRT  ? "SJF" : "Priority Scheduling";
    String overall   = (sjfAvgWT + sjfAvgTAT) <= (priAvgWT + priAvgTAT)
                       ? "SJF" : "Priority Scheduling";
 
    StringBuilder fin = new StringBuilder();
    fin.append("=== Final Conclusion ===\n\n");
    fin.append("Based on the tested workload:\n\n");
    fin.append("• Best Avg Waiting Time   → ").append(wtWinner).append("\n");
    fin.append("• Best Avg Turnaround     → ").append(tatWinner).append("\n");
    fin.append("• Best Avg Response Time  → ").append(rtWinner).append("\n\n");
    fin.append("Trade-off (Efficiency vs Urgency):\n");
    fin.append("• SJF focuses on EFFICIENCY — it minimizes overall waiting time\n");
    fin.append("  by always running the shortest available job first.\n");
    fin.append("• Priority Scheduling focuses on URGENCY — it ensures critical\n");
    fin.append("  processes run immediately, even if they are long.\n");
    fin.append("• These goals conflict when a short job has low priority,\n");
    fin.append("  or a long job has very high priority.\n\n");
    fin.append("Recommendation:\n");
    fin.append("→ For this workload, ").append(overall)
       .append(" performed better overall.\n");
    fin.append("→ Use SJF when minimizing average wait time is the goal.\n");
    fin.append("→ Use Priority when serving urgent/critical processes matters more.\n");
    txtFinalConclusion.setText(fin.toString());
}
 //===============================================================================================================

  
private JPanel createScenarioButtons() {
    JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 8));
    p.setBackground(new Color(248, 250, 252));
    p.setBorder(BorderFactory.createTitledBorder("  Quick Load Test Scenarios  "));
 
    // Scenario A — Basic mixed workload
    JButton btnA = new JButton("Scenario 1: Basic");
    btnA.addActionListener(e -> {
        model.setRowCount(0);
        model.addRow(new Object[]{1, 0, 5, 2});
        model.addRow(new Object[]{2, 1, 3, 1});
        model.addRow(new Object[]{3, 2, 4, 3});
        model.addRow(new Object[]{4, 3, 2, 2});
        lblStatus.setText("ⓘ Scenario A loaded.");
        lblStatus.setForeground(successGreen);
    });
 
    // Scenario B — Conflict: short burst low priority vs long burst high priority
    JButton btnB = new JButton("Scenario 2: Conflict");
    btnB.addActionListener(e -> {
        model.setRowCount(0);
        model.addRow(new Object[]{1, 0, 10, 2});   // short burst, LOW priority
        model.addRow(new Object[]{2, 3, 2, 2});   // long burst,  HIGH priority
        model.addRow(new Object[]{3, 4, 2, 4});
        model.addRow(new Object[]{4, 5, 6, 1});
        lblStatus.setText("ⓘ Scenario B loaded — shows conflict between burst & priority.");
        lblStatus.setForeground(successGreen);
    });

 
    // Scenario C — Validation (instructions shown as status message)
    JButton btnD = new JButton("Scenario 3: Validation");
    btnD.addActionListener(e -> {
        lblStatus.setText(
            "ⓘ Scenario D: Try entering Burst = 0, negative Arrival, duplicate PID, or letters.");
        lblStatus.setForeground(new Color(180, 100, 0));
    });
 
    Color btnColor = new Color(99, 102, 241);
    for (JButton btn : new JButton[]{btnA, btnB, btnD}) {
        btn.setBackground(btnColor);
        btn.setForeground(Color.WHITE);
        btn.setOpaque(true);
        btn.setBorderPainted(false);
        btn.setFont(new Font("SansSerif", Font.BOLD, 12));
        p.add(btn);
    }
    
    return p;
}
 


 //==========================================================================================================================================   
    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception e) {}
        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
    }
 
    // ── PriorityGanttPanel ────────────────────────────────────────────────────
    class PriorityGanttPanel extends JPanel {
        private final ArrayList<int[]> segments = new ArrayList<>();
        private final ArrayList<String> pids    = new ArrayList<>();
        private int totalTime = 1;
 
        private final Color[] PALETTE = {
            new Color(34, 197, 94), new Color(2, 132, 199),
            new Color(220, 38, 38), new Color(234, 179, 8),
            new Color(168, 85, 247), new Color(249, 115, 22)
        };
 
        public void updateTimeline(ArrayList<String[]> timeline, int maxTime) {
            segments.clear(); pids.clear();
            totalTime = Math.max(1, maxTime);
            for (String[] seg : timeline) {
                pids.add(seg[0]);
                segments.add(new int[]{Integer.parseInt(seg[1]), Integer.parseInt(seg[2])});
            }
            setPreferredSize(new Dimension(400, segments.size() * 60 + 60));
            revalidate(); repaint();
        }
 
        public void update(ArrayList<Process> result, int maxTime) {
            segments.clear(); pids.clear();
            totalTime = Math.max(1, maxTime);
            for (Process p : result) {
                pids.add(p.name);
                segments.add(new int[]{p.completion - p.burst, p.completion});
            }
            setPreferredSize(new Dimension(400, segments.size() * 60 + 60));
            revalidate(); repaint();
        }
 
        public void update(ArrayList<Process> result) {
            segments.clear(); pids.clear(); totalTime = 1;
            revalidate(); repaint();
        }
 
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
 
            if (segments.isEmpty()) {
                g2.setColor(Color.GRAY);
                g2.setFont(new Font("SansSerif", Font.ITALIC, 13));
                g2.drawString("Run simulation to see Gantt Chart...", 20, 40);
                return;
            }
 
            int leftMargin = 55, topMargin = 20, rowH = 36, rowGap = 22;
            int chartW = getWidth() - leftMargin - 25;
 
            for (int i = 0; i < segments.size(); i++) {
                int start = segments.get(i)[0];
                int end   = segments.get(i)[1];
                int y     = topMargin + i * (rowH + rowGap);
                int x     = leftMargin + (int)((double) start / totalTime * chartW);
                int w     = Math.max(10, (int)((double)(end - start) / totalTime * chartW));
 
                g2.setColor(Color.DARK_GRAY);
                g2.setFont(new Font("SansSerif", Font.BOLD, 12));
                String pidLabel = "P" + pids.get(i);
                g2.drawString(pidLabel, leftMargin - g2.getFontMetrics().stringWidth(pidLabel) - 6, y + rowH/2 + 4);
 
                Color c = PALETTE[i % PALETTE.length];
                g2.setColor(c);
                g2.fillRoundRect(x, y, w, rowH, 10, 10);
                g2.setColor(c.darker());
                g2.setStroke(new BasicStroke(1.2f));
                g2.drawRoundRect(x, y, w, rowH, 10, 10);
 
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("SansSerif", Font.BOLD, 11));
                String label = "P" + pids.get(i);
                FontMetrics fm = g2.getFontMetrics();
                Shape oldClip = g2.getClip();
                g2.setClip(x + 2, y, w - 4, rowH);
                g2.drawString(label, x + (w - fm.stringWidth(label)) / 2, y + rowH/2 + 4);
                g2.setClip(oldClip);
 
                g2.setColor(new Color(50, 50, 50));
                g2.setFont(new Font("SansSerif", Font.BOLD, 10));
                String startStr = String.valueOf(start);
                FontMetrics fmTime = g2.getFontMetrics();
                g2.drawString(startStr, x - fmTime.stringWidth(startStr)/2, y + rowH + 14);
                g2.setColor(Color.GRAY);
                g2.setStroke(new BasicStroke(1f));
                g2.drawLine(x, y + rowH, x, y + rowH + 6);
 
                g2.setColor(new Color(50, 50, 50));
                g2.setFont(new Font("SansSerif", Font.BOLD, 10));
                String endStr = String.valueOf(end);
                g2.drawString(endStr, (x + w) - fmTime.stringWidth(endStr)/2, y + rowH + 14);
                g2.setColor(Color.GRAY);
                g2.drawLine(x + w, y + rowH, x + w, y + rowH + 6);
 
                g2.setColor(new Color(200, 200, 200));
                g2.drawLine(x, y + rowH, x + w, y + rowH);
            }
        }
 
        @Override
        public Dimension getPreferredSize() {
            return new Dimension(400, Math.max(120, segments.size() * 60 + 60));
        }
    }
 
    // ── SjfGanttPanel ─────────────────────────────────────────────────────────
    class SjfGanttPanel extends JPanel {
        private final ArrayList<int[]> segments = new ArrayList<>();
        private final ArrayList<String> pids    = new ArrayList<>();
        private int totalTime = 1;
 
        private final Color[] PALETTE = {
            new Color(2, 132, 199), new Color(34, 197, 94),
            new Color(220, 38, 38), new Color(234, 179, 8),
            new Color(168, 85, 247), new Color(249, 115, 22)
        };
 
        // For Preemptive (timeline)
        public void updateTimeline(ArrayList<String[]> timeline, int maxTime) {
            segments.clear(); pids.clear();
            totalTime = Math.max(1, maxTime);
            for (String[] seg : timeline) {
                pids.add(seg[0]);
                segments.add(new int[]{Integer.parseInt(seg[1]), Integer.parseInt(seg[2])});
            }
            setPreferredSize(new Dimension(400, segments.size() * 60 + 60));
            revalidate(); repaint();
        }
 
        // For Non-Preemptive (result + maxTime)
        public void update(ArrayList<Process> result, int maxTime) {
            segments.clear(); pids.clear();
            totalTime = Math.max(1, maxTime);
            for (Process p : result) {
                pids.add(p.name);
                segments.add(new int[]{p.completion - p.burst, p.completion});
            }
            setPreferredSize(new Dimension(400, segments.size() * 60 + 60));
            revalidate(); repaint();
        }
 
        // For Reset
        public void update(ArrayList<Process> result) {
            segments.clear(); pids.clear(); totalTime = 1;
            revalidate(); repaint();
        }
 
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
 
            if (segments.isEmpty()) {
                g2.setColor(Color.GRAY);
                g2.setFont(new Font("SansSerif", Font.ITALIC, 13));
                g2.drawString("Run simulation to see Gantt Chart...", 20, 40);
                return;
            }
 
            int leftMargin = 55, topMargin = 20, rowH = 36, rowGap = 22;
            int chartW = getWidth() - leftMargin - 25;
 
            for (int i = 0; i < segments.size(); i++) {
                int start = segments.get(i)[0];
                int end   = segments.get(i)[1];
                int y     = topMargin + i * (rowH + rowGap);
                int x     = leftMargin + (int)((double) start / totalTime * chartW);
                int w     = Math.max(10, (int)((double)(end - start) / totalTime * chartW));
 
                g2.setColor(Color.DARK_GRAY);
                g2.setFont(new Font("SansSerif", Font.BOLD, 12));
                String pidLabel = "P" + pids.get(i);
                g2.drawString(pidLabel, leftMargin - g2.getFontMetrics().stringWidth(pidLabel) - 6, y + rowH/2 + 4);
 
                Color c = PALETTE[i % PALETTE.length];
                g2.setColor(c);
                g2.fillRoundRect(x, y, w, rowH, 10, 10);
                g2.setColor(c.darker());
                g2.setStroke(new BasicStroke(1.2f));
                g2.drawRoundRect(x, y, w, rowH, 10, 10);
 
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("SansSerif", Font.BOLD, 11));
                String label = "P" + pids.get(i);
                FontMetrics fm = g2.getFontMetrics();
                Shape oldClip = g2.getClip();
                g2.setClip(x + 2, y, w - 4, rowH);
                g2.drawString(label, x + (w - fm.stringWidth(label)) / 2, y + rowH/2 + 4);
                g2.setClip(oldClip);
 
                g2.setColor(new Color(50, 50, 50));
                g2.setFont(new Font("SansSerif", Font.BOLD, 10));
                String startStr = String.valueOf(start);
                FontMetrics fmTime = g2.getFontMetrics();
                g2.drawString(startStr, x - fmTime.stringWidth(startStr)/2, y + rowH + 14);
                g2.setColor(Color.GRAY);
                g2.setStroke(new BasicStroke(1f));
                g2.drawLine(x, y + rowH, x, y + rowH + 6);
 
                g2.setColor(new Color(50, 50, 50));
                g2.setFont(new Font("SansSerif", Font.BOLD, 10));
                String endStr = String.valueOf(end);
                g2.drawString(endStr, (x + w) - fmTime.stringWidth(endStr)/2, y + rowH + 14);
                g2.setColor(Color.GRAY);
                g2.drawLine(x + w, y + rowH, x + w, y + rowH + 6);
 
                g2.setColor(new Color(200, 200, 200));
                g2.drawLine(x, y + rowH, x + w, y + rowH);
            }
        }
 
        @Override
        public Dimension getPreferredSize() {
            return new Dimension(400, Math.max(120, segments.size() * 60 + 60));
        }
    }
}