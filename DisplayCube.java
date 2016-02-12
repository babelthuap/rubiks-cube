import squint.*;
//import javax.swing.*;
//import java.lang.*;

/**
 *  renders one of several models; most importantly, this generates
 *  an SImage of a 3D Rubik's cube given a configuration and a
 *  viewing angle (i.e. a rotation about the y-axis by angle yAngle
 *  and then the x-axis by angle xAngle)
 *  
 *  this uses the rasterization method
 *  
 * 
 *  Alec and Nicholas
 */
public class DisplayCube {
     // width and height of output image
    private final int IMAGE_SIZE = 400;

    // triangle mesh describing the model
    private Triangle[] triangleMesh;
    
    // an array of the cubies that compose a Rubik's cube
    // SET TO 9 TO INCLUDE THE CENTER CUBE, OTHERWISE SET TO 8
    private Cubie[] cubies = new Cubie[8];
    
    public DisplayCube( String whichModel ) {
        // initialize graphics data         
        model( whichModel );
    }

    // render an SImage of the current configuration of the cube
    public SImage render( double yAngle, double xAngle, boolean displayDepthBuffer,
                          int distance, double magnify ) {
        // set the focal length
        int focalLength = 4*distance/5;
        
        // integer version of magnify
        int magInt = (int)magnify;
                              
        // rotate and magnify the graphics data using matrices
        // and center at (x,y,z) = (0, 0, DISTANCE_TO_OBJECT)
        Matrix rotate = new Matrix( yAngle, xAngle );
        Vector zDist = new Vector(0, 0, distance);
        Triangle[] rotatedTriangleMesh;
        if ( magnify == 100 ) {
            rotatedTriangleMesh = rotate.affineTimes(triangleMesh, zDist);
        } else {
            Matrix scale = new Matrix( magnify/100,0,0,
                                       0,magnify/100,0,
                                       0,0,magnify/100 );
            Matrix transform = scale.times(rotate);
            rotatedTriangleMesh = transform.affineTimes(triangleMesh, zDist);
        }
        
        Vector center = new Vector(IMAGE_SIZE/2, IMAGE_SIZE/2, 0);
        
        // initialize Red, Green, and Blue output arrays and depth buffer
        int[][] outputR = new int[IMAGE_SIZE][IMAGE_SIZE];
        int[][] outputG = new int[IMAGE_SIZE][IMAGE_SIZE];
        int[][] outputB = new int[IMAGE_SIZE][IMAGE_SIZE];
        double[][] depthBuffer = new double[IMAGE_SIZE][IMAGE_SIZE];
        for (int x = 0; x < IMAGE_SIZE; ++x) {
            for (int y = 0; y < IMAGE_SIZE; ++y) {
                outputR[x][y] = 255; // background color
                outputG[x][y] = 255; // (this is white)
                outputB[x][y] = 255;
                depthBuffer[x][y] = Double.POSITIVE_INFINITY;
            }
        }
        
        // iterate over all triangles first (rasterization)
        for (Triangle T : rotatedTriangleMesh) {
            // if the triangle is facing towards the camera, draw the triangle
            if ( T.isVisible() ) {
                // project the triangle onto the virtual camera sensor
                Triangle Tp = T.project( focalLength ).shift( center );
                
                // the following are used in creating the depth buffer;
                // we calculate them up front to make the later calculations faster
                Vector A = Tp.vertex(0);
                Vector B = Tp.vertex(1);
                Vector C = Tp.vertex(2);
                Vector CA = C.minus(A);
                Vector BA = B.minus(A);
                Vector AB = A.minus(B);
                double CAxBA = CA.cross(BA).comp(2);
                double ABxCA = AB.cross(CA).comp(2);
                
                // determine the bounding box
                int xMin = min( Tp.vertex(0).comp(0), Tp.vertex(1).comp(0), Tp.vertex(2).comp(0) );
                int xMax = max( Tp.vertex(0).comp(0), Tp.vertex(1).comp(0), Tp.vertex(2).comp(0) );
                int yMin = min( Tp.vertex(0).comp(1), Tp.vertex(1).comp(1), Tp.vertex(2).comp(1) );
                int yMax = max( Tp.vertex(0).comp(1), Tp.vertex(1).comp(1), Tp.vertex(2).comp(1) );
                
                for (int x = xMin; x < xMax; ++x) {
                    for (int y = yMin; y < yMax; ++y) {
                        // this is the value for the depth buffer
                        double height = zValue( T, x, y, A, B, C, CAxBA, ABxCA );
                        
                        // update the pixel color and depthBuffer at point (x,y)
                        if ( height < depthBuffer[x][y] && Tp.contains(x,y) ) {
                            depthBuffer[x][y] = height;
                            if (displayDepthBuffer) {
                                // used to visualize the depth buffer
                                int shade = ((int)height-distance)*400/(3*magInt) - 40;
                                outputR[x][y] = shade;
                                outputG[x][y] = shade;
                                outputB[x][y] = shade;
                            } else {
                                // this is the actual coloring of the triagnles
                                int shade = ((int)height - distance)*75 / magInt - 180;
                                outputR[x][y] = Tp.color(0)*shade/256;
                                outputG[x][y] = Tp.color(1)*shade/256;
                                outputB[x][y] = Tp.color(2)*shade/256;
                            }
                        }
                    }
                }
            }
        }
       
        return new SImage( outputR, outputG, outputB );
    }
    
