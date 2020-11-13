package render;

import org.locationtech.jts.geom.*;
import org.locationtech.jts.triangulate.ConformingDelaunayTriangulationBuilder;
import org.locationtech.jts.triangulate.VoronoiDiagramBuilder;
import processing.core.PApplet;

/**
 * @author ZHANG Bai-zhou zhangbz
 * @project shopping_mall
 * @date 2020/10/10
 * @time 15:13
 * @description draw Jts Geometry
 */
public class JtsRender {
    private static final GeometryFactory gf = new GeometryFactory();
    private final PApplet app;

    public JtsRender(PApplet app) {
        this.app = app;
    }

    public void draw(Geometry geo) {
        String type = geo.getGeometryType();
        switch (type) {
            case "Point":
                drawPoint(geo);
                break;
            case "LineString":
                drawLineString(geo);
                break;
            case "Polygon":
                drawPolygon(geo);
                break;
            default:
                PApplet.println("not a basic geo type");
                break;
        }
    }

    /**
     * @return void
     * @description draw Point as ellipse
     */
    private void drawPoint(Geometry geo) {
        Point point = (Point) geo;
        app.ellipse((float) point.getX(), (float) point.getY(), 10, 10);
    }

    /**
     * @return void
     * @description draw LinearRing as continuous shape
     */
    private void drawLineString(Geometry geo) {
        LineString lr = (LineString) geo;
        Coordinate[] vs = lr.getCoordinates();
        app.beginShape();
        for (Coordinate v : vs) {
            app.vertex((float) v.x, (float) v.y);
        }
        app.endShape(app.CLOSE);
    }

    /**
     * @return void
     * @description draw Polygon as closed shape
     */
    private void drawPolygon(Geometry geo) {
        Polygon poly = (Polygon) geo;
        /*-------- outer boundary --------*/
        app.beginShape();
        LineString shell = poly.getExteriorRing();
        Coordinate[] coord_shell = shell.getCoordinates();
        for (Coordinate c_s : coord_shell) {
            app.vertex((float) c_s.x, (float) c_s.y);
        }
        /*-------- inner holes --------*/
        if (poly.getNumInteriorRing() > 0) {
            int interNum = poly.getNumInteriorRing();
            for (int i = 0; i < interNum; i++) {
                LineString in_poly = poly.getInteriorRingN(i);
                Coordinate[] in_coord = in_poly.getCoordinates();
                app.beginContour();
                for (int j = 0; j < in_coord.length; j++) {
                    app.vertex((float) in_coord[j].x, (float) in_coord[j].y);
                }
                app.endContour();
            }
        }
        app.endShape();
    }

    /**
     * @return void
     * @description draw delaunay triangles
     */
    public void drawDelaunayTriangle(ConformingDelaunayTriangulationBuilder delaunayBuilder) {
        Geometry triangles = delaunayBuilder.getTriangles(JtsRender.gf);
        int num = triangles.getNumGeometries();
        for (int i = 0; i < num; i++) {
            this.draw(triangles.getGeometryN(i));
        }
    }

    /**
     * @return void
     * @description draw voronoi polygons
     */
    public void drawVoronoi(VoronoiDiagramBuilder voronoiBuilder) {
        Geometry voronois = voronoiBuilder.getDiagram(JtsRender.gf);
        int num = voronois.getNumGeometries();
        for (int i = 0; i < num; i++) {
            this.draw(voronois.getGeometryN(i));
        }
    }
}