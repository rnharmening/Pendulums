package DoublePendulum;

import javafx.animation.*;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.util.Duration;

public class DoublePendulum extends Group{

    private DoubleProperty length1 = new SimpleDoubleProperty(180),
                           length2 = new SimpleDoubleProperty(150);
    private DoubleProperty theta1 = new SimpleDoubleProperty((Math.random()* 2 * Math.PI)),
                           theta2 = new SimpleDoubleProperty((Math.random()* 2 * Math.PI));
    private DoubleProperty mass1 = new SimpleDoubleProperty(50),
                           mass2 = new SimpleDoubleProperty(12);
    private double theta1_v = .01, theta2_v = .02;

    private DoubleProperty gravity = new SimpleDoubleProperty(0.0);

    private DoubleProperty duration = new SimpleDoubleProperty(1/240.0);

    private double old_x, old_y;
    private int color = 0;

    private Circle start, p1, p2;
    private Line l1, l2;
    private Group path = new Group();

    private boolean isSimulationRunning = false;

    public DoublePendulum(int transX, int transY) {
        this.setTranslateX(transX);
        this.setTranslateY(transY);
        start = new Circle(0,0,5, Color.BLACK);
        p1 = new Circle(0,0, 1, Color.BLACK);
        p1.radiusProperty().bind(sqrtBinding(mass1).multiply(2));
        p2 = new Circle(0,0, 1, Color.BLACK);
        p2.radiusProperty().bind(sqrtBinding(mass2).multiply(2));
        l1 = new Line();
        l2 = new Line();

        setUpPendulumBinding();

        this.getChildren().addAll(path, l1, l2, start, p1, p2);
    }

    private void setUpPendulumBinding(){
        bindLineToCircle(l1, start, p1);
        bindLineToCircle(l2, p1, p2);

        p1.centerXProperty().bind(Bindings.createDoubleBinding(
                () -> length1.getValue()*Math.sin(theta1.getValue()),
                length1, theta1));

        p2.centerXProperty().bind(Bindings.createDoubleBinding(
                () -> p1.centerXProperty().getValue() + length2.getValue()*Math.sin(theta2.getValue()),
                p1.centerXProperty(), length2, theta2));

        p1.centerYProperty().bind(Bindings.createDoubleBinding(
                () -> length1.getValue()*Math.cos(theta1.getValue()),
                length1, theta1));

        p2.centerYProperty().bind(Bindings.createDoubleBinding(
                () -> p1.centerYProperty().getValue() + length2.getValue()*Math.cos(theta2.getValue()),
                p1.centerYProperty(), length2, theta2));
    }

    private void bindLineToCircle(Line line, Circle c1, Circle c2){
        line.startXProperty().bind(c1.centerXProperty());
        line.startYProperty().bind(c1.centerYProperty());
        line.endXProperty().bind(c2.centerXProperty());
        line.endYProperty().bind(c2.centerYProperty());
    }

    public void simulatePendulum(boolean running){
        isSimulationRunning = running;

        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(duration.get()), ev -> {
            old_x = p2.getCenterX();
            old_y = p2.getCenterY();
            double theta1_a = calculateTheta1Acceleration();
            double theta2_a = calculateTheta2Acceleration();

            theta1_v += theta1_a;
            theta2_v += theta2_a;

            //theta1_v *= 0.99995;
            //theta2_v *= 0.99995;

            theta1.set(theta1.getValue() + theta1_v);
            theta2.set(theta2.getValue() + theta2_v);

            Color currentColor = getRainbowColor(color++, duration.getValue()/10);

            p2.setFill(currentColor);

            Line line = new Line(old_x, old_y, p2.getCenterX(), p2.getCenterY());
            line.setStrokeWidth(1.5);
            line.setStroke(currentColor);
            path.getChildren().add(line);
            if(path.getChildren().size() > 10000){
                path.getChildren().remove(0);
            }
            //System.out.println(theta1);
        }));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    private double calculateTheta1Acceleration() {
        double g = gravity.get(),
                m1 = mass1.get(), m2 = mass2.get(),
                a1 = theta1.get(), a2 = theta2.get(),
                v1 = theta1_v, v2 = theta2_v,
                l1 = length1.get(), l2 = length2.get();

        double n1 = -g*(2*m1+m2) * Math.sin(a1),
                n2 = m2*g*Math.sin(a1-2*a2),
                n3 = 2*Math.sin(a1-a2)*m2,
                n4 = v2*v2*l2 + v1*v1*l1*Math.cos(a1-a2),
                d  = l1*(2*m1 + m2 - m2 * Math.cos(2*a1-2*a2));

        return (n1-n2-n3*n4)/d;
    }

    private double calculateTheta2Acceleration() {
        double g = gravity.get(),
                m1 = mass1.get(), m2 = mass2.get(),
                a1 = theta1.get(), a2 = theta2.get(),
                v1 = theta1_v, v2 = theta2_v,
                l1 = length1.get(), l2 = length2.get();

        double n1 = 2*Math.sin(a1-a2),
               n2 = v1*v1*l1*(m1+m2),
               n3 = g*(m1+m2)*Math.cos(a1),
               n4 = v2*v2*l2*m2*Math.cos(a1-a2),
                d  = l2*(2*m1 + m2 - m2 * Math.cos(2*a1-2*a2));

        return (n1*(n2+n3+n4))/d;
    }


    private Color getRainbowColor(int n, double frequency){
        int red   = (int) (Math.sin(frequency*n + 0) * 127 + 128);
        int green = (int) (Math.sin(frequency*n +  2*Math.PI/3) * 127 + 128);
        int blue  = (int) (Math.sin(frequency*n +  4*Math.PI/3) * 127 + 128);
        return Color.color(red/255.0, green/255.0, blue/255.0);
    }

    private DoubleBinding sqrtBinding(DoubleProperty dp) {
        return Bindings.createDoubleBinding(() -> Math.sqrt(dp.getValue()), dp);
    }
}
