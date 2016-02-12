/**
 *  store a triangle as an ordered set of 3 vectors (its vertices)
 *  AND its color as an RGB triple
 *  AND the 2D coordinateas of its projection onto the screen
 * 
 *  Alec and Nicholas
 */
public class Triangle {
    // vertices
    private Vector[] vertices;
    
    // color as RGB triple or as string
    private int[] color = new int[3];

    public Triangle( Vector v0, Vector v1, Vector v2, String colorName ) {
        vertices = new Vector[]{v0,v1,v2};
        color = nameToRGB( colorName );
    }
        public Triangle( Vector v0, Vector v1, Vector v2, int[] colorArray ) {
        vertices = new Vector[]{v0,v1,v2};
        color = colorArray;
    }
    
    // determines whether the triangle is facing towards the negative
    // z-direction (with vertices in counterclockwise orientation)
    public boolean isVisible() {
        Vector v0 = vertices[0].flatProjection();
        Vector v1 = vertices[1].flatProjection();
        Vector v2 = vertices[2].flatProjection();
        Vector AB = v1.minus(v0);
        Vector AC = v2.minus(v0);
        return AB.cross(AC).comp(2) < 0;
    }
    
    // orthogonal projection (camera is pointing in negative
    // z-direction, and has focal length f)
    public Triangle project( double f ) {
        return new Triangle( vertices[0].intProjection(f),
                             vertices[1].intProjection(f),
                             vertices[2].intProjection(f),
                             color );
    }
    
    // perspective projection
    public Triangle orthoProject() {
        return new Triangle( vertices[0].intFlatProjection(),
                             vertices[1].intFlatProjection(),
                             vertices[2].intFlatProjection(),
                             color );
    }
    
    // determines whether a flat triangle, oriented counterclockise,
    // contains point p=(x,y)
    public boolean contains( int x, int y ) {
        Vector p = new Vector( x, y, 0 );
        Vector p_v0 = p.minus( vertices[0] );
        Vector p_v1 = p.minus( vertices[1] );
        Vector p_v2 = p.minus( vertices[2] );

        // if the cross products all point in the same direction
        // then p is in the triangle
        return p_v0.cross(p_v1).comp(2) <= 0 &&
               p_v1.cross(p_v2).comp(2) <= 0 &&
               p_v2.cross(p_v0).comp(2) <= 0;
    }
    
    // shift (translate) all vertices by v
    public Triangle shift( Vector v ) {
        return new Triangle( vertices[0].plus(v),
                             vertices[1].plus(v),
                             vertices[2].plus(v),
                             color );
    }
    
    // returns vertex v_i
    public Vector vertex( int i ) {
        return vertices[i];
    }
    
    // z-coordinate of vertex v_i
    public double zCoord( int i) {
        return vertices[i].comp(2);
    }
    
    // set z-coordinates
    public Triangle zSet( double a, double b, double c) {
        return new Triangle( vertices[0], vertices[1],
                             new Vector(a,b,c), color );
    }
    
    // returns the Red (0), Green (1), or Blue (2)
    //           component of the triangle's color
    public int color( int component ) {
        return color[ component ];
    }
    
    // returns the triangle's color array
    public int[] color() {
        return color;
    }
    
    // given a description of a color, this DEFINES the associated RGB values
    private int[] nameToRGB( String name ) {
        int[] output = new int[3];
        
        if ( name.equals("K") ) { // "K" stands for black
            output[0] = 39; output[1] = 39; output[2] = 39;
        } else if ( name.equals("W") ) {
            output[0] = 216; output[1] = 216; output[2] = 216;
        } else if ( name.equals("B") ) {
            output[0] = 0; output[1] = 0; output[2] = 255;
        } else if ( name.equals("R") ) {
            output[0] = 255; output[1] = 0; output[2] = 0;
        } else if ( name.equals("G") ) {
            output[0] = 0; output[1] = 128; output[2] = 0;
        } else if ( name.equals("P") ) {
            output[0] = 138; output[1] = 0; output[2] = 204;   // purple
            //output[0] = 255; output[1] = 127; output[2] = 0; // orange
        } else if ( name.equals("Y") ) {
            output[0] = 255; output[1] = 255; output[2] = 0;
        }
        
        return output;
    }
}