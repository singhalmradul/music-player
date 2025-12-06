package io.github.singhalmradul;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.ListIterator;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXSlider;
import com.jfoenix.controls.JFXToggleButton;
import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.UnsupportedTagException;

import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

public class MusicPlayer extends Application {
    public static void main(String... args) {
        launch(args);
    }
    private VBox vb, vb2;
    private Stage stage;
    private MenuItem openFile, openFolder;
    private Label stageTitle, title, artist, album, currentDuration, totalDuration, volumeValue, repeatTrack;
    private JFXToggleButton toggleRepeat;
    private JFXSlider songSlider, volumeSlider;
    private JFXButton logo, bt, minimize, exit, previousButton, playButton, pause, nextButton, mute, volume;
    private ImageView artwork, volumeIcon;
    private HBox hb7, hb, hb1, hb2, hb3;
    private MediaPlayer mediaPlayer;
    private ContextMenu cm;
    private BorderPane window, bp, bp2;
    private double x, y;
    private int lastVolume;

    /**
     * @see javafx.application.Application#stop()
     */
    public void stop() {

        System.exit(0);

    }

    public InputStream getResource(String location) {
        return MusicPlayer.class.getClassLoader().getResourceAsStream(location);
    }

    /**
     * @see javafx.application.Application#start(javafx.stage.Stage)
     */
    public void start(Stage stage) {

        this.stage = stage;
        stage.setTitle("music");

        try {

            stage.getIcons().add(new Image(getResource("static/assets/logo.png"), 32, 32, true, true));
            stage.setScene(getMainPlayer());

        } catch (FileNotFoundException e) {

            System.err.println(e.getMessage());

        }

        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setResizable(true);
        stage.show();

    }

    // private HBox makeTable(ArrayList<String> ar) throws UnsupportedTagException,
    // InvalidDataException, IOException {

    // Font xx = Font.font("Berlin Sans FB", FontWeight.BOLD, 12);

    // Label abe = new Label("title");
    // abe.setFont(xx);

    // Label abt = new Label("artist");
    // abt.setFont(xx);

    // Label abw = new Label("album");
    // abw.setFont(xx);

    // Label abr = new Label("length");
    // abr.setFont(xx);

    // VBox fqw = new VBox(20, abw);
    // fqw.setPadding(new Insets(20));

    // VBox fqt = new VBox(20, abt);
    // fqw.setPadding(new Insets(20));

    // VBox fqe = new VBox(20, abe);
    // fqw.setPadding(new Insets(20));

    // VBox fqr = new VBox(20, abr);
    // fqw.setPadding(new Insets(20));

    // HBox fqq = new HBox(20, fqe, fqt, fqw, fqr);
    // fqq.setPadding(new Insets(20));

    // ObservableList<Node> w = fqw.getChildren(), r = fqr.getChildren(), e =
    // fqe.getChildren(), t = fqt.getChildren();
    // for (Iterator<String> q = ar.iterator(); q.hasNext();) {

    // Mp3File mp3file = new Mp3File(new File(q.next()));

    // if (mp3file.hasId3v2Tag()) {

    // ID3v2 id3v2Tag = mp3file.getId3v2Tag();
    // Pane wq = new Pane(new Label(id3v2Tag.getAlbum()));
    // Pane eq = new Pane(new Label(id3v2Tag.getTitle()));
    // Pane rq = new Pane(new Label(getFormattedDuration(id3v2Tag.getLength())));
    // Pane tq = new Pane(new Label(id3v2Tag.getArtist()));

    // wq.setOnMouseClicked();
    // w.add(wq);
    // e.add(eq);
    // r.add(rq);
    // t.add(tq);
    // }
    // }
    // return fqq;

    // }

