/*
 * Author: Tyler Arseneault
 * Student ID: 5032106
 * Due Date: April 25, 2015
 * 
 * CS3010 Final Project
 * CarrotCapture
 */
package carrotcapture;


import java.io.File;
import java.io.IOException;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ObservableSet;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.print.PageLayout;
import javafx.print.PageOrientation;
import javafx.print.Paper;
import javafx.print.PrintColor;
import javafx.print.Printer;
import javafx.print.PrinterAttributes;
import javafx.print.PrinterJob;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.*;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextBoundsType;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javax.imageio.ImageIO;



interface Drawable {
    void setFillColor(Color c);
    void setStrokeColor(Color c); 
    void setStrokeWidth(double width);
}

class MyShape extends StackPane implements Drawable{
    public static final int CIRCLE = 0;
    public static final int RECTANGLE = 1;
    public static final int TRIANGLE = 2;
    public static final int OVAL = 3;
    public static final int ROUNDRECT = 5;
    public static final int TEXT = 6;
    public static final int IMAGE = 7;
    private final SimpleBooleanProperty selected;
    private final Shape shape;
    private static Paint defaultFillPaint = MenuInterface.getColourFill();
    private static Paint defaultStrokePaint = MenuInterface.getColourStroke();
    private static Paint defaultSelectedPaint = null;
    private static double defaultStrokeWidth = 0;
    private static int defaultShapeType = CIRCLE;
    private static double defaultWidth = 50;
    private static double defaultHeight = 50;
    private static String defaultText = "The Text";
    private static String defaultFontName = "Times New Roman";
    private static double defaultFontSize = 15;
    private int shapeType;

    private Shape makeShape(){
        Shape s = null;
        switch(defaultShapeType){
            case CIRCLE: s = new Circle(Math.min(defaultWidth/2, defaultHeight/2));
                        shapeType = CIRCLE;
                        break;
            case RECTANGLE: s = new Rectangle(defaultWidth,defaultHeight);
                            shapeType = RECTANGLE;
                            break;
            case TRIANGLE: s = new Polygon(defaultWidth,defaultHeight, defaultWidth*1.5,defaultHeight*2, defaultWidth/2,defaultHeight*2);
                           shapeType = TRIANGLE;
                           break;
            case OVAL: s = new Ellipse(Math.min(defaultWidth/2, defaultHeight/2), Math.min(defaultWidth, defaultHeight));
                       shapeType = OVAL;
                       break;
            case ROUNDRECT: Rectangle r = new Rectangle(defaultWidth, defaultHeight);
                            r.setArcWidth(10);
                            r.setArcHeight(10);
                            s = r;
                            shapeType = ROUNDRECT;
                            break;
            case TEXT: Text t = new Text();
                        Font.getFamilies();
                        t.setText(defaultText);
                        t.setStrokeWidth(defaultStrokeWidth);
                        t.setFont(new Font(defaultFontName, defaultFontSize));
                        s = t;
                        shapeType = TEXT;
                        break;
            case IMAGE: setDefaultShapeType(CIRCLE); 
                        shapeType = IMAGE;
                        break;
        }
        return s;
    }

    public MyShape(){
        super();
        selected = new SimpleBooleanProperty(false);
        selected.set(false);
        shape = makeShape();
        if(shape instanceof Text){//test this first because Text isa Shape
           ((Text)shape).setStroke(defaultStrokePaint);
           ((Text)shape).setBoundsType(TextBoundsType.VISUAL);
        } else if(shape instanceof Shape){
           ((Shape)shape).setFill(defaultFillPaint);
           ((Shape)shape).setStroke(defaultStrokePaint);
           ((Shape)shape).setStrokeWidth(defaultStrokeWidth);
        }
        this.getChildren().add(shape);
        this.setPadding(new Insets(5,5,5,5));//A couple of magic numbers 5..10
        this.setMinWidth(10);
        this.setMinHeight(10);
    }
    
    public MyShape(MyShape other){
        
        super();
        selected = new SimpleBooleanProperty(false);
        selected.set(false);
        defaultShapeType = other.getShapeType();
        shape = makeShape();
        if(shape instanceof Text){//test this first because Text isa Shape
           ((Text)shape).setFill(((Text)other.shape).getFill());
           ((Text)shape).setStroke(((Text)other.shape).getStroke());
           ((Text)shape).setStrokeWidth(((Text)other.shape).getStrokeWidth());
           ((Text)shape).setFont(((Text)other.shape).getFont());
           ((Text)shape).setText(((Text)other.shape).getText());
           ((Text)shape).setBoundsType(((Text)other.shape).getBoundsType());
        }
        else if (shape instanceof Shape){
            ((Shape)this.shape).setFill(((Shape)other.shape).getFill());
            ((Shape)this.shape).setStroke(((Shape)other.shape).getStroke());
            ((Shape)this.shape).setStrokeWidth(((Shape)other.shape).getStrokeWidth());
        }
        this.getChildren().add(this.shape);
    }
    
