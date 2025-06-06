package org.firstinspires.ftc.teamcode;


import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;

import java.util.Locale;

@Autonomous(name = "FeedMeBaby")
//@Disabled

public class Feed extends LinearOpMode {

    DcMotor leftFrontDrive;
    DcMotor rightFrontDrive;
    DcMotor leftBackDrive;
    DcMotor rightBackDrive;
    DcMotor armMotor;
    DcMotor slideMotor;
    CRServo servoBucket;
    CRServo servoPinceR;
    CRServo servoPinceL;

    GoBildaPinpointDriver odo; // Declare OpMode member for the Odometry Computer
    DriveToPoint nav = new DriveToPoint(this); //OpMode member for the point-to-point navigation class

    private static final int POSITION_1 = -850; // Preset position 1 (encoder counts)
    private static final int POSITION_2 = -3000;

    enum StateMachine {
        WAITING_FOR_START,
        AT_TARGET,
        DRIVE_TO_TARGET_1,
        DRIVE_TO_TARGET_1_1,
        DRIVE_TO_TARGET_2,
        DRIVE_TO_TARGET_2_1,
        DRIVE_TO_TARGET_3,
        DRIVE_TO_TARGET_3_1,
        DRIVE_TO_TARGET_4,
        DRIVE_TO_TARGET_4_1,
        DRIVE_TO_TARGET_5,
        DRIVE_TO_TARGET_5_1,
        DRIVE_TO_TARGET_6,
        DRIVE_TO_TARGET_6_1,
        DRIVE_TO_TARGET_7,
        DRIVE_TO_TARGET_7_1,
        DRIVE_TO_TARGET_8,
        DRIVE_TO_TARGET_9,
        DRIVE_TO_TARGET_10,
        DRIVE_TO_TARGET_11,
        DRIVE_TO_TARGET_12
    }

