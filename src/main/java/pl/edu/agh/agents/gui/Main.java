package pl.edu.agh.agents.gui;

import jade.core.*;
import jade.core.Runtime;
import jade.wrapper.*;
import javafx.animation.PathTransition;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main extends Application {
    private static final Color LANE_COLOR = Color.GRAY;
    private List<TrafficLane> lanes = new ArrayList<TrafficLane>();
    private Map<AID, Rectangle> carShapes = new HashMap<AID, Rectangle>();
    private AgentController supervisor;
    private ContainerController agentContainer;
    private Group cars;


    @Override
    public void start(Stage primaryStage) throws Exception {
        Group root = new Group();
        Scene scene = new Scene(root, 800, 600, Color.BLACK);
        primaryStage.setScene(scene);
        lanes.add(new TrafficLane(new Point(0,275), new Point(800, 325)));

        Group lanes = new Group();
        drawLanes(lanes);

        root.getChildren().add(lanes);
        cars = new Group();
        root.getChildren().add(cars);
        primaryStage.show();

        initializeJadePlatform();
    }

    private void initializeJadePlatform() throws StaleProxyException {
        Runtime rt = Runtime.instance();
        Profile profile = new ProfileImpl("127.0.0.1", 1199, Profile.PLATFORM_ID);
        profile.setParameter(Profile.PLATFORM_ID, "Platform Name");
        profile.setParameter(Profile.CONTAINER_NAME, "Container Name");
        agentContainer = rt.createMainContainer(profile);
        supervisor = agentContainer.createNewAgent("supervisor", "pl.edu.agh.agents.SupervizorAgent",
                new Object[] {this});

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

    public void moveCar(AID aid, Point point) {
        Rectangle car = carShapes.get(aid);
        Path path = new Path();
        path.getElements().add(new MoveTo(car.getX() + car.getWidth()/2, car.getY() + car.getHeight()/2));
        path.getElements().add(new LineTo(point.getX(), point.getY()));
        final PathTransition pathTransition = new PathTransition();
        pathTransition.setDuration(Duration.millis(500));
        pathTransition.setPath(path);
        pathTransition.setNode(car);
        pathTransition.setOrientation(PathTransition.OrientationType.ORTHOGONAL_TO_TANGENT);
        pathTransition.setCycleCount(1);
        pathTransition.play();

        //updateCarPosition(car, car.getX() + (double)point.getX(), car.getY());
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
                cars.getChildren().add(carShape);
            }
        });

    }

    public void updateCarPosition(Rectangle car, double x, double y) {
        car.setX(x);
        car.setY(y);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
