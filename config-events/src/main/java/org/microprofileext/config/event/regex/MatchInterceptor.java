package org.microprofileext.config.event.regex;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Priority;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import lombok.extern.java.Log;
import org.microprofileext.config.event.ChangeEvent;

@Log    
@Match(regex = "")
@Interceptor
@Priority(100)
public class MatchInterceptor {

    @AroundInvoke
    public Object observer(InvocationContext ctx) throws Exception {
        
        Match matchAnnotation = ctx.getMethod().getAnnotation(Match.class);
        Field onField = matchAnnotation.onField();
        String regex = matchAnnotation.regex();
        
        Optional<ChangeEvent> posibleChangeEvent = getChangeEvent(ctx);
        
        if(posibleChangeEvent.isPresent()){
            ChangeEvent changeEvent = posibleChangeEvent.get();            
            String value = getValueToApplyRegexOn(changeEvent,onField);
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(value);
            boolean b = matcher.matches();  
            if(!b)return null;
        }
        return ctx.proceed();
    }
    
    private String getValueToApplyRegexOn(ChangeEvent changeEvent,Field onField){
        String value = null;
        switch (onField){
            case key: 
                value = changeEvent.getKey();
                break;
            case fromSource:
                value = changeEvent.getFromSource();
                break;
            case newValue:
                value = changeEvent.getNewValue();
                break;
            case oldValue:
                value = changeEvent.getOldValue().orElse("");
        }
       
        return value;
    }
    
    private Optional<ChangeEvent> getChangeEvent(InvocationContext ctx){
        Object[] parameters = ctx.getParameters();
        
        for(Object parameter:parameters){
            if(parameter.getClass().equals(ChangeEvent.class)){
                ChangeEvent changeEvent = (ChangeEvent)parameter;
                return Optional.of(changeEvent);
            }
        }
        return Optional.empty();
    }   
}