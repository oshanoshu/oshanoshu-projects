package com.company;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

interface function1 {
    void newUsers() throws IOException;
}

public class thread implements Runnable {

    //Socket
    private Socket s;
    //all frequently used String defined
    public String ready="READY";
    public String createFailed="CREATEFAILED";
    public String firstWindow="1. New User\n2. Existing User\n3. Disconnect";
    public String secondWindow="1. Download\n2. Upload\n3. File List\n4. Disconnect";
    public String loginError="LOGINERROR";
    public String found="FOUND";
    public String downloadCompleted="DOWNLOADCOMPLETED";
    public String fileDownloadFailed="FILEDOWNLOADFAILED";
    public String error="ERROR";
    public String successful="SUCCESSFUL";
    public String userpw=null;
    public String username=null;

    //For transfer of data stream over the connection
    public DataOutputStream response;
    public DataInputStream input;

    //Constructors defined
    public thread(Socket s,DataOutputStream response,DataInputStream input)  {
        this.s=s;
        this.response=response;
        this.input=input;

    }

    public void run() {






            try {



//First window upon connection
                    String str="";
                    firstWin(str);
            }



            catch (IOException e1) {
                e1.printStackTrace ();
            }





        }

//First window
    public void firstWin(String strings) throws IOException {
        //DataOutputStream response=new DataOutputStream (s.getOutputStream ());
        String newUser="Please send new id and password!!!";
        response.writeUTF (strings+firstWindow);
        String ans=input.readUTF ();
        int answer=Integer.parseInt (ans);
        switch(answer) {
            //For new User scenario
            case 1:

                response.writeUTF (ready + "*" + newUser);
                userpw=newUsers ();
                username=getUsername (userpw);
                addToFile (userpw,"");
                //calls the second window
                secondWin (username,userpw);
                break;
//For existing user scenario
            case 2:
                int i = 0;
                response.writeUTF (ready);
                userpw = input.readUTF ();
                username = getUsername (userpw);
                //Validates the user
                while ((userValidation (userpw) == false) & i < 2) {

                    response.writeUTF (loginError);
                    userpw = input.readUTF ();

                    i++;
                }
                if ((userValidation (userpw) == false)&i==2) {
                    response.writeUTF(loginError);
                    firstWin (ready+"*");

                }
                break;

//For disconnect scenario
            case 3:
                response.writeUTF ("You chose to disconnect");
                input.close ();
                response.close ();
                s.close ();
                break;

                //input.close ();


        }
        //Calls the second window
        secondWin(username,userpw);
    }

    //the second window
    public void secondWin(String us,String usp) throws IOException{
        int answer = 0;
        username=us;
        userpw=usp;



            response.writeUTF (secondWindow);
            //Thread.sleep (2000);
            answer = Integer.parseInt (input.readUTF ());
            switch(answer)
            {
                //Download Scenario
            case 1:
                response.writeUTF (ready);
                String filename=input.readUTF ();
                if(fileExist (filename,username)==true)
                {
                    response.writeUTF (found);
                    if(input.readUTF().equals (ready))
                    send(s,username,filename);
                }
                else{
                    response.writeUTF (fileDownloadFailed+"*"+secondWindow);
                }
                if(input.readUTF ().equals (downloadCompleted))
                secondWin (username,userpw);
                break;


                case 2://Upload Scenario
                response.writeUTF (ready);
                String fu=input.readUTF ();
                if(fu.equals (error)){
                    secondWin(username,userpw);
                }
                else {

                    if ((filename = fu) != null) {
                        int i=1;
                        while(fileExist (filename,username)==true){

                            //filename=filename+"_copy_"+i+".txt";
                            filename.replace (".txt","_copy_"+i+".txt");
                            i++;

                        }
                        response.writeUTF ("CONTINUE");
                        recieve (s, username, filename);
                        addToFile (userpw,filename);
                        secondWin (username,userpw);

                    }
                }
                break;
//File List scenario
                case 3:

                    BufferedReader outfile=new BufferedReader (new FileReader ("/Users/oshanoshu/Desktop/account.txt"));
                    String line=outfile.readLine ();
                    while(line!=null){
                        if(line.contains (userpw)) {
                            String[] files = line.split (" ");
                            int i = 1;
                            line = "";
                            for (i=1;i<(files.length);i++) {

                            line += files[i] + " ";

                            }
                            response.writeUTF (line);
                            if(input.readUTF ().equals (successful)){
                            secondWin(username,userpw);

                                }

                    }
                        else {
                        line = outfile.readLine ();
                        }
                }
                break;
//Disconnect scenario
                case 4:
                    response.writeUTF ("You chose to disconnect");
                    input.close ();
                    response.close ();
                    s.close ();
                    break;
            }






    }
    //get username separating delimeter
    public String getUsername(String userpassword){
        //usrpw=input.readUTF ();
        String[] user=userpassword.split ("\\*");
        username=user[0];
        return username;


    }
    //create new user
    public String newUsers() throws IOException {
        userpw = input.readUTF ();
        username = getUsername (userpw);
        while (checkUsername (username)) {
            response.writeUTF (createFailed);
            newUsers ();
        }

return userpw;
    }

//Check username if already exist

