package edu.kse.chattingserver;

import edu.kse.logging.Log;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeoutException;

public class ChattingSession extends Thread{

    private final User user1;
    private final User user2;

    private List<OnCloseSessionListener> onCloseSessionListeners;

    private volatile boolean sessionRunning = true;

    public ChattingSession(User user1, User user2){
        if(user1 == null || user2 == null)
            throw new NullPointerException();
        this.user1 = user1;
        this.user2 = user2;
    }

    @Override
    public void run(){
        user1.println("Start chatting session");
        Log.i("Waiting for data ...");
        while (sessionRunning) {
            try {
                String data = user1.readLine();
//                Log.d("Got data : " + data.length());
                if (data.equalsIgnoreCase("Close")) {
                    user2.println(user1 + " has been close the session.");
                    break;
                }
                user2.println(data);
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }catch (TimeoutException e){
//                    Log.e("Read timeout");
            }
        }
        close();
    }

    public void close(){
        for(OnCloseSessionListener listener : onCloseSessionListeners){
            listener.onClose();
        }
    }

    public void stopSession(){
        sessionRunning = false;
        user1.startRequestListener();
        user2.startRequestListener();
    }

    public void addOnCloseSessionListener(OnCloseSessionListener listener){
        onCloseSessionListeners.add(listener);
    }

    public boolean isRunning() {
        return sessionRunning;
    }
}