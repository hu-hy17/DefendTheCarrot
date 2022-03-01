import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class MyRangeBullet {
    // 类型
    private int type;
    // 当前位置
    private double posX;
    private double posY;
    // 中心
    private double centerX;
    private double centerY;
    // 当前大小
    private double currentLength;
    // 贴图
    public ImageView imageView;
    // 是否存在
    public Boolean exist = Boolean.FALSE;

    MyRangeBullet(double posX, double posY, int type) {
        this.centerX = posX + Utils.placeLength/2.0;
        this.centerY = posY + Utils.placeLength/2.0;
        this.posX = posX + Utils.placeLength/2.0 - Utils.rangeBulletMinLength/2.0;
        this.posY = posY + Utils.placeLength/2.0 - Utils.rangeBulletMinLength/2.0;
        this.currentLength = Utils.rangeBulletMinLength;
        this.imageView = new ImageView();
        this.type = type;

        // 设置贴图
        this.imageView = new ImageView();
        String imgPath = Utils.bulletSrcRoot + "bullet_" + type + ".png";
        Platform.runLater(()-> {
            imageView.setPreserveRatio(false);
            imageView.setFitWidth(this.currentLength);
            imageView.setFitHeight(this.currentLength);
            imageView.setLayoutX(posX);
            imageView.setLayoutY(posY);
            imageView.setOpacity(0.5);
            imageView.setImage(new Image(imgPath));
            imageView.setVisible(this.exist);
        });
    }

    public int getType() {
        return type;
    }

    public void update() {
        if(this.exist && (this.currentLength < Utils.rangeBulletMaxLength) ) {
            this.currentLength += Utils.rangeBulletExpandSpeed;
            this.posX -= Utils.rangeBulletExpandSpeed/2.0;
            this.posY -= Utils.rangeBulletExpandSpeed/2.0;
        }
        else {
            this.exist = Boolean.FALSE;
        }
        Platform.runLater(()-> {
            imageView.setVisible(this.exist);
            imageView.setFitWidth(this.currentLength);
            imageView.setFitHeight(this.currentLength);
            imageView.setLayoutX(posX);
            imageView.setLayoutY(posY);
        });
    }

    public void remove() {
        Platform.runLater(()->this.imageView.setImage(null));
    }

    public void ready() {
        this.exist = Boolean.TRUE;
        this.posX = this.centerX - Utils.rangeBulletMinLength/2.0;
        this.posY = this.centerY - Utils.rangeBulletMinLength/2.0;
        this.currentLength = Utils.rangeBulletMinLength;
    }
}
