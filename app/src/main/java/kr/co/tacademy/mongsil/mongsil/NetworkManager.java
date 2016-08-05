package kr.co.tacademy.mongsil.mongsil;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by ccei on 2016-08-01.
 */
public class NetworkManager {
    private static NetworkManager instance;

    public static NetworkManager getInstance() {
        if (instance == null) {
            instance = new NetworkManager();
        }
        return instance;
    }
    // HTTP 연결을 관리하는 커넥션 풀
    ThreadPoolExecutor executor;
    // 쓰레드를 담는 블럭 큐
    BlockingQueue<Runnable> taskQueue =
            new LinkedBlockingQueue<Runnable>();

    private  NetworkManager() {
        executor = new ThreadPoolExecutor(
                3, 64, 10, TimeUnit.SECONDS,taskQueue);
    }
    // 요청한 쪽에서 결과를 필터할 수 있음
    public interface OnResultListener<T> {
        public void onSuccess(NetworkRequest<T> request, T result);
        public void onFailure(NetworkRequest<T> request,
                              int errorCode, int responseCode,
                              String message, Throwable exception);
    }

    private static final int MESSAGE_SUCCESS = 0;
    private static final int MESSAGE_FAILURE = 1;

    //결과 delicate 해주는 class
    static class NetworkHandler extends Handler {
        NetworkManager manager;
        public NetworkHandler(NetworkManager manager) {
            super();
            this.manager = manager;
        }

        public NetworkHandler(Looper looper, NetworkManager manager) {
            super(looper);
            this.manager = manager;
        }

        @Override
        public void handleMessage(Message msg) {
            NetworkRequest requestResult = (NetworkRequest) msg.obj;
            switch (msg.what) {
                case MESSAGE_SUCCESS :
                    requestResult.sendSuccess();
                    break;
                case MESSAGE_FAILURE :
                    requestResult.sendFailure();
                    break;
            }
            manager.requestList.remove(requestResult);
        }
    }
    Handler handler = new NetworkHandler(Looper.getMainLooper(), this);

    public void sendSuccess(NetworkRequest request) {
        Message msg = handler.obtainMessage(MESSAGE_SUCCESS, request);
        handler.sendMessage(msg);
    }

    public void sendFailure(NetworkRequest request) {
        Message msg = handler.obtainMessage(MESSAGE_FAILURE, request);
        handler.sendMessage(msg);
    }

    // 네트워크 요청 관리 리스트
    List<NetworkRequest> requestList = new ArrayList<NetworkRequest>();

    // 요청 객체 관리
    public <T> void getNetworkData(NetworkRequest<T> request,
                                   OnResultListener<T> listener) {
        requestList.add(request);

        request.setManager(this);
        request.setOnResultListener(listener);
        executor.execute(request);
    }

    void postCancelProcess(NetworkRequest request) {
        requestList.remove(request);
    }

    //Pool에 등록된 모든 객체 캔슬
    public void cancelAll (Object tag) {
        List<NetworkRequest> removeList = new ArrayList<NetworkRequest>();
        for(NetworkRequest r : removeList) {
            if (tag == null || r.getTag().equals(tag)) {
                removeList.add(r);
            }
        }
        for (NetworkRequest r : removeList) {
            r.cancel();
        }
    }

}
