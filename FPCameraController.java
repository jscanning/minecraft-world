/***************************************************************
* file: FPCameraController.java
* authors: Jeremy Canning, Dylan Chung, Camron Fortenbery, Grant Posner
* class: CS 4450: Computer Graphics
*
* assignment: Final Project
* date last modified: 3/27/2019
*
* purpose: This file contains the necessary code for camera movement 
* and creates the chunk.
*
****************************************************************/ 
package graphically_inclined_checkpoint_2;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.util.vector.Vector3f;

public class FPCameraController {
        private Vector3f pos = null;
        private Vector3f IPos = null;
        private float yaw = 0.0f;
        private float pitch = 0.0f;
        private Chunk[] chunks;
        static final int NUM_CHUNKS = 1;
        
        public FPCameraController(float x, float y, float z) {
            pos = new Vector3f(x,y,z);
            IPos = new Vector3f(x,y,z);
            IPos.x = 0.0f;
            IPos.y = 15.0f;
            IPos.z = 0.0f;
            chunks = new Chunk[NUM_CHUNKS * NUM_CHUNKS];
            int k = 0;
            for (int i = 0; i < NUM_CHUNKS; i++) {
                for (int j = 0; j < NUM_CHUNKS; j++) {
                    chunks[k] = new Chunk(i * -60, 0, j * -60);
                    k++;
                }
            }
        }
        
        // method: yaw
        // purpose: Adjusts the yaw of the camera based on the value passed to it.
        public void yaw(float amm) {
            yaw += amm;
        }
        
        // method: pitch
        // purpose: Adjusts the pitch of the camera based on the value passed to
        // it. Pitch is locked such that it never passes looking straight up or
        // straight down.
        public void pitch(float amm) {
            pitch -= amm;
            if (pitch > 90.0f) {
                pitch = 90.0f;
            } else if (pitch < -90.0f) {
                pitch = -90.0f;
            }
        }
        
        // method: ahead
        // purpose: Moves camera in the direction it's currently looking at.
        public void ahead(float dist) {
            float xOffset = dist*(float)Math.sin(Math.toRadians(yaw));
            float zOffset = dist*(float)Math.cos(Math.toRadians(yaw));
            pos.x -= xOffset;
            pos.z += zOffset;
        }
        
        // method: astern
        // purpose: Moves camera in reverse of the direction it's currently 
        // looking at.
        public void astern(float dist) {
            float xOffset = dist*(float)Math.sin(Math.toRadians(yaw));
            float zOffset = dist*(float)Math.cos(Math.toRadians(yaw));
            pos.x += xOffset;
            pos.z -= zOffset;
        }
        
        // method: aport
        // purpose: Moves camera left with respect to the direction it's 
        // currently looking at.
        public void aport(float dist) {
            float xOffset = dist*(float)Math.sin(Math.toRadians(yaw - 90));
            float zOffset = dist*(float)Math.cos(Math.toRadians(yaw - 90));
            pos.x -= xOffset;
            pos.z += zOffset;
        }
        
        // method: astarboard
        // purpose: Moves camera right with respect to the direction it's 
        // currently looking at.
        public void astarboard(float dist) {
            float xOffset = dist*(float)Math.sin(Math.toRadians(yaw + 90));
            float zOffset = dist*(float)Math.cos(Math.toRadians(yaw + 90));
            pos.x -= xOffset;
            pos.z += zOffset;
        }
        
        // method: ascend
        // purpose: Moves camera up.
        public void ascend(float dist) {
            pos.y -= dist;
        }
        
        // method: descend
        // purpose: Moves camera down.
        public void descend(float dist) {
            pos.y += dist;
        }
        
        // method: gaze
        // purpose: Transforms the matrix to provide dynamic 3D viewing of 
        // the scene.
        public void gaze() {
            glRotatef(pitch, 1.0f, 0.0f, 0.0f);
            glRotatef(yaw, 0.0f, 1.0f, 0.0f);
            glTranslatef(pos.x, pos.y, pos.z);
        }
        
