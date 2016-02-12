/**
 *  stores a configuration of the Rubik's cube with the following conventions:
 *  
 *  Solved state: (block, orientation)
position 0 = ( WBR, 0 ) - fixed
position 1 = ( WRG, 0 )
position 2 = ( WGP, 0 )
position 3 = ( WPB, 0 )
position 4 = ( YRB, 0 )
position 5 = ( YGR, 0 )
position 6 = ( YPG, 0 )
position 7 = ( YBP, 0 )
 *  
 *  colors are listed in clockise order (when looking towards center of cube)
 *  
 *  and orientation determines which face is facing vertically
 *                               (either upwards or downwards)
 *                               
 *  THE CONFIGURATION IS STORED LIKE THIS FOR EASE OF MANIPULATION; FOR
 *  DISPLAYING PURPOSES, HOWEVER, THE MAJORITY OF THE CODE HEREIN TRANSLATES
 *  THE ABOVE INFORMATION INTO INFORMATION ABOUT THE COLORS OF THE INDIVIDUAL
 *  FACES OF EACH CUBIE
 *  
 * 
 *  Alec and Nicholas
 */
public class CubeState {
    // arrays to hold the positions and orientations of the cubies
    private String[] pos;
    private int[] ort;

    public CubeState() {
        // initialize a solved cube
        pos = new String[]{ "WBR", "WRG", "WGP", "WPB",
                            "YRB", "YGR", "YPG", "YBP" };
        ort = new int[8];
    }

    // return the positions of the cubies
    public String[] pos() {
        return pos;
    }
    
    // return the orientations of the cubies
    public int[] ort() {
        return ort;
    }
    
    // this calls one of the moves (defined below) given its name
    // note that Fi = F^3, etc., so the inverses are easy
    public void doMove( String which ) {
        if ( which.equals("F") ) {
            F();
        } else if ( which.equals("Fi") ) {
            F(); F(); F();
        } else if ( which.equals("B") ) {
            B();
        } else if ( which.equals("Bi") ) {
            B(); B(); B();
        } else if ( which.equals("R") ) {
            R();
        } else if ( which.equals("Ri") ) {
            R(); R(); R();
        } else if ( which.equals("L") ) {
            L();
        } else if ( which.equals("Li") ) {
            L(); L(); L();
        } else if ( which.equals("U") ) {
            U();
        } else if ( which.equals("Ui") ) {
            U(); U(); U();
        } else if ( which.equals("D") ) {
            D();
        } else if ( which.equals("Di") ) {
            D(); D(); D();
        }
    }
    
    // determine whether the cube is in its solved state
    public boolean isSolved() {
        // if one of the faces is scrambled, return false
        for (int i = 0; i < 6; ++i) {
            if ( ! oneFaceSolved(i) ) {
                return false;
            }
        }
        
        // otherwise, the cube is solved
        return true;
    }
    // determine whether one face is solved (i.e. all one color)
    private boolean oneFaceSolved( int which ) {
        String[][] faceColors = colorList();
        
        // which four cubies make up each face
        // if ( which == 0 )
            int c0 = 0;  int c1 = 1;  int c2 = 2;  int c3 = 3;
        if ( which == 1 ) {
             c0 = 0;   c1 = 1;   c2 = 4;   c3 = 5;
        } else if ( which == 2 ) {
             c0 = 1;   c1 = 2;   c2 = 5;   c3 = 6;
        } else if ( which == 3 ) {
             c0 = 2;   c1 = 3;   c2 = 6;   c3 = 7;
        } else if ( which == 4 ) {
             c0 = 0;   c1 = 3;   c2 = 4;   c3 = 7;
        } else if ( which == 5 ) {
             c0 = 4;   c1 = 5;   c2 = 6;   c3 = 7;
        }
        
        return faceColors[c0][which].equals( faceColors[c1][which] ) &&
               faceColors[c1][which].equals( faceColors[c2][which] ) &&
               faceColors[c2][which].equals( faceColors[c3][which] ) ;
    }
    
    // first dimension specifies a face, 2nd dim. specifies a panel
    // (clockwise from upper left)
    public String[][] panelColors() {
        String[][] panels = new String[6][4];
        String[][] colors = this.colorList();
        
        // face 0
        panels[0][0] = colors[1][0];
        panels[0][1] = colors[2][0];
        panels[0][2] = colors[3][0];
        panels[0][3] = colors[0][0];
        
        // face 1
        panels[1][0] = colors[4][1];
        panels[1][1] = colors[5][1];
        panels[1][2] = colors[1][1];
        panels[1][3] = colors[0][1];
        
        // face 2
        panels[2][0] = colors[5][2];
        panels[2][1] = colors[6][2];
        panels[2][2] = colors[2][2];
        panels[2][3] = colors[1][2];
        
        // face 3
        panels[3][0] = colors[6][3];
        panels[3][1] = colors[7][3];
        panels[3][2] = colors[3][3];
        panels[3][3] = colors[2][3];
        
        // face 4
        panels[4][0] = colors[7][4];
        panels[4][1] = colors[4][4];
        panels[4][2] = colors[0][4];
        panels[4][3] = colors[3][4];
        
        // face 5
        panels[5][0] = colors[4][5];
        panels[5][1] = colors[7][5];
        panels[5][2] = colors[6][5];
        panels[5][3] = colors[5][5];
        
        return panels;
    }
    
