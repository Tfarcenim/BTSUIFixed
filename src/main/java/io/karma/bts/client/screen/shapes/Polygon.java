package io.karma.bts.client.screen.shapes;

import io.karma.bts.repackage.joml.Vector2i;

import java.util.ArrayList;
import java.util.List;

public class Polygon implements Shape{

    private final List<Vector2i> points = new ArrayList<>();
    private Rectangle enclosing;

    public void addPoint(Vector2i vector2i) {
        points.add(vector2i);
        if (points.size() > 1) {
            int minX = points.get(0).x;
            int minY = points.get(0).y;

            int maxX = points.get(0).x;
            int maxY = points.get(0).y;

            for (Vector2i point : points) {
                if (point.x < minX) {
                    minX = point.x;
                }
                if (point.y < minY) {
                    minY = point.y;
                }

                if (point.x > maxX) {
                    maxX = point.x;
                }
                if (point.y > maxY) {
                    maxY = point.y;
                }
            }
            enclosing = new Rectangle(new Vector2i(minX,minY),new Vector2i(maxX,maxY));
        }
    }

    public void addPoint(int x , int y) {
        addPoint(new Vector2i(x,y));
    }

    public static Polygon makeSimpleDiamond(int width, int height, Vector2i center)  {
        Polygon polygon = new Polygon();
        polygon.addPoint(center.x,center.y + height/2);//north
        polygon.addPoint(center.x+width/2,center.y);//east
        polygon.addPoint(center.x,center.y - height/2);//south
        polygon.addPoint(center.x-width/2,center.y);//west
        return polygon;
    }


    public boolean contains(int mouseX, int mouseY) {

        if (points.size() < 3 || !enclosing.contains(mouseX,mouseY)) return false;

        int hits = 0;

        Vector2i lastPoint = points.get(points.size()-1);

        int lastx = lastPoint.x;
        int lasty = lastPoint.y;
        int curx, cury;

        // Walk the edges of the polygon
        for (int i = 0; i < points.size(); lastx = curx, lasty = cury, i++) {
            Vector2i vector2i = points.get(i);
            curx = vector2i.x;
            cury = vector2i.y;

            if (cury == lasty) {
                continue;
            }

            int leftx;
            if (curx < lastx) {
                if (mouseX >= lastx) {
                    continue;
                }
                leftx = curx;
            } else {
                if (mouseX >= curx) {
                    continue;
                }
                leftx = lastx;
            }

            double test1, test2;
            if (cury < lasty) {
                if (mouseY < cury || mouseY >= lasty) {
                    continue;
                }
                if (mouseX < leftx) {
                    hits++;
                    continue;
                }
                test1 = mouseX - curx;
                test2 = mouseY - cury;
            } else {
                if (mouseY < lasty || mouseY >= cury) {
                    continue;
                }
                if (mouseX < leftx) {
                    hits++;
                    continue;
                }
                test1 = mouseX - lastx;
                test2 = mouseY - lasty;
            }

            if (test1 < (test2 / (lasty - cury) * (lastx - curx))) {
                hits++;
            }
        }

        return ((hits & 1) != 0);
    }

    public Rectangle getEnclosing() {
        return enclosing;
    }

    public List<Vector2i> getPoints() {
        return points;
    }
}
