import apiClient from "./client";

export async function fetchMonitors() {
  const { data } = await apiClient.get("/monitors");
  return data;
}

export async function createMonitor(payload) {
  const { data } = await apiClient.post("/monitors", payload);
  return data;
}

export async function deleteMonitor(id) {
  await apiClient.delete(`/monitors/${id}`);
}

export async function fetchMonitorLogs(id) {
  const { data } = await apiClient.get(`/monitors/${id}/logs`);
  return data;
}

export async function fetchMonitorStats(id) {
  const { data } = await apiClient.get(`/monitors/${id}/stats`);
  return data;
}
