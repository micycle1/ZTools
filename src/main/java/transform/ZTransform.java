package transform;

import igeo.ICurve;
import igeo.IPoint;
import igeo.IVec;
import org.locationtech.jts.geom.*;
import wblut.geom.*;

import java.util.ArrayList;
import java.util.List;

/**
 * transform between different geometries packages
 *
 * @author ZHANG Bai-zhou zhangbz
 * @project shopping_mall
 * @date 2020/10/9
 * @time 17:27
 * most only support simple polygon
 * ** IGeo <-> WB **
 * IPoint <-> WB_Point
 * IPoint -> WB_Point (scale)
 * ICurve -> WB_Geometry WB_Polygon / WB_Polyline / WB_Segment
 * ICurve -> WB_Geometry WB_Polygon / WB_Polyline / WB_Segment, (scale)
 * ICurve -> WB_PolyLine (scale)
 * WB_Coord -> IVec
 * WB_PolyLine -> ICurve
 * ** IGeo <-> jts **
 * IPoint -> Coordinate
 * IPoint -> Point
 * ICurve -> Geometry Polygon / LineString
 * ** WB <-> jts **
 * WB_Coord <-> Point
 * WB_Coord <-> Coordinate
 * WB_Polygon -> Polygon (validate)
 * Polygon -> WB_Polygon
 * LineString -> WB_PolyLine
 * WB_PolyLine -> LineString
 * WB_Segment -> LineString
 * ** WB <-> WB **
 * WB_Polygon -> WB_Polygon (validate)
 * WB_Polygon -> WB_PolyLine
 * WB_AABB -> WB_AABB offset WB_AABB
 * ** jts <-> jts **
 * Polygon -> LineString
 * <p>
 */
public class ZTransform {
    private static final GeometryFactory gf = new GeometryFactory();
    private static final WB_GeometryFactory wbgf = new WB_GeometryFactory();
    private static final double epsilon = 0.00000001;

    /*-------- IGeo <-> WB --------*/

    /**
     * IPoint -> WB_Point
     *
     * @param point input IPoint
     * @return wblut.geom.WB_Point
     */
    public static WB_Point IPointToWB_Point(final IPoint point) {
        return new WB_Point(point.x(), point.y(), point.z());
    }

    /**
     * IPoint -> WB_Point (scale)
     *
     * @param point input IPoint
     * @param scale scale
     * @return wblut.geom.WB_Point
     */
    public static WB_Point IPointToWB_Point(final IPoint point, final double scale) {
        return new WB_Point(point.x(), point.y(), point.z()).scale(scale);
    }

    /**
     * 将WB_Coord转换为IPoint
     *
     * @param point input WB_Point
     * @return igeo.IPoint
     */
    public static IPoint WB_CoordToIPoint(final WB_Coord point) {
        return new IPoint(point.xd(), point.yd(), point.zd());
    }

    /**
     * WB_Coord -> IVec
     *
     * @param point input WB_Point
     * @return igeo.IVec
     */
    public static IVec WB_CoordToIVec(final WB_Coord point) {
        return new IVec(point.xd(), point.yd(), point.zd());
    }

    /**
     * ICurve -> WB_Geometry (WB_PolyLine, WB_Polygon, WB_Segment)
     *
     * @param curve input ICurve
     * @return wblut.geom.WB_Geometry2D
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

    /**
     * ICurve -> WB_Geometry (WB_PolyLine, WB_Polygon, WB_Segment) (scale)
     *
     * @param curve input ICurve
     * @param scale scale
     * @return wblut.geom.WB_Geometry2D
     */
    public static WB_Geometry2D ICurveToWB(final ICurve curve, final double scale) {
        if (curve.cpNum() > 2 && !curve.isClosed()) {
            WB_Point[] points = new WB_Point[curve.cpNum()];
            for (int i = 0; i < curve.cpNum(); i++) {
                points[i] = new WB_Point(curve.cp(i).x(), curve.cp(i).y(), curve.cp(i).z()).scale(scale);
            }
            return wbgf.createPolyLine(points);
        } else if (curve.cpNum() > 2 && curve.isClosed()) {
            WB_Point[] points = new WB_Point[curve.cpNum()];
            for (int i = 0; i < curve.cpNum(); i++) {
                points[i] = new WB_Point(curve.cp(i).x(), curve.cp(i).y(), curve.cp(i).z()).scale(scale);
            }
            return wbgf.createSimplePolygon(points);
        } else if (curve.cpNum() == 2) {
            WB_Point start = new WB_Point(curve.cp(0).x(), curve.cp(0).y(), curve.cp(0).z()).scale(scale);
            WB_Point end = new WB_Point(curve.cp(1).x(), curve.cp(1).y(), curve.cp(1).z()).scale(scale);
            return new WB_Segment(start, end);
        } else {
            System.out.println("***MAYBE OTHER TYPE OF GEOMETRY***");
            return null;
        }
    }

