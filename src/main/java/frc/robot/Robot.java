/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.cscore.UsbCamera;
import edu.wpi.cscore.VideoSource;
import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.vision.VisionThread;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;


/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the TimedRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.gradle file in the
 * project.
 * 
 * @param <MyFindTapePipeline>
 */
// anisha says hi
public class Robot<MyFindTapePipeline> extends TimedRobot {

  public Command m_autonomousCommand;
  public RobotContainer robotContainer;
    
  public VideoSource usbCamera;
    
  // A vision pipeline. This could be handwritten or generated by GRIP.
  // This has to implement VisionPipeline.
  public MyFindTapePipeline findTapePipeline;
  public VisionThread findTapeThread;

  // The object to synchronize on to make sure the vision thread doesn't
  // write to variables the main thread is using.
  public final Object visionLock = new Object();

  // The pipeline outputs we want
  public boolean pipelineRan = false; // lets us know when the pipeline has actually run
  public double angleToTape = 0;
  public double distanceToTape = 0;

  /*this was suppose to copy the pipeline values and store them
  in separate variables, but I got rid of them because VS code
  didn't like it, and we don't really need it anyway.
  currently all it does is set pipelineRan to true
  */
  public void copyPipelineOutputs(MyFindTapePipeline pipeline) {
      synchronized (visionLock) {
          //confirms if the pipeline successfully ran
          this.pipelineRan = true;
      }
  }


  @Override
  public void robotInit() {
  
    robotContainer = new RobotContainer();
    //takes a picture with the camera
    UsbCamera camera = CameraServer.getInstance().startAutomaticCapture();
    //sets resolution of camera
    camera.setResolution(640, 480);
  }

  /**
   * This function is called every robot packet, no matter the mode. Use this for items like
   * diagnostics that you want ran during disabled, autonomous, teleoperated and test.
   *
   * <p>This runs after the mode specific periodic functions, but before
   * LiveWindow and SmartDashboard integrated updating.
   */
  @Override
  public void robotPeriodic() {
    
    CommandScheduler.getInstance().run();

    /**
     * place any SmartDashboard methods that should be running even when the robot is disabled here
     */

    //SmartDashboard.putNumber("relative rotations", robotContainer.drive.backRightModule.getRelativeAngleEncoder());
    //SmartDashboard.putNumberArray("abs + rel", robotContainer.drive.backRightModule.getAbsoluteAndRelativeAngleEncoderPositions());
  }

  /**
   * This function is called once each time the robot enters Disabled mode.
   */
  @Override
  public void disabledInit() {
  }

  @Override
  public void disabledPeriodic() {
  }

  /**
   * This autonomous runs the autonomous command selected by your {@link RobotContainer} class.
   */
  @Override
  public void autonomousInit() {
    m_autonomousCommand = robotContainer.getAutonomousCommand();

    // schedule the autonomous command (example)
    if (m_autonomousCommand != null) {
      m_autonomousCommand.schedule();
    }
  }


  
  double distanceToWall;
  /**
   * This function is called periodically during autonomous.
   * 
   */
  @Override 
  public void autonomousPeriodic() {

    //cosine of the angle to tape
    double angleCos;

    //constantly updates distance to wall
    synchronized (visionLock) {
      //if the pipeline hasn't been confirmed to run, it won't run.
      if (pipelineRan) {
        /*if the pipeline ran, it'll get the values for angle
          and distance, and then do math and find the distance
          from the camera to the wall */
        double y = this.angleToTape;
        double x = this.distanceToTape;
        y = Math.toRadians(y);

        angleCos = Math.cos(y);
        distanceToWall = angleCos * x;

      } else {
        System.out.println("Pipeline hasn't run yet, cannot find distance!");
      }
    }
  }

  //output for distanceToWall
  public double getDistanceToWall() {
    return distanceToWall;
  }


  @Override
  public void teleopInit() {
    
    if (m_autonomousCommand != null) {
      m_autonomousCommand.cancel();
    }

  }

  /**
   * This function is called periodically during operator control.
   */
  @Override
  public void teleopPeriodic() {
    /**
     * DO NOT PLACE SMARTDASHBOARD DIAGNOSTICS HERE
     * Place any teleop-only SmartDashboard diagnostics in the appropriate subsystem's periodic() method
     */
  }

  @Override
  public void testInit() {
    // Cancels all running commands at the start of test mode.
    CommandScheduler.getInstance().cancelAll();
  }

  /**
   * This function is called periodically during test mode.
   */
  @Override
  public void testPeriodic() {
  }
}
