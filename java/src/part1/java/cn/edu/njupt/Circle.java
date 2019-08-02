package cn.edu.njupt;

public class Circle extends Polygon {

    public float area() {
        log("Circle", "我还没来得及写好");
        return 0;
    }

    public static void main(String[] args) {
        Polygon p = new Circle();
        p.log("Circle", "PI=" + PI);
    }
}
