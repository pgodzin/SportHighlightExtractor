import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.Random;

public class HighlightGUI extends JPanel
        implements ActionListener,
        PropertyChangeListener {

    private JProgressBar progressBar;
    private JLabel progressLabel;
    private JButton startButton;
    private JButton openButton;

    //private JTextArea taskOutput;
    //private JTextArea textStatus;
    private JPanel highlightsArea;
    private String path;
    private Task task;


    //private HighlightExtractor highlightExtractor;

    class Task extends SwingWorker<Void, Void> {
        /*
         * Main task. Executed in background thread.
         */
        @Override
        public Void doInBackground() {
            Random random = new Random();
            setProgress(0);
            //Initialize progress property.
            HighlightExtractor extractor = new HighlightExtractor(path, progressLabel,progressBar, highlightsArea);

            /*while (progress < 100) {
                progressLabel.setText(String.valueOf(extractor.getProgress()));
                progress = extractor.getProgress();
                setProgress(Math.min(progress, 100));
            }*/
            return null;
        }

        /*
         * Executed in event dispatching thread
         */
        @Override
        public void done() {
            Toolkit.getDefaultToolkit().beep();
            startButton.setEnabled(true);
            setCursor(null); //turn off the wait cursor
            System.out.println("Done");
            progressLabel.setText("Done!\n");
            //taskOutput.append("Done!\n");
        }


    }

    public HighlightGUI() {
        super(new BorderLayout());




        //Create the demo's UI.
        startButton = new JButton("Start");
        startButton.setActionCommand("start");
        startButton.addActionListener(this);

        progressBar = new JProgressBar(0, 100);
        progressBar.setValue(0);
        progressBar.setStringPainted(true);

        progressLabel = new JLabel();
        progressLabel.setText("Status: ---");

        highlightsArea = new JPanel();
        JLabel teamsLabel = new JLabel();
        teamsLabel.setName("teamsLabel");
        highlightsArea.add(teamsLabel);

/*        taskOutput = new JTextArea(5, 20);
        taskOutput.setMargin(new Insets(5,5,5,5));
        taskOutput.setEditable(false);*/


        //Create File opener button
        openButton = new JButton("Open");
        openButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                JFileChooser openFile = new JFileChooser();



                int returnVal = openFile.showOpenDialog(null);


                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = openFile.getSelectedFile();
                    //TODO CALL TASK
                    path = selectedFile.getAbsoluteFile().toString();
                    System.out.println("Selected File: " + selectedFile.getName());
                    openFile.approveSelection();
                } else if (returnVal == JFileChooser.CANCEL_OPTION) {
                    System.out.println("canceled");

                }


            }
        });


        JPanel panel = new JPanel();
        panel.add(openButton);
        panel.add(startButton);
        panel.add(progressBar);
        panel.add(progressLabel);

        add(panel, BorderLayout.PAGE_START);
        add(highlightsArea, BorderLayout.CENTER);
        highlightsArea.setLayout(new BoxLayout(highlightsArea, BoxLayout.Y_AXIS));
        //add(new JScrollPane(taskOutput), BorderLayout.CENTER);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

    }

    /**
     * Invoked when the user presses the start button.
     */
    public void actionPerformed(ActionEvent evt) {
        //startButton.setEnabled(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        //Instances of javax.swing.SwingWorker are not reusuable, so
        //we create new instances as needed.
        System.out.println("Start");
        if(path != null){
            System.out.println("Done with that");
            task = new Task();
            task.addPropertyChangeListener(this);
            task.execute();
        }
    }

    /**
     * Invoked when task's progress property changes.
     */
    public void propertyChange(PropertyChangeEvent evt) {
        if ("progress" == evt.getPropertyName()) {
            int progress = (Integer) evt.getNewValue();
            progressBar.setValue(progress);
            progressLabel.setText(String.format(
                    "Completed %d%% of task.\n", task.getProgress()));
            //taskOutput.append(String.format(
            //        "Completed %d%% of task.\n", task.getProgress()));
        }
    }


    /**
     * Create the GUI and show it. As with all GUI code, this must run
     * on the event-dispatching thread.
     */
    private static void createAndShowGUI() {
        //Create and set up the window.
        JFrame frame = new JFrame("HighlightGUI");
        frame.setSize(900,500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Create and set up the content pane.
        JComponent newContentPane = new HighlightGUI();
        newContentPane.setOpaque(true); //content panes must be opaque
        frame.setContentPane(newContentPane);

        //Display the window.
        //frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
               createAndShowGUI();

            }
        });
    }
}