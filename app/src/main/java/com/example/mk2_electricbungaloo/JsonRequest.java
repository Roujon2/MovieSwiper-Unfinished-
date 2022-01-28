package com.example.mk2_electricbungaloo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

// ALL THIS I COPIED FROM A TUTORIAL I DON'T KNOW WHAT IT DOES
public class JsonRequest {

    private static HttpURLConnection connection;

    static String requestInfo(URL url){

        BufferedReader reader;
        String line;
        StringBuffer responseContent = new StringBuffer();

        try {

            connection = (HttpURLConnection) url.openConnection();

            // Request setup
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            int status = connection.getResponseCode();
            //System.out.println(status); TO CHECK CONNECTION STATUS


            if(status > 299){
                reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
                while((line = reader.readLine()) != null){
                    responseContent.append(line);
                }
                reader.close();
            }else{
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                while((line = reader.readLine()) != null){
                    responseContent.append(line);
                }
                reader.close();
            }

            return responseContent.toString();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            connection.disconnect();
        }
        return "Error: 69";
    }

}

