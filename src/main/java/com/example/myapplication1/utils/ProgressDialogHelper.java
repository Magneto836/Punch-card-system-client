package com.example.myapplication1.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.example.myapplication1.R;

public class ProgressDialogHelper {

    private AlertDialog progressDialog;
    private Activity activity;

    public ProgressDialogHelper(Activity activity) {
            this.activity = activity;
        }
        public void showProgressDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.progress_layout, null);

            // 设置文本
            TextView progressText = dialogView.findViewById(R.id.progress_text);
            progressText.setText(message);

            // 隐藏按钮
            Button closeButton = dialogView.findViewById(R.id.close_button);
            closeButton.setVisibility(View.GONE);

            builder.setView(dialogView);
            builder.setCancelable(false);
            progressDialog = builder.create();
            progressDialog.show();
        }

        public void updateProgressDialogMessage(String message) {
            if (progressDialog != null && progressDialog.isShowing()) {
                TextView progressText = progressDialog.findViewById(R.id.progress_text);
                progressText.setText(message);
            }
        }

        public void showProgressDialogButton() {
            if (progressDialog != null && progressDialog.isShowing()) {
                Button closeButton = progressDialog.findViewById(R.id.close_button);
                closeButton.setVisibility(View.VISIBLE);
                closeButton.setOnClickListener(v -> dismissProgressDialog());
            }
        }

        public void dismissProgressDialog() {
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        }
    public void showConfirmationDialog(String title, String message, String positiveButtonText, String negativeButtonText, DialogInterface.OnClickListener positiveListener, DialogInterface.OnClickListener negativeListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton(positiveButtonText, positiveListener)
                .setNegativeButton(negativeButtonText, negativeListener);
        progressDialog = builder.create();
        progressDialog.show();
    }


}
