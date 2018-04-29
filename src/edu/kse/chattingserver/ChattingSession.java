package edu.kse.chattingserver;

import java.io.PrintStream;
import java.util.Scanner;

public class ChattingSession extends Thread{
    private Scanner scanner;
    private PrintStream printer;

    public ChattingSession(Scanner scanner, PrintStream printer){
        if(scanner == null || printer == null)
            throw new NullPointerException();
        this.scanner = scanner;
        this.printer = printer;
    }

    @Override
    public void run(){
        while (true) {
            String data = scanner.nextLine();
            printer.println(data);
            if (data.equalsIgnoreCase("Close"))
                break;
        }
    }
}