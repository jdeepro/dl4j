package cn.edu.njupt;

public abstract class Polygon implements IArea {
    String name = "我是谁？";
    float[] points; // 坐标点序列 x1,y1,x2,y2..

    public void log(String tag, String s) {
        System.out.printf("%s: %s, %s\n", tag, name, s);
    }

    public static void main(String[] args) {
        //Polygon p = new Polygon();
        //p.log("Polygon", "main~");
    }
}