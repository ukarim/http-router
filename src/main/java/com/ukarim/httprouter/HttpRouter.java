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
            List<Node<T>> childNodes = nodeToInspect.getChildNodes();
            Node<T> paramNode = null;
            boolean matched = false;

            for (Node<T> childNode : childNodes) {
                if (paramNode == null && childNode.isParam()) {
                    paramNode = childNode;
                    continue;
                }

                if (pathSegment.equals(childNode.getName())) {
                    nodeToInspect = childNode;
                    matched = true;
                    break;
                }
            }

            if (!matched) {
                if (paramNode != null) {
                    String variableName = paramNode.getName();
                    paramsContainer.addParam(variableName, pathSegment);
                    nodeToInspect = paramNode;
                } else {
                    // totally unmatched. no such route
                    break;
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
            nodeToInspect = new Node<>(null, false);
            routesByHttpMethod.put(httpMethod, nodeToInspect);
        }

        List<String> pathSegments = UrlUtil.toPathSegments(path);
        int pathSegmentsCount = pathSegments.size();
        for (int i = 0; i < pathSegmentsCount; i++) {
            List<Node<T>> childNodes = nodeToInspect.getChildNodes();

            String currentSegment = pathSegments.get(i);
            boolean isParametrizedSegment = currentSegment.startsWith(":");
            String currentSegmentName = isParametrizedSegment ? currentSegment.substring(1) : currentSegment;
            boolean nodeMatched = false;
            Node<T> paramNode = null;
            boolean paramNodeFound = false;

            for (Node<T> childNode : childNodes) {
                if (!paramNodeFound) {
                    paramNodeFound = childNode.isParam();
                    if (paramNodeFound) {
                        // maybe need later for diagnostic message
                        paramNode = childNode;
                    }
                }
                if (currentSegmentName.equals(childNode.getName())) {
                    nodeMatched = true;
                    nodeToInspect = childNode; // to the next level
                    break;
                }
            }

            if (!nodeMatched && paramNodeFound && isParametrizedSegment) {
                /* Throw exception for cases like '/users/:login/posts' and '/users/:username/followers' */
                /* In such cases params must have the same name. For example '/users/:login/posts' and '/users/:login/followers' */
                String error = String.format("Path variables at the same position must have the same name. Problem with parameters :%s and :%s", currentSegmentName, paramNode.getName());
                throw new IllegalArgumentException(error);
            }

            if (!nodeMatched) {
                var newNode = new Node<T>(currentSegmentName, isParametrizedSegment);
                childNodes.add(newNode);
                nodeToInspect = newNode;
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
