
/*
 * Name: Andrew Chen
 * Date: December 16th 2023
 * Description: describes a paddle and it's properties and functions 
*/


/*
 *  Paddle class describes a paddle
 * 
 */

import java.awt.*;
import java.awt.event.*;

import javax.swing.ImageIcon;

public class Paddle extends Rectangle {

	public int yVelocity;
	public final int SPEED = 8; // movement speed of paddles
	public static final int PADDLE_WIDTH = 25; //25 with baguette
	public static final int PADDLE_LENGTH = 165;
	public Image bread;
	public ImageIcon i;
	
	public Paddle(int x, int y) {
		super(x, y, PADDLE_WIDTH, PADDLE_LENGTH);
	}

	// called from GamePanel wh  en any keyboard input is detected
	// updates the direction of the ball based on user input
	// if the keyboard input isn't any of the options (d, a, w, s), then nothing
	// happens
	public void keyPressed(KeyEvent e, int pad) {

		//if w or s --> left pad moves
		if (e.getKeyChar() == 'w' && pad == 0) {
			setYDirection(SPEED * -1);
			move();
		}

		if (e.getKeyChar() == 's' && pad == 0) {
			setYDirection(SPEED);
			move();
		}
		
		
		//if up and down --> right pad moves
		if (e.getKeyCode() == KeyEvent.VK_UP && pad == 1) {
			setYDirection(SPEED * -1);
			move();
		}

		if (e.getKeyCode() == KeyEvent.VK_DOWN && pad == 1) {
			setYDirection(SPEED);
			move();
		}
		

		
	}

	// called from GamePanel when any key is released (no longer being pressed down)
	// Makes the ball stop moving in that direction
	public void keyReleased(KeyEvent e, int pad) {

		if (e.getKeyChar() == 'w' && pad == 0) {
			setYDirection(0);
			move();
		}

		if (e.getKeyChar() == 's' && pad == 0) {
			setYDirection(0);
			move();
		}
		
		if (e.getKeyCode() == KeyEvent.VK_UP && pad == 1) {
			setYDirection(0);
			move();
		}

		if (e.getKeyCode() == KeyEvent.VK_DOWN && pad == 1) {
			setYDirection(0);
			move();
		}
		

		
	}


	// called whenever the movement of the ball changes in the y-direction (up/down)
	public void setYDirection(int yDirection) {
		yVelocity = yDirection;
	}

	// called frequently from both PlayerBall class and GamePanel class
	// updates the current location of the ball
	public void move() {
		y = y + yVelocity;
	}

	// called frequently from the GamePanel class
	// draws the current location of the ball to the screen
	public void draw(Graphics g) {
		
		//draws the bread image
		i = new ImageIcon("Images/breadPaddle.png");

		bread = i.getImage();
		g.drawImage(bread, x, y-22, null);

	}

}