    public void changeSizeButOnlyDuringADrag(double width, double height){ //Buggy
        if(shape instanceof Circle){
            Circle c = (Circle)shape; 
            c.setRadius(Math.min(width/2.0,height/2.0)-this.getInsets().getLeft()-c.getStrokeWidth()/2.0);
        } else if(shape instanceof Rectangle){
            Rectangle r = (Rectangle)shape;
            r.setWidth(width-this.getInsets().getLeft()-this.getInsets().getRight()-r.getStrokeWidth());
            r.setHeight(height-this.getInsets().getTop()-this.getInsets().getBottom()-r.getStrokeWidth());
        } else if(shape instanceof Polygon){
            Polygon p = (Polygon)shape;
            p.getPoints().remove(0, 6);
            p.getPoints().add(width - this.getInsets().getLeft() - this.getInsets().getRight() - p.getStrokeWidth()/2.0);
            p.getPoints().add(height);
            p.getPoints().add(width*1.5 - this.getInsets().getLeft() - this.getInsets().getRight() - p.getStrokeWidth()/2.0);
            p.getPoints().add(height*2 - this.getInsets().getTop() - this.getInsets().getBottom() - p.getStrokeWidth()/2.0);
            p.getPoints().add(width/2.0 - this.getInsets().getLeft() - this.getInsets().getRight() - p.getStrokeWidth()/2.0);
            p.getPoints().add(height*2 - this.getInsets().getTop() - this.getInsets().getBottom() - p.getStrokeWidth()/2.0);
            
        } else if(shape instanceof Ellipse){
            Ellipse e = (Ellipse)shape;
            e.setRadiusX(width/2.0-this.getInsets().getLeft()-e.getStrokeWidth()/2.0);
            e.setRadiusY(height/2.0-this.getInsets().getLeft()-e.getStrokeWidth()/2.0);
        } else if(shape instanceof Line){
            Line l = (Line)shape;
            l.setEndX(width - this.getInsets().getLeft() - this.getInsets().getRight() - l.getStrokeWidth()/2.0);
        }else if(shape instanceof Text){
            Text t = (Text)shape;
            Bounds boundsInLocal = t.getBoundsInLocal();
            double h = boundsInLocal.getHeight();
            double w = boundsInLocal.getWidth();
            double newHeight = height - getInsets().getTop() - getInsets().getBottom();
            double newWidth = width - getInsets().getLeft() - getInsets().getRight();
            double wr = newWidth/w;
            double hr = newHeight/h;
            double scale = Math.min(wr, hr);
            double newSize = Math.max(t.getFont().getSize()*scale,2);
            String name = t.getFont().getName();
            t.setFont(new Font(name,newSize));   
        }
    }
    public boolean isSelected(){
        return selected.get();
    }
    public void setSelected(boolean value){
        selected.set(value);
        if(value){
            this.setBackground(new Background(new BackgroundFill(null,CornerRadii.EMPTY,Insets.EMPTY)));
            this.setStyle("-fx-border-style: dashed;");
        }
        else {
            this.setBackground(Background.EMPTY);
            this.setStyle("-fx-border-style: none;");
        }

    }
    public BooleanProperty selectedProperty(){
        return selected;
    } 
    public boolean shapeContains(double x,  double y){
        if (shape instanceof Circle) return shape.contains(x-this.getWidth()/2, y-this.getHeight()/2);
        else if(shape instanceof Rectangle){
            System.out.println(x-this.getInsets().getLeft()/2);
            System.out.println(y-this.getInsets().getTop()/2);
            return shape.contains(x-this.getInsets().getLeft(), y-this.getInsets().getTop());
        }
        else if (shape instanceof Ellipse) return shape.contains(x-this.getWidth()/2, y-this.getHeight()/2);
        else if(shape instanceof Polygon){
            System.out.println(x-this.getInsets().getLeft()/2);
            System.out.println(y-this.getInsets().getTop()/2);
            System.out.println(shape.contains(x-this.getInsets().getLeft()/2, y-this.getInsets().getTop()/2));
            return shape.contains(x-this.getInsets().getLeft()-20, y-this.getInsets().getTop()-20);
        }
        else if(shape instanceof Text){
            Insets insets = this.getInsets();
            return x>insets.getLeft()&& x < this.getWidth()-insets.getRight()&&y>insets.getTop()&&y<this.getHeight()-insets.getBottom();   
        }
        else return false;
    }
    public static void setDefaultFillPaint(Paint value){
        defaultFillPaint = value;
    }
    public static void setDefaultStrokePaint(Paint value){
        defaultStrokePaint = value;
    }
    public static void setDefaultSelectedPaint(Paint value){
        defaultSelectedPaint = value;
    }
    public static void setDefaultStrokeWidth(double value){
        defaultStrokeWidth = value;
    }
    public static void setDefaultShapeType(int value){
        defaultShapeType = value;
    }
    public static void setDefaultWidth(double value){
        defaultWidth = value;
    }
    public static void setDefaultHeight(double value){
        defaultHeight = value;
    }
    public static double getDefaultWidth(){
        return defaultWidth;
    }
    public static double getDefaultHeight(){
        return defaultHeight;
    } 
    public static int getDefaultShapeType(){
        return defaultShapeType;
    }
    public void setFillColor(Color value){
        if(shape instanceof Shape){
            ((Shape)shape).setFill(value);
        }
    }
    public Color getFillColor(){
        if(shape instanceof Shape){
            return (Color)(((Shape)shape).getFill());
        }
        
        return null;
    }
    public void setStrokeColor(Color value){
        if(shape instanceof Shape){
            ((Shape)shape).setStroke(value);
        }
    }
    public void setStrokeWidth(double value){
        if(shape instanceof Shape){
            ((Shape)shape).setStrokeWidth(value);
        }
    }
    public void setStrokeDashArray(double [] value){
        if(shape instanceof Shape){
            ((Shape)shape).getStrokeDashArray().clear();
            for(int i = 0; i < value.length; i++)
               ((Shape)shape).getStrokeDashArray().add(value[i]);
        }
    }
    public static void setDefaultText(String t){
        defaultText = t;
    }
    public void setText(String s){
        if(shape instanceof Text) ((Text) shape).setText(s);
        
        
    }
    
