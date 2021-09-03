package com.santander.meeting.meetingroom;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.http.HttpRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.stream.Collectors;

public abstract class AppUtils {
    public static String getLowerCaseAlphanumericCharFromIndex(int index) {
        if ( index > 25 && index <= 34 ) {
            return Integer.valueOf( 34 - index ).toString();
        } else if ( index >= 0 ) {
            return Character.toString( 97 + index );
        } else {
            System.err.println( ( String.format( "Index out of range for alphanumeric characters (value must be from 0 to 34, %d " +
                                                         "received).", index ) ) );

            return "a";
        }
    }

    public static String getLowerCaseHexadecimalCharFromIndex(int index) {
        if ( index >= 0 && index <= 9 ) {
            return Integer.valueOf( index ).toString();
        } else if ( index <= 15 ) {
            return Character.toString( 97 + ( index - 9 ) );
        } else {
            System.err.println( ( String.format( "Index out of range for hexadecimal characters (value must be from 0 to 15, %d " +
                                                         "received).", index ) ) );

            return "a";
        }
    }

    public static String randomLowerCaseStr(int length) {
        return String.valueOf(
                new Random().ints( length, 97, 122 )
                            .mapToObj( Character::toString )
                            .collect( Collectors.joining() )
                            .toCharArray()
        );
    }

    public static String randomLowerCaseAlphanumericStr(int length) {
        return new Random().ints( length, 0, 34 )
                           .mapToObj( AppUtils::getLowerCaseAlphanumericCharFromIndex )
                           .collect( Collectors.joining( "" ) );
    }

    public static String randomHexadecimalStr(int length) {
        return new Random().ints( length, 0, 15 )
                           .mapToObj( AppUtils::getLowerCaseHexadecimalCharFromIndex )
                           .collect( Collectors.joining( "" ) );
    }

    public static String randomUUID() {
        return String.join( "-", Arrays.asList(
                randomHexadecimalStr( 8 ),
                randomHexadecimalStr( 4 ),
                randomHexadecimalStr( 4 ),
                randomHexadecimalStr( 4 ),
                randomHexadecimalStr( 12 )
        ) );
    }

    public static String randomSmallUUID() {
        return String.join( "-", Arrays.asList(
                randomHexadecimalStr( 4 ),
                randomHexadecimalStr( 4 ),
                randomHexadecimalStr( 6 )
        ) );
    }

    public static byte[] randomBytes(int length) {
        byte[] b = new byte[length];
        new Random().nextBytes( b );

        return b;
    }

    public static void printObj(Object value) {
        System.out.println( AppUtils.serializeObj( value ) );
    }

    public static String serializeObj(Object value) {
        ObjectMapper mapper = new ObjectMapper();

        try {
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString( value );
        } catch ( JsonProcessingException e ) {
            e.printStackTrace();
            return "null";
        }
    }
}
