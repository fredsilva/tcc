package br.com.uft.scicumulus.enums;

import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;

/**
 *
 * @author Frederico da Silva Santos, Thaylon Guede Santos
 */
public enum ResizeZone {
    /*
     ____________________
     | 1 |   1-2    | 2 |
     --------------------
     | 1 |          | 2 |
     | - |   Area   | - |
     | 3 |  Neutra  | 4 |
     |___|__________|___|
     | 3 |   3-4    | 4 |
     --------------------
     */

    NW("1", (Node node, double resize_margin, MouseEvent me) -> {
        return me.getX() <= resize_margin && me.getY() <= resize_margin;
    }, Cursor.NW_RESIZE),
    NE("2", (Node node, double resize_margin, MouseEvent me) -> {
        return node.getBoundsInParent().getWidth() - resize_margin <= me.getX() && me.getY() <= resize_margin;
    }, Cursor.NE_RESIZE),
    SW("3", (Node node, double resize_margin, MouseEvent me) -> {
        return me.getX() <= resize_margin && node.getBoundsInLocal().getHeight() - resize_margin <= me.getY();
    }, Cursor.SW_RESIZE),
    SE("4", (Node node, double resize_margin, MouseEvent me) -> {
        return node.getBoundsInLocal().getWidth() - resize_margin <= me.getX() && node.getBoundsInLocal().getHeight() - resize_margin <= me.getY();
    }, Cursor.SE_RESIZE),
    N("1-2", (Node node, double resize_margin, MouseEvent me) -> {
        return me.getX() >= resize_margin && me.getX() <= node.getBoundsInParent().getWidth() - 5 && me.getY() <= resize_margin;
    }, Cursor.N_RESIZE),
    S("3-4", (Node node, double resize_margin, MouseEvent me) -> {
        return me.getX() >= resize_margin && me.getX() <= node.getBoundsInParent().getWidth() - 5 && me.getY() >= node.getBoundsInParent().getHeight() - resize_margin;
    }, Cursor.S_RESIZE),
    W("1-3", (Node node, double resize_margin, MouseEvent me) -> {
        return me.getX() <= resize_margin && me.getY() >= resize_margin && me.getY() <= node.getBoundsInParent().getHeight() - resize_margin;
    }, Cursor.W_RESIZE),
    E("2-4", (Node node, double resize_margin, MouseEvent me) -> {
        return me.getX() >= node.getBoundsInParent().getWidth() - resize_margin && me.getY() >= resize_margin && me.getY() <= node.getBoundsInParent().getHeight() - resize_margin;
    }, Cursor.E_RESIZE);
    private String zone;
    private valideArea valideZone;
    private Cursor mouseCursor;

    public static ResizeZone getResizeZone(Node node, double resize_margin, MouseEvent me) {
        for (ResizeZone zone : values()) {
            if (zone.valideZone.valide(node, resize_margin, me)) {
                return zone;
            }
        }
        return null;
    }

    private ResizeZone(String s, valideArea valideZone, Cursor cursor) {
        this.zone = s;
        this.valideZone = valideZone;
        this.mouseCursor = cursor;
    }

    public Cursor getMouseCursor() {
        return mouseCursor;
    }

    public String getZone() {
        return zone;
    }

    @Override
    public String toString() {
        return "ResizeZone{" + "Zona =" + zone + ", mouseCursor=" + mouseCursor + '}';
    }

    private interface valideArea {

        public boolean valide(Node node, double resize_margin, MouseEvent me);
    }
}
