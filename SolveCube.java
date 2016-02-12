/**
 *  THIS IS PURE MAGIC
 * 
 *  Alec and Nicholas
 */
public class SolveCube {
    // the numbers of the faces:
    final int bottom = 0;
    final int left   = 1;
    final int front  = 2;
    final int right  = 3;
    final int back   = 4;
    final int top    = 5;
    
    // instance variables 
    String[][] panels;
    String[] finalOutput = new String[128];
    String bottomColor;
    String topColor;
    SolnState cube;
    int moveNum;

    // constructor
    public SolveCube( String[] pos, int[] ort ) {
        cube = new SolnState( pos, ort );
        for (int i = 0; i < finalOutput.length; ++i) {
            finalOutput[i] = "end" + i;
        }
        
        // exhaustively check 5 moves deep for a simple solve
        // check all orientations to determine whether to use method 1
        boolean found = false;
        String[] possible = new String[]{"F","Fi","R","Ri","U","Ui"};
        String[] soln = new String[1];
        for (int a = 0; a < 6; ++a) {
            cube.doMove( possible[a] );
            if ( cube.isSolved() ) {
                if ( ! found ) {
                    soln = new String[]{possible[a]};
                    found = true;
                }
                
            } else {
                for (int b = 0; b < 6; ++b) {
                    cube.doMove( possible[b] );
                    if ( cube.isSolved() ) {
                        if ( ! found ) {
                            soln = new String[]{possible[a],possible[b]};
                            found = true;
                        }
                    
                    } else {
                        for (int c = 0; c < 6; ++c) {
                            cube.doMove( possible[c] );
                            if ( cube.isSolved() ) {
                                if ( ! found ) {
                                    soln = new String[]{possible[a],possible[b],possible[c]};
                                    found = true;
                                }
                                
                            } else {
                                for (int d = 0; d < 6; ++d) {
                                    cube.doMove( possible[d] );
                                    if ( cube.isSolved() ) {
                                        if ( ! found ) {
                                            soln = new String[]{ possible[a],possible[b],
                                                                 possible[c],possible[d] };
                                            found = true;
                                        }
                                        
                                    } else {
                                        for (int e = 0; e < 6; ++e) {
                                            cube.doMove( possible[e] );
                                            if ( cube.isSolved() ) {
                                                if ( ! found ) {
                                                    soln = new String[]{ possible[a], possible[b],
                                                                         possible[c], possible[d],
                                                                         possible[e] };
                                                    found = true;
                                                }
                                                
                                            }
                                            cube = new SolnState( pos, ort );
                                            cube.doMove( possible[a] );
                                            cube.doMove( possible[b] );
                                            cube.doMove( possible[c] );
                                            cube.doMove( possible[d] );
                                        }
                                    }
                                    cube = new SolnState( pos, ort );
                                    cube.doMove( possible[a] );
                                    cube.doMove( possible[b] );
                                    cube.doMove( possible[c] );
                                }
                            }
                            cube = new SolnState( pos, ort );
                            cube.doMove( possible[a] );
                            cube.doMove( possible[b] );
                        }
                    }
                    cube = new SolnState( pos, ort );
                    cube.doMove( possible[a] );
                }
            }
            cube = new SolnState( pos, ort );
        }
        
        // if an easy solution was found, use that, otherwise use our robust algorithm
        if ( found ) {
            for (int i = 0; i < soln.length; ++i) {
                finalOutput[i] = soln[i];
            }
        } else {
            cube = new SolnState( pos, ort );
            moveNum = 0;
            
            solveBottom();
            orientTop();
            placeTop();
        }
    }
    
