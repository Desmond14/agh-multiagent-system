package pl.edu.agh.agents.gui;

import jade.core.AID;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Main extends Application {
    private static final Color LANE_COLOR = Color.BEIGE;
    private static final int MILLIS_PER_MOVE = 20;
    public static final int STAGE_WIDTH = 800;
    public static final int STAGE_HEIGHT = 600;
    private List<TrafficLane> trafficLanes = new ArrayList<TrafficLane>();
    private Map<AID, Rectangle> carShapes = new ConcurrentHashMap<AID, Rectangle>();
    private AgentController supervisor;
    private ContainerController agentContainer;
    private Group root;
    private Group lanes;


    @Override
    public void start(Stage primaryStage) throws Exception {
        root = new Group();
        Scene scene = new Scene(root, 800, 600, Color.BLACK);
        primaryStage.setScene(scene);
        //TODO: trafficLanes should be loaded from external source, e.g. xml
        trafficLanes.add(new TrafficLane(new Point(0, 275), new Point(800, 325)));
        trafficLanes.add(new TrafficLane(new Point(375, 0), new Point(425, 600)));
        lanes = new Group();
        drawLanes(lanes);

        root.getChildren().add(lanes);
        primaryStage.show();
//        displayText("Collision detected!");
        initializeJadePlatform();
    }

    private void initializeJadePlatform() throws StaleProxyException {
        Runtime rt = Runtime.instance();
        Profile profile = new ProfileImpl("127.0.0.1", 1199, Profile.PLATFORM_ID);
        profile.setParameter(Profile.PLATFORM_ID, "Platform Name");
        profile.setParameter(Profile.CONTAINER_NAME, "Container Name");
        agentContainer = rt.createMainContainer(profile);
        supervisor = agentContainer.createNewAgent("supervisor", "pl.edu.agh.agents.SupervizorAgent",
                new Object[]{this});

        supervisor.start();
    }

    private void drawLanes(Group parent) {
        for (TrafficLane lane : trafficLanes) {
            draw(parent, lane);
        }
    }

    private void draw(Group parent, TrafficLane lane) {
        double width = lane.getBottomRight().getX() - lane.getUpperLeft().getX();
        double height = lane.getBottomRight().getY() - lane.getUpperLeft().getY();
        double x = lane.getUpperLeft().getX();
        double y = lane.getUpperLeft().getY();
        Rectangle r = new Rectangle(x, y, width, height);
        r.setFill(LANE_COLOR);

        parent.getChildren().add(r);
    }

    public void moveCar(final AID aid, final Point point) {
        if (pointOutsideStage(point)){
            return;
        }
        final Timeline timeline = new Timeline();
        timeline.setAutoReverse(false);
        timeline.setCycleCount(1);
        timeline.setDelay(new Duration(MILLIS_PER_MOVE));
        KeyValue keyValueX = new KeyValue(carShapes.get(aid).xProperty(), point.getX());
        KeyValue keyValueY = new KeyValue(carShapes.get(aid).yProperty(), point.getY());

        timeline.getKeyFrames().add(new KeyFrame(new Duration(MILLIS_PER_MOVE), keyValueX, keyValueY));
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                timeline.play();
            }
        });
    }

    private boolean pointOutsideStage(Point point) {
        return point.getX() > STAGE_WIDTH || point.getY() > STAGE_HEIGHT;
    }

    public void addCar(AID aid, Car car) {
        final Rectangle carShape = new Rectangle(car.getUpperLeft().getX(), car.getUpperLeft().getY(), car.getWidth(),
                car.getHeight());
        carShape.setArcHeight(10);
        carShape.setArcWidth(10);
        carShape.setFill(car.getColor());
        carShapes.put(aid, carShape);
        Platform.runLater(new Runnable() {
            public void run() {
                lanes.getChildren().add(carShape);
            }
        });
    }

    public void displayText(String text){
        final Text textShape = new Text(50.0, 100.0, text);
        textShape.setFill(Color.RED);
        textShape.setFont(Font.font("Verdana", 30));
        textShape.setTextAlignment(TextAlignment.CENTER);
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                root.getChildren().add(textShape);
            }
        });

    }

    public static void main(String[] args) {
        launch(args);
    }
}
