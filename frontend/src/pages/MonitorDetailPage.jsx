import { useEffect, useState } from "react";
import { Link, useParams } from "react-router-dom";
import { fetchMonitorLogs, fetchMonitors, fetchMonitorStats } from "../api/monitors";
import { extractApiError } from "../api/client";
import AppShell from "../components/AppShell";

function MonitorDetailPage() {
  const { id } = useParams();
  const [monitor, setMonitor] = useState(null);
  const [logs, setLogs] = useState([]);
  const [stats, setStats] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    loadMonitorDetails();
  }, [id]);

  async function loadMonitorDetails() {
    setLoading(true);
    setError("");

    try {
      const [allMonitors, logData, statsData] = await Promise.all([
        fetchMonitors(),
        fetchMonitorLogs(id),
        fetchMonitorStats(id)
      ]);

      setMonitor(allMonitors.find((item) => item.id === id) ?? null);
      setLogs(logData);
      setStats(statsData);
    } catch (requestError) {
      setError(extractApiError(requestError));
    } finally {
      setLoading(false);
    }
  }

  return (
    <AppShell
      title={monitor?.name ?? "Monitor detail"}
      subtitle={
        monitor
          ? `${monitor.url} - Review current health, uptime, and recent checks.`
          : "Inspect log history and response trends."
      }
    >
      <div className="detail-actions">
        <Link className="ghost-button inline-link" to="/">
          Back to Dashboard
        </Link>
      </div>

      {error ? <p className="error-banner">{error}</p> : null}

      {loading ? <p className="panel empty-state">Loading monitor details...</p> : null}

      {!loading && monitor ? (
        <>
          <section className="stats-grid">
            <article className="panel stat-card">
              <p className="eyebrow">Current status</p>
              <h2>{monitor.currentStatus}</h2>
            </article>
            <article className="panel stat-card">
              <p className="eyebrow">Uptime</p>
              <h2>{stats?.uptimePercentage ?? 0}%</h2>
              <span>Calculated from the last {stats?.sampleSize ?? 0} checks</span>
            </article>
            <article className="panel stat-card">
              <p className="eyebrow">Average response</p>
              <h2>{stats?.averageResponseTimeMs ?? 0} ms</h2>
              <span>Rolling average across recent checks</span>
            </article>
          </section>

          <section className="panel">
            <div className="panel-header">
              <h2>Check history</h2>
              <p>{logs.length} recorded checks</p>
            </div>

            {logs.length === 0 ? (
              <p className="empty-state">No checks recorded yet. The scheduler will populate this page after the first run.</p>
            ) : (
              <div className="table-wrap">
                <table>
                  <thead>
                    <tr>
                      <th>Checked At</th>
                      <th>Status</th>
                      <th>HTTP Code</th>
                      <th>Response Time</th>
                    </tr>
                  </thead>
                  <tbody>
                    {logs.map((log) => (
                      <tr key={log.id}>
                        <td>{new Date(log.checkedAt).toLocaleString()}</td>
                        <td>
                          <span className={`status-pill ${log.status.toLowerCase()}`}>{log.status}</span>
                        </td>
                        <td>{log.statusCode ?? "N/A"}</td>
                        <td>{log.responseTimeMs} ms</td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            )}
          </section>
        </>
      ) : null}
    </AppShell>
  );
}

export default MonitorDetailPage;
