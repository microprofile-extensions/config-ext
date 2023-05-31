package org.microprofileext.config.source.memory;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import jakarta.inject.Inject;
import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.MediaType;
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
import org.microprofileext.config.event.ChangeEventNotifier;

/**
 * Expose the config as a REST endpoint
 * @author <a href="mailto:phillip.kruger@phillip-kruger.com">Phillip Kruger</a>
 */
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
    
    @Inject @ConfigProperty(name = "MemoryConfigSource.notifyOnChanges", defaultValue = "true")
    private boolean notifyOnChange;
    
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
        
        Map<String,String> before = new HashMap<>(memoryConfigSource.getProperties());
        memoryConfigSource.getProperties().put(key, value);
        Map<String,String> after = new HashMap<>(memoryConfigSource.getProperties());
        if(notifyOnChange)ChangeEventNotifier.getInstance().detectChangesAndFire(before, after,MemoryConfigSource.NAME);
        
        return Response.accepted().build();
    }

    @DELETE
    @Path("/key/{key}")
    @Operation(description = "Remove the value in the Memory config source")
    @APIResponse(responseCode = "202", description = "Accepted the key, value removed")
    public Response removeValue(@Parameter(name = "key", description = "The key for this config", required = true, allowEmptyValue = false, example = "some.key")
                                @PathParam("key") String key) {
        if(!enabled)return Response.status(Response.Status.FORBIDDEN).header(REASON, NOT_ENABLED).build();
        
        Map<String,String> before = new HashMap<>(memoryConfigSource.getProperties());
        memoryConfigSource.getProperties().remove(key);
        Map<String,String> after = new HashMap<>(memoryConfigSource.getProperties());
        if(notifyOnChange)ChangeEventNotifier.getInstance().detectChangesAndFire(before, after,MemoryConfigSource.NAME);
        
        return Response.accepted().build();
    }
    
    private JsonObject toJsonObject(ConfigSource source){
        return Json.createObjectBuilder().add(String.valueOf(source.getOrdinal()), source.getName()).build();
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