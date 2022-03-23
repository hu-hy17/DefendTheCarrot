import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class MyTower {
    private final ImageView chooserView;
    private final int cost;
    private final int type;
    private Image activeImg;
    private Image notActiveImg;
    private Image returnImg;
    private Boolean affordable;

    MyTower(ImageView view, int type) {
        this.chooserView = view;
        this.cost = Utils.towerCost[type];
        this.type = type;
        this.affordable = false;

        // 设置图片
        this.activeImg = new Image(Utils.towerSrcRoot + Utils.towerName[type] + ".png");
        this.notActiveImg = new Image(Utils.towerSrcRoot + Utils.towerName[type] + Utils.graySuffix + ".png");
        this.returnImg = new Image(Utils.towerSrcRoot + Utils.towerName[type] + Utils.returnSuffix + ".png");
    }

    public int getCost() {
        return cost;
    }

    public ImageView getChooserView() {
        return chooserView;
    }

    public int getType() {
        return type;
    }

    public Image getActiveImg() {
        return activeImg;
    }

    public void setActiveImg(Image activeImg) {
        this.activeImg = activeImg;
    }

    public Image getNotActiveImg() {
        return notActiveImg;
    }

    public Image getReturnImg() {
        return returnImg;
    }

    public Boolean isAffordable() {
        return affordable;
    }

    public void setAffordable(Boolean affordable) {
        this.affordable = affordable;
    }
}
