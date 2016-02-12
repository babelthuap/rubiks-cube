import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import squint.*;
import javax.swing.*;

/**
 *  Class RubiksCubeUI - a simple window to hold our Rubik's Cube solver
 * 
 *  Alec and Nicholas
 *  2011
 */
public class RubiksCubeUI extends GUIManager {
    private final int WINDOW_WIDTH = 600, WINDOW_HEIGHT = 750;
    private final int NUM_MOVES = 256; // how much to scramble the cube
    private final int FRAMES_PER_ANIM = 8; // (16-this) = # of steps per animation
                                           // (at 100% magnification)
    private final int MAX_FRAMES = 20;
    private final int PAUSE = 0; // ms to pause between frames of an animation
    private final int SOLVE_DELAY = 50; // ms to pause, while a solution is in
                                         // progress, between checking if the
                                         // current animation is done

    // used in animations
    private Timer animTimer;
    private Timer solnTimer;
    private int step; // step number - couldn't figure out how to make this local
    private int solnStep;
    private int solnCountDown;
    private boolean animInProgress = false;
    private boolean solveInProgress = false;
    private JLabel counter = new JLabel( "", SwingConstants.LEFT );
    
    // used to display a rendered model
    private JLabel display = new JLabel( "", SwingConstants.CENTER );
    
    // used to scramble the cube
    private ScrambleCube scrambler = new ScrambleCube();
    
    // sliders to change the cube's orientation
    // rotate about the x-axis or y-axis
    private JSlider xTiltAngle = new JSlider(JSlider.VERTICAL, -180, 180, -30);
    private JSlider yTiltAngle = new JSlider(-180, 180, 25);

    // "scramble" and "solve" buttons
    private JButton scramble = new JButton( "Scramble!  (" + NUM_MOVES + " moves)" );
    private JButton solve =    new JButton( "Solve" );
    private JButton toggle =   new JButton( "Toggle Colors/DepthBuffer" );
    private boolean displayDepthBuffer = false;
    
    // slider to adjust FRAMES_PER_ANIM
    private JSlider frames =  new JSlider(4, 12, FRAMES_PER_ANIM);
    
    // sliders to adjust the distance to object & focal length
    private JSlider distance = new JSlider(250, 2500, 1200);
    private JSlider magnify =  new JSlider(25, 150, 100);
    private JLabel distLabel = new JLabel("distance = 1200");
    private JLabel magLabel =  new JLabel("magnify: 100%");
    
    // menu box to select which model to view
    private JComboBox modelMenu;
    private DisplayCube displayCube = new DisplayCube( "Rubik's Cube" );
    private DisplayCube displayWingedCube = new DisplayCube( "Winged Cube" );
    private DisplayCube displayTetrahedron = new DisplayCube( "Tetrahedron" );
    private String model = "Rubik's Cube";
    
    // buttons to rotate the cube
    private JButton resetButton = new JButton( "Reset" );
    private JButton rotateF =     new JButton( "Front" );
    private JButton rotateFi =    new JButton( "Front'" );
    private JButton rotateB =     new JButton( "Back" );
    private JButton rotateBi =    new JButton( "Back'" );
    private JButton rotateR =     new JButton( "Right" );
    private JButton rotateRi =    new JButton( "Right'" );
    private JButton rotateL =     new JButton( "Left" );
    private JButton rotateLi =    new JButton( "Left'" );
    private JButton rotateU =     new JButton( "Up" );
    private JButton rotateUi =    new JButton( "Up'" );
    private JButton rotateD =     new JButton( "Down" );
    private JButton rotateDi =    new JButton( "Down'" );
    
    // the current configuration of the cube
    private CubeState cube = new CubeState();

