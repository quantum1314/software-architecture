package org.example.Iterator;

import org.example.compositePattern.Component;

public interface ComponentIterator {
    boolean hasNext();
    Component next();
}
