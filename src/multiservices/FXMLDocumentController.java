
package multiservices;

import java.net.URL;
import java.util.Observable;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;


public class FXMLDocumentController implements Initializable {
    ExecutorService services;
    ExecutorService playServices;
    
    @FXML
    private volatile Label lblStatus;
    @FXML
    private Button btnOn;
    @FXML
    private Button btnPlay;
    @FXML
    private Button btnCue;
    @FXML
    private TextField txtCue;
    @FXML
    private Button btnStop;
    @FXML
    private Label lblFlash;
    @FXML
    private Slider slTrackTime;
    
    String[] LABEL_COLORS = {"GREEN", "GREY"};
    String[] STATUS_COLORS = {"WHITE", "GREY"};
    String[] STATUS_PLAYING = {"#42f492", "#d1ffe5"};
    public static String PLAYER_STATUS = "PLAY";
    static volatile Duration startTime = null;
    boolean aEngaged = false;
    boolean bEngaged = false;
    boolean on = false;
    static final Object CUE_HOLDER = new Object();
    Track track;
    
    void reset() {
        bEngaged = false;
        aEngaged = false;
        Flash.flashing = false;
        PLAYER_STATUS = "PLAY";
        btnStop.setDisable(true);
        btnPlay.setStyle("-fx-background-color:" +
"#d1ffe5, linear-gradient(#73c496 0%, #0ddb66 80%)");
        btnPlay.setDisable(true);
        btnStop.setDisable(true);
        btnCue.setDisable(true);
    }
    @FXML
    private void Start() {
        System.out.println("private void Start(ActionEvent event) {");
        track = new Track("src/audio/sample.mp3");
        btnOn.setText("OFF");
        reset();
        lblStatus.setText("press Start to start");
        if (services.isShutdown()) services = Executors.newFixedThreadPool(5);
        
        playServices = Executors.newFixedThreadPool(4);
        
        if (services != null && !on) {
            btnOn.setText("OFF");
            on = true;
            Flash.flashing = true;
            System.out.println("services.submit(() -> { --1");
            services.submit(() -> {
                System.out.println("services.submit(() -> { --1");
                while (true) {
//                    synchronized(CUE_HOLDER) {
                    Thread.sleep(10);
                        if (btnCue.isPressed()) {
                            if (!track.isPlaying()) {
                                track.cueStart();  
                            }
                        }
//                    }
                }
                
                
            });
            System.out.println("services.submit(() -> { --2");
            
            services.submit(() -> {
                System.out.println("services.submit(() -> { --2");
                Flash flash = new Flash(lblFlash);
                flash.startFlashing(LABEL_COLORS);
            });
            System.out.println("services.submit(() -> { --3");
            services.submit(() -> {
                System.out.println("services.submit(() -> { --3");
                Flash statusFlash = new Flash(lblStatus);
                statusFlash.startFlashing(STATUS_COLORS);
            });
            System.out.println("services.submit(() -> { --4");
            services.submit(() -> {
                System.out.println("services.submit(() -> { --4");
                while (true) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    updateValues();
                }
            });
            btnPlay.setDisable(false);
            btnStop.setDisable(false);
            btnCue.setDisable(false);
        }
        else if (on) {
            btnOn.setText("ON");
            on = false;
            reset();
            lblStatus.setText("press Start to start");
            services.shutdown();
            playServices.shutdown();
            btnPlay.setText("PLAY");
            PLAYER_STATUS = "PLAY";
        }
    }
    @FXML
    private synchronized void playpause() {
        PLAYER_STATUS = PLAYER_STATUS.equals("PAUSE") ? "PLAY" : "PAUSE";
        btnPlay.setText(PLAYER_STATUS);
        if (track.isPlaying()) btnStop.setDisable(false);
        track.playTrack();
        if (PLAYER_STATUS.equals("PAUSE")) {
            if (playServices.isShutdown()) {
                playServices = Executors.newCachedThreadPool();
            }
            playServices.submit(() -> { 
                Flash playFlash = new Flash(btnPlay);
                playFlash.flashButton(STATUS_PLAYING);
            });
        }
        else {
            playServices.shutdown();
            btnPlay.setStyle("-fx-background-color:" +
   "#d1ffe5, linear-gradient(#73c496 0%, #0ddb66 80%)");
        }
    }
    @FXML
    private void cue() {

    }
    @FXML
    private synchronized void stop() {
        if (track.isPlaying()) {
            track.stop();
        }
        reset();
    }
    void trackTimeSliderSetting() {
        slTrackTime.setShowTickMarks(true);
        slTrackTime.setShowTickLabels(true);
        slTrackTime.setMajorTickUnit(0.25f);
        slTrackTime.setBlockIncrement(0.1f);
        slTrackTime.setMinWidth(50);
        slTrackTime.valueProperty().addListener(new InvalidationListener() {
            @Override
            public void invalidated(javafx.beans.Observable ov) {
                if (slTrackTime.isValueChanging()) {
                    if(track.currentTime() != null) {
                        System.out.println("slider change: "+slTrackTime.getValue());
                        track.setFromSlider(slTrackTime.getValue());
                    }
                }
            }
        });
    }
    protected void updateValues() {
        if (slTrackTime != null) {
            Platform.runLater(new Runnable() {
                public void run() {
                    slTrackTime.setDisable(track.currentTime().isUnknown());
                    if (!slTrackTime.isDisabled() 
                            && track.currentTime().greaterThan(Duration.ZERO) 
                            && !slTrackTime.isValueChanging()) {
                        slTrackTime.setValue(
                                (track.currentTime().toMillis() 
                                        / track.duration().toMillis()) * 100);
                    } 
                }
            });
        }
    }
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        services = Executors.newFixedThreadPool(5);
        lblStatus.setText("press Start to start");
        btnPlay.setDisable(true);
        btnStop.setDisable(true);
        btnCue.setDisable(true);
        lblFlash.setStyle("-fx-background-color:POWDERBLUE");
        btnPlay.setStyle("-fx-background-color:" +
"#d1ffe5, linear-gradient(#73c496 0%, #0ddb66 80%)");
           
        btnCue.setOnMouseReleased(e -> {
            track.cueRelease();
            if (!txtCue.getText().isEmpty())
                track.setCuePreset(Double.parseDouble(txtCue.getText()));
        });
        trackTimeSliderSetting();
    }
    
}
