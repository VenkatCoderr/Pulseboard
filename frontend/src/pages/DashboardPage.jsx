import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { createMonitor, deleteMonitor, fetchMonitors } from "../api/monitors";
import { extractApiError } from "../api/client";
import AppShell from "../components/AppShell";

const emptyForm = {
  name: "",
  url: ""
};

function DashboardPage() {
  const [monitors, setMonitors] = useState([]);
  const [form, setForm] = useState(emptyForm);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState("");

  useEffect(() => {
    loadMonitors();
  }, []);

  async function loadMonitors() {
    setLoading(true);
    setError("");

    try {
      const data = await fetchMonitors();
      setMonitors(data);
    } catch (requestError) {
      setError(extractApiError(requestError));
    } finally {
      setLoading(false);
    }
  }

  async function handleCreate(event) {
    event.preventDefault();
    setSaving(true);
    setError("");

    try {
      const createdMonitor = await createMonitor(form);
      setMonitors((current) => [createdMonitor, ...current]);
      setForm(emptyForm);
    } catch (requestError) {
      setError(extractApiError(requestError));
    } finally {
      setSaving(false);
    }
  }

  async function handleDelete(id) {
    const confirmed = window.confirm("Delete this monitor and all related logs?");

    if (!confirmed) {
      return;
    }

    try {
      await deleteMonitor(id);
      setMonitors((current) => current.filter((monitor) => monitor.id !== id));
    } catch (requestError) {
      setError(extractApiError(requestError));
    }
  }

  function updateField(event) {
    const { name, value } = event.target;
    setForm((current) => ({
      ...current,
      [name]: value
    }));
  }

  return (
    <AppShell
      title="Monitor fleet overview"
      subtitle="Create monitors, track current status, and drill into recent checks from a single dashboard."
    >
      <section className="dashboard-grid">
        <form className="panel create-panel" onSubmit={handleCreate}>
          <div className="panel-header">
            <h2>Create monitor</h2>
            <p>Add a new HTTP endpoint to the 5-minute scheduler.</p>
          </div>

          <label htmlFor="name">Monitor name</label>
          <input
            id="name"
            name="name"
            value={form.name}
            onChange={updateField}
            placeholder="Marketing site"
            maxLength={100}
            required
          />

          <label htmlFor="url">URL</label>
          <input
            id="url"
            name="url"
            type="url"
            value={form.url}
            onChange={updateField}
            placeholder="https://example.com"
            required
          />

          <button className="primary-button" type="submit" disabled={saving}>
            {saving ? "Creating..." : "Add Monitor"}
          </button>

          {error ? <p className="error-banner">{error}</p> : null}
        </form>

        <section className="panel list-panel">
          <div className="panel-header">
            <h2>Active monitors</h2>
            <p>{monitors.length} configured endpoint{monitors.length === 1 ? "" : "s"}</p>
          </div>

          {loading ? <p className="empty-state">Loading monitors...</p> : null}

          {!loading && monitors.length === 0 ? (
            <p className="empty-state">No monitors yet. Add your first endpoint to start collecting checks.</p>
          ) : null}

          <div className="monitor-list">
            {monitors.map((monitor) => (
              <article className="monitor-card" key={monitor.id}>
                <div className="monitor-topline">
                  <span className={`status-pill ${monitor.currentStatus?.toLowerCase() ?? "unknown"}`}>
                    {monitor.currentStatus}
                  </span>
                  <button className="text-button danger" onClick={() => handleDelete(monitor.id)} type="button">
                    Delete
                  </button>
                </div>

                <Link className="monitor-link" to={`/monitors/${monitor.id}`}>
                  <h3>{monitor.name}</h3>
                  <p>{monitor.url}</p>
                </Link>

                <div className="monitor-meta">
                  <span>Created {new Date(monitor.createdAt).toLocaleString()}</span>
                  <span>{monitor.active ? "Active" : "Paused"}</span>
                </div>
              </article>
            ))}
          </div>
        </section>
      </section>
    </AppShell>
  );
}

export default DashboardPage;