        // method: gameLoop
        // purpose: Displays the scene. Implements camera such that the chunk is 
        // rendered and the camera is controllable by the user. Additional 
        // controls are as follows: Holding Y and scrolling the mouse wheel 
        // raises or lowers the yaw sensitivity. Holding P and scrolling has an
        // equivalent effect on pitch sensitivity. Holding T and scrolling 
        // adjusts both at once. Pressing U sets the pitch sensitivity to the
        // current yaw sensitivity. Pressing O has the reverse effect to 
        // pressing U. Pressing I restores both sensitivities to their default
        // values. Holding M and scrolling raises or lowers the movement speed.
        // Pressing N restores movement speed to its default value. Pressing L
        // and comma simultaneously unlocks the camera from the chunk boundaries.
        // Pressing L and period simultaneously re-engages the lock. Off by 
        // default. Pressing Q and right shift simultaneously enables texture
        // transparency, while pressing Q disables texture transparency. Opaque
        // by default.
        public void gameLoop() {
            FPCameraController camera = new FPCameraController(pos.x, pos.y, pos.z);
            float dx;
            float dy;
            float yawSensitivity = 0.09f;
            float pitchSensitivity = 0.09f;
            float moveSpeed = 0.35f;
            boolean locked = false;
            Mouse.setGrabbed(true);
            while (!Display.isCloseRequested() && !Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
                dx = Mouse.getDX();
                dy = Mouse.getDY();
                camera.yaw(dx * yawSensitivity);
                camera.pitch(dy * pitchSensitivity);
                if (Keyboard.isKeyDown(Keyboard.KEY_Y)) {
                    yawSensitivity += (float)Mouse.getDWheel()/12000;
                    if (yawSensitivity < 0.01f) {
                        yawSensitivity = 0.01f;
                    } else if (yawSensitivity > 0.2f) {
                        yawSensitivity = 0.2f;
                    }
                }
                if (Keyboard.isKeyDown(Keyboard.KEY_P)) {
                    pitchSensitivity += (float)Mouse.getDWheel()/12000;
                    if (pitchSensitivity < 0.01f) {
                        pitchSensitivity = 0.01f;
                    } else if (pitchSensitivity > 0.2f) {
                        pitchSensitivity = 0.2f;
                    }
                }
                if (Keyboard.isKeyDown(Keyboard.KEY_T)) {
                    float dS = (float)Mouse.getDWheel()/12000;
                    yawSensitivity += dS;
                    pitchSensitivity += dS;
                    if (pitchSensitivity < 0.01f) {
                        pitchSensitivity = 0.01f;
                    } else if (pitchSensitivity > 0.2f) {
                        pitchSensitivity = 0.2f;
                    }
                    if (yawSensitivity < 0.01f) {
                        yawSensitivity = 0.01f;
                    } else if (yawSensitivity > 0.2f) {
                        yawSensitivity = 0.2f;
                    }
                }
                if (Keyboard.isKeyDown(Keyboard.KEY_U)) {
                    pitchSensitivity = yawSensitivity;
                }
                if (Keyboard.isKeyDown(Keyboard.KEY_O)) {
                    yawSensitivity = pitchSensitivity;
                }
                if (Keyboard.isKeyDown(Keyboard.KEY_I)) {
                    yawSensitivity = 0.09f;
                    pitchSensitivity = 0.09f;
                }
                if (Keyboard.isKeyDown(Keyboard.KEY_M)) {
                    moveSpeed += (float)Mouse.getDWheel()/2400;
                    if (moveSpeed < 0.05f) {
                        moveSpeed = 0.05f;
                    } else if (moveSpeed > 0.60f) {
                        moveSpeed = 0.60f;
                    }
                }
                if (Keyboard.isKeyDown(Keyboard.KEY_N)) {
                    moveSpeed = 0.35f;
                }
                if (Keyboard.isKeyDown(Keyboard.KEY_W) || Keyboard.isKeyDown(Keyboard.KEY_UP)) {
                    camera.ahead(moveSpeed);
                }
                if (Keyboard.isKeyDown(Keyboard.KEY_S) || Keyboard.isKeyDown(Keyboard.KEY_DOWN)) {
                    camera.astern(moveSpeed);
                }
                if (Keyboard.isKeyDown(Keyboard.KEY_A) || Keyboard.isKeyDown(Keyboard.KEY_LEFT)) {
                    camera.aport(moveSpeed);
                }
                if (Keyboard.isKeyDown(Keyboard.KEY_D) || Keyboard.isKeyDown(Keyboard.KEY_RIGHT)) {
                    camera.astarboard(moveSpeed);
                }
                if (Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
                    camera.ascend(moveSpeed);
                }
                if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
                    camera.descend(moveSpeed);
                }
                if (Keyboard.isKeyDown(Keyboard.KEY_L) && Keyboard.isKeyDown(Keyboard.KEY_COMMA)) {
                    locked = false;
                } else if (Keyboard.isKeyDown(Keyboard.KEY_L) && Keyboard.isKeyDown(Keyboard.KEY_PERIOD)) {
                    locked = true;
                }
                if (Keyboard.isKeyDown(Keyboard.KEY_Q) && Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
                    glEnable(GL_BLEND);
                } else if (Keyboard.isKeyDown(Keyboard.KEY_Q)) {
                    glDisable(GL_BLEND);
                }
                if (locked) {
                    if (camera.pos.x > (NUM_CHUNKS - 1) * 60 + 1) {
                        camera.pos.x = (NUM_CHUNKS - 1) * 60 + 1;
                    } else if (camera.pos.x < -59) {
                        camera.pos.x = -59;
                    }
                    if (camera.pos.y > 2) {
                        camera.pos.y = 2;
                    } else if (camera.pos.y < -58) {
                        camera.pos.y = -58;
                    }
                    if (camera.pos.z > (NUM_CHUNKS - 1) * 60 + 2) {
                        camera.pos.z = (NUM_CHUNKS - 1) * 60 + 2;
                    } else if (camera.pos.z < -58) {
                        camera.pos.z = -58;
                    }
                }
                glLoadIdentity();
                camera.gaze();
                glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
                for (int i = 0; i < chunks.length; i++) {
                    chunks[i].render();
                }
//                thirty.render();
                Display.update();
                Display.sync(60);
            }
            Display.destroy();
        }
    }