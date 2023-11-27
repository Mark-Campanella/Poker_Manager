package com.official.poker_manager;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

// ViewModel para guardar o valor do raise
public class ValueViewModel extends ViewModel {
    private final MutableLiveData<Integer> value = new MutableLiveData<Integer>();

    public LiveData<Integer> getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value.setValue(value);
    }
}
