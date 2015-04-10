package net.gilstraps;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.border.BevelBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * TODO
 */
public class GUI2 {

    private static final int MIN = 0;
    private static final int MAX = 366;
    private static final int REGULAR_DELAY = 1500;
    private static final Font BIG_FONT = new Font("Times New Roman", Font.BOLD, 48);

    public static void main(String[] args) {
        GUI2 gui = new GUI2();
        gui.go();
    }

    private JFrame mainWindow = new JFrame("Day Picker");
    private JPanel randomOrderPanel;
    private SpinnerModel model = new SpinnerNumberModel(MIN, MIN, MAX, 1);
    private JLabel firstDate;
    private DateRandomizer randomizer = new DateRandomizer();
    private SimpleDaySet daySet = new SimpleDaySet();
    private List<DayView> views = new ArrayList<DayView>();
    private List<DayView> calendarViews = new ArrayList<DayView>();
    private ChangeListener bigDatesChangeListener;
    private List<MonthView> monthViews;

    private int delta = 1;
    private Timer timer;


    private GUI2() {

        KeyListener myKeyListener = new KeyAdapter() {
            @Override
            public void keyTyped(final KeyEvent e) {
                char typed = e.getKeyChar();
                if (typed == 'f' || typed == 'F' || typed == ' ') {
                    startOrStopTimer();
                }
            }

            @Override
            public void keyReleased(final KeyEvent e) {
                int typed = e.getKeyCode();
                if ((timer != null)) {
                    if (typed == KeyEvent.VK_UP) {
                        delta = 1;
                    }
                    else if (typed == KeyEvent.VK_DOWN) {
                        delta = -1;
                    }
                }
                else {
                    if (typed == KeyEvent.VK_UP) {
                        int newValue = (Integer) model.getValue() + 1;
                        if (newValue <= MAX) {
                            model.setValue(newValue);
                        }
                    }
                    else if (typed == KeyEvent.VK_DOWN) {
                        int newValue = (Integer) model.getValue() - 1;
                        if (newValue >= MIN) {
                            model.setValue(newValue);
                        }
                    }
                }
            }
        };

        mainWindow.setBackground(Color.black);
        mainWindow.setFocusable(true);
        mainWindow.addKeyListener(myKeyListener);

        Container mainPane = mainWindow.getContentPane();
        mainPane.setLayout(new BorderLayout());
        mainPane.setBackground(Color.black);


        JPanel topPanel = new JPanel() {
            @Override
            public Dimension getMinimumSize() {
                return new Dimension(0, 120);
            }

            @Override
            public Dimension getPreferredSize() {
                Dimension retval = super.getPreferredSize();
                retval.height = Math.max(retval.height, 120);
                return retval;
            }
        };
        topPanel.setLayout(new FlowLayout(FlowLayout.LEADING));
        topPanel.setBackground(Color.black);
        final JButton randomize = new JButton("Randomize");
        randomize.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                randomizeDates();
            }
        });
        randomize.setFocusable(false);
        topPanel.add(randomize);

        model.addChangeListener(new ChangeListener() {
            public void stateChanged(final ChangeEvent e) {
                int val = (Integer) model.getValue();
                int min = Math.min(MIN, val);
                int max = Math.min(MAX - 1, val);
                List<Integer> vals = new ArrayList<Integer>();
                for (int i = min; i <= max; i++) {
                    DayView view = views.get(i);
                    vals.add(view.geDayNumber());
                }
                daySet.setTo(vals);
            }
        });
        bigDatesChangeListener = new ChangeListener() {
            public void stateChanged(final ChangeEvent e) {
                int val = (Integer) model.getValue();
                int max = Math.min(MAX - 1, val);
                GregorianCalendar cal = new GregorianCalendar(0, 0, 0, 0, 0, 0);
                DateFormat formatter = new SimpleDateFormat("MMM-dd");
                cal.set(Calendar.YEAR, 2008); // A leap year containing Feb 29th
                cal.set(Calendar.DAY_OF_YEAR, views.get(max).geDayNumber());
                firstDate.setText(formatter.format(cal.getTime()));
            }
        };
        model.addChangeListener(bigDatesChangeListener);

        class MyLabel extends JLabel {
            public MyLabel(final String text) {
                super(text);
                setFont(BIG_FONT);
                setOpaque(true);
                setBackground(Color.black);
                setForeground(Color.white);
                setHorizontalAlignment(JLabel.CENTER);
            }

            @Override
            public Dimension getMinimumSize() {
                return new Dimension(200, 120);
            }

            @Override
            public Dimension getPreferredSize() {
                return getMinimumSize();
            }
        }

        firstDate = new MyLabel("");
        firstDate.setBorder(new BevelBorder(BevelBorder.LOWERED));
        topPanel.add(firstDate);

        mainPane.add(topPanel, BorderLayout.NORTH);

        JTabbedPane tabber = new JTabbedPane();
        mainPane.add(tabber, BorderLayout.CENTER);

        JComponent calendarView = createCalendarView();
        calendarView.setBackground(Color.black);
        tabber.add(calendarView, "Calendar");

        JComponent randomView = createRandomView();
        tabber.add(randomView, "Generated Order");

        mainWindow.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(final WindowEvent e) {
                System.exit(0);
            }
        });
    }

    private JComponent createRandomView() {
        JPanel contentPane = new JPanel(new BorderLayout());

        randomOrderPanel = new JPanel(new GridLayout(21, 18));
        randomOrderPanel.setBackground(Color.black);
        contentPane.add(randomOrderPanel, BorderLayout.CENTER);
        contentPane.setBackground(Color.black);
        Dimension strip = new Dimension(10, 10000);
        contentPane.add(new Box.Filler(new Dimension(10, 0), strip, strip), BorderLayout.WEST);
        contentPane.add(new Box.Filler(new Dimension(10, 0), strip, strip), BorderLayout.EAST);

        return contentPane;
    }

    private JComponent createCalendarView() {
        JPanel calendarPanel = new JPanel(new GridLayout(4, 3));
        monthViews = new ArrayList<MonthView>(12);
        for (int i = 1; i <= 12; i++) {
            MonthView view = new MonthView(i);
            monthViews.add(view);
            calendarPanel.add(view.getGUI());
        }
        return calendarPanel;
    }

    private void clearRandomOrderGUI() {
        randomOrderPanel.removeAll();
        views.clear();
    }

    private void addToRandomOrderView(DayView aView) {
        randomOrderPanel.add(aView.getGUI());
        views.add(aView);
    }

    private void clearCalendarViewGUI() {
        calendarViews.clear();
    }

    private void addToCalendarView(DayView aView) {
        for (MonthView v : monthViews) {
            v.process(aView);
        }
        calendarViews.add(aView);
    }

    public void go() {
//        Dimension screenSize = mainWindow.getToolkit().getScreenSize();
        Dimension screenSize = new Dimension(1280, 1024);
        mainWindow.setSize(screenSize.width - 10, screenSize.height - 40);
        mainWindow.setLocation(5, 40);
        mainWindow.setVisible(true);
        mainWindow.setFocusCycleRoot(true);
        mainWindow.requestFocus();
    }

    private void startOrStopTimer() {
        synchronized (views) {
            if (timer != null) {
                timer.cancel();
                timer = null;
            }
            else {
                timer = new Timer(true);
                timer.schedule(new MyTimerTask(), REGULAR_DELAY, REGULAR_DELAY);
            }
        }
    }

    private void randomizeDates() {
        final List<Integer> dayNumbers = randomizer.randomize();
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                if (timer != null) {
                    startOrStopTimer();
                }
                clearRandomOrderGUI();
                clearCalendarViewGUI();
                daySet.clear();
                for (int dayNumber : dayNumbers) {
                    DayView view = new DayView(dayNumber);
                    view.setDaySet(daySet);
                    addToRandomOrderView(view);
                }
                for (int dayNumber : dayNumbers) {
                    DayView view = new DayView(dayNumber,"dd");
                    view.setDaySet(daySet);
                    addToCalendarView(view);
                }
                mainWindow.validate();
                model.setValue(0);
                daySet.assure(views.get(0).geDayNumber());
                bigDatesChangeListener.stateChanged(null);
                delta = 1;
            }
        });
    }

    private class MyTimerTask extends TimerTask {
        @Override
        public void run() {
            model.setValue(((Integer) model.getValue()) + delta);
        }
    }
}
