/*
 * Name: Andrew Chen
 * Date: December 16th 2023
 * Description: "Game loop" -- runs the game and calls what needs to be called
*/ 

/* GamePanel class acts as the main "game loop" - continuously runs the game and calls whatever needs to be called

Child of JPanel because JPanel contains methods for drawing to the screen

Implements KeyListener interface to listen for keyboard input

Implements Runnable interface to use "threading" - let the game do two things at once

*/
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

public class GamePanel extends JPanel implements Runnable, KeyListener {

	// dimensions of window
	public static final int GAME_WIDTH = 1200;
	public static final int GAME_HEIGHT = 750;

	//scores for the players
	public int leftScore = 0;
	public int rightScore = 0;
	
	//random for a random aspect of the ball movement
	public double random;
	
	//to display the instructions
	public boolean start = true;
	
	//padding for the breadPaddle since it has rounded edges. Makes the collision feel more natural
	public int roundMargin = 11;

	public Thread gameThread;
	public Image image;
	public Graphics graphics;
	public PongBall ball;
	public int ySpeedCap = 13;
	//paddles and vertical divider
	public Paddle leftPad;
	public Paddle rightPad;
	public Image bar;
	public ImageIcon i;
	
	public GamePanel() {
		ball = new PongBall((GAME_WIDTH / 2) - (PongBall.BALL_DIAMETER / 2),
				(GAME_HEIGHT / 2) - (PongBall.BALL_DIAMETER / 2), -1); // create a pong ball, set start
																		// location to middle of screen and move to left
		
		//create left and right bread paddles at the middle of the screen y-wise
		leftPad = new Paddle(0, (GAME_HEIGHT / 2) - (Paddle.PADDLE_LENGTH / 2));
		rightPad = new Paddle(GAME_WIDTH - Paddle.PADDLE_WIDTH, (GAME_HEIGHT / 2) - (Paddle.PADDLE_LENGTH / 2));

		this.setFocusable(true); // make everything in this class appear on the screen
		this.addKeyListener(this); // start listening for keyboard input

		this.setPreferredSize(new Dimension(GAME_WIDTH, GAME_HEIGHT));

		// make this class run at the same time as other classes (without this each
		// class would "pause" while another class runs). By using threading we can
		// remove lag, and also allows us to do features like display timers in real
		// time!
		gameThread = new Thread(this);
		gameThread.start();
	}

	// paint is a method in java.awt library that we are overriding. It is a special
	// method - it is called automatically in the background in order to update what
	// appears in the window. You NEVER call paint() yourself
	public void paint(Graphics g) {
		// we are using "double buffering here" - if we draw images directly onto the
		// screen, it takes time and the human eye can actually notice flashes of lag as
		// each pixel on the screen is drawn one at a time. Instead, we are going to
		// draw images OFF the screen, then simply move the image on screen as needed.
		image = createImage(GAME_WIDTH, GAME_HEIGHT); // draw off screen
		graphics = image.getGraphics();
		draw(graphics);
		g.drawImage(image, 0, 0, this); // move the image on the screen
	}

