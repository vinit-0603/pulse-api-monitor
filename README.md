# ⚡ PULSE: API Failure Detection & Monitoring System

PULSE is a Java desktop application that continuously checks whether your web APIs are up and responding correctly. Add the endpoints you care about, and PULSE calls them on a schedule in the background, records every result, and shows live health on a dark-themed dashboard.

## Features

- **Endpoint management**: add, edit, pause/resume and delete API endpoints
- **Flexible checks**: choose the HTTP method (GET or HEAD), interval, timeout and expected status codes (for example `200,201`)
- **Background monitoring**: each active endpoint runs on its own schedule in a thread pool, with one tracked task per endpoint so edits, pauses and deletes never leave duplicate tasks running
- **Failure detection**: flags wrong status codes, timeouts, DNS failures and connection errors
- **Live dashboard**: Total, Online, Failed and Uptime cards with a colour-coded status table that refreshes every 5 seconds
- **Monitoring history**: log of every check with time, status, HTTP code, response time and error message
- **Analytics**: response-time line chart
- **Persistent storage**: endpoints and history are saved in a local SQLite database
- **Modern UI**: dark gradient design with sidebar navigation, built on FlatLaf

## Tech Stack

| Area | Technology |
|---|---|
| Language | Java 17+ |
| Build tool | Maven |
| UI | Swing + FlatLaf |
| Database | SQLite (JDBC) |
| HTTP client | `java.net.http.HttpClient` |
| Charts | JFreeChart |
| Scheduling | `ScheduledExecutorService` |

## Project Structure

```
src/main/java/com/pulse
├── database    DatabaseManager, EndpointDao
├── model       Endpoint, HistoryRecord, Incident
├── network     HttpMonitor
├── service     MonitoringService
└── ui          PulseApp, DashboardPanel, EndpointsPanel,
                HistoryPanel, AnalyticsPanel, SettingsPanel,
                GradientPanel
```

## Getting Started

### Prerequisites

- JDK 17 or newer
- Maven 3.8 or newer

### Build and run

```bash
git clone https://github.com/<your-username>/pulse-monitor.git
cd pulse-monitor
mvn clean compile
mvn org.codehaus.mojo:exec-maven-plugin:3.1.0:java -Dexec.mainClass=com.pulse.ui.PulseApp
```

You can also open the project in your IDE (VS Code, IntelliJ, Eclipse) and run `PulseApp.java` directly.

The database is created automatically on first launch at `~/.pulse/pulse.db`, along with a few sample endpoints.

## Usage

1. Open **API Endpoints** and click **+ Add Endpoint**.
2. Fill in the name, URL, method, interval, timeout and expected status codes, then click **Save & Monitor**.
3. Watch the **Dashboard** for live status. A row turns green (ONLINE) or red (FAILED) after its first check.
4. To pause, edit or delete an endpoint, select its row in **API Endpoints** and use the buttons at the bottom.
5. Open **Monitoring History** to see each check and any error message.

### Quick test endpoints

| Name | URL | Expected codes | Result |
|---|---|---|---|
| Working API | `https://jsonplaceholder.typicode.com/posts/1` | `200` | SUCCESS |
| Server error | `https://httpbin.org/status/500` | `200` | FAILED |
| Timeout | `https://httpbin.org/delay/10` (timeout 3) | `200` | FAILED |
| Multiple codes | `https://httpbin.org/status/201` | `200,201` | SUCCESS |

## How It Works

1. `MonitoringService` schedules one task per active endpoint.
2. `HttpMonitor` sends the request and builds a `HistoryRecord` with the status, HTTP code, response time and error message.
3. `EndpointDao` saves the record to SQLite.
4. `DashboardPanel` reads the latest result for each endpoint and updates the cards and table.

## Known Limitations

- The Analytics chart uses sample data and is not connected to stored history yet
- Incidents have a table and model, but incident records are not created yet
- Settings (alerts and theme) are placeholders
- The History panel loads once at startup and does not auto-refresh
- No desktop or email alerts yet
- Old history is never cleaned up

## Roadmap

- [ ] Incident tracking with downtime duration
- [ ] Desktop and email alerts on failure
- [ ] Analytics based on real history and uptime percentage
- [ ] Auto-refreshing History panel with filters
- [ ] Automatic cleanup of old history and database indexes
- [ ] Foreign keys enabled on every database connection

## Contributing

Contributions are welcome. Fork the repository, create a feature branch, commit your changes and open a pull request.

## License

This project is licensed under the MIT License. Add a `LICENSE` file to the repository if you want this to apply.