    // constructor - place all GUI components in a window
    public RubiksCubeUI() {
        // create the GUI window
        this.createWindow( WINDOW_WIDTH, WINDOW_HEIGHT );
        this.setTitle( "Itty-Bitty Rubik’s Cube" );

        // switch to a border layout manager
        contentPane.setLayout( new BorderLayout() );
        contentPane.add( new JScrollPane( display ), BorderLayout.CENTER );
        
        // vertical slider
        JPanel vSliderPanel = new JPanel();
        vSliderPanel.setLayout( new GridLayout(2, 1) );
        vSliderPanel.add( new JLabel("") );
        vSliderPanel.add( xTiltAngle );
        contentPane.add(vSliderPanel, BorderLayout.WEST);

        // create model-choosing menu (added to UI later)
        modelMenu = new JComboBox();
        modelMenu.addItem( "Rubik's Cube" );
        modelMenu.addItem( "Winged Cube" );
        modelMenu.addItem( "Tetrahedron" );
        JPanel menuPane = new JPanel();
        menuPane.setLayout( new GridLayout(1, 2) );
        menuPane.add( new JLabel("choose a model: ", SwingConstants.RIGHT) );
        menuPane.add( modelMenu );
        
        // buttons, azimuthal slider, etc.
        JPanel bottomPane = new JPanel();
        bottomPane.setLayout( new GridLayout(7, 2) );
        bottomPane.add( yTiltAngle );
        bottomPane.add( scramble );
        JPanel speedPane = new JPanel();
          speedPane.setLayout( new GridLayout(1, 2) );
          speedPane.add( new JLabel("speed:", SwingConstants.RIGHT) );
          speedPane.add( frames );
          bottomPane.add( speedPane );
        //bottomPane.add( new JLabel("Alec/Nicholas, 2011", SwingConstants.CENTER) );
        JPanel solveResetPane = new JPanel(); // solve and rest buttons
          solveResetPane.setLayout( new GridLayout(1, 2) );
          solveResetPane.add( solve );
          solveResetPane.add( resetButton );
          bottomPane.add( solveResetPane );
        bottomPane.add( new JLabel("") );
        bottomPane.add( counter );
        bottomPane.add( toggle );
        bottomPane.add( menuPane );
        JPanel fPane = new JPanel(); // magnification control
          fPane.setLayout( new GridLayout(1, 2) );
          fPane.add( magnify );
          fPane.add( magLabel );
          bottomPane.add( fPane );
        JPanel dPane = new JPanel(); // distance control
          dPane.setLayout( new GridLayout(1, 2) );
          dPane.add( distance );
          dPane.add( distLabel );
          bottomPane.add( dPane );
        JPanel rotatePane1 = new JPanel(); // rotation buttons F, B, and R
          rotatePane1.setLayout( new GridLayout(1, 3) );
          rotatePane1.add( rotateF );
          rotatePane1.add( rotateB );
          rotatePane1.add( rotateR );
          bottomPane.add( rotatePane1 );
        JPanel rotatePane2 = new JPanel(); // rotation buttons L, U, and D
          rotatePane2.setLayout( new GridLayout(1, 3) );
          rotatePane2.add( rotateL );
          rotatePane2.add( rotateU );
          rotatePane2.add( rotateD );
          bottomPane.add( rotatePane2 );
        JPanel rotatePane3 = new JPanel(); // rotation buttons Fi, Bi, and Ri
          rotatePane3.setLayout( new GridLayout(1, 3) );
          rotatePane3.add( rotateFi );
          rotatePane3.add( rotateBi );
          rotatePane3.add( rotateRi );
          bottomPane.add( rotatePane3 );
        JPanel rotatePane4 = new JPanel(); // rotation buttons Li, Ui, and Di
          rotatePane4.setLayout( new GridLayout(1, 3) );
          rotatePane4.add( rotateLi );
          rotatePane4.add( rotateUi );
          rotatePane4.add( rotateDi );
          bottomPane.add( rotatePane4 );
        contentPane.add( bottomPane, BorderLayout.SOUTH );
        solve.setEnabled( false );
        
        // initialize the cube display using the DisplayCube class
        display.setIcon( displayCube.render( -25 * Math.PI / 180.0,
                                              30 * Math.PI / 180.0, false,
                                              1200, 100 ) );
    }

    // handle a button being clicked
    public void buttonClicked( JButton which ) {
        if (which == toggle) {
            if (displayDepthBuffer) {
                displayDepthBuffer = false;
                sliderChanged();
            } else {
                displayDepthBuffer = true;
                sliderChanged();
            }
        } else if (which == scramble) {
            // scramble the cube (using the ScrambleCube class)
            scrambler = new ScrambleCube( NUM_MOVES );
            cube = scrambler.scrambled();
            
            // update the display
            displayCube.updateColors( cube );
            sliderChanged();
            solve.setEnabled( true );
        } else if (which == solve) {
            allControlsOn(false);
            solve.setEnabled(false);
            solveInProgress = true;
            
            // solve the cube
            // THIS IS THE MAGIC PART
            
            // generate a list of solving moves
            SolveCube solver = new SolveCube( cube.pos(), cube.ort() );
            final String[] solveMoves = solver.solveList();
            
            // determine how long solution is
            solnCountDown = 0;
            while ( ! solveMoves[solnCountDown].startsWith("end") ) {
                ++solnCountDown;
            }
            final int initSolnCountDown = solnCountDown;
            counter.setText("steps left: " + (solnCountDown+1) + " (of " + initSolnCountDown + ")");
            
            
            solnStep = 0;
            
            ActionListener solnActionListener = new ActionListener() {
                public void actionPerformed(ActionEvent ae) {
                    if ( ! animInProgress && ! solveMoves[solnStep].equals("end") ) {
                        animateMove( solveMoves[solnStep] );
                        ++solnStep;
                        // update counter
                        --solnCountDown;
                        counter.setText( "steps left: " + (solnCountDown+1)
                                         + " (of " + initSolnCountDown + ")" );
                    }
                    
                    if ( solveMoves[solnStep].startsWith("end") ) {
                        solnTimer.stop(); // stop when done
                        scrambler = new ScrambleCube(); // reset scrambler
                        allControlsOn(true); // reenable all buttons
                        counter.setText("");
                        solveInProgress = false;
                    }
                }
            };
            
            // start timer!
            solnTimer = new Timer( SOLVE_DELAY, solnActionListener);
            solnTimer.setInitialDelay(0);
            solnTimer.start();
            
        } else if (which == resetButton) {
            // reset the cube
            solve.setEnabled( false );
            cube = new CubeState(); // reset cubestate
            displayCube.updateColors( cube );
            scrambler = new ScrambleCube(); // reset scrambler
            model = "Rubik's Cube";
            allControlsOn(true);
            sliderChanged();
        } else if (! animInProgress && ! solveInProgress) {
            if (which == rotateF) {
                scrambler.addToList("F");  animateMove("F");
            } else if (which == rotateFi) {
                scrambler.addToList("Fi");  animateMove("Fi");
            } else if (which == rotateB) {
                scrambler.addToList("B");  animateMove("B");
            } else if (which == rotateBi) {
                scrambler.addToList("Bi");  animateMove("Bi");
            } else if (which == rotateR) {
                scrambler.addToList("R");  animateMove("R");
            } else if (which == rotateRi) {
                scrambler.addToList("Ri");  animateMove("Ri");
            } else if (which == rotateL) {
                scrambler.addToList("L");  animateMove("L");
            } else if (which == rotateLi) {
                scrambler.addToList("Li");  animateMove("Li");
            } else if (which == rotateU) {
                scrambler.addToList("U");  animateMove("U");
            } else if (which == rotateUi) {
                scrambler.addToList("Ui");  animateMove("Ui");
            } else if (which == rotateD) {
                scrambler.addToList("D");  animateMove("D");
            } else if (which == rotateDi) {
                scrambler.addToList("Di");  animateMove("Di");
            }
        }
    }
    
