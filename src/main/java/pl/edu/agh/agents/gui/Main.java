package pl.edu.agh.agents.gui;

import jade.core.*;
import jade.core.Runtime;
import jade.wrapper.*;
import javafx.animation.PathTransition;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

public class Main extends Application {

    private static final Color LANE_COLOR = Color.GRAY;
    private List<TrafficLane> lanes = new ArrayList<TrafficLane>();

    @Override
    public void start(Stage primaryStage) throws Exception {
        initializeJadePlatform();

        Group root = new Group();
        Scene scene = new Scene(root, 800, 600, Color.BLACK);
        primaryStage.setScene(scene);
        lanes.add(new TrafficLane(new Point(0,275), new Point(800, 325)));

        Group lanes = new Group();
        drawLanes(lanes);
        Path path = new Path();
        Rectangle car = new Rectangle (0, 280, 40, 40);
        car.setArcHeight(10);
        car.setArcWidth(10);
        car.setFill(Color.ORANGE);
        path.getElements().add(new MoveTo(20, 300));
        path.getElements().add(new LineTo(780, 300));
        PathTransition pathTransition = new PathTransition();
        pathTransition.setDuration(Duration.millis(4000));
        pathTransition.setPath(path);
        pathTransition.setNode(car);
        pathTransition.setOrientation(PathTransition.OrientationType.ORTHOGONAL_TO_TANGENT);
        pathTransition.setCycleCount(Timeline.INDEFINITE);
        pathTransition.setAutoReverse(true);

        root.getChildren().add(lanes);
        Group cars = new Group();
        cars.getChildren().add(car);
        root.getChildren().add(cars);
        primaryStage.show();
        Thread.sleep(1000);
        pathTransition.play();
    }

    private void initializeJadePlatform() throws StaleProxyException {
        Runtime rt = Runtime.instance();
        Profile profile = new ProfileImpl("127.0.0.1", 1199, Profile.PLATFORM_ID);
        profile.setParameter(Profile.PLATFORM_ID, "Platform Name");
        profile.setParameter(Profile.CONTAINER_NAME, "Container Name");
        ContainerController agentContainer = rt.createMainContainer(profile);
        AgentController supervisor = agentContainer.createNewAgent("supervisor", "pl.edu.agh.agents.SupervizorAgent", null);

        supervisor.start();
    }

    private void drawLanes(Group parent) {
        for (TrafficLane lane : lanes) {
            draw(parent, lane);
        }
    }

    private void draw(Group parent, TrafficLane lane) {
        int width = lane.getBottomRight().getX() - lane.getUpperLeft().getX();
        int height = lane.getUpperLeft().getY() - lane.getBottomRight().getY();

        Rectangle r = new Rectangle(width, height, LANE_COLOR);
        r.setX(lane.getUpperLeft().getX());
        r.setY(lane.getBottomRight().getY());

        parent.getChildren().add(r);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
