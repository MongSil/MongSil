package kr.co.tacademy.mongsil.mongsil;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by ccei on 2016-08-09.
 */
public class MiddleSelectDialogFragment extends DialogFragment {
    private static final String SELECTOR = "selector";

    private int selector;

    public interface OnMiddleSelectDialogListener {
        void onMiddleSelect(int select);
    }

    OnMiddleSelectDialogListener onMiddleSelectDialogListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnMiddleSelectDialogListener) {
            onMiddleSelectDialogListener = (OnMiddleSelectDialogListener) context;
        }
    }

    public MiddleSelectDialogFragment() {
    }

    public static MiddleSelectDialogFragment newInstance(int selector) {
        MiddleSelectDialogFragment fragment = new MiddleSelectDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(SELECTOR, selector);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            selector = getArguments().getInt(SELECTOR);
        }
        setStyle(STYLE_NO_TITLE, R.style.DialogTheme);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_middle, container, false);

        final TextView dialog = (TextView) view.findViewById(R.id.text_dialog);
        View line = view.findViewById(R.id.middle_dialog_line);
        Button negative = (Button) view.findViewById(R.id.btn_negative);
        Button positive = (Button) view.findViewById(R.id.btn_positive);

        // 셀렉터가 0일 경우 positive와 negative, 1일 경우 positive만
        switch (selector) {
            case 0: // 삭제하는 경우 [취소 / 확인]
                dialog.setText(getResources().getText(R.string.post_remove_question));
                // if( ~~ ) {
                negative.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dismiss();
                    }
                });
                positive.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dismiss();

                    }
                });
                break;
            case 2: // 저장하는 경우 [취소 / 확인]
                dialog.setText(getResources().getText(R.string.save_edit_profile));
                negative.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dismiss();
                    }
                });
                positive.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dismiss();
                    }
                });
                break;
            case 99: // 계정 삭제하는 경우 [취소 / 확인]
                dialog.setText(getResources().getText(R.string.account_leave));
                negative.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dismiss();
                    }
                });
                positive.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dismiss();
                    }
                });
                break;
        }
        return view;
    }

    @Override
    public void onStop() {
        super.onStop();
        dismiss();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Window window = getDialog().getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.TOP;
        window.setAttributes(wlp);
    }
}