    public boolean checkUsername(String username) throws IOException {
        BufferedReader outfile=new BufferedReader (new FileReader ("/Users/oshanoshu/Desktop/account.txt"));
        String line=outfile.readLine ();
        while(line!=null){

                String[] files = line.split (" ");
                if(files[0].contains (username)){
                    return true;
                }
            line=outfile.readLine ();

            }
        return false;
    }
//check file if already exist
    public boolean fileExist(String filename,String username) throws IOException
    {
        BufferedReader infile=new BufferedReader (new FileReader ("/Users/oshanoshu/Desktop/account.txt"));
        //String file=infile.readLine ();
        String add;
        while((add=infile.readLine ())!=null)
        {
            if(add.startsWith (username)&add.contains (filename))
            {
                return true;
            }
        }
        return false;

    }
    //add changes to the account.txt
    public void addToFile(String userpw,String filename) throws IOException
    {
        List<String> lines=new ArrayList<String> ();
        File file1=new File("/Users/oshanoshu/Desktop/account.txt");

        //file2.createNewFile ();
        BufferedReader outfile=new BufferedReader (new FileReader (file1));
        String line=outfile.readLine ();
        boolean set=true;
        while(line!=null){

            String[] files = line.split (" ");
            if(files[0].equals (userpw)){
                line+=(" "+filename);
                set=false;
            }
            lines.add(line+"\n");
            line=outfile.readLine ();

        }
        if(set==true)
        {
            //writer.write(userpw);

            lines.add(userpw+"\n");

        }
        outfile.close ();
        file1.delete ();

        BufferedWriter infile=new BufferedWriter (new FileWriter (file1));
        for(String s : lines)
            infile.write (s);
            infile.flush ();
            infile.close ();

    }
//Validate user from the account.txt
    public boolean userValidation(String userpassword) throws IOException {
        BufferedReader outfile=new BufferedReader (new FileReader ("/Users/oshanoshu/Desktop/account.txt"));
        String line=outfile.readLine ();
        while(line!=null){

            String[] files = line.split (" ");
            if(files[0].equals (userpassword)){
                return true;
            }
            line=outfile.readLine ();


        }
        //return true;
        return false;

    }
//send file
    public void send(Socket sock,String user,String filename) throws IOException {
        new File("/Users/oshanoshu/Desktop/Files/"+user).mkdir ();
        File newFile=new File("/Users/oshanoshu/Desktop/Files/"+user+"/"+filename);
        byte [] mybytearray  = new byte [(int)newFile.length()];
        FileInputStream fis = new FileInputStream(newFile);
        BufferedInputStream bis = new BufferedInputStream(fis);
        bis.read(mybytearray,0,mybytearray.length);
       response.write(mybytearray,0,mybytearray.length);
        response.flush();
        System.out.println("File Transferred");

    }
    //receive files
    public void recieve(Socket client,String user,String filename) throws IOException {
        new File("/Users/oshanoshu/Desktop/Files/"+user).mkdir ();
        File file = new File("/Users/oshanoshu/Desktop/Files/"+user+"/"+filename);
        byte[] bytes = new byte[16 * 1024];
        FileOutputStream in = new FileOutputStream (file);
        input.read ();
        in.write(bytes);
        System.out.println("File Recieved Successfully");

    }


}










