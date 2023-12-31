package com.example.activitymicroservice.authentication;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class JwtRequestFilterTests {
    private final PrintStream standardOut = System.err;
    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();

    private transient JwtRequestFilter jwtRequestFilter;

    private transient HttpServletRequest mockRequest;
    private transient HttpServletResponse mockResponse;
    private transient FilterChain mockFilterChain;

    private transient JwtTokenVerifier mockJwtTokenVerifier;

    /**
     * Set up mocks.
     */
    @BeforeEach
    public void setup() {
        mockRequest = Mockito.mock(HttpServletRequest.class);
        mockResponse = Mockito.mock(HttpServletResponse.class);
        mockFilterChain = Mockito.mock(FilterChain.class);
        mockJwtTokenVerifier = Mockito.mock(JwtTokenVerifier.class);

        jwtRequestFilter = new JwtRequestFilter(mockJwtTokenVerifier);

        SecurityContextHolder.getContext().setAuthentication(null);

        System.setErr(new PrintStream(outputStreamCaptor));
    }

    /**
     * Cleanup afterEach method.
     *
     * @throws ServletException - error thrown by `doFilter`
     * @throws IOException      - error thrown by `doFilter`
     */
    @AfterEach
    public void assertChainContinues() throws ServletException, IOException {
        verify(mockFilterChain).doFilter(mockRequest, mockResponse);
        verifyNoMoreInteractions(mockFilterChain);

        System.setErr(standardOut);
    }

    @Test
    public void correctToken() throws ServletException, IOException {
        // Arrange
        String token = "randomtoken123";
        String user = "user123";
        when(mockRequest.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(mockJwtTokenVerifier.validateToken(token)).thenReturn(true);
        when(mockJwtTokenVerifier.getUserIdFromToken(token)).thenReturn(user);

        // Act
        jwtRequestFilter.doFilterInternal(mockRequest, mockResponse, mockFilterChain);

        // Assert
        assertThat(SecurityContextHolder.getContext().getAuthentication().getName())
                .isEqualTo(user);
    }

    @Test
    public void invalidToken() throws ServletException, IOException {
        // Arrange
        String token = "randomtoken123";
        String user = "user123";
        when(mockRequest.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(mockJwtTokenVerifier.validateToken(token)).thenReturn(false);
        when(mockJwtTokenVerifier.getUserIdFromToken(token)).thenReturn(user);

        // Act
        jwtRequestFilter.doFilterInternal(mockRequest, mockResponse, mockFilterChain);

        // Assert
        assertThat(SecurityContextHolder.getContext().getAuthentication())
                .isNull();
    }

    /**
     * Parameterized test for various token verification exceptions.
     *
     * @param throwable the exception to be tested
     */
    @ParameterizedTest
    @MethodSource("tokenVerificationExceptionGenerator")
    public void tokenVerificationException(Class<? extends Throwable> throwable)
            throws ServletException, IOException {
        // Arrange
        String token = "randomtoken123";
        String user = "user123";
        when(mockRequest.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(mockJwtTokenVerifier.validateToken(token)).thenThrow(throwable);
        when(mockJwtTokenVerifier.getUserIdFromToken(token)).thenReturn(user);

        // Act
        jwtRequestFilter.doFilterInternal(mockRequest, mockResponse, mockFilterChain);

        // Assert
        assertThat(SecurityContextHolder.getContext().getAuthentication())
                .isNull();
    }

    private static Stream<Arguments> tokenVerificationExceptionGenerator() {
        return Stream.of(
                Arguments.of(ExpiredJwtException.class),
                Arguments.of(IllegalArgumentException.class),
                Arguments.of(JwtException.class)

        );
    }

    @Test
    public void nullToken() throws ServletException, IOException {
        // Arrange
        when(mockRequest.getHeader("Authorization")).thenReturn(null);

        // Act
        jwtRequestFilter.doFilterInternal(mockRequest, mockResponse, mockFilterChain);

        // Assert
        assertThat(SecurityContextHolder.getContext().getAuthentication())
                .isNull();
    }

    @Test
    public void invalidPrefix() throws ServletException, IOException {
        // Arrange
        String token = "randomtoken123";
        String user = "user123";
        when(mockRequest.getHeader("Authorization")).thenReturn("Bearer1 " + token);
        when(mockJwtTokenVerifier.validateToken(token)).thenReturn(true);
        when(mockJwtTokenVerifier.getUserIdFromToken(token)).thenReturn(user);

        // Act
        jwtRequestFilter.doFilterInternal(mockRequest, mockResponse, mockFilterChain);

        // Assert
        assertThat(SecurityContextHolder.getContext().getAuthentication())
                .isNull();
    }

    @Test
    public void noPrefix() throws ServletException, IOException {
        // Arrange
        String token = "randomtoken123";
        String user = "user123";
        when(mockRequest.getHeader("Authorization")).thenReturn(token);
        when(mockJwtTokenVerifier.validateToken(token)).thenReturn(true);
        when(mockJwtTokenVerifier.getUserIdFromToken(token)).thenReturn(user);

        // Act
        jwtRequestFilter.doFilterInternal(mockRequest, mockResponse, mockFilterChain);

        // Assert
        assertThat(SecurityContextHolder.getContext().getAuthentication())
                .isNull();
    }

    @Test
    public void authorizationHeaderNullTest() throws ServletException, IOException {
        // Arrange
        String token = "randomToken";
        String user = "razvan";
        when(mockRequest.getHeader("Authorization")).thenReturn(token);
        when(mockJwtTokenVerifier.validateToken(token)).thenThrow(ExpiredJwtException.class);
        when(mockJwtTokenVerifier.getUserIdFromToken(token)).thenReturn(user);

        // Act
        jwtRequestFilter.doFilterInternal(mockRequest, mockResponse, mockFilterChain);

        // Assert
        verify(mockFilterChain).doFilter(mockRequest, mockResponse);
        assertThat(outputStreamCaptor.toString().trim()).isEqualTo("Invalid authorization header");
    }

    @Test
    public void expiredJwtExceptionTest1() throws ServletException, IOException {
        // Arrange
        String token = "randomToken";
        String user = "razvan";
        when(mockRequest.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(mockJwtTokenVerifier.validateToken(token)).thenThrow(ExpiredJwtException.class);
        when(mockJwtTokenVerifier.getUserIdFromToken(token)).thenReturn(user);

        // Act
        jwtRequestFilter.doFilterInternal(mockRequest, mockResponse, mockFilterChain);

        // Assert
        verify(mockFilterChain).doFilter(mockRequest, mockResponse);
        assertThat(outputStreamCaptor.toString().trim()).isEqualTo("JWT token has expired.");
    }

    @Test
    public void expiredJwtExceptionTest2() throws ServletException, IOException {
        // Arrange
        String token = "randomToken";
        String user = "razvan";
        when(mockRequest.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(mockJwtTokenVerifier.validateToken(token)).thenReturn(true);
        when(mockJwtTokenVerifier.getUserIdFromToken(token)).thenThrow(ExpiredJwtException.class);

        // Act
        jwtRequestFilter.doFilterInternal(mockRequest, mockResponse, mockFilterChain);

        // Assert
        verify(mockFilterChain).doFilter(mockRequest, mockResponse);
        assertThat(outputStreamCaptor.toString().trim()).isEqualTo("JWT token has expired.");
    }

    @Test
    public void illegalTokenTest() throws ServletException, IOException {
        // Arrange
        String token = "wrongToken";
        String user = "razvan";
        when(mockRequest.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(mockJwtTokenVerifier.validateToken(token)).thenThrow(IllegalArgumentException.class);
        when(mockJwtTokenVerifier.getUserIdFromToken(token)).thenReturn(user);

        // Act
        jwtRequestFilter.doFilterInternal(mockRequest, mockResponse, mockFilterChain);

        // Assert
        assertThat(outputStreamCaptor.toString().trim()).isEqualTo("Unable to parse JWT token");
    }
}
