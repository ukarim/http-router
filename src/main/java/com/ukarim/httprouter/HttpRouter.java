package com.ukarim.httprouter;

import java.util.*;

public class HttpRouter<T> {

    private static final Set<String> KNOWN_HTTP_METHODS = new HashSet<>(Arrays.asList(
            "CONNECT",
            "DELETE",
            "GET",
            "HEAD",
            "OPTIONS",
            "PATCH",
            "POST",
            "PUT",
            "TRACE"
    ));

    private final Map<String, Node<T>> routesByHttpMethod = new HashMap<>();


    public RouterMatch<T> match(String httpMethod, String path) {
        Node<T> nodeToInspect = routesByHttpMethod.get(httpMethod);
        if (nodeToInspect == null) {
            return null;
        }

        List<String> pathSegments = UrlUtil.toPathSegments(path);
        int pathSegmentsCount = pathSegments.size();
        Node<T> matchedNode = null;
        var paramsContainer = new Params();

        for (int i = 0; i < pathSegmentsCount; i++) {
            String pathSegment = pathSegments.get(i);

            var childNode = nodeToInspect.getChildNode(pathSegment);
            if (childNode != null) {
                nodeToInspect = childNode;
            } else {
                var paramChildNode = nodeToInspect.getParamChildNode();
                if (paramChildNode == null) {
                    // totally unmatched
                    break;
                } else {
                    String variableName = paramChildNode.getName();
                    paramsContainer.addParam(variableName, pathSegment);
                    nodeToInspect = paramChildNode;
                }
            }

            if (i == (pathSegmentsCount - 1)) {
                matchedNode = nodeToInspect;
            }
        }

        if (matchedNode == null) {
            return null;
        }

        T attachment = matchedNode.getAttachment();
        if (attachment == null) {
            return null;
        }

        return new RouterMatch<>(paramsContainer, attachment);
    }

    public HttpRouter<T> addRoute(String httpMethod, String path, T attachment) {
        if (path == null) {
            throw new IllegalArgumentException("Path cannot be null");
        }
        if (!path.startsWith("/")) {
            throw new IllegalArgumentException("Path must start with '/'");
        }
        if (!KNOWN_HTTP_METHODS.contains(httpMethod)) {
            throw new IllegalArgumentException("Unknown http method '" + httpMethod + "'");
        }
        if (attachment == null) {
            throw new IllegalArgumentException("Attachment cannot be null");
        }

        Node<T> nodeToInspect = routesByHttpMethod.get(httpMethod); // start from root node
        if (nodeToInspect == null) {
            // create root node if necessary
            nodeToInspect = new Node<>(null);
            routesByHttpMethod.put(httpMethod, nodeToInspect);
        }

        List<String> pathSegments = UrlUtil.toPathSegments(path);
        int pathSegmentsCount = pathSegments.size();
        for (int i = 0; i < pathSegmentsCount; i++) {
            String currentSegment = pathSegments.get(i);
            boolean isParametrizedSegment = currentSegment.startsWith(":");
            String currentSegmentName = isParametrizedSegment ? currentSegment.substring(1) : currentSegment;

            if (isParametrizedSegment) {
                var paramChildNode = nodeToInspect.getParamChildNode();
                if (paramChildNode != null) {
                    if (!paramChildNode.getName().equals(currentSegmentName)) {
                        /* Throw exception for cases like '/users/:login/posts' and '/users/:username/followers' */
                        /* In such cases params must have the same name. For example '/users/:login/posts' and '/users/:login/followers' */
                        String error = String.format("Path variables at the same position must have the same name. Problem with parameters :%s and :%s", currentSegmentName, paramChildNode.getName());
                        throw new IllegalArgumentException(error);
                    }
                    nodeToInspect = paramChildNode;
                } else {
                    var newNode = new Node<T>(currentSegmentName);
                    nodeToInspect.setParamChildNode(newNode);
                    nodeToInspect = newNode;
                }
            } else {
                var childNode = nodeToInspect.getChildNode(currentSegmentName);
                if (childNode != null) {
                    nodeToInspect = childNode;
                } else {
                    var newNode = new Node<T>(currentSegmentName);
                    nodeToInspect.addChildNode(newNode);
                    nodeToInspect = newNode;
                }
            }

            if (i == (pathSegmentsCount - 1)) {
                // if it's latest segment then save the attachment
                nodeToInspect.setAttachment(attachment);
            }
        }

        return this;
    }

    public HttpRouter<T> GET(String path, T attachment) {
        return addRoute("GET", path, attachment);
    }

    public HttpRouter<T> POST(String path, T attachment) {
        return addRoute("POST", path, attachment);
    }

    public HttpRouter<T> PUT(String path, T attachment) {
        return addRoute("PUT", path, attachment);
    }

    public HttpRouter<T> DELETE(String path, T attachment) {
        return addRoute("DELETE", path, attachment);
    }
}
