package BVs_Boids;

import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.Map;
import javax.swing.*;

class BoidsVisualisation extends JFrame implements MouseListener {
    public boolean isOneOfThemImportant = false;
    JFrame myJFrame;
    Field field;
    ControlPanel controlPanel = new ControlPanel();
    TaskBar taskBar = new TaskBar();
    Tutorial tutorial = new Tutorial();
    Timer timer = null;

    int controlWidth, fieldWidth, height, fieldHeight, taskbarHeight, width;

    public static void main(String[] args) {
        BoidsVisualisation bv = new BoidsVisualisation();
    }

    public BoidsVisualisation() {
        //Determine width and height of frame, field and control panel
        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        width = gd.getDisplayMode().getWidth();
        height = gd.getDisplayMode().getHeight();
        fieldWidth = (int) Math.round(width * 0.8);
        taskbarHeight = height / 25;
        fieldHeight = (height - taskbarHeight) - 100;
        controlWidth = width - fieldWidth;

        //create frame
        myJFrame = new JFrame("BagelVulture's Boid Simulation");
        myJFrame.setSize(width, height);
        myJFrame.setDefaultCloseOperation(EXIT_ON_CLOSE);

        //create field (simulation goes there)
        field = new Field();
        field.setPreferredSize(new Dimension(fieldWidth, fieldHeight));
        field.setBorder(BorderFactory.createLineBorder(Color.black));

        controlPanel.setPreferredSize(new Dimension(controlWidth, fieldHeight));
        controlPanel.setBorder(BorderFactory.createLineBorder(Color.black));

        taskBar.setPreferredSize(new Dimension(width, taskbarHeight));
        taskBar.setBorder(BorderFactory.createLineBorder(Color.black));

        tutorial.setPreferredSize(new Dimension(width / 6, height / 6));
        tutorial.setBorder(BorderFactory.createLineBorder(Color.black));

        //add components to frame
        Container content = myJFrame.getContentPane();
        content.setLayout(new BorderLayout());
        content.add(controlPanel, BorderLayout.EAST);
        content.add(taskBar, BorderLayout.SOUTH);
        content.add(field, BorderLayout.WEST);

        pack();

        myJFrame.setVisible(true);

        addMouseListener(this);
    }

    public void mouseClicked(MouseEvent me) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseReleased(MouseEvent e) {
    }

    public void mousePressed(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    double speedMultiplier = 1.0;
    double cohesionCoefficient = 100.0;
    int alignmentCoefficient = 8;
    double separationCoefficient = 10.0;
    int fovDegrees = 360;
    double newBoidsVelocity = 0.3;
    int thisSquaredTotalCells = 10;
    double boidSize = 1;
    int N = 100;                                 //number of boids to simulate
    int distance = 50;                           //how close a boid has to be to influence the velocity equalizer

    /**
     * Field --- implements visualisation of Boids.
     */
    class Field extends JPanel {
        Boids boids;

        public Field() {
            init(N, fieldWidth, fieldHeight);
            timer = new Timer(30, new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    boids.move(thisSquaredTotalCells, distance, cohesionCoefficient, alignmentCoefficient, separationCoefficient, speedMultiplier, fovDegrees);
                    myJFrame.repaint();
                }
            });
        }

        public void init(int N, int fieldWidth, int height) {
            boids = new Boids(N, fieldWidth, height, newBoidsVelocity);
        }

