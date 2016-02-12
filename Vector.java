import java.math.*;

/**
 *  store a 3-dimensional vector as an array of doubles
 *  
 *  this includes more methods than are strictly necessary
 *  for the Rubik's cube project
 *  
 * 
 *  Alec and Nicholas
 */
public class Vector {
    // call the vector v
    private double[] v;
    
    public Vector( double x, double y, double z ) {
        // initialise instance variables
        v = new double[]{x,y,z};
    }

    // FOR DEBUGGING
    public String view() {
        return "{"+v[0]+", "+v[1]+", "+v[2]+"}";
    }
    
    // return component c (c can be 0, 1, or 2)
    public double comp(int c) {
        return v[c];
    }
    
    // magnitude (length) of the vector
    public double mag() {
        return Math.sqrt( v[0]*v[0] + v[1]*v[1] + v[2]*v[2] );
    }
    
    // flat projection onto the interger lattice in the z=0 plane
    public Vector flatProjection() {
        return new Vector( v[0], v[1], 0 );
    }
    
    // flat projection onto the interger lattice in the z=0 plane
    public Vector intFlatProjection() {
        return new Vector( round(v[0]), round(v[1]), 0 );
    }
    
    // perspective projection onto the interger lattice in the z=f plane
    public Vector intProjection( double f ) {
        return new Vector( round(v[0] * f / v[2]), round(v[1] * f / v[2]), 0 );
    }
    
    // multply by scalar a
    public Vector scale( double a ) {
        return new Vector( a*v[0], a*v[1], a*v[2] );
    }
    
    // normalized direction of vector
    public Vector direction() {
        return scale( 1 / mag() );
    }
    
    // return v + w
    public Vector plus( Vector w ) {
        return new Vector( v[0]+w.comp(0), v[1]+w.comp(1), v[2]+w.comp(2) );
    }
    
    // return v - w
    public Vector minus( Vector w ) {
        return new Vector( v[0]-w.comp(0), v[1]-w.comp(1), v[2]-w.comp(2) );
    }
    
    // cross product v x w
    public Vector cross( Vector w ) {
        return new Vector( v[1]*w.comp(2) - v[2]*w.comp(1),
                           v[2]*w.comp(0) - v[0]*w.comp(2),
                           v[0]*w.comp(1) - v[1]*w.comp(0) );
    }

    // dot product v.w
    public double dot( Vector w ) {
        return v[0]*w.comp(0) + v[1]*w.comp(1) + v[2]*w.comp(2);
    }
    
    // round a double to the nearest integer
    private double round( double x ) {
        return Math.floor(x+0.5);
    }
}