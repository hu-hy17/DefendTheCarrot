import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Objects;

public class Main extends Application {
    static Stage primaryStage;

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("fxml/mainmenu.fxml")));
        primaryStage.setTitle("My Game");
        primaryStage.setScene(new Scene(root, 1200, 800));
        primaryStage.show();
        primaryStage.setOnCloseRequest(event -> System.exit(0));
        Main.primaryStage = primaryStage;
    }

    public static String getRealPath()
    {
        String realPath = Objects.requireNonNull(Main.class.getClassLoader().getResource(""))
                .getFile();
        java.io.File file = new java.io.File(realPath);
        realPath = file.getParentFile().getAbsolutePath(); //获取jar包的上级目录
        try {//路径decode转码
            realPath = java.net.URLDecoder.decode(realPath, "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return realPath ;
    }


    public static void main(String[] args) {
        launch(args);
    }
}
