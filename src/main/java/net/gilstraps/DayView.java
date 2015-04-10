package net.gilstraps;

import java.awt.Color;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import javax.swing.border.BevelBorder;

/**
 * Shows a particular date
 */
public class DayView implements DaySet.DaySetListener {

    private static final Color INCLUDED_BACKGROUND = Color.orange;
    private static final Color JUST_INCLUDED_BACKGROUND = Color.pink;
    private static final Color NORMAL_BACKGROUND = Color.black;
    private static final Color NORMAL_FOREGROUND = Color.white;
    private static final int A_YEAR_WHICH_IS_A_LEAP_YEAR = 2008;
    private static final String DEFAULT_DATE_FORMAT = "MMM-dd";
    private static final int MAX_DAY_NUMBER = 366;

    private int theDayNumber;
    private DaySet theSet;
    private JLabel label;
    private boolean included;

    public DayView( int dayNumber ) {
        this( dayNumber, DEFAULT_DATE_FORMAT);
    }

    public DayView( int dayNumber, String formatString ) {
        theDayNumber = dayNumber;
        if ( theDayNumber < 0 | theDayNumber > MAX_DAY_NUMBER ) {
            throw new IllegalArgumentException( "Day number outside valid range!" );
        }
        GregorianCalendar cal = new GregorianCalendar( 0, 0, 0, 0, 0, 0 );
        cal.set( Calendar.YEAR, A_YEAR_WHICH_IS_A_LEAP_YEAR); // A leap year containing Feb 29th
        cal.set( Calendar.DAY_OF_YEAR, theDayNumber );
        DateFormat formatter = new SimpleDateFormat( formatString );
        label = new JLabel( formatter.format( cal.getTime() ) );
        label.setBackground(NORMAL_BACKGROUND);
        label.setOpaque(true);
        label.setForeground(NORMAL_FOREGROUND);
        label.setBorder( new BevelBorder( BevelBorder.LOWERED ));
        label.setHorizontalAlignment( JLabel.CENTER );
    }

    public void setDaySet( DaySet aSet ) {
        if ( theSet == aSet ) return;
        if ( theSet != null ) {
            theSet.removeListener(this);
        }
        theSet = aSet;
        if ( theSet != null ) {
            mark( theSet.contains( theDayNumber) );
            theSet.addListener(this);
        }
    }

    public JComponent getGUI() {
        return label;
    }

    public int geDayNumber() {
        return theDayNumber;
    }

    public void dayAdded(final int dayNumber) {
        if ( dayNumber == theDayNumber ) {
            markIncluded();
        }
        else if ( included ) {
            label.setBackground(INCLUDED_BACKGROUND);
        }
    }

    public void dayRemoved(final int dayNumber) {
        if ( dayNumber == theDayNumber ) {
            markExcluded();
        }
        else if (included) {
            label.setBackground(INCLUDED_BACKGROUND);
        }
    }

    public void daysAdded(final List<Integer> dayNumbers) {
        if ( dayNumbers.contains(theDayNumber) ) {
            markIncluded();
        }
        else if ( included ) {
            label.setBackground(INCLUDED_BACKGROUND);
        }
    }

    public void daysRemoved(final List<Integer> dayNumbers) {
        if ( dayNumbers.contains(theDayNumber) ) {
            markExcluded();
        }
        else if ( included ) {
            label.setBackground(INCLUDED_BACKGROUND);
        }
    }

    private void mark( boolean included ) {
        if ( included ) {
            markIncluded();
        }
        else {
            markExcluded();
        }
    }

    private void markIncluded() {
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                included = true;
                label.setBackground(JUST_INCLUDED_BACKGROUND);
                label.setForeground(NORMAL_BACKGROUND);
                label.repaint();
            }
        });
    }

    private void markExcluded() {
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                included = false;
                label.setBackground(NORMAL_BACKGROUND);
                label.setForeground(NORMAL_FOREGROUND);
                label.repaint();
            }
        });
    }
}
