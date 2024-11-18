package org.example.Iterator;

import org.example.compositePattern.Component;
import org.example.compositePattern.User;

import java.util.Iterator;
import java.util.List;

public class UserIterator implements ComponentIterator {
    private Iterator<User> iterator;

    public UserIterator(List<User> users) {
        this.iterator = users != null ? users.iterator() : null;
    }

    @Override
    public boolean hasNext() {
        return iterator != null && iterator.hasNext();
    }

    @Override
    public Component next() {
        User user = iterator != null ? iterator.next() : null;
        if (user != null) {
            System.out.println("用户: " + user.getName() + " (GUID: " + user.getGUID() + ")");
        }
        return user;
    }

}
