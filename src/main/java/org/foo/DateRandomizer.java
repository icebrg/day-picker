package org.foo;

import java.security.SecureRandom;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class DateRandomizer {

    private static final int MAX_DAY_NUMBER = 366;

    private SecureRandom generator = new SecureRandom();
    private static final String DATE_FORMAT_STRING = "MMM dd";

    public static void main(String[] args) {
        GregorianCalendar cal = new GregorianCalendar( 0, 0, 0, 0, 0, 0 );
        DateRandomizer randomizer = new DateRandomizer();
        List<Integer> dayNumbers = randomizer.randomize();
        DateFormat formatter = new SimpleDateFormat(DATE_FORMAT_STRING);
        for (int i : dayNumbers) {
            cal.set(Calendar.DAY_OF_YEAR, i);
            Date d = cal.getTime();
            System.err.println(formatter.format(d));
        }
    }

    public List<Integer> randomize() {
        BitSet bits = new BitSet( MAX_DAY_NUMBER + 1 );
        List<Integer> retval = new ArrayList<Integer>();
        while ( bits.nextClearBit(1) < MAX_DAY_NUMBER ) {
            int guess;
            //noinspection StatementWithEmptyBody
            for ( guess = makeGuess() ; bits.get( guess ) ; guess = makeGuess() );
            retval.add( guess );
            bits.set( guess );
//            System.err.println( "We have a hole at " + bits.nextClearBit( 1 ) );
        }
        return retval;
    }

    private int makeGuess() {
        return ( Math.abs( generator.nextInt() % MAX_DAY_NUMBER ) ) + 1;
    }
}
