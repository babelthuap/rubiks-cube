/**
 *  stores the triangles for one cubie of a Rubik's cube
 *  (i.e. one of the little cubes)
 *  
 *  Alec and Nicholas
 */
public class Cubie {
    // side length of cubies
    private final int SIDE = 100;
    
    // how much to explode the cubie (move it away from the origin)
    private final double explode = 3;
    
    // the triangle mesh describing the cubie
    private Triangle[] triangleMesh;

    /*  "cNum" (stands for "cubieNumber") is determined by our convention:
     *  
     *  bottom layer:  top layer:
     *      0 3           4 7         (  back-left, back-right  )
     *      1 2           5 6         ( front-left, front-right )
     *  
     *  cNum = 8 denotes the center cube
     */
    public Cubie( int cNum, String[][] colorList ) {
        // vertex coordinates for v0 for each of the different cubies
        // (v0 is the lower back left vertex)
        Vector v0;
        if ( cNum == 0 ) {
            Vector moveAway = new Vector(-explode,explode,explode);
            v0 = new Vector(-SIDE,SIDE,SIDE).plus(moveAway);
        } else if ( cNum == 1 ) {
            Vector moveAway = new Vector(-explode,explode,-explode);
            v0 = new Vector(-SIDE,SIDE,0).plus(moveAway);
        } else if ( cNum == 2 ) {
            Vector moveAway = new Vector(explode,explode,-explode);
            v0 = new Vector(0,SIDE,0).plus(moveAway);
        } else if ( cNum == 3 ) {
            Vector moveAway = new Vector(explode,explode,explode);
            v0 = new Vector(0,SIDE,SIDE).plus(moveAway);
        } else if ( cNum == 4 ) {
            Vector moveAway = new Vector(-explode,-explode,explode);
            v0 = new Vector(-SIDE,0,SIDE).plus(moveAway);
        } else if ( cNum == 5 ) {
            Vector moveAway = new Vector(-explode,-explode,-explode);
            v0 = new Vector(-SIDE,0,0).plus(moveAway);
        } else if ( cNum == 6 ) {
            Vector moveAway = new Vector(explode,-explode,-explode);
            v0 = new Vector(0,0,0).plus(moveAway);
        } else if ( cNum == 7 ) {
            Vector moveAway = new Vector(explode,-explode,explode);
            v0 = new Vector(0,0,SIDE).plus(moveAway);
        } else { // if ( cNum == 8 )
            v0 = new Vector(-SIDE/2,SIDE/2,SIDE/2);
        }
        // the rest of the vertices are computed in relation to v0:
        Vector v1 = v0.plus( new Vector(0,0,-SIDE) );
        Vector v2 = v0.plus( new Vector(SIDE,0,-SIDE) );
        Vector v3 = v0.plus( new Vector(SIDE,0,0) );
        Vector v4 = v0.plus( new Vector(0,-SIDE,0) );
        Vector v5 = v0.plus( new Vector(0,-SIDE,-SIDE) );
        Vector v6 = v0.plus( new Vector(SIDE,-SIDE,-SIDE) );
        Vector v7 = v0.plus( new Vector(SIDE,-SIDE,0) );
        
        // create the triangle mesh describing the cubie
        triangleMesh =
            new Triangle[]{ new Triangle( v0, v2, v1, colorList[cNum][0]),
                            new Triangle( v0, v3, v2, colorList[cNum][0]),
                            new Triangle( v0, v1, v5, colorList[cNum][1]),
                            new Triangle( v0, v5, v4, colorList[cNum][1]),
                            new Triangle( v1, v2, v6, colorList[cNum][2]),
                            new Triangle( v1, v6, v5, colorList[cNum][2]),
                            new Triangle( v2, v3, v6, colorList[cNum][3]),
                            new Triangle( v3, v7, v6, colorList[cNum][3]),
                            new Triangle( v3, v0, v7, colorList[cNum][4]),
                            new Triangle( v0, v4, v7, colorList[cNum][4]),
                            new Triangle( v4, v5, v6, colorList[cNum][5]),
                            new Triangle( v4, v6, v7, colorList[cNum][5]) };
        
        // the center cube can be a little larger
        if ( cNum == 8 ) {
            Matrix scale = new Matrix( Math.sqrt(2),0,0,
                                       0,Math.sqrt(2),0,
                                       0,0,Math.sqrt(2) );
            triangleMesh = scale.affineTimes(triangleMesh, new Vector(0,0,0));
        }
    }
    
    // return the mesh of triangles describing the cube
    public Triangle[] mesh() {
        return triangleMesh;
    }
}