package org.microprofileext.config.example;

import java.util.logging.Logger;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Provider;
import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.config.spi.ConfigSource;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

/**
 * Example Service. JAX-RS
 * @author <a href="mailto:phillip.kruger@phillip-kruger.com">Phillip Kruger</a>
 */
@RequestScoped
@Path("/")
@Consumes(MediaType.APPLICATION_JSON) @Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Example service",description = "Just some example")
public class ExampleService {
    
    private static final Logger log = Logger.getLogger(ExampleService.class.getName());
            
    @Inject
    private Config config;
    
    @Inject @ConfigProperty(name="ysomekey")
    private Provider<String> ysomekey;
    
    @GET
    @Path("/all")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(operationId = "all", description = "Getting all config keys and values")
    @APIResponse(responseCode = "200", description = "Successful, returning the key-value in JSON format")
    public Response getAll(){
        JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
        for(String property:config.getPropertyNames()){
            String value = config.getValue(property, String.class);
            arrayBuilder.add(Json.createObjectBuilder().add(property, value).build());
        }
        return Response.ok(arrayBuilder.build()).build();
    }
    
    @GET
    @Path("/key/{key}")
    @Operation(operationId = "value", description = "Getting the value for a certain config key")
    @APIResponse(responseCode = "200", description = "Successful, returning the value")
    @Produces(MediaType.TEXT_PLAIN)
    public Response getValue(@Parameter(name = "key", description = "The key for this config", required = true, allowEmptyValue = false, example = "some.key")
                                @PathParam("key") String key) {
        return Response.ok(config.getOptionalValue(key, String.class).orElse(null)).build();   
    }
    
    @GET
    @Path("/ysomekey")
    @Operation(operationId = "valueYsomevalue", description = "Getting the value for ysomekey")
    @APIResponse(responseCode = "200", description = "Successful, returning the value")
    @Produces(MediaType.TEXT_PLAIN)
    public Response getValueYsomevalue() {
        return Response.ok(ysomekey.get()).build();   
    }
    
    @GET
    @Path("/sources")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(operationId = "configSources", description = "Getting all the current config sources")
    @APIResponse(responseCode = "200", description = "Successful, returning the config sources in JSON format")
    public Response getConfigSources(){
        JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
        for(ConfigSource source:config.getConfigSources()){
            arrayBuilder.add(Json.createObjectBuilder().add(String.valueOf(source.getOrdinal()), source.getName()).build());
        }
        return Response.ok(arrayBuilder.build()).build();
    }
}
