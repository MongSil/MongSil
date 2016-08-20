package kr.co.tacademy.mongsil.mongsil;

import android.os.Handler;
import android.provider.SyncStateContract;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.widget.EditText;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import io.socket.client.IO;
import io.socket.client.Socket;

/**
 * Created by ccei on 2016-08-19.
 */
public class MainSocketPostFragment extends Fragment{

    private static final int REQUEST_LOGIN = 0;

    private RecyclerView mMessagesView;
    private EditText mInputMessageView;

    //실제 채팅메세지가 모여져 있는 곳
    private List<PostSocket> mMessages = new ArrayList<PostSocket>();
    //채팅메세지 어댑터
    private RecyclerView.Adapter mAdapter;

    //현재 상대방이 입력중임을 채팅상대들에게 보여주기 위함.
    private boolean mTyping = false;

    //타이핑을 핸들링하기 위한 Handler
    private Handler mTypingHandler = new Handler();

    //Handler delay time
    private static final int TYPING_TIMER_LENGTH = 600;
    //내 이름(별명)
    private String mUsername;
    /*
     이 EngineIO(Socket.io의 자바버전)는 반드시 Main Thread에서
     생성해야 한다.
     */
    private Socket mSocket;
    {
        try {
            //채팅하고자 하는 http URL을 설정하고 소켓을 얻어온다.
            //IO#Options 내부클래스를 이용하여 다양한 소켓옵션을 선언할 수 있다(https등)
            mSocket = IO.socket(NetworkDefineConstant.GET_SERVER_POST);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
    public MainSocketPostFragment() {
        super();
    }



}
