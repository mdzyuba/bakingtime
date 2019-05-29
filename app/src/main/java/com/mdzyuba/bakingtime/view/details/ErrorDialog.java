package com.mdzyuba.bakingtime.view.details;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.mdzyuba.bakingtime.R;

public class ErrorDialog {

    public interface Retry {
        void retry();
    }

    public static void showErrorDialog(Context context, Retry callback) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Error");
        builder.setMessage(context.getString(R.string.error_loading_recipes));
        builder.setPositiveButton(R.string.try_again, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                callback.retry();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
