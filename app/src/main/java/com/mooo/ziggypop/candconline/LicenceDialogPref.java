package com.mooo.ziggypop.candconline;

import android.content.Context;
import android.preference.DialogPreference;
import android.preference.ListPreference;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by ziggypop on 4/19/15.
 */
public class LicenceDialogPref extends DialogPreference {


    public LicenceDialogPref(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs);
    }
    public LicenceDialogPref(Context context, AttributeSet attrs){
        super(context, attrs);
    }


    @Override
    public void onBindDialogView(View view){
        super.onBindDialogView(view);
        View.inflate(getContext(), R.layout.licence_fragment, null);


    }

    @Override
    public void onDialogClosed(boolean positiveResult){
        super.onDialogClosed(positiveResult);
    }
}
