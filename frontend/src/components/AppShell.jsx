import { Link, useNavigate } from "react-router-dom";
import { authStorage } from "../api/client";

function AppShell({ title, subtitle, children }) {
  const navigate = useNavigate();
  const email = authStorage.getEmail();

  function handleLogout() {
    authStorage.clear();
    navigate("/login", { replace: true });
  }

  return (
    <div className="app-shell">
      <header className="topbar">
        <div>
          <Link to="/" className="brand">
            PulseBoard
          </Link>
          <p className="brand-subtitle">Uptime monitoring with fast signal and clean recovery alerts.</p>
        </div>
        <div className="topbar-actions">
          <div className="identity">
            <span className="eyebrow">Signed in as</span>
            <strong>{email}</strong>
          </div>
          <button className="ghost-button" onClick={handleLogout}>
            Logout
          </button>
        </div>
      </header>

      <section className="page-hero">
        <div>
          <p className="eyebrow">Operations</p>
          <h1>{title}</h1>
          <p className="hero-copy">{subtitle}</p>
        </div>
      </section>

      <main>{children}</main>
    </div>
  );
}

export default AppShell;
