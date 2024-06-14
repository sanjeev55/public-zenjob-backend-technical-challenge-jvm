# ZenJob Challenge

### Task A

- **AS** a *company*
- **I CAN** cancel a *job* I ordered previously
- **AND** if the *job* gets cancelled all of its *shifts* get cancelled as well

### Task B

- **AS** a *company*
- **I CAN** cancel a single *shift* of a job I ordered previously

### Task C

- **AS** a *company*
- **I CAN** cancel all of my shifts which were booked for a specific talent
- **AND** replacement shifts are created with the same dates

## What am I looking at
You are looking at a simplified backend service for managing job and shift bookings, similar to Zenjob's platform. This service allows companies to create jobs with multiple shifts, book talents (workers) for these shifts, and manage cancellations. This implementation focuses on providing a clear and maintainable codebase that adheres to clean code principles.

## What are the features
1. **Create Jobs and Shifts**: Companies can create jobs with a specified start and end date, which automatically generates shifts for each day within that date range.
2. **Book Talents for Shifts**: Talents can be booked for available shifts, and the shift status is updated accordingly.
3. **Cancel Jobs and Shifts**: Companies can cancel entire jobs or individual shifts, and all associated shifts are updated to reflect the cancellation.
4. **Cancel Shifts for a Talent**: Companies can cancel all shifts booked for a specific talent and create replacement shifts for the same dates.
5. **Fetch Job and Shift Information**: Endpoints are provided to fetch details about jobs and their associated shifts.

## How do I run it


### Steps to Run
1. **Clone the Repository**
   ```sh
   git clone <repository-url>
   cd <repository-directory>
2. **Build Project**
    ```sh
   ./gradlew build
3. **Run Application**
    ```sh
   ./gradlew bootRun


## Endpoints
- **Create Job**: `POST /job`
- **Cancel Job**: `PUT /job/cancel/{jobId}`
- **Fetch Job**: `GET /job/{jobId}`
- **Fetch Shifts by Job**: `GET /job/{jobId}/shifts`
- **Book Talent for Shift**: `PUT /shift/book/{shiftId}`
- **Cancel Shift**: `PUT /shift/cancel/{shiftId}`
- **Cancel Shifts for Talent**: `PUT /shift/talent/{talentId}`

## What are covered
1. **Job and Shift Creation**: Validates date ranges and ensures shifts are created correctly.
2. **Shift Booking**: Checks if the shift is in a state that allows booking and updates the status.
3. **Cancellations**: Handles cancellation logic for jobs and shifts, ensuring all associated entities are updated accordingly.
4. **Exception Handling**: Comprehensive global exception handling for better error management and user feedback.
5. **Unit Tests**: Ensures core functionalities such as job creation, cancellation, and booking are thoroughly tested using JUnit and Mockito.

## How can we improve it in the future
1. **Detailed Logging**: Add more detailed logging for better traceability and debugging.
2. **Improved Exception Handling**: Introduce more specific exception types and enhance the global exception handler to provide more detailed error responses.
3. **API Documentation**: Use tools like Swagger to provide comprehensive API documentation for easier integration and usage.
4. **Add More Tests**: Add more tests to cover all cases for the service layers.
