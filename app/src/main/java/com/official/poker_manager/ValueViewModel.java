package com.official.poker_manager;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ValueViewModel extends ViewModel {
    private MutableLiveData<Integer> value = new MutableLiveData<Integer>();
    
    public void setValue(int value) {
        this.value.setValue(value);
    }
    
    public LiveData<Integer> getValue() {
        return value;
    }
}
