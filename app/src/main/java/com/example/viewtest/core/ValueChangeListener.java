package com.example.viewtest.core;

public interface ValueChangeListener<T> {
    void changed(T oldValue, T newValue);
}
