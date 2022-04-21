package br.com.omarcovelho.sockets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Logger;

public class EchoMultiServer {
    private static Logger log = Logger.getLogger(EchoMultiServer.class.getName());

    private ServerSocket serverSocket;

    public void start(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        log.info("Server is ready to accept connections");
        while(true) {
            Socket clientSocket = serverSocket.accept();
            log.info(String.format("A connection was made to the server from %s", clientSocket.getPort()));
            EchoClientHandler handler = new EchoClientHandler(clientSocket);
            log.info(String.format("Created %s for %s:%s, starting...", handler, clientSocket.getInetAddress(), clientSocket.getPort()));
            handler.start();
        }
    }

    public void stop() throws IOException {
        serverSocket.close();
    }

    private static class EchoClientHandler extends Thread {
        private static Logger log = Logger.getLogger(EchoClientHandler.class.getName());

        private Socket clientSocket;
        private PrintWriter out;
        private BufferedReader in;

        public EchoClientHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        @Override
        public void run() {
            try {
                out = new PrintWriter(clientSocket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                String inputLine;
                while((inputLine = in.readLine()) != null) {
                    if(".".equals(inputLine)) {
                        out.println("good bye");
                        break;
                    }
                    out.println(inputLine);
                }
                in.close();
                out.close();
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public String toString() {
            return "EchoClientHandler" + this.getName();
        }
    }

    public static void main(String[] args) throws IOException {
        EchoMultiServer server = new EchoMultiServer();
        server.start(5555);
    }
}