        @Override
        public void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g;
            boids.draw(g2d, boidSize, distance, fovDegrees);
        }
    }

    class ControlPanel extends JPanel {

        private final Map<String, JSlider> sliders = new HashMap<>();
        private final Map<String, JTextField> sliderTextFields = new HashMap<>();

        public ControlPanel() {
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

            JPanel coeffPanel = new JPanel();
            coeffPanel.setPreferredSize(new Dimension(controlWidth - 10, height / 2));
            coeffPanel.setLayout(new BoxLayout(coeffPanel, BoxLayout.Y_AXIS));

            coeffPanel.add(createSliderWithTextBox(
                    "Cohesion", 0, 100, 100,
                    val -> cohesionCoefficient = val, 1));

            coeffPanel.add(createSliderWithTextBox(
                    "Alignment", 0, 50, 8,
                    val -> alignmentCoefficient = (int) val, 1));

            coeffPanel.add(createSliderWithTextBox(
                    "Separation", 0, 100, (int) 10,
                    val -> separationCoefficient = val, 1));

            coeffPanel.add(createSliderWithTextBox(
                    "Boid's FOV", 1, 360, 360,
                    val -> fovDegrees = (int) val, 1));

            coeffPanel.add(createSliderWithTextBox(
                    "How Far Each Boid Moves Per Velocity", 5, 500, 100,
                    val -> speedMultiplier = val / 100.0, 100));

            coeffPanel.add(createSliderWithTextBox(
                    "Boid Icon Size", 1, 100, 10,
                    val -> boidSize = val / 10.0, 10));

            coeffPanel.add(createSliderWithTextBox(
                    "Initial Velocity of Boids After Restart", 0, 100, 30,
                    val -> newBoidsVelocity = val / 10.0, 100));

            coeffPanel.add(createSliderWithTextBox(
                    "Number of Boids", 1, 1000, 100,
                    val -> N = (int) val, 1));

            coeffPanel.add(createSliderWithTextBox(
                    "Neighbour View Distance", 1, 1000, 50,
                    val -> distance = (int) val, 1));

            JButton resetButton = new JButton("Reset Sliders");
            resetButton.addActionListener(e -> {
                resetSliders();
                isOneOfThemImportant = false;
            });
            JButton tutorialButton = new JButton("Start The Tutorial");
            tutorialButton.addActionListener(e -> {
                tutorial.showTutorial(1);
            });
            coeffPanel.add(resetButton);
            coeffPanel.add(tutorialButton);

            add(coeffPanel);

            setVisible(true);
        }

        private JPanel createSliderWithTextBox(String labelText, int min, int max, double initial,
                                               java.util.function.DoubleConsumer setter, int textboxValueDivider) {
            JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));

            JLabel label = new JLabel(labelText);
            JSlider slider = new JSlider(JSlider.HORIZONTAL, min, max, (int) initial);
            slider.setPreferredSize(new Dimension(150, 20));

            JTextField textField = new JTextField(String.valueOf(initial / textboxValueDivider), 6);

            slider.addChangeListener(e -> {
                int value = ((JSlider) e.getSource()).getValue();
                textField.setText(String.valueOf((double) value / textboxValueDivider));
                setter.accept(value);
            });

            textField.addActionListener(e -> {
                try {
                    double val = Double.parseDouble(textField.getText()) * textboxValueDivider;
                    setter.accept(val);
                    if (val < slider.getMinimum()) slider.setMinimum((int) Math.floor(val));
                    if (val > slider.getMaximum()) slider.setMaximum((int) Math.ceil(val));
                    slider.setValue((int) val);
                } catch (NumberFormatException ignored) {
                }
            });

            panel.add(label);
            panel.add(slider);
            panel.add(textField);

            sliders.put(labelText, slider);
            sliderTextFields.put(labelText, textField);

            return panel;
        }

        private void resetSliders() {
            speedMultiplier = 1.0;
            cohesionCoefficient = 100.0;
            alignmentCoefficient = 8;
            separationCoefficient = 10.0;
            fovDegrees = 360;
            newBoidsVelocity = 0.3;
            boidSize = 1;
            N = 100;
            distance = 50;

            updateSlider("Cohesion", (int) cohesionCoefficient, 1);
            updateSlider("Alignment", alignmentCoefficient, 1);
            updateSlider("Separation", (int) separationCoefficient, 1);
            updateSlider("Boid's FOV", fovDegrees, 1);
            updateSlider("Simulation Speed", (int) (speedMultiplier * 100), 100);
            updateSlider("Boid Icon Size", (int) (boidSize * 10), 10);
            updateSlider("Initial Velocity of Boids After Restart", (int) (newBoidsVelocity * 100), 100);
            updateSlider("Neighbour View Distance", distance, 1);
            updateSlider("Number of Boids", N, 1);
        }

        private void updateSlider(String key, int sliderValue, int divider) {
            JSlider slider = sliders.get(key);
            JTextField field = sliderTextFields.get(key);
            if (slider != null && field != null) {
                slider.setValue(sliderValue);
                field.setText(String.valueOf((double) sliderValue / divider));
            }
        }
    }

    class TaskBar extends JPanel {
        public TaskBar() {
            setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
            JPanel buttonPanel = new JPanel();
            buttonPanel.setPreferredSize(new Dimension(width / 2, taskbarHeight - 10));
            buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));

            JButton startstopButton = new JButton("Start/Stop");
            startstopButton.addActionListener(e -> {
                if (timer.isRunning()) {
                    timer.stop();
                } else {
                    timer.start();
                }
            });

            JButton initButton = new JButton("Reset Boids");
            initButton.addActionListener(e -> {
                field.init(N, fieldWidth, fieldHeight);
                timer.restart();
                field.repaint();
            });

            JButton focusButton = new JButton("Focus On A Random Boid");
            focusButton.addActionListener(e -> {
                field.boids.setImportantBoid();
                isOneOfThemImportant = true;
            });

            String fsText = null;

            JTextArea focusedStats = new JTextArea();
            focusedStats.setEditable(false);
            focusedStats.setLineWrap(true);
            focusedStats.setWrapStyleWord(true);
            focusedStats.setVisible(true);
            if (field != null) {
                for (Bird b : field.boids.getBirds()) {
                    if (b.isImportant) {
                        fsText = b.toString();
                    }
                }
            }
            if (fsText == null) {
                fsText = "Focus on a Boid to View it's Position and Velocity";
            }

            buttonPanel.add(startstopButton);
            buttonPanel.add(focusButton);
            buttonPanel.add(initButton);
            buttonPanel.add(focusedStats);
            add(buttonPanel);

            setVisible(true);
        }
    }

    class Tutorial extends JPanel{
        private JDialog dialog;
        private int stage = 0;

        public Tutorial() {
            dialog = new JDialog(myJFrame, "Tutorial", false);
            dialog.setSize(width / 6, height / 6);
            dialog.setMinimumSize(new Dimension(370, 250));
            dialog.setLocationRelativeTo(myJFrame);
        }

        public void showTutorial(int stage) {
            this.stage = stage;
            updateTutorialStage();
            dialog.setVisible(true);
        }

        private void updateTutorialStage() {
            dialog.getContentPane().removeAll();

            JPanel contentPanel = new JPanel();
            contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
            contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            JTextArea tutorialText = new JTextArea();
            tutorialText.setEditable(false);
            tutorialText.setLineWrap(true);
            tutorialText.setWrapStyleWord(true);

            JButton nextButton = new JButton("Next");

            switch (stage) {
                case 1 -> {
                    tutorialText.setText(
                        """
                                          Welcome to my boid simulation!
                        
                        
                        A boid is a bird-like object that has three basic rules.
                        The first is that every boid tries to move towards the
                        center of mass of all surrounding boids. this is the
                        cohesion slider on the right. Try playing around with it,
                        and when you're done click the "next" button below.""");
                    nextButton.addActionListener(e -> showTutorial(2));
                }
                case 2 -> {
                    tutorialText.setText("""
                        The second rule is that every boid tries to match the
                        speed and direction of all surrounding boids, the
                        alignment slider on the right. As before, try playing
                        around with it, and when you're done click the "next"
                        button below.""");
                    nextButton.addActionListener(e -> showTutorial(3));
                }
                case 3 -> {
                    tutorialText.setText("""
                        The third rule is that every boid tries to steer away
                        from all surrounding boids, the separation slider on
                        the right. Those are the three rules, but I have added
                        many more sliders, so play around with them.""");
                    nextButton.setText("Finish");
                    nextButton.addActionListener(e -> dialog.setVisible(false));
                }
            }

            contentPanel.add(tutorialText);
            contentPanel.add(Box.createVerticalStrut(10));
            contentPanel.add(nextButton);

            dialog.getContentPane().add(contentPanel, BorderLayout.CENTER);
            dialog.revalidate();
            dialog.repaint();
        }
    }
}