    /**
     * ICurve -> WB_PolyLine
     *
     * @param curve input ICurve
     * @param scale scale ratio
     * @return wblut.geom.WB_PolyLine
     */
    public static WB_PolyLine ICurveToWB_PolyLine(final ICurve curve, final double scale) {
        if (curve.cpNum() >= 2) {
            WB_Point[] points = new WB_Point[curve.cpNum()];
            for (int i = 0; i < curve.cpNum(); i++) {
                points[i] = new WB_Point(curve.cp(i).x(), curve.cp(i).y(), curve.cp(i).z()).scale(scale);
            }
            return wbgf.createPolyLine(points);
        } else {
            System.out.println("***MAYBE OTHER TYPE OF GEOMETRY***");
            return null;
        }
    }

    /**
     * WB_PolyLine -> ICurve
     *
     * @param polyLine input WB_PolyLine
     * @return igeo.ICurve
     */
    public static ICurve WB_PolyLineToICurve(final WB_PolyLine polyLine) {
        IVec[] vecs = new IVec[polyLine.getNumberOfPoints()];
        for (int i = 0; i < polyLine.getNumberOfPoints(); i++) {
            vecs[i] = WB_CoordToIVec(polyLine.getPoint(i));
        }
        return new ICurve(vecs);
    }

    /*-------- IGeo <-> Jts --------*/

    /**
     * IPoint -> Coordinate
     *
     * @param point input IPoint
     * @return org.locationtech.jts.geom.Coordinate
     */
    public static Coordinate IPointToJtsCoordinate(final IPoint point) {
        return new Coordinate(point.x(), point.y(), point.z());
    }

    /**
     * IPoint -> Point
     *
     * @param point input IPoint
     * @return org.locationtech.jts.geom.Point
     */
    public static Point IPointToJtsPoint(final IPoint point) {
        return gf.createPoint(IPointToJtsCoordinate(point));
    }

    /**
     * ICurve -> Geometry (Polygon, LineString)
     *
     * @param curve input ICurve
     * @return org.locationtech.jts.geom.Geometry
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
     * WB_Coord -> Point
     *
     * @param p input WB_Coord
     * @return org.locationtech.jts.geom.Point
     */
    public static Point WB_CoordToPoint(final WB_Coord p) {
        return gf.createPoint(new Coordinate(p.xd(), p.yd(), p.zd()));
    }

    /**
     * Point ->WB_Point
     *
     * @param p input Point
     * @return wblut.geom.WB_Point
     */
    public static WB_Point PointToWB_Point(final Point p) {
        return new WB_Point(p.getX(), p.getY(), 0);
    }

    /**
     * WB_Coord -> Coordinate
     *
     * @param p input WB_Coord
     * @return org.locationtech.jts.geom.Coordinate
     */
    public static Coordinate WB_CoordToCoordinate(final WB_Coord p) {
        return new Coordinate(p.xd(), p.yd(), p.zd());
    }

    /**
     * Coordinate -> WB_Point
     *
     * @param c input Coordinate
     * @return wblut.geom.WB_Point
     */
    public static WB_Point PointToWB_Point(final Coordinate c) {
        double z = c.z;
        if (Double.isNaN(z)) {
            return new WB_Point(c.x, c.y, 0);
        } else {
            return new WB_Point(c.x, c.y, c.z);
        }
    }

