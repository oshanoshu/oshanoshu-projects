package com.company;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executors;

public class Main {

    public static void main(String[] args) throws IOException {
        // write your code here

        try {
            ServerSocket listener = new ServerSocket (8000);
            Socket socket = null;
            //Read the file for display in front
            BufferedReader infile=new BufferedReader (new FileReader ("/Users/oshanoshu/Desktop/account.txt"));
            String file=infile.readLine ();
            String add;
            while((add=infile.readLine ())!=null)
            {
                file+=("\n"+add);
            }
            System.out.println (file);



    int i = 0;
    while (i < 5) {

        socket=listener.accept ();
        DataOutputStream response=new DataOutputStream (socket.getOutputStream ());
        DataInputStream input=new DataInputStream (socket.getInputStream ());
        //Calls the thread
        Thread t = new Thread (new thread (socket,response,input));
        t.start ();
        i++;
        //socket.close();
    }
    //Closes the connection when more than five
    while(i==5)
    {
        socket = listener.accept ();
        DataOutputStream response=new DataOutputStream (socket.getOutputStream ());
        DataInputStream input=new DataInputStream (socket.getInputStream ());
        response.writeUTF("maximum connections being made and can’t take any additional connection at this time. Please try again!!!");
        socket.close();
    }

   //Closes the connection
socket.close();
        } catch (Exception e) {
            e.printStackTrace ();
        }



        //System.out.println("The number of clients is "+(i+1));


    }
}
