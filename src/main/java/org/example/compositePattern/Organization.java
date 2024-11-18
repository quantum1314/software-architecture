package org.example.compositePattern;
import org.example.Iterator.ComponentIterator;
import org.example.Iterator.OrganizationIterator;

import java.util.ArrayList;
import java.util.List;

public class Organization implements Component {
    private String name;
    private String permissions;
    private List<Component> children;

    public Organization(String id, String name, String permissions) {
        this.name = name;
        this.permissions = permissions;
        this.children = new ArrayList<>();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getPermissions() {
        return permissions;
    }



    public void add(Component component) {
        children.add(component);
    }

    @Override
    public ComponentIterator createIterator() {
        return new OrganizationIterator(children);  // 返回自定义的迭代器
    }

}
