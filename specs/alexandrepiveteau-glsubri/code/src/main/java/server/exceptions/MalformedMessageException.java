package server.exceptions;

public class MalformedMessageException extends IllegalArgumentException {

  private String malformedMessage;

  public MalformedMessageException(String message) {
    super();
    this.malformedMessage = message;
  }

  public String getMalformedMessage() {
    return malformedMessage;
  }
}
