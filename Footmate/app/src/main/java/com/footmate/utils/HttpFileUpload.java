package com.footmate.utils;

import android.content.Context;
import android.util.Log;


import com.footmate.listeners.ExceptionListener;
import com.footmate.listeners.ResponseListener;

import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;



public class HttpFileUpload implements Runnable{
    URL connectURL;
    String responseString;
    String Title;
    String Description;
    byte[ ] dataToServer;
    FileInputStream fileInputStream = null;
//    FileInputStream fileInputStream = new FileInputStream("");

    // Set your server page url (and the file title/description)

 //   HttpFileUpload hfu = new HttpFileUpload("wcf.sal.com/kdn.aspx", eid+".jpg","my file description");


    public HttpFileUpload(String urlString, String vTitle, String vDesc,String ImagePath,Context C, ResponseListener res, ExceptionListener exc)
    {
            try{
            	
             connectURL = new URL(urlString);
            Title= vTitle;
            Description = vDesc;
            Log.d("", "2");
            FileInputStream fileInputStream = new FileInputStream(ImagePath);
            Log.d("", "3");
            Send_Now(fileInputStream);
            Log.d("", "End");
            res.handleResponse("SENT");
            
        }catch(Exception ex){
        	ex.printStackTrace();
                Log.i("HttpFileUpload","URL Malformatted");
                exc.handleException("Error !!! Storing Image");
        }
    }
    
    String Send_Now(FileInputStream fStream)
    {
           fileInputStream = fStream;
           Log.d("", "4");
        String server_response=Sending();
        Log.d("", "server_response"+server_response);
        Log.i("", "server_response"+server_response);
        return server_response;
    }
    
    String   Sending(){
            String iFileName = Title;
            String lineEnd = "\r\n";
            String twoHyphens = "--";
            String boundary = "*****";
            String Tag="fSnd";
            String res;
            try
            {
                    Log.e(Tag,"Starting Http File Sending to URL");
    
                    // Open a HTTP connection to the URL
                    HttpURLConnection conn =
(HttpURLConnection)connectURL.openConnection();
    
                    // Allow Inputs
                    conn.setDoInput(true);
    
                    // Allow Outputs
                    //conn.setDoOutput(true);
                    conn.setDoOutput(true);
                    // Don't use a cached copy.
                    conn.setUseCaches(false);
    
                    // Use a post method.
                    conn.setRequestMethod("POST");
    
                    conn.setRequestProperty("Connection", "Keep-Alive");
    
                    conn.setRequestProperty("Content-Type","multipart/form-data;boundary="+boundary);
    
                    DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
    
                    dos.writeBytes(twoHyphens + boundary + lineEnd);
                    dos.writeBytes("Content-Disposition: form-data;name=\"title\""+ lineEnd);
                    dos.writeBytes(lineEnd);
                    dos.writeBytes(Title);
                    dos.writeBytes(lineEnd);
                    dos.writeBytes(twoHyphens + boundary + lineEnd);
                    
                           
                    dos.writeBytes("Content-Disposition: form-data;name=\"description\""+ lineEnd);
                    dos.writeBytes(lineEnd);
                    dos.writeBytes(Description);
                    dos.writeBytes(lineEnd);
                    dos.writeBytes(twoHyphens + boundary + lineEnd);
                            
                    dos.writeBytes("Content-Disposition: form-data;name=\"UploadedFile\";filename=\"" + iFileName +"\"" +
lineEnd);
                    dos.writeBytes(lineEnd);
    
                    Log.e(Tag,"Headers are written"+dos+"");
    
                    // create a buffer of maximum size
                    int bytesAvailable = fileInputStream.available();
                            
                    int maxBufferSize = 1024*1024*100;
                    int bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    byte[ ] buffer = new byte[bufferSize];
    
                    // read file and write it into form...
                    int bytesRead = fileInputStream.read(buffer, 0, bufferSize);
    
                    while (bytesRead > 0)
                    {
                            dos.write(buffer, 0, bufferSize);
                            bytesAvailable = fileInputStream.available();
                            bufferSize = Math.min(bytesAvailable,maxBufferSize);
                            bytesRead = fileInputStream.read(buffer, 0,bufferSize);
                    }
                    dos.writeBytes(lineEnd);
                    dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
    
                    // close streams
                    fileInputStream.close();
                            
                    dos.flush();
                            
                    Log.e(Tag,"File Sent, Response:"+String.valueOf(conn.getResponseCode()));
                             
                    InputStream is = conn.getInputStream();
                            
                    // retrieve the response from server
                    int ch;
    
                    StringBuffer b =new StringBuffer();
                    while( ( ch = is.read() ) != -1 ){ b.append( (char)ch ); }
                    String s=b.toString();
                    res=b.toString();
                    
                    Log.i("Response-->",s);
                    
                    
                    dos.close();
                   
//                    DiscCacheUtils.removeFromCache(url, ImageLoader.getInstance().getDiscCache());
                    
                   
            }
            catch (MalformedURLException ex)
            {
                    Log.e(Tag, "URL error: " + ex.getMessage(), ex);
                    res="-1";
            }
    
            catch (IOException ioe)
            {
                    Log.e(Tag, "IO error: " + ioe.getMessage(), ioe);
                    res="-1";
            }
            return  res;
    }
    
    @Override
    public void run() {
            // TODO Auto-generated method stub
    }
}
