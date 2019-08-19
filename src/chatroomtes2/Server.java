/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatroomtes2;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;


public class Server {

    static ServerSocket myServer; //server socket

    static Socket serviceSocket; //client socket

    private static Set<PrintWriter> writers = new HashSet<>();
    private static Set<ClientControl> clientsList = new HashSet<>(); //adds clients to list to write to

    static int i = 1;


    //listen and accept permission
    public static void main(String[] args) throws IOException {

        try {
            myServer = new ServerSocket(12038); //makes a server with port 12038
        } catch (IOException e) {
            System.out.println(e);
        }


        while (true) {

            serviceSocket = null; //client socket - makes null everytime so new clients can connect

            try {

                serviceSocket = myServer.accept(); //listens for connections and accepts
                System.out.println(serviceSocket + "connected.");
                
                //makes new datainput and dataoutput streams with new client
                DataInputStream in = new DataInputStream(serviceSocket.getInputStream());
                DataOutputStream out = new DataOutputStream(serviceSocket.getOutputStream());

                //Starts a clientcontrol object
                ClientControl client = new ClientControl(serviceSocket, i, in, out);

                //makes a thread with a clientcontrol object adds client to the list and starts thread
                Thread cc = new Thread(client);
                clientsList.add(client);
                cc.start();

                i++;

            } catch (IOException e) {
                serviceSocket.close();
                System.out.println(e);
            }

        }//end of while
    }

    //makes datainputstream
    public static DataInputStream receiveInput() {
        try {
            DataInputStream input = new DataInputStream(serviceSocket.getInputStream());
            return input;
        } catch (IOException e) {
            System.out.println(e);
            return null;
        }

    }

    //makes dataoutputstream
    public static DataOutputStream printMessage() {
        try {
            DataOutputStream output = new DataOutputStream(serviceSocket.getOutputStream());
            return output;
        } catch (IOException e) {
            System.out.println(e);
            return null;
        }

    }

    //closes sockets
    public void closeSocket() {
        try {

            serviceSocket.close();

        } catch (IOException e) {
            System.out.println(e);
        }
    }

    private static class ClientControl implements Runnable { //runnable thread to handle client input and output

        DataInputStream inS;
        DataOutputStream outS;
        Socket client;
        int name;
        Scanner scInput;
        PrintWriter pwOutput;

        //gives in the client socket a name and an input and output datastreams
        public ClientControl(Socket client, int name, DataInputStream in, DataOutputStream out) { //takes in client socket, dis and dos
            this.client = client;
            this.inS = in;
            this.outS = out;
            this.name = name;

        }

        //takes in the input into datainputstream, if its = to exit it closes the client socket
        @Override
        public void run() {
            try {
                String receive;
                while (true) {
                    receive = inS.readUTF();

                    if (receive.equalsIgnoreCase("exit")) {
                        System.out.println(client + " has left");
                        this.client.close();
                        break;
                    }

                    //this loops through the clientsList and sends a message to all the clients
                    for (ClientControl users : Server.clientsList) {
                        if (this.name != users.name) { //only sends it to the clients who didnt send the message
                            users.outS.writeUTF(receive); //write to client
                        }
                    }

                }
            } catch (Exception e) {
                System.out.println(e);

            }

        }

    }
}
