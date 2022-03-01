import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;


public class BasicStageController{

    @FXML
    protected ImageView background;
    @FXML
    protected ImageView route;
    @FXML
    protected AnchorPane placePane;
    @FXML
    protected AnchorPane monsterPane;
    @FXML
    protected AnchorPane bulletPane;

    protected StageManager stageManager;

    @FXML
    protected ImageView tower_bottle;
    @FXML
    protected ImageView tower_snow;
    @FXML
    protected ImageView tower_sun;
    @FXML
    protected ImageView tower_arrow;
    @FXML
    protected ImageView tower_anchor;
    @FXML
    protected ImageView removeTower;
    @FXML
    protected ImageView pauseBtn;
    @FXML
    protected Label coinNum;
    @FXML
    protected Label curWave;
    @FXML
    protected Label totalWave;
    @FXML
    protected AnchorPane gameMenuPane;
    @FXML
    protected AnchorPane gameOverPane;
    @FXML
    protected  AnchorPane stageFinishPane;

    protected Boolean isPause = Boolean.FALSE;

    protected Timer gameTimer = new Timer();

    protected Timer numTimer = new Timer();

    protected int placeNum;

    protected int stageId;

    protected int[][] monsterOrder;

    MediaPlayer bgmPlayer;

    protected void gameInit() {
        // 初始化stage_manager
        stageManager = new StageManager(placeNum, stageId, monsterPane, monsterOrder);
        // 初始化放置位数组
        ObservableList<Node> nodeList = placePane.getChildren();
        for(Node x:nodeList) {
            String strId = x.getId();
            if (strId.startsWith("place_")) {
                String[] splitId = strId.split("_");
                int placeId = Integer.parseInt(splitId[1]);
                stageManager.placesArr[placeId] = new MyPlace((ImageView) x, bulletPane);
            }
        }

        // 初始化部分显示组件
        this.coinNum.setText("" + Utils.initCoinNum);
        int cWave = stageManager.getCurrentWave();
        int tWave = stageManager.getTotalWave();
        if(cWave < 10) {
            this.curWave.setText("0"+cWave);
        } else {
            this.curWave.setText(""+cWave);
        }
        if(tWave < 10) {
            this.totalWave.setText("/0"+tWave);
        } else {
            this.totalWave.setText("/"+tWave);
        }
        String imgPath = Utils.itemSrcRoot + "stop.png";
        pauseBtn.setImage(new Image(imgPath));
        gameMenuPane.setVisible(false);
        gameOverPane.setVisible(false);
        stageFinishPane.setVisible(false);

        this.gameTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    int stat = stageManager.update();
                    if(stat == Utils.GAME_CLEAR) {
                        stageFinish();
                    } else if(stat == Utils.GAME_OVER) {
                        gameOver();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        },0,Utils.updateInterval);