    public static String getText(){
        return defaultText;
    }
    
    public static void setDefaultFontName(String s){
        defaultFontName = s;
    }
    
    public void setFont(String s){
        if(shape instanceof Text) ((Text)shape).setFont(new Font(s, defaultFontSize));   
    }
    public int getShapeType(){
        return shapeType;
    }
}

class DrawPane extends Pane{
    private MyShape selectedShape=null;
    private boolean dragging = false;
    private double oldMouseX;
    private double oldMouseY;
    private ContextMenu ctMenu;
    MenuItem copy;
    MenuItem cut;
    MenuItem paste;
    MenuItem delete;
    MenuItem moveBack;
    MenuItem moveToBack;
    MenuItem moveUp;
    MenuItem moveToFront;
    private MyShape tmpShape;

    public DrawPane(){
        super();
        this.setPrefSize(800, 600);
        this.setOnMousePressed(e->mousePressed(e));
        this.setOnMouseReleased(e->mouseReleased(e));
        ctMenu = new ContextMenu();
        ctMenu.setAutoHide(true);
        
        copy = new MenuItem("Copy");
        copy.setDisable(true);
        cut = new MenuItem("Cut");
        cut.setDisable(true);
        paste = new MenuItem("Paste");
        paste.setDisable(true);
        delete = new MenuItem("Delete");
        delete.setDisable(true);
        moveBack = new MenuItem("Move Back");
        moveBack.setDisable(true);
        moveToBack = new MenuItem("Move To Back");
        moveToBack.setDisable(true);
        moveUp = new MenuItem("Move Up");
        moveUp.setDisable(true);
        moveToFront = new MenuItem("Move To Front");
        moveToFront.setDisable(true);
        copy.setOnAction(e->{
            copy();
        });
        cut.setOnAction(e->{
            cut();
        });
        delete.setOnAction(e->{
            delete();
        });
        moveBack.setOnAction(e->{
            moveBack();
        });
        moveToBack.setOnAction(e->{
            moveToBack();
        });
        moveUp.setOnAction(e->{
            moveUp();
        });
        moveToFront.setOnAction(e->{
            moveToFront();
        });
        
        ctMenu.getItems().addAll(copy, cut, paste, delete, moveBack, moveToBack, moveUp, moveToFront);
    }
    public MyShape getSelectedShape(){
        return selectedShape;
    }
    public MyShape [] getSelectedShapes(){ //This could be useful!
      return null;  
    }
    public MyShape [] getUnSelectedShapes(){ //This could be useful too!
      return null;  
    }
    private void mousePressed(MouseEvent me){
        System.out.println("MousePressed");
        MyShape s = new MyShape();
        if(me.isSecondaryButtonDown())contextMenu(me);
        else if(selectedShape == null && me.isPrimaryButtonDown()){
            if(MyShape.getDefaultShapeType()==MyShape.TEXT){
                   Bounds boundsInParent = s.getBoundsInParent();
                   double width = boundsInParent.getWidth();
                   double height = boundsInParent.getHeight();
                   s.relocate(me.getX()-s.getInsets().getLeft()-width/2,
                           me.getY()-s.getInsets().getTop()-height/2);           
            }
            else {
                s.relocate(me.getX()-s.getInsets().getLeft()-MyShape.getDefaultWidth()/2, 
                    me.getY()-s.getInsets().getTop()-MyShape.getDefaultHeight()/2); 
            }
            s.setOnMousePressed(e->shapePressed(e,s));
            s.setOnMouseReleased(e->shapeReleased(e,s));
            s.setOnMouseDragged(e->shapeDragged(e,s));
            this.getChildren().add(s);
        }else {
            this.getSelectedShape().setSelected(false);
            selectedShape = null;
        }
    }
    