    /**
     * WB_Polygon -> Polygon (holes supported)
     *
     * @param wbp input WB_Polygon
     * @return org.locationtech.jts.geom.Polygon
     */
    public static Polygon WB_PolygonToJtsPolygon(final WB_Polygon wbp) {
        if (wbp.getNumberOfHoles() == 0) {
            if (wbp.getPoint(0).equals(wbp.getPoint(wbp.getNumberOfPoints() - 1))) {
                Coordinate[] coords = new Coordinate[wbp.getNumberOfPoints()];
                for (int i = 0; i < wbp.getNumberOfPoints(); i++) {
                    coords[i] = new Coordinate(wbp.getPoint(i).xd(), wbp.getPoint(i).yd(), wbp.getPoint(i).zd());
                }
                return gf.createPolygon(coords);
            } else {
                Coordinate[] coords = new Coordinate[wbp.getNumberOfPoints() + 1];
                for (int i = 0; i < wbp.getNumberOfPoints(); i++) {
                    coords[i] = new Coordinate(wbp.getPoint(i).xd(), wbp.getPoint(i).yd(), wbp.getPoint(i).zd());
                }
                coords[wbp.getNumberOfPoints()] = coords[0];
                return gf.createPolygon(coords);
            }
        } else {
            // exterior
            List<Coordinate> exteriorCoords = new ArrayList<>();
            for (int i = 0; i < wbp.getNumberOfShellPoints(); i++) {
                exteriorCoords.add(new Coordinate(wbp.getPoint(i).xd(), wbp.getPoint(i).yd(), wbp.getPoint(i).zd()));
            }
            if (exteriorCoords.get(0).equals3D(exteriorCoords.get(exteriorCoords.size() - 1))) {
                exteriorCoords.add(exteriorCoords.get(0));
            }
            LinearRing exteriorLinearRing = gf.createLinearRing(exteriorCoords.toArray(new Coordinate[0]));

            // interior
            final int[] npc = wbp.getNumberOfPointsPerContour();
            int index = npc[0];
            LinearRing[] interiorLinearRings = new LinearRing[wbp.getNumberOfHoles()];
            for (int i = 0; i < wbp.getNumberOfHoles(); i++) {
                List<Coordinate> contour = new ArrayList<>();
                for (int j = 0; j < npc[i + 1]; j++) {
                    contour.add(new Coordinate(wbp.getPoint(index).xd(), wbp.getPoint(index).yd(), wbp.getPoint(index).zd()));
                    index++;
                }
                if (!contour.get(0).equals3D(contour.get(contour.size() - 1))) {
                    contour.add(contour.get(0));
                }
                interiorLinearRings[i] = gf.createLinearRing(contour.toArray(new Coordinate[0]));
            }

            return gf.createPolygon(exteriorLinearRing, interiorLinearRings);
        }
    }

    /**
     * Polygon -> WB_Polygon (holes supported)
     *
     * @param p input Polygon
     * @return wblut.geom.WB_Polygon
     */
    public static WB_Polygon jtsPolygonToWB_Polygon(final Polygon p) {
        if (p.getNumInteriorRing() == 0) {
            WB_Coord[] points = new WB_Point[p.getNumPoints()];
            for (int i = 0; i < p.getNumPoints(); i++) {
                points[i] = new WB_Point(p.getCoordinates()[i].x, p.getCoordinates()[i].y, p.getCoordinates()[i].z);
            }
            return new WB_Polygon(points).getSimplePolygon();
        } else {
            // exterior
            WB_Coord[] exteriorPoints = new WB_Point[p.getExteriorRing().getNumPoints()];
            for (int i = 0; i < p.getExteriorRing().getNumPoints(); i++) {
                exteriorPoints[i] = new WB_Point(p.getCoordinates()[i].x, p.getCoordinates()[i].y, p.getCoordinates()[i].z);
            }
            // interior
            int index = p.getExteriorRing().getNumPoints();
            WB_Coord[][] interiorHoles = new WB_Point[p.getNumInteriorRing()][];
            for (int i = 0; i < p.getNumInteriorRing(); i++) {
                LineString curr = p.getInteriorRingN(i);
                WB_Coord[] holePoints = new WB_Point[curr.getNumPoints()];
                for (int j = 0; j < curr.getNumPoints(); j++) {
                    WB_Point point = new WB_Point(curr.getCoordinates()[j].x, curr.getCoordinates()[j].y, curr.getCoordinates()[j].z);
                    holePoints[j] = point;
                }
                interiorHoles[i] = holePoints;
            }
            return new WB_Polygon(exteriorPoints, interiorHoles);
        }
    }

    /**
     * LineString -> WB_PolyLine
     *
     * @param p input LineString
     * @return wblut.geom.WB_PolyLine
     */
    public static WB_PolyLine jtsLineStringToWB_PolyLine(final LineString p) {
        WB_Coord[] points = new WB_Point[p.getNumPoints()];
        for (int i = 0; i < p.getNumPoints(); i++) {
            points[i] = new WB_Point(p.getCoordinates()[i].x, p.getCoordinates()[i].y, p.getCoordinates()[i].z);
        }
        return new WB_PolyLine(points);
    }

    /**
     * WB_PolyLine -> LineString
     *
     * @param wbp input WB_PolyLine
     * @return org.locationtech.jts.geom.LineString
     */
    public static LineString WB_PolyLineToJtsLineString(final WB_PolyLine wbp) {
        Coordinate[] coords = new Coordinate[wbp.getNumberOfPoints()];
        for (int i = 0; i < wbp.getNumberOfPoints(); i++) {
            coords[i] = new Coordinate(wbp.getPoint(i).xd(), wbp.getPoint(i).yd(), wbp.getPoint(i).zd());
        }
        return gf.createLineString(coords);
    }

