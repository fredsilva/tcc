package br.com.uft.scicumulus.graph;

import br.com.uft.scicumulus.enums.ResizeZone;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.scene.shape.Rectangle;

/**
 *
 * @author Thaylon Guede Santos
 * @email thaylon_guedes@hotmail.com
 */
public class EnableResizeAndDrag {

    ResizeZone resizeZone;
    private final int RESIZE_MARGIN = 6;
    private double initX;
    private double initY;
    private Point2D dragAnchor;
    private Node node;
    private boolean dragging;
    double initLayoutX;
    double initLayoutY;

    public EnableResizeAndDrag(Node node) {
        this.node = node;
    }

    public static void make(Node node) {
        EnableResizeAndDrag resizeAndDrag = new EnableResizeAndDrag(node);
        resizeAndDrag.init();
    }

    private void init() {
        node.addEventHandler(MouseEvent.MOUSE_MOVED, (MouseEvent me) -> {
            //resizeZone = ResizeZone.getResizeZone(node, RESIZE_MARGIN, me);
            if (resizeZone != null) {
                node.setCursor(resizeZone.getMouseCursor());
            } else {
                node.setCursor(Cursor.HAND);
            }
        });
        node.addEventHandler(MouseEvent.MOUSE_DRAGGED, (MouseEvent me) -> {
            dragging = true;
            if (resizeZone == null) {
                moverNode(me);                
            } else {
                resizeNode(me);
            }
            me.consume();
        });

        node.addEventHandler(MouseEvent.MOUSE_ENTERED, (MouseEvent me) -> {
            node.toFront();
            if (!dragging) {
                node.setCursor(Cursor.HAND);
            }
        });

        node.addEventHandler(MouseEvent.MOUSE_PRESSED, (MouseEvent me) -> {
            //System.out.println(node.getId());
            dragAnchor = new Point2D(me.getSceneX(), me.getSceneY());
            if (resizeZone != null) {
                initX = node.getBoundsInParent().getWidth();
                initY = node.getBoundsInParent().getHeight();
                initLayoutX = node.getLayoutX();
                initLayoutY = node.getLayoutY();
            } else {
                initX = node.getLayoutX();
                initY = node.getLayoutY();
            }
            me.consume();
        });

        node.addEventHandler(MouseEvent.MOUSE_RELEASED, (MouseEvent me) -> {
            dragging = false;
            node.setCursor(Cursor.HAND);
        });
    }

    private void resizeNode(MouseEvent me) {
        switch (resizeZone) {
            case E:
                resizeX(me, true);
                break;
            case N:
                resizeY(me, false);
                break;
            case S:
                resizeY(me, true);
                break;
            case W:
                resizeX(me, false);
                break;
            case NE:
                resizeX(me, true);
                resizeY(me, false);
                break;
            case NW:
                resizeX(me, false);
                resizeY(me, false);
                break;
            case SW:
                resizeX(me, false);
                resizeY(me, true);
                break;
            case SE:
                resizeX(me, true);
                resizeY(me, true);
                break;
        }
    }

    private void moverNode(MouseEvent me) {
        node.setCursor(Cursor.MOVE);
        double dragX = me.getSceneX() - dragAnchor.getX();
        double dragY = me.getSceneY() - dragAnchor.getY();
        double newXPosition = initX + dragX;
        double newYPosition = initY + dragY;
        node.setLayoutX(newXPosition);
        node.setLayoutY(newYPosition);
    }

    private void resizeX(MouseEvent me, boolean mantemLayout) {
        double espacoMovido = me.getSceneX() - dragAnchor.getX();
        if (!mantemLayout) {
            espacoMovido = espacoMovido * -1;
        }
        double newWidth = initX + espacoMovido;
        setNewSize(newWidth, espacoMovido, mantemLayout, true);
    }

    private void resizeY(MouseEvent me, boolean mantemLayout) {
        double espacoMovido = me.getSceneY() - dragAnchor.getY();
        if (!mantemLayout) {
            espacoMovido = espacoMovido * -1;
        }
        double newHeight = initY + espacoMovido;
        setNewSize(newHeight, espacoMovido, mantemLayout, false);
    }

    private void setNewSize(double newValue, double espacoMovido, boolean manterLayout, boolean isX) {
        if (node instanceof Region) {
            Region region = (Region) node;
            if (isX) {
                region.setPrefWidth(newValue);
            } else {
                region.setPrefHeight(newValue);
            }
        } else if (node instanceof Rectangle) {
            Rectangle rectangle = (Rectangle) node;
            if (isX) {
                rectangle.setWidth(newValue);
            } else {
                rectangle.setHeight(newValue);
            }
        } else {
            System.err.println("Objeto não tem resize conhecido: " + node);
        }
        if (!manterLayout) {
            if (isX) {
                node.setLayoutX(initLayoutX - espacoMovido);
            } else {
                node.setLayoutY(initLayoutY - espacoMovido);
            }
        }
    }

}