    private void showInfo(String path) throws UnsupportedTagException, InvalidDataException, IOException {

        Mp3File mp3file = new Mp3File(new File(path));
        String albumArtwork = "";

        if (mp3file.hasId3v2Tag()) {

            ID3v2 id3v2Tag = mp3file.getId3v2Tag();
            title.setText(id3v2Tag.getTitle());
            artist.setText(id3v2Tag.getArtist());
            album.setText(id3v2Tag.getAlbum());
            byte[] imageData = id3v2Tag.getAlbumImage();

            if (imageData != null) {

                albumArtwork = "static/assets/album-artwork." + id3v2Tag.getAlbumImageMimeType().substring(6);
                RandomAccessFile file = new RandomAccessFile(albumArtwork, "rw");
                file.write(imageData);
                file.close();

                try {

                    artwork.setImage(new Image(getResource(albumArtwork), 100, 100, true, true));

                } catch (Exception e) {

                    artwork.setImage(new Image(getResource("static/assets/louis-tomlinson.png"), 100, 100, true, true));

                }
            }

            stageTitle.setText(title.getText() + " - " + artist.getText());
            stage.setTitle(stageTitle.getText());

        }
    }

    private Scene getMainPlayer() throws FileNotFoundException {

        Scene sc = new Scene(mainPlayerSetup());
        return sc;

    }

    private Parent mainPlayerSetup() throws FileNotFoundException {

        songSlider = new JFXSlider(0, 100, 0);
        songSlider.setDisable(true);
        songSlider.setValueFactory(event -> Bindings.createStringBinding(
                () -> getFormattedDuration((long) mediaPlayer.getCurrentTime().toSeconds()),
                songSlider.valueProperty()));
        songSlider.setOnMousePressed(event -> mediaPlayer.seek(Duration.seconds(songSlider.getValue())));
        songSlider.setOnMouseDragged(event -> mediaPlayer.seek(Duration.seconds(songSlider.getValue())));

        logo = new JFXButton("",
                new ImageView(new Image(getResource("static/assets/logo.png"), 24, 24, true, true)));
        logo.setContextMenu(cm);
        logo.setTooltip(new Tooltip("file"));
        logo.setOnAction(e -> cm.show(stage));

        openFile = new MenuItem("open file",
                new ImageView(new Image(getResource("static/assets/note.png"), 24, 24, true, true)));
        openFile.setOnAction(e -> {

            FileChooser fc = new FileChooser();
            File fl = fc.showOpenDialog(stage);
            String path = fl.getAbsolutePath();
            ArrayList<String> file = new ArrayList<>();
            file.add(path);
            startPlaying(file);

        });

        openFolder = new MenuItem("open folder",
                new ImageView(new Image(getResource("static/assets/music-folder.png"), 24, 24, true, true)));
        openFolder.setOnAction(e -> {

            DirectoryChooser dc = new DirectoryChooser();
            File fl = dc.showDialog(stage);
            ArrayList<String> files = new ArrayList<>();
            getSongList(files, fl);
            startPlaying(files);

        });

        cm = new ContextMenu(openFile, openFolder);

        stageTitle = new Label("music");
        stageTitle.setPadding(new Insets(0, 40, 0, 40));
        stageTitle.setFont(Font.font("Berlin Sans FB", FontWeight.MEDIUM, 15));

        bt = new JFXButton("",
                new ImageView(new Image(getResource("static/assets/miniPlayer.png"), 16, 16, true, true)));

        minimize = new JFXButton("",
                new ImageView(new Image(getResource("static/assets/minimize.png"), 16, 16, true, true)));
        minimize.setOnAction(event -> stage.setIconified(true));

        exit = new JFXButton("",
                new ImageView(new Image(getResource("static/assets/cancel.png"), 16, 16, true, true)));
        exit.setOnAction(event -> stop());

        hb7 = new HBox(10, bt, minimize, exit);
        hb7.setAlignment(Pos.CENTER_RIGHT);

        window = new BorderPane();
        window.setLeft(logo);
        window.setCenter(stageTitle);
        window.setRight(hb7);
        window.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {

            x = stage.getX() - event.getScreenX();
            y = stage.getY() - event.getScreenY();

        });
        window.addEventHandler(MouseEvent.MOUSE_DRAGGED, event -> {

            stage.setX(event.getScreenX() + x);
            stage.setY(event.getScreenY() + y);

        });