    // return the list of solving moves
    public String[] solveList() {
        // first, shorten the list if possible
        while ( posOfTriple(finalOutput) != -1 || posOfPair(finalOutput) != -1 ) {
            int triple = posOfTriple(finalOutput);
            if ( triple != -1 ) {
                // replace with inverse move
                String move = finalOutput[triple];
                String inverseMove;
                if ( move.endsWith("i") ) {
                    inverseMove = move.substring(0,1);
                } else {
                    inverseMove = move + "i";
                }
                finalOutput[triple] = inverseMove;

                for (int i = (triple+1); i+2 < finalOutput.length; ++i) {
                    finalOutput[i] = finalOutput[i+2];
                }
            }

            int pair = posOfPair(finalOutput);
            if ( pair != -1 ) {
                // delete both
                for (int i = pair; i+2 < finalOutput.length; ++i) {
                    finalOutput[i] = finalOutput[i+2];
                }
            }
        }

        return finalOutput;
    }

    // find the position of the first set of 3 identical
    // consecutive moves in a String[]
    private int posOfTriple( String[] list ) {
        if ( list.length >= 3 ) {
            for (int i = 0; i+2 < list.length; ++i) {
                if ( list[i].equals(list[i+1]) && list[i+1].equals(list[i+2]) ) {
                    return i;
                }
            }
        }

        return -1; // return -1 if not found
    }

    // find the position of the first adjacent pair of mutually
    // inverse moves in a String[]
    private int posOfPair( String[] list ) {
        if ( list.length >= 2 ) {
            for (int i = 0; i+1 < list.length; ++i) {
                String move = list[i];
                String inverseMove;
                if ( move.endsWith("i") ) {
                    inverseMove = move.substring(0,1);
                } else {
                    inverseMove = move + "i";
                }

                if ( inverseMove.equals(list[i+1]) ) {
                    return i;
                }
            }
        }

        return -1; // return -1 if not found
    }

    private void solveBottom() {
        panels = cube.panelColors();
        bottomColor = panels[5][1];
        topColor = oppositeColor( bottomColor );

        /** GET SECOND CUBIE IN TOP LAYER
         *  (FIRST IS FREE, BY DEFINITION)
         */
        // figure out where next cubie is
        String[] nextCubieStrings = new String[]{ bottomColor, panels[3][1],
                oppositeColor(panels[4][0]) };
        int posOfNext = locateCubie( nextCubieStrings );

        if ( posOfNext != 6  ||  ! panels[5][2].equals(bottomColor) ) {
            // move next cubie to position 2
            if ( posOfNext == 4 ) {
                Li();
            } else if ( posOfNext == 5 ) {
                Fi();
            } else if ( posOfNext == 6 ) {
                F();
            }
            panels = cube.panelColors(); // update panel colors
            posOfNext = locateCubie( nextCubieStrings ); // update "next"'s position
            while (posOfNext != 2) {
                D(); // rotate bottom layer
                panels = cube.panelColors(); // update panel colors
                posOfNext = locateCubie( nextCubieStrings ); // update "next"'s position
            }

            // place "next" cubie
            if ( panels[2][2].equals(bottomColor) ) {
                // front
                F();  D();  Fi();
            } else if ( panels[0][1].equals(bottomColor) ) {
                // bottom
                Di();  F();  F();
            } else if ( panels[3][3].equals(bottomColor) ) {
                // right
                Fi();
            }
        }

        if ( ! cube.isSolved() ) {
            Ui();
            panels = cube.panelColors(); // update panel colors

            /** GET THIRD CUBIE IN TOP LAYER
             */
            // figure out where next cubie is
            nextCubieStrings = new String[]{ bottomColor, panels[3][1],
                oppositeColor(panels[4][0]) };
            posOfNext = locateCubie( nextCubieStrings );

            if ( posOfNext != 6  ||  ! panels[5][2].equals(bottomColor) ) {
                // move next cubie to position 2
                if ( posOfNext == 5 ) {
                    Fi();
                } else if ( posOfNext == 6 ) {
                    F();
                }
                panels = cube.panelColors(); // update panel colors
                posOfNext = locateCubie( nextCubieStrings ); // update "next"'s position
                while (posOfNext != 2) {
                    D(); // rotate bottom layer
                    panels = cube.panelColors(); // update panel colors
                    posOfNext = locateCubie( nextCubieStrings ); // update "next"'s position
                }

                // place "next" cubie
                if ( panels[2][2].equals(bottomColor) ) {
                    // front
                    F();  D();  Fi();
                } else if ( panels[0][1].equals(bottomColor) ) {
                    // bottom
                    Di();  F();  F();
                } else if ( panels[3][3].equals(bottomColor) ) {
                    // right
                    Fi();
                }
            }

            if ( ! cube.isSolved() ) {
                Ui();
                panels = cube.panelColors(); // update panel colors

                /** GET FOURTH (AND FINAL) CUBIE IN TOP LAYER
                 */
                // figure out where next cubie is
                nextCubieStrings = new String[]{ bottomColor, panels[3][1],
                    oppositeColor(panels[4][0]) };
                posOfNext = locateCubie( nextCubieStrings );

                if ( posOfNext != 6  ||  ! panels[5][2].equals(bottomColor) ) {
                    // move next cubie to position 2
                    if ( posOfNext == 6 ) {
                        F();  D();  Fi();
                    }
                    panels = cube.panelColors(); // update panel colors
                    posOfNext = locateCubie( nextCubieStrings ); // update "next"'s position
                    while (posOfNext != 2) {
                        D(); // rotate bottom layer
                        panels = cube.panelColors(); // update panel colors
                        posOfNext = locateCubie( nextCubieStrings ); // update "next"'s position
                    }

                    // place "next" cubie
                    if ( panels[2][2].equals(bottomColor) ) {
                        // front
                        F();  D();  Fi();
                    } else if ( panels[0][1].equals(bottomColor) ) {
                        // bottom
                        F();  Di();  Fi();  Ri();  D();  D();  R();
                    } else if ( panels[3][3].equals(bottomColor) ) {
                        // right
                        Ri();  Di();  R();
                    }
                }
            }
        }
    }

