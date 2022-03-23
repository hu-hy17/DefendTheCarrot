import java.util.Objects;

public class Utils {
    // 页面参数
    static final int placeLength = 80;
    static final int stageWidth = 1200;
    static final int stageHeight = 800;
    static final int hpViewHeight = 5;
    static final String itemSrcRoot = "assets/imgs/item/";

    // 刷新频率
    static final int updateInterval = 30;
    static final int animationInterval = 420;
    static final int monsterAppearInterval = 1200;

    // 防御塔编号
    static final int TOWER_BOTTLE = 0;
    static final int TOWER_SUN = 1;
    static final int TOWER_SNOW = 2;
    static final int TOWER_ARROW = 3;
    static final int TOWER_ANCHOR = 4;
    static final String towerSrcRoot = "assets/imgs/tower/";   // tower资源文件根目录
    static final String[] towerName = {"bottle", "sun", "snow", "arrow", "anchor"};
    static final String graySuffix = "-gray";
    static final String setSuffix = "-set";
    static final String returnSuffix = "-return";

    // 防御塔属性
    static final int[] towerInstantDamage = {15, 20, 0, 8, 25};
    static final double[] towerAttackRange = {200, 200, 200, 200, 150};
    static final int burnDamage = 5;
    static final double speedDecreaseRatio = 0.5;
    static final int[] towerMaxTargetNum = {1,10000,10000,5,1};

    // 子弹属性
    static final int[] towerAttackInterval = {300, 1200, 1500, 720, 900};
    static final double[] bulletSpeed = {18, 0, 0, 12, 5};
    static final int hitRange = 40;
    static final String bulletSrcRoot = "assets/imgs/bullet/"; //子弹资源文件根目录
    static final int[] bulletWidth = {22,0,0,19,36};
    static final int[] bulletHeight = {22,0,0,42,39};
    static final int maxArrowNum = 5;
    static final int maxAnchorDiv = 2;
    static final int rotateSpeed = 10;
    static final int rangeBulletMaxLength = 400;
    static final int rangeBulletMinLength = 200;
    static final int rangeBulletExpandSpeed = 10;
    static final int slowTime = 1800;

    // 金币相关
    static final int initCoinNum = 100;
    static final int[] towerCost = {100, 180, 160, 220, 260};
    static final int[] towerCostReturn = {80, 144, 128, 176, 208};
    static final int coinGrowNum = 10;
    static final int killMonsterReward = 20;

    // 怪物编号
    static final String monsterSrcRoot = "assets/imgs/monster/";
    static final int MONSTER_BAT = 0;
    static final int MONSTER_GREEN = 1;
    static final int MONSTER_SHEEP = 2;

    // 怪物属性
    static final int[] monsterBasicHP = {10,20,30};
    static final int[] monsterIncreaseHp = {50, 100, 200};
    static final double[] monsterSpeed = {2,1.5,1};
    static final int MONSTER_ALIVE = 0;
    static final int MONSTER_KILLED = 1;
    static final int MONSTER_REACH_END = 2;
    static final int LINEAR_HP_INCREASE = 50;
    static final double EXP_HP_INCREASE = 1.2;

    // 萝卜属性
    static final int carrotImgLength = 90;
    static final int carrotAnimationInterval = 300;
    static final String carrotImgSrc = "assets/imgs/item/";
    static final int carrotMaxHp = 10;

    // 游戏状态
    static final int GAME_CONTINUE = 1;
    static final int GAME_CLEAR = 2;
    static final int GAME_OVER = 3;

    // 方向
    static final int UP = 0;
    static final int DOWN = 1;
    static final int LEFT = 2;
    static final int RIGHT = 3;

    // 路线
    static final double[][] routeXList = {
            {110, 110, 411, 411, 708, 708, 1006, 1006},
            {708, 708, 210, 210, 100, 100, 405, 405, 709, 709, 1010, 1010},
            {14, 904, 904, 214, 214, 904}
    };

    static final double[][] routeYList = {
            {35, 311, 311, 206, 206, 309, 309, 22},
            {385, 512, 512, 325, 325, 226, 226, 133, 133, 43, 43, 405},
            {20, 20, 220 ,220, 416, 416}
    };
    static final int[][] routeDirection = {
            {DOWN, RIGHT, UP, RIGHT, DOWN, RIGHT, UP},
            {DOWN, LEFT, UP, LEFT, UP, RIGHT, UP, RIGHT, UP, RIGHT, DOWN},
            {RIGHT, DOWN, LEFT, DOWN, RIGHT}
    };