    private void mouseReleased(MouseEvent me){
        System.out.println("MouseReleased");
    }

    private void shapePressed(MouseEvent e, MyShape s) {
        System.out.println("ShapePressed");
        if(s.isSelected() && e.isSecondaryButtonDown()){
            System.out.println("Right clicked shape");
            copy.setDisable(false);
            cut.setDisable(false);
            delete.setDisable(false);
            moveBack.setDisable(false);
            moveToBack.setDisable(false);
            moveUp.setDisable(false);
            moveToFront.setDisable(false);
            contextMenu(e);
        }
        else if(e.isPrimaryButtonDown() && !s.isSelected()){//s.setSelected(!s.isSelected());
            if(selectedShape != s && selectedShape != null){
                selectedShape.setSelected(false);
                selectedShape = null;
            }
            s.setSelected(true);
            selectedShape = s;
            oldMouseX = e.getScreenX();
            oldMouseY = e.getScreenY();
        }else if (e.isPrimaryButtonDown() && s.isSelected()){
            oldMouseX = e.getScreenX();
            oldMouseY = e.getScreenY();
        } else selectedShape = null;
        e.consume();//Don't trigger any clicks in the parent
    }

    private void shapeReleased(MouseEvent e, MyShape s) {
        System.out.println("ShapeReleased");
        dragging=false;
    }

    private void shapeDragged(MouseEvent e, MyShape s) {
        System.out.println("ShapeDragged");
        if(s.isSelected()) {
            double newMouseX = e.getScreenX();
            double newMouseY = e.getScreenY();
            double dx = newMouseX-oldMouseX;
            double dy = newMouseY-oldMouseY;
            oldMouseX = newMouseX;
            oldMouseY = newMouseY;
            if(s.shapeContains(e.getX(),e.getY())||dragging){
               dragging=true;
               Bounds boundsInParent = s.getBoundsInParent();
               s.relocate(boundsInParent.getMinX()+dx,boundsInParent.getMinY()+dy);
            }
            else {
               s.setPrefHeight(s.getHeight()+dy);
               s.setPrefWidth(s.getWidth()+dx); 
            }
            s.changeSizeButOnlyDuringADrag(s.getWidth(), s.getHeight());
        }
    }
    
    private void contextMenu(MouseEvent me){
        ctMenu.getItems().removeAll();
        if(!ctMenu.isShowing()){
            ctMenu.show(this, me.getScreenX(), me.getScreenY());
            paste.setOnAction(e->{
                paste(me);
            });
        }else {
            copy.setDisable(true);
            cut.setDisable(true);
            paste.setDisable(true);
            delete.setDisable(true);
            moveBack.setDisable(true);
            moveToBack.setDisable(true);
            moveUp.setDisable(true);
            moveToFront.setDisable(true);
            ctMenu.hide();
        }
        
    }
    
    private void copy(){
        System.out.println("Copy");
        tmpShape = new MyShape(selectedShape);
        selectedShape.setSelected(false);
        selectedShape = null;
        paste.setDisable(false);
        
        copy.setDisable(true);
        cut.setDisable(true);
        delete.setDisable(true);
        moveBack.setDisable(true);
        moveToBack.setDisable(true);
        moveUp.setDisable(true);
        moveToFront.setDisable(true);
    }
    
    private void cut(){
        System.out.println("Cut");
        tmpShape = selectedShape;
        this.getChildren().remove(selectedShape);
        paste.setDisable(false);
        
        copy.setDisable(true);
        cut.setDisable(true);
        delete.setDisable(true);
        moveBack.setDisable(true);
        moveToBack.setDisable(true);
        moveUp.setDisable(true);
        moveToFront.setDisable(true);
    }
    
