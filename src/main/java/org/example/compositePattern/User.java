package org.example.compositePattern;

import org.example.Iterator.ComponentIterator;
import org.example.Iterator.UserIterator;

public class User implements Component {
    private String name;
    private String GUID;

    public User(String name, String GUID) {
        this.name = name;
        this.GUID = GUID;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getPermissions() {
        return "";
    }


    public String getGUID() {
        return GUID;
    }

    @Override
    public ComponentIterator createIterator() {
        return new UserIterator(null);  // 返回空的 UserIterator，因为用户本身没有子元素
    }
}
