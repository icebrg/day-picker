package org.foo;

import java.util.List;

public interface DaySet {

    public interface DaySetListener {
        void dayAdded( int dayNumber );
        void dayRemoved( int dayNumber );
        void daysAdded( List<Integer> dayNumbers );
        void daysRemoved( List<Integer> dayNumbers );
    }

    boolean contains( int dayNumber );

    void addListener( DaySetListener l );
    void removeListener( DaySetListener l );
}
