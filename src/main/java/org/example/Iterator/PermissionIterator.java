package org.example.Iterator;

import org.example.compositePattern.Component;

import java.util.Iterator;
import java.util.List;

public class PermissionIterator implements ComponentIterator {
    private Iterator<String> iterator;

    public PermissionIterator(List<String> permissions) {
        this.iterator = permissions.iterator();
    }

    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }

    @Override
    public Component next() {
        String permission = iterator.next();
        System.out.println(permission);
        return null;
    }

}