    private void paste(MouseEvent me){
        System.out.println("Paste");
        if(!(tmpShape == null)){
            //System.out.println(tmpShape.defaultFillPaint);
            selectedShape = tmpShape;
            tmpShape = null;
            if(selectedShape.getDefaultShapeType()==MyShape.TEXT){
                Bounds boundsInParent = selectedShape.getBoundsInParent();
                double width = boundsInParent.getWidth();
                double height = boundsInParent.getHeight();
                selectedShape.relocate(me.getX()-selectedShape.getInsets().getLeft()-width/2,
                me.getY()-selectedShape.getInsets().getTop()-height/2);  
            }else{
                selectedShape.relocate(me.getX()-selectedShape.getInsets().getLeft()-MyShape.getDefaultWidth()/2, 
                me.getY()-selectedShape.getInsets().getTop()-MyShape.getDefaultHeight()/2);
            }
            this.getChildren().add(selectedShape);
            paste.setDisable(true);
        }
        
    }
    
    private void delete(){
        System.out.println("Delete");
        this.getChildren().remove(getSelectedShape());
        getSelectedShape().setSelected(false);
        selectedShape = null;
    }
    
    private void moveBack(){
        System.out.println("Move Back");
        int i = this.getChildren().indexOf(getSelectedShape()) - 1;
        try{
            if(this.getChildren().get(i) != null){
                this.getChildren().remove(getSelectedShape());
                this.getChildren().add(i, getSelectedShape());
            }
        }catch(Exception e){
            System.out.println("Cannot be moved. Is already at the very back.");
        }
        
    }
    
    private void moveToBack(){
        System.out.println("Move To Back");
        getSelectedShape().toBack();
    }
    
    private void moveUp(){
        System.out.println("Move Up");
        int i = this.getChildren().indexOf(getSelectedShape()) + 1;
        try{
            if(this.getChildren().get(i) != null){
                this.getChildren().remove(getSelectedShape());
                this.getChildren().add(i, getSelectedShape());
            }
        }catch(Exception e){
            System.out.println("Cannot be moved. Already at the front.");
        }
    }
    
    private void moveToFront(){
        System.out.println("Move To Back");
        getSelectedShape().toFront();
    }
}

class MenuInterface extends HBox{
    
    private int loc = 0;
    private final MenuBar menuBar;
    
    private final Menu menuFile;
    private final Menu menuShape;
    private final Menu menuText;
    private final Text labelFill;
    private final Text labelStroke;
    private final Text labelThickness;
    private final Slider strokeSlider;
    
    //---File Menu---//
    private final MenuItem newFile;
    private final MenuItem open;
    private final MenuItem save;
    private final MenuItem print;
    private final MenuItem help;
    private final MenuItem close;
    
    //---Shape Menus--//
    private final MenuItem circle;
    private final MenuItem oval;
    private final MenuItem rectangle;
    private final MenuItem roundrect;
    private final MenuItem triangle;
    private final MenuItem img;
    
    //---Text Menu--//
    private final MenuItem text;
    private final MenuItem font;
    private final MenuItem changeText;
    
    //---Colour Picker---//
    private static ColorPicker colourPickerFill;
    private static ColorPicker colourPickerStroke;
    
