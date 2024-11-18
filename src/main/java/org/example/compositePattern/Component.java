package org.example.compositePattern;

import org.example.Iterator.ComponentIterator;

public interface Component {
    String getName();
    String getPermissions();


    ComponentIterator createIterator();  // 修改为返回自定义的 ComponentIterator
}