	// call the draw methods in each class to update positions as things move
	public void draw(Graphics g) {
		g.setColor(Color.white);

		//add the vertical spaghetti bar
		i = new ImageIcon("Images/divLine.png");
		bar = i.getImage();
		g.drawImage(bar, GAME_WIDTH / 2 - 25/2, 0, null);

		//set font size and display the scores, ball and paddles
		g.setFont(new Font("TimesRoman", Font.PLAIN, 60));
		g.drawString(Integer.toString(leftScore), 30, 50);
		g.drawString(Integer.toString(rightScore), GAME_WIDTH - 100, 50);
		ball.draw(g);
		leftPad.draw(g);
		rightPad.draw(g);

		// display menu and instructions while start bool is true (drawn over the preceding content to appear as a separate screen)
		if (start) {
			ball = new PongBall(GAME_WIDTH / 2, GAME_HEIGHT / 2, 1);
			g.setColor(Color.black);
			g.fillRect(0, 0, GAME_WIDTH, GAME_HEIGHT);
			g.setColor(Color.magenta);
			g.drawString("INSTRUCTIONS", 390, 100);
			g.setFont(new Font("TimesRoman", Font.PLAIN, 40));
			g.setColor(Color.white);

			g.drawString("Left Controls:", 200, 180);
			g.drawString("W - UP", 200, 250);
			g.drawString("S - DOWN", 200, 320);

			g.drawString("Right Controls:", 700, 180);
			g.drawString("^ - UP", 700, 250);
			g.drawString("Down arrow - DOWN", 700, 320);

			g.drawString("First to 5 points wins.", 290, 450);

			g.drawString("ENTER to play!", 290, 600);

		}

		//checks if the game is over and displays corresponding info
		if (leftScore >= 5) {
			g.setColor(Color.black);
			g.fillRect(0, 0, GAME_WIDTH, GAME_HEIGHT);
			g.setColor(Color.red);
			g.drawString("Left Player WINS!", 275, GAME_HEIGHT / 2);
			g.drawString("ENTER to restart.", 310, GAME_HEIGHT / 2 + 100);

			ball.xVelocity = 0;
			ball.yVelocity = 0;
		} else if (rightScore >= 5) {
			g.setColor(Color.black);
			g.fillRect(0, 0, GAME_WIDTH, GAME_HEIGHT);
			g.setColor(Color.cyan);
			g.drawString("Right Player WINS", 240, GAME_HEIGHT / 2);
			g.drawString("ENTER to restart.", 310, GAME_HEIGHT / 2 + 100);

			ball.xVelocity = 0;
			ball.yVelocity = 0;

		}


	}

	// call the move methods in other classes to update positions
	// this method is constantly called from run(). By doing this, movements appear
	// fluid and natural. If we take this out the movements appear sluggish and
	// laggy
	public void move() {
		ball.move();
		leftPad.move();
		rightPad.move();
	}