    /**
     * WB_Segment -> LineString
     *
     * @param seg input WB_Segment
     * @return org.locationtech.jts.geom.LineString
     */
    public static LineString WB_SegmentToJtsLineString(final WB_Segment seg) {
        Coordinate[] coords = new Coordinate[2];
        coords[0] = new Coordinate(seg.getOrigin().xd(), seg.getOrigin().yd(), seg.getOrigin().zd());
        coords[1] = new Coordinate(seg.getEndpoint().xd(), seg.getEndpoint().yd(), seg.getEndpoint().zd());
        return gf.createLineString(coords);
    }

    /*-------- WB <-> WB --------*/

    /**
     * check the start point and the end point of a WB_Polygon
     * validate WB_Polygon (holes supported)
     *
     * @param polygon input WB_Polygon
     * @return wblut.geom.WB_Polygon
     */
    public static WB_Polygon validateWB_Polygon(final WB_Polygon polygon) {
        if (polygon.getNumberOfHoles() == 0) {
            if (polygon.getPoint(0).equals(polygon.getPoint(polygon.getNumberOfPoints() - 1))) {
                return polygon;
            } else {
                List<WB_Coord> points = polygon.getPoints().toList();
                points.add(polygon.getPoint(0));
                return wbgf.createSimplePolygon(points);
            }
        } else {
            boolean flag = true;
            List<WB_Point> exterior = new ArrayList<>();
            for (int i = 0; i < polygon.getNumberOfShellPoints(); i++) {
                exterior.add(polygon.getPoint(i));
            }
            if (!exterior.get(0).equals(exterior.get(exterior.size() - 1))) {
                flag = false;
                exterior.add(exterior.get(0));
            }

            WB_Point[][] interior = new WB_Point[polygon.getNumberOfHoles()][];
            int[] npc = polygon.getNumberOfPointsPerContour();
            int index = npc[0];
            for (int i = 0; i < polygon.getNumberOfHoles(); i++) {
                List<WB_Point> contour = new ArrayList<>();
                for (int j = 0; j < npc[i + 1]; j++) {
                    contour.add(polygon.getPoint(index));
                    index = index + 1;
                }
                if (!contour.get(0).equals(contour.get(contour.size() - 1))) {
                    flag = false;
                    contour.add(contour.get(0));
                }
                interior[i] = contour.toArray(new WB_Point[0]);
            }
            if (flag) {
                return polygon;
            } else {
                return wbgf.createPolygonWithHoles(exterior.toArray(new WB_Point[0]), interior);
            }
        }
    }

    /**
     * WB_Polygon -> WB_PolyLine
     *
     * @param polygon input WB_Polygon
     * @return wblut.geom.WB_PolyLine
     */
    public static WB_PolyLine WB_PolygonToPolyLine(final WB_Polygon polygon) {
        WB_Point[] points = new WB_Point[polygon.getNumberOfPoints()];
        for (int i = 0; i < points.length; i++) {
            points[i] = polygon.getPoint(i);
        }
        return wbgf.createPolyLine(points);
    }

    /**
     * offset WB_AABB
     *
     * @param aabb input WB_AABB
     * @param t    offset scale
     * @return wblut.geom.WB_AABB
     */
    public static WB_AABB offsetAABB(final WB_AABB aabb, final double t) {
        WB_Point min = aabb.getMin();
        WB_Point max = aabb.getMax();
        WB_Point newMin = min.add(min.sub(aabb.getCenter()).scale(t));
        WB_Point newMax = max.add(max.sub(aabb.getCenter()).scale(t));
        return new WB_AABB(newMin, newMax);
    }

    /*-------- jts <-> jts --------*/

    /**
     * Polygon -> LineString
     *
     * @param polygon input Polygon
     * @return java.util.List<org.locationtech.jts.geom.LineString>
     */
    public static List<LineString> PolygonToLineString(final Polygon polygon) {
        List<LineString> result = new ArrayList<>();
        if (polygon.getNumInteriorRing() == 0) {
            result.add(gf.createLineString(polygon.getCoordinates()));
        } else {
            result.add(polygon.getExteriorRing());
            for (int i = 0; i < polygon.getNumInteriorRing(); i++) {
                result.add(polygon.getInteriorRingN(i));
            }
        }
        return result;
    }
}