    public MenuInterface(){
        
        menuBar = new MenuBar();
        menuBar.setStyle("-fx-background-color: #e6e6e6;");
    
        menuFile = new Menu("File");
        menuShape = new Menu("Shape");
        menuText = new Menu("Text");
        
        labelFill = new Text("Fill:");
        labelStroke = new Text("Stroke:");
        labelThickness = new Text("Thickness:");
        strokeSlider = new Slider(0.0, 10.0, 0.0);

        //---File Menu---//
        newFile = new MenuItem("New");
        open = new MenuItem("Open");
        save = new MenuItem("Save");
        print = new MenuItem("Print");
        help = new MenuItem("Help");
        close = new MenuItem("Exit");
        
        help.setOnAction(e->{
            helpMenu();
        });
        
        close.setOnAction(e->{
            Platform.exit();
        });
        

        //---Shape Menus--//
        circle = new MenuItem("Circle");
        oval = new MenuItem("Oval");
        rectangle = new MenuItem("Rectangle");
        roundrect = new MenuItem("Rounded Rectangle");
        triangle = new MenuItem("Triangle");
        img = new MenuItem("Image");
        
        circle.setOnAction(e->{
            MyShape.setDefaultShapeType(MyShape.CIRCLE);
        });
        
        oval.setOnAction(e->{
            MyShape.setDefaultShapeType(MyShape.OVAL);
        });
        
        rectangle.setOnAction(e->{
            MyShape.setDefaultShapeType(MyShape.RECTANGLE);
        });
        
        roundrect.setOnAction(e->{
            MyShape.setDefaultShapeType(MyShape.ROUNDRECT);
        });
        
        triangle.setOnAction(e->{
            MyShape.setDefaultShapeType(MyShape.TRIANGLE);
        });
        
        //---Text Menu--//
        text = new MenuItem("Add Text");
        font = new MenuItem("Change Font");
        changeText = new MenuItem("Change Text");
        
        text.setOnAction(e->{
            MyShape.setDefaultShapeType(MyShape.TEXT);
        });
        /*
        changeText.setOnAction(e->{
            changeTextMenu(pane);
        });
        */
        //---Colour Picker---//
        colourPickerFill = new ColorPicker();
        colourPickerFill.setValue(Color.BLACK);
        colourPickerFill.setStyle(menuBar.getStyle());
        
        colourPickerStroke = new ColorPicker();
        colourPickerStroke.setValue(Color.BLACK);
        colourPickerStroke.setStyle(menuBar.getStyle());
        
        
        
        menuBar.getMenus().addAll(menuFile, menuShape, menuText);
        menuFile.getItems().addAll(newFile, open, save, print, help, close);
        menuShape.getItems().addAll(circle, oval, rectangle, roundrect, triangle, img);
        menuText.getItems().addAll(text, font, changeText);
        
        this.setSpacing(10);
        this.setStyle(menuBar.getStyle());
        this.getChildren().addAll(menuBar, labelFill, colourPickerFill, 
                                  labelStroke, colourPickerStroke, labelThickness,
                                  strokeSlider);
    
    }
    
    public MenuItem getPrint(){
        return print;
    }
    
    public MenuItem getRectangle(){
        return rectangle;
    }
    
    public MenuItem getCircle(){
        return circle;
    }
    
    public MenuItem getTriangle(){
        return triangle;
    }
    
    public MenuItem getOVAL(){
        return oval;
    }
    
    public MenuItem getImageSelect(){
        return img;
    }
    
    public ColorPicker getPickerFill(){
        return colourPickerFill;
    }
    
    public ColorPicker getPickerStroke(){
        return colourPickerStroke;
    }
    
    public static Color getColourFill(){
        return colourPickerFill.getValue();
    }
    
    public static Color getColourStroke(){
        return colourPickerStroke.getValue();
    }
    
    public Slider getStrokeSlider(){
        return strokeSlider;
    }
    
    public MenuItem getChangeText(){
        return changeText;
    }
    
    public MenuItem getChangeFont(){
        return font;
    }
    
    public MenuItem getSave(){
        return save;
    }
    
    public MenuItem getOpen(){
        return open;
    }
    
    public MenuItem getNew(){
        return newFile;
    }
    
    public void helpMenu(){ //Prompts the help menu to pop up
        loc = 0;
        BorderPane helpMe = new BorderPane();   
        HBox helpBoxTop = new HBox();
        HBox helpBoxMiddle = new HBox();
        HBox pageNoHold = new HBox();
        FlowPane buttonHolder = new FlowPane();
        Text [] textArray = new Text[5];
        Text helpText = new Text("Welcome to CarrotCapture!\nHere's how to use the program:");
        textArray[0] = new Text("- Left click anywhere to place a shape or text on the screen.\n"
                              + "- Left click on a shape to select it.\n"
                              + "- Right click on a selected shape to bring up a number of options for that selected shape.\n"
                              + "- Select the fill color you want using the colour picker at the top. There is also one for the stroke colour.\n"
                              + "- Change the thickness of the stroke by sliding the thickness slider at the top left or right.");
        textArray[1] = new Text("- Select different shapes by opening the shape menu at the top, and clicking the desired shape.\n"
                              + "- If you would like to add text, click on the text menu at the top, and then select 'Add Text'\n"
                              + "- Change the contents of the text being placed or selected text with Text>Change Text.\n"
                              + "- Change the font of a new or selected text with Text>Change Font.");
        textArray[2] = new Text("- Resize a shape by selecting it and dragging the edges.\n"
                              + "- Move a shape by selecting it and dragging it by the middle of the shape.\n"
                              + "- Unselect an item by left clicking outside of the item.");
        textArray[3] = new Text("- Save a drawing with File>Save.\n"
                              + "- Open a drawing with File>Open.\n"
                              + "- Erase your drawing to start fresh with File>New.");
        textArray[4] = new Text("- Thank you for using CarrotCapture! :)");
        Text pageNo = new Text("          "+Integer.toString(loc+1)+"/"+Integer.toString(textArray.length));
        Button next = new Button("Next");
        Button prev = new Button("Prev");
        pageNoHold.getChildren().add(pageNo);
        pageNoHold.setAlignment(Pos.BOTTOM_RIGHT);
        buttonHolder.getChildren().addAll(prev, next);
        buttonHolder.setAlignment(Pos.CENTER);
        buttonHolder.getChildren().add(pageNoHold);
        
        next.setOnAction(e->{
            if(loc == textArray.length-1) loc = 0;
            else loc++;
            helpMe.setCenter(textArray[loc]);
            pageNo.setText("          "+Integer.toString(loc+1)+"/"+Integer.toString(textArray.length));
        });
        
        prev.setOnAction(e->{
            if(loc == 0) loc = textArray.length-1;
            else loc--;
            helpMe.setCenter(textArray[loc]);
            pageNo.setText("          "+Integer.toString(loc+1)+"/"+Integer.toString(textArray.length));
        });
        
        
        helpBoxTop.getChildren().add(helpText);
        helpMe.setTop(helpBoxTop);
        helpMe.setBottom(buttonHolder);
        helpMe.setCenter(textArray[0]);
        Stage helpStage = new Stage();
        Scene helpScene = new Scene(helpMe, 600, 300);
        
        helpStage.setTitle("Help Menu");
        helpStage.setScene(helpScene);
        helpStage.getIcons().add(new Image(this.getClass().getResourceAsStream("helpicon.jpg")));
        helpStage.setResizable(false);
        helpStage.show();
        
    }
    
