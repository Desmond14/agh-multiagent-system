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
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;
import pl.edu.agh.agents.Crossroad;
import pl.edu.agh.agents.Direction;
import pl.edu.agh.agents.Street;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Main extends Application {
    private static final Color LANE_COLOR = Color.BEIGE;
    private static final int MILLIS_PER_MOVE = 40;
    public static final int STAGE_WIDTH = 800;
    public static final int STAGE_HEIGHT = 600;
    private List<TrafficLane> trafficLanes = new ArrayList<TrafficLane>();
    private Map<AID, Rectangle> carShapes = new ConcurrentHashMap<AID, Rectangle>();
    private List<Crossroad> crossroads = new ArrayList<Crossroad>();
    private AgentController supervisor;
    private ContainerController agentContainer;
    private Group cars;
    private Group texts;


    @Override
    public void start(Stage primaryStage) throws Exception {
        Group root = new Group();
        Scene scene = new Scene(root, STAGE_WIDTH, STAGE_HEIGHT, Color.BLACK);
        primaryStage.setScene(scene);
        //TODO: trafficLanes should be loaded from external source, e.g. xml
        trafficLanes.add(new TrafficLane(new Point(0, 275), new Point(800, 325)));
        trafficLanes.add(new TrafficLane(new Point(375, 0), new Point(425, 600)));
        Group lanes = new Group();
        drawLanes(lanes);
        cars = new Group();
        lanes.getChildren().add(cars);
        texts = new Group();

        root.getChildren().add(lanes);
        root.getChildren().add(texts);
        createMenu(root);
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                Runtime rt = Runtime.instance();
                rt.setCloseVM(true);
                rt.shutDown();
                System.exit(0);
            }
        });
        primaryStage.show();
        initializeJadePlatform();
    }

    private void createMenu(Group root) {
        MenuBar menuBar = new MenuBar();
        Menu menu = new Menu("Menu");
        MenuItem restartItem = new MenuItem("Restart");
        restartItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                texts.getChildren().clear();
                cars.getChildren().clear();
                carShapes.clear();
                try {
                    supervisor.putO2AObject(new Object(), true);
                    System.out.println("Restart!");
                    Thread.currentThread().sleep(1000);
                    initializeJadePlatform();
                } catch (StaleProxyException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        menu.getItems().add(restartItem);
        menuBar.getMenus().add(menu);
        root.getChildren().add(menuBar);
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
                cars.getChildren().add(carShape);
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
                texts.getChildren().add(textShape);
            }
        });

    }

    public void assignStreetsToTrafficLanes(List<Street> streets) {
        int i = 0;
        for(TrafficLane trafficLane : trafficLanes) {
            trafficLane.setStreet(streets.get(i++));
        }
    }

    public List<Crossroad> calculateCrossroads() {
        List<Crossroad> crossroads = new ArrayList<>();
        for(TrafficLane trafficLane : trafficLanes) {
            for(TrafficLane otherTrafficLane : trafficLanes) {
                if(trafficLane != otherTrafficLane && trafficLane.getStreet().getNeighboorStreets().contains(otherTrafficLane.getStreet())
                        && !crossroadAlreadyExist(trafficLane.getStreet(), otherTrafficLane.getStreet(), crossroads)) { //jezeli sa sasiednimi ulicami i skrzyzowanie tych ulic nie zostalo znalezione wczesniej
                    Point crossroadUpperLeft;
                    Point crossroadBottomRight;
                    if(trafficLane.getStreet().getDirection() == Direction.VERTICAL) {
                        crossroadUpperLeft = new Point(trafficLane.getUpperLeft().getX(), otherTrafficLane.getUpperLeft().getY());
                        crossroadBottomRight = new Point(trafficLane.getBottomRight().getX(), otherTrafficLane.getBottomRight().getY());
                    }
                    else {
                        crossroadUpperLeft = new Point(otherTrafficLane.getUpperLeft().getX(), trafficLane.getUpperLeft().getY());
                        crossroadBottomRight = new Point(otherTrafficLane.getBottomRight().getX(), trafficLane.getBottomRight().getY());
                    }
                    Crossroad crossroad = new Crossroad(crossroadUpperLeft, crossroadBottomRight, trafficLane.getStreet(), otherTrafficLane.getStreet());
                    crossroads.add(crossroad);
                }
            }
        }
        this.crossroads = crossroads;
        return crossroads;
    }

    public List<Crossroad> getCrossRoadsForGivenStreetNumber(int streetNumber) {
        List<Crossroad> crossroadsWithGivenStreet = new ArrayList<Crossroad>();
        for (Crossroad crossroad : crossroads) {
            if(crossroad.getStreetOne().getStreetNumber() == streetNumber || crossroad.getStreetTwo().getStreetNumber() == streetNumber) {
                crossroadsWithGivenStreet.add(crossroad);
            }
        }
        return crossroadsWithGivenStreet;
    }

    private boolean crossroadAlreadyExist(Street streetOne, Street streetTwo, List<Crossroad> crossroads) {
        for (Crossroad crossroad : crossroads) {
            if((crossroad.getStreetOne() == streetOne && crossroad.getStreetTwo() == streetTwo)
                    || (crossroad.getStreetOne() == streetTwo && crossroad.getStreetTwo() == streetOne)) {
                return true;
            }
        }
        return false;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
