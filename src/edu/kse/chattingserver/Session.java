package edu.kse.chattingserver;

import com.sun.istack.internal.NotNull;

import java.io.*;
import java.util.Scanner;

public class Session {

    private User user1;
    private User user2;

    Session(@NotNull User user1, @NotNull User user2){
        if(user1 == null || user2 == null){
            throw new NullPointerException();
        }
        this.user1 = user1;
        this.user2 = user2;
    }

    public void start(){

        ChattingSession session1 = new ChattingSession(user1, user2);
        ChattingSession session2 = new ChattingSession(user2, user1);

        session1.addOnCloseSessionListener(() -> stopSession(session2));
        session2.addOnCloseSessionListener(() -> stopSession(session1));

        session1.start();
        session2.start();
    }

    private void stopSession(ChattingSession session){
        if(session.isRunning()) {
            session.stopSession();
            user1.startRequestListener();
            user2.startRequestListener();
        }
    }
}
