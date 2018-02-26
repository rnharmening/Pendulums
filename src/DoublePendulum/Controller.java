package DoublePendulum;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Slider;
import javafx.scene.layout.Pane;

import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable{

    @FXML
    private Pane canvas;

    @FXML
    private Slider slider1, slider2;

    private DoublePendulum doublePendulum = new DoublePendulum(400,400);

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        canvas.getChildren().add(doublePendulum);
        doublePendulum.simulatePendulum(true);
    }
}
