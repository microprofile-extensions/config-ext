package org.microprofileext.config.source.memory;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
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
    
    private ConfigSource memoryConfigSource;
    
    @Inject @ConfigProperty(name = "microprofile-ext.config.source.memory.enabled", defaultValue = "true")
    private boolean enabled;
    
    @PostConstruct
    public void init(){
        this.memoryConfigSource = getMemoryConfigSource();
    }
    
    @GET
    @Path("/all")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Getting all config keys and values")
    @APIResponse(responseCode = "200", description = "Successful, returning the key-value in JSON format")
    public Response getAll(){
        if(!enabled)return Response.status(Response.Status.FORBIDDEN).header(REASON, NOT_ENABLED).build();
        JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
        for(String property:config.getPropertyNames()){
            String value = config.getValue(property, String.class);
            arrayBuilder.add(Json.createObjectBuilder().add(property, value).build());
        }
        return Response.ok(arrayBuilder.build()).build();
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
            arrayBuilder.add(Json.createObjectBuilder().add(String.valueOf(source.getOrdinal()), source.getName()).build());
        }
        return Response.ok(arrayBuilder.build()).build();
    }
    
    @GET
    @Path("/key/{key}")
    @Operation(description = "Getting the value for a certain config key")
    @APIResponse(responseCode = "200", description = "Successful, returning the value")
    @Produces(MediaType.TEXT_PLAIN)
    public Response getValue(@Parameter(name = "key", description = "The key for this config", required = true, allowEmptyValue = false, example = "some.key")
                             @PathParam("key") String key) {
        if(!enabled)return Response.status(Response.Status.FORBIDDEN).header(REASON, NOT_ENABLED).build();
        return Response.ok(config.getValue(key, String.class)).build();
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
        memoryConfigSource.getProperties().remove(key);
        return Response.accepted().build();
    }
    
    private ConfigSource getMemoryConfigSource() {
        for(ConfigSource configSource:config.getConfigSources()){
            if(configSource.getName().equals(MemoryConfigSource.NAME))return configSource;
        }
        return null;
    }
    
    private static final String REASON = "reason";
    private static final String NOT_ENABLED = "The Memory config source REST API is disabled [microprofile-ext.config.source.memory.enabled=false]"; 
}
