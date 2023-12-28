/*
 * Name: Andrew Chen
 * Date: December 16th 2023
 * Description: defines behaviours for the pong ball  
*/

import java.awt.*;

import javax.swing.ImageIcon;

public class PongBall extends Rectangle{

  public int yVelocity = 0;
  public int xVelocity = 0;
  public final int SPEED = 10; //x movement speed of ball
  public static final int BALL_DIAMETER = 40; //size of ball
  public Image meatball;

  //constructor creates ball at given location with given dimensions
  public PongBall(int x, int y, int xDir){
    super(x, y, BALL_DIAMETER, BALL_DIAMETER);
    xVelocity = SPEED*xDir; // ball starts moving towards person that wins
  }



  //called whenever the movement of the ball changes in the y-direction (up/down)
  public void setYDirection(int yDirection){
    yVelocity = yDirection;
  }

  //called whenever the movement of the ball changes in the x-direction (left/right)
  public void setXDirection(int xDirection){
    xVelocity = xDirection;
  }

  //called frequently from both PlayerBall class and GamePanel class
  //updates the current location of the ball
  public void move(){
    y = y + yVelocity;
    x = x + xVelocity;
  }

  //called frequently from the GamePanel class
  //draws the current location of the ball to the screen
  public void draw(Graphics g){
    //draws the meatball
	ImageIcon i = new ImageIcon("Images/meatball.png");

	meatball = i.getImage();
	g.drawImage(meatball, x, y, null);

    
  }
  
}


