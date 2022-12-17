package ir.lecer.uwu.events.handler;

public abstract class Event {
  private boolean cancelled;
  
  public byte type;
  
  public void setCancelled(boolean cancelled) {
    this.cancelled = cancelled;
  }
  
  public void setType(byte type) {
    this.type = type;
  }
  
  public boolean equals(Object o) {
    if (o == this)
      return true; 
    if (!(o instanceof Event))
      return false; 
    Event other = (Event)o;
    return !other.canEqual(this) ? false : ((isCancelled() != other.isCancelled()) ? false : (!(getType() != other.getType())));
  }
  
  protected boolean canEqual(Object other) {
    return other instanceof Event;
  }
  
  public int hashCode() {
    int PRIME = 59;
    result = 1;
    result = result * 59 + (isCancelled() ? 79 : 97);
    return result * 59 + getType();
  }
  
  public String toString() {
    return "Event(cancelled=" + isCancelled() + ", type=" + getType() + ")";
  }
  
  public boolean isCancelled() {
    return this.cancelled;
  }
  
  public byte getType() {
    return this.type;
  }
  
  public Event call() {
    this.cancelled = false;
    call(this);
    return this;
  }
  
  private static void call(Event event) {
    ArrayHelper<Data> dataList = EventManager.get((Class)event.getClass());
    if (dataList != null)
      for (Data data : dataList) {
        try {
          data.target.invoke(data.source, new Object[] { event });
        } catch (Exception ex) {
          ex.printStackTrace();
        } 
      }  
  }
}
