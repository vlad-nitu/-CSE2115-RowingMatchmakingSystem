package nl.tudelft.cse.sem.template.user.authentication;

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
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class JwtRequestFilterTests {
    private transient JwtRequestFilter jwtRequestFilter;

    private transient HttpServletRequest mockRequest;
    private transient HttpServletResponse mockResponse;
    private transient FilterChain mockFilterChain;

    private transient JwtTokenVerifier mockJwtTokenVerifier;

    // two extra attributes to receive the messages that are printed
    private final PrintStream standardOut = System.err;
    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();

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
     * Method to make sure that after each test the chain can continue and the
     * System.err.println() is correctly printing into the console again.
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
        String errorMessage1 = "JWT token has expired.";
        String errorMessage2 = "Unable to parse JWT token";
        String outputMessage = outputStreamCaptor.toString().trim();
        assertTrue(outputMessage.equals(errorMessage2)
                || outputMessage.equals(errorMessage1));
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

        String errorMessage = "Invalid authorization header";
        String outputMessage = outputStreamCaptor.toString().trim();
        assertTrue(outputMessage.equals(errorMessage));
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

        String errorMessage = "Invalid authorization header";
        String outputMessage = outputStreamCaptor.toString().trim();
        assertTrue(outputMessage.equals(errorMessage));
    }
}
