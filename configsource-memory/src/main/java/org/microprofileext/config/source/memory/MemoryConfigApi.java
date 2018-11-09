package org.microprofileext.config.source.memory;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.microprofileext.config.event.ChangeEvent;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.MediaType;
import lombok.extern.java.Log;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.config.spi.ConfigSource;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.microprofileext.config.cdi.Name;
import org.microprofileext.config.cdi.ConfigSourceMap;
import org.microprofileext.config.event.Type;
import org.microprofileext.config.event.KeyFilter;
import org.microprofileext.config.event.SourceFilter;
import org.microprofileext.config.event.TypeFilter;

/**
 * Expose the config as a REST endpoint
 * @author <a href="mailto:phillip.kruger@phillip-kruger.com">Phillip Kruger</a>
 */
@Log
@Path("/microprofile-ext/memoryconfigsource")
@Tag(name = "MicroProfile Memory Config", description = "In-memory config source for MicroProfile")
public class MemoryConfigApi {
 
    @Inject
    private Config config;
    
    @Inject @Name(MemoryConfigSource.NAME)
    private ConfigSource memoryConfigSource;
    
    @Inject @ConfigSourceMap
    private Map<String,ConfigSource> configSourceMap;
    
    @Inject @ConfigProperty(name = "MemoryConfigSource.enabled", defaultValue = "true")
    private boolean enabled;
    
    @Inject
    private Event<ChangeEvent> broadcaster;
    
    @GET
    @Path("/all")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Getting all config keys and values")
    @APIResponse(responseCode = "200", description = "Successful, returning the key-value in JSON format")
    public Response getAll(@Parameter(name = "configsource", description = "Only look at a certain config source", required = false, allowEmptyValue = true, example = "MemoryConfigSource")
                                @QueryParam("configsource") String configsource){
        if(!enabled)return Response.status(Response.Status.FORBIDDEN).header(REASON, NOT_ENABLED).build();
        
        if(configsource==null || configsource.isEmpty()){
            return allToJson();
        }else{
            return allForConfigSourceToJson(configsource);
        }
    }
    
    @GET
    @Path("/sources")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Getting all the current config sources")
    @APIResponse(responseCode = "200", description = "Successful, returning the config sources in JSON format")
    public Response getConfigSources(){
        if(!enabled)return Response.status(Response.Status.FORBIDDEN).header(REASON, NOT_ENABLED).build();
        JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
        for(ConfigSource source:config.getConfigSources()){
            arrayBuilder.add(toJsonObject(source));
        }
        return Response.ok(arrayBuilder.build()).build();
        
    }
    
    @GET
    @Path("/source/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Getting the config source with a certain name")
    @APIResponse(responseCode = "200", description = "Successful, returning the config source in JSON format")
    public Response getConfigSource(@Parameter(name = "name", description = "The name for this config source", required = true, allowEmptyValue = false, example = "MemoryConfigSource")
                             @PathParam("name") String name){
        if(!enabled)return Response.status(Response.Status.FORBIDDEN).header(REASON, NOT_ENABLED).build();
        if(!configSourceMap.containsKey(name))return Response.noContent().header(REASON, NO_SUCH_CONFIGSOURCE).build();
            
        ConfigSource source = configSourceMap.get(name);
        return Response.ok(toJsonObject(source)).build();
    }
    
    @GET
    @Path("/key/{key}")
    @Operation(description = "Getting the value for a certain config key")
    @APIResponse(responseCode = "200", description = "Successful, returning the value")
    @Produces(MediaType.TEXT_PLAIN)
    public Response getValue(@Parameter(name = "key", description = "The key for this config", required = true, allowEmptyValue = false, example = "some.key")
                                @PathParam("key") String key,
                             @Parameter(name = "configsource", description = "Only look at a certain config source", required = false, allowEmptyValue = true, example = "MemoryConfigSource")
                                @QueryParam("configsource") String configsource) {
        if(!enabled)return Response.status(Response.Status.FORBIDDEN).header(REASON, NOT_ENABLED).build();
        
        if(configsource==null || configsource.isEmpty()){
            return Response.ok(config.getOptionalValue(key, String.class).orElse(null)).build();
        }else{
            if(!configSourceMap.containsKey(configsource))return Response.noContent().header(REASON, NO_SUCH_CONFIGSOURCE).build();
            ConfigSource source = configSourceMap.get(configsource);
            return Response.ok(source.getValue(key)).build();
        }
    }
    