    private void orientTop() {
        // check if cube is solved
        if ( ! cube.isSolved() ) {
            // flip cube
            Li(); Li(); R(); R();
            // update panel colors
            panels = cube.panelColors();
            
            // determine how many top cubies are oriented (can be 0, 1, 2, or 4)
            int numOriented = 0;
            for (int i = 0; i <= 3; ++i) {
                if ( panels[top][i].equals( topColor ) ) {
                    ++numOriented;
                }
            }
            
            // if 4 are oriented, we don't need to do anything
            if ( numOriented == 0) {
                // get two topColors facing front
                while ( ! ( panels[front][0].equals( topColor ) 
                            && panels[front][1].equals( topColor )
                             ) ) {
                    U(); // rotate bottom layer
                    panels = cube.panelColors(); // update panel colors
                }
                
                // check if [right][1] = topColor
                if ( panels[right][1].equals( topColor ) ) {
                    U();
                    Ri(); U(); R(); R(); Ui(); R(); R(); Ui(); R(); R(); U(); Ri();
                    panels = cube.panelColors();
                } else {
                    R(); R(); U(); U(); Ri(); U(); U(); R(); R();
                    panels = cube.panelColors();
                }
                
            } else if ( numOriented == 1) {
                // move oriented cubie into slot 5
                while ( ! panels[top][3].equals( topColor ) ) {
                    U(); // rotate bottom layer
                    panels = cube.panelColors(); // update panel colors
                }
                
                // we may have to do this sequence twice
                if ( panels[front][1].equals( topColor ) ) {
                    R(); U(); Ri(); U(); R(); U(); U(); Ri();
                    panels = cube.panelColors();
                } else {
                    R(); U(); Ri(); U(); R(); U(); U(); Ri();
                    U(); U();
                    R(); U(); Ri(); U(); R(); U(); U(); Ri();
                    panels = cube.panelColors();
                }
                
            } else if ( numOriented == 2) {
                // first, check for diagonal
                if ( (panels[top][0].equals(topColor) && panels[top][2].equals(topColor))
                     || (panels[top][1].equals(topColor) && panels[top][3].equals(topColor)) ) {
                    // rotate until we have L0, T3, and F1 all topColor
                    while ( ! (panels[left][0].equals( topColor )
                               && panels[top][3].equals( topColor )
                               && panels[front][1].equals( topColor )) ) {
                        U(); // rotate bottom layer
                        panels = cube.panelColors(); // update panel colors
                    }
                    
                    // fix cubies
                    R(); Ui(); Ri(); Ui(); Fi(); U(); F();
                    
                } else { // otherwise, two topColors are adjacent on the top
                    // rotate until we have both T2 and T3 topColor
                    while ( ! (panels[top][2].equals( topColor )
                               && panels[top][3].equals( topColor )) ) {
                        U(); // rotate bottom layer
                        panels = cube.panelColors(); // update panel colors
                    }
                    
                    // do one of two fixing moves
                    if ( panels[left][0].equals(topColor) ) {
                        R(); U(); Ri(); Ui(); Fi(); Ui(); F();
                        panels = cube.panelColors();
                    } else {
                        R(); U(); B(); Ui(); Bi(); Ri();
                        panels = cube.panelColors();
                    }
                    
                }
                
            }
        }
    }
    
