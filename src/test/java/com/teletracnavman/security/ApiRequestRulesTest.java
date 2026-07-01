package com.teletracnavman.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import jakarta.servlet.http.HttpServletRequest;

class ApiRequestRulesTest {

    @ParameterizedTest
    @ValueSource(strings = {"/echo", "/device", "/nocontent", "/api/v1/nocontent"})
    void isAllowedPostEndpoint_allowsKnownPostPaths(String uri) {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getMethod()).thenReturn("POST");
        when(request.getRequestURI()).thenReturn(uri);

        assertThat(ApiRequestRules.isAllowedPostEndpoint(request)).isTrue();
    }

    @Test
    void isAllowedPostEndpoint_rejectsUnknownPostPath() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getMethod()).thenReturn("POST");
        when(request.getRequestURI()).thenReturn("/unknown");

        assertThat(ApiRequestRules.isAllowedPostEndpoint(request)).isFalse();
    }

    @Test
    void isAllowedPostEndpoint_rejectsPathThatOnlyContainsNocontentSubstring() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getMethod()).thenReturn("POST");
        when(request.getRequestURI()).thenReturn("/not-nocontent");

        assertThat(ApiRequestRules.isAllowedPostEndpoint(request)).isFalse();
    }

    @Test
    void isAllowedPostEndpoint_rejectsNonPostRequestEvenForKnownPath() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getMethod()).thenReturn("GET");

        assertThat(ApiRequestRules.isAllowedPostEndpoint(request)).isFalse();
    }

    @Test
    void isH2Console_matchesH2ConsolePaths() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/h2-console/login");

        assertThat(ApiRequestRules.isH2Console(request)).isTrue();
    }

    @Test
    void isH2Console_rejectsApiPaths() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/echo");

        assertThat(ApiRequestRules.isH2Console(request)).isFalse();
    }
}
