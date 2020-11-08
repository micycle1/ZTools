package transform;

import igeo.ICurve;
import igeo.IG;
import igeo.IPoint;
import org.locationtech.jts.geom.*;
import wblut.geom.*;

/**
 * @author ZHANG Bai-zhou zhangbz
 * @project shopping_mall
 * @date 2020/10/9
 * @time 17:27
 * @description transform geometry data between IGeo, HE_Mesh and Jts
 */
public class ZTransform {
    private static final GeometryFactory gf = new GeometryFactory();
    private static final WB_GeometryFactory wbgf = new WB_GeometryFactory();

    /*-------- IGeo <-> WB --------*/

    /**
     * @return wblut.geom.WB_Point
     * @description load a single point to WB_Point
     */
    public static WB_Point IPointToWB(final IPoint point) {
        return new WB_Point(point.x(), point.y(), point.z());
    }

    /**
     * @return wblut.geom.WB_Geometry
     * @description load ICurve to WB_Polyline or WB_Polygon or WB_Segment
     */
    public static WB_Geometry2D ICurveToWB(final ICurve curve) {
        if (curve.cpNum() > 2 && !curve.isClosed()) {
            WB_Point[] points = new WB_Point[curve.cpNum()];
            for (int i = 0; i < curve.cpNum(); i++) {
                points[i] = new WB_Point(curve.cp(i).x(), curve.cp(i).y(), curve.cp(i).z());
            }
            return wbgf.createPolyLine(points);
        } else if (curve.cpNum() > 2 && curve.isClosed()) {
            WB_Point[] points = new WB_Point[curve.cpNum()];
            for (int i = 0; i < curve.cpNum(); i++) {
                points[i] = new WB_Point(curve.cp(i).x(), curve.cp(i).y(), curve.cp(i).z());
            }
            return wbgf.createSimplePolygon(points);
        } else if (curve.cpNum() == 2) {
            WB_Point start = new WB_Point(curve.cp(0).x(), curve.cp(0).y(), curve.cp(0).z());
            WB_Point end = new WB_Point(curve.cp(1).x(), curve.cp(1).y(), curve.cp(1).z());
            return new WB_Segment(start, end);
        } else {
            System.out.println("***MAYBE OTHER TYPE OF GEOMETRY***");
            return null;
        }
    }

    /*-------- IGeo <-> Jts --------*/

    /**
     * @return org.locationtech.jts.geom.Coordinate
     * @description load a single point to Jts Coordinate
     */
    public static Coordinate IPointToJtsCoordinate(final IPoint point) {
        return new Coordinate(point.x(), point.y(), point.z());
    }

    /**
     * @return org.locationtech.jts.geom.Coordinate
     * @description load a single point to Jts Point
     */
    public static Point IPointToJtsPoint(final IPoint point) {
        return gf.createPoint(IPointToJtsCoordinate(point));
    }

    /**
     * @return org.locationtech.jts.geom.Geometry
     * @description load ICurve to WB_Polyline or WB_Polygon or WB_Segment
     */
    public static Geometry ICurveToJts(final ICurve curve) {
        if (curve.cpNum() > 2 && curve.isClosed()) {
            Coordinate[] curvePts = new Coordinate[curve.cpNum()];
            for (int i = 0; i < curve.cpNum(); i++) {
                curvePts[i] = new Coordinate(curve.cp(i).x(), curve.cp(i).y(), curve.cp(i).z());
            }
            return gf.createPolygon(curvePts);
        } else if (curve.cpNum() > 2 && !curve.isClosed()) {
            Coordinate[] curvePts = new Coordinate[curve.cpNum()];
            for (int i = 0; i < curve.cpNum(); i++) {
                curvePts[i] = new Coordinate(curve.cp(i).x(), curve.cp(i).y(), curve.cp(i).z());
            }
            return gf.createLineString(curvePts);
        } else if (curve.cpNum() == 2) {
            Coordinate[] curvePts = new Coordinate[curve.cpNum()];
            for (int i = 0; i < curve.cpNum(); i++) {
                curvePts[i] = new Coordinate(curve.cp(i).x(), curve.cp(i).y(), curve.cp(i).z());
            }
            return gf.createLineString(curvePts);
        } else {
            System.out.println("***MAYBE OTHER TYPE OF GEOMETRY***");
            return null;
        }
    }

    /*-------- WB <-> Jts --------*/

    /**
     * @return com.vividsolutions.jts.geom.Polygon
     * @description transform WB_Polygon to jts Polygon
     */
    public static Polygon WB_PolygonToJtsPolygon(final WB_Polygon wbp) {
        Coordinate[] coords = new Coordinate[wbp.getNumberOfPoints()];
        for (int i = 0; i < wbp.getNumberOfPoints(); i++) {
            coords[i] = new Coordinate(wbp.getPoint(i).xd(), wbp.getPoint(i).yd(), wbp.getPoint(i).zd());
        }
        return gf.createPolygon(coords);
    }

    /**
     * @return wblut.geom.WB_Polygon
     * @description transform jts Polygon to WB_Polygon
     */
    public static WB_Polygon JtsPolygonToWB_Polygon(final Polygon p) {
        WB_Coord[] points = new WB_Point[p.getNumPoints()];
        for (int i = 0; i < p.getNumPoints(); i++) {
            points[i] = new WB_Point(p.getCoordinates()[i].x, p.getCoordinates()[i].y, p.getCoordinates()[i].z);
        }
        return new WB_Polygon(points).getSimplePolygon();
    }

    /**
     * @return org.locationtech.jts.geom.LineString
     * @description transform WB_Polygon to jts LineString
     */
    public static LineString WB_PolygonToJtsLineString(final WB_Polygon wbp) {
        Coordinate[] coords = new Coordinate[wbp.getNumberOfPoints()];
        for (int i = 0; i < wbp.getNumberOfPoints(); i++) {
            coords[i] = new Coordinate(wbp.getPoint(i).xd(), wbp.getPoint(i).yd(), wbp.getPoint(i).zd());
        }
        return gf.createLineString(coords);
    }

    /**
     * @return org.locationtech.jts.geom.LineSegment
     * @description transform WB_Segment to Jts LineString
     */
    public static LineString WB_SegmentToJtsLineString(final WB_Segment seg) {
        Coordinate[] coords = new Coordinate[2];
        coords[0] = new Coordinate(seg.getOrigin().xd(), seg.getOrigin().yd(), seg.getOrigin().zd());
        coords[1] = new Coordinate(seg.getEndpoint().xd(), seg.getEndpoint().yd(), seg.getEndpoint().zd());
        return gf.createLineString(coords);
    }

    /*-------- WB <-> WB --------*/

    /**
     * @return wblut.geom.WB_PolyLine
     * @description transform WB_Polygon to WB_PolyLine
     */
    public static WB_PolyLine WB_PolygonToPolyLine(final WB_Polygon polygon) {
        WB_Point[] points = new WB_Point[polygon.getNumberOfPoints()];
        for (int i = 0; i < points.length; i++) {
            points[i] = polygon.getPoint(i);
        }
        return wbgf.createPolyLine(points);
    }

    /**
     * @return wblut.geom.WB_AABB
     * @description 给 WB_AABB offset
     */
    public static WB_AABB offsetAABB(final WB_AABB aabb, final double t) {
        WB_Point min = aabb.getMin();
        WB_Point max = aabb.getMax();
        WB_Point newMin = min.add(min.sub(aabb.getCenter()).scale(t));
        WB_Point newMax = max.add(max.sub(aabb.getCenter()).scale(t));
        return new WB_AABB(newMin, newMax);
    }
}
