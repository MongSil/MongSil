package kr.co.tacademy.mongsil.mongsil;

import android.content.Context;
import android.content.Intent;
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
 * Created by ccei on 2016-08-12.
 */
public class BottomEditDialogFragment extends DialogFragment {
    public interface OnBottomEditDialogListener {
        void onSelectBottomEdit(int select);
    }

    OnBottomEditDialogListener onBottomEditDialogListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnBottomEditDialogListener) {
            onBottomEditDialogListener = (OnBottomEditDialogListener) context;
        }
    }

    public BottomEditDialogFragment() {
    }

    public static BottomEditDialogFragment newInstance() {
        BottomEditDialogFragment fragment = new BottomEditDialogFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_TITLE, R.style.DialogTheme);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_double_bottom, container, false);
        Button btnFirst = (Button) view.findViewById(R.id.btn_first);
        Button btnSecond = (Button) view.findViewById(R.id.btn_second);
        Button btnClose = (Button) view.findViewById(R.id.btn_close);

        // 글 수정
        btnFirst.setText(getResources().getString(R.string.post_modify));
        btnFirst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                onBottomEditDialogListener.onSelectBottomEdit(0);
            }
        });

        // 글 삭제
        btnSecond.setText(getResources().getString(R.string.post_remove));
        btnSecond.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                onBottomEditDialogListener.onSelectBottomEdit(1);
            }
        });

        // 닫기
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
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
        wlp.windowAnimations = R.style.BottomDialogAnimation;
        wlp.gravity = Gravity.BOTTOM;
        wlp.height = getResources().getDimensionPixelSize(R.dimen.dialog_pic_vertical);
        window.setAttributes(wlp);
    }
}
