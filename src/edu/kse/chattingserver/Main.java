package edu.kse.chattingserver;

import edu.kse.logging.Log;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static volatile List<User> users = new ArrayList<>();

    public static void main(String[] args) throws IOException {

        Log.enable();

        // Create server socket.
        int port = 4444;
        ServerSocket serverSocket = new ServerSocket(port);
        Log.d("Server socket has been created.");

        while (true) {
            // Wait to connect with a client.
            Socket socket = serverSocket.accept();
            // Print connect socket.
//            Log.d("A new socket has been connected.");
//            System.out.println(socket);

            // Create new user.
            User user = new User(socket);

            // Add current user to user's list.
//            Log.d("Add current user to user's list");
            users.add(user);

            // set OnRequestListener to the user to response when the request happens.
            user.addOnRequestListener(new OnRequestListener() {
                  @Override
                  public void onRequest(User remoteUser) {

                      // Stop remote user and current user to listen for requests.
                      remoteUser.stopListeningForRequests();
                      user.stopListeningForRequests();

                      user.println("Connect to " + remoteUser);
                      remoteUser.println("Connect to " + user);

                      // Create new session and start it.
                      Session session = new Session(user, remoteUser);
                      session.start();
                  }
            });
        }
    }
}