    private void placeTop() {
        // update panel colors
        panels = cube.panelColors();

        // determine how many pairs are solved (only 0, 1, or 4 are possible)
        int numPairs = 0;
        for (int face = 1; face <= 4; ++face) {
            if ( panels[face][0].equals( panels[face][1] ) ) {
                ++numPairs;
            }
        }

        // if one pair is solved, use Mihai's method
        if ( numPairs == 1 ) {
            // rotate so that the pair is on the back face
            while ( !( panels[back][0].equals( panels[back][1] ))) {
                U();
                panels = cube.panelColors();
            }
            // switch the 2 top-front cubies
            Ri(); F(); Ri(); B(); B(); R(); Fi(); Ri(); B(); B(); R(); R();
            panels = cube.panelColors();
            
        } else if ( numPairs == 0 ) {
            // otherwise, use methods from http://rubikscube.info/waterman/stage2.php
            String frontColor = panels[front][3];
            String backColor  = oppositeColor( frontColor );
            String leftColor  = panels[left][2];
            String rightColor = oppositeColor( leftColor );
            boolean method1 = false;
            
            // check all orientations to determine whether to use method 1
            for (int i = 0; i <= 3; ++i) {
                if ( panels[left][0].equals(frontColor) && panels[left][1].equals(backColor)
                     && panels[front][0].equals(leftColor) && panels[front][1].equals(frontColor) ) {
                    method1 = true;
                } else {
                    U(); // don't rotate if method 1 applies to current configuration
                    panels = cube.panelColors();
                    frontColor = panels[front][3];
                    backColor  = oppositeColor( frontColor );
                    leftColor  = panels[left][2];
                    rightColor = oppositeColor( leftColor );
                }
            }
            
            if ( method1 ) {
                R(); R(); B(); B(); R(); F(); Ri(); B(); B(); R(); Fi(); R();
                panels = cube.panelColors();
            } else { // use method 2
                while ( !( panels[left][0].equals(rightColor) && panels[left][1].equals(leftColor)
                           && panels[front][0].equals(frontColor) && panels[front][1].equals(backColor) )) {
                    U();
                    panels = cube.panelColors();
                }
                
                L(); L(); U(); Bi(); U(); B(); Ui(); L(); L(); Ui(); Bi(); Ui(); B();
                panels = cube.panelColors();
            }
            
        }
        
        // we should now be able to rotate top until solved
        while ( ! cube.isSolved() ) {
            Ui(); // rotate top layer
            panels = cube.panelColors(); // update panel colors
        }
    }
    
