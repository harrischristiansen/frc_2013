// Harris Christiansen
// Copyright Harris Christiansen 2013
// FRC Team 3245 - Waterford School
// Pre Season Practice Electronics Board
// Written Oct 29, 2012
// Updated 11-16-12
// Ported to NetBeans frc13 1-9-13

// Package
package edu.wpi.first.wpilibj.templates;
// For 4 Talon, Pneumatic Shifting Driving Train

// Imports
import edu.wpi.first.wpilibj.*;
public class Shifty extends IterativeRobot {
        // Motors
	private Talon leftMotorOne, leftMotorTwo, rightMotorOne, rightMotorTwo;
	
	// Current Motor Speeds
	private double leftSpeed, rightSpeed;
	
	// Controllers
	Joystick pilotStick;
	
	// Compressor
	Compressor mainComp;
	
	// Solenoids
	Solenoid driveLowSole;
	Solenoid driveHighSole;
	
	// Pilot Controls
	int leftStick=2, rightStick=4, fastBtn=8, slowBtn=7, lowGear=5, highGear=6; // Logitech Joy
    
    public void robotInit() {
        // PWM Motor Ports
    	leftMotorOne = new Talon(3);
    	leftMotorTwo = new Talon(4);
    	rightMotorOne = new Talon(1);
    	rightMotorTwo = new Talon(2);
    	
    	// Joysticks
    	pilotStick = new Joystick(1);
    	
    	// Zero Everything
	leftSpeed=0;rightSpeed=0;
		
	// Create Compressor Instance
	mainComp = new Compressor(1,8);
		
	// Create Solenoid Instances
	driveLowSole = new Solenoid(1,1);
	driveHighSole = new Solenoid(1,2);
    }
    public void autonomousInit() {
        
    }
    public void autonomousPeriodic() {

    }
    public void teleopInit() {
    	// Zero Everything
	leftSpeed=0;rightSpeed=0;
	mainComp.start();
    }
    public void teleopPeriodic() {
    	updateDrive();
        updateMotors();
        updateSolenoid();
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
    
    //Pneumatics System
    public void updateSolenoid() {
    	if(pilotStick.getRawButton(lowGear)) {
    		driveLowSole.set(true);
    		driveHighSole.set(false);
    	}
    	else if(pilotStick.getRawButton(highGear)) {
    		driveLowSole.set(false);
    		driveHighSole.set(true);
    	}
    	else {
    		driveLowSole.set(false);
    		driveHighSole.set(false);
    	}
    }
    
    // Drive Functions
    public void updateDrive() {
    	double drivePercent=0.55;
    	if(pilotStick.getRawButton(fastBtn)) {drivePercent=1.00;}
    	else if(pilotStick.getRawButton(slowBtn)) {drivePercent=0.3;}
    	leftSpeed=pilotStick.getRawAxis(leftStick)*drivePercent;
    	rightSpeed=pilotStick.getRawAxis(rightStick)*drivePercent;
    }
    
    // Set Motor Speeds
    public void updateMotors() {
    	leftMotorOne.set(-leftSpeed); // Motor Reversed
    	leftMotorTwo.set(-leftSpeed); // Motor Reversed
    	rightMotorOne.set(rightSpeed);
    	rightMotorTwo.set(rightSpeed);
    }
}