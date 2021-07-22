package sample;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.Vector;

public class Server {

    // Aktif clientları tutmak için
    static Vector<ClientHandler> ar2 = new Vector<>();

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(5656);

        Socket socket = null;

        while (true){
            socket = serverSocket.accept();

            System.out.println("New client received : " + socket);

            DataInputStream dis = new DataInputStream(socket.getInputStream());
            DataOutputStream dos = new DataOutputStream( socket.getOutputStream());

            System.out.println("Creating a new handler for this client...");

            String name = dis.readUTF();

            ClientHandler handler = new ClientHandler(socket, name, dis, dos);

            Thread thread = new Thread(handler);

            System.out.println("Adding this client to active client list");

            ar2.add(handler);

            thread.start();
        }
    }

}

class ClientHandler implements Runnable{

    Scanner scanner = new Scanner(System.in);
    private String name;
    final DataInputStream dis;
    final DataOutputStream dos;
    Socket socket;
    boolean isloggedin = true;

    ClientHandler(Socket socket, String name, DataInputStream dis, DataOutputStream dos) {
        this.dis = dis;
        this.dos = dos;
        this.socket = socket;
        this.name = name;
    }

    @Override
    public void run() {
        String received;

        while (true){
            try{
                received = dis.readUTF();

                System.out.println(received);

                if(received.equals("logout")){
                    this.isloggedin = false;
                    this.socket.close();
                    break;
                }

                StringTokenizer st = new StringTokenizer(received, "#");
                String MsgToSend = st.nextToken();
                String recipient = st.nextToken();

                for(ClientHandler handler : Server.ar2){
                    if(handler.name.matches(recipient) && handler.isloggedin){
                        handler.dos.writeUTF(this.name + " : " + MsgToSend);
                        break;
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try
        {
            // closing resources
            this.dis.close();
            this.dos.close();

        }catch(IOException e){
            e.printStackTrace();
        }
    }
}
