package pt.ulisboa.tecnico.cmov.simpleserver;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {

    public static final String FILES_PATH = "C:\\Users\\Diogo\\Desktop\\Files\\";
    private static ServerSocket serverSocket;
    private static Socket clientSocket;
    private static InputStreamReader inputStreamReader;
    private static BufferedReader bufferedReader;
    private static String message;

    public static void main(String[] args) {

        try {
            serverSocket = new ServerSocket(4444);
        } catch (IOException e) {
            System.out.println("Could not listen on port: 4444");
        }

        System.out.println("Server started. Listening to the port 4444");

        while (true) {
            try {
                clientSocket = serverSocket.accept();
                inputStreamReader = new InputStreamReader(clientSocket.getInputStream());
                bufferedReader = new BufferedReader(inputStreamReader);
                message = bufferedReader.readLine();

                JSONObject jsonRequest = new JSONObject(message);
                String request = jsonRequest.getString("request");
                String reply = "";

                String name;
                File file;
                String address;
                int phone_number;
                String email;

                switch (request) {
                    case "Create Contact":

                        name = jsonRequest.getString("name");
                        address = jsonRequest.getString("address");
                        phone_number = Integer.parseInt(jsonRequest.getString("phone_number"));
                        email = jsonRequest.getString("email");

                        Contact contact = new Contact(name, address, phone_number, email);

                        JSONObject jsonContact = new JSONObject();
                        jsonContact.put("name", name);
                        jsonContact.put("address", address);
                        jsonContact.put("phone_number", phone_number);
                        jsonContact.put("email", email);

                        FileWriter writer = new FileWriter(FILES_PATH + name);
                        writer.write(jsonContact.toString());
                        writer.close();

                        // TODO save contact

                        reply = contact.toString();
                        break;
                    case "Delete Contact":
                        name = jsonRequest.getString("name");
                        String filePath = FILES_PATH + name;
                        file = new File(filePath);
                        if(file.delete()){
                            System.out.println(file.getName() + " is deleted!");
                        }else{
                            System.out.println("Delete operation is failed.");
                        }
                        break;
                    case "List Contacts":


                        File folder = new File(FILES_PATH);
                        File[] listOfFiles = folder.listFiles();

                        for (File fileT : listOfFiles) {
                            if (fileT.isFile()) {
                                FileReader reader = new FileReader(fileT);
                                BufferedReader br = new BufferedReader(reader);
                                String s = "";
                                String t;
                                while((t = br.readLine()) != null) {
                                    s += t;
                                }

                                JSONObject jsonListContact = new JSONObject(s);

                                name = jsonListContact.getString("name");
                                address = jsonListContact.getString("address");
                                phone_number = jsonListContact.getInt("phone_number");
                                email = jsonListContact.getString("email");

                                Contact listContact = new Contact(name, address, phone_number, email);
                                System.out.println(listContact.toString());
                                reader.close();
                            }
                        }



                        break;
                }
                System.out.println(reply);
                inputStreamReader.close();
                clientSocket.close();

            } catch (IOException ex) {
                System.out.println("Problem in message reading");
            } catch (JSONException e) {
                System.out.println("Problem in json object creation : " + e.toString());
            }
        }
    }
}