	// handles all collision detection and responds accordingly
	public void checkCollision() {

		// force ball to remain on screen and bounce off walls
		if (ball.y <= 0) {
			ball.y = 0;
			ball.yVelocity *= -1;
		}
		if (ball.y >= GAME_HEIGHT - PongBall.BALL_DIAMETER) {
			ball.y = GAME_HEIGHT - PongBall.BALL_DIAMETER;
			ball.yVelocity *= -1;
		}
		if (ball.x <= -PongBall.BALL_DIAMETER) {
			ball.x = -PongBall.BALL_DIAMETER;
		}
		if (ball.x >= GAME_WIDTH) {
			ball.x = GAME_WIDTH;
		}

		// forces paddles to remain on screen
		if (leftPad.y <= roundMargin) {
			leftPad.y = roundMargin;
		}

		if (leftPad.y >= GAME_HEIGHT - leftPad.height - roundMargin) {
			leftPad.y = GAME_HEIGHT - leftPad.height - roundMargin;
		}

		if (rightPad.y <= roundMargin) {
			rightPad.y = roundMargin;
		}

		if (rightPad.y >= GAME_HEIGHT - rightPad.height - roundMargin) {
			rightPad.y = GAME_HEIGHT - rightPad.height - roundMargin;
		}
		
		
		// check if ball hits the left paddle head on
		if (ball.x > 0 && ball.x <= Paddle.PADDLE_WIDTH && ball.y  + PongBall.BALL_DIAMETER > leftPad.y
				&& ball.y <= leftPad.y + Paddle.PADDLE_LENGTH ) {
			
			//make ball bounce off and add the y vel
			ball.xVelocity = Math.abs(ball.xVelocity);
			
			//sets a speed cap at 13
			if(leftPad.yVelocity+ball.yVelocity > 0) {
				ball.yVelocity = Math.max(leftPad.yVelocity+ball.yVelocity, ySpeedCap);
			} else if (leftPad.yVelocity+ball.yVelocity < 0) {
				ball.yVelocity = Math.max(leftPad.yVelocity+ball.yVelocity, -ySpeedCap);
			} else {
				ball.yVelocity = 0;
			}
			

			
			//randomly add or sub 1px to make prevent endless loops
			random = Math.random();
			if (random > 0.5) {
				ball.yVelocity++;
			} else if (random < 0.5) {
				ball.yVelocity--;
			}

		}
		// check if the ball hits leftPad top
		else if (ball.x > -PongBall.BALL_DIAMETER/2 && ball.x <= Paddle.PADDLE_WIDTH
				&& ball.y + PongBall.BALL_DIAMETER >= leftPad.y-roundMargin
				&& ball.y + PongBall.BALL_DIAMETER <= leftPad.y + Paddle.PADDLE_LENGTH / 2) {
			// ^ checks it the ball is in the x range of the paddle, and the y coordinate is
			// touching the top of the left paddle

			if (ball.yVelocity > 0) { // if the ball is moving down
				// make the ball bounce off
				ball.yVelocity *= -1;
			}

			else if (ball.yVelocity <= 0) { // if the ball is moving up already or 0
				// make the ball up at a faster speed than the paddle
				ball.yVelocity = leftPad.yVelocity - 1;
			}
			ball.xVelocity = Math.abs(ball.xVelocity);
		}

		// check if the ball hits leftPad bot
		else if (ball.x > -PongBall.BALL_DIAMETER/2 && ball.x <= Paddle.PADDLE_WIDTH
				&& ball.y - roundMargin <= leftPad.y + Paddle.PADDLE_LENGTH && ball.y > leftPad.y + Paddle.PADDLE_LENGTH / 2) {
			// ^ checks it the ball is in the x range of the paddle, and the y coordinate is
			// touching the bottom of the left paddle

			if (ball.yVelocity >= 0) { // if the ball is moving down
				// make the ball up at a faster speed than the paddle 
				ball.yVelocity = leftPad.yVelocity + 1;
			}

			else if (ball.yVelocity < 0) { // if the ball is moving up already or 0
				// make the ball bounce off
				ball.yVelocity *= -1;
			}
			ball.xVelocity = Math.abs(ball.xVelocity);
		}

		// check if ball hits the right paddle head on
		if (ball.x + PongBall.BALL_DIAMETER < GAME_WIDTH
				&& ball.x + PongBall.BALL_DIAMETER >= GAME_WIDTH - Paddle.PADDLE_WIDTH && ball.y+PongBall.BALL_DIAMETER > rightPad.y
				&& ball.y <= rightPad.y + Paddle.PADDLE_LENGTH) {
			
			//make ball bounce off and add the y vel
			ball.xVelocity = -Math.abs(ball.xVelocity);
			
			//sets a speed cap at 13
			if(rightPad.yVelocity+ball.yVelocity > 0) {
				ball.yVelocity = Math.max(rightPad.yVelocity+ball.yVelocity, ySpeedCap);
			} else if (rightPad.yVelocity+ball.yVelocity < 0) {
				ball.yVelocity = Math.max(rightPad.yVelocity+ball.yVelocity, -ySpeedCap);
			} else {
				ball.yVelocity = 0;
			}

			//randomly add or sub 1px to make prevent endless loops
			random = Math.random();
			if (random > 0.5) {
				ball.yVelocity++;
			} else if (random < 0.5) {
				ball.yVelocity--;
			}

		}

		// check if the ball hits rightPad top
		else if (ball.x + PongBall.BALL_DIAMETER >= GAME_WIDTH - Paddle.PADDLE_WIDTH && ball.x < GAME_WIDTH - Paddle.PADDLE_WIDTH/2
				&& ball.y + PongBall.BALL_DIAMETER >= rightPad.y - roundMargin
				&& ball.y + PongBall.BALL_DIAMETER <= rightPad.y + Paddle.PADDLE_LENGTH / 2) {
			// ^ checks it the ball is in the x range of the paddle, and the y coordinate is
			// touching the top of the left paddle, gives it a half paddle error space so it
			// doesn't get stuck

			if (ball.yVelocity > 0) { // if the ball is moving down
				// make the ball bounce off
				ball.yVelocity *= -1;
			}

			else if (ball.yVelocity <= 0) { // if the ball is moving up already or 0
				// make the ball up at a faster speed than the paddle 
				ball.yVelocity = rightPad.yVelocity - 1;
			}
			ball.xVelocity = -Math.abs(ball.xVelocity);
		}

		// check if the ball hits rightPad bot
		else if (ball.x + PongBall.BALL_DIAMETER >= GAME_WIDTH - Paddle.PADDLE_WIDTH && ball.x <GAME_WIDTH - Paddle.PADDLE_WIDTH/2
				&& ball.y-roundMargin <= rightPad.y + Paddle.PADDLE_LENGTH && ball.y > rightPad.y + Paddle.PADDLE_LENGTH / 2) {
			// ^ checks it the ball is in the x range of the paddle, and the y coordinate is
			// touching the bottom of the left paddle

			if (ball.yVelocity >= 0) { // if the ball is moving down
				// make the ball up at a faster speed than the paddle 
				ball.yVelocity = rightPad.yVelocity + 1;
			}

			else if (ball.yVelocity < 0) { // if the ball is moving up already or 0
				// make the ball bounce off
				ball.yVelocity *= -1;
			}
			ball.xVelocity = -Math.abs(ball.xVelocity);
		}

		

		// ball starts moving towards person that wins
		if (ball.x >= GAME_WIDTH) {
			leftScore++;
			ball = new PongBall(GAME_WIDTH / 2, GAME_HEIGHT / 2, -1);
		}

		if (ball.x + PongBall.BALL_DIAMETER <= 0) {
			rightScore++;
			ball = new PongBall(GAME_WIDTH / 2, GAME_HEIGHT / 2, 1);
		}


	}

