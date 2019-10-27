package org.revo.ihear.streamer;

import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.Java2DFrameConverter;

import java.awt.image.BufferedImage;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;
import java.util.function.Function;

public class Viewer implements Runnable, Function<Frame, BufferedImage> {
    private String videoUrl;
    private Consumer<BufferedImage> onEach;
    private Consumer<FFmpegFrameGrabber> onStart;
    private Consumer<Exception> onExit;

    public Viewer(String videoUrl, Consumer<BufferedImage> onEach, Consumer<FFmpegFrameGrabber> onStart, Consumer<Exception> onExit) {
        this.videoUrl = videoUrl;
        this.onEach = onEach;
        this.onStart = onStart;
        this.onExit = onExit;
    }

    private Java2DFrameConverter converter = new Java2DFrameConverter();

    @Override
    public void run() {
        try {
            FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(videoUrl);
            grabber.start();
            if (this.onStart != null) this.onStart.accept(grabber);
            Frame frame;
            while (!Thread.interrupted() && (frame = grabber.grab()) != null) {
                if (frame.image == null) continue;
                onEach.accept(apply(frame));
            }
            grabber.stop();
            grabber.release();
            if (this.onExit != null) {
                this.onExit.accept(new TimeoutException("Stream frame invalid or  stream Interrupted ! It may be the end"));
            }
        } catch (FrameGrabber.Exception e) {
            if (this.onExit != null) {
                this.onExit.accept(e);
            }
        }
    }

    @Override
    public BufferedImage apply(Frame frame) {
        return converter.convert(frame);
    }
}
