package org.revo.ihear.streamer;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import static javafx.application.Platform.runLater;

public class Player extends Application {

    private static String videoUrl = "http://localhost:8083/video/5da0d0039c750d106725fce5";
    private Thread playThread;

    @Override
    public void start(final Stage primaryStage) {
        final StackPane root = new StackPane();
        final ImageView imageView = new ImageView();
        root.getChildren().add(imageView);
        imageView.fitWidthProperty().bind(primaryStage.widthProperty());
        imageView.fitHeightProperty().bind(primaryStage.heightProperty());
        final Scene scene = new Scene(root, 640, 480);
        primaryStage.setTitle("Video");
        primaryStage.setScene(scene);
        primaryStage.show();
        playThread = new Thread(new Viewer(videoUrl, image -> runLater(() -> imageView.setImage(SwingFXUtils.toFXImage(image, null))), grabber -> {
            primaryStage.setWidth(grabber.getImageWidth());
            primaryStage.setHeight(grabber.getImageHeight());
        }, e -> {
            System.out.println(e.getMessage());
            Platform.exit();
        }));
        playThread.start();
    }

    @Override
    public void stop() {
        playThread.interrupt();
    }

    //    ffmpeg -re -i  /dev/video0  -r 20 -c:v h264 -an -vf format=yuv420p  -rtsp_transport tcp -threads 2 -quality realtime -preset ultrafast -deadline .01 -tune zerolatency   -f rtsp  rtsp://127.0.0.1:8085/stream/5da0d0039c750d106725fce5
//    ffmpeg -re -r 20 -i  /dev/video0  -c:v h264 -vf format=yuv420p -an -rtsp_transport tcp -threads 2 -quality realtime -preset ultrafast -deadline .01 -tune zerolatency -f rtsp rtsp://127.0.0.1:8085/stream/5da0d0039c750d106725fce5
//    fps 20 yuv420p 640*480
//    make anyExchange().permitAll() for test
    public static void main(String[] args) {
        launch(args);
    }
}