	// run() method is what makes the game continue running without end. It calls
	// other methods to move objects, check for collision, and update the screen
	public void run() {
		// the CPU runs our game code too quickly - we need to slow it down! The
		// following lines of code "force" the computer to get stuck in a loop for short
		// intervals between calling other methods to update the screen.
		long lastTime = System.nanoTime();
		double amountOfTicks = 60;
		double ns = 1000000000 / amountOfTicks;
		double delta = 0;
		long now;

		while (true) { // this is the infinite game loop
			now = System.nanoTime();
			delta = delta + (now - lastTime) / ns;
			lastTime = now;

			// only move objects around and update screen if enough time has passed
			if (delta >= 1) {
				move();
				checkCollision();
				repaint();
				delta--;
			}
		}
	}

	// if a key is pressed, we'll send it over to the Paddle class for processing
	public void keyPressed(KeyEvent e) {
		leftPad.keyPressed(e, 0);
		rightPad.keyPressed(e, 1);

		
		//check if the player presses "enter", then start the game, OR restart the game
		if (e.getKeyCode() == KeyEvent.VK_ENTER && start) {
			// enter the game from instructions
			start = false;
			leftScore = 0;
			rightScore = 0;
		} else if (e.getKeyCode() == KeyEvent.VK_ENTER && !start && (leftScore == 5 || rightScore == 5)) {
			// start = true;
			leftScore = 0;
			rightScore = 0;
			ball = new PongBall(GAME_WIDTH / 2 - PongBall.BALL_DIAMETER, GAME_HEIGHT / 2 - PongBall.BALL_DIAMETER, -1);
			leftPad = new Paddle(0, (GAME_HEIGHT / 2) - (Paddle.PADDLE_LENGTH / 2));
			rightPad = new Paddle(GAME_WIDTH - Paddle.PADDLE_WIDTH, (GAME_HEIGHT / 2) - (Paddle.PADDLE_LENGTH / 2));

		}
	}

	// if a key is released, we'll send it over to the Paddle class for processing
	public void keyReleased(KeyEvent e) {
		leftPad.keyReleased(e, 0);
		rightPad.keyReleased(e, 1);

	}

	// left empty because we don't need it; must be here because it is required to
	// be overridded by the KeyListener interface
	public void keyTyped(KeyEvent e) {

	}
}