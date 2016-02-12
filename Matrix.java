/**
 *  store a 3x3 matrix as an array of its row vectors
 *  
 *  like the Vector class, this includes more methods than
 *  are strictly necessary for the Rubik's cube project
 *  
 *  
 *  Alec and Nicholas
 */
public class Matrix {
    // call the matrix M
    private Vector[] M = new Vector[3];

    // construct M given its row vectors
    public Matrix( Vector r1, Vector r2, Vector r3 ) {
        M[0] = r1;
        M[1] = r2;
        M[2] = r3;
    }
    
    // construct M given its nine elements
    public Matrix( double a, double b, double c,
                   double d, double e, double f, 
                   double g, double h, double i ) {
        M[0] = new Vector( a,b,c );
        M[1] = new Vector( d,e,f );
        M[2] = new Vector( g,h,i );
    }
    
    // construct M to be a 3D clockwise rotation matrix,
    // rotating about y-axis by angle theta and then x-axis by angle phi
    public Matrix( double theta, double phi ) {
        M[0] = new Vector( Math.cos(theta),                0,             Math.sin(theta)                );
        M[1] = new Vector( Math.sin(theta)*Math.sin(phi),  Math.cos(phi), -Math.cos(theta)*Math.sin(phi) );
        M[2] = new Vector( -Math.sin(theta)*Math.cos(phi), Math.sin(phi), Math.cos(theta)*Math.cos(phi)  );
    }
    
    // construct M to be a 3D clockwise rotation matrix,
    // specifying which axis to rotate about and by how much
    public Matrix( String axis, double angle ) {
        if ( axis.equals("x") ) {
            M[0] = new Vector( 1, 0,               0                );
            M[1] = new Vector( 0, Math.cos(angle), -Math.sin(angle) );
            M[2] = new Vector( 0, Math.sin(angle), Math.cos(angle)  );
        } else if ( axis.equals("y") ) {
            M[0] = new Vector( Math.cos(angle),  0, Math.sin(angle) );
            M[1] = new Vector( 0,                1, 0               );
            M[2] = new Vector( -Math.sin(angle), 0, Math.cos(angle) );
        } else if ( axis.equals("z") ) {
            M[0] = new Vector( Math.cos(angle), -Math.sin(angle), 0 );
            M[1] = new Vector( Math.sin(angle), Math.cos(angle),  0 );
            M[2] = new Vector( 0,               0,                1 );
        }
    }
    
    // FOR DEBUGGING
    public String view() {
        return "{ "+M[0].view()+", "+M[1].view()+", "+M[2].view()+" }";
    }
    // returns element M_ij
    public double elem( int i, int j ) {
        return M[i].comp(j);
    }
    
    // return row r of matrix M
    public Vector row( int r ) {
        return M[r];
    }
    
    // return column c of matrix M
    public Vector col( int c ) {
        return new Vector( M[0].comp(c), M[1].comp(c), M[2].comp(c) );
    }
    
    // multiply by scalar a
    public Matrix scale( double a ) {
        return new Matrix( M[0].scale(a), M[1].scale(a), M[2].scale(a) );
    }
    
    // apply this matrix to a vector v
    public Vector times( Vector v ) {
        // this is why we store row vectors instead of column vectors!
        return new Vector( M[0].dot(v), M[1].dot(v), M[2].dot(v) );
    }
    
    // apply this matrix to all vertices of a Triangle
    public Triangle times( Triangle T ) {
        return new Triangle( this.times( T.vertex(0) ),
                             this.times( T.vertex(1) ),
                             this.times( T.vertex(2) ),
                             T.color() );
    }

    // apply this matrix (plus a translation) to all Triangles in an array
    public Triangle[] affineTimes( Triangle[] triangleMesh, Vector trans ) {
        Triangle[] rotatedTriangleMesh = new Triangle[triangleMesh.length];
        for (int t = 0; t < triangleMesh.length; ++t) {
            rotatedTriangleMesh[t] = this.times( triangleMesh[t] ).shift( trans );
        }
        return rotatedTriangleMesh;
    }
    public Triangle[] affineTimes( Triangle[] triangleMesh ){
        return affineTimes( triangleMesh, new Vector(0,0,0) );
    }
    
    // returns the matrix product M.N
    public Matrix times( Matrix N ) {
        return new Matrix( M[0].dot(N.col(0)), M[0].dot(N.col(1)), M[0].dot(N.col(2)),
                           M[1].dot(N.col(0)), M[1].dot(N.col(1)), M[1].dot(N.col(2)),
                           M[2].dot(N.col(0)), M[2].dot(N.col(1)), M[2].dot(N.col(2)) );
    }
    
    // computes determinant of M
    public double det() {
        return M[0].dot( M[1].cross(M[2]) ); // tricky, right?
    }
    
    // computes inverse of M
    public Matrix inverse() {
        return new Matrix( elem(2,2)*elem(1,1) - elem(2,1)*elem(1,2),
                           elem(2,1)*elem(0,2) - elem(2,2)*elem(0,1),
                           elem(1,2)*elem(0,1) - elem(1,1)*elem(0,2),
                           elem(2,0)*elem(1,2) - elem(2,2)*elem(1,0),
                           elem(2,2)*elem(0,0) - elem(2,0)*elem(0,2),
                           elem(1,0)*elem(0,2) - elem(1,2)*elem(0,0),
                           elem(2,1)*elem(1,0) - elem(2,0)*elem(1,1),
                           elem(2,0)*elem(0,1) - elem(2,1)*elem(0,0),
                           elem(1,1)*elem(0,0) - elem(1,0)*elem(0,1) ).scale(1/det());
    }
}