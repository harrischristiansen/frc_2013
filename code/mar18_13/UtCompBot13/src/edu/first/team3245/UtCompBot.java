// Harris Christiansen
// FRC Team 3245 - Waterford School
// 2013 Competition Bot - Utah Regional
// Written Feb 5, 2013
// Updated 2-5-13

// Package
package edu.first.team3245;

// Imports
import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.camera.*;
import InsightLT.InsightLT;
import InsightLT.StringData;
import InsightLT.IntegerData;
import InsightLT.DecimalData;
public class UtCompBot extends IterativeRobot {
	// Shooter Speed
	double shtSetSpeed=0.85;
	// Pilot Controls
	int leftStick=2, rightStick=4, fastBtn=8, slowBtn=7, lowGearBtn=5, highGearBtn=6, revDrvBtn=4, shtBtn=3, shtPistonBtn=1;
	int coAutoShtBtn=8, coShtBtn=6, coShtPistonBtn=5, coShtSpeedInc=3, coShtSpeedDec=1, togCompBtn=10;
	boolean twoSticks=false;
	
        // Motors
	private Talon leftMotor, rightMotor, shtMot1, shtMot2, shtMot3;
	
	// Current Motor Speeds
	private double leftSpeed, rightSpeed, shtSpeed;
	
	// Controllers
	Joystick pilotStick, coPilotStick;
	
	// Compressor
	Compressor mainComp;
	
	// Solenoids
	Solenoid driveLowSole, driveHighSole, shtOutSole, shtInSole;
        
        // Drive Direction // true=normal, false=reversed
        boolean driveDirection=true;
	
	// Sensors
	//AxisCamera camera;
	//Encoder shooterEncoder;
	////InsightLT LTDisp = new InsightLT(InsightLT.TWO_ONE_LINE_ZONES);
	////DecimalData LTrowOne = new DecimalData("Batt:");
	////IntegerData LTrowTwo = new IntegerData("Team Number:");
	
	// Auto State
	int autoState=1;
    
    public void robotInit() {
        // PWM Motor Ports
    	rightMotor = new Talon(2);
    	leftMotor = new Talon(1);
	shtMot1 = new Talon(5);
	shtMot2 = new Talon(6);
	shtMot3 = new Talon(7);
        
    	
    	// Joysticks
    	pilotStick = new Joystick(1);
    	coPilotStick = new Joystick(2);
		
	// Create Compressor Instance
	mainComp = new Compressor(14,1);
		
	// Create Solenoid Instances
	driveLowSole = new Solenoid(1,3);
	driveHighSole = new Solenoid(1,4);
	shtOutSole = new Solenoid(1,1);
	shtInSole = new Solenoid(1,2);
	
	// Sensors
	//shooterEncoder=new Encoder(1,2);
	/*
	LTDisp.startDisplay();
	LTDisp.registerData(LTrowOne, 1);
	LTDisp.registerData(LTrowTwo, 2);
	LTrowTwo.setData(3245);
	*/
    	
    	zeroMotorSpeeds();
    }
    public void autonomousInit() {
    	zeroMotorSpeeds();
        // Start Compressor
	mainComp.start();
    }
    public void autonomousPeriodic() {
	//updateLTDisp();
	if(autoState==1) {updateShootAuto();}
	updateMotors();
    }
    public void teleopInit() {
    	zeroMotorSpeeds();
	autoPeriodCount=0;
        // Start Compressor
	mainComp.start();
    }
    public void teleopPeriodic() {
    	updateDrive();
        updateDriveShifter();
        updateReverseDrive();
	updateShooterPiston();
	updateToggleComp();
	updateShooter();
	//updateShooterAuto();
	updateShooterSpeed();
        updateMotors();
	//updateLTDisp();
    }
    public void disabledInit() {
    	zeroMotorSpeeds();
        
    }
    public void disabledPeriodic() {
        if(DriverStation.getInstance().getDigitalIn(1)) { autoState=1; }
        if(DriverStation.getInstance().getDigitalIn(2)) { autoState=2; }
        if(DriverStation.getInstance().getDigitalIn(3)) { autoState=3; }
        if(DriverStation.getInstance().getDigitalIn(4)) { autoState=4; }
	if(DriverStation.getInstance().getDigitalIn(5)) { twoSticks=true; }
	//updateLTDisp();
    }
    public void testInit() {
        
    }
    public void testPeriodic() {
    
    } 
    
    /////////// CUSTOM FUNCTIONS /////////////
    
    // Zero Motor Speeds
    public void zeroMotorSpeeds() {
        leftSpeed=0;
        rightSpeed=0;
	shtSpeed=0;
    }
   
    // Pneumatics Drive Train Shifting
    public void updateDriveShifter() {
    	if(pilotStick.getRawButton(lowGearBtn)) {
    		driveLowSole.set(true);
    		driveHighSole.set(false);
    	} else if(pilotStick.getRawButton(highGearBtn)) {
    		driveLowSole.set(false);
    		driveHighSole.set(true);
    	} else {
    		driveLowSole.set(false);
    		driveHighSole.set(false);
    	}
    }
    
