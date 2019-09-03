
package multiservices;

import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class Flash {
    Label flash;
    Button button;
    public volatile static boolean flashing = false;
    
    Flash(Label flash) {
        this.flash = flash;
    }
    Flash(Button button) {
        this.button = button;
    }
    public void flashButton(String[] colors) {
        while(flashing) {
            try { 
                button.setStyle("-fx-background-color:"+colors[0]);
                Thread.sleep(500);
                button.setStyle("-fx-background-color:"+colors[1]);
                Thread.sleep(500);
                }
            catch (InterruptedException ie) {
                ie.printStackTrace();
            }
        }
    }
    public void startFlashing(String[] colors) {
        while(flashing) {
            try {
                flash.setStyle("-fx-background-color:"+colors[0]);
                Thread.sleep(500);
                flash.setStyle("-fx-background-color:"+colors[1]);
                Thread.sleep(500);
                }
            catch (InterruptedException ie) {
                ie.printStackTrace();
            }
        }
    }
//    
}
