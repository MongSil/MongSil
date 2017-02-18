package kr.co.tacademy.mongsil.mongsil.Fragments;

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

import kr.co.tacademy.mongsil.mongsil.Managers.PropertyManager;
import kr.co.tacademy.mongsil.mongsil.R;

/**
 * Created by ccei on 2016-08-09.
 */
public class MiddleAloneDialogFragment extends DialogFragment {
    private static final String SELECTOR = "selector";

    public interface OnMiddleAloneDialogListener {
        void onMiddleAlone(int select);
    }

    OnMiddleAloneDialogListener onMiddleAloneDialogListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnMiddleAloneDialogListener) {
            onMiddleAloneDialogListener = (OnMiddleAloneDialogListener) context;
        }
    }

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
            case 3 : // 댓글 작성에 실패할 경우 [확인]
                dialog.setText(getResources().getText(R.string.save_reply_fail));
                positive.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dismiss();
                    }
                });
                break;
            case 10 : // 글 내용이 존재하지 않는 경우 [확인]
                dialog.setText(getResources().getText(R.string.none_save_post_cause_location));
                positive.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onMiddleAloneDialogListener.onMiddleAlone(10);
                        dismiss();
                    }
                });
                break;
            case 11 : // 지역을 선택하지 않은 경우 [확인]
                dialog.setText(getResources().getText(R.string.none_save_post_cause_location));
                positive.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dismiss();
                    }
                });
                break;
            case 12 : // 글 작성 내용이 없는 경우 [확인]
                dialog.setText(getResources().getText(R.string.none_save_post));
                positive.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dismiss();
                    }
                });
                break;
            case 13 : // 유저 정보가 없는 경우 [확인]
                dialog.setText(getResources().getText(R.string.none_user_info));
                positive.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dismiss();
                    }
                });
                break;
            case 14 : // 공유하기 눌렀을 때 퍼미션이 없는 경우 [확인]
                dialog.setText(getResources().getText(R.string.none_share_permission));
                positive.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dismiss();
                    }
                });
                break;
            case 81 : // 구글Api 연결에 실패한 경우
                dialog.setText(getResources().getText(R.string.google_api_connection_fail));
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
            case 93 : // 회원가입 시 중복된 닉네임이 있는 경우 [확인]
                dialog.setText(getResources().getText(R.string.signup_fail_overlap_nickname));
                positive.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dismiss();
                    }
                });
                break;
            case 98 : // 3G 네트워크를 사용하는 경우 [확인]
                dialog.setText(getResources().getText(R.string.network_connection_mobile));
                positive.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dismiss();
                    }
                });
                break;
            case 99 : // 사용 가능한 네트워크가 없는 경우 [확인]
                dialog.setText(getResources().getText(R.string.network_connection_fail));
                positive.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onMiddleAloneDialogListener.onMiddleAlone(99);
                        dismiss();
                    }
                });
                break;
            case 100 : // 글쓰기 처음 쓸 때 경고 [확인]
                dialog.setText(getResources().getText(R.string.post_warning));
                positive.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        PropertyManager.getInstance().setWarning(true);
                        dismiss();
                    }
                });
                break;
            case 999 : // 날씨 정보가 없는 경우 [확인]
                dialog.setText(getResources().getText(R.string.sorry_to_user));
                positive.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dismiss();
                    }
                });
                break;
            case 1000 : // 계정 삭제한 경우 [확인]
                dialog.setText(getResources().getText(R.string.delete_user));
                getDialog().setCanceledOnTouchOutside(false);
                positive.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onMiddleAloneDialogListener.onMiddleAlone(1000);
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