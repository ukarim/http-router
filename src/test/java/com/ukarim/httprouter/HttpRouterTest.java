package com.ukarim.httprouter;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class HttpRouterTest {

    @Test
    void shouldFailForUnknownMethod() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            new HttpRouter<String>().addRoute("WRONG_HTTP_METHOD", "/users", "TEST_ATTACHMENT");
        });
    }

    @Test
    void shouldFailForNullAttachment() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            new HttpRouter<String>().addRoute("GET", "/users", null);
        });
    }

    @Test
    void shouldFailForNonAbsolutePaths() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            new HttpRouter<String>().addRoute("GET", "users", "TEST_ATTACHMENT");
        });
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            new HttpRouter<String>().addRoute("GET", "", "TEST_ATTACHMENT");
        });
    }

    @Test
    void shouldFailForNullPath() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            new HttpRouter<String>().addRoute("GET", null, "TEST_ATTACHMENT");
        });
    }

    @Test
    void checkRouter() {
        var attachment = "test_attachment";
        var httpRouter = new HttpRouter<String>();
        httpRouter.addRoute("GET", "/users/:login/posts", attachment);
        httpRouter.addRoute("POST", "/users/new", attachment);
        httpRouter.addRoute("GET", "/users/:login/comments/:comment_id", attachment);

        RouterMatch<String> routerMatchGet = httpRouter.match("GET", "/users/ukarim/posts");
        Assertions.assertNotNull(routerMatchGet);
        Assertions.assertEquals(attachment, routerMatchGet.getAttachment());
        Assertions.assertEquals("ukarim", routerMatchGet.getParams().getParam("login"));

        RouterMatch<String> routerMatchPost = httpRouter.match("POST", "/users/new");
        Assertions.assertNotNull(routerMatchPost);
        Assertions.assertEquals(attachment, routerMatchPost.getAttachment());

        RouterMatch<String> routerMatch2Param = httpRouter.match("GET", "/users/ukarim/comments/1024");
        Assertions.assertNotNull(routerMatch2Param);
        Assertions.assertEquals(attachment, routerMatch2Param.getAttachment());
        Assertions.assertEquals("ukarim", routerMatch2Param.getParams().getParam("login"));
        Assertions.assertEquals("1024", routerMatch2Param.getParams().getParam("comment_id"));
    }

}