        artwork = new ImageView(
                new Image(getResource("static/assets/louis-tomlinson.png"), 100, 100, true, true));
        artwork.setEffect(new DropShadow());

        previousButton = new JFXButton("",
                new ImageView(new Image(getResource("static/assets/back-arrows.png"), 32, 32, true, true)));

        playButton = new JFXButton("",
                new ImageView(new Image(getResource("static/assets/play.png"), 32, 32, true, true)));
        playButton.setOnAction(event -> play());

        pause = new JFXButton("",
                new ImageView(new Image(getResource("static/assets/pause.png"), 32, 32, true, true)));
        pause.setOnAction(event -> pause());

        nextButton = new JFXButton("",
                new ImageView(new Image(getResource("static/assets/forward-arrows.png"), 32, 32, true, true)));

        hb1 = new HBox(30, previousButton, playButton, nextButton);
        hb1.setAlignment(Pos.CENTER);

        currentDuration = new Label("00:00");
        currentDuration.setAlignment(Pos.CENTER_LEFT);

        totalDuration = new Label("00:00");
        totalDuration.setAlignment(Pos.CENTER_RIGHT);

        bp = new BorderPane();
        bp.setLeft(currentDuration);
        bp.setCenter(hb1);
        bp.setRight(totalDuration);
        bp.setPadding(new Insets(5, 0, 10, 0));

        mute = new JFXButton("",
                new ImageView(new Image(getResource("static/assets/mute.png"), 32, 32, true, true)));
        mute.setOnAction(event -> {

            if (lastVolume == 0)
                volumeSlider.setValue(100);
            else
                volumeSlider.setValue(lastVolume);

        });

        volumeIcon = new ImageView(new Image(getResource("static/assets/speaker.png"), 32, 32, true, true));

        volume = new JFXButton("", volumeIcon);
        volume.setOnAction(event -> {

            int t = (int) volumeSlider.getValue();
            volumeSlider.setValue(0);
            lastVolume = t;

        });

        volumeSlider = new JFXSlider(0, 100, 100);
        volumeSlider.valueProperty().addListener(observable -> volumeHandler());

        volumeValue = new Label("100");
        volumeValue.setPadding(new Insets(0, 40, 0, 10));

        toggleRepeat = new JFXToggleButton();

        repeatTrack = new Label("repeat");
        repeatTrack.setFont(Font.font("Berlin Sans FB"));

        hb2 = new HBox(volume, volumeSlider, volumeValue);
        hb2.setAlignment(Pos.CENTER_LEFT);

        hb3 = new HBox(toggleRepeat, repeatTrack);
        hb3.setAlignment(Pos.CENTER_RIGHT);

        bp2 = new BorderPane();
        bp2.setLeft(hb2);
        bp2.setRight(hb3);

        title = new Label("");
        title.setFont(Font.font("Berlin Sans FB", FontWeight.BOLD, 18));

        artist = new Label("");
        artist.setFont(Font.font("Berlin Sans FB", FontWeight.MEDIUM, 17));

        album = new Label("");
        album.setFont(Font.font("Berlin Sans FB", 14));

        vb = new VBox(5, title, artist, album);
        vb.setAlignment(Pos.CENTER);

        hb = new HBox(50, artwork, vb);
        hb.setPadding(new Insets(40, 20, 20, 20));

        vb2 = new VBox(window, hb, songSlider, bp, bp2);
        vb2.setPadding(new Insets(15, 25, 15, 25));