    /* METHODS TO ROTATE THE DIFFERENT LAYERS
     * possible moves are F, B, R, L, U, D and their inverses
     *                       (default direction is clockwise)
     */
   
    // rotate front layer
    private void F() {
        String[] newPos = new String[8];
        int[] newOrt = new int[8];
        
        // 0,3,4,7 stay in place
        newPos[0] = pos[0];  newOrt[0] = ort[0];
        newPos[3] = pos[3];  newOrt[3] = ort[3];
        newPos[4] = pos[4];  newOrt[4] = ort[4];
        newPos[7] = pos[7];  newOrt[7] = ort[7];
        
        // rotate others: 1->5, 5->6, 6->2, 2->1
        newPos[5] = pos[1];  newOrt[5] = (ort[1] + 1)%3;
        newPos[6] = pos[5];  newOrt[6] = (ort[5] + 2)%3;
        newPos[2] = pos[6];  newOrt[2] = (ort[6] + 1)%3;
        newPos[1] = pos[2];  newOrt[1] = (ort[2] + 2)%3;
        
        pos = newPos;
        ort = newOrt;
    }
    // rotate back layer
    private void B() {
        String[] newPos = new String[8];
        int[] newOrt = new int[8];
        
        // 1,2,5,6 stay in place
        newPos[1] = pos[1];  newOrt[1] = ort[1];
        newPos[2] = pos[2];  newOrt[2] = ort[2];
        newPos[5] = pos[5];  newOrt[5] = ort[5];
        newPos[6] = pos[6];  newOrt[6] = ort[6];
        
        // rotate others: 7->4, 4->0, 0->3, 3->7
        newPos[4] = pos[7];  newOrt[4] = (ort[7] + 2)%3;
        newPos[0] = pos[4];  newOrt[0] = (ort[4] + 1)%3;
        newPos[3] = pos[0];  newOrt[3] = (ort[0] + 2)%3;
        newPos[7] = pos[3];  newOrt[7] = (ort[3] + 1)%3;
        
        pos = newPos;
        ort = newOrt;
    }
    // rotate right layer
    private void R() {
        String[] newPos = new String[8];
        int[] newOrt = new int[8];
        
        // 0,1,4,5 stay in place
        newPos[0] = pos[0];  newOrt[0] = ort[0];
        newPos[1] = pos[1];  newOrt[1] = ort[1];
        newPos[4] = pos[4];  newOrt[4] = ort[4];
        newPos[5] = pos[5];  newOrt[5] = ort[5];
        
        // rotate others: 2->6, 6->7, 7->3, 3->2
        newPos[6] = pos[2];  newOrt[6] = (ort[2] + 1)%3;
        newPos[7] = pos[6];  newOrt[7] = (ort[6] + 2)%3;
        newPos[3] = pos[7];  newOrt[3] = (ort[7] + 1)%3;
        newPos[2] = pos[3];  newOrt[2] = (ort[3] + 2)%3;
        
        pos = newPos;
        ort = newOrt;
    }
    // rotate left layer
    private void L() {
        String[] newPos = new String[8];
        int[] newOrt = new int[8];
        
        // 2,3,6,7 stay in place
        newPos[2] = pos[2];  newOrt[2] = ort[2];
        newPos[3] = pos[3];  newOrt[3] = ort[3];
        newPos[6] = pos[6];  newOrt[6] = ort[6];
        newPos[7] = pos[7];  newOrt[7] = ort[7];
        
        // rotate others: 4->5, 5->1, 1->0, 0->4
        newPos[5] = pos[4];  newOrt[5] = (ort[4] + 2)%3;
        newPos[1] = pos[5];  newOrt[1] = (ort[5] + 1)%3;
        newPos[0] = pos[1];  newOrt[0] = (ort[1] + 2)%3;
        newPos[4] = pos[0];  newOrt[4] = (ort[0] + 1)%3;
        
        pos = newPos;
        ort = newOrt;
    }
    // rotate top layer ("up")
    private void U() {
        String[] newPos = new String[8];
        int[] newOrt = new int[8];
        
        // cubies 0, 1, 2, and 3 stay in place
        for (int i = 0; i < 4; ++i) {
            newPos[i] = pos[i];  newOrt[i] = ort[i];
        }
        
        // rotate others: 4->7, 7->6, 6->5, 5->4
        // note that their orientations don't change
        newPos[7] = pos[4];  newOrt[7] = ort[4];
        newPos[6] = pos[7];  newOrt[6] = ort[7];
        newPos[5] = pos[6];  newOrt[5] = ort[6];
        newPos[4] = pos[5];  newOrt[4] = ort[5];
        
        pos = newPos;
        ort = newOrt;
    }
    // rotate bottom layer ("down")
    private void D() {
        String[] newPos = new String[8];
        int[] newOrt = new int[8];
        
        // cubies 4, 5, 6, and 7 stay in place
        for (int i = 4; i < 8; ++i) {
            newPos[i] = pos[i];  newOrt[i] = ort[i];
        }
        
        // rotate others: 0->1, 1->2, 2->3, 3->0
        // note that their orientations don't change
        newPos[1] = pos[0];  newOrt[1] = ort[0];
        newPos[2] = pos[1];  newOrt[2] = ort[1];
        newPos[3] = pos[2];  newOrt[3] = ort[2];
        newPos[0] = pos[3];  newOrt[0] = ort[3];
        
        pos = newPos;
        ort = newOrt;
    }
    // inverses F, B, R, L, U, D
    public void Fi() { F(); F(); F(); }
    public void Bi() { B(); B(); B(); }
    public void Ri() { R(); R(); R(); }
    public void Li() { L(); L(); L(); }
    public void Ui() { U(); U(); U(); }
    public void Di() { D(); D(); D(); }
    
