package com.example.fm25.util;

import java.io.*;
import java.net.Socket;

public class NetWorkUtil {
    private Socket socket;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;

    public NetWorkUtil(Socket socket) throws IOException {
        this.socket = socket;
        openStream();
    }

    private void openStream() throws IOException {
        try {
            oos = new ObjectOutputStream(socket.getOutputStream());
            oos.flush();
            ois = new ObjectInputStream(socket.getInputStream());
            System.out.println("Streams opened for socket: " + socket);
        } catch (IOException e) {
            System.err.println("Error opening streams: " + e.getMessage());
            throw e;
        }
    }

    public Object read() throws IOException, ClassNotFoundException {
        try {
            System.out.println("Reading object from socket: " + socket);
            Object obj = ois.readObject();
            System.out.println("Read object: " + obj);
            return obj;
        } catch (IOException e) {
            System.err.println("Error reading from socket: " + e.getMessage());
            throw e;
        }
    }

    public void write(Object obj) throws IOException {
        try {
            System.out.println("Writing object: " + obj + " to socket: " + socket);
            oos.writeObject(obj);
            oos.flush();
            oos.reset();
        } catch (IOException e) {
            System.err.println("Error writing to socket: " + e.getMessage());
            throw e;
        }
    }

    public void closeNetwork() {
        try {
            if (ois != null) ois.close();
            if (oos != null) oos.close();
            if (socket != null && !socket.isClosed()) socket.close();
            System.out.println("Closed network for socket: " + socket);
        } catch (IOException e) {
            System.err.println("Error closing streams or socket: " + e.getMessage());
        }
    }
}