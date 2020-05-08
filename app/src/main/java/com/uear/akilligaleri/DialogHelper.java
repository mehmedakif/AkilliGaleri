package com.uear.akilligaleri;

import android.app.AlertDialog;
import android.content.Context;
import android.view.ContextThemeWrapper;

public class DialogHelper {

    public static AlertDialog.Builder alertBuilder(Context context) {
        return new AlertDialog.Builder(new ContextThemeWrapper(context,
                R.style.ShowAlertDialogTheme));
    }
}