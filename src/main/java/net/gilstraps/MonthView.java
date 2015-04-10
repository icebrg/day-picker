package net.gilstraps;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;

/**
 * Abstract representation of a month in a simplified view of a calendar year -- not any particular year.
 */
public class MonthView {

    private static final int[] NUM_DAYS_IN = {
            0   // unused
            , 31 // January
            , 29 // February
            , 31 // March
            , 30 // April
            , 31 // May
            , 30 // June
            , 31 // July
            , 31 // August
            , 30 // September
            , 31 // October
            , 30 // November
            , 31 // December

    };

    private static final int[] STARTING_DAYS = {
            1 // seed value
            , 0 // January
            , 0 // February
            , 0 // March
            , 0 // April
            , 0 // May
            , 0 // June
            , 0 // July
            , 0 // August
            , 0 // September
            , 0 // October
            , 0 // November
            , 0 // December
    };

    private static final int[] ENDING_DAYS = {
            0 // seed value
            , 0 // January
            , 0 // February
            , 0 // March
            , 0 // April
            , 0 // May
            , 0 // June
            , 0 // July
            , 0 // August
            , 0 // September
            , 0 // October
            , 0 // November
            , 0 // December
    };

    private static final String[] MONTH_NAMES = {
            "", // Unused
            "Jan",
            "Feb",
            "Mar",
            "Apr",
            "May",
            "Jun",
            "Jul",
            "Aug",
            "Sep",
            "Oct",
            "Nov",
            "Dec"
    };

    static {
        for (int sd = 0; sd < 12; sd++) {
            STARTING_DAYS[sd + 1] = STARTING_DAYS[sd] + NUM_DAYS_IN[sd];
        }
        for (int sd = 1; sd <= 12; sd++) {
            ENDING_DAYS[sd] = STARTING_DAYS[sd] + (NUM_DAYS_IN[sd]-1);
        }
    }

    private static Font bigFont = new Font("Times New Roman", Font.BOLD, 24);

    private int monthNumber; // 1 - 12
    private int startingDay;
    private int lastDay;
    private int initialOffset;
    private JPanel mainPanel;
    private JPanel daysPanel;

    public MonthView( int aMonthNumber ) {
        if ( aMonthNumber < 1 || aMonthNumber > 12 ) {
            throw new IllegalArgumentException( "Month number must be between 1 & 12 inclusive" );
        }
        monthNumber = aMonthNumber;
        startingDay = STARTING_DAYS[monthNumber];
        lastDay = ENDING_DAYS[monthNumber];
        initialOffset = getInitialOffsetFor( monthNumber );
        createGUI();
    }

    public JComponent getGUI() {
        return mainPanel;
    }

    public void process( DayView aDayView ) {
        int dayNum = aDayView.geDayNumber();
        if ( dayNum >= startingDay && dayNum <= lastDay ) {
            placeDay( aDayView );
        }
    }

    private void createGUI() {
        mainPanel = new JPanel( new BorderLayout() );
        daysPanel = new JPanel( new GridLayout( 6, 7 ) );
        daysPanel.setBorder( new BevelBorder( BevelBorder.RAISED ) );
        daysPanel.setBackground(Color.black);
        Dimension min = new Dimension(0,0);
        Dimension max = new Dimension(Integer.MAX_VALUE,Integer.MAX_VALUE);
        for ( int i = 0 ; i < 42 ; i++ ) {
            daysPanel.add( new Box.Filler( min, min, max ) );
        }
        mainPanel.add( daysPanel, BorderLayout.CENTER );

        JLabel monthName = new JLabel( MONTH_NAMES[monthNumber] );
        monthName.setFont(bigFont);
        monthName.setHorizontalAlignment( JLabel.CENTER );
        monthName.setMinimumSize( new Dimension(100,75));
        monthName.setOpaque(true);
        monthName.setBackground(Color.black);
        monthName.setForeground(Color.white);
        mainPanel.add( monthName, BorderLayout.NORTH );
    }

    private void placeDay(final DayView aDayView) {
        int dayNum = aDayView.geDayNumber();
        int offset = (dayNum - startingDay) + initialOffset;
        daysPanel.remove( offset );
        daysPanel.add( aDayView.getGUI(), offset );
    }


    private static int getInitialOffsetFor( int monthNumber ) {
        int totalDays = 0;
        for ( int m = 1 ; m <= monthNumber - 1 ; m++ ) {
            totalDays += NUM_DAYS_IN[m];
        }
        return totalDays % 7;
    }

}
