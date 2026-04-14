import { useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import { login, register } from "../api/auth";
import { authStorage, extractApiError } from "../api/client";

const initialForm = {
  email: "",
  password: ""
};

function LoginPage() {
  const [mode, setMode] = useState("login");
  const [form, setForm] = useState(initialForm);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState("");
  const navigate = useNavigate();
  const location = useLocation();

  const redirectTo = location.state?.from?.pathname ?? "/";

  async function handleSubmit(event) {
    event.preventDefault();
    setSubmitting(true);
    setError("");

    try {
      const action = mode === "login" ? login : register;
      const data = await action(form);
      authStorage.saveSession(data.token, data.email);
      navigate(redirectTo, { replace: true });
    } catch (requestError) {
      setError(extractApiError(requestError));
    } finally {
      setSubmitting(false);
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
    <div className="auth-page">
      <div className="auth-panel hero-panel">
        <p className="eyebrow">PulseBoard</p>
        <h1>Watch every endpoint without babysitting your infrastructure.</h1>
        <p className="hero-copy">
          Track uptime, capture response trends, and notify customers the moment a service drops or comes back.
        </p>
        <div className="feature-grid">
          <div className="feature-tile">
            <strong>5-minute heartbeat</strong>
            <span>Automated checks with status change detection built in.</span>
          </div>
          <div className="feature-tile">
            <strong>Email incident alerts</strong>
            <span>Send DOWN and RECOVERED notifications straight from Gmail SMTP.</span>
          </div>
          <div className="feature-tile">
            <strong>Performance snapshots</strong>
            <span>Review uptime percentage and response times from recent checks.</span>
          </div>
        </div>
      </div>

      <div className="auth-panel form-panel">
        <div className="mode-toggle">
          <button
            className={mode === "login" ? "mode-button active" : "mode-button"}
            onClick={() => setMode("login")}
            type="button"
          >
            Login
          </button>
          <button
            className={mode === "register" ? "mode-button active" : "mode-button"}
            onClick={() => setMode("register")}
            type="button"
          >
            Register
          </button>
        </div>

        <form className="auth-form" onSubmit={handleSubmit}>
          <div>
            <label htmlFor="email">Email</label>
            <input
              id="email"
              name="email"
              type="email"
              placeholder="ops@yourcompany.com"
              value={form.email}
              onChange={updateField}
              required
            />
          </div>

          <div>
            <label htmlFor="password">Password</label>
            <input
              id="password"
              name="password"
              type="password"
              placeholder="Minimum 8 characters"
              value={form.password}
              onChange={updateField}
              minLength={8}
              required
            />
          </div>

          {error ? <p className="error-banner">{error}</p> : null}

          <button className="primary-button" type="submit" disabled={submitting}>
            {submitting ? "Working..." : mode === "login" ? "Login to Dashboard" : "Create Account"}
          </button>
        </form>
      </div>
    </div>
  );
}

export default LoginPage;
