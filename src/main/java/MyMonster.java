import javafx.application.Platform;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

import java.util.Timer;
import java.util.TimerTask;

public class MyMonster {

    private int type;
    private int hp;
    private int maxHp;
    public ImageView imageView;
    public ProgressBar hpView;
    private double speed;

    private int dirX = 0;
    private int dirY = 0;
    public double posX = 0;
    public double posY = 0;
    private int segId = 0;

    private Boolean slow = Boolean.FALSE;
    private int slowCount = 0;

    private int curFrame = 0;
    private final int frameNum = 2;
    private int frameCounter = 0;

    public Boolean alive = Boolean.TRUE;

    MyMonster(AnchorPane monsterPane, int type, int hp) {
        this.imageView = new ImageView();
        this.type = type;
        this.speed = Utils.monsterSpeed[type];
        this.maxHp = hp;
        this.hp = hp;
        this.frameCounter += Utils.updateInterval;
        String imgPath = Utils.monsterSrcRoot + "mon_" + type + "_" + curFrame + ".png";
        Platform.runLater(()->{
            this.imageView.setImage(new Image(imgPath));
            // 添加血条
            this.hpView = new ProgressBar();
            this.hpView.setProgress(100);
            this.hpView.setPrefWidth(Utils.placeLength);
            this.hpView.setPrefHeight(10);
            this.hpView.setStyle("-fx-accent: palegreen;");
            this.hpView.setVisible(false);
            this.hpView.setOpacity(0.7);
            monsterPane.getChildren().add(imageView);
            monsterPane.getChildren().add(this.hpView);
        });
    }

    public void setDirection(int dirX, int dirY) {
        this.dirX = dirX;
        this.dirY = dirY;
    }

    public void setPosition(double posX, double posY) {
        this.posX = posX;
        this.posY = posY;
        Platform.runLater(()-> {
            imageView.setLayoutX(posX);
            imageView.setLayoutY(posY);
            hpView.setLayoutX(posX);
            hpView.setLayoutY(posY-Utils.hpViewHeight);
        });
    }

    public void acceptDamage(double damageNum) {
        this.hp -= damageNum;
        if(this.hp <= 0) {
            remove();
        }
    }

    public void acceptSlowStat() {
        if(!this.slow) {
            this.slow = Boolean.TRUE;
            this.speed *= Utils.speedDecreaseRatio;
        }
        this.slowCount = 0;
    }

    public int getDirX() {
        return dirX;
    }

    public int getDirY() {
        return dirY;
    }

    public int getSegId() { return segId; }

    public int update(double nextX, double nextY, int terminalSeg, int[] nextDir) {
        if(!this.alive) {
            return Utils.MONSTER_KILLED;
        }
        // 更新位置
        double tmpX = posX + speed*dirX;
        double tmpY = posY + speed*dirY;
        double deltaX = tmpX - nextX;
        double deltaY = tmpY - nextY;
        if(deltaX*dirX + deltaY*dirY > 0 ) {
            // 越过了拐弯点，进入下一段
            if( (terminalSeg - segId) <= 2) {
                // 到达终点
                remove();
                return Utils.MONSTER_REACH_END;
            } else {
                tmpX = nextX;
                tmpY = nextY;
                this.dirX = nextDir[0];
                this.dirY = nextDir[1];
                this.segId = this.segId + 1;
            }
        }
        setPosition(tmpX, tmpY);

        // 更新动画
        if(this.frameCounter == 0) {
            curFrame = (curFrame + 1) % frameNum;
            String imgPath = Utils.monsterSrcRoot + "mon_" + type + "_" + curFrame + ".png";
            Platform.runLater(()->imageView.setImage(new Image(imgPath)));
        }
        this.frameCounter = (this.frameCounter+Utils.updateInterval)%Utils.animationInterval;

        // 更新hp条
        double progress = (double)this.hp / this.maxHp;
        Platform.runLater(()->{
            this.hpView.setProgress(progress);
            this.hpView.setVisible(true);
        });

        // 更新状态信息
        if(this.slow) {
            this.slowCount += Utils.updateInterval;
            if(this.slowCount >= Utils.slowTime) {
                this.slow = Boolean.FALSE;
                this.speed = Utils.monsterSpeed[this.type];
            }
        }
        return Utils.MONSTER_ALIVE;
    }

    // 清除该怪物
    private void remove() {
        this.speed = 0;
        this.alive = Boolean.FALSE;     // 标记为死亡
        Platform.runLater(()->this.imageView.setImage(null));  // 清除图片
    }
}
