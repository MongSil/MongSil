package kr.co.tacademy.mongsil.mongsil;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
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
 * Created by ccei on 2016-07-29.
 * EditProfileActivity와 PostingActivity에 사용
 */
public class BottomPicDialogFragment extends DialogFragment {
    public interface OnBottomPicDialogListener {
        void onSelectBottomPic(int select);
    }

    OnBottomPicDialogListener onBottomPicDialogListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnBottomPicDialogListener) {
            onBottomPicDialogListener = (OnBottomPicDialogListener) context;
        }
    }

    public BottomPicDialogFragment() {
    }

    public static BottomPicDialogFragment newInstance() {
        BottomPicDialogFragment fragment = new BottomPicDialogFragment();
        return fragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        checkPermission();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_TITLE, R.style.DialogTheme);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_triple_bottom, container, false);
        Button btnFirst = (Button) view.findViewById(R.id.btn_first);
        Button btnSecond = (Button) view.findViewById(R.id.btn_second);
        Button btnThird = (Button) view.findViewById(R.id.btn_third);
        Button btnClose = (Button) view.findViewById(R.id.btn_close);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        // 카메라
        btnFirst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                onBottomPicDialogListener.onSelectBottomPic(0);

            }
        });

        // 갤러리
        btnSecond.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                onBottomPicDialogListener.onSelectBottomPic(1);
            }
        });


        // 기본 이미지
        btnThird.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                onBottomPicDialogListener.onSelectBottomPic(2);
            }
        });

        return view;
    }

    private final int PERMISSION_REQUEST_STORAGE = 101;

    private void checkPermission() {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (getActivity().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED
                    || getActivity().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE},
                        PERMISSION_REQUEST_STORAGE);
                dismissAllowingStateLoss();
            } else {
                //사용자가 언제나 허락
            }
        }
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
