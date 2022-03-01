import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class MyBullet {

    // 类型
    private int type;
    // 初始位置以及目标位置
    private double posX;
    private double posY;
    private double tarPosX;
    private double tarPosY;
    // 速度
    private double speedX;
    private double speedY;
    // 贴图
    public ImageView imageView;
    // 是否存在
    public Boolean exist = Boolean.TRUE;
    // 是否分裂(anchor专用)
    public Boolean hasDivide = Boolean.FALSE;
    // 旋转角度(anchor专用)
    private int rotation = 0;

    MyBullet(double posX, double posY, double tPosX, double tPosY, int type) {
        this.posX = posX - Utils.bulletWidth[type]/2.0;
        this.posY = posY - Utils.bulletHeight[type]/2.0;
        this.tarPosX = tPosX - Utils.bulletWidth[type]/2.0;
        this.tarPosY = tPosY - Utils.bulletHeight[type]/2.0;
        this.type = type;

        // 计算速度
        double speed = Utils.bulletSpeed[type];
        if(posX == tPosX) {
            this.speedX = 0;
            this.speedY = speed;
            if(posY > tPosY) {
                this.speedY *= -1;
            }
        } else {
            double tan = (tPosY - posY)/(tPosX - posX);
            this.speedY = speed*Math.abs(tan)/Math.sqrt(1+tan*tan);
            this.speedX = speed/Math.sqrt(1+tan*tan);
            if(posX > tPosX) {
                this.speedX *= -1;
            }
            if(posY > tPosY) {
                this.speedY *= -1;
            }
        }
        // 设置贴图
        this.imageView = new ImageView();
        String imgPath = Utils.bulletSrcRoot + "bullet_" + type + ".png";
        Platform.runLater(()-> {
            imageView.setImage(new Image(imgPath));
            imageView.setLayoutX(posX);
            imageView.setLayoutY(posY);
        });
    }

    public int getType() {
        return type;
    }

    public void update() {
        this.posX += this.speedX;
        this.posY += this.speedY;
        double speed = Utils.bulletSpeed[type];
        // 子弹到达目标点后消失
        if (Utils.distance(posX, posY, tarPosX, tarPosY) < speed) {
            this.exist = Boolean.FALSE;
            return;
        }

        Platform.runLater(()-> {
            imageView.setLayoutX(posX);
            imageView.setLayoutY(posY);
            if (this.type == Utils.TOWER_ANCHOR) {
                imageView.setRotate(rotation);
                rotation = (rotation + Utils.rotateSpeed) % 360;
            }
        });
    }

    public void remove() {
        Platform.runLater(()->this.imageView.setImage(null));
    }

    public MyBullet[] divide(int stageId, int segId) {
        // 生成分裂弹
        MyBullet child1, child2;
        switch(Utils.routeDirection[stageId][segId]) {
            case Utils.UP:
            case Utils.DOWN:
                child1 = new MyBullet(this.posX + Utils.bulletWidth[Utils.TOWER_ANCHOR]/2.0,
                        this.posY + Utils.bulletHeight[Utils.TOWER_ANCHOR]/2.0 - Utils.placeLength/2.0,
                        Utils.routeXList[stageId][segId] + Utils.placeLength/2.0,
                        Utils.routeYList[stageId][segId] + Utils.placeLength/2.0, Utils.TOWER_ANCHOR);
                child2 = new MyBullet(this.posX + Utils.bulletWidth[Utils.TOWER_ANCHOR]/2.0,
                        this.posY + Utils.bulletHeight[Utils.TOWER_ANCHOR]/2.0 + Utils.placeLength/2.0,
                        Utils.routeXList[stageId][segId+1] + Utils.placeLength/2.0,
                        Utils.routeYList[stageId][segId+1] + Utils.placeLength/2.0, Utils.TOWER_ANCHOR);
                child1.hasDivide = Boolean.TRUE;
                child2.hasDivide = Boolean.TRUE;
                break;
            case Utils.LEFT:
            case Utils.RIGHT:
                child1 = new MyBullet(this.posX + Utils.bulletWidth[Utils.TOWER_ANCHOR]/2.0 - Utils.placeLength/2.0,
                         this.posY + Utils.bulletHeight[Utils.TOWER_ANCHOR]/2.0,
                        Utils.routeXList[stageId][segId] + Utils.placeLength/2.0,
                        Utils.routeYList[stageId][segId] + Utils.placeLength/2.0, Utils.TOWER_ANCHOR);
                child2 = new MyBullet(this.posX + Utils.bulletWidth[Utils.TOWER_ANCHOR]/2.0 + Utils.placeLength/2.0,
                        this.posY + Utils.bulletHeight[Utils.TOWER_ANCHOR]/2.0,
                        Utils.routeXList[stageId][segId+1] + Utils.placeLength/2.0,
                        Utils.routeYList[stageId][segId+1] + Utils.placeLength/2.0, Utils.TOWER_ANCHOR);
                child1.hasDivide = Boolean.TRUE;
                child2.hasDivide = Boolean.TRUE;
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + Utils.routeDirection[stageId][segId]);
        }
        return new MyBullet[]{child1, child2};
    }
}
