package io.swagger.api;

import io.swagger.model.BadRequestException;
import java.math.BigDecimal;
import io.swagger.model.Therapist;
import io.swagger.model.TherapistList;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import java.util.Map;
import java.util.List;
import javax.validation.constraints.*;
import javax.validation.Valid;

@Path("/therapist")

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaJAXRSSpecServerCodegen", date = "2024-01-20T15:04:50.048+05:30[Asia/Calcutta]")
public interface TherapistApi {

    @POST
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @Operation(summary = "Add a new therapist", description = "Adds a new therapist and returns the added therapist", tags={ "Therapist" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "202", description = "Successfully added a therapist", content = @Content(schema = @Schema(implementation = Therapist.class))),
        @ApiResponse(responseCode = "400", description = "Bad Request Exception", content = @Content(schema = @Schema(implementation = BadRequestException.class))),
        @ApiResponse(responseCode = "4XX", description = "Bad Request"),
        @ApiResponse(responseCode = "5XX", description = "Internal Server Error"),
        @ApiResponse(responseCode = "200", description = "Success") })
    Therapist addTherapist(@Valid Therapist body);
    @DELETE
    @Path("/{therapistId}")
    @Produces({ "application/json" })
    @Operation(summary = "Deletes the details of a therapist", description = "Deletes the details of the given therapist Id", tags={ "Therapist" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "202", description = "Successfully added a therapist", content = @Content(schema = @Schema(implementation = Therapist.class))),
        @ApiResponse(responseCode = "4XX", description = "Bad Request"),
        @ApiResponse(responseCode = "5XX", description = "Internal Server Error"),
        @ApiResponse(responseCode = "200", description = "Success") })
    Therapist deleteTherapist( @PathParam("therapistId")

 @Parameter(description = "The unique therapist ID") BigDecimal therapistId
);
    @GET
    @Path("/{therapistId}")
    @Produces({ "application/json" })
    @Operation(summary = "Get details of a single therapist", description = "Returns the details of the given therapistId", tags={ "Therapist" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "202", description = "Successfully added a therapist", content = @Content(schema = @Schema(implementation = Therapist.class))),
        @ApiResponse(responseCode = "4XX", description = "Bad Request"),
        @ApiResponse(responseCode = "5XX", description = "Internal Server Error"),
        @ApiResponse(responseCode = "200", description = "Success") })
    Therapist getTherapist( @PathParam("therapistId")

 @Parameter(description = "The unique therapist ID") BigDecimal therapistId
);
    @GET
    @Path("/search")
    @Produces({ "application/json" })
    @Operation(summary = "Get details of therapist name given by the client", description = "Returns the details of the therapist name given by client", tags={ "Therapist" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "211", description = "Successfully returned a list of therapists", content = @Content(schema = @Schema(implementation = TherapistList.class))),
        @ApiResponse(responseCode = "4XX", description = "Bad Request"),
        @ApiResponse(responseCode = "5XX", description = "Internal Server Error"),
        @ApiResponse(responseCode = "200", description = "Success") })
    TherapistList getTherapistByName(  @QueryParam("therapistName") 

 @Parameter(description = "Name of therapist being searched")  String therapistName
,  @QueryParam("therapistArea") 

 @Parameter(description = "Area of therapist being searched")  String therapistArea
,  @QueryParam("therapistType") 

 @Parameter(description = "Type of therapist being searched")  String therapistType
);
    @GET
    @Produces({ "application/json" })
    @Operation(summary = "Get list of therapists", description = "Returns the list of therapists", tags={ "Therapist" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "211", description = "Successfully returned a list of therapists", content = @Content(schema = @Schema(implementation = TherapistList.class))),
        @ApiResponse(responseCode = "4XX", description = "Bad Request"),
        @ApiResponse(responseCode = "5XX", description = "Internal Server Error"),
        @ApiResponse(responseCode = "200", description = "Success") })
    TherapistList getTherapistList();
    @PUT
    @Path("/{therapistId}")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @Operation(summary = "Update the details of a therapist", description = "Updates the details of the given therapist Id", tags={ "Therapist" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "202", description = "Successfully added a therapist", content = @Content(schema = @Schema(implementation = Therapist.class))),
        @ApiResponse(responseCode = "4XX", description = "Bad Request"),
        @ApiResponse(responseCode = "5XX", description = "Internal Server Error"),
        @ApiResponse(responseCode = "200", description = "Success") })
    Therapist updateTherapist(@Valid Therapist body, @PathParam("therapistId")

 @Parameter(description = "The unique therapist ID") BigDecimal therapistId
);}