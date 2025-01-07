package gr.aueb.cf.chatgptassistant2025.utils;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import androidx.annotation.NonNull;

import gr.aueb.cf.chatgptassistant2025.R;

public class CustomAlertDialog extends Dialog {

    private View.OnClickListener yesClickListener;
    private View.OnClickListener noClickListener;
    private Button yesBtn;
    private Button noBtn;

    public CustomAlertDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_alert_dialog);

        yesBtn = findViewById(R.id.yes_btn);
        noBtn = findViewById(R.id.no_btn);

        yesBtn.setOnClickListener(yesClickListener);
        noBtn.setOnClickListener(noClickListener);
    }

    public void yesPressed(View.OnClickListener onClickListener) {
        dismiss();
        this.yesClickListener = onClickListener;
    }

    public void noPressed(View.OnClickListener onClickListener) {
        dismiss();
        this.noClickListener = onClickListener;
    }
}