    // animate the specified move
    private void animateMove( final String move ) {
        animInProgress = true;
        final int STEPS = Math.min( (16-frames.getValue()) * 100 / magnify.getValue(), MAX_FRAMES)
                          *2;
        step = 1;
        
        ActionListener animActionListener = new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                if ( move.endsWith("i") ) {
                    // chop off "i"
                    displayCube.rotateLayer(move.substring(0,1), -step*Math.PI / (2*STEPS) );
                } else {
                    displayCube.rotateLayer(move, step*Math.PI / (2*STEPS) );
                }
                
                sliderChanged();
                ++step;
                if (step > STEPS) {
                    animTimer.stop(); // stop when done
                    animInProgress = false;
                    
                    cube.doMove( move );
                    // disable the "solve" button if the cube is solved
                    if ( cube.isSolved() ) {
                        solve.setEnabled( false );
                        scrambler = new ScrambleCube(); // reset scrambler
                    } else {
                        if (! solveInProgress) {
                            solve.setEnabled( true );
                        }
                    }
                    displayCube.updateColors( cube );
                }
            }
        };
        
        animTimer = new Timer(PAUSE, animActionListener);
        animTimer.setInitialDelay(0);
        animTimer.start();
    }
    
    // display the selected model
    public void menuItemSelected( ) {
        model = modelMenu.getSelectedItem().toString();
        if ( model.equals("Rubik's Cube") ) {
            scramble.setEnabled( true );
        } else{
            scramble.setEnabled( false );
            solve.setEnabled( false );
        }
        
        sliderChanged();
    }
    
    // rotate the cube when the slider is moved
    public void sliderChanged() {
        // slider values in radians
        double yAngle = yTiltAngle.getValue() * Math.PI / 180.0;
        double xAngle = xTiltAngle.getValue() * Math.PI / 180.0;
        
        // determine which model to display
        if ( model.equals("Winged Cube") ) {
            display.setIcon( displayWingedCube.render( -yAngle, -xAngle,
                                                       displayDepthBuffer,
                                                       distance.getValue(),
                                                       magnify.getValue()    ) );
        } else if ( model.equals("Tetrahedron") ) {
            display.setIcon( displayTetrahedron.render( -yAngle, -xAngle,
                                                        displayDepthBuffer,
                                                        distance.getValue(),
                                                        magnify.getValue()    ) );
        } else if ( model.equals("Rubik's Cube") ) {
            display.setIcon( displayCube.render( -yAngle, -xAngle,
                                                 displayDepthBuffer,
                                                 distance.getValue(),
                                                 magnify.getValue()    ) );
        } 
        
        distLabel.setText("distance = " + distance.getValue() );
        magLabel.setText("magnify: " + magnify.getValue() + "%" );
    }
    
    // enable or diable all controls (except "solve")
    public void allControlsOn( boolean on ) {
        modelMenu.setEnabled( on );
        scramble.setEnabled( on );
        toggle.setEnabled( on );
        resetButton.setEnabled( on );
        rotateF.setEnabled( on );
        rotateFi.setEnabled( on );
        rotateB.setEnabled( on );
        rotateBi.setEnabled( on );
        rotateR.setEnabled( on );
        rotateRi.setEnabled( on );
        rotateL.setEnabled( on );
        rotateLi.setEnabled( on );
        rotateU.setEnabled( on );
        rotateUi.setEnabled( on );
        rotateD.setEnabled( on );
        rotateDi.setEnabled( on );
    }
    
    // allow program to be shared
    public static void main( String[] args ) {
        new RubiksCubeUI();
    }
}