    @PUT
    @Path("/key/{key}")
    @Operation(description = "Change or add a new key")
    @APIResponse(responseCode = "202", description = "Accepted the key, value updated")
    @Consumes(MediaType.TEXT_PLAIN)
    public Response setValue(@Parameter(name = "key", description = "The key for this config", required = true, allowEmptyValue = false, example = "some.key")
                             @PathParam("key") String key, 
                             @RequestBody(description = "Value for this key") String value) {
        if(!enabled)return Response.status(Response.Status.FORBIDDEN).header(REASON, NOT_ENABLED).build();
        
        fireEvent(key,value);
        
        memoryConfigSource.getProperties().put(key, value);
        return Response.accepted().build();
    }

    @DELETE
    @Path("/key/{key}")
    @Operation(description = "Remove the value in the Memory config source")
    @APIResponse(responseCode = "202", description = "Accepted the key, value removed")
    public Response removeValue(@Parameter(name = "key", description = "The key for this config", required = true, allowEmptyValue = false, example = "some.key")
                                @PathParam("key") String key) {
        if(!enabled)return Response.status(Response.Status.FORBIDDEN).header(REASON, NOT_ENABLED).build();
        
        fireEvent(Type.REVERT,key,null);
        
        memoryConfigSource.getProperties().remove(key);
        return Response.accepted().build();
    }
    
    private JsonObject toJsonObject(ConfigSource source){
        return Json.createObjectBuilder().add(String.valueOf(source.getOrdinal()), source.getName()).build();
    }
    
    private void fireEvent(String key,String newValue){
        fireEvent(null,key,newValue);
    }
    
    private void fireEvent(Type type,String key,String newValue){
        String oldValue = config.getOptionalValue(key, String.class).orElse(null);
        
        if(type==null){
            if(oldValue==null || oldValue.isEmpty()){
                type = Type.NEW;
            }else{
                type = Type.OVERRIDE;
            }
        }
        
        List<Annotation> annotationList = new ArrayList<>();
        annotationList.add(new TypeFilter.TypeFilterLiteral(type));
        annotationList.add(new KeyFilter.KeyFilterLiteral(key));
        annotationList.add(new SourceFilter.SourceFilterLiteral(MemoryConfigSource.NAME));
        
        Event<ChangeEvent> selected = broadcaster.select(annotationList.toArray(new Annotation[annotationList.size()]));
        selected.fire(new ChangeEvent(type, key, getOptionalOldValue(oldValue), newValue));
    }
    
    private Optional<String> getOptionalOldValue(String oldValue){
        if(oldValue==null || oldValue.isEmpty())return Optional.empty();
        return Optional.of(oldValue);
    }
    
    private Response allToJson() {
        JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
        for(String property:config.getPropertyNames()){
            String value = config.getValue(property, String.class);
            arrayBuilder.add(Json.createObjectBuilder().add(property, value).build());
        }
        return Response.ok(arrayBuilder.build()).build();
    }
    
    private Response allForConfigSourceToJson(String configsource) {
        if(configSourceMap.containsKey(configsource)){
            ConfigSource source = configSourceMap.get(configsource);
            Set<Map.Entry<String, String>> propertiesSet = source.getProperties().entrySet();
            JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
            for(Map.Entry<String, String> propertyEntry:propertiesSet){
                arrayBuilder.add(Json.createObjectBuilder().add(propertyEntry.getKey(), propertyEntry.getValue()).build());
            }
            return Response.ok(arrayBuilder.build()).build();
        }
        return Response.noContent().header(REASON, NO_SUCH_CONFIGSOURCE).build();
        
    }
    
    private static final String REASON = "reason";
    private static final String NOT_ENABLED = "The Memory config source REST API is disabled [MemoryConfigSource.enabled=false]"; 
    private static final String NO_SUCH_CONFIGSOURCE = "No content source with that name available"; 
}