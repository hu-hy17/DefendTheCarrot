import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class StageSelectController {
    @FXML
    private ImageView stage1Btn;
    @FXML
    private ImageView stage2Btn;
    @FXML
    private ImageView stage3Btn;

    private void jmpStage(int stageId) {
        Scene scene = null;
        try {
            scene = new Scene(FXMLLoader.load(Objects.requireNonNull(getClass().getResource("fxml/stage" + stageId + ".fxml"))));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Main.primaryStage.setScene(scene);
    }

    public void jmpStage1() {
        jmpStage(1);
    }

    public void jmpStage2() {
        jmpStage(2);
    }

    public void jmpStage3() {
        jmpStage(3);
    }
}
