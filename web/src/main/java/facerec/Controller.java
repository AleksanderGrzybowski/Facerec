package facerec;

import spark.Spark;

public class Controller {
    public static void main(String[] args) {
        Spark.get("/", (req, res ) -> "Hello world");
    }
}
