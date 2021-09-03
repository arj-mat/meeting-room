/*
 * Biblioteca para obter dados JSON por requisições HTTP com modelagem de classes em Java.
 * Autor: Mateus Araújo.
 * https://github.com/arj-mat
 */

package com.santander.meeting.meetingroom.external;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.santander.meeting.meetingroom.AppUtils;

import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Supplier;

public abstract class ExternalRequest<O> {
    private Supplier<O> resultModelSupplier;

    public ExternalRequest(Supplier<O> resultModelSupplier) {
        this.resultModelSupplier = resultModelSupplier;
    }

    protected ExternalResult<O> _execute(HttpRequest request) {
        HttpClient client = HttpClient.newBuilder().build();

        Integer responseCode = -1;

        try {
            HttpResponse<String> response = client.send( request, HttpResponse.BodyHandlers.ofString() );

            responseCode = response.statusCode();

            ExternalResult<O> externalResult = new ExternalResult<>( responseCode );

            try {
                ObjectMapper mapper = new ObjectMapper();
                externalResult.data = Optional.ofNullable( (O) mapper.readValue( response.body(),
                                                                                 resultModelSupplier.get().getClass() ) );

                if ( responseCode != 200 && responseCode != 304 ) {
                    System.err.printf(
                            "[%s] [%s] [%s] [%d]\n%s\n",
                            this.getClass().getName(),
                            request.method(),
                            request.uri(),
                            responseCode,
                            AppUtils.serializeObj( externalResult.data.get() )
                    );
                }
            } catch ( Exception e ) {
                System.err.printf(
                        "[%s] [%s] [%s] [%d]\nFailed to parse JSON:\n",
                        this.getClass().getName(),
                        request.method(),
                        request.uri(),
                        responseCode
                );

                System.err.println( e );
            }

            return externalResult;
        } catch ( Exception e ) {
            System.err.printf(
                    "[%s] [%s] [%s] [%s]\nFailed to execute.\n",
                    this.getClass().getName(),
                    request.method(),
                    request.uri(),
                    ( responseCode != -1 ?
                      responseCode.toString() :
                      "no response" )
            );

            e.printStackTrace();

            return new ExternalResult<O>( responseCode );
        }
    }

    protected HttpRequest.BodyPublisher mapToJSONBody(Map<String, String> source) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return HttpRequest.BodyPublishers.ofString( mapper.writer().writeValueAsString( source ) );
    }

    protected HttpRequest.BodyPublisher mapToFormData(Map<String, String> source) throws JsonProcessingException {
        List<String> formLines = new ArrayList<>();

        source.forEach( (key, value) -> {
            formLines.add(
                    String.format(
                            "%s=%s",
                            URLEncoder.encode( key, StandardCharsets.UTF_8 ),
                            URLEncoder.encode( value, StandardCharsets.UTF_8 )

                    )
            );
        } );

        return HttpRequest.BodyPublishers.ofString( String.join( "&", formLines ) );
    }
}
