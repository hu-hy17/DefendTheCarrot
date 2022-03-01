import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class MainMenuController implements Initializable {

    MediaPlayer bgmPlayer;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // 设置bgm播放器
        File file =  new File(Utils.mainBgmSound);
        Media bgmMedia = new Media(file.toURI().toString());
        bgmPlayer = new MediaPlayer(bgmMedia);
        bgmPlayer.setCycleCount(Utils.bgmCycleTimes);
        bgmPlayer.play();
    }

    public void jmpToSelectStage() {
        this.bgmPlayer.stop();
        Scene scene = null;
        try {
            scene = new Scene(FXMLLoader.load(Objects.requireNonNull(getClass().getResource("fxml/stageselect.fxml"))));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Main.primaryStage.setScene(scene);
    }
}
