package kr.co.tacademy.mongsil.mongsil;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.github.rahatarmanahmed.cpv.CircularProgressView;

/**
 * Created by ccei on 2016-08-09.
 */
public class ProgressDialogFragment extends DialogFragment {
    private static final String SELECTOR = "selector";

    private int selector;

    public ProgressDialogFragment() { }
    public static ProgressDialogFragment newInstance(int selector) {
        ProgressDialogFragment fragment = new ProgressDialogFragment();
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
        setStyle(STYLE_NO_TITLE, R.style.ProgressDialogTheme);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_loading_progress, container, false);

        final CircularProgressView circularProgressView =
                (CircularProgressView) view.findViewById(R.id.progress_view);
        final TextView dialog = (TextView) view.findViewById(R.id.progress_text);

        switch (selector) {
            case 0 : // 저장 중이니 잠시만 기다려주세요..
                dialog.setText(getResources().getText(R.string.wait_message));
                break;
            case 1 : // 서버와의 연결에 실패했습니다.\n잠시만 기다려주세요..
                dialog.setText(getResources().getText(R.string.connection_fail_message));
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
        wlp.gravity = Gravity.CENTER;
        window.setAttributes(wlp);
    }
}