    // returns the minimum of 3 numbers within [0, IMAGE_SIZE]
    private int min( double x, double y, double z ) {
        return Math.max( 0, (int)Math.min( x, Math.min(y, z) ) );
    }
    
    // returns the maximum of 3 numbers within [0, IMAGE_SIZE]
    private int max( double x, double y, double z ) {
        return Math.min( IMAGE_SIZE, (int)Math.max( x, Math.max(y, z) ) );
    }
    
    // calculate the Barycentric coordinates of point P=(x,y,0) with
    // respect to vertices A, B, and C (of the projection of T)
    //     i.e. P = alpha*A + beta*B + gamma*C
    // and use these to calculate the z-value (for the depth buffer)
    private double zValue( Triangle T, int x, int y,
                           Vector A, Vector B, Vector C,
                           double CAxBA, double ABxCA ) {
        Vector P = new Vector(x,y,0);
        Vector AP = A.minus(P);
        Vector BP = B.minus(P);
        Vector CP = C.minus(P);
        
        // calculate the barycentric coordinates of P
        double alpha = CP.cross(BP).comp(2) / CAxBA;
        double beta = AP.cross(CP).comp(2) / ABxCA;
        double gamma = 1 - (alpha + beta);
        
        // calculate P_z
        return 1/( alpha/T.vertex(0).comp(2) + 
                   beta/T.vertex(1).comp(2) + 
                   gamma/T.vertex(2).comp(2) );
    }
    
    // rotate the given layer by the given angle (clockwise)
    public void rotateLayer( String layer, double angle) {
        if ( layer.equals("F") ) {
            rotateLayerGiven( 1,2,5,6, "z", angle ); // #s are which cubies to rotate
        } else if ( layer.equals("B") ) {
            rotateLayerGiven( 0,3,4,7, "z", -angle );
        } else if ( layer.equals("R") ) {
            rotateLayerGiven( 2,3,6,7, "x", -angle );
        } else if ( layer.equals("L") ) {
            rotateLayerGiven( 0,1,4,5, "x", angle );
        } else if ( layer.equals("U") ) {
            rotateLayerGiven( 4,5,6,7, "y", angle );
        } else if ( layer.equals("D") ) {
            rotateLayerGiven( 0,1,2,3, "y", -angle );
        }
    }
    
    // rotate the specified cubies about the given axis by the given angle (clockwise)
    private void rotateLayerGiven( int cubieA, int cubieB, int cubieC, int cubieD,
                                                        String axis, double angle) {
        // determine which cubies are not affected (0 means unaffected)
        int[] affected = new int[cubies.length];
        affected[cubieA] = 1;
        affected[cubieB] = 1;
        affected[cubieC] = 1;
        affected[cubieD] = 1;
        
        // the appropriate rotation matrix
        Matrix R = new Matrix( axis, angle );
        
        // a mesh of which cubies to rotate
        Triangle[] rotatedMesh = cubies[cubieA].mesh();
        rotatedMesh = concatenate( rotatedMesh, cubies[cubieB].mesh() );
        rotatedMesh = concatenate( rotatedMesh, cubies[cubieC].mesh() );
        rotatedMesh = concatenate( rotatedMesh, cubies[cubieD].mesh() );
        
        // rotate this mesh
        rotatedMesh = R.affineTimes( rotatedMesh );
        
        // update the total triangle mesh
        triangleMesh = rotatedMesh;
        for ( int i = 0; i < cubies.length; ++i ) {
            if (affected[i] == 0) {
                triangleMesh = concatenate( triangleMesh, cubies[i].mesh() );
            }
        }
    }
    
