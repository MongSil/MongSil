package kr.co.tacademy.mongsil.mongsil;

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
public class MiddleAloneDialogFragment extends DialogFragment {
    private static final String SELECTOR = "selector";

    private int selector;

    public MiddleAloneDialogFragment() { }
    public static MiddleAloneDialogFragment newInstance(int selector) {
        MiddleAloneDialogFragment fragment = new MiddleAloneDialogFragment();
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
        negative.setVisibility(View.GONE);
        line.setVisibility(View.GONE);

        switch (selector) {
            case 0 : // 삭제 완료한 경우 [확인]
                dialog.setText(getResources().getText(R.string.post_remove_done));
                positive.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dismiss();
                    }
                });
                break;
            case 1 : // 삭제 실패한 경우 [확인]
                dialog.setText(getResources().getText(R.string.post_remove_fail));
                positive.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dismiss();
                    }
                });
                break;
            case 2 : // 저장 실패한 경우 [확인]
                dialog.setText(getResources().getText(R.string.save_edit_profile_fail));
                positive.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dismiss();
                    }
                });
                break;
            case 90 : // 로그인 실패한 경우 [확인]
                dialog.setText(getResources().getText(R.string.login_fail));
                positive.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dismiss();
                    }
                });
                break;
            case 91 : // 회원가입 실패한 경우 [확인]
                dialog.setText(getResources().getText(R.string.signup_fail));
                positive.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dismiss();
                    }
                });
                break;
            case 92 : // 연결 실패한 경우 [확인]
                dialog.setText(getResources().getText(R.string.connection_fail));
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
        dismissAllowingStateLoss();
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