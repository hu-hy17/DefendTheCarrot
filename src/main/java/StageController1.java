import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;


public class StageController1 extends BasicStageController implements Initializable {

    // 初始化函数
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // this.background.setImage(new Image("file:assets/imgs/stage1/BG1-hd.png"));
        // this.route.setImage(new Image("file:assets/imgs/stage1/route1.png"));
        this.stageId = 0;
        this.placeNum = 30;
        this.monsterOrder = Utils.monsterOrder1;
        gameInit();
    }
}
