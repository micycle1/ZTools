package subdivision;

import geometry.ZGeoFactory;
import geometry.ZLine;
import geometry.ZSkeleton;
import math.ZGeoMath;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Polygon;
import processing.core.PApplet;
import render.JtsRender;
import transform.ZTransform;
import wblut.geom.*;
import wblut.hemesh.*;
import wblut.processing.WB_Render;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * double-strip subdivision
 *
 * @author ZHANG Bai-zhou zhangbz
 * @project shopping_mall
 * @date 2020/12/7
 * @time 14:25
 */
public class ZSD_DoubleStrip implements ZSubdivision {
    private final WB_Polygon originPolygon;
    private List<WB_Polygon> allSubPolygons;

    private List<List<Integer>> logicalStreets;
    private ZSkeleton skeleton;
    private HE_Mesh skeletonMesh;

    private List<WB_Polygon> unionByStreet;
    HE_Mesh mesh;

    public ZSD_DoubleStrip(WB_Polygon originPolygon) {
        this.originPolygon = originPolygon;

        this.logicalStreets = new ArrayList<>();
        logicalStreets.add(Arrays.asList(0));
        logicalStreets.add(Arrays.asList(1, 2));
        logicalStreets.add(Arrays.asList(3, 4, 5));
        logicalStreets.add(Arrays.asList(6, 7));

        performDivide();
    }

    @Override
    public void performDivide() {
        this.skeleton = new ZSkeleton(originPolygon);
        this.skeletonMesh = skeleton.getSkeletonMesh();

        List<WB_Polygon> allFacePolys = skeleton.getAllFacePolys(); // origin skeleton faces

        // union polygons by logical streets
        this.unionByStreet = new ArrayList<>();
        for (List<Integer> logicalStreet : logicalStreets) {
            if (logicalStreet.size() > 1) {
                List<WB_Polygon> strip = new ArrayList<>();
                for (Integer index : logicalStreet) {
                    strip = ZGeoFactory.wbgf.unionPolygons2D(allFacePolys.get(index), strip);
                }
                // check if union polygons is same direction with origin (usually not)
                for (WB_Polygon p : strip) {
                    if (ZGeoMath.isNormalEquals(p, allFacePolys.get(logicalStreet.get(0)))) {
                        unionByStreet.add(p);
                    } else {
                        unionByStreet.add(ZGeoMath.reversePolygon(p));
                    }
                }
            } else {
                unionByStreet.add(allFacePolys.get(logicalStreet.get(0)));
            }
        }
        this.mesh = new HEC_FromPolygons(unionByStreet).create();


    }

    @Override
    public WB_Polygon getOriginPolygon() {
        return originPolygon;
    }

    @Override
    public List<WB_Polygon> getAllSubPolygons() {
        return allSubPolygons;
    }

    @Override
    public HE_Mesh getMesh() {
        return new HEC_FromPolygons(allSubPolygons).create();
    }

    @Override
    public void display(PApplet app, WB_Render render, JtsRender jtsRender) {
        app.fill(200);
        for (WB_Polygon p : unionByStreet) {
            render.drawPolygonEdges2D(p);
        }

        app.fill(0);
        app.textSize(15);
        for (int j = 0; j < skeleton.getAllFacePolys().size(); j++) {
            app.text(j,
                    skeleton.getAllFacePolys().get(j).getCenter().xf(),
                    skeleton.getAllFacePolys().get(j).getCenter().yf(),
                    skeleton.getAllFacePolys().get(j).getCenter().zf()
            );
        }

        for (int i = 0; i < mesh.getAllBoundaryVertices().size(); i++) {
            app.ellipse(mesh.getAllBoundaryVertices().get(i).xf(),
                    mesh.getAllBoundaryVertices().get(i).yf(),
                    10, 10);
        }
        app.pushStyle();
        app.strokeWeight(3);
        List<HE_Halfedge> edgeList = new ArrayList<>();
        for (int i = 0; i < mesh.getNumberOfFaces() - 1; i++) {
            for (int j = i + 1; j < mesh.getNumberOfFaces(); j++) {
                if (mesh.getFaceWithIndex(i).isNeighbor(mesh.getFaceWithIndex(j))) {

                }
            }
        }
        System.out.println("edgeList: " + edgeList.size());

        for (HE_Halfedge e : mesh.getUnpairedHalfedges()) {
            render.drawEdge(e);
        }
        for (HE_Halfedge e : mesh.getFaces().get(0).getFaceHalfedges()) {
            render.drawEdge(e);
            WB_Point center = (WB_Point) e.getCenter();
            WB_Vector dir = (WB_Vector) e.getEdgeDirection();
            WB_Point arrow = center.add((dir.rotateAboutOrigin2D(Math.PI * 0.75)).scale(15));
            app.line(center.xf(), center.yf(), arrow.xf(), arrow.yf());
        }

        app.popStyle();

        for (int i = 0; i < mesh.getFaces().size(); i++) {
            app.text("face" + i,
                    mesh.getFaceWithIndex(i).getFaceCenter().xf(),
                    mesh.getFaceWithIndex(i).getFaceCenter().yf(),
                    mesh.getFaceWithIndex(i).getFaceCenter().zf()
            );
        }

    }
}