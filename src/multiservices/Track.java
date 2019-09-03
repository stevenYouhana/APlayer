
package multiservices;
import java.io.File;
import javafx.util.Duration;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class Track {
    String path;
    Media hit;
    MediaPlayer mediaPlayer;
    Duration cuePreset = new Duration(0);
    
    public Track(String path) {
        this.path = path;
    hit = new Media(new File(path).toURI().toString());
    mediaPlayer = new MediaPlayer(hit);
    }
    
    public synchronized void playTrack() {
        if (FXMLDocumentController.PLAYER_STATUS.equals("PAUSE")) {
            Flash.flashing = true;
            mediaPlayer.play();
        }
        else {
            mediaPlayer.pause();
            Flash.flashing = false;
        }
    }
    public void stop() {
        if (isPlaying()) {
            mediaPlayer.stop();
        }
    }
    public void setCuePreset(double time) {
        cuePreset = new Duration(time);
    }
    public boolean isPlaying() {
        return mediaPlayer.getStatus()
                .compareTo(mediaPlayer.getStatus().PLAYING) == 0 ? true : false;
    }
    public boolean isPaused() {
        return mediaPlayer.getStatus()
                .compareTo(mediaPlayer.getStatus().PAUSED) == 0 ? true : false;
    }
    public void cueStart() {
        mediaPlayer.setStartTime(cuePreset);
        mediaPlayer.play();
    }
    public synchronized void cueRelease() {
        mediaPlayer.pause();
    }
    public void setStartTime(Duration duration) {
        mediaPlayer.setStartTime(duration);
    }
    public javafx.util.Duration currentTime() {
        return mediaPlayer.getCurrentTime();
    }
    public javafx.util.Duration duration() {
        return mediaPlayer.getTotalDuration();
    }
    public void setFromSlider(double sliderValue) {
        mediaPlayer.seek(
                mediaPlayer.getTotalDuration().multiply(sliderValue / 100.0));
    }
}