    // concatenate two triangle meshes (used in the updateColors method)
    private Triangle[] concatenate( Triangle[] mesh1, Triangle[] mesh2 ) {
        int outputLength = mesh1.length + mesh2.length;
        Triangle[] output = new Triangle[outputLength];
        
        for (int i = 0; i < mesh1.length; ++i) {
            output[i] = mesh1[i];
        }
        for (int i = mesh1.length; i < outputLength; ++i ) {
            output[i] = mesh2[i - mesh1.length];
        }
        
        return output;
    }
    
    // update the colors on the cubies given a new CubeState
    public void updateColors( CubeState state ) {
        String[][] newColors = state.colorList(); 
        for (int i = 0; i < cubies.length; ++i) {
            cubies[i] = new Cubie( i, newColors );
        }
            
        // concatenate the triangle meshes from all the cubies
        triangleMesh = cubies[0].mesh();
        for (int i = 1; i < cubies.length; ++i) {
            triangleMesh = concatenate( triangleMesh, cubies[i].mesh() );
        }
    }
    
    // this method is responsible for initializing the different models
    private void model( String which ) {
        if ( which.equals("Rubik's Cube") ) {
            // initialize all cubies to "solved" state
            updateColors( new CubeState() );
            
        } else if ( which.equals("Winged Cube") ) {
            Vector v0 = new Vector(0,0,0).plus(new Vector(-100,100,100) );
            Vector v1 = new Vector(0,0,-200).plus(new Vector(-100,100,100) );
            Vector v2 = new Vector(200,0,-200).plus(new Vector(-100,100,100) );
            Vector v3 = new Vector(200,0,0).plus(new Vector(-100,100,100) );
            Vector v4 = new Vector(0,-200,0).plus(new Vector(-100,100,100) );
            Vector v5 = new Vector(0,-200,-200).plus(new Vector(-100,100,100) );
            Vector v6 = new Vector(200,-200,-200).plus(new Vector(-100,100,100) );
            Vector v7 = new Vector(200,-200,0).plus(new Vector(-100,100,100) );
        
            triangleMesh = new Triangle[]{ new Triangle( v0, v2, v1, "W"),
                                           new Triangle( v0, v3, v2, "W"),
                                           new Triangle( v0, v1, v5, "B"),
                                           new Triangle( v0, v5, v4, "B"),
                                           new Triangle( v1, v2, v6, "R"),
                                           new Triangle( v1, v6, v5, "R"),
                                           new Triangle( v2, v3, v6, "G"),
                                           new Triangle( v3, v7, v6, "G"),
                                           new Triangle( v3, v0, v7, "P"),
                                           new Triangle( v0, v4, v7, "P"),
                                           new Triangle( v4, v5, v6, "Y"),
                                           new Triangle( v4, v6, v7, "Y"),
                                           new Triangle( v1.minus(new Vector(50,0,0) ),
                                                 v5.minus(new Vector(50,0,0) ),
                                                 v4.minus(new Vector(50,0,0) ), "K"),
                                           new Triangle( v1.minus(new Vector(50,0,0) ),
                                                 v4.minus(new Vector(50,0,0) ),
                                                 v5.minus(new Vector(50,0,0) ), "K"),
                                           new Triangle( v2.plus(new Vector(50,0,0) ),
                                                 v6.plus(new Vector(50,0,0) ),
                                                 v7.plus(new Vector(50,0,0) ), "K"),
                                           new Triangle( v2.plus(new Vector(50,0,0) ),
                                                 v7.plus(new Vector(50,0,0) ),
                                                 v6.plus(new Vector(50,0,0) ), "K")
                                           };
        } else if ( which.equals("Tetrahedron") ) {
            Vector v00 = new Vector(-120, 50, 100);
            Vector v01 = new Vector(0, -100, 100);
            Vector v02 = new Vector(75, 50, 100);
              Triangle T0 = new Triangle( v00, v01, v02, "P");
            Vector v10 = new Vector(-120, 50, 100);
            Vector v11 = new Vector(75, 50, 100);
            Vector v12 = new Vector(0, 0, -150);
              Triangle T1 = new Triangle( v10, v11, v12, "G");
            Vector v20 = new Vector(75, 50, 100);
            Vector v21 = new Vector(0, -100, 100);
            Vector v22 = new Vector(0, 0, -150);
              Triangle T2 = new Triangle( v20, v21, v22, "W");
            Vector v30 = new Vector(0, -100, 100);
            Vector v31 = new Vector(-120, 50, 100);
            Vector v32 = new Vector(0, 0, -150);
              Triangle T3 = new Triangle( v30, v31, v32, "R");
            
            triangleMesh = new Triangle[]{T0, T1, T2, T3};
        }
    }
}