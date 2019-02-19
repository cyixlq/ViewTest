package com.example.viewtest.core;

public class ObserveData<T> {

    public ObserveData() {

    }

    public ObserveData(T value) {
        this.value = value;
    }

    private T value;
    private ValueChangeListener<T> listener;

    public void setValue(T v) {
        if (null != listener) {
            listener.changed(this.value, v);
        }
        this.value = v;
    }

    public T getValue() {
        return this.value;
    }

    public void setValueChangeListener(ValueChangeListener<T> listener) {
        this.listener = listener;
    }

}
