/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatroomtes2;

import java.awt.Component;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;
import javax.swing.JOptionPane;

public class Client {

    static Socket myClient;
    public static DataInputStream input;
    public static DataOutputStream output;
    public String name;

    public Client(String name) {
        this.name = name;
    }

    public void clientRun() {

        try {

            Scanner sc = new Scanner(System.in); //input so user can send messages

            //gets the ip of localhost and hostname and displays the ip.
            InetAddress ip = InetAddress.getByName("localhost");
            String ipName = InetAddress.getLocalHost().getHostName();
            System.out.println("Server name: " + ip);
            
            //gets ip from user to connect to server
            String ipAdd = JOptionPane.showInputDialog("Enter IP address of host");

            //makes a socket using the ip given and the port number 12038
            myClient = new Socket(ipAdd, 12038);
            input = receiveInput(); //this method starts a datainputstream
            output = printMessage(); //this method starts a dataoutputstream

            //this is the writing thread that will send the users input
            Thread write = new Thread(new Runnable() { //runnable 

                @Override
                public void run() { //overrides the run method
                    try {

                        while (true) { //gets user input and sends it through to dataoutputstream
                            String message = sc.nextLine();

                            if (message.equalsIgnoreCase("exit")) {
                                output.writeUTF(name + " has left");
                                myClient.close();
                                break;
                            }
                            output.writeUTF("<" + name + ">: " + message);
                        }

                    } catch (Exception e) {
                        System.out.println(e);
                    }

                }
            });
            write.start(); //starts the write thread

            //this is a thread to read the incoming datainputstream
            Thread read = new Thread(new Runnable() {
                public void run() {
                    try {
                        while (true) {
                            System.out.println(input.readUTF()); //prints out the input
                        }

                    } catch (Exception e) {
                        System.out.println("Socket Closed");
                    }
                }
            });
            read.start(); //starts the thread to read

        } catch (IOException e) {
            System.out.println(e);
        }

    }

    //returns the datainputstream using myClient
    public static DataInputStream receiveInput() { 
        try {
            DataInputStream input = new DataInputStream(myClient.getInputStream());
            return input;
        } catch (IOException e) {
            System.out.println(e);
            return null;
        }
    }
    
    
    //return the dataoutputstream using myClient
    public static DataOutputStream printMessage() {
        try {
            DataOutputStream output = new DataOutputStream(myClient.getOutputStream());
            return output;
        } catch (IOException e) {
            System.out.println(e);
            return null;
        }

    }

}
