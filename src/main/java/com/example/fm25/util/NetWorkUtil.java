package com.example.fm25.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class NetWorkUtil {
    private Socket socket;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;

    public NetWorkUtil(Socket socket) throws Exception {
        this.socket = socket;
        openStream();
    }

    private void openStream(){
        try {
            oos = new ObjectOutputStream(socket.getOutputStream());
            ois = new ObjectInputStream(socket.getInputStream());
        } catch (Exception e) {
            System.err.println("Error opening streams: " + e.getMessage());
        }
    }

    public Object read() throws IOException, ClassNotFoundException {
        return ois.readUnshared();
    }

    public void write(Object obj) throws IOException {
        oos.writeUnshared(obj);
        oos.reset();
        oos.flush();
    }

    public void closeNetwork() {
        try {
            if (ois != null) ois.close();
            if (oos != null) oos.close();
            //if (socket != null) socket.close();
        } catch (IOException e) {
            System.err.println("Error closing streams: " + e.getMessage());
        }
    }
}
