// Harris Christiansen
// FRC Team 3245 - Waterford School
// Pre Season Practice Electronics Board
// Written Jan 17, 3013
// Updated 2-16-13

// Package
package edu.wpi.first.wpilibj.templates;
// For 4 Talon Driving Train, 1 Talon Bottom Feeder

// Imports
import edu.wpi.first.wpilibj.*;
public class Kitbot extends IterativeRobot {
        // Motors
	private Talon leftMotorOne, leftMotorTwo, rightMotorOne, rightMotorTwo, floorFdMotor;
	
	// Current Motor Speeds
	private double leftSpeed, rightSpeed, floorFdSpeed;
	
	// Controllers
	Joystick pilotStick;
	
	// Pilot Controls
	int leftStick=2, rightStick=4, fastBtn=6, slowBtn=5, floorFdBtn=8, revDrvBtn=12;
        
        // Drive Direction // true=normal, false=reversed
        boolean driveDirection=true;
	
    
    public void robotInit() {
        // PWM Motor Ports
    	leftMotorOne = new Talon(3);
    	leftMotorTwo = new Talon(4);
    	rightMotorOne = new Talon(1);
    	rightMotorTwo = new Talon(2);
        floorFdMotor = new Talon(5);
    	
    	// Joysticks
    	pilotStick = new Joystick(1);
    	
    	// Zero Everything
	leftSpeed=0;rightSpeed=0;floorFdSpeed=0;
    }
    public void autonomousInit() {
        
    }
    public void autonomousPeriodic() {

    }
    public void teleopInit() {
    	// Zero Everything
	leftSpeed=0;rightSpeed=0;floorFdSpeed=0;
    }
    public void teleopPeriodic() {
        updateReverseDrive();
    	updateDrive();
        updateFeeders();
        updateMotors();
    }
    public void disabledInit() {
        
    }
    public void disabledPeriodic() {
        
    }
    public void testInit() {
        
    }
    public void testPeriodic() {
    
    } 
    
    /////////// CUSTOM FUNCTIONS /////////////
    
    // Drive Functions
    public void updateDrive() {
    	double drivePercent=0.55;
    	if(pilotStick.getRawButton(fastBtn)) {drivePercent=1.00;}
    	else if(pilotStick.getRawButton(slowBtn)) {drivePercent=0.3;}
        if(driveDirection) {
            leftSpeed=pilotStick.getRawAxis(leftStick)*drivePercent;
            rightSpeed=pilotStick.getRawAxis(rightStick)*drivePercent;
        } else {
            rightSpeed=-(pilotStick.getRawAxis(leftStick)*drivePercent);
            leftSpeed=-(pilotStick.getRawAxis(rightStick)*drivePercent);
        }
    }
    
    public void updateFeeders() {
        if(pilotStick.getRawButton(floorFdBtn)) {floorFdSpeed=1.00;}
        else {floorFdSpeed=0.0;}
    }
    
    // Set Motor Speeds
    public void updateMotors() {
    	leftMotorOne.set(-leftSpeed); // Motor Reversed
    	leftMotorTwo.set(-leftSpeed); // Motor Reversed
    	rightMotorOne.set(rightSpeed);
    	rightMotorTwo.set(rightSpeed);
        floorFdMotor.set(floorFdSpeed);
    }
    private boolean driveChanged=false;
    public void updateReverseDrive() {
    	if(pilotStick.getRawButton(revDrvBtn)&&!driveChanged&&driveDirection) {driveChanged=true; driveDirection=false;}
    	else if(pilotStick.getRawButton(revDrvBtn)&&!driveChanged&&!driveDirection) {driveChanged=true; driveDirection=true;}
    	if(!pilotStick.getRawButton(revDrvBtn)){driveChanged=false;}
    }
}