    // 怪物出现逻辑
    static final int waveInterval = 2000;
    static final int[][] monsterOrder1 = {
            {1,2,1,1,2,1,1,0,1,1,1,1,0,2,1,1,1,2},
            {0,1,0,0,1,2,2,0,0,0,1,0,2,1,0,1,1,1},
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,2},
            {2,2,2,1,2,1,1,0,2,1,2,1,0,2,1,1,1,2},
            {1,2,0,0,1,0,1,0,2,1,2,1,0,2,1,1,1,2}
    };

    static final int[][] monsterOrder2 = {
            {1,2,1,1,2,1,1,0,1,1,1,1,0,2,1,1,1,2},
            {0,1,0,0,1,2,2,0,0,0,1,0,2,1,0,1,1,1},
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,2},
            {2,2,2,1,2,1,1,0,2,1,2,1,0,2,1,1,1,2},
            {1,2,0,0,1,0,1,0,2,1,2,1,0,2,1,1,1,2},
            {1,2,1,1,2,1,1,0,1,1,1,1,0,2,1,1,1,2},
            {0,1,0,0,1,2,2,0,0,0,1,0,2,1,0,1,1,1},
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,2},
            {2,2,2,1,2,1,1,0,2,1,2,1,0,2,1,1,1,2},
            {1,2,0,0,1,0,1,0,2,1,2,1,0,2,1,1,1,2}
    };

    static final int[][] monsterOrder3 = {
            {1,2,1,1,2,1,1,0,1,1,1,1,0,2,1,1,1,2},
            {0,1,0,0,1,2,2,0,0,0,1,0,2,1,0,1,1,1},
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,2},
            {2,2,2,1,2,1,1,0,2,1,2,1,0,2,1,1,1,2},
            {1,2,0,0,1,0,1,0,2,1,2,1,0,2,1,1,1,2},
            {1,2,1,1,2,1,1,0,1,1,1,1,0,2,1,1,1,2},
            {0,1,0,0,1,2,2,0,0,0,1,0,2,1,0,1,1,1},
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,2},
            {2,2,2,1,2,1,1,0,2,1,2,1,0,2,1,1,1,2},
            {1,2,0,0,1,0,1,0,2,1,2,1,0,2,1,1,1,2},
            {1,1,2,2,1,2,1,2,1,2,2,2,1,2,1,2},
            {2,2,2,2,2,2,2,2,2},
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
            {2,1,2,2,1,1,0,0,0,1,2,0,1,2,2,2},
            {2,1,1,1,1,0,0,2,1,2,2,2,0,1,1},
            {2,2,2,1,2,1,1,0,2,1,2,1,0,2,1,1,1,2},
            {1,2,0,0,1,0,1,0,2,1,2,1,0,2,1,1,1,2},
            {1,2,1,1,2,1,1,0,1,1,1,1,0,2,1,1,1,2},
            {2,2,2,1,2,1,1,0,2,1,2,1,0,2,1,1,1,2},
            {2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2},
    };

    // 距离计算
    static double distance(double x1, double y1, double x2, double y2) {
        return Math.sqrt( (x1-x2)*(x1-x2) + (y1-y2)*(y1-y2) );
    }

    // 音效
    static final String soundResPath = "/assets/mp3/";
    static final String mainBgmSound = Objects.requireNonNull(Utils.class.getResource(soundResPath + "mainMenuBgm.mp3")).toString();
    static final String[] towerAttackSound = {
            Objects.requireNonNull(Utils.class.getResource(soundResPath + "Bottle.mp3")).toString(),
            Objects.requireNonNull(Utils.class.getResource(soundResPath + "Sun.mp3")).toString(),
            Objects.requireNonNull(Utils.class.getResource(soundResPath + "Snow.mp3")).toString(),
            Objects.requireNonNull(Utils.class.getResource(soundResPath + "Arrow.mp3")).toString(),
            Objects.requireNonNull(Utils.class.getResource(soundResPath + "Anchor.mp3")).toString(),
    };
    static final String carrotInjureSound = Objects.requireNonNull(Utils.class.getResource(soundResPath + "Injure.mp3")).toString();
    static final String gameWinSound = Objects.requireNonNull(Utils.class.getResource(soundResPath + "GameWin.mp3")).toString();
    static final String gameLoseSound = Objects.requireNonNull(Utils.class.getResource(soundResPath + "GameLose.mp3")).toString();
    static final int bgmCycleTimes = 1000;
}
