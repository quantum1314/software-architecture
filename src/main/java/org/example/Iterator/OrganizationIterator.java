package org.example.Iterator;

import org.example.compositePattern.Component;

import java.util.Iterator;
import java.util.List;

public class OrganizationIterator implements ComponentIterator {
    private Iterator<Component> iterator;

    public OrganizationIterator(List<Component> components) {
        this.iterator = components.iterator();
    }

    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }

    @Override
    public Component next() {
        return iterator.next();
    }

}
