package com.example.system.nablarch.handler.csrf;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import nablarch.common.web.WebConfigFinder;
import nablarch.common.web.session.SessionEntry;
import nablarch.common.web.session.SessionManager;
import nablarch.common.web.session.SessionStore;
import nablarch.core.repository.SystemRepository;
import nablarch.fw.ExecutionContext;
import nablarch.fw.web.HttpRequest;
import nablarch.fw.web.HttpResponse;
import nablarch.fw.web.RestMockHttpRequestBuilder;
import nablarch.fw.web.handler.CsrfTokenVerificationHandler;
import nablarch.fw.web.handler.csrf.UUIDv4CsrfTokenGenerator;

public class CsrfTokenEndpointHandlerTest {

    private CsrfTokenEndpointHandler sut;
    private CsrfTokenVerificationHandler csrfTokenVerificationHandler;
    private TestCsrfTokenGenerator csrfTokenHolder;

    @BeforeEach
    void init() {
        sut = new CsrfTokenEndpointHandler();

        //CsrfTokenVerificationHandlerの準備
        //SessionManagerが必要だけど、このテストではSessionStoreHandlerは通らないのでSessionStoreはダミー実装
        SessionManager sessionManager = new SessionManager();
        sessionManager.setDefaultStoreName("noop");
        sessionManager.setAvailableStores(List.of(new NoOperationSessionStore()));
        SystemRepository.load(() -> Map.of("sessionManager", sessionManager));
        csrfTokenHolder = new TestCsrfTokenGenerator();
        csrfTokenVerificationHandler = new CsrfTokenVerificationHandler();
        csrfTokenVerificationHandler.setCsrfTokenGenerator(csrfTokenHolder);
    }

    @Test
    void testDefaultJSONKeys() throws Exception {
        //リクエストを実行
        HttpRequest request = new RestMockHttpRequestBuilder().get("/path/to/csrf_token");
        ExecutionContext context = new ExecutionContext();
        context.addHandler(csrfTokenVerificationHandler);
        context.addHandler(sut);
        HttpResponse response = context.handleNext(request);

        //アサーション
        assertEquals("application/json", response.getContentType());
        JsonNode root = new ObjectMapper().readTree(response.getBodyString());
        assertEquals(WebConfigFinder.getWebConfig().getCsrfTokenHeaderName(), root.get("csrfTokenHeaderName").textValue());
        assertEquals(WebConfigFinder.getWebConfig().getCsrfTokenParameterName(), root.get("csrfTokenParameterName").textValue());
        assertEquals(csrfTokenHolder.csrfToken, root.get("csrfTokenValue").textValue());
    }

    @Test
    void testJSONKeys() throws Exception {

        String headerNameKey = "aaa";
        String parameterNameKey = "bbb";
        String valueKey = "ccc";

        sut.setHeaderNameKey(headerNameKey);
        sut.setParameterNameKey(parameterNameKey);
        sut.setValueKey(valueKey);

        HttpRequest request = new RestMockHttpRequestBuilder().get("/path/to/csrf_token");
        ExecutionContext context = new ExecutionContext();
        context.addHandler(csrfTokenVerificationHandler);
        context.addHandler(sut);
        HttpResponse response = context.handleNext(request);

        assertEquals("application/json", response.getContentType());
        JsonNode root = new ObjectMapper().readTree(response.getBodyString());
        assertEquals(WebConfigFinder.getWebConfig().getCsrfTokenHeaderName(), root.get(headerNameKey).textValue());
        assertEquals(WebConfigFinder.getWebConfig().getCsrfTokenParameterName(), root.get(parameterNameKey).textValue());
        assertEquals(csrfTokenHolder.csrfToken, root.get(valueKey).textValue());
    }

    static class TestCsrfTokenGenerator extends UUIDv4CsrfTokenGenerator {

        String csrfToken;

        @Override
        public String generateToken() {
            return csrfToken = super.generateToken();
        }
    }

    static class NoOperationSessionStore extends SessionStore {

        public NoOperationSessionStore() {
            super("noop");
        }

        @Override
        public List<SessionEntry> load(String sessionId, ExecutionContext executionContext) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void save(String sessionId, List<SessionEntry> entries, ExecutionContext executionContext) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void delete(String sessionId, ExecutionContext executionContext) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void invalidate(String sessionId, ExecutionContext executionContext) {
            throw new UnsupportedOperationException();
        }
    }
}
