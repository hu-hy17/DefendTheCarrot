import javafx.application.Platform;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.File;
import java.lang.reflect.Field;

public class MyCarrot {
    private double posX;
    private double posY;
    ImageView imageView;
    ProgressBar hpView;
    public int hp;

    private int curFrame = 0;
    private final int totalFrame = 4;
    private int animationCount = 0;

    private Boolean injured = Boolean.FALSE;

    MyCarrot(double posX, double posY) {
        this.imageView = new ImageView();
        String imgSrc = Utils.carrotImgSrc + "carrot_" + this.curFrame + ".png";
        this.posX = posX;
        this.posY = posY;
        this.animationCount += Utils.updateInterval;
        this.hp = Utils.carrotMaxHp;
        Platform.runLater(()-> {
            imageView.setImage(new Image(imgSrc));
            imageView.setLayoutX(posX);
            imageView.setLayoutY(posY);
            imageView.setFitWidth(Utils.carrotImgLength);
            imageView.setFitHeight(Utils.carrotImgLength);
            // 添加血条
            this.hpView = new ProgressBar();
            this.hpView.setProgress(100);
            this.hpView.setPrefWidth(Utils.carrotImgLength);
            this.hpView.setStyle("-fx-accent: palegreen;");
            this.hpView.setOpacity(0.9);
            this.hpView.setLayoutX(this.posX);
            this.hpView.setLayoutY(this.posY - 20);
            ((AnchorPane) this.imageView.getParent()).getChildren().add(this.hpView);
        });
    }

    public void update() {
        this.animationCount = (this.animationCount + Utils.updateInterval)%Utils.carrotAnimationInterval;
        if(this.animationCount == 0) {
            // 更新图片
            this.curFrame = (this.curFrame+1)%this.totalFrame;
            String imgSrc = Utils.carrotImgSrc + "carrot_" + this.curFrame + ".png";
            Platform.runLater(()->imageView.setImage(new Image(imgSrc)));
        }
        // 更新血条
        Platform.runLater(()->this.hpView.setProgress((double)this.hp/Utils.carrotMaxHp));
    }

    public void causeDamage() {
        this.hp--;
        this.injured = Boolean.TRUE;
        this.animationCount = 0;
        File file = new File(Utils.carrotInjureSound);
        MediaPlayer m = new MediaPlayer(new Media(file.toURI().toString()));
        Platform.runLater(()->{
            m.play();
            imageView.setImage(new Image(Utils.carrotImgSrc+"carrot_injure.png"));
        });
    }
}