    public void changeTextMenu(DrawPane p){
        String t;
        StackPane changeTextPane = new StackPane();
        TextField changeTextField = new TextField();
        t = MyShape.getText();
        changeTextField.setText(MyShape.getText());
        changeTextField.setText(t);
        changeTextPane.getChildren().add(changeTextField);
        Scene textScene = new Scene(changeTextPane, 400, 200);
        Stage changeTextStage = new Stage();
        changeTextStage.setTitle("Change Text");
        changeTextStage.setScene(textScene);
        changeTextStage.setResizable(false);
        changeTextStage.show();
        
        changeTextField.setOnAction(e->{
            if(p.getSelectedShape() != null) p.getSelectedShape().setText(changeTextField.getText());
            else MyShape.setDefaultText(changeTextField.getText());
            changeTextStage.close();
        });
    }
    
    public void fontMenu(DrawPane p){
        Button timesNewRoman = new Button("Times New Roman");
        Button arial = new Button("Arial");
        Button courierNew = new Button("Courier New");
        Button agencyFB = new Button("Agency FB");
        Button cambria = new Button("Cambria");
        timesNewRoman.setMinSize(115, 25);
        arial.setMinSize(115, 25);
        courierNew.setMinSize(115, 25);
        agencyFB.setMinSize(115, 25);
        cambria.setMinSize(115, 25);
        
        Stage fontStage = new Stage();
        VBox fontBox = new VBox();
        fontBox.getChildren().addAll(timesNewRoman, arial, courierNew, agencyFB, cambria);
        
        timesNewRoman.setOnAction(e->{
            MyShape.setDefaultFontName("Times New Roman");
            if(p.getSelectedShape() != null) p.getSelectedShape().setFont("Times New Roman");
            fontStage.close();
        });
        arial.setOnAction(e->{
            MyShape.setDefaultFontName("Arial");
            if(p.getSelectedShape() != null) p.getSelectedShape().setFont("Arial");
            fontStage.close();
        });
        courierNew.setOnAction(e->{
            MyShape.setDefaultFontName("Courier New");
            if(p.getSelectedShape() != null) p.getSelectedShape().setFont("Courier New");
            fontStage.close();
        });
        agencyFB.setOnAction(e->{
            MyShape.setDefaultFontName("Agency FB");
            if(p.getSelectedShape() != null) p.getSelectedShape().setFont("Agency FB");
            fontStage.close();
        });
        cambria.setOnAction(e->{
            MyShape.setDefaultFontName("Cambria");
            if(p.getSelectedShape() != null) p.getSelectedShape().setFont("Cambria");
            fontStage.close();
        });
        
        Scene fontScene = new Scene(fontBox, 400, 200);
        
        fontStage.setScene(fontScene);
        fontStage.setTitle("Font Selector");
        fontStage.show();
        
    }
    
}


/**
 *
 * @author Tyler PC
 */
public class CarrotCapture extends Application {
    
    
    
