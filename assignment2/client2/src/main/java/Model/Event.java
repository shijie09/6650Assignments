package Model;

import io.swagger.client.model.SwipeDetails;

public class Event {
  private String leftOrRight;
  private SwipeDetails curBody;

  public Event(String leftOrRight, SwipeDetails curBody) {
    this.leftOrRight = leftOrRight;
    this.curBody = curBody;
  }

  public Event() {
  }

  public void setLeftOrRight(String leftOrRight) {
    this.leftOrRight = leftOrRight;
  }

  public void setCurBody(SwipeDetails curBody) {
    this.curBody = curBody;
  }

  public String getLeftOrRight() {
    return leftOrRight;
  }

  public SwipeDetails getCurBody() {
    return curBody;
  }


}
