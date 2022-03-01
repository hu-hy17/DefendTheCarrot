import javafx.fxml.Initializable;
import javafx.scene.image.Image;

import java.net.URL;
import java.util.ResourceBundle;

public class StageController3 extends BasicStageController implements Initializable {

    // 初始化函数
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // this.background.setImage(new Image("file:assets/imgs/stage3/BG3-hd.png"));
        // this.route.setImage(new Image("file:assets/imgs/stage3/route3.png"));
        this.stageId = 2;
        this.placeNum = 24;
        this.monsterOrder = Utils.monsterOrder3;
        gameInit();
    }
}