        // 更新金币,当前波数
        this.numTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(()-> {
                    coinNum.setText(""+stageManager.getCoinNum());
                    int cWave = stageManager.getCurrentWave();
                    if(cWave < 10) {
                        curWave.setText("0"+cWave);
                    } else {
                        curWave.setText(""+cWave);
                    }
                });
            }
        },0,Utils.updateInterval);

        // 初始化部分参数
        this.isPause = Boolean.FALSE;
        this.gameMenuPane.setVisible(false);

        // 初始化bgm播放器
        // 设置bgm播放器
        File file =  new File(Utils.mainBgmSound);
        Media media1 = new Media(file.toURI().toString());
        bgmPlayer = new MediaPlayer(media1);
        bgmPlayer.setCycleCount(Utils.bgmCycleTimes);
        bgmPlayer.play();
    }

    // 放置区域点击事件
    public void placeClicked(MouseEvent e) {
        // 获取当前点击的位置
        ImageView tar = (ImageView)e.getSource();
        String[] splitId = tar.getId().split("_");
        int placeId = Integer.parseInt(splitId[1]);

        double x = tar.getLayoutX();
        double y = tar.getLayoutY();
        stageManager.chosenPlace = placeId;
        setTowerChooser(stageManager.placesArr[placeId].isPlant, Boolean.TRUE, x, y);
    }

    // 非放置区域点击
    public void placeClickCancel() {
        setTowerChooser(Boolean.FALSE,Boolean.FALSE, 0, 0);
    }

    // 设置防御塔
    public void setTower(MouseEvent e) {
        // 获取当前点击的位置
        ImageView tar = (ImageView)e.getSource();
        String[] splitId = tar.getId().split("_");
        String towerName = splitId[1];

        int cPlace = stageManager.chosenPlace;
        if(cPlace >= 0 && cPlace < stageManager.placeNum) {
            stageManager.setTower(cPlace, towerName);
        }
        setTowerChooser(Boolean.TRUE, Boolean.FALSE, 0, 0);
    }

    // 移除防御塔
    public void removeTower() {
        int cPlace = stageManager.chosenPlace;
        if(cPlace >= 0 && cPlace < stageManager.placeNum) {
            stageManager.removeTower(cPlace);
        }
        setTowerChooser(Boolean.FALSE, Boolean.FALSE, 0, 0);
    }

    /*
       设置tower选择器的显示状态
       show-是否显示 (posX,posY)-显示的基准位置
    */
    public void setTowerChooser(Boolean hasSet, Boolean show, double posX, double posY) {
        if(!show) {
            tower_anchor.setVisible(false);
            tower_arrow.setVisible(false);
            tower_snow.setVisible(false);
            tower_sun.setVisible(false);
            tower_bottle.setVisible(false);
            removeTower.setVisible(false);
            return;
        }
        // 没有放置防御塔，
        if(!hasSet) {
            tower_anchor.setVisible(true);
            tower_arrow.setVisible(true);
            tower_snow.setVisible(true);
            tower_sun.setVisible(true);
            tower_bottle.setVisible(true);
            removeTower.setVisible(false);

            tower_bottle.setLayoutX(posX);
            tower_bottle.setLayoutY(posY);
            tower_sun.setLayoutX(posX - Utils.placeLength);
            tower_sun.setLayoutY(posY);
            tower_snow.setLayoutX(posX + Utils.placeLength);
            tower_snow.setLayoutY(posY);
            tower_arrow.setLayoutX(posX);
            tower_arrow.setLayoutY(posY - Utils.placeLength);
            tower_anchor.setLayoutX(posX);
            tower_anchor.setLayoutY(posY + Utils.placeLength);
            if (posX < Utils.placeLength) {
                tower_sun.setLayoutX(posX + Utils.placeLength);
                tower_sun.setLayoutY(posY - Utils.placeLength);
            } else if (posX + 2 * Utils.placeLength > Utils.stageWidth) {
                tower_snow.setLayoutX(posX - Utils.placeLength);
                tower_snow.setLayoutY(posY - Utils.placeLength);
            } else if (posY + 2 * Utils.placeLength > Utils.stageHeight) {
                tower_anchor.setLayoutX(posX - Utils.placeLength);
                tower_anchor.setLayoutY(posY - Utils.placeLength);
            }
        } else {
            // 设置移除菜单
            tower_anchor.setVisible(false);
            tower_arrow.setVisible(false);
            tower_snow.setVisible(false);
            tower_sun.setVisible(false);
            tower_bottle.setVisible(false);
            int cPlace = stageManager.chosenPlace;
            if(cPlace >= 0 && cPlace < stageManager.placeNum) {
                String towerName = Utils.towerName[stageManager.placesArr[cPlace].getTowerId()];
                String imgPath = Utils.towerSrcRoot + towerName + "-return.png";
                removeTower.setImage(new Image(imgPath));
                removeTower.setLayoutX(posX);
                removeTower.setLayoutY(posY-Utils.placeLength);
                removeTower.setVisible(true);
            }
        }
    }

    // 暂停/恢复
    public void pauseBtnClicked() {
        if(this.isPause) {
            resumeGame();
        } else {
            pauseGame();
        }
    }

    private void pauseGame() {
        // 修改图标
        String imgPath = Utils.itemSrcRoot + "run.png";
        pauseBtn.setImage(new Image(imgPath));

        this.stageManager.pauseGenerateMonster();
        this.gameTimer.cancel();
        bgmPlayer.pause();
        this.isPause = Boolean.TRUE;
    }

    private void resumeGame() {
        // 修改图标
        String imgPath = Utils.itemSrcRoot + "stop.png";
        pauseBtn.setImage(new Image(imgPath));

        this.stageManager.resumeGenerateMonster();
        this.gameTimer = new Timer();
        this.gameTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    int stat = stageManager.update();
                    if(stat == Utils.GAME_CLEAR) {
                        stageFinish();
                    } else if(stat == Utils.GAME_OVER) {
                        gameOver();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        },0,Utils.updateInterval);

        bgmPlayer.play();

        this.isPause = Boolean.FALSE;
    }

    public void showMenuBtnClicked() {
        this.gameMenuPane.setVisible(true);
        pauseGame();
    }

    public void resumeGameBtnClicked() {
        this.gameMenuPane.setVisible(false);
        this.gameOverPane.setVisible(false);
        this.stageFinishPane.setVisible(false);
        resumeGame();
    }

    public void jmpSelectStage() {
        this.stageManager.clearAll(this.placePane, this.monsterPane, this.bulletPane);
        Scene scene = null;

        try {
            scene = new Scene(FXMLLoader.load(Objects.requireNonNull(getClass().getResource("fxml/stageselect.fxml"))));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Main.primaryStage.setScene(scene);
    }

    protected void gameOver() {
        this.gameOverPane.setVisible(true);
        File file = new File(Utils.gameLoseSound);
        MediaPlayer m = new MediaPlayer(new Media(file.toURI().toString()));
        Platform.runLater(m::play);
        pauseGame();
    }

    protected void stageFinish() {
        this.stageFinishPane.setVisible(true);
        File file = new File(Utils.gameWinSound);
        MediaPlayer m = new MediaPlayer(new Media(file.toURI().toString()));
        Platform.runLater(()->m.play());
        pauseGame();
    }

    public void gameRestartBtnClicked() {

        this.gameRestart();
    }

    private void gameRestart() {
        if(this.gameTimer != null) {
            this.gameTimer.cancel();
        }
        if(this.numTimer != null) {
            this.numTimer.cancel();
        }

        this.stageManager.clearAll(this.placePane, this.monsterPane, this.bulletPane);

        this.gameTimer = new Timer();
        this.numTimer = new Timer();
        gameInit();
    }
}
