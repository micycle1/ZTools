package demoTest;

import advancedGeometry.largestRectangle.ZLargestRectangle;
import guo_cam.CameraController;
import igeo.ICurve;
import igeo.IG;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Polygon;
import processing.core.PApplet;
import render.JtsRender;
import transform.ZTransform;
import wblut.geom.WB_Polygon;
import wblut.processing.WB_Render;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * test ZLargestRectangle
 *
 * @author zhangbz ZHANG Baizhou
 * @project shopping_mall
 * @date 2021/9/7
 * @time 17:48
 */
public class Test5LargestRect extends PApplet {

    /* ------------- settings ------------- */

    public void settings() {
        size(1000, 1000, P3D);
    }

    /* ------------- setup ------------- */

    private List<Geometry> polys;
    private List<WB_Polygon> largestRectangles;

    private JtsRender jtsRender;
    private WB_Render render;
    private CameraController gcam;

    public void setup() {
        this.gcam = new CameraController(this);
        this.jtsRender = new JtsRender(this);
        this.render = new WB_Render(this);

//        String path = this.getClass().getResource("/test_largest_rect.3dm").getPath();
//        String path = "E:\\AAA_Study\\0_JavaTools\\Ztools\\target\\test-classes\\test_largest_rect.3dm";
//        String path = "E:\\AAA_Study\\0_JavaTools\\Ztools\\src\\test\\resources\\test_largest_rect.3dm";
        String path = Objects.requireNonNull(
                this.getClass().getClassLoader().getResource("./test_largest_rect.3dm")
        ).getPath();

        IG.init();
        IG.open(path);
        this.polys = new ArrayList<>();

        ICurve[] polyLines = IG.layer("test").curves();
        for (ICurve polyLine : polyLines) {
            polys.add(ZTransform.ICurveToJts(polyLine));
        }

        // largest rectangle
        this.largestRectangles = new ArrayList<>();
        for (Geometry g : polys) {
            if (g instanceof Polygon) {
                ZLargestRectangle rectangle = new ZLargestRectangle(ZTransform.PolygonToWB_Polygon((Polygon) g));
                rectangle.init();
                largestRectangles.add(rectangle.getRectangleResult());
            }
        }
    }

    /* ------------- draw ------------- */

    public void draw() {
        background(255);
        strokeWeight(1);
        stroke(0);
        for (Geometry g : polys) {
            jtsRender.drawGeometry(g);
        }

        // rectangle
        strokeWeight(3);
        stroke(255, 0, 0);
        for (WB_Polygon rect : largestRectangles) {
            render.drawPolygonEdges2D(rect);
        }
    }

}
