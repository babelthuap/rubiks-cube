import java.util.Random;

/**
 *  - randomly generates a list of moves to scramble the cube
 *  - inverts this list (to generate the "cheating" solution)
 *  - computes the configuration of the cube produced by this list of moves
 * 
 *  Alec and Nicholas
 */
public class ScrambleCube {
    // instance variables
    private String[] list;
    private CubeState scrambledCube = new CubeState();
    
    // generate a null scrambling list
    public ScrambleCube() {
        list = new String[]{"empty"};
    }
    
    // generate a list of move with its length specified
    public ScrambleCube( int numMoves ) {
        String[] possibleMoves = new String[]{"F","B","R","L","U","D",
                                              "Fi","Bi","Ri","Li","Ui","Di"};
        Random randomGenerator = new Random();
        
        // generate random series of moves
        list = new String[numMoves];
        int[] numList = new int[numMoves]; // list of the move numbers
        numList[0] = randomGenerator.nextInt( possibleMoves.length );
        list[0] = possibleMoves[ numList[0] ];
        scrambledCube.doMove( list[0] );
        for (int i = 1; i < numMoves; ++i) {
            // randomly generate a valid move number
            int nextMove = randomGenerator.nextInt( possibleMoves.length );
            while ( ! isValidMove( nextMove, numList, i ) ) {
                nextMove = randomGenerator.nextInt( possibleMoves.length );
            }
            
            // add this valid move number to the list of move numbers
            numList[i] = nextMove;
            
            // update the list of move names and perform this move on the cube
            list[i] = possibleMoves[nextMove];
            scrambledCube.doMove( list[i] );
            
            // if the cube is solved, start over
            if ( scrambledCube.isSolved() ) {
                i = 1;
                scrambledCube = new CubeState();
                scrambledCube.doMove( list[ numList[0] ] );
            }
        }
    }

    // determine whether a given move is valid (used above)
    private boolean isValidMove( int moveNum, int[] numList, int i ) {
        // we don't want two consecutive moves to be inverse of one another
        if ( moveNum == ( numList[i-1] + 6)%12 ) {
            return false;
        }
        
        // we don't want to just rotate the cube
        // i.e. L and Ri consecutively, or Ui and D
        // So... if even, add 7; if odd, add 5
        int bad = ( moveNum + 6 + (int)Math.pow(-1,moveNum) )%12;
        if ( bad == numList[i-1] ) {
            return false;
        }
        
        // we don't want 3 moves in a row to be the same
        if ( i >= 2 ) {
            if ( moveNum == numList[i-1] && moveNum == numList[i-2] ) {
                return false;
            }
        }
        
        return true;
    }
    
    // return the list of scrambling moves
    public String[] movesList() {
        return list;
    }
    
    // add a move onto the list
    public void addToList( String move ) {
        // increase list length by 1...
        int oldLength = list.length;
        String[] newList = new String[ oldLength + 1 ];
        for (int i = 0; i < oldLength; ++i ) {
            newList[i] = list[i];
        }
        newList[ oldLength ] = move;
        
        list = newList;
        
        // if 3 moves in a row are the same, replace with one inverse move
        int i = list.length - 1;
        if ( i >= 2 ) {
            if ( list[i].equals(list[i-1]) && list[i-1].equals(list[i-2]) ) {

                if ( move.endsWith("i") ) {
                    move = move.substring(0,1);
                } else {
                    move = move + "i";
                }
                list[i-2] = move;
                
                newList = new String[ i-1 ];
                for (int j = 0; j < i-1; ++j ) {
                    newList[j] = list[j];
                }
        
                list = newList;
            }
        }
        
        // if the new move is the invese of the previous move, delete both
        i = list.length;
        if ( i >= 2 ) {
            move = list[i-1];
            String inverseMove;
            if ( move.endsWith("i") ) {
                inverseMove = move.substring(0,1);
            } else {
                inverseMove = move + "i";
            }
            
            if ( inverseMove.equals(list[i-2]) ) {
                newList = new String[ i-2 ];
                for (int j = 0; j < i-2; ++j ) {
                    newList[j] = list[j];
                }
        
                list = newList;
            }
        }
    }
    
    // invert the list of moves
    public String[] solveList() {
        int numMoves = list.length;
        
        String[] reverse = new String[numMoves];
        for (int i = 0; i < numMoves; ++i) {
            // switch clockwise with counterclockwise & vice versa
            String move = list[numMoves - 1 - i];
            if ( move.endsWith("i") ) {
                move = move.substring(0,1);
            } else {
                move = move + "i";
            }
            
            reverse[i] = move;
        }
        
        return reverse;
    }
        
    // return the CubeState produced by the initial scrambling
    public CubeState scrambled() {
        return scrambledCube;
    }
}