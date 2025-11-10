# CN_HW1
Cloud in the Calculator

# Calculator in the Cloud (Networking HW1)

## 1. Overview
Java socket-based client/server calculator supporting ADD, SUB, MUL, DIV operations.

## 2. Architecture
Client ⇄ Server (TCP socket)
- Client sends ASCII command lines
- Server parses, computes, and returns responses

## 3. Protocol
### Request
<OP> <NUM1> <NUM2>
Example: `ADD 10 20`

### Response
200 OK <RESULT>  
400 ERR <TYPE>  
201 BYE

### Error Types
- DIVZERO : divide by zero
- ARGERR : invalid number of arguments
- UNKNOP : unsupported operation
- NAN : invalid number format
- EMPTY : empty input