    static final Pose2D TARGET_1 = new Pose2D(DistanceUnit.MM, 0, 0, AngleUnit.DEGREES, 0);
    static final Pose2D TARGET_1_1 = new Pose2D(DistanceUnit.MM, 0, 0, AngleUnit.DEGREES, 0);
    static final Pose2D TARGET_2 = new Pose2D(DistanceUnit.MM, 0, 0, AngleUnit.DEGREES, 0);
    static final Pose2D TARGET_2_1 = TARGET_1_1;
    static final Pose2D TARGET_3 = new Pose2D(DistanceUnit.MM, 0, 0, AngleUnit.DEGREES, 0);
    static final Pose2D TARGET_3_1 = TARGET_1_1;
    static final Pose2D TARGET_4 = new Pose2D(DistanceUnit.MM, 0, 0, AngleUnit.DEGREES, 0);
    static final Pose2D TARGET_4_1 = TARGET_1_1;
    static final Pose2D TARGET_5 = new Pose2D(DistanceUnit.MM, 0, 0, AngleUnit.DEGREES, 0);
    static final Pose2D TARGET_5_1 = TARGET_1_1;
    static final Pose2D TARGET_6 = new Pose2D(DistanceUnit.MM, 0, 0, AngleUnit.DEGREES, 0);
    static final Pose2D TARGET_6_1 = TARGET_1_1;
    static final Pose2D TARGET_7 = new Pose2D(DistanceUnit.MM, 0, 0, AngleUnit.DEGREES, 0);
    static final Pose2D TARGET_7_1 = TARGET_1_1;
    static final Pose2D TARGET_8 = new Pose2D(DistanceUnit.MM, 0, 0, AngleUnit.DEGREES, 0);
    static final Pose2D TARGET_9 = new Pose2D(DistanceUnit.MM, 0, 0, AngleUnit.DEGREES, 0);
    static final Pose2D TARGET_10 = new Pose2D(DistanceUnit.MM, 0, 0, AngleUnit.DEGREES, 0);
    static final Pose2D TARGET_11 = new Pose2D(DistanceUnit.MM, 0, 0, AngleUnit.DEGREES, 0);
    static final Pose2D TARGET_12 = new Pose2D(DistanceUnit.MM, 0, 0, AngleUnit.DEGREES, 0);

// feed 6 block to the basket robot
    @Override
    public void runOpMode() {
        leftFrontDrive = hardwareMap.dcMotor.get("fl");
        leftBackDrive = hardwareMap.dcMotor.get("bl");
        rightFrontDrive = hardwareMap.dcMotor.get("fr");
        rightBackDrive = hardwareMap.dcMotor.get("br");
        armMotor = hardwareMap.dcMotor.get("arm");
        slideMotor = hardwareMap.get(DcMotor.class, "ele");
        servoBucket = hardwareMap.get(CRServo.class, "servoBucket");
        servoPinceR = hardwareMap.get(CRServo.class, "servoPinceR");
        servoPinceL = hardwareMap.get(CRServo.class, "servoPinceL");

        leftFrontDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightFrontDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        leftBackDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightBackDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        slideMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        armMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        armMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        slideMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        slideMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        rightFrontDrive.setDirection(DcMotorSimple.Direction.REVERSE);
        rightBackDrive.setDirection(DcMotorSimple.Direction.REVERSE);

        odo = hardwareMap.get(GoBildaPinpointDriver.class, "odo");
        odo.setOffsets(80.0, -55); //these are tuned for 3110-0002-0001 Product Insight #1
        odo.setEncoderResolution(GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_4_BAR_POD);
        odo.setEncoderDirections(GoBildaPinpointDriver.EncoderDirection.FORWARD, GoBildaPinpointDriver.EncoderDirection.REVERSED);

        odo.resetPosAndIMU();
        nav.setDriveType(DriveToPoint.DriveType.MECANUM);

        StateMachine stateMachine;
        stateMachine = StateMachine.WAITING_FOR_START;

        telemetry.addData("Status", "Initialized");
        telemetry.addData("X offset", odo.getXOffset());
        telemetry.addData("Y offset", odo.getYOffset());
        telemetry.addData("Device Version Number:", odo.getDeviceVersion());
        telemetry.addData("Device Scalar", odo.getYawScalar());
        telemetry.update();

        // Wait for the game to start (driver presses START)
        waitForStart();
        resetRuntime();
        servoPinceL.setPower(0);
        servoPinceR.setPower(0);
        while (opModeIsActive()) {
            odo.update();

            switch (stateMachine) {
                case WAITING_FOR_START://up the slider
                    stateMachine = StateMachine.DRIVE_TO_TARGET_1;
                    openArm();
                    break;
                case DRIVE_TO_TARGET_1:
                    if (nav.driveTo(odo.getPosition(), TARGET_1, 0.5, 0.0)) {//Drop pos 1 preload
                        telemetry.addLine("at position #1!");
                        openClaw();

                        stateMachine = StateMachine.DRIVE_TO_TARGET_1_1;
                    }
                case DRIVE_TO_TARGET_1_1:
                    if (nav.driveTo(odo.getPosition(), TARGET_1_1, 0.5, 0.0)) {//intermediate pos 1
                        telemetry.addLine("at position #1-1!");
                        upElevator();
                        closeArm();
                        stateMachine = StateMachine.DRIVE_TO_TARGET_2;
                    }

                    break;
                case DRIVE_TO_TARGET_2:
                    if (nav.driveTo(odo.getPosition(), TARGET_2, 0.5, 0.0)) {//pickup 2 field red 1
                        telemetry.addLine("at position #2!");
                        closeClaw();
                        stateMachine = StateMachine.DRIVE_TO_TARGET_2_1;

                    }

                    break;
                case DRIVE_TO_TARGET_2_1:
                    if (nav.driveTo(odo.getPosition(), TARGET_2_1, 0.5, 0.0)) {//intermediate pos 2
                        telemetry.addLine("at position #2-1!");

                        stateMachine = StateMachine.DRIVE_TO_TARGET_3;
                    }

                    break;
                case DRIVE_TO_TARGET_3:
                    if (nav.driveTo(odo.getPosition(), TARGET_3, 0.5, 0.0)) {// drop pos 2
                        telemetry.addLine("at position #3");
                        openClaw();
                        stateMachine = StateMachine.DRIVE_TO_TARGET_3_1;
                    }

                    break;
                case DRIVE_TO_TARGET_3_1:
                    if (nav.driveTo(odo.getPosition(), TARGET_3_1, 0.5, 0.0)) {//intermediate pos 3
                        telemetry.addLine("at position #3-1!");

                        stateMachine = StateMachine.DRIVE_TO_TARGET_4;
                    }

                    break;

                case DRIVE_TO_TARGET_4:
                    if (nav.driveTo(odo.getPosition(), TARGET_4, 0.5, 0.0)) {// pickup 3 field red 2
                        telemetry.addLine("at position #4");
                        closeClaw();
                        stateMachine = StateMachine.DRIVE_TO_TARGET_4_1;
                    }

                    break;
                case DRIVE_TO_TARGET_4_1:
                    if (nav.driveTo(odo.getPosition(), TARGET_4_1, 0.5, 0.0)) {//intermediate pos 4
                        telemetry.addLine("at position #4-1!");
                        stateMachine = StateMachine.DRIVE_TO_TARGET_5;
                    }

                    break;
                case DRIVE_TO_TARGET_5:
                    if (nav.driveTo(odo.getPosition(), TARGET_5, 0.5, 0.0)) {// drop pos 3
                        telemetry.addLine("at position #5");
                        openClaw();
                        stateMachine = StateMachine.DRIVE_TO_TARGET_5_1;
                    }
                    break;
                case DRIVE_TO_TARGET_5_1:
                    if (nav.driveTo(odo.getPosition(), TARGET_5_1, 0.5, 0.0)) {//intermediate pos 5
                        telemetry.addLine("at position #5-1!");
                        stateMachine = StateMachine.DRIVE_TO_TARGET_6;
                    }

                    break;

                case DRIVE_TO_TARGET_6:
                    if (nav.driveTo(odo.getPosition(), TARGET_6, 0.5, 0.0)) {// pickup 4 field red 3
                        telemetry.addLine("at position #6");
                        closeClaw();
                        stateMachine = StateMachine.DRIVE_TO_TARGET_6_1;
                    }

                    break;
                case DRIVE_TO_TARGET_6_1:
                    if (nav.driveTo(odo.getPosition(), TARGET_6_1, 0.5, 0.0)) {//intermediate pos 6
                        telemetry.addLine("at position #6-1!");

                        stateMachine = StateMachine.DRIVE_TO_TARGET_7;
                    }

                    break;
                case DRIVE_TO_TARGET_7:
                    if (nav.driveTo(odo.getPosition(), TARGET_7, 0.5, 0.0)) {// drop pos 4
                        telemetry.addLine("at position #7");
                        openClaw();
                        stateMachine = StateMachine.DRIVE_TO_TARGET_7_1;
                    }

                    break;
                case DRIVE_TO_TARGET_7_1:
                    if (nav.driveTo(odo.getPosition(), TARGET_7_1, 0.5, 0.0)) {//intermediate pos 7
                        telemetry.addLine("at position #7-1!");

                        stateMachine = StateMachine.DRIVE_TO_TARGET_8;
                    }

                    break;

                case DRIVE_TO_TARGET_8:
                    if (nav.driveTo(odo.getPosition(), TARGET_8, 0.5, 0.0)) {// pickup 5 human 1
                        telemetry.addLine("at position #8");
                        closeClaw();
                        stateMachine = StateMachine.DRIVE_TO_TARGET_9;

                    }

                    break;

                case DRIVE_TO_TARGET_9:
                    if (nav.driveTo(odo.getPosition(), TARGET_9, 0.5, 0.0)) {// drop pos 5
                        telemetry.addLine("at position #9");
                        openClaw();
                        stateMachine = StateMachine.DRIVE_TO_TARGET_10;
                    }

                    break;

                case DRIVE_TO_TARGET_10:
                    if (nav.driveTo(odo.getPosition(), TARGET_10, 0.5, 0.0)) {// pickup 6 human 2
                        telemetry.addLine("at position #10");
                        closeClaw();
                        stateMachine = StateMachine.DRIVE_TO_TARGET_11;
                    }

                    break;
                case DRIVE_TO_TARGET_11:
                    if (nav.driveTo(odo.getPosition(), TARGET_11, 0.5, 0.0)) {// drop pos 6
                        telemetry.addLine("at position #11");
                        openClaw();
                        closeArm();
                        stateMachine = StateMachine.DRIVE_TO_TARGET_12;
                    }

                    break;
                case DRIVE_TO_TARGET_12:
                    if (nav.driveTo(odo.getPosition(), TARGET_12, 0.5, 0.0)) {// park
                        telemetry.addLine("at position #12");
                        stateMachine = StateMachine.AT_TARGET;
                    }

                    break;

            }

            leftFrontDrive.setPower(nav.getMotorPower(DriveToPoint.DriveMotor.LEFT_FRONT));
            rightFrontDrive.setPower(nav.getMotorPower(DriveToPoint.DriveMotor.RIGHT_FRONT));
            leftBackDrive.setPower(nav.getMotorPower(DriveToPoint.DriveMotor.LEFT_BACK));
            rightBackDrive.setPower(nav.getMotorPower(DriveToPoint.DriveMotor.RIGHT_BACK));

        }






        telemetry.addData("current state:", stateMachine);

        Pose2D pos = odo.getPosition();
        String data = String.format(Locale.US, "{X: %.3f, Y: %.3f, H: %.3f}", pos.getX(DistanceUnit.MM), pos.getY(DistanceUnit.MM), pos.getHeading(AngleUnit.DEGREES));
        telemetry.addData("Position", data);

        telemetry.update();

    }