    // locate a specified cubie
    private int locateCubie( String[] f ) {
        // first output something like "WBR"
        String cubeName = "";
        int cubeNumber = -1;

        if ( f[0].equals("W") || f[1].equals("W") || f[2].equals("W") ) {
            cubeName = "W";

            int whichOne;
            if ( f[0].equals("W") ) {
                whichOne = 0;
            } else if ( f[1].equals("W") ) {
                whichOne = 1;
            } else { // if ( f[2].equals("W") )
                whichOne = 2;
            }

            String other1 = f[(whichOne + 1)%3];
            String other2 = f[(whichOne + 2)%3];

            if ( other1.equals("B") ) {
                if ( other2.equals("R") ) {
                    cubeName = "WBR";
                } else if ( other2.equals("P") ) {
                    cubeName = "WPB";
                }
            } else if ( other1.equals("R") ) {
                if ( other2.equals("G") ) {
                    cubeName = "WRG";
                } else if ( other2.equals("B") ) {
                    cubeName = "WBR";
                }
            } else if ( other1.equals("G") ) {
                if ( other2.equals("R") ) {
                    cubeName = "WRG";
                } else if ( other2.equals("P") ) {
                    cubeName = "WGP";
                }
            } else if ( other1.equals("P") ) {
                if ( other2.equals("G") ) {
                    cubeName = "WGP";
                } else if ( other2.equals("B") ) {
                    cubeName = "WPB";
                }
            } 

        } else { // one face is yellow
            cubeName = "Y";

            int whichOne = -1;
            if ( f[0].equals("Y") ) {
                whichOne = 0;
            } else if ( f[1].equals("Y") ) {
                whichOne = 1;
            } else if ( f[2].equals("Y") ) {
                whichOne = 2;
            }

            String other1 = f[(whichOne + 1)%3];
            String other2 = f[(whichOne + 2)%3];

            if ( other1.equals("B") ) {
                if ( other2.equals("R") ) {
                    cubeName = "YRB";
                } else if ( other2.equals("P") ) {
                    cubeName = "YBP";
                }
            } else if ( other1.equals("R") ) {
                if ( other2.equals("G") ) {
                    cubeName = "YGR";
                } else if ( other2.equals("B") ) {
                    cubeName = "YRB";
                }
            } else if ( other1.equals("G") ) {
                if ( other2.equals("R") ) {
                    cubeName = "YGR";
                } else if ( other2.equals("P") ) {
                    cubeName = "YPG";
                }
            } else if ( other1.equals("P") ) {
                if ( other2.equals("G") ) {
                    cubeName = "YPG";
                } else if ( other2.equals("B") ) {
                    cubeName = "YBP";
                }
            } 

        }

        // then locate this in cube.pos()
        String[] pos = cube.pos();
        for (int i = 0; i < 8; ++i) {
            if ( pos[i].equals(cubeName) ) {
                cubeNumber = i;
            }
        }

        return cubeNumber;
    }

    // determine opposite color
    private String oppositeColor( String color ) {
        if ( color.equals("W") ) {
            return "Y";
        } else if ( color.equals("B") ) {
            return "G";
        } else if ( color.equals("R") ) {
            return "P";
        } else if ( color.equals("G") ) {
            return "B";
        } else if ( color.equals("P") ) {
            return "R";
        } else { // if ( color.equals("Y")
            return "W";
        }
    }

    // determine if an int is in an array
    private boolean elementOf( int x, int[] S ) {
        for (int i = 0; i < S.length; ++i) {
            if ( x == S[i] ) {
                return true;
            }
        }

        return false;
    }

    // append to finalOutput
    private void addMove( String move ) {
        finalOutput[moveNum] = move;
        ++moveNum;
    }

    // F, B, R, L, U, D  & inverses
    private void F() { cube.doMove("F"); addMove("F"); }
    private void Fi() { cube.doMove("Fi"); addMove("Fi"); }
    private void B() { cube.doMove("B"); addMove("B"); }
    private void Bi() { cube.doMove("Bi"); addMove("Bi"); }
    private void R() { cube.doMove("R"); addMove("R"); }
    private void Ri() { cube.doMove("Ri"); addMove("Ri"); }
    private void L() { cube.doMove("L"); addMove("L"); }
    private void Li() { cube.doMove("Li"); addMove("Li"); }
    private void U() { cube.doMove("U"); addMove("U"); }
    private void Ui() { cube.doMove("Ui"); addMove("Ui"); }
    private void D() { cube.doMove("D"); addMove("D"); }
    private void Di() { cube.doMove("Di"); addMove("Di"); }

}