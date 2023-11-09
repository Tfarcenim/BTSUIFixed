package io.karma.bts.client.screen.shapes;

import org.joml.Vector2i;

public class Rectangle implements Shape{

    public final Vector2i point1;
    public final Vector2i point2;

    public Rectangle(Vector2i point1, Vector2i point2) {
        this.point1 = point1;
        this.point2 = point2;
    }

    @Override
    public boolean contains(int mouseX, int mouseY) {
        return mouseX >= this.point1.x && mouseY >= this.point1.y && mouseX < point2.x && mouseY < point2.y;
    }
}
