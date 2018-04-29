package edu.kse.chattingserver;

import com.sun.istack.internal.NotNull;
import edu.kse.logging.Log;

import java.io.IOException;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class User {

    private Socket socket;
    private InetAddress address;
    private SocketAddress socketAddress;
    private final RequestListener requestListener = new RequestListener();
    private List<OnRequestListener> onRequestListeners = new ArrayList<>();

    public User(@NotNull Socket socket){
        Log.d("A new User has been created (Constructor 1).");
        if(socket == null){
            throw new NullPointerException();
        }
        this.socket = socket;
        socketAddress = socket.getRemoteSocketAddress();
        address = socket.getInetAddress();
        requestListener.listen();
    }

    public User(InetAddress address){
        Log.d("A new User has been created (Constructor 2).");
        this.address = address;
    }

    public void addOnRequestListener(OnRequestListener listener){
        Log.d("Add new request listener.");
        onRequestListeners.add(listener);
    }

    public Socket getSocket() {
        return socket;
    }

    public InetAddress getAddress() {
        return address;
    }

    public List<OnRequestListener> getOnRequestListeners() {
        return onRequestListeners;
    }

    @Override
    public boolean equals(Object obj) {
        Log.d("Does " + address.getHostAddress() + " equals " + obj + "?");
        if(obj instanceof User){
            User user = (User)obj;
            return address == user.getAddress();
        }
        return false;
    }

    @Override
    public String toString() {
        return "User@(" + address + ")";
    }

    public void stopListeningForRequests(){
        Log.d(this.address.getHostAddress() + " Stop listening for requests");
        requestListener.interrupt();
    }

    private class RequestListener extends Thread{

        private void listen(){
            start();
        }

        @Override
        public void interrupt() {
            super.interrupt();
        }

        @Override
        public void run() {

            User user = User.this;

            Socket socket = user.getSocket();
            try {
                Scanner scanner = new Scanner(socket.getInputStream());
                PrintStream printer = new PrintStream(socket.getOutputStream());

                while (true) {
                    String data = scanner.nextLine();

                    if(data.equalsIgnoreCase("Close")){
                        break;
                    }
                    String ip = data;
                    User remoteUser = null;
                    try {
                        remoteUser = new User(InetAddress.getByName(ip));
                    }catch (UnknownHostException e){
                        printer.println(e.getMessage());
                        continue;
                    }

                    if (user.equals(remoteUser)) {
                        printer.println("Conn't connect to yourself.");
                        continue;
                    }

                    Log.d("Searching for " + ip + " ...");
                    String msg = ip + " not found.\n";
                    for (User u : Main.users) {
                        if (remoteUser.equals(u)) {
                            Log.d("Connecting to " + ip + " ...");
                            for (OnRequestListener listener : user.getOnRequestListeners()) {
                                listener.onRequest(u);
                            }
                            msg = "";
                            break;
                        }
                    }

                    printer.print(msg);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