    // Tank Drive
    public void updateDrive() {
    	double drivePercent=0.55;
    	if(pilotStick.getRawButton(fastBtn)) {drivePercent=1.00;}
    	else if(pilotStick.getRawButton(slowBtn)) {drivePercent=0.3;}
        if(driveDirection) { // Normal Drive Direction
            leftSpeed=pilotStick.getRawAxis(leftStick)*drivePercent;
            rightSpeed=pilotStick.getRawAxis(rightStick)*drivePercent;
        } else { // Reversed Drive Direction
            rightSpeed=-(pilotStick.getRawAxis(leftStick)*drivePercent);
            leftSpeed=-(pilotStick.getRawAxis(rightStick)*drivePercent);
        }
    }
    
    // Reverse Robot Drive Direction
    private boolean driveChanged=false;
    public void updateReverseDrive() {
    	if(pilotStick.getRawButton(revDrvBtn)&&!driveChanged) {driveChanged=true; driveDirection=!driveDirection;}
    	if(!pilotStick.getRawButton(revDrvBtn)){driveChanged=false;}
    }
    public int shtPisCount=0;
    public void updateShooterPiston() {
	if(coPilotStick.getRawButton(coShtPistonBtn)) {
	    if(shtPisCount<15) {
		shtPisCount++;
		shtInSole.set(false);
		shtOutSole.set(true);
	    } else {
		shtInSole.set(false);
		shtOutSole.set(false);
	    }
	} else {
	    if(shtPisCount>0) {
		shtInSole.set(true);
		shtOutSole.set(false);
		shtPisCount--;
	    } else {
		shtInSole.set(false);
		shtOutSole.set(false);
	    }
	}
    }
    public void updateShooter() {
	if(coPilotStick.getRawButton(coShtBtn)) {
	    shtSpeed=shtSetSpeed;
	} else {
	    shtSpeed=0.0;
	}
    }
    int shtCount=0;
    public void updateShooterAuto() {
	if(pilotStick.getRawButton(shtBtn)||coPilotStick.getRawButton(coAutoShtBtn)) {
	    shtSpeed=shtSetSpeed;
	    shtCount++;
	    if(shtCount>150&&shtCount<165) {
		shtInSole.set(false);
		shtOutSole.set(true);
	    } else if(shtCount>165&&shtCount<180) {
		shtInSole.set(true);
		shtOutSole.set(false);
	    } else if(shtCount==180) {
		shtInSole.set(false);
		shtOutSole.set(false);
		shtCount=0;
	    }
	} else {
	    shtSpeed=0.0;
	    shtCount=0;
	    shtInSole.set(false);
	    shtOutSole.set(false);
	}
    }
    public boolean shtSpeedUpdated=false;
    public void updateShooterSpeed() {
	if(coPilotStick.getRawButton(coShtSpeedInc)) {
	    if(!shtSpeedUpdated) {
		shtSetSpeed=shtSetSpeed+0.05;
		System.out.println("Shooter Speed: "+shtSetSpeed);
		shtSpeedUpdated=true;
	    }
	}
	else if(coPilotStick.getRawButton(coShtSpeedDec)) {
	    if(!shtSpeedUpdated) {
		shtSetSpeed=shtSetSpeed-0.05;
		System.out.println("Shooter Speed: "+shtSetSpeed);
		shtSpeedUpdated=true;
	    }
	}
	else { shtSpeedUpdated=false; }
	
    }
    boolean togCompSwitched=false;
    public void updateToggleComp() {
	if(coPilotStick.getRawButton(togCompBtn)) {
	    if(!togCompSwitched) {
		togCompSwitched=true;
		if(mainComp.enabled()) {
		    mainComp.stop();
		} else {
		    mainComp.start();
		}
	    }
	} else {
	    togCompSwitched=false;
	}
    }
    
    // Set Motor Speeds
    public void updateMotors() {
    	leftMotor.set(-leftSpeed*.85); // Motor Reversed
    	rightMotor.set(rightSpeed);
	shtMot1.set(-shtSpeed); // Motor Reversed
	shtMot2.set(-shtSpeed*.75); // Motor Reversed
	shtMot3.set(-shtSpeed); // Motor Reversed
    }
    /*
    public void updateLTDisp() {
	LTrowOne.setData(DriverStation.getInstance().getBatteryVoltage());
    }
    * */
    
    
    
    
    //// Autonomous Routines
    
    
    public int autoPeriodCount=0;
    public void updateShootAuto() {
	if(autoPeriodCount<500) {
	    shtSpeed=0.925;
	} else {shtSpeed=0;}
	if((autoPeriodCount>150&&autoPeriodCount<165)||(autoPeriodCount>300&&autoPeriodCount<315)||(autoPeriodCount>450&&autoPeriodCount<465)) {
	    shtInSole.set(false);
	    shtOutSole.set(true);
	}
	else if((autoPeriodCount>165&&autoPeriodCount<180)||(autoPeriodCount>315&&autoPeriodCount<330)||(autoPeriodCount>465&&autoPeriodCount<480)) {
	    shtInSole.set(true);
	    shtOutSole.set(false);
	}
	else {
	    shtInSole.set(false);
	    shtOutSole.set(false);
	}
	autoPeriodCount++;
    }
}