    public void upElevator() {
        slideMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        slideMotor.setTargetPosition(POSITION_2);
        slideMotor.setPower(1.0);

    }

    public void downElevator() {
        slideMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        slideMotor.setTargetPosition(POSITION_1);
        slideMotor.setPower(1.0);
    }

    public void flipBucket() {
        servoBucket.setPower(1);
        sleep(2000);
        servoBucket.setPower(-1);
        sleep(2000);
        servoBucket.setPower(0);
    }

    public void openClaw() {
        servoPinceL.setPower(-1);
        sleep(5);
        servoPinceR.setPower(1);
        sleep(2000);
        servoPinceL.setPower(0);
        servoPinceR.setPower(0);
    }

    public void closeClaw() {
        servoPinceL.setPower(1);
        sleep(5);
        servoPinceR.setPower(-1);
        sleep(2000);
        servoPinceL.setPower(0);
        servoPinceR.setPower(0);
    }

    public boolean notReadyToDump() {
        return slideMotor.getCurrentPosition() < POSITION_2 - 25 || slideMotor.getCurrentPosition() > POSITION_2 + 25;
    }

    public boolean notReadyToPick() {
        return slideMotor.getCurrentPosition() < POSITION_1 - 25 || slideMotor.getCurrentPosition() > POSITION_1 + 25;
    }

    public void openArm() {
        armMotor.setPower(0.25);
    }

    public void closeArm() {
        armMotor.setPower(-0.5);
        sleep(1000);
        armMotor.setPower(0);
    }

    public void stopArm() {
        armMotor.setPower(0);
    }

}
