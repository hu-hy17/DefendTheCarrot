import javafx.fxml.Initializable;
import javafx.scene.image.Image;

import java.net.URL;
import java.util.ResourceBundle;

public class StageController2 extends BasicStageController implements Initializable {

    // 初始化函数
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // this.background.setImage(new Image("file:assets/imgs/stage2/BG2-hd.png"));
        // this.route.setImage(new Image("file:assets/imgs/stage2/route2.png"));
        this.stageId = 1;
        this.placeNum = 28;
        this.monsterOrder = Utils.monsterOrder2;
        gameInit();
    }
}