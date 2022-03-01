import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.File;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentLinkedQueue;

public class MyPlace {
    public ImageView imageview;     // 对应的图片
    private AnchorPane bulletPane;  // 子弹画板
    public Boolean isPlant = Boolean.FALSE;    // 是否放置了防御塔

    private int towerId = -1;       // 防御塔id
    private int attackCount = 0;    // 攻击计数器，每次更新时模攻击间隔时间叠加，为0时代表可以发动攻击。

    private double tarPosX;
    private double tarPosY;

    private MyRangeBullet rBullet;

    private Media attackSound;
    private MediaPlayer soundPlayer;

    // private ImageView tarDot = new ImageView();

    MyPlace(ImageView iView, AnchorPane bPane){
        this.imageview = iView;
        this.bulletPane = bPane;
        iView.setImage(null);
    }

    public int getTowerId() { return towerId; }

    public void setTower(int type, String towerName) {
        this.towerId = type;
        this.isPlant = Boolean.TRUE;

        // 初始化朝向
        this.tarPosX = this.imageview.getLayoutX() + Utils.placeLength/2.0;
        this.tarPosY = this.imageview.getLayoutY();

        // 设置范围子弹
        if(this.towerId == Utils.TOWER_SUN || this.towerId == Utils.TOWER_SNOW) {
            this.rBullet = new MyRangeBullet(this.imageview.getLayoutX(), this.imageview.getLayoutY(), this.towerId);
            Platform.runLater(()->this.bulletPane.getChildren().add(this.rBullet.imageView));
        }

        // 设置音效
        File soundFile = new File(Utils.towerAttackSound[type]);
        this.attackSound = new Media(soundFile.toURI().toString());

        // 更新UI
        Platform.runLater(()-> {
            this.imageview.setImage(new Image(Utils.towerSrcRoot + towerName + "-set.png"));
            this.imageview.setRotate(0);
        });
    }

    public void removeTower() {
        this.isPlant = Boolean.FALSE;
        Platform.runLater(()-> this.imageview.setImage(null));

        // 清除范围子弹
        if(this.towerId == Utils.TOWER_SNOW || this.towerId == Utils.TOWER_SUN) {
            AnchorPane parent = (AnchorPane) this.rBullet.imageView.getParent();
            this.rBullet.remove();
            Platform.runLater(()-> parent.getChildren().remove(this.rBullet.imageView));
        }
        this.towerId = -1;
    }

    private void singleAttack(double tarX, double tarY, ConcurrentLinkedQueue<MyBullet> bList) {
        MyBullet newBullet = new MyBullet(this.imageview.getLayoutX()+Utils.placeLength/2.0,
                this.imageview.getLayoutY()+Utils.placeLength/2.0,
                tarX, tarY, this.towerId);
        bList.add(newBullet);
        ImageView bImgView = newBullet.imageView;

        double rotation = calRotate(this.imageview.getLayoutX()+Utils.placeLength/2.0,
                this.imageview.getLayoutY()+Utils.placeLength/2.0,
                tarX, tarY);
        Platform.runLater(()->{
            if(this.towerId == Utils.TOWER_ARROW || this.towerId == Utils.TOWER_ANCHOR) {
                bImgView.setRotate(rotation);
            }
            this.bulletPane.getChildren().add(bImgView);
        });
        this.tarPosX = tarX;
        this.tarPosY = tarY;
    }

    public void generateAttack(LinkedList<MyMonster> mList, ConcurrentLinkedQueue<MyBullet> bList) {
        if(this.attackCount != 0 || mList.isEmpty()) {
            // 未准备好攻击/目标为0
            return;
        }
        this.attackCount += Utils.updateInterval;
        for(MyMonster m:mList) {
            this.singleAttack(m.imageView.getLayoutX()+Utils.placeLength/2.0,
                    m.imageView.getLayoutY()+Utils.placeLength/2.0, bList);
        }
        try {
            this.soundPlayer.stop();
        } catch (Exception e) {}
        // 播放声音
        try {
            this.soundPlayer = new MediaPlayer(attackSound);
            Platform.runLater(() -> this.soundPlayer.play());
        } catch (Exception e) {}
    }

    public void generateRangeAttack(LinkedList<MyMonster> targets) {
        if(this.attackCount != 0 || targets.isEmpty()) {
            return;
        }
        this.attackCount += Utils.updateInterval;
        if(this.rBullet == null) {
            return;
        }
        this.rBullet.ready();
        if(this.towerId == Utils.TOWER_SUN) {
            for (MyMonster m : targets) {
                m.acceptDamage(Utils.towerInstantDamage[this.towerId]);
            }
        } else {
            for (MyMonster m : targets) {
                m.acceptSlowStat();
            }
        }
        // 播放声音
        try {
            this.soundPlayer = new MediaPlayer(attackSound);
            Platform.runLater(() -> this.soundPlayer.play());
        } catch (Exception e) {}
    }

    public void update() {
        if(!this.isPlant) {
            return;
        }
        if(this.attackCount != 0) {
            this.attackCount = (this.attackCount + Utils.updateInterval)%Utils.towerAttackInterval[this.towerId];
        }

        // 子弹型防御塔(旋转)/范围型防御塔(更新范围子弹)
        if(this.towerId == Utils.TOWER_SNOW || this.towerId == Utils.TOWER_SUN) {
            if(this.rBullet != null) {
                this.rBullet.update();
            }
        } else {
            double rotation = calTowerRotate();
            Platform.runLater(()->this.imageview.setRotate(rotation));
        }
    }

    // 计算防御塔的旋转角度
    private double calTowerRotate() {
        double centerX = this.imageview.getLayoutX() + Utils.placeLength/2.0;
        double centerY = this.imageview.getLayoutY() + Utils.placeLength/2.0;
        return calRotate(centerX, centerY, this.tarPosX, this.tarPosY);
    }

    private double calRotate(double startX, double startY, double tarX, double tarY) {
        if(startX == tarX) {
            if(startY > tarY) {
                return 0;
            } else {
                return 180;
            }
        }
        double tan = (startY - tarY)/(startX - tarX);
        double angle = Math.atan(tan)*(180/Math.PI);
        if(startX > tarX) {
            return angle-90;
        } else {
            return angle+90;
        }
    }
}
