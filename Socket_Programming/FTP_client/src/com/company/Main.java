package com.company;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;
//Client Side
public class Main
    {
        public static BufferedReader freader = new BufferedReader (new InputStreamReader (System.in));
        public static void main(String[] args) throws IOException, InterruptedException {

            System.out.println ("Please enter the IP address of the server you want  to get connected to:");
            String ipaddress = freader.readLine ();
            System.out.println ("Please enter the port you want  to get connected through:");
            String portno = freader.readLine ();

            Socket client = new Socket (ipaddress, Integer.parseInt(portno));
            String filename;
            InputStream in = client.getInputStream ();
            DataInputStream input = new DataInputStream (in);
            OutputStream out = client.getOutputStream ();
            DataOutputStream response = new DataOutputStream (out);


            while (true) {

                firstWindow (client, "");

            }
        }

        //first window
        public static void firstWindow(Socket s,String start) throws IOException {
            Socket sockets=s;
            InputStream in = s.getInputStream ();
            DataInputStream input = new DataInputStream (in);
            OutputStream out = s.getOutputStream ();
            DataOutputStream response = new DataOutputStream (out);
            System.out.println (start+input.readUTF ());
            System.out.println ("choose what you want to do");
            //Scanner scan = new Scanner (System.in);
            String c = freader.readLine ();
            String a="";

            //System.out.println (a);
            response.writeUTF (c);
            boolean set = true;
            System.out.println (input.readUTF ());
            String reply=null;

            switch (c){
                case "1"://New USer
                    while (set==true) {
                        System.out.println ("Enter the username");
                        a = freader.readLine ();
                        while (!checkUsername (a)) {
                            System.out.println ("Reenter username only alphanumeric");
                            a = freader.readLine ();
                        }


                        //System.out.println (input.readUTF ());
                        System.out.println ("Enter the password");
                        String b = freader.readLine ();
                        while (!checkPassword (b)) {
                            System.out.println ("Reenter password NO * in the password");
                            b = freader.readLine ();
                        }
                        response.writeUTF (a + "*" + b);
                        a=input.readUTF ();
                        if (a.equals ("CREATEFAILED")) {
                            set = true;
                        } else
                            set = false;
                    }
                   reply=a;
                    break;
                case "2"://Existing User
                    int i=0;
                    while (set==true) {
                        System.out.println ("Enter the username");
                        a = freader.readLine ();
                        while (!checkUsername (a)) {
                            System.out.println ("Reenter username only alphanumeric");
                            a = freader.readLine ();
                        }


                        //System.out.println (input.readUTF ());
                        System.out.println ("Enter the password");
                        String b = freader.readLine ();
                        while (!checkPassword (b)) {
                            System.out.println ("Reenter password NO * in the password");
                            b = freader.readLine ();
                        }
                        response.writeUTF (a+"*"+b);
                        reply=input.readUTF ();
                        String[] exUser=a.split("\\*");
                        i++;
                        if ((exUser[0].equals ("LOGINERROR"))&i<3) {
                            set = true;
                        }
                        else if(exUser[0].equals("LOGINERROR")&i==3){
                            firstWindow (sockets,exUser[0]);
                        }
                        else
                            set = false;
                    }
                    break;

                case "3"://disconnect User

                    System.out.println(input.readUTF ());
                    s.close ();
                    break;
            }
            secondWindow(sockets,reply);


        }
//Second window

        public static void secondWindow(Socket s, String str) throws IOException {
            Socket sockets =s;
            String filename;
            String reply=str;
            InputStream in = s.getInputStream ();
            DataInputStream input = new DataInputStream (in);
            OutputStream out = s.getOutputStream ();
            DataOutputStream response = new DataOutputStream (out);
            System.out.println(str);
            String a=freader.readLine ();
            //download scenario
            if(a.equals("1")) {
                response.writeUTF (a);
                String fd=input.readUTF ();
                if(fd.equals("READY")) {
                    System.out.println ("Filename Please");
                    filename = freader.readLine ();
                    response.writeUTF (filename);
                    String[] found=input.readUTF ().split ("\\*");
                    if((found[0]).equals ("FOUND")) {
                        response.writeUTF ("READY");
                        recieve (s, filename);
                        response.writeUTF ("DOWNLOADCOMPLETED");
                        secondWindow (sockets,reply);
                    }
                    else if(found[0].equals ("FILEDOWNLOADFAILED")){
                        secondWindow (sockets,found[1]);

                    }
                }


            }
            //Upload scenario
            else if(a.equals("2"))
            {
                response.writeUTF (a);
                if(input.readUTF ().equals ("READY")) {
                    //filename = freader.readLine ();
                    System.out.println ("Enter the filename");
                    filename= freader.readLine ();
                    int i=0;
                    while(filenamExist (filename)==false&i<2)
                    {
                        System.out.println ("Enter the filename again");
                        filename= freader.readLine ();
                        i++;
                    }
                    if(i==2)
                    {
                        response.writeUTF ("ERROR");
                        secondWindow (sockets,reply);
                    }
                    response.writeUTF (filename);
                    if(input.readUTF ().equals("CONTINUE")) {
                        send (s, filename);
                        secondWindow(sockets,reply);
                    }
                }
            }
            //file list scenario
            else if(a.equals("3"))
            {
                response.writeUTF(a);
                String fileList=input.readUTF ();
                if(fileList!=null) {
                    System.out.println (input.readUTF ());
                    response.writeUTF ("SUCCESSFUL");
                    secondWindow(sockets,reply);
                }
                else{
                    System.out.println ("You don't have any files bro");

                }

            }
            else if(a.equals("4")){
                System.out.println(input.readUTF ());
                s.close ();

            }

            else {
                System.out.println ("Error Occured");
            }



        }

        //Check if file exist in computer
        public static boolean filenamExist(String filename) throws IOException
        {
            try {
                File newFile = new File ("/Users/oshanoshu/Desktop/Client/" + filename);
                Scanner input=new Scanner (newFile);
            }
            catch (FileNotFoundException e)
            {
                System.out.println("The file was not found.");
                return false;
            }
            return true;
        }


        //send file to the receiver
        public static void recieve(Socket client,String filename) throws IOException {
            File file = new File("/Users/oshanoshu/Desktop/Client/"+filename);
        // Get the size of the file
        long length = file.length();
        byte[] bytes = new byte[16 * 1024];
        FileOutputStream in = new FileOutputStream (file);
        DataInputStream out = new DataInputStream (client.getInputStream());
        out.read (bytes);
        in.write(bytes);

        System.out.println("File Recieved Successfully");

        }



        //Send file to the server
        public static void send(Socket sock, String filename) throws IOException {

            File newFile=new File("/Users/oshanoshu/Desktop/Client/"+filename);
            byte [] mybytearray  = new byte [(int)newFile.length()];
            FileInputStream fis = new FileInputStream(newFile);
            BufferedInputStream bis = new BufferedInputStream(fis);
            bis.read(mybytearray,0,mybytearray.length);
            DataOutputStream os = new DataOutputStream (sock.getOutputStream());
            // DataInputStream os = new DataInputStream (sock.getOutputStream());
            //System.out.println("Sending " + FILE_TO_SEND + "(" + mybytearray.length + " bytes)");
            os.write(mybytearray,0,mybytearray.length);
            os.flush();
            System.out.println("File Transferred");

        }

        //Check Username validation
        public static boolean checkUsername(String a)
        {
            boolean isAlphaNumeric = a != null &&
                    a.chars().allMatch(Character::isLetterOrDigit);
            return isAlphaNumeric;
        }
        //Check Password Validation
        public static boolean checkPassword(String a)
        {

            if(a.contains ("*")) {
                return false;
            }
            return true;
        }
    }

