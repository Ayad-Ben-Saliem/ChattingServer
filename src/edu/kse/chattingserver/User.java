package edu.kse.chattingserver;

import com.sun.istack.internal.NotNull;
import edu.kse.logging.Log;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
//import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;

public class User {

    private final Socket socket;
    private final InetAddress address;
//    private final SocketAddress socketAddress;
    private final InputStream inputStream;
    private final OutputStream outputStream;
    private final Scanner scanner;
    private final PrintStream printer;
    private final RequestListener requestListener = new RequestListener();
    private final List<OnRequestListener> onRequestListeners = new ArrayList<>();

    private volatile boolean listenToRequests = true;

    public User(@NotNull Socket socket) throws IOException{
        Log.d("A new User has been created (Constructor 1).");
        if(socket == null){
            throw new NullPointerException();
        }
        this.socket = socket;
//        socketAddress = socket.getRemoteSocketAddress();
        address = socket.getInetAddress();

        inputStream = socket.getInputStream();
        outputStream = socket.getOutputStream();

        scanner = new Scanner(inputStream);
        printer = new PrintStream(outputStream);

        startRequestListener();
    }

    private User(InetAddress address){
        Log.d("A new User has been created (Constructor 2).");

        socket = null;
        scanner = null;
        printer = null;
//        socketAddress = null;
        inputStream = null;
        outputStream = null;
        this.address = address;
    }

    void addOnRequestListener(OnRequestListener listener){
        Log.d("Add new request listener.");
        onRequestListeners.add(listener);
    }

    public Socket getSocket() {
        return socket;
    }

    public InetAddress getAddress() {
        return address;
    }


    private List<OnRequestListener> getOnRequestListeners() {
        return onRequestListeners;
    }

    void println(String data){
        print(data);
        print("\n");
    }

    private void print(String data){
        synchronized (printer) {
            printer.print(data);
        }
    }

    public String readLine() throws IOException, InterruptedException, TimeoutException {
        int n = 0;
        while (inputStream.available() == 0 && n++<10)
            Thread.sleep(1);
        if(inputStream.available() == 0)
            throw new TimeoutException();

        synchronized (scanner) {
            return scanner.nextLine();
        }
    }

    void stopListeningForRequests(){
        Log.d(this + " Stop listening for requests");
        listenToRequests = false;
    }

    void startRequestListener(){
        requestListener.start();
    }

    private void close() throws IOException {
        socket.close();
    }

    @Override
    public boolean equals(Object obj) {
        Log.i("Does " + this + " equals " + obj + "?");
        if(obj instanceof User){
            User user = (User)obj;
            boolean status = this.toString().equals(user.toString());
            Log.d(status? "Yes" : "No");
            return status;
        }
        Log.d("No");
        return false;
    }

    @Override
    public String toString() {
        return "User@(" + address + ")";
    }

    private class RequestListener extends Thread {

        @Override
        public void run() {

            User user = User.this;

            while (listenToRequests) {

                try {
                    String data = user.readLine();
                    if(data.equalsIgnoreCase("Close")){ break; }

                    Log.i("New request");

                    String ip = data;
                    User remoteUser;
                    try {
                        remoteUser = new User(InetAddress.getByName(ip));
                    }catch (UnknownHostException e){
                        println(e.getMessage());
                        continue;
                    }

                    if (user.equals(remoteUser)) {
                        user.println("Conn't connect to yourself.");
                        continue;
                    }

                    Log.d("Searching for " + remoteUser + " ...");
                    String msg = remoteUser + " not found.\n";
                    for (User u : Main.users) {
                        if (remoteUser.equals(u)) {
                            Log.d("Connecting " + User.this + " to " + u + " ...");
                            user.println("Connecting to " + remoteUser + "...");
                            for (OnRequestListener listener : user.getOnRequestListeners()) {
                                listener.onRequest(u);
                            }
                            msg = "";
                            break;
                        }
                    }

                    user.println(msg);

                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }catch (TimeoutException e){
//                    Log.e("Read timeout");
                }
            }

            try {
                user.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
