package com.kadme.test;

import java.util.*;

public class OutlineBuilderImpl implements OutlineBuilder {

    @Override
    public Polygon buildOutline(Set<Line> lines) {
        Set<Point> pointHashSet = new HashSet<Point>();
        Point p = null;
        Line[] lineArr = lines.stream().toArray(Line[]::new);


        for(int i = 0; i<lineArr.length; i++){
            for(int j = i+1; j < lineArr.length; j++){
                p = getIntersectPoint(lineArr[i], lineArr[j]);
                if(p!=null) pointHashSet.add(p);
            }
        }
        List<Point> pointList= new ArrayList<Point>();
        for(Line line : lines){
            pointList.add( line.getP1());
            pointList.add( line.getP2());
        }
        Point[] point = pointHashSet.stream().toArray(Point[]::new);
        Set<Point> intHullPoint= convexHull(point,point.length);
        Point[] outPolyPoint = pointList.stream().toArray(Point[]::new);
        Set<Point> outHullPoint= convexHull(outPolyPoint,outPolyPoint.length);
        intHullPoint.addAll(outHullPoint);
        intHullPoint.stream().forEach(d -> System.out.println(d.getX()+":::::::" +d.getY()));
        return new Polygon(new ArrayList<Point>(intHullPoint));
    }

    public Point getIntersectPoint(Line line1, Line line2){
        double x1= line1.getP1().getX();
        double x2= line1.getP2().getX();
        double y1= line1.getP1().getY();
        double y2= line1.getP2().getY();
        double x3= line2.getP1().getX();
        double x4= line2.getP2().getX();
        double y3= line2.getP1().getY();
        double y4= line2.getP2().getY();
        double din = ((x1 - x2)*(y3 - y4) - (y1 - y2)*(x3 - x4));
        //if Denominator is Zero line never intersect and thus are parallel
        if(din == 0)
            return null;
        //x, y are Intersection point
        double x = ((x2 - x1)*(x3*y4 - x4*y3) - (x4 - x3)*(x1*y2 - x2*y1)) / din;
        double y = ((y3 - y4)*(x1*y2 - x2*y1) - (y1 - y2)*(x3*y4 - x4*y3)) / din;

        // Code to check if intersection point are not on line and thus potentially not intersect the Line
        double a[] = {x1,x2,x3,x4};
        double b[] = {y1,y2,y3,y4};
        double maxX = Arrays.stream(a).max().getAsDouble();
        double minX = Arrays.stream(a).min().getAsDouble();
        double maxY = Arrays.stream(b).max().getAsDouble();
        double minY = Arrays.stream(b).min().getAsDouble();
        if((x<=minX || x >= maxX) || (y<=minY || y >= maxY))
            return null;
        else
            return new Point(x,y);
    }

    public double orientation(Point p, Point q, Point r) {
        double val = (q.getY() - p.getY()) * (r.getX() - q.getX()) - (q.getX() - p.getX()) * (r.getY() - q.getY());
        if (val == 0) return 0;
        return (val > 0)? 1: 2;
    }

    public Set<Point> convexHull(Point points[], int n) {
        if (n < 3) return new HashSet<>();
        Set<Point> hull = new HashSet<>();
        int l = 0;
        for (int i = 1; i < n; i++)
            if (points[i].getX() < points[l].getX())
                l = i;
        int p = l, q;
        do
        {
            hull.add(points[p]);
            q = (p + 1) % n;
            for (int i = 0; i < n; i++)
            {
                if (orientation(points[p], points[i], points[q]) == 2)
                    q = i;
            }
            p = q;
        } while (p != l);
        return hull;
    }
}
