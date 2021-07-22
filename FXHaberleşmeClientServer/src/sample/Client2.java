package sample;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client2 extends Application {

    private final static int ServerPort = 5656;
    public static Button b = new Button("Gonder");
    public static TextField textField = new TextField();
    public static TextArea textArea = new TextArea();
    DataOutputStream dos;
    DataInputStream dis;
    // launch the application

    @Override
    public void start(Stage s) throws Exception {
        s.setTitle("Main");
//        Button b = new Button("Gonder");
        StackPane r = new StackPane();
        b.setTranslateX(100);
        b.setTranslateY(150);
        b.setMaxSize(75, 35);

        textField.setTranslateX(-40);
        textField.setTranslateY(150);
        textField.setMaxSize(195, 35);

        textArea.setTranslateX(-5);
        textArea.setTranslateY(-20);
        textArea.setMaxSize(260, 290);
        r.getChildren().add(b);
        r.getChildren().add(textField);
        r.getChildren().add(textArea);
        Scene sc = new Scene(r, 280, 350);
        s.setScene(sc);
        s.show();

        Socket socket = null;
        try {
            InetAddress ip = InetAddress.getByName("localhost");
            socket = new Socket(ip, ServerPort);
            // obtaining input and out streams
            dis = new DataInputStream(socket.getInputStream());
            dos = new DataOutputStream(socket.getOutputStream());

            dos.writeUTF("Main");

            Thread readMessage = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        try {
                            String msg = dis.readUTF();
                            textArea.appendText(msg + "\n");
                            System.out.println(msg);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            readMessage.start();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }

        b.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                String msg = textField.getText();
                msg += "#Client";
                textField.clear();
                try {
                    if (dos != null) {
                        dos.writeUTF(msg);
                        textArea.appendText(msg + "\n");
                        System.out.println(msg);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static void main(String[] args) throws UnknownHostException, IOException {
        launch(args);
    }
}

