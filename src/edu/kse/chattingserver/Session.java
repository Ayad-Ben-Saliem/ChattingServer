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

        try {
            Scanner scanner1 = new Scanner(user1.getSocket().getInputStream());
            PrintStream printer1 = new PrintStream(user1.getSocket().getOutputStream());

            Scanner scanner2 = new Scanner(user2.getSocket().getInputStream());
            PrintStream printer2 = new PrintStream(user2.getSocket().getOutputStream());

            ChattingSession session1 = new ChattingSession(scanner1, printer2);
            ChattingSession session2 = new ChattingSession(scanner2, printer1);

            session1.start();
            session2.start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
