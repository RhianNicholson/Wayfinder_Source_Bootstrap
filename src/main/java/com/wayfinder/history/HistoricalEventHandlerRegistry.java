package com.wayfinder.history;
import java.util.EnumMap;
import java.util.List;
import java.util.Optional;
public final class HistoricalEventHandlerRegistry {
 private final EnumMap<HistoricalEventType,HistoricalEventHandler> handlers=new EnumMap<>(HistoricalEventType.class);
 public HistoricalEventHandlerRegistry(List<HistoricalEventHandler> handlers){
  for(var handler:handlers){
   var previous=this.handlers.put(handler.type(),handler);
   if(previous!=null) throw new IllegalArgumentException("Duplicate historical event handler: "+handler.type());
  }
 }
 public Optional<HistoricalEventHandler> handlerFor(HistoricalEventType type){ return Optional.ofNullable(handlers.get(type)); }
}
