/*
 * Copyright (C) 2013 mewin<mewin001@hotmail.de>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.mewin.jhttp.http;

/**
 *
 * @author mewin<mewin001@hotmail.de>
 */
public enum StatusCode
{
    CONTINUE(100, "Continue"), 
    SWITCHING_PROTOCOLS(101, "Switching Protocols"), 
    OK(200, "OK"), 
    CREATED(201, "Created"), 
    ACCEPTED(202, "Accepted"), 
    NON_AUTHORITATIVE_INFORMATION(203, "Non-Authoritative Information"), 
    NO_CONTENT(204, "No Content"),
    RESET_CONTENT(205, "Reset Content"), 
    PARTIAL_CONTENT(206, "Partial Content"),
    MULTIPLE_CHOICES(300, "Multiple Choices"), 
    MOVED_PERMANENTLY(301, "Moved Permanently"), 
    FOUND(302, "Found"), 
    SEE_OTHER(303, "See Other"), 
    NOT_MODIFIED(304, "Not Modified"),
    USE_PROXY(305, "Use Proxy"), 
    TEMPORARY_REDIRECT(307, "Temporary Redirect"), 
    BAD_REQUEST(400, "Bad Request"), 
    UNAUTHORIZED(401, "Unauthorized"), 
    PAYMENT_REQUIRED(402, "Payment Required"), 
    FORBIDDEN(403, "Forbidden"), 
    NOT_FOUND(404, "Not Found"), 
    METHOD_NOT_ALLOWED(405, "Method Not Allowed"), 
    NOT_ACCEPTABLE(406, "Not Acceptable"), 
    PROXY_AUTHENTUCATION_REQUIRED(407, "Proxy Authentication_Required"), 
    REQUEST_TIMEOUT(408, "Request Timeout"), 
    CONFLICT(409, "Conflict"), 
    GONE(410, "Gone"), 
    LENGTH_REQUIRED(411, "Length Required"), 
    PRECONDITION_FAILED(412, "Precondition Failed"), 
    REQUEST_ENTITY_TOO_LARGE(413, "Request Entity Too Large"), 
    REQUEST_URI_TOO_LONG(414, "Request-URI Too Long"), 
    UNSUPPORTED_MEDIA_TYPE(415, "Unsupported Media Type"), 
    REQUESTED_RANGE_NOT_SATISFIABLE(416, "Requested Range Not Stisfiable"), 
    EXPECTATION_FAILED(417, "Expectation Failed"), 
    INTERNAL_SERVER_ERROR(500, "Internal Server Error"), 
    NOT_IMPLEMENTED(501, "Not Implemented"), 
    BAD_GATEWAY(502, "Bad Gateway"), 
    SERVICE_UNAVAILABLE(503, "Service Unavailable"), 
    GATEWAY_TIMEOUT(504, "Gateway Timeout"), 
    HTTP_VERSION_NOT_SUPPORTED(505, "HTTP Version Not Supported");
    
    private int code;
    private String name;
    
    private StatusCode(int code, String name)
    {
        this.code = code;
        this.name = name;
    }
    
    public int getCode()
    {
        return code;
    }
    
    public String getName()
    {
        return name;
    }
}