    @Override
    public void start(Stage primaryStage) {
        
        MenuInterface topMenu = new MenuInterface();
        
        
        DrawPane pane = new DrawPane();
        pane.setBackground(new Background(new BackgroundFill(Color.WHITE, null, null)));
        BorderPane root = new BorderPane();
        root.setCenter(pane);
        root.setTop(topMenu);
        Scene scene = new Scene(root);
        
        topMenu.getPrint().setOnAction(e->{
            this.print(pane);
        });
        
        
        topMenu.getPickerFill().setOnAction(e->{
            if(pane.getSelectedShape() != null)
                pane.getSelectedShape().setFillColor(topMenu.getColourFill());
            MyShape.setDefaultFillPaint(topMenu.getColourFill());
        });
        
        topMenu.getPickerStroke().setOnAction(e->{
            if(pane.getSelectedShape() != null)
                pane.getSelectedShape().setStrokeColor(topMenu.getColourStroke());
            MyShape.setDefaultStrokePaint(topMenu.getColourStroke());
        });
        
        topMenu.getStrokeSlider().valueProperty().addListener(e -> {
            System.out.println(topMenu.getStrokeSlider().getValue());
            if(pane.getSelectedShape() != null)
                pane.getSelectedShape().setStrokeWidth((double)topMenu.getStrokeSlider().getValue());
            MyShape.setDefaultStrokeWidth(topMenu.getStrokeSlider().getValue());
         });
        
        topMenu.getImageSelect().setOnAction(e->{
            
            openFile(pane);
        });
        
        topMenu.getNew().setOnAction(e ->{
            pane.getChildren().clear();
        });
        
        topMenu.getOpen().setOnAction(e ->{
            pane.getChildren().clear();
            openFile(pane);
            
        });
        
        topMenu.getChangeText().setOnAction(e->{
            topMenu.changeTextMenu(pane);
        });
        
        topMenu.getChangeFont().setOnAction(e->{
            topMenu.fontMenu(pane);
        });
        
        topMenu.getSave().setOnAction(e->{
            saveFile(pane);
        });
        
        
        
        primaryStage.setTitle("CarrotCapture");
        primaryStage.setScene(scene);
        primaryStage.getIcons().add(new Image(this.getClass().getResourceAsStream("carrotcatureblue (40x40).jpg")));
        primaryStage.show();
        
    }
    
    public void saveFile(DrawPane pane) {
        WritableImage image = pane.snapshot(new SnapshotParameters(), null);

        FileChooser save = new FileChooser();
        File file = save.showSaveDialog(new Stage());

        
        if(file != null){
            String fileName = file.getName();
            fileName = fileName + ".png";
            File saved = new File(fileName);
        

            try {
                ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", saved);
            } catch (IOException e) {

            }
        }
    }
    
    public void openFile(DrawPane pane){
        FileChooser imgPicker = new FileChooser();
        imgPicker.setTitle("Open Image");
        imgPicker.getExtensionFilters().add(new ExtensionFilter("Image Files",
                                            "*.png", "*.jpg", "*.gif"));
        File selectedImage = imgPicker.showOpenDialog(new Stage());
        ImageView imgView = new ImageView();
        if(selectedImage != null)imgView.setImage(new Image("File:" + selectedImage.getPath()));
        pane.getChildren().add(imgView);
        imgView.toBack();
        imgView.setPreserveRatio(true);
        imgView.setFitWidth(pane.getWidth());
        imgView.setFitHeight(pane.getHeight());
            
            
        MyShape.setDefaultShapeType(MyShape.IMAGE);
    }

    public void print(final Node node) {
        ObservableSet<Printer> allPrinters = Printer.getAllPrinters();
        for(Printer p:allPrinters){ //List  all the printers
            System.out.println(p.getName());
        }
    
        Printer printer = Printer.getDefaultPrinter();
        PrinterAttributes printerAttributes = printer.getPrinterAttributes();
        // Do something with the attributes of the default printer
        
        PageLayout pageLayout = printer.createPageLayout(Paper.NA_LETTER, 
                PageOrientation.LANDSCAPE, Printer.MarginType.DEFAULT);
 
        System.out.println("Printable Height:"+pageLayout.getPrintableHeight());
        System.out.println("Printable Width :"+pageLayout.getPrintableWidth());
        System.out.println("Top Margin      :"+pageLayout.getTopMargin());
        System.out.println("Bottom Margin   :"+pageLayout.getBottomMargin());
        System.out.println("Left Margin     :"+pageLayout.getLeftMargin());
        System.out.println("Right Margin    :"+pageLayout.getRightMargin());
        
        //You may need to apply a scale transform to the node
        //  and/or use PageOrientation.LANDSCAPE
        
        //Since printing may take some time you may print on a different thread.
        //Otherwise we may slow down the UI and make it seem un-responsive
        
        PrinterJob job = PrinterJob.createPrinterJob();
        job.getJobSettings().setPrintColor(PrintColor.MONOCHROME);
        if (job != null) {
            boolean success = job.printPage(node);
            if (success) {
                job.endJob();
            }
        }
    }    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
