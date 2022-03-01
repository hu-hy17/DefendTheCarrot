import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class StageManager {

    // 对象
    public int placeNum;
    public int chosenPlace;
    private int stageId;
    MyPlace[] placesArr;
    ConcurrentLinkedQueue<MyMonster> monsterList;
    ConcurrentLinkedQueue<MyBullet> bulletList;
    MyCarrot carrot;

    // 刷怪
    private AnchorPane monsterPane;
    private Timer monsterGenerator;
    private int monsterCount = 0;
    private int generateCount = 0;
    private int[][] monsterOrder;
    private Boolean waveFinish = Boolean.FALSE;
    // 参数
    private int coinNum;
    private int currentWave;
    private int totalWave;
    public int getCoinNum() { return coinNum; }

    public int getCurrentWave() { return currentWave; }

    public int getTotalWave() { return totalWave; }

    // 路线构造
    private double[] routeX;
    private double[] routeY;

    StageManager(int placeNum, int stageId, AnchorPane monsterPane, int[][] monsterOrder) {
        this.placeNum = placeNum;
        this.chosenPlace = -1;
        this.placesArr = new MyPlace[placeNum];
        this.monsterList = new ConcurrentLinkedQueue<>();
        this.bulletList = new ConcurrentLinkedQueue<>();

        this.routeX = Utils.routeXList[stageId];
        this.routeY = Utils.routeYList[stageId];
        this.stageId = stageId;

        this.coinNum = Utils.initCoinNum;
        this.currentWave = 1;
        this.totalWave = monsterOrder.length;

        this.monsterPane = monsterPane;
        this.monsterOrder = monsterOrder;

        this.carrot = new MyCarrot(this.routeX[routeX.length-1], this.routeY[routeY.length-1]);
        monsterPane.getChildren().add(this.carrot.imageView);

        this.monsterGenerator = new Timer();
        this.monsterGenerator.schedule(new TimerTask() {
            @Override
            public void run() {
                if(generateCount == 0) {
                    if(monsterCount<monsterOrder[currentWave-1].length) {
                        AddMonster(monsterOrder[currentWave-1][monsterCount],monsterPane);
                        ++monsterCount;
                    }
                } else if(monsterCount>=monsterOrder[currentWave-1].length && generateCount>=Utils.monsterAppearInterval/2) {
                    // 本轮刷怪结束
                    waveFinish = Boolean.TRUE;
                    this.cancel();
                }
                generateCount = (Utils.updateInterval+generateCount)%Utils.monsterAppearInterval;
            }
        },0, Utils.updateInterval);
    }

    private int[] judgeDir(int segId) {
        if( (segId + 1) >= routeX.length) {
            return new int[]{0,0};
        }
        double deltaX = routeX[segId+1] - routeX[segId];
        double deltaY = routeY[segId+1] - routeY[segId];
        if(deltaX > 0) {
            return new int[]{1,0};
        }
        else if(deltaX < 0) {
            return new int[]{-1,0};
        }
        else if(deltaY > 0) {
            return new int[]{0,1};
        }
        else {
            return new int[]{0,-1};
        }
    }

    public void AddMonster(int type, AnchorPane monsterPane) {
        int hp = Utils.monsterBasicHP[type] + (this.currentWave-1) * Utils.monsterIncreaseHp[type];
        MyMonster newMonster = new MyMonster(monsterPane, type, hp);
        int[] direction = judgeDir(0);
        newMonster.setDirection(direction[0], direction[1]);
        newMonster.setPosition(routeX[0], routeY[0]);
        this.monsterList.add(newMonster);
    }

    public int update() {
        // 更新怪物
        for(Iterator<MyMonster> mIt=monsterList.iterator(); mIt.hasNext();) {
            MyMonster m = mIt.next();

            // 更新位置
            int seg = m.getSegId();
            int stat = m.update(routeX[seg+1], routeY[seg+1], routeX.length, judgeDir(seg+1));

            // 如果怪物到达终点，减少萝卜生命值
            if(stat == Utils.MONSTER_REACH_END) {
                this.carrot.causeDamage();
            }
            else if(stat == Utils.MONSTER_KILLED) {
                // 如果怪物被消灭，加钱
                this.coinNum += Utils.killMonsterReward;
            }
            if(!m.alive) {
                // 怪物死亡，清除imageView,hpView，并删除链表项
                AnchorPane parentNode = (AnchorPane) m.imageView.getParent();
                Platform.runLater(()->{
                    parentNode.getChildren().remove(m.imageView);
                    parentNode.getChildren().remove(m.hpView);
                });
                mIt.remove();
            }
        }

        // 更新防御塔
        Arrays.stream(placesArr).parallel().forEach((x)->{
            if(x.isPlant == Boolean.TRUE) {
                // 寻找攻击目标
                LinkedList<MyMonster> targets = new LinkedList<>();
                int targetCount = 0;
                int maxTargetNum = Utils.towerMaxTargetNum[x.getTowerId()];

                for (MyMonster monster : monsterList) {
                    double dis = Utils.distance(monster.imageView.getLayoutX(), monster.imageView.getLayoutY(),
                            x.imageview.getLayoutX(), x.imageview.getLayoutY());
                    if (dis <= Utils.towerAttackRange[x.getTowerId()]) {
                        targets.add(monster);
                        ++targetCount;
                        if (targetCount >= maxTargetNum) {
                            // 到达目标上限
                            break;
                        }
                    }
                }
                if(x.getTowerId() == Utils.TOWER_SUN || x.getTowerId() == Utils.TOWER_SNOW) {
                    x.generateRangeAttack(targets);
                } else {
                    x.generateAttack(targets, bulletList);
                }

                x.update();
            }
        });

        // 更新子弹
        bulletList.parallelStream().forEach((bullet)->{
            if(bullet.exist) {
                    // 判断子弹是否碰撞
                for (MyMonster monster : monsterList) {
                    double mCenterX = monster.posX + Utils.placeLength/2.0;
                    double mCenterY = monster.posY + Utils.placeLength/2.0;
                    double bCenterX = bullet.imageView.getLayoutX() + Utils.bulletWidth[bullet.getType()]/2.0;
                    double bCenterY = bullet.imageView.getLayoutY() + Utils.bulletHeight[bullet.getType()]/2.0;
                    if (Utils.distance(mCenterX, mCenterY, bCenterX, bCenterY) < Utils.hitRange) {
                        // 已经碰撞到某个怪物
                        bullet.exist = Boolean.FALSE;
                        // 是否分裂
                        if (bullet.getType() == Utils.TOWER_ANCHOR && bullet.hasDivide == Boolean.FALSE) {
                            MyBullet[] childBullets = bullet.divide(this.stageId, monster.getSegId());
                            AnchorPane parent = (AnchorPane) bullet.imageView.getParent();
                            for (MyBullet cb : childBullets) {
                                this.bulletList.add(cb);
                                Platform.runLater(() -> parent.getChildren().add(cb.imageView));
                            }
                        }
                        // 怪物收到伤害
                        monster.acceptDamage(Utils.towerInstantDamage[bullet.getType()]);
                        break;
                    }
                }
                bullet.update();
            }
        });

        // 如果子弹碰撞到怪物或者已经消失，清理该子弹
        for(Iterator<MyBullet> bIt=bulletList.iterator(); bIt.hasNext();) {
            MyBullet bullet = bIt.next();
            if(!bullet.exist) {
                bullet.remove();
                AnchorPane parentNode = (AnchorPane) bullet.imageView.getParent();
                Platform.runLater(()->parentNode.getChildren().remove(bullet.imageView));
                bIt.remove();
            }
        }

        // 更新萝卜
        this.carrot.update();

        // 判断游戏状态
        if(this.carrot.hp <= 0) {
            return Utils.GAME_OVER;
        }
        if(this.waveFinish && this.monsterList.isEmpty()) {
            if(this.currentWave == this.totalWave) {
                return Utils.GAME_CLEAR;
            }
            // 下一轮开始
            this.waveFinish = Boolean.FALSE;
            ++this.currentWave;
            this.generateCount = 0;
            this.monsterCount = 0;

            this.monsterGenerator = new Timer();
            this.monsterGenerator.schedule(new TimerTask() {
                @Override
                public void run() {
                    if(generateCount == 0) {
                        if(monsterCount<monsterOrder[currentWave-1].length) {
                            AddMonster(monsterOrder[currentWave-1][monsterCount],monsterPane);
                            ++monsterCount;
                        }
                    } else if(monsterCount>=monsterOrder[currentWave-1].length && generateCount>=Utils.monsterAppearInterval/2) {
                        // 本轮刷怪结束
                        waveFinish = Boolean.TRUE;
                        this.cancel();
                    }
                    generateCount = (Utils.updateInterval+generateCount)%Utils.monsterAppearInterval;
                }
            },Utils.waveInterval, Utils.updateInterval);
        }
        return Utils.GAME_CONTINUE;
    }

    public void pauseGenerateMonster() {
        if(!this.waveFinish) {
            this.monsterGenerator.cancel();
        }
    }

    public void resumeGenerateMonster() {
        if(!this.waveFinish) {
            this.monsterGenerator = new Timer();
            this.monsterGenerator.schedule(new TimerTask() {
                @Override
                public void run() {
                    if (generateCount == 0) {
                        if (monsterCount < monsterOrder[currentWave - 1].length) {
                            AddMonster(monsterOrder[currentWave - 1][monsterCount], monsterPane);
                            ++monsterCount;
                        }
                    } else if(monsterCount>=monsterOrder[currentWave-1].length && generateCount>=Utils.monsterAppearInterval/2) {
                        // 本轮刷怪结束
                        waveFinish = Boolean.TRUE;
                        this.cancel();
                    }
                    generateCount = (Utils.updateInterval + generateCount) % Utils.monsterAppearInterval;
                }
            }, 0, Utils.updateInterval);
        }
    }

    public void setTower(int cPlace, String towerName) {
        if(this.placesArr[cPlace].isPlant == Boolean.FALSE) {
            int type = 0;
            switch (towerName) {
                case "bottle":
                    type = Utils.TOWER_BOTTLE;
                    break;
                case "sun":
                    type = Utils.TOWER_SUN;
                    break;
                case "snow":
                    type = Utils.TOWER_SNOW;
                    break;
                case "arrow":
                    type = Utils.TOWER_ARROW;
                    break;
                case "anchor":
                    type = Utils.TOWER_ANCHOR;
                    break;
            }

            if(this.coinNum >= Utils.towerCost[type]) {
                this.placesArr[cPlace].setTower(type, towerName);
                this.coinNum -= Utils.towerCost[type];
            }
        }
    }

    public void removeTower(int cPlace) {
        if(this.placesArr[cPlace].isPlant == Boolean.TRUE) {
            this.coinNum += Utils.towerCostReturn[placesArr[cPlace].getTowerId()];
            this.placesArr[cPlace].removeTower();
        }
    }

    public void clearAll(AnchorPane placePane, AnchorPane monsterPane, AnchorPane bulletPane) {
        for(MyPlace x:this.placesArr) {
            if(x.isPlant) {
                x.imageview.setImage(null);
            }
        }

        for (MyMonster monster : monsterList) {
            monster.imageView.setImage(null);
        }

        for (MyBullet bullet : bulletList) {
            bullet.imageView.setImage(null);
        }

        monsterPane.getChildren().clear();
        bulletPane.getChildren().clear();
    }
}
