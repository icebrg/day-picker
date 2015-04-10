package net.gilstraps;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.ArrayList;

/**
 * TODO
 */
public class SimpleDaySet implements DaySet {

    private final Set<Integer> theDayNumbers = new HashSet<Integer>();
    private Set<DaySetListener> listeners = new HashSet<DaySetListener>();

    public void add(int aDayNumber) {
        if (aDayNumber < 0 || aDayNumber > 366) {
            throw new IllegalArgumentException("Days are numbered 0-366!");
        }
        synchronized (theDayNumbers) {
            if (!theDayNumbers.add(aDayNumber)) {
                throw new IllegalArgumentException("Day #" + aDayNumber + " already present!");
            }
        }
        notifyListenersAdded(aDayNumber);
    }

    public void add(List<Integer> dayNumbers) {
        for (int aDayNumber : dayNumbers) {
            if (aDayNumber < 0 || aDayNumber > 366) {
                throw new IllegalArgumentException("Days are numbered 0-366!");
            }
        }
        final List<Integer> thoseAdded = new ArrayList<Integer>();
        synchronized (theDayNumbers) {
            for (int aDayNumber : dayNumbers) {
                if (!theDayNumbers.contains(aDayNumber)) {
                    thoseAdded.add(aDayNumber);
                }
            }
            if (thoseAdded.size() > 0) {
                for (int aDayNumber : thoseAdded) {
                    theDayNumbers.add(aDayNumber);
                }
            }
        }
        if (thoseAdded.size() > 0) {
            notifyListenersAdded(thoseAdded);
        }
    }

    public void clear() {
        final List<Integer> removed = new ArrayList<Integer>();
        synchronized ( theDayNumbers ) {
            removed.addAll(theDayNumbers);
            theDayNumbers.clear();
        }
        if( removed.size()> 0) {
            notifyListenersRemoved(removed);
        }
    }


    public void assure(int aDayNumber) {
        boolean added = false;
        synchronized (theDayNumbers) {
            if (!theDayNumbers.contains(aDayNumber)) {
                theDayNumbers.add(aDayNumber);
                added = true;
            }
        }
        if (added) {
            notifyListenersAdded(aDayNumber);
        }
    }
//
//    public void setTo( int start, int end ) {
//        List<Integer> toAdd = new ArrayList<Integer>();
//        int s = start;
//        int e = end;
//        if ( s < e ) {
//            s = end;
//            e = start;
//        }
//        while( s <= e ) {
//            toAdd.add( s );
//            s += 1;
//        }
//        synchronized ( theDayNumbers ) {
//            theDayNumbers.clear();
//            add( toAdd );
//        }
//    }

    public void setTo(List<Integer> vals) {
        final List<Integer> removed = new ArrayList<Integer>();
        final List<Integer> added = new ArrayList<Integer>( vals );
        synchronized (theDayNumbers) {
            removed.addAll( theDayNumbers );
            removed.removeAll( vals );
            added.removeAll( theDayNumbers );
            theDayNumbers.addAll( added );
            theDayNumbers.removeAll( removed );
        }
        if (removed.size() > 0) {
            notifyListenersRemoved(removed);
        }
        if ( added.size() > 0 ) {
            notifyListenersAdded( added );
        }
    }

    public void remove(int aDayNumber) {
        if (aDayNumber < 0 || aDayNumber > 366) {
            throw new IllegalArgumentException("Days are numbered 0-366!");
        }
        synchronized (theDayNumbers) {
            if (!theDayNumbers.remove(aDayNumber)) {
                throw new IllegalArgumentException("Day #" + aDayNumber + " not present!");
            }
        }
        notifyListenersRemoved(aDayNumber);
    }

    public void remove(List<Integer> dayNumbers) {
        for (int aDayNumber : dayNumbers) {
            if (aDayNumber < 0 || aDayNumber > 366) {
                throw new IllegalArgumentException("Days are numbered 0-366!");
            }
        }
        synchronized (theDayNumbers) {
            for (int aDayNumber : dayNumbers) {
                if (!theDayNumbers.contains(aDayNumber)) {
                    throw new IllegalArgumentException("Day #\" + aDayNumber + \" not present!");
                }
            }
            for (int aDayNumber : dayNumbers) {
                theDayNumbers.remove(aDayNumber);
            }
        }
        notifyListenersRemoved(dayNumbers);
    }

    public boolean contains(final int dayNumber) {
        synchronized (theDayNumbers) {
            return theDayNumbers.contains(dayNumber);
        }
    }

    public void addListener(final DaySetListener l) {
        synchronized (listeners) {
            listeners.add(l);
        }
    }

    public void removeListener(final DaySetListener l) {
        synchronized (listeners) {
            listeners.remove(l);
        }
    }

    private void notifyListenersAdded(final int aDayNumber) {
        Set<DaySetListener> copy;
        synchronized (listeners) {
            copy = new HashSet<DaySetListener>(listeners);
        }
        for (DaySetListener l : copy) {
            l.dayAdded(aDayNumber);
        }
    }

    private void notifyListenersAdded(final List<Integer> dayNumbers) {
        Set<DaySetListener> copy;
        synchronized (listeners) {
            copy = new HashSet<DaySetListener>(listeners);
        }
        for (DaySetListener l : copy) {
            l.daysAdded(dayNumbers);
        }
    }

    private void notifyListenersRemoved(final int aDayNumber) {
        Set<DaySetListener> copy;
        synchronized (listeners) {
            copy = new HashSet<DaySetListener>(listeners);
        }
        for (DaySetListener l : copy) {
            l.dayRemoved(aDayNumber);
        }
    }

    private void notifyListenersRemoved(final List<Integer> dayNumbers) {
        Set<DaySetListener> copy;
        synchronized (listeners) {
            copy = new HashSet<DaySetListener>(listeners);
        }
        for (DaySetListener l : copy) {
            l.daysRemoved(dayNumbers);
        }
    }

}
