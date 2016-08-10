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

/**
 * Created by ccei on 2016-08-09.
 */
public class MiddleDialogFragment extends DialogFragment {
    private static final String SELECTOR = "selector";

    public MiddleDialogFragment() { }
    public static MiddleDialogFragment newInstance(int selector) {
        MiddleDialogFragment f = new MiddleDialogFragment();
        Bundle b = new Bundle();
        b.putInt(SELECTOR, selector);
        f.setArguments(b);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_TITLE, R.style.DialogTheme);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_middle, container, false);

        Button dialog = (Button) view.findViewById(R.id.btn_dialog);
        View line = view.findViewById(R.id.middle_dialog_line);
        Button negative = (Button) view.findViewById(R.id.btn_negative);
        Button positive = (Button) view.findViewById(R.id.btn_positive);

        // 셀렉터가 0일 경우 positive와 negative, 1일 경우 positive만
        switch (getArguments().getInt(SELECTOR)) {
            case 0 :
                negative.setVisibility(View.VISIBLE);
                line.setVisibility(View.VISIBLE);

                dialog.setText(getResources().getText(R.string.post_remove_question));
                // if( ~~ ) {
                negative.setText(getResources().getText(R.string.cancel));
                positive.setText(getResources().getText(R.string.confirm));
                negative.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dismiss();
                    }
                });
                positive.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // TODO : 글 삭제하는 요청을 서버에 보내야함
                        dismiss();
                        getActivity().getSupportFragmentManager().beginTransaction()
                                .show(MiddleDialogFragment.newInstance(1)).commit();
                    }
                });
                return view;
            case 1 :
                negative.setVisibility(View.GONE);
                line.setVisibility(View.GONE);

                dialog.setText(getResources().getText(R.string.post_remove_done));
                positive.setText(getResources().getText(R.string.confirm));
                positive.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dismiss();
                    }
                });
                return view;
        }
        return null;
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