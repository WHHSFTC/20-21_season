package org.firstinspires.ftc.teamcode.tele;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import java.util.List;
@TeleOp(name = "ServoTest", group = "Test")
public class ServoTest extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        List<Servo> servos = hardwareMap.getAll(Servo.class);
        waitForStart();
        Tester tester1 = new Tester(gamepad1, servos, "d1");
        Tester tester2 = new Tester(gamepad2, servos, "d2");
        Thread thread1 = new Thread(() -> {
            try {
                tester1.testServos();
            } catch (InterruptedException ex) {
            }
        });
        Thread thread2 = new Thread(() -> {
            try {
                tester2.testServos();
            } catch (InterruptedException ex) {
            }
        });
        thread1.start();
        thread2.start();
        while (opModeIsActive()) {
            tester1.log(telemetry);
            tester2.log(telemetry);
            telemetry.update();
            Thread.sleep(500);
        }
    }

    class Tester {
        private Gamepad gamepad;
        private List<Servo> servos;
        private String name;

        Tester(Gamepad gamepad, List<Servo> servos, String name) {
            this.gamepad = gamepad;
            this.servos = servos;
            this.name = name;
        }

        int i = 0;
        Servo servo;
        boolean stick = false;
        boolean prevInc = false;
        boolean prevDec = false;
        boolean prevStick = false;
        double offset = 0;
        double precision = 0.05;
        double a = 0;
        double b = 0.5;
        double y = 1;

        void log(Telemetry telemetry) {
            telemetry.addData("Stick" + name, stick);
            telemetry.addData("Offset" + name, offset);
            telemetry.addData("Precision" + name, precision);
            //telemetry.addData("Controller" + name, servo.getController().getDeviceName());
//            telemetry.addData("Port" + name, servo.getPortNumber());
            telemetry.addData("Names" + name, hardwareMap.getNamesOf(servo));
            telemetry.addData("Position" + name, servo.getPosition());
            telemetry.addData("a" + name, a);
            telemetry.addData("b" + name, b);
            telemetry.addData("y" + name, y);
        }

        void testServos() throws InterruptedException {
            while (opModeIsActive()) {
                servo = servos.get(i);
                if (gamepad.right_bumper && !prevInc && i < servos.size() - 1) i++;
                prevInc = gamepad.right_bumper;
                if (gamepad.left_bumper && !prevDec && i > 0) i--;
                prevDec = gamepad.left_bumper;
                if (gamepad.left_trigger > 0.5) {
                    if (gamepad.a) a = servo.getPosition();
                    if (gamepad.b) b = servo.getPosition();
                    if (gamepad.y) y = servo.getPosition();
                } else if (gamepad.right_trigger > 0.5) {
                    if (gamepad.a) servo.setPosition(a);
                    if (gamepad.b) servo.setPosition(b);
                    if (gamepad.y) servo.setPosition(y);
                } else {
                    if (gamepad.x) offset = servo.getPosition();
                    if (gamepad.b && !prevStick) stick = !stick;
                    prevStick = gamepad.b;
                }
                if (gamepad.dpad_left) precision = 0.05;
                if (gamepad.dpad_right) precision = 0.01;
                if (stick) {
                    servo.setPosition(gamepad.left_stick_y * precision + offset);
                } else {
                    if (gamepad.dpad_up && servo.getPosition() <= 1 - precision) {
                        servo.setPosition(servo.getPosition() + precision);
                        Thread.sleep(200);
                    }
                    if (gamepad.dpad_down && servo.getPosition() >= precision) {
                        servo.setPosition(servo.getPosition() - precision);
                        Thread.sleep(200);
                    }
                }
            }
        }
    }
}