        return vb2;
    }

    private ArrayList<String> getSongList(ArrayList<String> files, File fl) {

        for (File file : fl.listFiles()) {

            String name = file.getName();

            if (name.endsWith(".mp3") || name.endsWith(".m4a") || name.endsWith(".wav"))
                files.add(file.getAbsolutePath());

            else if (file.isDirectory())
                getSongList(files, file);

        }
        return files;
    }

    private void volumeHandler() {

        if (mediaPlayer != null)
            mediaPlayer.setVolume(volumeSlider.getValue() / 100);

        lastVolume = (int) Math.ceil(volumeSlider.getValue());
        volumeValue.setText(String.valueOf(lastVolume));

        if (volumeSlider.getValue() == 0)
            hb2.getChildren().set(0, mute);

        else
            try {

                if (volumeSlider.getValue() < 25)
                    volumeIcon.setImage(new Image(getResource("static/assets/speaker1.png"), 32, 32, true, true));

                else if (volumeSlider.getValue() < 50)
                    volumeIcon.setImage(new Image(getResource("static/assets/speaker2.png"), 32, 32, true, true));

                else if (volumeSlider.getValue() < 75)
                    volumeIcon.setImage(new Image(getResource("static/assets/speaker3.png"), 32, 32, true, true));

                else
                    volumeIcon.setImage(new Image(getResource("static/assets/speaker.png"), 32, 32, true, true));

            } catch (Exception e) {

                System.err.println(e.getLocalizedMessage());

            } finally {

                hb2.getChildren().set(0, volume);

            }
    }

    private String getFormattedDuration(long duration) {

        return duration / 60 + ((duration % 60) < 10 ? ":0" : ":") + duration % 60;

    }

    private void startPlaying(ArrayList<String> ar) {
        ListIterator<String> q = ar.listIterator();
        if (q.hasNext())
            startPlaying(q.next());

        mediaPlayer.setOnEndOfMedia(() -> {

            if (q.hasNext())
                startPlaying(q.next());

            else if (toggleRepeat.isSelected())
                startPlaying(ar);

            else
                stopPlaying();

        });
        nextButton.setOnAction(event -> startPlaying(q.next()));
        previousButton.setOnAction(event -> startPlaying(q.previous()));
    }

    private void stopPlaying() {
        mediaPlayer.stop();
        mediaPlayer = null;
        songSlider.setValue(0);
        currentDuration.setText("0:00");
        totalDuration.setText("0:00");
        title.setText("");
        artist.setText("");
        album.setText("");
        stageTitle.setText("music");
        try {
            artwork.setImage(new Image(getResource("static/assets/louis-tomlinson.png"), 100, 100, true, true));
        } catch (Exception e) {
        }

    }

    private void startPlaying(String path) {

        if (mediaPlayer != null)
        mediaPlayer.stop();

        mediaPlayer = new MediaPlayer(new Media(new File(path).toURI().toString()));
        mediaPlayer.currentTimeProperty().addListener((observable, oldValue, newValue) -> {

            songSlider.setValue(newValue.toSeconds());
            currentDuration.setText(getFormattedDuration(((long) newValue.toSeconds())));

        });
        mediaPlayer.setOnReady(() -> {

            songSlider.setMax(mediaPlayer.getMedia().getDuration().toSeconds());
            totalDuration.setText(getFormattedDuration((long) mediaPlayer.getMedia().getDuration().toSeconds()));
            mediaPlayer.setVolume(volumeSlider.getValue() / 100);

        });
        try {

            showInfo(path);

        } catch (UnsupportedTagException | InvalidDataException | IOException e) {

            System.out.println(e.getMessage());

        }

        play();

    }

    private void play() {

        if (mediaPlayer != null) {

            mediaPlayer.play();
            songSlider.setDisable(false);
            hb1.getChildren().set(1, pause);

        }
    }

    private void pause() {

        mediaPlayer.pause();
        hb1.getChildren().set(1, playButton);

    }
}