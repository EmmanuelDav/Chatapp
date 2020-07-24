package com.rad5.chatapp.Utils;

import android.text.Editable;
import android.widget.EditText;

public class SpaceRemoval {

    public static boolean CheckSpace(String input){
        return input.trim().isEmpty() || input.trim().length() == 0;
    }


}
