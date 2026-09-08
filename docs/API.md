# PulseBoard API Documentation

Base URL: `http://localhost:8080`

All monitor endpoints require a valid JWT, obtained from `/api/auth/login`, sent as a Bearer token in the `Authorization` header.

---

## Authentication

### Register a new user

`POST /api/auth/register`

Creates a new user account and returns an auth token.

**Request**
```json
{
    "email": "balaram@gmail.com",
    "password": "balaram@1234"
}
```

**Response `201 Created`**
```json
{
    "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJiYWxhcmFtQGdtYWlsLmNvbSIsImlhdCI6MTc4ODc5ODU0OSwiZXhwIjoxNzg4ODg0OTQ5fQ.4DxddZbwRdnDbkb8styer7Rw4fdvMX_TqYCqnzgSFlg",
    "tokenType": "Bearer",
    "email": "balaram@gmail.com"
}
```

**Error — email already registered `409 Conflict`**
```json
{
    "timestamp": "2026-09-08T04:26:07.926917900Z",
    "status": 409,
    "error": "Conflict",
    "message": "An account with this email already exists",
    "path": "/api/auth/register"
}
```

---

### Log in

`POST /api/auth/login`

Authenticates an existing user and returns a fresh auth token.

**Request**
```json
{
    "email": "balaram@gmail.com",
    "password": "balaram@1234"
}
```

**Response `200 OK`**
```json
{
    "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJiYWxhcmFtQGdtYWlsLmNvbSIsImlhdCI6MTc4ODc5ODU0OSwiZXhwIjoxNzg4ODg0OTQ5fQ.4DxddZbwRdnDbkb8styer7Rw4fdvMX_TqYCqnzgSFlg",
    "tokenType": "Bearer",
    "email": "balaram@gmail.com"
}
```

---

## Monitors

All endpoints below require `Authorization: Bearer <token>`.

### Create a monitor

`POST /api/monitors`

Registers a new URL to be monitored. New monitors start with `currentStatus: "UNKNOWN"` until the first scheduled check runs.

**Request**
```json
{
    "name": "krishna",
    "url": "http://www.krishna.com"
}
```

**Response `201 Created`**
```json
{
    "id": "999dafe5-2933-4e1c-bbb2-661b97e65bff",
    "name": "krishna",
    "url": "http://www.krishna.com",
    "active": true,
    "currentStatus": "UNKNOWN",
    "createdAt": "2026-09-08T04:30:49.053607300Z"
}
```

**Error — required field missing `400 Bad Request`**

Request sent with `url` omitted:
```json
{
    "name": "krishna"
}
```
Response:
```json
{
    "timestamp": "2026-09-08T04:33:22.973729700Z",
    "status": 400,
    "error": "Bad Request",
    "message": "must not be blank",
    "path": "/api/monitors"
}
```

---

### List monitors

`GET /api/monitors`

Returns all monitors belonging to the authenticated user.

**Response `200 OK`**
```json
[
    {
        "id": "227e41c8-6ac5-4757-81db-ac5a82c23eab",
        "name": "balaram",
        "url": "http://www.balaram.com",
        "active": true,
        "currentStatus": "UP",
        "createdAt": "2026-09-07T16:33:39.221030Z"
    }
]
```

---

### Get monitor logs

`GET /api/monitors/{id}/logs`

Returns the check history for a single monitor. `{id}` is the monitor's `id`, returned when it was created (see **Create a monitor** above).

**Example request**
```
GET /api/monitors/227e41c8-6ac5-4757-81db-ac5a82c23eab/logs
```

**Response `200 OK`**
```json
[
    {
        "id": "aefffdaa-fda7-4766-8e3d-2ace1a364b10",
        "checkedAt": "2026-09-07T16:43:53.841522Z",
        "status": "UP",
        "responseTimeMs": 109,
        "statusCode": 200
    },
    {
        "id": "75df9cc3-5205-454c-814a-6de79eef4dd4",
        "checkedAt": "2026-09-07T16:42:53.082023Z",
        "status": "UP",
        "responseTimeMs": 61,
        "statusCode": 200
    }
]
```
*(response truncated here for brevity — the full list returns each scheduled check, most recent first)*

**Error — monitor does not exist `404 Not Found`**
```
GET /api/monitors/00000000-0000-0000-0000-000000000000/logs
```
```json
{
    "timestamp": "2026-09-08T04:38:20.295174600Z",
    "status": 404,
    "error": "Not Found",
    "message": "Monitor not found",
    "path": "/api/monitors/00000000-0000-0000-0000-000000000000/logs"
}
```