    /* The colorList method returns a 2-dimensional String array
     * describing, in detail, the configuration of the cube. This is
     * directly used in modeling the cube (in the Cubie class).
     * 
     * The output is an 9x6 array.  The first dimension specifies a
     * cubie, and the second dimension specifies a face of a given cubie.
     * For this we need CONVENTIONS!
     * 
     * The cubies are numbered as follows:
     *    bottom layer:  top layer:
     *        0 3           4 7         (  back-left, back-right  )
     *        1 2           5 6         ( front-left, front-right )
     * (the ninth "cubie" is the center cube)
     * 
     * The faces of each cubie are numbered as follows:
     *              +----------+
     *             /   top    /|
     *            /     5    / | <-- back=4
     *           +----------+ R|
     *           |          | 3|
     * left=1 -->|  front   |  +
     *           |    2     | /
     *           |          |/ <-- bottom=0
     *           +----------+
     */
    public String[][] colorList() {
        String[][] colors = new String[9][6];
        
        // over half of the entries will be black, so we initialize all
        // entries as black
        for (int cubie=0; cubie<9; ++cubie) {
            for (int face=0; face<6; ++face) {
                colors[cubie][face] = "K"; // "K" stands for black
            }
        }
        
        // THE REST IS JUST TEDIOUS BOOKKEEPING...
        
        // cubie 0 (bottom back-left)
        String[] c0 = colorTriple( pos[0], ort[0], 0 );
        colors[0][0] = c0[0]; // bottom
        colors[0][4] = c0[1]; // back
        colors[0][1] = c0[2]; // left
        
        // cubie 1 (bottom front-left)
        String[] c1 = colorTriple( pos[1], ort[1], 1 );
        colors[1][0] = c1[0];
        colors[1][1] = c1[1];
        colors[1][2] = c1[2]; 
        
        // cubie 2 (bottom front-right)
        String[] c2 = colorTriple( pos[2], ort[2], 2 );
        colors[2][0] = c2[0];
        colors[2][2] = c2[1];
        colors[2][3] = c2[2];
        
        // cubie 3 (bottom back-right)
        String[] c3 = colorTriple( pos[3], ort[3], 3 );
        colors[3][0] = c3[0];
        colors[3][3] = c3[1];
        colors[3][4] = c3[2];
        
        // cubie 4 (top back-left)
        String[] c4 = colorTriple( pos[4], ort[4], 4 );
        colors[4][5] = c4[0];
        colors[4][1] = c4[1];
        colors[4][4] = c4[2];
        
        // cubie 5 (top front-left)
        String[] c5 = colorTriple( pos[5], ort[5], 5 );
        colors[5][5] = c5[0];
        colors[5][2] = c5[1];
        colors[5][1] = c5[2];
        
        // cubie 6 (top front-right)
        String[] c6 = colorTriple( pos[6], ort[6], 6 );
        colors[6][5] = c6[0];
        colors[6][3] = c6[1];
        colors[6][2] = c6[2];
        
        // cubie 7 (top back-right)
        String[] c7 = colorTriple( pos[7], ort[7], 7 );
        colors[7][5] = c7[0];
        colors[7][4] = c7[1];
        colors[7][3] = c7[2];
        
        return colors;
    }
    
    /* takes a string of three letters, and separates them into an
     * array of color names, shifted right by the value of "orient"
     *      e.g. (WBR, 0) --> {"W", "B", "R"}
     *           (WBR, 1) --> {"B", "R", "W"}
     *           (WBR, 2) --> {"R", "W", "B"}
     */
    private String[] colorTriple( String input, int orient, int cNum ) {
        return new String[]{ input.charAt( orient ) + "",
                             input.charAt( (orient+1)%3 ) + "",
                             input.charAt( (orient+2)%3 ) + "" };
    }
}